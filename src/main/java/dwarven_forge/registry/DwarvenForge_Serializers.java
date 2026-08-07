package dwarven_forge.registry;

import dwarven_forge.DwarvenForge_Registries;
import dwarven_forge.recipes.RuneSmithingRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.function.Supplier;

public class DwarvenForge_Serializers {


    public static DeferredHolder<RecipeSerializer<?>, RecipeSerializer<?>> RUNE_SMITHING;
    public static void initSerializers(){
        RUNE_SMITHING=register("rune_smithing", RuneSmithingRecipe.Serializer::new);
    }

    public static DeferredHolder<RecipeSerializer<?>, RecipeSerializer<?>> register(String id, Supplier<RecipeSerializer<?>> supplier){
        return DwarvenForge_Registries.SERIALIZERS.register(id, supplier);
    }
}
