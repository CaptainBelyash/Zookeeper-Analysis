import Blockade.Client.BlockadeHttpClient;
import Zookeeper.ZookeeperBlockadeCluster;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {

        var client = new BlockadeHttpClient("localhost", 5000);

        // broken blockades
        client.GetAllBlockades();

        var blockade = new ZookeeperBlockadeCluster(3, 2).GetBlockade("somenamebeta4");
        client.StartNewBlockade(blockade);

        client.GetBlockade(blockade);

        client.StartNewPartition(blockade, new String[][]{new String[]{"zoo_1"},new String[]{"zoo_2", "zoo_3", "zoo_4", "zoo_5"}});
        client.GetBlockade(blockade);

        client.DestroyBlockade(blockade);


    }
}
