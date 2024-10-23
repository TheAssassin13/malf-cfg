package org.example;

import org.example.automatons.PushdownAutomaton;
import org.example.expressions.ExpressionFactory;
import org.example.expressions.InvalidExpression;

public class Main {
    public static void main(String[] args) throws InvalidExpression {
        var exp = ExpressionFactory.parseExpression(args[0]);

        var grammar = exp.toContextFreeGrammar();
        PushdownAutomaton pa = new PushdownAutomaton(grammar);

        System.out.println("GLC 1 M:");
        System.out.println(grammar);

        System.out.println("AP M:");
        System.out.println(pa);
    }
}