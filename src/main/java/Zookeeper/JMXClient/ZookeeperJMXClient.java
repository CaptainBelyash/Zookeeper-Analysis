package Zookeeper.JMXClient;

import Zookeeper.ZookeeperCluster;
import Zookeeper.ZookeeperServer;
import org.apache.zookeeper.server.quorum.*;

import javax.management.MBeanServerInvocationHandler;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class ZookeeperJMXClient {
    private final HashMap<ZookeeperServer, JMXConnector> jmxConnectors = new HashMap<>();

    public ZookeeperJMXClient(ZookeeperCluster cluster) throws IOException {
        for (var server: cluster.Servers) {
            var url = new JMXServiceURL(String.format("service:jmx:rmi:///jndi/rmi://%s:4048/jmxrmi", server.Address));
            jmxConnectors.put(server, JMXConnectorFactory.connect(url));
        }
    }

    public void Close() throws IOException {
        for (var connector: jmxConnectors.values()) {
            connector.close();
        }
    }

    public HashMap<ServerState, ArrayList<ZookeeperServer>> GroupServersByStates() throws Exception {
        var stateToServers = new HashMap<ServerState, ArrayList<ZookeeperServer>>();
        for (var server: jmxConnectors.keySet()) {
            var state = GetNodeState(server);
            if (!stateToServers.containsKey(state))
                stateToServers.put(state, new ArrayList<>());
            stateToServers.get(state).add(server);
        }
        return stateToServers;
    }


    public HashMap<ServerState, ArrayList<ZookeeperServer>> GroupServersByStates(ZookeeperServer[] servers) throws Exception {
        var stateToServers = new HashMap<ServerState, ArrayList<ZookeeperServer>>();
        for (var server: servers) {
            var state = GetNodeState(server);
            if (!stateToServers.containsKey(state))
                stateToServers.put(state, new ArrayList<>());
            stateToServers.get(state).add(server);
        }
        return stateToServers;
    }

    public ServerState GetNodeState(ZookeeperServer server) throws Exception {
        LocalPeerMXBean replicaMBean;
        try{
            var replicaMBeanName = new ObjectName(
                    String.format("org.apache.ZooKeeperService:name0=ReplicatedServer_id%d,name1=replica.%d",
                            server.ServerId,
                            server.ServerId));

            var mbeanServerConnection = jmxConnectors.get(server).getMBeanServerConnection();
            replicaMBean = MBeanServerInvocationHandler.newProxyInstance(
                    mbeanServerConnection, replicaMBeanName, LocalPeerMXBean.class, true);
        } catch (Exception e){
            throw new Exception("Something goes wrong with JMX. Error message: " + e.getMessage());
        }

        return ServerState.GetByState(replicaMBean.getState());
    }

    public ArrayList<Long> GetLastElectionTimeTaken() throws Exception {
        var result = new ArrayList<Long>();

        for (var server : jmxConnectors.keySet()){
            var state = GetNodeState(server);
            switch (state)
            {
                case LEADER:
                    result.add(GetLastElectionTimeTakenLeader(server));
                    break;
                case FOLLOWER:
                    result.add(GetLastElectionTimeTakenFollower(server));
                    break;
            }
        }

        return result;
    }

    public long GetLastElectionTimeTakenLeader(ZookeeperServer server) throws Exception {
        LeaderMXBean leader;
        try{
            var leaderMXBeanName = new ObjectName(
                    String.format("org.apache.ZooKeeperService:name0=ReplicatedServer_id%d,name1=replica.%d,name2=Leader",
                            server.ServerId,
                            server.ServerId));

            var mbeanServerConnection = jmxConnectors.get(server).getMBeanServerConnection();
            leader = MBeanServerInvocationHandler.newProxyInstance(
                    mbeanServerConnection, leaderMXBeanName, LeaderMXBean.class, true);
        } catch (Exception e){
            throw new Exception("Something goes wrong with JMX. Error message: " + e.getMessage());
        }

        return leader.getElectionTimeTaken();
    }

    public long GetLastElectionTimeTakenFollower(ZookeeperServer server) throws Exception {
        FollowerMXBean follower;
        try{
            var followerMBeanName = new ObjectName(
                    String.format("org.apache.ZooKeeperService:name0=ReplicatedServer_id%d,name1=replica.%d,name2=Follower",
                            server.ServerId,
                            server.ServerId));

            var mbeanServerConnection = jmxConnectors.get(server).getMBeanServerConnection();
            follower = MBeanServerInvocationHandler.newProxyInstance(
                    mbeanServerConnection, followerMBeanName, FollowerMXBean.class, true);
        } catch (Exception e){
            throw new Exception("Something goes wrong with JMX. Error message: " + e.getMessage());
        }

        return follower.getElectionTimeTaken();
    }
}
