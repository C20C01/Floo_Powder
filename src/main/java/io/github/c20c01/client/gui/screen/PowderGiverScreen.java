package io.github.c20c01.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import io.github.c20c01.CCMain;
import io.github.c20c01.savedData.shareData.PortalPointInfo;
import io.github.c20c01.savedData.shareData.SharePointInfos;
import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault

public class PowderGiverScreen extends Screen {
    private static final ResourceLocation FLOO_POWDER_GIVER_GUI_BACKGROUND = new ResourceLocation(CCMain.ID, "textures/gui/floo_powder_giver_gui_background.png");
    private static int page = 0;
    private static int select = -1;
    private static final Button[] selectButtons = new Button[6];
    private static Button okButton;
    private static Button nextButton;
    private static Button backButton;
    private static List<PortalPointInfo> infos;
    private final int inventoryKeyID;

    public PowderGiverScreen(Component component, int inventoryKeyID) {
        super(component);
        this.inventoryKeyID = inventoryKeyID;
    }

    @Override
    protected void init() {
        makeSelectButtons();
        makeCancelButton();
        makeOkButton();
        makeNextButton();
        makeBackButton();
    }

    @Override
    public void render(PoseStack poseStack, int p_96563_, int p_96564_, float p_96565_) {
        float w = (float) (this.width / 2.0 - 140);
        this.renderBackground(poseStack);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, FLOO_POWDER_GIVER_GUI_BACKGROUND);
        blit(poseStack, this.width / 2 - 150, 5, 0, 0, 300, 216, 300, 216);
        PortalPointInfo[] infoList = getPage(page);
        for (int i = 0; i < 6; i++) {
            PortalPointInfo info = infoList[i];
            if (info != null) {
                this.font.draw(poseStack, FormattedCharSequence.forward(formatText(info.name()), Style.EMPTY.withColor(ChatFormatting.BLACK)), w, 17 + i * 30, 0);
                this.font.draw(poseStack, FormattedCharSequence.forward(formatText(info.describe()), Style.EMPTY.withColor(ChatFormatting.DARK_GRAY)), w, 29 + i * 30, 0);
            }
        }
        super.render(poseStack, p_96563_, p_96564_, p_96565_);
    }

    @Override
    public boolean keyPressed(int key, int p_96553_, int p_96554_) {
        if (key == inventoryKeyID) {
            close();
            return true;
        }
        return super.keyPressed(key, p_96553_, p_96554_);
    }

    private String formatText(String s) {
        return s.length() > 26 ? s.substring(0, 26) + "..." : s;
    }

    private void makeCancelButton() {
        // “取消”按钮
        Button cancelButton = new Button(this.width / 2 + 63, 197, 58, 20, new TranslatableComponent(CCMain.TEXT_CANCEL), (b) -> close());
        this.addRenderableWidget(cancelButton);
    }

    public static void reset() {
        if (select != -1) selectButtons[select].active = true;
        select = -1;
        page = 0;
        updateBackButton();
        updateNextButton();
        updateOkButton();
    }

    public void close() {
        reset();
        this.onClose();
    }

    private void makeOkButton() {
        // “领取”按钮
        okButton = new Button(this.width / 2, 197, 58, 20, new TranslatableComponent(CCMain.TEXT_DONE), (b) -> {
            assert Minecraft.getInstance().player != null;
            SharePointInfos.sendPointInfoToS(Minecraft.getInstance().player.getUUID(), infos.get(page * 6 + select));
            close();
        });
        this.addRenderableWidget(okButton);
        updateOkButton();
    }

    private static void updateOkButton() {
        okButton.active = (select != -1) && (nextButton.active || ((infos != null && infos.size() > 0) && select < (infos.size() % 6 == 0 ? 6 : infos.size() % 6)));
    }

    private void makeNextButton() {
        // “下一页”按钮
        nextButton = new Button(this.width / 2 - 81, 197, 58, 20, new TranslatableComponent(CCMain.TEXT_NEXT_PAGE), (b) -> {
            page++;
            if (select != -1) selectButtons[select].active = true;
            select = -1;
            updateNextButton();
            updateBackButton();
            updateOkButton();
        });
        this.addRenderableWidget(nextButton);
        updateNextButton();
    }

    private static void updateNextButton() {
        nextButton.active = infos != null && page < (infos.size() - 1) / 6;
    }

    private void makeBackButton() {
        // “上一页”按钮
        backButton = new Button(this.width / 2 - 144, 197, 58, 20, new TranslatableComponent(CCMain.TEXT_PREVIOUS_PAGE), (b) -> {
            page--;
            if (select != -1) selectButtons[select].active = true;
            select = -1;
            updateNextButton();
            updateBackButton();
            updateOkButton();
        });
        this.addRenderableWidget(backButton);
        updateBackButton();
    }

    private static void updateBackButton() {
        backButton.active = (page > 0);
    }

    private void newSelectButtons() {
        int w = this.width / 2 + 126;
        var t = TextComponent.EMPTY;
        selectButtons[0] = new Button(w, 15, 20, 20, t, (button) -> selectButton(0));
        selectButtons[1] = new Button(w, 45, 20, 20, t, (button) -> selectButton(1));
        selectButtons[2] = new Button(w, 75, 20, 20, t, (button) -> selectButton(2));
        selectButtons[3] = new Button(w, 105, 20, 20, t, (button) -> selectButton(3));
        selectButtons[4] = new Button(w, 135, 20, 20, t, (button) -> selectButton(4));
        selectButtons[5] = new Button(w, 165, 20, 20, t, (button) -> selectButton(5));
    }

    private void makeSelectButtons() {
        newSelectButtons();
        for (int i = 0; i < 6; i++) {
            this.addRenderableWidget(selectButtons[i]);
            if (select != -1) selectButtons[select].active = false;
        }
    }

    private void selectButton(int i) {
        if (select != -1) selectButtons[select].active = true;
        selectButtons[i].active = false;
        select = i;
        updateOkButton();
    }

    private static PortalPointInfo[] getPage(int page) {
        PortalPointInfo[] infoList = new PortalPointInfo[6];
        int size = infos == null ? 0 : infos.size();
        for (int i = 0; i < 6; i++) {
            int j = page * 6 + i;
            if (j >= size) break;
            assert infos != null;
            infoList[i] = infos.get(j);
        }
        return infoList;
    }

    public static void setUp(List<PortalPointInfo> infos) {
        reset();
        PowderGiverScreen.infos = infos;
        updateNextButton();
    }
}
