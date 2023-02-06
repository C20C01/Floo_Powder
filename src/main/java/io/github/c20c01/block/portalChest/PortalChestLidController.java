package io.github.c20c01.block.portalChest;

import net.minecraft.util.Mth;

public class PortalChestLidController {
    private boolean open;
    private float openness;
    private float oOpenness;
    private int tick = 0;

    public void tickLid() {
        this.oOpenness = this.openness;
        float f = 0.05F;
        if (this.open) {
            if (this.openness < 1.0F) {
                this.openness = Math.min(this.openness + f, 1.0F);
            } else if (tick++ > 50) {
                setOpen(Boolean.FALSE);
            }
        } else {
            if (this.openness > 0.0F) this.openness = Math.max(this.openness - f, 0.0F);
        }
    }

    public float getOpenness(float v) {
        return Mth.lerp(v, this.oOpenness, this.openness);
    }

    public void setOpen(boolean b) {
        if (b) tick = 0;
        this.open = b;
    }
}
