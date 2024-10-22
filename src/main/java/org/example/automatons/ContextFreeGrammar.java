package org.example.automatons;

import java.util.List;


public class ContextFreeGrammar {
    List<String> nonTerminalStates;
    List<String> terminalStates;
    List<String> transitions;
    String initialState;

    public ContextFreeGrammar(List<String> nonTerminalStates, List<String> terminalStates, List<String> transitions, String initialState) {
        this.nonTerminalStates = nonTerminalStates;
        this.terminalStates = terminalStates;
        this.transitions = transitions;
        this.initialState = initialState;
    }
}
