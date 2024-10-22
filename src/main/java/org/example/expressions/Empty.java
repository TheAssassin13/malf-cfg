package org.example.expressions;


import org.example.automatons.ContextFreeGrammar;

public class Empty implements Expression {

    @Override
    public ContextFreeGrammar toContextFreeGrammar() {
        return null;
    }
}
