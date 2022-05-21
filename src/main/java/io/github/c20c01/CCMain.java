package io.github.c20c01;

import io.github.c20c01.block.PortalFireBlock;
import io.github.c20c01.block.PortalFireBlockEntity;
import io.github.c20c01.block.PortalPointBlock;
import io.github.c20c01.block.PortalPointBlockEntity;
import io.github.c20c01.item.FlooPowder;
import io.github.c20c01.item.PortalFlintAndSteel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod(CCMain.ID)
public class CCMain {
    public static final String ID = "cc2001_floo_powder";

    public static final String TEXT_GET_FLOO_TITLE = "advancement." + ID + ".get_floo.title";
    public static final String TEXT_GET_FLOO_DESC = "advancement." + ID + ".get_floo.description";

    public static final String TEXT_INTO_FIRE_TITLE = "advancement." + ID + ".into_fire.title";
    public static final String TEXT_INTO_FIRE_DESC = "advancement." + ID + ".into_fire.description";

    public static final String TEXT_GET_POINT_BLOCK_TITLE = "advancement." + ID + ".get_portal_point_block.title";
    public static final String TEXT_GET_POINT_BLOCK_DESC = "advancement." + ID + ".get_portal_point_block.description";

    public static final String TEXT_NEEDS_ACTIVATION = "chat." + ID + ".use_portal_flint_and_steel";
    public static final String TEXT_NOT_LOADED = "chat." + ID + ".point_has_not_been_loaded";
    public static final String TEXT_NOT_FOUND = "chat." + ID + ".no_point_find";
    public static final String TEXT_NOT_FOUND_BOOK = "chat." + ID + ".no_point_find_book";
    public static final String TEXT_FOUND_BOOK = "chat." + ID + ".point_find_book";

    public static final ResourceLocation GET_FLOO_ADVANCEMENT_ID = new ResourceLocation(ID, "get_floo");
    public static final ResourceLocation INTO_FIRE_ADVANCEMENT_ID = new ResourceLocation(ID, "into_fire");
    public static final ResourceLocation GET_POINT_BLOCK_ADVANCEMENT_ID = new ResourceLocation(ID, "get_portal_point_block");

    public static final ResourceLocation FLOO_POWDER_ID = new ResourceLocation(ID, "floo_powder");
    public static final RegistryObject<FlooPowder> FLOO_POWDER_ITEM = RegistryObject.create(CCMain.FLOO_POWDER_ID, ForgeRegistries.ITEMS);

    public static final ResourceLocation PORTAL_FLINT_AND_STEEL_ID = new ResourceLocation(ID, "portal_flint_and_steel");
    public static final RegistryObject<PortalFlintAndSteel> PORTAL_FLINT_AND_STEEL_ITEM = RegistryObject.create(CCMain.PORTAL_FLINT_AND_STEEL_ID, ForgeRegistries.ITEMS);

    public static final ResourceLocation PORTAL_POINT_BLOCK_ITEM_ID = new ResourceLocation(ID, "portal_point_block_item");
    public static final RegistryObject<BlockItem> PORTAL_POINT_BLOCK_ITEM = RegistryObject.create(CCMain.PORTAL_POINT_BLOCK_ITEM_ID, ForgeRegistries.ITEMS);

    public static final ResourceLocation PORTAL_BOOK_ID = new ResourceLocation(ID, "portal_book");
    public static final RegistryObject<FlooPowder> PORTAL_BOOK_ITEM = RegistryObject.create(CCMain.PORTAL_BOOK_ID, ForgeRegistries.ITEMS);

    public static final ResourceLocation PORTAL_FIRE_BLOCK_ID = new ResourceLocation(ID, "portal_fire_block");
    public static final RegistryObject<PortalFireBlock> PORTAL_FIRE_BLOCK = RegistryObject.create(CCMain.PORTAL_FIRE_BLOCK_ID, ForgeRegistries.BLOCKS);
    public static final RegistryObject<BlockEntityType<PortalFireBlockEntity>> PORTAL_FIRE_BLOCK_ENTITY = RegistryObject.create(CCMain.PORTAL_FIRE_BLOCK_ID, ForgeRegistries.BLOCK_ENTITIES);

    public static final ResourceLocation PORTAL_POINT_BLOCK_ID = new ResourceLocation(ID, "portal_point_block");
    public static final RegistryObject<PortalPointBlock> PORTAL_POINT_BLOCK = RegistryObject.create(CCMain.PORTAL_POINT_BLOCK_ID, ForgeRegistries.BLOCKS);
    public static final RegistryObject<BlockEntityType<PortalPointBlockEntity>> PORTAL_POINT_BLOCK_ENTITY = RegistryObject.create(CCMain.PORTAL_POINT_BLOCK_ID, ForgeRegistries.BLOCK_ENTITIES);
}