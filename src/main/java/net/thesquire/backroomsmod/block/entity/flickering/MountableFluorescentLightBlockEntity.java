package net.thesquire.backroomsmod.block.entity.flickering;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.thesquire.backroomsmod.block.ModBlockEntities;
import net.thesquire.backroomsmod.block.custom.MountableFluorescentLightBlock;
import net.thesquire.backroomsmod.block.entity.FlickeringBlockEntity;

//TODO add level_0 dimension local blackout events to temporarily turn off a group of lights

public class MountableFluorescentLightBlockEntity extends FlickeringBlockEntity {

    public static final int defaultLuminance = 15;

    public static void tick(World world, BlockPos pos, BlockState state, MountableFluorescentLightBlockEntity blockEntity) {
        if(world.isClient() || !state.get(MountableFluorescentLightBlock.FLICKERING)) return;
        else if(blockEntity.isUnlit()) {
            blockEntity.decreaseTime();
//            if(blockEntity.getTime() % 20 == 0)
//                Objects.requireNonNull(world.getServer()).sendMessage(Text.literal(Integer.toString(blockEntity.getTime())));
            return;
        }

        /*if(blockEntity.isOffSeconds() || blockEntity.isOffMinutes()) {
            blockEntity.setTime(
                    (ModUtils.boolToInt(blockEntity.isOffSeconds(), 1, 0) * secondsProvider.get(rand) * 20) +
                            (ModUtils.boolToInt(blockEntity.isOffMinutes(), 1, 0) * minutesProvider.get(rand) * 1200)
            );
            blockEntity.setOffSeconds(false);
            blockEntity.setOffMinutes(false);
            world.setBlockState(pos, state.with(MountableFluorescentLightBlock.LUMINANCE, 0), Block.NOTIFY_LISTENERS);
            return;
        }*/

        FlickeringBlockEntity.tick(world, pos, state, blockEntity);



        /*
        else if(blockEntity.isUnlit()) {
            blockEntity.decreaseTime();
            if(blockEntity.getTime() % 20 == 0)
                Objects.requireNonNull(world.getServer()).sendMessage(Text.literal(Integer.toString(blockEntity.getTime())));
            return;
        }

        double d = rand.nextDouble();
        int lum = defaultLuminance;

        if(blockEntity.isOffSeconds() || blockEntity.isOffMinutes()) {
            blockEntity.setTime(
                    (ModUtils.boolToInt(blockEntity.isOffSeconds(), 1, 0) * secondsProvider.get(rand) * 20) +
                            (ModUtils.boolToInt(blockEntity.isOffMinutes(), 1, 0) * minutesProvider.get(rand) * 1200)
            );
            blockEntity.setOffSeconds(false);
            blockEntity.setOffMinutes(false);
            lum = 0;
        }
        else if(d < 0.15) {
            if(d >= 0.1)
                lum -= dim1.get(rand);
            else if(d >= 0.003)
                lum -= dim2.get(rand);
            else if(d >= 0.0001) {
                blockEntity.setOffSeconds(true);
                lum = 0;
            }
            else {
                blockEntity.setOffMinutes(true);
                lum = 0;
            }
        }

        world.setBlockState(pos, state.with(MountableFluorescentLightBlock.LUMINANCE, lum), Block.NOTIFY_LISTENERS);*/
    }

    /////////////////////////////////////////////////////////////////////////////////

    private boolean offSeconds = false;
    private boolean offMinutes = false;
    private int time = 0;
    public MountableFluorescentLightBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.MOUNTABLE_FLUORESCENT_LIGHT, pos, state, defaultLuminance);
    }

    public boolean isOffSeconds() { return offSeconds; }
    public boolean isOffMinutes() { return offMinutes; }
    public void setOffSeconds(boolean b) { offSeconds = b; }
    public void setOffMinutes(boolean b) { offMinutes = b; }

    public boolean isUnlit() { return time > 0; }
    public void decreaseTime() { time -= 1; }
    public void setTime(int t) { time = t; }

    public int getTime() { return time; }

}
