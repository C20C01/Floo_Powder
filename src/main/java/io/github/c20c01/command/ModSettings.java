//package io.github.c20c01.command;
//
//import io.github.c20c01.saveData.SettingSavedData;
//import net.minecraft.server.level.ServerLevel;
//
//import java.util.HashMap;
//
//public class ModSettings {
//    public static final HashMap<String, Boolean> MAP = new HashMap<>();
//    private static SettingSavedData savedData;
//    public static final String HAND_USE_FP = "handUseFP";
//    public static final String TRUE = "true";
//    public static final String FALSE = "false";
//
//    public static void load(ServerLevel level) {
//        savedData = SettingSavedData.get(level.getServer());
//        buildSettingsMap();
//    }
//
//    private static void buildSettingsMap() {
//        MAP.putIfAbsent(HAND_USE_FP, false);    //Outside creative mode, can uuid use the FP without a fire block?
//    }
//
//    public static void put(String key, boolean value) {
//        if (MAP.containsKey(key)) {
//            MAP.put(key, value);
//        }
//        savedData.changed();
//    }
//
//    public static boolean get(String key) {
//        return MAP.get(key);
//    }
//
//    public static void clear() {
//        MAP.clear();
//    }
//}
