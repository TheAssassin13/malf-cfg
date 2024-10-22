package org.example.expressions;

import org.example.automatons.ContextFreeGrammar;

public class Kleene implements Expression {
  protected Expression expression;

  public Kleene(Expression expression) {
    this.expression = expression;
  }

  @Override
  public ContextFreeGrammar toContextFreeGrammar() {
    return null;
  }
}
