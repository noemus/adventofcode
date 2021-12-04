package done.advent2020;

import org.junit.Test;

import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;

@SuppressWarnings("unused")
public class AdapterArray {

    // partial[0] = number of arrangements for fixed number n followed by fixed (n+1): (1), (2)  == 1 way
    // partial[1] = 2^0 - subtract[0]
    // partial[1] = number of arrangements for fixed number n followed by (n+1) and fixed (n+2): (1), 2, (3) == 2 ways
    // partial[1] = 2^1 - subtract[0]
    // partial[2] = number of arrangements for fixed number n followed by (n+1), (n+2) and fixed (n+3): (1), 2, 3, (4) == 4 ways
    // partial[2] = 4*1 - subtract[1]
    // partial[3] = number of arrangements for fixed number n followed by (n+1), (n+2), (n+3) and fixed (n+4): (1), 2, 3, 4, (5) == 7 ways
    // partial[3] = 4*2 - subtract[2]
    // partial[4] = 4*4 - subtract[3]
    // partial[n+1] = 4*lastPow - subtract[n]
    static int[] partials = {
            1,
            2,
            4,
            7,
            0,
            0,
            0,
            0,
    };

    // subtract[0] = 0
    // subtract[1] = 0
    // subtract[2] = 1
    // subtract[3] = 2*subtract[2] + 2^0 = 3
    // subtract[4] = 2*subtract[3] + 2^1 = 8
    // subtract[n+1] = 2*subtract[n] + lastPow
    static int[] subtract = {
            0,
            0,
            1,
            3,
            0,
            0,
            0,
            0,
    };

    static int lastIndex = 3;
    static int lastPow = 2;

    static int partial(int idx) {
        if (idx < 0) {
            return 1;
        }
        if (idx > lastIndex) {
            computeNext();
        }
        return partials[idx];
    }

    static void computeNext() {
        lastIndex++;
        if (lastIndex == partials.length) {
            partials = Arrays.copyOf(partials, partials.length * 2);
            subtract = Arrays.copyOf(subtract, subtract.length * 2);
        }
        subtract[lastIndex] = 2*subtract[lastIndex - 1] + lastPow;
        lastPow *= 2;
        partials[lastIndex] = 4*lastPow - subtract[lastIndex - 1];
    }

    static int[] numbers;
    static int diff_1 = 0;
    static int diff_3 = 0;

    public static void main(String[] args) {
        try (Scanner in = new Scanner(INPUT2)) {
            numbers = Stream.generate(new LineSupplier(in))
                    .takeWhile(Objects::nonNull)
                    .mapToInt(Integer::parseInt)
                    .toArray();

            long result = 1L;

            Arrays.sort(numbers);

            int adapter = 0;
            int lastFixed = 0;

            for (int num : numbers) {
                int diff = num - adapter;
                switch (diff) {
                    case 1:
                        // go to next
                        System.out.println("+1");
                        break;
                    case 3:
                        // fixed number
                        System.out.println("+3: (" + lastFixed + ") .. (" + adapter + "), diff = " + (adapter - lastFixed - 1));
                        result *= partial(adapter - lastFixed - 1);
                        lastFixed = num;
                        break;
                    default:
                        break;
                }
                adapter = num;
            }

            System.out.println("+3: (" + lastFixed + ") .. (" + adapter + "), diff = " + (adapter - lastFixed - 1));
            result *= partial(adapter - lastFixed - 1);

            System.out.println("Result: " + result);
        }
    }

    private static final String INPUT = "" +
            "28\n" +
            "33\n" +
            "18\n" +
            "42\n" +
            "31\n" +
            "14\n" +
            "46\n" +
            "20\n" +
            "48\n" +
            "47\n" +
            "24\n" +
            "23\n" +
            "49\n" +
            "45\n" +
            "19\n" +
            "38\n" +
            "39\n" +
            "11\n" +
            "1\n" +
            "32\n" +
            "25\n" +
            "35\n" +
            "8\n" +
            "17\n" +
            "7\n" +
            "9\n" +
            "4\n" +
            "2\n" +
            "34\n" +
            "10\n" +
            "3\n";

    private static final String INPUT2 = "" +
            "99\n" +
            "151\n" +
            "61\n" +
            "134\n" +
            "112\n" +
            "70\n" +
            "75\n" +
            "41\n" +
            "119\n" +
            "137\n" +
            "158\n" +
            "50\n" +
            "167\n" +
            "60\n" +
            "116\n" +
            "117\n" +
            "62\n" +
            "82\n" +
            "31\n" +
            "3\n" +
            "72\n" +
            "88\n" +
            "165\n" +
            "34\n" +
            "8\n" +
            "14\n" +
            "27\n" +
            "108\n" +
            "166\n" +
            "71\n" +
            "51\n" +
            "42\n" +
            "135\n" +
            "122\n" +
            "140\n" +
            "109\n" +
            "1\n" +
            "101\n" +
            "2\n" +
            "77\n" +
            "85\n" +
            "76\n" +
            "143\n" +
            "100\n" +
            "127\n" +
            "7\n" +
            "107\n" +
            "13\n" +
            "148\n" +
            "118\n" +
            "56\n" +
            "159\n" +
            "133\n" +
            "21\n" +
            "154\n" +
            "152\n" +
            "130\n" +
            "78\n" +
            "54\n" +
            "104\n" +
            "160\n" +
            "153\n" +
            "95\n" +
            "49\n" +
            "19\n" +
            "69\n" +
            "142\n" +
            "63\n" +
            "11\n" +
            "12\n" +
            "29\n" +
            "98\n" +
            "84\n" +
            "28\n" +
            "17\n" +
            "146\n" +
            "161\n" +
            "115\n" +
            "4\n" +
            "94\n" +
            "24\n" +
            "126\n" +
            "136\n" +
            "91\n" +
            "57\n" +
            "30\n" +
            "155\n" +
            "79\n" +
            "66\n" +
            "141\n" +
            "48\n" +
            "125\n" +
            "162\n" +
            "37\n" +
            "40\n" +
            "147\n" +
            "18\n" +
            "20\n" +
            "45\n" +
            "55\n" +
            "83\n";

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
        assertEquals(2, partial(1));
        assertEquals(4, partial(2));
        assertEquals(7, partial(3));
        assertEquals(13, partial(4));
        assertEquals(24, partial(5));
        assertEquals(44, partial(6));
    }
}
/*
(0), 1, 2, 3, 4, 5, 6, 7
(0),    2, 3, 4, 5, 6, 7  2*1 - 0 = 2

(0), 1,    3, 4, 5, 6, 7
(0),       3, 4, 5, 6, 7  2*2 - 0 = 4

(0), 1, 2,    4, 5, 6, 7
(0),    2,    4, 5, 6, 7
(0), 1,       4, 5, 6, 7  2*4 - 2*0 - 1 = 7
(0),          4, 5, 6, 7  = = = =

(0), 1, 2, 3,    5, 6, 7
(0),    2, 3,    5, 6, 7
(0), 1,    3,    5, 6, 7
(0),       3,    5, 6, 7
(0), 1, 2,       5, 6, 7
(0),    2,       5, 6, 7  2*8 - 2*1 - 1 = 13
(0), 1,          5, 6, 7  = = =
(0),             5, 6, 7  = = = =

(0), 1, 2, 3, 4,    6, 7
(0),    2, 3, 4,    6, 7
(0), 1,    3, 4,    6, 7
(0),       3, 4,    6, 7
(0), 1, 2,    4,    6, 7
(0),    2,    4,    6, 7
(0), 1,       4,    6, 7
(0),          4,    6, 7  = = = =
(0), 1, 2, 3,       6, 7
(0),    2, 3,       6, 7
(0), 1,    3,       6, 7
(0),       3,       6, 7  2*16 - 2*3 - 2 = 24
(0), 1, 2,          6, 7  = =
(0),    2,          6, 7  = =
(0), 1,             6, 7  = = =
(0),                6, 7  = = = =

(0), 1, 2, 3, 4, 5,    7
(0),    2, 3, 4, 5,    7
(0), 1,    3, 4, 5,    7
(0),       3, 4, 5,    7
(0), 1, 2,    4, 5,    7
(0),    2,    4, 5,    7
(0), 1,       4, 5,    7
(0),          4, 5,    7  = = = =
(0), 1, 2, 3,    5,    7
(0),    2, 3,    5,    7
(0), 1,    3,    5,    7
(0),       3,    5,    7
(0), 1, 2,       5,    7
(0),    2,       5,    7
(0), 1,          5,    7  = = =
(0),             5,    7  = = = =
(0), 1, 2, 3, 4,       7
(0),    2, 3, 4,       7
(0), 1,    3, 4,       7
(0),       3, 4,       7
(0), 1, 2,    4,       7
(0),    2,    4,       7
(0), 1,       4,       7  2*32 - 2*8 - 4 = 44
(0),          4,       7  = = = =
(0), 1, 2, 3,       6, 7  =
(0),    2, 3,       6, 7  =
(0), 1,    3,       6, 7  =
(0),       3,       6, 7  =
(0), 1, 2,             7  = =
(0),    2,             7  = =
(0), 1,                7  = = =
(0),                   7  = = = =

    +1 +1 +1 +1 +3 +1  +3  +1  +1  +1  +1  +3
(0), 1, 2, 3, 4, 7, 8, 11, 12, 13, 14, 15, 18
(0),    2, 3, 4, 7, 8, 11, 12, 13, 14, 15, 18
(0), 1,    3, 4, 7, 8, 11, 12, 13, 14, 15, 18
(0),       3, 4, 7, 8, 11, 12, 13, 14, 15, 18
(0), 1, 2,    4, 7, 8, 11, 12, 13, 14, 15, 18
(0),    2,    4, 7, 8, 11, 12, 13, 14, 15, 18
(0), 1,       4, 7, 8, 11, 12, 13, 14, 15, 18  == 7

(0), 1, 2, 3, 4, 7, 8, 11, 12, 13, 14, 15, 18  == 1


(0), 1, 3, 4, 5, 6
(0),    3, 4, 5, 6
(0), 1,    4, 5, 6
(0), 1, 3,    5, 6
(0),    3,    5, 6
(0), 1, 3, 4,    6
(0),    3, 4,    6
(0), 1,    4,    6
(0), 1, 3,       6
(0),    3,       6

(0), 1, 3, 5, 6
(0),    3, 5, 6
(0), 1,    5, 6
(0), 1, 3,    6
(0),    3,    6

 */