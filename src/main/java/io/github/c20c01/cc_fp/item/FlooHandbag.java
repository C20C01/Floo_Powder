package io.github.c20c01.cc_fp.item;

import io.github.c20c01.cc_fp.CCMain;
import io.github.c20c01.cc_fp.tool.MessageSender;
import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LayeredCauldronBlock;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class FlooHandbag extends Item {
    private static final int BAR_COLOR = Mth.color(0.4F, 0.4F, 1.0F);
    private static final int MAX_SIZE = 64;

    public FlooHandbag(Properties properties) {
        super(properties);
        CauldronInteraction.WATER.put(this, (blockState, level, blockPos, player, hand, itemStack) -> {
            Item item = itemStack.getItem();
            if (!item.equals(this)) {
                return InteractionResult.PASS;
            } else {
                if (!FlooHandbag.hasPlaces(itemStack)) {
                    return InteractionResult.PASS;
                } else {
                    if (!level.isClientSide) {
                        FlooHandbag.clear(itemStack);
                        LayeredCauldronBlock.lowerFillLevel(blockState, level, blockPos);
                    }

                    return InteractionResult.sidedSuccess(level.isClientSide);
                }
            }
        });
    }

    private static void clear(ItemStack bag) {
        bag.setTag(null);
    }

    @Override
    public boolean isBarVisible(ItemStack itemStack) {
        return itemStack.hasTag();
    }

    @Override
    public int getBarColor(ItemStack itemStack) {
        return BAR_COLOR;
    }

    @Override
    public int getBarWidth(ItemStack bag) {
        return Math.min(Math.round(getPowderSize(bag) * 13.0F / MAX_SIZE), 13);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (player.isShiftKeyDown()) {
            String nextPlace = setNextPlace(itemStack);
            if (!nextPlace.isEmpty() && player instanceof ServerPlayer serverPlayer) {
                MessageSender.gameInfo(serverPlayer, Component.literal(nextPlace));
                return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide);
            }
            return InteractionResultHolder.fail(itemStack);
        }

        if (tryToDropPowder(player, itemStack)) {
            player.awardStat(Stats.ITEM_USED.get(this));
            player.getCooldowns().addCooldown(this, 10);
            return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide);
        }

        return InteractionResultHolder.fail(itemStack);
    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack bag, ItemStack other, Slot slot, ClickAction clickAction, Player player, SlotAccess slotAccess) {
        if (clickAction == ClickAction.SECONDARY && slot.allowModification(player)) {
            if (other.is(CCMain.FLOO_POWDER_ITEM.get())) {
                if (hasPlaces(bag) && tryToPutPowder(bag, other) > 0) {
                    player.playSound(SoundEvents.BUNDLE_INSERT, 0.8F, 0.8F + player.getRandom().nextFloat() * 0.4F);
                }
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void appendHoverText(ItemStack bag, @Nullable Level level, List<Component> components, TooltipFlag tooltipFlag) {
        components.addAll(getTooltipComponent(bag));
    }

    private static String setNextPlace(ItemStack bag) {
        if (hasPlaces(bag)) {
            CompoundTag tag = bag.getTag();
            assert tag != null;
            List<String> places = getPlaces(bag);
            String place = tag.getString("Place");
            int index;
            if (places.contains(place)) {
                index = places.indexOf(place) + 1;
            } else {
                index = 0;
            }
            index = index < places.size() ? index : 0;
            tag.putString("Place", places.get(index));
            return places.get(index);
        }
        return "";
    }

    private static boolean hasPlaces(ItemStack bag) {
        var tag = bag.getTag();
        if (tag == null) return false;
        return !tag.getList("Places", Tag.TAG_STRING).isEmpty();
    }

    private static List<String> getPlaces(ItemStack bag) {
        List<String> places = new ArrayList<>();
        CompoundTag compoundtag = bag.getTag();
        if (compoundtag != null) {
            for (Tag place : compoundtag.getList("Places", Tag.TAG_STRING)) {
                places.add(place.getAsString());
            }
        }
        return places;
    }

    private static List<Component> getTooltipComponent(ItemStack bag) {
        List<Component> components = new ArrayList<>();
        var tag = bag.getTag();
        if (tag != null) {
            components.add(Component.literal(getPowderSize(bag) + "/64").withStyle(ChatFormatting.GRAY));
            MutableComponent component = Component.literal("[");
            List<String> places = getPlaces(bag);
            for (int i = 0; i < places.size(); i++) {
                String place = places.get(i);
                component.append(place.equals(tag.getString("Place")) ? Component.literal(place).withStyle(ChatFormatting.DARK_GREEN) : Component.literal(place));
                component.append(i < places.size() - 1 ? "," : "");
            }
            component.append("]");
            components.add(component.withStyle(ChatFormatting.GRAY));
        }
        return components;
    }

    private static int getPowderSize(ItemStack bag) {
        CompoundTag compoundtag = bag.getTag();
        if (compoundtag == null) {
            return 0;
        } else {
            return compoundtag.getInt("Powders");
        }
    }

    public static boolean addPowderName(ItemStack bag, String place) {
        CompoundTag compoundtag = bag.getOrCreateTag();
        boolean contains = compoundtag.contains("Places");
        ListTag listTag = contains ? compoundtag.getList("Places", Tag.TAG_STRING) : new ListTag();
        StringTag placeTag = StringTag.valueOf(place);
        if (contains && listTag.contains(placeTag)) {
            return false;
        }

        listTag.add(placeTag);
        compoundtag.put("Places", listTag);
        return true;
    }

    private static int tryToPutPowder(ItemStack bag, ItemStack other) {
        CompoundTag compoundtag = bag.getOrCreateTag();
        int n = getPowderSize(bag);
        int min = Math.min(MAX_SIZE - n, other.getCount());
        if (min <= 0) {
            return 0;
        } else {
            other.shrink(min);
            compoundtag.putInt("Powders", n + min);
            return min;
        }
    }

    private static boolean tryToDropPowder(Player player, ItemStack bag) {
        var tag = bag.getTag();
        if (tag != null) {
            int n = getPowderSize(bag);
            ItemStack powder = getPowderStack(bag);
            if (n > 0 && powder != null) {
                player.playSound(SoundEvents.BUNDLE_DROP_CONTENTS, 0.8F, 0.8F + player.getRandom().nextFloat() * 0.4F);
                player.drop(powder, Boolean.TRUE);
                tag.putInt("Powders", n - 1);
                return true;
            }
        }
        return false;
    }

    @Nullable
    private static ItemStack getPowderStack(ItemStack bag) {
        var tag = bag.getTag();
        ItemStack itemStack = null;
        if (tag != null) {
            String place = tag.getString("Place");
            if (place.isEmpty()) {
                setNextPlace(bag);
            } else {
                itemStack = FlooPowder.getNamedPowder(place);
            }
        }
        return itemStack;
    }
}