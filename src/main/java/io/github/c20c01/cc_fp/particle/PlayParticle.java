package io.github.c20c01.cc_fp.particle;

import io.github.c20c01.cc_fp.client.particles.RayParticle;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.phys.Vec3;

/**
 * 在客户端生成粒子效果，与服务端无关
 */
public class PlayParticle {
    public static void play(SendParticle.Particles particleID, SendParticle.Modes ModeID, Vec3 pos, float[] args) {
        ParticleOptions particle;
        switch (particleID) {
            case RAY -> particle = new RayParticle.Option();
            case SMOKE -> particle = ParticleTypes.SMOKE;
            case FLAME -> particle = ParticleTypes.FLAME;
            default -> particle = ParticleTypes.CLOUD;
        }
        switch (ModeID) {
            case BALL -> ball(particle, pos);
            case LINE -> line(particle, pos, new Vec3(args[0], args[1], args[2]));
            case SUCK -> suck(particle, pos, args[0]);
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

    private static void line(ParticleOptions particle, Vec3 pos, Vec3 speedVec) {
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

    private static void suck(ParticleOptions particle, Vec3 pos, float size) {
        var level = Minecraft.getInstance().level;
        assert level != null;
        var random = level.random;
        for (int j = 0; j < size * 4; ++j) {
            double d1 = (random.nextDouble() - 0.5D) * size;
            double d2 = (random.nextDouble() - 0.2D) * size;
            double d3 = (random.nextDouble() - 0.5D) * size;
            Vec3 particlePos = new Vec3(d1, d2, d3).add(pos);
            Vec3 speedVec = new Vec3(d1, d2, d3).scale(-0.1 / size);
            level.addParticle(particle, particlePos.x, particlePos.y, particlePos.z, speedVec.x, speedVec.y, speedVec.z);
        }
    }
}
