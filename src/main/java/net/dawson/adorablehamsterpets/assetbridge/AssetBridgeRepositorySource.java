package net.dawson.adorablehamsterpets.assetbridge;

import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraft.server.packs.FilePackResources;

import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Haengt das lokal gebaute Pack als immer aktives, nicht abschaltbares Pack unten in die Liste.
 * Nutzt nur Vanilla-Klassen (aehnlich FolderRepositorySource), um die API-Flaeche klein zu halten.
 * Signaturen bitte gegen 26.2 pruefen: PackLocationInfo, PackSelectionConfig, Pack.readMetaAndCreate.
 */
public final class AssetBridgeRepositorySource implements RepositorySource {
    public static final String PACK_ID = "adorablehamsterpets_original_assets";

    @Override
    public void loadPacks(Consumer<Pack> consumer) {
        Path zip = AssetBridge.packPath();
        if (zip == null) return;

        PackLocationInfo info = new PackLocationInfo(
                PACK_ID,
                Component.literal("Adorable Hamster Pets (Original-Assets)"),
                PackSource.BUILT_IN,
                Optional.empty());
        Pack pack = Pack.readMetaAndCreate(
                info,
                new FilePackResources.FileResourcesSupplier(zip),
                PackType.CLIENT_RESOURCES,
                new PackSelectionConfig(true, Pack.Position.BOTTOM, true));
        if (pack != null) {
            consumer.accept(pack);
        } else {
            AssetBridge.LOGGER.error("Asset-Pack {} konnte nicht gelesen werden (pack.mcmeta?)", zip);
        }
    }
}
