package io.github.c20c01.block.portalPoint;

import io.github.c20c01.CCMain;
import io.github.c20c01.pos.PosMap;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class PortalPointBlock extends Block implements EntityBlock {
    public PortalPointBlock(Properties properties) {
        super(properties);
    }

    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PortalPointBlockEntity(pos, state);
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (player instanceof ServerPlayer serverPlayer && level.getBlockEntity(blockPos) instanceof PortalPointBlockEntity blockEntity) {
            if (blockEntity.name.equals("")) {
                if (blockEntity.lit) {
                    var text = new TranslatableComponent(CCMain.TEXT_ACTIVATED_BY_BOOK);
                    serverPlayer.sendMessage(text, ChatType.GAME_INFO, Util.NIL_UUID);
                } else {
                    if (!player.getItemInHand(hand).is(CCMain.PORTAL_FLINT_AND_STEEL_ITEM.get())) {
                        var text = new TranslatableComponent(CCMain.TEXT_NEEDS_ACTIVATION);
                        serverPlayer.sendMessage(text, ChatType.GAME_INFO, Util.NIL_UUID);
                    } else return super.use(blockState, level, blockPos, player, hand, hitResult);
                }
            } else {
                var text = new TextComponent(blockEntity.name);
                serverPlayer.sendMessage(text, ChatType.GAME_INFO, Util.NIL_UUID);
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void neighborChanged(BlockState blockState, Level level, BlockPos blockPos, Block block, BlockPos blockPos1, boolean p_60514_) {
        if (blockPos.above().equals(blockPos1) && level.getBlockEntity(blockPos) instanceof PortalPointBlockEntity blockEntity) {
            BlockState blockState1 = level.getBlockState(blockPos1);
            var haveName = !blockEntity.name.equals("");
            if (blockState1.is(Blocks.AIR)) {
                if (blockEntity.lit) {
                    level.setBlock(blockPos1, Blocks.FIRE.defaultBlockState(), Block.UPDATE_ALL);
                    level.playSound(null, blockPos, SoundEvents.FIRECHARGE_USE, SoundSource.BLOCKS, 2.0F, 0.9F + level.random.nextFloat() * 0.2F);
                }
            } else if (blockState1.is(Blocks.FIRE)) {
                if (!haveName && !blockEntity.lit) {
                    level.removeBlock(blockPos1, false);
                    level.playSound(null, blockPos, SoundEvents.VILLAGER_NO, SoundSource.BLOCKS, 8.0F, 0.9F + level.random.nextFloat() * 0.2F);
                }
            } else if (blockState1.canOcclude()) {
                if (haveName || blockEntity.lit) {
                    if (level instanceof ServerLevel serverLevel) {
                        PosMap.remove(blockEntity.name, serverLevel, blockPos);
                    }
                    blockEntity.name = "";
                    blockEntity.lit = false;
                    level.playSound(null, blockPos, SoundEvents.BEACON_DEACTIVATE, SoundSource.BLOCKS, 5.0F, 1.0F);
                }
            }
        }
        super.neighborChanged(blockState, level, blockPos, block, blockPos1, p_60514_);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onRemove(BlockState blockState, Level level, BlockPos blockPos, BlockState blockState1, boolean p_60519_) {
        if (level.getBlockEntity(blockPos) instanceof PortalPointBlockEntity blockEntity) {
            if (level instanceof ServerLevel serverLevel) {
                PosMap.remove(blockEntity.name, serverLevel, blockPos);
            }
            if (blockEntity.lit || !blockEntity.name.equals(""))
                level.playSound(null, blockPos, SoundEvents.BEACON_DEACTIVATE, SoundSource.BLOCKS, 5.0F, 1.0F);
        }
        super.onRemove(blockState, level, blockPos, blockState1, p_60519_);
    }
}