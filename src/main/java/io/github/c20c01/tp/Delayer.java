package io.github.c20c01.tp;

import io.github.c20c01.CCMain;
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
            MinecraftForge.EVENT_BUS.unregister(this);
            task.run();
        }
    }

    public static void add(int tick, Runnable task) {
        new Delayer(tick, task);
    }
}
