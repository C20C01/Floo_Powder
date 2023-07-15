package io.github.c20c01.cc_fp.data;

import io.github.c20c01.cc_fp.CCMain;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

@Mod.EventBusSubscriber(modid = CCMain.ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CCLanguageProvider extends LanguageProvider {
    private static final String EN_US = "en_us";
    private static final String ZH_CN = "zh_cn";

    private final String locale;

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event) {
        var generator = event.getGenerator();
        generator.addProvider(new CCLanguageProvider(generator, EN_US));
        generator.addProvider(new CCLanguageProvider(generator, ZH_CN));
    }

    private CCLanguageProvider(DataGenerator gen, String locale) {
        super(gen, CCMain.ID, locale);
        this.locale = locale;
    }

    @Override
    protected void addTranslations() {
        this.add(CCMain.TAB_ID, switch (this.locale) {
            default -> throw new IllegalStateException();
            case EN_US -> "Floo powder";
            case ZH_CN -> "飞路粉";
        });
        this.add(CCMain.PORTAL_POINT_BLOCK.get(), switch (this.locale) {
            default -> throw new IllegalStateException();
            case EN_US -> "Teleportation core";
            case ZH_CN -> "传送核心";
        });
        this.add(CCMain.FLOO_POWDER_ITEM.get(), switch (this.locale) {
            default -> throw new IllegalStateException();
            case EN_US -> "Floo powder";
            case ZH_CN -> "飞路粉";
        });
        this.add(CCMain.LASTING_POWDER_ITEM.get(), switch (this.locale) {
            default -> throw new IllegalStateException();
            case EN_US -> "Lasting powder";
            case ZH_CN -> "不灭粉";
        });
        this.add(CCMain.PORTAL_FLINT_AND_STEEL_ITEM.get(), switch (this.locale) {
            default -> throw new IllegalStateException();
            case EN_US -> "Teleport Flint and Steel";
            case ZH_CN -> "传送火石";
        });
        this.add(CCMain.PORTAL_FIRE_BLOCK.get(), switch (this.locale) {
            default -> throw new IllegalStateException();
            case EN_US -> "Teleportation fire";
            case ZH_CN -> "传送火焰";
        });
        this.add(CCMain.FAKE_PORTAL_FIRE_BLOCK.get(), switch (this.locale) {
            default -> throw new IllegalStateException();
            case EN_US -> "Teleportation fire*";
            case ZH_CN -> "传送火焰*";
        });
        this.add(CCMain.PORTAL_BOOK_ITEM.get(), switch (this.locale) {
            default -> throw new IllegalStateException();
            case EN_US -> "Book of Teleportation cores";
            case ZH_CN -> "传送核心大全";
        });
        this.add(CCMain.POWDER_GIVER_BLOCK.get(), switch (this.locale) {
            default -> throw new IllegalStateException();
            case EN_US -> "Powder giver";
            case ZH_CN -> "飞路粉分发器";
        });
        this.add(CCMain.PORTAL_WAND_ITEM.get(), switch (this.locale) {
            default -> throw new IllegalStateException();
            case EN_US -> "Teleportation wand";
            case ZH_CN -> "传送法杖";
        });
        this.add(CCMain.PORTAL_CHEST_BLOCK.get(), switch (this.locale) {
            default -> throw new IllegalStateException();
            case EN_US -> "Teleportation trap chest";
            case ZH_CN -> "传送陷阱箱";
        });
        this.add(CCMain.FIRE_BASE_BLOCK.get(), switch (this.locale) {
            default -> throw new IllegalStateException();
            case EN_US -> "Bounce fire base";
            case ZH_CN -> "弹性火焰底座";
        });
        this.add(CCMain.FLOO_HANDBAG_ITEM.get(), switch (this.locale) {
            default -> throw new IllegalStateException();
            case EN_US -> "Floo handbag";
            case ZH_CN -> "飞路口袋";
        });
        this.add(CCMain.NAME_STONE_ITEM.get(), switch (this.locale) {
            default -> throw new IllegalStateException();
            case EN_US -> "Name stone";
            case ZH_CN -> "命名石";
        });
        this.add(CCMain.POWDER_POT_BLOCK.get(), switch (this.locale) {
            default -> throw new IllegalStateException();
            case EN_US -> "FlooPowder pot";
            case ZH_CN -> "飞路粉陶盆";
        });

        this.add(CCMain.FLOO_REEL_ITEM.get(), switch (this.locale) {
            default -> throw new IllegalStateException();
            case EN_US -> "Floo reel";
            case ZH_CN -> "传送卷轴";
        });
        final String EXP_REEL_US = "Expansion reel——";
        final String EXP_REEL_CN = "拓展卷轴——";
        this.add(CCMain.EXP_REEL_EVERYONE_ITEM.get(), switch (this.locale) {
            default -> throw new IllegalStateException();
            case EN_US -> EXP_REEL_US + "Everyone";
            case ZH_CN -> EXP_REEL_CN + "任何玩家";
        });
        this.add(CCMain.EXP_REEL_FRIENDS_ITEM.get(), switch (this.locale) {
            default -> throw new IllegalStateException();
            case EN_US -> EXP_REEL_US + "Friend";
            case ZH_CN -> EXP_REEL_CN + "仅限好友";
        });
        this.add(CCMain.EXP_REEL_ITEM_ITEM.get(), switch (this.locale) {
            default -> throw new IllegalStateException();
            case EN_US -> EXP_REEL_US + "Item";
            case ZH_CN -> EXP_REEL_CN + "物品";
        });
        this.add(CCMain.EXP_REEL_MINECART_ITEM.get(), switch (this.locale) {
            default -> throw new IllegalStateException();
            case EN_US -> EXP_REEL_US + "Minecart";
            case ZH_CN -> EXP_REEL_CN + "矿车";
        });
        this.add(CCMain.EXP_REEL_FALLING_BLOCK_ITEM.get(), switch (this.locale) {
            default -> throw new IllegalStateException();
            case EN_US -> EXP_REEL_US + "Falling block";
            case ZH_CN -> EXP_REEL_CN + "下落的方块";
        });
        this.add(CCMain.EXP_REEL_TNT_ITEM.get(), switch (this.locale) {
            default -> throw new IllegalStateException();
            case EN_US -> EXP_REEL_US + "TNT";
            case ZH_CN -> EXP_REEL_CN + "点燃的TNT";
        });
        this.add(CCMain.EXP_REEL_PROJECTILE_ITEM.get(), switch (this.locale) {
            default -> throw new IllegalStateException();
            case EN_US -> EXP_REEL_US + "Projectile";
            case ZH_CN -> EXP_REEL_CN + "弹射物";
        });
        this.add(CCMain.EXP_REEL_MONSTER_ITEM.get(), switch (this.locale) {
            default -> throw new IllegalStateException();
            case EN_US -> EXP_REEL_US + "Monster";
            case ZH_CN -> EXP_REEL_CN + "怪物";
        });
        this.add(CCMain.EXP_REEL_AGEABLE_MOB_ITEM.get(), switch (this.locale) {
            default -> throw new IllegalStateException();
            case EN_US -> EXP_REEL_US + "Ageable mob";
            case ZH_CN -> EXP_REEL_CN + "被动生物";
        });
        this.add(CCMain.EXP_REEL_SAVE_DIRECTION_ITEM.get(), switch (this.locale) {
            default -> throw new IllegalStateException();
            case EN_US -> EXP_REEL_US + "Save direction";
            case ZH_CN -> EXP_REEL_CN + "保留朝向";
        });

        this.add(CCMain.TEXT_NOT_FOUND_BOOK, switch (this.locale) {
            default -> throw new IllegalStateException();
            case EN_US -> "No available Teleportation core found";
            case ZH_CN -> "未发现任何可用的传送核心";
        });
        this.add(CCMain.TEXT_SET_PORTAL_FIRE_BOOK, switch (this.locale) {
            default -> throw new IllegalStateException();
            case EN_US -> "Exists after transmission";
            case ZH_CN -> "传送后不熄灭";
        });
        this.add(CCMain.TEXT_SET_PORTAL_POINT_FIRE_BOOK, switch (this.locale) {
            default -> throw new IllegalStateException();
            case EN_US -> "Try to make a fire";
            case ZH_CN -> "核心顶部尝试生火";
        });
        this.add(CCMain.TEXT_NOT_OWNER, switch (this.locale) {
            default -> throw new IllegalStateException();
            case EN_US -> "You are not the owner";
            case ZH_CN -> "你不是它的拥有者";
        });
        this.add(CCMain.TEXT_ALREADY_EXISTS, switch (this.locale) {
            default -> throw new IllegalStateException();
            case EN_US -> "A same named point already exists";
            case ZH_CN -> "已经存在同名的传送点了";
        });
        this.add(CCMain.TEXT_NAME_TOO_LONG, switch (this.locale) {
            default -> throw new IllegalStateException();
            case EN_US -> "The name of this point is too long";
            case ZH_CN -> "传送点名称过长";
        });
        this.add(CCMain.TEXT_RENAME_COST, switch (this.locale) {
            default -> throw new IllegalStateException();
            case EN_US -> "Rename Cost: 1";
            case ZH_CN -> "命名花费: 1";
        });
        this.add(CCMain.TEXT_RENAME_NEED_NO_EMPTY, switch (this.locale) {
            default -> throw new IllegalStateException();
            case EN_US -> "Name can not be empty";
            case ZH_CN -> "命名不能为空";
        });
        this.add(CCMain.TEXT_POINT_SET_PUBLIC, switch (this.locale) {
            default -> throw new IllegalStateException();
            case EN_US -> "Point is now public for everyone";
            case ZH_CN -> "传送点向所有玩家公开";
        });
        this.add(CCMain.TEXT_POINT_SET_NO_PUBLIC, switch (this.locale) {
            default -> throw new IllegalStateException();
            case EN_US -> "Point is no longer public for everyone";
            case ZH_CN -> "传送点不再向所有玩家公开";
        });
        this.add(CCMain.TEXT_POINT_SET_DESC, switch (this.locale) {
            default -> throw new IllegalStateException();
            case EN_US -> "Point's describe has been changed";
            case ZH_CN -> "传送点描述已更改";
        });
        this.add(CCMain.TEXT_POINT_UNACTIVATED, switch (this.locale) {
            default -> throw new IllegalStateException();
            case EN_US -> "Point has not been activated yet";
            case ZH_CN -> "传送点尚未建立";
        });
        this.add(CCMain.TEXT_POT_UNLIMITED, switch (this.locale) {
            default -> throw new IllegalStateException();
            case EN_US -> "Unlimited floo powder";
            case ZH_CN -> "无限飞路粉";
        });
        this.add(CCMain.TEXT_POWDER_GIVER_GROUP, switch (this.locale) {
            default -> throw new IllegalStateException();
            case EN_US -> "Public group";
            case ZH_CN -> "公开组";
        });
        this.add(CCMain.TEXT_POT_NAME, switch (this.locale) {
            default -> throw new IllegalStateException();
            case EN_US -> "Pot name";
            case ZH_CN -> "陶盆名称";
        });

        this.add(CCMain.TEXT_CAN_NOT_ADD_YOURSELF, switch (this.locale) {
            default -> throw new IllegalStateException();
            case EN_US -> "Can not add yourself as friend";
            case ZH_CN -> "无法添加自己为好友";
        });
        this.add(CCMain.TEXT_ALREADY_FRIEND, switch (this.locale) {
            default -> throw new IllegalStateException();
            case EN_US -> "Already friend";
            case ZH_CN -> "已经是好友了";
        });
        this.add(CCMain.TEXT_OUT_OF_FRIEND_SIZE, switch (this.locale) {
            default -> throw new IllegalStateException();
            case EN_US -> "Out of friend size";
            case ZH_CN -> "好友数量已达上限";
        });
        this.add(CCMain.TEXT_ALREADY_SEND, switch (this.locale) {
            default -> throw new IllegalStateException();
            case EN_US -> "Already sent request";
            case ZH_CN -> "已经发送过请求了";
        });
        this.add(CCMain.TEXT_REQUEST_SEND, switch (this.locale) {
            default -> throw new IllegalStateException();
            case EN_US -> "Request has been sent";
            case ZH_CN -> "请求已发送";
        });
        this.add(CCMain.TEXT_INVITE_DESC, switch (this.locale) {
            default -> throw new IllegalStateException();
            case EN_US -> "Invite you to be its friend";
            case ZH_CN -> "邀请你成为其好友";
        });
        this.add(CCMain.TEXT_INVITE_HOVER, switch (this.locale) {
            default -> throw new IllegalStateException();
            case EN_US -> "You will be able to see all points they have and teleport to their friends only points\n" +
                    "(Attention: Only the inviter can remove this friend relationship, you can't remove it)";
            case ZH_CN -> "你将能看到其拥有的所有传送点，并可传送至其对好友开放传送点\n" +
                    "（注意: 只有邀请者才能解除你们的好友关系，您无法解除）";
        });
        this.add(CCMain.TEXT_REQUEST_DESC, switch (this.locale) {
            default -> throw new IllegalStateException();
            case EN_US -> "Request to be your friend";
            case ZH_CN -> "请求成为你的好友";
        });
        this.add(CCMain.TEXT_REQUEST_HOVER, switch (this.locale) {
            default -> throw new IllegalStateException();
            case EN_US ->
                    "All teleportation points you have will be visible to them (including coordinate!)\nAnd they can teleport to your friends only points";
            case ZH_CN -> "你拥有的的所有传送点都将对其可见（包含具体坐标！）\n同时其能传送至你对好友开放的传送点";
        });
        this.add(CCMain.TEXT_UNKNOWN_REQUEST, switch (this.locale) {
            default -> throw new IllegalStateException();
            case EN_US -> "Unknown request";
            case ZH_CN -> "未知请求";
        });
        this.add(CCMain.TEXT_UNKNOWN_PLAYER, switch (this.locale) {
            default -> throw new IllegalStateException();
            case EN_US -> "Unknown player";
            case ZH_CN -> "未知玩家";
        });
        this.add(CCMain.TEXT_ADDED_SUCCESSFULLY, switch (this.locale) {
            default -> throw new IllegalStateException();
            case EN_US -> "Added successfully";
            case ZH_CN -> "添加成功";
        });
        this.add(CCMain.TEXT_CAN_NOT_REMOVE_YOURSELF, switch (this.locale) {
            default -> throw new IllegalStateException();
            case EN_US -> "Can not remove yourself";
            case ZH_CN -> "无法移除自己";
        });
        this.add(CCMain.TEXT_REMOVED_SUCCESSFULLY, switch (this.locale) {
            default -> throw new IllegalStateException();
            case EN_US -> "Removed successfully";
            case ZH_CN -> "移除成功";
        });
        this.add(CCMain.TEXT_NOT_FRIEND, switch (this.locale) {
            default -> throw new IllegalStateException();
            case EN_US -> "Not friend";
            case ZH_CN -> "不是好友";
        });
        this.add(CCMain.TEXT_NOTHING, switch (this.locale) {
            default -> throw new IllegalStateException();
            case EN_US -> "Nothing";
            case ZH_CN -> "空";
        });
        this.add(CCMain.TEXT_REMOVE_FRIEND, switch (this.locale) {
            default -> throw new IllegalStateException();
            case EN_US -> "Remove friend";
            case ZH_CN -> "移除好友";
        });

        this.add(CCMain.TEXT_GET, switch (this.locale) {
            default -> throw new IllegalStateException();
            case EN_US -> "Select";
            case ZH_CN -> "领取所选";
        });
        this.add(CCMain.TEXT_CANCEL, switch (this.locale) {
            default -> throw new IllegalStateException();
            case EN_US -> "Cancel";
            case ZH_CN -> "取消";
        });
        this.add(CCMain.TEXT_PREVIOUS_PAGE, switch (this.locale) {
            default -> throw new IllegalStateException();
            case EN_US -> "Previous";
            case ZH_CN -> "上一页";
        });
        this.add(CCMain.TEXT_NEXT_PAGE, switch (this.locale) {
            default -> throw new IllegalStateException();
            case EN_US -> "Next";
            case ZH_CN -> "下一页";
        });
        this.add(CCMain.TEXT_RENAME, switch (this.locale) {
            default -> throw new IllegalStateException();
            case EN_US -> "Rename";
            case ZH_CN -> "重命名";
        });

        this.add(CCMain.FLOO_BALL_ITEM.get(), switch (this.locale) {
            default -> throw new IllegalStateException();
            case EN_US -> "Floo ball";
            case ZH_CN -> "飞路弹";
        });
        this.add(CCMain.FLOO_BALL_ENTITY.get(), switch (this.locale) {
            default -> throw new IllegalStateException();
            case EN_US -> "Floo ball";
            case ZH_CN -> "飞路弹";
        });


        this.add(CCMain.TEXT_GET_FLOO_DESC, switch (this.locale) {
            default -> throw new IllegalStateException();
            case EN_US -> "Throw it in the fire to get a portal to the Teleportation core of the same name";
            case ZH_CN -> "将其丢入火中就能获得通往相同命名的传送核心的通道";
        });
        this.add(CCMain.TEXT_GET_FLOO_TITLE, switch (this.locale) {
            default -> throw new IllegalStateException();
            case EN_US -> "Floo powder";
            case ZH_CN -> "飞路粉";
        });

        this.add(CCMain.TEXT_INTO_FIRE_DESC, switch (this.locale) {
            default -> throw new IllegalStateException();
            case EN_US -> "Enter the teleport flame and try to teleport";
            case ZH_CN -> "进入传送火焰并尝试传送";
        });
        this.add(CCMain.TEXT_INTO_FIRE_TITLE, switch (this.locale) {
            default -> throw new IllegalStateException();
            case EN_US -> "Going in~";
            case ZH_CN -> "进去了~";
        });

        this.add(CCMain.TEXT_GET_POINT_BLOCK_DESC, switch (this.locale) {
            default -> throw new IllegalStateException();
            case EN_US -> "Use a named Teleport Flint and Steel to activate and name the teleportation core";
            case ZH_CN -> "使用命名的传送火石即可激活并命名";
        });
        this.add(CCMain.TEXT_GET_POINT_BLOCK_TITLE, switch (this.locale) {
            default -> throw new IllegalStateException();
            case EN_US -> "Teleportation core!";
            case ZH_CN -> "传送核心！";
        });
    }
}