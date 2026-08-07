package dwarven_forge.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import dwarven_forge.entity.DwarfEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.sensing.VillagerHostilesSensor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(VillagerHostilesSensor.class)
public class VillagerHostilesSensorMixin {


    @ModifyVariable(method = "isClose", at = @At("STORE"))
    private float modifyRangeForWarrior(float distance, @Local(argsOnly = true, ordinal = 0) LivingEntity dwarf){

        if(dwarf instanceof DwarfEntity dwarfEntity && dwarfEntity.isWarrior()){
            return distance*3;
        }


        return distance;
    }
}
