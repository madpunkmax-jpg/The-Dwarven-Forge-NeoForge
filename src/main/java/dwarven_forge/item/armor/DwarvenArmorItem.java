package dwarven_forge.item.armor;

import dwarven_forge.DwarvenForgeMain;
import dwarven_forge.entity.DwarvenForge_EntityAttributes;
import dwarven_forge.geo.item.renderer.DwarvenArmorRenderer;
import dwarven_forge.utils.GeoControllersUtil;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;

public class DwarvenArmorItem extends ArmorItem implements GeoItem {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public DwarvenArmorItem(Holder<ArmorMaterial> material, Type type, Properties properties) {
        super(material, type, properties);
    }

    // Replaces the old Forge ArmorItemMixin, which shadowed ArmorItem's now-gone
    // Multimap<Attribute, AttributeModifier> field to bolt on magic/explosion resistance.
    // ArmorItem now derives its modifiers from a memoized ItemAttributeModifiers Supplier
    // (see ArmorItem#getDefaultAttributeModifiers), so overriding it here on our own
    // subclass is simpler and avoids mixing into vanilla at all.
    @Override
    @SuppressWarnings("deprecation")
    public ItemAttributeModifiers getDefaultAttributeModifiers() {
        EquipmentSlotGroup slotGroup = EquipmentSlotGroup.bySlot(this.getEquipmentSlot());

        return super.getDefaultAttributeModifiers()
                .withModifierAdded(DwarvenForge_EntityAttributes.GENERIC_MAGIC_RESISTANCE,
                        new AttributeModifier(ResourceLocation.fromNamespaceAndPath(DwarvenForgeMain.MOD_ID, "dwarven_magic_resistance"),
                                0.10, AttributeModifier.Operation.ADD_MULTIPLIED_BASE),
                        slotGroup)
                .withModifierAdded(DwarvenForge_EntityAttributes.GENERIC_EXPLOSION_RESISTANCE,
                        new AttributeModifier(ResourceLocation.fromNamespaceAndPath(DwarvenForgeMain.MOD_ID, "dwarven_explosion_resistance"),
                                0.15, AttributeModifier.Operation.ADD_MULTIPLIED_BASE),
                        slotGroup);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private GeoArmorRenderer<?> renderer;

            public @NotNull HumanoidModel<?> getHumanoidArmorModel(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot, HumanoidModel<?> original) {
                if (this.renderer == null) {
                    this.renderer = new DwarvenArmorRenderer();
                }

                this.renderer.prepForRender(livingEntity, itemStack, equipmentSlot, original);
                return this.renderer;
            }
        });
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(GeoControllersUtil.genericIdleController(this));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
