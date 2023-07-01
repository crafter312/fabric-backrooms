package net.thesquire.backroomsmod.event.custom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.PositionSource;
import net.minecraft.world.event.listener.GameEventListener;
import net.thesquire.backroomsmod.block.ModBlockEntities;
import net.thesquire.backroomsmod.block.ModBlockProperties;
import net.thesquire.backroomsmod.block.entity.flickering.MountableFluorescentLightBlockEntity;
import net.thesquire.backroomsmod.tag.ModTags;
import net.thesquire.backroomsmod.util.ModUtils;

import java.util.Optional;

/**
 * This is experimental code. All of the relevant methods for implementation in
 * {@link net.thesquire.backroomsmod.block.custom.MountableFluorescentLightBlock} and
 * {@link net.thesquire.backroomsmod.block.entity.flickering.MountableFluorescentLightBlockEntity}
 * have been commented out until I can either figure out how to make this work or an alternate
 * method by which I can implement some kind of event which other blocks can listen for.
 * See {@link net.minecraft.world.event.listener.VibrationListener} for an example.
 */

public class BlackoutListener implements GameEventListener {

    public static Codec<BlackoutListener> createCodec(BlackoutListener.Callback callback) {
        return RecordCodecBuilder.create(instance -> instance.group(
                        PositionSource.CODEC.fieldOf("positionSource").forGetter(BlackoutListener::getPositionSource),
                        Codecs.NONNEGATIVE_INT.fieldOf("range").forGetter(BlackoutListener::getRange),
                        Codecs.NONNEGATIVE_INT.fieldOf("blackoutTime").forGetter(BlackoutListener::getBlackoutTime))
                .apply(instance, (positionSource, range, blackoutTime) -> new BlackoutListener(positionSource, range, callback, blackoutTime)));
    }

    ///////////////////////////////////////////////////////////////////////////

    protected final PositionSource positionSource;
    protected final int range;
    protected final Callback callback;
    protected int blackoutTime;

    public BlackoutListener(PositionSource positionSource, int range, Callback callback, int blackoutTime) {
        this.positionSource = positionSource;
        this.range = range;
        this.callback = callback;
        this.blackoutTime = blackoutTime;
    }

    @Override
    public boolean listen(ServerWorld world, GameEvent event, GameEvent.Emitter emitter, Vec3d emitterPos) {
        if(this.blackoutTime > 0) return false;

        Optional<MountableFluorescentLightBlockEntity> optionalBlockEntity = world.getBlockEntity(new BlockPos(ModUtils.vec3dtoi(emitterPos)), ModBlockEntities.MOUNTABLE_FLUORESCENT_LIGHT);
        MountableFluorescentLightBlockEntity blockEntity;
        if(optionalBlockEntity.isEmpty() || !this.callback.canAccept(event, blockEntity = optionalBlockEntity.get())) return false;

        Optional<Vec3d> optionalPos = this.positionSource.getPos(world);
        if (optionalPos.isEmpty()) return false;

        Vec3d listenerPos = optionalPos.get();
        float distance = (float)emitterPos.distanceTo(listenerPos);
        if (distance > blockEntity.getBlackoutRange()) return false;

        // with all the checks completed, start the blackout event
        this.blackoutTime = blockEntity.getBlackoutTime();
        BlockPos listenerBlockPos = new BlockPos(ModUtils.vec3dtoi(listenerPos));
        BlockState listenerState = world.getBlockState(listenerBlockPos);
        if (listenerState.contains(ModBlockProperties.LUMINANCE))
            world.setBlockState(listenerBlockPos, listenerState.with(ModBlockProperties.LUMINANCE, 0), Block.NOTIFY_ALL);

        return true;
    }

    public void tick() {
        if(this.blackoutTime == 0) return;
        this.blackoutTime -= 1;
    }

    @Override
    public PositionSource getPositionSource() {
        return this.positionSource;
    }

    @Override
    public int getRange() {
        return this.range;
    }

    public boolean isBlackout() { return this.blackoutTime > 0; }

    public int getBlackoutTime() { return this.blackoutTime; }

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    public interface Callback {

        default TagKey<GameEvent> getTag() {
            return ModTags.BLACKOUT_TRIGGERS;
        }

        default boolean canAccept(GameEvent event, MountableFluorescentLightBlockEntity blockEntity) {
            return event.isIn(this.getTag()) && blockEntity.isBlackoutSource() && blockEntity.getBlackoutTime() > 0;
        }

    }

}
