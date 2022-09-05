package io.github.c20c01.network;

import io.github.c20c01.CCMain;
import io.github.c20c01.block.FlooPowderGiverBlock;
import io.github.c20c01.gui.FlooPowderGiverGui;
import io.github.c20c01.gui.GuiData;
import io.github.c20c01.particle.PlayParticle;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class CCNetwork {

    public static final SimpleChannel CHANNEL_DESC_TO_S = NetworkRegistry.newSimpleChannel(CCMain.NETWORK_ID_DESC_TO_S, () -> CCMain.NETWORK_VERSION, CCMain.NETWORK_VERSION::equals, CCMain.NETWORK_VERSION::equals);
    public static final SimpleChannel CHANNEL_DESC_TO_C = NetworkRegistry.newSimpleChannel(CCMain.NETWORK_ID_DESC_TO_C, () -> CCMain.NETWORK_VERSION, CCMain.NETWORK_VERSION::equals, CCMain.NETWORK_VERSION::equals);
    public static final SimpleChannel CHANNEL_NAME_TO_S = NetworkRegistry.newSimpleChannel(CCMain.NETWORK_ID_NAME_TO_S, () -> CCMain.NETWORK_VERSION, CCMain.NETWORK_VERSION::equals, CCMain.NETWORK_VERSION::equals);
    public static final SimpleChannel CHANNEL_PLAYER_CODE_TO_C = NetworkRegistry.newSimpleChannel(CCMain.NETWORK_ID_PLAYER_CODE_TO_C, () -> CCMain.NETWORK_VERSION, CCMain.NETWORK_VERSION::equals, CCMain.NETWORK_VERSION::equals);
    public static final SimpleChannel CHANNEL_Particle_TO_C = NetworkRegistry.newSimpleChannel(CCMain.NETWORK_ID_PARTICLE_TO_C, () -> CCMain.NETWORK_VERSION, CCMain.NETWORK_VERSION::equals, CCMain.NETWORK_VERSION::equals);

    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        CHANNEL_DESC_TO_S.registerMessage(0, PointDescPacket.class, PointDescPacket::encode, PointDescPacket::decode, PointDescPacket::handleToS, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        CHANNEL_DESC_TO_C.registerMessage(0, PointDescPacket.class, PointDescPacket::encode, PointDescPacket::decode, PointDescPacket::handleToC, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        CHANNEL_NAME_TO_S.registerMessage(0, PowderNamePacket.class, PowderNamePacket::encode, PowderNamePacket::decode, PowderNamePacket::handleToS, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        CHANNEL_PLAYER_CODE_TO_C.registerMessage(0, PowderNamePacket.class, PowderNamePacket::encode, PowderNamePacket::decode, PowderNamePacket::handleToC, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        CHANNEL_Particle_TO_C.registerMessage(0, ParticlePacket.class, ParticlePacket::encode, ParticlePacket::decode, ParticlePacket::handleToC, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    }

    public record PointDescPacket(Map<String, String> map) {
        public static PointDescPacket decode(FriendlyByteBuf buf) {
            return new PointDescPacket(buf.readMap(FriendlyByteBuf::readUtf, FriendlyByteBuf::readUtf));
        }

        public void encode(FriendlyByteBuf buf) {
            buf.writeMap(this.map, FriendlyByteBuf::writeUtf, FriendlyByteBuf::writeUtf);
        }

        public void handleToS(Supplier<NetworkEvent.Context> supplier) {
            supplier.get().enqueueWork(() -> GuiData.setDescMapFromClient((HashMap<String, String>) this.map));
            supplier.get().setPacketHandled(true);
        }

        public void handleToC(Supplier<NetworkEvent.Context> supplier) {
            supplier.get().enqueueWork(() -> GuiData.setDescMapFromServer((HashMap<String, String>) this.map));
            supplier.get().setPacketHandled(true);
        }
    }

    public record PowderNamePacket(String name, int code) {
        public static PowderNamePacket decode(FriendlyByteBuf buf) {
            return new PowderNamePacket(buf.readUtf(), buf.readInt());
        }

        public void encode(FriendlyByteBuf buf) {
            buf.writeUtf(this.name);
            buf.writeInt(this.code);
        }

        public void handleToS(Supplier<NetworkEvent.Context> supplier) {
            supplier.get().enqueueWork(() -> FlooPowderGiverBlock.handle_S(this.name, this.code));
            supplier.get().setPacketHandled(true);
        }

        public void handleToC(Supplier<NetworkEvent.Context> supplier) {
            supplier.get().enqueueWork(() -> FlooPowderGiverGui.playerCode = this.code);
            supplier.get().setPacketHandled(true);
        }
    }

    public record ParticlePacket(BlockPos pos, double r, short particleID) {
        public static ParticlePacket decode(FriendlyByteBuf buf) {
            return new ParticlePacket(buf.readBlockPos(), buf.readDouble(), buf.readShort());
        }

        public void encode(FriendlyByteBuf buf) {
            buf.writeBlockPos(this.pos);
            buf.writeDouble(this.r);
            buf.writeShort(this.particleID);
        }

        public void handleToC(Supplier<NetworkEvent.Context> supplier) {
            supplier.get().enqueueWork(() -> PlayParticle.playParticle_C(pos,r,particleID));
            supplier.get().setPacketHandled(true);
        }
    }
}