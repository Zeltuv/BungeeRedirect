package tr.zeltuv.bungeeredirect.utils;

import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import tr.zeltuv.bungeeredirect.RedirectPlugin;

import java.io.*;

public class ConfigUtils {

    public static Configuration initConfig(String s) {
        RedirectPlugin plugin = RedirectPlugin.get();

        plugin.getDataFolder().mkdirs();

        File configFile = new File(plugin.getDataFolder(), s+".yml");

        if (!configFile.exists()) {
            try {
                configFile.createNewFile();

                FileOutputStream outputStream = new FileOutputStream(configFile);
                InputStream in = plugin.getResourceAsStream(s+".yml");
                copy(in,outputStream);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        try {
            return ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(plugin.getDataFolder(), s+".yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static void copy(InputStream source, OutputStream target) throws IOException {
        byte[] buf = new byte[8192];
        int length;
        while ((length = source.read(buf)) != -1) {
            target.write(buf, 0, length);
        }
    }
}
