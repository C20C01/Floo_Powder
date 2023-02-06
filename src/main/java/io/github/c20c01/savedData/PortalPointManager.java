package io.github.c20c01.savedData;

import io.github.c20c01.CCMain;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class PortalPointManager extends SavedData {
    private static final String TAG = "PortalPoints";
    private static final String DATA_NAME = CCMain.ID + "_" + TAG;
    private static final String DEFAULT_NAME = "UNKNOWN";
    private static final PortalPointManager clientStorageCopy = new PortalPointManager();

    private final HashMap<String, PortalPoint> portalPoints = new HashMap<>();

    public static PortalPointManager get(@Nullable MinecraftServer server) {
        if (server != null) {
            ServerLevel overWorld = server.getLevel(Level.OVERWORLD);
            return Objects.requireNonNull(overWorld).getDataStorage().computeIfAbsent(PortalPointManager::read, PortalPointManager::new, DATA_NAME);
        }
        return clientStorageCopy;
    }

    public static PortalPointManager read(CompoundTag compoundTag) {
        PortalPointManager portalPointManager = new PortalPointManager();
        ListTag tagList = compoundTag.getList(TAG, Tag.TAG_COMPOUND);
        for (Tag tag : tagList) {
            CompoundTag compound = (CompoundTag) tag;
            PortalPoint portalPoint = PortalPoint.read(compound);
            portalPointManager.portalPoints.put(portalPoint.name(), portalPoint);
        }
        return portalPointManager;
    }

    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag compoundTag) {
        ListTag tagList = new ListTag();
        for (PortalPoint portalPoint : portalPoints.values()) {
            tagList.add(PortalPoint.write(portalPoint, new CompoundTag()));
        }
        compoundTag.put(TAG, tagList);
        return compoundTag;
    }

    public void add(PortalPoint portalPoint) {
        portalPoints.putIfAbsent(portalPoint.name(), portalPoint);
        setDirty();
    }

    @Nullable
    public PortalPoint remove(String name) {
        setDirty();
        return portalPoints.remove(name);
    }

    /**
     * @param blockPos 传送点坐标（传送核心向上一格）
     */
    @Nullable
    public PortalPoint remove(String name, BlockPos blockPos) {
        if (portalPoints.get(name).pos().equals(blockPos)) {
            setDirty();
            return portalPoints.remove(name);
        }
        return null;
    }

    @Nullable
    public PortalPoint replace(PortalPoint portalPoint) {
        String name = portalPoint.name();
        var oldPoint = portalPoints.remove(name);
        portalPoints.put(name, portalPoint);
        setDirty();
        return oldPoint;
    }

    /**
     * @return 不存在同名点 ? 名为'DEFAULT_NAME'定义的点 : 名为输入的点
     */
    @Nullable
    public PortalPoint get(String name) {
        PortalPoint portalPoint = portalPoints.get(name);
        if (portalPoint == null) return portalPoints.get(DEFAULT_NAME);
        return portalPoint;
    }

    public boolean contains(String name) {
        return portalPoints.containsKey(name);
    }


    /**
     * @param someone 玩家的uuid
     * @return 此玩家有权前往的所有传送点
     */
    public List<PortalPoint> getViablePointsForSomeone(MinecraftServer server, UUID someone) {
        PermissionManager permissionManager = PermissionManager.get(server);
        List<PortalPoint> points = new ArrayList<>();
        Set<UUID> friends = new HashSet<>();
        friends.add(someone);

        for (PortalPoint point : portalPoints.values()) {
            if (point.isPublic()) {
                points.add(point);
                continue;
            }

            if (point.isTemporary()) {
                continue;
            }

            UUID owner = point.ownerUid();
            if (friends.contains(owner)) {
                points.add(point);
                continue;
            }

            Permission permission = permissionManager.get(owner);
            if (permission != null && permission.isFriend(someone)) {
                friends.add(owner);
                points.add(point);
            }
        }

        return points;
    }

    public PortalPoint[] getAll() {
        return portalPoints.values().toArray(new PortalPoint[0]);
    }
}
