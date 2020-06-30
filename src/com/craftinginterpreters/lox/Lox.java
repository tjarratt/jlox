package com.craftinginterpreters.lox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static java.lang.System.exit;

public class Lox {
    private static boolean hadError = false;

    public static void main(String[] args) throws IOException {
        if (args.length > 1) {
            printUsage();
            exit(64);
        } else if (args.length == 1) {
            runFile(args[0]);
        } else {
            runPrompt();
        }
    }

    private static void runFile(String filePath) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(filePath));
        run(new String(bytes, Charset.defaultCharset()));

        if (hadError) exit(65);
    }

    private static void run(String sourceCode) {
        Scanner scanner = new Scanner(sourceCode);
        List<Token> tokens = scanner.scanTokens();

        Parser parser = new Parser(tokens);
        Expr expression = parser.parse();

        // return early if there was a syntax error
        if (hadError) return;

        System.out.println(new AstPrinter().print(expression));
    }

    private static void runPrompt() throws IOException {
        InputStreamReader inputStream = new InputStreamReader(System.in);
        BufferedReader bufferedInput = new BufferedReader(inputStream);

        while (true) {
            System.out.println("> ");

            run(bufferedInput.readLine());
            hadError = false;
        }
    }

    private static void printUsage() {
        System.out.println("Usage: jlox path/to/your/script/to/run");
    }

    static void error(int line, String message) {
        report(line, "", message);
    }

    static void error(Token token, String message) {
        if (token.type == TokenType.EOF) {
            report(token.line, " at end", message);
        } else {
            report(token.line, " at '" + token.lexeme + "'", message);
        }
    }

    private static void report(int line, String where, String message) {
        System.err.println(
                "[line " + line + "] Error " + where + ": " + message
        );

        hadError = true;
    }
}
