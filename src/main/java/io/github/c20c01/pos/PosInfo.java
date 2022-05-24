package io.github.c20c01.pos;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.Nullable;

public class PosInfo {
    public BlockPos blockPos;
    public ServerLevel level;

    public PosInfo(ServerLevel level, BlockPos blockPos) {
        this.blockPos = blockPos;
        this.level = level;
    }

    public void setLevel(@Nullable ServerLevel level) {
        if (level == null) return;
        this.level = level;
    }

    public void setPos(@Nullable BlockPos blockPos) {
        if (blockPos == null) return;
        this.blockPos = blockPos;
    }

    public boolean noNull() {
        return blockPos != null && level != null;
    }

    @Override
    public String toString() {
        return "{" + (blockPos == null ? "Null" : blockPos.toShortString()) + "} world: " + (level == null ? "Null" : level.hashCode());
    }
}
