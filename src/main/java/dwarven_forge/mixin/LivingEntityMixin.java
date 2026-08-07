package dwarven_forge.mixin;

import dwarven_forge.entity.DwarvenForge_EntityAttributes;
import dwarven_forge.registry.DwarvenForge_Items;
import net.minecraft.world.damagesource.CombatRules;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @Unique
    LivingEntity THIS = (LivingEntity)(Object)this;


    @Inject(method = "createLivingAttributes", at = @At("RETURN"), cancellable = true)
    private static void addAttributes(CallbackInfoReturnable<AttributeSupplier.Builder> cir){
        cir.setReturnValue(cir.getReturnValue().add(DwarvenForge_EntityAttributes.GENERIC_MAGIC_RESISTANCE).add(DwarvenForge_EntityAttributes.GENERIC_EXPLOSION_RESISTANCE));
    }

    //Armor penetration
    @Unique
    private boolean hasWeaponWithArmorPenetration = false;
    @Unique
    private float initDamage = 0;
    @Inject(method ="getDamageAfterArmorAbsorb", at = @At("HEAD"))
    private void getAttackerWeaponWithPenetration(DamageSource source, float amount, CallbackInfoReturnable<Float> cir){
        initDamage=amount;
        if(source.getEntity() instanceof LivingEntity livingEntity){
            this.hasWeaponWithArmorPenetration =
                    //Has the armor penetration weapon in hand
                    livingEntity.getMainHandItem().getItem() == DwarvenForge_Items.DWARVEN_AXE.get() &&
                            //To avoid ignore armor with attacks with projectiles
                            !((source.getDirectEntity() instanceof Projectile) || (source.getDirectEntity() instanceof AbstractArrow))
                            //To avoid ignore armor when there's thorns damage
                            && !(source.typeHolder() == DamageTypes.MAGIC);
        } else {
            hasWeaponWithArmorPenetration = false;
        }
    }

    @Inject(method = "getDamageAfterArmorAbsorb", at = @At(value = "RETURN"), cancellable = true)
    private void injectArmorPenetration(DamageSource source, float damage, CallbackInfoReturnable<Float> cir){
        float armor = THIS.getArmorValue();
        float armorToughness = (float) THIS.getAttributeValue(Attributes.ARMOR_TOUGHNESS);

        if(hasWeaponWithArmorPenetration){
            //Axe
            //+25% Armor penetration
            //+10% Armour toughness penetration
            armor=           armor*(1f-0.25f);
            armorToughness = armorToughness*(1f-0.15f);

            damage=CombatRules.getDamageAfterAbsorb(THIS, initDamage, source, armor, armorToughness);

            cir.setReturnValue(damage);
        }

    }

}
