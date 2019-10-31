package com.craftinginterpreters.lox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.craftinginterpreters.lox.TokenType.*;

public class Scanner {
    private final String sourceCode;
    private int start = 0;  // offset in file to current lexeme being scanned
    private int current = 0;// offset in file to current character under consideration
    private int line = 1;   // current line number being scanned

    private final List<Token> tokens = new ArrayList<>();
    private static final Map<String, TokenType> keywords;

    static {
        keywords = new HashMap<>();
        keywords.put("and",    AND);
        keywords.put("class",  CLASS);
        keywords.put("else",   ELSE);
        keywords.put("false",  FALSE);
        keywords.put("for",    FOR);
        keywords.put("fun",    FUN);
        keywords.put("if",     IF);
        keywords.put("nil",    NIL);
        keywords.put("or",     OR);
        keywords.put("print",  PRINT);
        keywords.put("return", RETURN);
        keywords.put("super",  SUPER);
        keywords.put("this",   THIS);
        keywords.put("true",   TRUE);
        keywords.put("var",    VAR);
        keywords.put("while",  WHILE);
    }

    public Scanner(String sourceCode) {
        this.sourceCode = sourceCode;
    }

    public List<Token> scanTokens() {
        while (!isAtEnd()) {
            start = current;
            scanToken();
        }

        tokens.add(new Token(EOF, "", null, line));
        return tokens;
    }

    private void scanToken() {
        char c = advance();
        switch (c) {
            case '(': addToken(LEFT_PAREN); break;
            case ')': addToken(RIGHT_PAREN); break;
            case '{': addToken(LEFT_BRACE); break;
            case '}': addToken(RIGHT_BRACE); break;
            case ',': addToken(COMMA); break;
            case '.': addToken(DOT); break;
            case '-': addToken(MINUS); break;
            case '+': addToken(PLUS); break;
            case ';': addToken(SEMICOLON); break;
            case '*': addToken(STAR); break;
            case '!': addToken(match('=') ? BANG_EQUAL : BANG); break;
            case '=': addToken(match('=') ? EQUAL_EQUAL : EQUAL); break;
            case '<': addToken(match('=') ? LESS_EQUAL : LESS); break;
            case '>': addToken(match('=') ? GREATER_EQUAL : GREATER); break;
            case '/': addSlashOrConsumeComments(); break;
            case ' ':
            case '\r':
            case '\t': break;
            case '\n': line++; break;
            case '"': string(); break;
            case '0': number(); break;
            case '1': number(); break;
            case '2': number(); break;
            case '3': number(); break;
            case '4': number(); break;
            case '5': number(); break;
            case '6': number(); break;
            case '7': number(); break;
            case '8': number(); break;
            case '9': number(); break;
            default:
                if (isAlpha(c)) identifier();
                else Lox.error(line, "Unexpected Error");
                break;
        }
    }

    private void addToken(TokenType tokenType) {
        addToken(tokenType, null);
    }

    private void addToken(TokenType tokenType, Object literal) {
        String text = sourceCode.substring(start, current);
        tokens.add(new Token(tokenType, text, literal, line));
    }

    private char peek() {
        if (isAtEnd()) return '\0';

        return sourceCode.charAt(current);
    }

    private char peekNext() {
        if (current + 1 >= sourceCode.length()) return '\0';

        return sourceCode.charAt(current + 1);
    }

    private boolean match(char expected) {
        if (isAtEnd()) return false;
        if (sourceCode.charAt(current) != expected) return false;

        current++;
        return true;
    }

    private char advance() {
        current += 1;
        return sourceCode.charAt(current - 1);
    }

    private boolean isAtEnd() {
        return current >= sourceCode.length();
    }

    private void number() {
        while (isDigit(peek())) advance();

        // check if we have a decimal point
        if (peek() == '.' && isDigit(peekNext())) {
            advance(); // consumes the '.'
            while (isDigit(peek())) advance();
        }

        addToken(
                NUMBER,
                Double.parseDouble(sourceCode.substring(start, current))
        );
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private void string() {
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n') line += 1;
            advance();
        }

        // handle unterminated string
        if (isAtEnd()) {
            Lox.error(line, "Unterminated string.");
            return;
        }

        advance(); // the closing quote
        String value = sourceCode.substring(start + 1, current - 1);
        addToken(STRING, value);
    }

    private void addSlashOrConsumeComments() {
        if (match('/') == false) {
            addToken(SLASH);
            return;
        }
        if (match('*')) {
            consumeMultiLineComments();
        }

        while (peek() != '\n' && !isAtEnd()) advance();
    }

    private void consumeMultiLineComments() {
        while (true) {
            if (isAtEnd()) break;

            boolean hasStar = match('*');
            advance();

            if (hasStar && match('/')) break;
        }
    }

    private void identifier() {
        while (isAlphaNumeric(peek())) advance();

        // check if we consumed a keyword
        String text = sourceCode.substring(start, current);
        TokenType type = keywords.get(text);
        if (type == null) type = IDENTIFIER;

        addToken(type);
    }

    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }

    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') ||
                (c >= 'A' && c <= 'Z') ||
                c == '_';
    }
}
