package org.example.expressions;


import org.example.automatons.ContextFreeGrammar;

import java.util.LinkedList;

public class Char implements Expression {
  char character;

  Char(char character) {
    this.character = character;
  }

  @Override
  public ContextFreeGrammar toContextFreeGrammar() {
    var grammar = new ContextFreeGrammar();

    grammar.addNonTerminalState("<S0>");
    grammar.addTerminalState(String.valueOf(character));
    grammar.addTransition("<S0>", String.valueOf(character));
    grammar.setInitialState("<S0>");

    return grammar;
  }
}
