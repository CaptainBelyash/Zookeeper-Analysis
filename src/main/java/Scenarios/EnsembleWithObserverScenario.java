package Scenarios;

import Blockade.Client.BlockadeHttpClient;
import Utils.ArrayUtils;
import Utils.JSONtoFile;
import Zookeeper.JMXClient.ServerState;
import Zookeeper.JMXClient.ZookeeperJMXClient;
import Zookeeper.ZookeeperBlockadeCluster;

import java.util.ArrayList;
import java.util.HashMap;

public class EnsembleWithObserverScenario {
    private final String baseTestName = "WithObservers%d";
    private final BlockadeHttpClient blockadeClient;
    private final int quorumSize;
    private final int testCount;
    private final int heartbeatTime = 7000;

    public EnsembleWithObserverScenario(BlockadeHttpClient blockadeClient, int quorumSize, int testCount){
        this.blockadeClient = blockadeClient;
        this.quorumSize = quorumSize;
        this.testCount = testCount;
    }

    public void Execute(int maxObserversCount) throws Exception {
        var result = new HashMap<Integer, ArrayList<ArrayList<Long>>>();

        for (var i = 0; i <= maxObserversCount + 1; i++) {
            result.put(i, GetElectionTimesByNodeCount(i));
        }

        JSONtoFile.PrintToFile(result, "EnsembleWithObservers.json");
    }

    public ArrayList<ArrayList<Long>> GetElectionTimesByNodeCount(int observersCount) throws Exception {
        var cluster = new ZookeeperBlockadeCluster(quorumSize, observersCount, String.format(baseTestName, observersCount));
        var blockade = cluster.GetBlockade();

        var result = new ArrayList<ArrayList<Long>>();
        ZookeeperJMXClient jmxClient = null;
        try{
            blockadeClient.CreateBlockade(blockade);
            Thread.sleep(heartbeatTime);
            jmxClient = new ZookeeperJMXClient(cluster);
            Thread.sleep(heartbeatTime);

            for (var i=0; i < testCount; i++) {
                System.out.printf("Observers scenario with %d observers. Test %d/%d\n", observersCount, i + 1, testCount);

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
