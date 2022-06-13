package io.github.c20c01.saveData;

import io.github.c20c01.CCMain;
import io.github.c20c01.command.ModSettings;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;

public class SettingSavedData extends SavedData {
    private static final String DATA_NAME = CCMain.ID + "_settings";

    public SettingSavedData(CompoundTag nbt) {
        this.read(nbt);
    }

    public SettingSavedData() {
        super();
    }

    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag compound) {
        compound = new CompoundTag();
        var map = ModSettings.MAP;
        for (String key : map.keySet()) {
            compound.putBoolean(key, map.get(key));
        }
        return compound;
    }

    public void read(CompoundTag compound) {
        for (String key : compound.getAllKeys()) {
            ModSettings.MAP.put(key, compound.getBoolean(key));
        }
    }

    public static SettingSavedData get(MinecraftServer server) {
        if (server != null) {
            ServerLevel overWorld = server.getLevel(Level.OVERWORLD);
            if (overWorld != null)
                return overWorld.getDataStorage().computeIfAbsent(SettingSavedData::new, SettingSavedData::new, DATA_NAME);
        }
        return null;
    }

    public void changed() {
        this.setDirty();
    }
}
