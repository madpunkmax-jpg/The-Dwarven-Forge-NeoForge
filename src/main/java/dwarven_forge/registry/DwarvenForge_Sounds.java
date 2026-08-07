package dwarven_forge.registry;

import dwarven_forge.DwarvenForgeMain;
import dwarven_forge.DwarvenForge_Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;

public class DwarvenForge_Sounds {

    public static DeferredHolder<SoundEvent, SoundEvent> DWARF_AMBIENT;

    public static DeferredHolder<SoundEvent, SoundEvent> DWARF_TRADE;

    public static DeferredHolder<SoundEvent, SoundEvent> DWARF_HURT;

    public static DeferredHolder<SoundEvent, SoundEvent> DWARF_DEATH;

    public static DeferredHolder<SoundEvent, SoundEvent> DWARF_YES;

    public static DeferredHolder<SoundEvent, SoundEvent> DWARF_NO;

    public static DeferredHolder<SoundEvent, SoundEvent> DWARF_CELEBRATE;


    public static void initSounds() {

        DWARF_AMBIENT=register("dwarf_ambient");
        DWARF_TRADE=register("dwarf_trade");
        DWARF_HURT=register("dwarf_hurt");
        DWARF_DEATH=register("dwarf_death");
        DWARF_YES=register("dwarf_yes");
        DWARF_NO=register("dwarf_no");
        DWARF_CELEBRATE=register("dwarf_celebrate");
    }

    public static DeferredHolder<SoundEvent, SoundEvent> register(String id){
        return DwarvenForge_Registries.SOUND_EVENTS.register(id, () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(DwarvenForgeMain.MOD_ID, id)));
    }
}
