package io.github.c20c01.cc_fp.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import io.github.c20c01.cc_fp.CCMain;
import io.github.c20c01.cc_fp.item.NameStone;
import io.github.c20c01.cc_fp.network.CCNetwork;
import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.font.TextFieldHelper;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.item.ItemStack;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault

public class NameStoneScreen extends Screen {
    private static final ResourceLocation GUI_BACKGROUND = new ResourceLocation(CCMain.ID, "textures/gui/name_stone_gui_background.png");
    private static final Minecraft MINECRAFT = Minecraft.getInstance();
    private final ItemStack itemStack;
    private final Player player;
    private final InteractionHand hand;
    private final TextFieldHelper editor;
    private final String lastName;
    private String name;

    public NameStoneScreen(ItemStack itemStack, Player player, InteractionHand hand) {
        super(TextComponent.EMPTY);
        this.itemStack = itemStack;
        this.player = player;
        this.hand = hand;
        lastName = NameStone.getStoneName(itemStack);
        name = lastName;
        editor = new TextFieldHelper(this::getText, this::setText, this::getClipboard, this::setClipboard,
                (s) -> s.length() < AnvilMenu.MAX_NAME_LENGTH && this.font.width(s) < 180);
    }

    @Override
    protected void init() {
        MINECRAFT.keyboardHandler.setSendRepeatsToGui(true);
        this.addRenderableWidget(new Button(this.width / 2 - 100, this.height / 2 + 64, 80, 20, new TranslatableComponent(CCMain.TEXT_RENAME), (b) -> tryToChangeName()));
        this.addRenderableWidget(new Button(this.width / 2 + 20, this.height / 2 + 64, 80, 20, new TranslatableComponent(CCMain.TEXT_CANCEL), (b) -> this.onClose()));
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(poseStack);
        this.setFocused(null);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, GUI_BACKGROUND);
        int w = this.width / 2;
        int h = this.height / 2;
        this.blit(poseStack, (w - 96), (h - 32), 0, 0, 192, 64);
        float x = w - this.font.width(name) / 2.0F;
        float y = h - this.font.lineHeight / 2.0F;
        this.font.draw(poseStack, new TextComponent(name).withStyle(ChatFormatting.WHITE), x, y, 0);
        drawLine(poseStack, x, y);
        drawInfo(poseStack, w, h + 16);
        super.render(poseStack, mouseX, mouseY, partialTicks);
    }

    private void drawLine(PoseStack poseStack, float x, float y) {
        int i = editor.getCursorPos();
        this.font.draw(poseStack, new TextComponent("|").withStyle(ChatFormatting.GREEN), x + this.font.width(name.substring(0, i)) - 1, y, 0);
    }

    private void drawInfo(PoseStack poseStack, float x, float y) {
        if (nameChanged()) {
            TranslatableComponent component = name.isEmpty() ? new TranslatableComponent(CCMain.TEXT_RENAME_NEED_NO_EMPTY) : new TranslatableComponent(CCMain.TEXT_RENAME_COST);
            float halfLen = this.font.width(component) / 2.0F;
            this.font.drawShadow(poseStack, component.withStyle(ChatFormatting.YELLOW), x - halfLen, y, 0);
        }
    }

    private boolean nameChanged() {
        return !lastName.equals(name);
    }

    private void tryToChangeName() {
        if (nameChanged()) {
            if (!name.isEmpty() && (player.experienceLevel > 0 || player.getAbilities().instabuild)) {
                int slot = this.hand == InteractionHand.MAIN_HAND ? this.player.getInventory().selected : 40;
                var packet = new CCNetwork.ItemStackPacket(player.getUUID(), slot, NameStone.setStoneName(itemStack, name));
                CCNetwork.CHANNEL_ITEM_STACK_TO_S.sendToServer(packet);
            } else {
                player.playSound(SoundEvents.VILLAGER_NO, 1, 1);
            }
        }
        this.onClose();
    }

    @Override
    public void removed() {
        MINECRAFT.keyboardHandler.setSendRepeatsToGui(false);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        if (Screen.isSelectAll(keyCode)) {
            this.editor.selectAll();
            return true;
        }
        if (Screen.isCopy(keyCode)) {
            this.editor.copy();
            return true;
        }
        if (Screen.isPaste(keyCode)) {
            this.editor.paste();
            return true;
        }
        if (Screen.isCut(keyCode)) {
            this.editor.cut();
            return true;
        }
        switch (keyCode) {
            case 259 -> {
                this.editor.removeCharsFromCursor(-1);
                return true;
            }
            case 261 -> {
                this.editor.removeCharsFromCursor(1);
                return true;
            }
            case 262 -> {
                this.editor.moveByChars(1, Screen.hasShiftDown());
                return true;
            }
            case 263 -> {
                this.editor.moveByChars(-1, Screen.hasShiftDown());
                return true;
            }
            default -> {
                return false;
            }
        }
    }

    @Override
    public boolean charTyped(char character, int modifiers) {
        if (super.charTyped(character, modifiers)) {
            return true;
        }
        if (SharedConstants.isAllowedChatCharacter(character)) {
            this.editor.insertText(Character.toString(character));
            return true;
        }
        return false;
    }

    private String getText() {
        return name;
    }

    private void setText(String s) {
        this.name = s;
    }

    private void setClipboard(String p_98148_) {
        TextFieldHelper.setClipboardContents(MINECRAFT, p_98148_);
    }

    private String getClipboard() {
        return TextFieldHelper.getClipboardContents(MINECRAFT);
    }
}
