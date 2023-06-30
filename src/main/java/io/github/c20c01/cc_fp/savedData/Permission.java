package io.github.c20c01.cc_fp.savedData;

import io.github.c20c01.cc_fp.config.CCConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * @param uuid    玩家的uuid
 * @param friends 有权传送至此玩家的传送点的玩家们
 */

public record Permission(UUID uuid, Set<UUID> friends) {
    public Permission(UUID uuid) {
        this(uuid, new HashSet<>());
    }

    public boolean contains(UUID others) {
        return friends.contains(others);
    }

    FriendRequestManager.Result canBeFriendWith(UUID friend) {
        if (this.uuid.equals(friend)) {
            return FriendRequestManager.Result.SELF;
        }
        if (friends.contains(friend)) {
            return FriendRequestManager.Result.CONTAINED;
        }
        if (friends.size() >= CCConfig.maxFriendSize.get()) {
            return FriendRequestManager.Result.OUT_OF_SIZE;
        }
        return FriendRequestManager.Result.SUCCESS;
    }

    public void addFriend(UUID uuid) {
        friends.add(uuid);
    }

    public boolean removeFriend(UUID uuid) {
        return friends.remove(uuid);
    }

    public static Permission read(CompoundTag compoundTag) {
        UUID uuid = compoundTag.getUUID("UUID");
        Set<UUID> friends = new HashSet<>();
        ListTag tagList = compoundTag.getList("Friends", Tag.TAG_INT_ARRAY);
        for (Tag tag : tagList) {
            friends.add(NbtUtils.loadUUID(tag));
        }
        return new Permission(uuid, friends);
    }

    public static CompoundTag write(Permission permission, CompoundTag compoundTag) {
        compoundTag.putUUID("UUID", permission.uuid());
        ListTag tagList = new ListTag();
        for (UUID friend : permission.friends()) {
            tagList.add(NbtUtils.createUUID(friend));
        }
        compoundTag.put("Friends", tagList);
        return compoundTag;
    }
}
