package dwarven_forge.entity;

import dwarven_forge.DwarvenForge_Registries;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.function.Supplier;

public class DwarvenForge_EntityAttributes {
    public static DeferredHolder<Attribute, Attribute> GENERIC_MAGIC_RESISTANCE;
    public static DeferredHolder<Attribute, Attribute> GENERIC_EXPLOSION_RESISTANCE;

    public static void initEntityAttributes(){
        GENERIC_MAGIC_RESISTANCE = register(
                "generic.magic_resistance",
                ()-> new RangedAttribute("attribute.name.generic.magic_resistance",
                        //Default
                        0.0,
                        //Min
                        0.0,
                        //Max
                        100.0)
                        .setSyncable(true)
        );

        GENERIC_EXPLOSION_RESISTANCE = register(
                "generic.explosion_resistance",
                ()-> new RangedAttribute("attribute.name.generic.explosion_resistance",
                        //Default
                        0.0,
                        //Min
                        0.0,
                        //Max
                        100.0)
                        .setSyncable(true)
        );
    }

    private static DeferredHolder<Attribute, Attribute> register(String id, Supplier<Attribute> attribute) {
        return DwarvenForge_Registries.ATTRIBUTES.register(id, attribute);
    }
}
