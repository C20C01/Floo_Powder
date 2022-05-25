package io.github.c20c01.pos;

import io.github.c20c01.saveData.PosWorldSavedData;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.level.ChunkPos;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class PosMap {
    private static final HashMap<String, PosInfo> MAP = new HashMap<>();

    public static HashMap<String, PosInfo> getMap() {
        return MAP;
    }

    public static void set(String name, @Nullable ServerLevel level, @Nullable BlockPos blockPos) {
        if (name.equals("")) return;
        PosInfo info = MAP.get(name);
        if (info != null) {
            info.setLevel(level);
            info.setPos(blockPos);
        } else MAP.put(name, new PosInfo(level, blockPos));
        if (level != null) PosWorldSavedData.get(level.getServer()).changed();
    }

    public static PosInfo get(String name) {
        if (MAP.get(name) == null) return MAP.get("[UNKNOWN]");
            //Where to try to teleport when there is no corresponding teleportation point
            //Names are enclosed in square brackets
        else return MAP.get(name);
    }

    public static void load(ServerLevel level) {
        PosWorldSavedData.get(level.getServer());
        for (ServerLevel world : level.getServer().getAllLevels()) {
            for (PosInfo info : MAP.values()) {
                world.getChunkSource().addRegionTicket(TicketType.PORTAL, new ChunkPos(info.blockPos), 1, info.blockPos);
            }
        }
    }

    public static void clear() {
        MAP.clear();
    }

    public static void remove(String key, ServerLevel level) {
        MAP.remove(key);
        PosWorldSavedData.get(level.getServer()).changed();
    }
}
