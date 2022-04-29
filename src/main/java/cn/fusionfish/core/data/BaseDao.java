package cn.fusionfish.core.data;

import cn.fusionfish.core.plugin.FusionPlugin;

import java.sql.*;
import java.util.List;

/**
 * @author JeremyHu
 */
public interface BaseDao<T> {

    /**
     * 获取数据库连接
     * @return 数据库连接
     * @throws SQLException SQL错误
     */
    default Connection getConnection() throws SQLException {
        return FusionPlugin.getConnection();
    }

    /**
     * 从表中移除一个数据
     * @param type 数据
     */
    void remove(T type);

    /**
     * 获取所有数据
     * @return 所有数据
     */
    List<T> getAll();

    /**
     * 创建表
     */
    void createTable();

    /**
     * 关闭所有连接
     * @param connection connection
     * @param statement statement
     * @param resultSet resultSet
     * @throws SQLException SQL错误
     */
    default void closeAll(Connection connection, Statement statement, ResultSet resultSet) throws SQLException {

        if (resultSet != null) {
            resultSet.close();
        }

        if (statement != null) {
            statement.close();
        }

        if (connection != null) {
            connection.close();
        }
    }

}
