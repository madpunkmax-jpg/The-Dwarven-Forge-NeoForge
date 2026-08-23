package dwarven_forge.registry;

import dwarven_forge.DwarvenForge_Registries;
import dwarven_forge.ai.sensing.DwarfBabiesSensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.neoforged.neoforge.registries.DeferredHolder;

public class DwarvenForge_Sensors {

    public static final DeferredHolder<SensorType<?>, SensorType<DwarfBabiesSensor>> DWARF_BABIES = DwarvenForge_Registries.SENSOR_TYPES.register(
            "dwarf_babies",
            () -> new SensorType<>(DwarfBabiesSensor::new));

    public static void initSensors(){}

}
