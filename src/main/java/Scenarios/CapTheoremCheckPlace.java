package Scenarios;

import Blockade.Client.BlockadeHttpClient;
import Utils.JSONtoFile;
import Zookeeper.ZookeeperBlockadeCluster;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.retry.RetryOneTime;
import org.apache.zookeeper.KeeperException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class CapTheoremCheckPlace {
    private final BlockadeHttpClient blockadeClient;

    private final int sleepTime = 1000;

    public CapTheoremCheckPlace(BlockadeHttpClient blockadeClient){
        this.blockadeClient = blockadeClient;
    }

    public void Execute() throws Exception {
        var result = new HashMap<String, ArrayList<Integer>>();
        var cluster = new ZookeeperBlockadeCluster(5, "CapCheckScenario");
        var blockade = cluster.GetBlockade();

        for (var serverName : cluster.GetServerNames())
            result.put(serverName, new ArrayList<>());

        var workingPart = new String[]{"zoo_1", "zoo_2", "zoo_3"};
        var brokenPart = new String[]{"zoo_4", "zoo_5"};

        try{
            blockadeClient.CreateBlockade(blockade);

            for (var server : cluster.Servers){
                var curatorClient = CuratorFrameworkFactory.newClient(
                        String.format("%s:%d", server.Address, server.Port), new RetryOneTime(500));
                curatorClient.start();
                curatorClient.create().forPath(String.format("/%s", server.Name), new byte[0]);
                curatorClient.setData().forPath(String.format("/%s", server.Name), String.format("data from %s", server.Name).getBytes());
            }

            for (var i = 0; i < 3; i++){
                for (var server : cluster.Servers){
                    try{
                        var curatorClient= CuratorFrameworkFactory.newClient(
                                String.format("%s:%d", server.Address, server.Port), new RetryOneTime(500));
                        curatorClient.start();

                        System.out.println(curatorClient.checkExists().forPath(String.format("/%s", server.Name)));
                        System.out.println(Arrays.toString(curatorClient.getData().forPath(String.format("/%s", server.Name))));

                        result.get(server.Name).add(1);
                    }catch (KeeperException e){
                        result.get(server.Name).add(0);
                        System.out.println(String.format("connection lost for %s", server.Name));
                    }
                }
            }

            blockadeClient.StartNewPartition(blockade, new String[][]{workingPart, brokenPart});

            Thread.sleep(sleepTime*3);
            for (var i = 0; i < 3; i++){
                for (var server : cluster.Servers){
                    try{
                        var curatorClient= CuratorFrameworkFactory.newClient(
                                String.format("%s:%d", server.Address, server.Port), new RetryOneTime(500));
                        curatorClient.start();

                        System.out.println(curatorClient.checkExists().forPath(String.format("/%s", server.Name)));
                        System.out.println(Arrays.toString(curatorClient.getData().forPath(String.format("/%s", server.Name))));

                        result.get(server.Name).add(1);
                    }catch (KeeperException e){
                        result.get(server.Name).add(0);
                        System.out.println(String.format("connection lost for %s", server.Name));
                    }
                }
            }
            blockadeClient.RemoveAllPartitions(blockade);

            Thread.sleep(sleepTime*3);
            for (var i = 0; i < 3; i++){
                for (var server : cluster.Servers){
                    try{
                        var curatorClient= CuratorFrameworkFactory.newClient(
                                String.format("%s:%d", server.Address, server.Port), new RetryOneTime(500));
                        curatorClient.start();

                        System.out.println(curatorClient.checkExists().forPath(String.format("/%s", server.Name)));
                        System.out.println(Arrays.toString(curatorClient.getData().forPath(String.format("/%s", server.Name))));

                        result.get(server.Name).add(1);
                    }catch (KeeperException e){
                        result.get(server.Name).add(0);
                        System.out.println(String.format("connection lost for %s", server.Name));
                    }
                }
            }
        }
        finally {
            blockadeClient.DestroyBlockade(blockade);
        }
        JSONtoFile.PrintToFile(result, "CapScenario.json");
    }

}
