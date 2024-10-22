package org.example.automatons;

import com.sun.source.tree.Tree;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


public class ContextFreeGrammar {
    Set<String> nonTerminalStates;
    Set<String> terminalStates;
    List<String[]> transitions;
    String initialState;

    public ContextFreeGrammar() {
        nonTerminalStates = new TreeSet<>();
        terminalStates = new TreeSet<>();
        transitions = new ArrayList<>();
        initialState = "";
    }

    public ContextFreeGrammar(Set<String> nonTerminalStates, Set<String> terminalStates, List<String[]> transitions, String initialState) {
        this.nonTerminalStates = nonTerminalStates;
        this.terminalStates = terminalStates;
        this.transitions = transitions;
        this.initialState = initialState;
    }

    public void addNonTerminalState(String state) {
        nonTerminalStates.add(state);
    }

    public void addTerminalState(String state) {
        terminalStates.add(state);
    }

    public void addTransition(String from, String to) {
        transitions.add(new String[]{from, to});
    }

    public void setInitialState(String initialState) {
        this.initialState = initialState;
    }

    public void increaseStateNumbers(int increment) {
        initialState = increaseStateNumber(initialState, increment);
        Set<String> updatedSet = nonTerminalStates.stream()
                .map(s -> increaseStateNumber(s, increment))
                .collect(Collectors.toSet());
        nonTerminalStates.clear();
        nonTerminalStates.addAll(updatedSet);
        transitions.replaceAll(t -> new String[]{increaseStateNumber(t[0], increment), increaseStateNumbersInString( t[1], increment)});
    }

    private String increaseStateNumber(String state, int increment) {
        int number = Integer.parseInt(state.substring(2, state.length() - 1)) + increment;
        return "<S" + Integer.toString(number) + ">";
    }

    private String increaseStateNumbersInString(String input, int increment) {
        Pattern pattern = Pattern.compile("<S(\\d+)>");
        Matcher matcher = pattern.matcher(input);
        StringBuilder result = new StringBuilder();

        while (matcher.find()) {
            int number = Integer.parseInt(matcher.group(1)) + increment;
            matcher.appendReplacement(result, "<S" + number + ">");
        }
        matcher.appendTail(result);

        return result.toString();
    }

    public String getInitialState() {
        return initialState;
    }

    public List<String[]> getTransitions() {
        return transitions;
    }

    public Set<String> getTerminalStates() {
        return terminalStates;
    }

    public Set<String> getNonTerminalStates() {
        return nonTerminalStates;
    }

    public int getStatesQuantity() {
        return nonTerminalStates.size();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("V={");
        sb.append(String.join(",", nonTerminalStates));
        sb.append("}\n");

        sb.append("Sigma={");
        sb.append(String.join(",", terminalStates));
        sb.append("}\n");

        sb.append("R = {");

        for (String[] transition : transitions) {
            sb.append("(");
            sb.append(transition[0]).append(", ").append(transition[1]);
            sb.append(")");
            if (!transition.equals(transitions.get(transitions.size() - 1))) {
                sb.append(",");
            }
        }
        sb.append("}\n");

        sb.append("S=").append(initialState);

        return sb.toString();
    }

    public static ContextFreeGrammar join(ContextFreeGrammar left, ContextFreeGrammar right) {
        var grammar = new ContextFreeGrammar();

        left.increaseStateNumbers(1);
        right.increaseStateNumbers(1  + left.getStatesQuantity());

        grammar.getNonTerminalStates().addAll(left.getNonTerminalStates());
        grammar.getNonTerminalStates().addAll(right.getNonTerminalStates());

        grammar.getTerminalStates().addAll(left.getTerminalStates());
        grammar.getTerminalStates().addAll(right.getTerminalStates());

        grammar.getTransitions().addAll(left.getTransitions());
        grammar.getTransitions().addAll(right.getTransitions());

        return grammar;
    }
}
