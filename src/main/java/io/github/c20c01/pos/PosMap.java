package io.github.c20c01.pos;

import io.github.c20c01.saveData.PosWorldSavedData;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.level.ChunkPos;

import java.util.HashMap;

public class PosMap {
    private static final HashMap<String, PosInfo> MAP = new HashMap<>();
    private static PosWorldSavedData posWorldSavedData;

    public static HashMap<String, PosInfo> getMap() {
        return MAP;
    }

    public static void set(String name, ServerLevel level, BlockPos blockPos) {
        // 使用传送火石添加传送点
        if (!name.equals("")) {
            MAP.put(name, new PosInfo(level, blockPos));
            dataChanged(level);
        }
    }

    public static void set(String name, String[] pos) {
        // 从SavedData读取传送点的名称和坐标
        if (!name.equals("") && pos.length == 3) {
            int x = Integer.parseInt(pos[0]);
            int y = Integer.parseInt(pos[1]);
            int z = Integer.parseInt(pos[2]);
            var blockPos = new BlockPos(x, y, z);

            PosInfo info = MAP.get(name);
            if (info != null) info.blockPos = blockPos;
            else MAP.put(name, new PosInfo(null, blockPos));
        }
    }

    public static void set(String name, ServerLevel level) {
        // 传送点被加载后添加其所在的世界
        if (!name.equals("")) {
            PosInfo info = MAP.get(name);
            if (info != null) info.level = level;
            else MAP.put(name, new PosInfo(level, null));
        }
    }

    public static PosInfo get(String name) {
        if (MAP.get(name) == null) return MAP.get("[UNKNOWN]");
            //Where to try to teleport when there is no corresponding teleportation point
            //Names are enclosed in square brackets
        else return MAP.get(name);
    }

    public static void load(ServerLevel level) {
        posWorldSavedData = PosWorldSavedData.get(level.getServer());
        for (ServerLevel world : level.getServer().getAllLevels()) {
            for (PosInfo info : MAP.values()) {
                BlockPos blockPos = info.blockPos;
                if (info.level == null && blockPos != null) {
                    world.getChunkSource().addRegionTicket(TicketType.PORTAL, new ChunkPos(blockPos), 1, blockPos);
                }
            }
        }
    }

    public static void clear() {
        MAP.clear();
        posWorldSavedData = null;
    }

    public static void remove(String key, ServerLevel level, BlockPos pos) {
        // 如果是被覆盖掉的核心则不删除传送点
        if (MAP.get(key) != null && MAP.get(key).blockPos.equals(pos.above())) {
            MAP.remove(key);
            dataChanged(level);
        }
    }

    private static void dataChanged(ServerLevel level) {
        if (posWorldSavedData == null) {
            if (level != null) posWorldSavedData = PosWorldSavedData.get(level.getServer());
            else return;
        }
        posWorldSavedData.changed();
    }
}
