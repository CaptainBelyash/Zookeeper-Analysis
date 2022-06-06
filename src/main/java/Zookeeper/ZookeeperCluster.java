package Zookeeper;

import java.util.ArrayList;

public class ZookeeperCluster {
    private final String networkPrefix = "172.17.0.";
    private final int zookeeperStartPort = 2181;

    protected final ArrayList<ZookeeperServer> servers = new ArrayList<>();

    public ZookeeperCluster(int serversCount){
        for (var i = 0; i < serversCount; i++)
            servers.add(new ZookeeperServer(i + 1, networkPrefix + (i + 2), zookeeperStartPort + i));
    }

    public ZookeeperCluster(int quorumServerCount, int observerServerCount){
        for (var i = 0; i < quorumServerCount; i++)
            servers.add(new ZookeeperServer(i + 1, networkPrefix + (i + 2), zookeeperStartPort + i));

        for (var i = quorumServerCount; i < quorumServerCount + observerServerCount; i++)
            servers.add(new ZookeeperServer(i + 1, networkPrefix + (i + 2), zookeeperStartPort + i, true));
    }
}
