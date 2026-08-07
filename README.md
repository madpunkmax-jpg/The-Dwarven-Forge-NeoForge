# The Dwarven Forge (NeoForge 1.21.1)

**Main goal of this repository: porting *The Dwarven Forge* from Forge 1.20.1 to NeoForge 1.21.1.**

The original mod targets Forge 1.20.1. This repository is a from-scratch NeoForge port: registries, entities/AI, mixins, datagen, and worldgen were all re-verified against the real NeoForge 21.1.244 / Minecraft 1.21.1 API rather than assumed from the old Forge code. The full step-by-step porting log, methodology, and every non-obvious API change/gotcha encountered along the way is written up in [PORTING_PROGRESS.md](PORTING_PROGRESS.md).

## What the mod adds

A new playable-adjacent race — dwarves — along with:
- Dwarven weapons, tools and armor, craftable through normal recipes.
- A **rune system**: runes are applied to armor at a smithing table and removed at a grindstone, granting bonuses (strength, speed, armor, toughness, health), each in lesser/normal/greater tiers.
- An axe with an "armor penetration" combat mechanic.
- Dwarf villagers with professions, trading, and a jigsaw-generated underground/near-surface village structure (`dwarven_village`) that spawns them into the world.
- A dwarf-to-warrior conversion mechanic (hit an unemployed dwarf with an axe while sneaking).

## Requirements

- Minecraft 1.21.1
- NeoForge 21.1.244+
- [GeckoLib](https://github.com/bernie-g/geckolib) 4.9.2
- [JEI](https://github.com/mezz/JustEnoughItems) 19.42.0.379
- [Health Indicator TXF](https://modrinth.com/mod/health-indicator-txf) 1.21.1-1.3.12

## Building / running

```
gradlew.bat runClient   # Windows
./gradlew runClient     # Linux/macOS
```

Standard NeoForge MDK setup — see the NeoForged docs below for IDE/toolchain details.

## License

Mod code: MIT (see `mod_license` in `gradle.properties`). The MDK template scaffolding itself is under NeoForged's own MIT license (`TEMPLATE_LICENSE.txt`).

## Additional resources

- Community docs: https://docs.neoforged.net/
- NeoForged Discord: https://discord.neoforged.net/
