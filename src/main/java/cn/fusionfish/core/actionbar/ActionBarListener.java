package cn.fusionfish.core.actionbar;

import cn.fusionfish.core.FusionCore;
import cn.fusionfish.core.listener.AutoRegisterListener;
import lombok.extern.log4j.Log4j2;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

/**
 * @author JeremyHu
 */
@AutoRegisterListener
@Log4j2
public class ActionBarListener implements Listener {

    @EventHandler
    public void onJoin(@NotNull PlayerJoinEvent event) {
        ActionBarManager manager = FusionCore.getManager(ActionBarManager.class);
        Player player = event.getPlayer();
        manager.add(player);
    }

    @EventHandler
    public void onQuit(@NotNull PlayerQuitEvent event) {
        ActionBarManager manager = FusionCore.getManager(ActionBarManager.class);
        Player player = event.getPlayer();
        manager.remove(player);
    }
}
