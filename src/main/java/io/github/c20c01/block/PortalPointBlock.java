package io.github.c20c01.block;

import io.github.c20c01.CCMain;
import io.github.c20c01.pos.PosMap;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
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
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class PortalPointBlock extends Block implements EntityBlock {
    public PortalPointBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new PortalPointBlockEntity(pos, state);
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (player instanceof ServerPlayer serverPlayer) {
            var text = Component.nullToEmpty("Test: " + ((PortalPointBlockEntity) Objects.requireNonNull(level.getBlockEntity(blockPos))).name);
            serverPlayer.sendMessage(text, ChatType.GAME_INFO, Util.NIL_UUID);
        }


        return super.use(blockState, level, blockPos, player, hand, hitResult);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void neighborChanged(@NotNull BlockState blockState, @NotNull Level level, @NotNull BlockPos blockPos, @NotNull Block block, @NotNull BlockPos blockPos1, boolean p_60514_) {
        if (blockPos.above().equals(blockPos1) && level.getBlockEntity(blockPos) instanceof PortalPointBlockEntity blockEntity) {
            BlockState blockState1 = level.getBlockState(blockPos1);
            if (blockEntity.name.equals("")) {
                if (blockState1.is(Blocks.FIRE)) {
                    level.removeBlock(blockPos.above(), false);
                    level.playSound(null, blockPos, SoundEvents.VILLAGER_NO, SoundSource.BLOCKS, 8.0F, 0.9F + level.random.nextFloat() * 0.2F);
                }
            } else {
                if (blockState1.is(Blocks.AIR)) {
                    level.setBlock(blockPos.above(), Blocks.FIRE.defaultBlockState(), Block.UPDATE_ALL);
                    level.playSound(null, blockPos, SoundEvents.FIRECHARGE_USE, SoundSource.BLOCKS, 2.0F, 0.9F + level.random.nextFloat() * 0.2F);
                } else if (blockState1.canOcclude()) {
                    PosMap.remove(blockEntity.name);
                    blockEntity.name = "";
                    level.playSound(null, blockPos, SoundEvents.BEACON_DEACTIVATE, SoundSource.BLOCKS, 5.0F, 1.0F);
                }
            }
        }
        super.neighborChanged(blockState, level, blockPos, block, blockPos1, p_60514_);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onRemove(@NotNull BlockState blockState, @NotNull Level level, @NotNull BlockPos blockPos, @NotNull BlockState blockState1, boolean p_60519_) {
        if (level.getBlockEntity(blockPos) instanceof PortalPointBlockEntity blockEntity) {
            PosMap.remove(blockEntity.name);
            if (!blockEntity.name.equals(""))
                level.playSound(null, blockPos, SoundEvents.BEACON_DEACTIVATE, SoundSource.BLOCKS, 5.0F, 1.0F);
        }
        super.onRemove(blockState, level, blockPos, blockState1, p_60519_);
    }
}