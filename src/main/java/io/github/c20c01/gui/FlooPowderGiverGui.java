package io.github.c20c01.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import io.github.c20c01.CCMain;
import io.github.c20c01.pos.PosInfo;
import io.github.c20c01.saveData.PointDescWorldSavedData;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.FormattedCharSequence;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;
import java.util.List;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class FlooPowderGiverGui extends Screen {
    private static List<String> nameList;
    public static HashMap<String, String> descMap = new HashMap<>();
    private static PointDescWorldSavedData savedData;
    private static int page = 0;
    private static int set = -1;
    private static final Button[] buttons = new Button[6];
    private static Button okButton;
    private static Button cancelButton;
    private static Button nextButton;
    private static Button backButton;
    private static Button editButton;
    private static EditBox editBox;
    public static boolean op = false;
    private static boolean edit = false;

    public FlooPowderGiverGui(Component component) {
        super(component);
    }

    @Override
    public void onClose() {
        editBox = null;
        edit = false;
        op = false;
        super.onClose();
    }

    @Override
    protected void init() {
        makeSelectButtons();
        makeCancelButton();
        if (op) makeEditButton();
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
        blit(poseStack, this.width / 2 - 150, 20, 0, 0, 300, 216, 300, 216);
        String[] name = getPage(page);
        for (int i = 0; i < 6; i++) {
            if (name[i] != null) {
                this.font.draw(poseStack, FormattedCharSequence.forward(name[i], Style.EMPTY), w, 32 + i * 30, 0);
                String desc = descMap.get(name[i]);
                if (desc != null)
                    this.font.draw(poseStack, FormattedCharSequence.forward(desc, Style.EMPTY), w, 44 + i * 30, 0);
            }
        }
        super.render(poseStack, p_96563_, p_96564_, p_96565_);
    }

    private void makeEditButton() {
        editButton = new Button(this.width / 2 + 126, 212, 20, 20, new TextComponent("#"), (b) -> editTool(true));
        this.addRenderableWidget(editButton);
    }

    private void editTool(boolean save) {
        String name = getPage(page)[set];
        String desc = descMap.get(name);
        if (edit) {
            edit = false;
            editBox.visible = false;
            cancelButton.active = true;
            if (save) {
                descMap.put(name, editBox.getValue());
                savedData.changed();
            }
            updateOkButton();
            updateBackButton();
            updateNextButton();
        } else {
            edit = true;
            if (editBox == null) {
                editBox = new EditBox(this.font, this.width / 2 - 144, 212, 264, 20, new TextComponent("Edit"));
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
        cancelButton = new Button(this.width / 2 - 81, 212, 58, 20, new TextComponent("取消"), (b) -> close());
        this.addRenderableWidget(cancelButton);
    }

    public static void reset() {
        page = 0;
        set = -1;
        op = false;
    }

    public void close() {
        reset();
        this.onClose();
    }

    private void makeOkButton() {
        okButton = new Button(this.width / 2 - 144, 212, 58, 20, new TextComponent("确定"), (b) -> {
            System.out.println(nameList.get(page * 6 + set));
            close();
        });
        this.addRenderableWidget(okButton);
        updateOkButton();
    }

    private static void updateOkButton() {
        boolean b = (set != -1) && (nextButton.active || (nameList != null && set < nameList.size() % 6));
        okButton.active = b;
        if (op) editButton.active = b;
    }

    private void makeNextButton() {
        nextButton = new Button(this.width / 2 + 63, 212, 58, 20, new TextComponent("下一页"), (b) -> {
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
        backButton = new Button(this.width / 2, 212, 58, 20, new TextComponent("上一页"), (b) -> {
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
        var t = new TextComponent("");
        buttons[0] = new Button(w, 30, 20, 20, t, (button) -> selectButton(0));
        buttons[1] = new Button(w, 60, 20, 20, t, (button) -> selectButton(1));
        buttons[2] = new Button(w, 90, 20, 20, t, (button) -> selectButton(2));
        buttons[3] = new Button(w, 120, 20, 20, t, (button) -> selectButton(3));
        buttons[4] = new Button(w, 150, 20, 20, t, (button) -> selectButton(4));
        buttons[5] = new Button(w, 180, 20, 20, t, (button) -> selectButton(5));
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
        if (edit) editTool(false);
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

    public static void setMap(HashMap<String, PosInfo> map) {
        nameList = map.keySet().stream().toList();
        for (String key : nameList) {
            descMap.putIfAbsent(key, key.substring(1, key.length() - 1));
        }
        updateNextButton();
        updateBackButton();
    }

    public static void loadDesc(ServerLevel level) {
        savedData = PointDescWorldSavedData.get(level.getServer());
    }
}
