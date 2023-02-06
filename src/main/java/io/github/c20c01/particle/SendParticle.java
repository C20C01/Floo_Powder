package io.github.c20c01.particle;

import io.github.c20c01.network.CCNetwork;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;

/**
 * 用来在服务端向客户端发送信息生成粒子效果
 */
public class SendParticle {
    public enum Particles {
        PORTAL, FALLING_LAVA, FLAME, SMOKE, RAY, CLOUD
    }

    public enum Modes {
        BALL, LINE
    }

    public static void ball(ServerLevel level, Particles particleID, Vec3 pos) {
        var packet = new CCNetwork.ParticlePacket(particleID, Modes.BALL, pos, null);
        sendAll(level, packet);
    }

    public static void line(ServerLevel level, ServerPlayer player, Particles particleID, Vec3 pos, Vec3 speedVec) {
        var packet = new CCNetwork.ParticlePacket(particleID, Modes.LINE, pos, speedVec);
        sendOther(level, player, packet);
    }

    private static void sendAll(ServerLevel level, CCNetwork.ParticlePacket packet) {
        var list = level.getPlayers(ServerPlayer::isAlive);
        for (ServerPlayer p : list)
            CCNetwork.CHANNEL_Particle_TO_C.send(PacketDistributor.PLAYER.with(() -> p), packet);
    }

    private static void sendOther(ServerLevel level, ServerPlayer self, CCNetwork.ParticlePacket packet) {
        var list = level.getPlayers(ServerPlayer::isAlive);
        list.remove(self);
        for (ServerPlayer p : list)
            CCNetwork.CHANNEL_Particle_TO_C.send(PacketDistributor.PLAYER.with(() -> p), packet);
    }
}
