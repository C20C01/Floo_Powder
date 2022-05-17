package io.github.c20c01.data;

import io.github.c20c01.CCMain;
import io.github.c20c01.item.CCItems;
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
                .display(new DisplayInfo(CCItems.FlooPowder.getDefaultInstance(), new TranslatableComponent(CCMain.TEXT_GET_FLOO_TITLE), new TranslatableComponent(CCMain.TEXT_GET_FLOO_DESC), new ResourceLocation(CCMain.ID, "textures/gui/advancements/backgrounds/floo_powder.png"), FrameType.TASK, true, true, false))
                .addCriterion(CCMain.GET_FLOO_ADVANCEMENT_ID.getPath(), InventoryChangeTrigger.TriggerInstance.hasItems(CCItems.FlooPowder)).build(CCMain.GET_FLOO_ADVANCEMENT_ID));

        makeAdvancement(Advancement.Builder.advancement().parent(advancement)
                .display(new DisplayInfo(CCItems.PortalPointBlock.getDefaultInstance(), new TranslatableComponent(CCMain.TEXT_GET_POINT_BLOCK_TITLE), new TranslatableComponent(CCMain.TEXT_GET_POINT_BLOCK_DESC), null, FrameType.GOAL, true, true, false))
                .addCriterion(CCMain.GET_POINT_BLOCK_ADVANCEMENT_ID.getPath(), InventoryChangeTrigger.TriggerInstance.hasItems(CCItems.PortalPointBlock)).build(CCMain.GET_POINT_BLOCK_ADVANCEMENT_ID));

        makeAdvancement(Advancement.Builder.advancement().parent(advancement)
                .display(new DisplayInfo(CCItems.PortalFlintAndSteel.getDefaultInstance(), new TranslatableComponent(CCMain.TEXT_INTO_FIRE_TITLE), new TranslatableComponent(CCMain.TEXT_INTO_FIRE_DESC), null, FrameType.CHALLENGE, true, true, false))
                .addCriterion(CCMain.INTO_FIRE_ADVANCEMENT_ID.getPath(), EnterBlockTrigger.TriggerInstance.entersBlock(CCMain.PORTAL_FIRE_BLOCK.get())).build(CCMain.INTO_FIRE_ADVANCEMENT_ID));

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