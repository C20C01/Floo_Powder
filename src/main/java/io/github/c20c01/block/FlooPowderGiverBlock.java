package io.github.c20c01.block;

import io.github.c20c01.CCMain;
import io.github.c20c01.gui.FlooPowderGiverGui;
import io.github.c20c01.pos.PosMap;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class FlooPowderGiverBlock extends Block {

    public FlooPowderGiverBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        var gui = new FlooPowderGiverGui(TextComponent.EMPTY);
        if (level.isClientSide) {
            if (player.getItemInHand(hand).is(CCMain.PORTAL_BOOK_ITEM.get()))
                FlooPowderGiverGui.op = true;
            Minecraft.getInstance().setScreen(gui);
            return InteractionResult.SUCCESS;
        } else {
            var map = PosMap.getMap();
            FlooPowderGiverGui.setMap(map);
            return InteractionResult.CONSUME;
        }
    }

}
