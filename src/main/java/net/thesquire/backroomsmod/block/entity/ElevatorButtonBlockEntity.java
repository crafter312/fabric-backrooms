package net.thesquire.backroomsmod.block.entity;

import com.google.common.collect.ImmutableList;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.thesquire.backroomsmod.BackroomsMod;
import net.thesquire.backroomsmod.block.ModBlockEntities;
import net.thesquire.backroomsmod.util.ModUtils;
import qouteall.imm_ptl.core.commands.PortalCommand;
import qouteall.imm_ptl.core.portal.Portal;
import qouteall.imm_ptl.core.portal.PortalExtension;
import qouteall.imm_ptl.core.portal.PortalManipulation;
import qouteall.imm_ptl.core.portal.animation.DeltaUnilateralPortalState;
import qouteall.imm_ptl.core.portal.animation.NormalAnimation;
import qouteall.imm_ptl.core.portal.animation.TimingFunction;
import qouteall.q_misc_util.Helper;

public class ElevatorButtonBlockEntity extends PortalPlacerBlockEntity {

    private Direction portalFacing;
    private Vec3d offset;
    private int durationTicks;

    // the distance the portal is moved when the elevator is triggered
    private double moveDistance;

    // true when the portal is at the top of the elevator, false when it is at the bottom
    private boolean elevatorState;

    public ElevatorButtonBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ELEVATOR_BUTTON, pos, state);
        this.portalFacing = Direction.NORTH;
        this.offset = Vec3d.ZERO;
        this.durationTicks = 40;
        this.moveDistance = 2.1;
        this.elevatorState = true;
    }

    @Override
    public void initPortal(ServerWorld serverWorld, BlockState state) {
        if((this.portal = Portal.entityType.create(serverWorld)) == null) return;

        this.portal.setOriginPos(this.origin.add(this.offset == null ? Vec3d.ZERO : this.offset));
        this.portal.setDestinationDimension(this.dimensionTo != null ? this.dimensionTo : serverWorld.getRegistryKey());
        this.portal.setDestination(new Vec3d(
                this.destinationX != null ? this.destinationX : this.origin.getX(),
                this.destinationY != null ? this.destinationY : this.origin.getY(),
                this.destinationZ != null ? this.destinationZ : this.origin.getZ()
        ));

        this.portal.setOrientationAndSize(
                this.getPortalHorizontalVec(this.portalFacing),
                this.getPortalUpVec(this.portalFacing),
                this.width,
                this.height
        );

        if(!this.portal.getWorld().spawnEntity(this.portal)) {
            BackroomsMod.LOGGER.warn("Failed to spawn portal at " + this.origin);
            return;
        }

        PortalManipulation.completeBiFacedPortal(this.portal, Portal.entityType);
        this.portalUUID = this.portal.getUuid();
    }

    public void toggleElevatorState() {
        this.updateAnimation(this.portal);
        this.playAnimation(this.portal);
        this.elevatorState = !this.elevatorState;
    }

    private void updateAnimation(Portal portal) {
        portal.pauseAnimation();
        portal.clearAnimationDrivers(true, true);

        int moveDir = ModUtils.boolToInt(this.elevatorState, -1, 1);
        DeltaUnilateralPortalState portalDelta = (new DeltaUnilateralPortalState.Builder())
                .offset(new Vec3d(0, this.moveDistance * moveDir, 0)).build();

        NormalAnimation portalAnimation = new NormalAnimation.Builder()
                .phases(ImmutableList.<NormalAnimation.Phase>builder()
                        .add(new NormalAnimation.Phase.Builder()
                                .durationTicks(this.durationTicks)
                                .timingFunction(TimingFunction.sine)
                                .delta(portalDelta)
                                .build())
                        .build())
                .loopCount(1)
                .startingGameTime(portal.getAnimationEffectiveTime())
                .build();

        portal.addThisSideAnimationDriver(portalAnimation);
        portal.addOtherSideAnimationDriver(portalAnimation);
        PortalCommand.reloadPortal(portal);
    }

    private void playAnimation(Portal portal) {
        portal.resumeAnimation();
        PortalExtension.forClusterPortals(portal, Portal::reloadAndSyncToClientNextTick);
    }

    public void onBreak() {
        if(this.portal != null) {
            PortalManipulation.removeConnectedPortals(this.portal, p -> {});
            this.portal.kill();
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        if(this.portalFacing != null)
            Helper.putVec3i(nbt, "portalFacing", this.portalFacing.getVector());
        Helper.putVec3d(nbt, "offset", this.offset);
        nbt.putInt("durationTicks", this.durationTicks);
        nbt.putDouble("moveDistance", this.moveDistance);
        nbt.putBoolean("elevatorState", this.elevatorState);
        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        if(nbt.contains("portalFacingX")) {
            Vec3i vec = Helper.getVec3i(nbt, "portalFacing");
            this.portalFacing = Direction.fromVector(vec.getX(), vec.getY(), vec.getZ());
        }
        this.offset = Helper.getVec3dOptional(nbt, "offset");
        if(nbt.contains("durationTicks"))
            this.durationTicks = nbt.getInt("durationTicks");
        if(nbt.contains("moveDistance"))
            this.moveDistance = nbt.getDouble("moveDistance");
        if(nbt.contains("elevatorState"))
            this.elevatorState = nbt.getBoolean("elevatorState");

        super.readNbt(nbt);
    }

    private Vec3d getPortalUpVec(Direction portalFacing) {
        Direction upDir = portalFacing.getAxis().isHorizontal() ? Direction.UP : Direction.NORTH;
        return new Vec3d(upDir.getOffsetX(), upDir.getOffsetY(), upDir.getOffsetZ());
    }

    private Vec3d getPortalHorizontalVec(Direction portalFacing) {
        Vec3i facingVec = portalFacing.getVector();
        return getPortalUpVec(portalFacing).crossProduct(new Vec3d(facingVec.getX(), facingVec.getY(), facingVec.getZ()));
    }

    @Override
    protected Vec3i getFacingOrDefault(BlockState state) {
        return this.portalFacing == null ? Direction.NORTH.getVector() : this.portalFacing.getVector();
    }

}
