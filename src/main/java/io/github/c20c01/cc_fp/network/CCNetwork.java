package io.github.c20c01.cc_fp.network;

import io.github.c20c01.cc_fp.CCMain;
import io.github.c20c01.cc_fp.particle.PlayParticle;
import io.github.c20c01.cc_fp.particle.SendParticle;
import io.github.c20c01.cc_fp.savedData.shareData.PortalPointInfo;
import io.github.c20c01.cc_fp.savedData.shareData.SharePointInfos;
import io.github.c20c01.cc_fp.tp.TpTool;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

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
    public static final SimpleChannel CHANNEL_ITEM_STACK_TO_S = NetworkRegistry.newSimpleChannel(CCMain.CHANNEL_ITEM_STACK_TO_S, () -> CCMain.NETWORK_VERSION, CCMain.NETWORK_VERSION::equals, CCMain.NETWORK_VERSION::equals);


    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        CHANNEL_POINT_TO_S.registerMessage(0, PointInfosPacket.class, PointInfosPacket::encode, PointInfosPacket::decode, PointInfosPacket::handleOnServer, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        CHANNEL_POINT_TO_C.registerMessage(0, PointInfosPacket.class, PointInfosPacket::encode, PointInfosPacket::decode, PointInfosPacket::handleOnClient, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        CHANNEL_Particle_TO_C.registerMessage(0, ParticlePacket.class, ParticlePacket::encode, ParticlePacket::decode, ParticlePacket::handleOnClient, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        CHANNEL_Movement_TO_C.registerMessage(0, MovementPacket.class, MovementPacket::encode, MovementPacket::decode, MovementPacket::handleOnClient, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        CHANNEL_ITEM_STACK_TO_S.registerMessage(0, ItemStackPacket.class, ItemStackPacket::encode, ItemStackPacket::decode, ItemStackPacket::handleOnServer, Optional.of(NetworkDirection.PLAY_TO_SERVER));
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

    public record ParticlePacket(SendParticle.Particles p, SendParticle.Modes m, Vec3 pos, float[] args) {
        public static ParticlePacket decode(FriendlyByteBuf buf) {
            var p = buf.readEnum(SendParticle.Particles.class);
            var m = buf.readEnum(SendParticle.Modes.class);
            Vec3 pos = new Vec3(buf.readFloat(), buf.readFloat(), buf.readFloat());
            float[] args;
            switch (m) {
                case LINE -> args = getArgs(buf, 3);
                case SUCK -> args = getArgs(buf, 1);
                default -> args = getArgs(buf, 0);
            }
            return new ParticlePacket(p, m, pos, args);
        }

        private static float[] getArgs(FriendlyByteBuf buf, int size) {
            float[] args = new float[size];
            for (int i = 0; i < size; i++) {
                args[i] = buf.readFloat();
            }
            return args;
        }

        public void encode(FriendlyByteBuf buf) {
            buf.writeEnum(p);
            buf.writeEnum(m);
            buf.writeFloat((float) pos.x);
            buf.writeFloat((float) pos.y);
            buf.writeFloat((float) pos.z);
            for (float arg : args) {
                buf.writeFloat(arg);
            }
        }

        public void handleOnClient(Supplier<NetworkEvent.Context> supplier) {
            supplier.get().enqueueWork(() -> PlayParticle.play(p, m, pos, args));
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

    public record ItemStackPacket(UUID uuid, int slot, ItemStack itemStack) {
        public static ItemStackPacket decode(FriendlyByteBuf buf) {
            return new ItemStackPacket(buf.readUUID(), buf.readInt(), buf.readItem());
        }

        public void encode(FriendlyByteBuf buf) {
            buf.writeUUID(uuid);
            buf.writeInt(slot);
            buf.writeItem(itemStack);
        }

        public void handleOnServer(Supplier<NetworkEvent.Context> supplier) {
            supplier.get().enqueueWork(() -> UpdateItemStack.update(uuid, slot, itemStack));
            supplier.get().setPacketHandled(true);
        }
    }
}