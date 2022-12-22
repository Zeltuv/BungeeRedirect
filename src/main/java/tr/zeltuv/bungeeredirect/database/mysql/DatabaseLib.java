package tr.zeltuv.bungeeredirect.database.mysql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import tr.zeltuv.bungeeredirect.RedirectPlugin;

import java.sql.*;
import java.util.List;
import java.util.StringJoiner;

/**
 * @author Zeltuv
 */

public class DatabaseLib {

    private String host;
    private String username;
    private String password;
    private String database;
    private int port;
    private int maxPoolSize;
    private boolean useSSL;

    private HikariDataSource hikariDataSource;

    public HikariDataSource getHikariDataSource() {
        return hikariDataSource;
    }

    /**
     * @param host        The host is your database IP, for example 127.0.0.1
     * @param username    The username will be used for the autentification for the connection to the database
     * @param password    The password will be used for the autentification for the connection to the database
     * @param database    Put the the database name you want to connect
     * @param port        The port is the port that the connection to the database is going to use
     * @param maxPoolSize The maximum connection that will be created
     * @param ssl
     */
    public DatabaseLib(String host, String username, String password, String database, int port, int maxPoolSize, boolean ssl) {
        this.host = host;
        this.username = username;
        this.password = password;
        this.database = database;
        this.port = port;
        this.maxPoolSize = maxPoolSize;
        this.useSSL = ssl;
    }

    /**
     * Call this function to init connection to the database.
     */
    public void init() {
        ;
        HikariConfig hikariConfig = new HikariConfig();

        hikariConfig.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=" + useSSL);
        hikariConfig.setDriverClassName("com.mysql.cj.jdbc.Driver");
        hikariConfig.setUsername(username);
        hikariConfig.setPassword(password);
        hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        hikariConfig.setMinimumIdle(1);
        hikariConfig.setAutoCommit(true);
        hikariConfig.setMaximumPoolSize(maxPoolSize);

        hikariDataSource = new HikariDataSource(hikariConfig);
    }

    public Connection getConnection() throws SQLException {
        try {
            if (hikariDataSource == null) {
                RedirectPlugin.log("Reconnecting to the database ...");
                init();
                RedirectPlugin.log("Reconnected to the database.");
            }
            return hikariDataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean createTable(String tableName, List<Column> columns) {
        return createTable(tableName, columns.toArray(new Column[0]));
    }

    /**
     * @param tableName Put the table name of the table you want to create.
     *                  NOTE: If the table name exists it won't create the table
     * @param columns   Put the columns you want on your table
     * @return It will return true if the creation of database is successfull and false if there is any errors
     */
    public boolean createTable(String tableName, Column... columns) {
        try {

            StringJoiner stringJoiner = new StringJoiner(",");

            for (Column column : columns) {
                if (column.getDataType() == DataType.DOUBLE) {
                    stringJoiner.add(column.getName() + " " + column.getDataType() + (column.getLimit() == 0 ? "" : "(" + column.getLimit() + "," + column.getLimitDecimal() + ")"));
                    if (column.isPrimaryKey()) {
                        stringJoiner.add("PRIMARY KEY(" + column.getName() + ")");
                    }
                } else {
                    stringJoiner.add(column.getName() + " " + column.getDataType() + (column.getLimit() == 0 ? "" : "(" + column.getLimit() + ")"));
                    if (column.isPrimaryKey()) {
                        stringJoiner.add("PRIMARY KEY(" + column.getName() + ")");
                    }
                }
            }

            Connection connection = getConnection();

            PreparedStatement preparedStatement = connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS " + tableName + " (" + stringJoiner.toString() + ") CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci");

            preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();

            return true;

        } catch (SQLException sqlException) {

            sqlException.printStackTrace();

            return false;
        }
    }

    /**
     * @param table
     * @param value
     * @param where
     * @return
     */
    public ResultSet get(String table, String value, String where) {
        try {

            Connection connection = getConnection();

            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT * FROM " + table + " WHERE `" + where + "`='" + value + "'");

            return preparedStatement.executeQuery();
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
            return null;
        }
    }

    public void truncate(String table) {
        try {
            Connection connection = getConnection();

            PreparedStatement preparedStatement = connection.prepareStatement(
                    "TRUNCATE TABLE " + table
            );

            preparedStatement.executeUpdate();

            connection.close();
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }


    /**
     * @param table
     * @param where
     * @param value
     * @return
     */
    public boolean exists(String table, String where, String value) {
        try {
            ResultSet resultSet = get(table, value, where);

            boolean exists = resultSet.next();

            resultSet.getStatement().close();
            resultSet.close();
            resultSet.getStatement().getConnection().close();

            return exists;
        } catch (SQLException sqlException) {

            sqlException.printStackTrace();

            return false;
        }
    }

    /**
     * @param table
     * @param databaseValues
     * @return
     */
    public boolean add(String table, DatabaseValue... databaseValues) {
        try {

            Connection connection = getConnection();

            StringJoiner fields = new StringJoiner(",");
            StringJoiner questionMarks = new StringJoiner(",");

            for (DatabaseValue databaseValue : databaseValues) {
                questionMarks.add("?");
                fields.add("`" + databaseValue.getName() + "`");
            }

            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO " + table + " (" + fields + ") VALUES (" + questionMarks + ")");

            int i = 1;

            for (DatabaseValue databaseValue : databaseValues) {

                Object value = databaseValue.getValue();

                if (value instanceof String) {
                    preparedStatement.setString(i, (String) value);
                } else if (value instanceof Timestamp) {
                    preparedStatement.setTimestamp(i, (Timestamp) value);
                } else if (value instanceof Boolean) {
                    preparedStatement.setBoolean(i, (Boolean) value);
                } else if (value instanceof Double) {
                    preparedStatement.setDouble(i, (Double) value);
                } else if (value instanceof Integer) {
                    preparedStatement.setInt(i, (Integer) value);
                }
                i++;
            }

            preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();

            return true;
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
            return false;
        }
    }

    /**
     * @param table
     * @param where
     * @param value
     */
    public boolean remove(String table, String where, String value) {
        try {
            Connection connection = getConnection();

            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM `" + table + "` WHERE `" + where + "`='" + value + "'");

            preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();
            return true;
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
            return false;
        }
    }


    public void updateAsync(String table, String column, Object value, String where, Object whereValue) {
        RedirectPlugin.get().getProxy().getScheduler().runAsync(RedirectPlugin.get(),()->{
            update(table, column, value, where, whereValue);
        });
    }

    public void removeAsync(String table, String where, String value) {
        RedirectPlugin.get().getProxy().getScheduler().runAsync(RedirectPlugin.get(),()->{
            remove(table, where, value);
        });
    }

    public void addAsync(String table, DatabaseValue... databaseValues) {
        RedirectPlugin.get().getProxy().getScheduler().runAsync(RedirectPlugin.get(),()->{
            add(table, databaseValues);
        });
    }

    /**
     * @param table
     * @param column
     * @param value
     * @param where
     * @param whereValue "UPDATE "+table+" SET "+column+"=? WHERE "+where+"=?"
     */
    public void update(String table, String column, Object value, String where, Object whereValue) {
        try {
            Connection connection = getConnection();

            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE " + table + " SET " + column + "=? WHERE " + where + "=?");

            if (value instanceof String) {
                preparedStatement.setString(1, (String) value);
            } else if (value instanceof Timestamp) {
                preparedStatement.setTimestamp(1, (Timestamp) value);
            } else if (value instanceof Boolean) {
                preparedStatement.setBoolean(1, (Boolean) value);
            } else if (value instanceof Double) {
                preparedStatement.setDouble(1, (Double) value);
            } else if (value instanceof Integer) {
                preparedStatement.setInt(1, (Integer) value);
            }


            if (whereValue instanceof String) {
                preparedStatement.setString(2, (String) whereValue);
            } else if (whereValue instanceof Timestamp) {
                preparedStatement.setTimestamp(2, (Timestamp) whereValue);
            } else if (whereValue instanceof Boolean) {
                preparedStatement.setBoolean(2, (Boolean) whereValue);
            } else if (whereValue instanceof Double) {
                preparedStatement.setDouble(2, (Double) whereValue);
            } else if (whereValue instanceof Integer) {
                preparedStatement.setInt(2, (Integer) whereValue);
            }

            preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }

    /**
     * @param table
     * @return
     */
    public ResultSet getAllValue(String table) {
        try {

            PreparedStatement preparedStatement = getConnection().prepareStatement(
                    "SELECT * FROM `" + table + "`");

            return preparedStatement.executeQuery();
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
            return null;
        }
    }

    public static void closeAll(PreparedStatement preparedStatement){
        try {
            Connection connection = preparedStatement.getConnection();

            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public static void closeAll(ResultSet resultSet){
        try {
            Statement statement = resultSet.getStatement();
            Connection connection = statement.getConnection();

            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

