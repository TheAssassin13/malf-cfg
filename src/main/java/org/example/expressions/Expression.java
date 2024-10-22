package org.example.expressions;

import org.example.automatons.ContextFreeGrammar;

public interface Expression {
    abstract ContextFreeGrammar toContextFreeGrammar();
}
