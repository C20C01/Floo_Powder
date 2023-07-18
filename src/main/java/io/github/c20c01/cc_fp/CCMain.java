package io.github.c20c01.cc_fp;

import com.mojang.datafixers.DSL;
import io.github.c20c01.cc_fp.block.FireBaseBlock;
import io.github.c20c01.cc_fp.block.portalChest.PortalChestBlock;
import io.github.c20c01.cc_fp.block.portalChest.PortalChestBlockEntity;
import io.github.c20c01.cc_fp.block.portalFire.FakePortalFireBlock;
import io.github.c20c01.cc_fp.block.portalFire.PortalFireBlock;
import io.github.c20c01.cc_fp.block.portalFire.PortalFireBlockEntity;
import io.github.c20c01.cc_fp.block.portalPoint.PortalPointBlock;
import io.github.c20c01.cc_fp.block.portalPoint.PortalPointBlockEntity;
import io.github.c20c01.cc_fp.block.powderGiver.PowderGiverBlock;
import io.github.c20c01.cc_fp.block.powderGiver.PowderGiverBlockEntity;
import io.github.c20c01.cc_fp.block.powderPot.PowderPotBlock;
import io.github.c20c01.cc_fp.block.powderPot.PowderPotBlockEntity;
import io.github.c20c01.cc_fp.client.gui.menu.PortalPointMenu;
import io.github.c20c01.cc_fp.client.particles.PortalFireParticle;
import io.github.c20c01.cc_fp.client.particles.RayParticle;
import io.github.c20c01.cc_fp.config.CCConfig;
import io.github.c20c01.cc_fp.entity.FlooBall;
import io.github.c20c01.cc_fp.item.*;
import io.github.c20c01.cc_fp.item.flooReel.ExpansionReel;
import io.github.c20c01.cc_fp.item.flooReel.FlooReel;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod(CCMain.ID)
public class CCMain {
    public static final String ID = "cc_fp";
    public static final String VERSION = "1.6.0";

    // 进度文本
    public static final String TEXT_GET_FLOO_TITLE = "advancement." + ID + ".get_floo.title";
    public static final String TEXT_GET_FLOO_DESC = "advancement." + ID + ".get_floo.description";

    public static final String TEXT_INTO_FIRE_TITLE = "advancement." + ID + ".into_fire.title";
    public static final String TEXT_INTO_FIRE_DESC = "advancement." + ID + ".into_fire.description";

    public static final String TEXT_GET_POINT_BLOCK_TITLE = "advancement." + ID + ".get_portal_point_block.title";
    public static final String TEXT_GET_POINT_BLOCK_DESC = "advancement." + ID + ".get_portal_point_block.description";

    // 提示文本
    public static final String TEXT_NOT_FOUND_BOOK = "chat." + ID + ".no_point_find_book";
    public static final String TEXT_SET_PORTAL_FIRE_BOOK = "chat." + ID + ".set_portal_fire_book";
    public static final String TEXT_SET_PORTAL_POINT_FIRE_BOOK = "chat." + ID + ".set_portal_point_fire_book";
    public static final String TEXT_NOT_OWNER = "chat." + ID + ".not_owner";
    public static final String TEXT_ALREADY_EXISTS = "chat." + ID + ".already_existed";
    public static final String TEXT_NAME_TOO_LONG = "chat." + ID + ".name_too_long";
    public static final String TEXT_POINT_SET_PUBLIC = "chat." + ID + ".point_set_public";
    public static final String TEXT_POINT_SET_NO_PUBLIC = "chat." + ID + ".point_set_no_public";
    public static final String TEXT_POINT_SET_DESC = "chat." + ID + ".point_set_desc";
    public static final String TEXT_POINT_UNACTIVATED = "chat." + ID + ".point_unactivated";
    public static final String TEXT_POT_UNLIMITED = "chat." + ID + ".pot_unlimited";
    public static final String TEXT_POWDER_GIVER_GROUP = "chat." + ID + ".powder_giver_group";
    public static final String TEXT_POT_NAME = "chat." + ID + ".pot_name";

    // 命令文本
    public static final String TEXT_CAN_NOT_ADD_YOURSELF = "commands." + ID + ".can_not_add_yourself";
    public static final String TEXT_ALREADY_FRIEND = "commands." + ID + ".already_friend";
    public static final String TEXT_OUT_OF_FRIEND_SIZE = "commands." + ID + ".out_of_friend_size";
    public static final String TEXT_ALREADY_SEND = "commands." + ID + ".already_send";
    public static final String TEXT_REQUEST_SEND = "commands." + ID + ".request_send";
    public static final String TEXT_INVITE_DESC = "commands." + ID + ".invite_desc";
    public static final String TEXT_INVITE_HOVER = "commands." + ID + ".invite_hover";
    public static final String TEXT_REQUEST_DESC = "commands." + ID + ".request_desc";
    public static final String TEXT_REQUEST_HOVER = "commands." + ID + ".request_hover";
    public static final String TEXT_UNKNOWN_REQUEST = "commands." + ID + ".unknown_request";
    public static final String TEXT_UNKNOWN_PLAYER = "commands." + ID + ".unknown_player";
    public static final String TEXT_ADDED_SUCCESSFULLY = "commands." + ID + ".added_successfully";
    public static final String TEXT_CAN_NOT_REMOVE_YOURSELF = "commands." + ID + ".can_not_remove_yourself";
    public static final String TEXT_REMOVED_SUCCESSFULLY = "commands." + ID + ".removed_successfully";
    public static final String TEXT_NOT_FRIEND = "commands." + ID + ".not_friend";
    public static final String TEXT_REMOVE_FRIEND = "commands." + ID + ".remove_friend";

    // GUI文本
    public static final String TEXT_GET = "gui." + ID + ".get";
    public static final String TEXT_CANCEL = "gui." + ID + ".cancel";
    public static final String TEXT_PREVIOUS_PAGE = "gui." + ID + ".previous_page";
    public static final String TEXT_NEXT_PAGE = "gui." + ID + ".next_page";
    public static final String TEXT_RENAME_NEED_NO_EMPTY = "chat." + ID + ".rename_need_no_empty";
    public static final String TEXT_RENAME_COST = "chat." + ID + ".rename_cost";
    public static final String TEXT_RENAME = "gui." + ID + ".rename";

    // 网络相关
    public static final String NETWORK_VERSION = "1";
    public static final ResourceLocation CHANNEL_POINT_TO_S = new ResourceLocation(ID, "network_point_to_s");
    public static final ResourceLocation CHANNEL_POINT_TO_C = new ResourceLocation(ID, "network_point_to_c");
    public static final ResourceLocation CHANNEL_PARTICLE_TO_C = new ResourceLocation(ID, "network_particle_to_c");
    public static final ResourceLocation CHANNEL_MOVEMENT_TO_C = new ResourceLocation(ID, "network_movement_to_c");
    public static final ResourceLocation CHANNEL_ITEM_STACK_TO_S = new ResourceLocation(ID, "network_item_stack_to_c");

    // 进度相关
    public static final ResourceLocation ADVANCEMENT_GET_FLOO = new ResourceLocation(ID, "get_floo");
    public static final ResourceLocation ADVANCEMENT_INTO_FIRE = new ResourceLocation(ID, "into_fire");
    public static final ResourceLocation ADVANCEMENT_GET_POINT_BLOCK = new ResourceLocation(ID, "get_portal_point_block");

    // 注册器
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ID);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, ID);
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, ID);
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.MENU_TYPES, ID);
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, ID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ID);

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

    public static final String FLOO_HANDBAG_ID = "floo_handbag";
    public static final RegistryObject<FlooHandbag> FLOO_HANDBAG_ITEM;

    public static final String NAME_STONE_ID = "name_stone";
    public static final RegistryObject<NameStone> NAME_STONE_ITEM;

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
    public static final RegistryObject<BlockEntityType<PowderGiverBlockEntity>> POWDER_GIVER_BLOCK_ENTITY;

    public static final String FIRE_BASE_BLOCK_ID = "fire_base_block";
    public static final RegistryObject<FireBaseBlock> FIRE_BASE_BLOCK;
    public static final RegistryObject<BlockItem> FIRE_BASE_BLOCK_ITEM;

    public static final String PORTAL_CHEST_BLOCK_ID = "portal_chest_block";
    public static final RegistryObject<PortalChestBlock> PORTAL_CHEST_BLOCK;
    public static final RegistryObject<BlockItem> PORTAL_CHEST_BLOCK_ITEM;
    public static final RegistryObject<BlockEntityType<PortalChestBlockEntity>> PORTAL_CHEST_BLOCK_ENTITY;

    public static final String POWDER_POT_BLOCK_ID = "powder_pot_block";
    public static final RegistryObject<PowderPotBlock> POWDER_POT_BLOCK;
    public static final RegistryObject<BlockItem> POWDER_POT_BLOCK_ITEM;
    public static final RegistryObject<BlockEntityType<PowderPotBlockEntity>> POWDER_POT_BLOCK_ENTITY;

    // 粒子效果
    public static final String RAY_PARTICLE_ID = "ray_particle";
    public static final RegistryObject<RayParticle.Option> RAY_PARTICLE;

    public static final String PORTAL_FIRE_PARTICLE_ID = "portal_fire_particle";
    public static final RegistryObject<PortalFireParticle.Option> PORTAL_FIRE_PARTICLE;

    // GUI
    public static final String PORTAL_POINT_MENU_ID = "portal_point_menu";
    public static final RegistryObject<MenuType<PortalPointMenu>> PORTAL_POINT_MENU;

    // 物品及其实体
    public static final String FLOO_BALL_ID = "floo_ball";
    public static final RegistryObject<FlooBallItem> FLOO_BALL_ITEM;
    public static final RegistryObject<EntityType<FlooBall>> FLOO_BALL_ENTITY;

    static {
        FLOO_POWDER_ITEM = ITEMS.register(FLOO_POWDER_ID, () -> new FlooPowder(getBaseProperties()));
        LASTING_POWDER_ITEM = ITEMS.register(LASTING_POWDER_ID, () -> new LastingPowder(getBaseProperties()));
        PORTAL_FLINT_AND_STEEL_ITEM = ITEMS.register(PORTAL_FLINT_AND_STEEL_ID, () -> new PortalFlintAndSteel(getBaseProperties().durability(1)));
        PORTAL_BOOK_ITEM = ITEMS.register(PORTAL_BOOK_ID, () -> new PortalBook(getBaseProperties().stacksTo(1).rarity(Rarity.EPIC)));
        PORTAL_WAND_ITEM = ITEMS.register(PORTAL_WAND_ID, () -> new PortalWand(getBaseProperties().stacksTo(1).rarity(Rarity.EPIC)));
        FLOO_REEL_ITEM = ITEMS.register(FLOO_REEL_ID, () -> new FlooReel(getBaseProperties()));
        FLOO_HANDBAG_ITEM = ITEMS.register(FLOO_HANDBAG_ID, () -> new FlooHandbag(getBaseProperties().stacksTo(1)));
        NAME_STONE_ITEM = ITEMS.register(NAME_STONE_ID, () -> new NameStone(getBaseProperties().durability(64)));
        FLOO_BALL_ITEM = ITEMS.register(FLOO_BALL_ID, () -> new FlooBallItem(getBaseProperties()));

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
        PORTAL_POINT_BLOCK_ITEM = ITEMS.register(PORTAL_POINT_BLOCK_ID, () -> new BlockItem(PORTAL_POINT_BLOCK.get(), getBaseProperties()));
        PORTAL_POINT_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register(PORTAL_POINT_BLOCK_ID, () -> BlockEntityType.Builder.of(PortalPointBlockEntity::new, PORTAL_POINT_BLOCK.get()).build(DSL.remainderType()));
        POWDER_GIVER_BLOCK = BLOCKS.register(POWDER_GIVER_BLOCK_ID, PowderGiverBlock::new);
        POWDER_GIVER_BLOCK_ITEM = ITEMS.register(POWDER_GIVER_BLOCK_ID, () -> new BlockItem(POWDER_GIVER_BLOCK.get(), getBaseProperties().rarity(Rarity.EPIC)));
        POWDER_GIVER_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register(POWDER_GIVER_BLOCK_ID, () -> BlockEntityType.Builder.of(PowderGiverBlockEntity::new, POWDER_GIVER_BLOCK.get()).build(DSL.remainderType()));
        FIRE_BASE_BLOCK = BLOCKS.register(FIRE_BASE_BLOCK_ID, FireBaseBlock::new);
        FIRE_BASE_BLOCK_ITEM = ITEMS.register(FIRE_BASE_BLOCK_ID, () -> new BlockItem(FIRE_BASE_BLOCK.get(), getBaseProperties()));
        PORTAL_CHEST_BLOCK = BLOCKS.register(PORTAL_CHEST_BLOCK_ID, PortalChestBlock::new);
        PORTAL_CHEST_BLOCK_ITEM = ITEMS.register(PORTAL_CHEST_BLOCK_ID, () -> new BlockItem(PORTAL_CHEST_BLOCK.get(), getBaseProperties().rarity(Rarity.EPIC)));
        PORTAL_CHEST_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register(PORTAL_CHEST_BLOCK_ID, () -> BlockEntityType.Builder.of(PortalChestBlockEntity::new, PORTAL_CHEST_BLOCK.get()).build(DSL.remainderType()));
        POWDER_POT_BLOCK = BLOCKS.register(POWDER_POT_BLOCK_ID, PowderPotBlock::new);
        POWDER_POT_BLOCK_ITEM = ITEMS.register(POWDER_POT_BLOCK_ID, () -> new BlockItem(POWDER_POT_BLOCK.get(), getBaseProperties()));
        POWDER_POT_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register(POWDER_POT_BLOCK_ID, () -> BlockEntityType.Builder.of(PowderPotBlockEntity::new, POWDER_POT_BLOCK.get()).build(DSL.remainderType()));

        RAY_PARTICLE = PARTICLE_TYPES.register(RAY_PARTICLE_ID, RayParticle.Option::new);
        PORTAL_FIRE_PARTICLE = PARTICLE_TYPES.register(PORTAL_FIRE_PARTICLE_ID, PortalFireParticle.Option::new);

        PORTAL_POINT_MENU = MENU_TYPES.register(PORTAL_POINT_MENU_ID, () -> new MenuType<>(PortalPointMenu::new, FeatureFlags.VANILLA_SET));

        FLOO_BALL_ENTITY = ENTITY_TYPES.register(FLOO_BALL_ID, () -> EntityType.Builder.<FlooBall>of(FlooBall::new, MobCategory.MISC).sized(0.25F, 0.25F).build(FLOO_BALL_ID));


        CREATIVE_MODE_TABS.register(ID + "_tab", () -> CreativeModeTab.builder()
                .icon(() -> FLOO_POWDER_ITEM.get().getDefaultInstance())
                .displayItems((parameters, output) -> {
                    for (var entry : ITEMS.getEntries()) {
                        var item = entry.get();
                        output.accept(item.getDefaultInstance());
                    }
                })
                .title(Component.translatable(FLOO_POWDER_ITEM.get().getDescriptionId()))
                .build()
        );
    }

    private static RegistryObject<ExpansionReel> registerExpansionReel(String typeID, ExpansionReel.Type type) {
        return ITEMS.register(EXP_REEL_ID_PREFIX + typeID, () -> new ExpansionReel(getBaseProperties().stacksTo(1), type));
    }

    private static Item.Properties getBaseProperties() {
        return new Item.Properties();
    }

    public CCMain() {
        var modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ITEMS.register(modEventBus);
        BLOCKS.register(modEventBus);
        BLOCK_ENTITY_TYPES.register(modEventBus);
        PARTICLE_TYPES.register(modEventBus);
        MENU_TYPES.register(modEventBus);
        ENTITY_TYPES.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);

        // 注册模组设置
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CCConfig.COMMON_CONFIG);
    }
}