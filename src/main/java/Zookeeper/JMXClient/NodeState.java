package Zookeeper.JMXClient;

public enum NodeState {
    LEADER("Leader", "leading"),
    FOLLOWER("Follower", "following"),
    OBSERVER("Observer", "observing"),
    ELECTION("LeaderElection", "leaderelection");

    private final String role;
    private final String state;

    NodeState(String role, String state)
    {
        this.role = role;
        this.state = state;
    }

    public static NodeState GetByState(String state) throws Exception {
        switch (state){
            case "leading":
                return LEADER;
            case "following":
                return FOLLOWER;
            case "observing":
                return OBSERVER;
            case "leaderelection":
                return ELECTION;
            default:
                throw new Exception("Unexpected state");
        }
    }

    public String getRole(){ return role;}

    public String getState(){ return state;}
}
