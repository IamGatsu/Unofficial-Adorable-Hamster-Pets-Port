# Asset Bridge: this port ships no original assets

The textures, sounds, models, animations, lang files and Patchouli content are "All Rights Reserved"
(see ASSETS_LICENSE.txt), so this project does not include them.
At runtime the mod reads them from the original jar that the player downloaded themselves
(Fabric, Minecraft 1.21.1, **exactly mod version 3.7.0**; other versions in the folder are ignored)
and adapts them in memory to 26.2.

## Building
1. Put the Patchouli 26.2 jar into `libs/` (see PORTING_NOTES_26.2.md).
2. Datagen needs the assets briefly again:
   ```
   python tools/restore_assets.py <original.jar>
   ./gradlew runDatagen
   python tools/strip_assets.py <original.jar>
   ```
   (`src/main/generated` stays afterwards; it is produced by code.)
   Genetics no longer reads palette colors from PNGs in the jar but from `data/adorablehamsterpets/genetics/color_cache.json`
   (a table of numbers, generated with `python tools/gen_color_cache.py <original.jar>`; only regenerate if the palettes change).
3. `./gradlew build`
4. Check: `unzip -l build/libs/adorablehamsterpets-*.jar | grep -E "textures/|sounds/|geckolib/|patchouli_books/|lang/|\.ogg|\.png"`
   At most the 13 self-made trim masks are allowed (see below).
   The backup of the original assets lives next to the project in `AHP_original_assets_BACKUP/` and is never published.

## For players
1. Put this jar into `mods/` (plus Fabric API, Architectury, GeckoLib, Fzzy Config, the Patchouli port).
2. Download the original jar (version 3.7.0) yourself and place it in `config/adorablehamsterpets/original/`. Do NOT put it into `mods/`.
3. Start the game. On first start `config/adorablehamsterpets/cache/ahp-assets-<hash>-<port-version>.zip` is created;
   when the port is updated, the cache is rebuilt automatically and the old one is deleted.
Without the original jar the game still runs (hamsters invisible and silent), and a notice appears on the title screen.
Dedicated servers only need this jar; clients additionally need the original.

## Structure
- `assetbridge/`: find the jar (`OriginalAssetLocator`, checks the exact version), build the pack (`AssetPackBuilder` + transformers),
  register it as a pack (`AssetBridgeRepositorySource`, `mixin/client/PackRepositoryMixin`), notice when assets are missing (`AssetBridgeNotice`).
- Transformers: `GeckoLibPathTransformer` (geo/ and animations/ -> geckolib/...), `LangKeyTransformer`
  (key.category.*), `SupportLinkLangTransformer` (support/Ko-fi menu texts), `BuiltinEntityModelTransformer` (bed item models),
  `ItemDefinitionTransformer` (items/*.json).
- Fallback without assets: entity renderer -> `NoopRenderer`; no block entity renderers/particles/shoulder layer/bed item renderer.

## Self-made files that stay in the repo
- 13 trim masks (`textures/entity/hamster/armor/trims/hamster_armor_trim_{bolt,dune,flow,host,raiser,rib,shaper,silence,snout,spire,tide,ward,wayfinder}.png`).
  Keep them only if they are not derived from original textures; otherwise delete them (fallback: the `border` mask).
- `assets/minecraft/atlases/mob_effects.json` (small atlas configuration).

## Still to verify (not compiled/tested)
`pack.mcmeta` (min_format/max_format as [major, minor], see AssetPackBuilder), signatures of `PackRepository`/`PackLocationInfo`/`PackSelectionConfig`/
`Pack.readMetaAndCreate`, `NoopRenderer`, `AlertScreen`.
