package cn.fusionfish.core.actionbar;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Data;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author JeremyHu
 */
@Data
public class PlayerActionBar {

    private final Player player;
    private static final Component EMPTY_COMPONENT = Component.text("");

    private final Map<TempActionBarMessage, Integer> tempTrackTimeMap = Maps.newHashMap();
    private final List<TempActionBarMessage> tempTrack = Lists.newArrayList();

    private SustainActionBarMessage sustainTrack = null;

    private final BukkitRunnable runnable = new BukkitRunnable() {
        @Override
        public void run() {
            tick();
        }
    };

    private void tick() {
        show();
        updateTempTrack();
    }

    private void show() {
        if (!tempTrack.isEmpty()) {
            TempActionBarMessage newestMessage = getNewestMessage();
            assert newestMessage != null;
            player.sendActionBar(newestMessage.getText());
            return;
        }

        if (sustainTrack != null) {
            player.sendActionBar(sustainTrack.getText());
            return;
        }

        sendEmptyMessage();
    }

    public void clear() {
        tempTrack.clear();
        tempTrackTimeMap.clear();
        sustainTrack = null;
    }

    private void updateTempTrack() {
        Iterator<TempActionBarMessage> iterator = tempTrack.iterator();
        while (iterator.hasNext()) {
            TempActionBarMessage message = iterator.next();
            int time = tempTrackTimeMap.get(message);
            if (time == 0) {
                tempTrackTimeMap.remove(message);
                iterator.remove();
            }
        }

        tempTrackTimeMap.keySet().forEach(key -> tempTrackTimeMap.merge(key, -1, Integer::sum));
    }

    private void addTempTrack(@NotNull TempActionBarMessage message) {
        int duration = message.getDuration();
        tempTrackTimeMap.put(message, duration);
        tempTrack.add(message);
    }

    private void setSustainTrack(@NotNull SustainActionBarMessage message) {
        this.sustainTrack = message;
    }

    public void add(ActionBarMessage message) {
        if(message instanceof TempActionBarMessage temp) {
            addTempTrack(temp);
            return;
        };

        if (message instanceof SustainActionBarMessage sustain) {
            setSustainTrack(sustain);
        }
    }

    private @Nullable TempActionBarMessage getNewestMessage() {
        if (tempTrack.isEmpty()) {
            return null;
        }

        int size = tempTrack.size();
        return tempTrack.get(size - 1);
    }

    private void sendEmptyMessage() {
        player.sendActionBar(EMPTY_COMPONENT);
    }

}
