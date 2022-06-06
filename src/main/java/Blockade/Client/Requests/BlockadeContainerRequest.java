package Blockade.Client.Requests;

import Blockade.BlockadeContainer;
import com.alibaba.fastjson2.annotation.JSONField;

import java.util.HashMap;

public class BlockadeContainerRequest {
    @JSONField(name = "image")
    public final String Image;
    @JSONField(name = "hostname")
    public final String Hostname;
    @JSONField(name = "ports")
    public final int[] Ports;
    @JSONField(name = "environment")
    public final HashMap<String, String> Environment;

    private BlockadeContainerRequest(String image, String hostname, int[] ports, HashMap<String, String> environment){
        Image = image;
        Hostname = hostname;
        Ports = ports;
        Environment = environment;
    }

    public static BlockadeContainerRequest FromValue(BlockadeContainer container){
        return new BlockadeContainerRequest(container.Image, container.Hostname, container.Ports, container.Environment);
    }
}
