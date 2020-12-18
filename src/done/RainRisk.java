package done;

import org.junit.Test;
import util.LineSupplier;

import java.util.Objects;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Stream;

import static done.RainRisk.Command.Direction.*;
import static org.junit.Assert.assertEquals;

@SuppressWarnings("unused")
public class RainRisk {

    static Command[] commands;

    static class Command {
        final Direction dir;
        final int value;

        Command(Direction dir, int value) {
            this.dir = dir;
            this.value = value;
        }

        static Command toCommand(String line) {
            Direction dir = Direction.from(line.charAt(0));
            int value = Integer.parseInt(line.substring(1));
            return new Command(dir, value);
        }

        enum Direction {
            NORTH,
            EAST,
            SOUTH,
            WEST,

            LEFT,
            RIGHT,
            FORWARD,
            ;

            static Direction from(char ch) {
                return switch(ch) {
                    case 'N' -> NORTH;
                    case 'W' -> WEST;
                    case 'S' -> SOUTH;
                    case 'E' -> EAST;
                    case 'L' -> LEFT;
                    case 'R' -> RIGHT;
                    case 'F' -> FORWARD;
                    default -> throw new IllegalArgumentException("Illegal direction: " + ch);
                };
            }

            Direction turn(int value) {
                int ord = ((ordinal() % 4) + value + 4) % 4;
                return values()[ord];
            }
        }
    }

    static class Waypoint {
        int eastPos = 10;
        int northPos = 1;

        void move(int value, Command.Direction direction) {
            switch (direction) {
                case NORTH -> northPos += value;
                case EAST -> eastPos += value;
                case SOUTH -> northPos -= value;
                case WEST -> eastPos -= value;
                case LEFT -> turnLeft(value);
                case RIGHT -> turnRight(value);
                default -> {}
            }
        }

        private void turnRight(int value) {
            turn((value + 360) % 360);
        }

        private void turnLeft(int value) {
            turn((-value + 720) % 360);
        }

        private void turn(int value) {
            int east = eastPos;
            int north = northPos;
            eastPos = switch (value) {
                case 90 -> north;
                case 180 -> -east;
                case 270 -> -north;
                default -> east;
            };
            northPos = switch (value) {
                case 90 -> -east;
                case 180 -> -north;
                case 270 -> east;
                default -> north;
            };
        }

        @Override
        public String toString() {
            return "waypoint: east=" + eastPos + ", north=" + northPos;
        }
    }

    static class Ship {
        int eastPos = 0;
        int northPos = 0;
        Waypoint waypoint = new Waypoint();

        void move(Command cmd) {
            if (cmd.dir == FORWARD) {
                move(cmd.value);
            } else {
                waypoint.move(cmd.value, cmd.dir);
            }
//            System.out.println(toString());
        }

        private void move(int value) {
            eastPos += waypoint.eastPos * value;
            northPos += waypoint.northPos * value;
        }

        int manhattanPosition() {
            return Math.abs(eastPos) + Math.abs(northPos);
        }

        @Override
        public String toString() {
            return "ship: east=" + eastPos + ", north=" + northPos + "\n" + waypoint;
        }
    }

    static Ship ship = new Ship();

    public static void main(String[] args) {
        try (Scanner in = new Scanner(INPUT2)) {
            commands = Stream.generate(new LineSupplier(in))
                    .takeWhile(Objects::nonNull)
                    .map(Command::toCommand)
                    .toArray(Command[]::new);

            Stream.of(commands).forEach(ship::move);

            System.out.println("Result: " + ship.manhattanPosition());
        }
    }

    private static final String INPUT = "" +
            "F10\n" +
            "N3\n" +
            "F7\n" +
            "R90\n" +
            "F11\n";

    private static final String INPUT2 = "" +
            "L90\n" +
            "N5\n" +
            "L180\n" +
            "L180\n" +
            "S4\n" +
            "F21\n" +
            "W4\n" +
            "S1\n" +
            "R270\n" +
            "F18\n" +
            "S4\n" +
            "F44\n" +
            "R90\n" +
            "N5\n" +
            "F18\n" +
            "E5\n" +
            "R270\n" +
            "F1\n" +
            "L90\n" +
            "W5\n" +
            "N4\n" +
            "W5\n" +
            "F37\n" +
            "R90\n" +
            "S1\n" +
            "W2\n" +
            "S1\n" +
            "L90\n" +
            "W5\n" +
            "R90\n" +
            "N1\n" +
            "W2\n" +
            "L180\n" +
            "L90\n" +
            "E3\n" +
            "N1\n" +
            "L90\n" +
            "F30\n" +
            "E4\n" +
            "N2\n" +
            "E2\n" +
            "F76\n" +
            "R90\n" +
            "W4\n" +
            "S3\n" +
            "R90\n" +
            "F2\n" +
            "L90\n" +
            "S3\n" +
            "R180\n" +
            "N5\n" +
            "E4\n" +
            "L90\n" +
            "W4\n" +
            "F1\n" +
            "N2\n" +
            "E1\n" +
            "F8\n" +
            "R90\n" +
            "F88\n" +
            "R180\n" +
            "F60\n" +
            "W2\n" +
            "R90\n" +
            "E3\n" +
            "N3\n" +
            "W5\n" +
            "F56\n" +
            "S1\n" +
            "E5\n" +
            "F5\n" +
            "L90\n" +
            "E4\n" +
            "N3\n" +
            "R90\n" +
            "E2\n" +
            "F34\n" +
            "W4\n" +
            "L90\n" +
            "F100\n" +
            "W4\n" +
            "L90\n" +
            "F40\n" +
            "L90\n" +
            "F51\n" +
            "E5\n" +
            "F52\n" +
            "N1\n" +
            "F45\n" +
            "W2\n" +
            "N2\n" +
            "F56\n" +
            "N2\n" +
            "W3\n" +
            "R180\n" +
            "F14\n" +
            "N3\n" +
            "L90\n" +
            "N2\n" +
            "F18\n" +
            "L180\n" +
            "E1\n" +
            "R90\n" +
            "N2\n" +
            "E3\n" +
            "L180\n" +
            "S5\n" +
            "F87\n" +
            "L90\n" +
            "F32\n" +
            "E1\n" +
            "F92\n" +
            "N1\n" +
            "W3\n" +
            "F89\n" +
            "E2\n" +
            "N1\n" +
            "R90\n" +
            "W1\n" +
            "F9\n" +
            "E1\n" +
            "F74\n" +
            "S1\n" +
            "L270\n" +
            "S1\n" +
            "F99\n" +
            "L90\n" +
            "W1\n" +
            "R90\n" +
            "F78\n" +
            "L90\n" +
            "W4\n" +
            "S2\n" +
            "R180\n" +
            "E3\n" +
            "S4\n" +
            "L90\n" +
            "F78\n" +
            "E5\n" +
            "L90\n" +
            "S5\n" +
            "W2\n" +
            "R90\n" +
            "N1\n" +
            "E5\n" +
            "F33\n" +
            "W1\n" +
            "R180\n" +
            "S1\n" +
            "W4\n" +
            "N1\n" +
            "F69\n" +
            "S5\n" +
            "R90\n" +
            "N5\n" +
            "F89\n" +
            "L90\n" +
            "W1\n" +
            "F91\n" +
            "L90\n" +
            "F19\n" +
            "E5\n" +
            "L90\n" +
            "F53\n" +
            "L90\n" +
            "S5\n" +
            "L90\n" +
            "S4\n" +
            "W1\n" +
            "S2\n" +
            "L180\n" +
            "F3\n" +
            "N5\n" +
            "N5\n" +
            "F78\n" +
            "E3\n" +
            "S1\n" +
            "L180\n" +
            "F79\n" +
            "L90\n" +
            "W4\n" +
            "R180\n" +
            "W3\n" +
            "N4\n" +
            "W5\n" +
            "F84\n" +
            "S4\n" +
            "L180\n" +
            "S1\n" +
            "S3\n" +
            "E2\n" +
            "S4\n" +
            "R90\n" +
            "N1\n" +
            "E5\n" +
            "S4\n" +
            "W4\n" +
            "R90\n" +
            "F44\n" +
            "R90\n" +
            "E5\n" +
            "S5\n" +
            "W1\n" +
            "N4\n" +
            "F37\n" +
            "N2\n" +
            "F41\n" +
            "R90\n" +
            "F58\n" +
            "L90\n" +
            "F5\n" +
            "R90\n" +
            "W4\n" +
            "L90\n" +
            "F45\n" +
            "N4\n" +
            "F48\n" +
            "S1\n" +
            "E2\n" +
            "S1\n" +
            "R90\n" +
            "F30\n" +
            "W2\n" +
            "L90\n" +
            "F53\n" +
            "L90\n" +
            "W5\n" +
            "R90\n" +
            "N2\n" +
            "E1\n" +
            "S3\n" +
            "F29\n" +
            "N5\n" +
            "L270\n" +
            "S2\n" +
            "F87\n" +
            "S4\n" +
            "F86\n" +
            "S4\n" +
            "R90\n" +
            "W5\n" +
            "F59\n" +
            "N2\n" +
            "F35\n" +
            "L90\n" +
            "W5\n" +
            "N3\n" +
            "E3\n" +
            "L90\n" +
            "S2\n" +
            "E2\n" +
            "N2\n" +
            "L90\n" +
            "R90\n" +
            "N5\n" +
            "L270\n" +
            "N5\n" +
            "R90\n" +
            "N4\n" +
            "E2\n" +
            "S3\n" +
            "W2\n" +
            "F55\n" +
            "E4\n" +
            "S1\n" +
            "L90\n" +
            "W3\n" +
            "S4\n" +
            "F95\n" +
            "W5\n" +
            "E2\n" +
            "R90\n" +
            "S3\n" +
            "F54\n" +
            "L90\n" +
            "N5\n" +
            "F69\n" +
            "R90\n" +
            "N1\n" +
            "W3\n" +
            "N4\n" +
            "F49\n" +
            "N4\n" +
            "E5\n" +
            "S2\n" +
            "W5\n" +
            "S5\n" +
            "R90\n" +
            "N1\n" +
            "F76\n" +
            "S5\n" +
            "E4\n" +
            "S5\n" +
            "L90\n" +
            "N2\n" +
            "R90\n" +
            "F68\n" +
            "L90\n" +
            "S1\n" +
            "R90\n" +
            "F67\n" +
            "L90\n" +
            "N3\n" +
            "E1\n" +
            "F51\n" +
            "S1\n" +
            "F94\n" +
            "S3\n" +
            "E5\n" +
            "N3\n" +
            "F76\n" +
            "R180\n" +
            "F53\n" +
            "R90\n" +
            "R90\n" +
            "F96\n" +
            "L270\n" +
            "N1\n" +
            "R90\n" +
            "E3\n" +
            "L90\n" +
            "F57\n" +
            "S5\n" +
            "F39\n" +
            "N2\n" +
            "F95\n" +
            "R270\n" +
            "W1\n" +
            "S4\n" +
            "N5\n" +
            "N4\n" +
            "F5\n" +
            "L90\n" +
            "F83\n" +
            "L180\n" +
            "E4\n" +
            "F82\n" +
            "N5\n" +
            "R90\n" +
            "F52\n" +
            "L90\n" +
            "F13\n" +
            "N5\n" +
            "R90\n" +
            "L90\n" +
            "F10\n" +
            "N5\n" +
            "F80\n" +
            "E4\n" +
            "L180\n" +
            "N1\n" +
            "R90\n" +
            "E1\n" +
            "R180\n" +
            "E5\n" +
            "F25\n" +
            "S3\n" +
            "L180\n" +
            "F29\n" +
            "N1\n" +
            "W1\n" +
            "F20\n" +
            "W1\n" +
            "R180\n" +
            "F56\n" +
            "E5\n" +
            "S2\n" +
            "L90\n" +
            "F67\n" +
            "N4\n" +
            "W3\n" +
            "E2\n" +
            "R180\n" +
            "E1\n" +
            "F16\n" +
            "F59\n" +
            "R180\n" +
            "E5\n" +
            "F21\n" +
            "E2\n" +
            "R90\n" +
            "N4\n" +
            "E5\n" +
            "S5\n" +
            "E3\n" +
            "L90\n" +
            "W1\n" +
            "L90\n" +
            "E2\n" +
            "S3\n" +
            "R90\n" +
            "F59\n" +
            "W4\n" +
            "F44\n" +
            "S2\n" +
            "W1\n" +
            "S1\n" +
            "N5\n" +
            "W1\n" +
            "S3\n" +
            "E1\n" +
            "N3\n" +
            "R90\n" +
            "E2\n" +
            "F39\n" +
            "R90\n" +
            "F2\n" +
            "E1\n" +
            "N5\n" +
            "W5\n" +
            "F24\n" +
            "E3\n" +
            "L90\n" +
            "S3\n" +
            "E2\n" +
            "F57\n" +
            "E2\n" +
            "R90\n" +
            "F12\n" +
            "R90\n" +
            "N2\n" +
            "W3\n" +
            "L180\n" +
            "N4\n" +
            "F78\n" +
            "R180\n" +
            "N4\n" +
            "F92\n" +
            "L90\n" +
            "L180\n" +
            "N2\n" +
            "W4\n" +
            "R90\n" +
            "F7\n" +
            "S4\n" +
            "E3\n" +
            "S4\n" +
            "E1\n" +
            "S4\n" +
            "L180\n" +
            "S2\n" +
            "F81\n" +
            "E5\n" +
            "L90\n" +
            "F3\n" +
            "N4\n" +
            "F39\n" +
            "S2\n" +
            "W4\n" +
            "F28\n" +
            "R90\n" +
            "F75\n" +
            "W1\n" +
            "S3\n" +
            "W5\n" +
            "S1\n" +
            "F67\n" +
            "E3\n" +
            "F62\n" +
            "R90\n" +
            "N3\n" +
            "R180\n" +
            "W2\n" +
            "F67\n" +
            "S2\n" +
            "W1\n" +
            "L90\n" +
            "L90\n" +
            "S2\n" +
            "E3\n" +
            "R90\n" +
            "N5\n" +
            "S4\n" +
            "F14\n" +
            "R180\n" +
            "N2\n" +
            "R90\n" +
            "W3\n" +
            "L180\n" +
            "F37\n" +
            "W1\n" +
            "S4\n" +
            "E1\n" +
            "F45\n" +
            "W4\n" +
            "S5\n" +
            "L180\n" +
            "S2\n" +
            "W1\n" +
            "L90\n" +
            "N4\n" +
            "R90\n" +
            "F44\n" +
            "S1\n" +
            "E3\n" +
            "S4\n" +
            "W5\n" +
            "N4\n" +
            "W4\n" +
            "R270\n" +
            "S1\n" +
            "W3\n" +
            "L90\n" +
            "R90\n" +
            "F95\n" +
            "N1\n" +
            "R90\n" +
            "S1\n" +
            "F48\n" +
            "L90\n" +
            "F53\n" +
            "E2\n" +
            "R180\n" +
            "N5\n" +
            "F46\n" +
            "W5\n" +
            "F98\n" +
            "S3\n" +
            "F81\n" +
            "N5\n" +
            "F98\n" +
            "N4\n" +
            "F67\n" +
            "S1\n" +
            "E1\n" +
            "F10\n" +
            "R90\n" +
            "F66\n" +
            "W3\n" +
            "N1\n" +
            "L180\n" +
            "N1\n" +
            "F27\n" +
            "F54\n" +
            "W2\n" +
            "F3\n" +
            "R90\n" +
            "F68\n" +
            "E2\n" +
            "E4\n" +
            "F30\n" +
            "L90\n" +
            "F62\n" +
            "S2\n" +
            "L90\n" +
            "F99\n" +
            "R90\n" +
            "F48\n" +
            "E4\n" +
            "S4\n" +
            "F96\n" +
            "W4\n" +
            "N5\n" +
            "W5\n" +
            "F44\n" +
            "F90\n" +
            "N1\n" +
            "L90\n" +
            "F68\n" +
            "N4\n" +
            "W1\n" +
            "F83\n" +
            "S5\n" +
            "E1\n" +
            "N3\n" +
            "R90\n" +
            "W4\n" +
            "N5\n" +
            "F59\n" +
            "R90\n" +
            "L180\n" +
            "W2\n" +
            "F14\n" +
            "L90\n" +
            "N1\n" +
            "F58\n" +
            "R90\n" +
            "E2\n" +
            "L90\n" +
            "S5\n" +
            "F30\n" +
            "R90\n" +
            "F17\n" +
            "W1\n" +
            "F29\n" +
            "E3\n" +
            "R90\n" +
            "S3\n" +
            "R90\n" +
            "W1\n" +
            "N2\n" +
            "S3\n" +
            "W2\n" +
            "S2\n" +
            "R90\n" +
            "W2\n" +
            "N2\n" +
            "L90\n" +
            "W1\n" +
            "F55\n" +
            "S3\n" +
            "W4\n" +
            "R180\n" +
            "N3\n" +
            "W1\n" +
            "L90\n" +
            "F59\n" +
            "E5\n" +
            "L90\n" +
            "L180\n" +
            "F70\n" +
            "W1\n" +
            "F41\n" +
            "L180\n" +
            "S5\n" +
            "F22\n" +
            "S5\n" +
            "L270\n" +
            "F11\n" +
            "R90\n" +
            "S3\n" +
            "W2\n" +
            "N4\n" +
            "R90\n" +
            "W5\n" +
            "R180\n" +
            "F17\n" +
            "R90\n" +
            "F99\n" +
            "L180\n" +
            "F26\n" +
            "R90\n" +
            "W5\n" +
            "R180\n" +
            "S5\n" +
            "F28\n" +
            "N5\n" +
            "W1\n" +
            "N5\n" +
            "F100\n" +
            "S4\n" +
            "E2\n" +
            "L270\n" +
            "N4\n" +
            "F100\n" +
            "S1\n" +
            "R180\n" +
            "F81\n" +
            "S5\n" +
            "W5\n" +
            "L180\n" +
            "F1\n" +
            "R90\n" +
            "W5\n" +
            "L90\n" +
            "R90\n" +
            "N4\n" +
            "F69\n" +
            "W5\n" +
            "L180\n" +
            "F68\n" +
            "S5\n" +
            "F21\n" +
            "E4\n" +
            "L180\n" +
            "W3\n" +
            "S3\n" +
            "R90\n" +
            "E3\n" +
            "R90\n" +
            "E2\n" +
            "R90\n" +
            "F19\n" +
            "N3\n" +
            "R90\n" +
            "F81\n" +
            "S1\n" +
            "R90\n" +
            "F1\n" +
            "N1\n" +
            "L90\n" +
            "R90\n" +
            "W1\n" +
            "S4\n" +
            "F93\n" +
            "W5\n" +
            "F31\n" +
            "W1\n" +
            "N1\n" +
            "W1\n" +
            "F59\n" +
            "L180\n" +
            "W5\n" +
            "S4\n" +
            "L90\n" +
            "S1\n" +
            "R270\n" +
            "N1\n" +
            "R90\n" +
            "S3\n" +
            "R90\n" +
            "W2\n" +
            "R90\n" +
            "W2\n" +
            "R180\n" +
            "F83\n" +
            "S3\n" +
            "R90\n" +
            "F99\n" +
            "R90\n" +
            "F25\n" +
            "S2\n" +
            "F81\n" +
            "F33\n" +
            "F55\n" +
            "R90\n" +
            "F40\n" +
            "N5\n" +
            "L90\n" +
            "N5\n" +
            "E5\n" +
            "F56\n" +
            "L180\n" +
            "S2\n" +
            "F52\n" +
            "E4\n" +
            "F99\n" +
            "S2\n" +
            "E1\n" +
            "L180\n" +
            "F47\n" +
            "S3\n" +
            "W4\n" +
            "W3\n" +
            "L90\n" +
            "N1\n" +
            "F26\n" +
            "R90\n" +
            "W5\n" +
            "R90\n" +
            "W5\n" +
            "L90\n" +
            "E2\n" +
            "N1\n" +
            "F35\n" +
            "L90\n" +
            "S3\n" +
            "F20\n" +
            "W5\n" +
            "F29\n" +
            "L90\n" +
            "S2\n" +
            "W4\n" +
            "L180\n" +
            "N5\n" +
            "F27\n" +
            "L90\n" +
            "F80\n" +
            "S1\n" +
            "L90\n" +
            "R180\n" +
            "F37\n";

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
        assertEquals(NORTH, EAST.turn(-90/90));
        assertEquals(WEST, EAST.turn(-180/90));
        assertEquals(EAST, EAST.turn(-360/90));
        assertEquals(SOUTH, EAST.turn(90/90));
        assertEquals(WEST, EAST.turn(180/90));
        assertEquals(NORTH, EAST.turn(270/90));
        assertEquals(EAST, EAST.turn(360/90));
    }
}