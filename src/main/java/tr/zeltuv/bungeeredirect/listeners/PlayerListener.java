package tr.zeltuv.bungeeredirect.listeners;

import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.*;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import tr.zeltuv.bungeeredirect.RedirectPlugin;
import tr.zeltuv.bungeeredirect.data.User;

public class PlayerListener implements Listener {

    private RedirectPlugin plugin;

    public PlayerListener(RedirectPlugin plugin) {
        this.plugin = plugin;
    }
    @EventHandler
    public void onServerConnect(ServerConnectEvent event){
        if(event.getReason() != ServerConnectEvent.Reason.JOIN_PROXY)
            return;

        ServerInfo serverInfo = plugin.getProxy().getServerInfo(plugin.getDefaultServer());

        if (!plugin.connectToDefaultServer()) {
            User user = plugin.getDatabase().getUser(event.getPlayer().getUniqueId());
            String server = user.getServer();
            serverInfo = plugin.getProxy().getServerInfo(server);
        }

        event.setTarget(serverInfo);

    }

    @EventHandler
    public void onServerLeave(PlayerDisconnectEvent event){
        ProxiedPlayer player = event.getPlayer();

        String server = player.getServer().getInfo().getName();

        plugin.getDatabase().updateUser(player.getUniqueId(),server);
    }
}
