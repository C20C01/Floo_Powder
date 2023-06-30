package io.github.c20c01.cc_fp.savedData;

import io.github.c20c01.cc_fp.CCMain;
import io.github.c20c01.cc_fp.tool.Delayer;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class FriendRequestManager {
    public enum Result {
        SUCCESS, OUT_OF_SIZE, CONTAINED, SELF, PLAYER_NOT_FOUND, NO_REQUEST, ALREADY_SEND
    }

    private static final Set<FriendRequest> REQUESTS = new HashSet<>();

    protected static boolean add(UUID friend, UUID inserted) {
        var request = new FriendRequest(friend, inserted);
        if (REQUESTS.add(request)) {
            Delayer.add(6000, () -> REQUESTS.remove(request));
            return true;
        }
        return false;
    }

    protected static boolean accept(UUID friend, UUID inserted, MinecraftServer server) {
        var request = new FriendRequest(friend, inserted);
        boolean flag = REQUESTS.remove(request);
        if (flag) {
            request.accept(server);
        }
        return flag;
    }

    @Mod.EventBusSubscriber(modid = CCMain.ID)
    public static class ServerStopping {
        @SubscribeEvent
        public static void stoppingEvent(ServerStoppingEvent event) {
            REQUESTS.clear();
        }
    }
}
