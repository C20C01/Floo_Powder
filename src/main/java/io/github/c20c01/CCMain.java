package io.github.c20c01;

import com.mojang.datafixers.DSL;
import io.github.c20c01.block.FlooPowderGiverBlock;
import io.github.c20c01.block.portalFire.FakePortalFireBlock;
import io.github.c20c01.block.portalFire.PortalFireBlock;
import io.github.c20c01.block.portalFire.PortalFireBlockEntity;
import io.github.c20c01.block.portalPoint.PortalPointBlock;
import io.github.c20c01.block.portalPoint.PortalPointBlockEntity;
import io.github.c20c01.item.FlooPowder;
import io.github.c20c01.item.PortalBook;
import io.github.c20c01.item.PortalFlintAndSteel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
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
    public static final String TEXT_ACTIVATED_BY_BOOK = "chat." + ID + ".only_fire_not_point";
    public static final String TEXT_LOADED_WRONG = "chat." + ID + ".point_has_been_loaded_wrong";
    public static final String TEXT_NOT_FOUND = "chat." + ID + ".no_point_find";
    public static final String TEXT_NOT_FOUND_BOOK = "chat." + ID + ".no_point_find_book";
    public static final String TEXT_FOUND_BOOK = "chat." + ID + ".point_find_book";
    public static final String TEXT_SET_PORTAL_FIRE_BOOK = "chat." + ID + ".set_portal_fire_book";
    public static final String TEXT_SET_PORTAL_POINT_BOOK = "chat." + ID + ".set_portal_point_book";

    public static final String TEXT_DONE = "gui." + ID + ".done";
    public static final String TEXT_CANCEL = "gui." + ID + ".cancel";
    public static final String TEXT_PREVIOUS_PAGE = "gui." + ID + ".previous_page";
    public static final String TEXT_NEXT_PAGE = "gui." + ID + ".next_page";

    public static final String NETWORK_VERSION = "1";
    public static final ResourceLocation NETWORK_ID_DESC_TO_S = new ResourceLocation(ID, "network_desc_to_s");
    public static final ResourceLocation NETWORK_ID_DESC_TO_C = new ResourceLocation(ID, "network_desc_to_c");
    public static final ResourceLocation NETWORK_ID_NAME_TO_S = new ResourceLocation(ID, "network_name_to_s");
    public static final ResourceLocation NETWORK_ID_PLAYER_CODE_TO_C = new ResourceLocation(ID, "network_player_code_to_c");
    public static final ResourceLocation NETWORK_ID_PARTICLE_TO_C = new ResourceLocation(ID, "network_particle_to_c");

    public static final ResourceLocation FLOO_POWDER_GIVER_GUI_BACKGROUND = new ResourceLocation(ID, "textures/gui/floo_powder_giver_gui_background.png");

    public static final ResourceLocation GET_FLOO_ADVANCEMENT_ID = new ResourceLocation(ID, "get_floo");
    public static final ResourceLocation INTO_FIRE_ADVANCEMENT_ID = new ResourceLocation(ID, "into_fire");
    public static final ResourceLocation GET_POINT_BLOCK_ADVANCEMENT_ID = new ResourceLocation(ID, "get_portal_point_block");

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ID);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, ID);

    public static final String FLOO_POWDER_ID = "floo_powder";
    public static final RegistryObject<FlooPowder> FLOO_POWDER_ITEM;

    public static final String PORTAL_FLINT_AND_STEEL_ID = "portal_flint_and_steel";
    public static final RegistryObject<PortalFlintAndSteel> PORTAL_FLINT_AND_STEEL_ITEM;

    public static final String PORTAL_BOOK_ID = "portal_book";
    public static final RegistryObject<PortalBook> PORTAL_BOOK_ITEM;

    public static final String FAKE_PORTAL_FIRE_BLOCK_ID = "fake_portal_fire_block";
    public static final RegistryObject<FakePortalFireBlock> FAKE_PORTAL_FIRE_BLOCK;

    public static final String PORTAL_FIRE_BLOCK_ID = "portal_fire_block";
    public static final RegistryObject<PortalFireBlock> PORTAL_FIRE_BLOCK;
    public static final RegistryObject<BlockEntityType<PortalFireBlockEntity>> PORTAL_FIRE_BLOCK_ENTITY;

    public static final String PORTAL_POINT_BLOCK_ID = "portal_point_block";
    public static final RegistryObject<PortalPointBlock> PORTAL_POINT_BLOCK;
    public static final String PORTAL_POINT_BLOCK_ITEM_ID = "portal_point_block_item";
    public static final RegistryObject<BlockItem> PORTAL_POINT_BLOCK_ITEM;
    public static final RegistryObject<BlockEntityType<PortalPointBlockEntity>> PORTAL_POINT_BLOCK_ENTITY;

    public static final String FLOO_POWDER_GIVER_BLOCK_ID = "powder_giver_block";
    public static final RegistryObject<FlooPowderGiverBlock> FLOO_POWDER_GIVER_BLOCK;
    public static final String FLOO_POWDER_GIVER_BLOCK_ITEM_ID = "powder_giver_block_item";
    public static final RegistryObject<BlockItem> FLOO_POWDER_GIVER_BLOCK_ITEM;


    static {
        FLOO_POWDER_ITEM = ITEMS.register(FLOO_POWDER_ID,
                () -> new FlooPowder(new Item.Properties().tab(CreativeModeTab.TAB_TRANSPORTATION)));
        PORTAL_FLINT_AND_STEEL_ITEM = ITEMS.register(PORTAL_FLINT_AND_STEEL_ID,
                () -> new PortalFlintAndSteel(new Item.Properties().tab(CreativeModeTab.TAB_TRANSPORTATION).durability(1)));
        PORTAL_BOOK_ITEM = ITEMS.register(PORTAL_BOOK_ID,
                () -> new PortalBook(new Item.Properties().tab(CreativeModeTab.TAB_TRANSPORTATION).stacksTo(1)));
        FAKE_PORTAL_FIRE_BLOCK = BLOCKS.register(FAKE_PORTAL_FIRE_BLOCK_ID,
                () -> new FakePortalFireBlock(BlockBehaviour.Properties.of(Material.FIRE, MaterialColor.COLOR_LIGHT_GREEN).noCollission().instabreak().lightLevel((x) -> 15), 0F));
        PORTAL_FIRE_BLOCK = BLOCKS.register(PORTAL_FIRE_BLOCK_ID,
                () -> new PortalFireBlock(BlockBehaviour.Properties.of(Material.FIRE, MaterialColor.COLOR_LIGHT_GREEN).noCollission().instabreak().lightLevel((x) -> 15), 0F));
        PORTAL_FIRE_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register(PORTAL_FIRE_BLOCK_ID,
                () -> BlockEntityType.Builder.of(PortalFireBlockEntity::new, PORTAL_FIRE_BLOCK.get()).build(DSL.remainderType()));
        PORTAL_POINT_BLOCK = BLOCKS.register(PORTAL_POINT_BLOCK_ID,
                () -> new PortalPointBlock(BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BLACK).strength(30.0F, 1200.0F)));
        PORTAL_POINT_BLOCK_ITEM = ITEMS.register(PORTAL_POINT_BLOCK_ITEM_ID,
                () -> new BlockItem(PORTAL_POINT_BLOCK.get(), new Item.Properties().tab(CreativeModeTab.TAB_TRANSPORTATION)));
        PORTAL_POINT_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register(PORTAL_POINT_BLOCK_ID,
                () -> BlockEntityType.Builder.of(PortalPointBlockEntity::new, PORTAL_POINT_BLOCK.get()).build(DSL.remainderType()));
        FLOO_POWDER_GIVER_BLOCK = BLOCKS.register(FLOO_POWDER_GIVER_BLOCK_ID,
                () -> new FlooPowderGiverBlock(BlockBehaviour.Properties.of(Material.STONE, MaterialColor.STONE).strength(10.0F, 1200.0F).lightLevel((x) -> 15)));
        FLOO_POWDER_GIVER_BLOCK_ITEM = ITEMS.register(FLOO_POWDER_GIVER_BLOCK_ITEM_ID,
                () -> new BlockItem(FLOO_POWDER_GIVER_BLOCK.get(), new Item.Properties().tab(CreativeModeTab.TAB_TRANSPORTATION)));
    }

    public CCMain() {
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
        BLOCK_ENTITY_TYPES.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

}