package cn.fusionfish.core.command.parser;

import cn.fusionfish.core.exception.command.ParseException;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

/**
 * @author JeremyHu
 */
public class VectorParser implements Parser<Vector> {
    @Override
    public Vector parse(@NotNull String arg) throws ParseException {

        if (arg.equalsIgnoreCase(NULL_STRING)) {
            return null;
        }

        String trimmed = arg.replace("(", "").replace(")", "");
        String[] coordinates = trimmed.split(",");
        if (coordinates.length != 3) {
            throw new ParseException(arg);
        }

        try {
            double x = Double.parseDouble(coordinates[0]);
            double y = Double.parseDouble(coordinates[1]);
            double z = Double.parseDouble(coordinates[2]);
            return new Vector(x,y,z);
        } catch (Exception e) {
            throw new ParseException(arg);
        }
    }
}
