package dwarven_forge.ai.behavior;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import dwarven_forge.entity.DwarfEntity;
import dwarven_forge.registry.DwarvenForge_Entities;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.*;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.level.block.Blocks;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class DwarfGoalPackages {
    private static final float STROLL_SPEED_MODIFIER = 0.4F;

    public DwarfGoalPackages() {
    }

    public static Predicate<Holder<PoiType>> notDwarfStations(){
        return poiH->!(
                poiH.value().is(Blocks.BARREL.defaultBlockState()) ||
                        poiH.value().is(Blocks.LOOM.defaultBlockState()) ||
                        poiH.value().is(Blocks.FLETCHING_TABLE.defaultBlockState()) ||
                        poiH.value().is(Blocks.LECTERN.defaultBlockState()) ||
                        poiH.value().is(Blocks.CARTOGRAPHY_TABLE.defaultBlockState()) ||
                        poiH.value().is(Blocks.BREWING_STAND.defaultBlockState()) ||
                        poiH.value().is(Blocks.LOOM.defaultBlockState()) ||
                        poiH.value().is(Blocks.BLAST_FURNACE.defaultBlockState()) ||
                        poiH.value().is(Blocks.GRINDSTONE.defaultBlockState())
        );
    }

    public static ImmutableList<Pair<Integer, ? extends BehaviorControl<? super Villager>>> getCorePackage(VillagerProfession profession, float speed) {
        return ImmutableList.of(
                // Priority 0
                Pair.of(0, new Swim(0.8F)),
                Pair.of(0, InteractWithDoor.create()),
                Pair.of(0, new LookAtTargetSink(45, 90)),
                Pair.of(0, new VillagerPanicTrigger()),
                Pair.of(0, WakeUp.create()),
                Pair.of(0, ReactToBell.create()),
                Pair.of(0, SetRaidStatus.create()),
                Pair.of(0, ValidateNearbyDwarfPoi.create(profession.heldJobSite(), MemoryModuleType.JOB_SITE)),
                Pair.of(0, ValidateNearbyDwarfPoi.create(profession.acquirableJobSite(), MemoryModuleType.POTENTIAL_JOB_SITE)),

                // Priority 1
                Pair.of(1, new MoveToTargetSink()),

                // Priority 2
                Pair.of(2, PoiCompetitorScan.create()),
                Pair.of(2, StartAttackingDwarf.create(dwarf-> !dwarf.isBaby(), DwarfGoalPackages::findNearestValidAttackTarget)),

                // Priority 3
                Pair.of(3, new LookAndFollowTradingPlayerSink(speed)),

                // Priority 5
                Pair.of(5, GoToWantedItem.create(speed, false, 4)),

                // Priority 6
                Pair.of(6, AcquireDwarfPoi.create(
                        profession.acquirableJobSite(),
                        MemoryModuleType.JOB_SITE,
                        MemoryModuleType.POTENTIAL_JOB_SITE,
                        true,
                        Optional.empty()
                )),

                // Priority 7
                Pair.of(7, new GoToPotentialJobSite(speed)),

                // Priority 8
                Pair.of(8, YieldJobSite.create(speed)),

                // Priority 10
                Pair.of(10, AcquirePoi.create(
                        poi -> poi.is(PoiTypes.HOME),
                        MemoryModuleType.HOME,
                        false,
                        Optional.of((byte)14)
                )),
                Pair.of(10, AcquirePoi.create(
                        poi -> poi.is(PoiTypes.MEETING),
                        MemoryModuleType.MEETING_POINT,
                        true,
                        Optional.of((byte)14)
                )),
                Pair.of(10, AssignProfessionFromJobSite.create()),
                Pair.of(10, ResetProfession.create())
        );
    }

    public static ImmutableList<Pair<Integer, ? extends BehaviorControl<? super Villager>>> getCoreWarriorPackage(VillagerProfession profession, float speed) {
        return ImmutableList.of(
                // Priority 0
                Pair.of(0, new Swim(0.8F)),
                Pair.of(0, InteractWithDoor.create()),
                Pair.of(0, new LookAtTargetSink(45, 90)),
//                Pair.of(0, new VillagerPanicTrigger()),
                Pair.of(0, WakeUp.create()),
//                Pair.of(0, ReactToBell.create()),
//                Pair.of(0, SetRaidStatus.create()),

                // Priority 1
                Pair.of(1, new MoveToTargetSink()),

//                Pair.of(2, PoiCompetitorScan.create()),
                Pair.of(2, StartAttackingDwarf.create(dwarf->true, DwarfGoalPackages::findNearestValidWarriorAttackTarget)),

                Pair.of(3, new LookAndFollowTradingPlayerSink(speed)),

                Pair.of(5, GoToWantedItem.create(speed, false, 4)),

                Pair.of(10, AcquirePoi.create(
                        poi -> poi.is(PoiTypes.HOME),
                        MemoryModuleType.HOME,
                        false,
                        Optional.of((byte)14)
                )),

                Pair.of(10, AcquirePoi.create(
                        poi -> poi.is(PoiTypes.MEETING),
                        MemoryModuleType.MEETING_POINT,
                        true,
                        Optional.of((byte)14)
                ))
        );
    }



    public static ImmutableList<Pair<Integer, ? extends BehaviorControl<? super Villager>>> getWorkPackage(VillagerProfession profession, float p_24591_) {
        WorkAtPoi $$2;
        if (profession == VillagerProfession.FARMER) {
            $$2 = new WorkAtComposter();
        } else {
            $$2 = new WorkAtPoi();
        }


        return ImmutableList.of(
                getMinimalLookBehavior(),

                Pair.of(5, new RunOne<>(ImmutableList.of(
                        Pair.of($$2, 7),
                        Pair.of(StrollAroundPoi.create(MemoryModuleType.JOB_SITE, 0.4F, 4), 2),
                        Pair.of(StrollToPoi.create(MemoryModuleType.JOB_SITE, 0.4F, 1, 10), 5),
                        Pair.of(StrollToPoiList.create(MemoryModuleType.SECONDARY_JOB_SITE, p_24591_, 1, 6, MemoryModuleType.JOB_SITE), 5),
                        Pair.of(new HarvestFarmland(), profession == VillagerProfession.FARMER ? 2 : 5),
                        Pair.of(new UseBonemeal(), profession == VillagerProfession.FARMER ? 4 : 7)
                ))),

                Pair.of(10, new ShowTradesToPlayer(400, 1600)),

                Pair.of(10, SetLookAndInteract.create(EntityType.PLAYER, 4)),

                Pair.of(2, SetWalkTargetFromBlockMemory.create(
                        MemoryModuleType.JOB_SITE, p_24591_, 9, 100, 1200
                )),

                Pair.of(3, new GiveGiftToHero(100)),

                Pair.of(99, UpdateActivityFromSchedule.create())
        );
    }

    public static ImmutableList<Pair<Integer, ? extends BehaviorControl<? super Villager>>> getPlayPackage(float p_24584_) {
        return ImmutableList.of(
                Pair.of(0, new MoveToTargetSink(80, 120)),
                getFullLookBehavior(),
                Pair.of(5, PlayTagWithOtherKids.create()),
                Pair.of(5, new RunOne<>(
                        ImmutableMap.of(MemoryModuleType.VISIBLE_VILLAGER_BABIES, MemoryStatus.VALUE_ABSENT),
                        ImmutableList.of(
                                Pair.of(InteractWith.of(DwarvenForge_Entities.DWARF.get(), 8, MemoryModuleType.INTERACTION_TARGET, p_24584_, 2), 2),
                                Pair.of(InteractWith.of(EntityType.CAT, 8, MemoryModuleType.INTERACTION_TARGET, p_24584_, 2), 1),
                                Pair.of(VillageBoundRandomStroll.create(p_24584_), 1),
                                Pair.of(SetWalkTargetFromLookTarget.create(p_24584_, 2), 1),
                                Pair.of(new JumpOnBed(p_24584_), 2),
                                Pair.of(new DoNothing(20, 40), 2)
                        ))),
                Pair.of(99, UpdateActivityFromSchedule.create())
        );
    }

    public static ImmutableList<Pair<Integer, ? extends BehaviorControl<? super Villager>>> getRestPackage(VillagerProfession p_24593_, float p_24594_) {
        return ImmutableList.of(
                Pair.of(2, SetWalkTargetFromBlockMemory.create(
                        MemoryModuleType.HOME, p_24594_, 1, 150, 1200)),

                Pair.of(3, ValidateNearbyPoi.create(
                        poi -> poi.is(PoiTypes.HOME), MemoryModuleType.HOME)),
                Pair.of(3, new SleepInBed()),

                Pair.of(5, new RunOne<>(
                        ImmutableMap.of(MemoryModuleType.HOME, MemoryStatus.VALUE_ABSENT),
                        ImmutableList.of(
                                Pair.of(SetClosestHomeAsWalkTarget.create(p_24594_), 1),
                                Pair.of(InsideBrownianWalk.create(p_24594_), 4),
                                Pair.of(GoToClosestVillage.create(p_24594_, 4), 2),
                                Pair.of(new DoNothing(20, 40), 2)
                        ))),

                getMinimalLookBehavior(),

                Pair.of(99, UpdateActivityFromSchedule.create())
        );
    }

    public static ImmutableList<Pair<Integer, ? extends BehaviorControl<? super Villager>>> getMeetWarriorPackage(VillagerProfession profession, float p_24597_) {
        return ImmutableList.of(
                Pair.of(2, TriggerGate.triggerOneShuffled(ImmutableList.of(
                        Pair.of(StrollAroundPoi.create(MemoryModuleType.MEETING_POINT, 0.4F, 40), 2),
                        Pair.of(SocializeAtBell.create(), 2)
                ))),

                Pair.of(10, SetLookAndInteract.create(EntityType.PLAYER, 4)),

                Pair.of(2, SetWalkTargetFromBlockMemory.create(
                        MemoryModuleType.MEETING_POINT, p_24597_, 6, 100, 200
                )),

                Pair.of(3, new GiveGiftToHero(100)),

                Pair.of(3, ValidateNearbyPoi.create(
                        (p_217493_) -> p_217493_.is(PoiTypes.MEETING), MemoryModuleType.MEETING_POINT
                )),

                Pair.of(3, new GateBehavior<>(
                        ImmutableMap.of(),
                        ImmutableSet.of(MemoryModuleType.INTERACTION_TARGET),
                        GateBehavior.OrderPolicy.ORDERED,
                        GateBehavior.RunningPolicy.RUN_ONE,
                        ImmutableList.of(Pair.of(new DwarfTradeWithVillager(), 1))
                )),

                getFullLookBehavior(),

                Pair.of(99, UpdateActivityFromSchedule.create())
        );
    }


    public static ImmutableList<Pair<Integer, ? extends BehaviorControl<? super Villager>>> getMeetPackage(VillagerProfession profession, float p_24597_) {
        return ImmutableList.of(
                Pair.of(2, TriggerGate.triggerOneShuffled(ImmutableList.of(
                        Pair.of(StrollAroundPoi.create(MemoryModuleType.MEETING_POINT, 0.4F, 40), 2),
                        Pair.of(SocializeAtBell.create(), 2)
                ))),

                Pair.of(10, new ShowTradesToPlayer(400, 1600)),
                Pair.of(10, SetLookAndInteract.create(EntityType.PLAYER, 4)),

                Pair.of(2, SetWalkTargetFromBlockMemory.create(
                        MemoryModuleType.MEETING_POINT, p_24597_, 6, 100, 200
                )),

                Pair.of(3, new GiveGiftToHero(100)),

                Pair.of(3, ValidateNearbyPoi.create(
                        (p_217493_) -> p_217493_.is(PoiTypes.MEETING), MemoryModuleType.MEETING_POINT
                )),

                Pair.of(3, new GateBehavior<>(
                        ImmutableMap.of(),
                        ImmutableSet.of(MemoryModuleType.INTERACTION_TARGET),
                        GateBehavior.OrderPolicy.ORDERED,
                        GateBehavior.RunningPolicy.RUN_ONE,
                        ImmutableList.of(Pair.of(new DwarfTradeWithVillager(), 1))
                )),

                getFullLookBehavior(),

                Pair.of(99, UpdateActivityFromSchedule.create())
        );
    }

    public static ImmutableList<Pair<Integer, ? extends BehaviorControl<? super Villager>>> getIdleWarriorPackage(VillagerProfession profession, float p_24600_) {
        return ImmutableList.of(

                Pair.of(2, new RunOne<>(ImmutableList.of(
                        Pair.of(InteractWith.of(DwarvenForge_Entities.DWARF.get(), 8, MemoryModuleType.INTERACTION_TARGET, p_24600_, 2), 2),
                        Pair.of(InteractWith.of(DwarvenForge_Entities.DWARF.get(), 8, AgeableMob::canBreed, AgeableMob::canBreed, MemoryModuleType.BREED_TARGET, p_24600_, 2), 1),
                        Pair.of(InteractWith.of(EntityType.CAT, 8, MemoryModuleType.INTERACTION_TARGET, p_24600_, 2), 1),
                        Pair.of(VillageBoundRandomStroll.create(p_24600_), 1),
                        Pair.of(SetWalkTargetFromLookTarget.create(p_24600_, 2), 1),
                        Pair.of(new JumpOnBed(p_24600_), 1),
                        Pair.of(new DoNothing(30, 60), 1)
                ))),

                Pair.of(3, new GiveGiftToHero(100)),
                Pair.of(3, SetLookAndInteract.create(EntityType.PLAYER, 4)),

                Pair.of(3, new GateBehavior<>(
                        ImmutableMap.of(),
                        ImmutableSet.of(MemoryModuleType.INTERACTION_TARGET),
                        GateBehavior.OrderPolicy.ORDERED,
                        GateBehavior.RunningPolicy.RUN_ONE,
                        ImmutableList.of(Pair.of(new DwarfTradeWithVillager(), 1))
                )),

                Pair.of(3, new GateBehavior<>(
                        ImmutableMap.of(),
                        ImmutableSet.of(MemoryModuleType.BREED_TARGET),
                        GateBehavior.OrderPolicy.ORDERED,
                        GateBehavior.RunningPolicy.RUN_ONE,
                        ImmutableList.of(Pair.of(new DwarfMakeLove(), 1))
                )),

                getFullLookBehavior(),

                Pair.of(99, UpdateActivityFromSchedule.create())
        );
    }


    public static ImmutableList<Pair<Integer, ? extends BehaviorControl<? super Villager>>> getIdlePackage(VillagerProfession profession, float p_24600_) {
        return ImmutableList.of(
                Pair.of(2, new RunOne<>(ImmutableList.of(
                        Pair.of(InteractWith.of(DwarvenForge_Entities.DWARF.get(), 8, MemoryModuleType.INTERACTION_TARGET, p_24600_, 2), 2),
                        Pair.of(InteractWith.of(DwarvenForge_Entities.DWARF.get(), 8, AgeableMob::canBreed, AgeableMob::canBreed, MemoryModuleType.BREED_TARGET, p_24600_, 2), 1),
                        Pair.of(InteractWith.of(EntityType.CAT, 8, MemoryModuleType.INTERACTION_TARGET, p_24600_, 2), 1),
                        Pair.of(VillageBoundRandomStroll.create(p_24600_), 1),
                        Pair.of(SetWalkTargetFromLookTarget.create(p_24600_, 2), 1),
                        Pair.of(new JumpOnBed(p_24600_), 1),
                        Pair.of(new DoNothing(30, 60), 1)
                ))),

                Pair.of(3, new GiveGiftToHero(100)),
                Pair.of(3, SetLookAndInteract.create(EntityType.PLAYER, 4)),
                Pair.of(3, new ShowTradesToPlayer(400, 1600)),

                Pair.of(3, new GateBehavior<>(
                        ImmutableMap.of(),
                        ImmutableSet.of(MemoryModuleType.INTERACTION_TARGET),
                        GateBehavior.OrderPolicy.ORDERED,
                        GateBehavior.RunningPolicy.RUN_ONE,
                        ImmutableList.of(Pair.of(new DwarfTradeWithVillager(), 1))
                )),

                Pair.of(3, new GateBehavior<>(
                        ImmutableMap.of(),
                        ImmutableSet.of(MemoryModuleType.BREED_TARGET),
                        GateBehavior.OrderPolicy.ORDERED,
                        GateBehavior.RunningPolicy.RUN_ONE,
                        ImmutableList.of(Pair.of(new DwarfMakeLove(), 1))
                )),

                getFullLookBehavior(),

                Pair.of(99, UpdateActivityFromSchedule.create())
        );
    }

    public static ImmutableList<Pair<Integer, ? extends BehaviorControl<? super Villager>>> getPanicPackage(VillagerProfession p_24602_, float p_24603_) {
        float $$2 = p_24603_ * 1.5F;
        return ImmutableList.of(Pair.of(0, VillagerCalmDown.create()), Pair.of(1, SetWalkTargetAwayFrom.entity(MemoryModuleType.NEAREST_HOSTILE, $$2, 6, false)), Pair.of(1, SetWalkTargetAwayFrom.entity(MemoryModuleType.HURT_BY_ENTITY, $$2, 6, false)), Pair.of(3, VillageBoundRandomStroll.create($$2, 2, 2)), getMinimalLookBehavior());
    }

    public static ImmutableList<Pair<Integer, ? extends BehaviorControl<? super Villager>>> getPreRaidPackage(VillagerProfession p_24605_, float p_24606_) {
        return ImmutableList.of(Pair.of(0, RingBell.create()), Pair.of(0, TriggerGate.triggerOneShuffled(ImmutableList.of(Pair.of(SetWalkTargetFromBlockMemory.create(MemoryModuleType.MEETING_POINT, p_24606_ * 1.5F, 2, 150, 200), 6), Pair.of(VillageBoundRandomStroll.create(p_24606_ * 1.5F), 2)))), getMinimalLookBehavior(), Pair.of(99, ResetRaidStatus.create()));
    }

    public static ImmutableList<Pair<Integer, ? extends BehaviorControl<? super Villager>>> getRaidPackage(VillagerProfession profession, float p_24609_, boolean isWarrior) {
//
//        if(isWarrior){
//            return ImmutableList.of(
//                    Pair.of(0, BehaviorBuilder.sequence(
//                            BehaviorBuilder.triggerIf(DwarfGoalPackages::raidExistsAndNotVictory),
//                            TriggerGate.triggerOneShuffled(ImmutableList.of(
//                                    Pair.of(MoveToSkySeeingSpot.create(p_24609_), 5),
//                                    Pair.of(VillageBoundRandomStroll.create(p_24609_ * 1.1F), 2)
//                            ))
//                    )),
//
//                    Pair.of(0, new CelebrateVillagersSurvivedRaid(600, 600)),
//
//                    Pair.of(2, BehaviorBuilder.sequence(
//                            BehaviorBuilder.triggerIf(DwarfGoalPackages::raidExistsAndActive),
//                            LocateHidingPlace.create(24, p_24609_ * 1.4F, 1)
//                    )),
//
//                    getMinimalLookBehavior(),
//
//                    Pair.of(99, ResetRaidStatus.create())
//            );
//        }

        return ImmutableList.of(
                Pair.of(0, BehaviorBuilder.sequence(
                        BehaviorBuilder.triggerIf(DwarfGoalPackages::raidExistsAndNotVictory),
                        TriggerGate.triggerOneShuffled(ImmutableList.of(
                                Pair.of(MoveToSkySeeingSpot.create(p_24609_), 5),
                                Pair.of(VillageBoundRandomStroll.create(p_24609_ * 1.1F), 2)
                        ))
                )),

                Pair.of(0, new CelebrateVillagersSurvivedRaid(600, 600)),

                Pair.of(2, BehaviorBuilder.sequence(
                        BehaviorBuilder.triggerIf(DwarfGoalPackages::raidExistsAndActive),
                        LocateHidingPlace.create(24, p_24609_ * 1.4F, 1)
                )),

                getMinimalLookBehavior(),

                Pair.of(99, ResetRaidStatus.create())
        );
    }

    public static ImmutableList<Pair<Integer, ? extends BehaviorControl<? super Villager>>> getHidePackage(VillagerProfession profession, float p_24612_) {

        return ImmutableList.of(
                Pair.of(0, SetHiddenState.create(15, 3)),
                Pair.of(1, LocateHidingPlace.create(32, p_24612_ * 1.25F, 2)),
                getMinimalLookBehavior()
        );
    }

    private static Pair<Integer, BehaviorControl<LivingEntity>> getFullLookBehavior() {
        return Pair.of(5, new RunOne<>(ImmutableList.of(Pair.of(SetEntityLookTarget.create(EntityType.CAT, 8.0F), 8), Pair.of(SetEntityLookTarget.create(EntityType.VILLAGER, 8.0F), 2), Pair.of(SetEntityLookTarget.create(DwarvenForge_Entities.DWARF.get(), 8.0F), 2), Pair.of(SetEntityLookTarget.create(EntityType.PLAYER, 8.0F), 2), Pair.of(SetEntityLookTarget.create(MobCategory.CREATURE, 8.0F), 1), Pair.of(SetEntityLookTarget.create(MobCategory.WATER_CREATURE, 8.0F), 1), Pair.of(SetEntityLookTarget.create(MobCategory.AXOLOTLS, 8.0F), 1), Pair.of(SetEntityLookTarget.create(MobCategory.UNDERGROUND_WATER_CREATURE, 8.0F), 1), Pair.of(SetEntityLookTarget.create(MobCategory.WATER_AMBIENT, 8.0F), 1), Pair.of(SetEntityLookTarget.create(MobCategory.MONSTER, 8.0F), 1), Pair.of(new DoNothing(30, 60), 2))));
    }

    private static Pair<Integer, BehaviorControl<LivingEntity>> getMinimalLookBehavior() {
        return Pair.of(5, new RunOne<>(ImmutableList.of(Pair.of(SetEntityLookTarget.create(EntityType.VILLAGER, 8.0F), 2), Pair.of(SetEntityLookTarget.create(DwarvenForge_Entities.DWARF.get(), 8.0F), 2), Pair.of(SetEntityLookTarget.create(EntityType.PLAYER, 8.0F), 2), Pair.of(new DoNothing(30, 60), 8))));
    }

    private static boolean raidExistsAndActive(ServerLevel level, LivingEntity livingEntity) {
        Raid raid = level.getRaidAt(livingEntity.blockPosition());
        return raid != null && raid.isActive() && !raid.isVictory() && !raid.isLoss();
    }

    private static boolean raidExistsAndNotVictory(ServerLevel p_259939_, LivingEntity p_259384_) {
        Raid $$2 = p_259939_.getRaidAt(p_259384_.blockPosition());
        return $$2 != null && $$2.isVictory();
    }

    public static ImmutableList<? extends BehaviorControl<? super Villager>> initFightActivity(DwarfEntity dwarf) {
        return ImmutableList.of(
                StopAttackingIfTargetInvalid.create(
                        (target) -> !isNearestValidAttackTarget(dwarf, target)
                ),
                SetWalkTargetFromAttackTargetIfTargetOutOfReach.create(0.7F),
                MeleeAttack.create(20)
        );
    }

    private static boolean isNearestValidAttackTarget(DwarfEntity dwarf, LivingEntity target) {
        return findNearestValidAttackTarget(dwarf).filter((entity) -> entity == target).isPresent();
    }

    private static Optional<? extends LivingEntity> findNearestValidAttackTarget(Mob dwarf) {
        Brain<?> brain = dwarf.getBrain();

        Optional<LivingEntity> optional = BehaviorUtils.getLivingEntityFromUUIDMemory(dwarf, MemoryModuleType.ANGRY_AT);

        if (optional.isPresent() && Sensor.isEntityAttackableIgnoringLineOfSight(dwarf, optional.get())) {
            return optional;
        }
        else if (brain.getMemory(MemoryModuleType.NEAREST_HOSTILE).isPresent()){
            return brain.getMemory(MemoryModuleType.NEAREST_HOSTILE);
        }
        else {
            return brain.getMemory(MemoryModuleType.ATTACK_TARGET);
        }
    }

    // Warriors additionally hunt down anything vanilla's Iron Golem would attack on sight
    // (any Enemy except Creeper, see IronGolem's Mob-class NearestAttackableTargetGoal), on
    // top of the reactive anger/hostile-sensor targeting every dwarf already has.
    private static Optional<? extends LivingEntity> findNearestValidWarriorAttackTarget(Mob dwarf) {
        Optional<? extends LivingEntity> reactiveTarget = findNearestValidAttackTarget(dwarf);
        return reactiveTarget.isPresent() ? reactiveTarget : findNearestVisibleEnemy(dwarf);
    }

    private static Optional<LivingEntity> findNearestVisibleEnemy(Mob dwarf) {
        return dwarf.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES)
                .flatMap(nearestVisibleLivingEntities -> nearestVisibleLivingEntities.findClosest(
                        entity -> entity instanceof Enemy && !(entity instanceof Creeper) && Sensor.isEntityAttackableIgnoringLineOfSight(dwarf, entity)
                ));
    }

    public static void wasHurtBy(DwarfEntity dwarf, LivingEntity enemy) {
        if (!(enemy instanceof DwarfEntity)) {
            Brain<?> brain = dwarf.getBrain();

            if (dwarf.isBaby()) {
                brain.setMemoryWithExpiry(MemoryModuleType.AVOID_TARGET, enemy, 100L);
                if (Sensor.isEntityAttackableIgnoringLineOfSight(dwarf, enemy)) {
                    broadcastAngerTarget(dwarf, enemy);
                }
            } else {
                maybeRetaliate(dwarf, enemy);
            }
        }

    }

    protected static void maybeRetaliate(DwarfEntity dwarf, LivingEntity enemy) {
        if (!dwarf.getBrain().isActive(Activity.AVOID) && Sensor.isEntityAttackableIgnoringLineOfSight(dwarf, enemy) && !BehaviorUtils.isOtherTargetMuchFurtherAwayThanCurrentAttackTarget(dwarf, enemy, 4.0F)) {

            setAngerTarget(dwarf, enemy);
            broadcastAngerTarget(dwarf, enemy);
        }
    }

    private static void setAngerTargetToNearestTargetablePlayerIfFound(DwarfEntity p_34945_, LivingEntity p_34946_) {
        Optional<Player> optional = getNearestVisibleTargetablePlayer(p_34945_);
        if (optional.isPresent()) {
            setAngerTarget(p_34945_, optional.get());
        } else {
            setAngerTarget(p_34945_, p_34946_);
        }
    }

    protected static void setAngerTarget(DwarfEntity dwarf, LivingEntity target) {
        if (Sensor.isEntityAttackableIgnoringLineOfSight(dwarf, target)) {
            dwarf.getBrain().eraseMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
            dwarf.getBrain().setMemoryWithExpiry(MemoryModuleType.ANGRY_AT, target.getUUID(), 600L);
        }

    }

    public static Optional<Player> getNearestVisibleTargetablePlayer(DwarfEntity brain) {
        return brain.getBrain().hasMemoryValue(MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER) ? brain.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER) : Optional.empty();
    }

    protected static void broadcastAngerTarget(DwarfEntity dwarf, LivingEntity entity) {
        //Alert the guards in a 128 blocks area
        List<DwarfEntity> list = dwarf.level().getEntitiesOfClass(DwarfEntity.class, dwarf.getBoundingBox().inflate(128.0F));

        list.forEach((dwarf1) -> {
            setAngerTargetIfCloserThanCurrent(dwarf1, entity);
        });

    }

    private static void setAngerTargetIfCloserThanCurrent(DwarfEntity dwarf, LivingEntity target) {
        Optional<LivingEntity> optional = BehaviorUtils.getLivingEntityFromUUIDMemory(dwarf, MemoryModuleType.ANGRY_AT);
        LivingEntity livingentity = BehaviorUtils.getNearestTarget(dwarf, optional, target);
        if (optional.isEmpty() || optional.get() != livingentity) {
            setAngerTarget(dwarf, livingentity);
        }

    }
}
