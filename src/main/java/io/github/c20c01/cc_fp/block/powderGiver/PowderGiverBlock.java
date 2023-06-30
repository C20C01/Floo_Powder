package io.github.c20c01.cc_fp.block.powderGiver;

import io.github.c20c01.cc_fp.CCMain;
import io.github.c20c01.cc_fp.client.gui.screen.PowderGiverScreen;
import io.github.c20c01.cc_fp.network.GiveFlooPowder;
import io.github.c20c01.cc_fp.savedData.PortalPointManager;
import io.github.c20c01.cc_fp.savedData.shareData.SharePointInfos;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
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
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault

public class PowderGiverBlock extends Block implements EntityBlock {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final EnumProperty<PortalPointManager.CheckType> TYPE = EnumProperty.create("type", PortalPointManager.CheckType.class);
    public static final List<PortalPointManager.CheckType> TYPES = new ArrayList<>();

    public PowderGiverBlock() {
        super(BlockBehaviour.Properties.of(Material.STONE, MaterialColor.STONE).strength(10.0F, 1200.0F).noDrops().lightLevel((x) -> 15));
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
        this.registerDefaultState(this.stateDefinition.any().setValue(TYPE, PortalPointManager.CheckType.PUBLIC));
        TYPES.add(PortalPointManager.CheckType.ALL_AVAILABLE);
        TYPES.add(PortalPointManager.CheckType.MINE);
        TYPES.add(PortalPointManager.CheckType.OTHERS);
        TYPES.add(PortalPointManager.CheckType.PUBLIC);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PowderGiverBlockEntity(pos, state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
        builder.add(TYPE);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    @SuppressWarnings("deprecation")
    public PushReaction getPistonPushReaction(BlockState blockState) {
        return PushReaction.BLOCK;
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (player.getAbilities().instabuild && player.getItemInHand(hand).is(CCMain.NAME_STONE_ITEM.get())) {
            return InteractionResult.PASS;
        }
        if (level.isClientSide) {
            openGui();
            player.playSound(SoundEvents.NOTE_BLOCK_BIT, 1F, 10F);
        } else {
            GiveFlooPowder.setPlayerList(Objects.requireNonNull(player.getServer()).getPlayerList());
            String groupName = ((PowderGiverBlockEntity) Objects.requireNonNull(level.getBlockEntity(blockPos))).getPublicGroup();
            SharePointInfos.sendPointInfosToC((ServerPlayer) player, level.getBlockState(blockPos).getValue(TYPE), groupName);
        }
        return InteractionResult.SUCCESS;
    }

    @OnlyIn(Dist.CLIENT)
    public static void openGui() {
        int inventoryKeyID = Minecraft.getInstance().options.keyInventory.getKey().getValue();
        Minecraft.getInstance().setScreen(new PowderGiverScreen(inventoryKeyID));
    }

    public static PortalPointManager.CheckType getNextType(PortalPointManager.CheckType type) {
        int index = TYPES.indexOf(type) + 1;
        if (index >= TYPES.size()) index = 0;
        return TYPES.get(index);
    }
}