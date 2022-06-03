package io.github.c20c01.block;

import io.github.c20c01.CCMain;
import io.github.c20c01.gui.FlooPowderGiverGui;
import io.github.c20c01.gui.GuiData;
import io.github.c20c01.network.CCNetwork;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault

public class FlooPowderGiverBlock extends Block {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    private static final HashMap<Integer, ServerPlayer> PLAYERS = new HashMap<>();

    public FlooPowderGiverBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
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
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (level.isClientSide) {
            openGui(player.getItemInHand(hand).is(CCMain.PORTAL_BOOK_ITEM.get()));
            player.playSound(SoundEvents.NOTE_BLOCK_BIT,0.6F,10F);
            return InteractionResult.SUCCESS;
        } else {
            ServerPlayer serverPlayer = (ServerPlayer) player;
            int code = serverPlayer.hashCode();
            GuiData.sendToClient(serverPlayer, code);
            PLAYERS.put(code, serverPlayer);
            return InteractionResult.CONSUME;
        }
    }

    public static void clear() {
        PLAYERS.clear();
    }

    @OnlyIn(Dist.CLIENT)
    public static void openGui(boolean editMode) {
        FlooPowderGiverGui.editMode = editMode;
        Minecraft.getInstance().setScreen(new FlooPowderGiverGui(TextComponent.EMPTY));
    }

    public static void handle_C(String name, int code) {
        ServerPlayer serverPlayer = PLAYERS.get(code);
        if (serverPlayer == null) {
            CCNetwork.CHANNEL_NAME_TO_S.sendToServer(new CCNetwork.PowderNamePacket(name, code));
        } else {
            handle_S(name, code);
        }
    }

    public static void handle_S(String name, int code) {
        if (name.equals("")) {
            PLAYERS.remove(code);
        } else {
            givePowder_S(name, code);
        }
    }

    public static void givePowder_S(String name, int code) {
        ServerPlayer serverPlayer = PLAYERS.get(code);
        if (serverPlayer != null) {
            ItemStack itemStack = new ItemStack(CCMain.FLOO_POWDER_ITEM.get()).setHoverName(new TextComponent(name));
            serverPlayer.getInventory().add(itemStack);
            serverPlayer.level.playSound(null, serverPlayer.getX(), serverPlayer.getY(), serverPlayer.getZ(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2F, ((serverPlayer.getRandom().nextFloat() - serverPlayer.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F);
            PLAYERS.remove(code);
        }
    }
}