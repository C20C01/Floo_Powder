package io.github.c20c01;

import io.github.c20c01.block.PortalFireBlock;
import io.github.c20c01.block.PortalFireBlockEntity;
import io.github.c20c01.block.PortalPointBlock;
import io.github.c20c01.block.PortalPointBlockEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod(CCMain.ID)
public class CCMain {
    public static final String ID = "cc2001_floo_powder";
    public static final ResourceLocation FLOO_POWDER_ID = new ResourceLocation(ID, "floo_powder");
    public static final ResourceLocation PORTAL_FLINT_AND_STEEL_ID = new ResourceLocation(ID, "portal_flint_and_steel");
    public static final ResourceLocation PORTAL_POINT_BLOCK_ITEM_ID = new ResourceLocation(ID, "portal_point_block_item");

    public static final ResourceLocation PORTAL_FIRE_BLOCK_ID = new ResourceLocation(ID, "portal_fire_block");
    public static final RegistryObject<PortalFireBlock> PORTAL_FIRE_BLOCK = RegistryObject.create(CCMain.PORTAL_FIRE_BLOCK_ID, ForgeRegistries.BLOCKS);
    public static final RegistryObject<BlockEntityType<PortalFireBlockEntity>> PORTAL_FIRE_BLOCK_ENTITY = RegistryObject.create(CCMain.PORTAL_FIRE_BLOCK_ID, ForgeRegistries.BLOCK_ENTITIES);

    public static final ResourceLocation PORTAL_POINT_BLOCK_ID = new ResourceLocation(ID, "portal_point_block");
    public static final RegistryObject<PortalPointBlock> PORTAL_POINT_BLOCK = RegistryObject.create(CCMain.PORTAL_POINT_BLOCK_ID, ForgeRegistries.BLOCKS);
    public static final RegistryObject<BlockEntityType<PortalPointBlockEntity>> PORTAL_POINT_BLOCK_ENTITY = RegistryObject.create(CCMain.PORTAL_POINT_BLOCK_ID, ForgeRegistries.BLOCK_ENTITIES);
}
