package net.dawson.adorablehamsterpets.assetbridge;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Map;

/**
 * Seit 1.21.4 braucht jedes Item eine "items/<id>.json". Das alte Jar kennt nur models/item/<id>.json.
 * Fuer jedes dieser Modelle ohne passende Definition wird eine schlichte Definition erzeugt.
 * Das Pack steht unten in der Reihenfolge, deine Datagen-Dateien im Mod-Jar gewinnen also bei Konflikten.
 * Geckolib-Items (hamster_bed, hamster_guide_book ...) ggf. hier ausnehmen.
 */
public final class ItemDefinitionTransformer implements AssetTransformer {
    private static final String NS = "adorablehamsterpets";
    private static final String MODELS = "assets/" + NS + "/models/item/";
    private static final String ITEMS = "assets/" + NS + "/items/";

    @Override
    public void apply(Map<String, byte[]> files) {
        for (String path : new ArrayList<>(files.keySet())) {
            if (!path.startsWith(MODELS) || !path.endsWith(".json")) continue;
            String id = path.substring(MODELS.length(), path.length() - 5);
            if (id.contains("/")) continue;
            String target = ITEMS + id + ".json";
            if (files.containsKey(target)) continue;
            String json = "{\"model\":{\"type\":\"minecraft:model\",\"model\":\"" + NS + ":item/" + id + "\"}}";
            files.put(target, json.getBytes(StandardCharsets.UTF_8));
        }
    }
}
