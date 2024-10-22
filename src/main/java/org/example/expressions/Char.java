package org.example.expressions;


import org.example.automatons.ContextFreeGrammar;

public class Char implements Expression {
  char character;

  Char(char character) {
    this.character = character;
  }

  @Override
  public ContextFreeGrammar toContextFreeGrammar() {
    return null;
  }
}
