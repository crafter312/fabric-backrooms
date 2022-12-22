package net.thesquire.backroomsmod.portal.frame;

import net.kyrptonaught.customportalapi.portal.frame.PortalFrameTester;
import net.kyrptonaught.customportalapi.portal.frame.VanillaPortalAreaHelper;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldAccess;

import java.util.Optional;
import java.util.function.Predicate;

public class Level0PortalAreaHelper extends VanillaPortalAreaHelper {
    
    public Level0PortalAreaHelper() {
        super();
    }

    @Override
    public Optional<PortalFrameTester> getNewPortal(WorldAccess worldAccess, BlockPos blockPos, Direction.Axis axis, Block... foundations) {
        return getOrEmpty(worldAccess, blockPos, PortalFrameTester::isValidFrame, axis, foundations);
    }

    @Override
    public Optional<PortalFrameTester> getOrEmpty(WorldAccess worldAccess, BlockPos blockPos, Predicate<PortalFrameTester> predicate, Direction.Axis axis, Block... foundations) {
        Optional<PortalFrameTester> optional = Optional.of(new Level0PortalAreaHelper().init(worldAccess, blockPos, axis, foundations)).filter(predicate);
        if (optional.isPresent()) {
            return optional;
        } else {
            Direction.Axis axis2 = axis == Direction.Axis.X ? Direction.Axis.Z : Direction.Axis.X;
            return Optional.of(new Level0PortalAreaHelper().init(worldAccess, blockPos, axis2, foundations)).filter(predicate);
        }
    }

}
