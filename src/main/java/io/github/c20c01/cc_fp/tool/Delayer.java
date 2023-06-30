package io.github.c20c01.cc_fp.tool;

import io.github.c20c01.cc_fp.CCMain;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CCMain.ID)
public class Delayer {
    private int tick = 0;
    private final int end;
    private final Runnable task;

    @SubscribeEvent
    public void serverTickEvent(TickEvent.ServerTickEvent event) {
        tick();
    }

    private Delayer(int delayTick, Runnable task) {
        this.end = delayTick;
        this.task = task;
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void tick() {
        if (tick++ >= end) {
            stop();
            task.run();
        }
    }

    public void stop() {
        MinecraftForge.EVENT_BUS.unregister(this);
    }

    public static void add(int tick, Runnable task) {
        new Delayer(tick, task);
    }
}
