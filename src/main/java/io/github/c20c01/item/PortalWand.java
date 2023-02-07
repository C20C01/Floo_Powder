package io.github.c20c01.item;

import io.github.c20c01.CCMain;
import io.github.c20c01.block.portalFire.BasePortalFireBlock;
import io.github.c20c01.block.portalFire.PortalFireBlockEntity;
import io.github.c20c01.particle.PlayParticle;
import io.github.c20c01.particle.SendParticle;
import io.github.c20c01.client.particles.RayParticle;
import io.github.c20c01.savedData.PortalPoint;
import io.github.c20c01.savedData.PortalPointManager;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.UUID;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class PortalWand extends Item {
    private static final String SUFFIX = "_PortalWand_Temporary_Teleport_Point_";
    private static final String OWNER = "Owner";
    private static final String IN = "In";
    private static final String OUT = "Out";

    public PortalWand(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        // shift >> 传入, without shift >> 传出
        var stack = player.getItemInHand(hand);
        if (!checkUUID(player, stack)) {
            if (player instanceof ServerPlayer serverPlayer) {
                var text = new TranslatableComponent(CCMain.TEXT_NOT_OWNER);
                serverPlayer.sendMessage(text, ChatType.GAME_INFO, Util.NIL_UUID);
            }
            return InteractionResultHolder.fail(stack);
        }

        if (level instanceof ServerLevel serverLevel) {
            player.getCooldowns().addCooldown(stack.getItem(), 10);
            SendParticle.line(serverLevel, (ServerPlayer) player, SendParticle.Particles.RAY, player.getEyePosition(), player.getViewVector(1F));
            BlockHitResult hitResult = rayTrace(player);
            if (hitResult != null) {
                var hitPos = hitResult.getBlockPos().relative(hitResult.getDirection());
                var firPos = getFirePos(level, hitPos, 2);
                if (firPos != null) {
                    if (player.isShiftKeyDown()) {
                        setFireIn(serverLevel, firPos, stack);
                    } else {
                        setFireOut(serverLevel, firPos, stack);
                    }
                }
            }
        } else {
            PlayParticle.line(new RayParticle.Option(), player.getEyePosition(), player.getViewVector(1.0F));
        }
        level.playSound(null, player, SoundEvents.DRAGON_FIREBALL_EXPLODE, SoundSource.PLAYERS, 1, 1);
        return InteractionResultHolder.success(stack);
    }

    @Override
    public void onDestroyed(ItemEntity itemEntity) {
        var uuid = getUUID(itemEntity.getItem());
        var server = itemEntity.level.getServer();
        removePoints(uuid, server);
        super.onDestroyed(itemEntity);
    }

    public static void removePoints(@Nullable UUID uuid, @Nullable MinecraftServer server) {
        if (uuid != null && server != null) {
            String name = uuid + SUFFIX;
            var portalPointManager = PortalPointManager.get(server);
            var inPoint = portalPointManager.remove(name + IN);
            if (inPoint != null) inPoint.removeFire(server);
            var outPoint = portalPointManager.remove(name + OUT);
            if (outPoint != null) outPoint.removeFire(server);
        }
    }

    private static boolean checkUUID(Player player, ItemStack stack) {
        var uuid = player.getUUID();
        if (stack.getTag() != null && stack.getTag().contains(OWNER)) {
            return uuid.equals(stack.getTag().getUUID(OWNER));
        } else {
            var tag = stack.getOrCreateTag();
            tag.putUUID(OWNER, uuid);
            var display = stack.getOrCreateTagElement(ItemStack.TAG_DISPLAY);
            var lore = new ListTag();
            var name = "{\"text\":\"" + player.getDisplayName().getString() + "\"}";
            lore.add(StringTag.valueOf(name));
            display.put(ItemStack.TAG_LORE, lore);
            return true;
        }
    }

    @Nullable
    private static UUID getUUID(ItemStack stack) {
        if (stack.getTag() != null && stack.getTag().contains(OWNER)) {
            return stack.getTag().getUUID(OWNER);
        }
        return null;
    }

    private static void updateFire(ServerLevel level, BlockPos pos, BlockState blockState, ItemStack stack, String mode) {
        final var oldFire = writeNewFire(level, pos, stack, mode);
        if (oldFire != null) {
            removeOldFire(level, pos, oldFire, blockState);
        }
    }

    private static void setFireIn(ServerLevel level, BlockPos pos, ItemStack stack) {
        final var blockState = CCMain.PORTAL_FIRE_BLOCK.get().defaultBlockState();
        updateFire(level, pos, blockState, stack, IN);
        level.setBlock(pos, blockState, Block.UPDATE_ALL);
        if (level.getBlockEntity(pos) instanceof PortalFireBlockEntity blockEntity) {
            blockEntity.setName(getUUID(stack) + SUFFIX + OUT);
        }
    }

    private static void setFireOut(ServerLevel level, BlockPos pos, ItemStack stack) {
        final var blockState = CCMain.FAKE_PORTAL_FIRE_BLOCK.get().defaultBlockState();
        updateFire(level, pos, blockState, stack, OUT);
        level.setBlock(pos, blockState, Block.UPDATE_ALL);
    }

    @Nullable
    private static PortalPoint writeNewFire(ServerLevel newLevel, BlockPos newPos, ItemStack stack, String mode) {
        var point = new PortalPoint(getUUID(stack) + SUFFIX + mode, mode, newPos, newLevel.dimension(), getUUID(stack), false, true);
        return PortalPointManager.get(newLevel.getServer()).replace(point);
    }

    private static void removeOldFire(ServerLevel newLevel, BlockPos newPos, PortalPoint oldPoint, BlockState blockState) {
        var server = newLevel.getServer();
        var oldLevel = server.getLevel(oldPoint.dimension());
        if (oldLevel == null) return;
        var oldPos = oldPoint.pos();
        if (oldPos == newPos && oldLevel == newLevel) return;
        if (oldLevel.getBlockState(oldPos).equals(blockState)) {
            oldPoint.removeFire(server);
        }
    }

    @Nullable
    private static BlockPos getFirePos(Level level, BlockPos blockPos, int step) {
        var below = blockPos.below();
        if (level.getBlockState(blockPos).isAir() && BasePortalFireBlock.canSurviveOnBlock(level.getBlockState(below), level, below)) {
            return blockPos;
        } else if (step-- > 0) {
            return getFirePos(level, below, step);
        }
        return null;
    }

    @Nullable
    private static BlockHitResult rayTrace(Entity entity) {
        final int MAX_DISTANCE = 50;
        BlockHitResult blockHitResult = (BlockHitResult) entity.pick(MAX_DISTANCE, 1.0F, false);
        if (blockHitResult.getType() == HitResult.Type.BLOCK) {
            return blockHitResult;
        }
        return null;
    }

    @Mod.EventBusSubscriber(modid = CCMain.ID)
    public static class PlayerLoggedOut {
        @SubscribeEvent
        public static void loggedOutEvent(PlayerEvent.PlayerLoggedOutEvent event) {
            if (event.getPlayer() instanceof ServerPlayer serverPlayer) {
                removePoints(serverPlayer.getUUID(), serverPlayer.getServer());
            }
        }
    }
}
