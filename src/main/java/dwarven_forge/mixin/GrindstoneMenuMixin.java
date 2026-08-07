package dwarven_forge.mixin;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.inventory.GrindstoneMenu;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// Note: the old Forge version injected into createResult() and captured its locals (itemSlot1,
// itemSlot2, two booleans) via mixin @Local ordinals. In 1.21.1 createResult() takes no
// parameters and just delegates to computeResult(inputItem, additionalItem), which returns the
// result directly - so there's no more local-variable capturing needed, we just override the
// return value of computeResult itself.
@Mixin(GrindstoneMenu.class)
public class GrindstoneMenuMixin {

    @Inject(method = "computeResult", at = @At("RETURN"), cancellable = true)
    private void removeRunes(ItemStack inputItem, ItemStack additionalItem, CallbackInfoReturnable<ItemStack> cir){

        if(inputItem.getItem() instanceof ArmorItem){
            CompoundTag customTag = inputItem.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();

            if(customTag.contains("Runes")){
                ItemStack result = inputItem.copy();

                CustomData.update(DataComponents.CUSTOM_DATA, result, tag -> tag.remove("Runes"));

                cir.setReturnValue(result);
            }
        }
    }

}
