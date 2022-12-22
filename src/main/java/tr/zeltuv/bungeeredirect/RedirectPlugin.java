package tr.zeltuv.bungeeredirect;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import tr.zeltuv.bungeeredirect.database.MySQLDatabase;
import tr.zeltuv.bungeeredirect.listeners.PlayerListener;

import java.util.logging.Logger;

import static tr.zeltuv.bungeeredirect.utils.ConfigUtils.initConfig;

public class RedirectPlugin extends Plugin {

    private static RedirectPlugin plugin;
    private static Logger logger;

    private Configuration configuration;
    private MySQLDatabase database = new MySQLDatabase();

    private boolean connectToDefaultServer;
    private String defaultServer;

    @Override
    public void onEnable() {
        plugin = this;
        logger = getLogger();

        configuration = initConfig("config");

        database.init(this);
        loadConfig();

        getProxy().getPluginManager().registerListener(this,new PlayerListener(this));
    }

    @Override
    public void onDisable() {
        database.stop();
    }

    public void loadConfig(){
        defaultServer = configuration.getString("default-server");
        connectToDefaultServer = configuration.getBoolean("send-to-default");
    }

    public String getDefaultServer() {
        return defaultServer;
    }

    public boolean connectToDefaultServer() {
        return connectToDefaultServer;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public MySQLDatabase getDatabase() {
        return database;
    }

    public static void log(String msg){
        logger.info(msg);
    }

    public static RedirectPlugin get(){
        return plugin;
    }
}
