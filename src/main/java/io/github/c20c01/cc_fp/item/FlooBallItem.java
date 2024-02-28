package io.github.c20c01.cc_fp.item;

import io.github.c20c01.cc_fp.block.portalFire.BasePortalFireBlock;
import io.github.c20c01.cc_fp.entity.FlooBall;
import io.github.c20c01.cc_fp.tp.TpTool;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.AbstractProjectileDispenseBehavior;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault

public class FlooBallItem extends Item implements IDestroyByFireToUse {
    public FlooBallItem(Properties properties) {
        super(properties);
        DispenserBlock.registerBehavior(this, new AbstractProjectileDispenseBehavior() {
            @Override
            protected Projectile getProjectile(Level level, Position position, ItemStack itemStack) {
                return Util.make(new FlooBall(level, position.x(), position.y(), position.z()), (x) -> x.setItem(itemStack));
            }

            @Override
            protected float getUncertainty() {
                return super.getUncertainty() * 1.5F;
            }

            @Override
            protected float getPower() {
                return super.getPower() * 1.5F;
            }

            @Override
            protected void playSound(BlockSource blockSource) {
                blockSource.getLevel().levelEvent(1018, blockSource.getPos(), 0);
            }
        });
    }

    @Override
    public void destroyByFire(ItemEntity itemEntity) {
        Level level = itemEntity.level();
        BlockPos blockPos = IDestroyByFireToUse.getBlockPos(itemEntity);
        if (BasePortalFireBlock.canChangeToPortalFire(blockPos, level)) {
            IDestroyByFireToUse.changeFireBlock(blockPos, level, TpTool.getItemName(itemEntity.getItem()));
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.BLAZE_SHOOT, SoundSource.NEUTRAL, 1F, level.getRandom().nextFloat() * 0.2F + 0.8F);
        if (!level.isClientSide) {
            FlooBall flooBall = new FlooBall(level, player);
            flooBall.setItem(itemStack);
            flooBall.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.5F, 1.0F);
            level.addFreshEntity(flooBall);
            player.getCooldowns().addCooldown(this, 100);
        }

        player.awardStat(Stats.ITEM_USED.get(this));
        if (!player.getAbilities().instabuild) {
            itemStack.shrink(1);
        }

        return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide());
    }
}
