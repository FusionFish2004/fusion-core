package cn.fusionfish.core.actionbar;

import cn.fusionfish.core.manager.AutoRegisterManager;
import cn.fusionfish.core.manager.Manager;
import com.google.common.collect.Maps;
import org.bukkit.entity.Player;

import java.util.Map;

/**
 * @author JeremyHu
 */

@AutoRegisterManager
public class ActionBarManager implements Manager {
    private final Map<Player, PlayerActionBar> actionBarMap = Maps.newHashMap();

    public void remove(Player player) {
        actionBarMap.remove(player);
    }

    public void add(Player player) {
        actionBarMap.put(player, new PlayerActionBar(player));
    }

    public PlayerActionBar get(Player player) {
        if (!actionBarMap.containsKey(player)) {
            add(player);
        }
        return actionBarMap.get(player);
    }

    public Map<Player, PlayerActionBar> getActionBarMap() {
        return actionBarMap;
    }

    public void broadcast(ActionBarMessage message) {
        actionBarMap.values().forEach(bar -> bar.add(message));
    }
}
