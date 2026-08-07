package dwarven_forge.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import dwarven_forge.item.RuneItem;
import dwarven_forge.registry.DwarvenForge_Items;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ItemStack.class)
public class ItemStackMixin {

    @Unique
    ItemStack THIS = (ItemStack) (Object) this;

    // The old Forge mixin targeted two separate hasTag() calls inside getTooltipLines by ordinal -
    // that method (and ItemStack's NBT tag entirely) no longer exists. Instead we inject right
    // after the vanilla call to Item#appendHoverText, which is the one line in getTooltipLines
    // that's guaranteed to run for every item and gives us the same mutable `list` to append to.
    @SuppressWarnings("all")
    @Inject(method = "getTooltipLines", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/item/Item;appendHoverText(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/Item$TooltipContext;Ljava/util/List;Lnet/minecraft/world/item/TooltipFlag;)V",
            shift = At.Shift.AFTER))
    private void addDwarvenTooltips(Item.TooltipContext tooltipContext, Player player, TooltipFlag tooltipFlag,
                                     CallbackInfoReturnable<List<Component>> cir, @Local List<Component> list){

        if(THIS.is(DwarvenForge_Items.DWARVEN_AXE.get()))
            list.add(CommonComponents.space().append(Component.translatable("tooltip.dwarven_forge.armor_penetration", 25))
                .withStyle(ChatFormatting.DARK_GREEN));

        CompoundTag customTag = THIS.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        if(customTag.contains("Runes")){
            runesNamer(list, customTag.getCompound("Runes"));
        }
    }

    @Unique
    private void runesNamer(List<Component> list, CompoundTag runesTag){
        list.add(CommonComponents.EMPTY);

        list.add(Component.translatable("tooltip.item.dwarven_forge.runes").withStyle(ChatFormatting.AQUA));

        for(String runeName: runesTag.getAllKeys()){
            CompoundTag runeTag = runesTag.getCompound(runeName);

            Item rune = BuiltInRegistries.ITEM.get(ResourceLocation.parse(runeTag.getString("Item")));

            if(rune instanceof RuneItem runeItem){
                MutableComponent RuneName = Component.translatable("tooltip.dwarven_forge.rune."+runeItem.getRuneTooltip()).withStyle(ChatFormatting.GRAY);

                list.add(CommonComponents.space().append(RuneName));
            }
        }

        //To put an [Empty] to available slots
        if(THIS.getItem() instanceof ArmorItem armor){
            int defense = armor.getMaterial().value().defense().get(ArmorItem.Type.CHESTPLATE);
            int enchantmentValue = armor.getEnchantmentValue();

            int runeSlots=(defense/4)+(enchantmentValue/25);

            int runesSetSize = runesTag.getAllKeys().size();
            if(runesSetSize < runeSlots){

                for (int i=0; i<(runeSlots-runesSetSize) ;i++){
                    MutableComponent RuneName = Component.translatable("tooltip.item.dwarven_forge.empty").withStyle(ChatFormatting.GRAY);

                    list.add(CommonComponents.space().append(RuneName));
                }

            }

        }

    }
}
