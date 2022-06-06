package Blockade;

import java.util.Arrays;
import java.util.stream.Collectors;

public class Blockade {
    public String Name;
    public BlockadeContainer[] Containers;

    public Blockade(String name, BlockadeContainer[] containers){
        Name = name;
        Containers = containers;
    }

    public String[] GetContainerNames(){
        return Arrays.stream(Containers).map(x -> x.Hostname).toArray(String[]::new);
    }
}
