package dwarven_forge.recipes;

import dwarven_forge.item.RuneItem;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.LinkedHashMap;
import java.util.Map;

// Recipe datagen moved from Consumer<FinishedRecipe> (FinishedRecipe is gone) to RecipeOutput,
// which conveniently also hands out a fresh Advancement.Builder per recipe via advancement()
// (see vanilla's SmithingTransformRecipeBuilder, which this mirrors).
public class RuneSmithingRecipeBuilder {
    private final RecipeCategory category;
    private final Ingredient rune;
    private final Ingredient base;
    private final Ingredient addition;
    private final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();

    public RuneSmithingRecipeBuilder(RecipeCategory category, Ingredient rune, Ingredient base, Ingredient addition) {
        this.category = category;
        this.rune = rune;
        this.base = base;
        this.addition = addition;
    }

    public static RuneSmithingRecipeBuilder smithingRune(Ingredient rune, Ingredient base, RecipeCategory category) {
        // The addition ingredient used to be derived dynamically at parse time (fromJson/fromNetwork)
        // rather than stored in the recipe JSON. The new codec-based serializer reads/writes all three
        // ingredients symmetrically, so it's computed once here instead, at datagen time - same result,
        // since the rune item alone always determines it.
        Ingredient addition;
        if (rune.getItems().length > 0 && rune.getItems()[0].getItem() instanceof RuneItem runeItem) {
            addition = Ingredient.of(runeItem.getAddition());
        } else {
            addition = Ingredient.of(Items.GOLD_NUGGET);
        }

        return new RuneSmithingRecipeBuilder(category, rune, base, addition);
    }

    public RuneSmithingRecipeBuilder unlocks(String name, Criterion<?> criterion) {
        this.criteria.put(name, criterion);

        return this;
    }

    public void save(RecipeOutput recipeOutput, ResourceLocation location) {
        this.ensureValid(location);

        Advancement.Builder advancement = recipeOutput.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(location))
                .rewards(AdvancementRewards.Builder.recipe(location))
                .requirements(AdvancementRequirements.Strategy.OR);
        this.criteria.forEach(advancement::addCriterion);

        RuneSmithingRecipe recipe = new RuneSmithingRecipe(this.rune, this.base, this.addition);

        recipeOutput.accept(location, recipe, advancement.build(location.withPrefix("recipes/" + this.category.getFolderName() + "/")));
    }

    private void ensureValid(ResourceLocation location) {
        if (this.criteria.isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + location);
        }
    }
}
