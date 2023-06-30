package io.github.c20c01.cc_fp.item;

import io.github.c20c01.cc_fp.block.portalFire.BasePortalFireBlock;
import io.github.c20c01.cc_fp.tp.TpTool;
import io.github.c20c01.cc_fp.CCMain;
import io.github.c20c01.cc_fp.config.CCConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class FlooPowder extends Item implements IDestroyByFireToUse {

    public FlooPowder(Properties properties) {
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
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide && (player.getAbilities().instabuild || CCConfig.canUseInSurvival.get())) {
            player.getCooldowns().addCooldown(this, 100);
            TpTool.forceTeleportEntity(player, TpTool.getItemName(stack));
            return InteractionResultHolder.success(stack);
        }
        return InteractionResultHolder.pass(stack);
    }

    public static ItemStack getNamedPowder(String name) {
        ItemStack itemStack = CCMain.FLOO_POWDER_ITEM.get().getDefaultInstance();
        if (!name.isEmpty()) {
            itemStack.setHoverName(new TextComponent(name));
        }
        return itemStack;
    }
}
