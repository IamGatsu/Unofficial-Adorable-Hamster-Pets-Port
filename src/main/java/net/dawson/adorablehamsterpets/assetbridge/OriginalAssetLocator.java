package net.dawson.adorablehamsterpets.assetbridge;

import net.fabricmc.loader.api.FabricLoader;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.Optional;
import java.util.zip.ZipFile;

/**
 * Findet das vom Nutzer selbst heruntergeladene Original-Jar (alte MC-Version).
 * Es liegt bewusst NICHT in mods/, sonst versucht Fabric es als Mod zu laden
 * (gleiche Mod-ID, falsche Minecraft-Version).
 */
public final class OriginalAssetLocator {
    /** Datei, an der wir ein echtes Original-Jar erkennen. */
    private static final String MARKER = "assets/adorablehamsterpets/geo/hamster.geo.json";

    /** Exakt diese Version des Original-Mods wird benoetigt. */
    public static final String REQUIRED_ORIGINAL_VERSION = "3.7.0";
    private static final String ORIGINAL_MOD_ID = "adorablehamsterpets";

    /** Gesetzt, wenn Jars im Ordner liegen, aber keines die benoetigte Version hat. */
    private static String versionProblem;

    public static final Path ORIGINAL_DIR =
            FabricLoader.getInstance().getConfigDir().resolve("adorablehamsterpets").resolve("original");
    public static final Path CACHE_DIR =
            FabricLoader.getInstance().getConfigDir().resolve("adorablehamsterpets").resolve("cache");

    public record Found(Path jar, String sha1) {}

    private OriginalAssetLocator() {}

    /** Hinweistext, falls ein Original-Jar mit falscher Version gefunden wurde (sonst null). */
    public static String versionProblem() {
        return versionProblem;
    }

    public static Optional<Found> find() {
        versionProblem = null;
        try {
            Files.createDirectories(ORIGINAL_DIR);
            try (DirectoryStream<Path> ds = Files.newDirectoryStream(ORIGINAL_DIR, "*.jar")) {
                for (Path p : ds) {
                    if (!looksLikeOriginal(p)) continue;
                    String version = readOriginalVersion(p);
                    if (isRequiredVersion(version)) {
                        return Optional.of(new Found(p, sha1(p)));
                    }
                    versionProblem = "Falsche Version des Original-Mods in " + p.getFileName() + ": "
                            + (version == null ? "unbekannt" : version)
                            + " (benoetigt wird genau " + REQUIRED_ORIGINAL_VERSION + ")";
                    AssetBridge.LOGGER.warn(versionProblem);
                }
            }
        } catch (IOException e) {
            AssetBridge.LOGGER.warn("Konnte {} nicht lesen", ORIGINAL_DIR, e);
        }
        return Optional.empty();
    }

    /** "3.7.0" oder "3.7.0+1.21.1-fabric" etc. akzeptieren, "3.7.1"/"3.6.0"/"3.7.0x" nicht. */
    private static boolean isRequiredVersion(String version) {
        if (version == null) return false;
        return version.equals(REQUIRED_ORIGINAL_VERSION)
                || version.startsWith(REQUIRED_ORIGINAL_VERSION + "+")
                || version.startsWith(REQUIRED_ORIGINAL_VERSION + "-");
    }

    /** Liest "version" aus der fabric.mod.json im Jar; null, wenn nicht lesbar oder andere Mod-ID. */
    private static String readOriginalVersion(Path jar) {
        try (ZipFile zf = new ZipFile(jar.toFile())) {
            var entry = zf.getEntry("fabric.mod.json");
            if (entry == null) return null;
            try (InputStream in = zf.getInputStream(entry);
                 InputStreamReader r = new InputStreamReader(in, StandardCharsets.UTF_8)) {
                JsonObject o = JsonParser.parseReader(r).getAsJsonObject();
                if (!o.has("id") || !ORIGINAL_MOD_ID.equals(o.get("id").getAsString())) return null;
                return o.has("version") ? o.get("version").getAsString() : null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    private static boolean looksLikeOriginal(Path jar) {
        try (ZipFile zf = new ZipFile(jar.toFile())) {
            return zf.getEntry(MARKER) != null;
        } catch (IOException e) {
            return false;
        }
    }

    private static String sha1(Path p) throws IOException {
        try (InputStream in = Files.newInputStream(p)) {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] buf = new byte[1 << 16];
            for (int n; (n = in.read(buf)) > 0; ) md.update(buf, 0, n);
            return HexFormat.of().formatHex(md.digest());
        } catch (java.security.NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
