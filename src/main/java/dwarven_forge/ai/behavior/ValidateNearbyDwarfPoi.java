package dwarven_forge.ai.behavior;

import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Holder;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Predicate;

public class ValidateNearbyDwarfPoi {
    private static final int MAX_DISTANCE = 16;

    public ValidateNearbyDwarfPoi() {
    }

    public static BehaviorControl<LivingEntity> create(Predicate<Holder<PoiType>> mainPoi, MemoryModuleType<GlobalPos> p_259635_) {
        return BehaviorBuilder.create((p_259215_) -> p_259215_.group(p_259215_.present(p_259635_)).apply(p_259215_, (p_259498_) -> (p_259843_, p_259259_, p_260036_) -> {

            Predicate<Holder<PoiType>> finalMainPoi=mainPoi.and(DwarfGoalPackages.notDwarfStations());

            GlobalPos $$6 = p_259215_.get(p_259498_);
            BlockPos $$7 = $$6.pos();
            if (p_259843_.dimension() == $$6.dimension() && $$7.closerToCenterThan(p_259259_.position(), (double)16.0F)) {
                ServerLevel $$8 = p_259843_.getServer().getLevel($$6.dimension());
                if ($$8 != null && $$8.getPoiManager().exists($$7, finalMainPoi)) {
                    if (bedIsOccupied($$8, $$7, p_259259_)) {
                        p_259498_.erase();
                        p_259843_.getPoiManager().release($$7);
                        DebugPackets.sendPoiTicketCountPacket(p_259843_, $$7);
                    }


                } else {
                    p_259498_.erase();
                }

                return true;
            } else {
                return false;
            }
        }));
    }

    private static boolean bedIsOccupied(ServerLevel p_24531_, BlockPos p_24532_, LivingEntity p_24533_) {
        BlockState $$3 = p_24531_.getBlockState(p_24532_);
        return $$3.is(BlockTags.BEDS) && $$3.getValue(BedBlock.OCCUPIED) && !p_24533_.isSleeping();
    }

    private static boolean IsNotDwarfJobSite(ServerLevel level, BlockPos blockPos, LivingEntity livingEntity) {
        BlockState blockState = level.getBlockState(blockPos);
        if(blockState.is(Blocks.BREWING_STAND)){
            return true;
        }

        return false;
    }
}
