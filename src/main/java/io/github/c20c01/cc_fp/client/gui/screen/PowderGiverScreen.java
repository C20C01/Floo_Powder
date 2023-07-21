package io.github.c20c01.cc_fp.client.gui.screen;

import io.github.c20c01.cc_fp.CCMain;
import io.github.c20c01.cc_fp.savedData.shareData.PortalPointInfo;
import io.github.c20c01.cc_fp.savedData.shareData.SharePointInfos;
import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault

public class PowderGiverScreen extends Screen {
    private static final ResourceLocation GUI_BACKGROUND = new ResourceLocation(CCMain.ID, "textures/gui/floo_powder_giver_gui_background.png");
    private static int page = 0;
    private static int select = -1;
    private static final Button[] selectButtons = new Button[6];
    private static Button okButton;
    private static Button nextButton;
    private static Button backButton;
    private static List<PortalPointInfo> infos;
    private final int inventoryKeyID;

    public PowderGiverScreen(int inventoryKeyID) {
        super(Component.empty());
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
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        int w = this.width / 2 - 140;
        this.renderBackground(guiGraphics);
        guiGraphics.blit(GUI_BACKGROUND, this.width / 2 - 150, 5, 0, 0, 300, 216, 300, 216);
        PortalPointInfo[] infoList = getPage(page);
        for (int i = 0; i < 6; i++) {
            PortalPointInfo info = infoList[i];
            if (info != null) {
                guiGraphics.drawString(this.font, FormattedCharSequence.forward(formatText(info.name()), Style.EMPTY.withColor(ChatFormatting.BLACK)), w, 17 + i * 30, -1, Boolean.FALSE);
                guiGraphics.drawString(this.font, FormattedCharSequence.forward(formatText(info.describe()), Style.EMPTY.withColor(ChatFormatting.DARK_GRAY)), w, 29 + i * 30, -1, Boolean.FALSE);
            }
        }
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == inventoryKeyID) {
            close();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private String formatText(String s) {
        int i = this.font.getSplitter().plainIndexAtWidth(s, 240, Style.EMPTY);
        return s.length() == i ? s : s.substring(0, i) + "...";
    }

    private void makeCancelButton() {
        // “取消”按钮
        Button cancelButton = new Button.Builder(Component.translatable(CCMain.TEXT_CANCEL), (b) -> close()).pos(this.width / 2 + 63, 197).size(58, 20).build();
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
        okButton = new Button.Builder(Component.translatable(CCMain.TEXT_GET), (b) -> {
            assert Minecraft.getInstance().player != null;
            SharePointInfos.sendPointInfoToS(infos.get(page * 6 + select));
            close();
        }).pos(this.width / 2, 197).size(58, 20).build();
        this.addRenderableWidget(okButton);
        updateOkButton();
    }

    private static void updateOkButton() {
        okButton.active = (select != -1) &&
                (nextButton.active || ((infos != null && infos.size() > 0) && select <
                        (infos.size() % 6 == 0 ? 6 : infos.size() % 6)));
    }

    private void makeNextButton() {
        // “下一页”按钮
        nextButton = new Button.Builder(Component.translatable(CCMain.TEXT_NEXT_PAGE), (b) -> {
            page++;
            if (select != -1) selectButtons[select].active = true;
            select = -1;
            updateNextButton();
            updateBackButton();
            updateOkButton();
        }).pos(this.width / 2 - 81, 197).size(58, 20).build();
        this.addRenderableWidget(nextButton);
        updateNextButton();
    }

    private static void updateNextButton() {
        nextButton.active = infos != null && page < (infos.size() - 1) / 6;
    }

    private void makeBackButton() {
        // “上一页”按钮
        backButton = new Button.Builder(Component.translatable(CCMain.TEXT_PREVIOUS_PAGE), (b) -> {
            page--;
            if (select != -1) selectButtons[select].active = true;
            select = -1;
            updateNextButton();
            updateBackButton();
            updateOkButton();
        }).pos(this.width / 2 - 144, 197).size(58, 20).build();
        this.addRenderableWidget(backButton);
        updateBackButton();
    }

    private static void updateBackButton() {
        backButton.active = (page > 0);
    }

    private void newSelectButtons() {
        int x = this.width / 2 + 126;
        for (int i = 0; i < 6; i++) {
            int finalI = i;
            selectButtons[finalI] = new Button.Builder(Component.empty(), (button) -> selectButton(finalI))
                    .pos(x, 15 + 30 * i).size(20, 20).build();
        }
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
