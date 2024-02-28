package io.github.c20c01.cc_fp.item;

import io.github.c20c01.cc_fp.CCMain;
import io.github.c20c01.cc_fp.block.portalFire.BasePortalFireBlock;
import io.github.c20c01.cc_fp.block.portalFire.PortalFireBlock;
import io.github.c20c01.cc_fp.block.portalFire.PortalFireBlockEntity;
import io.github.c20c01.cc_fp.config.CCConfig;
import io.github.c20c01.cc_fp.particle.PlayParticle;
import io.github.c20c01.cc_fp.particle.SendParticle;
import io.github.c20c01.cc_fp.tool.MessageSender;
import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.common.UsernameCache;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;
import java.util.UUID;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class PortalWand extends Item {
    private static final HashMap<UUID, TemporaryFires> TEMPORARY_FIRES = new HashMap<>();
    private static final BlockState TEMPORARY_FIRE_IN = CCMain.PORTAL_FIRE_BLOCK.get().defaultBlockState().setValue(BasePortalFireBlock.TEMPORARY, Boolean.TRUE);
    private static final BlockState TEMPORARY_FIRE_OUT = CCMain.FAKE_PORTAL_FIRE_BLOCK.get().defaultBlockState().setValue(BasePortalFireBlock.TEMPORARY, Boolean.TRUE);
    private static final int MAX_POWER = 96;
    private static final int BAR_COLOR = Mth.color(0.4F, 1F, 0.4F);

    public PortalWand(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        // shift >> tp in, without shift >> tp out
        var itemStack = player.getItemInHand(hand);
        if (!checkUUID(player, itemStack)) {
            if (player instanceof ServerPlayer serverPlayer) {
                MessageSender.gameInfo(serverPlayer, Component.translatable(CCMain.TEXT_NOT_OWNER));
            }
            return InteractionResultHolder.fail(itemStack);
        }

        if (!usePower(player, itemStack)) {
            return InteractionResultHolder.fail(itemStack);
        }

        if (level instanceof ServerLevel serverLevel) {
            TEMPORARY_FIRES.putIfAbsent(player.getUUID(), new TemporaryFires());
            player.getCooldowns().addCooldown(this, 10);
            SendParticle.line(serverLevel, (ServerPlayer) player, SendParticle.Particles.RAY, player.getEyePosition(), player.getViewVector(1F));
            shoot(player, serverLevel, itemStack);
        } else {
            var viewVector = player.getViewVector(1.0F);
            PlayParticle.play(SendParticle.Particles.RAY, SendParticle.Modes.LINE, player.getEyePosition(), new float[]{(float) viewVector.x, (float) viewVector.y, (float) viewVector.z});
        }
        level.playSound(null, player.blockPosition(), SoundEvents.TRIDENT_THROW, player.getSoundSource(), 1F, 1F);
        return InteractionResultHolder.success(itemStack);
    }

    @Override
    public void appendHoverText(ItemStack wand, @Nullable Level level, List<Component> components, TooltipFlag tooltipFlag) {
        if (wand.getTag() != null && wand.getTag().contains("Owner")) {
            String name = UsernameCache.getLastKnownUsername(wand.getTag().getUUID("Owner"));
            if (name != null) {
                components.add(Component.literal(name).withStyle(ChatFormatting.DARK_GREEN));
            }
        }
    }

    @Override
    public void onDestroyed(ItemEntity itemEntity, DamageSource damageSource) {
        var uuid = getUUID(itemEntity.getItem());
        var server = itemEntity.getServer();
        if (server != null) {
            TEMPORARY_FIRES.get(uuid).removeAllFireBlock(server);
        }
    }

    @Override
    public boolean isBarVisible(ItemStack wand) {
        return wand.hasTag() && getPower(wand) != MAX_POWER;
    }

    @Override
    public int getBarColor(ItemStack itemStack) {
        return BAR_COLOR;
    }

    @Override
    public int getBarWidth(ItemStack wand) {
        return Math.min(Math.round(getPower(wand) * 13.0F / MAX_POWER), 13);
    }

    public static int getPower(ItemStack wand) {
        if (wand.getTag() != null && wand.getTag().contains("Power")) {
            return wand.getTag().getInt("Power");
        } else {
            setPower(wand, MAX_POWER);
            return MAX_POWER;
        }
    }

    private static void setPower(ItemStack wand, int energy) {
        CompoundTag tag = wand.getOrCreateTag();
        tag.putInt("Power", energy);
    }

    private static boolean usePower(Player player, ItemStack wand) {
        int energy = getPower(wand);
        if (energy > 0) {
            if (!player.getAbilities().instabuild) {
                setPower(wand, energy - 1);
                if (energy == 1) {
                    player.playSound(SoundEvents.ITEM_BREAK, 0.8F, 0.8F + player.level().random.nextFloat() * 0.4F);
                }
            }
            return true;
        }
        return false;
    }

    public static void addPower(ItemStack wand, int energy) {
        int power = getPower(wand);
        setPower(wand, Math.min(power + energy, MAX_POWER));
    }

    @Nullable
    public static GlobalPos getWandPoint(String targetName) {
        TemporaryFires temporaryFires = TEMPORARY_FIRES.get(UUID.fromString(targetName));
        if (temporaryFires == null) return null;
        return temporaryFires.out;
    }

    private static boolean checkUUID(Player player, ItemStack stack) {
        var uuid = player.getUUID();
        if (stack.getTag() != null && stack.getTag().contains("Owner")) {
            return uuid.equals(stack.getTag().getUUID("Owner"));
        } else {
            var tag = stack.getOrCreateTag();
            tag.putUUID("Owner", uuid);
            return true;
        }
    }

    @Nullable
    private static UUID getUUID(ItemStack stack) {
        if (stack.getTag() != null && stack.getTag().contains("Owner")) {
            return stack.getTag().getUUID("Owner");
        }
        return null;
    }

    private static void setFireIn(ServerLevel serverLevel, BlockPos blockPos, ItemStack itemStack) {
        UUID uuid = getUUID(itemStack);
        if (uuid == null) {
            return;
        }

        Stack<GlobalPos> in = TEMPORARY_FIRES.get(uuid).in;
        if (!in.isEmpty()) {
            if (TemporaryFires.removeFireBlock(in.peek(), serverLevel.getServer(), Boolean.FALSE)) {
                in.pop();
            }
        }
        in.add(GlobalPos.of(serverLevel.dimension(), blockPos));
        serverLevel.setBlock(blockPos, TEMPORARY_FIRE_IN, Block.UPDATE_ALL);

        if (serverLevel.getBlockEntity(blockPos) instanceof PortalFireBlockEntity blockEntity) {
            blockEntity.setTargetName(uuid.toString());
        }
    }

    private static void setFireOut(ServerLevel serverLevel, BlockPos blockPos, ItemStack itemStack) {
        UUID uuid = getUUID(itemStack);
        if (uuid == null) {
            return;
        }

        GlobalPos out = TEMPORARY_FIRES.get(uuid).out;
        if (out != null) {
            TemporaryFires.removeFireBlock(out, serverLevel.getServer(), Boolean.FALSE);
        }
        TEMPORARY_FIRES.get(uuid).out = GlobalPos.of(serverLevel.dimension(), blockPos);
        serverLevel.setBlock(blockPos, TEMPORARY_FIRE_OUT, Block.UPDATE_ALL);
    }

    @Nullable
    private static BlockPos getFirePos(Level level, BlockPos blockPos, int step) {
        var below = blockPos.below();
        if (level.getBlockState(blockPos).isAir() && BasePortalFireBlock.canSurviveOnBlock(level, below)) {
            return blockPos;
        } else if (step-- > 0) {
            return getFirePos(level, below, step);
        }
        return null;
    }

    private static void shoot(Entity shooter, ServerLevel serverLevel, ItemStack itemStack) {
        BlockHitResult blockHitResult = (BlockHitResult) shooter.pick(CCConfig.shootDistance.get(), 1.0F, false);
        if (blockHitResult.getType().equals(HitResult.Type.BLOCK) && blockHitResult.getDirection() != Direction.DOWN) {
            var hitPos = blockHitResult.getBlockPos().relative(blockHitResult.getDirection());
            var firPos = getFirePos(serverLevel, hitPos, 2);
            if (firPos != null) {
                if (shooter.isShiftKeyDown()) {
                    setFireIn(serverLevel, firPos, itemStack);
                } else {
                    setFireOut(serverLevel, firPos, itemStack);
                }
            }
        }
    }

    public static class TemporaryFires {
        public GlobalPos out = null;
        public final Stack<GlobalPos> in = new Stack<>();

        public static void removePoint(ServerLevel serverLevel, BlockPos blockPos, boolean in) {
            GlobalPos globalPos = GlobalPos.of(serverLevel.dimension(), blockPos);
            if (in) {
                for (TemporaryFires fires : TEMPORARY_FIRES.values()) {
                    if (fires.in.remove(globalPos)) {
                        return;
                    }
                }

            } else {
                for (TemporaryFires fires : TEMPORARY_FIRES.values()) {
                    if (fires.out != null && fires.out.equals(globalPos)) {
                        fires.out = null;
                        return;
                    }
                }

            }
        }

        public static boolean removeFireBlock(GlobalPos globalPos, MinecraftServer server, boolean force) {
            var level = server.getLevel(globalPos.dimension());
            if (level != null) {
                BlockState blockState = level.getBlockState(globalPos.pos());
                if (blockState.equals(TEMPORARY_FIRE_OUT) || blockState.equals(TEMPORARY_FIRE_IN)) {
                    level.removeBlock(globalPos.pos(), Boolean.FALSE);
                    return true;
                }
                if (force && blockState.equals(TEMPORARY_FIRE_IN.setValue(PortalFireBlock.LASTING, Boolean.TRUE))) {
                    level.removeBlock(globalPos.pos(), Boolean.FALSE);
                }
            }
            return false;
        }

        public void removeAllFireBlock(MinecraftServer server) {
            if (out != null) removeFireBlock(out, server, Boolean.TRUE);
            for (GlobalPos pos : in) {
                removeFireBlock(pos, server, Boolean.TRUE);
            }
        }
    }

    public static void removeOnesFire(Entity entity) {
        if (entity instanceof ServerPlayer serverPlayer && TEMPORARY_FIRES.containsKey(serverPlayer.getUUID())) {
            MinecraftServer server = serverPlayer.getServer();
            if (server != null) {
                TEMPORARY_FIRES.get(serverPlayer.getUUID()).removeAllFireBlock(serverPlayer.getServer());
            }
        }
    }

    @Mod.EventBusSubscriber(modid = CCMain.ID)
// 当玩家退出游戏或死亡时删除其由法杖生成的传送点
    public static class RemovePoints {
        @SubscribeEvent
        public static void playerLoggedOutEvent(PlayerEvent.PlayerLoggedOutEvent event) {
            removeOnesFire(event.getEntity());
        }

        @SubscribeEvent
        public static void LivingDeathEvent(LivingDeathEvent event) {
            removeOnesFire(event.getEntity());
        }
    }
}
