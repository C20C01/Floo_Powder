package io.github.c20c01.cc_fp.config;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.common.ForgeConfigSpec;

@SuppressWarnings("SameReturnValue")
public class CCConfig {
    public static final ForgeConfigSpec COMMON_CONFIG;
    public static final ForgeConfigSpec.IntValue maxConversion; // 火焰连锁反应的最大次数
    public static final ForgeConfigSpec.IntValue maxFriendSize; // 每个玩家的传送白名单的最大容量
    public static final ForgeConfigSpec.IntValue shootDistance; // 传送法杖的射程
    public static final ForgeConfigSpec.BooleanValue canUseInSurvival; // 生存下可以直接手持飞路粉进行使用
    public static final ForgeConfigSpec.BooleanValue lastingPowderCanSummonLava; // 将不灭粉丢入岩浆会将其变为岩浆源
    public static final ForgeConfigSpec.ConfigValue<String> defaultPoint; // 向不存在的传送点传送后会尝试传送到的默认传送点（""为不开启此功能）

    static {
        ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();
        COMMON_BUILDER.comment("General settings").push("general");

        maxConversion = COMMON_BUILDER.comment("Maximum number of conversion flames. (100)").defineInRange("maxConversion", 100, 0, 512);
        maxFriendSize = COMMON_BUILDER.comment("Maximum number of inserted friends. (32)").defineInRange("maxFriendSize", 32, 0, 64);
        shootDistance = COMMON_BUILDER.comment("The range of the PortalWand. (64)").defineInRange("shootDistance", 64, 32, 256);
        canUseInSurvival = COMMON_BUILDER.comment("Player can TP by using floo powder in hand in survival mode (false)").define("canUseInSurvival", false);
        lastingPowderCanSummonLava = COMMON_BUILDER.comment("Lasting powder can turn lava into the source of lava (true)").define("lastingPowderCanSummonLava", true);
        defaultPoint = COMMON_BUILDER.comment("TP to where when inserted try to TP to a non-existent point. (empty to close this function)").define("defaultPoint", "");

        COMMON_BUILDER.pop();
        COMMON_CONFIG = COMMON_BUILDER.build();
    }

    public static <T> LiteralArgumentBuilder<CommandSourceStack> getCommand(ForgeConfigSpec.ConfigValue<T> configValue, ArgumentType<T> type, Class<T> valeClass) {
        String configName = getConfigName(configValue);
        return Commands.literal(configName).requires(cs -> cs.hasPermission(4)).executes((context -> getConfigValue(context, configName, configValue)))
                .then(Commands.argument(configName, type).executes(context -> setConfigValue(context, configName, configValue, valeClass)));
    }

    private static <T> String getConfigName(ForgeConfigSpec.ConfigValue<T> configValue) {
        var path = configValue.getPath();
        return path.get(path.size() - 1);
    }

    private static <T> int getConfigValue(CommandContext<CommandSourceStack> context, String configName, ForgeConfigSpec.ConfigValue<T> configValue) {
        context.getSource().sendSuccess(new TextComponent(configName + ": " + configValue.get().toString()), Boolean.FALSE);
        return Command.SINGLE_SUCCESS;
    }

    private static <T> int setConfigValue(CommandContext<CommandSourceStack> context, String configName, ForgeConfigSpec.ConfigValue<T> configValue, Class<T> valeClass) {
        configValue.set(context.getArgument(configName, valeClass));
        context.getSource().sendSuccess(new TextComponent(configName + ": " + configValue.get().toString()), Boolean.FALSE);
        return Command.SINGLE_SUCCESS;
    }
}
