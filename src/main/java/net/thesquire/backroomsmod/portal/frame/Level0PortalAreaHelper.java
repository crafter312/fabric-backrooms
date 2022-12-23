package net.thesquire.backroomsmod.portal.frame;

import net.kyrptonaught.customportalapi.portal.frame.PortalFrameTester;
import net.kyrptonaught.customportalapi.portal.frame.VanillaPortalAreaHelper;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockLocating;
import net.minecraft.world.TeleportTarget;
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

    public TeleportTarget getTPTargetInPortal(BlockLocating.Rectangle portalRect, Direction.Axis portalAxis, Direction.Axis prevPortalAxis, Vec3d prevOffset, Entity entity) {
        EntityDimensions entityDimensions = entity.getDimensions(entity.getPose());
        double width = portalRect.width - entityDimensions.width;
        double height = portalRect.height - entityDimensions.height;
        double x = MathHelper.lerp(prevOffset.x, portalRect.lowerLeft.getX(), portalRect.lowerLeft.getX() + width);
        double y = MathHelper.lerp(prevOffset.y, portalRect.lowerLeft.getY(), portalRect.lowerLeft.getY() + height);
        double z = MathHelper.lerp(prevOffset.z, portalRect.lowerLeft.getZ(), portalRect.lowerLeft.getZ() + width);
        if (portalAxis == Direction.Axis.X)
            z = portalRect.lowerLeft.getZ() + 0.5D;
        else if (portalAxis == Direction.Axis.Z)
            x = portalRect.lowerLeft.getX() + .5D;

        float yaw = entity.getYaw() + (portalAxis == prevPortalAxis ? 0f : 90f);

        return new TeleportTarget(new Vec3d(x, y, z), entity.getVelocity(), yaw, entity.getPitch());
    }
}