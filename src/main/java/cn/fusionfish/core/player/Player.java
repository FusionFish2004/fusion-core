package cn.fusionfish.core.player;

import com.google.common.collect.Maps;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.UUID;

public abstract class Player {
    @SerializedName("data")
    private final HashMap<String, Object> data = Maps.newHashMap();

    protected final Object get(String key) {
        return data.get(key);
    }

    protected final void set(String key, Object value) {
        data.put(key, value);
    }

    public String getName() {
        return (String) get("name");
    }

    public void setName(String name) {
        set("name", name);
    }

    public UUID getUuid() {
        return (UUID) get("uuid");
    }

    public void setUuid(UUID uuid) {
        set("uuid", uuid);
    }

    public long getLastLogin() {
        return (long) get("lastLogin");
    }

    public void setLastLogin(long lastLogin) {
        set("lastLogin", lastLogin);
    }
}
