package net.thesquire.backroomsmod.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.thesquire.backroomsmod.BackroomsMod;
import net.thesquire.backroomsmod.block.ModBlockEntities;
import net.thesquire.backroomsmod.block.custom.OfficeWindowBlock;
import net.thesquire.backroomsmod.dimension.ModDimensionKeys;
import net.thesquire.backroomsmod.util.ModUtils;
import qouteall.imm_ptl.core.portal.Portal;

public class OfficeWindowBlockEntity extends PortalPlacerBlockEntity {

    private float offset;

    public OfficeWindowBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.OFFICE_WINDOW, pos, state);
        this.offset = -0.5f;
        this.dimensionTo = ModDimensionKeys.VOID;
        this.height = 1;
        this.width = 1;
        this.origin = this.getPortalOrigin(state);
    }

    @Override
    public void initPortal(ServerWorld serverWorld, BlockState state) {
        if((this.portal = Portal.ENTITY_TYPE.create(serverWorld)) == null) return;

        this.portal.setOriginPos(this.origin);
        this.portal.setDestinationDimension(this.dimensionTo != null ? this.dimensionTo : serverWorld.getRegistryKey());
        this.portal.setDestination(new Vec3d(
                this.destinationX != null ? this.destinationX : this.origin.getX(),
                this.destinationY != null ? this.destinationY : this.origin.getY(),
                this.destinationZ != null ? this.destinationZ : this.origin.getZ()
        ));

        this.portal.setOrientationAndSize(
                getPortalHorizontalVec(state),
                getPortalUpVec(state),
                this.width,
                this.height
        );

        if(!this.portal.getWorld().spawnEntity(this.portal)) {
            BackroomsMod.LOGGER.warn("Failed to spawn portal at " + this.origin);
            return;
        }
        this.portalUUID = this.portal.getUuid();
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        nbt.putFloat("offset", this.offset);
        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        this.offset = nbt.getFloat("offset");
        super.readNbt(nbt);
    }

    public void onBreak() {
        if(this.portal == null) return;
        this.portal.kill();
    }

    @Override
    protected Vec3d getPortalOrigin(BlockState state) {
        return getPos().toCenterPos().add(ModUtils.vec3itod(this.getFacingOrDefault(state)).multiply(this.offset));
    }

    @Override
    protected Vec3i getFacingOrDefault(BlockState state) {
        return state.contains(OfficeWindowBlock.FACING) ? state.get(OfficeWindowBlock.FACING).getVector() : Direction.SOUTH.getVector();
    }

}
