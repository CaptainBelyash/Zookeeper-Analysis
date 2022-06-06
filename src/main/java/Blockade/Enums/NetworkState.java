package Blockade.Enums;

public enum NetworkState {
    FAST("fast"),
    SLOW("slow"),
    DUPLICATE("duplicate"),
    FLAKY("flaky");

    private final String state;
    NetworkState(String state){
        this.state = state;
    }
    public String getState(){ return state;}
}
