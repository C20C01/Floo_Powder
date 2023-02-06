package io.github.c20c01.tp;

import io.github.c20c01.CCMain;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Stack;

/**
 * 用来保证传送与主循环同步
 */

@Mod.EventBusSubscriber(modid = CCMain.ID)
class ServerTick {
    private static final ServerTick serverTick = new ServerTick();
    private static final Stack<TpContext> tpStack = new Stack<>();

    @SubscribeEvent
    public void serverTickEvent(TickEvent.ServerTickEvent event) {
        while (!tpStack.isEmpty()) {
            TpContext context = tpStack.pop();
            TpTool.teleportTo(context);
        }
        TpTool.cleanSet();
        MinecraftForge.EVENT_BUS.unregister(serverTick);
    }

    protected static void add(TpContext context) {
        tpStack.add(context);
        register();
    }

    protected static void register() {
        MinecraftForge.EVENT_BUS.register(serverTick);
    }
}
