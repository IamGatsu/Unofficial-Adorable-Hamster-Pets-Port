# Port to Minecraft 26.2 (Fabric only)

## Build
- **Patchouli (required):** copy your self-built Patchouli 26.2 port (`patchouli-26.2-94-fabric.jar` from its
  `build/libs/`) into `libs/`. The same jar must also be installed in the game.
- `./gradlew build` (Java 25, Gradle 9.5.1, Fabric Loom 1.17, no mappings – 26.2 is unobfuscated)
- Datagen: `./gradlew runDatagen` writes to `src/main/generated` (models, item definitions, recipes, loot, tags, lang).
  Run it once before the first build, otherwise models/recipes are missing from the jar.
- Versions are defined in `gradle.properties` (please check Mod Menu / Jade against your versions).

## Removed / disabled
- Forge/NeoForge/Architectury multiloader structure removed (Architectury API stays as a runtime dependency).
- Trinkets/Accessories integration: `disabled/optional-integrations/`.
- Iris integration is active again (`integration/iris/IrisIntegration`): registers the PBR loader for the
  generated hamster textures via reflection – no Iris jar needed to build, tested against the Iris 1.11.4 (26.2) API.
  Important: Iris 26.x only knows a DynamicTexture after `getTexture()` has been called; therefore
  `HamsterTextureUtil` calls it after registering (otherwise no hamster PBR and no glowing trims).
- Active again: hiding the Acorn Ring in the off hand (`HeldItemRendererMixin` -> `ItemInHandRenderer#submitArmWithItem`,
  `HeldItemFeatureRendererMixin` -> `ItemInHandLayer#submitArmWithItem`) and the shadow offset while rolling
  (`EntityRenderDispatcherMixin` around `SubmitNodeCollector#submitShadow`, offset via DataTicket `HamsterRenderer.ROLL_SHADOW_OFFSET`).
- Block jiggle is active again: `BlockJiggleRenderer` (LevelExtractionEvents.END_EXTRACTION +
  LevelRenderEvents.COLLECT_SUBMITS, `submitMovingBlock` like vanilla's FallingBlockRenderer) and
  `BlockEntityRenderDispatcherMixin` (now injects into `submit` instead of `render`).

- Riders are back on the animated bone `body_child`: `HamsterPassengerLayer` (GeckoLib per-bone render, only the bone's T+R,
  seat offset, vanilla yaw neutralized). The vanilla rider is suppressed via `EntityRendererRidingTagMixin` (tag in the RenderState)
  + `EntityRenderDispatcherMixin` (cancel in `submit`). `disabled/render-mixins` now only contains
  obsolete files (replaced by Fabric API callbacks).

## Known simplifications
- Particles from animation keyframes start at an estimated nose/foot position.
- The item tag `minecraft:flowers` no longer exists -> detection via the block tag `minecraft:flowers`.
- The `random_patch` feature no longer exists -> sunflowers/bushes via SIMPLE_BLOCK + placement rules.

## Save format
- Entity/bed/player data is written through an NBT bridge in the old layout -> existing worlds remain readable.

## Armor trims
- Hamster masks exist for all 18 vanilla patterns (new: bolt, dune, flow, host, raiser, rib, shaper, silence,
  snout, spire, tide, ward, wayfinder). Patterns without their own mask use the generic `border` mask.
- Materials (incl. resin) come directly from the vanilla palettes; glowing via LabPBR specular alpha + Iris.

## Fixed porting bugs
- Owner comparisons: `getOwnerReference()` returns an `EntityReference` in 26.x, not a UUID.
  `uuid.equals(getOwnerReference())` was therefore always false (flute shoulder call, teleport rescue incl. parents,
  bed rescue while sleeping). Now done via `PetOwnershipUtil.isOwnedBy(pet, uuid)`.
- Cheese chewing sound restored via the consumable component (`Consumables.defaultFood().sound(...)`).
- Age counter 72x too fast: interval checks used `this.age` (breeding age) instead of `tickCount`.

## Asset Bridge (no original assets in the repo)
See `ASSET_BRIDGE.md`. Assets are loaded at runtime from the 1.21.1 original jar (exactly version 3.7.0) provided by the player.
Before `runDatagen`, restore the assets with `tools/restore_assets.py`, then remove them again with `tools/strip_assets.py`.
