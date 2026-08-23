package dwarven_forge.registry;

import java.util.List;

/**
 * Stable list of internal settlement-name IDs. This class only knows the slugs (how many
 * names exist and their order) - the actual display text lives entirely in the lang files
 * ({@code dwarven_forge.settlement.name.<slug>}), one entry per supported language. Adding a
 * settlement name means adding a slug here plus a translation for it in each lang file; no
 * visible text is ever hardcoded in Java.
 */
public class DwarvenForge_SettlementNames {

    public static final List<String> NAME_KEYS = List.of(
            "dwarven_forge.settlement.name.khazad_dum",
            "dwarven_forge.settlement.name.hammer_clan_stronghold",
            "dwarven_forge.settlement.name.stone_clan_stronghold",
            "dwarven_forge.settlement.name.fiery_forge_stronghold",
            "dwarven_forge.settlement.name.gear_clan_stronghold",
            "dwarven_forge.settlement.name.axe_clan_stronghold",
            "dwarven_forge.settlement.name.khar_tol",
            "dwarven_forge.settlement.name.azgad_dum",
            "dwarven_forge.settlement.name.amethyston",
            "dwarven_forge.settlement.name.emeraldon",
            "dwarven_forge.settlement.name.gar_tarnat",
            "dwarven_forge.settlement.name.white_stone",
            "dwarven_forge.settlement.name.golden_forge",
            "dwarven_forge.settlement.name.stalacton"
    );

    private DwarvenForge_SettlementNames() {
    }
}
