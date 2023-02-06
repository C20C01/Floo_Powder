package io.github.c20c01.item.destroyByFireToUse;

import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public interface DestroyByFireToUse {
    static void explode(Vec3 pos, Level level) {
        level.explode(null, pos.x(), pos.y(), pos.z(), 0.1F, false, Explosion.BlockInteraction.NONE);
    }
}