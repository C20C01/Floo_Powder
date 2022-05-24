package io.github.c20c01.saveData;

import io.github.c20c01.CCMain;
import io.github.c20c01.gui.FlooPowderGiverGui;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class PointDescWorldSavedData extends SavedData {
    private static final String DATA_NAME = CCMain.ID + "_point_desc";
    private static final HashMap<String, String> map = FlooPowderGiverGui.descMap;

    public PointDescWorldSavedData(CompoundTag nbt) {
        this.read(nbt);
    }

    public PointDescWorldSavedData() {
        super();
    }

    public void changed() {
        this.setDirty();
    }

    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag compound) {
        compound = new CompoundTag();
        for (String key : map.keySet()) {
            compound.putString(key, map.get(key));
        }
        return compound;
    }

    public void read(CompoundTag compound) {
        for (String key : compound.getAllKeys()) {
            map.put(key, compound.getString(key));
        }
    }

    public static PointDescWorldSavedData get(MinecraftServer server) {
        if (server != null) {
            ServerLevel overWorld = server.getLevel(Level.OVERWORLD);
            if (overWorld != null)
                return overWorld.getDataStorage().computeIfAbsent(PointDescWorldSavedData::new, PointDescWorldSavedData::new, DATA_NAME);
        }
        return null;
    }
}