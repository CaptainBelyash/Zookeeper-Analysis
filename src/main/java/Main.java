import Blockade.Client.BlockadeHttpClient;
import Scenarios.EnsembleWithObserverScenario;
import Scenarios.OnlyLeaderDownScenario;
import Scenarios.SimpleScenario;

public class Main {
    public static void main(String[] args) throws Exception {

        var blockadeClient = new BlockadeHttpClient("localhost", 5000);

//        var simpleScenario = new SimpleScenario(blockadeClient);
//        simpleScenario.Execute();
//
//        var onlyLeaderDies = new OnlyLeaderDownScenario(blockadeClient, 10);
//        onlyLeaderDies.Execute(3, 5, 2);
//
        var onlyLeaderDies = new OnlyLeaderDownScenario(blockadeClient, 10);
        onlyLeaderDies.Execute(3, 10, 1);
//
//        var observersScenario = new EnsembleWithObserverScenario(blockadeClient, 5, 10);
//        observersScenario.Execute(5);
    }
}
