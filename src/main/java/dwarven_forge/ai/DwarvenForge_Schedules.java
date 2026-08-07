package dwarven_forge.ai;

import dwarven_forge.DwarvenForge_Registries;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.entity.schedule.Schedule;
import net.minecraft.world.entity.schedule.ScheduleBuilder;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.function.Supplier;

public class DwarvenForge_Schedules {
    public static DeferredHolder<Schedule, Schedule> DWARF_WARRIOR_SCHEDULE;

    public static void initSchedules(){
        DWARF_WARRIOR_SCHEDULE = register("dwarf_warrior", () ->
                new ScheduleBuilder(new Schedule())
                        .changeActivityAt(0,     Activity.IDLE)      // Patrol in the morning   - (06:00:00 - 10:00:00)
                        .changeActivityAt(4000,  Activity.REST)      // Midday nap              - (10:00:00 - 13:00:00)
                        .changeActivityAt(7000,  Activity.IDLE)      // Patrol at midday        - (13:00:00 - 15:00:00)
                        .changeActivityAt(9000,  Activity.MEET)      // Gather with villagers   - (15:00:00 - 16:00:00)
                        .changeActivityAt(10000, Activity.IDLE)      // Patrol in the night     - (16:00:00 - 05:00:00)
                        .changeActivityAt(23000, Activity.REST)      // Midnight nap            - (05:00:00 - 06:00:00)
                        .build());
    }

    @SuppressWarnings("all")
    protected static DeferredHolder<Schedule, Schedule> register(String name, Supplier<Schedule> supplier) {
        return DwarvenForge_Registries.SCHEDULES.register(name, supplier);
    }
}
