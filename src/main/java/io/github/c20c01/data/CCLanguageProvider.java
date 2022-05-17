package io.github.c20c01.data;

import io.github.c20c01.CCMain;
import io.github.c20c01.item.CCItems;
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
        this.add(CCMain.PORTAL_POINT_BLOCK.get(), switch (this.locale) {
            default -> throw new IllegalStateException();
            case EN_US -> "Teleportation core";
            case ZH_CN -> "传送核心";
        });
        this.add(CCItems.FlooPowder, switch (this.locale) {
            default -> throw new IllegalStateException();
            case EN_US -> "Floo powder";
            case ZH_CN -> "飞路粉";
        });
        this.add(CCItems.PortalFlintAndSteel, switch (this.locale) {
            default -> throw new IllegalStateException();
            case EN_US -> "Teleport Flint and Steel";
            case ZH_CN -> "传送火石";
        });
        this.add(CCItems.PortalBook, switch (this.locale) {
            default -> throw new IllegalStateException();
            case EN_US -> "Teleportation Point Block";
            case ZH_CN -> "传送点大全";
        });
        this.add(CCMain.TEXT_NEEDS_ACTIVATION, switch (this.locale) {
            default -> throw new IllegalStateException();
            case EN_US -> "Use Teleport Flint and Steel to light up the core";
            case ZH_CN -> "使用传送火石以点亮核心";
        });
        this.add(CCMain.TEXT_NOT_LOADED, switch (this.locale) {
            default -> throw new IllegalStateException();
            case EN_US -> "The teleportation point has not been loaded yet";
            case ZH_CN -> "传送点尚未加载完毕";
        });
        this.add(CCMain.TEXT_NOT_FOUND, switch (this.locale) {
            default -> throw new IllegalStateException();
            case EN_US -> "No corresponding Teleportation core found";
            case ZH_CN -> "未找到对应的传送核心";
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
            case EN_US -> "Teleportation core！";
            case ZH_CN -> "传送核心！";
        });
    }
}
