package io.github.c20c01.block.portalChest;

import io.github.c20c01.CCMain;
import io.github.c20c01.tp.Delayer;
import io.github.c20c01.tp.TpTool;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault

public class PortalChestBlock extends ChestBlock {
    private static final Set<Player> CHEST_CD_PLAYERS = new HashSet<>();

    public PortalChestBlock() {
        super(BlockBehaviour.Properties.of(Material.WOOD).strength(2.5F).sound(SoundType.WOOD), CCMain.PORTAL_CHEST_BLOCK_ENTITY::get);
    }

    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new PortalChestBlockEntity(blockPos, blockState);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> blockEntityType) {
        return level.isClientSide ? createTickerHelper(blockEntityType, CCMain.PORTAL_CHEST_BLOCK_ENTITY.get(), (level1, blockPos, blockState1, blockEntity) -> PortalChestBlockEntity.lidAnimateTick(blockEntity)) : null;
    }

    @Override
    public void tick(BlockState blockState, ServerLevel level, BlockPos blockPos, Random random) {
    }

    @Override
    public InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (level.isClientSide) return InteractionResult.SUCCESS;
        if (!CHEST_CD_PLAYERS.add(player)) return InteractionResult.PASS;
        Delayer.add(50, () -> CHEST_CD_PLAYERS.remove(player));
        if (level.getBlockEntity(blockPos) instanceof PortalChestBlockEntity blockEntity && blockEntity.isNamedChest()) {
            String name = blockEntity.getDisplayName().getString();
            teleportTrap(blockState, level, blockPos, player, name);
            return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }

    @Mod.EventBusSubscriber(modid = CCMain.ID)
    public static class ServerStopping {
        @SubscribeEvent
        public static void stoppingEvent(ServerStoppingEvent event) {
            CHEST_CD_PLAYERS.clear();
        }
    }

    @Override
    public void onRemove(BlockState blockState, Level level, BlockPos blockPos, BlockState blockState1, boolean b) {
        level.updateNeighbourForOutputSignal(blockPos, this);

        if (blockState.hasBlockEntity() && (!blockState.is(blockState1.getBlock()) || !blockState1.hasBlockEntity())) {
            level.removeBlockEntity(blockPos);
        }
    }

    private static void playOpenSound(Level level, BlockPos blockPos) {
        BlockState blockState = level.getBlockState(blockPos);
        if (blockState.getBlock() instanceof ChestBlock) {
            ChestType chesttype = blockState.getValue(ChestBlock.TYPE);
            double d0 = (double) blockPos.getX() + 0.5D;
            double d1 = (double) blockPos.getY() + 0.5D;
            double d2 = (double) blockPos.getZ() + 0.5D;
            if (chesttype != ChestType.SINGLE) {
                Direction direction = ChestBlock.getConnectedDirection(blockState);
                d0 += (double) direction.getStepX() * 0.5D;
                d2 += (double) direction.getStepZ() * 0.5D;
            }
            level.playSound(null, d0, d1, d2, SoundEvents.CHEST_OPEN, SoundSource.BLOCKS, 0.5F, level.random.nextFloat() * 0.1F + 0.9F);
        }
    }

    private static void teleportTrap(BlockState blockState, Level level, BlockPos blockPos, Player player, String name) {
        level.blockEvent(blockPos, blockState.getBlock(), 1, 0);
        playOpenSound(level, blockPos);
        TpTool.addEffect(player, MobEffects.BLINDNESS, 50, 0);
        TpTool.addEffect(player, MobEffects.MOVEMENT_SLOWDOWN, 20, 255);
        Delayer.add(40, () -> TpTool.forceTeleportEntity(player, name));
    }
}
