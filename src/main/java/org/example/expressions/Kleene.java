package org.example.expressions;

import org.example.automatons.ContextFreeGrammar;

public class Kleene implements Expression {
  protected Expression expression;

  public Kleene(Expression expression) {
    this.expression = expression;
  }

  @Override
  public ContextFreeGrammar toContextFreeGrammar() {
    var grammar = expression.toContextFreeGrammar();

    grammar.increaseStateNumbers(1);

    grammar.addTerminalState("_");
    grammar.addNonTerminalState("<S0>");

    grammar.addTransition("<S0>", grammar.getInitialState() + "<S0>");
    grammar.addTransition("<S0>", "_");

    grammar.setInitialState("<S0>");

    return grammar;
  }
}
