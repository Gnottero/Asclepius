package com.gnottero.asclepius.datagen;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Shared utility methods for reading JSON asset files from the classpath during datagen.
 */
public class DatagenUtils {

    /**
     * Reads a JSON asset file from the classpath under the given block identifier's namespace.
     */
    public static JsonObject readJson(Identifier id, String folder) {
        var path = "/assets/" + id.getNamespace() + "/" + folder + "/" + id.getPath() + ".json";
        try (var stream = DatagenUtils.class.getResourceAsStream(path)) {
            if (stream == null) return null;
            return JsonParser.parseReader(new InputStreamReader(stream, StandardCharsets.UTF_8)).getAsJsonObject();
        } catch (Exception ignored) {
            return null;
        }
    }

    /**
     * Reads a block model JSON file from the classpath given its fully-qualified model identifier.
     */
    public static JsonObject readModelJson(Identifier modelId) {
        return readJson(modelId, "models");
    }

    /**
     * Parses a raw texture or model reference string into an Identifier.
     */
    public static Identifier parseId(String raw) {
        return Identifier.parse(raw);
    }

    /**
     * Resolves the full-block model identifier from a vanilla slab block's blockstate definition.
     */
    public static Identifier getFullBlockModel(Block slab) {
        var id         = BuiltInRegistries.BLOCK.getKey(slab);
        var blockstate = readJson(id, "blockstates");
        var variants   = blockstate != null ? blockstate.getAsJsonObject("variants") : null;

        if (variants != null) {
            for (Map.Entry<String, JsonElement> entry : variants.entrySet()) {
                if (!entry.getKey().contains("type=double")) continue;
                var variant  = entry.getValue();
                var modelObj = variant.isJsonArray()
                        ? variant.getAsJsonArray().get(0).getAsJsonObject()
                        : variant.getAsJsonObject();
                return parseId(modelObj.get("model").getAsString());
            }
        }
        throw new IllegalStateException("No type=double variant found for " + id);
    }
}
