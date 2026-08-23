package dwarven_forge;

import net.minecraft.ChatFormatting;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Sends each player a personal chat message when they enter or leave a generated dwarven
 * settlement, naming it. Uses per-player edge-triggered state (only messages on an actual
 * transition, never while stationary) plus spatial hysteresis: once inside a settlement, the
 * player must walk past its bounding box PLUS {@link #EXIT_MARGIN} blocks before being
 * considered "left", so grazing the settlement's edge doesn't spam messages back and forth.
 * <p>
 * The settlement's name is picked deterministically from its origin chunk plus the world seed
 * (see {@link DwarvenForge_SettlementNames}), so the same generated settlement always gets the
 * same name across restarts without needing any persistent storage.
 */
@EventBusSubscriber(modid = DwarvenForgeMain.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class DwarvenForge_SettlementTracker {

    private static final ResourceKey<Structure> DWARVEN_VILLAGE = ResourceKey.create(
            Registries.STRUCTURE, ResourceLocation.fromNamespaceAndPath(DwarvenForgeMain.MOD_ID, "dwarven_village"));

    private static final int EXIT_MARGIN = 16;
    private static final int CHECK_INTERVAL_TICKS = 20;

    // Value is the origin chunk of the settlement the player is currently considered "inside" -
    // doubles as both the "was inside" flag (null = outside) and the key to name that settlement.
    private static final Map<UUID, ChunkPos> CURRENT_SETTLEMENT = new ConcurrentHashMap<>();

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        if (player.tickCount % CHECK_INTERVAL_TICKS != 0) {
            return;
        }

        ServerLevel level = player.serverLevel();
        Structure structure = level.registryAccess().registryOrThrow(Registries.STRUCTURE).getOrThrow(DWARVEN_VILLAGE);

        ChunkPos wasInside = CURRENT_SETTLEMENT.get(player.getUUID());
        ChunkPos nowInside = findEnclosingSettlement(level, player, structure, wasInside);

        if (!Objects.equals(wasInside, nowInside)) {
            if (nowInside != null) {
                CURRENT_SETTLEMENT.put(player.getUUID(), nowInside);
                sendSettlementMessage(player, true, nowInside, level);
            } else {
                CURRENT_SETTLEMENT.remove(player.getUUID());
                sendSettlementMessage(player, false, wasInside, level);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        CURRENT_SETTLEMENT.remove(event.getEntity().getUUID());
    }

    /**
     * Returns the origin chunk of the settlement the player is currently inside, or null if
     * outside all of them. The settlement the player was already tracked as being inside (if
     * any) is checked against its bounding box inflated by {@link #EXIT_MARGIN}; every other
     * nearby settlement is checked against its real bounding box - the hysteresis behavior.
     */
    private static ChunkPos findEnclosingSettlement(ServerLevel level, ServerPlayer player, Structure structure, ChunkPos wasInside) {
        for (StructureStart start : level.structureManager().startsForStructure(SectionPos.of(player.blockPosition()), structure)) {
            boolean isTrackedSettlement = start.getChunkPos().equals(wasInside);
            BoundingBox box = isTrackedSettlement ? start.getBoundingBox().inflatedBy(EXIT_MARGIN) : start.getBoundingBox();
            if (box.isInside(player.blockPosition())) {
                return start.getChunkPos();
            }
        }

        return null;
    }

    private static void sendSettlementMessage(ServerPlayer player, boolean entered, ChunkPos settlementOrigin, ServerLevel level) {
        String messageKey = entered ? "message.dwarven_forge.settlement.enter" : "message.dwarven_forge.settlement.leave";
        Component name = Component.translatable(nameKeyFor(settlementOrigin, level));
        player.sendSystemMessage(Component.translatable(messageKey, name).withStyle(ChatFormatting.YELLOW));
    }

    private static String nameKeyFor(ChunkPos settlementOrigin, ServerLevel level) {
        return DwarvenForge_SettlementNamesData.get(level).getOrAssignName(settlementOrigin, level.getSeed());
    }
}
