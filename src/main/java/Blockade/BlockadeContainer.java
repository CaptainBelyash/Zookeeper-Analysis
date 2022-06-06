package Blockade;

import java.util.HashMap;

public class BlockadeContainer {
    public String Image;
    public String Hostname;
    public int[] Ports;
    public HashMap<String, String> Environment;

    public BlockadeContainer(String image, String hostname, int[] ports, HashMap<String, String> environment){
        Image = image;
        Hostname = hostname;
        Ports = ports;
        Environment = environment;
    }
}
