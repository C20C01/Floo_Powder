package io.github.c20c01.particle;

import io.github.c20c01.client.particles.RayParticle;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

/**
 * 在客户端生成粒子效果，与服务端无关
 */
public class PlayParticle {
    public static void play(SendParticle.Particles particleID, SendParticle.Modes ModeID, Vec3 pos, @Nullable Vec3 speedVec) {
        ParticleOptions particle;
        switch (particleID) {
            case RAY -> particle = new RayParticle.Option();
            case SMOKE -> particle = ParticleTypes.SMOKE;
            default -> particle = ParticleTypes.CLOUD;
        }
        switch (ModeID) {
            case BALL -> ball(particle, pos);
            case LINE -> line(particle, pos, speedVec);
        }
    }

    private static void ball(ParticleOptions particle, Vec3 pos) {
        final float d = 0.8F;
        var level = Minecraft.getInstance().level;
        assert level != null;
        var random = level.random;
        for (int j = 0; j < 128; ++j) {
            double d1 = pos.x + (random.nextDouble() - 0.5D) * d;
            double d2 = pos.y + (random.nextDouble() - 0.5D) * d;
            double d3 = pos.z + (random.nextDouble() - 0.5D) * d;
            float f1 = (random.nextFloat() - 0.5F) * 0.2F;
            float f2 = (random.nextFloat() - 0.5F) * 0.2F;
            float f3 = (random.nextFloat() - 0.5F) * 0.2F;
            level.addParticle(particle, d1, d2, d3, f1, f2, f3);
        }
    }

    public static void line(ParticleOptions particle, Vec3 pos, Vec3 speedVec) {
        final int count = 256;
        final int speed = 10;
        pos = pos.add(speedVec);
        var level = Minecraft.getInstance().level;
        assert level != null;
        for (int i = 0; i < count; i++) {
            double d1 = speedVec.x * speed * i / count;
            double d2 = speedVec.y * speed * i / count;
            double d3 = speedVec.z * speed * i / count;
            level.addParticle(particle, pos.x, pos.y, pos.z, d1, d2, d3);
        }
    }
}
