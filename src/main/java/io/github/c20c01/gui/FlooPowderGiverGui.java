package io.github.c20c01.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import io.github.c20c01.CCMain;
import io.github.c20c01.block.FlooPowderGiverBlock;
import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.FormattedCharSequence;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;
import java.util.List;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault

@OnlyIn(Dist.CLIENT)
public class FlooPowderGiverGui extends Screen {
    private static HashMap<String, String> descMap;
    private static List<String> nameList;
    private static int page = 0;
    private static int set = -1;
    private static final Button[] buttons = new Button[6];
    private static Button okButton;
    private static Button cancelButton;
    private static Button nextButton;
    private static Button backButton;
    private static Button editButton;
    private static EditBox editBox;
    public static boolean editMode = false;
    private static boolean editing = false;
    public static int playerCode;

    public FlooPowderGiverGui(Component component) {
        super(component);
    }

    @Override
    public void onClose() {
        editBox = null;
        editing = false;
        editMode = false;
        FlooPowderGiverBlock.handle_C("", playerCode);
        super.onClose();
    }

    @Override
    protected void init() {
        makeSelectButtons();
        makeCancelButton();
        makeEditButton(editMode);
        makeOkButton();
        makeNextButton();
        makeBackButton();
    }

    @Override
    public void render(PoseStack poseStack, int p_96563_, int p_96564_, float p_96565_) {
        float w = (float) (this.width / 2.0 - 140);
        this.renderBackground(poseStack);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, CCMain.FLOO_POWDER_GIVER_GUI_BACKGROUND);
        blit(poseStack, this.width / 2 - 150, 5, 0, 0, 300, 216, 300, 216);
        String[] name = getPage(page);
        for (int i = 0; i < 6; i++) {
            if (name[i] != null) {
                this.font.draw(poseStack, FormattedCharSequence.forward(formatText(name[i]), Style.EMPTY), w, 17 + i * 30, 0);
                if (descMap != null)
                    this.font.draw(poseStack, FormattedCharSequence.forward(formatText(descMap.get(name[i])), Style.EMPTY.withColor(ChatFormatting.DARK_GRAY)), w, 29 + i * 30, 0);
            }
        }
        super.render(poseStack, p_96563_, p_96564_, p_96565_);
    }

    private String formatText(String s) {
        return s.length() > 26 ? s.substring(0, 26) + "..." : s;
    }

    private void makeEditButton(boolean visible) {
        editButton = new Button(this.width / 2 + 126, 197, 20, 20, new TextComponent("#"), (b) -> editTool(true));
        editButton.visible = visible;
        this.addRenderableWidget(editButton);
    }

    private void editTool(boolean save) {
        String name = getPage(page)[set];
        String desc = descMap.get(name);
        if (editing) {
            editing = false;
            editBox.visible = false;
            cancelButton.active = true;
            if (save) {
                descMap.put(name, editBox.getValue());
                GuiData.sendToServer(descMap);
            }
            updateOkButton();
            updateBackButton();
            updateNextButton();
        } else {
            editing = true;
            if (editBox == null) {
                editBox = new EditBox(this.font, this.width / 2 - 144, 197, 264, 20, TextComponent.EMPTY);
                this.addRenderableWidget(editBox);
            } else editBox.visible = true;
            editBox.setValue(desc == null ? name : desc);
            cancelButton.active = false;
            okButton.active = false;
            nextButton.active = false;
            backButton.active = false;
        }
    }

    private void makeCancelButton() {
        cancelButton = new Button(this.width / 2 - 81, 197, 58, 20, new TranslatableComponent(CCMain.TEXT_CANCEL), (b) -> close());
        this.addRenderableWidget(cancelButton);
    }

    public static void reset() {
        if (set != -1) buttons[set].active = true;
        set = -1;
        page = 0;
        updateBackButton();
        updateNextButton();
        updateOkButton();
        editMode = false;
    }

    public void close() {
        reset();
        this.onClose();
    }

    private void makeOkButton() {
        okButton = new Button(this.width / 2 - 144, 197, 58, 20, new TranslatableComponent(CCMain.TEXT_DONE), (b) -> {
            String name = nameList.get(page * 6 + set);
            String result = name.substring(1, name.length() - 1);
            System.out.println(result);
            FlooPowderGiverBlock.handle_C(result, playerCode);
            close();
        });
        this.addRenderableWidget(okButton);
        updateOkButton();
    }

    private static void updateOkButton() {
        boolean b = (set != -1) && (nextButton.active || ((nameList != null && nameList.size() > 0) && set < (nameList.size() % 6 == 0 ? 6 : nameList.size() % 6)));
        okButton.active = b;
        if (editMode) editButton.active = b;
    }

    private void makeNextButton() {
        nextButton = new Button(this.width / 2 + 63, 197, 58, 20, new TranslatableComponent(CCMain.TEXT_NEXT_PAGE), (b) -> {
            page++;
            if (set != -1) buttons[set].active = true;
            set = -1;
            updateNextButton();
            updateBackButton();
            updateOkButton();
        });
        this.addRenderableWidget(nextButton);
        updateNextButton();
    }

    private static void updateNextButton() {
        nextButton.active = nameList != null && page < (nameList.size() - 1) / 6;
    }

    private void makeBackButton() {
        backButton = new Button(this.width / 2, 197, 58, 20, new TranslatableComponent(CCMain.TEXT_PREVIOUS_PAGE), (b) -> {
            page--;
            if (set != -1) buttons[set].active = true;
            set = -1;
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
        buttons[0] = new Button(w, 15, 20, 20, t, (button) -> selectButton(0));
        buttons[1] = new Button(w, 45, 20, 20, t, (button) -> selectButton(1));
        buttons[2] = new Button(w, 75, 20, 20, t, (button) -> selectButton(2));
        buttons[3] = new Button(w, 105, 20, 20, t, (button) -> selectButton(3));
        buttons[4] = new Button(w, 135, 20, 20, t, (button) -> selectButton(4));
        buttons[5] = new Button(w, 165, 20, 20, t, (button) -> selectButton(5));
    }

    private void makeSelectButtons() {
        newSelectButtons();
        for (int i = 0; i < 6; i++) {
            this.addRenderableWidget(buttons[i]);
            if (set != -1) buttons[set].active = false;
        }
    }

    private void selectButton(int i) {
        if (set != -1) buttons[set].active = true;
        buttons[i].active = false;
        set = i;
        updateOkButton();
        if (editing) editTool(false);
    }

    private static String[] getPage(int page) {
        String[] name = new String[6];
        int size = nameList == null ? 0 : nameList.size();
        for (int i = 0; i < 6; i++) {
            int j = page * 6 + i;
            if (j >= size) break;
            assert nameList != null;
            name[i] = nameList.get(j);
        }
        return name;
    }

    public static void setUp(HashMap<String, String> map) {
        reset();
        descMap = map;
        nameList = descMap.keySet().stream().toList();
        updateNextButton();
    }
}
