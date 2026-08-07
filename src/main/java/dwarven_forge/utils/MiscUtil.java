package dwarven_forge.utils;

import dwarven_forge.item.armor.DwarvenArmorItem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class MiscUtil {

    public static float getArmorMagicResistance(LivingEntity entity){
        float magicResistance=0.0f;

        for(ItemStack stack: entity.getArmorSlots()){

            if(stack.getItem() instanceof DwarvenArmorItem){
                magicResistance+=0.1f;
            }
        }

        return magicResistance;
    }

    public static float getArmorExplosionResistance(LivingEntity entity){
        float magicResistance=0.0f;

        for(ItemStack stack: entity.getArmorSlots()){

            if(stack.getItem() instanceof DwarvenArmorItem){
                magicResistance+=0.15f;
            }
        }

        return magicResistance;
    }
}
