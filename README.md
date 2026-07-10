# Asclepius

<!-- TODO: one-line tagline -->

## Overview

<!-- TODO: 2-3 sentence description of what the mod adds -->

## Features

- **Pale Altar** — ritual crafting block: transforms, socket grants, and enchantment merging, powered by a Hammer under open sky.
- **Chunk Loader** — force-loads the chunk it's placed in.
- **Forgotten Relics** — repairable, socketable items that permanently apply an effect to another item once applied.
- **Eye of Recall / Golden Eye of Recall** — charge-limited teleports to your spawn point or a linked Lodestone.
- **Hammers & Paxels** — area-of-effect mining tools.
- **Teru Teru Bozu** — a weather charm that clears rain/thunder.
- **Misc items** — Ender Key (End City vault key), Fox Amulet.

See each `feature/` package's `package-info.java` for implementation details.

## Requirements

- Minecraft `~26.2`
- Fabric Loader `>=0.19.3`
- Fabric API
- Java `>=25`

(from `fabric.mod.json`'s `depends` block)

## Installation

1. Install [Fabric Loader](https://fabricmc.net/use/) for Minecraft 26.2.
2. Install [Fabric API](https://modrinth.com/mod/fabric-api).
3. Drop the mod jar into your `mods` folder.

## Building from source

- Build: `./gradlew build`
- Run client (dev environment): `./gradlew runClient`
- Run server (dev environment): `./gradlew runServer`
- Data generation (recipes, tags, loot tables, models): `./datagen.sh` — always use this script rather than calling `runDatagen` directly, since raw datagen output must be merged into resources afterward.

## Project structure

Source is organized by feature under `feature/` (e.g. `feature.pale_altar`, `feature.forgotten_relics`), each a self-contained vertical slice (block/item/recipe/renderer as needed). Cross-cutting infrastructure — `registry/` (the composition root wiring every feature into Minecraft's registries), `mixin/`, `datagen/`, `utils/` — stays outside `feature/`. Each package's `package-info.java` documents its own boundary; that's the source of truth, not this section.

## Contributing

<!-- TODO: issue/PR process, code style expectations -->

## License

CC0-1.0 (see `LICENSE`).

## Credits

<!-- TODO -->
