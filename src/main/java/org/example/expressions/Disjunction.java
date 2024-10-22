package org.example.expressions;

import org.example.automatons.ContextFreeGrammar;

public class Disjunction implements Expression {
  protected Expression left;
  protected Expression right;

  public Disjunction(Expression left, Expression right) {
    this.left = left;
    this.right = right;
  }

  @Override
  public ContextFreeGrammar toContextFreeGrammar() {
    return null;
  }
}
