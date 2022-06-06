package Zookeeper;

public class ZookeeperServer {
    public final int ServerId;
    public final String Address;
    public final String Name;
    public final int Port;
    public final boolean IsObserver;

    public ZookeeperServer(int serverId, String address, int port) {
        this(serverId, address, port, false);
    }

    public ZookeeperServer(int serverId, String address, int port, boolean isObserver) {
        ServerId = serverId;
        Address = address;
        Name = String.format("zoo_%s", ServerId);
        Port = port;
        IsObserver = isObserver;
    }
}
