package io.github.c20c01.cc_fp.tool;

import net.minecraft.Util;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class MessageSender {
    public static void gameInfo(ServerPlayer player , Component component){
        player.sendMessage(component, ChatType.GAME_INFO, Util.NIL_UUID);
    }

    public static void chat(ServerPlayer player , Component component){
        player.sendMessage(component, ChatType.CHAT, Util.NIL_UUID);
    }
}
