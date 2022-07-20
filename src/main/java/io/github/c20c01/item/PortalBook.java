package io.github.c20c01.item;

import io.github.c20c01.CCMain;
import io.github.c20c01.block.portalFire.PortalFireBlockEntity;
import io.github.c20c01.pos.PosInfo;
import io.github.c20c01.pos.PosMap;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class PortalBook extends Item {

    public PortalBook(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (player instanceof ServerPlayer serverPlayer) {
            if (Minecraft.getInstance().hitResult instanceof BlockHitResult hitResult) {
                var blockPos = hitResult.getBlockPos();
                if (level.getBlockEntity(blockPos) instanceof PortalFireBlockEntity blockEntity) {
                    setPortalFire(serverPlayer, blockEntity);
                    return InteractionResultHolder.pass(player.getItemInHand(hand));
                }
            }
            showPosInfo(serverPlayer);
        }
        return InteractionResultHolder.pass(player.getItemInHand(hand));
    }

    private static void setPortalFire(ServerPlayer player, PortalFireBlockEntity blockEntity) {
        blockEntity.lasting = !blockEntity.lasting;
        var text = new TranslatableComponent(CCMain.TEXT_SET_PORTAL_FIRE_BOOK).append(": " + blockEntity.lasting);
        player.sendMessage(text, ChatType.GAME_INFO, Util.NIL_UUID);
    }

    private static void showPosInfo(ServerPlayer serverPlayer) {
        var map = PosMap.getMap();
        var keySet = map.keySet();
        if (!keySet.isEmpty()) {
            var text = new TranslatableComponent(CCMain.TEXT_FOUND_BOOK);
            serverPlayer.sendMessage(text, ChatType.CHAT, Util.NIL_UUID);
            for (var key : keySet) {
                PosInfo i = map.get(key);
                var text1 = new TextComponent(key + ": " + i);
                serverPlayer.sendMessage(text1, ChatType.CHAT, Util.NIL_UUID);
            }
        } else {
            var text = new TranslatableComponent(CCMain.TEXT_NOT_FOUND_BOOK);
            serverPlayer.sendMessage(text, ChatType.CHAT, Util.NIL_UUID);
        }
    }
}
