package net.dawson.adorablehamsterpets.assetbridge;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.Optional;

/** Zentrale Stelle: findet das Original, baut das Pack (einmalig, gecacht) und meldet den Status. */
public final class AssetBridge {
    public static final Logger LOGGER = LoggerFactory.getLogger("AHP/AssetBridge");

    private static boolean initialized;
    private static Path packPath;
    private static String error;

    private AssetBridge() {}

    /** Idempotent. Wird beim ersten Aufruf von loadPacks bzw. vom Client-Init ausgefuehrt. */
    public static synchronized void init() {
        if (initialized) return;
        initialized = true;
        Optional<OriginalAssetLocator.Found> found = OriginalAssetLocator.find();
        if (found.isEmpty()) {
            String vp = OriginalAssetLocator.versionProblem();
            error = (vp != null ? vp + ". " : "Original-Jar (Version " + OriginalAssetLocator.REQUIRED_ORIGINAL_VERSION + ") fehlt. ")
                    + "Lege es nach " + OriginalAssetLocator.ORIGINAL_DIR;
            LOGGER.warn(error);
            return;
        }
        try {
            packPath = AssetPackBuilder.buildIfNeeded(found.get());
        } catch (Exception e) {
            error = "Asset-Pack konnte nicht gebaut werden: " + e;
            LOGGER.error(error, e);
        }
    }

    public static boolean isAvailable() {
        init();
        return packPath != null;
    }

    public static Path packPath() {
        init();
        return packPath;
    }

    /** Text fuer Titelscreen/Toast, null wenn alles in Ordnung ist. */
    public static String errorMessage() {
        init();
        return error;
    }
}
