package dwarven_forge.geo.item.model;

import dwarven_forge.DwarvenForgeMain;
import dwarven_forge.item.armor.DwarvenArmorItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class DwarvenArmorModel extends GeoModel<DwarvenArmorItem> {
    @Override
    public ResourceLocation getModelResource(DwarvenArmorItem dwarvenArmorItem) {
        return ResourceLocation.fromNamespaceAndPath(DwarvenForgeMain.MOD_ID, "geo/armor/dwarven_armor.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(DwarvenArmorItem dwarvenArmorItem) {
        return ResourceLocation.fromNamespaceAndPath(DwarvenForgeMain.MOD_ID, "textures/models/dwarven_armor.png");

    }

    @Override
    public ResourceLocation getAnimationResource(DwarvenArmorItem dwarvenArmorItem) {
        return ResourceLocation.fromNamespaceAndPath(DwarvenForgeMain.MOD_ID, "animations/misc/dummy.animation.json");
    }
}
