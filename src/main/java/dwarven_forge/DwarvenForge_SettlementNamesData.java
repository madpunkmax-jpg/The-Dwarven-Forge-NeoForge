package dwarven_forge;

import dwarven_forge.registry.DwarvenForge_SettlementNames;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Persists which settlement (by its structure's origin chunk) has been assigned which name, so
 * that two settlements never end up sharing a name - something a purely position-derived hash
 * can't guarantee on its own, since it has no way to know what any other settlement already got.
 * Each settlement's name is still picked starting from its deterministic (origin chunk, seed)
 * hash for flavor/reproducibility, but the assignment walks forward past any name already taken
 * by another settlement. Only once every name in {@link DwarvenForge_SettlementNames#NAME_KEYS}
 * is already in use does a new settlement fall back to sharing one.
 */
public class DwarvenForge_SettlementNamesData extends SavedData {

    private static final String DATA_NAME = "dwarven_forge_settlement_names";

    public static final SavedData.Factory<DwarvenForge_SettlementNamesData> FACTORY = new SavedData.Factory<>(
            DwarvenForge_SettlementNamesData::new, DwarvenForge_SettlementNamesData::load);

    private final Map<Long, String> assignedNames = new HashMap<>();

    public static DwarvenForge_SettlementNamesData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(FACTORY, DATA_NAME);
    }

    public String getOrAssignName(ChunkPos settlementOrigin, long seed) {
        long key = settlementOrigin.toLong();
        String existing = assignedNames.get(key);
        if (existing != null) {
            return existing;
        }

        List<String> nameKeys = DwarvenForge_SettlementNames.NAME_KEYS;
        int startIndex = Math.floorMod(Long.hashCode(key ^ seed), nameKeys.size());
        Set<String> takenNames = new HashSet<>(assignedNames.values());

        String chosenName = nameKeys.get(startIndex);
        for (int offset = 0; offset < nameKeys.size(); offset++) {
            String candidate = nameKeys.get((startIndex + offset) % nameKeys.size());
            if (!takenNames.contains(candidate)) {
                chosenName = candidate;
                break;
            }
        }

        assignedNames.put(key, chosenName);
        setDirty();
        return chosenName;
    }

    private static DwarvenForge_SettlementNamesData load(CompoundTag tag, HolderLookup.Provider registries) {
        DwarvenForge_SettlementNamesData data = new DwarvenForge_SettlementNamesData();
        for (String key : tag.getAllKeys()) {
            data.assignedNames.put(Long.parseLong(key), tag.getString(key));
        }

        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        assignedNames.forEach((pos, name) -> tag.putString(Long.toString(pos), name));
        return tag;
    }
}
