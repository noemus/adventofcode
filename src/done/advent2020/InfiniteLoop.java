package done.advent2020;

import org.junit.Test;

import java.util.Objects;
import java.util.Scanner;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class InfiniteLoop {

    static final int VISITED = -1;
    static final int FINISHED = -2;

    static long accumulator;
    static Instr swapped;
    static int swappedAddress;
    static Instr[] instructions;

    static class Instr {
        final int value;
        Type type;
        boolean visited;

        Instr(Type type, int value) {
            this.type = type;
            this.value = value;
        }

        int execute(int addr) {
            visited = true;

            return switch (type) {
                case acc -> {
                    accumulator += value;
                    yield addr + 1;
                }
                case nop -> addr + 1;
                case jmp -> addr + value;
            };
        }

        void swap() {
            if (type == Type.jmp) {
                type = Type.nop;
            } else if (type == Type.nop) {
                type = Type.jmp;
            }
        }

        static void reset(Instr instr) {
            instr.visited = false;
        }

        public boolean canSwap() {
            return type != Type.acc;
        }

        enum Type {
            nop,
            acc,
            jmp,
        }
    }

    public static void main(String[] args) {
        try (Scanner in = new Scanner(INPUT2)) {
            instructions = Stream.generate(new LineSupplier(in))
                    .takeWhile(Objects::nonNull)
                    .map(InfiniteLoop::toInstr)
                    .toArray(Instr[]::new);

            int addr = 0;
            while (addr != FINISHED) {
                while (addr >= 0) {
                    addr = execute(addr);
                }
                if (addr == VISITED) {
                    reset();
                    swapNext();
                    addr = 0;
                }
            }

            System.out.println("Result: " + accumulator);
        }
    }

    private static void reset() {
        Stream.of(instructions).forEach(Instr::reset);
        accumulator = 0;
    }

    private static void swapNext() {
        int addr;
        if (swapped != null) {
            swapped.swap();
            addr = swappedAddress + 1;
        } else {
            addr = 0;
        }
        swappedAddress = findNextToSwap(addr);
        swapped = instructions[swappedAddress];
        System.out.println("swapping: " + swappedAddress + ": " + swapped.type + " " + swapped.value);
        swapped.swap();
    }

    private static int findNextToSwap(int addr) {
        for (int i = addr; i < instructions.length; i++) {
            if (instructions[i].canSwap()) {
                return i;
            }
        }
        return instructions.length;
    }

    private static int execute(int addr) {
        if (addr == instructions.length) {
            return FINISHED;
        }
        return execute(addr, instructions[addr]);
    }

    private static int execute(int addr, Instr instr) {
        if (instr.visited) {
            return VISITED;
        }
        return instr.execute(addr);
    }

    private static Instr toInstr(String line) {
        int blank = line.indexOf(' ');
        Instr.Type type = Instr.Type.valueOf(line.substring(0, blank));
        int value = Integer.parseInt(line.substring(blank + 1).trim());
        return new Instr(type, value);
    }

    private static final String INPUT = "" +
            "nop +0\n" +
            "acc +1\n" +
            "jmp +4\n" +
            "acc +3\n" +
            "jmp -3\n" +
            "acc -99\n" +
            "acc +1\n" +
            "jmp -4\n" +
            "acc +6\n";

    private static final String INPUT2 = "" +
            "acc +33\n" +
            "acc -7\n" +
            "acc +39\n" +
            "jmp +214\n" +
            "jmp +250\n" +
            "jmp +51\n" +
            "acc +29\n" +
            "acc +6\n" +
            "acc +20\n" +
            "jmp +489\n" +
            "nop +181\n" +
            "acc +4\n" +
            "jmp +187\n" +
            "nop +454\n" +
            "acc -10\n" +
            "acc +44\n" +
            "jmp +343\n" +
            "acc +14\n" +
            "acc +24\n" +
            "acc +37\n" +
            "acc -12\n" +
            "jmp +596\n" +
            "acc +21\n" +
            "acc +39\n" +
            "jmp +601\n" +
            "acc -15\n" +
            "jmp +304\n" +
            "acc -7\n" +
            "jmp +302\n" +
            "acc +38\n" +
            "jmp +148\n" +
            "acc -6\n" +
            "jmp +235\n" +
            "acc +6\n" +
            "nop +429\n" +
            "acc +49\n" +
            "acc +3\n" +
            "jmp +255\n" +
            "acc +2\n" +
            "jmp +10\n" +
            "acc +27\n" +
            "acc +0\n" +
            "acc -3\n" +
            "acc +28\n" +
            "jmp +565\n" +
            "acc -16\n" +
            "acc +39\n" +
            "acc -5\n" +
            "jmp +513\n" +
            "acc +43\n" +
            "acc +24\n" +
            "jmp +26\n" +
            "nop +19\n" +
            "nop +71\n" +
            "nop +182\n" +
            "jmp +477\n" +
            "acc +42\n" +
            "jmp +535\n" +
            "acc +38\n" +
            "acc +29\n" +
            "acc +1\n" +
            "jmp +1\n" +
            "jmp +72\n" +
            "acc +25\n" +
            "acc +43\n" +
            "acc +6\n" +
            "jmp +1\n" +
            "jmp +111\n" +
            "acc +43\n" +
            "acc +13\n" +
            "jmp +30\n" +
            "acc +4\n" +
            "acc +24\n" +
            "acc +20\n" +
            "acc -14\n" +
            "jmp +161\n" +
            "jmp +73\n" +
            "nop +108\n" +
            "jmp +547\n" +
            "nop +273\n" +
            "acc -8\n" +
            "nop +358\n" +
            "nop +284\n" +
            "jmp +526\n" +
            "acc +50\n" +
            "jmp +274\n" +
            "jmp +486\n" +
            "nop +167\n" +
            "acc -13\n" +
            "jmp +11\n" +
            "acc +10\n" +
            "jmp +508\n" +
            "acc -11\n" +
            "acc +46\n" +
            "acc +44\n" +
            "jmp +335\n" +
            "jmp +1\n" +
            "acc -16\n" +
            "acc +30\n" +
            "jmp +289\n" +
            "acc +15\n" +
            "nop +265\n" +
            "jmp +1\n" +
            "nop +68\n" +
            "jmp +107\n" +
            "acc -15\n" +
            "jmp -101\n" +
            "acc +28\n" +
            "acc -13\n" +
            "jmp +17\n" +
            "acc +21\n" +
            "acc +46\n" +
            "acc +19\n" +
            "acc -8\n" +
            "jmp +274\n" +
            "nop +237\n" +
            "jmp -111\n" +
            "nop +419\n" +
            "acc +28\n" +
            "acc +26\n" +
            "jmp +275\n" +
            "acc -4\n" +
            "jmp +483\n" +
            "jmp +1\n" +
            "jmp +201\n" +
            "jmp +234\n" +
            "acc +26\n" +
            "acc +21\n" +
            "acc +18\n" +
            "jmp +149\n" +
            "acc +0\n" +
            "acc +29\n" +
            "acc +11\n" +
            "jmp -41\n" +
            "nop +111\n" +
            "nop +212\n" +
            "jmp +172\n" +
            "acc +31\n" +
            "acc +17\n" +
            "acc +6\n" +
            "jmp -40\n" +
            "acc +7\n" +
            "acc +44\n" +
            "acc +41\n" +
            "acc +4\n" +
            "jmp -74\n" +
            "acc -16\n" +
            "acc +37\n" +
            "jmp +119\n" +
            "acc -13\n" +
            "acc +44\n" +
            "acc +21\n" +
            "acc +38\n" +
            "jmp +92\n" +
            "acc +30\n" +
            "jmp +444\n" +
            "jmp +35\n" +
            "acc +3\n" +
            "acc +11\n" +
            "acc +31\n" +
            "jmp -104\n" +
            "acc -10\n" +
            "acc +5\n" +
            "acc +8\n" +
            "acc +31\n" +
            "jmp +127\n" +
            "nop +168\n" +
            "acc +16\n" +
            "acc +6\n" +
            "acc +0\n" +
            "jmp +455\n" +
            "acc +15\n" +
            "acc +0\n" +
            "acc +22\n" +
            "acc -1\n" +
            "jmp +191\n" +
            "acc +16\n" +
            "jmp +56\n" +
            "acc -12\n" +
            "acc +40\n" +
            "nop -140\n" +
            "acc +44\n" +
            "jmp +138\n" +
            "acc +44\n" +
            "jmp +237\n" +
            "acc +15\n" +
            "acc +40\n" +
            "jmp +360\n" +
            "acc +14\n" +
            "acc +14\n" +
            "jmp +185\n" +
            "nop +211\n" +
            "acc +27\n" +
            "acc -8\n" +
            "acc +17\n" +
            "jmp +247\n" +
            "acc +50\n" +
            "acc -2\n" +
            "jmp -49\n" +
            "acc +37\n" +
            "jmp +330\n" +
            "acc +14\n" +
            "acc +44\n" +
            "acc +15\n" +
            "nop -43\n" +
            "jmp +382\n" +
            "jmp -45\n" +
            "acc +46\n" +
            "acc -11\n" +
            "acc +47\n" +
            "jmp +61\n" +
            "nop +252\n" +
            "acc +44\n" +
            "acc -13\n" +
            "jmp +292\n" +
            "acc -6\n" +
            "jmp +199\n" +
            "acc +44\n" +
            "acc +28\n" +
            "acc +17\n" +
            "acc +31\n" +
            "jmp -158\n" +
            "acc -8\n" +
            "jmp +338\n" +
            "acc +0\n" +
            "acc -2\n" +
            "nop +306\n" +
            "jmp -78\n" +
            "acc +11\n" +
            "acc +33\n" +
            "acc +40\n" +
            "acc +33\n" +
            "jmp -169\n" +
            "jmp +273\n" +
            "acc +8\n" +
            "jmp -135\n" +
            "acc +20\n" +
            "acc -14\n" +
            "acc -15\n" +
            "nop +370\n" +
            "jmp +20\n" +
            "nop +51\n" +
            "acc -4\n" +
            "acc -10\n" +
            "jmp -215\n" +
            "acc +22\n" +
            "acc +22\n" +
            "jmp +209\n" +
            "acc +40\n" +
            "acc -18\n" +
            "jmp -158\n" +
            "jmp -130\n" +
            "acc +13\n" +
            "jmp -169\n" +
            "nop +225\n" +
            "acc +7\n" +
            "jmp -23\n" +
            "acc +21\n" +
            "acc +0\n" +
            "jmp +273\n" +
            "jmp +293\n" +
            "acc +39\n" +
            "jmp -71\n" +
            "acc +20\n" +
            "jmp +49\n" +
            "acc +6\n" +
            "jmp -60\n" +
            "acc +35\n" +
            "jmp +84\n" +
            "acc +14\n" +
            "jmp +266\n" +
            "acc +47\n" +
            "jmp -247\n" +
            "acc -3\n" +
            "acc +47\n" +
            "acc +23\n" +
            "acc +30\n" +
            "jmp +105\n" +
            "acc +18\n" +
            "jmp +109\n" +
            "jmp -188\n" +
            "nop -70\n" +
            "acc -2\n" +
            "acc +0\n" +
            "jmp +195\n" +
            "acc +15\n" +
            "jmp +246\n" +
            "acc +49\n" +
            "acc +28\n" +
            "jmp -18\n" +
            "nop +120\n" +
            "jmp +91\n" +
            "acc -15\n" +
            "acc +15\n" +
            "acc +30\n" +
            "jmp +39\n" +
            "acc +46\n" +
            "nop +250\n" +
            "acc +49\n" +
            "jmp -250\n" +
            "acc -10\n" +
            "acc +0\n" +
            "acc +39\n" +
            "jmp -254\n" +
            "nop +55\n" +
            "acc -4\n" +
            "acc -3\n" +
            "jmp +88\n" +
            "jmp +35\n" +
            "acc +47\n" +
            "nop -154\n" +
            "acc -16\n" +
            "jmp +271\n" +
            "nop +253\n" +
            "jmp -199\n" +
            "acc +5\n" +
            "acc +35\n" +
            "jmp +1\n" +
            "acc +49\n" +
            "jmp +234\n" +
            "acc +27\n" +
            "acc +33\n" +
            "acc -3\n" +
            "jmp -138\n" +
            "jmp -107\n" +
            "acc -11\n" +
            "acc +47\n" +
            "acc +14\n" +
            "jmp -288\n" +
            "jmp -205\n" +
            "acc +0\n" +
            "jmp +191\n" +
            "acc -15\n" +
            "jmp -116\n" +
            "acc +35\n" +
            "nop +121\n" +
            "acc +2\n" +
            "acc -14\n" +
            "jmp +223\n" +
            "acc +33\n" +
            "acc -10\n" +
            "acc +24\n" +
            "jmp +73\n" +
            "acc +39\n" +
            "jmp +255\n" +
            "acc +19\n" +
            "jmp -16\n" +
            "nop +1\n" +
            "jmp -177\n" +
            "nop +107\n" +
            "nop -194\n" +
            "jmp +260\n" +
            "acc -16\n" +
            "acc -12\n" +
            "jmp -148\n" +
            "acc +11\n" +
            "acc +18\n" +
            "acc +33\n" +
            "jmp +84\n" +
            "acc +27\n" +
            "acc -13\n" +
            "acc +36\n" +
            "acc +26\n" +
            "jmp +100\n" +
            "nop -110\n" +
            "jmp -98\n" +
            "acc -2\n" +
            "acc +29\n" +
            "acc +25\n" +
            "acc -8\n" +
            "jmp +128\n" +
            "acc +16\n" +
            "acc +1\n" +
            "acc +7\n" +
            "jmp -290\n" +
            "acc +18\n" +
            "nop -235\n" +
            "acc +0\n" +
            "jmp -127\n" +
            "acc -18\n" +
            "acc +38\n" +
            "jmp -297\n" +
            "acc +19\n" +
            "acc -8\n" +
            "acc +20\n" +
            "acc +3\n" +
            "jmp -230\n" +
            "jmp -67\n" +
            "jmp +124\n" +
            "acc -15\n" +
            "acc +26\n" +
            "acc -19\n" +
            "jmp +120\n" +
            "jmp +173\n" +
            "jmp -338\n" +
            "acc -15\n" +
            "jmp -309\n" +
            "acc +19\n" +
            "acc +26\n" +
            "acc +18\n" +
            "acc +8\n" +
            "jmp -6\n" +
            "acc -7\n" +
            "acc +10\n" +
            "jmp -375\n" +
            "acc +5\n" +
            "acc -16\n" +
            "acc +18\n" +
            "acc +46\n" +
            "jmp -309\n" +
            "acc +48\n" +
            "acc +40\n" +
            "nop -227\n" +
            "jmp -380\n" +
            "jmp -290\n" +
            "acc +46\n" +
            "acc +5\n" +
            "jmp -154\n" +
            "acc -9\n" +
            "acc +15\n" +
            "jmp -187\n" +
            "acc -10\n" +
            "acc +0\n" +
            "acc +28\n" +
            "acc +30\n" +
            "jmp -284\n" +
            "acc +43\n" +
            "acc +25\n" +
            "acc +14\n" +
            "jmp -205\n" +
            "acc -13\n" +
            "acc +1\n" +
            "nop -340\n" +
            "jmp -326\n" +
            "jmp +1\n" +
            "acc +9\n" +
            "acc +17\n" +
            "acc +1\n" +
            "jmp -346\n" +
            "jmp -158\n" +
            "acc +23\n" +
            "jmp -26\n" +
            "nop -257\n" +
            "jmp +140\n" +
            "acc +11\n" +
            "acc +10\n" +
            "acc +29\n" +
            "acc +48\n" +
            "jmp +177\n" +
            "acc +28\n" +
            "acc -12\n" +
            "acc -19\n" +
            "acc +37\n" +
            "jmp +79\n" +
            "acc -14\n" +
            "jmp -184\n" +
            "nop +153\n" +
            "jmp -170\n" +
            "acc -17\n" +
            "acc +10\n" +
            "acc -6\n" +
            "nop -174\n" +
            "jmp -391\n" +
            "jmp +148\n" +
            "acc +50\n" +
            "acc -8\n" +
            "jmp -426\n" +
            "jmp +1\n" +
            "acc +16\n" +
            "jmp +20\n" +
            "jmp +1\n" +
            "jmp -217\n" +
            "nop +84\n" +
            "jmp +71\n" +
            "acc +16\n" +
            "acc -7\n" +
            "acc +23\n" +
            "acc +24\n" +
            "jmp -329\n" +
            "acc +9\n" +
            "acc -7\n" +
            "acc -4\n" +
            "nop +117\n" +
            "jmp -16\n" +
            "acc +30\n" +
            "nop -222\n" +
            "acc +32\n" +
            "acc +9\n" +
            "jmp -175\n" +
            "acc +18\n" +
            "acc +15\n" +
            "acc +41\n" +
            "jmp -192\n" +
            "acc -3\n" +
            "acc +8\n" +
            "acc -13\n" +
            "acc +24\n" +
            "jmp -210\n" +
            "acc +17\n" +
            "acc -7\n" +
            "acc -19\n" +
            "jmp +76\n" +
            "acc +26\n" +
            "acc +2\n" +
            "acc +4\n" +
            "jmp +27\n" +
            "jmp -104\n" +
            "acc +38\n" +
            "acc +46\n" +
            "nop -67\n" +
            "nop +37\n" +
            "jmp -186\n" +
            "jmp +5\n" +
            "acc +37\n" +
            "acc +8\n" +
            "acc +30\n" +
            "jmp -409\n" +
            "acc +44\n" +
            "acc +4\n" +
            "jmp +109\n" +
            "nop -8\n" +
            "jmp -395\n" +
            "acc +20\n" +
            "acc +12\n" +
            "acc +16\n" +
            "acc +9\n" +
            "jmp -87\n" +
            "nop -406\n" +
            "acc -8\n" +
            "jmp -209\n" +
            "jmp -137\n" +
            "jmp -179\n" +
            "acc +44\n" +
            "jmp -399\n" +
            "nop -141\n" +
            "jmp +18\n" +
            "jmp +1\n" +
            "nop +55\n" +
            "jmp +39\n" +
            "acc +20\n" +
            "acc +40\n" +
            "acc +44\n" +
            "acc +45\n" +
            "jmp +74\n" +
            "acc -16\n" +
            "jmp -170\n" +
            "jmp -48\n" +
            "jmp -537\n" +
            "acc -9\n" +
            "acc +6\n" +
            "nop -101\n" +
            "acc +2\n" +
            "jmp -418\n" +
            "jmp -81\n" +
            "jmp +1\n" +
            "jmp -338\n" +
            "nop +43\n" +
            "acc +20\n" +
            "jmp -109\n" +
            "acc -1\n" +
            "jmp -343\n" +
            "acc +29\n" +
            "acc +11\n" +
            "nop -439\n" +
            "jmp -310\n" +
            "jmp -374\n" +
            "acc +33\n" +
            "nop +25\n" +
            "acc -16\n" +
            "nop -333\n" +
            "jmp -14\n" +
            "jmp -5\n" +
            "jmp -162\n" +
            "nop -432\n" +
            "acc +16\n" +
            "acc +17\n" +
            "jmp -87\n" +
            "acc -16\n" +
            "nop -265\n" +
            "acc +20\n" +
            "jmp -356\n" +
            "acc +0\n" +
            "jmp +5\n" +
            "acc +39\n" +
            "acc -15\n" +
            "jmp -325\n" +
            "jmp -39\n" +
            "nop -376\n" +
            "nop -116\n" +
            "acc +38\n" +
            "jmp -175\n" +
            "jmp -450\n" +
            "jmp +1\n" +
            "acc +19\n" +
            "jmp -58\n" +
            "nop -39\n" +
            "acc +40\n" +
            "acc +42\n" +
            "jmp -232\n" +
            "acc -14\n" +
            "jmp -17\n" +
            "acc +4\n" +
            "acc -9\n" +
            "acc +45\n" +
            "jmp -229\n" +
            "jmp -18\n" +
            "acc +13\n" +
            "acc +17\n" +
            "jmp -591\n" +
            "jmp -604\n" +
            "jmp -356\n" +
            "acc +1\n" +
            "acc +18\n" +
            "nop -52\n" +
            "acc +39\n" +
            "jmp -361\n" +
            "jmp -303\n" +
            "acc +8\n" +
            "nop -477\n" +
            "acc +3\n" +
            "acc -8\n" +
            "jmp -404\n" +
            "acc +24\n" +
            "acc +5\n" +
            "jmp -88\n" +
            "acc +27\n" +
            "jmp -54\n" +
            "jmp -18\n" +
            "acc +31\n" +
            "acc +40\n" +
            "acc +18\n" +
            "acc -16\n" +
            "jmp +1\n";

    private static Set<Integer> toSet(String group) {
        return group.chars().boxed().collect(Collectors.toSet());
    }

    private static <T> Set<T> disjoint(Set<T> g1, Set<T> g2) {
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

    private static class LineSupplier implements Supplier<String> {
        final Scanner in;

        public LineSupplier(Scanner in) {
            this.in = in;
        }

        @Override
        public String get() {
            return in.hasNext()
                    ? in.nextLine()
                    : null;
        }
    }

    private static class BatchSupplier implements Supplier<String> {
        final Scanner in;

        public BatchSupplier(Scanner in) {
            this.in = in;
        }

        @Override
        public String get() {
            StringBuilder buffer = new StringBuilder();
            while (in.hasNext()) {
                String line = in.nextLine();
                if (line.isBlank()) {
                    break;
                }
                buffer.append(' ').append(line);
            }
            String res = buffer.toString();
            return res.isBlank() ? null : res.trim();
        }
    }

    @Test
    public void test() {

    }
}
