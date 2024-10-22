package org.example;

import org.example.expressions.ExpressionFactory;
import org.example.expressions.InvalidExpression;

public class Main {
    public static void main(String[] args) throws InvalidExpression {
        var exp = ExpressionFactory.parseExpression(args[0]);

        var grammar = exp.toContextFreeGrammar();
        System.out.println("GLC 1 M:");
        System.out.println(grammar);

    }
}