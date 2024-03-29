package io.github.c20c01.cc_fp.savedData;

import com.mojang.logging.LogUtils;
import io.github.c20c01.cc_fp.CCMain;
import io.github.c20c01.cc_fp.block.portalPoint.PortalPointBlockEntity;
import io.github.c20c01.cc_fp.config.CCConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class PortalPointManager extends SavedData {
    private static final String TAG = "PortalPoints";
    private static final String DATA_NAME = CCMain.ID + "_" + TAG;
    private static final PortalPointManager clientStorageCopy = new PortalPointManager();

    private final HashMap<String, PortalPoint> portalPoints = new HashMap<>();

    public enum CheckType {
        MINE, PUBLIC, OTHERS, ALL, OP_ALL;

        public CheckType nextType() {
            // 不能返回OP_ALL(OP专用)
            return values()[(ordinal() + 1) % 4];
        }
    }

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
        if (portalPoints.containsKey(portalPoint.name())) {
            LogUtils.getLogger().error("Portal point named'{}' just be replaced. (Point was not successfully saved last time?)", portalPoint.name());
        }
        portalPoints.put(portalPoint.name(), portalPoint);
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
        PortalPoint point = portalPoints.get(name);
        if (point != null && point.pos().equals(blockPos)) {
            return remove(name);
        }
        return null;
    }

    @Nullable
    public PortalPoint get(String name, boolean getDefault) {
        PortalPoint portalPoint = portalPoints.get(name);
        if (portalPoint == null && getDefault) {
            String defaultPoint = CCConfig.defaultPoint.get();
            if (defaultPoint != null) {
                return portalPoints.get(defaultPoint);
            }
        }
        return portalPoint;
    }

    public void changePointInfo(PortalPoint point) {
        portalPoints.put(point.name(), point);
        this.setDirty();
    }

    public boolean containsKey(String name) {
        return portalPoints.containsKey(name);
    }

    public static boolean alreadyExisted(MinecraftServer server, String name) {
        PortalPointManager portalPointManager = get(server);
        boolean flag = portalPointManager.containsKey(name);
        if (!flag) {
            return false;
        }
        PortalPoint point = portalPointManager.portalPoints.get(name);
        ServerLevel level = server.getLevel(point.dimension());
        if (Objects.requireNonNull(level).getBlockEntity(point.pos().below()) instanceof PortalPointBlockEntity blockEntity) {
            return blockEntity.getPointName().equals(name);
        } else {
            return false;
        }
    }

    public static List<PortalPoint> getPublicPointsByGroup(ServerPlayer player, String groupName) {
        List<PortalPoint> points = new ArrayList<>();
        for (PortalPoint point : PortalPointManager.get(player.getServer()).getAll()) {
            if (point.isPublic(groupName)) {
                points.add(point);
            }
        }
        return points;
    }

    public static List<PortalPoint> getPoints(CheckType checkType, ServerPlayer player) {
        MinecraftServer server = player.getServer();
        UUID uuid = player.getUUID();
        List<PortalPoint> points;
        switch (checkType) {
            case MINE -> points = PortalPointManager.getOwnPoints(server, uuid);
            case PUBLIC -> points = PortalPointManager.getPublicPoints(server);
            case OTHERS -> points = PortalPointManager.getViablePointsForSomeone(server, uuid, Boolean.FALSE);
            case OP_ALL -> points = PortalPointManager.get(server).getAll();
            default -> points = PortalPointManager.getViablePointsForSomeone(server, uuid, Boolean.TRUE);
        }
        return points;
    }

    private static List<PortalPoint> getPublicPoints(MinecraftServer server) {
        List<PortalPoint> points = new ArrayList<>();
        for (PortalPoint point : PortalPointManager.get(server).getAll()) {
            if (point.isPublic()) {
                points.add(point);
            }
        }
        return points;
    }

    private static List<PortalPoint> getOwnPoints(MinecraftServer server, UUID someone) {
        List<PortalPoint> points = new ArrayList<>();
        for (PortalPoint point : PortalPointManager.get(server).getAll()) {
            if (point.ownerUid().equals(someone)) {
                points.add(point);
            }
        }
        return points;
    }

    private static List<PortalPoint> getViablePointsForSomeone(MinecraftServer server, UUID someone, boolean containsSelf) {
        PermissionManager permissionManager = PermissionManager.get(server);
        PortalPointManager portalPointManager = PortalPointManager.get(server);
        List<PortalPoint> points = new ArrayList<>();
        Set<UUID> friends = new HashSet<>();

        if (containsSelf) {
            friends.add(someone);
        }

        for (PortalPoint point : portalPointManager.portalPoints.values()) {
            UUID owner = point.ownerUid();
            if (!containsSelf && owner.equals(someone)) {
                continue;
            }

            if (point.isPublic() || friends.contains(owner)) {
                points.add(point);
                continue;
            }

            Permission permission = permissionManager.get(owner);
            if (permission != null && permission.contains(someone)) {
                friends.add(owner);
                points.add(point);
            }
        }
        return points;
    }

    public List<PortalPoint> getAll() {
        return new ArrayList<>(portalPoints.values());
    }
}
