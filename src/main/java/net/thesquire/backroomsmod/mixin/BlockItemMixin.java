package net.thesquire.backroomsmod.mixin;

import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.ActionResult;
import net.thesquire.backroomsmod.world.activity.DatabaseManager;
import net.thesquire.backroomsmod.world.activity.SectionActivityTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockItem.class)
public abstract class BlockItemMixin extends Item {

    public BlockItemMixin(Settings settings) {
        super(settings);
    }

    @Inject(method = "place(Lnet/minecraft/item/ItemPlacementContext;)Lnet/minecraft/util/ActionResult;",
            at = @At("RETURN"))
    public void onPlace(ItemPlacementContext context, CallbackInfoReturnable<ActionResult> ci) {
        if (ci.getReturnValue().isAccepted() && !context.getWorld().isClient()) {
            SectionActivityTracker.TrackSectionBlocksPlacedData(context);
            DatabaseManager.logBlockPlacement(context);
        }
    }

}
