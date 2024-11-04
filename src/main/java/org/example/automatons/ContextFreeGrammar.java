package org.example.automatons;

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
        terminalStates = new TreeSet<>(pda.getAlphabet());
        transitions = new ArrayList<>();
        initialState = "<S>";
        nonTerminalStates.add(initialState);
        String[] newTransition;
        // first rule
        for (String f : pda.getFinalState()) {
            newTransition = new String[]{initialState, "<" + pda.getInitialState() + ",_," + f + ">"};
            transitions.add(newTransition);
            nonTerminalStates.add("<" + pda.getInitialState() + ",_," + f + ">");
        }
        // second rule
        for (String s : pda.getStates()) {
            newTransition = new String[]{"<" + s + ",_," + s + ">", "_"};
            transitions.add(newTransition);
            nonTerminalStates.add("<" + s + ",_," + s + ">");
        }

        for (PushdownAutomatonTransition pdaTransition : pda.getTransitions()) {
            addTransitionsFromPdaTransition(pdaTransition, pda);
        }
    }

    private void addTransitionsFromPdaTransition(PushdownAutomatonTransition pdaTransition, PushdownAutomaton pda) {
        String[] newTransition;
        List<String> stackAlphabet = separateStackAlphabet(pdaTransition.getToStack(), pda.getStackAlphabet());
        // rule 4 and 5
        if (Objects.equals(pdaTransition.getToStack(), "_") && Objects.equals(pdaTransition.getToPop(), "_")) {
            pda.getStackAlphabet().add("_");
            for (String c : pda.getStackAlphabet()) {
                for (String s : pda.getStates()) {
                    newTransition = new String[]{
                            "<" + pdaTransition.getInitialState() + "," + c + "," + s + ">",
                            pdaTransition.getCharacter() + "<" + pdaTransition.getFinalState() + "," + c + "," + s + ">"
                    };
                    transitions.add(newTransition);
                    nonTerminalStates.add("<" + pdaTransition.getInitialState() + "," + c + "," + s + ">");
                    nonTerminalStates.add("<" + pdaTransition.getFinalState() + "," + c + "," + s + ">");
                }
            }
            pda.getStackAlphabet().remove("_");
            // rule 3
        } else if (Objects.equals(pdaTransition.getToStack(), "_")) {
            for (String s : pda.getStates()) {
                newTransition = new String[]{
                        "<" + pdaTransition.getInitialState() + "," + pdaTransition.getToPop() + "," + s + ">",
                        pdaTransition.getCharacter() + "<" + pdaTransition.getFinalState() + ",_," + s + ">"
                };
                transitions.add(newTransition);
                nonTerminalStates.add("<" + pdaTransition.getInitialState() + "," + pdaTransition.getToPop() + "," + s + ">");
                nonTerminalStates.add("<" + pdaTransition.getFinalState() + ",_," + s + ">");
            }
            // rule 7 and 8
        } else if (Objects.equals(pdaTransition.getToPop(), "_")) {
            addTransitionWithStackAlphabet(pdaTransition, pda, stackAlphabet, "_", false);

            for (String c : pda.getStackAlphabet()) {
                addTransitionWithStackAlphabet(pdaTransition, pda, stackAlphabet, c, true);
            }
            // rule 6
        } else {
            addTransitionWithStackAlphabet(pdaTransition, pda, stackAlphabet, pdaTransition.getToPop(), false);
        }
    }

    private void addTransitionWithStackAlphabet(PushdownAutomatonTransition pdaTransition, PushdownAutomaton pda, List<String> stackAlphabet, String toPop, boolean lastEqualPop) {
        String[] newTransition;
        String newState;
        int numberOfCombinations = stackAlphabet.size();
        if (lastEqualPop) numberOfCombinations++;
        List<List<String>> combinatorics = generateCombinations(pda.getStates(), pdaTransition.getFinalState(), numberOfCombinations);
        for (String s : pda.getStates()) {
            for (int i = 0; i < combinatorics.size(); i++) {
                newTransition = new String[]{"<" + pdaTransition.getInitialState() + "," + toPop + "," + s + ">", pdaTransition.getCharacter()};
                nonTerminalStates.add("<" + pdaTransition.getInitialState() + "," + toPop + "," + s + ">");
                for (int j = 0; j < combinatorics.get(0).size() - 1; j++) {
                    newState = "<" + combinatorics.get(i).get(j) + "," +
                            stackAlphabet.get(j) + "," + combinatorics.get(i).get(j + 1) + ">";
                    newTransition[1] += newState;
                    nonTerminalStates.add(newState);
                }
                int last = combinatorics.get(0).size() - 1;
                if (lastEqualPop) newState = "<" + combinatorics.get(i).get(last) + "," + toPop + "," + s + ">";
                else newState = "<" + combinatorics.get(i).get(last) + "," + stackAlphabet.get(last) + "," + s + ">";
                newTransition[1] += newState;
                transitions.add(newTransition);
                nonTerminalStates.add(newState);
            }
        }
    }

    private List<String> separateStackAlphabet(String string, Set<String> stackAlphabet) {
        List<String> result = new ArrayList<>();
        int i = 0;
        int j = 1;
        while (j < string.length() + 1) {
            if (stackAlphabet.contains(string.substring(i, j))) {
                result.add(string.substring(i, j));
                i = j;
            }
            j++;
        }
        return result;
    }

    private boolean allStatesAreInSet(String states, Set<String> set) {
        List<String> separatedStates = getNonTerminalStatesFromTransition(states);
        for (String state : separatedStates) {
            if (!set.contains(state)) return false;
        }
        return true;
    }

    private static List<String> getNonTerminalStatesFromTransition(String transition) {
        List<String> result = new ArrayList<>();
        int openIndex = -1;
        int balance = 0;

        for (int i = 0; i < transition.length(); i++) {
            char c = transition.charAt(i);

            if (c == '<') {
                if (balance == 0) {
                    openIndex = i;
                }
                balance++;
            }

            else if (c == '>') {
                balance--;
                if (balance == 0 && openIndex != -1) {
                    result.add(transition.substring(openIndex, i + 1));
                    openIndex = -1;
                }
            }
        }
        return result;
    }

    public void minimize() {
        do {
            removeUnitProductions();
            removeUselessProductions();
        } while (inlineExpansion());

        renameNonTerminalStates();
        removeEmptyCharacterWhenConcatenated();
    }

    private boolean inlineExpansion() {
        var changed = false;
        for (String[] t : transitions) {
            List<String> states = getNonTerminalStatesFromTransition(t[1]);
            if (!states.isEmpty())
                continue;
            int flag = 0;
            for (String[] t2 : transitions) {
                if (Objects.equals(t2[0], t[0]) && !Objects.equals(t2[1], t[1])) {
                    flag = 1;
                }
            }
            if (flag == 1)
                continue;
            for (String[] t2 : transitions) {
                if (t2[1].contains(t[0])) {
                    t2[1] = t2[1].replaceAll(t[0], t[1]);
                    changed = true;
                }
            }
        }
        return changed;
    }

    private void removeEmptyCharacterWhenConcatenated() {
        for (String[] t : transitions) {
            if (t[1].length() <= 1)
                continue;
            t[1] = t[1].replaceAll("_", "");
        }
    }

    private void removeUnitProductions() {
        List<String[]> newTransitions = new ArrayList<>();
        for (String[] t : transitions) {
            List<String> states = getNonTerminalStatesFromTransition(t[1]);
            if (!terminalStates.contains(Character.toString(t[1].charAt(0))) && states.size() == 1) continue;
            newTransitions.add(t);
        }

        for (String[] t : transitions) {
            List<String> states = getNonTerminalStatesFromTransition(t[1]);
            if (states.size() != 1) continue;
            if (terminalStates.contains(Character.toString(t[1].charAt(0)))) continue;
            for (String[] t2 : transitions) {
                if (Objects.equals(t2[0], states.get(0))) {
                    newTransitions.add(new String[]{t[0], t2[1]});
                }
            }
        }

        transitions = newTransitions;
    }

    private void removeUselessProductions() {
        List<String[]> copy = new ArrayList<>();
        while (!copy.equals(transitions)) {
            copy = new ArrayList<>(transitions);
            removeInfiniteProductions();
            removeUnreachableVariables();
        }
    }

    private void removeInfiniteProductions() {
        Set<String> toRemove = new TreeSet<>(nonTerminalStates);
        Set<String> terminalStates = new TreeSet<>();
        for (String[] t : transitions) {
            List<String> states = getNonTerminalStatesFromTransition(t[1]);
            if (states.isEmpty()) {
                toRemove.remove(t[0]);
                terminalStates.add(t[0]);
            }
        }

        Set<String> copy = new TreeSet<>();
        while (!copy.equals(toRemove)) {
            copy = new TreeSet<>(toRemove);
            for (String[] t : transitions) {
                if (allStatesAreInSet(t[1], terminalStates)) {
                    toRemove.remove(t[0]);
                    terminalStates.add(t[0]);
                }
            }
        }

        for (String s : toRemove) {
            removeNonTerminalState(s);
        }
    }

    private void removeUnreachableVariables() {
        Set<String> toRemove = new TreeSet<>(nonTerminalStates);
        Set<String> copy = new TreeSet<>();
        while (!copy.equals(toRemove)) {
            copy = new TreeSet<>(toRemove);
            for (String s : nonTerminalStates) {
                if (Objects.equals(s, initialState)) {
                    toRemove.remove(s);
                    continue;
                }
                for (String[] t : transitions) {
                    List<String> states = getNonTerminalStatesFromTransition(t[1]);
                    if (states.contains(s)) toRemove.remove(s);
                }
            }

            for (String s : toRemove) {
                removeNonTerminalState(s);
            }
        }

    }

    private void removeNonTerminalState(String state) {
        ArrayList<String[]> toRemove = new ArrayList<>();
        for (String[] t : transitions) {
            if (t[0].contains(state)) toRemove.add(t);
            if (t[1].contains(state)) toRemove.add(t);
        }
        for (String[] t : toRemove) {
            transitions.remove(t);
        }
        nonTerminalStates.remove(state);
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

    private void renameNonTerminalStates() {
        List<String> toRemove = new ArrayList<>();
        List<String> toAdd = new ArrayList<>();
        int counter = 1;
        for (String state : nonTerminalStates) {
            if (Objects.equals(state, initialState)) continue;
            for (String[] t : transitions) {
                t[0] = t[0].replaceAll(state, "<S" + counter + ">");
                t[1] = t[1].replaceAll(state, "<S" + counter + ">");
            }
            toRemove.add(state);
            toAdd.add("<S" + counter + ">");
            counter++;
        }
        for (String s : toRemove) {
            nonTerminalStates.remove(s);
        }
        nonTerminalStates.addAll(toAdd);
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
