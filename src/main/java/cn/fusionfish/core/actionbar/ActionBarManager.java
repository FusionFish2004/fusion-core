package cn.fusionfish.core.actionbar;

import com.google.common.collect.Maps;
import org.bukkit.entity.Player;

import java.util.Map;

/**
 * @author JeremyHu
 */

public class ActionBarManager {
    private final Map<Player, PlayerActionBar> actionBarMap = Maps.newHashMap();

    public void remove(Player player) {
        actionBarMap.remove(player);
    }

    public void add(Player player) {
        actionBarMap.put(player, new PlayerActionBar(player));
    }

    public PlayerActionBar get(Player player) {
        return actionBarMap.get(player);
    }

    public void broadcast(ActionBarMessage message) {
        actionBarMap.values().forEach(bar -> bar.add(message));
    }
}
