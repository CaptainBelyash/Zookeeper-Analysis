import Blockade.Client.BlockadeHttpClient;
import Scenarios.OnlyLeaderDownScenario;
import com.alibaba.fastjson.JSON;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import static Utils.JSONtoFile.PrintToFile;

public class Main {
    public static void main(String[] args) throws Exception {

        var blockadeClient = new BlockadeHttpClient("localhost", 5000);

        // broken blockades, restart daemon and kill all broken zookeeper containers manually if this not empty
        blockadeClient.GetAllBlockades();

        //var leaderDownScenario = new OnlyLeaderDownScenario(blockadeClient, 20);


        var quorumDownScenario = new OnlyLeaderDownScenario(blockadeClient, 20);
        var result = quorumDownScenario.GetElectionTimesByNodeCount(3);
        PrintToFile(result, "OnlyLeaderDownScenario.json");
    }
}
