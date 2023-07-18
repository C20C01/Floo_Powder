package io.github.c20c01.cc_fp.data;

import io.github.c20c01.cc_fp.CCMain;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.critereon.EnterBlockTrigger;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeAdvancementProvider;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class CCAdvancementProvider extends ForgeAdvancementProvider {
    private static final List<AdvancementGenerator> ADVANCEMENTS = new ArrayList<>();

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event) {
        var generator = event.getGenerator();
        registerAdvancements();
        generator.addProvider(Boolean.TRUE, new CCAdvancementProvider(generator.getPackOutput(), event.getLookupProvider(), event.getExistingFileHelper()));
    }

    private CCAdvancementProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, ExistingFileHelper existingFileHelper) {
        super(output, registries, existingFileHelper, CCAdvancementProvider.ADVANCEMENTS);
    }

    protected static void registerAdvancements() {
        Advancement advancement = makeAdvancement(Advancement.Builder.advancement()
                .display(new DisplayInfo(CCMain.FLOO_POWDER_ITEM.get().getDefaultInstance(), Component.translatable(CCMain.TEXT_GET_FLOO_TITLE), Component.translatable(CCMain.TEXT_GET_FLOO_DESC), new ResourceLocation(CCMain.ID, "textures/gui/advancements/backgrounds/floo_powder.png"), FrameType.TASK, true, true, false))
                .addCriterion(CCMain.ADVANCEMENT_GET_FLOO.getPath(), InventoryChangeTrigger.TriggerInstance.hasItems(CCMain.FLOO_POWDER_ITEM.get())).build(CCMain.ADVANCEMENT_GET_FLOO));

        makeAdvancement(Advancement.Builder.advancement().parent(advancement)
                .display(new DisplayInfo(CCMain.PORTAL_POINT_BLOCK_ITEM.get().getDefaultInstance(), Component.translatable(CCMain.TEXT_GET_POINT_BLOCK_TITLE), Component.translatable(CCMain.TEXT_GET_POINT_BLOCK_DESC), null, FrameType.GOAL, true, true, false))
                .addCriterion(CCMain.ADVANCEMENT_GET_POINT_BLOCK.getPath(), InventoryChangeTrigger.TriggerInstance.hasItems(CCMain.PORTAL_POINT_BLOCK_ITEM.get())).build(CCMain.ADVANCEMENT_GET_POINT_BLOCK));

        makeAdvancement(Advancement.Builder.advancement().parent(advancement)
                .display(new DisplayInfo(CCMain.PORTAL_FLINT_AND_STEEL_ITEM.get().getDefaultInstance(), Component.translatable(CCMain.TEXT_INTO_FIRE_TITLE), Component.translatable(CCMain.TEXT_INTO_FIRE_DESC), null, FrameType.CHALLENGE, true, true, false))
                .addCriterion(CCMain.ADVANCEMENT_INTO_FIRE.getPath(), EnterBlockTrigger.TriggerInstance.entersBlock(CCMain.PORTAL_FIRE_BLOCK.get())).build(CCMain.ADVANCEMENT_INTO_FIRE));
    }

    private static Advancement makeAdvancement(Advancement advancement) {
        ADVANCEMENTS.add((registries, saver, existingFileHelper) -> saver.accept(advancement));
        return advancement;
    }
}