package io.github.c20c01.cc_fp.item;

import io.github.c20c01.cc_fp.block.portalFire.BasePortalFireBlock;
import io.github.c20c01.cc_fp.tp.TpTool;
import io.github.c20c01.cc_fp.entity.FlooBall;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault

public class FlooBallItem extends Item implements IDestroyByFireToUse {
    public FlooBallItem(Properties properties) {
        super(properties);
    }

    @Override
    public void destroyByFire(ItemEntity itemEntity) {
        Level level = itemEntity.level;
        BlockPos blockPos = new BlockPos(itemEntity.position());

        if (BasePortalFireBlock.canChangeToPortalFire(blockPos, level)) {
            IDestroyByFireToUse.changeFireBlock(blockPos, level, TpTool.getItemName(itemEntity.getItem()));
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.BLAZE_SHOOT, SoundSource.NEUTRAL, 1F, level.getRandom().nextFloat() * 0.4F + 0.6F);
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
