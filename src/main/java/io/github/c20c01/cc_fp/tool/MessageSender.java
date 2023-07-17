package io.github.c20c01.cc_fp.tool;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

public class MessageSender {
    public static void gameInfo(Player player, Component component) {
        player.displayClientMessage(component, Boolean.TRUE);
    }

    public static void chat(Player player, Component component) {
        player.displayClientMessage(component, Boolean.FALSE);
    }
}
