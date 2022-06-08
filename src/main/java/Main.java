import Blockade.Client.BlockadeHttpClient;
import Zookeeper.JMXClient.ZookeeperJMXClient;
import Zookeeper.ZookeeperBlockadeCluster;
import org.apache.zookeeper.server.quorum.FollowerMXBean;
import org.apache.zookeeper.server.quorum.LeaderMXBean;

public class Main {
    public static void main(String[] args) throws Exception {

        var client = new BlockadeHttpClient("localhost", 5000);

        // broken blockades, restart daemon and kill all broken zookeeper containers manually if this not empty
        client.GetAllBlockades();

        var cluster = new ZookeeperBlockadeCluster(3, 2);
        var blockade = cluster.GetBlockade("somenamebeta10");

        client.StartNewBlockade(blockade);

        client.GetBlockade(blockade);
        var metric = ZookeeperJMXClient.GetServerMetrics(cluster.Servers.get(0));

        assert metric != null;
        switch (metric.State)
        {
            case LEADER:
                System.out.println(((LeaderMXBean) metric.ServerBean).getElectionTimeTaken());
            case FOLLOWER:
                System.out.println(((FollowerMXBean) metric.ServerBean).getElectionTimeTaken());
        }

        client.StartNewPartition(blockade, new String[][]{new String[]{"zoo_1"},new String[]{"zoo_2", "zoo_3", "zoo_4", "zoo_5"}});

        client.RemoveAllPartitions(blockade);

        client.DestroyBlockade(blockade);
    }
}
