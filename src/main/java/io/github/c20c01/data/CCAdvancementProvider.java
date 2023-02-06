package io.github.c20c01.data;

import io.github.c20c01.CCMain;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.critereon.EnterBlockTrigger;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.advancements.AdvancementProvider;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashSet;
import java.util.function.Consumer;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class CCAdvancementProvider extends AdvancementProvider {
    private static final HashSet<Advancement> ADVANCEMENTS = new HashSet<>();

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event) {
        var generator = event.getGenerator();
        generator.addProvider(new CCAdvancementProvider(generator, event.getExistingFileHelper()));
    }

    private CCAdvancementProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, existingFileHelper);
    }

    @Override
    protected void registerAdvancements(Consumer<Advancement> consumer, ExistingFileHelper fileHelper) {
        Advancement advancement = makeAdvancement(Advancement.Builder.advancement()
                .display(new DisplayInfo(CCMain.FLOO_POWDER_ITEM.get().getDefaultInstance(), new TranslatableComponent(CCMain.TEXT_GET_FLOO_TITLE), new TranslatableComponent(CCMain.TEXT_GET_FLOO_DESC), new ResourceLocation(CCMain.ID, "textures/gui/advancements/backgrounds/floo_powder.png"), FrameType.TASK, true, true, false))
                .addCriterion(CCMain.ADVANCEMENT_GET_FLOO.getPath(), InventoryChangeTrigger.TriggerInstance.hasItems(CCMain.FLOO_POWDER_ITEM.get())).build(CCMain.ADVANCEMENT_GET_FLOO));

        makeAdvancement(Advancement.Builder.advancement().parent(advancement)
                .display(new DisplayInfo(CCMain.PORTAL_POINT_BLOCK_ITEM.get().getDefaultInstance(), new TranslatableComponent(CCMain.TEXT_GET_POINT_BLOCK_TITLE), new TranslatableComponent(CCMain.TEXT_GET_POINT_BLOCK_DESC), null, FrameType.GOAL, true, true, false))
                .addCriterion(CCMain.ADVANCEMENT_GET_POINT_BLOCK.getPath(), InventoryChangeTrigger.TriggerInstance.hasItems(CCMain.PORTAL_POINT_BLOCK_ITEM.get())).build(CCMain.ADVANCEMENT_GET_POINT_BLOCK));

        makeAdvancement(Advancement.Builder.advancement().parent(advancement)
                .display(new DisplayInfo(CCMain.PORTAL_FLINT_AND_STEEL_ITEM.get().getDefaultInstance(), new TranslatableComponent(CCMain.TEXT_INTO_FIRE_TITLE), new TranslatableComponent(CCMain.TEXT_INTO_FIRE_DESC), null, FrameType.CHALLENGE, true, true, false))
                .addCriterion(CCMain.ADVANCEMENT_INTO_FIRE.getPath(), EnterBlockTrigger.TriggerInstance.entersBlock(CCMain.PORTAL_FIRE_BLOCK.get())).build(CCMain.ADVANCEMENT_INTO_FIRE));

        acceptAll(consumer);
    }

    private static Advancement makeAdvancement(Advancement advancement) {
        ADVANCEMENTS.add(advancement);
        return advancement;
    }

    private static void acceptAll(Consumer<Advancement> consumer) {
        ADVANCEMENTS.forEach((consumer));
        ADVANCEMENTS.clear();
    }
}