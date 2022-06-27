package cn.fusionfish.core.utils.parser;

import cn.fusionfish.core.exception.command.ParseException;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * @author JeremyHu
 */

public class PlayerParser implements ParamParser<Player> {
    @Override
    public @Nullable Player parse(String arg) throws ParseException {
        //优先解析UUID
        try {
            UUID uuid = UUID.fromString(arg);
            Player player = Bukkit.getPlayer(uuid);

            if (player == null) {
                throw new ParseException(arg);
            }

            if (player.isOnline()) {
                return player;
            }

            throw new ParseException(arg);
        } catch (IllegalArgumentException e) {
            //按名字解析
            Player player = Bukkit.getPlayerExact(arg);
            if (player == null) {
                throw new ParseException(arg);
            }

            if (player.isOnline()) {
                return player;
            }

            throw new ParseException(arg);
        }
    }
}
