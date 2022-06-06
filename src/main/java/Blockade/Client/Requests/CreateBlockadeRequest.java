package Blockade.Client.Requests;

import Blockade.Blockade;
import com.alibaba.fastjson2.annotation.JSONField;

import java.util.LinkedHashMap;

public class CreateBlockadeRequest {
    @JSONField(name = "containers")
    public final LinkedHashMap<String, BlockadeContainerRequest> Containers;

    public CreateBlockadeRequest(Blockade blockade){
        Containers = new LinkedHashMap<>();

        for (var container : blockade.Containers) {
            Containers.put(container.Hostname, BlockadeContainerRequest.FromValue(container));
        }
    }
}

