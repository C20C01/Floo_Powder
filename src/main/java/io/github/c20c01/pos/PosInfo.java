//package io.github.c20c01.pos;
//
//import net.minecraft.core.BlockPos;
//import net.minecraft.server.level.ServerLevel;
//
//public class PosInfo {
//    public BlockPos blockPos;
//    public ServerLevel level;
//
//    public PosInfo(ServerLevel level, BlockPos blockPos) {
//        this.blockPos = blockPos;
//        this.level = level;
//    }
//
//    public boolean noNull() {
//        return blockPos != null && level != null;
//    }
//
//    @Override
//    public String toString() {
//        return "{" + (blockPos == null ? "Null" : blockPos.toShortString()) + "} world: " + (level == null ? "Null" : level.hashCode());
//    }
//}
