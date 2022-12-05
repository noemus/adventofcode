package done.advent2022;

import util.LineSupplier;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static java.util.Collections.reverse;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static util.Utils.Repeat.repeat;

public class SupplyStacks {

    static final int CRATE_INPUT_WIDTH = "[A] ".length();

    static final CrateMover9001 stacks = CrateMover9001.create();

    public static void main(String[] args) {
        try (Scanner in = new Scanner(INPUT)) {
            Stream.generate(new LineSupplier(in))
                    .takeWhile(not(String::isBlank))
                    .map(Crates::new)
                    .flatMap(Crates::crateInput)
                    .forEach(stacks::addLine);

            System.out.println("Before: " + stacks.collect());

            Stream.generate(new LineSupplier(in))
                  .takeWhile(Objects::nonNull)
                    .map(Command::create)
                    .forEach(stacks::execute);

            System.out.println("Result: " + stacks.collect());
        }
    }

    record Command(int fromStack, int toStack, int count) {
        static Command create(String line) {
            // example line:
            // move 8 from 7 to 1
            String[] tokens = line.split(" ");
            int count = Integer.parseInt(tokens[1]);
            int from = Integer.parseInt(tokens[3]);
            int to = Integer.parseInt(tokens[5]);
            return new Command(from, to, count);
        }
    }

    record CrateMover9000(List<SupplyStack> stacks) {
        void addLine(CrateInput input) {
            while (input.index() >= stacks.size()) {
                stacks.add(SupplyStack.create());
            }
            stacks.get(input.index()).add(input.crate());
        }

        void execute(Command command) {
            move(command.fromStack, command.toStack, command.count);
        }

        void move(int fromStack, int toStack, int count) {
            repeat(count).action(() -> move(fromStack, toStack));
        }

        void move(int fromStack, int toStack) {
            move(get(fromStack), get(toStack));
        }

        void move(SupplyStack from, SupplyStack to) {
            to.put(from.take());
        }

        SupplyStack get(int stackId) {
            return stacks.get(stackId - 1);
        }

        static CrateMover9000 create() {
            return new CrateMover9000(new ArrayList<>());
        }

        String collect() {
            return stacks.stream().map(SupplyStack::top).collect(joining());
        }
    }

    record CrateMover9001(List<SupplyStack> stacks) {
        void addLine(CrateInput input) {
            while (input.index() >= stacks.size()) {
                stacks.add(SupplyStack.create());
            }
            stacks.get(input.index()).add(input.crate());
        }

        void execute(Command command) {
            move(command.fromStack, command.toStack, command.count);
        }

        void move(int fromStack, int toStack, int count) {
            SupplyStack to = get(toStack);
            get(fromStack).take(count).forEach(to::put);
        }

        SupplyStack get(int stackId) {
            return stacks.get(stackId - 1);
        }

        static CrateMover9001 create() {
            return new CrateMover9001(new ArrayList<>());
        }

        String collect() {
            return stacks.stream().map(SupplyStack::top).collect(joining());
        }
    }

    record SupplyStack(Deque<Crate> crates) {
        void add(Crate crate) {
            crates.add(crate);
        }

        String top() {
            return crates.isEmpty()
                   ? ""
                   : crates.peek().code();
        }

        Crate take() {
            return crates.pop();
        }

        Stream<Crate> take(int count) {
            List<Crate> result = repeat(count).stream(crates::pop).collect(toList());
            reverse(result);
            return result.stream();
        }

        void put(Crate crate) {
            crates.push(crate);
        }

        static SupplyStack create() {
            return new SupplyStack(new ArrayDeque<>());
        }
    }

    record Crate(String code) {}

    record Crates(String line) {
        Stream<CrateInput> crateInput() {
            return Stream.generate(new CratesSupplier(line))
                    .takeWhile(Objects::nonNull);
        }
    }

    record CrateInput(Crate crate, int index) {}

    static class CratesSupplier implements Supplier<CrateInput> {
        private final String line;
        private int searchIndex = 0;

        CratesSupplier(String line) {
            this.line = line;
        }

        @Override
        public CrateInput get() {
            int crateStart = line.indexOf('[', searchIndex);
            if (crateStart != -1) {
                int crateEnd = line.indexOf(']', crateStart);
                searchIndex = crateEnd + 1;
                return new CrateInput(new Crate(line.substring(crateStart + 1, crateEnd)), crateStart / CRATE_INPUT_WIDTH);
            }
            return null;
        }
    }

    @SuppressWarnings("unused")
    private static final String INPUT1 = """
                [D]        
            [N] [C]        
            [Z] [M] [P]
             1   2   3 
            
            move 1 from 2 to 1
            move 3 from 1 to 3
            move 2 from 2 to 1
            move 1 from 1 to 2
            """;

    @SuppressWarnings("unused")
    private static final String INPUT = """
                            [B]     [L]     [S]
                    [Q] [J] [C]     [W]     [F]
                [F] [T] [B] [D]     [P]     [P]
                [S] [J] [Z] [T]     [B] [C] [H]
                [L] [H] [H] [Z] [G] [Z] [G] [R]
            [R] [H] [D] [R] [F] [C] [V] [Q] [T]
            [C] [J] [M] [G] [P] [H] [N] [J] [D]
            [H] [B] [R] [S] [R] [T] [S] [R] [L]
             1   2   3   4   5   6   7   8   9 
            
            move 8 from 7 to 1
            move 9 from 1 to 9
            move 4 from 5 to 4
            move 4 from 6 to 1
            move 3 from 8 to 5
            move 6 from 5 to 9
            move 1 from 5 to 1
            move 4 from 4 to 9
            move 7 from 3 to 7
            move 6 from 7 to 3
            move 1 from 8 to 7
            move 2 from 7 to 6
            move 1 from 8 to 9
            move 1 from 6 to 3
            move 4 from 3 to 5
            move 5 from 1 to 3
            move 1 from 1 to 8
            move 2 from 3 to 4
            move 1 from 4 to 1
            move 7 from 9 to 2
            move 1 from 6 to 3
            move 2 from 1 to 9
            move 20 from 9 to 7
            move 6 from 4 to 9
            move 1 from 2 to 9
            move 6 from 9 to 4
            move 1 from 4 to 6
            move 1 from 8 to 6
            move 1 from 4 to 7
            move 5 from 2 to 3
            move 2 from 6 to 4
            move 3 from 9 to 5
            move 5 from 3 to 5
            move 3 from 3 to 8
            move 3 from 5 to 6
            move 1 from 9 to 8
            move 5 from 4 to 5
            move 3 from 4 to 9
            move 1 from 8 to 2
            move 2 from 8 to 2
            move 11 from 5 to 6
            move 16 from 7 to 1
            move 2 from 1 to 7
            move 14 from 6 to 1
            move 11 from 1 to 6
            move 2 from 1 to 4
            move 4 from 3 to 4
            move 9 from 2 to 4
            move 2 from 4 to 8
            move 2 from 5 to 3
            move 9 from 4 to 7
            move 2 from 3 to 2
            move 1 from 2 to 7
            move 1 from 8 to 4
            move 4 from 1 to 4
            move 1 from 9 to 1
            move 7 from 4 to 7
            move 2 from 6 to 5
            move 1 from 8 to 6
            move 1 from 4 to 2
            move 10 from 1 to 6
            move 5 from 7 to 3
            move 1 from 4 to 7
            move 2 from 1 to 2
            move 2 from 2 to 4
            move 4 from 3 to 4
            move 18 from 7 to 6
            move 6 from 6 to 4
            move 1 from 7 to 4
            move 1 from 7 to 6
            move 11 from 4 to 5
            move 14 from 5 to 9
            move 1 from 8 to 7
            move 8 from 6 to 2
            move 2 from 4 to 5
            move 7 from 9 to 1
            move 6 from 9 to 7
            move 5 from 1 to 8
            move 1 from 3 to 6
            move 10 from 6 to 3
            move 1 from 9 to 6
            move 1 from 5 to 4
            move 4 from 3 to 8
            move 1 from 5 to 9
            move 9 from 2 to 3
            move 1 from 9 to 5
            move 4 from 8 to 4
            move 1 from 5 to 3
            move 5 from 8 to 7
            move 5 from 7 to 2
            move 3 from 4 to 1
            move 8 from 6 to 5
            move 1 from 7 to 9
            move 4 from 1 to 3
            move 2 from 4 to 6
            move 5 from 5 to 2
            move 4 from 6 to 9
            move 1 from 1 to 2
            move 1 from 5 to 6
            move 7 from 2 to 8
            move 5 from 6 to 8
            move 4 from 7 to 9
            move 15 from 3 to 9
            move 1 from 7 to 3
            move 1 from 5 to 3
            move 6 from 2 to 6
            move 1 from 5 to 2
            move 2 from 3 to 9
            move 1 from 6 to 8
            move 5 from 8 to 9
            move 2 from 3 to 8
            move 3 from 3 to 6
            move 11 from 9 to 4
            move 1 from 2 to 1
            move 2 from 8 to 4
            move 1 from 1 to 4
            move 7 from 4 to 7
            move 9 from 6 to 3
            move 4 from 7 to 8
            move 4 from 7 to 6
            move 19 from 9 to 4
            move 7 from 8 to 5
            move 5 from 3 to 6
            move 6 from 6 to 9
            move 3 from 3 to 5
            move 1 from 3 to 9
            move 8 from 4 to 5
            move 2 from 9 to 6
            move 3 from 8 to 2
            move 1 from 8 to 4
            move 1 from 2 to 5
            move 19 from 4 to 1
            move 2 from 5 to 7
            move 2 from 2 to 4
            move 13 from 5 to 2
            move 1 from 5 to 1
            move 2 from 6 to 9
            move 1 from 8 to 7
            move 9 from 9 to 3
            move 2 from 3 to 8
            move 1 from 4 to 2
            move 5 from 6 to 7
            move 1 from 4 to 6
            move 2 from 8 to 7
            move 7 from 1 to 5
            move 1 from 6 to 7
            move 10 from 1 to 8
            move 1 from 1 to 3
            move 1 from 1 to 2
            move 6 from 5 to 3
            move 4 from 5 to 3
            move 5 from 7 to 1
            move 3 from 1 to 2
            move 4 from 7 to 5
            move 8 from 3 to 6
            move 2 from 1 to 7
            move 4 from 5 to 8
            move 7 from 3 to 5
            move 3 from 7 to 2
            move 1 from 7 to 3
            move 12 from 2 to 8
            move 23 from 8 to 2
            move 16 from 2 to 6
            move 1 from 9 to 6
            move 7 from 5 to 7
            move 7 from 2 to 4
            move 2 from 3 to 8
            move 1 from 1 to 9
            move 5 from 8 to 1
            move 2 from 3 to 9
            move 2 from 7 to 1
            move 4 from 1 to 3
            move 4 from 7 to 2
            move 2 from 1 to 4
            move 11 from 2 to 9
            move 3 from 3 to 4
            move 1 from 9 to 1
            move 2 from 2 to 7
            move 4 from 4 to 8
            move 2 from 9 to 5
            move 2 from 5 to 7
            move 4 from 4 to 6
            move 1 from 3 to 8
            move 1 from 9 to 8
            move 4 from 4 to 2
            move 2 from 1 to 3
            move 1 from 8 to 4
            move 2 from 3 to 5
            move 3 from 9 to 7
            move 2 from 8 to 9
            move 1 from 9 to 6
            move 2 from 7 to 3
            move 2 from 8 to 1
            move 1 from 4 to 9
            move 18 from 6 to 2
            move 1 from 6 to 5
            move 1 from 5 to 9
            move 18 from 2 to 3
            move 1 from 8 to 7
            move 2 from 5 to 9
            move 1 from 1 to 4
            move 3 from 2 to 1
            move 9 from 9 to 4
            move 7 from 4 to 6
            move 2 from 7 to 3
            move 2 from 4 to 9
            move 7 from 6 to 7
            move 3 from 7 to 2
            move 7 from 6 to 3
            move 2 from 6 to 9
            move 24 from 3 to 9
            move 2 from 6 to 8
            move 1 from 4 to 2
            move 2 from 8 to 5
            move 31 from 9 to 3
            move 6 from 7 to 4
            move 35 from 3 to 7
            move 1 from 1 to 8
            move 1 from 5 to 7
            move 1 from 5 to 4
            move 1 from 3 to 9
            move 1 from 8 to 2
            move 3 from 1 to 7
            move 7 from 4 to 5
            move 1 from 9 to 8
            move 4 from 5 to 6
            move 2 from 5 to 2
            move 6 from 2 to 5
            move 2 from 5 to 7
            move 2 from 2 to 1
            move 2 from 5 to 4
            move 1 from 8 to 4
            move 3 from 4 to 6
            move 4 from 6 to 7
            move 1 from 5 to 2
            move 2 from 6 to 9
            move 1 from 6 to 4
            move 1 from 4 to 8
            move 2 from 9 to 6
            move 1 from 8 to 9
            move 34 from 7 to 9
            move 6 from 7 to 3
            move 1 from 7 to 2
            move 1 from 5 to 8
            move 1 from 8 to 6
            move 6 from 7 to 4
            move 1 from 7 to 3
            move 7 from 3 to 5
            move 6 from 4 to 6
            move 31 from 9 to 1
            move 3 from 5 to 7
            move 24 from 1 to 3
            move 1 from 2 to 4
            move 3 from 9 to 1
            move 14 from 3 to 5
            move 1 from 4 to 3
            move 1 from 9 to 7
            move 8 from 3 to 7
            move 1 from 2 to 9
            move 7 from 1 to 5
            move 3 from 6 to 8
            move 3 from 6 to 1
            move 1 from 1 to 3
            move 4 from 3 to 2
            move 4 from 2 to 3
            move 2 from 5 to 1
            move 9 from 7 to 4
            move 1 from 6 to 5
            move 1 from 1 to 7
            move 3 from 8 to 9
            move 5 from 4 to 2
            move 3 from 2 to 3
            move 1 from 2 to 3
            move 2 from 4 to 1
            move 2 from 9 to 4
            move 1 from 9 to 3
            move 1 from 6 to 1
            move 1 from 9 to 6
            move 25 from 5 to 4
            move 4 from 1 to 9
            move 2 from 3 to 7
            move 2 from 6 to 9
            move 2 from 9 to 5
            move 6 from 7 to 1
            move 5 from 3 to 6
            move 10 from 4 to 3
            move 10 from 4 to 8
            move 2 from 4 to 2
            move 5 from 1 to 9
            move 2 from 6 to 4
            move 6 from 9 to 6
            move 7 from 6 to 4
            move 3 from 9 to 4
            move 3 from 2 to 4
            move 4 from 3 to 8
            move 2 from 5 to 3
            move 10 from 4 to 9
            move 4 from 9 to 7
            move 5 from 9 to 5
            move 4 from 5 to 1
            move 9 from 4 to 6
            move 10 from 1 to 3
            move 1 from 5 to 4
            move 3 from 4 to 5
            move 2 from 5 to 7
            move 1 from 7 to 3
            move 1 from 6 to 9
            move 11 from 8 to 6
            move 14 from 6 to 5
            move 1 from 4 to 7
            move 7 from 5 to 3
            move 3 from 5 to 4
            move 2 from 9 to 5
            move 2 from 4 to 3
            move 2 from 7 to 4
            move 11 from 3 to 9
            move 2 from 8 to 2
            move 2 from 2 to 3
            move 1 from 8 to 2
            move 1 from 2 to 9
            move 3 from 4 to 5
            move 2 from 6 to 9
            move 1 from 1 to 8
            move 10 from 9 to 7
            move 2 from 9 to 3
            move 23 from 3 to 9
            move 4 from 6 to 4
            move 9 from 5 to 6
            move 1 from 5 to 3
            move 5 from 6 to 7
            move 1 from 1 to 7
            move 1 from 3 to 9
            move 4 from 6 to 7
            move 1 from 8 to 7
            move 1 from 7 to 5
            move 1 from 5 to 1
            move 12 from 7 to 6
            move 9 from 9 to 3
            move 6 from 6 to 4
            move 8 from 7 to 3
            move 3 from 7 to 4
            move 6 from 3 to 1
            move 10 from 4 to 8
            move 10 from 8 to 7
            move 2 from 3 to 7
            move 9 from 3 to 8
            move 2 from 6 to 3
            move 10 from 7 to 1
            move 3 from 4 to 6
            move 5 from 8 to 5
            move 3 from 5 to 7
            move 1 from 3 to 2
            move 1 from 2 to 6
            move 6 from 9 to 1
            move 12 from 1 to 3
            move 3 from 6 to 9
            move 3 from 1 to 7
            move 1 from 3 to 2
            move 7 from 1 to 7
            move 1 from 2 to 7
            move 2 from 6 to 4
            move 1 from 4 to 5
            move 3 from 8 to 7
            move 2 from 6 to 3
            move 2 from 6 to 1
            move 1 from 3 to 8
            move 5 from 3 to 4
            move 2 from 8 to 5
            move 14 from 7 to 4
            move 1 from 3 to 2
            move 1 from 3 to 7
            move 7 from 7 to 4
            move 2 from 5 to 3
            move 2 from 1 to 4
            move 9 from 4 to 6
            move 1 from 1 to 2
            move 4 from 9 to 4
            move 8 from 9 to 3
            move 2 from 2 to 7
            move 13 from 4 to 8
            move 4 from 4 to 1
            move 2 from 7 to 6
            move 12 from 3 to 2
            move 11 from 2 to 9
            move 6 from 4 to 9
            move 18 from 9 to 4
            move 2 from 1 to 6
            move 6 from 8 to 1
            move 13 from 6 to 5
            move 8 from 4 to 5
            move 1 from 2 to 9
            move 8 from 1 to 4
            move 7 from 4 to 8
            move 4 from 3 to 5
            move 10 from 8 to 5
            move 13 from 5 to 8
            move 12 from 4 to 5
            move 2 from 9 to 8
            move 29 from 5 to 9
            move 24 from 9 to 2
            move 23 from 2 to 4
            move 5 from 9 to 2
            move 7 from 5 to 7
            move 1 from 5 to 1
            move 7 from 4 to 8
            move 14 from 8 to 1
            move 5 from 2 to 6
            move 16 from 4 to 7
            move 8 from 1 to 6
            move 1 from 2 to 8
            move 20 from 7 to 6
            move 11 from 6 to 4
            move 3 from 1 to 5
            move 3 from 4 to 3
            move 8 from 4 to 9
            move 8 from 6 to 1
            move 2 from 1 to 4
            move 3 from 5 to 2
            move 12 from 8 to 2
            move 1 from 7 to 1
            move 1 from 3 to 5
            move 1 from 7 to 8
            move 1 from 7 to 3
            move 12 from 2 to 8
            move 13 from 6 to 4
            move 2 from 1 to 9
            move 3 from 2 to 6
            move 3 from 9 to 7
            move 5 from 9 to 1
            move 4 from 6 to 4
            move 2 from 3 to 6
            move 1 from 5 to 9
            move 1 from 6 to 7
            move 9 from 1 to 5
            move 11 from 8 to 3
            move 1 from 6 to 8
            move 3 from 7 to 1
            move 1 from 8 to 7
            move 2 from 8 to 9
            move 7 from 1 to 2
            move 17 from 4 to 7
            move 1 from 8 to 6
            move 4 from 7 to 2
            move 4 from 9 to 7
            move 4 from 2 to 3
            move 1 from 1 to 4
            move 2 from 4 to 3
            move 9 from 5 to 4
            move 1 from 6 to 8
            move 6 from 2 to 1
            move 5 from 1 to 9
            move 9 from 4 to 3
            move 1 from 4 to 6
            move 2 from 9 to 7
            move 1 from 1 to 5
            move 1 from 2 to 7
            move 1 from 8 to 9
            move 1 from 6 to 8
            move 1 from 5 to 4
            move 1 from 8 to 7
            move 23 from 3 to 7
            move 36 from 7 to 6
            move 33 from 6 to 1
            move 1 from 4 to 8
            move 7 from 1 to 5
            move 1 from 8 to 1
            move 3 from 7 to 2
            move 24 from 1 to 3
            move 7 from 7 to 3
            move 3 from 5 to 1
            move 4 from 5 to 3
            move 1 from 9 to 8
            move 2 from 9 to 6
            move 1 from 8 to 5
            move 3 from 2 to 5
            move 30 from 3 to 5
            move 1 from 6 to 7
            move 6 from 1 to 8
            move 7 from 3 to 2
            move 1 from 7 to 5
            move 2 from 3 to 2
            move 2 from 6 to 8
            move 1 from 6 to 1
            move 7 from 5 to 8
            move 8 from 8 to 7
            move 20 from 5 to 8
            move 2 from 9 to 7
            move 8 from 2 to 1
            move 7 from 7 to 3
            move 1 from 2 to 1
            move 3 from 7 to 9
            move 4 from 8 to 3
            move 5 from 5 to 6
            move 1 from 5 to 9
            move 4 from 9 to 4
            move 1 from 5 to 9
            move 2 from 3 to 6
            move 1 from 5 to 8
            move 7 from 6 to 3
            move 1 from 4 to 1
            move 7 from 3 to 2
            move 3 from 3 to 5
            move 2 from 4 to 7
            """;
}