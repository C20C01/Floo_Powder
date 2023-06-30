package io.github.c20c01.cc_fp.command;


import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.c20c01.cc_fp.CCMain;
import io.github.c20c01.cc_fp.item.PortalWand;
import io.github.c20c01.cc_fp.savedData.PermissionManager;
import io.github.c20c01.cc_fp.savedData.PortalPoint;
import io.github.c20c01.cc_fp.savedData.PortalPointManager;
import io.github.c20c01.cc_fp.config.CCConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.UuidArgument;
import net.minecraft.network.chat.*;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@SuppressWarnings("SameReturnValue")
public class CCCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal(CCMain.ID).executes(CCCommand::about)
                .then(Commands.literal("about").executes(CCCommand::about)) //关于
                .then(Commands.literal("config") //查询和修改设置
                        .then(CCConfig.getCommand(CCConfig.maxConversion, IntegerArgumentType.integer(0, 512), Integer.class))
                        .then(CCConfig.getCommand(CCConfig.maxFriendSize, IntegerArgumentType.integer(0, 64), Integer.class))
                        .then(CCConfig.getCommand(CCConfig.shootDistance, IntegerArgumentType.integer(32, 256), Integer.class))
                        .then(CCConfig.getCommand(CCConfig.canUseInSurvival, BoolArgumentType.bool(), Boolean.class))
                        .then(CCConfig.getCommand(CCConfig.lastingPowderCanSummonLava, BoolArgumentType.bool(), Boolean.class))
                        .then(CCConfig.getCommand(CCConfig.defaultPoint, StringArgumentType.word(), String.class))
                )
                .then(Commands.literal("friends")
                        .then(Commands.literal("accept")
                                .then(Commands.literal("invite") //接受好友邀请
                                        .then(Commands.argument("uuid", UuidArgument.uuid()).executes(CCCommand::friendInviteAccept))
                                )
                                .then(Commands.literal("request") //接受好友请求
                                        .then(Commands.argument("uuid", UuidArgument.uuid()).executes(CCCommand::friendRequestAccept))
                                )
                        )
                        .then(Commands.literal("invite") //邀请成为好友
                                .then(Commands.argument("player", EntityArgument.player()).executes(CCCommand::friendInvite))
                        )
                        .then(Commands.literal("request") //请求成为好友
                                .then(Commands.argument("player", EntityArgument.player()).executes(CCCommand::friendRequest))
                        )
                        .then(Commands.literal("check").executes(new CheckFriends()::check) //查看自己的好友
                                .then(Commands.argument("page", IntegerArgumentType.integer(1)).executes(new CheckFriends()::checkWithPage))
                        )
                        .then(Commands.literal("remove") //删除自己的好友
                                .then(Commands.argument("uuid", UuidArgument.uuid()).executes(CCCommand::friendRemove))
                        )
                )
                .then(Commands.literal("points")
                        .then(Commands.literal("check").executes(new CheckPoints(PortalPointManager.CheckType.ALL_AVAILABLE)::check) //查询传送点
                                .then(checkPoints(PortalPointManager.CheckType.ALL_AVAILABLE))
                                .then(checkPoints(PortalPointManager.CheckType.MINE))
                                .then(checkPoints(PortalPointManager.CheckType.OTHERS))
                                .then(checkPoints(PortalPointManager.CheckType.PUBLIC))
                                .then(checkPoints(PortalPointManager.CheckType.ALL).requires(cs -> cs.hasPermission(3)))
                                .then(Commands.literal("public_by_group")
                                        .then(Commands.argument("group", StringArgumentType.word())
                                                .executes(new CheckPointsByGroup()::check)
                                                .then(Commands.argument("page", IntegerArgumentType.integer(1))
                                                        .executes(new CheckPointsByGroup()::checkWithPage)
                                                )
                                        )
                                )
                        )
                )
                .then(Commands.literal("wandFire").requires(cs -> cs.hasPermission(2))
                        .then(Commands.literal("remove") //删除指定玩家传送法杖所产生的火焰
                                .then(Commands.argument("player", EntityArgument.players()).executes(CCCommand::removeWandFire)
                                )
                        )
                )
        );
    }

    private static class CheckFriends extends Check<UUID> {
        @Override
        List<UUID> getData(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
            var src = context.getSource();
            return new ArrayList<>(PermissionManager.get(src.getServer()).get(src.getPlayerOrException().getUUID()).friends());
        }

        @Override
        MutableComponent getComponent(int page, int maxPage, List<UUID> data) {
            return getFriendsComponent(page, maxPage, data);
        }
    }

    private static class CheckPoints extends Check<PortalPoint> {
        private final PortalPointManager.CheckType checkType;

        public CheckPoints(PortalPointManager.CheckType checkType) {
            this.checkType = checkType;
        }

        @Override
        List<PortalPoint> getData(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
            var src = context.getSource();
            return PortalPointManager.getPoints(checkType, src.getPlayerOrException());
        }

        @Override
        MutableComponent getComponent(int page, int maxPage, List<PortalPoint> data) {
            return getPointsComponent(page, maxPage, data, checkType.getSerializedName());
        }
    }

    private static class CheckPointsByGroup extends Check<PortalPoint> {
        @Override
        List<PortalPoint> getData(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
            var src = context.getSource();
            return PortalPointManager.getPublicPointsByGroup(src.getPlayerOrException(), context.getArgument("group", String.class));
        }

        @Override
        MutableComponent getComponent(int page, int maxPage, List<PortalPoint> data) {
            return getPointsComponent(page, maxPage, data, "public");
        }
    }

    private static LiteralArgumentBuilder<CommandSourceStack> checkPoints(PortalPointManager.CheckType checkType) {
        return Commands.literal(checkType.getSerializedName())
                .executes(new CheckPoints(checkType)::check)
                .then(Commands.argument("page", IntegerArgumentType.integer(1))
                        .executes(new CheckPoints(checkType)::checkWithPage));
    }

    private static int about(CommandContext<CommandSourceStack> context) {
        String URL = "https://github.com/C20C01/Floo_Powder";
        String Version = "1.5.0_Beta";
        MutableComponent component = new TextComponent("• Floo Powder ").setStyle(Style.EMPTY
                .withColor(ChatFormatting.DARK_GREEN)
                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent(Version))
                )
        );
        component.append(new TextComponent("Github↗\n").setStyle(Style.EMPTY
                        .withColor(ChatFormatting.BLUE)
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent(URL)))
                        .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, URL))
                )
        );
        component.append(new TextComponent("• Made with ❤ by CC2001").withStyle(ChatFormatting.DARK_PURPLE));

        context.getSource().sendSuccess(component, Boolean.FALSE);
        return Command.SINGLE_SUCCESS;
    }

    private static void friendAskToAdd(CommandContext<CommandSourceStack> context, boolean invite) throws CommandSyntaxException {
        CommandSourceStack src = context.getSource();
        Player other = EntityArgument.getPlayer(context, "player");
        Player self = src.getPlayerOrException();

        Player friend = invite ? other : self;
        Player inserted = invite ? self : other;

        switch (PermissionManager.get(src.getServer()).askToAddFriend(friend.getUUID(), inserted.getUUID())) {
            case SELF -> src.sendFailure(new TextComponent("不能添加自己！"));
            case CONTAINED -> src.sendFailure(new TextComponent("已经是好友了！"));
            case OUT_OF_SIZE -> src.sendFailure(new TextComponent("好友数已超过上限！"));
            case ALREADY_SEND -> src.sendFailure(new TextComponent("存在相同的请求！"));
            case SUCCESS -> {
                friendAskingSend(self, other, invite);
                src.sendSuccess(new TextComponent("请求已发送！"), Boolean.FALSE);
            }
        }
    }

    private static void friendAskingSend(Player sender, Player respondent, boolean invite) {
        String senderName = sender.getDisplayName().getString();
        MutableComponent[] components = invite ?
                new TextComponent[]{
                        new TextComponent("-- Invite --\n"),
                        new TextComponent("想邀请你成为其好友"),
                        new TextComponent("你将能传送至" + senderName + "对好友开放的传送点")
                } :
                new TextComponent[]{
                        new TextComponent("-- Request --\n"),
                        new TextComponent("想成为你的好友"),
                        new TextComponent(senderName + "将能传送至你对好友开放的传送点")
                };
        MutableComponent component = components[0].withStyle(ChatFormatting.GOLD);
        component.append(sender.getDisplayName());
        component.append(components[1]);
        component.append(new TextComponent(" [").withStyle(ChatFormatting.YELLOW));
        component.append(new TextComponent("✔").setStyle(Style.EMPTY
                .withColor(ChatFormatting.DARK_GREEN)
                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, components[2]))
                .withClickEvent(new ClickEvent(
                                ClickEvent.Action.SUGGEST_COMMAND,
                                ("/" + CCMain.ID + " friends accept " + (invite ? "invite " : "request ") + sender.getUUID())
                        )
                )
        ));
        component.append(new TextComponent("]").withStyle(ChatFormatting.YELLOW));
        respondent.sendMessage(component, sender.getUUID());
    }

    private static int friendInvite(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        friendAskToAdd(context, Boolean.TRUE);
        return Command.SINGLE_SUCCESS;
    }

    private static int friendRequest(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        friendAskToAdd(context, Boolean.FALSE);
        return Command.SINGLE_SUCCESS;
    }

    private static void friendAccept(CommandContext<CommandSourceStack> context, boolean invite) throws CommandSyntaxException {
        CommandSourceStack src = context.getSource();
        UUID other = UuidArgument.getUuid(context, "uuid");
        UUID self = src.getPlayerOrException().getUUID();

        UUID friend = invite ? self : other;
        UUID inserted = invite ? other : self;

        switch (PermissionManager.get(src.getServer()).acceptAddingFriend(friend, inserted, src.getServer())) {
            case SELF -> src.sendFailure(new TextComponent("不能添加自己！"));
            case CONTAINED -> src.sendFailure(new TextComponent("好友已经包含此人！"));
            case OUT_OF_SIZE -> src.sendFailure(new TextComponent("好友数已超过上限！"));
            case NO_REQUEST -> src.sendFailure(new TextComponent("未知请求！"));
            case PLAYER_NOT_FOUND -> src.sendFailure(new TextComponent("查无此人"));
            case SUCCESS -> src.sendSuccess(new TextComponent("添加成功！"), Boolean.FALSE);
        }
    }

    private static int friendInviteAccept(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        friendAccept(context, Boolean.TRUE);
        return Command.SINGLE_SUCCESS;
    }

    private static int friendRequestAccept(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        friendAccept(context, Boolean.FALSE);
        return Command.SINGLE_SUCCESS;
    }

    private static int friendRemove(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack src = context.getSource();
        UUID friend = UuidArgument.getUuid(context, "uuid");
        UUID self = src.getPlayerOrException().getUUID();
        if (self.equals(friend)) {
            src.sendFailure(new TextComponent("无法删除自己！"));
        } else {
            PermissionManager manager = PermissionManager.get(src.getServer());
            if (manager.removeFriend(self, friend)) {
                src.sendSuccess(new TextComponent("删除成功!"), Boolean.FALSE);
            } else {
                src.sendFailure(new TextComponent("好友不存在！"));
            }
        }
        return Command.SINGLE_SUCCESS;
    }

    private static int removeWandFire(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        for (Player player : EntityArgument.getPlayers(context, "player")) {
            PortalWand.removeOnesFire(player);
        }
        return Command.SINGLE_SUCCESS;
    }

    protected static MutableComponent toAlign(MutableComponent component) {
        final int len = 40;
        int componentLen = component.getString().length();
        if (componentLen >= len) {
            return component;
        }
        int total = len - componentLen;
        int left = total / 2;
        int right = total - left;
        return TextComponent.EMPTY.copy()
                .append(new TextComponent("-".repeat(left) + " ").withStyle(ChatFormatting.YELLOW))
                .append(component)
                .append(new TextComponent(" " + "-".repeat(right)).withStyle(ChatFormatting.YELLOW));
    }
}
