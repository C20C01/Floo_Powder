package io.github.c20c01.cc_fp.item;

import io.github.c20c01.cc_fp.CCMain;
import io.github.c20c01.cc_fp.block.portalFire.PortalFireBlock;
import io.github.c20c01.cc_fp.block.portalFire.PortalFireBlockEntity;
import io.github.c20c01.cc_fp.block.portalPoint.PortalPointBlock;
import io.github.c20c01.cc_fp.block.portalPoint.PortalPointBlockEntity;
import io.github.c20c01.cc_fp.block.powderGiver.PowderGiverBlockEntity;
import io.github.c20c01.cc_fp.block.powderPot.PowderPotBlockEntity;
import io.github.c20c01.cc_fp.savedData.Permission;
import io.github.c20c01.cc_fp.savedData.PermissionManager;
import io.github.c20c01.cc_fp.savedData.PortalPoint;
import io.github.c20c01.cc_fp.savedData.PortalPointManager;
import io.github.c20c01.cc_fp.tool.MessageSender;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
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
                return InteractionResultHolder.success(player.getItemInHand(hand));
            }

            if (blockEntity instanceof PortalPointBlockEntity e) {
                setPortalPointFire(serverPlayer, e, blockPos, level);
                return InteractionResultHolder.success(player.getItemInHand(hand));
            }

            if (blockEntity instanceof PowderGiverBlockEntity e) {
                e.changeToNextCheckType();
                MessageSender.gameInfo(serverPlayer, Component.literal(e.getCheckType().name()));
                return InteractionResultHolder.success(player.getItemInHand(hand));
            }

            if (blockEntity instanceof PowderPotBlockEntity e) {
                MessageSender.gameInfo(serverPlayer, Component.translatable(CCMain.TEXT_POT_UNLIMITED).append(": " + e.setUnlimited()));
                return InteractionResultHolder.success(player.getItemInHand(hand));
            }

            if (player.isShiftKeyDown()) {
                showPerInfo(serverPlayer);
            } else {
                showPosInfo(serverPlayer);
            }
        }
        return InteractionResultHolder.success(player.getItemInHand(hand));
    }

    private static void getCloneItem(ServerPlayer player, PortalFireBlockEntity blockEntity) {
        ItemStack itemStack = CCMain.FLOO_POWDER_ITEM.get().getDefaultInstance().setHoverName(Component.literal(blockEntity.getTargetName()));
        player.getInventory().add(itemStack);
    }

    private static void setLasting(ServerPlayer player, Level level, BlockPos blockPos) {
        MessageSender.gameInfo(player, Component.translatable(CCMain.TEXT_SET_PORTAL_FIRE_BOOK).append(": " + PortalFireBlock.changeLasting(level, blockPos)));
    }

    private static void setPortalPointFire(ServerPlayer player, PortalPointBlockEntity blockEntity, BlockPos pos, Level level) {
        var blockState = blockEntity.getBlockState();
        var flag = !blockState.getValue(PortalPointBlock.FIRE);
        var blockStateAbove = level.getBlockState(pos.above());
        level.setBlock(pos, blockState.setValue(PortalPointBlock.FIRE, flag), Block.UPDATE_ALL_IMMEDIATE);
        if (flag) {
            if (blockStateAbove.isAir()) level.setBlock(pos.above(), Blocks.FIRE.defaultBlockState(), Block.UPDATE_ALL);
        } else {
            if (blockStateAbove.is(Blocks.FIRE)) level.removeBlock(pos.above(), Boolean.FALSE);
        }
        MessageSender.gameInfo(player, Component.translatable(CCMain.TEXT_SET_PORTAL_POINT_FIRE_BOOK).append(": " + flag));
    }

    private static void showPosInfo(ServerPlayer serverPlayer) {
        var list = PortalPointManager.get(serverPlayer.getServer()).getAll();
        if (list.size() == 0) {
            MessageSender.chat(serverPlayer, Component.translatable(CCMain.TEXT_NOT_FOUND_BOOK));
        } else {
            for (PortalPoint p : list) {
                MessageSender.chat(serverPlayer, Component.literal(p.toString()));
            }
        }
    }

    private static void showPerInfo(ServerPlayer serverPlayer) {
        for (Permission p : PermissionManager.get(serverPlayer.getServer()).getAll()) {
            MessageSender.chat(serverPlayer, Component.literal(p.toString()));
        }
    }
}
