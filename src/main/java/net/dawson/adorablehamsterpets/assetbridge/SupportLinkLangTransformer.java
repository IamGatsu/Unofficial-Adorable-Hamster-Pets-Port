package net.dawson.adorablehamsterpets.assetbridge;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Map;

/**
 * Overrides the "Support the Mod" config entry text of the original lang files in every language,
 * so the menu entry reads "Support Original Creator" even if the original lang file defines it.
 */
public final class SupportLinkLangTransformer implements AssetTransformer {
    private static final String LANG = "assets/adorablehamsterpets/lang/";
    private static final Map<String, String> OVERRIDES = Map.of(
            "adorablehamsterpets.root.visitWebsite", "Support Original Creator",
            "adorablehamsterpets.root.visitWebsite.desc",
            "Support the creator of the original Adorable Hamster Pets mod. This is an unofficial port.",
            "config.adorablehamsterpets.main.help_and_resources.support_the_mod", "Buy him a Coffee",
            "config.adorablehamsterpets.main.help_and_resources.support_the_mod.desc",
            "Opens the original creator's Ko-fi page.");

    @Override
    public void apply(Map<String, byte[]> files) {
        for (String path : new ArrayList<>(files.keySet())) {
            if (!path.startsWith(LANG) || !path.endsWith(".json")) continue;
            try {
                JsonObject o = JsonParser.parseString(new String(files.get(path), StandardCharsets.UTF_8)).getAsJsonObject();
                OVERRIDES.forEach(o::addProperty);
                files.put(path, new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create()
                        .toJson(o).getBytes(StandardCharsets.UTF_8));
            } catch (RuntimeException e) {
                AssetBridge.LOGGER.warn("Lang file {} skipped: {}", path, e.toString());
            }
        }
    }
}
