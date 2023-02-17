package io.github.c20c01.savedData;

import io.github.c20c01.block.portalFire.BasePortalFireBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

import java.util.Objects;
import java.util.UUID;

/**
 * @param name        名称
 * @param describe    描述
 * @param pos         坐标（传送核心向上一格，火焰所在的那格）
 * @param dimension   维度
 * @param ownerUid    拥有者uuid
 * @param isPublic    是否公开（暴露给其他玩家）
 * @param isTemporary 是否为临时的（传送法杖建立、没有权限限制、玩家退出或死亡会消失）
 */

public record PortalPoint(String name, String describe, BlockPos pos,
                          ResourceKey<Level> dimension, UUID ownerUid, boolean isPublic, boolean isTemporary) {

    public ServerLevel getLevel(MinecraftServer server) {
        return Objects.requireNonNull(server).getLevel(dimension);
    }

    public void removeFire(MinecraftServer server) {
        var level = server.getLevel(dimension);
        if (level != null && level.getBlockState(pos).getBlock() instanceof BasePortalFireBlock) {
            level.removeBlock(pos, Boolean.FALSE);
        }
    }

    @Override
    public String toString() {
        return "PortalPoint{" +
                "name='" + name + '\'' +
                ", describe='" + describe + '\'' +
                ", pos=" + pos +
                ", dimension=" + dimension.location() +
                ", ownerUid=" + ownerUid +
                ", isPublic=" + isPublic +
                ", isTemporary=" + isTemporary +
                '}';
    }

    public static PortalPoint read(CompoundTag compoundTag) {
        String name = compoundTag.getString("Name");
        String describe = compoundTag.getString("Describe");
        BlockPos pos = NbtUtils.readBlockPos(compoundTag.getCompound("BlockPos"));
        ResourceKey<Level> dimension = ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(compoundTag.getString("Dimension")));
        UUID ownerUid = NbtUtils.loadUUID(Objects.requireNonNull(compoundTag.get("OwnerUid")));
        boolean isPublic = compoundTag.getBoolean("IsPublic");
        boolean isOnce = compoundTag.getBoolean("IsTemporary");
        return new PortalPoint(name, describe, pos, dimension, ownerUid, isPublic, isOnce);
    }

    public static CompoundTag write(PortalPoint portalPoint, CompoundTag compoundTag) {
        compoundTag.putString("Name", portalPoint.name());
        compoundTag.putString("Describe", portalPoint.describe());
        compoundTag.put("BlockPos", NbtUtils.writeBlockPos(portalPoint.pos()));
        compoundTag.putString("Dimension", portalPoint.dimension().location().toString());
        compoundTag.put("OwnerUid", NbtUtils.createUUID(portalPoint.ownerUid()));
        compoundTag.putBoolean("IsPublic", portalPoint.isPublic());
        compoundTag.putBoolean("IsTemporary", portalPoint.isTemporary());
        return compoundTag;
    }
}
