package io.github.c20c01.block;

import com.mojang.datafixers.DSL;
import io.github.c20c01.CCMain;
import io.github.c20c01.pos.PosMap;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class PortalPointBlockEntity extends BlockEntity {
    public static BlockEntity blockEntity;
    public String name = "";

    @SubscribeEvent
    public static void onRegisterBlockEntityType(RegistryEvent.Register<BlockEntityType<?>> event) {
        event.getRegistry().register(BlockEntityType.Builder.of(PortalPointBlockEntity::new,
                CCMain.PORTAL_POINT_BLOCK.get()).build(DSL.remainderType()).setRegistryName(CCMain.PORTAL_POINT_BLOCK_ID));
    }

    public PortalPointBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(CCMain.PORTAL_POINT_BLOCK_ENTITY.get(), blockPos, blockState);
        blockEntity = this;
    }

    @Override
    public void setLevel(Level level) {
        if (level instanceof ServerLevel serverLevel) {
            PosMap.set(name, serverLevel, null);
            //System.out.println("Loaded level: " + serverLevel.hashCode());
            super.setLevel(serverLevel);
        }
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (!Objects.equals(name, ""))
            tag.putString("Name", name);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        name = tag.getString("Name");
    }
}