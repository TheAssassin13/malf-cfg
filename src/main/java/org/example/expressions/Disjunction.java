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
    var left_grammar = left.toContextFreeGrammar();
    var right_grammar = right.toContextFreeGrammar();

    var grammar = ContextFreeGrammar.join(left_grammar, right_grammar);

    grammar.addNonTerminalState("<S0>");
    grammar.setInitialState("<S0>");
    grammar.addTransition("<S0>", left_grammar.getInitialState());
    grammar.addTransition("<S0>", right_grammar.getInitialState());

    return grammar;
  }
}
