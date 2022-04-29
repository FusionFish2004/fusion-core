package cn.fusionfish.core.web.http;

import com.google.gson.*;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.Map;

/**
 * @author JeremyHu
 */
public class Query {

    private Map<String, String> data;
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public Query(Map<String, String> data) {
        this.data = data;
    }

    public String get(String key) {
        return data.get(key);
    }

    public int getAsInteger(String key) {
        return Integer.parseInt(data.get(key));
    }

    public long getAsLong(String key) {
        return Long.parseLong(data.get(key));
    }

    public double getAsDouble(String key) {
        return Double.parseDouble(data.get(key));
    }

    public float getAsFloat(String key) {
        return Float.parseFloat(data.get(key));
    }

    public boolean getAsBoolean(@NotNull String key) {
        String lowerCase = key.toLowerCase(Locale.ROOT);
        return Boolean.parseBoolean(lowerCase);
    }

    public <T> T getAsObjectFromJson(String key, Class<T> classOfObject) {
        String json = data.get(key);
        return GSON.fromJson(json, classOfObject);
    }

    public JsonElement getAsJsonElement(String key) {
        String json = data.get(key);
        return new JsonParser().parse(json);
    }

}
