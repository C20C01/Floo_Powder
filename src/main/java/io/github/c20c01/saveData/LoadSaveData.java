package io.github.c20c01.saveData;

import io.github.c20c01.CCMain;
import io.github.c20c01.command.ModSettings;
import io.github.c20c01.gui.GuiData;
import io.github.c20c01.pos.PosMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CCMain.ID)
public class LoadSaveData {

    @SubscribeEvent
    public static void serverStarted(ServerStartedEvent event) {
        new Thread(() -> {
            ServerLevel level = event.getServer().getLevel(Level.OVERWORLD);
            if (level == null) return;
            PosMap.load(level);
            //Loading points
            GuiData.load(level);
            //Loading desc
            ModSettings.load(level);
            //Loading settings
        }).start();
    }

    @SubscribeEvent
    public static void serverStopped(ServerStoppedEvent event) {
        PosMap.clear();
        GuiData.clear();
        ModSettings.clear();
    }
}