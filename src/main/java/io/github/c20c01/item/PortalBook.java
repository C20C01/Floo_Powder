package io.github.c20c01.item;

import io.github.c20c01.pos.PosInfo;
import io.github.c20c01.pos.PosMap;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.Util;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class PortalBook extends Item {

    public PortalBook(Properties properties) {
        super(properties);
        CCItems.PortalBook = this;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.sendMessage(new TextComponent("*******************************"), ChatType.CHAT, Util.NIL_UUID);
            for (var key : PosMap.getMap().keySet()) {
                PosInfo i = PosMap.getMap().get(key);
                var text = new TextComponent(key + ": " + i);
                serverPlayer.sendMessage(text, ChatType.CHAT, Util.NIL_UUID);
            }
        }
        return InteractionResultHolder.pass(player.getItemInHand(hand));
    }
}
