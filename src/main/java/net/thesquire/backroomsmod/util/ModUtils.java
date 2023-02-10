package net.thesquire.backroomsmod.util;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.state.State;
import net.minecraft.state.StateManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.thesquire.backroomsmod.BackroomsMod;
import net.thesquire.backroomsmod.config.ModConfig;

import java.util.Optional;

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

    // this is a slightly modified version of Minecraft's NbtHelper::toBlockState method
    public static BlockState blockStateFromNbt(NbtCompound nbt){
        if (!nbt.contains("Name", NbtElement.STRING_TYPE)) {
            return Blocks.AIR.getDefaultState();
        }
        Identifier identifier = new Identifier(nbt.getString("Name"));
        Optional<RegistryEntry.Reference<Block>> optional = Registries.BLOCK.getEntry(RegistryKey.of(RegistryKeys.BLOCK, identifier));
        if (optional.isEmpty()) {
            return Blocks.AIR.getDefaultState();
        }
        Block block = (Block)((RegistryEntry<?>)optional.get()).value();
        BlockState blockState = block.getDefaultState();
        if (nbt.contains("Properties", NbtElement.COMPOUND_TYPE)) {
            NbtCompound nbtCompound = nbt.getCompound("Properties");
            StateManager<Block, BlockState> stateManager = block.getStateManager();
            for (String string : nbtCompound.getKeys()) {
                net.minecraft.state.property.Property<?> property = stateManager.getProperty(string);
                if (property == null) continue;
                blockState = withProperty(blockState, property, string, nbtCompound, nbt);
            }
        }
        return blockState;
    }

    private static <S extends State<?, S>, T extends Comparable<T>> S withProperty(S state, net.minecraft.state.property.Property<T> property, String key, NbtCompound properties, NbtCompound root) {
        Optional<T> optional = property.parse(properties.getString(key));
        if (optional.isPresent()) {
            return state.with(property, optional.get());
        }
        BackroomsMod.LOGGER.warn("Unable to read property: {} with value: {} for blockstate: {}", key, properties.getString(key), root.toString());
        return state;
    }

}
