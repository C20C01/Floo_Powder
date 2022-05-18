package io.github.c20c01.pos;

import io.github.c20c01.block.PortalPointBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
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
        } else
            MAP.put(name, new PosInfo(level, blockPos));
        if (level != null)
            PosWorldSavedData.get(level.getServer()).changed();
    }

    public static PosInfo get(String name) {
        if (MAP.get(name) == null)
            return MAP.get("[UNKNOWN]");
            //Where to try to teleport when there is no corresponding teleportation point
            //Names are enclosed in square brackets
        else
            return MAP.get(name);
    }

    public static void load(Level level) {
        if (level instanceof ServerLevel serverLevel) {
            PosWorldSavedData.get(serverLevel.getServer());
            //System.out.println("All name:" + PosMap.MAP.keySet());
            for (ServerLevel world : serverLevel.getServer().getAllLevels()) {
                //System.out.println("\nWorld: " + world + " ,hash: " + world.hashCode());
                for (String key : PosMap.MAP.keySet()) {
                    PosInfo info = MAP.get(key);
                    //System.out.println(info);
                    if (info.blockPos != null && world.getBlockEntity(info.blockPos) instanceof PortalPointBlockEntity)
                        world.getChunkSource().addRegionTicket(TicketType.PORTAL, new ChunkPos(info.blockPos), 1, info.blockPos);
                }
            }
            //checkAll();
        }
    }

    public static void clear() {
        MAP.clear();
    }

    public static void checkAll() {
        //used when debugging
        System.out.println("**************************************");
        for (var key : MAP.keySet()) {
            PosInfo i = MAP.get(key);
            System.out.println("Name: " + key + i);
        }
        System.out.println("**************************************");
    }

    public static void remove(String key) {
        MAP.remove(key);
    }
}
