package org.example;

import org.example.automatons.ContextFreeGrammar;
import org.example.automatons.PushdownAutomaton;
import org.example.automatons.PushdownAutomatonTransition;
import org.example.expressions.ExpressionFactory;
import org.example.expressions.InvalidExpression;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class Main {
    public static void main(String[] args) throws InvalidExpression {
        var exp = ExpressionFactory.parseExpression(args[0]);

        var grammar = exp.toContextFreeGrammar();
        PushdownAutomaton pa = new PushdownAutomaton(grammar);

        var grammar2 = new ContextFreeGrammar(pa);

        System.out.println("GLC 1 M:");
        System.out.println(grammar);

        System.out.println("AP M:");
        System.out.println(pa);

        System.out.println("GLC 2 M:");
        System.out.println(grammar2);

        grammar2.minimize();

        System.out.println("GLC 2 M (minimizado):");
        System.out.println(grammar2);

        // construction of pda for testing purposes
        Set<String> states = new TreeSet<>();
        states.add("0");
        states.add("1");
        Set<String> alphabet = new TreeSet<>();
        alphabet.add("a");
        alphabet.add("b");
        Set<String> stackAlphabet = new TreeSet<>();
        stackAlphabet.add("#");
        List<PushdownAutomatonTransition> transitions = new ArrayList<>();
        transitions.add(new PushdownAutomatonTransition("0", "a", "_", "0", "#"));
        transitions.add(new PushdownAutomatonTransition("0", "b", "#", "1", "_"));
        transitions.add(new PushdownAutomatonTransition("1", "b", "#", "1", "_"));
        var pda = new PushdownAutomaton(states, alphabet, stackAlphabet, transitions, "0", states);
        var test = new ContextFreeGrammar(pda);
        test.minimize();
        System.out.println(test);
    }
}