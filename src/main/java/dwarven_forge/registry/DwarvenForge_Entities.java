package dwarven_forge.registry;

import dwarven_forge.DwarvenForge_Registries;
import dwarven_forge.entity.DwarfEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;

public class DwarvenForge_Entities {

    public static final DeferredHolder<EntityType<?>, EntityType<DwarfEntity>> DWARF = DwarvenForge_Registries.ENTITY_TYPES.register(
            "dwarf",
            () -> EntityType.Builder.of(DwarfEntity::new, MobCategory.MISC)
                    .sized(0.6f, 1.65f).build("dwarf"));

    public static void initEntities(){}

}
