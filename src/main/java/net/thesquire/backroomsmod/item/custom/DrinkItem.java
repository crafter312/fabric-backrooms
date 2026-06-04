package net.thesquire.backroomsmod.item.custom;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

public class DrinkItem extends Item {
    public DrinkItem(Settings settings) {
        super(settings);
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.DRINK;
    }

    @Override
    public SoundEvent getEatSound() {
        return getDrinkSound();
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        user.eatFood(world, stack);

        PlayerEntity playerEntity = user instanceof PlayerEntity ? (PlayerEntity) user : null;
        Item remainder = getRecipeRemainder();
        if ((playerEntity == null || !playerEntity.getAbilities().creativeMode) && remainder != null) {
            if(stack.isEmpty()) return new ItemStack(remainder);
            else if(playerEntity != null) playerEntity.getInventory().offerOrDrop(new ItemStack(remainder));
        }

        return stack;
    }
}
