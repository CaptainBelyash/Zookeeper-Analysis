package Scenarios;

import Blockade.Client.BlockadeHttpClient;
import Utils.ArrayUtils;
import Zookeeper.JMXClient.ServerState;
import Zookeeper.JMXClient.ZookeeperJMXClient;
import Zookeeper.ZookeeperBlockadeCluster;

import java.util.ArrayList;
import java.util.HashMap;

import static Utils.JSONtoFile.PrintToFile;

public class OnlyLeaderDownScenario {
    private final String baseTestName = "OnlyLeaderDown%d";
    private final BlockadeHttpClient blockadeClient;
    private final int testCount;
    private final int heartbeatTime = 4000;

    public OnlyLeaderDownScenario(BlockadeHttpClient blockadeClient, int testCount){
        this.blockadeClient = blockadeClient;
        this.testCount = testCount;
    }

    public HashMap<Integer, ArrayList<ArrayList<Long>>> GetElectionTimesByNodeCountsInterval(int minNodesCount, int maxNodesCount, int step) throws Exception {
        var result = new HashMap<Integer, ArrayList<ArrayList<Long>>>();

        for (var i=minNodesCount; i <= maxNodesCount; i+=step) {
            result.put(i, GetElectionTimesByNodeCount(i));
            PrintToFile(result.get(i), String.format("OnlyLeaderDownScenario_%d_Nodes", i));
        }

        return result;
    }

    public ArrayList<ArrayList<Long>> GetElectionTimesByNodeCount(int nodesCount) throws Exception {
        var cluster = new ZookeeperBlockadeCluster(nodesCount, String.format(baseTestName, nodesCount));
        var blockade = cluster.GetBlockade();

        var result = new ArrayList<ArrayList<Long>>();
        ZookeeperJMXClient jmxClient = null;
        try{
            blockadeClient.CreateBlockade(blockade);
            jmxClient = new ZookeeperJMXClient(cluster);
            Thread.sleep(heartbeatTime);

            for (var i=0; i < testCount; i++) {
                System.out.printf("Only leader down with %d nodes. Test %d/%d\n", nodesCount, i + 1, testCount);

                String leader = null;
                // in case when new leader didn't crown
                while (leader == null) {
                    var groupedByState = jmxClient.GroupServersByStates();

                    if (groupedByState.containsKey(ServerState.LEADER))
                        leader = groupedByState.get(ServerState.LEADER).get(0).Name;
                    Thread.sleep(heartbeatTime / 2);
                }

                var others = ArrayUtils.GetReminder(new String[]{leader}, cluster.GetServerNames());

                blockadeClient.StartNewPartition(blockade, new String[][]{new String[]{leader}, others});

                Thread.sleep(heartbeatTime);
                // wait for new election done
                while (jmxClient.GroupServersByStates(cluster.GetServersByNames(others)).containsKey(ServerState.ELECTION)){
                    Thread.sleep(heartbeatTime / 2);
                }

                result.add(jmxClient.GetLastElectionTimeTaken());
                blockadeClient.RemoveAllPartitions(blockade);
                Thread.sleep(heartbeatTime);
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
        return result;
    }
}
