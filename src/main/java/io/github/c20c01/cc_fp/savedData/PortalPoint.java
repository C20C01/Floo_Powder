package io.github.c20c01.cc_fp.savedData;

import io.github.c20c01.cc_fp.command.Check;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.UUID;

/**
 * @param name        名称
 * @param describe    描述
 * @param pos         坐标（传送核心向上一格，火焰所在的那格）
 * @param dimension   维度
 * @param ownerUid    拥有者uuid
 * @param publicGroup 公开传送点的组别名称
 */

public record PortalPoint(String name, String describe, BlockPos pos,
                          ResourceKey<Level> dimension, UUID ownerUid, String publicGroup) {

    @Nullable
    public ServerLevel getLevel(MinecraftServer server) {
        return Objects.requireNonNull(server).getLevel(dimension);
    }

    @Override
    public String toString() {
        return "PortalPoint{" +
                "name='" + name + '\'' +
                ", describe='" + describe + '\'' +
                ", pos=" + pos +
                ", dimension=" + dimension +
                ", ownerUid=" + ownerUid +
                ", publicGroup='" + publicGroup + '\'' +
                '}';
    }

    public boolean isPublic() {
        return !publicGroup.isEmpty();
    }

    public boolean isPublic(String groupName) {
        return publicGroup.equals(groupName);
    }

    public MutableComponent getComponent() {
        MutableComponent pointInfo = new TextComponent("Owner: ")
                .append(Check.getPlayerNameByUuid(ownerUid))
                .append(new TextComponent("\nDescribe: " + describe));
        if (isPublic()) {
            pointInfo.append(new TextComponent("\nPublicGroup: " + publicGroup));
        }

        MutableComponent component = new TextComponent("• ").withStyle(ChatFormatting.GOLD);
        component.append(new TextComponent(name).setStyle(Style.EMPTY
                        .withColor(ChatFormatting.WHITE)
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, pointInfo))
                )
        );
        component.append(new TextComponent(" [").setStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW)));

        MutableComponent posInfo = new TextComponent("Level: " + dimension.location())
                .append("\nLocation: " + pos.toShortString());

        component.append(new TextComponent("→").setStyle(Style.EMPTY
                        .withColor(ChatFormatting.DARK_GREEN)
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, posInfo))
                        .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/execute in " + dimension.location() + " run tp @s " + pos.getX() + " " + pos.getY() + " " + pos.getZ()))
                )
        );
        component.append(new TextComponent("]").setStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW)));
        return component;
    }

    public static PortalPoint read(CompoundTag compoundTag) {
        String name = compoundTag.getString("Name");
        String describe = compoundTag.getString("Describe");
        BlockPos pos = NbtUtils.readBlockPos(compoundTag.getCompound("BlockPos"));
        ResourceKey<Level> dimension = ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(compoundTag.getString("Dimension")));
        UUID ownerUid = NbtUtils.loadUUID(Objects.requireNonNull(compoundTag.get("OwnerUid")));
        String publicGroupName = compoundTag.getString("PublicGroupName");
        return new PortalPoint(name, describe, pos, dimension, ownerUid, publicGroupName);
    }

    public static CompoundTag write(PortalPoint portalPoint, CompoundTag compoundTag) {
        compoundTag.putString("Name", portalPoint.name());
        compoundTag.putString("Describe", portalPoint.describe());
        compoundTag.put("BlockPos", NbtUtils.writeBlockPos(portalPoint.pos()));
        compoundTag.putString("Dimension", portalPoint.dimension().location().toString());
        compoundTag.put("OwnerUid", NbtUtils.createUUID(portalPoint.ownerUid()));
        compoundTag.putString("PublicGroupName", portalPoint.publicGroup());
        return compoundTag;
    }

    public PortalPoint setDesc(String desc) {
        return new PortalPoint(name, desc, pos, dimension, ownerUid, publicGroup);
    }

    public PortalPoint setPublicGroupName(String groupName) {
        return new PortalPoint(name, describe, pos, dimension, ownerUid, groupName);
    }
}
