package done.misc;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Optional;

import static done.misc.Playground.TokenType.CLOSE_BRACE;
import static done.misc.Playground.TokenType.EPSILON;
import static done.misc.Playground.TokenType.MINUS;
import static done.misc.Playground.TokenType.NUMBER;
import static done.misc.Playground.TokenType.OPEN_BRACE;
import static done.misc.Playground.TokenType.PLUS;

public class Playground {
    public static void main(String[] args) {
        parseExpression("5 +6 -   ( 7+ 4) ").print();
        parseExpression("10 + ((51+9)-(-17-3)) + 1").print();
    }

    private static Expression parseExpression(String expression) {
        Tokenizer tokenizer = new Tokenizer(expression);
        Parser parser = new Parser(tokenizer.tokens());
        return parser.expression();
    }

    static class Tokenizer {
        private final StringBuilder buffer = new StringBuilder();
        private final Deque<Token> tokens = new ArrayDeque<>();
        private final char[] characters;
        private int pos = 0;

        Tokenizer(String expression) {
            this.characters = expression.toCharArray();
        }

        Deque<Token> tokens() {
            while (hasNext()) {
                nextToken().ifPresent(tokens::add);
            }
            return tokens;
        }

        private Optional<Token> nextToken() {
            return switch (peek()) {
                case '+' -> Optional.of(symbol(PLUS));
                case '-' -> Optional.of(symbol(MINUS));
                case '(' -> Optional.of(symbol(OPEN_BRACE));
                case ')' -> Optional.of(symbol(CLOSE_BRACE));

                case ' ', '\t', '\r', '\n' -> whitespace();
                default -> Optional.of(number());
            };
        }

        private Optional<Token> whitespace() {
            pop();
            return Optional.empty();
        }

        private Token symbol(TokenType type) {
            pop();
            return Token.of(type);
        }

        private Token number() {
            buffer.setLength(0);

            while (hasNext() && Character.isDigit(peek())) {
                buffer.append(pop());
            }

            if (buffer.isEmpty()) {
                throw new IllegalStateException("Expected digits but got: " + peek());
            }

            return new Token(NUMBER, buffer.toString());
        }

        private char peek() {
            return characters[pos];
        }

        private char pop() {
            return characters[pos++];
        }

        private boolean hasNext() {
            return pos < characters.length;
        }
    }

    static class Parser {
        private final Deque<Token> tokens;

        public Parser(Deque<Token> tokens) {
            this.tokens = tokens;
        }

        /**
         * expression ::= addition
         */
        public Expression expression() {
            return addition();
        }

        /**
         * addition ::= subtraction (PLUS subtraction)*
         */
        private Expression addition() {
            Expression expr = subtraction();

            while (peek() == PLUS) {
                consume(PLUS);
                expr = new PlusExpr(expr, subtraction());
            }

            return expr;
        }

        /**
         * subtraction ::= atomic (MINUS atomic)*
         */
        private Expression subtraction() {
            Expression expr = atomic();

            while (peek() == MINUS) {
                consume(MINUS);
                expr = new MinusExpr(expr, atomic());
            }

            return expr;
        }

        /**
         * atomic ::= braces | number
         */
        private Expression atomic() {
            if (peek() == OPEN_BRACE) {
                return braces();
            }
            return number();
        }

        /**
         * braces ::= OPEN_BRACE expression CLOSE_BRACE
         */
        private Expression braces() {
            consume(OPEN_BRACE);
            Expression expr = expression();
            consume(CLOSE_BRACE);
            return new BracesExpr(expr);
        }

        /**
         * number ::= (MINUS)? NUMBER
         */
        private Expression number() {
            if (peek() == MINUS) {
                return negativeNumber();
            }
            return positiveNumber();
        }

        private Expression negativeNumber() {
            consume(MINUS);
            return new NegateExpr(positiveNumber());
        }

        private Expression positiveNumber() {
            expect(NUMBER);
            Token token = token();
            return ConstantExpr.create(token.content());
        }

        private void consume(TokenType expected) {
            expect(expected);
            token();
        }

        private void expect(TokenType expected) {
            if (peek() != expected) {
                fail(expected, peek());
            }
        }

        private void fail(TokenType expected, TokenType actual) {
            throw new IllegalStateException("Expected " + expected.description() + " but got: " + actual);
        }

        private TokenType peek() {
            Token token = tokens.peek();
            return token != null ? token.type() : EPSILON;
        }

        private Token token() {
            return tokens.pop();
        }
    }

    static class BracesExpr extends UnaryExpr {
        BracesExpr(Expression expr) {
            super(expr);
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

    static class NegateExpr extends UnaryExpr {
        NegateExpr(Expression expr) {
            super(expr);
        }

        @Override
        public int evaluate() {
            return -expr.evaluate();
        }

        @Override
        public String toString() {
            return MINUS.code() + expr.toString();
        }
    }

    abstract static class UnaryExpr implements Expression {
        protected final Expression expr;

        UnaryExpr(Expression expr) {
            this.expr = expr;
        }
    }

    static class PlusExpr extends BinaryExpr {
        PlusExpr(Expression expr1, Expression expr2) {
            super(expr1, expr2);
        }

        @Override
        protected String code() {
            return PLUS.code();
        }

        @Override
        public int evaluate() {
            return expr1.evaluate() + expr2.evaluate();
        }
    }

    static class MinusExpr extends BinaryExpr {
        MinusExpr(Expression expr1, Expression expr2) {
            super(expr1, expr2);
        }

        @Override
        protected String code() {
            return MINUS.code();
        }

        @Override
        public int evaluate() {
            return expr1.evaluate() - expr2.evaluate();
        }
    }

    abstract static class BinaryExpr implements Expression {
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

        default void print() {
            System.out.println("'" + this + "' -> " + evaluate());
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

        static Token of(TokenType type) {
            return new Token(type, type.code());
        }
    }

    enum TokenType {
        PLUS("+", "plus"),
        MINUS("-", "minus"),

        OPEN_BRACE("(", "open brace"),
        CLOSE_BRACE(")", "close brace"),

        NUMBER("number"),
        EPSILON("epsilon"),
        ;

        private final String code;
        private final String description;

        TokenType(String desc) {
            this(null, desc);
        }

        TokenType(String code, String desc) {
            this.code = code;
            this.description = desc;
        }

        public String code() {
            return code;
        }

        public String description() {
            return description;
        }


        @Override
        public String toString() {
            return name() + ": " + code();
        }
    }
}
