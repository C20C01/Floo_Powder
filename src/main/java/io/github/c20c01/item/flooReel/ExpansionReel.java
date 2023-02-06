package io.github.c20c01.item.flooReel;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.Item;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault

public class ExpansionReel extends Item {
    public enum Type {
        everyone, friends, item, minecart, fallingBlock, tnt, projectile, ageableMob, monster, saveDirection
    }

    public final Type type;

    public ExpansionReel(Properties properties, Type type) {
        super(properties);
        this.type = type;
    }

    public boolean allow(Entity entity) {
        boolean allow = false;
        switch (type) {
            case everyone, friends -> allow = entity instanceof Player;
            case item -> allow = entity instanceof ItemEntity;
            case minecart -> allow = entity instanceof AbstractMinecart;
            case fallingBlock -> allow = entity instanceof FallingBlockEntity;
            case tnt -> allow = entity instanceof PrimedTnt;
            case projectile -> allow = entity instanceof Projectile;
            case ageableMob -> allow = entity instanceof AgeableMob;
            case monster -> allow = entity instanceof Monster;
        }
        return allow;
    }

    public boolean saveDirection() {
        return type == Type.saveDirection;
    }

    public boolean allowEveryone() {
        return type == Type.everyone;
    }
}
