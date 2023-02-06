package io.github.c20c01.block.portalFire;

import io.github.c20c01.client.particles.PortalFireParticle;
import io.github.c20c01.item.destroyByFireToUse.DestroyByFireToUse;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.Vec3;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Random;


@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class PortalFireBlock extends BasePortalFireBlock implements EntityBlock {
    public static final BooleanProperty LASTING = BooleanProperty.create("lasting"); // 成功传送后不熄灭

    public PortalFireBlock() {
        super();
        this.registerDefaultState(this.stateDefinition.any().setValue(LASTING, Boolean.FALSE));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LASTING);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PortalFireBlockEntity(pos, state);
    }

    @Override
    public void entityInside(BlockState blockState, Level level, BlockPos blockPos, Entity entity) {
        if (entity instanceof ItemEntity itemEntity && itemEntity.getItem().getItem() instanceof DestroyByFireToUse) {
            entity.hurt(DamageSource.IN_FIRE, 1.0F);
            return;
        }
        if (!level.isClientSide && entity.getPassengers().isEmpty()) {
            // 不传送有乘客的载具，方便后面实现不下车传送
            if (level.getBlockEntity(blockPos) instanceof PortalFireBlockEntity blockEntity) {
                blockEntity.teleportEntity(level, blockPos, entity, entity.getDeltaMovement(), blockState.getValue(LASTING));
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
    public void animateTick(BlockState blockState, Level level, BlockPos blockPos, Random random) {
        super.animateTick(blockState, level, blockPos, random);
        var type = blockState.getValue(LASTING) ? PortalFireParticle.Option.TYPE.IN_LASTING : PortalFireParticle.Option.TYPE.IN;
        playParticle(new PortalFireParticle.Option(type), level, Vec3.atCenterOf(blockPos), random);
    }
}