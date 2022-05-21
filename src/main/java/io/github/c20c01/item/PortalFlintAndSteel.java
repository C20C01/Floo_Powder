package io.github.c20c01.item;

import io.github.c20c01.block.PortalPointBlock;
import io.github.c20c01.block.PortalPointBlockEntity;
import io.github.c20c01.pos.PosMap;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;

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
        name = useOnContext.getItemInHand().getDisplayName().getString();
        Player player = useOnContext.getPlayer();
        Level level = useOnContext.getLevel();
        BlockPos blockPos = useOnContext.getClickedPos();
        BlockPos blockPos1 = blockPos.relative(useOnContext.getClickedFace());
        if (level.getBlockState(blockPos).getBlock() instanceof PortalPointBlock && level.getBlockState(blockPos1.below()).getBlock() instanceof PortalPointBlock) {
            level.playSound(player, blockPos1, SoundEvents.AMETHYST_CLUSTER_BREAK, SoundSource.BLOCKS, 2.0F, 0.9F + level.random.nextFloat() * 0.2F);
            ItemStack itemstack = useOnContext.getItemInHand();
            if (player instanceof ServerPlayer) {
                CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayer) player, blockPos1, itemstack);
                itemstack.hurtAndBreak(1, player, (x) -> x.broadcastBreakEvent(useOnContext.getHand()));
            }
            if (level instanceof ServerLevel serverLevel) {
                serverLevel.setBlock(blockPos1, Blocks.FIRE.defaultBlockState(), Block.UPDATE_ALL);
                BlockEntity blockEntity = serverLevel.getBlockEntity(blockPos);
                if (blockEntity instanceof PortalPointBlockEntity portalPointBlockEntity) {
                    portalPointBlockEntity.name = this.name;
                    portalPointBlockEntity.setLevel(serverLevel);
                    PosMap.set(name, serverLevel, blockPos1);
                }
            }
            level.playSound(null, blockPos, SoundEvents.BEACON_ACTIVATE, SoundSource.BLOCKS, 5.0F, 1.0F);

            return InteractionResult.sidedSuccess(level.isClientSide());
        } else {
            return InteractionResult.FAIL;
        }
    }
}
