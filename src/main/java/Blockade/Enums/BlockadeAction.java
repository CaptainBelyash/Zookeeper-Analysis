package Blockade.Enums;

public enum BlockadeAction {
    START("start"),
    STOP("stop"),
    RESTART("restart"),
    KILL("kill");

    private final String action;
    BlockadeAction(String action){
        this.action = action;
    }
    public String getAction(){ return action;}
}
