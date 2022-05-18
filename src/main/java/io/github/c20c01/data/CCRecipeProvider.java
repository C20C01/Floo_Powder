package io.github.c20c01.data;

import io.github.c20c01.CCMain;
import io.github.c20c01.item.CCItems;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.data.BlockFamilies;
import net.minecraft.data.BlockFamily;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Consumer;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class CCRecipeProvider extends RecipeProvider {

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event) {
        var generator = event.getGenerator();
        generator.addProvider(new CCRecipeProvider(generator));
    }

    public CCRecipeProvider(DataGenerator generator) {
        super(generator);
    }

    @Override
    protected void buildCraftingRecipes(Consumer<FinishedRecipe> consumer) {
        BlockFamilies.getAllFamilies().filter(BlockFamily::shouldGenerateRecipe).forEach((x) -> generateRecipes(consumer, x));
        ShapedRecipeBuilder.shaped(CCItems.FlooPowder, 8).define('G', Items.GUNPOWDER).define('E', Items.EMERALD).pattern("GGG").pattern("GEG").pattern("GGG").unlockedBy("in_air", insideOf(Blocks.AIR)).save(consumer);
        ShapelessRecipeBuilder.shapeless(CCItems.PortalFlintAndSteel, 1).requires(Items.AMETHYST_SHARD).requires(Items.LAPIS_LAZULI).unlockedBy("has_floo", has(CCItems.FlooPowder)).save(consumer);
        ShapedRecipeBuilder.shaped(CCMain.PORTAL_POINT_BLOCK.get(), 1).define('#', Items.GOLD_INGOT).define('G', CCItems.FlooPowder).define('O', Blocks.OBSIDIAN).define('S', CCItems.PortalFlintAndSteel).define('N', Items.NETHERITE_INGOT).pattern("#G#").pattern("OSO").pattern("NON").unlockedBy("has_floo", has(CCItems.FlooPowder)).save(consumer);
    }
}
