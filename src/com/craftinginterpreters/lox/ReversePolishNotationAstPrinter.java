package com.craftinginterpreters.lox;

public class ReversePolishNotationAstPrinter implements Expr.Visitor<String> {

    String print(Expr expr) {
        return expr.accept(this);
    }

    @Override
    public String visitAssignExpr(Expr.Assign expr) {
      return expr.name + " " + expr.value.accept(this) + " =";
    }

    @Override
    public String visitVariableExpr(Expr.Variable expr) {
      return expr.name.lexeme;
    }

    @Override
    public String visitBinaryExpr(Expr.Binary expr) {
        // ( 1 + 2)
        // becomes
        // 1 2 +
        return expr.left.accept(this) + " " + expr.right.accept(this) + " " + expr.operator.lexeme;
    }

    @Override
    public String visitGroupingExpr(Expr.Grouping expr) {
        return expr.expression.accept(this);
    }

    @Override
    public String visitLiteralExpr(Expr.Literal expr) {
        if (expr.value == null) { return "nil"; }

        return expr.value.toString();
    }

    @Override
    public String visitLogicalExpr(Expr.Logical expr) {
      return expr.left.toString() + " " + expr.right.toString() + " " + expr.operator.lexeme;
    }


    @Override
    public String visitUnaryExpr(Expr.Unary expr) {
        return expr.right.accept(this) + " " + expr.operator.lexeme;
    }

    @Override
    public String visitCallExpr(Expr.Call expr) {
        Expr[] arguments = expr.arguments.toArray(new Expr[expr.arguments.size()]);

        return parenthesize("", arguments) + " " + expr.callee;
    }

    @Override
    public String visitLambdaExpr(Expr.Lambda expr) {
      Token[] params = expr.params.toArray(new Token[expr.params.size()]);
      String result = "fun (" + parenthesize("", params) + " { ... }";

      return result;
    }

    // pragma mark - private

    private String parenthesize(String name, Expr... exprs) {
        StringBuilder builder = new StringBuilder();

        builder.append("(").append(name);
        for (Expr expr : exprs) {
            builder.append(" ");
            builder.append(expr.accept(this));
        }
        builder.append(")");

        return builder.toString();
    }

    private String parenthesize(String name, Token ... tokens) {
        StringBuilder builder = new StringBuilder();

        builder.append("(").append(name);
        for (Token token : tokens) {
            builder.append(" ");
            builder.append(token.lexeme);
        }
        builder.append(")");

        return builder.toString();
    }

    // present only for debugging purposes
    public static void main(String[] args) {
      Expr expression = new Expr.Binary(
        new Expr.Grouping(new Expr.Binary(
            new Expr.Literal(1),
            new Token(TokenType.PLUS, "+", null, 1),
            new Expr.Literal(2)
        )),

        new Token(TokenType.STAR, "*", null, 1),

        new Expr.Grouping(new Expr.Binary(
            new Expr.Literal(4),
            new Token(TokenType.MINUS, "-", null, 1),
            new Expr.Literal(3)
        ))
      );

      System.out.println(new ReversePolishNotationAstPrinter().print(expression));
    }
}
