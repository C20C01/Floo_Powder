package io.github.c20c01.cc_fp.client;

import io.github.c20c01.cc_fp.CCMain;
import io.github.c20c01.cc_fp.client.gui.screen.PortalPointScreen;
import io.github.c20c01.cc_fp.client.particles.PortalFireParticle;
import io.github.c20c01.cc_fp.client.particles.RayParticle;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.blockentity.ChestRenderer;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Setup {
    @SubscribeEvent
    public static void setupEvent(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            // 设置方块渲染类型
//            ItemBlockRenderTypes.setRenderLayer(CCMain.FAKE_PORTAL_FIRE_BLOCK.get(), RenderType.cutout());
//            ItemBlockRenderTypes.setRenderLayer(CCMain.PORTAL_FIRE_BLOCK.get(), RenderType.cutout());
//            ItemBlockRenderTypes.setRenderLayer(CCMain.PORTAL_POINT_BLOCK.get(), RenderType.solid());
//            ItemBlockRenderTypes.setRenderLayer(CCMain.POWDER_GIVER_BLOCK.get(), RenderType.solid());
//            ItemBlockRenderTypes.setRenderLayer(CCMain.FIRE_BASE_BLOCK.get(), RenderType.solid());

            BlockEntityRenderers.register(CCMain.PORTAL_CHEST_BLOCK_ENTITY.get(), ChestRenderer::new);

            // 注册菜单页面
            MenuScreens.register(CCMain.PORTAL_POINT_MENU.get(), PortalPointScreen::new);

            // 注册飞路球的渲染
            EntityRenderers.register(CCMain.FLOO_BALL_ENTITY.get(), ThrownItemRenderer::new);
        });
    }

    @SubscribeEvent
    public static void particlesRegister(RegisterParticleProvidersEvent event) {
        // 注册粒子效果
        event.registerSpriteSet(CCMain.RAY_PARTICLE.get(), RayParticle.Provider::new);
        event.registerSpriteSet(CCMain.PORTAL_FIRE_PARTICLE.get(), PortalFireParticle.Provider::new);
    }
}
