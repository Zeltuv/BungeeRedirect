package tr.zeltuv.bungeeredirect.data;

import java.util.UUID;

public class User {

    private UUID uuid;
    private String server;

    public User(UUID uuid, String server) {
        this.uuid = uuid;
        this.server = server;
    }

    public UUID getUUID() {
        return uuid;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }
}