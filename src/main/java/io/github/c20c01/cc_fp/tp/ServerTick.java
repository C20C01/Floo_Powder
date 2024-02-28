package io.github.c20c01.cc_fp.tp;

import io.github.c20c01.cc_fp.CCMain;
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
    private static final Stack<Runnable> taskStack = new Stack<>();

    @SubscribeEvent
    public void serverTickEvent(TickEvent.ServerTickEvent event) {
        while (!taskStack.isEmpty()) {
            taskStack.pop().run();
        }
        MinecraftForge.EVENT_BUS.unregister(serverTick);
    }

    protected static void addTask(Runnable runnable) {
        taskStack.add(runnable);
    }

    protected static void register() {
        MinecraftForge.EVENT_BUS.register(serverTick);
    }
}
