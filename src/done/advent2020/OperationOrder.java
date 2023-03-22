package done.advent2020;

import org.junit.jupiter.api.Test;
import util.LineSupplier;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class OperationOrder {

    public static void main(String[] args) {
        try (Scanner in = new Scanner(INPUT2)) {
            long result = Stream.generate(new LineSupplier(in))
                    .takeWhile(Objects::nonNull)
                    .map(Parser::fromLine)
                    .map(Parser::parse)
                    .peek(System.out::print)
                    .mapToLong(Expr::eval)
                    .peek(num -> System.out.println(" = " + num))
                    .sum();

            System.out.println("Result: " + result);
        }
    }

    static class Parser {
        final Deque<String> tokens = new ArrayDeque<>();

        Parser(List<String> tokens) {
            this.tokens.addAll(tokens);
        }

        Expr parse() {
            return term();
        }

        private Expr term() {
            return multiplications();
        }

        private Expr multiplications() {
            Expr term = addition();
            while ("*".equals(peekToken())) {
                term = mult(term);
            }
            return term;
        }

        private Expr addition() {
            Expr term = atomic();
            while ("+".equals(peekToken())) {
                term = add(term);
            }
            return term;
        }

        private Expr add(Expr left) {
            token(); // consume "+"
            return new Add(left, atomic());
        }

        private Expr mult(Expr left) {
            token(); // consume "*"
            return new Mult(left, addition());
        }

        private Expr atomic() {
            return "(".equals(peekToken())
                    ? brackets()
                    : number();
        }

        private Brackets brackets() {
            token(); // consume "("
            Brackets brackets = new Brackets(term());
            token(); // consume ")"
            return brackets;
        }

        private Const number() {
            return Const.of(token());
        }

        private boolean hasNextToken() {
            return tokens.peek() != null;
        }

        private String peekToken() {
            final String token = tokens.peek();
            return token != null ? token.trim() : "";
        }

        private String token() {
            final String token = tokens.pop();
            return token != null ? token.trim() : "";
        }

        static Parser fromLine(String line) {
            List<String> tokens = tokenize(line);
            return new Parser(tokens);
        }

        static List<String> tokenize(String line) {
            List<String> tokens = new ArrayList<>();
            StringBuilder buf = new StringBuilder();
            for (int i = 0; i < line.length(); i++) {
                char ch = line.charAt(i);
                if (!Character.isDigit(ch) && !buf.isEmpty()) {
                    tokens.add(buf.toString());
                    buf.setLength(0);
                }
                if (Character.isWhitespace(ch)) {
                    continue;
                }
                switch (ch) {
                    case '(' -> tokens.add("(");
                    case ')' -> tokens.add(")");
                    case '*' -> tokens.add("*");
                    case '+' -> tokens.add("+");
                    default -> buf.append(ch);
                }
            }
            if (!buf.isEmpty()) {
                tokens.add(buf.toString());
                buf.setLength(0);
            }
            return tokens;
        }
    }

    static abstract sealed class Expr {
        abstract long eval();
    }

    static final class Const extends Expr {
        final long num;

        Const(long num) {
            this.num = num;
        }

        public static Const of(String token) {
            return new Const(Long.parseLong(token));
        }

        @Override
        long eval() {
            return num;
        }

        @Override
        public String toString() {
            return Long.toString(num);
        }
    }

    static final class Add extends Expr {
        private final Expr left;
        private final Expr right;

        Add(Expr left, Expr right) {
            this.left = left;
            this.right = right;
        }

        @Override
        long eval() {
            return left.eval() + right.eval();
        }

        @Override
        public String toString() {
            return left + " + " + right;
        }
    }

    static final class Mult extends Expr {
        private final Expr left;
        private final Expr right;

        Mult(Expr left, Expr right) {
            this.left = left;
            this.right = right;
        }

        @Override
        long eval() {
            return left.eval() * right.eval();
        }

        @Override
        public String toString() {
            return left + " * " + right;
        }
    }

    static final class Brackets extends Expr {
        private final Expr nested;

        Brackets(Expr nested) {
            this.nested = nested;
        }

        @Override
        long eval() {
            return nested.eval();
        }

        @Override
        public String toString() {
            return "( " + nested + " )";
        }
    }

    private static final String INPUT = """
            1 + (2 * 3) + (4 * (5 + 6))
            2 * 3 + (4 * 5)
            5 + (8 * 3 + 9 + 3 * 4 * 3)
            5 * 9 * (7 * 3 * 3 + 9 * 3 + (8 + 6 * 4))
            ((2 + 4 * 9) * (6 + 9 * 8 + 6) + 6) + 2 + 4 * 2
            """;

    private static final String INPUT2 = """
            (9 * 3 + 4) * (7 + (2 + 3 + 6 * 2) + 3) * 4 * 5
            (8 + 4 + 9 + (7 * 9 + 4 + 6 + 9) * 3) + 6 + 5 + ((8 * 2) + 2 * 9) * 5
            5 * 6 * 8 * (9 + 7 * 8 * 9) + 6 + 2
            9 * 9 + (4 * (6 * 7 * 7 + 4 * 6) * 5 + 6 * (2 * 5)) * (8 + 7 + 6 + 2 * 7) + 5
            5 + (7 + 3 * 7 + 9 * (7 + 7 + 4 + 3 * 3)) + 8 * 4
            (6 + 2 + 5 + 4) * 2 * 9 * 2 * 8
            7 + 8 + ((4 + 6 + 6) * 6 + 9 + 3 * 8 + 9) + 5
            3 + ((6 * 6 * 7 * 6) * 6 + 6 * 4) + (2 + 5 * 6 + 6 + (2 * 4) + 6) * 9
            (2 * (4 * 9 * 3) * 8 + 6 + (7 + 2 + 6)) * 6 * (5 * 4 + 8 + 7 + (7 + 2 * 3))
            8 * ((7 + 4 + 9) * 5 + 6 + (4 + 9 + 8 * 3 * 8 + 3) * 5 * (5 + 8 + 7 + 3 * 7 * 4))
            6 * 5 * 5 + (8 * 5 * 8 * 5 + 7) + ((2 + 2 * 6 * 3) * 4) * 6
            2 + 2 * 9 + 8 * (3 * 7 * 7 * (2 + 5) * 3 + 2)
            6 + 2
            9 * 2 + ((6 * 7 * 7 * 2) + 8) + (7 * (4 * 6 + 7) * 2 * 2 * 9) + 4 * 8
            (7 * 9 + 9 * 2 * 4 * 9) + 4 * 2 * 9 + 6
            6 * 3 + 7 + 9 + 9 + ((6 + 6 + 7 * 5) * 6 * 8 * 5)
            ((8 * 8 * 6 + 6 * 4) + 3 + 3 + (3 * 5 + 3 * 5 + 4 + 7)) * 8 + (6 + 7 * 2 + 2) + (9 + 9)
            7 + 8 + 3 * 5 + (5 * 5 + 6 + 6) * (9 * 6)
            5 + 5 + 2
            ((3 + 3) * 2 + (5 + 8 * 2 + 4 * 3)) * 8
            ((5 + 6 + 5 * 8 + 4) + 9 + (5 + 5)) * (7 * 2 + (8 + 7)) + 9
            9 * (9 * 8) + (3 * 2 * (9 + 2 * 9 * 5) * 4 * 2) + 5
            4 * 4 + (2 + 5 + 5 * (3 + 6 * 4 + 7)) * (2 * 2) + 6
            6 + 2
            6 + 5 + (4 + 8) + 5
            6 * (4 * 6) + (4 * 5 + 4 + 5 + 9) + 7 * (2 + 7 + 9)
            9 * (3 * (5 + 9 * 4 * 5 + 3 * 9)) + ((4 * 4 * 3 * 6) * (9 * 8 * 3) * 9 * 9) + 3
            5 + (6 * (3 + 9 + 7 * 6 + 2) * (2 + 4 * 2 * 4 * 6) * 9 + 3) + 6 + 8 + 5 * 3
            8 + 9 * (8 + (3 + 2 + 4) * 4 + 3 * 4 * 5)
            9 + 5 + 8 + 9 * (8 + (4 * 2 * 9 + 8 + 7) * (8 + 6 * 3) * 7 * 7)
            6 * ((9 + 2) * (7 + 8 + 5 + 9 * 7) * 4) * 4 * ((7 + 7 + 4 * 5 * 2) + 2) + 9
            (4 + 9 * 8) * (6 + (7 * 6) * 9 + 8 * 2 * 7) + 4 + 9 + 3 * (6 + 8 + (8 * 4 + 7 * 3) * 8)
            (5 * 9 * 2 * 7 * 8 + 6) + (5 + 5)
            6 + 7 * 9 + 3
            ((7 + 8 * 3 * 7 + 5) * 7 * 5 + 6 * 6 + 5) + 9 + 4
            (5 * 2 * 4 * 9 * 2 + (8 + 6 * 7 + 6)) + ((4 * 4 * 6 * 9 + 7) + 9 * 7)
            (3 + (5 * 8 + 4 + 4 * 7)) * 7 + 8 + (7 + (3 * 5 * 4 + 6 + 9)) + ((7 + 2 + 7 * 9 + 6 * 8) + 3 + 3 + (2 * 9 * 7) + 4 + (6 + 3 * 5)) * 9
            5 * 4 + ((6 + 8 * 5 * 6) * 8) + 7 + 7
            5 * (4 * (2 * 9 * 7 * 7) * 5) + ((6 * 2 * 9 + 2) * 3 * 2 + 7 + 3) + 9 + 8
            (7 + 3 * 3 * 6 * 2 * 3) * 6 + 3 * ((3 * 4 * 3 * 9 * 7) + 9 + 8) * 9
            ((4 + 8 * 9) + 5 * 4 * 2 * 8 * 4) * (6 + 2 * (9 + 5) * 9 * 7 + 8)
            9 + 8 + (3 * 3 * (9 + 2 + 8 * 7 * 9))
            (5 * 8 * (7 * 2 + 8)) + 2 * 8 + 8 + 6 * 5
            2 + (2 * 9 * 5 + 4) * (5 + 9 * 3 * 4) * 4 + 2
            8 * 2 * (7 * 7 * 4 + 5 + 9 + 4)
            4 * 9 + 2 * 8
            6 + 2 + ((5 + 3 * 5 + 7 + 9) + 4 * 6 + 6) * 5
            (8 * 3 * 7) + (4 * 5 + 8 * 7 + 6)
            3 * 7 * ((3 + 3 * 5 + 9 + 7) + 9 * 2 + (4 + 3))
            (4 + 2 * 2 + 3 * 2 + 2) * 4 + 2 * 4 + 6 + 3
            ((9 * 7 * 7 + 8) * (2 * 5 + 2 + 3) * 2 * 4 * 7) * 9 + 5 + 5
            4 + 9 * 2 + 5 + (3 + 5 + (4 + 3 * 3 * 7 * 5 * 8) + 9 * 2 * 2) * (4 + (2 * 6) + 7 * 5 * 7 + 2)
            6 + 2 * 4 * (6 + 8) * (4 + 2) * 3
            3 + ((8 + 8 + 6) * 5) + 9 + 7 + ((7 * 2 * 9 + 5 * 3 + 5) + 3 + (2 * 5) + 3 + 5)
            4 + 2 * 6 + 6 + 6
            (5 * (4 * 9 * 8 + 5 * 8) * 6 * 9 + 3) * ((2 * 7 * 3 + 3 * 4 * 3) + 9 + 8 + 8) + 4
            4 * 4 + 9 * 9 + (2 * (6 * 5 + 9 + 7 * 3) + (2 * 8 + 3 + 2 + 3))
            6 * 4 + 5 * (6 * (6 * 7 + 2 * 6 + 4 * 7)) * 4 + ((5 * 9 * 8 * 9 * 5 * 5) * 5)
            3 + (5 * 4 + (3 + 6 * 2 + 8 + 7 * 8) + 3 + (9 + 8 * 6)) * (5 + 6 * (8 * 9 + 4 * 9 + 5) * 7 * 5) + 5
            9 + 2 + 9 + (3 + 9) + (4 * 4 + 7 * 5) + 5
            5 + 6
            6 + (7 + 2 * 6 * 9 * 7 + 5) * 9 + (4 * 5 * 8) + 2 * 3
            8 + 7 * (9 + (9 + 3 * 3 * 7) * (5 + 6 * 3 * 7 + 8 * 2)) * 7
            (5 * 7 + 5 + 3 + 6) * 8
            5 + ((9 + 5) + 9 + 4 * (5 * 4 * 7 + 3) * 3)
            5 * 8 * 7 + 8 * ((8 + 9 + 3 * 6 * 5) * 4 * 9 + 4)
            8 * (6 + (6 + 9 * 7 * 8 * 7) + 7 * 2) + ((2 * 9) * 7) + 4
            6 + ((4 + 5 + 6 + 7) * 7)
            8 * (5 * 6 * 4 * 8 + 6 * 6) + 2 * 9
            7 + 3 + 2 * (6 + 8)
            9 * ((4 + 4 * 9 + 6 * 8 * 7) + 9 + 2 + 6 * (4 * 9 + 7 + 6) * 3) * 5 + 7
            ((2 + 3 * 2 * 9 + 6) + 4 + 8 * 2 * 5 + 8) * 5 + 5 + (9 * 6) + 9 + (3 * 2 * 8)
            4 + (6 + 2) * 9 + (5 + 4 * (8 * 3 + 7 + 6) * 4) * ((7 + 2 + 3) * 6 + 9 * (6 + 7 * 2))
            6 + 2 + ((2 * 4 * 8) * 2 * 4 * 7 + 9) * (7 + 8 + (6 + 7 * 9 + 9 * 7) + 4) * 8
            9 * 9 + 7 + 9 * (9 * 4) * 9
            (7 * 3 + (3 * 7 + 4 * 4 * 8) * 2 * 2 * (3 * 3)) + 8
            9 * 6 + 3 * (2 * 5 * 7 * 7 + 7) * 8 + (2 + 4)
            (8 * 9) + 8 * 6
            (3 * 2 + 6 + (7 * 8 * 5 * 4)) + 5 * 6
            4 + 3 + (9 + 3 + (2 * 9 * 6) + 7) * 8 + 3 * 8
            9 + 6 * 8 + 4 + 9 * (9 * (8 * 4 + 9) + 3)
            5 + 4 * 6 * (8 * (2 + 9 + 6) * (6 * 3 + 2) + 6) * (8 * 8 * 3 * (6 * 9) * 3 + (4 + 4 + 2 * 2 * 6)) + 6
            4 + (6 + 6 * (4 * 7 * 7) + 5) * 5 + 6 * (7 + 2 * 4 * 2 + 9 + 9)
            ((8 + 7) * (7 + 5 * 9) + (9 + 2 * 6 * 2) + 6 * 7) * (3 + 3 * 2 + 3 + 7) * 4 + 7 * 7
            (6 + 9 * 7 * 4 + 7) * 6 + 7 + 5 + 3 + 8
            9 + 2 + (9 * (8 * 9 * 2 * 6 + 2 + 5)) + 6 + 4 + 8
            6 + 4 * ((2 * 9) * 6 * 5) + 2 * 9
            (6 + 8 * 2 + (5 + 2 * 7 * 7) * 5 + 4) * (2 * 3 + 8 * (9 + 6 * 4 + 3 + 6 + 4) * (2 + 5 * 8 * 2 + 4))
            3 + (9 * (2 * 7 * 6) * 9 + 7 + 3) + (7 * (5 * 6 + 7) + 3 * (5 * 7 * 5 * 4) * 8 + 6) * 4 * 5 + 9
            (6 + 6 + 4) + 3 * 4 + 3
            9 * (2 * 7 + 9 + (4 + 2) * 5) + (2 + 5 + (7 + 6)) * 8
            2 * (6 * 6 * 9 + 8) + 4 * 2
            ((4 * 3) + 8) + 5 + 5
            5 + 8 * 7 + 6 + 6 * ((9 * 2 * 5 + 4 * 7) + 8 + 6 + 5 + (3 + 7))
            8 + (8 * 8 * 8 * (3 * 7 * 8) * 4) * 8 + 5 * 8 * 3
            (8 * 3 * 8) * 4 * 3 * 3 * 6
            5 + 6 * (9 + (5 + 6 + 8 + 3 * 4) * (6 * 3 + 3 + 4 * 6 + 2))
            3 * (2 * (5 * 9) * 5 + 8 + 5 * 6) * 8 + (8 + 5 + 7 * (6 + 5 + 9 * 2 * 9) + 2 * (3 * 2 * 2 + 9 * 4))
            (2 * 8 + 5 + 7 + (8 + 2 + 7 * 5 * 5 + 4) * 2) + 5 * ((8 * 7 + 5) * 3 * 4 + (4 * 7 * 6) * (7 + 6 + 4 + 7 * 7) + (8 * 7 + 2 * 2 + 9 + 3)) + ((4 + 8) + 9)
            3 * (8 + (2 + 9) + 2 * 8 + (9 * 4 * 9)) * 2 * 3 * 4
            (4 + (9 * 4) + 5) * 8 * (3 + 7 * 3 * 7 + 4) * 7 * 2
            2 * 4 * (2 * 8 + 9 + 7 + (8 * 3 * 6 + 3 + 5) + 9) + (5 * 8 * 8 + (4 * 5 * 2 + 4 * 2 + 5) * 4) + 2 * 6
            7 + 8 + ((5 + 7 + 2 + 9 * 4 * 3) * 4) + 7 + 6 + 8
            ((4 + 4 * 4 * 9 * 6 + 9) * (7 + 9) * 2 + 8) * 8 * 6 + 9
            (2 * (6 + 7 * 3 + 6 * 7) + 4 * 2 + (3 + 8 + 9) * 8) * 8 + 8 + 8 + (2 + 8 * 5 * 9)
            (7 + 5 + (7 + 9 + 5) + 2) * 4 + 9
            3 * 6 + 8 + 2 * 7
            6 + 2 + ((7 * 6 + 3 + 4) * 8 * (2 + 6 * 5) * 6) + 4 * 2
            (4 * 8) + 7 * 3 + 3 * 7 + 5
            5 + 6 + 3 + 7 * (2 * 7) + (5 + (3 + 4 + 3 + 4 + 7) + 8)
            2 + ((4 + 7 * 6) + 8 + 9 * 6 * 9)
            (6 * 2) * (7 + 5 * (7 * 6 + 7 + 3 + 8 + 7) + 2 + 5 + 8) + 6
            (3 * 2 * 8 + 7) + (5 * 4 * 9 + 6) + 4 * 3 + 4
            8 + (8 + 2 * 3 + 2 + 2)
            (7 + 8 * 5) * (3 * (9 + 5 + 6 + 9 + 3 + 9) * 3) + 8 + 7 * (5 + 9 + 3 * 2 + 4 + 5) + 4
            7 * ((6 * 7 * 2 * 9 + 8) + 8 * 6 * 8 * 5) * 2 + 3 + 8 * 3
            (4 + (5 * 7 + 3 + 4 + 9)) * 7
            7 * 5 * 2 * (4 + 4 + 6 + 3 * 7) + (8 * (6 + 2 + 8 * 9 + 6) * 3 * 6 + 3 + (4 + 5 + 9)) * 7
            3 * 4 + 2
            ((6 + 9 + 5 + 4 + 9 * 7) * 9 + 8 + (9 * 2 * 6 * 4 * 7 * 3)) + (8 + 6 + 7 + 4) * 9 * (8 * 5) + 7
            (9 * 8 + 4) + (3 * 4 + (9 * 5 * 3 * 4) * 9)
            (7 + 8 * 3 + 6 * (4 + 5 + 7 * 8 + 9) + 4) * 3
            ((7 * 4) + (6 * 7 * 6) * 7 + 9 * 3) + 2 * (4 + 8) + (4 * 5 + 8 + 8 + 5) * (4 + 9) * 5
            (2 + 3 + 7) * 6 + 8 + (4 + 7 * 6 * (3 + 4 + 4 + 7 * 6) * 8 + 3) * 4
            6 * (8 * 3 + 4 * (6 + 9 * 2 + 3 + 8 * 5) * 8 + 6)
            8 + ((2 * 8 + 5 * 5 * 5 * 3) * 9 + 5) + 9
            (8 + 4 * 9 * 8) + 5 * 5 * (7 + 7 + 2 * 9 + 8 * 6) + 6 * (6 * 6 + 6 + 3 + 9 * 8)
            5 * 9 + (5 + (9 * 5 + 7) + (2 + 2 * 7) * 4) * (9 * 9 + 5 * 4) + 9
            7 + 7 * (2 * 9 * 6 + 3 + 5)
            2 + (8 * 9 * 7 * (4 * 9 + 9 * 4 + 5) + 4 * 4) + 5 + 2 + 8
            (2 + (4 + 9 + 4 * 7 * 3) + 4 + 8) + (2 + 4 * 5 * (3 + 4) + 8 * 5)
            2 + ((7 * 8 + 6 * 6) * 4 + 6 + 7 * 6 * 9) + ((7 + 9 * 5 * 5) + 4 * 9 + 6 + 7) * ((6 + 9 + 4 * 7 * 2) * 7 + 9 * (7 + 2 + 3 * 3 + 5) * 5)
            (3 * 8 + 2 * 2 + (3 * 7 + 8 * 5 * 6) + 6) + 4 + 5
            8 + 5 * 8 + 7 * 2
            (3 + (6 * 9 * 8 + 4 + 8) * 4 + 2) + 8
            (7 + 7) + 5 * (9 * 2 + 9) + ((4 * 3 * 3 * 4 + 4 * 9) * 8 * 8 + (3 * 9 + 7 + 8) * 3 + 9)
            (6 + (5 + 7 * 5) * 8 * 9) * 6 + 3
            3 + 2 * (5 * 8) + 5 + 6
            7 * 4 + 7 + 7 * 4 * (4 + 9 * 6 * 5)
            3 + 4 + (6 * 9) * (6 + (5 * 5 + 6 * 8) + 9 * (7 * 3 + 6)) + 8
            4 + 4 * 6 * ((4 * 3 * 2) * 4 + 3 + (7 + 8 + 7 * 6 * 9 * 3)) * (5 + 8)
            (6 + 7 * 8 + 3 + 9) * 5 * 3 * 3
            8 + 5 + 3 * 8 * 2 + 9
            (9 * (5 + 8 + 4 + 4 + 9 + 4) * 2 + 7 * (6 * 4 + 7 + 4 + 8)) + 9 + 5
            7 + 7 * 3 + 8 * ((5 + 3 * 4 * 9) * 4 * 6 * 8)
            2 + 5 + 8 + (3 + 3 * 4 + 8)
            5 + 4 * 3 * 9 + ((5 * 2 * 2) * 5 + 4 + (4 * 2))
            (3 * 7 + 2) * 3
            8 + 7 * 3 + 9 + 9 * 5
            2 + (2 * 6 + 3 + (4 + 3 + 4 + 2 + 7 * 9)) + 8 + 2 * 7 * 9
            (4 * 4 + 4 + 7) * 8 + 5
            2 * (5 * (8 * 5 * 2 * 6 * 8) * 6 * 3 + 8 * 8)
            8 + 3 * 9 + 5 * 9 * (5 * 7 + 6 + 5 * 3 + 7)
            2 + (3 + 7) * 8 * 9
            7 + 9 + 6
            4 + (6 + 7 + 8 + 4 * 5) + 2 * 6 * 8 * 2
            (9 + 3 * 7 * 3) * 8 + (9 + 5 * 2 * 8 + 3 * 4) * 8 + 2 * 6
            4 + 2 * 3 * 6 * ((3 * 5 * 3) * 2 + 6 + 5 * 4)
            3 + 9 * (7 * (8 + 7) * 9 * 7 + 7)
            6 + 3 * 6 + 4 * (7 * 7 + 2 * 9 + 4 + 2) + (6 + 6 + 6 + 7)
            (5 * 4) * 6 * (5 + 4 + 5 + 3 * (8 * 6) * 5) * 7
            (8 * 4) + 2 * (2 * 2 * (9 * 9 + 8) + 8 * 9 + 6) * 7
            3 + 5 * (4 + 9 + 3) + 9 * ((5 * 2 * 4 + 2) + 6 + 7 + 7 + 5)
            (3 + (9 + 7 * 3 + 6 * 6)) + 9 * 7 * 4 + (9 * 4 * 9 + 7 * 7 * 7)
            (3 * 7) + 4
            4 + (2 + 5 * 5) + 6 + 2 * 8 + 9
            8 * (6 * (7 * 3) + 8 + 5 + 8 + (8 * 2 + 6 + 9 + 4 * 3))
            7 * 8 * (5 * (2 * 6 * 9) * 3 * 6 * (6 + 8 + 5))
            (5 + 9 * 4 * 7 * 7 + 8) + 4
            ((8 * 9 * 6 + 4 * 6 + 3) * 6 + 2 * (4 + 8) * 7) + (4 + 4) + (7 + 7 * 7 * (4 + 5 + 2))
            (4 + (8 + 4 * 4 * 2 + 4) + 7 + 6 * 2 + 6) * 9 + 3 * 3 * (9 * 9)
            (9 * 5) * 6 + ((5 + 3) + 9 + 4)
            7 + ((8 * 9 * 5 + 2 + 2 * 7) * 9 * 3 * 7 * 2) * 6 * 2 + 6 * (9 * 4 * 2 + (2 + 7 + 5) * 4 + 7)
            8 + (5 * 4 * 7 + 2) * 6 + (6 * 3) * 4
            7 + (9 + 9 * 4 + 3) * 8 * (9 + (6 + 9 + 2 + 6 + 8) * 9 * 7 * (2 * 8 * 5 * 8)) * (4 + 7 + 8 + 2)
            6 + 3 * 5 * 8 + ((9 + 5 + 6) * 9 * (3 + 7 * 8 + 6) * (2 * 9 + 5)) + (7 + 6)
            5 * (9 + 7) + 2 * 4 + 3
            7 + 2 * (5 + 6 + 9 + 4 * (7 + 6 * 5 * 6 + 8) * 4) * 6 + 4
            6 * 8 * 8 * 4 + 8 * (2 * (9 * 4 * 7 + 9) * 5)
            8 * 4 + (4 + 7 * 3 + 5 + 8) + 4 * 7 + ((4 * 9 + 3 * 8) * 5 * 6 + 7 + 5 * 4)
            (9 * 3 + 3) + 9 + ((9 + 9 + 6 + 8) * 9 + 7) * 4 + 9 * ((8 + 7 + 8 * 2 + 8) * 5 + 8 + 3 * (3 + 5 + 2 * 6) * 8)
            (8 * 4 * 3) + (4 * 3 * 8 + (5 * 9) * 3 * 9) + 9 + (4 * 2) + 9 + 4
            2 * 5 + (9 + (5 + 2 * 2 * 7 * 3 * 9))
            (4 * 2 + 8) + (4 + 5 + 3 + 9 + (9 * 2 * 4) * (2 * 6 + 6))
            9 * 5 + 2 * ((3 * 3 + 3 + 9 * 7 + 9) * 3 * 3 + 6) + 7
            8 + (5 + 4 + 9) + ((6 + 9 * 6) * 8 + 3 + 6 * 8 * 6) * ((6 + 4) * (3 + 5 * 7 * 9 * 4 + 3) + (2 * 4)) + 4
            5 + 7 + (5 + 2 + (5 + 6 * 2) * 7) + 4 * 4
            (7 * 8 * 8) * (3 * (2 * 3 * 4 + 3 + 5 + 6) + 2 + 9 + (7 + 5 + 7 * 9) + 9) + 2
            2 * 8 + ((4 + 9 + 6 + 8 * 2 * 9) * 9 * (5 * 7 + 7 * 4 + 3 + 9) * 3 + 2 * 8) * (3 + 7 * 7 * 5 * 3) * 9 * 8
            6 * 8 * (5 * 7 + 3 * 2 * 3 + 4) + 7 + (4 + 2 * 8 * 8) + ((9 * 4 + 5) * (7 + 6 + 2 * 4))
            4 + 9 + (9 + 5 + 3 * 8) * 5 * 9
            5 + 2 + ((6 + 8 + 3 * 7) * 6 * 5 + 8 + 3 * 9) + 6
            3 + (7 + (3 * 6 * 7) + 9 * 2 * 2 + (9 * 4)) * 8 * 8 * 6
            ((5 + 9 + 8 + 6) + 4 + (3 * 4 + 8) * (2 + 5 * 2 * 6)) + 9 + 8 * 2
            8 * 9 + 2
            (3 * (3 * 5 * 2 * 3 * 3) * 3 * 7) * 3 * 6 * 5 + 8
            6 * 8 + 6 + 9 * 9 * (7 * 2 * 5 * 3 * 4)
            (8 + 2 + 4 * 3 + (4 * 6 + 7 * 2)) + (6 + 8) + 8
            9 + 4
            ((4 + 8) + 8 * (4 * 5 + 8 + 2 + 8 * 9) + 7 * (3 * 7 * 4 * 8) + 4) * 6 * 7 * (2 * 3 + 6)
            6 + (4 + 5 * 9) * 6 + 9
            (4 * (5 * 3 * 3 * 7 * 2 + 7) + 4) + 4 * 8 * 4 * 3
            8 + (9 + 8 + 9) * 5
            9 + 8 + 8 + 9 * (8 * 3)
            (6 + 8 * 5 * (5 + 7 * 8 + 4 + 3) * 6 + 4) * (2 * 4 * 5) + 7 * 8 + (9 * 6)
            ((9 + 2 + 5 + 5 + 5) + (9 + 6 + 7) * (7 + 5 + 9 + 5 * 6)) * (4 + (7 + 8 + 9 + 7) * (7 + 7 + 6 * 4 + 2) + 5 + 5 + 6) * 3
            7 * (6 + 8 * 6 * (5 + 6 + 5 * 4) * (9 * 2 * 6 * 4 + 8)) * 2 + 9
            (7 * 3 * 3 * 9 * 4) + 9 * (9 * (8 * 8 + 2 * 8 + 3 + 6) * 5 + (4 + 3 * 3 * 4 + 5 * 8)) + 9 * 4
            (7 + 3 + 4) * (2 + (4 * 7 + 5 * 3 * 7) * 4 * (3 + 5) + 8 * (8 * 9 + 2 + 7))
            7 + (8 * 7 * 2 + (7 + 6 + 8) * 3 * 3) + (4 * (7 * 7 + 4 * 7 * 4) + 7 * 5 + (6 * 6 + 4))
            5 + 3 + 4 + (2 + 4 * 5 * 2 + 8)
            (3 + 3 + 2 + 9 * 4) + 7 + (2 * 8 * 7 * 3 + (3 + 9 + 9 * 2)) + 7 * (9 * 7) + 2
            7 * 5 * (4 * 8 * 4 * (7 + 6 * 5 * 5 + 7 * 9)) + 9
            (2 * 5 + 2 + (6 + 3 + 4)) + 2 + ((6 + 8 + 9) * 4) + 2
            8 * (2 * 9 + 3 + 2) + 9
            (5 + (5 + 9 * 5 + 5 * 4 * 9) * 7) + (8 * 8 * 9 + 8 * 3) + 3 + 6 * 5
            8 + (7 * (7 + 9) + 3)
            (7 + 4 * 5 + (8 + 2 + 7) * 7 + 8) + (9 * (9 * 8 + 7 + 9 * 4)) * (4 + 9 * 5) + 2
            (7 * 5 + 6 * 5 * (2 * 4 * 3 + 4 + 7)) * 5 * (7 * (7 + 3 * 4) * 9)
            4 * (6 + 9 * 3) + 9 + 5 * 9
            (9 + 3 + 8 * 7 * 2 * 6) * (8 + 7 + 2 + 7 + 6) + (5 * 6 * 6 + 3) + 2 + 9
            5 * 5 + 6 + (3 * 7 + 6)
            2 * 7 + (5 * (5 + 8 + 9 * 5 + 3) + (3 * 2) * 3 + 7 * 2) + 2 + 5 + 3
            (9 * 5 * (4 * 9 * 6) + 3 * 8) * 2 * 8 * 4 * ((3 * 7 * 9 + 5) + 6 * 7)
            5 * 9 + ((9 + 4 + 5 * 7) + 4 * 3 + 6 * (2 * 2)) + 7 + 2
            2 * (6 + 2) * ((6 + 3 * 8 * 3 + 7) * 7 * 5 + (9 + 2 + 3 * 9 * 8 * 8) * 7) + (9 * 9 + 8 * 7 + (9 * 3 * 8) + 9)
            4 + 2 * 2 * 5 + 5 * 4
            4 + 4 * 8 * 7 + 3
            9 + (3 + 8 * (4 * 2 + 3 + 4) + 5 + 8) * 8 + 4 * 7 * (4 + 3 * 9 * 4)
            6 + (3 + 2 * 2 + 3 + 7 * 8) * 4 * 8 + 2
            (7 * 4 + 5 * 2 * (5 * 8 + 3 + 6 * 2)) + (7 * 2) * 9 * (7 + 7 + 5) * (8 + 2 * 9) * 6
            3 + 9 * (7 + 2 * 6 + 2 + 5) * 4
            9 + (8 * 2 + (8 + 8 + 8)) * 8 + 7
            6 * 7 * 7 * 4
            2 * (2 + (4 + 7 * 7 + 6 * 8)) + 4
            (9 + 8 + (5 + 9 + 4 + 9 * 6 + 3)) * 4
            6 + ((9 + 2 + 4 * 2 + 9) * 7 * 3 + 5 * 6 * 6) + 7 * 4 * 9
            (5 + (5 + 9 + 6 + 7)) * 5 + 4 + 6
            (5 * 8 * 6 + 8 + 7) * 2 + 7 + 8 + 5
            (7 * 5 + 6 + 5 * (6 + 4 * 9)) + 4 * 8 + 3
            4 * 9 * (8 * 5 * 3 * 9 * (4 * 2 * 3 * 4) * 3) * ((5 + 5 * 3) + 9 * 4 + (9 + 6) * 2 * 6)
            7 * ((3 + 8 * 3 * 6 * 8 * 6) + (8 * 3 * 7 + 4 * 2 * 5) * 5 * 8 * (7 + 9 * 4 + 6 * 2 * 8) * 4) + 7 + 7
            (7 * (7 + 4 + 4 * 5 * 5 + 2) * 2 + 5 * 6) + 2 * 5 + 3
            (2 + 7) * 6 * (3 + (9 * 4 + 8 * 8 + 5) + (2 + 7 * 5 * 5) * 5 + (3 * 6) * 2) + (8 + 5 * 9 + 9 + 8)
            6 * 5 * 3 + 7 * (4 + 2 + 7 + 9)
            (9 + 6 + (2 + 2 + 5 + 6 * 5 * 8)) + 7 * 2
            ((7 + 7 * 9) + (5 + 8 * 2 + 4 * 7 * 7) + 7 * 3) * (8 + 2) + 7
            3 + 6 * (8 * 7 * 3 * 7) + (5 + 4 * 3) + 6
            (7 + 7) + 9 + (5 * (5 + 9 * 3 + 3 * 2 + 3) * 4 * (8 * 7) * 2 * (7 * 9 * 6 + 4 * 6 + 2)) + 6
            ((6 * 2 * 6 + 3 * 8) + (9 + 8 + 9 + 2 + 3 + 5) * 7 * 9) + 8 + 6 * 3 + 6 + 7
            ((9 * 8 + 2) * 8 + 6 * (4 + 8 + 4 * 9 * 9) + 9) * 7 * 5
            8 + 6 + (4 + (6 + 6 + 3 + 9)) + 3 * 3 + 4
            8 + (3 + 5 * 9 + 3) * 9 + 8 + 8 + 7
            5 + (3 * 4 + (8 + 4 + 3 + 4) * 8 + 7)
            (9 * (6 * 7 * 9 + 5) * (7 + 6 * 2 * 4 + 2 + 8)) + 4 + 8
            3 * 5 * (9 + 2 * (9 * 6 * 7 + 9) + 2) * 7 + 6
            4 + (3 * 7)
            (3 + 7 * 9) * 6 * 6 + 3 + 3 + (2 + (9 * 4 + 6 + 7 + 9))
            (4 * 2 * (9 + 5 * 9 + 5 * 9 + 9) + 8) * 7 * 7 * 3 + (9 * 4 * 7)
            3 * (4 * 5) + 9 + ((7 * 8 * 4 * 8) + 7 * 3)
            8 + 9 + 4 * 4 * ((5 + 3 * 6) * 5) * 6
            9 + 8 + (9 * 9 + 4) + 5
            9 * ((8 * 7 * 2 + 4 + 8) * 9 + 5 + (2 + 4 + 6 * 7 * 3 + 3) + (7 * 9 * 2 + 7 * 9) * 3) + (6 * (9 + 7 * 3 + 7 + 6) + 6 + 6)
            4 * ((5 * 7 * 9 * 9 * 4) + 2 + (5 * 5) * 2 * 7 * 9) * 4 * 7 + 2 + 9
            9 + (7 + 5) + 4 + (3 + 2 * 4) * 8 + 7
            (8 * 7 + 7 + 6 + 7 * 6) * 5 + 7 + 5
            9 + 5 * 5 * (3 + 8 * 4 + 8 + 4 * 5) + 3 + 4
            4 * 5 * 6 + 6 + (7 * 7 + 6 + 4 + 4 * 3)
            (4 * 2 + 9) + (4 * 3 + 9) * 9 + 8
            9 * 6 + ((7 + 2 + 6) + (2 + 2 * 3)) * 6
            ((5 + 6 + 6 * 6) + 8 + (3 * 8 * 3 * 3 * 6) * 4) * (2 + 6 * 7 * 6 * (3 + 5)) * 6 * 2
            (5 * 3) + (2 + 9) + 2
            4 * 8 + 6
            2 + 6 + 2 + 4
            5 * 8 * (9 * 2 * 8) + 8 + 9 + 4
            5 * (4 + 7 * (5 * 7 * 6 * 5 * 8) + (6 + 3 * 6 + 3 + 7) * (7 * 5 * 8) * 4)
            5 * 9 + 5 + 5 * (9 * 2 + 4 + 9 * 7 + 4) * 7
            5 + (4 + 7 + (9 + 7 + 5) + 9 * 6 * 3) * 5 * 3 + 5
            (7 * 8 * 7) + 6 + 2 * 4 + 6 + 4
            (2 + (4 + 4 + 6) + 9 + 5 * (2 * 7 * 3) * 2) + 7 + ((9 + 5) + (5 * 3 + 8 + 5 * 6 + 8) * 2 + 7) * 9 * 3
            (2 * (9 * 5) + 9 + 4) + 8 * 7 + 8 + 8 + 8
            2 + 3 * 3 + (7 * (9 + 5) + (8 + 6 + 8 + 2) * 7 * 2 + (7 + 8 + 9 * 3)) + 5 + 9
            5 + 4 + (7 * 3 * (2 * 6 + 2 * 3) + 3) * 7
            9 + (3 + (7 * 3) + 7 + 3) + 5 + (7 * 3 + 7)
            7 + 4 * 2 + 4 + (8 + 3 + 7) * 5
            9 * 4 + (6 + 9 + 3)
            2 + 3 + 7 + 7 + 8 + ((4 * 7 + 9 + 8 + 6 * 3) * 3 * 7 * 9 * 6 * 7)
            (7 * 9 * 4 * 2) + (3 + (5 + 2 + 4 + 6 * 8 * 5) + 5 + 5) + 4
            (6 * 2 + 8 + 3) * ((8 * 3 * 9 * 2 * 7) * 4 + 9 + 6) * 6 + 8
            (3 * 5 * 8 * 4 + (2 * 5) + 2) + 5 * 8 * 5 * 3 * 4
            4 + (9 + 7 * (6 + 3 + 9 + 7))
            (9 * 2 + 3 + 7) * 7 * (8 * 2 * 7) * 3 + 4
            6 * (8 * 2 * 8 + 8 + 9 + (4 * 7 * 4 + 9 + 5 + 5)) * 4
            5 * (2 * 4) * 6
            4 + 4 * 4 + 6 + ((5 + 3 + 2 * 2 + 4) * 3 * 2 * (7 + 7 + 8 + 6 + 3) + 2)
            7 * 6 * 8 + (9 * 9 + 3 + 9 + 3 * 7) + 3
            8 * (2 * 8 * (2 * 4 + 6 * 4 * 8)) * (9 * 3 + 8 * 3 * 8 * (6 + 8 + 6 * 7 + 3)) * 6 * 6 * 7
            (3 + (7 * 7 * 4 + 2 * 4 + 7)) + 6 * 5 + 9 + 4
            (2 + 2 * 3) + (3 * 3 + 2 * 3 * 5 + 9) + 8 * (8 + 9 * (9 + 9 + 2 * 9) + 9 + (6 + 3 * 7) * 5) * 4
            4 * 8 * 3 + 5 * (6 * (5 * 4 + 9 + 4 * 6 * 3)) + 9
            4 * ((7 + 4 * 8) * 2) * 6 * 6 + (7 * 4) * 2
            7 + 3 * 3 + 6
            2 * (3 + (6 + 5) + (3 * 2 + 6) + 4) + 6 * 7 * (5 * 2 + 3 + 9 * 9)
            (7 * 5 * (8 + 4 + 6 + 3 * 5)) + 2 * 9
            3 * 4 * 7 * (7 + (5 + 7)) + 9
            9 * ((9 + 7) + 3)
            5 + ((6 * 2 + 8) * 7)
            7 + 2 * (6 + 6 + 6 + 3) * 2
            (9 + 3 * (4 + 8 + 5 + 5 + 7 * 9)) * ((7 * 5 * 9 * 2 * 6) + 5 + 6 * 3 + 9) * 8 + 7 * (5 + 6)
            3 + ((8 + 5 * 9 * 6 + 6) * 2 * 5 + 9 * (8 * 5 * 8 + 4 + 9 * 2) * 8) * 8 + 7
            3 + 2 * 3 + ((9 + 3) * 9)
            8 * (2 + (6 + 2 * 8)) * 2 * 7 + 8
            (4 + 9 * (8 + 4 * 2 + 9 * 6 + 8) * 6 + (8 * 9) + 3) * 6
            3 * (3 * 9 * 5 * 6 + 2 + 4) * 9 * (8 * 4 * 7 * 7 * 8 * 3) * 5
            8 * 2 + (7 + 4 + 5 + 2 * 4 * 8) + (2 * 4 + 7 + 4) * 4
            (7 + 9 + 9 * 6) * 6 + 9 * 8
            5 + 8 + 3 * 3 + (6 + (9 * 8 + 8 * 5) + (3 * 5 * 4))
            (2 * 9 + 3 * 9 * 8) * (6 + 6 * 8) * 9 * 4
            2 * (8 * 9 + 4)
            2 + 5 * (5 * 4 * 9 * 3 * 8 * 3) * 8
            9 * 9 * (7 * 3 * 4) * 8 * 8
            4 + (9 + 8 + 6 + 8) * 9 + (6 + 6 + 9 * 5) + 9 + (8 + 4 * (5 + 6 + 3 * 6 + 6) + 5 * (8 * 3 * 8 * 3 * 7 * 2))
            (5 * 8 * 7 * 5 * 2 + 4) * 6 + 8 * ((4 * 5 * 9 + 3 + 2) * 4 + 4)
            2 * 9 + (9 * 7 * 8 * 6 * 7 * 9) + 4 + (7 + (3 + 9 * 5 + 5 + 2) + (6 * 3 + 2 + 7 * 7)) * 7
            7 * 3 * 7 + (2 * 2 * 4) * 5
            (2 + 8 * 6 * 3 * (6 * 5 + 9 + 3 * 8 + 8) + (3 + 6 + 9 + 4 * 3 * 6)) + 4 + (3 * 4 + 2 + (5 * 3 * 7 + 5 + 9)) * ((2 + 3 + 6 + 6 * 4 * 2) + 4 * 9 + 7 * (6 + 2)) + (6 + (8 * 7 + 2 + 2) + 8) * (7 + (9 + 3 + 9))
            7 * 4 * (7 + 5 * 3 + (9 * 2 * 5 * 2) + 9 + 2) * (5 + 4 * (8 + 8 * 6) + 2)
            7 * (9 * (7 + 4 * 3 + 8 + 3) + 8 + 8) + 9
            ((8 + 6 + 4) + (9 + 6 * 7 + 4) + (6 + 9 * 5 + 4) + 3) + 5 + 9 + (9 + 9 * 7) * 3 + 2
            8 + 4 + (9 * 9 + 2 * 8 + 4) + 5 * 4
            9 * (3 * (7 + 8 * 7 * 8) + 5 * 7 * (5 * 6 + 7 * 4 + 6)) + 9
            (8 + 3 * 6 + 8 + 8) * 5 * 3
            ((5 + 7 + 4 + 2 * 6) + (6 * 4 + 6 * 3 + 8) * 5 + 8 + 8 * (5 * 6 + 2)) + (2 * 4 + 9 * 7 * 6 * (9 * 6 + 6 * 8)) * 6 * 9 + 3
            ((3 * 7) * 7 * 2 * 8 + 2 * (2 + 6 + 7 * 3 + 2 * 5)) + (6 * 3) * (4 * 5 + 4 + 9) + 6 + (9 * 7 * 5 + 5 * 9) + 3
            6 + 4 + 5 + 8 + (2 * (2 * 2 + 8) * 9 * 7) + 6
            5 * 4 + 7 + (2 + 6 * 3 + (2 * 2 * 2))
            (8 * 3 + (2 * 6 * 4 * 2 * 9) + 5 + 9 * 8) * 5 + 7
            5 * 5 * (3 + 4 * 3) + ((7 * 3 + 5 + 8) + 2) + 6
            ((9 * 5) + 4) * 6 + 4 * ((5 * 2 * 9) + 5 + 3) + 6 + 3
            9 * (2 + 4 + 4 * (5 * 9 * 3 + 4 * 5 + 8) * (3 * 7) * 6) + 6
            (3 + 2 * 5 + 5) * (9 * 3 + 5 * 7) + 3 * 9
            6 * ((7 + 9 + 9 * 2 * 4) * 8) + 9 + (6 * 8) * 3
            4 + 9 * 8 + (3 * 4 * 4 * 7 * (7 * 6 + 7 * 2 * 7 + 8))
            (7 + 7) + 7 * ((7 + 3 * 4 + 8 + 8 + 6) + 9 + 2 * 6 * 9 + 2)
            9 + 7 + 9 * 9 + 5 + 5
            8 + 2 + 3 + 2 + 6
            8 + (4 * (3 * 2) + 9 + 6)
            9 * 8 * 2 + (6 + 6 * 9) + 8 * 8
            7 + 4 * 2 * 5 + 4 + 7
            5 * 8 * 4 * 2 * (6 + 5 + 2)
            3 * 5 * (3 + 9 * 2) * (3 + 5)
            3 * 6 * 7 * (2 + 8 * (8 + 6 + 3 * 9 + 9 * 5) * 3 + 8 * (6 + 3 * 5 + 8 + 4)) * (9 + 5 + 9) * 5
            5 * 3
            (3 * 2 + 7 + 8) * 2
            ((7 + 8 * 4) + 6 + 9 * (6 + 5 + 4 + 9 + 7 * 6) + 9 + 5) + 5 * 4
            3 * 9 * 6 * 4 * ((4 + 8 + 6) + (4 + 6 * 2 + 9 + 7 * 2) + 7 * 2 + (5 + 9 * 2 + 2))
            (8 + 6 + 5) * 4 * 8
            7 * 4 + 8 + (4 + 9 + 3 + 5 + 2 + 6) + (7 + 2 * 5 * 7) + 3
            6 + 7 * 4 + (8 * 3 + 8 * (7 * 5 + 3 * 4 * 5) * 9 * 2) * 2 + 3
            9 + 6 * (6 * (4 + 7)) + 4 * 7
            (4 + 5 + 8) * 8 + (8 * 7 + 9 * 5) * 9
            5 * 3 * (9 * 9 + 5 * 6 * 7 + 6) + 7 + 5
            7 + 3 * 7 * ((4 + 6 * 4 + 8) + 5 + 9 + 7 * 5)
            9 * (8 + 9 + 3 * 5 * (4 + 6 * 4 + 7 + 7) + (3 * 8 * 2 + 2 + 8)) * 8 + 8 * (3 * 9) + 6
            2 + (7 * 9 * 3) + (5 * 8 * 8 * (2 + 2 * 2 * 6 * 7 + 6)) * (9 * 6 + 2)
            8 * (4 + (8 + 3 * 4 * 2 * 5) * (8 * 2) + (2 + 2 + 7 * 6 + 3 * 2) + (2 * 7)) * 7
            3 * 7 * 3 + 4 + (2 + (6 * 3) + (2 + 5 + 8 + 3 * 3) + 2 + (4 * 7 + 2)) * 4
            (7 * 7 + 5 * 8) * 3 + 2 + 9 + (2 * 8 * 4 + 7 + 4 * 8) * (7 + 8 * 5)
            2 * ((7 * 5 * 4) * (4 + 7 * 9 + 3 + 3 * 6) + (5 + 5 + 9 + 7 * 8) * (5 * 2 * 8 + 4 * 6) + (5 + 4 + 3 * 9 * 4 + 9)) * 6 * 8 * 5
            (8 + 2) * 3 + 8 * ((5 * 9 * 7 * 7) * 7) + 8
            (3 + 7 + 9 + 7) * 9 * (2 + 5 * 8 * 9) + (5 * 5 + 9) + (5 + (4 * 4 * 7 * 9) + 4 * (2 * 2 + 5))
            3 + (4 + 8 + 3 + 4 + 7 + 6) + 4 + 3 * 4 + ((5 * 6) + 2 * 5 * 2 + 8 * 3)
            """;

    @Test
    public void test() {

    }
}