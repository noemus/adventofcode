package done.advent2021;

import util.LineSupplier;

import java.util.Objects;
import java.util.Scanner;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.lang.System.lineSeparator;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.joining;

public class SeaCucumber {

    static Cucumbers cucumbers;

    public static void main(String[] args) {
        try (Scanner in = new Scanner(INPUT)) {
            System.out.println();

            cucumbers = new Cucumbers(
                    Stream.generate(new LineSupplier(in))
                          .takeWhile(Objects::nonNull)
                          .map(Cucumbers::row)
                          .toArray(Cucumber[][]::new));

            System.out.println(cucumbers);
            System.out.println();

            while (cucumbers.canMove()) {
                cucumbers.step();
                System.out.println(cucumbers);
                System.out.println();
            }

            long result = cucumbers.steps + 1;

            System.out.println("Result: " + result);
        }
    }

    static class Cucumbers {
        private final Cucumber[][] cucumbers;
        private long steps = 0L;

        private Cucumbers(Cucumber[][] cucumbers) {
            this.cucumbers = cucumbers;
        }

        void step() {
            moveEast();
            moveSouth();
            steps++;
        }

        static Cucumber[] row(String line) {
            int row = Cucumber.nextRow();
            return line.chars()
                       .mapToObj(c -> Cucumber.create(row, c))
                       .toArray(Cucumber[]::new);
        }

        public boolean canMove() {
            return canMoveEast() || canMoveSouth();
        }

        private boolean canMoveEast() {
            return cucumbers().filter(Cucumber::isEast)
                              .anyMatch(Cucumber::canMove);
        }

        private boolean canMoveSouth() {
            return cucumbers().filter(Cucumber::isSouth)
                              .anyMatch(Cucumber::canMove);
        }

        private Cucumber get(int row, int col) {
            return cucumbers[row][col];
        }

        private void set(Cucumber cucumber) {
            cucumbers[cucumber.row][cucumber.col] = cucumber;
        }

        private void clear(int row, int col) {
            cucumbers[row][col] = new Cucumber(row, col, Direction.EMPTY);
        }

        private void moveEast() {
            cucumbers().filter(Cucumber::isEast)
                       .filter(Cucumber::canMove)
                       .toList().stream()
                       .map(Cucumber::move)
                       .forEach(this::set);
        }

        private void moveSouth() {
            cucumbers().filter(Cucumber::isSouth)
                       .filter(Cucumber::canMove)
                       .toList().stream()
                       .map(Cucumber::move)
                       .forEach(this::set);
        }

        private Stream<Cucumber> cucumbers() {
            return rows().mapToObj(row -> cols().mapToObj(col -> get(row, col)))
                         .flatMap(identity());
        }

        private IntStream rows() {
            return IntStream.range(0, Cucumber.rows);
        }

        private IntStream cols() {
            return IntStream.range(0, Cucumber.cols);
        }

        @Override
        public String toString() {
            return "--- step " + steps + " ---" + lineSeparator() +
                    Stream.of(cucumbers)
                          .map(row -> Stream.of(row)
                                            .map(Cucumber::direction)
                                            .map(Direction::toString)
                                            .collect(joining()))
                          .collect(joining(lineSeparator()));
        }
    }

    record Cucumber(int row, int col, Direction direction) {
        static int rows = 0;
        static int cols = 0;

        static int nextRow() {
            cols = 0;
            return rows++;
        }

        static int nextCol() {
            return cols++;
        }

        public static Cucumber create(int row, int c) {
            return new Cucumber(row, Cucumber.nextCol(), Direction.valueOf(c));
        }

        Cucumber move() {
            cucumbers.clear(row, col);
            return switch (direction) {
                case EAST -> new Cucumber(row, (col + 1) % cols, direction);
                case SOUTH -> new Cucumber((row + 1) % rows, col, direction);
                case EMPTY -> this;
            };
        }

        boolean canMove() {
            return switch (direction) {
                case EAST -> cucumbers.get(row, (col + 1) % cols).isEmpty();
                case SOUTH -> cucumbers.get((row + 1) % rows, col).isEmpty();
                case EMPTY -> false;
            };
        }

        private boolean isEast() {
            return direction == Direction.EAST;
        }

        private boolean isSouth() {
            return direction == Direction.SOUTH;
        }

        private boolean isEmpty() {
            return direction == Direction.EMPTY;
        }
    }

    enum Direction {
        EAST,
        SOUTH,
        EMPTY,
        ;

        public static Direction valueOf(int c) {
            return switch(c) {
                case '>' -> EAST;
                case 'v' -> SOUTH;
                default -> EMPTY;
            };
        }

        @Override
        public String toString() {
            return switch(this) {
                case EAST -> ">";
                case SOUTH -> "v";
                case EMPTY -> ".";
            };
        }
    }

    @SuppressWarnings("unused")
    private static final String INPUT1 = """
            v...>>.vv>
            .vv>>.vv..
            >>.>v>...v
            >>v>>.>.v.
            v>v.vv.v..
            >.>>..v...
            .vv..>.>v.
            v.v..>>v.v
            ....v..v.>""";

    @SuppressWarnings("unused")
    private static final String INPUT = """
            ...>..>..>vv>.>v..>..>>v.v...>.v.>......v.>>..>>v.v.vv.>.>.v..>..vv..>.>>vv..v>v>vv>.>...v.>.>.>v>v>.>>v..v..v..>...>.v...>.>>>...>v.v>>v>v
            >vvv>vv..>..v...>>.v>>>v..>v..vv>vv..>>.vv..>...v...>..>.v.v>.>..>..>>.>v...>.v.v>......>v..v.v>......>.v.v..>...>...>.v.>>>v>vv....v.>v.>.
            .>.v..>..v.vv>.>>.v.>...>..>>v.vv.v.>>..vv.>vvv>...>.>.......v.>>>.v>vv>>.....>.>vv..vv.vv.v>..>..v.v.vvv>..vv>.v.vvvvv.v.>..vv.>v>v.>..vv.
            .vvv...v.....v..vv>>.>.>..>v..>v.v..>>>vv>.>vv>...>v>.>......v>>.v..>.v>...v>...>.vv..>>.>>.v..v....>..>vvv..vv.v.>..v..v.>v.>...v>>.>.>v..
            >>>..vv>.>..>>.v.v...>>...v>vv.....v.v..v.vvvvv....>..v>.>.v.>...>>......>.vv.v.v.v>>.>..>>.vv..>.>.v...>>>v..v>..v.vv>>..v..>.....>vvv...>
            ...vv.>..>....v>>>>.v...>...>.>vv....>.....>v>v.v...>>..........>...>>.....v>v>..>v>..........>.....>v>..>v>v.....v>>>v...v>vv.v>v.v>v..v>v
            >..>..>>.v.....>.v.v..v.vv>v.v......>v>v.v>...>>...v>.v.v>.>......>>>>.>v.>>vv>>vv>.>.>>.v..>v.......v>v..v..>vvvvv....>...v>vv.>v..>v.vv..
            ..v>v>vv.>..>v.v>.>v.vv...>.vv>.v.v>.>vv.v>.>v.v..>>..>v.v>...>>.>.....v..v.>.>v>.>>>..v>v.v..v>.....>>.....>.v.v.v.>vv.>v.>v>v..v>>>>.v.>.
            .>vvv>.v>v>vv>>.vv>.vv>v>....>..v.>vv..v>.>.>.v..>.v...>>v>.v.>.>>.>....>..>...>.>...vv>.v...>...v>..vvv...v.......>>v.>v.v>v>v.v>.>.>v..vv
            ...>>vvv..>v.>v......vv.>vv>vvvv>.>>...>vv>v>>.>vv..vv..v....vv.vvv>.v.v>v.>>..>v>.v>>....>..vvv......v>.>vv>..>>.v>..>.v>.v.......v.v..>.>
            ...v>.v..v>>v.v.vv>.vvv.>vvvvv..>.....v.......>.v.v.vvv.v.v...vv..v>>v>>>>..>...v>>>>.vv.>>v.>........vv.vv.vv>>vv.>..vvv...>.>......v.>..v
            v.v>vv.>vv>vvv...vv.vv...>...>>...vv.>.vv.v>>..>..>......vv.....>..v..v.>v>.>>vv...v..>.v.v.....vvv.v>.v.vv......v....v....v.>>>.v..>v>....
            ..v.>..>>>v.....>vv..>>.>>...v.v>v....>..v.v>..v...v..vvv.v>....>>v>.>.>...>v..>..>v.>>>v.>>.>vv>.>v>.>.>>.v.>>..>..vv.v>v....>>>vv..v>....
            .>...v>.vv>>..v.v..v.v.vv..v>.>>v..vv>.....v>....>...v......v>.>v>.>..>..>>v.>>>.vv...>>.v>.>>.>.>v>>......v.v...>v..v.>>>v>v..>.v.vvv>>.v.
            ...v....vv..v.>>.>>.>..>.vv.>.>>>..>..vv..v>.>>v.>..v....vv>....v....v...v.v..vvv.v.v>.vv>.v>..vv.>v.vv>vv....>...>........>...>.>.....vvv>
            .v.>........v..>.vv>>vv......v..vv..>>>>>v...v>>v>..vv.>..>v>..vv.vv.>v.v.>.>...v..>>>..vv>.v.v>...>>..>.v>v>>v.>....vv..vv.v.....>v>.>.>.>
            .>.v..>v....v>..v>v....v>vv....v>>..vv.>...>.>..>..v.v.>v...vv..v.>....v>>.>vvvvvv..v.>..v.v>..v>>...>.v.>>>..vv.v.>....v>.>v>>.vv.>.v..v..
            >>..vvv...v..v.v...>.v.>..>...>.>v>.v.v>v..v.v>..vv>>>..vv..>v.v.>..vv....>v.>>.v...v>v.v>...>v>v......>.>>.>.v.>.>v>>vvv.>v..>...>>v>v..>.
            >..v>.v.>v>vvv.v...v>v>..v>.>...v.>..v....>..>.....>..>v....v..vv.v>>v>>...vv...>vv>>.>.>.>.v.....>..v>>.v.>v>v>>..>v>....>.>>.v>.>v>.v.vv.
            ..vv.>....v..vv>...v>>>v>v>.>.v.>>.vv.v.vv...>.>...>.v>..>v.>v.>>v.v>.>v..v.>>..>.v.>>vvv...v..vv.vv.vvv>...>.v.>>v..vv.>..v..v>..>v>>.....
            ..v...v>...>>.>.v.v..>..v..>..>>>>vv.v.......v>..v....v.v..v>v.>.....v..vv>.vv..v>>v.>>>..>v.>..vv>.>.v>...v>.v.v>.>..>.>..>..>..v...v>.v..
            vv.>v.>.vv>.v.v.vv..>v.v>>>>v..v.v>v>>>>>..>vvv..>.v....>>v....>.>..v>.>...v>vv..v>.>v.v..v...v.....>>>>v.vv..v.v..>v>..v>>>..>>vvv.v>.>vv.
            v.>..>>.v>v..>v..vv.v>>vv>..>...>vv...>>..>>v>>..v.v..vv.>....v.>.......>v..v...>v.v>>....vvv..>....>>v>..v.>.v...v..>.v>...>v>v>.>v>.>v..v
            v>...v>..>...>>...v....vv>>>.....>.>vv.v..>.v.v.v...v>v.v.v.....vv.>>.vvvv..v...>...vv>...>v.v>.>v.>>>>>v>.>>>>......>.>>.>>v.>.>>>>vv.>..>
            >.>..v..v>.....>...v..>.>>.>v>...>v.>>..>..v.>>.v>>.v>>>..v.v.....v...v...v..v...>.v>.vvv...>>..v>>.>..>.v...>..v>v..>...vv>v.>..vv>>..>.v.
            .v.vv...v>..>>.......vv>..v.v>v.>..v....v.>v.>>.v..v.v..vv.v.v.>>...v>vvv>>..>v>....v...v>>vv...v>...v>.v..vv>..>..>.......>>..>v>>vv..v.v>
            ..vv>..v...v.>>vv...>.v.....v>>.........v>v>.>...v>>.>.>v>.>.>..>>>.v.v..vv...>..>>v.v..>..>.>>>.v>..vv.>>v.>>v>..v.v....v...>..v..>>...vv.
            .v...v.>v......>.>>v>...v.....vv..v.>.>>.>v..>...vvv.v>....>v>>.....>v...vv>v..>>.>v.>>.v..v..>v>.....>..vvv.vv.>.vv....>....>.>>.....v>...
            ..>.v......v.>....>vv..v>>v.>.v...>>...>.....v.vv>>.vvvvv.v...v..v.vv>.....vv.vvv...vv>....>.>v...>>>v>..v.v>.v>..v.>..>.>>..v..>..>.>.>v>>
            v...vv..v..v>>.vv.>>.v..>v>....vv>v>.>.v>v.>..v.>....v>>.>...vv>.>.>.v.vvv...v....vv>>v...>.vv.v..v.v.vv.v..v.v...>>>.>.>>>>>..v>v>.v.>v.>.
            v>.v.v>v..v>vvv....>vv>vv..v..>vv.>v>v..>..vv>>v..v..>.v...v.>>...>.......v>.v>...>v.vv.>...>.v.>>.>>.>>.v>.v....>..>v...>..>.>>.v>v>..>.v.
            v>v.>.>>..>.v>v.vvv.>>>>>>.>....v.>>>.>v.v...>vv.>>>v..v>.>>v>vvv...v>v.....v.v>.v.>.>.vv.>v.......v..>v>>.v.>.>.v..vv>.>>>..>v>vvv.v>...v.
            ....v..v>...v>.>v.vv..v>>v.vv>..>.........v..>>.>.v....vv..v.>v..vvv.v....>..>.vvv.v>..v.>....v>.v.>v>v.v.>..v..>vv.....>>>vv.v.>v>>v>>>..>
            ...>.vv>..vv.>.v.>>v>>v...>..>.>>>>.>vv.>..>..v.>.>vv.v>..v>vvv>.vv....v.v.>.>.>..vv....v.vvv.vv>v>>>.......>v.>.....>..>.v>.>.>.v.>v.>>..v
            ..vv.>...>>vvv>..v..>..v..>vv.>.vvv>.v.>...vv>.....>.>vv.vv>.>..>vv.vv.vv>.vv...vv.vv.vv..>.>.>.>....>.>v.vv>.v.>......v>v>.vv.>......v.>..
            v>vvv>>>.v..>v.v>v.v>>.v>.>>v>.>.>....vv>v.v>......v>..>..>>v>..>>...>v.......>..v.>.>vv.>>>...>>...v.v....>>..>.>.vv..>>vvv...>vv>>..>...>
            v.vv.v.>.v..v...>..>.v...>v.>.>..v.>.>v..>>>.>...>vv.>>..v.>...>v>..>v....v..>..>>v>>..v..v...v.v.....>v>...v.>vvvv.>.>.>.v..>v>>v>>>v.>.>>
            ..vvv...v.v.>v...v>>....>v>>.>..>.>v>v...v.v.......v>vv...vvvv>..>.v...>.v>..vv>>.....>>>v.>..v>.>>.vv..v.v>.>.......>>.v..v..v>v>>>>.....v
            .v>vv.>.>>.>v..>v....v>..>>>vv..>..v.>vv>>.v>v.......>>..v.>>>vv..v..vvvvvv.>>v..v.vv>.vv....vv..v.v.>v>..v.>>>>..v.v...>>v>......v.>.>....
            v.>.vv>..>v.......>>>.>.....>>.>>.>.v>>...>v.v...v.>.>vv>.vv>.>v.v.>.>>v.>vv.>>vv...>v.v....v.v.>>.>>>v>.>.>>v..v...>vv>...>>v.....>>v>v>vv
            >.v...v..v>v..>v..v....v>...v.>.v..>v.v>>....v>...>vv...v>.vv...>>.v.>..v.v>>.>v.v>.>.v..>.v.....>v.v>v.>v>v.>v>.>v...>.>>..>>v...v.v>.v.v.
            .>vv..v..>.v.>>.>..>...v..>>.vv.v>.v.>v>>v....v.v.>...>....>>>v..>>>..v...vv...v>>v.v..v..>>..>v.>>...vvvv..>v.v.v.v.>v>>.v>>.....vvv>..>.>
            >.>.>v>>.v>v...>.v...>...v..>>....>v>.vvv>..>...>......vv..>.>.v.v..>>..>.>.vv.v>>vv.v>.>...>>.v.>...v....>>v...........>>.>.v>.>>v.>v.vvv>
            ...vv.>....v.vvv>v.....>.vvv>.>.v..v.v.v...v.>>...>vv.>.v.>.>..vv.vv>v>>....v.vv.vv>v...v>v>.>>v..>..>vv..>...>>v....>>...vvv>>>>>.v.....>.
            >....vv..v..v>.....vv.v.>..v>.>v.>v.v>.v..>...>>.....>..>>..>.vv>..v>.v...v.v.>>.>v>v..v..vv>vvvv>>vvv....>..>...>..v...v>>.v.>.>..>v>.v.v.
            ...>.v>>..vv..>>>..vv.v>v.v..vv>......v....>.>..v.>...v.v>.v>v>v..>>v..v>v>v...v>.vvv.>v..v..vv>.......>.v>v>>vv>>..>..vv>>.v..vv>>...vv>>.
            >...v...v.v.v>>v..>>..vvv.v...>..>v...>v>>>>>..>....vvvv....vv.v.>.v>>.....v..>>.v...v.v.>...v.>v>>vv..v>v....>>vv...>v..>>>.v.v>..v....>.v
            .>.v>vv.v>vv....v>..v.v>v.v>v..v>v.vv>>..v>..vv.....vv.v..>v.>.vvv..v..>.v>..v>.>..>.v.v>>..>.v>v>>>v.v...v.v>.v......v.....vv....>.....>>.
            >...>>>...>v>v..v>v....>...vv>v..>..>..v>.>>v.vv.>>>.>....v>.>vv>.>.>..v....>>....>>v>.v..>>.>..v>>.vv..v>....v.>.v.>v...>vvv..v>>v....v..>
            >.>>.vvv>v>>.v..vv>...>.v.v>v.v....v>>.v..>.>...vvvvv..>v>.v>.>>.>.v..>...>...>.>.>>.>v.v..vvv...>.>>v....>.v....vvvvv.>>..v>.>vvv.v>v.>>>v
            v>..vv......>v...vv.>.....>.v>..>..>vvv....v..v.>>v>....>.>.>vvvvv.>...v..v.vv.>........>>v...........v>.>>vv>>>>.......>......>vv..v.....>
            .>........v.>>...v........>>>.>.v..>.v..>.>..>>.>.....v>>.>.v>.>v.vv>>>>v>v...vv.v>.v....vvvvv.>>.>>>..>..v>>>.>v.>>..v....>..>>>...vv...>.
            .v..>.v..>..v.v.>>.vv.......>..v>...>v...v..>..>v.....v.....v.v>v.v..>.v..v>>.v>>>..v>..>...>>>>..vvv....v>v..>vv..>>.>vv.vvv>>>....v>.vv.>
            .>>>v.vvv..>v....vvv.>>>...v...>...vv>>>v>>>vv.v.v>v..v>v>>..>...vv.>>.vv.......vv.v>>.>v>.v.>v..>.>.v.....v>.>>.>...v...>>>>.>v>.vv.>....>
            >>.>>.v..v.>>.v...vv...vvv.>.>...v>..>..vv.vv...v>..>v>.v>>..>.vv.>..>..v..vv.>>>v..>>.......>v.>>vvv....v>.v>.v..vvv>.>>>.v>>...>..v>>.v.>
            v>>.>vv....>vv.>v..>v.v....>v>...v>vv..v..>...>v>.>v>....v......>vv..v...>v....>>.>>.v..v.vv....v.>v>.>v>.>v.>.>>>>..v....>.>.....>v..>.>..
            >>v.v.>>...vvv.v..>vv.v>>v..v.>>v.v.>.>>...>vv.>.v.....vv.>>...>...>v..>>>v.>.v...>..v.v>v>.....v..>..v..>v.v.>v.>>.v.>v.v.v>>.v......v.vvv
            >..v>.vvv.>>...>..>v....>v>........v..v...>...vv....vv..>>...v.....>v.>.>v...v..v>vv>>.v....>vv..v>v..>.....v>.>v.v.vv.v>......>v....>>>v>.
            >..>>v..>>>>......vv>v.>..>.>.>>...>...vv..v.>>.>.>v.v.v..>..>v>....>.>..>vv.>>.>.>.v...vv....v>..>.....>v>....>.>.v....vv..>..v>>.vv...v>.
            v.vv..v>>vv>.v.>.>..>..v..>..>>>v.>..vv>vv>.>>v>v>>v>v.>..>vv....>....v.v.v>>.vv.>.>.>>..>>.vv>.vv>..v>vv.v...>v.>v>v.v....>v...vvv.>.v...>
            v>v.>.vv...>v>.>>vv>v...>..v...v.>v...>v..>>..vv..v>>>.>v.v>....>>>..vvv.v>vvv>>.v.>>.v....>.v>...>v...v>.>.vv>.>v..>v.v.v>.v.>......>>...v
            v>.v.v...v.>..>>>...>>vvvv....v.....>..>.>.>....>.......>v>..>..>..v>...>v>v.vv.v>>.v>..>v>.v..v.>>..>.>>>v...>>>>.v..>>v..v.>v.v....>>.v..
            .v>v.>...v..>..>.>...>..v.v..>.>v.>>v..vv..v..>v..v..>>...v..>..v..vvvv.>.v>vv>vv.>..v..>.v..v.>.....v..v>.v.v..>...v..v.vv..v>>>..>.>vv>..
            .v..v.>....>v.v.>...v...>.v...>...v..v..>.>vv.>v.>>>...vv..>>.>...>.v>>v.>>>>..v...>...>.......>.>....vv.v.....v.>>v.....vv>...>...v..v>>.>
            ....>.v.>v...>..>..v.......v..v.vvv>.>v.>..v>v.>>>vvvvv....>vv...>vv.>.>.vv....v..>>>>>vv..>v>>v>>>>.v..v...v>>.>v>vv..v>v.>vv.>..>>>.v..vv
            .>..v.v...v.>.>.vv.v>>.v>..v...>>.v.>>v>..v>..>...>vvv>>v.>.v>>>v..>.v>...>>>...>.v..>>..v.....>v....v>.>>.v>v...>>.>>>...>vv.vv...>>>>.v.v
            ...vv.>.vvv..v.>>.......>v..v>...v>.v>.>..>vvv..vv>v.v..v>.....>.v.>>....vv>.>...>.v...>>.v.v>.>v.vv.v>v>>..vvv>>..>v....>>v..>.>..vvv>.>.>
            ..>.>>...>.....v>v>...>.>v.>..vv>..vv...v..>...>...v>>...>v...>..>v..v.>v....v.>v>.>.vv.....>v>vv......v..>.>.vvv..v..v....>...v>v.vvv...>.
            >..v...>v.>......>v.v>...>>...>>v>..>.>v>.>v..v>>>>>......>..>>....v.v..>.>>v.>v...>vvvv.>>v.>.v>>.vv>>...v.>.>.v>v.v..>..v>.....>.>.>>...v
            .>>.v...>....vvv.v..v>...v..v....v>v.>>>..>.>.....>..v.v...>v>..>v>.v.>>vvv...>.vvv.>v....v...v>...vv..v..v>..>.vvvvvv>>>>.>>.v..v..>..vv>>
            ...vv.v....v>>v..v.>.vvv>>v...>...>>.v>.v.v>v.>vv>>vv.v>>.>.....>>v.vv...v.vv...v.v.>.>v.....>.vv..>v....vv>.>.v>>.v.>.vv.>...vv.v..v..v.>>
            >v..vv>.v>>v>.v....>.v..v....v..>..>>v..>.....v......>>.vv>>v>v...>.v..vv.v>v.v>...v..>>vv.>.v>>vv.vvv...>..v>.>v>.>..>v..>>>.......v..v.>.
            ..>...vv>.>>>vv......>.vv>v..>.>...>...>.>>.>....v>...>.>vvv..v.v.>v...v>..>>>v>v...v...>v.>.v..>..>...>.v>v>>.v....v>.>v>...v.vv>......v.v
            >..v......vv.>>>.v.>.v>.v..>>v.v>>vvv.vvv>.v.>>.v.v.>.v.>>.>.>>.v.>vvvv>v.....>>..v>.........vv>vvv>v.v.>v>v.>v>vv>.......>...v>.v.v.>>.>vv
            ....v..vv...v...>vvv...v...>>.v>v>v...>.>.>....>v>>v.>v>vv>.>..v>.v....>>.v.vv.vvv.v...>.v.>v.vv.v>>..>.>.v.....>.>..vvvv.....v>>.>v.>...vv
            .>vv...>....>...v.vv..v.v>>.>.>>vvv.v.vv.v...v...vv.>.v...v.>v>>>..v.v>>>v..v..v..>.v..>>.............vv.>..v.>>..vv...>....>v>>>....>>v..v
            >v.>>..>>v>..v.>v...v>.>>>>>..>vv..>>.>v.>>v.v..>v.vv.v....v..v...>vvvv.>vv>..v>v.v...>.v>vv..v...>>v..vv>vv..>>v.v.vvv>.v..>>..v>vvv.>v>v.
            .>>v>vv..>v...>vv>..v.v....v.>>.v..v..>.v...>>>..>...>v>>.>.>..vv>.....>>.v>....v..>>>..v.v>..v>...>...v.>.v....v.v.vv>v.vvvv>>v.>...>>.v.>
            .v.v>.>vv.......>>.>.>....>v.>>.vv.>.>vv.vv..>v>.......v>...v..>vv..>>.>.>v>v.vv..v>..v.vv....v.v.>....>>v>>vvv.>.v>.v..>v>>v.v.vvv>v..>v>.
            .>v.....v..vv>>.v...>...>...>v>.......v>..vv.v>.>.>v.vv.......v>.>..>>>.........v.v.>.v..>>>.v..vv>.>>v>>>vv...>.>v>.>.>.v..>>>.vv.v..v.>..
            .>vv>vv>.....>..>...vv>..>.>.v.>v.>..>.>..v>v....v...>.........v.>......v>.>..>....v.>...v.>>.v>v>>.>.v>.v.v.v>>v>>..v>vv.v..>.v.vv>v...>..
            >v>.v.v..v..v..>..>vv.v>vv..v.>.>v......>.>.>...>v.>...>.v>..>>...>.v.>>.vv..vv>......>..v..>.>>vvv...v>>...vv....v....>>>>>v.v.v>>.>..>...
            >vv..v..>.>v.v......>>..v>>.>v>>v.v>..v>.>...>v.vv.v>v.vv>.v>>..>>>.v....vvv..v.>>.>>...>>.>v>v.>>....>v.v..>v.>v>....>.>v.>...v.>>.>..vv..
            v.>vv....>vv..>.v...v>.>>.v.....>.vv..v>v.>..>vv.>.v.......v.v.vv....v.>..vvv.>.vvv>..>.v.......>.v..>v>>>>.v.v>....v..>.....v.>v...>.v>v>v
            .....>>v>.v.>v.v.>v..>>.....v>.v....vv.>..>...>v>>v...v>vv>..>....>...v>..>v...v>v.>...>..>>....v.>v>.......v.>..v.....>>.>v.v......vvvv>v.
            .....v>vv.v...>..v..>...v.>....>..>>.v..>..vvv.v.vv>..>v..>.v...v..v>vv.v..>>v.vv.>.>...>v>>.>v..>.>....>vv...>>......>v..v>v..>>>.v>.>.>v.
            ..>v.>>v.>vv>.>..........v>.vvvv...>v.>.v>.v..v>..>>.v.>..v>.v>v.v.v>...v.>.vv>......>v...vvvv>vv.>>>.vv>v....v>>>vvv>v>.v.v>>.v>.v........
            ...v>>..>v....v.v..v>vv.>>....>...>>>...>.>..v..v.>.v.v>>.>vv..>>.....>.>.vv.v.v>v.>>........>v.v..>>v.vv.>v>v.>.>.vvv.....>.>.vv>>.v.....>
            >.v.vv.>>>.v..v>v.v>.>>.v.v..>>>.v..v..>.v.>vv..>>.v>...>..vvv>..>v>>>.v>>.v...>.>vv>v.>>vv..>....>....v...vvv>..>v..v.v>>>v>...v>>.v>v.>>v
            ....v..v...v..v.v.>>>.v.v..v.v>>v..v..v>.v..>v.v>>.v.vv..v>>..>.>.>>.v>vv.>>>>...>....vv.>......>.v>>.>v.>>.......v..>v>>>.vv.v>vvv.v>>.v.>
            .v..>..>v>v.v...v..>>..>vv.....v>>.>.vvv.vv..>>>vv>vv.>v>.>........>..>v.v>.>.>.>v...v.>..>>.v>>...vv>.vv>.>.v.>..v>.>v.vv>>vv.v>v.v..>vvv>
            >...v..>.v>v>.>vv>>v.v...>v...v>>.>.v>.>.>v>>..v.>...vv.v.vv..vv>.v..v.v.>>.v.>>vvv..>v.....v..vvv.>v>v...>..>v..vv.>v>vv.v>.v>.v.v.>.>..>.
            >v.>>v>>..>.v>.....>v.>...vv.>>..v>>...>vv>v..v..v..v..v...>.....vvv.>..>v...>v.....v>.....v>>.>>>vv>.>...v.>.v>v>.v>v>...v.....v>vv.>..v..
            >..v>v>..>v.>.>>..>...v.>>v.>..vv...>vv>vv>.>v..v..v>..>>.v>>..v>.>..>>>>>v>...v>>v..>.>.v>......>>.>vv>>.vv..>>...>>>>>..vvvv>>>..>>...vv>
            .v.v>..>v..>.>v....>vv>>>>v.>v>..>v....v...v.>..>v.......>>>v..>vv.v>..>..v>.vvvv.v.vvv.....vvv>v...v.>....>.>>v.>>...>..>>..>>vv...v>>>..v
            ..>>>..>...v.v...v.v...>v..vv.>vv....>>>vv>.vvv>>.>v.v.v.>v>.>>.>v..v>.>.v>.>..>v>>vv..v>..>>.>>>>...v.>>.v.>.>v..v.vvv..>v...>.>v...vvv...
            >vv.v>.v.v..v.v.>.....v.>>.....>>vv.>vv..>v>.v.....>v>>>...>...v...v.>...>v.v.>..>vv.....>....>>.>>.>v..>>>>v...>vv.v>..v>v..>v.v..v>>>.>>.
            v>>>.v.v>v>v.>.>.>>....v..>>v>...>.v>>.......>v>.>...>v>..>>..vv.vv...v..>..vv>...>....vv>>..>v>.>.......>..v>v>v....>.>>..>.v.v>>.v>.>vv.v
            v>>v>.vvv>>>..>v.v..v..>.......vv.>.v.....>.>>vv>..v.v>...v>v...vv>.>v>>.>.>v..>...v>vv.>>v.vvv.vv>..>v>....>v>vv...>...v.>v.>..>.>.>.v>..v
            .>>>..v>v...>.v...>....v.v>v>..>.v..v.vvv>>..>>>v>>>..>v.v.>>v..>.>.v.>v..v...vv>v....v>....>>.>..>v..>>v>.v..>.>......v.v..v>.v...>.v.>v>v
            v.v....v>vvv.vv.>v>......>.>...v.>.v....v>.vvvv.>>.>v.>vv..v.>vvv>.v..v>>.v.>vvv...>.>>>>>v>.>v.v..v>.v>vv..>.>.v.>....v.v>>.vv...>>>v...>.
            >.>v>v.v.>.v.>.>>.......v...v..v.>>>v.>vvv..>...>>.v......v.vv>..>>.vv.v>.vv..vv.v.v>>...>.>.vv.>..v.>vv..v........vvv>v>v.vv....v..>.>v...
            .>v....>v.>>>.v..>...v>...>.>v...>>v>v>....v>.v.v..vvvvv.v>>....>vv.v..v.>>.v>>>>>>.>vv..v>.....v.v..v.>v..v.....vv...>v>.v>.v...v..v.v.>vv
            .v.v..>v.v...>>....>>..v..>>v.>>vv>>>......>v...v>>>.>..vv>..>.vvvv.v..>.>v....>..vv....v>>>.......v..>.v..>v..v>.vv.....>.>>>>>...v.vv.>.>
            v.>.vv>>.>v...>..v>>.v...>.>>..>>..vvvv.v>>vv>v......vv.>>vv.....>..>..v...>>..v>.>v.vv>v>v..>>>>...v>v...>>>>>v>>.v.v>.>v.>>vvv.>......>>>
            ...vvvvv.vvvv>...vv...>..>.>>...>.>.v>v.>.vv>vv.>>vv.>vvv...>.v.>>>v.....v...v.v>v......v..v..v.>>>vv...>.v>.v.>v.>...>v>.v>.v.v>.>v>vv...>
            .>..v.....v.v.>...v>v.v>v...v..vv>vvv>>>.>.v...>vvv.>vv.v....v...v...vv.....>..v>..>v.>v.v.>v.....>v>v....v.>.v>.>.v>>.v..v...v>>...v>.>.>v
            .vv>v....vv.v.v>.>v..v.v>>.>.vv.>..v.>>.>....>...>vv...>.....>..>vv..v.....>v...v>....v..v...vv.......v.v>v>v>>..>v...v>.v.>..>v.>>..>vv.v.
            v..>..v.>....vv.vv..v.>.>v>..v.........v.>.>.>.>>>>>v>>.......>>>.....v.>......>vv>vvv..v>.>...>>.v...>>.>.>....>.vvvv.....vv..vvv..vv.>>.>
            .>>..v...>..v.>..vv.>>...>.v..vv.>..v..>.vv.>v..>>...>.>>>.v>>v..>>....>>v....>.>.v..>>.v.vv>..v.>>.v..>.>v.>.vv..vvv..v.v>..v..>v.vv.v>v>.
            ..>>.v...vv>>.>v...v>v..>.>.v>.v>vv>......>..>.v..v>vvv>.v>.>v.>.>v..v.v>v..>>.>.>v>v..v.v.v>.>...>..v...>>..v>.>..v......>vv.v.v>v.>.....>
            .v.>>vv>>..>.v..vv..>......vv>v.>v....v>>..v..vvv>v.>v.v.v.>>.v.>>..v...vv..>>.v..>v>..>vvv>vv>vvvv>...>v.....vvv>v>.v..vv.>>..vv.vvvv..>.v
            v>>....vv>.v.v.>.>.v.v.>v.>>.v>v>>.>>..>.>>.>vv...>..v....v...v.v..vv.....>vv>...v.v.......>.>.v>.>v..>..v...>>.>>>>>.v.>...>>.>.>v.v...>>v
            ..v......>.....>.>v..>vvv..>v..v.>.....v....>.>.>v.v>.>vv>.>..v>>>>.>>v.v.v.>.vv>v..>v.>v..vv>.v>v...v..v.vv>..>..vv.>v...v>.....>.v......>
            v>>....v...v.vvv.v..>.v>v...v>......>.v......>..v.>..>>v........>.v..v.vv.vvv....>v>vvvv....>.vv.>>.v>.v...>.v.v.vv..v..vv>..v.vv.v>...>.>>
            v.v..vv>v>.>>..>>..>v>v..v...>.v.>.>>...>.v..>.>>.>>.v.v...v.>v.>vv.>>v.>.>>>.v>..v.vvv.>...v>.....>v.>>.v>>..vv.v>.v..v.>.>.>.>.>v...vv>..
            >..vvvv.>.vv.v>.v>vv.....vv.vv>.>..>.vv.>.>..v.....>>>>.>.>>>v.vv>>vv..v.>>>>>.>...v.v..v>>>.v.>>.......vvv>v>.>..vv..v.vv>>v....>v........
            ...>>.>.v..>v..>>..v.v>>..v.>>...>.v.>>..v.v.vvvvv>.v>.v.v>.>....>v...>.>v..vvv..vv>v.v.....>>.v>v....>.....v.v.>v..v.>....>v>..v..vv...>>v
            .vv.v.>vv..>..>......vv..v>....>v.v....vv.>.v..vv>.>v...>>.....v>....>>.....v>v.>.v.>.v.>>..>>.........>>>>.v..>v>>.>..v....>......v.>>>.v.
            >vv.v>..v.>v.v.v>.v..>.v..v>..>v.>vvv..v.>.>.vv.>v.>...v.....>....v..v..>.>...v..v.v>..>>>vvv......v.>.v...>vvv....>v.v.v.v>>v>>v>>..>v>>.v
            >>.....>.>..>>..>>vv.>...>v.v>>v>>.>v.>.v>>v.v>.vvv>.....>>.>..>..>v.vv..v.v..v.>>v>....v.>>.vv.>v>>v>v>v>.>v.v>.v.>..>.v...vv.>v..>>..>>>v
            .>>>..vv......vv...v.v.v.>v>.>.>>>..v...>>.v.>.vv..v..>>..v.v...>>v>vv>>>v>....>>v.v>.v>>..v.>.>vv>>v.>...>>.v.>...v...>>.>>v>>v..v.v.>vv..
            v>>..v.v.>v.v....v.v.>.>v..>.>.>.v.....v>vv..>vvv.>......>..>...>>v>....>vvvv.>.v.>v>..>>.v..v..>>>..v>>>..v...>>v>.v..>.v...v...v.v>.v.>v.
            ...>v.v...vvvv......>.v..>>>.>........>..v..>v>.v...vv>vv>>..vv..>>.v....v>>>..v..>..>>vv..v.>v>.>.>>>vv..v...v.vv....v..>v..v>.>.>.v.>..v>
            >.v.v>>.>v..v>v..v....v.>>.>vv>v....>.vv......vv>...v.v.vv..v>....>v.>.v.vv..v.v.>v>>v>>>..>>.>.>..>vv..>v..>.vv...v>>..v>v..>.>>..v..>>.>.
            .>........>.>>>>.v....>v.>v>...>.v.vv>>..v.>...v.v.>vv.>v.v>..>.>vvvvv...v>.v.>..>....>.vv....>....>..>>vv..vv>.>.v.v>.>..v.v.>...>.>v>>.v.
            v..>vv>vvv..vvv......v..v..v..>v.v>>.v.>v.vv.>...v>v>..v>>>..v...vvv>v.....>..vv.v.v...v.>v>....v.>>..v>..v...v.....v>...vv.>v..>.>>.>v>v>>
            >v>..>....>..v>>v>>>..>v.vv.v...v.>>.>.v.vv..>vvvv....v....>.v..>v>v>...v.v.v..v>..>.>v.v..>..v.v..v..v.v>...>...v>v..v.>vvv.v>vv..>.......
            ............v..>.v>v>v>.v>vv>.v>.>v.vvv.v>..>v>..vvv.>>..>>vv...>.>.>.v>v>v.v>.vv.vvv>vv...>vv.>.>.v.....>.>.....>v.......>>>v.v>.v>v.vv.>.
            v>.v>>..>....>.vv>>.>.>..v...v.>v.>v..>>...>.v.>v>v..>vvv.>vv.v>>..>>..>>....v....v>.vv....v>>v.v..>......v>v>>.v..>.>vv.>..vv>>v.>>.....>.
            ..v...v.>v..>.v.>>..>..>v...>.v..>.>..>..>..>v..v.>.>..vv.>vv..>.>>..v.v>..v...>v>.>....>>.>v.>v.v....>..>.v.v>..v>>>.v.v>.v..>>>>.v>v.>>vv
            ..>>v>>vv>v......>>...>.>>>>>>..>>..>>>.>>>.....>v>>.>>..v>..>>..>>....>>.>.>>.>..vv>.>.v......v>....v>v.>vv...vv>..v.>vv..>v.>..vv..>...v>
            .v>vvv.v..>.v>.v.v>..v>...>.........>vv...>.v>.>v.>v.>.....vvv...>vv>.v>>>.v.>....>..vv>vv>...v>..>v>....>....>vvv>>v>vv.>v.>..v.vvv>v>>>.v
            ..>v....vv.v...>..v...vvv...>>........v...>.>>.>>...v..>.>>..>v.>v.v>..>>.v.v..v>..>.>...v.>v.>v.vv>>v>>>....>>vv.>v.>>>>.v......>>vv.v....
            v.>vvv..v..>...v.>v>.>>.v>v.vv>...>.......>...>>..vv..>.>>vv>.v>..vv.....vv.....>v>..>v...v..>..v.>..v......v>..>..>>...>..v.>.v.>.v.vv.>vv
            .v.>....v.v>.>.v.>>>v...>>...>>...>>v>>v>.>.>v.>>.>>.>v.>vv>....>.v.>>.v...>v>>v.>v..vv>v...vv.v>.>v...v.v.vvv.>v>.vv>..>v...>>v...>..>...>
            ......v.vvv.....v...>.....v..v.>>v.>.vv..>..v>v.v.v>v..v..v.>v>>vv>..v.>v.>.....v.>.>v.....>>.v>>...vvv>>.vv.v.>vv..vv..v>v>.>.>>>>v.vv...v
            """;
}