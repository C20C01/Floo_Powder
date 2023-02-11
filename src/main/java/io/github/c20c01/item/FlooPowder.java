package io.github.c20c01.item;

import io.github.c20c01.CCMain;
import io.github.c20c01.block.portalFire.BasePortalFireBlock;
import io.github.c20c01.tp.TpTool;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class FlooPowder extends Item implements DestroyByFireToUse {

    public FlooPowder(Properties properties) {
        super(properties);
    }

    @Override
    public void onDestroyed(ItemEntity itemEntity, DamageSource damageSource) {
        if (damageSource.isFire()) {
            Level level = itemEntity.level;
            BlockPos blockPos = new BlockPos(itemEntity.position());

            if (BasePortalFireBlock.canChangeToPortalFire(blockPos, level)) {
                changeFireBlock(blockPos, level, TpTool.getItemName(itemEntity.getItem()));
            }
        }
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide && player.getAbilities().instabuild) {
            player.getCooldowns().addCooldown(stack.getItem(), 100);
            TpTool.forceTeleportEntity(player, TpTool.getItemName(stack));
            return InteractionResultHolder.success(stack);
        }
        return InteractionResultHolder.pass(stack);
    }

    private void changeFireBlock(BlockPos blockPos, Level level, String name) {
        BasePortalFireBlock.changeAllToPortalFire(blockPos, level, name);
        level.playSound(null, blockPos, SoundEvents.FIRECHARGE_USE, SoundSource.BLOCKS, 5.0F, 0.9F + level.random.nextFloat() * 0.2F);
    }

    public static ItemStack getNamedPowder(String name) {
        ItemStack itemStack = new ItemStack(CCMain.FLOO_POWDER_ITEM.get());
        itemStack.setHoverName(new TextComponent(name));
        return itemStack;
    }
}
