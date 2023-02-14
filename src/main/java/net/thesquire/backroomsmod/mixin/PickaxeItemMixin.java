package net.thesquire.backroomsmod.mixin;

import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.PickaxeItem;
import net.minecraft.util.ActionResult;
import net.thesquire.backroomsmod.util.mixin.callback.IPickaxeItemCallback;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public abstract class PickaxeItemMixin {

    @Inject(method = "useOnBlock", at = @At("HEAD"), cancellable = true)
    public void onUseOnBlock(ItemUsageContext context, CallbackInfoReturnable<ActionResult> ci) {
        if(((Object) this) instanceof PickaxeItem)
            IPickaxeItemCallback.EVENT.invoker().interact(context, ci);
    }

}
