package org.example.automatons;

public class PushdownAutomatonTransition {
    private String initialState;
    private String character;
    private String toPop;
    private String finalState;
    private String toStack;

    public PushdownAutomatonTransition(String initialState, String character, String toPop, String finalState, String toStack) {
        this.initialState = initialState;
        this.character = character;
        this.toPop = toPop;
        this.finalState = finalState;
        this.toStack = toStack;
    }

    public String getInitialState() {
        return initialState;
    }

    public void setInitialState(String initialState) {
        this.initialState = initialState;
    }

    public String getCharacter() {
        return character;
    }

    public void setCharacter(String character) {
        this.character = character;
    }

    public String getToPop() {
        return toPop;
    }

    public void setToPop(String toPop) {
        this.toPop = toPop;
    }

    public String getFinalState() {
        return finalState;
    }

    public void setFinalState(String finalState) {
        this.finalState = finalState;
    }

    public String getToStack() {
        return toStack;
    }

    public void setToStack(String toStack) {
        this.toStack = toStack;
    }
}
