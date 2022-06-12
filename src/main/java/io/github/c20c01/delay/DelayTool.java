package io.github.c20c01.delay;

import io.github.c20c01.CCMain;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CCMain.ID)
public class DelayTool {
    private int tick;
    private final int end;
    private final Runnable task;

    @SubscribeEvent
    public void serverTickEvent(TickEvent.ServerTickEvent event) {
        tick++;
        check();
    }

    public DelayTool(int delayTick, Runnable runnable) {
        tick = 0;
        end = delayTick;
        task = runnable;
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void check() {
        if (tick >= end) {
            MinecraftForge.EVENT_BUS.unregister(this);
            tick = 0;
            task.run();
        }
    }
}
