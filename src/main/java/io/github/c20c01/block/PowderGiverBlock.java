package io.github.c20c01.block;

import io.github.c20c01.client.gui.screen.PowderGiverScreen;
import io.github.c20c01.item.FlooPowder;
import io.github.c20c01.savedData.shareData.PortalPointInfo;
import io.github.c20c01.savedData.shareData.SharePointInfos;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;
import java.util.UUID;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault

public class PowderGiverBlock extends Block {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    private static PlayerList playerList;

    public PowderGiverBlock() {
        super(BlockBehaviour.Properties.of(Material.STONE, MaterialColor.STONE).strength(10.0F, 1200.0F).noDrops().lightLevel((x) -> 15));
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
    public PushReaction getPistonPushReaction(BlockState blockState) {
        return PushReaction.BLOCK;
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (level.isClientSide) {
            openGui();
            player.playSound(SoundEvents.NOTE_BLOCK_BIT, 1F, 10F);
            return InteractionResult.SUCCESS;
        } else {
            playerList = Objects.requireNonNull(player.getServer()).getPlayerList();
            SharePointInfos.sendPointInfosToC((ServerPlayer) player);
            return InteractionResult.CONSUME;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static void openGui() {
        int inventoryKeyID = Minecraft.getInstance().options.keyInventory.getKey().getValue();
        Minecraft.getInstance().setScreen(new PowderGiverScreen(TextComponent.EMPTY, inventoryKeyID));
    }

    public static void givePowder(UUID uuid, PortalPointInfo info) {
        ServerPlayer serverPlayer = playerList.getPlayer(uuid);
        if (serverPlayer != null) {
            serverPlayer.getInventory().add(FlooPowder.getNamedPowder(info.name()));
            serverPlayer.level.playSound(null, serverPlayer.getX(), serverPlayer.getY(), serverPlayer.getZ(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2F, ((serverPlayer.getRandom().nextFloat() - serverPlayer.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F);
        }
    }
}