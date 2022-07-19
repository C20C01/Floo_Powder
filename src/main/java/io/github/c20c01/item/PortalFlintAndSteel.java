package io.github.c20c01.item;

import io.github.c20c01.block.portalPoint.PortalPointBlock;
import io.github.c20c01.block.portalPoint.PortalPointBlockEntity;
import io.github.c20c01.pos.PosMap;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.FlintAndSteelItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class PortalFlintAndSteel extends FlintAndSteelItem {
    public String name;

    public PortalFlintAndSteel(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext useOnContext) {
        Level level = useOnContext.getLevel();
        BlockPos blockPos = useOnContext.getClickedPos();
        BlockPos blockPos1 = blockPos.above();
        if (level.getBlockState(blockPos).getBlock() instanceof PortalPointBlock && useOnContext.getClickedFace().equals(Direction.UP) && BaseFireBlock.canBePlacedAt(level, blockPos1, useOnContext.getHorizontalDirection())) {
            level.setBlock(blockPos1, Blocks.FIRE.defaultBlockState(), Block.UPDATE_ALL);
            if (level instanceof ServerLevel serverLevel && serverLevel.getBlockEntity(blockPos) instanceof PortalPointBlockEntity blockEntity) {
                name = useOnContext.getItemInHand().getDisplayName().getString();
                blockEntity.name = name;
                blockEntity.setLevel(serverLevel);
                PosMap.set(name, serverLevel, blockPos1);
            }
            Player player = useOnContext.getPlayer();
            if (useOnContext.getPlayer() instanceof ServerPlayer serverPlayer) {
                ItemStack itemstack = useOnContext.getItemInHand();
                CriteriaTriggers.PLACED_BLOCK.trigger(serverPlayer, blockPos1, itemstack);
                itemstack.hurtAndBreak(1, player, (x) -> x.broadcastBreakEvent(useOnContext.getHand()));
            }
            level.playSound(player, blockPos1, SoundEvents.AMETHYST_CLUSTER_BREAK, SoundSource.BLOCKS, 2.0F, 0.9F + level.random.nextFloat() * 0.2F);
            level.playSound(null, blockPos, SoundEvents.BEACON_ACTIVATE, SoundSource.BLOCKS, 5.0F, 1.0F);
            return InteractionResult.sidedSuccess(level.isClientSide());
        } else {
            return InteractionResult.FAIL;
        }
    }
}
