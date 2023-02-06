package io.github.c20c01.network;

import io.github.c20c01.CCMain;
import io.github.c20c01.particle.PlayParticle;
import io.github.c20c01.particle.SendParticle;
import io.github.c20c01.savedData.shareData.PortalPointInfo;
import io.github.c20c01.savedData.shareData.SharePointInfos;
import io.github.c20c01.tp.TpTool;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class CCNetwork {

    public static final SimpleChannel CHANNEL_POINT_TO_S = NetworkRegistry.newSimpleChannel(CCMain.CHANNEL_POINT_TO_S, () -> CCMain.NETWORK_VERSION, CCMain.NETWORK_VERSION::equals, CCMain.NETWORK_VERSION::equals);
    public static final SimpleChannel CHANNEL_POINT_TO_C = NetworkRegistry.newSimpleChannel(CCMain.CHANNEL_POINT_TO_C, () -> CCMain.NETWORK_VERSION, CCMain.NETWORK_VERSION::equals, CCMain.NETWORK_VERSION::equals);
    public static final SimpleChannel CHANNEL_Particle_TO_C = NetworkRegistry.newSimpleChannel(CCMain.CHANNEL_PARTICLE_TO_C, () -> CCMain.NETWORK_VERSION, CCMain.NETWORK_VERSION::equals, CCMain.NETWORK_VERSION::equals);
    public static final SimpleChannel CHANNEL_Movement_TO_C = NetworkRegistry.newSimpleChannel(CCMain.CHANNEL_MOVEMENT_TO_C, () -> CCMain.NETWORK_VERSION, CCMain.NETWORK_VERSION::equals, CCMain.NETWORK_VERSION::equals);

    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        CHANNEL_POINT_TO_S.registerMessage(0, PointInfosPacket.class, PointInfosPacket::encode, PointInfosPacket::decode, PointInfosPacket::handleOnServer, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        CHANNEL_POINT_TO_C.registerMessage(0, PointInfosPacket.class, PointInfosPacket::encode, PointInfosPacket::decode, PointInfosPacket::handleOnClient, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        CHANNEL_Particle_TO_C.registerMessage(0, ParticlePacket.class, ParticlePacket::encode, ParticlePacket::decode, ParticlePacket::handleOnClient, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        CHANNEL_Movement_TO_C.registerMessage(0, MovementPacket.class, MovementPacket::encode, MovementPacket::decode, MovementPacket::handleOnClient, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    }

    public record PointInfosPacket(UUID uuid, List<PortalPointInfo> infos) {
        public static PointInfosPacket decode(FriendlyByteBuf buf) {
            UUID uuid = buf.readUUID();
            int size = buf.readInt();
            List<PortalPointInfo> infos = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                infos.add(new PortalPointInfo(buf.readUtf(), buf.readUtf()));
            }
            return new PointInfosPacket(uuid, infos);
        }

        public void encode(FriendlyByteBuf buf) {
            buf.writeUUID(uuid);
            buf.writeInt(infos.size());
            infos.forEach((info) -> {
                buf.writeUtf(info.name());
                buf.writeUtf(info.describe());
            });
        }

        public void handleOnServer(Supplier<NetworkEvent.Context> supplier) {
            supplier.get().enqueueWork(() -> SharePointInfos.getPointInfosFromC(uuid, infos.get(0)));
            supplier.get().setPacketHandled(true);
        }

        public void handleOnClient(Supplier<NetworkEvent.Context> supplier) {
            supplier.get().enqueueWork(() -> SharePointInfos.getPointInfosFromS(infos));
            supplier.get().setPacketHandled(true);
        }
    }

    public record ParticlePacket(SendParticle.Particles p, SendParticle.Modes m, Vec3 pos, @Nullable Vec3 speedVec) {
        public static ParticlePacket decode(FriendlyByteBuf buf) {
            var p = buf.readEnum(SendParticle.Particles.class);
            var m = buf.readEnum(SendParticle.Modes.class);
            Vec3 pos = new Vec3(buf.readFloat(), buf.readFloat(), buf.readFloat());
            Vec3 speedVec = null;
            if (m == SendParticle.Modes.LINE) {
                speedVec = new Vec3(buf.readFloat(), buf.readFloat(), buf.readFloat());
            }
            return new ParticlePacket(p, m, pos, speedVec);
        }

        public void encode(FriendlyByteBuf buf) {
            buf.writeEnum(p);
            buf.writeEnum(m);
            buf.writeFloat((float) pos.x);
            buf.writeFloat((float) pos.y);
            buf.writeFloat((float) pos.z);
            if (m == SendParticle.Modes.LINE) {
                assert speedVec != null;
                buf.writeFloat((float) speedVec.x);
                buf.writeFloat((float) speedVec.y);
                buf.writeFloat((float) speedVec.z);
            }
        }

        public void handleOnClient(Supplier<NetworkEvent.Context> supplier) {
            supplier.get().enqueueWork(() -> PlayParticle.play(p, m, pos, speedVec));
            supplier.get().setPacketHandled(true);
        }
    }

    public record MovementPacket(float mx, float my, float mz) {
        public static MovementPacket decode(FriendlyByteBuf buf) {
            return new MovementPacket(buf.readFloat(), buf.readFloat(), buf.readFloat());
        }

        public void encode(FriendlyByteBuf buf) {
            buf.writeFloat(mx);
            buf.writeFloat(my);
            buf.writeFloat(mz);
        }

        public void handleOnClient(Supplier<NetworkEvent.Context> supplier) {
            supplier.get().enqueueWork(() -> TpTool.changePlayerMovement(mx, my, mz));
            supplier.get().setPacketHandled(true);
        }
    }
}