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
            src.sendSuccess(() -> getComponent(0, 0, new ArrayList<>()), Boolean.FALSE);
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
        int finalPage = page;
        src.sendSuccess(() -> getComponent(finalPage, maxPage, dataInPage), Boolean.FALSE);
    }

    public static MutableComponent getPlayerNameByUuid(UUID uuid) {
        String ownerName = UsernameCache.getLastKnownUsername(uuid);
        return ownerName == null ? Component.translatable(CCMain.TEXT_UNKNOWN_PLAYER) : Component.literal(ownerName);
    }

    abstract MutableComponent getComponent(int page, int maxPage, List<T> data);

    protected static MutableComponent getPageInfo(int page, int maxPage, String command) {
        MutableComponent component = Component.empty();
        component.append(Component.literal("<<< ").setStyle(
                page > 1 ?
                        Style.EMPTY
                                .withColor(ChatFormatting.DARK_GREEN)
                                .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + CCMain.ID + command + (page - 1))) :
                        Style.EMPTY
                                .withColor(ChatFormatting.DARK_GRAY)));
        component.append(Component.literal(page + " / " + maxPage).withStyle(ChatFormatting.GOLD));
        component.append(Component.literal(" >>>").setStyle(
                page < maxPage ?
                        Style.EMPTY
                                .withColor(ChatFormatting.DARK_GREEN)
                                .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + CCMain.ID + command + (page + 1))) :
                        Style.EMPTY
                                .withColor(ChatFormatting.DARK_GRAY)));
        return Component.literal("\n").append(CCCommand.toAlign(component));

    }

    protected static MutableComponent getFriendsComponent(int page, int maxPage, List<UUID> friends) {
        MutableComponent component = Component.empty();
        MutableComponent tableName = Component.literal("Friends").withStyle(ChatFormatting.GOLD);
        component.append(CCCommand.toAlign(tableName));
        for (UUID uuid : friends) {
            if (uuid == null) break;
            component.append(Component.literal("\n• ").withStyle(ChatFormatting.GOLD));
            String name = UsernameCache.getLastKnownUsername(uuid);
            if (name == null) {
                component.append(Component.literal(CCMain.TEXT_UNKNOWN_PLAYER).setStyle(
                                Style.EMPTY
                                        .withColor(ChatFormatting.DARK_GRAY)
                                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ENTITY, new HoverEvent.EntityTooltipInfo(EntityType.PLAYER, uuid, Component.literal("? ? ?"))))
                        )
                );
            } else {
                component.append(Component.literal(name).setStyle(
                                Style.EMPTY
                                        .withColor(ChatFormatting.WHITE)
                                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ENTITY, new HoverEvent.EntityTooltipInfo(EntityType.PLAYER, uuid, Component.literal(name))))
                        )
                );
            }
            component.append(Component.literal(" [").withStyle(ChatFormatting.YELLOW));
            component.append(Component.literal("×").setStyle(
                            Style.EMPTY
                                    .withColor(ChatFormatting.DARK_RED)
                                    .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable(CCMain.TEXT_REMOVE_FRIEND)))
                                    .withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/" + CCMain.ID + " friends remove " + uuid))
                    )
            );
            component.append(Component.literal("]").withStyle(ChatFormatting.YELLOW));
        }

        return component.append(getPageInfo(page, maxPage, " friends check "));
    }

    protected static MutableComponent getPointsComponent(int page, int maxPage, List<PortalPoint> points, String type) {
        MutableComponent component = Component.empty();
        MutableComponent tableName = Component.literal("Points(" + type + ")").withStyle(ChatFormatting.GOLD);
        component.append(CCCommand.toAlign(tableName));
        for (PortalPoint point : points) {
            if (point == null) break;
            component.append("\n").append(point.getComponent());
        }

        return component.append(getPageInfo(page, maxPage, " points " + type + " "));
    }
}
