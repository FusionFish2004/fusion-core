package cn.fusionfish.core.command.parser;

import cn.fusionfish.core.exception.command.ParseException;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

/**
 * @author JeremyHu
 */
public class WorldParser implements Parser<World> {
    @Override
    public World parse(@NotNull String arg) throws ParseException {

        if (arg.equalsIgnoreCase(NULL_STRING)) {
            return null;
        }

        World world = Bukkit.getWorld(arg);
        if (world == null) {
            throw new ParseException(arg);
        }

        return world;
    }
}
