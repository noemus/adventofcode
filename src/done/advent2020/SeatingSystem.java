package done.advent2020;

import org.junit.Test;
import util.LineSupplier;

import java.util.Objects;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class SeatingSystem {

    static Seat[][] seats;

    public static void main(String[] args) {
        try (Scanner in = new Scanner(INPUT2)) {
            seats = Stream.generate(new LineSupplier(in))
                    .takeWhile(Objects::nonNull)
                    .map(String::getBytes)
                    .map(SeatingSystem::toSeats)
                    .toArray(Seat[][]::new);

            long result;
            boolean changed;
            int round = 1;
            do {
//                printSeats();

                Stream.of(seats).flatMap(Stream::of).forEach(Seat::update);

                changed = Stream.of(seats)
                        .flatMap(Stream::of)
                        .anyMatch(Seat::isChanged);

                Stream.of(seats).flatMap(Stream::of).forEach(Seat::reset);

                result = Stream.of(seats)
                        .flatMap(Stream::of)
                        .filter(Seat::isOccupied)
                        .count();

                System.out.println("Round[ "+round+" ]: " + result);
                round++;
            } while(changed);

            result = Stream.of(seats)
                    .flatMap(Stream::of)
                    .filter(Seat::isOccupied)
                    .count();

            System.out.println("Result: " + result);
        }
    }

    private static void printSeats() {
        Stream.of(seats).forEach(row -> {
                    Stream.of(row).map(Seat::print).forEach(System.out::print);
                    System.out.println();
                });
        System.out.println();
    }

    static int rows = 0;

    private static Seat[] toSeats(byte[] line) {
        Seat[] seats = new Seat[line.length];
        for (int i=0;i< line.length;i++) {
            seats[i] = new Seat(rows, i, Seat.State.of(line[i]));
        }
        rows++;
        return seats;
    }

    static class Seat {
        final int row;
        final int col;
        State state;
        State newState;
        boolean changed = false;

        Seat(int row, int col, State state) {
            this.row = row;
            this.col = col;
            this.state = state;
        }

        String print() {
            return switch (state) {
                case OCCUPIED -> "#";
                case EMPTY -> "L";
                case FLOOR -> ".";
            };
        }

        void update() {
            newState = switch (state) {
                case EMPTY -> updateEmpty();
                case OCCUPIED -> updateOccupied();
                case FLOOR -> State.FLOOR;
            };
            changed = newState != state;
        }

        private State updateEmpty() {
            boolean allEmpty = Stream
                    .of(top(), bottom(), left(), right(), topleft(), topright(), bottomleft(), bottomright())
                    .noneMatch(State::isOccupied);
            if (allEmpty) {
                return State.OCCUPIED;
            }
            return State.EMPTY;
        }

        private State updateOccupied() {
            long occupied = Stream
                    .of(top(), bottom(), left(), right(), topleft(), topright(), bottomleft(), bottomright())
                    .filter(State::isOccupied)
                    .count();
            return occupied >= 5
                    ? State.EMPTY
                    : State.OCCUPIED;
        }

        private State top() {
            int curRow = row;
            while (curRow > 0) {
                curRow--;
                if (seats[curRow][col].state != State.FLOOR) {
                    return seats[curRow][col].state;
                }
            }
            return State.FLOOR;
        }

        private State bottom() {
            int curRow = row;
            while (curRow < seats.length - 1) {
                curRow++;
                if (seats[curRow][col].state != State.FLOOR) {
                    return seats[curRow][col].state;
                }
            }
            return State.FLOOR;
        }

        private State left() {
            int curCol = col;
            while (curCol > 0) {
                curCol--;
                if (seats[row][curCol].state != State.FLOOR) {
                    return seats[row][curCol].state;
                }
            }
            return State.FLOOR;
        }

        private State right() {
            int curCol = col;
            while (curCol < seats[row].length - 1) {
                curCol++;
                if (seats[row][curCol].state != State.FLOOR) {
                    return seats[row][curCol].state;
                }
            }
            return State.FLOOR;
        }

        private State topleft() {
            int curRow = row;
            int curCol = col;
            while (curRow > 0 && curCol > 0) {
                curRow--;
                curCol--;
                if (seats[curRow][curCol].state != State.FLOOR) {
                    return seats[curRow][curCol].state;
                }
            }
            return State.FLOOR;
        }

        private State topright() {
            int curRow = row;
            int curCol = col;
            while (curRow > 0 && curCol < seats[row].length - 1) {
                curRow--;
                curCol++;
                if (seats[curRow][curCol].state != State.FLOOR) {
                    return seats[curRow][curCol].state;
                }
            }
            return State.FLOOR;
        }

        private State bottomleft() {
            int curRow = row;
            int curCol = col;
            while (curRow < seats.length - 1 && curCol > 0) {
                curRow++;
                curCol--;
                if (seats[curRow][curCol].state != State.FLOOR) {
                    return seats[curRow][curCol].state;
                }
            }
            return State.FLOOR;
        }

        private State bottomright() {
            int curRow = row;
            int curCol = col;
            while (curRow < seats.length - 1 && curCol < seats[row].length - 1) {
                curRow++;
                curCol++;
                if (seats[curRow][curCol].state != State.FLOOR) {
                    return seats[curRow][curCol].state;
                }
            }
            return State.FLOOR;
        }

        void reset() {
            changed = false;
            state = newState;
        }

        boolean isChanged() {
            return changed;
        }

        boolean isOccupied() {
            return state.isOccupied();
        }

        enum State {
            EMPTY,
            OCCUPIED,
            FLOOR,
            ;

            public static State of(byte b) {
                return switch(b) {
                    case 'L' -> EMPTY;
                    case '#' -> OCCUPIED;
                    default -> FLOOR;
                };
            }

            boolean isOccupied() {
                return this == OCCUPIED;
            }
        }
    }

    private static final String INPUT = "" +
            "L.LL.LL.LL\n" +
            "LLLLLLL.LL\n" +
            "L.L.L..L..\n" +
            "LLLL.LL.LL\n" +
            "L.LL.LL.LL\n" +
            "L.LLLLL.LL\n" +
            "..L.L.....\n" +
            "LLLLLLLLLL\n" +
            "L.LLLLLL.L\n" +
            "L.LLLLL.LL\n";

    private static final String INPUT2 = "" +
            "LLLLLLLLLL.LLLLLLLLL.LLLLL.LLLLL.LLLLLLL.LLLL..LLLLLLLL.LLLLLLLL.LLLLLLLL..LLLLLLLL.LLLLLLLL.LLLLL\n" +
            "LLLLLLLLLL.LLLLLLLLLLLLLLLLLLLLL..LLLLLL.LLLLL.LLLLLLLLLLLLLLLLL.LLLLLLLL.LLLLLLLLL.LLLLLLLL.LLLLL\n" +
            "LLLLLLLLLLLLLLLLLLLL.LLLLL.LLLLL.LLLLLLL.LLLLLLLLLLLLLL.LLLL.LLLLLLLLLLLL.LLLLLLLLL.LLLL.LLL.LLLLL\n" +
            "LLLLLLLLLLLLLLLLLLLL.LLLLL.LLLLLLLLLLLLL.LLLLL.LLLLLLLL.LLLLLLLL.LLLLLLLLLLLLLLLLLL.LLL.LLLLLLLLLL\n" +
            "LLLLLLLLLL.LLLLLLLLL.LLLLL.LLLLL.LLLLLLL.LLLLLLLLLLLLLL.LLLLLLLL.LLLLLLLL.LLLLLLLLLLLLLLLLLL.LLLLL\n" +
            "LLLLLLLLLL.LLLLLLLLL.LLLLL.L.LLLLLLLLLLL.LLLLLLLLLLLLLL.LLLLLLLL.LLLLLLLLLLLLLLLLLL.LLLLLLLL.LLLLL\n" +
            "LLLLL.LLLL.LLLLLLLLL.LLLLL.LLLLLLLLLLLLLLL.LLL.LLLLLLLLLLLLL.LLL.LLLL.LLL.LLLLLLLLL.LLLLLLLL.LLLLL\n" +
            "LLLLLLLLLL.LL.LLLLLL.LLLLL.LLLLL.LLLLLLL.LLLLL.LLLLLLLL.LLLLLLLL.LLLL.LLL.LLLLLLLLL.LLLLLLLL.LLLLL\n" +
            "LLLLLLLLLLLLLLLLLLLL.LLLLL.LLLLLLLLLLLLL.LLLLL.LLLLLLLL.LLLLLLLLLLLLLLLLLLLLLLLLLLL...LLLLLLLLLLL.\n" +
            "LL..LL..L..L.L...L.L..L.L....L.LL..L.L......LL.LL....LL..LL..LLLL.L.LLL.L.....LLL.LL........L.....\n" +
            "LLLLLLLLLLLLLLLLLLLL.LLLLLLLLLLL.LLL..LL.LLLLLLL.LL.LLL.LLLL.LLLLLLLLLLLL.LLLLLLLLL.LLLLLLLL.LLLLL\n" +
            "LLLLLLLLLL.LLLL.LLLLLLLLLL.LLLLLLLLLLLLLLLLLLL.LLLLLLLL.LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL.LLLLL\n" +
            "LLLLLLLLLL.LLLLLLLLL.LLLLL.LLLLL.LLLLLLL.LLLLL.LLLLL.LL.LLLLLLL..LLLLLLLL.LL.LLLLLLLLLLLLLLLLLLLLL\n" +
            "LLLLLLLLL..LLLLLLLLLLLLLLL.LLLLLLLLLLLLL.LLLLLLLLLLLLLL.LLLLLLLLLLLLLLLLL.LLLLLLLLL.LLLLLLLL.LLLLL\n" +
            "LLLLLLLLLL.LLLLLLLLL.LLLLLLLLLLL.LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL.LLLLLLLL.LLLLLLLLL.LLLLLLLLLLLLLL\n" +
            "LLLLLLLLLL.LLLLLLLLLLLLLLLLLLLLL.LLLLLLL.LLLLL.LLLLLLLL.LLLLLLLLLLLLLLLLL.LLLLLLLLL.LLLLLLLLL.LLLL\n" +
            "LLLLLLLLLL.LLLLLLLLL.LLLLL.LLLLLLLLLLLLL.LLLLLLLLLLLLLLLLLLLLLLL.LLLLLLLL.LLLLLLLLL.LLLLLLLLLLLLLL\n" +
            "LLLLLLLLLL.LLLLLLLLL.L.LLL.LLLLLLLLLLLLL.LLLLL.LLLLLLLL.LLLLLLLLLLLL.LLLLLLLLLLLLLLLLLLLLLLL.LLLLL\n" +
            "LLLLLLLLLL.LLLLLLLLLLLLLLL.LLLLL.LLLLLLLLLLLLL.LLLLLLLL.LLLLLLLL.LLLLLLLL.LLLLLLLLLLLLLLLLLLLLLLLL\n" +
            "...........L.....L............LL..L...LL...L.LLL.L....L.L.L......L..L...LL......L.L.L.........LL..\n" +
            "LLLLLLLLLL.LLLLLLLLL.LLLLLLLLLLL.LLLLLLL.LLLLLLLLLLLLLLLLLLLLLLL.LLLLLLLL.LLLLLLLLL.LLLLLLLL.LLLLL\n" +
            "LL.LLLLLLLLLLLLLLLLL.LLLL.LLLLLL.LLLLLLL.LLLLL.LLLLLLLL.LLLLLLLL.LLLLLLLL.LLLLLLLLL.LLLLLLLL.LLLLL\n" +
            "LLLLLLLLLL.LLLLLLLLL.LLLLL.LLLLLLLLLLLLL.LLLLL.LLLLLLLL.LLLLLLLL.LLLLLLLLLLLLLLLLLL.LLLLLLLL.LLLLL\n" +
            "LLLLLLLLLLLLLLLLLLLLLLLLLL.LLLLLLLLLLLLLLLLLLL.LLLLLLLL.L.LLLLLLLLLLLLLLLLLLLLLLLLL.LLLLLLLLLLLLLL\n" +
            "LLLLLLLLLL.LLLLLLLLL.LLLLL.LLLLL..LLLLLL.LLLLLLLLLLLLLL.LLLLLLLL.LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL\n" +
            "............L......L......L...L....L.....LLL....L.......L.....L.L..L....L..L.L....LL.....L....L.L.\n" +
            "LLLLLLLLLL..LLLLLLLLLLLLLL.LLLL.LLLLLLLL.LLLLL.LLLLLLLLLLLLLLLLL.LLLLLLLL.LLLLLLLLL.LLLLLLLL.LLLLL\n" +
            "LLLLLLLLLL.LLLLLLLLLLLLLLL.LL.LLLLLLLLLLLLLLLLLLLLLLLLL.LLLLLLLL.LLLL.LLLLLLLLLLLLL.LLLLLLLLLLLLLL\n" +
            "LLLLLLLLLL.LLL.LLLLL.LLLLL.LLLLLLLLLLLLLLLLLLL.LLLLLLLLLLLLLLLLL.LLLLLLLL.LLLLLLLLLLLLLLLLLL.LLLLL\n" +
            "LLLL.LLLLL.LLLLLL.LLLLLLLL.LLLLL.LLLLLLLLLLLLL.LLLLLLLL.LLLLLLLL.LLLLLLLL.LLLLLL.LLLLLLLLLLLLLLLLL\n" +
            "LLLLLLLLLL.LLLLLLLLL.LLLLL.LLLLL.LLLLLLLLLLLLL.LLLLLLLL.LLLLLLLL.LLLLLLLL.LLLLLLLLL.LLLLLLLLLLLLLL\n" +
            "LLLLLLLLLLLLLLLLLLLLLLLL.L.LLLLL.LLLLLLL.LLLLLLLLLLLLLL.LLLLLLLL.LLLLLLLL.LLLLLLLLL.LLLLLLLLLLLLLL\n" +
            "LLLLLLLLLLLLLLLLLLLL.LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL.LLLLLLLLLLLLLLLLLLLLLLLLLLL.LLLLLLLL.LLLLL\n" +
            ".LL........L.......L....LL.L..L.L...L.LLLLLL.L..............L.L.........LL........L....L.LL.....L.\n" +
            "LLLLLLLLLL.LLLLLLLLL.LLLLL.LLL.L.LLLLLLL.LLLLL.LLLLLLLL.LLLLL.LL.LLLLLLLLLLLLLLLLLL.LLLLLLLL.LLLLL\n" +
            "LLLLLLLLLL.LLLLLLLLL.LLLLL.LLLLL.LL.LLLLLL.LLL.LLLLLLLLLLLLLLLLLLLLL.LLLL.LLLLLLLLLLLLLLLLLL.LLLLL\n" +
            "LLLLLLLLLL.LLLLLLLLLLLLLLLLLLLLL.LLLLLLL.LLLLL.LLLLLLLL.LLLLLLLLLLL.LLLLLLLLLLLLLLL.LLLLLLLLLLLLLL\n" +
            "LLLLLLLLLLLLLLLLLLLL.LLLLL.LLLLL.LLLLLLLLLLLLL.LLLLLLLLLLLL.LLLL.LLLLLLLLLLLLLLLLLL.LLLLLLLLLLLLL.\n" +
            "LLLLLLLLLL.LLLLLLLLL.LLLLL.LLLLL.LLLLLLL.LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL\n" +
            "LLLLLLLLLL.LLLLLLLLL.LLLLLLLLLLL.LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL.LLLLLLLL.LLLLLLLLL.LLLLLLLLLLLLL.\n" +
            "LLLLLLLLLL.LLLLLLLLL.LLLLLLLLLLL.LLLLLLLLLLLLL.LLLLLL.L.LLLLLLLL.LLLLLLLLLLLLLLLLLLLLLLLLLLL.LLLLL\n" +
            "...L..L..L.LL.L.L.L......L.LLLL.L..L.L..L..L.L...L.LL.L...L....LL..L.L..L.L...........L.L..L..LL..\n" +
            "LLLLLL.LLL.LLLLLLLLL.LLLLLLLLL.L.LLLLLLL.LLLLL.LLLLLLLL.LLLLLLLLLLLLLLLLL.LLLLLLLLL.LLLLLLLLLLLLLL\n" +
            "LLLLL.LLLL.LLL.LLLLL.LLLLL.LLLLLLLLLLLL.LLLLLL.L.LLLLLLLLLLLLLLL..LLLLLLL.LLLLLLLLL.LLLLLLLLLLLL.L\n" +
            "LLLLLLLLLL.LLLLLLLLLLLLLLL.LLLLLLLLLLLLL.LLLLLLLLLLLLLLL.LLLLLLL.LLLLL.LLLLLLLLLLLL.LLLLLLLLLLLLLL\n" +
            "LLLL.LL.LLLLLLLLLLLL.LLLLL.L.LLLLLLLLLLLLLLLLLLLLLLLLLL.LLLLLLLL.LL.LLLLLLLLLLLLLLLLLLLLLLLL.LLLLL\n" +
            "LLLLLLLLLL.LLL.LLLLL.LLLLL.LLLLLLLLLLLLLLLLLLL.LLLLLLLL.LLLLLLLLLLLLLLLLLLLLLLLLLLL.LLLLLLLL.LLLLL\n" +
            "LLLLLLLLLL.LLLLLLLLL.L.LLL..LLL.LLL.LLLL.LLLLL.LLLLLLLL.LLLLLLLL.LLLLLLLLLLLLLLLLLL.LLLLLLLL.LLLLL\n" +
            "...LL.L..L..........L......L.L..L.LL.......LL...L.LL..L.L..L.L......L..L......L..L..L..L.L........\n" +
            "LLLLLLL.LL.LLLLLLLLLLLLLLL.LLLLL.LLLLLLL.LLLLL.LLLLLLLL.LLLLLLLL.LLLLLLLLLLLLLLLLLLLLLLLLLLL.LLLLL\n" +
            "LLL.LLLLLL.LLLLLLLLL.LLLLL.LLLLL.LLLLLLL.LLLLLLLLLLLLLL.LLLLLLLL.LLLLLLLL.LLLLLLL.L.LLLLLLLL.LLLLL\n" +
            "LLLLLLLLLL.LLLLLLLLL.LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL.LLLLLL.LLL.LLLLLLL.LLLLLLLL.LLLLL\n" +
            "LLLLLLLLLL.LLLLLLLLL.LLLLL.LLLLL.LLLLLLLLLLLLL.LLLLLLLLLLLLLLLLL.LLLLLLLLLLLLLLLLLLLLLLLLLLL.LLLLL\n" +
            "LLLLLLLLLL.LLLLL.LLL.LLL.L.LLLLLLLLLLLLL.LLLLL.LLLLLLLL.LLLLLLLL.LLLLLLLLLLLLLLLLLL.LLLLLLLL.LLLLL\n" +
            "LLLLLLLLLL.LLLLLLLLLLLLLLL.LLLLLLLLLLLLL.LLLLL.LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL.LLL.LLLLLLLLLL\n" +
            "LLLLLLL.LLLLLL.LLLLLLLLLLL.LLLLLLLLLLLLLLLLL.L.LLLLLLLL.LLLLLLL.LLLLLLLLL.LLLLLLLLL.LLLLLLLL.LLLLL\n" +
            "L.....L..LL.L.......L.L..L....L..L.L...L.LLLL.......L.L...L....LL.L...L.L.L.LL.L......L.L.L.L.L...\n" +
            "LLL.LLLLLLLLLLLLLLLL.LLLLLLLLLLL.LLLLLLL.LLLLL.LLLLLLLL.LLLLLLLLLLLLLLLLL..L.LLLLLLLLLLLLLLLLLLLLL\n" +
            "L.LLLLLLLL.LLLLLLLLLLLLLLL.LLLLL.LLLLLLL.LLLLLLLLLLLLLL.LLLLLLLL.LLLLLLLL.LLLLLLLLLLLLLLLLLL.LLLLL\n" +
            "LLLLLLLLLL.LLLLLLLLLLLLLLL.LLLLL.LLLLLLLLLLLLL.LLLLLLLL.LLLLLLLL.LLLLLLLL.LLLLLLLLLLLLLLLLLLLLLLLL\n" +
            "LLLLLL.LLLLLLLLLLLLL.LLLLLLLLLLL.LLLLLLLLLLLLL.LLLLLLLL..LLLLLLLLLLLLLLLLLLLL.LLLLL.LLLLLLLLLLLLLL\n" +
            "LLLLLLLLLLLLLLLLLLLLLLLLLL.LLLLL.LLLLLLL.LLLLLLLLLLLLLL.LLLLLLL..LLLLLLLL.LLLLLLLLL.LLLLLLLLLLLLLL\n" +
            "LLLLLLLLLL.LLLLLLLLLLLLLLL.LLLLL.LLLLLLLLLLLLL.LLLLLLLL.LLLLLLLL.LLLLLLLLLLLLLLLLL..LLLLLLLLLLLLLL\n" +
            "LLLLLLLLLL.LLLLLLLLLLLLLLLLLLLLL.LLLLLLL.LLLLLLLLLLLLLLLLLLLLLL.LLLLLLLLL.LLLLLLLLLLLLLLLLLLLLLLLL\n" +
            ".......L..L..L.L....LLLLLL..L..LL.LLL..LL..L..L...L...LL.....L..LL.LL..L.L..L.L..L.L.L....LL....L.\n" +
            "LLLLLLLLLLLLLLLL.LLL.LLLLL.LLLLL.LLLLLLL.LLLLL.LLLLLLLL.LLLLLLLL.LLL.LLLL.LLLLLLLLL.L.LLLLLLLLLLLL\n" +
            "LLLLLLL.LL.LLLLLLLLLLLL.LL.LLLLL.LLLLLLLLLLLLL.LLLLLLLL.LLLLLLLL.LLLLLLLL.LLLLLLLLL.LLLLLLLL.LLLLL\n" +
            "LLLLLLLLLL.LLLLLLLL.LLLLLLLLLLLLL.LLLLLLLLLLLLLLLLL.LLL.LLLLL.LL.LLLLLLLLLLLLLLLLLL.LLLLLLLLLLLLLL\n" +
            "LLLLLLLLLL.LLLLLLLLLLLL.LL.LLLLL.LLLLLLL.LLLLL.LLLLLLLL.LLLLLLLL.LLLLLLL..LLLLLLLLL.LLLLLLLL.LLLLL\n" +
            ".....L.L..L.......L.LL..........L...LLLL.LL..L...L.L.L.LLLL...L.....L...L.LLLLLL..L..........LL...\n" +
            "LLLLLLLLLL.LLLLLLLLLLLLLLL.LLLLL.LLLLLLL.LLLLL.LLLLLLLL.LLLLLLLL.LLLLLLLL.LLLLLLLLLLLLLLLLLLLLLLLL\n" +
            "LLLLLLLLLL..LLLLLLLL.LLLLLLLLLLLLLLLLLLL.LLLLLLLLLLLLLL.LLLLLLLLLLLLLLLLL.LLLLLLLLLLLLLLLLLLLLLLLL\n" +
            "LLLLLLLLLL.LLLLLLLLLLLLLLL.LLLLLLLLLLLLL.LLLLL.LLLLLL.L.LLLLLLLL.LLLLLLLLLLLLLLLLLL.LLLLLLLL.LLLLL\n" +
            "L.LLLLLLLL.LLLLLLLLL.LLLLLLLLLLLLLLLLLLL.LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL.LLLLL.LLLLLLLLLLLLLLLLLL\n" +
            "..L.........L..L.L...L................LLL..L..LLL.L......L....L.L.....L.L.L.L.L..L.L.L.L....L....L\n" +
            "LLLLLLLLLL..LLLLLLLL.LLLLLLLLLLL.LLLLLLLLLLLLL.LLLLLLLL.LLLLLLLL.LLLLLLLL.LL.LLLLLL.LLLLLLLLLLLLLL\n" +
            "LLLLLLLLLL.LLLLLLLLL.LLLLLLLLLLL.LLLLLLL.LLLLL.LLLLLLLL.LLLLLLLL.LLLLLLLL.LLLLLLLLL.LLLLLLLL.LLLL.\n" +
            "LLLLLLLLLL.LLLLLLLLL.LLLLL.LLLLLLLLLLLLL.LLLL..LLLLLLLLLLLLLLLLLLLLLLLLLL.LLLLLLLLL.L.LLLLLL.LLLLL\n" +
            "LLLLL.LLLLLLLLLLLLLL.LLLLLLLLLLL.LLLLLLLLLLLL.LLLLLLLLL.LLLLLLLL.LLLLLLLLLLLLLLLLLLLLLLLLLLL.LLLLL\n" +
            "LLLLLLLLLL.LLLL.LLLL.LLLLL.LLLLLLLLLLLLL.LLLLL.LLLLLLLLLLLLLLLLL.LLLLLLLLLLLLLLLLL.LLLLLLLLLLLLLLL\n" +
            "LLLLLLLLLLLLLLLLLLL..LLLLL.LLLLL.LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL.LLLLLLLLLLLLLLLLLLLL\n" +
            "...LLL.....L..L.LL.L.L.LL.....L..LL.LLLL...L.L....LL.L...L..LL...L..LLL......LL..L..L.L..L.L...LL.\n" +
            "LLLLLLLLLL.LLLLLLLLL.LLLLL.LLLLL.LLLLLLL.LLLLL.LLLLLLLL.LLL.LLLL.LLLLLLLL.LLLLLLLLL.LLLLLLLL.LLLLL\n" +
            "LLLLLLLLLLLLLLLLLLLLLLLLLLLLL.LL.LLLLLLL.LLLLLLLLL.LLLL.LLLLLLLL.LLLLLLLL.LL.LLLLL.LLLLLLLLL.LLLLL\n" +
            "LLLLLLLLLL.LLLLLLLLL.LLLLLLLLLLL.LLLLLLL.LLLLL.LLLLLL.LLLLLLLL.LLLLLLLLLL.LLLLLLLLLLLLLLL.LL.LLLLL\n" +
            "LLLLLLLLLL.LLLLLLLLL.LLLLL.LLLLLLLLLLLLL.LLLLL.LLLLLLLLLLLLLLLLL.LLL.LLLL.LLLLLLLLL.LLLLLL.L.LLLLL\n" +
            "LLLLLLLLLLLLLLLLLLLL.LLLLLLLLLLLLLLLLLL..LLLLLLLLLLLLLL.LLLLLLLL.LLLLLLLLLLLLLLLLLL.L.LLLLLL.LLLLL\n" +
            "LL.LLLLLL..LLLLLLLLL.LLLLL.LLLLL.LLLLLLL.LLLLL.LLLLLLLL.LLLLLLLL.LLLLLLL..LLL.LLLLL.LLLLLLLL.LLLLL\n" +
            "LLLLLLLLLL.LLLLLLLLL.LLLLL.LLLLLLLLLLLLL.LLLLL.LLLLLLLL..LLLLLL.LLLLLLLLLLLLLLLLLLLLLLLLLLLL.LLLLL\n" +
            "LLLLLLLLLL.LLLLLLLLL.LLLLLLLLLLLLLLLLLLL.LLLLLLLLLLLLLL.LLLLLLLLLLLLLLLLL.LLLLLLLLLLLLLLLLLLLLLLLL\n" +
            "...L....L..L.L..L..L.LLLL....L..L.....L.....L.L..L.L.L...L.L.........L....L..L..L.LL.L...L.L...L..\n" +
            "LLLLLLLLLLLLLLLLLLLL.LLLLL.LLLLL.LLLLLLLLLLLLL.LLLLLLLLLLLLLLLLL.LLLLLLLL..LLLLL.LL.LLLLLLLLLLLLLL\n" +
            "LLLLLLLLLLLLLLLLLLLL.LLLL..LLLLL.LLLLLLLLLLLLL.LLLLLLLLLLLLLLLLL.LLLLLLLL.LLLLLLLL..LL.LLLLL.LLLLL\n" +
            "LLLLLLLLLL.LLLLLLLLLLLLLLLLLLLLL.LLLLLLL.LLLLL.LLLLLLLL.LLLLLLLL.LLLLLLLLLLLLLLLLLL.LLLLLLLL.LL.LL\n" +
            "LLLLLLLLLL.LLLLLLLLL.LLLLL.LLLLL.L.LLLLL.LLLLL.LLLLLLLLLLLLLLLLL.LLLLLLLL.LLLLLLLLL.LLLLLLLL.LLLLL\n" +
            "LLLLLLLLLLLLLLLLLLLL.LLLLLLLLLLLLLLLLLLLLLLLLL.LLLLLLLLLLLLLLLLLLLLLL.LLL.LLLLLLLLLL.LLLLLLL.LLLLL\n" +
            "LLLLLLLLLL.LLLLLLLLLLLLLLLLLLLLL.LLLLLL..LLLLLLLLLLLLLL.LLLLLLLL.LLLLLLLL..LLLLLLLL.LLLLLLLL.LLLLL\n" +
            "LLLLLL.L.L.LLLLLLLLLLLLLLLLLLLLL.LLLLLL.LLLLLL.LLLLLLLL.LLLLLLLL.LLLLLLLL.LLLLLLLLL.LLLLLLLL.LLLLL\n" +
            "L.LLLLLLLLLLLLLLLLLLLLLLLL.LLLLL.LLLLLLL.LL.LLLLLLLLLLLLLLLLLLLLLLLLLLLLL.LLLLLLLLLLLLLL.LLL..LLLL\n";

    static <T> Set<T> disjoint(Set<T> g1, Set<T> g2) {
        g1.retainAll(g2);
        return g1;
    }

    static boolean validIntRange(String text, int lowerBound, int upperBound) {
        try {
            int value = Integer.parseInt(text);
            return value >= lowerBound && value <= upperBound;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Test
    public void test() {

    }
}