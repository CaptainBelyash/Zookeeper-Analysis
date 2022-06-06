package Blockade.Client.Requests;

import com.alibaba.fastjson2.annotation.JSONField;

public class AddPartitionRequest {
    @JSONField(name = "partitions")
    public final String[][] Partitions;

    public AddPartitionRequest(String[][] partitions){
        Partitions = partitions;
    }
}
