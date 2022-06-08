package Zookeeper;

import java.util.ArrayList;

public class ZookeeperCluster {
    private final String networkPrefix = "172.17.0.";
    private final int zookeeperStartPort = 2181;

    public final ArrayList<ZookeeperServer> Servers = new ArrayList<>();

    public ZookeeperCluster(int serversCount){
        for (var i = 0; i < serversCount; i++)
            Servers.add(new ZookeeperServer(i + 1, networkPrefix + (i + 2), zookeeperStartPort + i));
    }

    public ZookeeperCluster(int quorumServerCount, int observerServerCount){
        for (var i = 0; i < quorumServerCount; i++)
            Servers.add(new ZookeeperServer(i + 1, networkPrefix + (i + 2), zookeeperStartPort + i));

        for (var i = quorumServerCount; i < quorumServerCount + observerServerCount; i++)
            Servers.add(new ZookeeperServer(i + 1, networkPrefix + (i + 2), zookeeperStartPort + i, true));
    }
}
