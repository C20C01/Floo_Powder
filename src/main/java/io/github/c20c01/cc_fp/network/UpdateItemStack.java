package io.github.c20c01.cc_fp.network;

import io.github.c20c01.cc_fp.CCMain;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;

public class UpdateItemStack {
    private static PlayerList playerList;

    public static void setPlayerList(PlayerList list) {
        UpdateItemStack.playerList = list;
    }

    public static void update(UUID uuid, int slot, ItemStack itemStack) {
        if (playerList == null) {
            return;
        }
        ServerPlayer player = playerList.getPlayer(uuid);
        if (player != null) {
            player.getInventory().setItem(slot, itemStack);
            if (itemStack.is(CCMain.NAME_STONE_ITEM.get())) {
                if (!player.getAbilities().instabuild) {
                    itemStack.hurtAndBreak(8, player, (x) -> x.broadcastBreakEvent(slot == 40 ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND));
                    player.giveExperienceLevels(-1);
                }
                player.playSound(SoundEvents.ANVIL_USE);
            }
        }
    }
}
