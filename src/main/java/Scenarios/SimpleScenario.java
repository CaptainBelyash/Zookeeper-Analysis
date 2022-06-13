package Scenarios;

import Blockade.Client.BlockadeHttpClient;
import Utils.ArrayUtils;
import Zookeeper.JMXClient.ServerState;
import Zookeeper.JMXClient.ZookeeperJMXClient;
import Zookeeper.ZookeeperBlockadeCluster;
import org.apache.zookeeper.server.ServerStats;

import java.util.ArrayList;
import java.util.HashMap;

import static Utils.JSONtoFile.PrintToFile;

public class SimpleScenario {
    private final ArrayList<ServerState> AvailableStates = new ArrayList<>();
    private final BlockadeHttpClient blockadeClient;

    private final int sleepTime = 1000;

    public SimpleScenario(BlockadeHttpClient blockadeClient){
        this.blockadeClient = blockadeClient;
        AvailableStates.add(ServerState.LEADER);
        AvailableStates.add(ServerState.FOLLOWER);
    }

    public void Execute() throws Exception {
        var cluster = new ZookeeperBlockadeCluster(5, "SimpleScenario222");
        var blockade = cluster.GetBlockade();
        blockadeClient.GetAllBlockades();

        var serverAvailable = new HashMap<String, ArrayList<Integer>>();
        for (var server : cluster.GetServerNames())
            serverAvailable.put(server, new ArrayList<>());

        ZookeeperJMXClient jmxClient = null;
        try{
            blockadeClient.CreateBlockade(blockade);
            jmxClient = new ZookeeperJMXClient(cluster);

            for (var i = 0; i < 10; i++){
                for (var server : cluster.Servers){
                    serverAvailable.get(server.Name).add(AvailableStates.contains(jmxClient.GetNodeState(server)) ? 1 : 0);
                }
                Thread.sleep(sleepTime);
            }

            blockadeClient.StartNewPartition(blockade, new String[][]{
                    new String[]{"zoo_1", "zoo_2", "zoo_3"},
                    new String[]{"zoo_4", "zoo_5"}});

            for (var i = 0; i < 10; i++){
                for (var server : cluster.Servers){
                    serverAvailable.get(server.Name).add(AvailableStates.contains(jmxClient.GetNodeState(server)) ? 1 : 0);
                }
                Thread.sleep(sleepTime);
            }

            blockadeClient.RemoveAllPartitions(blockade);

            for (var i = 0; i < 10; i++){
                for (var server : cluster.Servers){
                    serverAvailable.get(server.Name).add(AvailableStates.contains(jmxClient.GetNodeState(server)) ? 1 : 0);
                }
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
        PrintToFile(serverAvailable, "SimpleScenario.json");
    }
}
