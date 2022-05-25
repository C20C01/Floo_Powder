package io.github.c20c01.block;

import io.github.c20c01.CCMain;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)

public class BlocksSetRender {
    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void clientSetRender(final FMLClientSetupEvent event) {
        ItemBlockRenderTypes.setRenderLayer(CCMain.PORTAL_FIRE_BLOCK.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(CCMain.PORTAL_POINT_BLOCK.get(), RenderType.solid());
        ItemBlockRenderTypes.setRenderLayer(CCMain.FLOO_POWDER_GIVER_BLOCK.get(), RenderType.solid());
    }
}