package Blockade.Client;

import Blockade.Blockade;
import Blockade.Client.Requests.AddPartitionRequest;
import Blockade.Client.Requests.ChangeNetworkStateRequest;
import Blockade.Client.Requests.CreateBlockadeRequest;
import Blockade.Client.Requests.ExecuteActionRequest;
import Blockade.Enums.BlockadeAction;
import Blockade.Enums.NetworkState;
import com.alibaba.fastjson2.JSON;
import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;

import java.io.IOException;

public class BlockadeHttpClient {
    private final String baseUrl;

    public BlockadeHttpClient(String address, int port){
        baseUrl = String.format("http://%s:%d/blockade", address, port);
    }

    // todo: not void
    public void GetAllBlockades() throws IOException {
        final Content getBlockade = Request.Get(baseUrl)
                .execute().returnContent();
        System.out.println(getBlockade.asString());
    }

    // todo: not void
    public void GetBlockade(Blockade blockade) throws IOException {
        final Content getBlockade = Request.Get(BuildUrl(baseUrl, blockade.Name))
                .execute().returnContent();
        System.out.println(getBlockade.asString());
    }

    public void StartNewBlockade(Blockade blockade) throws IOException {
        CreateBlockade(blockade);
        ReturnToNormal(blockade);
    }

    public void CreateBlockade(Blockade blockade) throws IOException {
        final Content postCreateBlockade = Request.Post(BuildUrl(baseUrl, blockade.Name))
                .addHeader("Content-Type", "application/json")
                .bodyString(JSON.toJSONString(new CreateBlockadeRequest(blockade)), ContentType.APPLICATION_JSON)
                .execute().returnContent();
    }

    public void DestroyBlockade(Blockade blockade) throws IOException {
        ExecuteAction(blockade, blockade.GetContainerNames(), BlockadeAction.KILL);
        DeleteBlockade(blockade);
    }

    public void DeleteBlockade(Blockade blockade) throws IOException {
        final Content deleteBlockade = Request.Delete(BuildUrl(baseUrl, blockade.Name))
                .execute().returnContent();
    }

    public void DeleteBlockade(String blockadeName) throws IOException {
        final Content deleteBlockade = Request.Delete(BuildUrl(baseUrl, blockadeName))
                .execute().returnContent();
    }

    public void StartNewPartition(Blockade blockade, String[][] partitions) throws IOException {
        RemoveAllPartitions(blockade);
        AddPartition(blockade, partitions);
    }

    public void AddPartition(Blockade blockade, String[][] partitions) throws IOException {
        final Content postAddPartition = Request.Post(BuildUrl(baseUrl, blockade.Name, "partitions"))
                .addHeader("Content-Type", "application/json")
                .bodyString(JSON.toJSONString(new AddPartitionRequest(partitions)), ContentType.APPLICATION_JSON)
                .execute().returnContent();
    }

    public void RemoveAllPartitions(Blockade blockade) throws IOException {
        final Content deletePartitions = Request.Delete(BuildUrl(baseUrl, blockade.Name, "partitions"))
                .execute().returnContent();
    }

    public void ReturnToNormal(Blockade blockade) throws IOException {
        var containers = blockade.GetContainerNames();
        ExecuteAction(blockade, containers, BlockadeAction.START);
        ChangeNetworkState(blockade, containers, NetworkState.FAST);
    }

    public void ChangeNetworkState(Blockade blockade, String[] containerNames, NetworkState state) throws IOException {
        final Content postChangeNetworkState = Request.Post(BuildUrl(baseUrl, blockade.Name, "network_state"))
                .addHeader("Content-Type", "application/json")
                .bodyString(JSON.toJSONString(new ChangeNetworkStateRequest(containerNames, state)), ContentType.APPLICATION_JSON)
                .execute().returnContent();
    }

    public void ExecuteAction(Blockade blockade, String[] containerNames, BlockadeAction action) throws IOException {
        final Content postExecuteAction = Request.Post(BuildUrl(baseUrl, blockade.Name, "action"))
                .addHeader("Content-Type", "application/json")
                .bodyString(JSON.toJSONString(new ExecuteActionRequest(containerNames, action)), ContentType.APPLICATION_JSON)
                .execute().returnContent();
    }

    private String BuildUrl(String... urlParts){
        return String.join("/", urlParts);
    }
}
