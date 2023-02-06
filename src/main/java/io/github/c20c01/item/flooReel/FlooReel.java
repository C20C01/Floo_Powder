package io.github.c20c01.item.flooReel;

import io.github.c20c01.CCMain;
import io.github.c20c01.tp.TpTool;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault

public class FlooReel extends Item {
    public FlooReel(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide) {
            return InteractionResultHolder.pass(stack);
        }
        player.getCooldowns().addCooldown(stack.getItem(), 100);
        if (TpTool.forceTeleportEntity(player, TpTool.getItemName(stack)) == TpTool.Result.success) {
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
            level.playSound(null, player.blockPosition(), SoundEvents.FIRECHARGE_USE, SoundSource.BLOCKS, 2.0F, 0.9F + level.random.nextFloat() * 0.2F);
        }
        return InteractionResultHolder.success(stack);
    }

    public static ItemStack getNamedReel(String name) {
        ItemStack itemStack = new ItemStack(CCMain.FLOO_REEL_ITEM.get());
        itemStack.setHoverName(new TextComponent(name));
        return itemStack;
    }
}
