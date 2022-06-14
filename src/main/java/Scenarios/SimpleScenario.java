package Scenarios;

import Blockade.Client.BlockadeHttpClient;
import Blockade.Enums.NetworkState;
import Zookeeper.ZookeeperBlockadeCluster;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

public class SimpleScenario {
    private final BlockadeHttpClient blockadeClient;
    private final String nodeBaseName = "/node%d";
    private final String nodeBaseValue = "value%d";

    private final int sleepTime = 10000;

    public SimpleScenario(BlockadeHttpClient blockadeClient){
        this.blockadeClient = blockadeClient;
    }

    public void Execute(NetworkState clusterNetworkState) throws Exception {
        var cluster = new ZookeeperBlockadeCluster(5, String.format("SimpleScenario%s", clusterNetworkState.getState()));
        var blockade = cluster.GetBlockade();
        blockadeClient.GetAllBlockades();

        try{
            blockadeClient.CreateBlockade(blockade);

            blockadeClient.ChangeNetworkState(blockade, cluster.GetServerNames(), clusterNetworkState);

            for (var i = 0; i < 10; i++){
                var curatorClient = CuratorFrameworkFactory.newClient(
                        String.format("%s:%d", cluster.Servers.get(0).Address, cluster.Servers.get(0).Port), new ExponentialBackoffRetry(500, 5));
                curatorClient.start();
                curatorClient.create().forPath(String.format(nodeBaseName, i), new byte[0]);
                curatorClient.setData()
                        .forPath(String.format(nodeBaseName, i), String.format(nodeBaseValue, i).getBytes());
            }

            Thread.sleep(sleepTime);

            for (var server : cluster.Servers) {
                for (var i = 0; i < 10; i++) {
                    var curatorClient = CuratorFrameworkFactory.newClient(
                            String.format("%s:%d", server.Address, server.Port), new ExponentialBackoffRetry(500, 5));
                    curatorClient.start();
                    var result = curatorClient.getData().forPath(String.format(nodeBaseName, i));
                    assert new String(result).equals(String.format(nodeBaseName, i));
                }
            }
        }
        finally {
            blockadeClient.DestroyBlockade(blockade);
        }
    }
}
