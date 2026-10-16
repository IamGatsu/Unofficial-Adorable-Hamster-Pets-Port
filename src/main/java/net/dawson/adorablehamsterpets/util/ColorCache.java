package net.dawson.adorablehamsterpets.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.dawson.adorablehamsterpets.AdorableHamsterPets;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Vorberechnete Durchschnittsfarben der Genetik-Paletten (siehe tools/gen_color_cache.py).
 * Ersetzt das Einlesen der PNGs aus dem Mod-Jar, denn dieses Jar enthaelt keine Original-Assets mehr
 * und Server haben das Original ohnehin nicht. Liegt unter data/, wird also auch serverseitig mitgeliefert.
 */
public final class ColorCache {
    private static final String PATH = "/data/adorablehamsterpets/genetics/color_cache.json";
    private static final Map<String, Vec3> CACHE = load();

    private ColorCache() {}

    /** @param resourcePath z. B. "assets/adorablehamsterpets/textures/entity/hamster/jimcerberus/rust.png" (mit oder ohne fuehrenden Slash) */
    @Nullable
    public static Vec3 lookup(String resourcePath) {
        String key = resourcePath.startsWith("/") ? resourcePath.substring(1) : resourcePath;
        return CACHE.get(key);
    }

    private static Map<String, Vec3> load() {
        Map<String, Vec3> map = new HashMap<>();
        try (InputStream in = ColorCache.class.getResourceAsStream(PATH)) {
            if (in == null) {
                AdorableHamsterPets.LOGGER.warn("Color cache {} not found; falling back to reading palette images.", PATH);
                return map;
            }
            JsonObject root = JsonParser.parseReader(new InputStreamReader(in, StandardCharsets.UTF_8)).getAsJsonObject();
            for (Map.Entry<String, JsonElement> e : root.entrySet()) {
                JsonArray a = e.getValue().getAsJsonArray();
                map.put(e.getKey(), new Vec3(a.get(0).getAsDouble(), a.get(1).getAsDouble(), a.get(2).getAsDouble()));
            }
        } catch (Exception ex) {
            AdorableHamsterPets.LOGGER.error("Failed to read color cache {}", PATH, ex);
        }
        return map;
    }
}
