package dwarven_forge.geo.item.renderer;

import dwarven_forge.geo.item.model.DwarvenArmorModel;
import dwarven_forge.item.armor.DwarvenArmorItem;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class DwarvenArmorRenderer extends GeoArmorRenderer<DwarvenArmorItem> {
    public DwarvenArmorRenderer() {
        super(new DwarvenArmorModel());
    }
}
