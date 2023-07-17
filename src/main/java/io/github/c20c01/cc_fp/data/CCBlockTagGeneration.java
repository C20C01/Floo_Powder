package io.github.c20c01.cc_fp.data;

import io.github.c20c01.cc_fp.CCMain;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;


@Mod.EventBusSubscriber(modid = CCMain.ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CCBlockTagGeneration {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent e) {
        DataGenerator generator = e.getGenerator();
        if (e.includeServer()) {
            BlockTagGenerator blockTagGenerator = new BlockTagGenerator(generator, e.getLookupProvider(), CCMain.ID, e.getExistingFileHelper());
            generator.addProvider(Boolean.TRUE, blockTagGenerator);
        }
    }

    private static class BlockTagGenerator extends BlockTagsProvider {
        public BlockTagGenerator(DataGenerator generator, CompletableFuture<HolderLookup.Provider> lookupProvider, String modId, @Nullable ExistingFileHelper existingFileHelper) {
            super(generator.getPackOutput(), lookupProvider, modId, existingFileHelper);
        }

        @Override
        protected void addTags(HolderLookup.@NotNull Provider provider) {
            tag(BlockTags.DRAGON_IMMUNE).add(CCMain.PORTAL_POINT_BLOCK.get(), CCMain.POWDER_GIVER_BLOCK.get());
            tag(BlockTags.NEEDS_DIAMOND_TOOL).add(CCMain.PORTAL_POINT_BLOCK.get());
            tag(BlockTags.INFINIBURN_OVERWORLD).add(CCMain.PORTAL_POINT_BLOCK.get(), CCMain.FIRE_BASE_BLOCK.get());
            tag(BlockTags.INFINIBURN_NETHER).add(CCMain.PORTAL_POINT_BLOCK.get(), CCMain.FIRE_BASE_BLOCK.get());
            tag(BlockTags.INFINIBURN_END).add(CCMain.PORTAL_POINT_BLOCK.get(), CCMain.FIRE_BASE_BLOCK.get());
            tag(BlockTags.MINEABLE_WITH_PICKAXE).add(CCMain.PORTAL_POINT_BLOCK.get(), CCMain.FIRE_BASE_BLOCK.get());
        }
    }
}
