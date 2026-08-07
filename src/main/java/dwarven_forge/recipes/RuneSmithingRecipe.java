package dwarven_forge.recipes;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dwarven_forge.item.RuneItem;
import dwarven_forge.registry.DwarvenForge_Serializers;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.item.crafting.SmithingRecipeInput;
import org.jetbrains.annotations.NotNull;

public class RuneSmithingRecipe implements SmithingRecipe {
    final Ingredient rune;
    final Ingredient armor;
    final Ingredient addition;

    public RuneSmithingRecipe(Ingredient rune, Ingredient armor, Ingredient addition) {
        this.rune = rune;
        this.armor = armor;
        this.addition = addition;
    }

    @Override
    public boolean isTemplateIngredient(@NotNull ItemStack itemStack) {
        return this.rune.test(itemStack);
    }

    @Override
    public boolean isBaseIngredient(@NotNull ItemStack itemStack) {
        return this.armor.test(itemStack);
    }

    @Override
    public boolean isAdditionIngredient(@NotNull ItemStack itemStack) {
        return this.addition.test(itemStack);
    }

    @Override
    public boolean matches(@NotNull SmithingRecipeInput input, @NotNull net.minecraft.world.level.Level level) {
        return this.rune.test(input.template()) && this.armor.test(input.base()) && this.addition.test(input.addition());
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull SmithingRecipeInput input, HolderLookup.@NotNull Provider registries) {
        ItemStack runeStack = input.template();
        ItemStack armorStackOriginal = input.base();
        //Formula to get Rune Slots:
        //From the Chestplate:
        //((Armor/4)+(enchantmentValue/25)
        //(((528 x 8))/1000)/2 == 2.112 -> Diamond
        //(((592 x 8))/1000)/2 == 2.368 -> Diamond

        int runeSlots=1;
        if(armorStackOriginal.getItem() instanceof ArmorItem armorItem){
            // Note: this intentionally always reads the CHESTPLATE defense value (not the
            // piece's own), matching the original 1.20.1 Forge behavior exactly.
            int defense = armorItem.getMaterial().value().defense().get(ArmorItem.Type.CHESTPLATE);
            int enchantmentValue = armorItem.getEnchantmentValue();

            runeSlots=(defense/4)+(enchantmentValue/25);
        }

        //If it doesn't have any rune slot
        if(runeSlots==0){
            return ItemStack.EMPTY;
        }

        CompoundTag customTag = armorStackOriginal.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();

        //To handle items that have runes already
        if(customTag.contains("Runes")){
            CompoundTag runesTag = customTag.getCompound("Runes");

            //If it has the same amount or more than the available rune slots
            if(runesTag.getAllKeys().size() >= runeSlots){
                return ItemStack.EMPTY;
            }


            if(runeStack.getItem() instanceof RuneItem runeItem) {
                //If it has a rune with the same index
                if (runesTag.contains("Rune"+runeItem.getIndex())) {
                    CompoundTag runeTag = runesTag.getCompound("Rune"+runeItem.getIndex());

                    if (runeTag.contains("Item") && runeTag.contains("Index") && runeTag.contains("Level")) {
                        String runeID = runeTag.getString("Item");
                        int runeIndex = runeTag.getInt("Index");
                        int runeLevelTag = runeTag.getInt("Level");

                        //If tries to apply the exactly same rune twice, doesn't let you
                        if (BuiltInRegistries.ITEM.getKey(runeStack.getItem()).toString().equals(runeID)) {
                            return ItemStack.EMPTY;
                        }
                        //If it's the same kind of rune but is a greater level, it let you override the rune
                        else if (runeIndex == runeItem.getIndex() && runeLevelTag < runeItem.level) {
                            ItemStack armorStackFinal = armorStackOriginal.copy();
                            armorStackFinal.setCount(1);

                            RuneItem.setRuneTag(armorStackFinal, runeStack);

                            return armorStackFinal;
                        } else {
                            return ItemStack.EMPTY;
                        }
                    }
                }

            }
        }


        if (armorStackOriginal.getItem() instanceof ArmorItem) {
            ItemStack armorStackFinal = armorStackOriginal.copy();
            armorStackFinal.setCount(1);

            RuneItem.setRuneTag(armorStackFinal, runeStack);

            return armorStackFinal;
        }

        return ItemStack.EMPTY;
    }

    @Override
    public @NotNull ItemStack getResultItem(HolderLookup.@NotNull Provider registries) {
        return new ItemStack(Items.IRON_CHESTPLATE);
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return DwarvenForge_Serializers.RUNE_SMITHING.get();
    }

    public static class Serializer implements RecipeSerializer<RuneSmithingRecipe> {
        public static final MapCodec<RuneSmithingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Ingredient.CODEC.fieldOf("rune").forGetter(recipe -> recipe.rune),
                Ingredient.CODEC.fieldOf("base").forGetter(recipe -> recipe.armor),
                Ingredient.CODEC.fieldOf("addition").forGetter(recipe -> recipe.addition)
        ).apply(instance, RuneSmithingRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, RuneSmithingRecipe> STREAM_CODEC = StreamCodec.composite(
                Ingredient.CONTENTS_STREAM_CODEC, recipe -> recipe.rune,
                Ingredient.CONTENTS_STREAM_CODEC, recipe -> recipe.armor,
                Ingredient.CONTENTS_STREAM_CODEC, recipe -> recipe.addition,
                RuneSmithingRecipe::new
        );

        public Serializer() {
        }

        @Override
        public @NotNull MapCodec<RuneSmithingRecipe> codec() {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, RuneSmithingRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
