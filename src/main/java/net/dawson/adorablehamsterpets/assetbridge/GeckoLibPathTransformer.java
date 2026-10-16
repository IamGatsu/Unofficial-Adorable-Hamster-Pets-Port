package net.dawson.adorablehamsterpets.assetbridge;

import java.util.ArrayList;
import java.util.Map;

/**
 * GeckoLib 4 (1.21.1) legt Modelle in assets/<ns>/geo und Animationen in assets/<ns>/animations ab,
 * GeckoLib 5 erwartet assets/<ns>/geckolib/models bzw. geckolib/animations.
 * Geprueft: Die Dateiinhalte sind zwischen Original und Port byte-identisch, es aendert sich nur der Pfad.
 */
public final class GeckoLibPathTransformer implements AssetTransformer {
    private static final String BASE = "assets/adorablehamsterpets/";

    @Override
    public void apply(Map<String, byte[]> files) {
        for (String path : new ArrayList<>(files.keySet())) {
            if (path.startsWith(BASE + "geo/")) {
                files.put(BASE + "geckolib/models/" + path.substring((BASE + "geo/").length()), files.remove(path));
            } else if (path.startsWith(BASE + "animations/")) {
                files.put(BASE + "geckolib/animations/" + path.substring((BASE + "animations/").length()), files.remove(path));
            }
        }
    }
}
