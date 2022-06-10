package Scenarios;

import Blockade.Client.BlockadeHttpClient;
import Utils.ArrayUtils;
import Zookeeper.JMXClient.ServerState;
import Zookeeper.JMXClient.ZookeeperJMXClient;
import Zookeeper.ZookeeperBlockadeCluster;

import java.util.ArrayList;
import java.util.HashMap;

import static Utils.JSONtoFile.PrintToFile;

public class CriticalQuorumDownTests {
    private final String baseTestName = "OnlyLeaderDown%d";
    private final BlockadeHttpClient blockadeClient;
    private final int testCount;
    private final int heartbeatTime = 4000;
    private final int quorumUnavailableTime = 8000;

    public CriticalQuorumDownTests(BlockadeHttpClient blockadeClient, int testCount){
        this.blockadeClient = blockadeClient;
        this.testCount = testCount;
    }

    public HashMap<Integer, ArrayList<ArrayList<Long>>> GetElectionTimesByNodeCountsInterval(int minNodesCount, int maxNodesCount, int step) throws Exception {
        var result = new HashMap<Integer, ArrayList<ArrayList<Long>>>();

        for (var i=minNodesCount; i <= maxNodesCount; i+=step) {
            result.put(i, GetElectionTimesByNodeCount(i));
        }

        return result;
    }

    public ArrayList<ArrayList<Long>> GetElectionTimesByNodeCount(int nodesCount) throws Exception {
        var cluster = new ZookeeperBlockadeCluster(nodesCount, String.format(baseTestName, nodesCount));
        var blockade = cluster.GetBlockade();

        int quorumMinSize = nodesCount / 2 + 1;

        var result = new ArrayList<ArrayList<Long>>();
        ZookeeperJMXClient jmxClient = null;
        try{
            blockadeClient.CreateBlockade(blockade);
            jmxClient = new ZookeeperJMXClient(cluster);
            Thread.sleep(heartbeatTime);

            for (var i=0; i < testCount; i++) {
                System.out.printf("Critical Quorum down with %d nodes. Test %d/%d\n", nodesCount, i + 1, testCount);

                var criticalClusterPart = ArrayUtils.GetRandomSlice(cluster.GetServerNames(), quorumMinSize);
                var firstHalfOfQuorum = ArrayUtils.GetRandomSlice(criticalClusterPart, quorumMinSize / 2);
                var secondHalfOfQuorum = ArrayUtils.GetReminder(criticalClusterPart, firstHalfOfQuorum);
                var otherServers = ArrayUtils.GetReminder(criticalClusterPart, cluster.GetServerNames());

                blockadeClient.StartNewPartition(blockade, new String[][]{firstHalfOfQuorum, secondHalfOfQuorum, otherServers});
                Thread.sleep(quorumUnavailableTime);

                blockadeClient.RemoveAllPartitions(blockade);
                Thread.sleep(quorumUnavailableTime * 2);
                blockadeClient.StartNewPartition(blockade, new String[][]{new String[]{"zoo_1"}, new String[]{"zoo_2", "zoo_3"}});
                Thread.sleep(quorumUnavailableTime * 2);
                blockadeClient.RemoveAllPartitions(blockade);
                Thread.sleep(quorumUnavailableTime * 2);


                // wait for new election done
                while (jmxClient.GroupServersByStates().containsKey(ServerState.ELECTION)){
                    Thread.sleep(heartbeatTime / 2);
                }

                result.add(jmxClient.GetLastElectionTimeTaken());
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
