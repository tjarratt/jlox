package com.craftinginterpreters.lox;

import java.util.List;

class LoxFunction implements LoxCallable {
  private final Stmt.Function declaration;
  private final Environment closure;
  private final boolean isInitializer;

  LoxFunction(Stmt.Function declaration, Environment closure, boolean isInitializer) {
    this.closure = closure;
    this.declaration = declaration;
    this.isInitializer = isInitializer;
  }

  LoxFunction bind(LoxInstance instance) {
    Environment environment = new Environment(this.closure);
    environment.define("this", instance);
    return new LoxFunction(this.declaration, environment, this.isInitializer);
  }

  @Override
  public Object call(Interpreter interpreter, List<Object> arguments) {
    Environment env = new Environment(this.closure);

    for (int i = 0; i < this.declaration.params.size(); i++) {
      env.define(declaration.params.get(i).lexeme, arguments.get(i)); 
    }

    try {
      interpreter.executeBlock(this.declaration.body, env);
    } catch (Return returnValue) {
      if (this.isInitializer) { return this.closure.getAt(0, "this"); }

      return returnValue.value; 
    }

    if (this.isInitializer) { return this.closure.getAt(0, "this"); }
    return null;
  }

  @Override
  public int arity() {
    return this.declaration.params.size();
  }

  @Override
  public String toString() {
    return "<fn " + declaration.name.lexeme + ">";
  }
}
