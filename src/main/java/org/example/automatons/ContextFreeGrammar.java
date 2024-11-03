package org.example.automatons;

import com.sun.source.tree.Tree;

import java.util.*;
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

    public ContextFreeGrammar(PushdownAutomaton pda) {
        nonTerminalStates = new TreeSet<>();
        terminalStates = new TreeSet<>();
        transitions = new ArrayList<>();
        initialState = "<S>";
        nonTerminalStates.add(initialState);
        String[] newTransition;
        // first and second rule
        for (String f : pda.getFinalState()) {
            newTransition = new String[]{initialState, "<" + pda.getInitialState() + ",_," + f + ">"};
            transitions.add(newTransition);
        }
        for (String s : pda.getStates()) {
            newTransition = new String[]{"<" + s + ",_," + s + ">", "_"};
            transitions.add(newTransition);
        }

        for (PushdownAutomatonTransition pdaTransition : pda.getTransitions()) {
            addTransitionsFromPdaTransition(pdaTransition, pda);
        }
    }

    private void addTransitionsFromPdaTransition(PushdownAutomatonTransition pdaTransition, PushdownAutomaton pda) {
        String[] newTransition;
        List<String> stackAlphabet = separateStackAlphabet(pdaTransition.getToStack(), pda);
        if (Objects.equals(pdaTransition.getToStack(), "_") && Objects.equals(pdaTransition.getToPop(), "_")) {
            pda.getStackAlphabet().add("_");
            for (String c : pda.getStackAlphabet()) {
                for (String s : pda.getStates()) {
                    newTransition = new String[]{
                            "<" + pdaTransition.getInitialState() + "," + c + "," + s + ">",
                            pdaTransition.getCharacter() + "<" + pdaTransition.getFinalState() + "," + c + "," + s + ">"
                    };
                    transitions.add(newTransition);
                }
            }
            pda.getStackAlphabet().remove("_");
        } else if (Objects.equals(pdaTransition.getToStack(), "_")) {
            for (String s : pda.getStates()) {
                newTransition = new String[]{
                        "<" + pdaTransition.getInitialState() + "," + pdaTransition.getToPop() + "," + s + ">",
                        pdaTransition.getCharacter() + "<" + pdaTransition.getFinalState() + ",_," + s + ">"
                };
                transitions.add(newTransition);
            }
        } else if (Objects.equals(pdaTransition.getToPop(), "_")) {
            addTransitionWithStackAlphabet(pdaTransition, pda, stackAlphabet, "_", false);

            for (String c : pda.getStackAlphabet()) {
                addTransitionWithStackAlphabet(pdaTransition, pda, stackAlphabet, c, true);
            }
        } else {
            addTransitionWithStackAlphabet(pdaTransition, pda, stackAlphabet, pdaTransition.getToPop(), false);
        }
    }

    private void addTransitionWithStackAlphabet(PushdownAutomatonTransition pdaTransition, PushdownAutomaton pda, List<String> stackAlphabet, String toPop, boolean lastEqualPop) {
        String[] newTransition;
        int numberOfCombinations = stackAlphabet.size();
        if (lastEqualPop) numberOfCombinations++;
        List<List<String>> combinatorics = generateCombinations(pda.getStates(), pdaTransition.getFinalState(), numberOfCombinations);
        for (String s : pda.getStates()) {
            for (int i = 0; i < combinatorics.size(); i++) {
                newTransition = new String[]{"<" + pdaTransition.getInitialState() + "," + toPop + "," + s + ">", pdaTransition.getCharacter()};
                for (int j = 0; j < combinatorics.get(0).size() - 1; j++) {
                    newTransition[1] += "<" + combinatorics.get(i).get(j) + "," +
                            stackAlphabet.get(j) + "," + combinatorics.get(i).get(j + 1) + ">";
                }
                int last = combinatorics.get(0).size() - 1;
                if (lastEqualPop) newTransition[1] += "<" + combinatorics.get(i).get(last) + "," + toPop + "," + s + ">";
                else newTransition[1] += "<" + combinatorics.get(i).get(last) + "," + stackAlphabet.get(last) + "," + s + ">";
                transitions.add(newTransition);
            }
        }
    }

    private List<String> separateStackAlphabet(String string, PushdownAutomaton pda) {
        List<String> stackAlphabet = new ArrayList<>();
        int i = 0;
        int j = 1;
        while (j < string.length() + 1) {
            if (pda.getStackAlphabet().contains(string.substring(i, j))) {
                stackAlphabet.add(string.substring(i, j));
                i = j;
            }
            j++;
        }
        return stackAlphabet;
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
        transitions.replaceAll(t -> new String[]{increaseStateNumber(t[0], increment), increaseStateNumbersInString(t[1], increment)});
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

        sb.append("S=").append(initialState).append("\n");

        return sb.toString();
    }

    public static ContextFreeGrammar join(ContextFreeGrammar left, ContextFreeGrammar right) {
        var grammar = new ContextFreeGrammar();

        left.increaseStateNumbers(1);
        right.increaseStateNumbers(1 + left.getStatesQuantity());

        grammar.getNonTerminalStates().addAll(left.getNonTerminalStates());
        grammar.getNonTerminalStates().addAll(right.getNonTerminalStates());

        grammar.getTerminalStates().addAll(left.getTerminalStates());
        grammar.getTerminalStates().addAll(right.getTerminalStates());

        grammar.getTransitions().addAll(left.getTransitions());
        grammar.getTransitions().addAll(right.getTransitions());

        return grammar;
    }

    private List<List<String>> generateCombinations(Set<String> set, String lock, int n) {
        List<List<String>> results = new ArrayList<>();

        // Start recursive generation with the locked first element
        List<String> currentCombination = new ArrayList<>();
        currentCombination.add(lock); // Lock the first element
        generateRecursive(set, n - 1, currentCombination, results); // Start with n - 1 levels remaining

        return results;
    }

    private void generateRecursive(Set<String> set, int depth, List<String> currentCombination, List<List<String>> results) {
        if (depth == 0) {
            // Base case: if depth is 0, add a copy of the current combination to results
            results.add(new ArrayList<>(currentCombination));
            return;
        }

        // Recursive case: iterate over all elements in the set and add them to the combination
        for (String element : set) {
            currentCombination.add(element);
            generateRecursive(set, depth - 1, currentCombination, results); // Recur with one less level
            currentCombination.remove(currentCombination.size() - 1); // Backtrack
        }
    }
}
