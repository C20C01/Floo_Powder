package io.github.c20c01.block;

import io.github.c20c01.CCMain;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)

public class CCBlocks {

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry().register(makeBlock(CCMain.PORTAL_FIRE_BLOCK_ID, new PortalFireBlock(BlockBehaviour.Properties.of(Material.FIRE, MaterialColor.COLOR_LIGHT_GREEN).noCollission().instabreak().lightLevel((x) -> 15), 0F)));
        event.getRegistry().register(makeBlock(CCMain.PORTAL_POINT_BLOCK_ID, new PortalPointBlock(BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BLACK).strength(30.0F, 1200.0F))));
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void clientSetRender(final FMLClientSetupEvent event) {
        ItemBlockRenderTypes.setRenderLayer(CCMain.PORTAL_FIRE_BLOCK.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(CCMain.PORTAL_POINT_BLOCK.get(), RenderType.solid());
    }

    private static Block makeBlock(ResourceLocation name, Block block) {
        block.setRegistryName(name);
        return block;
    }

}
