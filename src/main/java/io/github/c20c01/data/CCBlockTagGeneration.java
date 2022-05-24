package io.github.c20c01.data;

import io.github.c20c01.CCMain;
import io.github.c20c01.block.CCBlocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import org.jetbrains.annotations.Nullable;


@Mod.EventBusSubscriber(modid = CCMain.ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CCBlockTagGeneration {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent e) {
        DataGenerator generator = e.getGenerator();
        if (e.includeServer()) {
            BlockTagGenerator blockTagGenerator = new BlockTagGenerator(generator, CCMain.ID, e.getExistingFileHelper());
            generator.addProvider(blockTagGenerator);
        }
    }

    private static class BlockTagGenerator extends BlockTagsProvider {
        public BlockTagGenerator(DataGenerator generator, String modId, @Nullable ExistingFileHelper existingFileHelper) {
            super(generator, modId, existingFileHelper);
        }

        @Override
        protected void addTags() {
            tag(BlockTags.INFINIBURN_OVERWORLD).add(CCMain.PORTAL_POINT_BLOCK.get());
            tag(BlockTags.DRAGON_IMMUNE).add(CCMain.PORTAL_POINT_BLOCK.get());
            tag(BlockTags.MINEABLE_WITH_PICKAXE).add(CCMain.PORTAL_POINT_BLOCK.get());
            super.addTags();
        }
    }
}
