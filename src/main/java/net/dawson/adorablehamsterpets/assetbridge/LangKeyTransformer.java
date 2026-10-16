package net.dawson.adorablehamsterpets.assetbridge;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.GsonBuilder;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Map;

/**
 * 26.x nennt die Keybinding-Kategorie "key.category.<ns>.main" statt "key.categories.<ns>.main".
 * Das ist der einzige Lang-Unterschied zwischen Original und Port (geprueft an allen Sprachdateien).
 * Der Wert wird aus dem alten Key uebernommen, also je Sprache identisch zum Original.
 */
public final class LangKeyTransformer implements AssetTransformer {
    private static final String LANG = "assets/adorablehamsterpets/lang/";
    private static final String OLD = "key.categories.adorablehamsterpets.main";
    private static final String NEW = "key.category.adorablehamsterpets.main";

    @Override
    public void apply(Map<String, byte[]> files) {
        for (String path : new ArrayList<>(files.keySet())) {
            if (!path.startsWith(LANG) || !path.endsWith(".json")) continue;
            try {
                JsonObject o = JsonParser.parseString(new String(files.get(path), StandardCharsets.UTF_8)).getAsJsonObject();
                if (o.has(OLD) && !o.has(NEW)) {
                    o.add(NEW, o.get(OLD));
                    files.put(path, new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create()
                            .toJson(o).getBytes(StandardCharsets.UTF_8));
                }
            } catch (RuntimeException e) {
                AssetBridge.LOGGER.warn("Lang-Datei {} uebersprungen: {}", path, e.toString());
            }
        }
    }
}
