package org.example.automatons;

import java.util.*;

public class PushdownAutomaton {
    private Set<String> states; // K
    private Set<String> alphabet;  // Sigma
    private Set<String> stackAlphabet; // Gamma
    private List<PushdownAutomatonTransition> transitions; // Delta
    private String initialState;  // s
    private Set<String> finalState;  // F

    public PushdownAutomaton(Set<String> states, Set<String> alphabet, Set<String> stackAlphabet, List<PushdownAutomatonTransition> transitions, String initialState, Set<String> finalState) {
        this.states = states;
        this.alphabet = alphabet;
        this.stackAlphabet = stackAlphabet;
        this.transitions = transitions;
        this.initialState = initialState;
        this.finalState = finalState;
    }

    public PushdownAutomaton(ContextFreeGrammar cfg) {
        formalizeAutomaton(cfg);
    }

    private void formalizeAutomaton(ContextFreeGrammar cfg) {
        this.states = new TreeSet<>(List.of("q0", "q1"));
        this.alphabet = new TreeSet<>(cfg.getTerminalStates());
        this.stackAlphabet = new HashSet<>(cfg.getTerminalStates());
        this.initialState = "q0";
        this.finalState = new TreeSet<>(List.of("q1"));
        this.transitions = getTransitions(cfg);

        this.stackAlphabet.addAll(cfg.nonTerminalStates);
    }

    private List<PushdownAutomatonTransition> getTransitions(ContextFreeGrammar cfg) {
        List<PushdownAutomatonTransition> transitions = new ArrayList<>();

        transitions.add(new PushdownAutomatonTransition(this.initialState, "_", "_", this.finalState.iterator().next(), cfg.getInitialState()));

        transitions.addAll(getTerminalTransitions(cfg.terminalStates));
        transitions.addAll(getNonTerminalTransitions(cfg.transitions));

        return transitions;
    }

    private List<PushdownAutomatonTransition> getTerminalTransitions(Set<String> cfgTerminals) {
        List<PushdownAutomatonTransition> transitions = new ArrayList<>();

        for (String s : cfgTerminals) {
            transitions.add(new PushdownAutomatonTransition(this.finalState.iterator().next(), s, s, this.finalState.iterator().next(), "_"));
        }

        return transitions;
    }

    private List<PushdownAutomatonTransition> getNonTerminalTransitions(List<String[]> cfgNonTerminals) {
        List<PushdownAutomatonTransition> transitions = new ArrayList<>();

        for (String[] s : cfgNonTerminals) {
            transitions.add(new PushdownAutomatonTransition(this.finalState.iterator().next(), "_", s[0], this.finalState.iterator().next(), s[1]));
        }

        return transitions;
    }

    public Set<String> getStates() {
        return states;
    }

    public Set<String> getAlphabet() {
        return alphabet;
    }

    public Set<String> getStackAlphabet() {
        return stackAlphabet;
    }

    public List<PushdownAutomatonTransition> getTransitions() {
        return transitions;
    }

    public String getInitialState() {
        return initialState;
    }

    public Set<String> getFinalState() {
        return finalState;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("K={");
        sb.append(String.join(",", this.states));
        sb.append("}\n");

        sb.append("Sigma={");
        sb.append(String.join(",", this.alphabet));
        sb.append("}\n");

        sb.append("Gamma={");
        sb.append(String.join(",", this.stackAlphabet));
        sb.append("}\n");

        sb.append("Delta = {");

        for (PushdownAutomatonTransition transition : this.transitions) {
            sb.append("(");

            sb.append("(").append(transition.getInitialState()).append(",").append(transition.getCharacter()).append(",").append(transition.getToPop()).append(")");
            sb.append(",");
            sb.append("(").append(transition.getFinalState()).append(",").append(transition.getToStack()).append(")");
            sb.append(")");


            if (!transition.equals(this.transitions.get(this.transitions.size() - 1))) {
                sb.append(",");
            }
        }
        sb.append("}\n");

        sb.append("s=").append(this.initialState).append("\n");

        sb.append("F=").append("{").append(this.finalState.iterator().next()).append("}").append("\n");

        return sb.toString();
    }
}
