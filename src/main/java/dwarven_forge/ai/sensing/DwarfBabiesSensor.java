package dwarven_forge.ai.sensing;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import dwarven_forge.registry.DwarvenForge_Entities;
import java.util.List;
import java.util.Set;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.minecraft.world.entity.ai.sensing.Sensor;

/**
 * Copy of vanilla {@link net.minecraft.world.entity.ai.sensing.VillagerBabiesSensor}
 * with the hardcoded {@code EntityType.VILLAGER} check replaced by the dwarf entity
 * type, so baby dwarves see each other and can play tag together.
 */
public class DwarfBabiesSensor extends Sensor<LivingEntity> {
    @Override
    public Set<MemoryModuleType<?>> requires() {
        return ImmutableSet.of(MemoryModuleType.VISIBLE_VILLAGER_BABIES);
    }

    @Override
    protected void doTick(ServerLevel level, LivingEntity entity) {
        entity.getBrain().setMemory(MemoryModuleType.VISIBLE_VILLAGER_BABIES, this.getNearestDwarfBabies(entity));
    }

    private List<LivingEntity> getNearestDwarfBabies(LivingEntity livingEntity) {
        return ImmutableList.copyOf(this.getVisibleEntities(livingEntity).findAll(this::isDwarfBaby));
    }

    private boolean isDwarfBaby(LivingEntity livingEntity) {
        return livingEntity.getType() == DwarvenForge_Entities.DWARF.get() && livingEntity.isBaby();
    }

    private NearestVisibleLivingEntities getVisibleEntities(LivingEntity livingEntity) {
        return livingEntity.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES).orElse(NearestVisibleLivingEntities.empty());
    }
}
