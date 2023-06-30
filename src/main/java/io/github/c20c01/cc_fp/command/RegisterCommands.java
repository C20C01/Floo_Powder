package io.github.c20c01.cc_fp.command;

import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class RegisterCommands {

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        CCCommand.register(event.getDispatcher());
    }
}