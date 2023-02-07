package net.thesquire.backroomsmod.block.entity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.thesquire.backroomsmod.BackroomsMod;
import net.thesquire.backroomsmod.block.ModBlockEntities;
import net.thesquire.backroomsmod.util.ModUtils;
import qouteall.imm_ptl.core.portal.Portal;
import qouteall.imm_ptl.core.portal.PortalManipulation;
import qouteall.q_misc_util.Helper;
import qouteall.q_misc_util.dimension.DimId;

// for the time being, this block entity assumes that there is no scale transformation between the two dimensions
public class PortalPlacerBlockEntity extends BlockEntity {

    private RegistryKey<World> dimensionTo = null;
    private int width = 10;
    private int height = 4;
    private Vec3d origin;
    private Vec3d destination;

    /**
     * This parameter can be written to via the {@code PortalPlacerBlockEntity::readNbt} method,
     * but not saved as nbt via {@code PortalPlacerBlockEntity::writeNbt}. This means that the
     * parameter always initializes as {@code true}. The intention is that in order to set the
     * nbt data of this BlockEntity without it creating a portal, you set this parameter to
     * {@code false}. Then, next time the BlockEntity is loaded into the world, it creates the
     * specified portal on its first tick, since the {@code active} parameter always initializes
     * as {@code true}.
     */
    private boolean active = true;

    public PortalPlacerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PORTAL_PLACER, pos, state);
        this.origin = getPortalOrigin(state);
        this.destination = this.origin;
    }

    public void tick(World world, BlockState state) {
        if(world.isClient() || !this.active || this.dimensionTo == null || !state.contains(Properties.FACING)) return;

        initPortal((ServerWorld) world, state);
        world.setBlockState(getPos(), Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        if(this.dimensionTo != null)
            DimId.putWorldId(nbt, "dimensionTo", this.dimensionTo);
        nbt.putInt("width", this.width);
        nbt.putInt("height", this.height);
        Helper.putVec3d(nbt, "origin", this.origin);
        Helper.putVec3d(nbt, "destination", this.destination);
    }

    // each nbt key is treated as optional so the nbt can be partially set using the setblock command
    @Override
    public void readNbt(NbtCompound nbt) {
        if(nbt.contains("dimensionTo"))
            this.dimensionTo = DimId.idToKey(nbt.getString("dimensionTo"));
        if(nbt.contains("width"))
            this.width = nbt.getInt("width");
        if(nbt.contains("height"))
            this.height = nbt.getInt("height");
        this.origin = ModUtils.getVec3dComponents(nbt, "origin", this.origin);
        this.destination = ModUtils.getVec3dComponents(nbt, "destination", this.destination);

        if(nbt.contains("active"))
            this.active = nbt.getBoolean("active");
    }

    public void initPortal(ServerWorld serverWorld, BlockState state) {
        Portal portal = Portal.entityType.create(serverWorld);
        if(portal == null) return;

        portal.setOriginPos(this.origin);
        portal.setDestinationDimension(this.dimensionTo != null ? this.dimensionTo : serverWorld.getRegistryKey());
        portal.setDestination(this.destination);

        portal.setOrientationAndSize(
                getPortalHorizontalVec(state),
                getPortalUpVec(state),
                this.width,
                this.height
        );

        portal.world.spawnEntity(portal);
        PortalManipulation.completeBiWayBiFacedPortal(
                portal,
                (p) -> BackroomsMod.LOGGER.info("Removed " + p),
                (p) -> BackroomsMod.LOGGER.info("Added " + p),
                Portal.entityType
        );
    }

    private Vec3d getPortalOrigin(BlockState state) {
        return getPos().toCenterPos()
                .add(getPortalUpVec(state).multiply((((float) this.height) / 2) - 0.5))
                .add(getPortalHorizontalVec(state).multiply((1 - (this.width % 2)) * 0.5));
    }

    private Vec3d getPortalUpVec(BlockState state) {
        Direction upDir = !state.contains(Properties.FACING) || state.get(Properties.FACING).getAxis().isHorizontal() ? Direction.UP : Direction.NORTH;
        return new Vec3d(upDir.getOffsetX(), upDir.getOffsetY(), upDir.getOffsetZ());
    }

    private Vec3d getPortalHorizontalVec(BlockState state) {
        Vec3i facing = state.contains(Properties.FACING) ? state.get(Properties.FACING).getVector() : Direction.SOUTH.getVector();
        return getPortalUpVec(state).crossProduct(new Vec3d(facing.getX(), facing.getY(), facing.getZ()));
    }

}
