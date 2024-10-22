package org.example.expressions;


import org.example.automatons.ContextFreeGrammar;

public class Empty implements Expression {

    @Override
    public ContextFreeGrammar toContextFreeGrammar() {
        var grammar = new ContextFreeGrammar();

        grammar.addNonTerminalState("<S0>");
        grammar.setInitialState("<S0>");

        return grammar;
    }
}
