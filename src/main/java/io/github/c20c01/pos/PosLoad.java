package io.github.c20c01.pos;

import io.github.c20c01.CCMain;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CCMain.ID)
public class PosLoad {

    @SubscribeEvent
    public static void serverStarted(ServerStartedEvent event) {
        PosMap.load(event.getServer().getLevel(Level.OVERWORLD));
        //Loading points
    }

    @SubscribeEvent
    public static void serverStopped(ServerStoppedEvent event) {
        PosMap.clear();
        //Clearing points
    }
}