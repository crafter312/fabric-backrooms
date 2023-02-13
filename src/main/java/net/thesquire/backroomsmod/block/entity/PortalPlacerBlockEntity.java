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
import net.minecraft.world.gen.structure.Structure;
import net.thesquire.backroomsmod.BackroomsMod;
import net.thesquire.backroomsmod.block.ModBlockEntities;
import net.thesquire.backroomsmod.portal.util.PortalUtils;
import net.thesquire.backroomsmod.util.ModUtils;
import qouteall.imm_ptl.core.portal.Portal;
import qouteall.imm_ptl.core.portal.PortalManipulation;
import qouteall.q_misc_util.Helper;
import qouteall.q_misc_util.dimension.DimId;

import java.util.Optional;

/**
 * For the time being, this block entity assumes that there is no scale transformation between the two dimensions.
 * Additionally, if creating a vertical portal, please don't change the facing direction of the block when placing
 * it! The BlockEntity uses the block's default facing direction as reference when determining how much to rotate
 * the destination structure when placing it.
 */
public class PortalPlacerBlockEntity extends BlockEntity {

    private RegistryKey<World> dimensionTo = null;
    private int width = 10;
    private int height = 4;
    private Vec3d origin;
    private boolean isMiddlePortal = false;
    private Double destinationX = null;
    private Double destinationY = null;
    private Double destinationZ = null;
    private BlockState replacementState = Blocks.AIR.getDefaultState();
    private Vec3i destStructureOffset = new Vec3i(0, 0, 0);

    // registry key of a structure naturally generating in the destination dimension
    private RegistryKey<Structure> structureKey = null;

    // string describing the file path of the destination structure nbt file
    // e.g. "backroomsmod:level_1/level_1_portal_destination_8w"
    private String destStructureNbtPath = null;

    // the portal is not stored as nbt, it's just used to get the destination world for extra structure placement
    private Portal portal = null;

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

        ServerWorld serverWorld = (ServerWorld) world;

        // update portal origin to account for changed variables like isMiddlePortal
        this.origin = getPortalOrigin(state);

        boolean success = initPortal(serverWorld, state);
        if(success) {
            placeDestStructure(serverWorld, state);
            world.setBlockState(getPos(), this.replacementState, Block.NOTIFY_ALL);
            world.removeBlockEntity(this.pos);
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        if(this.dimensionTo != null)
            DimId.putWorldId(nbt, "dimensionTo", this.dimensionTo);
        nbt.putInt("width", this.width);
        nbt.putInt("height", this.height);
        nbt.putBoolean("isMiddlePortal", this.isMiddlePortal);
        if(this.destinationX != null)
            nbt.putDouble("destinationX", this.destinationX);
        if(this.destinationY != null)
            nbt.putDouble("destinationY", this.destinationY);
        if(this.destinationZ != null)
            nbt.putDouble("destinationZ", this.destinationZ);
        nbt.put("replacementState", NbtHelper.fromBlockState(this.replacementState));
        Helper.putVec3i(nbt, "destStructureOffset", this.destStructureOffset);
        if(this.structureKey != null)
            nbt.putString("structureKey", this.structureKey.getValue().toString());
        if(this.destStructureNbtPath != null)
            nbt.putString("destStructureNbtPath", this.destStructureNbtPath);
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
        if(nbt.contains("isMiddlePortal"))
            this.isMiddlePortal = nbt.getBoolean("isMiddlePortal");
        if(nbt.contains("destinationX"))
            this.destinationX = nbt.getDouble("destinationX");
        if(nbt.contains("destinationY"))
            this.destinationY = nbt.getDouble("destinationY");
        if(nbt.contains("destinationZ"))
            this.destinationZ = nbt.getDouble("destinationZ");
        if(nbt.contains("replacementState"))
            this.replacementState = ModUtils.blockStateFromNbt(nbt.getCompound("replacementState"));
        this.destStructureOffset = ModUtils.getVec3iComponents(nbt, "destStructureOffset", this.destStructureOffset);
        if(nbt.contains("structureKey"))
            this.structureKey = ModUtils.structureIdToKey(nbt.getString("structureKey"));
        if(nbt.contains("destStructureNbtPath"))
            this.destStructureNbtPath = nbt.getString("destStructureNbtPath");

        // read only nbt value to disable portal creation upon placement by setblock command or similar method
        if(nbt.contains("active"))
            this.active = nbt.getBoolean("active");
    }

    public boolean initPortal(ServerWorld serverWorld, BlockState state) {
        this.portal = Portal.entityType.create(serverWorld);
        if(this.portal == null)
            return false;

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

        boolean spawned = this.portal.world.spawnEntity(this.portal);
        if(!spawned) {
            BackroomsMod.LOGGER.warn("Failed to spawn portal at " + this.origin);
            return false;
        }
        PortalManipulation.completeBiWayBiFacedPortal(
                this.portal,
                (p) -> BackroomsMod.LOGGER.info("Removed " + p),
                (p) -> {},
                Portal.entityType
        );
        return true;
    }

    public void placeDestStructure(ServerWorld serverWorld, BlockState state) {
        if(this.structureKey == null || this.destStructureNbtPath == null) return;

        Optional<BlockPos> optional = ModUtils.findStructure(serverWorld, this.getPos(), this.structureKey);
        if(optional.isPresent()) {
            float angle;
            try {
                angle = PortalUtils.getAngle(state.getBlock().getDefaultState().get(Properties.FACING), state.get(Properties.FACING));
            }
            catch (IllegalStateException illegalStateException) {
                angle = 0;
            }

            ModUtils.placeStructure(
                    (ServerWorld) this.portal.getDestinationWorld(),
                    this.destStructureNbtPath,
                    optional.get().add(
                            this.destStructureOffset.getX(),
                            this.pos.getY() + this.destStructureOffset.getY(),
                            this.destStructureOffset.getZ()),
                    (int) angle);
        }
    }

    private Vec3d getPortalOrigin(BlockState state) {
        return getPos().toCenterPos()
                .add(getPortalUpVec(state).multiply((((float) this.height) / 2) - 0.5))
                .add(getPortalHorizontalVec(state).multiply((1 - (this.width % 2)) * 0.5))
                .add(ModUtils.vec3itod(getFacingOrDefault(state)).multiply(ModUtils.boolToFloat(this.isMiddlePortal, 0, 0.5f)));
    }

    private Vec3d getPortalUpVec(BlockState state) {
        Direction upDir = !state.contains(Properties.FACING) || state.get(Properties.FACING).getAxis().isHorizontal() ? Direction.UP : Direction.NORTH;
        return new Vec3d(upDir.getOffsetX(), upDir.getOffsetY(), upDir.getOffsetZ());
    }

    private Vec3d getPortalHorizontalVec(BlockState state) {
        Vec3i facing = getFacingOrDefault(state);
        return getPortalUpVec(state).crossProduct(new Vec3d(facing.getX(), facing.getY(), facing.getZ()));
    }

    private Vec3i getFacingOrDefault(BlockState state) {
        return state.contains(Properties.FACING) ? state.get(Properties.FACING).getVector() : Direction.SOUTH.getVector();
    }

}
