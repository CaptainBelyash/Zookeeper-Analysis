package Zookeeper;

import Blockade.Blockade;
import Blockade.BlockadeContainer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringJoiner;

public class ZookeeperBlockadeCluster extends ZookeeperCluster {
    private Blockade blockade;

    public ZookeeperBlockadeCluster(int serversCount) {
        super(serversCount);
    }

    public ZookeeperBlockadeCluster(int quorumServerCount, int observerServerCount) {
        super(quorumServerCount, observerServerCount);
    }

    public Blockade GetBlockade(String name){
        if (blockade != null)
            return blockade;

        var containers = new ArrayList<BlockadeContainer>();
        for (var i = 0; i < servers.size(); i++){
            var server = servers.get(i);

            var sj = new StringJoiner(" ");
            sj.add(String.format(
                    "server.%d=0.0.0.0:2888:3888%s;%d",
                    server.ServerId,
                    server.IsObserver ? ":observer" : "",
                    server.Port));

            for (var j = 0; j < servers.size(); j++){
                if (i == j)
                    continue;

                var neighbour = servers.get(j);
                sj.add(String.format("server.%d=%s:2888:3888%s;%d",
                        neighbour.ServerId,
                        neighbour.Address,
                        neighbour.IsObserver ? ":observer" : "",
                        neighbour.Port));
            }

            var environment = new HashMap<String, String>();
            environment.put("ZOO_MY_ID", String.valueOf(server.ServerId));
            environment.put("ZOO_SERVERS", sj.toString());

            if (server.IsObserver)
                environment.put("ZOO_CFG_EXTRA", "peerType=observer");

            // Environment variables for monitoring
            environment.put("JMXPORT", "4048");

            // In theory, these settings are default.
            // But I somehow caught a case when these settings were different from the default ones,
            // so now I'm setting them manually.
            // But now I can't repeat this case again.
            environment.put("JMXLOCALONLY", "false");
            environment.put("JMXDISABLE", "false");
            environment.put("JMXSSL", "false");
            environment.put("JMXAUTH", "false");

            // Also, for some reason the "*" argument here is not working properly.
            environment.put("ZOO_4LW_COMMANDS_WHITELIST", "conf, cons, crst, dump, envi, ruok, wchp, " +
                    "srst, srvr, stat, wchs, wchc, dirs, mntr");

            containers.add(new BlockadeContainer(
                    "zookeeper",
                    server.Name,
                    new int[] {server.Port},
                    environment
                    ));
        }

        blockade = new Blockade(name, containers.toArray(new BlockadeContainer[0]));
        return blockade;
    }
}
