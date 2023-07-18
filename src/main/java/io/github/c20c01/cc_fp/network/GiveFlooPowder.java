package io.github.c20c01.cc_fp.network;

import io.github.c20c01.cc_fp.item.FlooPowder;
import io.github.c20c01.cc_fp.savedData.shareData.PortalPointInfo;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;

public class GiveFlooPowder {
    public static void give(ServerPlayer serverPlayer, PortalPointInfo info) {
        if (serverPlayer != null) {
            serverPlayer.getInventory().add(FlooPowder.getNamedPowder(info.name()));
            serverPlayer.playSound(SoundEvents.ITEM_PICKUP, 0.2F, ((serverPlayer.getRandom().nextFloat() - serverPlayer.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F);
        }
    }
}
