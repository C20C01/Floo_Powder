//package io.github.c20c01.command;
//
//import com.mojang.brigadier.CommandDispatcher;
//import com.mojang.brigadier.context.CommandContext;
//import io.github.c20c01.CCMain;
//import net.minecraft.commands.CommandSourceStack;
//import net.minecraft.commands.Commands;
//import net.minecraft.network.chat.ChatType;
//import net.minecraft.network.chat.TextComponent;
//import net.minecraft.server.level.ServerPlayer;
//import net.minecraft.world.entity.Entity;
//import net.minecraftforge.event.RegisterCommandsEvent;
//import net.minecraftforge.eventbus.api.SubscribeEvent;
//import net.minecraftforge.fml.common.Mod;
//
//@Mod.EventBusSubscriber
//public class CommandEventHandler extends ModSettings {
//
//    @SubscribeEvent
//    public static void registerCommands(RegisterCommandsEvent event) {
//        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
//        dispatcher.register(
//                Commands.literal(CCMain.ID)
//                        .requires((sourceStack) -> sourceStack.hasPermission(2))
//                        .executes(context -> print(context, "❤ Floo Powder ❤"))
//                        .then(Commands.literal(HAND_USE_FP).executes(context -> print(context, HAND_USE_FP + ": " + MAP.get(HAND_USE_FP)))
//                                .then(Commands.literal(TRUE).executes(context -> {
//                                    put(HAND_USE_FP, true);
//                                    return print(context, HAND_USE_FP + " ← " + TRUE);
//                                }))
//                                .then(Commands.literal(FALSE).executes(context -> {
//                                    put(HAND_USE_FP, false);
//                                    return print(context, HAND_USE_FP + " ← " + FALSE);
//                                }))
//                        )
//        );
//    }
//
//    @SuppressWarnings("SameReturnValue")
//    private static int print(CommandContext<CommandSourceStack> commandContext, String s) {
//        Entity entity = commandContext.getSource().getEntity();
//        if (entity instanceof ServerPlayer serverPlayer) {
//            var text = new TextComponent(s);
//            serverPlayer.sendMessage(text, ChatType.CHAT, entity.getUUID());
//        }
//        return 0;
//    }
//}