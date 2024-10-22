package org.example.expressions;


import org.example.automatons.ContextFreeGrammar;

public class Parenthesis implements Expression {
  protected Expression expr;

  public Parenthesis(Expression expression) {
    expr = expression;
  }

  @Override
  public ContextFreeGrammar toContextFreeGrammar() {
    return expr.toContextFreeGrammar();
  }
}
