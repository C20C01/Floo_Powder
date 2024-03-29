package io.github.c20c01.cc_fp.block.powderGiver;

import io.github.c20c01.cc_fp.CCMain;
import io.github.c20c01.cc_fp.client.gui.screen.PowderGiverScreen;
import io.github.c20c01.cc_fp.savedData.shareData.SharePointInfos;
import io.github.c20c01.cc_fp.tool.MessageSender;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault

public class PowderGiverBlock extends Block implements EntityBlock {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    public PowderGiverBlock() {
        super(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(10.0F, 1200.0F).noLootTable().lightLevel((x) -> 15));
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PowderGiverBlockEntity(pos, state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public PushReaction getPistonPushReaction(BlockState blockState) {
        return PushReaction.BLOCK;
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (player.getAbilities().instabuild && player.getItemInHand(hand).is(CCMain.NAME_STONE_ITEM.get())) {
            return InteractionResult.PASS;
        }
        PowderGiverBlockEntity blockEntity = (PowderGiverBlockEntity) level.getBlockEntity(blockPos);
        if (blockEntity == null) {
            return InteractionResult.PASS;
        }

        if (player.isShiftKeyDown()) {
            if (!level.isClientSide) MessageSender.gameInfo(player, Component.literal(blockEntity.getDesc()));
        } else {
            usePowderGiver(level, blockEntity, player);
        }

        return InteractionResult.SUCCESS;
    }

    private static void usePowderGiver(Level level, PowderGiverBlockEntity blockEntity, Player player) {
        if (level.isClientSide) {
            openGui();
        } else {
            String groupName = blockEntity.getPublicGroup();
            SharePointInfos.sendPointInfosToC((ServerPlayer) player, blockEntity.getCheckType(), groupName);
        }
    }

    @OnlyIn(Dist.CLIENT)
    private static void openGui() {
        int inventoryKeyID = Minecraft.getInstance().options.keyInventory.getKey().getValue();
        Minecraft.getInstance().setScreen(new PowderGiverScreen(inventoryKeyID));
    }
}