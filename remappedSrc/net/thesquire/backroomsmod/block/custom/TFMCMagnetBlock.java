package net.thesquire.backroomsmod.block.custom;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import reborncore.api.ToolManager;
import techreborn.init.ModSounds;

import java.util.List;

public class TFMCMagnetBlock extends PillarBlock {

    public TFMCMagnetBlock() {
        super(FabricBlockSettings.of(Material.METAL).strength(2f).sounds(BlockSoundGroup.METAL));
    }

    @SuppressWarnings("deprecation")
    @Override
    public ActionResult onUse(BlockState state, World worldIn, BlockPos pos, PlayerEntity playerIn,
                              Hand hand, BlockHitResult hitResult) {

        ItemStack tool = playerIn.getStackInHand(Hand.MAIN_HAND);
        if (tool.isEmpty()) return ActionResult.PASS;
        if (!ToolManager.INSTANCE.canHandleTool(tool)) return ActionResult.PASS;

        if (ToolManager.INSTANCE.handleTool(tool, pos, worldIn, playerIn, hitResult.getSide(), false)) {
            if (!playerIn.isSneaking()) return ActionResult.PASS;
            ItemStack drop = new ItemStack(this);
            dropStack(worldIn, pos, drop);
            worldIn.playSound(null, playerIn.getX(), playerIn.getY(), playerIn.getZ(), ModSounds.BLOCK_DISMANTLE,
                    SoundCategory.BLOCKS, 0.6F, 1F);
            if (!worldIn.isClient) {
                worldIn.setBlockState(pos, Blocks.AIR.getDefaultState(), 2);
            }
            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable BlockView worldIn, List<Text> tooltip, TooltipContext flagIn) {
        super.appendTooltip(stack, worldIn, tooltip, flagIn);
        tooltip.add(new TranslatableTextContent("block.backroomsmod.tfmc_magnet.tooltip_1"));
        tooltip.add(new TranslatableTextContent("block.backroomsmod.tfmc_magnet.tooltip_2"));
    }

}
