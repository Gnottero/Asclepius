# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project overview

Asclepius is a Fabric mod for Minecraft (targeting Minecraft 26.2, Java 25) built on the **Polymer** library stack (`polymer-core`, `polymer-blocks`, `polymer-resource-pack`, `polymer-virtual-entity`, `polymer-autohost`) plus `factorytools`. Polymer lets the mod register custom blocks/items/block entities that are rendered to vanilla clients via server-side resource pack injection and packet trickery, without requiring players to install the mod client-side — keep this in mind when working with rendering/model code (e.g. `PolymerBlock`, `PolymerTexturedBlock`, `SimplePolymerItem`, `BlockAwareAttachment`).

## Commands

- Build: `./gradlew build`
- Run client (dev environment): `./gradlew runClient`
- Run server (dev environment): `./gradlew runServer`
- Data generation (recipes, tags, loot tables, models): `./datagen.sh` — runs `./gradlew runDatagen`, then copies generated files from `src/main/generated/{data,assets}` into `src/main/resources/{data,assets}` and deletes the generated dir. Always use this script rather than calling `runDatagen` directly, since raw datagen output must be merged into resources.
- Deploy to remote test server: `./deploy.sh` — builds the jar and scp's it (plus the polymer auto-host config) to a remote host. This file is gitignored because it embeds server SSH credentials; never commit it or print its contents.

There is no test suite in this repo.

## Architecture

**Entry point**: `Asclepius.java` (`ModInitializer`). `onInitialize()` calls each registry's `registerAll()` in a fixed order (components → blocks → block entities → items → dispenser behaviors → events → loot tables → tags → recipes), then registers the mod's resource pack assets with Polymer. When adding a new registry category, follow this same call-order pattern (register dependencies before things that reference them).

**Registries** (`registry/`): One class per vanilla registry (`AsclepiusBlocks`, `AsclepiusItems`, `AsclepiusBlockEntities`, `AsclepiusComponents`, `AsclepiusRecipes`, `AsclepiusTags`, `AsclepiusLootTables`, `AsclepiusEvents`, `AsclepiusDispenserBehaviors`). Each holds `public static final` instances built through small `register(...)` helper methods and exposes a `registerAll()` used only for side-effecting registration (Polymer/Fabric hookups) and log output — the actual `Registry.register` calls happen at static-init time via the field initializers. Follow this pattern for new content: declare the field with a `register(...)` call, don't add ad-hoc registration logic elsewhere.

**Custom data components** (`AsclepiusComponents`): mod-specific `DataComponentType`s stored on `ItemStack`s to drive gameplay systems — `REPAIRED`/`REPAIR_MATERIALS` (Forgotten Relic repair state), `MAX_SOCKETS`/`SOCKETS` (how many relics an item can hold and which), `EYE_CHARGE`. All are registered with `PolymerComponent.registerDataComponent` so they sync correctly to vanilla clients.

**Forgotten Relics system** (`item/forgotten_relics/`): `ForgottenRelicItem` is the abstract base for consumable items that permanently socket an effect onto another item.
- A relic must be "repaired" first (right-click while un-repaired rolls a random list of `REPAIR_MATERIALS` from the `FORGOTTEN_RELICS_MASS`/`FORGOTTEN_RELICS_VALUE` item tags; feeding those materials via shift-click stacking clears the requirement).
- Once repaired, shift-clicking the relic onto a target item (`overrideStackedOnOther`) applies its effect (`applyAttribute`, implemented by subclasses like `AttributeRelicItem`), adds an entry to the target's `SOCKETS` component, and appends a lore line with an atlas-sprite icon — gated by `canApplyOnItem` (target must have `requiredComponent` and free socket capacity) and `satisfiesRelicConditions`.
- `AttributeRelicItem` stacks attribute modifiers of the same attribute+operation instead of adding duplicates (additive stacking for `ADD_VALUE`/`ADD_MULTIPLIED_BASE`, multiplicative combination for `ADD_MULTIPLIED_TOTAL`).
- `ForgottenRelicsRarity` defines a level-scaled weighted roll table (deteriorated → eternal) — `calculateDynamicRarity` shifts weights toward higher rarities as player level increases.

**Pale Altar recipe system** (`recipe/`, `block/PaleAltarBlock.java`, `block/entity/PaleAltarBlockEntity.java`): a custom `Recipe<AltarRecipeInput>` type (`AltarRecipe`) takes an altar item + a catalyst item and produces a result, matched via `IngredientWithComponents` (ingredient + required data components) and gated by `AltarRecipeConditions`. `assemble()` merges enchantments from the recipe result on top of the input's existing enchantments/stored enchantments rather than overwriting them (see `mergeEnchantments`). `PaleAltarBlockEntity` stores a single `ItemStack` and pushes UI updates to its Polymer virtual-entity `Model` (`PaleAltarBlock.Model`) via `BlockAwareAttachment` whenever the stored item changes.

**Enchantment merging** (`utils/EnchantmentMerger.java`): a separate, standalone merge routine (used outside the altar recipe flow) that combines enchantments from a catalyst onto a base item, bumping matching enchantments by one level (capped at max+1), skipping incompatible enchantments, and computing an XP cost based on enchantment rarity weight and level — scales total cost slightly when multiple enchantments are merged at once. Pairs with `utils/PlayerXpUtils` for converting between player level/progress and total XP points when charging that cost.

**Hammer AoE mining** (`item/tools/HammerItem.java`, `event/HammerBreakHandler.java`): registered on `PlayerBlockBreakEvents.BEFORE`. When a hammer breaks a block (and the player isn't sneaking), it computes the 3x3 plane perpendicular to the mined face and breaks/drops all non-air blocks in that plane the tool is correct for, durability-costing the tool once per extra block broken.

**Chunk loading** (`world/ChunkLoaderManager.java`, `block/ChunkLoaderBlock.java`): an in-memory (non-persisted) registry of forced chunks keyed by dimension + `ChunkPos`, storing the set of loader `BlockPos`s responsible for forcing each chunk; a chunk is force-loaded only while at least one loader block references it, and un-forced once the last one is removed.

**Mixins**: `mixin/EndCityElytraMixin.java` injects into vanilla's End City structure piece generation (`EndCityPieces$EndCityPiece#handleDataMarker`) to replace the vanilla elytra item frame with a custom loot `VaultBlock` (dropping `AsclepiusItems.ENDER_KEY` as the vault's key item, loot table `AsclepiusLootTables.VAULT_END_SHIP`). `mixin/LiquidBlockMixin.java` + `LiquidBlockAccessor.java` are accessor/injector mixins for liquid block behavior. Client-only mixins live under `src/client/.../mixin/` and are registered in the separate `asclepius-features.client.mixins.json` config (only loaded in the `client` environment, per `fabric.mod.json`).

## Project layout notes

- Fabric's split source sets are enabled (`loom.splitEnvironmentSourceSets()`): `src/main` is common/server logic, `src/client` is client-only logic (rendering, datagen providers, client mixins). Don't put client-only rendering code in `src/main`.
- Data generation providers live in `src/client/java/.../datagen/` (`AsclepiusModelProvider`, `AsclepiusRecipeProvider`, `AsclepiusBlockTagProvider`, `AsclepiusItemTagProvider`, `AsclepiusBlockLootTableProvider`) and run through the `fabric-datagen` entrypoint declared in `fabric.mod.json`.
- Mixin configs are separate per environment: `src/main/resources/asclepius-features.mixins.json` (common) and `src/client/resources/asclepius-features.client.mixins.json` (client-only, references `com.gnottero.asclepius.mixin.client`).
