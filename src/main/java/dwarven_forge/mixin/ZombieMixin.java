package dwarven_forge.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import dwarven_forge.entity.DwarfEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Zombie;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Zombie.class)
public class ZombieMixin {

    @Inject(method = "killedEntity", at = @At(value = "INVOKE", target ="Lnet/minecraft/server/level/ServerLevel;getDifficulty()Lnet/minecraft/world/Difficulty;", ordinal = 2), cancellable = true)
    private void stopDwarvesFromConverting(ServerLevel level, LivingEntity livingEntity, CallbackInfoReturnable<Boolean> cir, @Local boolean flag){
        if(livingEntity instanceof DwarfEntity){
            cir.setReturnValue(flag);
        }

    }
}
