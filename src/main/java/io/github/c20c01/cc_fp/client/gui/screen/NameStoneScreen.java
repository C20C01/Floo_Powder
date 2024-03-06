package io.github.c20c01.cc_fp.client.gui.screen;

import io.github.c20c01.cc_fp.CCMain;
import io.github.c20c01.cc_fp.item.NameStone;
import io.github.c20c01.cc_fp.network.CCNetwork;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.font.TextFieldHelper;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault

@OnlyIn(Dist.CLIENT)
public class NameStoneScreen extends Screen {
    private static final ResourceLocation GUI_BACKGROUND = new ResourceLocation(CCMain.ID, "textures/gui/naming_reel_gui_background.png");
    private static final Minecraft MINECRAFT = Minecraft.getInstance();
    private final ItemStack itemStack;
    private final Player player;
    private final InteractionHand hand;
    private final TextFieldHelper editor;
    private final String lastName;
    private String name;

    public NameStoneScreen(ItemStack itemStack, Player player, InteractionHand hand) {
        super(Component.empty());
        this.itemStack = itemStack;
        this.player = player;
        this.hand = hand;
        lastName = NameStone.getStoneName(itemStack);
        name = lastName;
        editor = new TextFieldHelper(() -> name, (s) -> name = s, TextFieldHelper.createClipboardGetter(MINECRAFT), TextFieldHelper.createClipboardSetter(MINECRAFT),
                (s) -> s.length() < AnvilMenu.MAX_NAME_LENGTH && this.font.width(s) < 180);
    }

    @Override
    protected void init() {
        super.init();
        this.addRenderableWidget(new Button.Builder(Component.translatable(CCMain.TEXT_RENAME), (b) -> tryToChangeName()).pos(this.width / 2 - 100, this.height / 2 + 64).size(80, 20).build());
        this.addRenderableWidget(new Button.Builder(Component.translatable(CCMain.TEXT_CANCEL), (b) -> onClose()).pos(this.width / 2 + 20, this.height / 2 + 64).size(80, 20).build());
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(guiGraphics);
        int w = this.width / 2;
        int h = this.height / 2;
        guiGraphics.blit(GUI_BACKGROUND, (w - 112), (h - 38), 0, 0, 224, 76);
        int x = w - this.font.width(name) / 2;
        int y = h - this.font.lineHeight / 2;
        guiGraphics.drawString(this.font, name, x, y, 0, Boolean.FALSE);
        drawLineAndSelectBox(guiGraphics, x, y);
        drawInfo(guiGraphics, w, h + 16);
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
    }

    private void drawLineAndSelectBox(GuiGraphics guiGraphics, int x, int y) {
        int i = editor.getCursorPos();
        int k = this.font.width(name.substring(0, i));
        guiGraphics.drawString(this.font, "|", x + k, y, 43520, Boolean.FALSE);
        if (i != editor.getSelectionPos()) {
            guiGraphics.fill(RenderType.guiTextHighlight(), x, y - 1, x + k, y + 9, -16776961);
        }
    }

    private void drawInfo(GuiGraphics guiGraphics, int x, int y) {
        if (nameChanged()) {
            MutableComponent component = name.isEmpty() ? Component.translatable(CCMain.TEXT_RENAME_NEED_NO_EMPTY) : Component.translatable(CCMain.TEXT_RENAME_COST);
            int halfLen = this.font.width(component) / 2;
            guiGraphics.drawString(this.font, component, x - halfLen, y, 43520, Boolean.FALSE);
        }
    }

    private boolean nameChanged() {
        return !lastName.equals(name);
    }

    private void tryToChangeName() {
        if (nameChanged()) {
            if (!name.isEmpty() && (player.experienceLevel > 0 || player.getAbilities().instabuild)) {
                int slot = this.hand == InteractionHand.MAIN_HAND ? this.player.getInventory().selected : 40;
                var packet = new CCNetwork.ItemStackPacket(slot, NameStone.setStoneName(itemStack, name));
                CCNetwork.CHANNEL_ITEM_STACK_TO_S.sendToServer(packet);
            } else {
                player.playSound(SoundEvents.VILLAGER_NO);
            }
        }
        this.onClose();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 265) {
            this.editor.setCursorToEnd();
            return true;
        } else if (keyCode != 264 && keyCode != 257 && keyCode != 335) {
            return this.editor.keyPressed(keyCode) || super.keyPressed(keyCode, scanCode, modifiers);
        } else {
            this.editor.setCursorToEnd();
            return true;
        }
    }

    @Override
    public boolean charTyped(char character, int modifiers) {
        this.editor.charTyped(character);
        return true;
    }
}
