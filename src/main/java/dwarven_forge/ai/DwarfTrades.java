package dwarven_forge.ai;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import dwarven_forge.registry.DwarvenForge_Items;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.Util;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.SuspiciousStewEffects;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class DwarfTrades {

    //Toolsmith
    //Butcher
    //Leatherworker
    //Farmer
    //Mason

    //Warrior

    public static final Map<VillagerProfession, Int2ObjectMap<VillagerTrades.ItemListing[]>> TRADES = Util.make(Maps.newHashMap(),
            (map) -> {
                //Forger
                map.put(VillagerProfession.TOOLSMITH, toIntMap(ImmutableMap.of(
                        1, new VillagerTrades.ItemListing[]{
                                new AmethystForItems(Items.COAL, 24, 16, 2),

                                //Lesser
                                //Armor
                                new ItemsForAmethysts(new ItemStack(DwarvenForge_Items.RUNE_ARMOR_LESSER.get()), 24, 1, 4, 5, 0.2F),
                                //Toughness
                                new ItemsForAmethysts(new ItemStack(DwarvenForge_Items.RUNE_TOUGHNESS_LESSER.get()), 24, 1, 4, 5, 0.2F),
                                //Speed
                                new ItemsForAmethysts(new ItemStack(DwarvenForge_Items.RUNE_SPEED_LESSER.get()), 24, 1, 4, 5, 0.2F),
                                //Health
                                new ItemsForAmethysts(new ItemStack(DwarvenForge_Items.RUNE_HEALTH_LESSER.get()), 24, 1, 4, 5, 0.2F),
                                //Strength
                                new ItemsForAmethysts(new ItemStack(DwarvenForge_Items.RUNE_STRENGTH_LESSER.get()), 24, 1, 4, 5, 0.2F),
                        },
                        2, new VillagerTrades.ItemListing[]{
                                new AmethystForItems(Items.GOLD_INGOT, 5, 12, 10),

                                new ItemsForAmethysts(new ItemStack(DwarvenForge_Items.DWARVEN_METAL_INGOT.get()), 4, 1, 16, 10, 0.2F)
                        },
                        3, new VillagerTrades.ItemListing[]{
                                new AmethystForItems(Items.COPPER_INGOT, 18, 12, 20),

                                new EnchantedItemForAmethysts(Items.IRON_AXE, 2, 3, 10, 0.2F),
                                new EnchantedItemForAmethysts(Items.IRON_PICKAXE, 3, 3, 10, 0.2F),

                                //Normal
                                //Armor
                                new ItemsForAmethysts(new ItemStack(DwarvenForge_Items.RUNE_ARMOR.get()), 36, 1, 4, 15, 0.2F),
                                //Toughness
                                new ItemsForAmethysts(new ItemStack(DwarvenForge_Items.RUNE_TOUGHNESS.get()), 36, 1, 4, 15, 0.2F),
                                //Speed
                                new ItemsForAmethysts(new ItemStack(DwarvenForge_Items.RUNE_SPEED.get()), 36, 1, 4, 15, 0.2F),
                        },
                        4, new VillagerTrades.ItemListing[]{
                                new EnchantedItemForAmethysts(DwarvenForge_Items.DWARVEN_AXE.get(), 24, 2, 15, 0.2F),
                                new EnchantedItemForAmethysts(DwarvenForge_Items.DWARVEN_PICKAXE.get(), 24, 2, 15, 0.2F),

                                //Normal
                                //Health
                                new ItemsForAmethysts(new ItemStack(DwarvenForge_Items.RUNE_HEALTH.get()), 36, 1, 4, 15, 0.2F),
                                //Strength
                                new ItemsForAmethysts(new ItemStack(DwarvenForge_Items.RUNE_STRENGTH.get()), 36, 1, 4, 15, 0.2F),
                        },
                        5, new VillagerTrades.ItemListing[]{
                                //Greater
                                //Armor
                                new ItemsForAmethysts(new ItemStack(DwarvenForge_Items.RUNE_ARMOR_GREATER.get()), 48, 1, 4, 30, 0.2F),
                                //Toughness
                                new ItemsForAmethysts(new ItemStack(DwarvenForge_Items.RUNE_TOUGHNESS_GREATER.get()), 48, 1, 4, 30, 0.2F),
                                //Speed
                                new ItemsForAmethysts(new ItemStack(DwarvenForge_Items.RUNE_SPEED_GREATER.get()), 48, 1, 4, 30, 0.2F),
                                //Health
                                new ItemsForAmethysts(new ItemStack(DwarvenForge_Items.RUNE_HEALTH_GREATER.get()), 48, 1, 4, 30, 0.2F),
                                //Strength
                                new ItemsForAmethysts(new ItemStack(DwarvenForge_Items.RUNE_STRENGTH_GREATER.get()), 48, 1, 4, 30, 0.2F),
                        }
                )));

                //Butcher
                map.put(VillagerProfession.BUTCHER, toIntMap(ImmutableMap.of(
                        1, new VillagerTrades.ItemListing[]{
                                new AmethystForItems(Items.PORKCHOP, 12, 16, 2),
                                new AmethystForItems(Items.CHICKEN, 4, 16, 2),
                                new AmethystForItems(Items.BEEF, 8, 16, 2)
                        },
                        2, new VillagerTrades.ItemListing[]{
                                new AmethystForItems(Items.COAL, 14, 16, 2),
                                new AmethystForItems(Items.MUTTON, 6, 16, 20),
                                new ItemsForAmethysts(Items.COOKED_CHICKEN,  8, 2, 16, 5)
                        },
                        3, new VillagerTrades.ItemListing[]{
                                new ItemsForAmethysts(Items.COOKED_PORKCHOP, 8, 6, 16, 5),
                                new ItemsForAmethysts(Items.COOKED_MUTTON, 8, 12, 16, 5),
                        },
                        4, new VillagerTrades.ItemListing[]{
                                new ItemsForAmethysts(Items.COOKED_BEEF, 12, 4, 30)
                        },
                        5, new VillagerTrades.ItemListing[]{
                                new AmethystForItems(Items.GLOW_BERRIES, 8, 12, 30)
                        }
                )));

                //Leatherworker
                map.put(VillagerProfession.LEATHERWORKER, toIntMap(ImmutableMap.of(
                        1, new VillagerTrades.ItemListing[]{
                                new AmethystForItems(Items.LEATHER, 4, 16, 2),
                                new DwarfTrades.DyedArmorForAmethysts(Items.LEATHER_LEGGINGS, 4, 12, 2),
                                new DwarfTrades.DyedArmorForAmethysts(Items.LEATHER_CHESTPLATE, 6, 12, 2)
                        },
                        2, new VillagerTrades.ItemListing[]{
                                new AmethystForItems(Items.FLINT, 26, 12, 10),
                                new DwarfTrades.DyedArmorForAmethysts(Items.LEATHER_HELMET, 4, 12, 10),
                                new DwarfTrades.DyedArmorForAmethysts(Items.LEATHER_BOOTS, 2, 12, 10)
                        },
                        3, new VillagerTrades.ItemListing[]{
                                new ItemsForAmethysts(Items.CHAINMAIL_LEGGINGS, 4, 1,12, 15),
                                new ItemsForAmethysts(Items.CHAINMAIL_CHESTPLATE, 6, 1,12, 15)
                        },
                        4, new VillagerTrades.ItemListing[]{
                                new ItemsForAmethysts(Items.CHAINMAIL_HELMET, 4, 1, 12, 15),
                                new ItemsForAmethysts(Items.CHAINMAIL_BOOTS, 2, 1, 12, 15),
                                new DwarfTrades.DyedArmorForAmethysts(Items.LEATHER_HORSE_ARMOR, 6, 12, 15)
                        },
                        5, new VillagerTrades.ItemListing[]{
                                new ItemsForAmethysts(new ItemStack(Items.SADDLE), 6, 1, 12, 30, 0.2F)
                        }
                )));

                //Farmer
                map.put(VillagerProfession.FARMER, toIntMap(ImmutableMap.of(

                        1, new VillagerTrades.ItemListing[]{
                                new AmethystForItems(Items.POTATO, 26, 16, 2),
                                new AmethystForItems(Items.CARROT, 22, 16, 2),
                                new ItemsForAmethysts(Items.BEETROOT, 8, 16, 2)
                        },

                        2, new VillagerTrades.ItemListing[]{
                                new AmethystForItems(Items.GLOW_BERRIES, 12, 8, 10),
                                new ItemsForAmethysts(Items.APPLE, 1, 4, 16, 5)
                        },

                        3, new VillagerTrades.ItemListing[]{
                                new ItemsForAmethysts(Items.BROWN_MUSHROOM, 1, 8, 10),
                                new ItemsForAmethysts(Items.RED_MUSHROOM, 1, 4, 10)
                        },

                        4, new VillagerTrades.ItemListing[]{
                                new ItemsForAmethysts(Blocks.CAKE, 1, 1, 12, 15),
                                new DwarfTrades.SuspiciousStewForAmethyst(MobEffects.FIRE_RESISTANCE, 200, 15),
                                new DwarfTrades.SuspiciousStewForAmethyst(MobEffects.DIG_SPEED, 200, 15),
                                new DwarfTrades.SuspiciousStewForAmethyst(MobEffects.NIGHT_VISION, 160, 15),
                                new DwarfTrades.SuspiciousStewForAmethyst(MobEffects.REGENERATION, 140, 15)
                        },

                        5, new VillagerTrades.ItemListing[]{
                                new ItemsForAmethysts(Items.GOLDEN_CARROT, 3, 3, 30),
                                new ItemsForAmethysts(Items.GOLDEN_APPLE.getDefaultInstance(), 32, 1, 4, 30, 0.2F)
                        }
                )));


                //Miner
                map.put(VillagerProfession.MASON, toIntMap(ImmutableMap.of(
                        1, new VillagerTrades.ItemListing[]{
                                new AmethystForItems(Blocks.STONE, 20, 16, 2),
                                new ItemsForAmethysts(Items.COAL, 8, 6, 16, 1)
                        },
                        2, new VillagerTrades.ItemListing[]{
                                new ItemsForAmethysts(Items.RAW_COPPER, 14, 8, 16, 5),
                                new ItemsForAmethysts(Blocks.CHISELED_STONE_BRICKS, 1, 4, 16, 5)
                        },
                        3, new VillagerTrades.ItemListing[]{
                                new AmethystForItems(Blocks.GRANITE, 16, 16, 20),
                                new AmethystForItems(Blocks.ANDESITE, 16, 16, 20),
                                new ItemsForAmethysts(Items.RAW_GOLD, 24, 4, 16, 10),
                                new ItemsForAmethysts(Items.RAW_IRON, 18, 4, 16, 10),

                                new ItemsForAmethysts(Blocks.POLISHED_DIORITE, 1, 4, 16, 10),
                                new ItemsForAmethysts(Blocks.POLISHED_GRANITE, 1, 4, 16, 10)
                        },
                        4, new VillagerTrades.ItemListing[]{
                                new ItemsForAmethysts(Items.LAPIS_LAZULI, 16, 8, 16, 10),
                                new ItemsForAmethysts(Items.REDSTONE, 24, 8, 16, 10),

                                new ItemsForAmethysts(Blocks.CUT_COPPER, 12, 8, 16, 10),
                                new ItemsForAmethysts(Blocks.IRON_BARS, 24, 18, 16, 10)
                        },
                        5, new VillagerTrades.ItemListing[]{
                                new ItemsForAmethysts(Blocks.TUFF, 1, 12, 16, 30),
                                new ItemsForAmethysts(Items.DIAMOND, 48, 1, 12, 30)
                        }
                )));
    });


    private static Int2ObjectMap<VillagerTrades.ItemListing[]> toIntMap(ImmutableMap<Integer, VillagerTrades.ItemListing[]> p_35631_) {
        return new Int2ObjectOpenHashMap<>(p_35631_);
    }

    static class ItemsForAmethysts implements VillagerTrades.ItemListing {
        private final ItemStack itemStack;
        private final int amethystCost;
        private final int numberOfItems;
        private final int maxUses;
        private final int villagerXp;
        private final float priceMultiplier;

        public ItemsForAmethysts(Block block, int amethystCost, int numberOfItems, int maxUses, int villagerXp) {
            this(new ItemStack(block), amethystCost, numberOfItems, maxUses, villagerXp);
        }

        public ItemsForAmethysts(Item item, int amethystCost, int numberOfItems, int villagerXp) {
            this(new ItemStack(item), amethystCost, numberOfItems, 12, villagerXp);
        }

        public ItemsForAmethysts(Item item, int amethystCost, int numberOfItems, int maxUses, int villagerXp) {
            this(new ItemStack(item), amethystCost, numberOfItems, maxUses, villagerXp);
        }

        public ItemsForAmethysts(ItemStack itemStack, int amethystCost, int numberOfItems, int maxUses, int villagerXp) {
            this(itemStack, amethystCost, numberOfItems, maxUses, villagerXp, 0.05F);
        }

        public ItemsForAmethysts(ItemStack itemStack, int amethystCost, int numberOfItems, int maxUses, int villagerXp, float priceMultiplier) {
            this.itemStack = itemStack;
            this.amethystCost = amethystCost;
            this.numberOfItems = numberOfItems;
            this.maxUses = maxUses;
            this.villagerXp = villagerXp;
            this.priceMultiplier = priceMultiplier;
        }


        public MerchantOffer getOffer(@NotNull Entity p_219699_, @NotNull RandomSource p_219700_) {
            return new MerchantOffer(new ItemCost(Items.AMETHYST_SHARD, this.amethystCost), new ItemStack(this.itemStack.getItem(), this.numberOfItems), this.maxUses, this.villagerXp, this.priceMultiplier);
        }
    }

    static class AmethystForItems implements VillagerTrades.ItemListing {
        private final Item item;
        private final int cost;
        private final int maxUses;
        private final int villagerXp;
        private final float priceMultiplier;

        public AmethystForItems(ItemLike item, int cost, int maxUses, int villagerXp) {
            this.item = item.asItem();
            this.cost = cost;
            this.maxUses = maxUses;
            this.villagerXp = villagerXp;
            this.priceMultiplier = 0.05F;
        }

        public MerchantOffer getOffer(@NotNull Entity p_219682_, @NotNull RandomSource p_219683_) {
            return new MerchantOffer(new ItemCost(this.item, this.cost), new ItemStack(Items.AMETHYST_SHARD), this.maxUses, this.villagerXp, this.priceMultiplier);
        }
    }

    static class EnchantedItemForAmethysts implements VillagerTrades.ItemListing {
        private final ItemStack itemStack;
        private final int baseAmethystCost;
        private final int maxUses;
        private final int villagerXp;
        private final float priceMultiplier;

        public EnchantedItemForAmethysts(Item item, int baseAmethystCost, int maxUses, int villagerXp) {
            this(item, baseAmethystCost, maxUses, villagerXp, 0.05F);
        }

        public EnchantedItemForAmethysts(Item item, int baseAmethystCost, int maxUses, int villagerXp, float priceMultiplier) {
            this.itemStack = new ItemStack(item);
            this.baseAmethystCost = baseAmethystCost;
            this.maxUses = maxUses;
            this.villagerXp = villagerXp;
            this.priceMultiplier = priceMultiplier;
        }

        public MerchantOffer getOffer(@NotNull Entity entity, RandomSource random) {
            int i = 5 + random.nextInt(15);
            RegistryAccess registryAccess = entity.level().registryAccess();
            Optional<HolderSet.Named<Enchantment>> optional = registryAccess.registryOrThrow(Registries.ENCHANTMENT).getTag(EnchantmentTags.ON_TRADED_EQUIPMENT);
            ItemStack itemstack = EnchantmentHelper.enchantItem(random, new ItemStack(this.itemStack.getItem()), i, registryAccess, optional);
            int j = Math.min(this.baseAmethystCost + i, 64);
            return new MerchantOffer(new ItemCost(Items.AMETHYST_SHARD, j), itemstack, this.maxUses, this.villagerXp, this.priceMultiplier);
        }
    }

    static class DyedArmorForAmethysts implements VillagerTrades.ItemListing {
        private final Item item;
        private final int value;
        private final int maxUses;
        private final int villagerXp;

        public DyedArmorForAmethysts(Item item, int value) {
            this(item, value, 12, 1);
        }

        public DyedArmorForAmethysts(Item item, int value, int maxUses, int villagerXp) {
            this.item = item;
            this.value = value;
            this.maxUses = maxUses;
            this.villagerXp = villagerXp;
        }

        public MerchantOffer getOffer(@NotNull Entity p_219679_, @NotNull RandomSource p_219680_) {
            ItemStack itemstack1 = new ItemStack(this.item);
            if (itemstack1.is(ItemTags.DYEABLE)) {
                List<DyeItem> list = Lists.newArrayList();
                list.add(getRandomDye(p_219680_));
                if (p_219680_.nextFloat() > 0.7F) {
                    list.add(getRandomDye(p_219680_));
                }

                if (p_219680_.nextFloat() > 0.8F) {
                    list.add(getRandomDye(p_219680_));
                }

                itemstack1 = net.minecraft.world.item.component.DyedItemColor.applyDyes(itemstack1, list);
            }

            return new MerchantOffer(new ItemCost(Items.AMETHYST_SHARD, this.value), itemstack1, this.maxUses, this.villagerXp, 0.2F);
        }

        private static DyeItem getRandomDye(RandomSource p_219677_) {
            return DyeItem.byColor(DyeColor.byId(p_219677_.nextInt(16)));
        }
    }

    static class SuspiciousStewForAmethyst implements VillagerTrades.ItemListing {
        final Holder<MobEffect> effect;
        final int duration;
        final int xp;
        private final float priceMultiplier;

        public SuspiciousStewForAmethyst(Holder<MobEffect> effect, int duration, int xp) {
            this.effect = effect;
            this.duration = duration;
            this.xp = xp;
            this.priceMultiplier = 0.05F;
        }

        @Nullable
        public MerchantOffer getOffer(@NotNull Entity p_219702_, @NotNull RandomSource p_219703_) {
            ItemStack itemstack = new ItemStack(Items.SUSPICIOUS_STEW, 1);
            itemstack.set(net.minecraft.core.component.DataComponents.SUSPICIOUS_STEW_EFFECTS,
                    new SuspiciousStewEffects(List.of(new SuspiciousStewEffects.Entry(this.effect, this.duration))));
            return new MerchantOffer(new ItemCost(Items.AMETHYST_SHARD, 1), itemstack, 12, this.xp, this.priceMultiplier);
        }
    }
}
