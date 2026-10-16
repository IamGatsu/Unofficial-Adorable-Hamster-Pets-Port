Installation
Dependencies

Unofficial Patchouli Port,
Fabric-API,
Architectury API 21.1.0+,
Geckolib 5.5.0+,
Fzzy_Config 0.7.7+,
Adorable Hamster Pets 3.7.0 for 1.21.1 Fabric

## Building
1. Copy the Patchouli 26.2 jar to `libs/` (see PORTING_NOTES_26.2.md).
2. Datagen needs the assets temporarily back:
python tools/restore_assets.py <original.jar>
./gradlew runDatagen
python tools/strip_assets.py <original.jar>
(`src/main/generated` remains afterwards; it is generated via code.)
Genetics no longer reads palette colors from PNGs in the jar, but from `data/adorablehamsterpets/genetics/color_cache.json` (number table, generated with `python tools/gen_color_cache.py <original.jar>`; only regenerate if palettes change).
3. `./gradlew build`
4. Verification: `unzip -l build/libs/adorablehamsterpets-*.jar | grep -E "textures/|sounds/|geckolib/|patchouli_books/|lang/|\.ogg|\.png"`
At most, the 13 self-created trim masks are allowed (see below).
The backup of the original assets is located next to the project in `AHP_original_assets_BACKUP/` and is never published.

## For Players
1. Put this jar into `mods/` (plus Fabric API, Architectury, GeckoLib, Fzzy Config, Patchouli port).
2. Download the original jar yourself and place it into `config/adorablehamsterpets/original/`. NOT into `mods/`.
3. Start the game. On the first start, `config/adorablehamsterpets/cache/ahp-assets-<hash>.zip` will be created.
Without the original jar, the game still runs (hamsters invisible and silent), and a notice appears on the title screen.
Dedicated servers only need this jar, while clients additionally need the original.
