package cn.fusionfish.core.command.parser;

import cn.fusionfish.core.exception.command.ParseException;
import org.bukkit.Bukkit;
import org.bukkit.World;

/**
 * @author JeremyHu
 */
public class WorldParser implements Parser<World> {
    @Override
    public World parse(String arg) throws ParseException {
        World world = Bukkit.getWorld(arg);
        if (world == null) {
            throw new ParseException(arg);
        }

        return world;
    }
}
