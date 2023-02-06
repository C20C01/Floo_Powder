package io.github.c20c01.client.particles;

import io.github.c20c01.CCMain;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.PortalParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;


@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault

@OnlyIn(Dist.CLIENT)
public class PortalFireParticle extends PortalParticle {

    private final Option.TYPE type;
    private double[] start;

    public PortalFireParticle(ClientLevel level, double x, double y, double z, double xd, double yd, double zd, Option.TYPE type) {
        super(level, x, y, z, xd, yd, zd);
        this.type = type;

        switch (type) {
            case ONT -> {
                start = new double[]{x, y, z};
                this.age = this.lifetime;
            }
            case IN_LASTING -> {
                float f = this.random.nextFloat() * 0.4F + 0.6F;
                this.rCol = f;
                this.gCol = f * 0.9F;
                this.bCol = f * 0.1F;
            }
        }
    }

    @Override
    public void tick() {
        switch (type) {
            case ONT -> tickOfOut();
            case IN, IN_LASTING -> super.tick();
        }
    }

    public void tickOfOut() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age-- <= 0) {
            this.remove();
        } else {
            float f = (float) this.age / (float) this.lifetime;
            float f1 = -f + f * f * 2.0F;
            float f2 = 1.0F - f1;
            this.x = this.start[0] + this.xd * (double) f2;
            this.y = this.start[1] + this.yd * (double) f2 + (double) (1.0F - f);
            this.z = this.start[2] + this.zd * (double) f2;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;

        public Provider(SpriteSet spriteSet) {
            this.sprite = spriteSet;
        }

        @Override
        public Particle createParticle(SimpleParticleType particleType, ClientLevel level, double x, double y, double z, double xd, double yd, double zd) {
            PortalFireParticle particle = new PortalFireParticle(level, x, y, z, xd, yd, zd, ((Option) particleType).type);
            particle.pickSprite(this.sprite);
            return particle;
        }
    }

    public static class Option extends SimpleParticleType {
        public enum TYPE {IN, ONT, IN_LASTING}

        public TYPE type;

        public Option() {
            this(TYPE.IN);
        }

        public Option(TYPE type) {
            super(Boolean.FALSE);
            this.type = type;
        }

        @Override
        public @NotNull SimpleParticleType getType() {
            return CCMain.PORTAL_FIRE_PARTICLE.get();
        }
    }
}
