package Zookeeper.JMXClient;

import Zookeeper.ZookeeperServer;
import org.apache.zookeeper.server.quorum.*;

import javax.management.MBeanServerInvocationHandler;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;
import java.util.Arrays;

public class ZookeeperJMXClient {
    public static ZookeeperNodeMetrics GetServerMetrics(ZookeeperServer server) {
        try{
            var url = new JMXServiceURL(String.format("service:jmx:rmi:///jndi/rmi://%s:4048/jmxrmi", server.Address));
            var jmxConnector = JMXConnectorFactory.connect(url);

            var mbeanServerConnection = jmxConnector.getMBeanServerConnection();

            ObjectName replicaMBeanName = new ObjectName(
                    String.format(
                            "org.apache.ZooKeeperService:name0=ReplicatedServer_id%d,name1=replica.%d",
                            server.ServerId,
                            server.ServerId));

            var replicaMBean = MBeanServerInvocationHandler.newProxyInstance(
                    mbeanServerConnection, replicaMBeanName, LocalPeerMXBean.class, true);

            var state = NodeState.GetByState(replicaMBean.getState());

            var serverMBeanName = new ObjectName(
                    String.format(
                            "org.apache.ZooKeeperService:name0=ReplicatedServer_id%d,name1=replica.%d,name2=%s",
                            server.ServerId,
                            server.ServerId,
                            state.getRole()));

            // I don't want to messed up with reflection
            switch (state){
                case LEADER:
                    return new ZookeeperNodeMetrics(
                            state,
                            replicaMBean,
                            MBeanServerInvocationHandler.newProxyInstance(
                                    mbeanServerConnection,
                                    serverMBeanName,
                                    LeaderMXBean.class,
                                    true));
                case FOLLOWER:
                    return new ZookeeperNodeMetrics(
                            state,
                            replicaMBean,
                            MBeanServerInvocationHandler.newProxyInstance(
                                    mbeanServerConnection,
                                    serverMBeanName,
                                    FollowerMXBean.class,
                                    true));
                case OBSERVER:
                    return new ZookeeperNodeMetrics(
                            state,
                            replicaMBean,
                            MBeanServerInvocationHandler.newProxyInstance(
                                    mbeanServerConnection,
                                    serverMBeanName,
                                    ObserverMXBean.class,
                                    true));
                case ELECTION:
                    return new ZookeeperNodeMetrics(
                            state,
                            replicaMBean,
                            MBeanServerInvocationHandler.newProxyInstance(
                                    mbeanServerConnection,
                                    serverMBeanName,
                                    LeaderElectionMXBean.class,
                                    true));
            }
        }
        catch (IOException | MalformedObjectNameException e){
            System.out.println(e.getMessage());
        }
        catch (Exception e){
            System.out.println("Unexpected error. Probably wrong name of mBean");
            System.out.println(Arrays.toString(e.getStackTrace()));
        }
        return null;
    }
}
