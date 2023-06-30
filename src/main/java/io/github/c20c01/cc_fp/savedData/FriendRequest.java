package io.github.c20c01.cc_fp.savedData;

import net.minecraft.server.MinecraftServer;

import java.util.UUID;

public record FriendRequest(UUID friend, UUID inserted) {

    void accept(MinecraftServer server) {
        PermissionManager manager = PermissionManager.get(server);
        manager.get(inserted).addFriend(friend);
    }

    @Override
    public int hashCode() {
        return new UUID(friend.hashCode(), inserted.hashCode()).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof FriendRequest request) {
            return request.friend.equals(this.friend) && request.inserted.equals(this.inserted);
        }
        return false;
    }
}
