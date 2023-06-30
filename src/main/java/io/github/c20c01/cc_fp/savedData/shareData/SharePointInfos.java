package io.github.c20c01.cc_fp.savedData.shareData;

import io.github.c20c01.cc_fp.CCMain;
import io.github.c20c01.cc_fp.client.gui.screen.PowderGiverScreen;
import io.github.c20c01.cc_fp.network.CCNetwork;
import io.github.c20c01.cc_fp.network.GiveFlooPowder;
import io.github.c20c01.cc_fp.savedData.PortalPoint;
import io.github.c20c01.cc_fp.savedData.PortalPointManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * 实现服务端向客户端发送查询者有权限的所有传送点（供{@link PowderGiverScreen PowderGiverScreen}使用），<br>
 * 实现客户端向服务端发送一个传送点，让服务器给予对应的飞路粉。
 */

public class SharePointInfos {
    // 储存玩家上一次获取的传送点信息，若请求的传送点信息未发生变化则不会发送信息
    private static final HashMap<UUID, List<PortalPointInfo>> lastInfo = new HashMap<>();

    public static void getPointInfosFromS(List<PortalPointInfo> infos) {
        PowderGiverScreen.setUp(infos);
    }

    public static void sendPointInfosToC(ServerPlayer player, PortalPointManager.CheckType checkType, String groupName) {
        List<PortalPointInfo> infos = new ArrayList<>();
        List<PortalPoint> points;
        if (checkType.equals(PortalPointManager.CheckType.PUBLIC) && !groupName.isEmpty()) {
            points = PortalPointManager.getPublicPointsByGroup(player, groupName);
        } else {
            points = PortalPointManager.getPoints(checkType, player);
        }
        points.forEach((point -> infos.add(new PortalPointInfo(point))));
        UUID uuid = player.getUUID();
        if (!infos.equals(lastInfo.get(uuid))) {
            lastInfo.put(uuid, infos);
            var packet = new CCNetwork.PointInfosPacket(uuid, infos);
            CCNetwork.CHANNEL_POINT_TO_C.send(PacketDistributor.PLAYER.with(() -> player), packet);
        }
    }

    @Mod.EventBusSubscriber(modid = CCMain.ID)
    public static class PlayerLoggedOut {
        @SubscribeEvent
        public static void loggedOutEvent(PlayerEvent.PlayerLoggedOutEvent event) {
            if (event.getPlayer() instanceof ServerPlayer serverPlayer) {
                lastInfo.remove(serverPlayer.getUUID());
            }
        }
    }

    public static void getPointInfosFromC(UUID uuid, PortalPointInfo info) {
        GiveFlooPowder.give(uuid, info);
    }

    public static void sendPointInfoToS(UUID uuid, PortalPointInfo info) {
        var packet = new CCNetwork.PointInfosPacket(uuid, List.of(new PortalPointInfo[]{info}));
        CCNetwork.CHANNEL_POINT_TO_S.sendToServer(packet);
    }
}
