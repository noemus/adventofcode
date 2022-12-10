package done.advent2022;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.lang.System.lineSeparator;
import static util.LineSupplier.lines;

public class CathodeRayTube {

    public static void main(String[] args) {
        try (Scanner in = new Scanner(INPUT)) {
            CPU cpu = new CPU(
                    lines(in)
                            .map(Command::create)
                            .toArray(Command[]::new)
            );

            cpu.run();

            System.out.println("Result: " + cpu.signalStrength());
            System.out.println();
            System.out.println(cpu.printCRT());
        }
    }

    static class CPU {
        private final Command[] instructions;
        private int cycle = 0;
        private int X = 1;
        private final List<Integer> signals = new ArrayList<>();
        private final StringBuilder CRT = new StringBuilder();

        CPU(Command[] instructions) {
            this.instructions = instructions;
        }

        void execute(Command cmd) {
            cmd.increments().forEach(inc -> {
                cycle++;
                updateSignal(cmd);
                updateCRT();
                X += inc;
            });
        }

        void run() {
            Stream.of(instructions).forEach(this::execute);
        }

        private void updateSignal(Command cmd) {
            if (cycle == 20
                    || cycle == 60
                    || cycle == 100
                    || cycle == 140
                    || cycle == 180
                    || cycle == 220) {
                signals.add(cycle * X);
                System.out.println("cycle: " + cycle + ", X: " + X + " (" + cmd + ")");
            }
        }

        private void updateCRT() {
            int pos = (cycle - 1) % 40;
            if (cycle > 1 && pos == 0) {
                CRT.append(lineSeparator());
            }
            if (pos == X - 1 || pos == X || pos == X + 1) {
                CRT.append('#');
            } else {
                CRT.append('.');
            }
        }

        String printCRT() {
            return CRT.toString();
        }

        int signalStrength() {
            return signals.stream()
                          .mapToInt(Integer::intValue)
                          .sum();
        }
    }

    sealed interface Command {
        Command NOOP = new Noop();

        IntStream increments();

        static Command create(String line) {
            String[] tokens = line.split(" ");
            return switch(tokens[0]) {
                case "noop" -> NOOP;
                case "addx" -> new Addx(Integer.parseInt(tokens[1]));
                default -> throw new IllegalArgumentException("Unsupported instruction: " + line);
            };
        }
    }

    record Addx(int num) implements Command {
        @Override
        public IntStream increments() {
            return IntStream.of(0, num);
        }

        @Override
        public String toString() {
            return "addx " + num;
        }
    }
    record Noop() implements Command {
        public IntStream increments() {
            return IntStream.of(0);
        }

        @Override
        public String toString() {
            return "noop";
        }
    }

    @SuppressWarnings("unused")
    private static final String INPUT1 = """
            noop
            addx 3
            addx -5
            """;

    @SuppressWarnings("unused")
    private static final String INPUT2 = """
            addx 15
            addx -11
            addx 6
            addx -3
            addx 5
            addx -1
            addx -8
            addx 13
            addx 4
            noop
            addx -1
            addx 5
            addx -1
            addx 5
            addx -1
            addx 5
            addx -1
            addx 5
            addx -1
            addx -35
            addx 1
            addx 24
            addx -19
            addx 1
            addx 16
            addx -11
            noop
            noop
            addx 21
            addx -15
            noop
            noop
            addx -3
            addx 9
            addx 1
            addx -3
            addx 8
            addx 1
            addx 5
            noop
            noop
            noop
            noop
            noop
            addx -36
            noop
            addx 1
            addx 7
            noop
            noop
            noop
            addx 2
            addx 6
            noop
            noop
            noop
            noop
            noop
            addx 1
            noop
            noop
            addx 7
            addx 1
            noop
            addx -13
            addx 13
            addx 7
            noop
            addx 1
            addx -33
            noop
            noop
            noop
            addx 2
            noop
            noop
            noop
            addx 8
            noop
            addx -1
            addx 2
            addx 1
            noop
            addx 17
            addx -9
            addx 1
            addx 1
            addx -3
            addx 11
            noop
            noop
            addx 1
            noop
            addx 1
            noop
            noop
            addx -13
            addx -19
            addx 1
            addx 3
            addx 26
            addx -30
            addx 12
            addx -1
            addx 3
            addx 1
            noop
            noop
            noop
            addx -9
            addx 18
            addx 1
            addx 2
            noop
            noop
            addx 9
            noop
            noop
            noop
            addx -1
            addx 2
            addx -37
            addx 1
            addx 3
            noop
            addx 15
            addx -21
            addx 22
            addx -6
            addx 1
            noop
            addx 2
            addx 1
            noop
            addx -10
            noop
            noop
            addx 20
            addx 1
            addx 2
            addx 2
            addx -6
            addx -11
            noop
            noop
            noop
            """;

    @SuppressWarnings("unused")
    private static final String INPUT = """
            noop
            noop
            noop
            addx 3
            addx 20
            noop
            addx -12
            noop
            addx 4
            noop
            noop
            noop
            addx 1
            addx 2
            addx 5
            addx 16
            addx -14
            addx -25
            addx 30
            addx 1
            noop
            addx 5
            noop
            addx -38
            noop
            noop
            noop
            addx 3
            addx 2
            noop
            noop
            noop
            addx 5
            addx 5
            addx 2
            addx 13
            addx 6
            addx -16
            addx 2
            addx 5
            addx -15
            addx 16
            addx 7
            noop
            addx -2
            addx 2
            addx 5
            addx -39
            addx 4
            addx -2
            addx 2
            addx 7
            noop
            addx -2
            addx 17
            addx -10
            noop
            noop
            addx 5
            addx -1
            addx 6
            noop
            addx -2
            addx 5
            addx -8
            addx 12
            addx 3
            addx -2
            addx -19
            addx -16
            addx 2
            addx 5
            noop
            addx 25
            addx 7
            addx -29
            addx 3
            addx 4
            addx -4
            addx 9
            noop
            addx 2
            addx -20
            addx 23
            addx 1
            noop
            addx 5
            addx -10
            addx 14
            addx 2
            addx -1
            addx -38
            noop
            addx 20
            addx -15
            noop
            addx 7
            noop
            addx 26
            addx -25
            addx 2
            addx 7
            noop
            noop
            addx 2
            addx -5
            addx 6
            addx 5
            addx 2
            addx 8
            addx -3
            noop
            addx 3
            addx -2
            addx -38
            addx 13
            addx -6
            noop
            addx 1
            addx 5
            noop
            noop
            noop
            noop
            addx 2
            noop
            noop
            addx 7
            addx 3
            addx -2
            addx 2
            addx 5
            addx 2
            noop
            addx 1
            addx 5
            noop
            noop
            noop
            noop
            noop
            noop
            """;

    @Test
    public void test() {

    }
}