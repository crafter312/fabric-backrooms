package net.thesquire.backroomsmod.util;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.thesquire.backroomsmod.config.ModConfig;

public class ModUtils {

    public static BlockPos findSuitableTeleportDestination(World world, BlockPos pos) {
        for (int i = 0; i < ModConfig.MAX_TELEPORT_DEST_SEARCH_DISTANCE; i++) {
            BlockPos destPos = pos.add(i, 0, i);
            BlockPos destPosUp = destPos.add(Direction.UP.getVector());
            BlockPos destPosDown = destPos.add(Direction.DOWN.getVector());
            BlockState block = world.getBlockState(destPos);
            BlockState blockAbove = world.getBlockState(destPosUp);
            BlockState blockBelow = world.getBlockState(destPosDown);
            if (!block.isSolidBlock(world, destPos) && !blockAbove.isSolidBlock(world, destPosUp) && blockBelow.isSolidBlock(world, destPosDown)) {
                return destPos;
            }
        }
        return pos;
    }

    public static int boolToInt(boolean b, int first, int second) {
        return b ? first : second;
    }

}
