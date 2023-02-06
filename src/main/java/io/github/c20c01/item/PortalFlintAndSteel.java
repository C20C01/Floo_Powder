package io.github.c20c01.item;

import io.github.c20c01.CCMain;
import io.github.c20c01.block.portalPoint.PortalPointBlock;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class PortalFlintAndSteel extends FlintAndSteelItem {
    public PortalFlintAndSteel(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext useOnContext) {
        if (!useOnContext.getClickedFace().equals(Direction.UP)) {
            return InteractionResult.FAIL;
        }

        Level level = useOnContext.getLevel();
        BlockPos blockPos = useOnContext.getClickedPos();
        BlockState blockState = level.getBlockState(blockPos);

        if (!blockState.is(CCMain.PORTAL_POINT_BLOCK.get())) {
            return InteractionResult.FAIL;
        }
        if (blockState.getValue(PortalPointBlock.FIRE)) {
            return InteractionResult.FAIL;
        }
        if (!BaseFireBlock.canBePlacedAt(level, blockPos.above(), useOnContext.getHorizontalDirection())) {
            return InteractionResult.FAIL;
        }

        Player player = useOnContext.getPlayer();

        if (player instanceof ServerPlayer serverPlayer) {
            if (blockState.getValue(PortalPointBlock.POINT)) {
                PortalPointBlock.turnOnFire(level, blockPos);
            } else {
                PortalPointBlock.LitUp(level, blockPos, serverPlayer, level.dimension());
            }

            ItemStack itemstack = useOnContext.getItemInHand();
            CriteriaTriggers.PLACED_BLOCK.trigger(serverPlayer, blockPos.above(), itemstack);
            itemstack.hurtAndBreak(1, player, (x) -> x.broadcastBreakEvent(useOnContext.getHand()));
        }

        level.playSound(player, blockPos.above(), SoundEvents.AMETHYST_CLUSTER_BREAK, SoundSource.BLOCKS, 2.0F, 0.9F + level.random.nextFloat() * 0.2F);
        return InteractionResult.sidedSuccess(level.isClientSide());
    }
}
