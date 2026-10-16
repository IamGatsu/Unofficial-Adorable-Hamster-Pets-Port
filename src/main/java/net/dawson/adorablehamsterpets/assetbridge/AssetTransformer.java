package net.dawson.adorablehamsterpets.assetbridge;

import java.util.Map;

/**
 * Passt Dateien aus dem alten Jar im Speicher an das 26.2-Format an.
 * Schluessel = Pfad im Pack (z. B. "assets/adorablehamsterpets/lang/en_us.json").
 * Eine Transformation darf Eintraege ersetzen, entfernen oder neue hinzufuegen.
 */
public interface AssetTransformer {
    void apply(Map<String, byte[]> files);
}
