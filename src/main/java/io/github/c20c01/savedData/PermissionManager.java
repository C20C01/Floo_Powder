package io.github.c20c01.savedData;

import io.github.c20c01.CCMain;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.UUID;

public class PermissionManager extends SavedData {
    private static final String TAG = "Permissions";
    private static final String DATA_NAME = CCMain.ID + "_" + TAG;
    private static final PermissionManager clientStorageCopy = new PermissionManager();

    private final HashMap<UUID, Permission> permissions = new HashMap<>();

    public static PermissionManager get(@Nullable MinecraftServer server) {
        if (server != null) {
            ServerLevel overWorld = server.getLevel(Level.OVERWORLD);
            return Objects.requireNonNull(overWorld).getDataStorage().computeIfAbsent(PermissionManager::read, PermissionManager::new, DATA_NAME);
        }
        return clientStorageCopy;
    }

    public static PermissionManager read(CompoundTag compoundTag) {
        PermissionManager permissionManager = new PermissionManager();
        ListTag tagList = compoundTag.getList(TAG, Tag.TAG_COMPOUND);
        for (Tag tag : tagList) {
            CompoundTag compound = (CompoundTag) tag;
            Permission permission = Permission.read(compound);
            permissionManager.permissions.put(permission.uuid(), permission);
        }
        return permissionManager;
    }

    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag compoundTag) {
        ListTag tagList = new ListTag();
        for (Permission permission : permissions.values()) {
            tagList.add(Permission.write(permission, new CompoundTag()));
        }
        compoundTag.put(TAG, tagList);
        return compoundTag;
    }

    public void add(Permission permission) {
        permissions.putIfAbsent(permission.uuid(), permission);
        setDirty();
    }

    public void addNew(Player player) {
        permissions.putIfAbsent(player.getUUID(), new Permission(player.getUUID(), player.getDisplayName().getString(), new HashSet<>()));
        setDirty();
    }

    public void remove(UUID ownerUuid) {
        permissions.remove(ownerUuid);
        setDirty();
    }

    @Nullable
    public Permission get(UUID uuid) {
        return permissions.get(uuid);
    }

    public Permission get(Player player) {
        UUID uuid = player.getUUID();
        Permission permission = permissions.get(uuid);
        if (permission == null)
            permission = new Permission(uuid, player.getDisplayName().getString(), new HashSet<>());
        return permission;
    }

    public Permission[] getAll() {
        return permissions.values().toArray(new Permission[0]);
    }
}
