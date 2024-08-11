package com.craftinginterpreters.lox;

import java.util.List;

class LoxFunction implements LoxCallable {
  private final Object declaration;
  private final Environment closure;

  LoxFunction(Stmt.Function declaration, Environment closure) {
    this.closure = closure;
    this.declaration = declaration;
  }

  LoxFunction(Expr.Lambda declaration, Environment closure) {
    this.closure = closure;
    this.declaration = declaration;
  }

  @Override
  public Object call(Interpreter interpreter, List<Object> arguments) {
    Environment env = new Environment(this.closure);

    List<Token> params = params();
    List<Stmt> body = body();

    for (int i = 0; i < params.size(); i++) {
      env.define(params.get(i).lexeme, arguments.get(i)); 
    }

    try {
      interpreter.executeBlock(body, env);
    } catch (Return returnValue) {
      return returnValue.value; 
    }

    return null;
  }

  @Override
  public int arity() {
    return params().size();
  }

  @Override
  public String toString() {
    String name;

    if (this.declaration instanceof Stmt.Function) {
      name = ((Stmt.Function) this.declaration).name.lexeme;
    } else if (this.declaration instanceof Expr.Lambda) {
      name = "(anonymous)";
    } else {
      throw new RuntimeError(null, "Internal error -- unknown type of function declaration (" + this.declaration + ")");
    }

    return "<fn " + name + ">";
  }

  private List<Token> params() {
    if (this.declaration instanceof Stmt.Function) {
      return ((Stmt.Function)this.declaration).params;
    } else if (this.declaration instanceof Expr.Lambda) {
      return ((Expr.Lambda)this.declaration).params;
    }

    throw new RuntimeError(null, "Internal error -- unknown type of function declaration (" + this.declaration + ")");
  }

  private List<Stmt> body() {
     if (this.declaration instanceof Stmt.Function) {
      return ((Stmt.Function)this.declaration).body;
    } else if (this.declaration instanceof Expr.Lambda) {
      return ((Expr.Lambda)this.declaration).body;
    }

    throw new RuntimeError(null, "Internal error -- unknown type of function declaration (" + this.declaration + ")");
  }
}
