package io.github.c20c01.item;

import io.github.c20c01.CCMain;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)

public class CCItems {
    public static Item FlooPowder;
    public static Item PortalFlintAndSteel;
    public static Item PortalPointBlock;
    public static Item PortalBook;

    @SubscribeEvent
    public static void onRegisterItem(RegistryEvent.Register<Item> event) {
        event.getRegistry().register(new FlooPowder(new Item.Properties().tab(CreativeModeTab.TAB_TRANSPORTATION)).setRegistryName(CCMain.FLOO_POWDER_ID));
        event.getRegistry().register(new PortalFlintAndSteel((new Item.Properties()).durability(1).tab(CreativeModeTab.TAB_TRANSPORTATION)).setRegistryName(CCMain.PORTAL_FLINT_AND_STEEL_ID));
        event.getRegistry().register(portalPointBlock(new BlockItem(CCMain.PORTAL_POINT_BLOCK.get(), new Item.Properties().tab(CreativeModeTab.TAB_TRANSPORTATION))).setRegistryName(CCMain.PORTAL_POINT_BLOCK_ITEM_ID));
        event.getRegistry().register(new PortalBook(new Item.Properties().tab(CreativeModeTab.TAB_TRANSPORTATION).stacksTo(1)).setRegistryName(CCMain.PORTAL_BOOK_ID));
    }

    private static Item portalPointBlock(Item item) {
        PortalPointBlock = item;
        return item;
    }
}
