package net.thesquire.backroomsmod.block.entity;

import net.minecraft.util.math.MathHelper;

public class ElevatorDoorAnimator {

    private boolean open;
    private float progress;
    private float lastProgress;
    private final float f;

    public ElevatorDoorAnimator(float f) {
        this.f = f;
    }

    public void step() {
        this.lastProgress = this.progress;
        if (!this.open && this.progress > 0.0f) {
            this.progress = Math.max(this.progress - this.f, 0.0f);
        } else if (this.open && this.progress < 1.0f) {
            this.progress = Math.min(this.progress + this.f, 1.0f);
        }
    }

    public float getProgress(float delta) {
        return MathHelper.lerp(delta, this.lastProgress, this.progress);
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

}
