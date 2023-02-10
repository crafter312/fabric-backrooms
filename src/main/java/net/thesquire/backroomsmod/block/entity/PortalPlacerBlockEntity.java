package net.thesquire.backroomsmod.block.entity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
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
import qouteall.q_misc_util.dimension.DimId;

// for the time being, this block entity assumes that there is no scale transformation between the two dimensions
public class PortalPlacerBlockEntity extends BlockEntity {

    private RegistryKey<World> dimensionTo = null;
    private int width = 10;
    private int height = 4;
    private final Vec3d origin;
    private Double destinationX = null;
    private Double destinationY = null;
    private Double destinationZ = null;
    private BlockState replacementState = Blocks.AIR.getDefaultState();

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
    }

    public void tick(World world, BlockState state) {
        if(world.isClient() || !this.active || this.dimensionTo == null || !state.contains(Properties.FACING)) return;

        boolean success = initPortal((ServerWorld) world, state);
        if(success)
            world.setBlockState(getPos(), this.replacementState, Block.NOTIFY_ALL);
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        if(this.dimensionTo != null)
            DimId.putWorldId(nbt, "dimensionTo", this.dimensionTo);
        nbt.putInt("width", this.width);
        nbt.putInt("height", this.height);
        if(destinationX != null)
            nbt.putDouble("destinationX", this.destinationX);
        if(destinationY != null)
            nbt.putDouble("destinationY", this.destinationY);
        if(destinationZ != null)
            nbt.putDouble("destinationZ", this.destinationZ);
        nbt.put("replacementState", NbtHelper.fromBlockState(this.replacementState));
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
        if(nbt.contains("destinationX"))
            this.destinationX = nbt.getDouble("destinationX");
        if(nbt.contains("destinationY"))
            this.destinationY = nbt.getDouble("destinationY");
        if(nbt.contains("destinationZ"))
            this.destinationZ = nbt.getDouble("destinationZ");
        if(nbt.contains("replacementState"))
            this.replacementState = ModUtils.blockStateFromNbt(nbt.getCompound("replacementState"));

        // read only nbt value to disable portal creation upon placement by setblock command or similar method
        if(nbt.contains("active"))
            this.active = nbt.getBoolean("active");
    }

    public boolean initPortal(ServerWorld serverWorld, BlockState state) {
        Portal portal = Portal.entityType.create(serverWorld);
        if(portal == null)
            return false;

        portal.setOriginPos(this.origin);
        portal.setDestinationDimension(this.dimensionTo != null ? this.dimensionTo : serverWorld.getRegistryKey());
        portal.setDestination(new Vec3d(
                this.destinationX != null ? this.destinationX : this.origin.getX(),
                this.destinationY != null ? this.destinationY : this.origin.getY(),
                this.destinationZ != null ? this.destinationZ : this.origin.getZ()
        ));

        portal.setOrientationAndSize(
                getPortalHorizontalVec(state),
                getPortalUpVec(state),
                this.width,
                this.height
        );

        boolean spawned = portal.world.spawnEntity(portal);
        if(!spawned) {
            BackroomsMod.LOGGER.warn("Failed to spawn portal at " + this.origin);
            return false;
        }
        PortalManipulation.completeBiWayBiFacedPortal(
                portal,
                (p) -> BackroomsMod.LOGGER.info("Removed " + p),
                (p) -> {},
                Portal.entityType
        );
        return true;
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
