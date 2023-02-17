package io.github.c20c01.savedData;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public record Permission(UUID uuid, String name, Set<UUID> friends) {

    public boolean isFriend(UUID others) {
        return friends.contains(others);
    }

    public boolean addFriend(UUID uuid) {
        return !this.uuid.equals(uuid) && friends.add(uuid);
    }

    public boolean removeFriend(UUID uuid) {
        return friends.remove(uuid);
    }

    public static Permission read(CompoundTag compoundTag) {
        UUID uuid = compoundTag.getUUID("UUID");
        String name = compoundTag.getString("Name");
        Set<UUID> friends = new HashSet<>();
        ListTag tagList = compoundTag.getList("Friends", Tag.TAG_INT);
        for (Tag tag : tagList) {
            friends.add(NbtUtils.loadUUID(tag));
        }
        return new Permission(uuid, name, friends);
    }

    public static CompoundTag write(Permission permission, CompoundTag compoundTag) {
        compoundTag.putUUID("UUID", permission.uuid());
        compoundTag.putString("Name", permission.name());
        ListTag tagList = new ListTag();
        for (UUID friend : permission.friends()) {
            tagList.add(NbtUtils.createUUID(friend));
        }
        compoundTag.put("Friends", tagList);
        return compoundTag;
    }
}
