package org.example.expressions;

import org.example.automatons.ContextFreeGrammar;

public class Concatenation implements Expression {
  protected Expression left;
  protected Expression right;

  public Concatenation(Expression left, Expression right) {
    this.left = left;
    this.right = right;
  }

  @Override
  public ContextFreeGrammar toContextFreeGrammar() {
    return null;
  }
}
