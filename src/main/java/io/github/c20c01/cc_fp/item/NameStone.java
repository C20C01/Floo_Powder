package io.github.c20c01.cc_fp.item;

import io.github.c20c01.cc_fp.CCMain;
import io.github.c20c01.cc_fp.block.portalPoint.PortalPointBlockEntity;
import io.github.c20c01.cc_fp.block.powderGiver.PowderGiverBlockEntity;
import io.github.c20c01.cc_fp.block.powderPot.PowderPotBlockEntity;
import io.github.c20c01.cc_fp.client.gui.screen.NameStoneScreen;
import io.github.c20c01.cc_fp.savedData.PortalPointManager;
import io.github.c20c01.cc_fp.tool.MessageSender;
import io.github.c20c01.cc_fp.tp.TpTool;
import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class NameStone extends Item {
    public NameStone(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stone, @Nullable Level level, List<Component> components, TooltipFlag tooltipFlag) {
        String name = getStoneName(stone);
        if (!name.isEmpty()) {
            components.add(Component.literal(name).withStyle(ChatFormatting.DARK_GREEN));
        }
    }

    @Override
    public InteractionResult useOn(UseOnContext useOnContext) {
        Player player = useOnContext.getPlayer();
        if (player == null) {
            return InteractionResult.PASS;
        }
        ItemStack itemStack = useOnContext.getItemInHand();
        String name = getStoneName(itemStack);
        if (name.isEmpty()) {
            return InteractionResult.PASS;
        }

        Level level = useOnContext.getLevel();
        BlockPos blockPos = useOnContext.getClickedPos();
        if (level.getBlockEntity(blockPos) instanceof PortalPointBlockEntity blockEntity) {
            if (level instanceof ServerLevel serverLevel) {
                player.getCooldowns().addCooldown(this, 40);
                if (!blockEntity.canUse(serverLevel.getServer(), player)) {
                    MessageSender.gameInfo(player, Component.translatable(CCMain.TEXT_NOT_OWNER));
                    return InteractionResult.FAIL;
                }

                PortalPointManager portalPointManager = PortalPointManager.get(serverLevel.getServer());
                var point = portalPointManager.get(blockEntity.getPointName(), Boolean.FALSE);
                if (point == null) {
                    MessageSender.gameInfo(player, Component.translatable(CCMain.TEXT_POINT_UNACTIVATED));
                    return InteractionResult.FAIL;
                }

                if (player.isShiftKeyDown()) {
                    if (point.publicGroup().equals(name)) {
                        portalPointManager.changePointInfo(point.setPublicGroupName(""));
                        MessageSender.gameInfo(player, Component.translatable(CCMain.TEXT_POINT_SET_NO_PUBLIC));
                    } else {
                        portalPointManager.changePointInfo(point.setPublicGroupName(name));
                        MessageSender.gameInfo(player, Component.translatable(CCMain.TEXT_POINT_SET_PUBLIC));
                    }
                } else {
                    portalPointManager.changePointInfo(point.setDesc(name));
                    MessageSender.gameInfo(player, Component.translatable(CCMain.TEXT_POINT_SET_DESC));
                }
                itemStack.hurtAndBreak(1, player, (x) -> x.broadcastBreakEvent(useOnContext.getHand()));
                level.playSound(null, blockPos, SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 1.0F, 0.8F + level.getRandom().nextFloat() * 0.4F);
                return InteractionResult.CONSUME;
            }
            return InteractionResult.SUCCESS;
        }

        if (player.getAbilities().instabuild && level.getBlockEntity(blockPos) instanceof PowderGiverBlockEntity blockEntity) {
            if (level instanceof ServerLevel) {
                MessageSender.gameInfo(player, Component.translatable(CCMain.TEXT_POWDER_GIVER_GROUP).append(": " + name));
                blockEntity.setPublicGroup(name);
                level.playSound(null, blockPos, SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 1.0F, 0.8F + level.getRandom().nextFloat() * 0.4F);
            }
            return InteractionResult.SUCCESS;
        }

        if (level.getBlockEntity(blockPos) instanceof PowderPotBlockEntity blockEntity) {
            if (level instanceof ServerLevel) {
                if (blockEntity.setName(name)) {
                    itemStack.hurtAndBreak(1, player, (x) -> x.broadcastBreakEvent(useOnContext.getHand()));
                    level.playSound(null, blockPos, SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 1.0F, 0.8F + level.getRandom().nextFloat() * 0.4F);
                }
                MessageSender.gameInfo(player, Component.translatable(CCMain.TEXT_POT_NAME).append(": " + name));
                return InteractionResult.CONSUME;
            } else {
                return InteractionResult.SUCCESS;
            }
        }

        return super.useOn(useOnContext);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (level.isClientSide) {
            openGui(itemStack, player, hand);
        }
        player.awardStat(Stats.ITEM_USED.get(this));
        return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide());
    }

    @OnlyIn(Dist.CLIENT)
    private static void openGui(ItemStack itemStack, Player player, InteractionHand hand) {
        Minecraft.getInstance().setScreen(new NameStoneScreen(itemStack, player, hand));
    }

    @Override
    public boolean overrideStackedOnOther(ItemStack itemStack, Slot slot, ClickAction clickAction, Player player) {
        if (clickAction != ClickAction.SECONDARY) {
            return false;
        }

        ItemStack other = slot.getItem();
        if (other.isEmpty()) {
            return false;
        }

        String name = getStoneName(itemStack);
        if (name.isEmpty()) {
            return false;
        }

        if (other.is(CCMain.FLOO_HANDBAG_ITEM.get())) {
            if (FlooHandbag.addPowderName(other, name)) {
                itemStack.hurtAndBreak(4, player, (x) -> x.broadcastBreakEvent(player.getUsedItemHand()));
                player.level().playSound(null, player.blockPosition(), SoundEvents.ENCHANTMENT_TABLE_USE, player.getSoundSource(), 1, 0.8F + player.getRandom().nextFloat() * 0.4F);
            }
            return true;
        }

        if (!TpTool.getItemName(other).equals(name)) {
            slot.set(other.copy().setHoverName(Component.literal(name)));
            itemStack.hurtAndBreak(1, player, (x) -> x.broadcastBreakEvent(player.getUsedItemHand()));
            player.level().playSound(null, player.blockPosition(), SoundEvents.ENCHANTMENT_TABLE_USE, player.getSoundSource(), 1, 0.8F + player.getRandom().nextFloat() * 0.4F);
            return true;
        }

        return true;
    }

    public static String getStoneName(ItemStack itemStack) {
        return itemStack.getOrCreateTag().getString("StoneName");
    }

    public static ItemStack setStoneName(ItemStack itemStack, String name) {
        itemStack.getOrCreateTag().putString("StoneName", name);
        return itemStack;
    }
}
