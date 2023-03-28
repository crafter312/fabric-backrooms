package net.thesquire.backroomsmod.block.custom;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

public class ModDoorBlock extends DoorBlock {

    public static final BooleanProperty LOCKED = Properties.LOCKED;

    private final SoundEvent closeSound;
    private final SoundEvent openSound;

    /**
     * Access widened by fabric-transitive-access-wideners-v1 to accessible
     * Access widened by architectury to accessible
     *
     * @param settings block settings
     * @param closeSound door close sound
     * @param openSound door open sound
     */
    public ModDoorBlock(Settings settings, SoundEvent closeSound, SoundEvent openSound) {
        super(settings, closeSound, openSound);
        this.setDefaultState(this.getDefaultState().with(LOCKED, false));
        this.closeSound = closeSound;
        this.openSound = openSound;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (state.get(LOCKED)) {
            player.sendMessage(Text.translatable("door.locked"), true);
            return ActionResult.PASS;
        }
        state = state.cycle(OPEN);
        world.setBlockState(pos, state, Block.NOTIFY_LISTENERS | Block.REDRAW_ON_MAIN_THREAD);
        this.playOpenCloseSound(player, world, pos, state.get(OPEN));
        world.emitGameEvent(player, this.isOpen(state) ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, pos);
        return ActionResult.success(world.isClient);
    }

    private void playOpenCloseSound(@Nullable Entity entity, World world, BlockPos pos, boolean open) {
        world.playSound(entity, pos, open ? this.openSound : this.closeSound, SoundCategory.BLOCKS, 1.0f, world.getRandom().nextFloat() * 0.1f + 0.9f);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(LOCKED);
    }
}
