package tr.zeltuv.bungeeredirect.database;

import net.md_5.bungee.config.Configuration;
import tr.zeltuv.bungeeredirect.RedirectPlugin;
import tr.zeltuv.bungeeredirect.data.User;
import tr.zeltuv.bungeeredirect.database.mysql.Column;
import tr.zeltuv.bungeeredirect.database.mysql.DataType;
import tr.zeltuv.bungeeredirect.database.mysql.DatabaseLib;
import tr.zeltuv.bungeeredirect.database.mysql.DatabaseValue;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class MySQLDatabase {

    private RedirectPlugin plugin;

    private DatabaseLib databaseLib;

    private static final String USER_TABLE = "users_data";

    public void init(RedirectPlugin plugin) {
        this.plugin = plugin;

        Configuration config = plugin.getConfiguration();

        Configuration mysql = config.getSection("MySQL");

        String host = mysql.getString("host");
        String user = mysql.getString("user");
        String password = mysql.getString("password");
        String database = mysql.getString("database");
        int port = mysql.getInt("port");
        int pool = mysql.getInt("max-pool");
        boolean useSSL = mysql.getBoolean("useSSL");

        databaseLib = new DatabaseLib(
                host,
                user,
                password,
                database,
                port,
                pool,
                useSSL
        );

        databaseLib.init();

        databaseLib.createTable(USER_TABLE,
                Column.create(DataType.VARCHAR,"uuid",38,true),
                Column.create(DataType.VARCHAR,"last_server",38,false)
                );
    }

    public void stop() {
        databaseLib.getHikariDataSource().close();
    }

    public void addUser(UUID uuid) {
        databaseLib.add(USER_TABLE, DatabaseValue.get("uuid",uuid.toString()),
                DatabaseValue.get("last_server",plugin.getDefaultServer()));
    }

    public void updateUser(UUID uuid, String server){
        databaseLib.update(USER_TABLE,"last_server",
                server,
                "uuid",
                uuid.toString()
                );
    }

    public User getUser(UUID uuid) {
        User user = new User(uuid, plugin.getDefaultServer());

        if(databaseLib.exists(USER_TABLE,"uuid",uuid.toString())){
            ResultSet resultSet = databaseLib.get(USER_TABLE,uuid.toString(), "uuid");

            try {
                resultSet.next();

                String lastServer = resultSet.getString("last_server");
                user.setServer(lastServer);

                DatabaseLib.closeAll(resultSet);
            }catch (SQLException e){
                e.printStackTrace();
            }
        }else{

            addUser(uuid);
        }

        return user;
    }
}
