package oop;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toMap;
import static oop.Playground.TokenType.CLOSE_BRACE;
import static oop.Playground.TokenType.EPSILON;
import static oop.Playground.TokenType.NUMBER;
import static oop.Playground.TokenType.OPEN_BRACE;

public class Playground {
    public static void main(String[] args) {
        String expression = "5 +6 -   ( 7+ 4) ";
        Deque<Token> tokens = expression.chars()
                                        .mapToObj(c -> (char) c)
                                        .map(Playground::parseToken)
                                        .filter(Objects::nonNull)
                                        .collect(toCollection(ArrayDeque::new));

        Parser parser = new Parser(tokens);
        Expression expr = parser.expression();

        System.out.println("Expr '" + expr + "' value: " + expr.evaluate());
    }

    private static Token parseToken(Character c) {
        if (Character.isWhitespace(c)) {
            return null;
        }
        return Character.isDigit(c)
               ? new Token(NUMBER, c.toString())
               : new Token(TokenType.fromCode(c.toString()), c.toString());
    }

    static class BracesExpr implements Expression {
        private final Expression expr;

        BracesExpr(Expression expr) {
            this.expr = expr;
        }

        @Override
        public int evaluate() {
            return expr.evaluate();
        }

        @Override
        public String toString() {
            return OPEN_BRACE.code() + " " + expr.toString() + " " + CLOSE_BRACE.code();
        }
    }

    static class PlusExpression extends BinaryExpr {
        PlusExpression(Expression expr1, Expression expr2) {
            super(expr1, expr2);
        }

        @Override
        protected String code() {
            return "+";
        }

        @Override
        public int evaluate() {
            return expr1.evaluate() + expr2.evaluate();
        }
    }

    static class MinusExpression extends BinaryExpr {
        MinusExpression(Expression expr1, Expression expr2) {
            super(expr1, expr2);
        }

        @Override
        protected String code() {
            return "-";
        }

        @Override
        public int evaluate() {
            return expr1.evaluate() - expr2.evaluate();
        }
    }

    static abstract class BinaryExpr implements Expression {
        protected final Expression expr1;
        protected final Expression expr2;

        BinaryExpr(Expression expr1, Expression expr2) {
            this.expr1 = expr1;
            this.expr2 = expr2;
        }

        @Override
        public String toString() {
            return expr1.toString() + " " + code() + " " + expr2.toString();
        }

        protected abstract String code();
    }

    static class ConstantExpr implements Expression {
        private final int value;

        ConstantExpr(int n) {
            this.value = n;
        }

        @Override
        public int evaluate() {
            return value;
        }

        @Override
        public String toString() {
            return Integer.toString(value);
        }

        static ConstantExpr create(String str) {
            return new ConstantExpr(Integer.parseInt(str.trim()));
        }
    }

    interface Expression {
        int evaluate();
    }

    static class Parser {
        private final Deque<Token> tokens;

        public Parser(Deque<Token> tokens) {
            this.tokens = tokens;
        }

        public Expression expression() {
            if (peek() == OPEN_BRACE) {
                return braces();
            }

            return simpleExpression();
        }

        public Expression simpleExpression() {
            Expression expr1 = number();

            if (peek() == EPSILON || peek() == CLOSE_BRACE) {
                return expr1;
            }

            TokenType op = token().type();
            Expression expr2 = expression();

            return switch (op) {
                case PLUS -> new PlusExpression(expr1, expr2);
                case MINUS -> new MinusExpression(expr1, expr2);
                default -> fail("operation");
            };
        }

        public Expression braces() {
            openBrace();
            Expression expr = expression();
            closeBrace();
            return new BracesExpr(expr);
        }

        private void openBrace() {
            if (peek() != OPEN_BRACE) {
                fail("open brace");
            }
            token();
        }

        private void closeBrace() {
            if (peek() != CLOSE_BRACE) {
                fail("close brace");
            }
            token();
        }

        public Expression number() {
            if (peek() != NUMBER) {
                return fail("number");
            }
            Token token = token();
            return ConstantExpr.create(token.content());
        }

        private Expression fail(String what) {
            throw new IllegalStateException("Expected " + what + " but got: " + peek());
        }

        private TokenType peek() {
            Token token = tokens.peek();
            return token != null ? token.type : EPSILON;
        }

        private Token token() {
            return tokens.pop();
        }
    }

    static class Token {
        private final TokenType type;
        private final String content;

        Token(TokenType type, String content) {
            this.type = type;
            this.content = content;
        }

        public TokenType type() {
            return type;
        }

        public String content() {
            return content;
        }

        @Override
        public String toString() {
            return type + ": " + content;
        }
    }

    enum TokenType {
        PLUS("+"),
        MINUS("-"),

        OPEN_BRACE("("),
        CLOSE_BRACE(")"),

        NUMBER,
        EPSILON,
        ;

        private final String code;

        TokenType() {
            this(null);
        }

        TokenType(String code) {
            this.code = code;
        }

        public String code() {
            return code;
        }

        private static final Map<String, TokenType> codeToOperation =
                Stream.of(values())
                      .filter(val -> val != NUMBER)
                      .filter(val -> val != EPSILON)
                      .collect(toMap(TokenType::code, identity()));

        static TokenType fromCode(String code) {
            TokenType operation = codeToOperation.get(code);
            if (operation == null) {
                throw new IllegalStateException("Unknown operation code: " + code);
            }
            return operation;
        }
    }
}
