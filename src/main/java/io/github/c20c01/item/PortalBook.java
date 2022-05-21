package io.github.c20c01.item;

import io.github.c20c01.CCMain;
import io.github.c20c01.pos.PosInfo;
import io.github.c20c01.pos.PosMap;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.Util;
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
        return InteractionResultHolder.pass(player.getItemInHand(hand));
    }
}
