package io.github.c20c01.cc_fp.item.flooReel;

import io.github.c20c01.cc_fp.CCMain;
import io.github.c20c01.cc_fp.block.portalFire.BasePortalFireBlock;
import io.github.c20c01.cc_fp.item.IDestroyByFireToUse;
import io.github.c20c01.cc_fp.particle.PlayParticle;
import io.github.c20c01.cc_fp.particle.SendParticle;
import io.github.c20c01.cc_fp.tp.TpTool;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault

public class FlooReel extends Item implements IDestroyByFireToUse {
    public FlooReel(Properties properties) {
        super(properties);
    }

    @Override
    public void destroyByFire(ItemEntity itemEntity) {
        Level level = itemEntity.level();
        BlockPos blockPos = itemEntity.getOnPos();
        //TODO:查错
        if (BasePortalFireBlock.canChangeToPortalFire(blockPos, level)) {
            IDestroyByFireToUse.changeFireBlock(blockPos, level, TpTool.getItemName(itemEntity.getItem()));
        }
    }

    @Override
    public int getUseDuration(ItemStack p_151222_) {
        return 64;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack itemStack) {
        return UseAnim.BOW;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        player.startUsingItem(hand);
        player.getCooldowns().addCooldown(this, 128);
        return InteractionResultHolder.consume(player.getItemInHand(hand));
    }

    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack itemStack, int tick) {
        if (tick % 8 != 0) return;
        if (livingEntity instanceof Player player) {
            Vec3 pos = new Vec3(player.getX(), (player.getY() + player.getEyeHeight() * 0.7), player.getZ());
            float size = tick / 8F;
            if (level instanceof ServerLevel serverLevel) {
                SendParticle.suck(serverLevel, (ServerPlayer) player, SendParticle.Particles.FLAME, pos, size);
            } else {
                PlayParticle.play(SendParticle.Particles.FLAME, SendParticle.Modes.SUCK, pos, new float[]{size});
            }
        }
    }

    @Override
    public ItemStack finishUsingItem(ItemStack itemStack, Level level, LivingEntity livingEntity) {
        level.playSound(null, livingEntity.blockPosition(), SoundEvents.FIRECHARGE_USE, SoundSource.PLAYERS, 1.0F, (level.random.nextFloat() - level.random.nextFloat()) * 0.2F + 1.0F);
        if (livingEntity instanceof Player player && !player.getAbilities().instabuild) {
            itemStack.shrink(1);
        }
        if (!level.isClientSide) {
            TpTool.forceTeleportEntity(livingEntity, TpTool.getItemName(itemStack));
        }
        return itemStack;
    }

    public static ItemStack getNamedReel(String name) {
        ItemStack itemStack = CCMain.FLOO_REEL_ITEM.get().getDefaultInstance();
        itemStack.setHoverName(Component.literal(name));
        return itemStack;
    }
}
