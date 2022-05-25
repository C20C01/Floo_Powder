package io.github.c20c01.gui;

import io.github.c20c01.CCMain;
import io.github.c20c01.block.FlooPowderGiverBlock;
import io.github.c20c01.network.CCNetwork;
import io.github.c20c01.pos.PosMap;
import io.github.c20c01.saveData.PointDescWorldSavedData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

import java.util.HashMap;
import java.util.HashSet;

@Mod.EventBusSubscriber(modid = CCMain.ID)
public class GuiData {
    public static HashMap<String, String> descMap = new HashMap<>();
    public static final HashSet<Player> updatedPlayers = new HashSet<>();
    private static HashMap<String, String> lastDescMap;
    private static PointDescWorldSavedData savedData;

    public static void sendToClient(ServerPlayer player ,int code) {
        HashMap<String, String> temp = new HashMap<>();
        for (String key : PosMap.getMap().keySet()) {
            String desc = descMap.get(key);
            temp.put(key, desc == null ? "???" : desc);
        }
        descMap = temp;
        if (!descMap.equals(lastDescMap)) {
            CCNetwork.CHANNEL_DESC_TO_C.send(PacketDistributor.PLAYER.with(() -> player), new CCNetwork.PointDescPacket(descMap));
            lastDescMap = descMap;
            updatedPlayers.clear();
        } else if (!updatedPlayers.contains(player)) {
            CCNetwork.CHANNEL_DESC_TO_C.send(PacketDistributor.PLAYER.with(() -> player), new CCNetwork.PointDescPacket(descMap));
            updatedPlayers.add(player);
        }
        CCNetwork.CHANNEL_PLAYER_CODE_TO_C.send(PacketDistributor.PLAYER.with(() -> player), new CCNetwork.PowderNamePacket("", code));
    }

    @SubscribeEvent
    public static void playerOut(PlayerEvent.PlayerLoggedOutEvent event) {
        Player player = event.getPlayer();
        updatedPlayers.remove(player);
    }

    public static void load(ServerLevel level) {
        savedData = PointDescWorldSavedData.get(level.getServer());
    }

    public static void clear() {
        descMap.clear();
        updatedPlayers.clear();
        FlooPowderGiverBlock.clear();
    }

    public static void setDescMapFromServer(HashMap<String, String> map) {
        FlooPowderGiverGui.setUp(map);
    }

    public static void setDescMapFromClient(HashMap<String, String> map) {
        descMap = map;
        savedData.changed();
    }

    @OnlyIn(Dist.CLIENT)
    public static void sendToServer(HashMap<String, String> map) {
        CCNetwork.CHANNEL_DESC_TO_S.sendToServer(new CCNetwork.PointDescPacket(map));
    }
}