package net.thesquire.backroomsmod.block.entity.flickering;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.thesquire.backroomsmod.block.ModBlockEntities;
import net.thesquire.backroomsmod.block.ModBlockProperties;
import net.thesquire.backroomsmod.block.entity.FlickeringBlockEntity;

//TODO add level_0 dimension local blackout events to temporarily turn off a group of lights

public class MountableFluorescentLightBlockEntity extends FlickeringBlockEntity /*implements BlackoutListener.Callback*/ {

    public static final int defaultLuminance = 15;

    public static void tick(World world, BlockPos pos, BlockState state, MountableFluorescentLightBlockEntity blockEntity) {
        if(world.isClient() || !state.contains(ModBlockProperties.FLICKERING) || !state.contains(ModBlockProperties.LUMINANCE))
            return;
        /*
        else if(blockEntity.getEventListener().isBlackout()) {
            if(state.get(ModBlockProperties.LUMINANCE) != 0)
                world.setBlockState(pos, state.with(ModBlockProperties.LUMINANCE, 0), Block.NOTIFY_ALL);
            blockEntity.getEventListener().tick();
//            if(blockEntity.getTime() % 20 == 0)
//                Objects.requireNonNull(world.getServer()).sendMessage(Text.literal(Integer.toString(blockEntity.getTime())));
            return;
        }
        else if(!state.get(ModBlockProperties.FLICKERING)) {
            if(state.get(ModBlockProperties.LUMINANCE) == 0)
                world.setBlockState(pos, state.with(ModBlockProperties.LUMINANCE, defaultLuminance), Block.NOTIFY_ALL);
            return;
        }

        double d = blockEntity.randomDouble();
        if(d < 0.00001) {
            BlockState blackoutState = state.with(ModBlockProperties.IS_BLACKOUT_SOURCE, true)
                            .with(ModBlockProperties.BLACKOUT_TIME, blockEntity.generateBlackoutTime())
                            .with(ModBlockProperties.BLACKOUT_RANGE, blockEntity.generateBlackoutRange());
            world.setBlockState(pos, blackoutState, Block.NOTIFY_ALL);
            world.emitGameEvent(ModGameEvents.BLACKOUT, pos, new GameEvent.Emitter(null, blackoutState));
            return;
        }
*/
        FlickeringBlockEntity.tick(world, pos, state, blockEntity);
    }

    /////////////////////////////////////////////////////////////////////////////////

//    private BlackoutListener listener;
//    private final UniformIntProvider secondsProvider = UniformIntProvider.create(1, 60);
//    private final BiasedToBottomIntProvider minutesProvider = BiasedToBottomIntProvider.create(0, 60);
//    private final UniformIntProvider rangeProvider = UniformIntProvider.create(ModConfig.minBlackoutRange, ModConfig.maxBlackoutRange);

    public MountableFluorescentLightBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.MOUNTABLE_FLUORESCENT_LIGHT, pos, state, defaultLuminance);
//        this.listener = new BlackoutListener(new BlockPositionSource(this.pos), ModGameEvents.BLACKOUT.getRange(), this, false, 0);
    }
//    public BlackoutListener getEventListener() { return listener; }

    /*@Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        if (nbt.contains("listener", NbtElement.COMPOUND_TYPE)) {
            BlackoutListener.createCodec(this).parse(new Dynamic<>(NbtOps.INSTANCE, nbt.getCompound("listener")))
                    .resultOrPartial(BackroomsMod.LOGGER::error).ifPresent(listener -> this.listener = listener);
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        BlackoutListener.createCodec(this).encodeStart(NbtOps.INSTANCE, this.listener).resultOrPartial(BackroomsMod.LOGGER::error)
                .ifPresent(listenerNbt -> nbt.put("listener", listenerNbt));
    }

    public int generateBlackoutTime() {
        return (secondsProvider.get(this.rand) * 20) + (minutesProvider.get(this.rand));
    }

    public int generateBlackoutRange() {
        return rangeProvider.get(this.rand);
    }*/

}
