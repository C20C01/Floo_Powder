package io.github.c20c01.cc_fp.network;

import io.github.c20c01.cc_fp.CCMain;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

public class UpdateItemStack {
    public static void update(ServerPlayer serverPlayer, int slot, ItemStack itemStack) {
        if (serverPlayer != null) {
            serverPlayer.getInventory().setItem(slot, itemStack);
            if (itemStack.is(CCMain.NAME_STONE_ITEM.get())) {
                if (!serverPlayer.getAbilities().instabuild) {
                    itemStack.hurtAndBreak(8, serverPlayer, (x) -> x.broadcastBreakEvent(slot == 40 ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND));
                    serverPlayer.giveExperienceLevels(-1);
                }
                serverPlayer.level().playSound(null, serverPlayer.blockPosition(), SoundEvents.ANVIL_USE, serverPlayer.getSoundSource(), 1F, 1F);
            }
        }
    }
}
