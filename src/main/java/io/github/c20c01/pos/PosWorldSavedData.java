package io.github.c20c01.pos;

import io.github.c20c01.CCMain;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;

public class PosWorldSavedData extends SavedData {
    private static final String DATA_NAME = CCMain.ID + "_pos";

    public PosWorldSavedData(CompoundTag nbt) {
        this.read(nbt);
    }

    public PosWorldSavedData() {
        super();
    }

    public void changed() {
        this.setDirty();
    }

    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag compound) {
        compound = new CompoundTag();
        var map = PosMap.getMap();
        for (String name : map.keySet()) {
            if (map.get(name).blockPos != null) {
                compound.putString(name, map.get(name).blockPos.toShortString());
                //System.out.println(name + "," + map.get(name).blockPos.toShortString());
            }
        }
        return compound;
    }

    public void read(CompoundTag compound) {
        for (String name : compound.getAllKeys()) {
            String[] pos = compound.getString(name).split(", ");
            if (pos.length == 3)
                PosMap.set(name, null, new BlockPos(Integer.parseInt(pos[0]), Integer.parseInt(pos[1]), Integer.parseInt(pos[2])));
        }
    }

    public static PosWorldSavedData get(MinecraftServer server) {
        if (server != null) {
            ServerLevel overWorld = server.getLevel(Level.OVERWORLD);
            if (overWorld != null)
                return overWorld.getDataStorage().computeIfAbsent(PosWorldSavedData::new, PosWorldSavedData::new, DATA_NAME);
        }
        return null;
    }
}