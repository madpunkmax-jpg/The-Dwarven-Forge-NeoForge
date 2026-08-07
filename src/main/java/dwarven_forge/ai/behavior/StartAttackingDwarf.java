package dwarven_forge.ai.behavior;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.event.entity.living.LivingChangeTargetEvent;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

public class StartAttackingDwarf {

    StartAttackingDwarf(){
    }

    public static BehaviorControl<Mob> create(Predicate<Mob> predicate, Function<Mob, Optional<? extends LivingEntity>> function) {

        return BehaviorBuilder.create((livingEntityInstance) ->
                livingEntityInstance.group(livingEntityInstance.absent(MemoryModuleType.ATTACK_TARGET),
                        livingEntityInstance.registered(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE)).apply(livingEntityInstance,
                        (livingEntityMemoryAccessor, longMemoryAccessor) ->
                                (level, livingEntity, l) -> {

            if (!predicate.test(livingEntity)) {
                return false;
            } else {
                Optional<? extends LivingEntity> optional = function.apply(livingEntity);
                if (optional.isEmpty()) {
                    return false;
                } else {
                    LivingEntity livingentity = optional.get();
                    if (!livingEntity.canAttack(livingentity)) {
                        return false;
                    } else {
                        LivingChangeTargetEvent changeTargetEvent = CommonHooks.onLivingChangeTarget(livingEntity, livingentity, LivingChangeTargetEvent.LivingTargetType.BEHAVIOR_TARGET);
                        if (changeTargetEvent.isCanceled()) {
                            return false;
                        } else {
                            livingEntityMemoryAccessor.set(changeTargetEvent.getNewAboutToBeSetTarget());
                            longMemoryAccessor.erase();
                            return true;
                        }
                    }
                }
            }
        }));
    }
}
