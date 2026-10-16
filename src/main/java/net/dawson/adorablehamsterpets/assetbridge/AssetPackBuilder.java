package net.dawson.adorablehamsterpets.assetbridge;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import java.util.Enumeration;

/**
 * Baut aus dem Original-Jar ein lokales Resource-Pack (nur im Cache-Ordner des Nutzers).
 * Dieses Zip wird nie verteilt und ist durch den SHA-1 des Originals eindeutig benannt.
 */
public final class AssetPackBuilder {
    private static final String PREFIX = "assets/adorablehamsterpets/";
    private static final String XAERO_PREFIX = "assets/xaerominimap/entity/";

    /** Reihenfolge ist wichtig, spaeter kommende Transformer sehen das Ergebnis der fruehren. */
    private static final List<AssetTransformer> TRANSFORMERS = List.of(
            new GeckoLibPathTransformer(),
            new LangKeyTransformer(),
            new SupportLinkLangTransformer(),
            new BuiltinEntityModelTransformer(),
            new ItemDefinitionTransformer()
    );

    private AssetPackBuilder() {}

    public static Path buildIfNeeded(OriginalAssetLocator.Found original) throws IOException {
        Files.createDirectories(OriginalAssetLocator.CACHE_DIR);
        // Cache-Key = Original-Jar (SHA-1) + Version dieses Ports: aktualisiert sich der Port, wird neu gebaut.
        String name = "ahp-assets-" + original.sha1() + "-" + portVersion() + ".zip";
        Path out = OriginalAssetLocator.CACHE_DIR.resolve(name);
        if (Files.isRegularFile(out)) {
            deleteStaleCaches(name);
            return out;
        }

        Map<String, byte[]> files = new LinkedHashMap<>();
        try (ZipFile zf = new ZipFile(original.jar().toFile())) {
            Enumeration<? extends ZipEntry> en = zf.entries();
            while (en.hasMoreElements()) {
                ZipEntry e = en.nextElement();
                if (e.isDirectory() || !(e.getName().startsWith(PREFIX) || e.getName().startsWith(XAERO_PREFIX))) continue;
                try (InputStream in = zf.getInputStream(e)) {
                    files.put(e.getName(), in.readAllBytes());
                }
            }
        }
        for (AssetTransformer t : TRANSFORMERS) t.apply(files);
        files.put("pack.mcmeta", packMcmeta().getBytes(StandardCharsets.UTF_8));

        Path tmp = out.resolveSibling(out.getFileName() + ".tmp");
        try (OutputStream os = Files.newOutputStream(tmp); ZipOutputStream zos = new ZipOutputStream(os)) {
            for (Map.Entry<String, byte[]> e : files.entrySet()) {
                zos.putNextEntry(new ZipEntry(e.getKey()));
                zos.write(e.getValue());
                zos.closeEntry();
            }
        }
        Files.move(tmp, out, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        AssetBridge.LOGGER.info("Asset-Pack gebaut: {} ({} Dateien)", out, files.size());
        deleteStaleCaches(name);
        return out;
    }

    /** Version dieses Ports (aus fabric.mod.json), dateinamentauglich. */
    private static String portVersion() {
        String v = net.fabricmc.loader.api.FabricLoader.getInstance()
                .getModContainer("adorablehamsterpets")
                .map(c -> c.getMetadata().getVersion().getFriendlyString())
                .orElse("unknown");
        return v.replaceAll("[^A-Za-z0-9._-]", "_");
    }

    /** Loescht alte Cache-Zips (anderer Port-Stand oder anderes Original), damit nichts veraltet/anwaechst. */
    private static void deleteStaleCaches(String keep) {
        try (var ds = Files.newDirectoryStream(OriginalAssetLocator.CACHE_DIR, "ahp-assets-*.zip*")) {
            for (Path p : ds) {
                if (!p.getFileName().toString().equals(keep)) {
                    Files.deleteIfExists(p);
                    AssetBridge.LOGGER.info("Veralteten Asset-Cache entfernt: {}", p.getFileName());
                }
            }
        } catch (IOException e) {
            AssetBridge.LOGGER.warn("Alte Asset-Caches konnten nicht aufgeraeumt werden", e);
        }
    }

    /**
     * In 26.x liefert packVersion(...) ein PackFormat (major/minor) statt eines int. Neuere pack.mcmeta-Dateien
     * deklarieren min_format/max_format als [major, minor] und brauchen kein pack_format mehr.
     */
    private static String packMcmeta() {
        var fmt = net.minecraft.SharedConstants.getCurrentVersion()
                .packVersion(net.minecraft.server.packs.PackType.CLIENT_RESOURCES);
        String v = "[" + fmt.major() + "," + fmt.minor() + "]";
        return "{\"pack\":{\"description\":\"Adorable Hamster Pets - lokale Original-Assets\","
                + "\"min_format\":" + v + ",\"max_format\":" + v + "}}";
    }

}
