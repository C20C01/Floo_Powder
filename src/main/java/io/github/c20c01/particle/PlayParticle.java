package io.github.c20c01.particle;

import io.github.c20c01.network.CCNetwork;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.PacketDistributor;

public class PlayParticle {

    public static void playParticle_C(BlockPos pos, double r, short particleID) {
        var level = Minecraft.getInstance().level;
        if (level == null) return;
        var random = level.random;
        ParticleOptions particle;
        switch (particleID) {
            case 1 -> particle = ParticleTypes.PORTAL;
            case 2 -> particle = ParticleTypes.FALLING_LAVA;
            default -> particle = ParticleTypes.CLOUD;
        }

        for (int j = 0; j < 128; ++j) {
            float f = (random.nextFloat() - 0.5F) * 0.2F;
            float f1 = (random.nextFloat() - 0.5F) * 0.2F;
            float f2 = (random.nextFloat() - 0.5F) * 0.2F;
            double d1 = pos.getX() + 0.5 + (random.nextDouble() - 0.5D) * r;
            double d2 = pos.getY() + 0.2 + random.nextDouble();
            double d3 = pos.getZ() + 0.5 + (random.nextDouble() - 0.5D) * r;
            level.addParticle(particle, d1, d2, d3, f, f1, f2);
        }
    }

    public static void playParticle_S(ServerLevel level, BlockPos blockPos, double r, short particleID) {
        for (ServerPlayer p : level.getPlayers(ServerPlayer::isAlive))
            CCNetwork.CHANNEL_Particle_TO_C.send(PacketDistributor.PLAYER.with(() -> p), new CCNetwork.ParticlePacket(blockPos, r, particleID));
    }
}
