//package io.github.c20c01.saveData;
//
//import io.github.c20c01.CCMain;
//import io.github.c20c01.pos.PosMap;
//import net.minecraft.nbt.CompoundTag;
//import net.minecraft.server.MinecraftServer;
//import net.minecraft.server.level.ServerLevel;
//import net.minecraft.world.level.Level;
//import net.minecraft.world.level.saveddata.SavedData;
//import org.jetbrains.annotations.NotNull;
//
//public class PosWorldSavedData extends SavedData {
//    private static final String DATA_NAME = CCMain.ID + "_pos";
//    private static final String REGEX = ", ";
//
//    public PosWorldSavedData(CompoundTag nbt) {
//        this.read(nbt);
//    }
//
//    public PosWorldSavedData() {
//        super();
//    }
//
//    public void changed() {
//        this.setDirty();
//    }
//
//    @Override
//    public @NotNull CompoundTag save(@NotNull CompoundTag compound) {
//        compound = new CompoundTag();
//        var map = PosMap.getMap();
//        for (String name : map.keySet()) {
//            if (map.get(name).blockPos != null) {
//                compound.putString(name, map.get(name).blockPos.toShortString());
//            }
//        }
//        return compound;
//    }
//
//    public void read(CompoundTag compound) {
//        for (String name : compound.getAllKeys()) {
//            String[] pos = compound.getString(name).split(REGEX);
//            PosMap.set(name, pos);
//        }
//    }
//
//    public static PosWorldSavedData get(MinecraftServer server) {
//        if (server != null) {
//            ServerLevel overWorld = server.getLevel(Level.OVERWORLD);
//            if (overWorld != null)
//                return overWorld.getDataStorage().computeIfAbsent(PosWorldSavedData::new, PosWorldSavedData::new, DATA_NAME);
//        }
//        return null;
//    }
//}