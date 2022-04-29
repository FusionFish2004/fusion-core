package cn.fusionfish.core.utils;

import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author JeremyHu
 */
public class SqlUtil {
    public static boolean hasTable(@NotNull Connection connection, String name) {
        ResultSet resultSet = null;
        try {
            resultSet = connection.getMetaData().getTables(null, null, name, null);
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
