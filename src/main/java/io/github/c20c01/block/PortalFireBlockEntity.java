package io.github.c20c01.block;

import com.mojang.datafixers.DSL;
import io.github.c20c01.CCMain;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class PortalFireBlockEntity extends BlockEntity {
    public static BlockEntity blockEntity;
    public String name;

    @SubscribeEvent
    public static void onRegisterBlockEntityType(RegistryEvent.Register<BlockEntityType<?>> event) {
        event.getRegistry().register(BlockEntityType.Builder.of(PortalFireBlockEntity::new, CCMain.PORTAL_FIRE_BLOCK.get())
                .build(DSL.remainderType()).setRegistryName(CCMain.PORTAL_FIRE_BLOCK_ID));
    }

    public PortalFireBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(CCMain.PORTAL_FIRE_BLOCK_ENTITY.get(), blockPos, blockState);
        blockEntity = this;
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putString("Name", name);
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        name = tag.getString("Name");
    }
}