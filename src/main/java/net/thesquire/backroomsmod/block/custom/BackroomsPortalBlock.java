package net.thesquire.backroomsmod.block.custom;

import net.kyrptonaught.customportalapi.CustomPortalBlock;
import net.kyrptonaught.customportalapi.interfaces.EntityInCustomPortal;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.thesquire.backroomsmod.portal.teleport.Level0Teleporter;

/*
 * This class is virtually identical to CustomPortalAPI's CustomPortalBlock!
 * It's only difference is that it refers to a project-local copy of the
 * CustomTeleporter class. This is done purely for debugging purposes, and
 * is not intended to otherwise be used. References to this class should
 * be commented out when not debugging in a development environment.
 */

public class BackroomsPortalBlock extends CustomPortalBlock {

    public BackroomsPortalBlock(Settings settings) {
        super(settings);
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        EntityInCustomPortal entityInPortal = (EntityInCustomPortal) entity;
        entityInPortal.tickInPortal(pos.toImmutable());
        if (!entityInPortal.didTeleport()) {
            if (entityInPortal.getTimeInPortal() >= entity.getMaxNetherPortalTime()) {
                entityInPortal.setDidTP(true);
                if (!world.isClient) {
                    Level0Teleporter.TPToDim(world, entity, getPortalBase(world, pos), pos);
                }
            }
        }
    }

}
