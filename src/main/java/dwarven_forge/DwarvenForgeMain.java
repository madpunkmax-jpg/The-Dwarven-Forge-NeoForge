package dwarven_forge;

import com.mojang.logging.LogUtils;
import dwarven_forge.ai.DwarvenForge_Schedules;
import dwarven_forge.entity.DwarfEntity;
import dwarven_forge.entity.DwarvenForge_EntityAttributes;
import dwarven_forge.registry.DwarvenForge_Entities;
import dwarven_forge.registry.DwarvenForge_Items;
import dwarven_forge.registry.DwarvenForge_ItemsMaterials;
import dwarven_forge.registry.DwarvenForge_Sensors;
import dwarven_forge.registry.DwarvenForge_Serializers;
import dwarven_forge.registry.DwarvenForge_Sounds;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
// Only mod-bus (@EventBusSubscriber(bus = MOD)) static handlers live here - see
// DwarvenForgeEvents for the game-bus (NeoForge.EVENT_BUS) ones. NeoForge validates that
// register(Class) targets only contain handlers valid for that one specific bus, so mixing
// MOD-bus-only events (like EntityAttributeCreationEvent) and GAME-bus events in a class
// that also gets registered to the other bus fails at mod-construction time.
@Mod(DwarvenForgeMain.MOD_ID)
@EventBusSubscriber(modid = DwarvenForgeMain.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class DwarvenForgeMain
{
    // Define mod id in a common place for everything to reference
    public static final String MOD_ID = "dwarven_forge";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();


    public DwarvenForgeMain(IEventBus modEventBus, ModContainer modContainer)
    {
        // Register the commonSetup method for mod loading
        modEventBus.addListener(this::commonSetup);

        //Entities
        DwarvenForge_Entities.initEntities(); DwarvenForge_Registries.ENTITY_TYPES.register(modEventBus);

        //Attributes
        DwarvenForge_EntityAttributes.initEntityAttributes(); DwarvenForge_Registries.ATTRIBUTES.register(modEventBus);

        //Materials (must load before ITEMS' RegisterEvent fires - see DwarvenForge_ItemsMaterials.touch())
        DwarvenForge_ItemsMaterials.touch(); DwarvenForge_Registries.ARMOR_MATERIALS.register(modEventBus);

        //Items
        DwarvenForge_Items.initItems(); DwarvenForge_Registries.ITEMS.register(modEventBus);
        DwarvenForge_Items.initGroupItems(); DwarvenForge_Registries.CREATIVE_MODE_TABS.register(modEventBus);

        //Sounds
        DwarvenForge_Sounds.initSounds(); DwarvenForge_Registries.SOUND_EVENTS.register(modEventBus);

        //Serializers
        DwarvenForge_Serializers.initSerializers(); DwarvenForge_Registries.SERIALIZERS.register(modEventBus);

        //Schedules
        DwarvenForge_Schedules.initSchedules(); DwarvenForge_Registries.SCHEDULES.register(modEventBus);

        //Sensors
        DwarvenForge_Sensors.initSensors(); DwarvenForge_Registries.SENSOR_TYPES.register(modEventBus);

        // Register our mod's ModConfigSpec so that NeoForge can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.COMMON, DwarvenForgeConfig.SPEC);
    }



    private void commonSetup(final FMLCommonSetupEvent event)
    {

    }

    @SubscribeEvent
    public static void setEntitiesAttributes(EntityAttributeCreationEvent event) {
        //Dwarf
        event.put(DwarvenForge_Entities.DWARF.get(), DwarfEntity.setAttributes().build());
    }
}
