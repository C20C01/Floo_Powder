package io.github.c20c01;

import com.mojang.datafixers.DSL;
import io.github.c20c01.block.FireBaseBlock;
import io.github.c20c01.block.PowderGiverBlock;
import io.github.c20c01.block.portalChest.PortalChestBlock;
import io.github.c20c01.block.portalChest.PortalChestBlockEntity;
import io.github.c20c01.block.portalFire.FakePortalFireBlock;
import io.github.c20c01.block.portalFire.PortalFireBlock;
import io.github.c20c01.block.portalFire.PortalFireBlockEntity;
import io.github.c20c01.block.portalPoint.PortalPointBlock;
import io.github.c20c01.block.portalPoint.PortalPointBlockEntity;
import io.github.c20c01.client.gui.menu.PortalPointMenu;
import io.github.c20c01.client.particles.PortalFireParticle;
import io.github.c20c01.client.particles.RayParticle;
import io.github.c20c01.item.PortalBook;
import io.github.c20c01.item.PortalFlintAndSteel;
import io.github.c20c01.item.PortalWand;
import io.github.c20c01.item.destroyByFireToUse.FlooPowder;
import io.github.c20c01.item.destroyByFireToUse.LastingPowder;
import io.github.c20c01.item.flooReel.ExpansionReel;
import io.github.c20c01.item.flooReel.FlooReel;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

@Mod(CCMain.ID)
public class CCMain {
    public static final String ID = "cc_fp";

    // 进度文本
    public static final String TEXT_GET_FLOO_TITLE = "advancement." + ID + ".get_floo.title";
    public static final String TEXT_GET_FLOO_DESC = "advancement." + ID + ".get_floo.description";

    public static final String TEXT_INTO_FIRE_TITLE = "advancement." + ID + ".into_fire.title";
    public static final String TEXT_INTO_FIRE_DESC = "advancement." + ID + ".into_fire.description";

    public static final String TEXT_GET_POINT_BLOCK_TITLE = "advancement." + ID + ".get_portal_point_block.title";
    public static final String TEXT_GET_POINT_BLOCK_DESC = "advancement." + ID + ".get_portal_point_block.description";

    // 提示文本
    public static final String TEXT_NEEDS_ACTIVATION = "chat." + ID + ".use_portal_flint_and_steel";
    public static final String TEXT_ACTIVATED_BY_BOOK = "chat." + ID + ".only_fire_not_point";
    public static final String TEXT_LOADED_WRONG = "chat." + ID + ".point_has_been_loaded_wrong";
    public static final String TEXT_NOT_FOUND = "chat." + ID + ".no_point_find";
    public static final String TEXT_NOT_FOUND_BOOK = "chat." + ID + ".no_point_find_book";
    public static final String TEXT_FOUND_BOOK = "chat." + ID + ".point_find_book";
    public static final String TEXT_SET_PORTAL_FIRE_BOOK = "chat." + ID + ".set_portal_fire_book";
    public static final String TEXT_SET_PORTAL_POINT_BOOK = "chat." + ID + ".set_portal_point_book";

    // GUI文本
    public static final String TEXT_DONE = "gui." + ID + ".done";
    public static final String TEXT_CANCEL = "gui." + ID + ".cancel";
    public static final String TEXT_PREVIOUS_PAGE = "gui." + ID + ".previous_page";
    public static final String TEXT_NEXT_PAGE = "gui." + ID + ".next_page";

    // 网络相关
    public static final String NETWORK_VERSION = "1";
    public static final ResourceLocation CHANNEL_POINT_TO_S = new ResourceLocation(ID, "network_point_to_s");
    public static final ResourceLocation CHANNEL_POINT_TO_C = new ResourceLocation(ID, "network_point_to_c");
    public static final ResourceLocation CHANNEL_PARTICLE_TO_C = new ResourceLocation(ID, "network_particle_to_c");
    public static final ResourceLocation CHANNEL_MOVEMENT_TO_C = new ResourceLocation(ID, "network_movement_to_c");

    // 进度相关
    public static final ResourceLocation ADVANCEMENT_GET_FLOO = new ResourceLocation(ID, "get_floo");
    public static final ResourceLocation ADVANCEMENT_INTO_FIRE = new ResourceLocation(ID, "into_fire");
    public static final ResourceLocation ADVANCEMENT_GET_POINT_BLOCK = new ResourceLocation(ID, "get_portal_point_block");

    // 注册器
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ID);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, ID);
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, ID);
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.CONTAINERS, ID);

    // 物品
    public static final String FLOO_POWDER_ID = "floo_powder";
    public static final RegistryObject<FlooPowder> FLOO_POWDER_ITEM;

    public static final String LASTING_POWDER_ID = "lasting_powder";
    public static final RegistryObject<LastingPowder> LASTING_POWDER_ITEM;

    public static final String PORTAL_FLINT_AND_STEEL_ID = "portal_flint_and_steel";
    public static final RegistryObject<PortalFlintAndSteel> PORTAL_FLINT_AND_STEEL_ITEM;

    public static final String PORTAL_BOOK_ID = "portal_book";
    public static final RegistryObject<PortalBook> PORTAL_BOOK_ITEM;

    public static final String PORTAL_WAND_ID = "portal_wand";
    public static final RegistryObject<PortalWand> PORTAL_WAND_ITEM;

    public static final String FLOO_REEL_ID = "floo_reel";
    public static final RegistryObject<FlooReel> FLOO_REEL_ITEM;

    public static final String EXP_REEL_ID_PREFIX = "expansion_reel_";
    public static final RegistryObject<ExpansionReel> EXP_REEL_EVERYONE_ITEM;
    public static final RegistryObject<ExpansionReel> EXP_REEL_FRIENDS_ITEM;
    public static final RegistryObject<ExpansionReel> EXP_REEL_ITEM_ITEM;
    public static final RegistryObject<ExpansionReel> EXP_REEL_MINECART_ITEM;
    public static final RegistryObject<ExpansionReel> EXP_REEL_FALLING_BLOCK_ITEM;
    public static final RegistryObject<ExpansionReel> EXP_REEL_TNT_ITEM;
    public static final RegistryObject<ExpansionReel> EXP_REEL_PROJECTILE_ITEM;
    public static final RegistryObject<ExpansionReel> EXP_REEL_MONSTER_ITEM;
    public static final RegistryObject<ExpansionReel> EXP_REEL_AGEABLE_MOB_ITEM;
    public static final RegistryObject<ExpansionReel> EXP_REEL_SAVE_DIRECTION_ITEM;

    // 方块及其物品
    public static final String FAKE_PORTAL_FIRE_BLOCK_ID = "fake_portal_fire_block";
    public static final RegistryObject<FakePortalFireBlock> FAKE_PORTAL_FIRE_BLOCK;

    public static final String PORTAL_FIRE_BLOCK_ID = "portal_fire_block";
    public static final RegistryObject<PortalFireBlock> PORTAL_FIRE_BLOCK;
    public static final RegistryObject<BlockEntityType<PortalFireBlockEntity>> PORTAL_FIRE_BLOCK_ENTITY;

    public static final String PORTAL_POINT_BLOCK_ID = "portal_point_block";
    public static final RegistryObject<PortalPointBlock> PORTAL_POINT_BLOCK;
    public static final RegistryObject<BlockItem> PORTAL_POINT_BLOCK_ITEM;
    public static final RegistryObject<BlockEntityType<PortalPointBlockEntity>> PORTAL_POINT_BLOCK_ENTITY;

    public static final String POWDER_GIVER_BLOCK_ID = "powder_giver_block";
    public static final RegistryObject<PowderGiverBlock> POWDER_GIVER_BLOCK;
    public static final RegistryObject<BlockItem> POWDER_GIVER_BLOCK_ITEM;

    public static final String FIRE_BASE_BLOCK_ID = "fire_base_block";
    public static final RegistryObject<FireBaseBlock> FIRE_BASE_BLOCK;
    public static final RegistryObject<BlockItem> FIRE_BASE_BLOCK_ITEM;

    public static final String PORTAL_CHEST_BLOCK_ID = "portal_chest_block";
    public static final RegistryObject<PortalChestBlock> PORTAL_CHEST_BLOCK;
    public static final RegistryObject<BlockItem> PORTAL_CHEST_BLOCK_ITEM;
    public static final RegistryObject<BlockEntityType<PortalChestBlockEntity>> PORTAL_CHEST_BLOCK_ENTITY;

    // 粒子效果
    public static final String RAY_PARTICLE_ID = "ray_particle";
    public static final RegistryObject<RayParticle.Option> RAY_PARTICLE;

    public static final String PORTAL_FIRE_PARTICLE_ID = "portal_fire_particle";
    public static final RegistryObject<PortalFireParticle.Option> PORTAL_FIRE_PARTICLE;

    // GUI
    public static final String PORTAL_POINT_MENU_ID = "portal_point_menu";
    public static final RegistryObject<MenuType<PortalPointMenu>> PORTAL_POINT_MENU;

    // 创造物品栏
    public static final String TAB_ID = "itemGroup." + ID;
    public static final CreativeModeTab TAB = new CreativeModeTab(ID) {
        public @NotNull ItemStack makeIcon() {
            return new ItemStack(FLOO_POWDER_ITEM.get());
        }
    };

    static {
        FLOO_POWDER_ITEM = ITEMS.register(FLOO_POWDER_ID, () -> new FlooPowder(new Item.Properties().tab(TAB)));
        LASTING_POWDER_ITEM = ITEMS.register(LASTING_POWDER_ID, () -> new LastingPowder(new Item.Properties().tab(TAB)));
        PORTAL_FLINT_AND_STEEL_ITEM = ITEMS.register(PORTAL_FLINT_AND_STEEL_ID, () -> new PortalFlintAndSteel(new Item.Properties().tab(TAB).durability(1)));
        PORTAL_BOOK_ITEM = ITEMS.register(PORTAL_BOOK_ID, () -> new PortalBook(new Item.Properties().tab(TAB).stacksTo(1)));
        PORTAL_WAND_ITEM = ITEMS.register(PORTAL_WAND_ID, () -> new PortalWand(new Item.Properties().tab(TAB).stacksTo(1).rarity(Rarity.EPIC)));
        FLOO_REEL_ITEM = ITEMS.register(FLOO_REEL_ID, () -> new FlooReel(new Item.Properties().tab(TAB)));

        EXP_REEL_EVERYONE_ITEM = registerExpansionReel("everyone", ExpansionReel.Type.everyone);
        EXP_REEL_FRIENDS_ITEM = registerExpansionReel("friends", ExpansionReel.Type.friends);
        EXP_REEL_ITEM_ITEM = registerExpansionReel("item", ExpansionReel.Type.item);
        EXP_REEL_MINECART_ITEM = registerExpansionReel("minecart", ExpansionReel.Type.minecart);
        EXP_REEL_FALLING_BLOCK_ITEM = registerExpansionReel("falling_block", ExpansionReel.Type.fallingBlock);
        EXP_REEL_TNT_ITEM = registerExpansionReel("tnt", ExpansionReel.Type.tnt);
        EXP_REEL_PROJECTILE_ITEM = registerExpansionReel("projectile", ExpansionReel.Type.projectile);
        EXP_REEL_AGEABLE_MOB_ITEM = registerExpansionReel("ageable_mob", ExpansionReel.Type.ageableMob);
        EXP_REEL_MONSTER_ITEM = registerExpansionReel("monster", ExpansionReel.Type.monster);

        EXP_REEL_SAVE_DIRECTION_ITEM = registerExpansionReel("save_direction", ExpansionReel.Type.saveDirection);

        FAKE_PORTAL_FIRE_BLOCK = BLOCKS.register(FAKE_PORTAL_FIRE_BLOCK_ID, FakePortalFireBlock::new);
        PORTAL_FIRE_BLOCK = BLOCKS.register(PORTAL_FIRE_BLOCK_ID, PortalFireBlock::new);
        PORTAL_FIRE_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register(PORTAL_FIRE_BLOCK_ID, () -> BlockEntityType.Builder.of(PortalFireBlockEntity::new, PORTAL_FIRE_BLOCK.get()).build(DSL.remainderType()));
        PORTAL_POINT_BLOCK = BLOCKS.register(PORTAL_POINT_BLOCK_ID, PortalPointBlock::new);
        PORTAL_POINT_BLOCK_ITEM = ITEMS.register(getBlockItemID(PORTAL_POINT_BLOCK_ID), () -> new BlockItem(PORTAL_POINT_BLOCK.get(), new Item.Properties().tab(TAB)));
        PORTAL_POINT_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register(PORTAL_POINT_BLOCK_ID, () -> BlockEntityType.Builder.of(PortalPointBlockEntity::new, PORTAL_POINT_BLOCK.get()).build(DSL.remainderType()));
        POWDER_GIVER_BLOCK = BLOCKS.register(POWDER_GIVER_BLOCK_ID, PowderGiverBlock::new);
        POWDER_GIVER_BLOCK_ITEM = ITEMS.register(getBlockItemID(POWDER_GIVER_BLOCK_ID), () -> new BlockItem(POWDER_GIVER_BLOCK.get(), new Item.Properties().tab(TAB).rarity(Rarity.EPIC)));
        FIRE_BASE_BLOCK = BLOCKS.register(FIRE_BASE_BLOCK_ID, FireBaseBlock::new);
        FIRE_BASE_BLOCK_ITEM = ITEMS.register(getBlockItemID(FIRE_BASE_BLOCK_ID), () -> new BlockItem(FIRE_BASE_BLOCK.get(), new Item.Properties().tab(TAB)));
        PORTAL_CHEST_BLOCK = BLOCKS.register(PORTAL_CHEST_BLOCK_ID, PortalChestBlock::new);
        PORTAL_CHEST_BLOCK_ITEM = ITEMS.register(getBlockItemID(PORTAL_CHEST_BLOCK_ID), () -> new BlockItem(PORTAL_CHEST_BLOCK.get(), new Item.Properties().tab(TAB).rarity(Rarity.EPIC)));
        PORTAL_CHEST_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register(PORTAL_CHEST_BLOCK_ID, () -> BlockEntityType.Builder.of(PortalChestBlockEntity::new, PORTAL_CHEST_BLOCK.get()).build(DSL.remainderType()));

        RAY_PARTICLE = PARTICLE_TYPES.register(RAY_PARTICLE_ID, RayParticle.Option::new);
        PORTAL_FIRE_PARTICLE = PARTICLE_TYPES.register(PORTAL_FIRE_PARTICLE_ID, PortalFireParticle.Option::new);

        PORTAL_POINT_MENU = MENU_TYPES.register(PORTAL_POINT_MENU_ID, () -> new MenuType<>(PortalPointMenu::new));
    }

    private static String getBlockItemID(String blockID) {
        return blockID + "_item";
    }

    private static RegistryObject<ExpansionReel> registerExpansionReel(String typeID, ExpansionReel.Type type) {
        return ITEMS.register(EXP_REEL_ID_PREFIX + typeID, () -> new ExpansionReel(new Item.Properties().tab(TAB).stacksTo(1), type));
    }

    public CCMain() {
        var modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ITEMS.register(modEventBus);
        BLOCKS.register(modEventBus);
        BLOCK_ENTITY_TYPES.register(modEventBus);
        PARTICLE_TYPES.register(modEventBus);
        MENU_TYPES.register(modEventBus);
    }
}