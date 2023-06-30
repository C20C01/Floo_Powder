package io.github.c20c01.cc_fp.network;

import io.github.c20c01.cc_fp.item.FlooPowder;
import io.github.c20c01.cc_fp.savedData.shareData.PortalPointInfo;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

import java.util.UUID;

public class GiveFlooPowder {
    private static PlayerList playerList;

    public static void setPlayerList(PlayerList list) {
        playerList = list;
    }

    public static void give(UUID uuid, PortalPointInfo info) {
        ServerPlayer serverPlayer = playerList.getPlayer(uuid);
        if (serverPlayer != null) {
            serverPlayer.getInventory().add(FlooPowder.getNamedPowder(info.name()));
            serverPlayer.level.playSound(null, serverPlayer.getX(), serverPlayer.getY(), serverPlayer.getZ(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2F, ((serverPlayer.getRandom().nextFloat() - serverPlayer.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F);
        }
    }
}
