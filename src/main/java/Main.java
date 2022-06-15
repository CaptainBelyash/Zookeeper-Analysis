import Blockade.Client.BlockadeHttpClient;
import Blockade.Enums.NetworkState;
import Scenarios.EnsembleWithObserverScenario;
import Scenarios.OnlyLeaderDownScenario;
import Scenarios.PartitionScenario;
import Scenarios.SimpleScenario;

public class Main {
    public static void main(String[] args) throws Exception {

        var blockadeClient = new BlockadeHttpClient("localhost", 5000);
//        var partScenario = new PartitionScenario(blockadeClient);
//        partScenario.Execute(5);

//        var simpleScenario = new SimpleScenario(blockadeClient);
//        simpleScenario.Execute(NetworkState.FAST);
//        simpleScenario.Execute(NetworkState.SLOW);
//        simpleScenario.Execute(NetworkState.DUPLICATE);
//        simpleScenario.Execute(NetworkState.FLAKY);
//
//        var onlyLeaderDies = new OnlyLeaderDownScenario(blockadeClient, 10);
//        onlyLeaderDies.Execute(3, 5, 2);
//
        var onlyLeaderDies = new OnlyLeaderDownScenario(blockadeClient, 100);
        onlyLeaderDies.Execute(5, 5, 2);
        onlyLeaderDies.Execute(11, 11, 2);
//
//        var observersScenario = new EnsembleWithObserverScenario(blockadeClient, 5, 10);
//        observersScenario.Execute(5);
    }
}
