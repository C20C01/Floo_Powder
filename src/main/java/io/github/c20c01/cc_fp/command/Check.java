package io.github.c20c01.cc_fp.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.c20c01.cc_fp.CCMain;
import io.github.c20c01.cc_fp.savedData.PortalPoint;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.*;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.common.UsernameCache;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@SuppressWarnings("SameReturnValue")
public abstract class Check<T> {
    static final int numPerPage = 8;

    abstract List<T> getData(CommandContext<CommandSourceStack> context) throws CommandSyntaxException;

    protected int check(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        checkingAtPage(context, 1);
        return Command.SINGLE_SUCCESS;
    }

    protected int checkWithPage(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        int page = context.getArgument("page", Integer.class);
        checkingAtPage(context, page);
        return Command.SINGLE_SUCCESS;
    }

    private void checkingAtPage(CommandContext<CommandSourceStack> context, int page) throws CommandSyntaxException {
        List<T> data = getData(context);
        var src = context.getSource();
        int size = data.size();
        if (size == 0) {
            src.sendSuccess(new TextComponent("没东西"), Boolean.FALSE);
            return;
        }
        int maxPage = size / numPerPage + (size % numPerPage == 0 ? 0 : 1);
        page = Math.min(page, maxPage);
        List<T> dataInPage = new ArrayList<>();
        for (int i = 0; i < numPerPage; i++) {
            int index = (page - 1) * numPerPage + i;
            if (index >= size) break;
            dataInPage.add(data.get(index));
        }
        data.clear();
        src.sendSuccess(getComponent(page, maxPage, dataInPage), Boolean.FALSE);
    }

    public static TextComponent getPlayerNameByUuid(UUID uuid) {
        String ownerName = UsernameCache.getLastKnownUsername(uuid);
        return ownerName == null ? new TextComponent("不存在的玩家") : new TextComponent(ownerName);
    }

    abstract MutableComponent getComponent(int page, int maxPage, List<T> data);

    protected static MutableComponent getPageInfo(int page, int maxPage, String command) {
        MutableComponent component = TextComponent.EMPTY.copy();
        component.append(new TextComponent("<<< ").setStyle(
                page > 1 ?
                        Style.EMPTY
                                .withColor(ChatFormatting.DARK_GREEN)
                                .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + CCMain.ID + command + (page - 1))) :
                        Style.EMPTY
                                .withColor(ChatFormatting.DARK_GRAY)));
        component.append(new TextComponent(page + " / " + maxPage).withStyle(ChatFormatting.GOLD));
        component.append(new TextComponent(" >>>").setStyle(
                page < maxPage ?
                        Style.EMPTY
                                .withColor(ChatFormatting.DARK_GREEN)
                                .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + CCMain.ID + command + (page + 1))) :
                        Style.EMPTY
                                .withColor(ChatFormatting.DARK_GRAY)));
        return new TextComponent("\n").append(CCCommand.toAlign(component));

    }

    protected static MutableComponent getFriendsComponent(int page, int maxPage, List<UUID> friends) {
        MutableComponent component = TextComponent.EMPTY.copy();
        MutableComponent tableName = new TextComponent("Friends").withStyle(ChatFormatting.GOLD);
        component.append(CCCommand.toAlign(tableName));
        for (UUID uuid : friends) {
            if (uuid == null) break;
            component.append(new TextComponent("\n• ").withStyle(ChatFormatting.GOLD));
            String name = UsernameCache.getLastKnownUsername(uuid);
            if (name == null) {
                component.append(new TextComponent("不存在的玩家").setStyle(
                                Style.EMPTY
                                        .withColor(ChatFormatting.DARK_GRAY)
                                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ENTITY, new HoverEvent.EntityTooltipInfo(EntityType.PLAYER, uuid, new TextComponent("? ? ?"))))
                        )
                );
            } else {
                component.append(new TextComponent(name).setStyle(
                                Style.EMPTY
                                        .withColor(ChatFormatting.WHITE)
                                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ENTITY, new HoverEvent.EntityTooltipInfo(EntityType.PLAYER, uuid, new TextComponent(name))))
                        )
                );
            }
            component.append(new TextComponent(" [").withStyle(ChatFormatting.YELLOW));
            component.append(new TextComponent("×").setStyle(
                            Style.EMPTY
                                    .withColor(ChatFormatting.DARK_RED)
                                    .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent("删除好友")))
                                    .withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/" + CCMain.ID + " friends remove " + uuid))
                    )
            );
            component.append(new TextComponent("]").withStyle(ChatFormatting.YELLOW));
        }

        return component.append(getPageInfo(page, maxPage, " friends check "));
    }

    protected static MutableComponent getPointsComponent(int page, int maxPage, List<PortalPoint> points, String type) {
        MutableComponent component = TextComponent.EMPTY.copy();
        MutableComponent tableName = new TextComponent("Points(" + type + ")").withStyle(ChatFormatting.GOLD);
        component.append(CCCommand.toAlign(tableName));
        for (PortalPoint point : points) {
            if (point == null) break;
            component.append("\n").append(point.getComponent());
        }

        return component.append(getPageInfo(page, maxPage, " points " + type + " "));
    }
}
