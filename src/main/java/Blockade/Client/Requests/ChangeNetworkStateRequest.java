package Blockade.Client.Requests;

import Blockade.Enums.NetworkState;
import com.alibaba.fastjson2.annotation.JSONField;

public class ChangeNetworkStateRequest {
    @JSONField(name = "network_state")
    public final String NetworkState;

    @JSONField(name = "container_names")
    public final String[] ContainerNames;

    public ChangeNetworkStateRequest(String[] containerNames, NetworkState networkState){
        ContainerNames = containerNames;
        NetworkState = networkState.getState();
    }
}
