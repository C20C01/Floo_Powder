package io.github.c20c01.item;

import io.github.c20c01.CCMain;
import io.github.c20c01.block.portalFire.PortalFireBlock;
import io.github.c20c01.block.portalFire.PortalFireBlockEntity;
import io.github.c20c01.block.portalPoint.PortalPointBlock;
import io.github.c20c01.block.portalPoint.PortalPointBlockEntity;
import io.github.c20c01.savedData.Permission;
import io.github.c20c01.savedData.PermissionManager;
import io.github.c20c01.savedData.PortalPoint;
import io.github.c20c01.savedData.PortalPointManager;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class PortalBook extends Item {

    public PortalBook(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (player instanceof ServerPlayer serverPlayer) {
            var blockPos = getPlayerPOVHitResult(level, player, ClipContext.Fluid.ANY).getBlockPos();
            var blockEntity = level.getBlockEntity(blockPos);
            if (blockEntity instanceof PortalFireBlockEntity e) {
                if (player.isShiftKeyDown()) {
                    getCloneItem(serverPlayer, e);
                } else {
                    setLasting(serverPlayer, level, blockPos);
                }
                return InteractionResultHolder.pass(player.getItemInHand(hand));
            }
            if (blockEntity instanceof PortalPointBlockEntity e) {
                setPortalPoint(serverPlayer, e, blockPos, level);
                return InteractionResultHolder.pass(player.getItemInHand(hand));
            }
            if (player.isShiftKeyDown()) {
                showPerInfo(serverPlayer);
            } else {
                showPosInfo(serverPlayer);
            }

        }
        return InteractionResultHolder.pass(player.getItemInHand(hand));
    }

    private static void getCloneItem(ServerPlayer player, PortalFireBlockEntity blockEntity) {
        ItemStack itemStack = new ItemStack(CCMain.FLOO_POWDER_ITEM.get()).setHoverName(new TextComponent(blockEntity.getName()));
        player.getInventory().add(itemStack);
    }

    private static void setLasting(ServerPlayer player, Level level, BlockPos blockPos) {
        var text = new TranslatableComponent(CCMain.TEXT_SET_PORTAL_FIRE_BOOK).append(": " + PortalFireBlock.changeLasting(level, blockPos));
        player.sendMessage(text, ChatType.GAME_INFO, Util.NIL_UUID);
    }

    private static void setPortalPoint(ServerPlayer player, PortalPointBlockEntity blockEntity, BlockPos pos, Level level) {
        var blockState = blockEntity.getBlockState();
        blockState.setValue(PortalPointBlock.FIRE, !blockState.getValue(PortalPointBlock.FIRE));
        var blockStateAbove = level.getBlockState(pos.above());
        if (blockState.getValue(PortalPointBlock.FIRE)) {
            if (blockStateAbove.isAir())
                level.setBlock(pos.above(), Blocks.FIRE.defaultBlockState(), Block.UPDATE_ALL);
        } else {
            if (blockStateAbove.is(Blocks.FIRE))
                level.removeBlock(pos.above(), Boolean.FALSE);
        }
        var text = new TranslatableComponent(CCMain.TEXT_SET_PORTAL_POINT_BOOK).append(": " + blockState.getValue(PortalPointBlock.FIRE));
        player.sendMessage(text, ChatType.GAME_INFO, Util.NIL_UUID);
    }

    private static void showPosInfo(ServerPlayer serverPlayer) {
        for (PortalPoint p : PortalPointManager.get(serverPlayer.getServer()).getAll()) {
            var text1 = new TextComponent(p.toString());
            serverPlayer.sendMessage(text1, ChatType.CHAT, Util.NIL_UUID);
        }
    }

    private static void showPerInfo(ServerPlayer serverPlayer) {
        for (Permission p : PermissionManager.get(serverPlayer.getServer()).getAll()) {
            var text1 = new TextComponent(p.toString());
            serverPlayer.sendMessage(text1, ChatType.CHAT, Util.NIL_UUID);
        }
    }
}
