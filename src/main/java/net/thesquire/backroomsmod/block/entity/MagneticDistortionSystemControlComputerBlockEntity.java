package net.thesquire.backroomsmod.block.entity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.PillarBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Pair;
import net.minecraft.util.math.*;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.BlockLocating;
import net.minecraft.world.World;
import net.thesquire.backroomsmod.block.ModBlocks;
import net.thesquire.backroomsmod.config.ModConfig;
import net.thesquire.backroomsmod.dimension.ModDimensionKeys;
import net.thesquire.backroomsmod.portal.teleport.Level0Teleporter;
import net.thesquire.backroomsmod.util.ModUtils;
import net.thesquire.backroomsmod.portal.util.PortalUtils;
import qouteall.imm_ptl.core.portal.Portal;
import qouteall.imm_ptl.core.portal.PortalManipulation;
import reborncore.common.blockentity.MachineBaseBlockEntity;
import reborncore.common.blockentity.MultiblockWriter;
import reborncore.common.blocks.BlockMachineBase;
import reborncore.common.screen.BuiltScreenHandler;
import reborncore.common.screen.BuiltScreenHandlerProvider;
import reborncore.common.screen.builder.ScreenHandlerBuilder;
import reborncore.common.util.RebornInventory;
import techreborn.blockentity.machine.GenericMachineBlockEntity;
import techreborn.init.TRContent;

import java.util.*;

//TODO fix destination portal 1 block offset
//TODO fix non-axis-aligned portals facing opposite direction
//TODO fix crash when loading world with existing destination portal

public class MagneticDistortionSystemControlComputerBlockEntity extends GenericMachineBlockEntity implements BuiltScreenHandlerProvider {

    public static final double portalWidth = 2D;
    public static final double portalHeight = 3D;
    public static final Block frameBlock = ModBlocks.TFMC_MAGNET;
    public static final RegistryKey<World> destDim = ModDimensionKeys.LEVEL_0;

    // true gives a multiplier of 1, false gives a multiplier of -1
    // this will be converted later
    public boolean side;
    public boolean active;

    private final int initEnergyUsage = ModConfig.magneticDistortionSystemControlComputerInitEnergyUsage;
    private final int energyUsage = ModConfig.magneticDistortionSystemControlComputerEnergyUsage;

    private boolean initPortal = true;
    private UUID portalUUID = null;
    private Portal portal;
    private UUID destPortalUUID = null;
    private Portal destPortal = null;
    private float destAngle;
    private Pair<Optional<BlockLocating.Rectangle>, Direction> destPortalInfo;

    public MagneticDistortionSystemControlComputerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.MAGNETIC_DISTORTION_SYSTEM_CONTROL_COMPUTER, pos, state, "MagneticDistortionSystemControlComputer",
                ModConfig.magneticDistortionSystemControlComputerMaxInput, ModConfig.magneticDistortionSystemControlComputerMaxEnergy,
                ModBlocks.MAGNETIC_DISTORTION_SYSTEM_CONTROL_COMPUTER, 0);
        this.inventory = new RebornInventory<>(1, "MagneticDistortionSystemControlComputerBlockEntity", 64, this);
        this.side = false;
        this.active = false;
    }

    private void initPortal(ServerWorld serverWorld, Direction dir) {
        portal = Portal.entityType.create(serverWorld);
        assert portal != null;

        portal.setOriginPos(getPortalOrigin(dir));
        portal.setDestinationDimension(destDim);
        portal.setDestination(PortalUtils.getPortalOrigin(destPortalInfo.getLeft().get(), destPortalInfo.getRight().getAxis()));

        portal.setOrientationAndSize(
                new Vec3d(dir.getOffsetX(), dir.getOffsetY(), dir.getOffsetZ()),
                new Vec3d(0, 1, 0),
                portalWidth,
                portalHeight
        );

        destAngle = PortalUtils.getAngle(getFacing().getOpposite(), destPortalInfo.getRight());
        portal.setRotationTransformation(Vec3f.POSITIVE_Y.getDegreesQuaternion(destAngle));
    }

    @Override
    public BuiltScreenHandler createScreenHandler(int syncID, PlayerEntity player) {
        return new ScreenHandlerBuilder("magneticdistortionsystem").player(player.getInventory()).inventory().hotbar()
                .addInventory().blockEntity(this).energySlot(0, 8, 72).syncEnergyValue()
                .sync(this::getSide, this::setSide).sync(this::getActive, this::setActive).addInventory().create(this, syncID);
    }

    @Override
    public void writeMultiblock(MultiblockWriter writer) {
        Map<Vec3i, BlockState> map = getPortalMultiblock(side, getFacing());
        for(Map.Entry<Vec3i, BlockState> entry : map.entrySet()) {
            Vec3i relPos = entry.getKey();
            writer.add(relPos.getX(), relPos.getY(), relPos.getZ(), entry.getValue());
        }
    }

    @Override
    public boolean isMultiblockValid() {
        MultiblockWriter.MultiblockVerifier verifier = new MultiblockWriter.MultiblockVerifier(this.getPos(), this.getWorld());
        this.writeMultiblock(verifier.rotate(this.getFacing().getOpposite()));
        return verifier.isValid();
    }

    @Override
    public boolean canBeUpgraded() {
        return false;
    }

    @Override
    public void tick(World world, BlockPos pos, BlockState state, MachineBaseBlockEntity blockEntity) {
        super.tick(world, pos, state, blockEntity);

        if(world == null || world.isClient) return;
        ServerWorld serverWorld = (ServerWorld) world;

        Direction portalDir = getSideDir(side);

        assert portalDir != null;
        Vec3d portalOrigin = getPortalOrigin(portalDir);

        // This runs once on first tick to check if the portal is already lit upon loading the world
        if(initPortal) {
            destPortalInfo = Level0Teleporter.findDestinationPortal(
                    Objects.requireNonNull(serverWorld.getServer().getWorld(destDim)),
                    new BlockPos(portalOrigin),
                    frameBlock);
            if (destPortalInfo.getLeft().isEmpty())
                throw new IllegalStateException("Unable to find destination for portal at " + portalOrigin.toString());

            if(active && portalUUID != null) {
                Entity entity = serverWorld.getEntity(portalUUID);
                assert entity != null;
                if(entity.getType().equals(Portal.entityType)) {
                    portal = (Portal) serverWorld.getEntity(portalUUID);
                    // TODO check if portal destination matches destPortalInfo
                    assert portal != null;
                    destPortal = PortalUtils.findRotatedPortal(portal, destAngle);
                }
                else initPortal(serverWorld, portalDir);
            }
            else initPortal(serverWorld, portalDir);

            initPortal = false;
        }

        if(active) {

            // portal deactivation conditions
            if(!isMultiblockValid() || !portal.isAlive()) setActive(false);

            // energy usage
            if(getEnergy() > energyUsage) useEnergy(maxInput / 2);
            else setActive(false);
        }
    }

    @Override
    public void onBreak(World world, PlayerEntity playerEntity, BlockPos blockPos, BlockState blockState) {
        super.onBreak(world, playerEntity, blockPos, blockState);
        if(portal != null && destPortal != null) {
            portal.kill();
            destPortal.kill();
        }
    }

    @Override
    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);
        this.side = tag.getBoolean("side");
        this.active = tag.getBoolean("active");
        if(tag.contains("portalUUID")) this.portalUUID = tag.getUuid("portalUUID");
        if(tag.contains("destPortalUUID")) this.destPortalUUID = tag.getUuid("destPortalUUID");
    }

    @Override
    public void writeNbt(NbtCompound tag) {
        super.writeNbt(tag);
        tag.putBoolean("side", this.side);
        tag.putBoolean("active", this.active);

        if(this.portal !=null) tag.putUuid("portalUUID", this.portal.getUuid());
        if(this.destPortal !=null) tag.putUuid("destPortalUUID", this.destPortal.getUuid());
    }

    public boolean getSide() { return side; }

    public void setSide(boolean side) {
        if(this.side != side) {
//            setActive(false);
            Vec3d newPos = getPortalOrigin(getSideDir(side));
            portal.setOriginPos(newPos);
        }

        this.side = side;
    }

    public boolean getActive() { return active; }

    public void setActive(boolean active) {
        if(!this.active && active && isMultiblockValid() && getEnergy()>initEnergyUsage) activatePortal();
        else if(this.active && !active) deactivatePortal();

        assert world != null;
        world.setBlockState(pos, world.getBlockState(pos).with(BlockMachineBase.ACTIVE, active));
        this.active = active;
    }

    private void activatePortal() {
        if(portal.isRemoved()) portal.myUnsetRemoved();
        portal.world.spawnEntity(portal);

        if(destPortal == null) destPortal = PortalManipulation.completeBiWayPortal(portal, Portal.entityType);
        else {
            destPortal.myUnsetRemoved();
            destPortal.world.spawnEntity(destPortal);
        }
    }

    private void deactivatePortal() {
        portal.kill();
        destPortal.kill();
        portalUUID = null;
        destPortalUUID = null;
    }

    private Direction getSideDir(boolean side) {
        return Direction.fromVector(new BlockPos(getFacing().rotateYCounterclockwise().getVector()
                .multiply(ModUtils.boolToInt(side, 1, -1))));
    }

    // "dir" is a Direction object describing on what side of this block entity the portal is located
    private Vec3d getPortalOrigin(Direction dir) {
        double x = pos.getX();
        double y = pos.getY() + 1 + (portalHeight / 2);
        double z = pos.getZ();

        Vec3d origin = new Vec3d(x, y, z);

        // calculate origin if portal is on positive side of block
        if(dir.getDirection() == Direction.AxisDirection.POSITIVE) {
            origin = origin.add(new Vec3d(dir.getUnitVector()).multiply(2 + (portalWidth / 2)));
            if(getFacing().getAxis() == Direction.Axis.X) origin = origin.add(0.5, 0, 0);
            else origin = origin.add(0, 0, 0.5);

            return origin;
        }

        // calculate origin if portal is on negative side of block
        origin = origin.add(new Vec3d(dir.getUnitVector()).multiply(1 + (portalWidth / 2)));
        if(getFacing().getAxis() == Direction.Axis.X) origin = origin.add(0.5, 0, 0);
        else origin = origin.add(0, 0, 0.5);

        return origin;
    }

    /***************************************************************/

    public static Map<Vec3i, BlockState> getPortalMultiblock(Boolean side, Direction facing) {
        Map<Vec3i, BlockState> map = new HashMap<>();

        BlockState casing = TRContent.MachineBlocks.INDUSTRIAL.casing.getDefaultState();
        BlockState magnetVertical = frameBlock.getDefaultState().with(PillarBlock.AXIS, Direction.Axis.Y);

        Direction.Axis horizontalAxis = (facing.equals(Direction.NORTH) || facing.equals(Direction.SOUTH)) ? Direction.Axis.X : Direction.Axis.Z;
        BlockState magnetHorizontal = frameBlock.getDefaultState().with(PillarBlock.AXIS, horizontalAxis);

        Vec3i dir = Direction.SOUTH.getVector().multiply(-1 + (2 * (side ? 1 : 0)));
        Vec3i up = Direction.UP.getVector();

        // add corner blocks
        map.put(dir, casing);
        map.put(dir.multiply(4), casing);
        map.put(dir.add(up.multiply(4)), casing);
        map.put(dir.multiply(4).add(up.multiply(4)), casing);

        // add horizontal sides
        map.put(dir.multiply(2), magnetHorizontal);
        map.put(dir.multiply(3), magnetHorizontal);
        map.put(up.multiply(4).add(dir.multiply(2)), magnetHorizontal);
        map.put(up.multiply(4).add(dir.multiply(3)), magnetHorizontal);

        // add vertical sides
        map.put(dir.add(up), magnetVertical);
        map.put(dir.add(up.multiply(2)), magnetVertical);
        map.put(dir.add(up.multiply(3)), magnetVertical);
        map.put(dir.multiply(4).add(up), magnetVertical);
        map.put(dir.multiply(4).add(up.multiply(2)), magnetVertical);
        map.put(dir.multiply(4).add(up.multiply(3)), magnetVertical);

        return map;
    }

}
