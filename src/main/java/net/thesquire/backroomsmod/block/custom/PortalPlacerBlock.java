package net.thesquire.backroomsmod.block.custom;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.thesquire.backroomsmod.block.ModBlockEntities;
import net.thesquire.backroomsmod.block.entity.PortalPlacerBlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class PortalPlacerBlock extends BlockWithEntity {

    public static final DirectionProperty FACING = Properties.FACING;

    ////////////////////////////////////////////////////////////////////////

    public PortalPlacerBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(FACING, Direction.SOUTH));
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) { return BlockRenderType.INVISIBLE; }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new PortalPlacerBlockEntity(pos, state);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return getDefaultState().with(FACING, ctx.getPlayerLookDirection().getOpposite());
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return !world.isClient() ? checkType(type, ModBlockEntities.PORTAL_PLACER,
                (world1, pos, state1, blockEntity) -> blockEntity.tick(world1, state1)) : null;
    }

    @SuppressWarnings("deprecation")
    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @SuppressWarnings("deprecation")
    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    @SuppressWarnings("deprecation")
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        Optional<PortalPlacerBlockEntity> optional = world.getBlockEntity(pos, ModBlockEntities.PORTAL_PLACER);
        if(world.isClient() || hand == Hand.OFF_HAND || optional.isEmpty()) return ActionResult.PASS;

        optional.get().initPortal((ServerWorld) world, world.getBlockState(pos));
        return ActionResult.SUCCESS;
    }

}
