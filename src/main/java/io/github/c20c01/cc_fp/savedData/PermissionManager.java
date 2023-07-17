package io.github.c20c01.cc_fp.savedData;

import io.github.c20c01.cc_fp.CCMain;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
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

    public void addPermission(UUID uuid) {
        if (!permissions.containsKey(uuid)) {
            permissions.put(uuid, new Permission(uuid));
        }
    }

    private FriendRequestManager.Result beforeAddingFriend(UUID friend, UUID inserted) {
        var permission = permissions.get(inserted);
        if (permission == null) {
            return FriendRequestManager.Result.PLAYER_NOT_FOUND;
        }
        return permission.canBeFriendWith(friend);
    }

    public FriendRequestManager.Result askToAddFriend(UUID friend, UUID inserted) {
        var result = beforeAddingFriend(friend, inserted);
        if (result == FriendRequestManager.Result.SUCCESS) {
            if (!FriendRequestManager.add(friend, inserted)) {
                return FriendRequestManager.Result.ALREADY_SEND;
            }
        }
        return result;
    }

    public FriendRequestManager.Result acceptAddingFriend(UUID friend, UUID inserted, MinecraftServer server) {
        var result = beforeAddingFriend(friend, inserted);
        if (result == FriendRequestManager.Result.SUCCESS) {
            if (FriendRequestManager.accept(friend, inserted, server)) {
                this.setDirty();
            } else {
                return FriendRequestManager.Result.NO_REQUEST;
            }
        }
        return result;
    }

    public boolean removeFriend(UUID self, UUID other) {
        if (permissions.get(self).removeFriend(other)) {
            this.setDirty();
            return true;
        }
        return false;
    }

    public Permission get(UUID uuid) {
        return permissions.get(uuid);
    }

    public Collection<Permission> getAll() {
        return permissions.values();
    }

    @Mod.EventBusSubscriber(modid = CCMain.ID)
    // 当玩家进入时添加权限，保证每个人都有
    public static class PlayerLoggedIn {
        @SubscribeEvent
        public static void playerLoggedInEvent(PlayerEvent.PlayerLoggedInEvent event) {
            if (event.getEntity() instanceof ServerPlayer serverPlayer) {
                PermissionManager manager = PermissionManager.get(serverPlayer.getServer());
                manager.addPermission(serverPlayer.getUUID());
            }
        }
    }
}
