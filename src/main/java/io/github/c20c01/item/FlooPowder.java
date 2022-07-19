package io.github.c20c01.item;

import io.github.c20c01.block.portalFire.BasePortalFireBlock;
import io.github.c20c01.command.ModSettings;
import io.github.c20c01.tp.TpTool;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class FlooPowder extends Item {

    public FlooPowder(Item.Properties properties) {
        super(properties);
    }

    @Override
    public void onDestroyed(ItemEntity itemEntity, DamageSource damageSource) {
        if (damageSource.isFire()) {
            var pos = itemEntity.position();
            var blockPos = new BlockPos(pos);
            var level = itemEntity.level;
            if (BasePortalFireBlock.canChangeToPortalFire(blockPos, level)) {
                changeFireBlock(blockPos, level, itemEntity.getItem().getDisplayName().getString());
            } else {
                explode(pos, level);
            }
        }
    }

    private void explode(Vec3 inPos, Level level) {
        level.explode(null, inPos.x(), inPos.y(), inPos.z(), 0.1F, false, Explosion.BlockInteraction.NONE);
    }

    private void changeFireBlock(BlockPos inPos, Level level, String name) {
        BasePortalFireBlock.changeAllFireBlock(inPos, level, name);
        level.playSound(null, inPos, SoundEvents.FIRECHARGE_USE, SoundSource.BLOCKS, 5.0F, 0.9F + level.random.nextFloat() * 0.2F);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        if (player instanceof ServerPlayer serverPlayer) {
            if (serverPlayer.gameMode.isCreative() || ModSettings.get(ModSettings.HAND_USE_FP)) {
                ItemStack stack = player.getItemInHand(hand);
                player.getCooldowns().addCooldown(stack.getItem(), 60);
                TpTool.gogo(serverPlayer, stack.getDisplayName().getString(), level, player.getOnPos());
            }
        }
        return InteractionResultHolder.pass(player.getItemInHand(hand));
    }
}
