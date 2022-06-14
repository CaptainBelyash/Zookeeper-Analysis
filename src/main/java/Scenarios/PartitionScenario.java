package Scenarios;

import Blockade.Client.BlockadeHttpClient;
import Blockade.Enums.NetworkState;
import Utils.ArrayUtils;
import Zookeeper.JMXClient.ServerState;
import Zookeeper.JMXClient.ZookeeperJMXClient;
import Zookeeper.ZookeeperBlockadeCluster;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.Arrays;

public class PartitionScenario {
    private final BlockadeHttpClient blockadeClient;
    private final String nodeBaseName = "/node%d";
    private final String nodeBaseValue = "value%d";

    private final int sleepTime = 3000;

    public PartitionScenario(BlockadeHttpClient blockadeClient){
        this.blockadeClient = blockadeClient;
    }

    public void Execute(int serversCount) throws Exception {
        var cluster = new ZookeeperBlockadeCluster(serversCount, "PartitionScenario");
        var blockade = cluster.GetBlockade();
        blockadeClient.GetAllBlockades();

        ZookeeperJMXClient jmxClient = null;
        try{
            blockadeClient.CreateBlockade(blockade);
            jmxClient = new ZookeeperJMXClient(cluster);

            for (var i = 0; i < (serversCount + 1) / 2; i++){
                var brokeRange = Arrays.copyOfRange(cluster.GetServerNames(), 0, i);
                blockadeClient.StartNewPartition(blockade, new String[][]{brokeRange, ArrayUtils.GetReminder(cluster.GetServerNames(), brokeRange)});

                Thread.sleep(sleepTime);

                System.out.println(jmxClient.GroupServersByStates().get(ServerState.ELECTION).size());
                assert (long) jmxClient.GroupServersByStates().get(ServerState.ELECTION).size() == i;

                blockadeClient.RemoveAllPartitions(blockade);
                Thread.sleep(sleepTime);
            }
        }
        finally {
            try {
                assert jmxClient != null;
                jmxClient.Close();
            }
            // Sometimes jmxClient closing throws an exception.
            // We need to Destroy Blockade after every use, otherwise we must manually drop the docker containers
            finally {
                blockadeClient.DestroyBlockade(blockade);
            }
        }
    }
}
