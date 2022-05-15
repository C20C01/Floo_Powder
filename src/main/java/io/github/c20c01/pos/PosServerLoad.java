package io.github.c20c01.pos;

import io.github.c20c01.CCMain;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CCMain.ID)
public class PosServerLoad {

    @SubscribeEvent
    public static void serverStarted(ServerStartedEvent event) {
        System.out.println("""
                *************************
                | Loading all positions |
                *************************
                """);
        PosMap.load(event.getServer().getLevel(Level.OVERWORLD));
    }
}
