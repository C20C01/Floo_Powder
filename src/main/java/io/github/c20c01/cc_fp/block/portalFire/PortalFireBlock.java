package io.github.c20c01.cc_fp.block.portalFire;

import io.github.c20c01.cc_fp.block.FireBaseBlock;
import io.github.c20c01.cc_fp.client.particles.PortalFireParticle;
import io.github.c20c01.cc_fp.item.IDestroyByFireToUse;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.Vec3;

import javax.annotation.ParametersAreNonnullByDefault;


@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class PortalFireBlock extends BasePortalFireBlock implements EntityBlock {
    public static final BooleanProperty LASTING = BooleanProperty.create("lasting"); // 成功传送后不熄灭

    public PortalFireBlock() {
        super();
        this.registerDefaultState(this.stateDefinition.any().setValue(TEMPORARY, Boolean.FALSE).setValue(LASTING, Boolean.FALSE));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(TEMPORARY).add(LASTING);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PortalFireBlockEntity(pos, state);
    }

    @Override
    public void entityInside(BlockState blockState, Level level, BlockPos blockPos, Entity entity) {
        if (entity instanceof ItemEntity itemEntity && itemEntity.getItem().getItem() instanceof IDestroyByFireToUse) {
            entity.hurt(level.damageSources().inFire(), 1.0F);
            return;
        }
        if (!level.isClientSide && entity.getPassengers().isEmpty()) {
            teleportEntityInside(blockState, level, blockPos, entity);
        }
    }

    private static void teleportEntityInside(BlockState blockState, Level level, BlockPos blockPos, Entity entity) {
        if (level.getBlockEntity(blockPos) instanceof PortalFireBlockEntity blockEntity) {
            boolean lasting = blockState.getValue(LASTING);
            boolean temporary = blockState.getValue(TEMPORARY);
            boolean saveMovement = level.getBlockState(blockPos.below()).getBlock() instanceof FireBaseBlock;
            Vec3 movement = null;
            if (saveMovement) {
                movement = entity.getDeltaMovement();
                // 玩家的竖直方向上的动量不准，需要靠下落距离来算
                if (entity instanceof Player) {
                    movement = new Vec3(movement.x, (float) (Math.sqrt(entity.fallDistance * 2 / 0.08) * 0.08), movement.z);
                }
            }

            var result = blockEntity.teleportEntity(entity, movement, temporary);

            switch (result) {
                case fail -> BasePortalFireBlock.removeAllPortalFire(blockPos, level, blockState);
                case success -> {
                    if (!lasting){
                        BasePortalFireBlock.removeAllPortalFire(blockPos, level, blockState);
                    }
                }
            }
        }
    }

    public static boolean changeLasting(Level level, BlockPos blockPos) {
        BlockState blockState = level.getBlockState(blockPos);
        boolean lasting = !blockState.getValue(PortalFireBlock.LASTING);
        level.setBlock(blockPos, blockState.setValue(LASTING, lasting), Block.UPDATE_ALL_IMMEDIATE);
        return lasting;
    }

    public static void setLasting(Level level, BlockPos blockPos, boolean lasting) {
        level.setBlock(blockPos, level.getBlockState(blockPos).setValue(LASTING, lasting), Block.UPDATE_ALL_IMMEDIATE);
    }

    @Override
    public void animateTick(BlockState blockState, Level level, BlockPos blockPos, RandomSource random) {
        super.animateTick(blockState, level, blockPos, random);
        var type = blockState.getValue(LASTING) ? PortalFireParticle.Option.TYPE.IN_LASTING : PortalFireParticle.Option.TYPE.IN;
        playParticle(new PortalFireParticle.Option(type), level, Vec3.atCenterOf(blockPos), random);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onRemove(BlockState blockState, Level level, BlockPos blockPos, BlockState blockState1, boolean b) {
        if (!blockState.is(blockState1.getBlock())) {
            removeTemporaryFire(level, blockPos, Boolean.TRUE);
        }
    }
}