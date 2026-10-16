package net.dawson.adorablehamsterpets.assetbridge;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * Die Bett-Item-Modelle verwenden im Original "parent": "builtin/entity" (GeckoLib-Item-Rendering).
 * Der Port hat diesen Parent entfernt und stattdessen eine Partikel-Textur gesetzt.
 */
public final class BuiltinEntityModelTransformer implements AssetTransformer {
    private static final String ITEM = "assets/adorablehamsterpets/models/item/";
    private static final List<String> TARGETS = List.of("hamster_bed.json", "upside_down_hamster_bed_icon.json");

    @Override
    public void apply(Map<String, byte[]> files) {
        for (String name : TARGETS) {
            byte[] raw = files.get(ITEM + name);
            if (raw == null) continue;
            JsonObject o = JsonParser.parseString(new String(raw, StandardCharsets.UTF_8)).getAsJsonObject();
            o.remove("parent");
            JsonObject tex = new JsonObject();
            tex.addProperty("particle", "adorablehamsterpets:block/hamster_bed_oak");
            o.add("textures", tex);
            files.put(ITEM + name, new GsonBuilder().setPrettyPrinting().create().toJson(o).getBytes(StandardCharsets.UTF_8));
        }
    }
}
