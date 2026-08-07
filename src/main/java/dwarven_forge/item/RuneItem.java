package dwarven_forge.item;

import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class RuneItem extends Item {

    public final AttributeModifier modifier;
    public final Holder<Attribute> attribute;
    public final String runeTooltip;
    public final Item addition;
    public final int index;
    public final int level;

    public RuneItem(Properties properties, Holder<Attribute> attribute, AttributeModifier modifier, String runeTooltip, Item addition, int index, int level) {
        super(properties);
        this.attribute=attribute;
        this.modifier=modifier;
        this.runeTooltip=runeTooltip;
        this.addition= addition;
        this.index=index;
        this.level=level;
    }

    public String getRuneTooltip() {
        return runeTooltip;
    }

    public Item getAddition() {
        return addition;
    }

    public int getIndex() {
        return index;
    }


    private static final Component APPLIES_TO_TITLE = Component.translatable(Util.makeDescriptionId("item", ResourceLocation.withDefaultNamespace("smithing_template.applies_to"))).withStyle(ChatFormatting.GRAY);

    private static final Component INGREDIENTS_TITLE = Component.translatable(Util.makeDescriptionId("item", ResourceLocation.withDefaultNamespace("smithing_template.ingredients"))).withStyle(ChatFormatting.GRAY);

    private static final Component ARMOR_TRIM_APPLIES_TO = Component.translatable(Util.makeDescriptionId("item", ResourceLocation.withDefaultNamespace("smithing_template.armor_trim.applies_to"))).withStyle(ChatFormatting.BLUE);

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull Item.TooltipContext context, @NotNull List<Component> list, @NotNull TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, list, tooltipFlag);
        list.add(Component.translatable("tooltip.dwarven_forge.rune."+this.getRuneTooltip()).withStyle(ChatFormatting.GRAY));
        list.add(CommonComponents.EMPTY);
        list.add(APPLIES_TO_TITLE);
        list.add(CommonComponents.space().append(ARMOR_TRIM_APPLIES_TO));
        list.add(INGREDIENTS_TITLE);
        list.add(CommonComponents.space().append(this.addition.getDescription()).withStyle(ChatFormatting.BLUE));
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack stack) {
        return Component.translatable("item.dwarven_forge.rune");
    }

    // Runes used to be stored as a "Runes" CompoundTag directly on the ItemStack's NBT.
    // Since 1.20.5, ItemStacks no longer carry a free-form NBT tag - custom data has to go
    // through the DataComponents.CUSTOM_DATA component, which still wraps a CompoundTag
    // internally, so the tag layout itself is unchanged.
    public static void setRuneTag(ItemStack stack, ItemStack ingredient) {
        if (stack.getItem() instanceof ArmorItem && ingredient.getItem() instanceof RuneItem rune) {
            CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> {
                CompoundTag runesNbt = tag.contains("Runes") ? tag.getCompound("Runes") : new CompoundTag();

                CompoundTag individualRune = new CompoundTag();

                individualRune.putString("Item", BuiltInRegistries.ITEM.getKey(rune).toString());
                individualRune.putInt("Index", rune.index);
                individualRune.putInt("Level", rune.level);
                individualRune.putUUID("UUID", Mth.createInsecureUUID(RandomSource.createNewThreadLocalInstance()));

                runesNbt.put("Rune"+rune.index, individualRune);

                tag.put("Runes", runesNbt);
            });
        }
    }


}
