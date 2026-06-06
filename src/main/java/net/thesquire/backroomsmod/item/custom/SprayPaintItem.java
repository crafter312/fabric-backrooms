package net.thesquire.backroomsmod.item.custom;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.thesquire.backroomsmod.block.ModBlocks;
import net.thesquire.backroomsmod.block.custom.GraffitiBlock;

public class SprayPaintItem extends Item {

    public SprayPaintItem(Settings settings) {
        super(settings);
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 40;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BRUSH;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);

        // Raycast to see what the player is looking at
        HitResult hitResult = user.raycast(5.0D, 0.0F, false);

        if (hitResult.getType() == HitResult.Type.BLOCK) {
            BlockHitResult blockHitResult = (BlockHitResult) hitResult;
            Direction side = blockHitResult.getSide();
            BlockPos wallPos = blockHitResult.getBlockPos();

            // Check if the player is looking at a wall (horizontal direction, side of block is full square)
            if (side.getAxis().isHorizontal() && world.getBlockState(wallPos).isSideSolidFullSquare(world, wallPos, side)) {
                BlockPos targetPos = blockHitResult.getBlockPos().offset(side);

                // Ensure the target space is replaceable (like air)
                if (world.getBlockState(targetPos).canReplace(new ItemPlacementContext(new ItemUsageContext(user, hand, blockHitResult)))) {
                    user.setCurrentHand(hand);

                    // Optional: Play a starting spray sound loop here if desired
                    return TypedActionResult.consume(itemStack);
                }
            }
        }

        return TypedActionResult.fail(itemStack);
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if (!world.isClient() && user instanceof PlayerEntity player) {

            // Re-verify they are still looking at a wall to prevent exploits
            HitResult hitResult = player.raycast(5.0D, 0.0F, false);
            if (hitResult.getType() == HitResult.Type.BLOCK) {
                BlockHitResult blockHitResult = (BlockHitResult) hitResult;
                Direction side = blockHitResult.getSide();
                if (side.getAxis().isHorizontal()) {
                    BlockPos targetPos = blockHitResult.getBlockPos().offset(side);
                    if (world.getBlockState(targetPos).canReplace(new ItemPlacementContext(new ItemUsageContext(player, Hand.MAIN_HAND, blockHitResult)))) {

                        // Place the Graffiti Block
                        BlockState graffitiState = ModBlocks.GRAFFITI.getDefaultState().with(GraffitiBlock.FACING, side);
                        world.setBlockState(targetPos, graffitiState, Block.NOTIFY_ALL);

                        // Play a completion sound (e.g., standard item use or a custom rattle/hiss)
                        world.playSound(null, targetPos, SoundEvents.ITEM_BOTTLE_FILL_DRAGONBREATH, SoundCategory.BLOCKS, 1.0F, 1.0F);

                        // Damage the item by 1 durability
                        stack.damage(1, player, (p) -> p.sendToolBreakStatus(player.getActiveHand()));
                    }
                }
            }
        }
        return stack;
    }

}
