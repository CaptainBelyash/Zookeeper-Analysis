package Zookeeper.JMXClient;

import org.apache.zookeeper.server.ZooKeeperServerMXBean;
import org.apache.zookeeper.server.quorum.LeaderElectionMXBean;
import org.apache.zookeeper.server.quorum.LocalPeerMXBean;

public class ZookeeperNodeMetrics {
    public final NodeState State;
    public final LocalPeerMXBean ReplicaBean;
    public final ZooKeeperServerMXBean ServerBean;
    public final LeaderElectionMXBean ElectionBean;

    public ZookeeperNodeMetrics(NodeState state, LocalPeerMXBean replicaBean, ZooKeeperServerMXBean serverBean) {
        State = state;
        ReplicaBean = replicaBean;
        ServerBean = serverBean;
        ElectionBean = null;
    }

    public ZookeeperNodeMetrics(NodeState state, LocalPeerMXBean replicaBean, LeaderElectionMXBean electionBean) {
        State = state;
        ReplicaBean = replicaBean;
        ElectionBean = electionBean;
        ServerBean = null;
    }
}
