package io.github.c20c01.cc_fp.block.portalPoint;

import io.github.c20c01.cc_fp.CCMain;
import io.github.c20c01.cc_fp.savedData.PortalPoint;
import io.github.c20c01.cc_fp.savedData.PortalPointManager;
import io.github.c20c01.cc_fp.tool.MessageSender;
import io.github.c20c01.cc_fp.tp.TpTool;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.ParametersAreNonnullByDefault;


@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class PortalPointBlock extends Block implements EntityBlock {
    public static final int maxNameLength = 23;
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty FIRE = BooleanProperty.create("fire"); // 持续点火
    public static final BooleanProperty POINT = BooleanProperty.create("point"); // 作为传送点

    public PortalPointBlock() {
        super(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLACK).requiresCorrectToolForDrops().strength(30.0F, 1200.0F).lightLevel(state -> state.getValue(POINT) || state.getValue(FIRE) ? 15 : 0));
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(FIRE, Boolean.FALSE).setValue(POINT, Boolean.FALSE));
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PortalPointBlockEntity(pos, state);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(POINT, FIRE, FACING);
    }

    @Override
    public void fallOn(Level level, BlockState blockState, BlockPos blockPos, Entity entity, float f) {
        entity.causeFallDamage(f, 0.0F, level.damageSources().fall());
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isSignalSource(BlockState blockState) {
        return true;
    }

    @Override
    @SuppressWarnings("deprecation")
    public int getSignal(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, Direction direction) {
        return hasSignal(blockGetter, blockPos) ? 15 : 0;
    }

    private static boolean hasSignal(BlockGetter blockGetter, BlockPos blockPos) {
        if (blockGetter.getBlockEntity(blockPos) instanceof PortalPointBlockEntity blockEntity) {
            return blockEntity.hasSignal();
        }
        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void tick(BlockState blockState, ServerLevel level, BlockPos blockPos, RandomSource random) {
        setSignal(level, blockPos, Boolean.FALSE);
    }

    public static void signalPulse(ServerLevel level, BlockPos blockPos) {
        if (setSignal(level, blockPos, Boolean.TRUE)) {
            if (level.getBlockTicks().hasScheduledTick(blockPos, CCMain.PORTAL_POINT_BLOCK.get())) {
                level.getBlockTicks().clearArea(new BoundingBox(blockPos));
            }
            level.scheduleTick(blockPos, CCMain.PORTAL_POINT_BLOCK.get(), 60);
        }
    }

    private static boolean setSignal(ServerLevel level, BlockPos blockPos, boolean signal) {
        if (level.getBlockEntity(blockPos) instanceof PortalPointBlockEntity blockEntity) {
            blockEntity.setSignal(signal);
            level.updateNeighborsAt(blockPos, CCMain.PORTAL_POINT_BLOCK.get());
            return true;
        }
        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        ItemStack itemStack = player.getItemInHand(hand);

        if (hitResult.getDirection().equals(Direction.UP) && itemStack.is(CCMain.PORTAL_FLINT_AND_STEEL_ITEM.get())) {
            return InteractionResult.PASS;
        }

        if (itemStack.is(CCMain.NAME_STONE_ITEM.get()) && blockState.getValue(POINT)) {
            return InteractionResult.PASS;
        }

        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        if (itemStack.is(Items.WATER_BUCKET) && blockState.getValue(FIRE)) {
            if (!player.getAbilities().instabuild) player.setItemInHand(hand, Items.BUCKET.getDefaultInstance());
            turnOffFire(level, blockPos);
            return InteractionResult.CONSUME;
        }

        PortalPointBlockEntity blockEntity = (PortalPointBlockEntity) level.getBlockEntity(blockPos);
        if (blockEntity == null) {
            return InteractionResult.FAIL;
        }

        if (blockEntity.canUse(((ServerLevel) level).getServer(), player)) {
            NetworkHooks.openScreen((ServerPlayer) player, blockEntity, blockPos);
            return InteractionResult.SUCCESS;
        } else {
            MessageSender.gameInfo(player, Component.translatable(CCMain.TEXT_NOT_OWNER));
            return InteractionResult.FAIL;
        }
    }

    /**
     * 点亮传送核心、分为三种情况：<p>
     * 1.传送核心内无飞路粉：<br>
     * 传送核心转为点火状态，仅能持续生火、无法作为传送点使用。<p>
     * <p>
     * 2.传送核心内放有飞路粉、存在同名传送点：<br>
     * 进行提示。<p>
     * <p>
     * 3.传送核心内放有飞路粉、不存在同名传送点：<br>
     * 建立与飞路粉的命名相同的传送点，传送核心转为激活状态。
     */
    public static void LitUp(Level level, BlockPos blockPos, ServerPlayer player, ResourceKey<Level> dimension) {
        if (level.getBlockEntity(blockPos) instanceof PortalPointBlockEntity blockEntity) {
            ItemStack powder = blockEntity.getPowder();
            if (powder.isEmpty()) {
                turnOnFire(level, blockPos);
                return;
            }

            String name = TpTool.getItemName(powder);
            if (PortalPointManager.alreadyExisted(player.getServer(), name)) {
                refuseToLit(level, blockPos, player, CCMain.TEXT_ALREADY_EXISTS);
                return;
            }

            if (name.length() > maxNameLength) {
                refuseToLit(level, blockPos, player, CCMain.TEXT_NAME_TOO_LONG);
                return;
            }

            turnOnFire(level, blockPos);
            turnOnPoint(level, blockPos, blockEntity, player, dimension, name);
        }
    }

    private static void refuseToLit(Level level, BlockPos blockPos, ServerPlayer player, String reasonID) {
        MessageSender.gameInfo(player, Component.translatable(reasonID));
        level.playSound(null, blockPos, SoundEvents.VILLAGER_NO, SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    public static void turnOnFire(Level level, BlockPos blockPos) {
        level.setBlock(blockPos, level.getBlockState(blockPos).setValue(FIRE, Boolean.TRUE), Block.UPDATE_ALL_IMMEDIATE);
        level.setBlock(blockPos.above(), Blocks.FIRE.defaultBlockState(), Block.UPDATE_ALL);
    }

    private static void turnOffFire(Level level, BlockPos blockPos) {
        level.setBlock(blockPos, level.getBlockState(blockPos).setValue(FIRE, Boolean.FALSE), Block.UPDATE_ALL_IMMEDIATE);
        if (level.getBlockState(blockPos.above()).getBlock() instanceof BaseFireBlock) {
            level.removeBlock(blockPos.above(), Boolean.FALSE);
            level.playSound(null, blockPos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5F, 2.6F + (level.random.nextFloat() - level.random.nextFloat()) * 0.8F);
        }
    }

    private static void turnOnPoint(Level level, BlockPos blockPos, PortalPointBlockEntity blockEntity, ServerPlayer player, ResourceKey<Level> dimension, String name) {
        level.setBlock(blockPos, level.getBlockState(blockPos).setValue(POINT, Boolean.TRUE), Block.UPDATE_ALL_IMMEDIATE);
        blockEntity.setPointName(name);
        addPoint(player, name, blockPos.above(), dimension);
        level.playSound(null, blockPos, SoundEvents.BEACON_ACTIVATE, SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    /**
     * @param pointPos 传送点的位置(传送核心向上一格)
     */
    private static void addPoint(ServerPlayer player, String name, BlockPos pointPos, ResourceKey<Level> dimension) {
        MinecraftServer server = player.getServer();
        PortalPointManager.get(server).add(new PortalPoint(name, "", pointPos, dimension, player.getUUID(), ""));
    }

    protected static void disable(Level level, BlockPos blockPos) {
        if (level.getBlockEntity(blockPos) instanceof PortalPointBlockEntity blockEntity) {
            if (blockEntity.isPointNamed()) {
                boolean flag = PortalPointManager.get(level.getServer()).remove(blockEntity.getPointName(), blockPos.above()) != null;
                blockEntity.setPointName("");
                if (flag) {
                    level.playSound(null, blockPos, SoundEvents.BEACON_DEACTIVATE, SoundSource.BLOCKS, 1.0F, 1.0F);
                }
            }
        }
        if (level.getBlockState(blockPos).is(CCMain.PORTAL_POINT_BLOCK.get())) {
            level.setBlock(blockPos, level.getBlockState(blockPos).setValue(POINT, Boolean.FALSE).setValue(FIRE, Boolean.FALSE), Block.UPDATE_ALL_IMMEDIATE);
        }
        if (level.getBlockState(blockPos.above()).getBlock() instanceof BaseFireBlock) {
            level.removeBlock(blockPos.above(), Boolean.FALSE);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public void neighborChanged(BlockState blockState, Level level, BlockPos blockPos, Block block, BlockPos blockPos1, boolean b) {
        if (blockPos.above().equals(blockPos1)) {
            BlockState blockState1 = level.getBlockState(blockPos1);
            if (blockState1.isAir()) {
                if (blockState.getValue(FIRE)) {
                    level.setBlock(blockPos1, Blocks.FIRE.defaultBlockState(), Block.UPDATE_ALL);
                    level.playSound(null, blockPos, SoundEvents.FIRECHARGE_USE, SoundSource.BLOCKS, 1.0F, (level.random.nextFloat() - level.random.nextFloat()) * 0.2F + 1.0F);
                }
            } else if (blockState1.canOcclude()) {
                disable(level, blockPos);
            }
        }
        super.neighborChanged(blockState, level, blockPos, block, blockPos1, b);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onRemove(BlockState blockState, Level level, BlockPos blockPos, BlockState blockState1, boolean b) {
        if (!blockState.is(blockState1.getBlock())) {
            disable(level, blockPos);
            if (level.getBlockEntity(blockPos) instanceof PortalPointBlockEntity blockEntity) {
                Containers.dropContents(level, blockPos, blockEntity);
            }
        }
        super.onRemove(blockState, level, blockPos, blockState1, b);
    }
}