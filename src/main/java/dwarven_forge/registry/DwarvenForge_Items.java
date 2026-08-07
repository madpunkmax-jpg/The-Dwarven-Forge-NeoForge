package dwarven_forge.registry;

import dwarven_forge.DwarvenForgeMain;
import dwarven_forge.DwarvenForge_Registries;
import dwarven_forge.item.RuneItem;
import dwarven_forge.item.armor.DwarvenArmorItem;
import dwarven_forge.registry.DwarvenForge_Entities;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.*;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.function.Supplier;

public class DwarvenForge_Items {

    public static DeferredItem<Item> DWARVEN_METAL_INGOT;

    public static DeferredItem<Item> DWARVEN_HELMET;
    public static DeferredItem<Item> DWARVEN_CHESTPLATE;
    public static DeferredItem<Item> DWARVEN_LEGGINGS;
    public static DeferredItem<Item> DWARVEN_BOOTS;

    public static DeferredItem<Item> DWARVEN_SWORD;
    public static DeferredItem<Item> DWARVEN_PICKAXE;
    public static DeferredItem<Item> DWARVEN_AXE;
    public static DeferredItem<Item> DWARVEN_SHOVEL;
    public static DeferredItem<Item> DWARVEN_HOE;
    public static DeferredItem<Item> DWARF_SPAWN_EGG;

    public static DeferredItem<Item> RUNE_ARMOR_LESSER;
    public static DeferredItem<Item> RUNE_ARMOR;
    public static DeferredItem<Item> RUNE_ARMOR_GREATER;

    public static DeferredItem<Item> RUNE_TOUGHNESS_LESSER;
    public static DeferredItem<Item> RUNE_TOUGHNESS;
    public static DeferredItem<Item> RUNE_TOUGHNESS_GREATER;

    public static DeferredItem<Item> RUNE_SPEED_LESSER;
    public static DeferredItem<Item> RUNE_SPEED;
    public static DeferredItem<Item> RUNE_SPEED_GREATER;

    public static DeferredItem<Item> RUNE_STRENGTH_LESSER;
    public static DeferredItem<Item> RUNE_STRENGTH;
    public static DeferredItem<Item> RUNE_STRENGTH_GREATER;

    public static DeferredItem<Item> RUNE_HEALTH_LESSER;
    public static DeferredItem<Item> RUNE_HEALTH;
    public static DeferredItem<Item> RUNE_HEALTH_GREATER;


    public static void initItems() {
        DWARVEN_METAL_INGOT=registerItem("dwarven_metal_ingot", () -> new Item(new Item.Properties()));

        DWARVEN_HELMET=registerItem("dwarven_helmet", () -> new DwarvenArmorItem(DwarvenForge_ItemsMaterials.DWARVEN_ARMOR_MATERIAL, ArmorItem.Type.HELMET,
                new Item.Properties().rarity(Rarity.UNCOMMON).durability(DwarvenForge_ItemsMaterials.DWARVEN_ARMOR_DURABILITY_HELMET)));
        DWARVEN_CHESTPLATE=registerItem("dwarven_chestplate", () -> new DwarvenArmorItem(DwarvenForge_ItemsMaterials.DWARVEN_ARMOR_MATERIAL, ArmorItem.Type.CHESTPLATE,
                new Item.Properties().rarity(Rarity.UNCOMMON).durability(DwarvenForge_ItemsMaterials.DWARVEN_ARMOR_DURABILITY_CHESTPLATE)));
        DWARVEN_LEGGINGS=registerItem("dwarven_leggings", () -> new DwarvenArmorItem(DwarvenForge_ItemsMaterials.DWARVEN_ARMOR_MATERIAL, ArmorItem.Type.LEGGINGS,
                new Item.Properties().rarity(Rarity.UNCOMMON).durability(DwarvenForge_ItemsMaterials.DWARVEN_ARMOR_DURABILITY_LEGGINGS)));
        DWARVEN_BOOTS=registerItem("dwarven_boots", () -> new DwarvenArmorItem(DwarvenForge_ItemsMaterials.DWARVEN_ARMOR_MATERIAL, ArmorItem.Type.BOOTS,
                new Item.Properties().rarity(Rarity.UNCOMMON).durability(DwarvenForge_ItemsMaterials.DWARVEN_ARMOR_DURABILITY_BOOTS)));

        DWARVEN_SWORD=registerItem("dwarven_sword", ()-> new SwordItem(DwarvenForge_ItemsMaterials.DWARVEN_TOOL_TIER,
                new Item.Properties().rarity(Rarity.UNCOMMON).attributes(SwordItem.createAttributes(DwarvenForge_ItemsMaterials.DWARVEN_TOOL_TIER, 4, -2.6f))));
        DWARVEN_PICKAXE=registerItem("dwarven_pickaxe", ()-> new PickaxeItem(DwarvenForge_ItemsMaterials.DWARVEN_TOOL_TIER,
                new Item.Properties().rarity(Rarity.UNCOMMON).attributes(PickaxeItem.createAttributes(DwarvenForge_ItemsMaterials.DWARVEN_TOOL_TIER, 1, -2.8f))));
        DWARVEN_AXE=registerItem("dwarven_axe", ()-> new AxeItem(DwarvenForge_ItemsMaterials.DWARVEN_TOOL_TIER,
                new Item.Properties().rarity(Rarity.UNCOMMON).attributes(AxeItem.createAttributes(DwarvenForge_ItemsMaterials.DWARVEN_TOOL_TIER, 5, -3.0f))));
        DWARVEN_SHOVEL=registerItem("dwarven_shovel", ()-> new ShovelItem(DwarvenForge_ItemsMaterials.DWARVEN_TOOL_TIER,
                new Item.Properties().rarity(Rarity.UNCOMMON).attributes(ShovelItem.createAttributes(DwarvenForge_ItemsMaterials.DWARVEN_TOOL_TIER, 1.5f, -3.0f))));
        DWARVEN_HOE=registerItem("dwarven_hoe", ()-> new HoeItem(DwarvenForge_ItemsMaterials.DWARVEN_TOOL_TIER,
                new Item.Properties().rarity(Rarity.UNCOMMON).attributes(HoeItem.createAttributes(DwarvenForge_ItemsMaterials.DWARVEN_TOOL_TIER, -4, 0))));

        DWARF_SPAWN_EGG = registerItem("dwarf_spawn_egg",
                ()-> new DeferredSpawnEggItem(DwarvenForge_Entities.DWARF, 0x494b5f, 0xc49a84, new Item.Properties()));

        //Runes
        {
            //Strength
            {
                RUNE_STRENGTH_LESSER =registerItem("rune_strength_lesser", ()-> new RuneItem(new Item.Properties(),
                        Attributes.ATTACK_DAMAGE,
                        new AttributeModifier(rune("strength_rune"), 0.02, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL),
                        "lesser_strength",
                        Items.DIAMOND,
                        0,
                        0)
                );

                RUNE_STRENGTH =registerItem("rune_strength", ()-> new RuneItem(new Item.Properties(),
                        Attributes.ATTACK_DAMAGE,
                        new AttributeModifier(rune("strength_rune"), 0.03, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL),
                        "strength",
                        Items.DIAMOND,
                        0,
                        1)
                );

                RUNE_STRENGTH_GREATER =registerItem("rune_strength_greater", ()-> new RuneItem(new Item.Properties(),
                        Attributes.ATTACK_DAMAGE,
                        new AttributeModifier(rune("strength_rune"), 0.05, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL),
                        "greater_strength",
                        Items.DIAMOND,
                        0,
                        2)
                );
            }

            //Speed
            {
                RUNE_SPEED_LESSER =registerItem("rune_speed_lesser", ()-> new RuneItem(new Item.Properties(),
                        Attributes.MOVEMENT_SPEED,
                        new AttributeModifier(rune("speed_rune"), 0.02, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL),
                        "lesser_speed",
                        Items.AMETHYST_SHARD,
                        1,
                        0)
                );

                RUNE_SPEED =registerItem("rune_speed", ()-> new RuneItem(new Item.Properties(),
                        Attributes.MOVEMENT_SPEED,
                        new AttributeModifier(rune("speed_rune"), 0.03, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL),
                        "speed",
                        Items.AMETHYST_SHARD,
                        1,
                        1)
                );

                RUNE_SPEED_GREATER =registerItem("rune_speed_greater", ()-> new RuneItem(new Item.Properties(),
                        Attributes.MOVEMENT_SPEED,
                        new AttributeModifier(rune("speed_rune"), 0.05, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL),
                        "greater_speed",
                        Items.AMETHYST_SHARD,
                        1,
                        2)
                );
            }

            //Armor
            {
                RUNE_ARMOR_LESSER =registerItem("rune_armor_lesser", ()-> new RuneItem(new Item.Properties(),
                        Attributes.ARMOR,
                        new AttributeModifier(rune("armor_rune"), 1, AttributeModifier.Operation.ADD_VALUE),
                        "lesser_armor",
                        Items.IRON_INGOT,
                        2,
                        0)
                );

                RUNE_ARMOR =registerItem("rune_armor", ()-> new RuneItem(new Item.Properties(),
                        Attributes.ARMOR,
                        new AttributeModifier(rune("armor_rune"), 2, AttributeModifier.Operation.ADD_VALUE),
                        "armor",
                        Items.IRON_INGOT,
                        2,
                        1)
                );

                RUNE_ARMOR_GREATER =registerItem("rune_armor_greater", ()-> new RuneItem(new Item.Properties(),
                        Attributes.ARMOR,
                        new AttributeModifier(rune("armor_rune"), 3, AttributeModifier.Operation.ADD_VALUE),
                        "greater_armor",
                        Items.IRON_INGOT,
                        2,
                        2)
                );
            }

            //Toughness
            {
                RUNE_TOUGHNESS_LESSER =registerItem("rune_toughness_lesser", ()-> new RuneItem(new Item.Properties(),
                        Attributes.ARMOR_TOUGHNESS,
                        new AttributeModifier(rune("toughness_rune"), 1, AttributeModifier.Operation.ADD_VALUE),
                        "lesser_toughness",
                        Items.GOLD_INGOT,
                        3,
                        0)
                );

                RUNE_TOUGHNESS =registerItem("rune_toughness", ()-> new RuneItem(new Item.Properties(),
                        Attributes.ARMOR_TOUGHNESS,
                        new AttributeModifier(rune("toughness_rune"), 2, AttributeModifier.Operation.ADD_VALUE),
                        "toughness",
                        Items.GOLD_INGOT,
                        3,
                        1)
                );

                RUNE_TOUGHNESS_GREATER =registerItem("rune_toughness_greater", ()-> new RuneItem(new Item.Properties(),
                        Attributes.ARMOR_TOUGHNESS,
                        new AttributeModifier(rune("toughness_rune"), 3, AttributeModifier.Operation.ADD_VALUE),
                        "greater_toughness",
                        Items.GOLD_INGOT,
                        3,
                        2)
                );
            }

            //Health
            {
                RUNE_HEALTH_LESSER =registerItem("rune_health_lesser", ()-> new RuneItem(new Item.Properties(),
                        Attributes.MAX_HEALTH,
                        new AttributeModifier(rune("health_rune"), 1, AttributeModifier.Operation.ADD_VALUE),
                        "lesser_health",
                        Items.ECHO_SHARD,
                        4,
                        0)
                );

                RUNE_HEALTH =registerItem("rune_health", ()-> new RuneItem(new Item.Properties(),
                        Attributes.MAX_HEALTH,
                        new AttributeModifier(rune("health_rune"), 3, AttributeModifier.Operation.ADD_VALUE),
                        "health",
                        Items.ECHO_SHARD,
                        4,
                        1)
                );

                RUNE_HEALTH_GREATER =registerItem("rune_health_greater", ()-> new RuneItem(new Item.Properties(),
                        Attributes.MAX_HEALTH,
                        new AttributeModifier(rune("health_rune"), 5, AttributeModifier.Operation.ADD_VALUE),
                        "greater_health",
                        Items.ECHO_SHARD,
                        4,
                        2)
                );
            }
        }

    }

    private static ResourceLocation rune(String name){
        return ResourceLocation.fromNamespaceAndPath(DwarvenForgeMain.MOD_ID, name);
    }

    public static void initGroupItems(){
        DwarvenForge_Registries.CREATIVE_MODE_TABS.register("main", () -> CreativeModeTab.builder()
                .withTabsBefore(CreativeModeTabs.COMBAT)
                .icon(() -> DWARVEN_METAL_INGOT.get().getDefaultInstance())
                .title(Component.translatable("itemGroup.dwarven_forge.main"))
                .displayItems((parameters, output) -> {
                    //Armor
                    {
                        output.accept(DWARVEN_METAL_INGOT.get());
                        output.accept(DWARVEN_HELMET.get());
                        output.accept(DWARVEN_CHESTPLATE.get());
                        output.accept(DWARVEN_LEGGINGS.get());
                        output.accept(DWARVEN_BOOTS.get());
                    }

                    //Tools
                    {
                        output.accept(DWARVEN_SWORD.get());
                        output.accept(DWARVEN_PICKAXE.get());
                        output.accept(DWARVEN_AXE.get());
                        output.accept(DWARVEN_SHOVEL.get());
                        output.accept(DWARVEN_HOE.get());
                    }

                    output.accept(DWARF_SPAWN_EGG.get());

                    output.accept(RUNE_STRENGTH_LESSER.get());
                    output.accept(RUNE_STRENGTH.get());
                    output.accept(RUNE_STRENGTH_GREATER.get());

                    output.accept(RUNE_SPEED_LESSER.get());
                    output.accept(RUNE_SPEED.get());
                    output.accept(RUNE_SPEED_GREATER.get());

                    output.accept(RUNE_ARMOR_LESSER.get());
                    output.accept(RUNE_ARMOR.get());
                    output.accept(RUNE_ARMOR_GREATER.get());

                    output.accept(RUNE_TOUGHNESS_LESSER.get());
                    output.accept(RUNE_TOUGHNESS.get());
                    output.accept(RUNE_TOUGHNESS_GREATER.get());

                    output.accept(RUNE_HEALTH_LESSER.get());
                    output.accept(RUNE_HEALTH.get());
                    output.accept(RUNE_HEALTH_GREATER.get());

                }).build());
    }

    public static DeferredItem<Item> registerItem(String name, Supplier<Item> item) {
        return DwarvenForge_Registries.ITEMS.register(name, item);
    }
}
