package Blockade.Client.Requests;

import Blockade.Enums.BlockadeAction;
import com.alibaba.fastjson2.annotation.JSONField;

public class ExecuteActionRequest {
    @JSONField(name = "command")
    public final String Action;

    @JSONField(name = "container_names")
    public final String[] ContainerNames;

    public ExecuteActionRequest(String[] containerNames, BlockadeAction action){
        ContainerNames = containerNames;
        Action = action.getAction();
    }
}
