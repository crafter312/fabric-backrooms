package net.thesquire.backroomsmod.block.entity.flickering;

import com.mojang.serialization.Dynamic;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.intprovider.BiasedToBottomIntProvider;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.world.World;
import net.minecraft.world.event.BlockPositionSource;
import net.minecraft.world.event.GameEvent;
import net.thesquire.backroomsmod.BackroomsMod;
import net.thesquire.backroomsmod.block.ModBlockEntities;
import net.thesquire.backroomsmod.block.ModBlockProperties;
import net.thesquire.backroomsmod.block.entity.FlickeringBlockEntity;
import net.thesquire.backroomsmod.config.ModConfig;
import net.thesquire.backroomsmod.event.ModGameEvents;
import net.thesquire.backroomsmod.event.custom.BlackoutListener;

import java.util.Objects;

//TODO add level_0 dimension local blackout events to temporarily turn off a group of lights

public class MountableFluorescentLightBlockEntity extends FlickeringBlockEntity implements BlackoutListener.Callback {

    public static final int defaultLuminance = 15;
    private static final UniformIntProvider secondsProvider = UniformIntProvider.create(1, 60);
    private static final BiasedToBottomIntProvider minutesProvider = BiasedToBottomIntProvider.create(0, 60);
    private static final UniformIntProvider rangeProvider = UniformIntProvider.create(ModConfig.minBlackoutRange, ModConfig.maxBlackoutRange);

    public static void tick(World world, BlockPos pos, BlockState state, MountableFluorescentLightBlockEntity blockEntity) {
        if(world.isClient() || !state.contains(ModBlockProperties.FLICKERING) || !state.contains(ModBlockProperties.LUMINANCE))
            return;

        BlackoutListener listener = blockEntity.getEventListener();
        if(listener.isBlackout()) {
            listener.tick();
            if(blockEntity.isBlackoutSource() && listener.getBlackoutTime() % 100 == 0)
                Objects.requireNonNull(world.getServer()).sendMessage(Text.literal(Integer.toString(listener.getBlackoutTime())));
            return;
        }

        if(!state.get(ModBlockProperties.FLICKERING) && state.get(ModBlockProperties.LUMINANCE) == 0)
            world.setBlockState(pos, state.with(ModBlockProperties.LUMINANCE, defaultLuminance), Block.NOTIFY_ALL);
        blockEntity.resetBlackoutParams();

        if(state.get(ModBlockProperties.FLICKERING) && blockEntity.randomDouble() < 0.01) {
            blockEntity.generateBlackoutParams();
            Objects.requireNonNull(world.getServer()).sendMessage(Text.literal("Range: " + blockEntity.getBlackoutRange()));
            Objects.requireNonNull(world.getServer()).sendMessage(Text.literal("Time: " + blockEntity.getBlackoutTime()));
            world.emitGameEvent(ModGameEvents.BLACKOUT, pos, new GameEvent.Emitter(null, null));
            return;
        }

        FlickeringBlockEntity.tick(world, pos, state, blockEntity);
    }

    /////////////////////////////////////////////////////////////////////////////////

    private BlackoutListener listener;
    private int blackoutTime = 0;
    private int blackoutRange = 0;
    private boolean blackoutSource = false;

    public MountableFluorescentLightBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.MOUNTABLE_FLUORESCENT_LIGHT, pos, state, defaultLuminance);
        this.listener = new BlackoutListener(new BlockPositionSource(this.pos), ModGameEvents.BLACKOUT.getRange(), this, this.blackoutTime);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        if (nbt.contains("listener", NbtElement.COMPOUND_TYPE)) {
            BlackoutListener.createCodec(this).parse(new Dynamic<>(NbtOps.INSTANCE, nbt.getCompound("listener")))
                    .resultOrPartial(BackroomsMod.LOGGER::error).ifPresent(listener -> this.listener = listener);
        }
        if (nbt.contains("blackoutTime"))
            this.blackoutTime = nbt.getInt("blackoutTime");
        if (nbt.contains("blackoutRange"))
            this.blackoutRange = nbt.getInt("blackoutRange");
        if (nbt.contains("blackoutSource"))
            this.blackoutSource = nbt.getBoolean("blackoutSource");
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        BlackoutListener.createCodec(this).encodeStart(NbtOps.INSTANCE, this.listener).resultOrPartial(BackroomsMod.LOGGER::error)
                .ifPresent(listenerNbt -> nbt.put("listener", listenerNbt));
        nbt.putInt("blackoutTime", this.blackoutTime);
        nbt.putInt("blackoutRange", this.blackoutRange);
        nbt.putBoolean("blackoutSource", this.blackoutSource);
    }

    public void generateBlackoutParams() {
        this.blackoutTime = (secondsProvider.get(this.rand) * 20) + (minutesProvider.get(this.rand));
        this.blackoutRange = rangeProvider.get(this.rand);
        this.blackoutSource = true;
    }

    public void resetBlackoutParams() {
        this.blackoutTime = 0;
        this.blackoutRange = 0;
        this.blackoutSource = false;
    }

    public boolean isBlackoutSource() { return this.blackoutSource; }

    public BlackoutListener getEventListener() { return this.listener; }

    public int getBlackoutTime() { return this.blackoutTime; }

    public int getBlackoutRange() { return this.blackoutRange; }

}
