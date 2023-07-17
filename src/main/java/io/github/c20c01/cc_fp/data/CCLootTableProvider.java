package io.github.c20c01.cc_fp.data;

import io.github.c20c01.cc_fp.CCMain;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)

public class CCLootTableProvider extends LootTableProvider {
    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event) {
        var generator = event.getGenerator();
        generator.addProvider(event.includeServer(), new CCLootTableProvider(generator));
    }

    private CCLootTableProvider(DataGenerator gen) {
        super(gen.getPackOutput(), Set.of(CCMain.FIRE_BASE_BLOCK.getId()), List.of(new SubProviderEntry(CCBlockLoot::new, LootContextParamSets.BLOCK)));
    }

    @Override
    protected void validate(Map<ResourceLocation, LootTable> map, ValidationContext validationcontext) {

        System.out.println(map);

        map.forEach((key, value) -> value.validate(validationcontext));
    }

    private static class CCBlockLoot extends BlockLootSubProvider {
        protected CCBlockLoot() {
            super(Set.of(), FeatureFlags.REGISTRY.allFlags());
        }

        @Override
        protected void generate() {
            getKnownBlocks().forEach(this::dropSelf);
        }

        @Override
        protected Iterable<Block> getKnownBlocks() {
            Set<Block> blocks = new HashSet<>();
            blocks.add(CCMain.FIRE_BASE_BLOCK.get());
            blocks.add(CCMain.PORTAL_POINT_BLOCK.get());
            blocks.add(CCMain.POWDER_POT_BLOCK.get());
            return blocks;
        }
    }
}