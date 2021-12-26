package done.advent2021;

import util.LineSupplier;

import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;

public class ArithmeticLogicUnit {

    static Instr[] instructions;
    static ALU alu;

    public static void main(String[] args) {
        try (Scanner in = new Scanner(INPUT)) {
            instructions = Stream.generate(new LineSupplier(in))
                                 .takeWhile(Objects::nonNull)
                                 .map(Instr::parse)
                                 .toArray(Instr[]::new);
            alu = new ALU(instructions);

            SerialGenerator growingSerialGenerator = new GrowingSerialGenerator();
            Stream.generate(growingSerialGenerator)
                  .map(alu::execute)
                  .filter(ALU::isValid)
                  .map(ALU::number)
                  .map(num -> IntStream.of(num).mapToObj(String::valueOf).collect(joining()))
                  .findFirst()
                  .ifPresent(System.out::println);

            SerialGenerator decreasingSerialGenerator = new DecreasingSerialGenerator();
            Stream.generate(decreasingSerialGenerator)
                  .map(alu::execute)
                  .filter(ALU::isValid)
                  .map(ALU::number)
                  .map(num -> IntStream.of(num).mapToObj(String::valueOf).collect(joining()))
                  .findFirst()
                  .ifPresent(System.out::println);
        }
    }

    interface SerialGenerator extends Supplier<int[]> {
        boolean hasNext(int[] number);
    }

    static class DecreasingSerialGenerator implements SerialGenerator {
        private final int[] lastSerial = new int[] {
                9,9,6,9,9,9,9,1,9,7,9,9,9,9 // 14-digits
        };
        private static final int SERIAL_LENGTH = 14;
        private static final int LAST_DIGIT = SERIAL_LENGTH - 1;
        private boolean wasLast = false;

        @Override
        public int[] get() {
            int[] result = Arrays.copyOf(lastSerial, lastSerial.length);
            for (int i = LAST_DIGIT; i >= 0; i--) {
                if (i == 2 || i == 3 || i == 6 || i == 7 || i == 9 || i == 10) continue;

                if (lastSerial[i] > 1) {
                    lastSerial[i]--;
                    break;
                }
                lastSerial[i] = 9;
            }
            return result;
        }

        @Override
        public boolean hasNext(int[] number) {
            boolean last = IntStream.of(number).limit(14).allMatch(d -> d == 1);
            if (last) {
                if (wasLast) {
                    return false;
                }
                wasLast = true;
                return true;
            }
            return true;
        }
    }

    static class GrowingSerialGenerator implements SerialGenerator {
        private final int[] lastSerial = new int[] {
                2,1,1,4,1,1,9,1,1,1,3,1,1,1 // 14-digits
        };
        private static final int SERIAL_LENGTH = 14;
        private static final int LAST_DIGIT = SERIAL_LENGTH - 1;
        private boolean wasLast = false;

        @Override
        public int[] get() {
            int[] result = Arrays.copyOf(lastSerial, lastSerial.length);
            for (int i = LAST_DIGIT; i >= 0; i--) {
                if (i == 2 || i == 3 || i == 6 || i == 7 || i == 9 || i == 10) continue;

                if (lastSerial[i] < 9) {
                    lastSerial[i]++;
                    break;
                }
                lastSerial[i] = 1;
            }
            return result;
        }

        @Override
        public boolean hasNext(int[] number) {
            boolean last = IntStream.of(number).limit(14).allMatch(d -> d == 9);
            if (last) {
                if (wasLast) {
                    return false;
                }
                wasLast = true;
                return true;
            }
            return true;
        }
    }

    static final class ALU {
        private final int[] registers = new int[4];
        private final int[] input = new int[14];
        private final Instr[] instructions;
        private int inputPos = 0;
        private final StringBuilder buffer = new StringBuilder();

        ALU(Instr[] instructions) {
            this.instructions = instructions;
        }

        void input(int[] number) {
            if (number.length > 14) {
                throw new IllegalArgumentException("Illegal input number length: " + number.length);
            }
            System.arraycopy(number, 0, input, 0, number.length);
            reset();
        }

        private void reset() {
            Arrays.fill(registers, 0);
            inputPos = 0;
        }

        void execute() {
            Stream.of(instructions).forEach(instr -> instr.execute(this));
        }

        ALU execute(int[] number) {
            ALU alu = new ALU(instructions);
            alu.input(number);
            alu.execute();
            return alu;
        }

        boolean isValid() {
            return z() == 0;
        }

        int[] number() {
            return input;
        }

        int z() {
            return get(Register.Z);
        }

        int get(Register reg) {
            return registers[reg.index()];
        }

        void inp(Register reg) {
            if (inputPos > 10) {
                buffer.append(this).append("|");
            }
            registers[reg.index()] = input[inputPos++];
        }

        void add(Register r1, Register r2) {
            registers[r1.index()] += registers[r2.index()];
        }

        void add(Register r1, int n) {
            registers[r1.index()] += n;
        }

        void mul(Register r1, Register r2) {
            registers[r1.index()] *= registers[r2.index()];
        }

        void mul(Register r1, int n) {
            registers[r1.index()] *= n;
        }

        void div(Register r1, Register r2) {
            registers[r1.index()] /= registers[r2.index()];
        }

        void div(Register r1, int n) {
            registers[r1.index()] /= n;
        }

        void mod(Register r1, Register r2) {
            registers[r1.index()] %= registers[r2.index()];
        }

        void mod(Register r1, int n) {
            registers[r1.index()] %= n;
        }

        void eql(Register r1, Register r2) {
            registers[r1.index()] = (registers[r1.index()] == registers[r2.index()] ? 1 : 0);
        }

        void eql(Register r1, int n) {
            registers[r1.index()] = (registers[r1.index()] == n ? 1 : 0);
        }

        @Override
        public String toString() {
            return buffer + "x=" + get(Register.X) + ",y=" + get(Register.Y) + ",z=" + get(Register.Z) + ",w=" + get(Register.W);
        }
    }

    record inp(Register reg) implements Instr {
        @Override
        public void execute(ALU alu) {
            alu.inp(reg);
        }
    }

    record add(Register r1, Register r2) implements Instr {
        @Override
        public void execute(ALU alu) {
            alu.add(r1, r2);
        }

        static Instr parse(Register reg, String other) {
            Register r2 = Register.parse(other);
            return r2 != null
                   ? new add(reg, r2)
                   : new addn(reg, Integer.parseInt(other));
        }
    }

    record addn(Register r1, int n) implements Instr {
        @Override
        public void execute(ALU alu) {
            alu.add(r1, n);
        }
    }

    record mul(Register r1, Register r2) implements Instr {
        @Override
        public void execute(ALU alu) {
            alu.mul(r1, r2);
        }

        static Instr parse(Register reg, String other) {
            Register r2 = Register.parse(other);
            return r2 != null
                   ? new mul(reg, r2)
                   : new muln(reg, Integer.parseInt(other));
        }
    }

    record muln(Register r1, int n) implements Instr {
        @Override
        public void execute(ALU alu) {
            alu.mul(r1, n);
        }
    }

    record div(Register r1, Register r2) implements Instr {
        @Override
        public void execute(ALU alu) {
            alu.div(r1, r2);
        }

        static Instr parse(Register reg, String other) {
            Register r2 = Register.parse(other);
            return r2 != null
                   ? new div(reg, r2)
                   : new divn(reg, Integer.parseInt(other));
        }
    }

    record divn(Register r1, int n) implements Instr {
        @Override
        public void execute(ALU alu) {
            alu.div(r1, n);
        }
    }

    record mod(Register r1, Register r2) implements Instr {
        @Override
        public void execute(ALU alu) {
            alu.mod(r1, r2);
        }

        static Instr parse(Register reg, String other) {
            Register r2 = Register.parse(other);
            return r2 != null
                   ? new mod(reg, r2)
                   : new modn(reg, Integer.parseInt(other));
        }
    }

    record modn(Register r1, int n) implements Instr {
        @Override
        public void execute(ALU alu) {
            alu.mod(r1, n);
        }
    }

    record eql(Register r1, Register r2) implements Instr {
        @Override
        public void execute(ALU alu) {
            alu.eql(r1, r2);
        }

        static Instr parse(Register reg, String other) {
            Register r2 = Register.parse(other);
            return r2 != null
                   ? new eql(reg, r2)
                   : new eqln(reg, Integer.parseInt(other));
        }
    }

    record eqln(Register r1, int n) implements Instr {
        @Override
        public void execute(ALU alu) {
            alu.eql(r1, n);
        }
    }

    interface Instr {
        void execute(ALU alu);

        static Instr parse(String line) {
            String[] parts = line.split("[ ]");
            Register reg = Register.parse(parts[1]);
            return switch(parts[0]) {
                case "inp" -> new inp(reg);
                case "add" -> add.parse(reg, parts[2]);
                case "mul" -> mul.parse(reg, parts[2]);
                case "div" -> div.parse(reg, parts[2]);
                case "mod" -> mod.parse(reg, parts[2]);
                case "eql" -> eql.parse(reg, parts[2]);
                default -> throw new IllegalArgumentException("Cannot parse instruction: " + line);
            };
        }
    }

    enum Register {
        X, Y, Z, W;

        int index() {
            return ordinal();
        }

        static Register parse(String reg) {
            return Stream.of(values())
                         .filter(v -> v.name().equals(reg.toUpperCase()))
                         .findFirst()
                         .orElse(null);
        }
    }

    @SuppressWarnings("unused")
    private static final String INPUT1 = """
            inp x
            mul x -1""";

    @SuppressWarnings("unused")
    private static final String INPUT2 = """
            inp z
            inp x
            mul z 3
            eql z x""";

    @SuppressWarnings("unused")
    private static final String INPUT3 = """
            inp w
            mul x 0     ; x = 0
            add x z     ; x = 0
            mod x 26    ; x = 0
            div z 1     ; z = 0
            add x 13    ; x = 13
            eql x w     ; x = 0
            eql x 0     ; x = 1
            mul y 0     ; y = 0
            add y 25    ; y = 25
            mul y x     ; y = 25
            add y 1     ; y = 26
            mul z y     ; z = 0
            mul y 0     ; y = 0
            add y w     ; y = w
            add y 0     ; y = w
            mul y x     ; y = w
            add z y     ; z = w
            inp w
            mul x 0     ;
            add x z     ; x = w(0)
            mod x 26    ; x = w(0)
            div z 1     ; z = w(0)
            add x 11    ; x = w(0) + 11
            eql x w
            eql x 0     ; x = 1
            mul y 0
            add y 25    ; y = 25
            mul y x     ; y = 25
            add y 1     ; y = 26
            mul z y     ; z = 26 * w(0)
            mul y 0
            add y w     ; y = w(1)
            add y 3     ; y = w(1) + 3
            mul y x     ; y = w(1) + 3
            add z y     ; z = 26 * w(0) + w(1) + 3
            inp w
            mul x 0
            add x z     ; x = 26 * w(0) + w(1) + 3
            mod x 26    ; x = w(1) + 3
            div z 1     ; z = 26 * w(0) + w(1) + 3
            add x 14    ; x = w(1) + 3 + 14
            eql x w
            eql x 0     ; x = 1
            mul y 0     ; y = 0
            add y 25    ; y = 25
            mul y x
            add y 1     ; y = 26
            mul z y     ; z = 26 * 26 * w(0) + 26 * (w(1) + 3)
            mul y 0
            add y w     ; y = w(2)
            add y 8     ; y = w(2) + 8
            mul y x     ; y = w(2) + 8
            add z y     ; z = 26 * 26 * w(0) + 26 * (w(1) + 3) + w(2) + 8
            inp w
            mul x 0
            add x z
            mod x 26    ; x = w(2) + 8
            div z 26    ; z = 26 * w(0) + w(1) + 3
            add x -5    ; x = w(2) + 3
            eql x w
            eql x 0     ; x = w(3) == (w(2) + 3) ? 0 : 1
            mul y 0
            add y 25
            mul y x     ; y = 25 * x
            add y 1     ; y = 25 * (w(3) != w(2) + 3) + 1
            mul z y     ; z = (25 * (w(3) != w(2) + 3) + 1) * (26 * w(0) + w(1) + 3)
            mul y 0
            add y w     ; y = w(3)
            add y 5     ; y = w(3) + 5
            mul y x     ; y = (w(3) + 5) * (w(3) != w(2) + 3)
            add z y     ; z = (25 * (w(3) != w(2) + 3) + 1) * (26 * w(0) + w(1) + 3) + (w(3) + 5) * (w(3) != w(2) + 3)
            inp w
            mul x 0
            add x z
            mod x 26    ; x = (w(3) != w(2) + 3) ? (w(3) + 5) : (w(1) + 3)
            div z 1
            add x 14    ; x = (w(3) != w(2) + 3) ? (w(3) + 5) : (w(1) + 3) + 14
            eql x w
            eql x 0     ; x = 1
            mul y 0
            add y 25
            mul y x
            add y 1     ; y = 26
            mul z y     ; z = 26 * ((w(3) != w(2) + 3) ? 26 * (26 * w(0) + w(1) + 3) + (w(3) + 5) : 26 * w(0) + w(1) + 3)
            mul y 0
            add y w     ; y = w(4)
            add y 13    ; y = w(4) + 13
            mul y x
            add z y     ; z = 26 * ((w(3) != w(2) + 3) ? 26 * (26 * w(0) + w(1) + 3) + (w(3) + 5) : 26 * w(0) + w(1) + 3) + w(4) + 13
            inp w
            mul x 0
            add x z
            mod x 26    ; x = w(4) + 13
            div z 1
            add x 10    ; x = w(4) + 23
            eql x w
            eql x 0     ; x = 1
            mul y 0
            add y 25
            mul y x
            add y 1     ; y = 26
            mul z y     ; z = 26 * (26 * ((w(3) != w(2) + 3) ? 26 * (26 * w(0) + w(1) + 3) + (w(3) + 5) : 26 * w(0) + w(1) + 3) + w(4) + 13)
            mul y 0
            add y w
            add y 9     ; y = w(5) + 9
            mul y x
            add z y     ; z = 26 * (26 * ((w(3) != w(2) + 3) ? 26 * (26 * w(0) + w(1) + 3) + (w(3) + 5) : 26 * w(0) + w(1) + 3) + w(4) + 13) + w(5) + 9
            inp w
            mul x 0
            add x z
            mod x 26    ; x = w(5) + 9
            div z 1
            add x 12    ; x = w(5) + 21
            eql x w
            eql x 0     ; x = 1
            mul y 0
            add y 25
            mul y x
            add y 1     ; y = 26
            mul z y     ; z = 26 * (26 * (26 * ((w(3) != w(2) + 3) ? 26 * (26 * w(0) + w(1) + 3) + (w(3) + 5) : 26 * w(0) + w(1) + 3) + w(4) + 13) + w(5) + 9)
            mul y 0
            add y w
            add y 6     ; y = w(6) + 6
            mul y x
            add z y     ; z = 26 * (26 * (26 * ((w(3) != w(2) + 3) ? 26 * (26 * w(0) + w(1) + 3) + (w(3) + 5) : 26 * w(0) + w(1) + 3) + w(4) + 13) + w(5) + 9) + w(6) + 6
            inp w
            mul x 0
            add x z
            mod x 26    ; x = w(6) + 6
            div z 26    ; z = 26 * (26 * ((w(3) != w(2) + 3) ? 26 * (26 * w(0) + w(1) + 3) + (w(3) + 5) : 26 * w(0) + w(1) + 3) + w(4) + 13) + w(5) + 9
            add x -14   ; x = w(6) - 8
            eql x w     ; x = (w(7) == w(6) - 8)
            eql x 0     ; x = (w(7) != w(6) - 8)
            mul y 0
            add y 25
            mul y x
            add y 1     ; y = (w(7) != w(6) - 8) ? 26 : 1
            mul z y     ; z = (w(7) != w(6) - 8) ? 26 * (26 * (26 * ((w(3) != w(2) + 3) ? 26 * (26 * w(0) + w(1) + 3) + (w(3) + 5) : 26 * w(0) + w(1) + 3) + w(4) + 13) + w(5) + 9) : 26 * (26 * ((w(3) != w(2) + 3) ? 26 * (26 * w(0) + w(1) + 3) + (w(3) + 5) : 26 * w(0) + w(1) + 3) + w(4) + 13) + w(5) + 9
            mul y 0
            add y w
            add y 1     ; y = w(7) + 1
            mul y x     ; (w(7) != w(6) - 8) ? w(7) + 1 : 0
            add z y     ; z = (w(7) != w(6) - 8) ? 26 * (26 * (26 * ((w(3) != w(2) + 3) ? 26 * (26 * w(0) + w(1) + 3) + (w(3) + 5) : 26 * w(0) + w(1) + 3) + w(4) + 13) + w(5) + 9) + w(7) + 1 : 26 * (26 * ((w(3) != w(2) + 3) ? 26 * (26 * w(0) + w(1) + 3) + (w(3) + 5) : 26 * w(0) + w(1) + 3) + w(4) + 13) + w(5) + 9
            inp w
            mul x 0
            add x z
            mod x 26    ; x = (w(7) != w(6) - 8) ? w(7) + 1 : w(5) + 9
            div z 26    ; z = (w(7) != w(6) - 8) ? 26 * (26 * ((w(3) != w(2) + 3) ? 26 * (26 * w(0) + w(1) + 3) + (w(3) + 5) : 26 * w(0) + w(1) + 3) + w(4) + 13) + w(5) + 9 : 26 * ((w(3) != w(2) + 3) ? 26 * (26 * w(0) + w(1) + 3) + (w(3) + 5) : 26 * w(0) + w(1) + 3) + w(4) + 13
            add x -8    ; x = (w(7) != w(6) - 8) ? w(7) - 9 : w(5) + 2
            eql x w     ; x = w(8) == ((w(7) != w(6) - 8) ? w(7) - 9 : w(5) + 2) ? 1 : 0
            eql x 0     ; x = w(8) != ((w(7) != w(6) - 8) ? w(7) - 9 : w(5) + 2) ? 1 : 0
            mul y 0
            add y 25
            mul y x
            add y 1     ; y = (w(8) != ((w(7) != w(6) - 8) ? w(7) - 9 : w(5) + 2)) ? 26 : 1
            mul z y     ; z = (w(8) != ((w(7) != w(6) - 8) ? w(7) - 9 : w(5) + 2)) ? 26 * ((w(7) != w(6) - 8) ? 26 * (26 * ((w(3) != w(2) + 3) ? 26 * (26 * w(0) + w(1) + 3) + (w(3) + 5) : 26 * w(0) + w(1) + 3) + w(4) + 13) + w(5) + 9 : 26 * ((w(3) != w(2) + 3) ? 26 * (26 * w(0) + w(1) + 3) + (w(3) + 5) : 26 * w(0) + w(1) + 3) + w(4) + 13) : (w(7) != w(6) - 8) ? 26 * (26 * ((w(3) != w(2) + 3) ? 26 * (26 * w(0) + w(1) + 3) + (w(3) + 5) : 26 * w(0) + w(1) + 3) + w(4) + 13) + w(5) + 9 : 26 * ((w(3) != w(2) + 3) ? 26 * (26 * w(0) + w(1) + 3) + (w(3) + 5) : 26 * w(0) + w(1) + 3) + w(4) + 13
            mul y 0
            add y w
            add y 1     ; y = w(8) + 1
            mul y x     ; y = (w(8) != ((w(7) != w(6) - 8) ? w(7) - 9 : w(5) + 2)) ? w(8) + 1 : 0
            add z y     ; z = (w(8) != ((w(7) != w(6) - 8) ? w(7) - 9 : w(5) + 2)) ? 26 * ((w(7) != w(6) - 8) ? 26 * (26 * ((w(3) != w(2) + 3) ? 26 * (26 * w(0) + w(1) + 3) + (w(3) + 5) : 26 * w(0) + w(1) + 3) + w(4) + 13) + w(5) + 9 : 26 * ((w(3) != w(2) + 3) ? 26 * (26 * w(0) + w(1) + 3) + (w(3) + 5) : 26 * w(0) + w(1) + 3) + w(4) + 13) + w(8) + 1 : (w(7) != w(6) - 8) ? 26 * (26 * ((w(3) != w(2) + 3) ? 26 * (26 * w(0) + w(1) + 3) + (w(3) + 5) : 26 * w(0) + w(1) + 3) + w(4) + 13) + w(5) + 9 : 26 * ((w(3) != w(2) + 3) ? 26 * (26 * w(0) + w(1) + 3) + (w(3) + 5) : 26 * w(0) + w(1) + 3) + w(4) + 13
            inp w
            mul x 0
            add x z
            mod x 26    ; x = (w(8) != ((w(7) != w(6) - 8) ? w(7) - 9 : w(5) + 2)) ? w(8) + 1 : ((w(7) != w(6) - 8) ? w(5) + 9 : w(4) + 13)
            div z 1
            add x 13    ; x = (w(8) != ((w(7) != w(6) - 8) ? w(7) - 9 : w(5) + 2)) ? w(8) + 14 : ((w(7) != w(6) - 8) ? w(5) + 22 : w(4) + 26)
            eql x w
            eql x 0     ; x = 1
            mul y 0
            add y 25
            mul y x
            add y 1     ; y = 26
            mul z y     ; z = 26 * (w(8) != ((w(7) != w(6) - 8) ? w(7) - 9 : w(5) + 2)) ? 26 * ((w(7) != w(6) - 8) ? 26 * (26 * ((w(3) != w(2) + 3) ? 26 * (26 * w(0) + w(1) + 3) + (w(3) + 5) : 26 * w(0) + w(1) + 3) + w(4) + 13) + w(5) + 9 : 26 * ((w(3) != w(2) + 3) ? 26 * (26 * w(0) + w(1) + 3) + (w(3) + 5) : 26 * w(0) + w(1) + 3) + w(4) + 13) + w(8) + 1 : (w(7) != w(6) - 8) ? 26 * (26 * ((w(3) != w(2) + 3) ? 26 * (26 * w(0) + w(1) + 3) + (w(3) + 5) : 26 * w(0) + w(1) + 3) + w(4) + 13) + w(5) + 9 : 26 * ((w(3) != w(2) + 3) ? 26 * (26 * w(0) + w(1) + 3) + (w(3) + 5) : 26 * w(0) + w(1) + 3) + w(4) + 13)
            mul y 0
            add y w
            add y 2     ; y = w(9) + 2
            mul y x
            add z y     ; z = 26 * (w(8) != ((w(7) != w(6) - 8) ? w(7) - 9 : w(5) + 2)) ? 26 * ((w(7) != w(6) - 8) ? 26 * (26 * ((w(3) != w(2) + 3) ? 26 * (26 * w(0) + w(1) + 3) + (w(3) + 5) : 26 * w(0) + w(1) + 3) + w(4) + 13) + w(5) + 9 : 26 * ((w(3) != w(2) + 3) ? 26 * (26 * w(0) + w(1) + 3) + (w(3) + 5) : 26 * w(0) + w(1) + 3) + w(4) + 13) + w(8) + 1 : (w(7) != w(6) - 8) ? 26 * (26 * ((w(3) != w(2) + 3) ? 26 * (26 * w(0) + w(1) + 3) + (w(3) + 5) : 26 * w(0) + w(1) + 3) + w(4) + 13) + w(5) + 9 : 26 * ((w(3) != w(2) + 3) ? 26 * (26 * w(0) + w(1) + 3) + (w(3) + 5) : 26 * w(0) + w(1) + 3) + w(4) + 13) + w(9) + 2
            inp w
            mul x 0
            add x z
            mod x 26    ; x = w(9) + 2
            div z 26    ; z = w(8) != ((w(7) != w(6) - 8) ? w(7) - 9 : w(5) + 2)) ? 26 * ((w(7) != w(6) - 8) ? 26 * (26 * ((w(3) != w(2) + 3) ? 26 * (26 * w(0) + w(1) + 3) + (w(3) + 5) : 26 * w(0) + w(1) + 3) + w(4) + 13) + w(5) + 9 : 26 * ((w(3) != w(2) + 3) ? 26 * (26 * w(0) + w(1) + 3) + (w(3) + 5) : 26 * w(0) + w(1) + 3) + w(4) + 13) + w(8) + 1 : (w(7) != w(6) - 8) ? 26 * (26 * ((w(3) != w(2) + 3) ? 26 * (26 * w(0) + w(1) + 3) + (w(3) + 5) : 26 * w(0) + w(1) + 3) + w(4) + 13) + w(5) + 9 : 26 * ((w(3) != w(2) + 3) ? 26 * (26 * w(0) + w(1) + 3) + (w(3) + 5) : 26 * w(0) + w(1) + 3) + w(4) + 13
            add x 0
            eql x w     ; x = (w(10) == w(9) + 2) ? 1 : 0
            eql x 0     ; x = (w(10) != w(9) + 2) ? 1 : 0
            mul y 0
            add y 25
            mul y x
            add y 1     ; y = (w(10) != w(9) + 2) ? 26 : 1
            mul z y     ; z = (w(10) != w(9) + 2) ? 26*Z : Z
            mul y 0
            add y w
            add y 7     ; y = w(10) + 7
            mul y x     ; y = ((w(10) != w(9) + 2) ? 26*Z : Z) * w(10) + 7
            add z y     ; z = z + ((w(10) != w(9) + 2) ? 26*Z : Z) * w(10) + 7
            inp w
            mul x 0
            add x z     ; x = z
            mod x 26    ; x = z % 26
            div z 26    ; z = z / 26 ; z == 0
            add x -5    ; x = (z % 26) - 5
            eql x w
            eql x 0
            mul y 0
            add y 25
            mul y x
            add y 1
            mul z y     ; z = x == 1 ? 26 * z : z
            mul y 0
            add y w
            add y 5     ; y = w(11) + 5
            mul y x     ; x = x * (w(11) + 5)
            add z y
            inp w       ; 13th digit
            mul x 0
            add x z
            mod x 26    ; x = z % 26
            div z 26    ; z = z / 26        ; 2*26 <= z <= 10*26
            add x -9    ; x = (z % 26) - 9  ; 10 <= z % 26 <= 18
            eql x w
            eql x 0     ; x = w(12) != x ? 1 : 0
            mul y 0
            add y 25
            mul y x
            add y 1     ; y = 25 * x + 1    ; x == 0, 2 <= z <= 10
            mul z y     ; z = z * (25 * x + 1) ; x == 0 => z = z * 1
            mul y 0
            add y w
            add y 8     ; y = w(12) + 8
            mul y x     ; y = x * (w(12) + 8) ; x != 0 ? 0 <= z <= 1 : 2 <= z <= 10
            add z y     ; 2 <= z + y <= 10""";

    @SuppressWarnings("unused")
    private static final String INPUT = """
            inp w
            mul x 0     ; x = 0
            add x z     ; x = 0
            mod x 26    ; x = 0
            div z 1     ; z = 0
            add x 13    ; x = 13
            eql x w     ; x = 0
            eql x 0     ; x = 1
            mul y 0     ; y = 0
            add y 25    ; y = 25
            mul y x     ; y = 25
            add y 1     ; y = 26
            mul z y     ; z = 0
            mul y 0     ; y = 0
            add y w     ; y = w
            add y 0     ; y = w
            mul y x     ; y = w
            add z y     ; z = w
            inp w
            mul x 0     ;
            add x z     ; x = w(0)
            mod x 26    ; x = w(0)
            div z 1     ; z = w(0)
            add x 11    ; x = w(0) + 11
            eql x w
            eql x 0     ; x = 1
            mul y 0
            add y 25    ; y = 25
            mul y x     ; y = 25
            add y 1     ; y = 26
            mul z y     ; z = 26 * w(0)
            mul y 0
            add y w     ; y = w(1)
            add y 3     ; y = w(1) + 3
            mul y x     ; y = w(1) + 3
            add z y     ; z = 26 * w(0) + w(1) + 3
            inp w
            mul x 0
            add x z     ; x = 26 * w(0) + w(1) + 3
            mod x 26    ; x = w(1) + 3
            div z 1     ; z = 26 * w(0) + w(1) + 3
            add x 14    ; x = w(1) + 3 + 14
            eql x w
            eql x 0     ; x = 1
            mul y 0     ; y = 0
            add y 25    ; y = 25
            mul y x
            add y 1     ; y = 26
            mul z y     ; z = 26 * 26 * w(0) + 26 * (w(1) + 3)
            mul y 0
            add y w     ; y = w(2)
            add y 8     ; y = w(2) + 8
            mul y x     ; y = w(2) + 8
            add z y     ; z = 26 * 26 * w(0) + 26 * (w(1) + 3) + w(2) + 8
            inp w
            mul x 0
            add x z
            mod x 26    ; x = w(2) + 8
            div z 26    ; z = 26 * w(0) + w(1) + 3
            add x -5    ; x = w(2) + 3
            eql x w
            eql x 0     ; x = w(3) == (w(2) + 3) ? 0 : 1
            mul y 0
            add y 25
            mul y x     ; y = 25 * x
            add y 1     ; y = 25 * (w(3) != w(2) + 3) + 1
            mul z y     ; z = (25 * (w(3) != w(2) + 3) + 1) * (26 * w(0) + w(1) + 3)
            mul y 0
            add y w     ; y = w(3)
            add y 5     ; y = w(3) + 5
            mul y x     ; y = (w(3) + 5) * (w(3) != w(2) + 3)
            add z y     ; z = (25 * (w(3) != w(2) + 3) + 1) * (26 * w(0) + w(1) + 3) + (w(3) + 5) * (w(3) != w(2) + 3)
            inp w
            mul x 0
            add x z
            mod x 26    ; x = (w(3) != w(2) + 3) ? (w(3) + 5) : (w(1) + 3)
            div z 1
            add x 14    ; x = (w(3) != w(2) + 3) ? (w(3) + 5) : (w(1) + 3) + 14
            eql x w
            eql x 0     ; x = 1
            mul y 0
            add y 25
            mul y x
            add y 1     ; y = 26
            mul z y     ; z = 26 * ((w(3) != w(2) + 3) ? 26 * (26 * w(0) + w(1) + 3) + (w(3) + 5) : 26 * w(0) + w(1) + 3)
            mul y 0
            add y w     ; y = w(4)
            add y 13    ; y = w(4) + 13
            mul y x
            add z y     ; z = 26 * ((w(3) != w(2) + 3) ? 26 * (26 * w(0) + w(1) + 3) + (w(3) + 5) : 26 * w(0) + w(1) + 3) + w(4) + 13
            inp w
            mul x 0
            add x z
            mod x 26    ; x = w(4) + 13
            div z 1
            add x 10    ; x = w(4) + 23
            eql x w
            eql x 0     ; x = 1
            mul y 0
            add y 25
            mul y x
            add y 1     ; y = 26
            mul z y     ; z = 26 * (26 * ((w(3) != w(2) + 3) ? 26 * (26 * w(0) + w(1) + 3) + (w(3) + 5) : 26 * w(0) + w(1) + 3) + w(4) + 13)
            mul y 0
            add y w
            add y 9     ; y = w(5) + 9
            mul y x
            add z y     ; z = 26 * (26 * ((w(3) != w(2) + 3) ? 26 * (26 * w(0) + w(1) + 3) + (w(3) + 5) : 26 * w(0) + w(1) + 3) + w(4) + 13) + w(5) + 9
            inp w
            mul x 0
            add x z
            mod x 26    ; x = w(5) + 9
            div z 1
            add x 12    ; x = w(5) + 21
            eql x w
            eql x 0     ; x = 1
            mul y 0
            add y 25
            mul y x
            add y 1     ; y = 26
            mul z y     ; z = 26 * (26 * (26 * ((w(3) != w(2) + 3) ? 26 * (26 * w(0) + w(1) + 3) + (w(3) + 5) : 26 * w(0) + w(1) + 3) + w(4) + 13) + w(5) + 9)
            mul y 0
            add y w
            add y 6     ; y = w(6) + 6
            mul y x
            add z y     ; z = 26 * (26 * (26 * ((w(3) != w(2) + 3) ? 26 * (26 * w(0) + w(1) + 3) + (w(3) + 5) : 26 * w(0) + w(1) + 3) + w(4) + 13) + w(5) + 9) + w(6) + 6
            inp w
            mul x 0
            add x z
            mod x 26    ; x = w(6) + 6
            div z 26    ; z = 26 * (26 * ((w(3) != w(2) + 3) ? 26 * (26 * w(0) + w(1) + 3) + (w(3) + 5) : 26 * w(0) + w(1) + 3) + w(4) + 13) + w(5) + 9
            add x -14   ; x = w(6) - 8
            eql x w     ; x = (w(7) == w(6) - 8)
            eql x 0     ; x = (w(7) != w(6) - 8)
            mul y 0
            add y 25
            mul y x
            add y 1     ; y = (w(7) != w(6) - 8) ? 26 : 1
            mul z y     ; z = (w(7) != w(6) - 8) ? 26 * (26 * (26 * ((w(3) != w(2) + 3) ? 26 * (26 * w(0) + w(1) + 3) + (w(3) + 5) : 26 * w(0) + w(1) + 3) + w(4) + 13) + w(5) + 9) : 26 * (26 * ((w(3) != w(2) + 3) ? 26 * (26 * w(0) + w(1) + 3) + (w(3) + 5) : 26 * w(0) + w(1) + 3) + w(4) + 13) + w(5) + 9
            mul y 0
            add y w
            add y 1     ; y = w(7) + 1
            mul y x     ; (w(7) != w(6) - 8) ? w(7) + 1 : 0
            add z y     ; z = (w(7) != w(6) - 8) ? 26 * (26 * (26 * ((w(3) != w(2) + 3) ? 26 * (26 * w(0) + w(1) + 3) + (w(3) + 5) : 26 * w(0) + w(1) + 3) + w(4) + 13) + w(5) + 9) + w(7) + 1 : 26 * (26 * ((w(3) != w(2) + 3) ? 26 * (26 * w(0) + w(1) + 3) + (w(3) + 5) : 26 * w(0) + w(1) + 3) + w(4) + 13) + w(5) + 9
            inp w
            mul x 0
            add x z
            mod x 26    ; x = (w(7) != w(6) - 8) ? w(7) + 1 : w(5) + 9
            div z 26    ; z = (w(7) != w(6) - 8) ? 26 * (26 * ((w(3) != w(2) + 3) ? 26 * (26 * w(0) + w(1) + 3) + (w(3) + 5) : 26 * w(0) + w(1) + 3) + w(4) + 13) + w(5) + 9 : 26 * ((w(3) != w(2) + 3) ? 26 * (26 * w(0) + w(1) + 3) + (w(3) + 5) : 26 * w(0) + w(1) + 3) + w(4) + 13
            add x -8    ; x = (w(7) != w(6) - 8) ? w(7) - 9 : w(5) + 2
            eql x w     ; x = w(8) == ((w(7) != w(6) - 8) ? w(7) - 9 : w(5) + 2) ? 1 : 0
            eql x 0     ; x = w(8) != ((w(7) != w(6) - 8) ? w(7) - 9 : w(5) + 2) ? 1 : 0
            mul y 0
            add y 25
            mul y x
            add y 1     ; y = (w(8) != ((w(7) != w(6) - 8) ? w(7) - 9 : w(5) + 2)) ? 26 : 1
            mul z y     ; z = (w(8) != ((w(7) != w(6) - 8) ? w(7) - 9 : w(5) + 2)) ? 26 * ((w(7) != w(6) - 8) ? 26 * (26 * ((w(3) != w(2) + 3) ? 26 * (26 * w(0) + w(1) + 3) + (w(3) + 5) : 26 * w(0) + w(1) + 3) + w(4) + 13) + w(5) + 9 : 26 * ((w(3) != w(2) + 3) ? 26 * (26 * w(0) + w(1) + 3) + (w(3) + 5) : 26 * w(0) + w(1) + 3) + w(4) + 13) : (w(7) != w(6) - 8) ? 26 * (26 * ((w(3) != w(2) + 3) ? 26 * (26 * w(0) + w(1) + 3) + (w(3) + 5) : 26 * w(0) + w(1) + 3) + w(4) + 13) + w(5) + 9 : 26 * ((w(3) != w(2) + 3) ? 26 * (26 * w(0) + w(1) + 3) + (w(3) + 5) : 26 * w(0) + w(1) + 3) + w(4) + 13
            mul y 0
            add y w
            add y 1     ; y = w(8) + 1
            mul y x     ; y = (w(8) != ((w(7) != w(6) - 8) ? w(7) - 9 : w(5) + 2)) ? w(8) + 1 : 0
            add z y     ; z = (w(8) != ((w(7) != w(6) - 8) ? w(7) - 9 : w(5) + 2)) ? 26 * ((w(7) != w(6) - 8) ? 26 * (26 * ((w(3) != w(2) + 3) ? 26 * (26 * w(0) + w(1) + 3) + (w(3) + 5) : 26 * w(0) + w(1) + 3) + w(4) + 13) + w(5) + 9 : 26 * ((w(3) != w(2) + 3) ? 26 * (26 * w(0) + w(1) + 3) + (w(3) + 5) : 26 * w(0) + w(1) + 3) + w(4) + 13) + w(8) + 1 : (w(7) != w(6) - 8) ? 26 * (26 * ((w(3) != w(2) + 3) ? 26 * (26 * w(0) + w(1) + 3) + (w(3) + 5) : 26 * w(0) + w(1) + 3) + w(4) + 13) + w(5) + 9 : 26 * ((w(3) != w(2) + 3) ? 26 * (26 * w(0) + w(1) + 3) + (w(3) + 5) : 26 * w(0) + w(1) + 3) + w(4) + 13
            inp w
            mul x 0
            add x z
            mod x 26    ; x = (w(8) != ((w(7) != w(6) - 8) ? w(7) - 9 : w(5) + 2)) ? w(8) + 1 : ((w(7) != w(6) - 8) ? w(5) + 9 : w(4) + 13)
            div z 1
            add x 13    ; x = (w(8) != ((w(7) != w(6) - 8) ? w(7) - 9 : w(5) + 2)) ? w(8) + 14 : ((w(7) != w(6) - 8) ? w(5) + 22 : w(4) + 26)
            eql x w
            eql x 0     ; x = 1
            mul y 0
            add y 25
            mul y x
            add y 1     ; y = 26
            mul z y     ; z = 26 * (w(8) != ((w(7) != w(6) - 8) ? w(7) - 9 : w(5) + 2)) ? 26 * ((w(7) != w(6) - 8) ? 26 * (26 * ((w(3) != w(2) + 3) ? 26 * (26 * w(0) + w(1) + 3) + (w(3) + 5) : 26 * w(0) + w(1) + 3) + w(4) + 13) + w(5) + 9 : 26 * ((w(3) != w(2) + 3) ? 26 * (26 * w(0) + w(1) + 3) + (w(3) + 5) : 26 * w(0) + w(1) + 3) + w(4) + 13) + w(8) + 1 : (w(7) != w(6) - 8) ? 26 * (26 * ((w(3) != w(2) + 3) ? 26 * (26 * w(0) + w(1) + 3) + (w(3) + 5) : 26 * w(0) + w(1) + 3) + w(4) + 13) + w(5) + 9 : 26 * ((w(3) != w(2) + 3) ? 26 * (26 * w(0) + w(1) + 3) + (w(3) + 5) : 26 * w(0) + w(1) + 3) + w(4) + 13)
            mul y 0
            add y w
            add y 2     ; y = w(9) + 2
            mul y x
            add z y     ; z = 26 * (w(8) != ((w(7) != w(6) - 8) ? w(7) - 9 : w(5) + 2)) ? 26 * ((w(7) != w(6) - 8) ? 26 * (26 * ((w(3) != w(2) + 3) ? 26 * (26 * w(0) + w(1) + 3) + (w(3) + 5) : 26 * w(0) + w(1) + 3) + w(4) + 13) + w(5) + 9 : 26 * ((w(3) != w(2) + 3) ? 26 * (26 * w(0) + w(1) + 3) + (w(3) + 5) : 26 * w(0) + w(1) + 3) + w(4) + 13) + w(8) + 1 : (w(7) != w(6) - 8) ? 26 * (26 * ((w(3) != w(2) + 3) ? 26 * (26 * w(0) + w(1) + 3) + (w(3) + 5) : 26 * w(0) + w(1) + 3) + w(4) + 13) + w(5) + 9 : 26 * ((w(3) != w(2) + 3) ? 26 * (26 * w(0) + w(1) + 3) + (w(3) + 5) : 26 * w(0) + w(1) + 3) + w(4) + 13) + w(9) + 2
            inp w
            mul x 0
            add x z
            mod x 26    ; x = w(9) + 2
            div z 26    ; z = w(8) != ((w(7) != w(6) - 8) ? w(7) - 9 : w(5) + 2)) ? 26 * ((w(7) != w(6) - 8) ? 26 * (26 * ((w(3) != w(2) + 3) ? 26 * (26 * w(0) + w(1) + 3) + (w(3) + 5) : 26 * w(0) + w(1) + 3) + w(4) + 13) + w(5) + 9 : 26 * ((w(3) != w(2) + 3) ? 26 * (26 * w(0) + w(1) + 3) + (w(3) + 5) : 26 * w(0) + w(1) + 3) + w(4) + 13) + w(8) + 1 : (w(7) != w(6) - 8) ? 26 * (26 * ((w(3) != w(2) + 3) ? 26 * (26 * w(0) + w(1) + 3) + (w(3) + 5) : 26 * w(0) + w(1) + 3) + w(4) + 13) + w(5) + 9 : 26 * ((w(3) != w(2) + 3) ? 26 * (26 * w(0) + w(1) + 3) + (w(3) + 5) : 26 * w(0) + w(1) + 3) + w(4) + 13
            add x 0
            eql x w     ; x = (w(10) == w(9) + 2) ? 1 : 0
            eql x 0     ; x = (w(10) != w(9) + 2) ? 1 : 0
            mul y 0
            add y 25
            mul y x
            add y 1     ; y = (w(10) != w(9) + 2) ? 26 : 1
            mul z y     ; z = (w(10) != w(9) + 2) ? 26*Z : Z
            mul y 0
            add y w
            add y 7     ; y = w(10) + 7
            mul y x     ; y = ((w(10) != w(9) + 2) ? 26*Z : Z) * w(10) + 7
            add z y     ; z = z + ((w(10) != w(9) + 2) ? 26*Z : Z) * w(10) + 7
            inp w
            mul x 0
            add x z     ; x = z
            mod x 26    ; x = z % 26
            div z 26    ; z = z / 26 ; z == 0
            add x -5    ; x = (z % 26) - 5
            eql x w
            eql x 0
            mul y 0
            add y 25
            mul y x
            add y 1
            mul z y     ; z = x == 1 ? 26 * z : z
            mul y 0
            add y w
            add y 5     ; y = w(11) + 5
            mul y x     ; x = x * (w(11) + 5)
            add z y     ; z = z + x * (w(11) + 5)
            inp w       ; 13th digit
            mul x 0
            add x z
            mod x 26    ; x = z % 26
            div z 26    ; z = z / 26        ; 2*26 <= z <= 10*26
            add x -9    ; x = (z % 26) - 9  ; 10 <= z % 26 <= 18
            eql x w
            eql x 0     ; x = w(12) != x ? 1 : 0
            mul y 0
            add y 25
            mul y x
            add y 1     ; y = 25 * x + 1    ; x == 0, 2 <= z <= 10
            mul z y     ; z = z * (25 * x + 1) ; x == 0 => z = z * 1
            mul y 0
            add y w
            add y 8     ; y = w(12) + 8
            mul y x     ; y = x * (w(12) + 8) ; x != 0 ? 0 <= z <= 1 : 2 <= z <= 10
            add z y     ; 2 <= z + y <= 10
            inp w       ; 14th digit
            mul x 0
            add x z     ; x = z      ; 2 <= z <= 10
            mod x 26    ; x = z % 26 ; 2 <= z <= 10
            div z 26    ; z = z / 26 ; z < 26
            add x -1    ; x = (z % 26) - 1
            eql x w     ; x = w(13) == x ? 1 : 0
            eql x 0     ; x = w(13) != x ? 1 : 0
            mul y 0     ; y = 0
            add y 25    ; y = 25
            mul y x     ; y = y * x = 25 or 0
            add y 1     ; y = 26 or 1
            mul z y     ; z = 26 * z or z ; z == 0
            mul y 0
            add y w     ; y = w(13)
            add y 15    ; y = w(13) + 15
            mul y x     ; x == 0
            add z y     ; z == 0
            """;
}