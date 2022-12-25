package net.thesquire.backroomsmod.portal.frame;

import net.kyrptonaught.customportalapi.portal.frame.PortalFrameTester;
import net.kyrptonaught.customportalapi.portal.frame.VanillaPortalAreaHelper;
import net.kyrptonaught.customportalapi.util.CustomPortalHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockLocating;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
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

    public TeleportTarget getTPTargetInPortal(BlockLocating.Rectangle portalRect, Direction portalDir, Direction prevPortalDir, Vec3d prevOffset, Entity entity) {
        EntityDimensions entityDimensions = entity.getDimensions(entity.getPose());
        double width = portalRect.width - entityDimensions.width;
        double height = portalRect.height - entityDimensions.height;
        double x = MathHelper.lerp(prevOffset.x, portalRect.lowerLeft.getX(), portalRect.lowerLeft.getX() + width);
        double y = MathHelper.lerp(prevOffset.y, portalRect.lowerLeft.getY(), portalRect.lowerLeft.getY() + height);
        double z = MathHelper.lerp(prevOffset.z, portalRect.lowerLeft.getZ(), portalRect.lowerLeft.getZ() + width);

        float dyaw = getAngle(prevPortalDir, portalDir);
        float yaw = entity.getYaw() + dyaw;

        Direction.Axis destPortalAxis = portalDir.getAxis();
        if (destPortalAxis == Direction.Axis.X)  {
            if (dyaw == 90f || dyaw == 270f)
                z = MathHelper.lerp(prevOffset.x, portalRect.lowerLeft.getZ(), portalRect.lowerLeft.getZ() + width);
            x = portalRect.lowerLeft.getX() + .5D;
        }
        else if (destPortalAxis == Direction.Axis.Z) {
            if (dyaw == 90f || dyaw == 270f)
                x = MathHelper.lerp(prevOffset.z, portalRect.lowerLeft.getX(), portalRect.lowerLeft.getX() + width);
            z = portalRect.lowerLeft.getZ() + 0.5D;
        }

        return new TeleportTarget(new Vec3d(x, y, z), entity.getVelocity(), yaw, entity.getPitch());
    }

    // this function calculates how much you have to rotate clockwise to get from the first vector to the second vector
    // it is used to rotate an entity when teleporting between portals with non-aligned axes
    private float getAngle(Direction first, Direction second) {
        if (first.getAxis().isVertical() || second.getAxis().isVertical())
            throw new IllegalStateException("Unable to get angle with vertical portal direction!");

        float degrees = 0;
        Direction temp = first;
        while(temp != second) {
            degrees += 90;
            temp = temp.rotateYClockwise();
        }

        return degrees;
    }

    // this function gets the direction of a portal based on which side of the portal is open
    public static Direction getDirectionFrom(World world, BlockState state, BlockPos pos) {
        Direction.Axis axis = flipHorizontalAxis(CustomPortalHelper.getAxisFrom(state));
        Direction dir = Direction.from(axis, Direction.AxisDirection.POSITIVE);

        if(world.getBlockState(pos.add(dir.getVector())).getMaterial().isSolid()) return dir.getOpposite();
        return dir;
    }

    public static Direction.Axis flipHorizontalAxis(Direction.Axis axis) {
        return axis == Direction.Axis.X ? Direction.Axis.Z : Direction.Axis.X;
    }

}