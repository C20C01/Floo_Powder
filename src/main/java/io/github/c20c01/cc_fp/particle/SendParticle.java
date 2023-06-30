package io.github.c20c01.cc_fp.particle;

import io.github.c20c01.cc_fp.network.CCNetwork;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;

import java.util.List;

/**
 * 用来在服务端向客户端发送信息生成粒子效果
 */
public class SendParticle {
    public enum Particles {
        FLAME, SMOKE, RAY, CLOUD
    }

    public enum Modes {
        BALL, LINE, SUCK
    }

    public static void ball(ServerLevel level, Particles particleID, Vec3 pos) {
        var packet = new CCNetwork.ParticlePacket(particleID, Modes.BALL, pos, new float[0]);
        sendToAll(level, packet);
    }

    public static void line(ServerLevel level, ServerPlayer player, Particles particleID, Vec3 pos, Vec3 speedVec) {
        var packet = new CCNetwork.ParticlePacket(particleID, Modes.LINE, pos, new float[]{(float) speedVec.x, (float) speedVec.y, (float) speedVec.z});
        sendToOther(level, player, packet);
    }

    public static void suck(ServerLevel level, ServerPlayer player, Particles particleID, Vec3 pos, float size) {
        var packet = new CCNetwork.ParticlePacket(particleID, Modes.SUCK, pos, new float[]{size});
        sendToOther(level, player, packet);
    }

    private static void sendToAll(ServerLevel level, CCNetwork.ParticlePacket packet) {
        sendParticle(level.getPlayers(ServerPlayer::isAlive), packet);
    }

    private static void sendToOther(ServerLevel level, ServerPlayer self, CCNetwork.ParticlePacket packet) {
        var list = level.getPlayers(ServerPlayer::isAlive);
        list.remove(self);
        sendParticle(list, packet);
    }

    private static void sendParticle(List<ServerPlayer> list, CCNetwork.ParticlePacket packet) {
        for (ServerPlayer p : list)
            CCNetwork.CHANNEL_Particle_TO_C.send(PacketDistributor.PLAYER.with(() -> p), packet);
    }
}
