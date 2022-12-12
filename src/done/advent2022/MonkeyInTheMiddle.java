package done.advent2022;

import org.junit.Test;

import java.util.Collection;
import java.util.List;
import java.util.Scanner;
import java.util.function.LongToIntFunction;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static util.BatchSupplier.batch;
import static util.Utils.Repeat.repeat;
import static util.Utils.split;
import static util.Utils.substring;

public class MonkeyInTheMiddle {

    static final long DIV = 2*3*5*7*11*13*17*19L;
    static Monkeys monkeys;

    public static void main(String[] args) {
        try (Scanner in = new Scanner(INPUT)) {

            monkeys = new Monkeys(
                    batch(in).withDelimiter('|').lines()
                             .map(Monkey::create)
                             .toArray(Monkey[]::new)
            );

            monkeys.executeRounds(10_000);

            System.out.println("Result: " + monkeys.monkeyBusinessLevel());
        }
    }

    @SuppressWarnings("unused")
    private static void print() {
        System.out.println("== After round " + monkeys.rounds + " ==");
        for (int m = 0; m < monkeys.size(); m++) {
            System.out.println("Monkey " + m + " inspected items " + monkeys.monkey(m).inspectionCount() + " times.");
        }
    }

    static class Monkeys {
        private final Monkey[] monkeys;
        private int rounds = 0;

        Monkeys(Monkey[] monkeys) {
            this.monkeys = monkeys;
        }

        void nextRound() {
            rounds++;
            Stream.of(monkeys)
                  .map(Monkey::inspectAll)
                  .flatMap(Collection::stream)
                  .forEach(this::addTo);
        }

        void executeRounds(int rounds) {
            repeat(rounds).action(this::nextRound);
        }

        void addTo(Move move) {
            addTo(move.worry(), move.monkey());
        }

        void addTo(long number, int monkey) {
            monkeys[monkey].add(number);
        }

        long monkeyBusinessLevel() {
            return Stream.of(monkeys)
                         .mapToLong(Monkey::inspectionCount)
                         .sorted()
                         .skip(monkeys.length - 2L)
                         .reduce((l1, l2) -> l1 * l2)
                         .orElse(1L);
        }

        int size() {
            return monkeys.length;
        }

        Monkey monkey(int m) {
            return monkeys[m];
        }
    }

    static class Counter {
        private long count;

        void inc(int size) {
            if (size < 0) {
                throw new IllegalStateException("size = " + size);
            }
            count += size;
        }

        public long count() {
            return count;
        }
    }

    record Monkey(List<Long> items, Expr op, LongToIntFunction action, Counter counter) {
        Monkey(String items, String expression, int divBy, int giveIfTrue, int giveIfFalse) {
            this(parseItems(items),
                 new Mod(Expr.create(expression), DIV),
                 old -> (old % divBy) == 0 ? giveIfTrue : giveIfFalse,
                 new Counter()
            );
        }

        List<Move> inspectAll() {
            counter.inc(items.size());
            List<Move> moves = items.stream()
                                    .mapToLong(Long::longValue)
                                    .map(op::eval)
                                    .mapToObj(num -> Move.create(num, action))
                                    .toList();
            items.clear();
            return moves;
        }

        void add(long number) {
            items.add(number);
        }

        long inspectionCount() {
            return counter().count();
        }

        private static List<Long> parseItems(String items) {
            return split(items).map(Long::valueOf).collect(toList());
        }

        static Monkey create(String line) {
            String[] parts = line.split("[|]");
            return new Monkey(
                    substring(parts[1], ": "),
                    substring(parts[2], "= "),
                    Integer.parseInt(substring(parts[3], "by ")),
                    Integer.parseInt(substring(parts[4], "monkey ")),
                    Integer.parseInt(substring(parts[5], "monkey "))
            );
        }
    }

    record Move(long worry, int monkey) {
        public static Move create(long num, LongToIntFunction action) {
            return new Move(num, action.applyAsInt(num));
        }
    }

    sealed interface Expr {
        long eval(long old);

        @SuppressWarnings("unused")
        static Expr createWithDiv3(String expr) {
            return new Div3(create(expr));
        }

        private static Expr create(String expr) {
            String[] tokens = expr.split(" ");
            return switch (tokens[1]) {
                case "*" -> new Mult(parseLiteral(tokens[0]), parseLiteral(tokens[2]));
                case "+" -> new Add(parseLiteral(tokens[0]), parseLiteral(tokens[2]));
                default -> throw new IllegalArgumentException("Unsupported expression: " + expr);
            };
        }

        static Expr parseLiteral(String expr) {
            if ("old".equals(expr)) {
                return new Old();
            }
            return new Const(Long.parseLong(expr));
        }
    }

    record Mult(Expr expr1, Expr expr2) implements Expr {
        @Override
        public long eval(long old) {
            return expr1.eval(old) * expr2.eval(old);
        }
    }

    record Add(Expr expr1, Expr expr2) implements Expr {
        @Override
        public long eval(long old) {
            return expr1.eval(old) + expr2.eval(old);
        }
    }

    record Const(long num) implements Expr {
        @Override
        public long eval(long old) {
            return num;
        }
    }

    record Old() implements Expr {
        @Override
        public long eval(long old) {
            return old;
        }
    }

    record Div3(Expr expr) implements Expr {
        @Override
        public long eval(long old) {
            return expr.eval(old) / 3;
        }
    }

    record Mod(Expr expr, long divBy) implements Expr {
        @Override
        public long eval(long old) {
            return expr.eval(old) % divBy;
        }
    }

    @SuppressWarnings("unused")
    private static final String INPUT1 = """
            Monkey 0:
              Starting items: 79, 98
              Operation: new = old * 19
              Test: divisible by 23
                If true: throw to monkey 2
                If false: throw to monkey 3
                        
            Monkey 1:
              Starting items: 54, 65, 75, 74
              Operation: new = old + 6
              Test: divisible by 19
                If true: throw to monkey 2
                If false: throw to monkey 0
                        
            Monkey 2:
              Starting items: 79, 60, 97
              Operation: new = old * old
              Test: divisible by 13
                If true: throw to monkey 1
                If false: throw to monkey 3
                        
            Monkey 3:
              Starting items: 74
              Operation: new = old + 3
              Test: divisible by 17
                If true: throw to monkey 0
                If false: throw to monkey 1
            """;

    @SuppressWarnings("unused")
    private static final String INPUT = """
            Monkey 0:
              Starting items: 54, 82, 90, 88, 86, 54
              Operation: new = old * 7
              Test: divisible by 11
                If true: throw to monkey 2
                If false: throw to monkey 6
                        
            Monkey 1:
              Starting items: 91, 65
              Operation: new = old * 13
              Test: divisible by 5
                If true: throw to monkey 7
                If false: throw to monkey 4
                        
            Monkey 2:
              Starting items: 62, 54, 57, 92, 83, 63, 63
              Operation: new = old + 1
              Test: divisible by 7
                If true: throw to monkey 1
                If false: throw to monkey 7
                        
            Monkey 3:
              Starting items: 67, 72, 68
              Operation: new = old * old
              Test: divisible by 2
                If true: throw to monkey 0
                If false: throw to monkey 6
                        
            Monkey 4:
              Starting items: 68, 89, 90, 86, 84, 57, 72, 84
              Operation: new = old + 7
              Test: divisible by 17
                If true: throw to monkey 3
                If false: throw to monkey 5
                        
            Monkey 5:
              Starting items: 79, 83, 64, 58
              Operation: new = old + 6
              Test: divisible by 13
                If true: throw to monkey 3
                If false: throw to monkey 0
                        
            Monkey 6:
              Starting items: 96, 72, 89, 70, 88
              Operation: new = old + 4
              Test: divisible by 3
                If true: throw to monkey 1
                If false: throw to monkey 2
                        
            Monkey 7:
              Starting items: 79
              Operation: new = old + 8
              Test: divisible by 19
                If true: throw to monkey 4
                If false: throw to monkey 5
            """;

    @Test
    public void test() {

    }
}