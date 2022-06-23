package cn.fusionfish.core.actionbar;

import cn.fusionfish.core.annotations.FusionListener;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * @author JeremyHu
 */
@FusionListener
public class ActionBarListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {

    }
}
