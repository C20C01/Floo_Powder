package io.github.c20c01.cc_fp.data;

import io.github.c20c01.cc_fp.CCMain;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Consumer;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class CCRecipeProvider extends RecipeProvider {

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event) {
        var generator = event.getGenerator();
        generator.addProvider(Boolean.TRUE, new CCRecipeProvider(generator));
    }

    public CCRecipeProvider(DataGenerator generator) {
        super(generator.getPackOutput());
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> consumer) {
        // 飞路粉：火药*8+绿宝石*1
        ShapedRecipeBuilder.shaped(RecipeCategory.TRANSPORTATION, CCMain.FLOO_POWDER_ITEM.get(), 8).define('G', Items.GUNPOWDER).define('E', Items.EMERALD).pattern("GGG").pattern("GEG").pattern("GGG").unlockedBy("has_gunpowder", has(Items.GUNPOWDER)).save(consumer);

        // 传送火石：青金石*1+紫水晶碎片*1
        ShapelessRecipeBuilder.shapeless(RecipeCategory.TOOLS, CCMain.PORTAL_FLINT_AND_STEEL_ITEM.get(), 1).requires(Items.AMETHYST_SHARD).requires(Items.LAPIS_LAZULI).unlockedBy("has_floo", has(CCMain.FLOO_POWDER_ITEM.get())).save(consumer);

        // 传送核心：金锭*2+飞路粉*1+黑曜石*3+传送火石*1+铜锭*2
        ShapedRecipeBuilder.shaped(RecipeCategory.TRANSPORTATION, CCMain.PORTAL_POINT_BLOCK_ITEM.get(), 1).define('#', Items.GOLD_INGOT).define('G', CCMain.FLOO_POWDER_ITEM.get()).define('O', Blocks.OBSIDIAN).define('S', CCMain.PORTAL_FLINT_AND_STEEL_ITEM.get()).define('C', Items.COPPER_INGOT).pattern("#G#").pattern("OSO").pattern("COC").unlockedBy("has_floo", has(CCMain.FLOO_POWDER_ITEM.get())).save(consumer);

        // 传送卷轴：飞路粉*1+纸*2
        ShapelessRecipeBuilder.shapeless(RecipeCategory.TRANSPORTATION, CCMain.FLOO_REEL_ITEM.get(), 1).requires(CCMain.FLOO_POWDER_ITEM.get()).requires(Items.PAPER, 2).unlockedBy("has_floo", has(CCMain.FLOO_POWDER_ITEM.get())).save(consumer);

        //
    }
}