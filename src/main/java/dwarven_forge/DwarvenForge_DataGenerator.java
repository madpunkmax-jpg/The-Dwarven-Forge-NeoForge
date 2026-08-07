package dwarven_forge;

import dwarven_forge.recipes.RuneSmithingRecipeBuilder;
import dwarven_forge.registry.DwarvenForge_Items;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.tags.BiomeTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.registries.DeferredItem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, modid = DwarvenForgeMain.MOD_ID)
public class DwarvenForge_DataGenerator {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        // Data generators may require some of these as constructor parameters.
        // See below for more details on each of these.
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        generator.addProvider(event.includeClient(), new ItemModelGenerator(output, existingFileHelper));

        generator.addProvider(event.includeServer(), new RecipeGenerator(output, lookupProvider));

        BlockTagGenerator blockTagGenerator = generator.addProvider(event.includeServer(),
                new BlockTagGenerator(output, lookupProvider, existingFileHelper));

        generator.addProvider(event.includeServer(),
                new ItemTagGenerator(output, lookupProvider, blockTagGenerator.contentsGetter(), existingFileHelper));

        generator.addProvider(event.includeServer(),
                new BiomeTagGenerator(output, lookupProvider, existingFileHelper));


        generator.addProvider(event.includeServer(), ChestLootTablesGenerator.create(output, lookupProvider));
    }

    public static class ItemModelGenerator extends ItemModelProvider {

        public ItemModelGenerator(PackOutput output, ExistingFileHelper existingFileHelper) {
            super(output, DwarvenForgeMain.MOD_ID, existingFileHelper);
        }

        @Override
        protected void registerModels() {
            spawnEgg(DwarvenForge_Items.DWARF_SPAWN_EGG);

            simpleItem(DwarvenForge_Items.DWARVEN_METAL_INGOT);

            simpleItem(DwarvenForge_Items.DWARVEN_HELMET);
            simpleItem(DwarvenForge_Items.DWARVEN_CHESTPLATE);
            simpleItem(DwarvenForge_Items.DWARVEN_LEGGINGS);
            simpleItem(DwarvenForge_Items.DWARVEN_BOOTS);

            handheld(DwarvenForge_Items.DWARVEN_SWORD);
            handheld(DwarvenForge_Items.DWARVEN_PICKAXE);
            handheld(DwarvenForge_Items.DWARVEN_AXE);
            handheld(DwarvenForge_Items.DWARVEN_SHOVEL);
            handheld(DwarvenForge_Items.DWARVEN_HOE);

            simpleItem(DwarvenForge_Items.RUNE_STRENGTH_LESSER);
            simpleItem(DwarvenForge_Items.RUNE_STRENGTH);
            simpleItem(DwarvenForge_Items.RUNE_STRENGTH_GREATER);

            simpleItem(DwarvenForge_Items.RUNE_SPEED_LESSER);
            simpleItem(DwarvenForge_Items.RUNE_SPEED);
            simpleItem(DwarvenForge_Items.RUNE_SPEED_GREATER);

            simpleItem(DwarvenForge_Items.RUNE_ARMOR_LESSER);
            simpleItem(DwarvenForge_Items.RUNE_ARMOR);
            simpleItem(DwarvenForge_Items.RUNE_ARMOR_GREATER);

            simpleItem(DwarvenForge_Items.RUNE_TOUGHNESS_LESSER);
            simpleItem(DwarvenForge_Items.RUNE_TOUGHNESS);
            simpleItem(DwarvenForge_Items.RUNE_TOUGHNESS_GREATER);

            simpleItem(DwarvenForge_Items.RUNE_HEALTH_LESSER);
            simpleItem(DwarvenForge_Items.RUNE_HEALTH);
            simpleItem(DwarvenForge_Items.RUNE_HEALTH_GREATER);
        }

        @SuppressWarnings("all")
        private ItemModelBuilder simpleItem(DeferredItem<Item> item){
            return withExistingParent(item.getId().getPath(),
                    ResourceLocation.withDefaultNamespace("item/generated")).texture("layer0",
                    ResourceLocation.fromNamespaceAndPath(DwarvenForgeMain.MOD_ID, "item/"+ item.getId().getPath()));
        }

        @SuppressWarnings("all")
        private ItemModelBuilder handheld(DeferredItem<Item> item){
            return withExistingParent(item.getId().getPath(),
                    ResourceLocation.withDefaultNamespace("item/handheld")).texture("layer0",
                    ResourceLocation.fromNamespaceAndPath(DwarvenForgeMain.MOD_ID, "item/"+ item.getId().getPath()));
        }


        @SuppressWarnings("all")
        private ItemModelBuilder spawnEgg(DeferredItem<Item> item){
            return this.getBuilder(item.getId().getPath())
                    .parent(this.getExistingFile(ResourceLocation.withDefaultNamespace("item/template_spawn_egg")));
        }

    }

    public static class ItemTagGenerator extends ItemTagsProvider {

        public ItemTagGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> completableFuture, CompletableFuture<TagLookup<Block>> future, @Nullable ExistingFileHelper existingFileHelper) {
            super(output, completableFuture, future, DwarvenForgeMain.MOD_ID, existingFileHelper);
        }

        @Override
        protected void addTags(HolderLookup.@NotNull Provider provider) {

            this.tag(ItemTags.AXES)
                    .add(DwarvenForge_Items.DWARVEN_AXE.get());

            this.tag(ItemTags.HOES)
                    .add(DwarvenForge_Items.DWARVEN_HOE.get());

            this.tag(ItemTags.PICKAXES)
                    .add(DwarvenForge_Items.DWARVEN_PICKAXE.get());

            this.tag(ItemTags.SHOVELS)
                    .add(DwarvenForge_Items.DWARVEN_SHOVEL.get());

            this.tag(ItemTags.SWORDS)
                    .add(DwarvenForge_Items.DWARVEN_SWORD.get());

            // ItemTags.TOOLS was removed from vanilla; the equivalent convention tag now lives
            // under the "c" (common) namespace shared with Fabric.
            this.tag(Tags.Items.TOOLS)
                    .add(
                            DwarvenForge_Items.DWARVEN_AXE.get(),
                            DwarvenForge_Items.DWARVEN_HOE.get(),
                            DwarvenForge_Items.DWARVEN_PICKAXE.get(),
                            DwarvenForge_Items.DWARVEN_SHOVEL.get());

            // Forge's "forge:armors" convention tag was unified with Fabric under the "c" namespace.
            this.tag(Tags.Items.ARMORS)
                    .add(
                            DwarvenForge_Items.DWARVEN_HELMET.get(),
                            DwarvenForge_Items.DWARVEN_CHESTPLATE.get(),
                            DwarvenForge_Items.DWARVEN_LEGGINGS.get(),
                            DwarvenForge_Items.DWARVEN_BOOTS.get());
        }
    }

    public static class BlockTagGenerator extends BlockTagsProvider {

        public BlockTagGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
            super(output, lookupProvider, DwarvenForgeMain.MOD_ID, existingFileHelper);
        }

        @Override
        protected void addTags(HolderLookup.@NotNull Provider provider) {

        }
    }

    public static class BiomeTagGenerator extends BiomeTagsProvider {


        public BiomeTagGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> completableFuture, @Nullable ExistingFileHelper existingFileHelper) {
            super(output, completableFuture, DwarvenForgeMain.MOD_ID, existingFileHelper);
        }

        @Override
        protected void addTags(HolderLookup.@NotNull Provider provider) {

            this.tag(TagKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath(DwarvenForgeMain.MOD_ID, "has_structure/dwarven_village")))
                    .addTag(BiomeTags.IS_MOUNTAIN)
                    .addTag(BiomeTags.IS_HILL)

                    .addTag(BiomeTags.IS_FOREST)
                    .addTag(BiomeTags.IS_TAIGA)
                    .addTag(BiomeTags.IS_JUNGLE)

                    .add(Biomes.MUSHROOM_FIELDS)
                    .add(Biomes.ICE_SPIKES)
                    .add(Biomes.WINDSWEPT_SAVANNA)

                    .add(Biomes.DESERT)
                    .add(Biomes.SAVANNA)
                    .add(Biomes.SAVANNA_PLATEAU)
                    .add(Biomes.SUNFLOWER_PLAINS)
                    .add(Biomes.SNOWY_PLAINS)
                    .add(Biomes.PLAINS)

                    .add(Biomes.SWAMP)
                    .add(Biomes.MANGROVE_SWAMP)

                    .add(Biomes.DRIPSTONE_CAVES)
                    .add(Biomes.LUSH_CAVES)

                    .addTag(BiomeTags.IS_BADLANDS);

        }
    }


    public static class RecipeGenerator extends RecipeProvider implements IConditionBuilder {

        public RecipeGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
            super(output, registries);
        }

        @Override
        protected void buildRecipes(@NotNull RecipeOutput recipeOutput) {
            createRuneRecipe(recipeOutput, DwarvenForge_Items.RUNE_STRENGTH_LESSER);
            createRuneRecipe(recipeOutput, DwarvenForge_Items.RUNE_STRENGTH);
            createRuneRecipe(recipeOutput, DwarvenForge_Items.RUNE_STRENGTH_GREATER);

            createRuneRecipe(recipeOutput, DwarvenForge_Items.RUNE_SPEED_LESSER);
            createRuneRecipe(recipeOutput, DwarvenForge_Items.RUNE_SPEED);
            createRuneRecipe(recipeOutput, DwarvenForge_Items.RUNE_SPEED_GREATER);

            createRuneRecipe(recipeOutput, DwarvenForge_Items.RUNE_ARMOR_LESSER);
            createRuneRecipe(recipeOutput, DwarvenForge_Items.RUNE_ARMOR);
            createRuneRecipe(recipeOutput, DwarvenForge_Items.RUNE_ARMOR_GREATER);

            createRuneRecipe(recipeOutput, DwarvenForge_Items.RUNE_TOUGHNESS_LESSER);
            createRuneRecipe(recipeOutput, DwarvenForge_Items.RUNE_TOUGHNESS);
            createRuneRecipe(recipeOutput, DwarvenForge_Items.RUNE_TOUGHNESS_GREATER);

            createRuneRecipe(recipeOutput, DwarvenForge_Items.RUNE_HEALTH_LESSER);
            createRuneRecipe(recipeOutput, DwarvenForge_Items.RUNE_HEALTH);
            createRuneRecipe(recipeOutput, DwarvenForge_Items.RUNE_HEALTH_GREATER);


            ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, DwarvenForge_Items.DWARVEN_BOOTS.get())
                    .define('X', DwarvenForge_Items.DWARVEN_METAL_INGOT.get())
                    .pattern("X X")
                    .pattern("X X")
                    .unlockedBy("has_dwarven_metal_ingot", has(DwarvenForge_Items.DWARVEN_METAL_INGOT.get()))
                    .save(recipeOutput);

            ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, DwarvenForge_Items.DWARVEN_CHESTPLATE.get())
                    .define('X', DwarvenForge_Items.DWARVEN_METAL_INGOT.get())
                    .pattern("X X")
                    .pattern("XXX")
                    .pattern("XXX")
                    .unlockedBy("has_dwarven_metal_ingot", has(DwarvenForge_Items.DWARVEN_METAL_INGOT.get()))
                    .save(recipeOutput);

            ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, DwarvenForge_Items.DWARVEN_HELMET.get())
                    .define('X', DwarvenForge_Items.DWARVEN_METAL_INGOT.get())
                    .pattern("XXX")
                    .pattern("X X")
                    .unlockedBy("has_dwarven_metal_ingot", has(DwarvenForge_Items.DWARVEN_METAL_INGOT.get()))
                    .save(recipeOutput);

            ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, DwarvenForge_Items.DWARVEN_LEGGINGS.get())
                    .define('X', DwarvenForge_Items.DWARVEN_METAL_INGOT.get())
                    .pattern("XXX")
                    .pattern("X X")
                    .pattern("X X")
                    .unlockedBy("has_dwarven_metal_ingot", has(DwarvenForge_Items.DWARVEN_METAL_INGOT.get()))
                    .save(recipeOutput);

            ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, DwarvenForge_Items.DWARVEN_SWORD.get())
                    .define('#', Items.GOLD_INGOT)
                    .define('X', DwarvenForge_Items.DWARVEN_METAL_INGOT.get())
                    .pattern("X")
                    .pattern("X")
                    .pattern("#")
                    .unlockedBy("has_dwarven_metal_ingot", has(DwarvenForge_Items.DWARVEN_METAL_INGOT.get()))
                    .save(recipeOutput);

            ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, DwarvenForge_Items.DWARVEN_AXE.get())
                    .define('#', Items.GOLD_INGOT)
                    .define('X', DwarvenForge_Items.DWARVEN_METAL_INGOT.get())
                    .pattern("XX")
                    .pattern("X#")
                    .pattern(" #")
                    .unlockedBy("has_dwarven_metal_ingot", has(DwarvenForge_Items.DWARVEN_METAL_INGOT.get()))
                    .save(recipeOutput);

            ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, DwarvenForge_Items.DWARVEN_HOE.get())
                    .define('#', Items.GOLD_INGOT)
                    .define('X', DwarvenForge_Items.DWARVEN_METAL_INGOT.get())
                    .pattern("XX")
                    .pattern(" #")
                    .pattern(" #")
                    .unlockedBy("has_dwarven_metal_ingot", has(DwarvenForge_Items.DWARVEN_METAL_INGOT.get()))
                    .save(recipeOutput);

            ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, DwarvenForge_Items.DWARVEN_PICKAXE.get())
                    .define('#', Items.GOLD_INGOT)
                    .define('X', DwarvenForge_Items.DWARVEN_METAL_INGOT.get())
                    .pattern("XXX")
                    .pattern(" # ")
                    .pattern(" # ")
                    .unlockedBy("has_dwarven_metal_ingot", has(DwarvenForge_Items.DWARVEN_METAL_INGOT.get()))
                    .save(recipeOutput);

            ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, DwarvenForge_Items.DWARVEN_SHOVEL.get())
                    .define('#', Items.GOLD_INGOT)
                    .define('X', DwarvenForge_Items.DWARVEN_METAL_INGOT.get())
                    .pattern("X")
                    .pattern("#")
                    .pattern("#")
                    .unlockedBy("has_dwarven_metal_ingot", has(DwarvenForge_Items.DWARVEN_METAL_INGOT.get()))
                    .save(recipeOutput);

        }

        protected void createRuneRecipe(RecipeOutput recipeOutput, DeferredItem<Item> rune){
            RuneSmithingRecipeBuilder.smithingRune(
                            Ingredient.of(rune.get()),
                            Ingredient.of(Tags.Items.ARMORS),
                            RecipeCategory.MISC)
                    .unlocks("has_"+rune.getId().getPath(), has(rune.get()))
                    .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(DwarvenForgeMain.MOD_ID, rune.getId().getPath()+"_smithing"));
        }
    }



    public static class ChestLootTablesGenerator implements LootTableSubProvider {


        public static LootTableProvider create(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
            return new LootTableProvider(output, Set.of(), List.of(
                    new LootTableProvider.SubProviderEntry(ChestLootTablesGenerator::new, LootContextParamSets.CHEST)
            ), registries);
        }

        // LootTableSubProvider factories now take a HolderLookup.Provider - unused here since none
        // of these loot pools reference registry-dependent data.
        public ChestLootTablesGenerator(HolderLookup.Provider registries) {
        }

        private static ResourceKey<LootTable> lootTable(String path){
            return ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.fromNamespaceAndPath(DwarvenForgeMain.MOD_ID, path));
        }

        @Override
        public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> biConsumer) {

            //Common
            biConsumer.accept(
                    lootTable("chests/dwarves/dwarf_house"),
                    LootTable.lootTable().withPool(
                            LootPool.lootPool()
                                    .setRolls(UniformGenerator.between(3.0F, 8.0F))
                                    .add(LootItem.lootTableItem(Items.GOLD_NUGGET)
                                            .setWeight(2)
                                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 3.0F))))
                                    .add(LootItem.lootTableItem(Items.BROWN_MUSHROOM)
                                            .setWeight(2))
                                    .add(LootItem.lootTableItem(Items.RED_MUSHROOM)
                                            .setWeight(1))
                                    .add(LootItem.lootTableItem(Items.BEETROOT)
                                            .setWeight(10)
                                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 7.0F))))
                                    .add(LootItem.lootTableItem(Items.BREAD)
                                            .setWeight(10)
                                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 4.0F))))
                                    .add(LootItem.lootTableItem(Items.AMETHYST_SHARD)
                                            .setWeight(2)
                                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 2.0F))))
                                    .add(LootItem.lootTableItem(Blocks.COBBLESTONE)
                                            .setWeight(5)
                                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 6.0F))))
                    )
            );

            //Forger
            biConsumer.accept(
                    lootTable("chests/dwarves/dwarf_forge"),
                    LootTable.lootTable().withPool(
                            LootPool.lootPool()
                                    .setRolls(UniformGenerator.between(3.0F, 8.0F))

                                    // Valuable items
                                    .add(LootItem.lootTableItem(Items.GOLD_INGOT)
                                            .setWeight(5)
                                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 3.0F))))
                                    .add(LootItem.lootTableItem(Items.IRON_INGOT)
                                            .setWeight(10)
                                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 5.0F))))

                                    // Food
                                    .add(LootItem.lootTableItem(Items.BEETROOT)
                                            .setWeight(10)
                                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 3.0F))))

                                    // Weapons
                                    .add(LootItem.lootTableItem(Items.IRON_PICKAXE)
                                            .setWeight(5))
                                    .add(LootItem.lootTableItem(Items.IRON_SWORD)
                                            .setWeight(5))
                                    .add(LootItem.lootTableItem(Items.IRON_AXE)
                                            .setWeight(5))

                                    // Runes
                                    .add(LootItem.lootTableItem(DwarvenForge_Items.RUNE_ARMOR_LESSER.get())
                                            .setWeight(1))
                                    .add(LootItem.lootTableItem(DwarvenForge_Items.RUNE_TOUGHNESS_LESSER.get())
                                            .setWeight(1))
                                    .add(LootItem.lootTableItem(DwarvenForge_Items.RUNE_HEALTH_LESSER.get())
                                            .setWeight(1))
                                    .add(LootItem.lootTableItem(DwarvenForge_Items.RUNE_STRENGTH_LESSER.get())
                                            .setWeight(1))
                                    .add(LootItem.lootTableItem(DwarvenForge_Items.RUNE_SPEED_LESSER.get())
                                            .setWeight(1))

                                    .add(LootItem.lootTableItem(Blocks.OBSIDIAN)
                                            .setWeight(3)
                                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(3.0F, 7.0F))))

                                    // Dwarves
                                    .add(LootItem.lootTableItem(DwarvenForge_Items.DWARVEN_METAL_INGOT.get())
                                            .setWeight(2)
                                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 4.0F))))
                    )
            );

            //Miner
            biConsumer.accept(
                    lootTable("chests/dwarves/dwarf_mine"),
                    LootTable.lootTable().withPool(
                            LootPool.lootPool()
                                    .setRolls(UniformGenerator.between(1.0F, 5.0F))

                                    // Mason materials
                                    .add(LootItem.lootTableItem(Blocks.STONE)
                                            .setWeight(5))
                                    .add(LootItem.lootTableItem(Blocks.STONE_BRICKS)
                                            .setWeight(2))
                                    .add(LootItem.lootTableItem(Blocks.COBBLESTONE)
                                            .setWeight(5))

                                    // Food
                                    .add(LootItem.lootTableItem(Items.BEETROOT)
                                            .setWeight(4)
                                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 4.0F))))

                                    // Currency
                                    .add(LootItem.lootTableItem(Items.AMETHYST_SHARD)
                                            .setWeight(2))

                                    //Mined items
                                    .add(LootItem.lootTableItem(Items.DIAMOND)
                                            .setWeight(1)
                                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 2.0F))))
                                    .add(LootItem.lootTableItem(Items.RAW_GOLD)
                                            .setWeight(4)
                                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 6.0F))))
                                    .add(LootItem.lootTableItem(Items.RAW_IRON)
                                            .setWeight(4)
                                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 8.0F))))
                                    .add(LootItem.lootTableItem(Items.RAW_COPPER)
                                            .setWeight(5)
                                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 12.0F))))
                    )
            );

            //Leatherworker
            biConsumer.accept(
                    lootTable("chests/dwarves/dwarf_tannery"),
                    LootTable.lootTable().withPool(
                            LootPool.lootPool()
                                    .setRolls(UniformGenerator.between(1.0F, 5.0F))

                                    // Raw materials
                                    .add(LootItem.lootTableItem(Items.LEATHER)
                                            .setWeight(1)
                                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 4.0F))))

                                    // Leather armor
                                    .add(LootItem.lootTableItem(Items.LEATHER_CHESTPLATE)
                                            .setWeight(2))
                                    .add(LootItem.lootTableItem(Items.CHAINMAIL_CHESTPLATE)
                                            .setWeight(2))
                                    .add(LootItem.lootTableItem(Items.LEATHER_HELMET)
                                            .setWeight(2))
                                    .add(LootItem.lootTableItem(Items.CHAINMAIL_HELMET)
                                            .setWeight(2))


                                    .add(LootItem.lootTableItem(Items.BEETROOT)
                                            .setWeight(5)
                                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 4.0F))))


                                    .add(LootItem.lootTableItem(Items.AMETHYST_SHARD)
                                            .setWeight(1)
                                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 2.0F))))
                    )
            );

            //Butcher
            biConsumer.accept(
                    lootTable("chests/dwarves/dwarf_butcher_house"),
                    LootTable.lootTable().withPool(
                            LootPool.lootPool()
                                    .setRolls(UniformGenerator.between(1.0F, 5.0F))

                                    // Currency
                                    .add(LootItem.lootTableItem(Items.AMETHYST_SHARD)
                                            .setWeight(1))

                                    // Meats
                                    .add(LootItem.lootTableItem(Items.PORKCHOP)
                                            .setWeight(6)
                                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 3.0F))))
                                    .add(LootItem.lootTableItem(Items.BEEF)
                                            .setWeight(6)
                                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 3.0F))))
                                    .add(LootItem.lootTableItem(Items.MUTTON)
                                            .setWeight(6)
                                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 3.0F))))

                                    // Farming & fuel
                                    .add(LootItem.lootTableItem(Items.WHEAT)
                                            .setWeight(6)
                                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 3.0F))))
                                    .add(LootItem.lootTableItem(Items.COAL)
                                            .setWeight(3)
                                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 3.0F))))
                    )
            );


        }
    }
}
