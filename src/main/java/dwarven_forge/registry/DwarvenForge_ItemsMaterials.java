package dwarven_forge.registry;

import dwarven_forge.DwarvenForgeMain;
import dwarven_forge.DwarvenForge_Registries;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.SimpleTier;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.EnumMap;
import java.util.List;

public class DwarvenForge_ItemsMaterials {

    // Durability values kept identical to the Forge 1.20.1 mod, where they were derived as
    // (per-type base health) * (durabilityMultiplier=40): boots=13, leggings=15, chestplate=16, helmet=11.
    public static final int DWARVEN_ARMOR_DURABILITY_BOOTS = 13 * 40;
    public static final int DWARVEN_ARMOR_DURABILITY_LEGGINGS = 15 * 40;
    public static final int DWARVEN_ARMOR_DURABILITY_CHESTPLATE = 16 * 40;
    public static final int DWARVEN_ARMOR_DURABILITY_HELMET = 11 * 40;

    // Tool tier is not a registry - it's a plain instance, same as in the Forge version.
    public static final Tier DWARVEN_TOOL_TIER = new SimpleTier(
            Tags.Blocks.NEEDS_NETHERITE_TOOL,
            2500,
            9.0F,
            4.0F,
            30,
            () -> Ingredient.of(DwarvenForge_Items.DWARVEN_METAL_INGOT.get())
    );

    // Armor material became a registry object in 1.20.5+, so it's registered through a DeferredRegister.
    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> DWARVEN_ARMOR_MATERIAL = DwarvenForge_Registries.ARMOR_MATERIALS.register(
            "dwarven",
            () -> new ArmorMaterial(
                    Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                        map.put(ArmorItem.Type.BOOTS, 3);
                        map.put(ArmorItem.Type.LEGGINGS, 6);
                        map.put(ArmorItem.Type.CHESTPLATE, 8);
                        map.put(ArmorItem.Type.HELMET, 3);
                    }),
                    25,
                    SoundEvents.ARMOR_EQUIP_NETHERITE,
                    () -> Ingredient.of(DwarvenForge_Items.DWARVEN_METAL_INGOT.get()),
                    List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(DwarvenForgeMain.MOD_ID, "dwarven"))),
                    0.0F,
                    0.0F
            )
    );

    // DWARVEN_ARMOR_MATERIAL is only added to the ARMOR_MATERIALS DeferredRegister when this
    // class is first loaded (its registration happens in a static field initializer). Items
    // only reference DwarvenForge_ItemsMaterials from inside a registration *supplier* lambda,
    // which NeoForge doesn't invoke until the ITEM registry's RegisterEvent fires - by then the
    // ARMOR_MATERIAL registry's own RegisterEvent has already closed. Calling this from
    // DwarvenForgeMain's constructor forces the class (and its registration) to load early.
    public static void touch(){}
}
