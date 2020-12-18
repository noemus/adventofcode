package done;

import org.junit.Test;

import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;

@SuppressWarnings("unused")
public class SeatID {

    public static void main(String[] args) {
        try (Scanner in = new Scanner(INPUT)) {
            int[] seatID = Stream.generate(new LineSupplier(in))
                    .takeWhile(Objects::nonNull)
                    .map(String::getBytes)
                    .mapToInt(SeatID::toBinary)
                    .toArray();

            Arrays.sort(seatID);

            for (int i = 0; i < seatID.length - 1; i++) {
                int seat = seatID[i];
                if (seat < 7) continue;
                if (seat >= 1015) continue;

                int nextSeat = seatID[i + 1];
                if (seat + 2 == nextSeat) {
                    System.out.println("Result: " + (seat + 1));
                    break;
                }
            }
        }
    }

    private static int toBinary(byte[] bytes) {
        int exp = 0;
        int num = 0;
        for (int i = 0; i < bytes.length; i++) {
            exp = 1 << i;
            if (isTrue(bytes[bytes.length - i - 1])) {
                num += exp;
            }
        }
        return num;
    }

    private static boolean isTrue(byte c) {
        return switch (c) {
            case 'F', 'L' -> false;
            default -> true;
        };
    }

    private static final String INPUT2 = "" +
            "BFFFBBFRRR\n" +
            "FFFBBBFRRR\n" +
            "BBFFBBFRLL\n";

    private static final String INPUT = "" +
            "FFBBBFFLRL\n" +
            "BFFBFBFRLR\n" +
            "FFFBBFBRRR\n" +
            "BFFBBBBRRL\n" +
            "FFFBBFBLLR\n" +
            "BBFBFBFLLR\n" +
            "FBBFFBFLRL\n" +
            "BFBFBBFLLL\n" +
            "FBFBBFBLRR\n" +
            "FFFBBBBRLR\n" +
            "FFFFBFFRRL\n" +
            "BFFFBFBLRL\n" +
            "BBFFFBBRLL\n" +
            "FFBFFFBLRL\n" +
            "FBFBFFFRLL\n" +
            "BFFBBBFRRL\n" +
            "BFFFBBFRLR\n" +
            "BBBFFFBRLL\n" +
            "FBBBFFBRLL\n" +
            "FFFBFFBRLL\n" +
            "BBFBFBFLLL\n" +
            "BFFBFFFLRL\n" +
            "BBFBBFFLRL\n" +
            "FBFBBBFRRR\n" +
            "FBBBBFBRRL\n" +
            "FBBFFFFLRL\n" +
            "FFFBBFBLRL\n" +
            "BFFFFFFLLR\n" +
            "FFBFFBBRRR\n" +
            "FFFFBBBRLR\n" +
            "FFFBFFFLRR\n" +
            "FBBFBBFLLL\n" +
            "FBBFBBBRLR\n" +
            "BFFBFFFLLR\n" +
            "FFBFBFFRLL\n" +
            "FFFFBBFLRR\n" +
            "BBFBBBFLRR\n" +
            "BFBFBBBRLR\n" +
            "BFFBBFFLRL\n" +
            "BFFBFFFLLL\n" +
            "FBFFFBFLRR\n" +
            "FFFBBBFRLR\n" +
            "FBBFBBFRLL\n" +
            "BBFFFBBLLL\n" +
            "BBFFFFFLLL\n" +
            "FBFFBBBLLL\n" +
            "FBFFFBFLRL\n" +
            "FFBFBBFLLL\n" +
            "BFFBBFBLLR\n" +
            "BFBBBFFRRR\n" +
            "BFBBBBBLLR\n" +
            "FBFFFFFRLR\n" +
            "BBFFBFFLLR\n" +
            "FFBFBFFLLR\n" +
            "BBFBBBFRRL\n" +
            "FFFBFFFRLR\n" +
            "FBFBFFFLRL\n" +
            "BBFFFFBLLL\n" +
            "FFBBFFFLRL\n" +
            "BBFFBFBRRR\n" +
            "FBBFBBBLLR\n" +
            "BFFFBFBRRR\n" +
            "BBFFFFFLRL\n" +
            "FBFBBFBRRR\n" +
            "FFFBBBBLRR\n" +
            "FBBFBFFLLR\n" +
            "BBFBFFFRLR\n" +
            "BBFFFFBRLR\n" +
            "FBFBFFBRLR\n" +
            "FFFBBFFLLR\n" +
            "FBBFFFFLRR\n" +
            "BBFFBBBLLR\n" +
            "BBFFFFBRRL\n" +
            "BFBBBBBRRL\n" +
            "FBBBFFBRLR\n" +
            "FFBBBFBRLR\n" +
            "BFBBBBBRLR\n" +
            "FBFBBBFRLR\n" +
            "BBFBBFBRRR\n" +
            "FFBFFBBLRR\n" +
            "FFBBBBFLLR\n" +
            "BFFFBBBLRL\n" +
            "FBBFBFFLRL\n" +
            "FBFFBFFRRL\n" +
            "FBBBFFFRRR\n" +
            "FFBBFBFRLL\n" +
            "FBBFBBBLLL\n" +
            "FFFFBFFRLR\n" +
            "BBFBBBFRLR\n" +
            "FBFBBBBLRL\n" +
            "FFFFBBBLRL\n" +
            "BFFBBFBRRL\n" +
            "BBFFFBFRLR\n" +
            "FBFFBFFRRR\n" +
            "BBFBBFBRLL\n" +
            "FBBFBFFRRR\n" +
            "BFFFBFBLRR\n" +
            "FFFBBFBLRR\n" +
            "FFBFBFFRRL\n" +
            "FFBBBBBRLR\n" +
            "FBBBBBBLLR\n" +
            "BBFBFBBRRL\n" +
            "FBBFFFFRLR\n" +
            "BFBBBBFRRL\n" +
            "FBBFFBFLRR\n" +
            "FFFBBBBLRL\n" +
            "BBFFBBFLRR\n" +
            "BFBFFBBRLL\n" +
            "FFBBBBBLRR\n" +
            "BBBFFFFRRR\n" +
            "BFFFFBFRRR\n" +
            "FBFFFFBLRR\n" +
            "FBBBBBFLRR\n" +
            "FFBBBBFRRR\n" +
            "FFBFBFFLLL\n" +
            "FBFBFFFLLR\n" +
            "BFBBBBFLLL\n" +
            "FBBFBBFRLR\n" +
            "FFFBFBFLLL\n" +
            "FBBFBBFLLR\n" +
            "FBFFFFFRRL\n" +
            "BFFBBBBRLL\n" +
            "BFFBFFBLRR\n" +
            "FBBFFFBLLR\n" +
            "FBBFBFBLLL\n" +
            "BFFFBFBRLR\n" +
            "FBFBFBBRLR\n" +
            "FBFFFBFRRR\n" +
            "BFFBBFFRRR\n" +
            "BFBFFFBRRL\n" +
            "BFFBFBBRRL\n" +
            "BBFFBFBRRL\n" +
            "FBBBBFBRRR\n" +
            "FFFBBBBRRL\n" +
            "BFFFFBFLLL\n" +
            "FBBBFBFLLR\n" +
            "BBFFBFFRLR\n" +
            "FBBFBFFRLR\n" +
            "BFFFBFFLRR\n" +
            "FBBFFBFLLL\n" +
            "FFBBFFBRRR\n" +
            "FFBFFFBLRR\n" +
            "FFBFBBBRLR\n" +
            "BBFBBFFLLR\n" +
            "BBFFFBFLRR\n" +
            "FFFBBBFLRR\n" +
            "FBFFFBFLLR\n" +
            "BBFBFBFLRL\n" +
            "BFBFBBBLRR\n" +
            "BFFBBFFRLR\n" +
            "FBFBBBBRLL\n" +
            "BBFFBBBLRL\n" +
            "BBFFFBFLLR\n" +
            "BBFFBFFRLL\n" +
            "FFFFBFBRRR\n" +
            "BFBFFBBLRR\n" +
            "BFBBBBBRLL\n" +
            "FBBFFFFRRR\n" +
            "BFFFBBBRRL\n" +
            "BFFBFBFLRL\n" +
            "FFFFBFFRLL\n" +
            "FBFFFFBRRL\n" +
            "BBBFFFBRRL\n" +
            "BFBBFFFLLL\n" +
            "FFFFBBBRRL\n" +
            "FBFFBBBRLR\n" +
            "FFBFFFFRLL\n" +
            "BFFBBBBRRR\n" +
            "BBFFBBFRRL\n" +
            "BFBBBFBLLL\n" +
            "FBBBFBBLLL\n" +
            "FBFBFFFLLL\n" +
            "BFBFFFFRLR\n" +
            "FFBFFBFLLR\n" +
            "BFFBFBBRLL\n" +
            "BFBBBFFLLL\n" +
            "FBBFBFFRLL\n" +
            "FFBFBBFLLR\n" +
            "FBFBFBBRRL\n" +
            "BFBFBFFLRL\n" +
            "FFBFFFFRRL\n" +
            "BFFBFFFRLR\n" +
            "BFFFBBBLRR\n" +
            "BBFBFBBLLR\n" +
            "BFFBFBFLLR\n" +
            "BFBBFBFRRR\n" +
            "FBFBFFBLRR\n" +
            "BBFBBBBRRL\n" +
            "BFFFBFFLLR\n" +
            "FFFFBBFRRL\n" +
            "FFFBFFBRRL\n" +
            "FBFFFBBRRR\n" +
            "BFBBBBFLLR\n" +
            "BBFBFBFRRL\n" +
            "FFFBBFFLRL\n" +
            "BBBFFFFLRL\n" +
            "FFFBFFFRRL\n" +
            "FBFBFFFLRR\n" +
            "FFBFBBFLRL\n" +
            "BBFBFBFRLR\n" +
            "BBFBBFFRLR\n" +
            "FBBFBFBRRR\n" +
            "FBFFFBBRLR\n" +
            "BBFFBBBRLL\n" +
            "BFFBFFBLLR\n" +
            "FBBBFBBLRL\n" +
            "FFFBFFFRRR\n" +
            "BFBBBFFLLR\n" +
            "FBBFFFFLLL\n" +
            "BFBBFBFRRL\n" +
            "FBBFFBBRRR\n" +
            "BFBFFFBLRL\n" +
            "BBBFFBFLLL\n" +
            "FFBFFBFRRL\n" +
            "FBFFBFBRRL\n" +
            "BFFFFBFRLL\n" +
            "FFBFFFBRLL\n" +
            "FFBBFFBLLL\n" +
            "BFFBBFFLLL\n" +
            "FBFFFFBLRL\n" +
            "BFBFFBFLLR\n" +
            "FFFFBBBLRR\n" +
            "BFFBFBBRLR\n" +
            "FBBBFFFRRL\n" +
            "BBFBFBBLLL\n" +
            "FFBBBFBRRL\n" +
            "BBFBFFBLRR\n" +
            "FBFBFFBLRL\n" +
            "BBFBBFBLRL\n" +
            "BFBBFFBRRL\n" +
            "FFBBBFBLLL\n" +
            "FFBFBBFRLL\n" +
            "BFFFFBBLLL\n" +
            "FBBBFBBLRR\n" +
            "FBFBBFBLLL\n" +
            "FFBBBBFRRL\n" +
            "BBFBBBBLLL\n" +
            "FBFBBBBLLR\n" +
            "BFFFBFBLLR\n" +
            "FBBBFFFRLR\n" +
            "FFBBBFBRRR\n" +
            "BBFFFFBLLR\n" +
            "BBFFBBBLLL\n" +
            "BBFFBBFLRL\n" +
            "BBBFFFBLRR\n" +
            "BBBFFFFRLL\n" +
            "BFBFFFFRRR\n" +
            "BFFFBFBRRL\n" +
            "FBBBBBBLRL\n" +
            "FFBFBBBRRL\n" +
            "FFBFBFFLRL\n" +
            "FFBBFFFLLL\n" +
            "BBFFBFBLLR\n" +
            "BFBBBFBRLL\n" +
            "FFBBBFBRLL\n" +
            "FBFBBFBRRL\n" +
            "FBFFBFFLLR\n" +
            "BBBFFFBRRR\n" +
            "BBFBBBBLRR\n" +
            "BBFFBBBRLR\n" +
            "FBBBBFFRRR\n" +
            "BFFFFBBLRR\n" +
            "FFBFFBBRRL\n" +
            "FBFBBBFRLL\n" +
            "BBFBFBBLRL\n" +
            "BFBBFBBRRR\n" +
            "BFBBFBBRLR\n" +
            "FFFBFBBLLR\n" +
            "FFFBFFFLLR\n" +
            "BFFFBBFLRL\n" +
            "FBFBBFFLRL\n" +
            "BFBFBBBRRL\n" +
            "BBFBFBBRRR\n" +
            "BBFFBFBRLR\n" +
            "BFBFBFBRLL\n" +
            "FBBFBFBLRL\n" +
            "FFBBFBBRRL\n" +
            "FFBFFFBLLL\n" +
            "FBBBFFBRRL\n" +
            "BFBBFFBLRL\n" +
            "FFFBBBBRLL\n" +
            "FBFBFBFLRR\n" +
            "FFFBFBFLRL\n" +
            "BFBBBBBLRR\n" +
            "BBFFBFFLLL\n" +
            "FFFFBFFLRR\n" +
            "FFBBFFBRLR\n" +
            "FBBBFFBLRR\n" +
            "FBBBBFBLRL\n" +
            "FBFFBBFRLR\n" +
            "FFBFBFFRRR\n" +
            "FBFFFBFRLL\n" +
            "FFFBFBFRRR\n" +
            "BFFFBFFRRL\n" +
            "FFFBFBFLRR\n" +
            "BBFFBFFLRL\n" +
            "BBBFFBFLLR\n" +
            "FFFBFFBLRL\n" +
            "BFBBBBFRRR\n" +
            "BFFBFFFLRR\n" +
            "FBFFFFBRRR\n" +
            "BFFBFFFRLL\n" +
            "FBFFBFFRLL\n" +
            "FFFBBBBLLR\n" +
            "FFFBBBFLLR\n" +
            "BFBBBFFLRR\n" +
            "BBBFFFFRRL\n" +
            "FFFBBFFRRR\n" +
            "BBFFBBBLRR\n" +
            "FFBFFFBRRR\n" +
            "BFFBFFFRRL\n" +
            "BFFBFBFRRL\n" +
            "BBFFFBFLLL\n" +
            "FBFBFFBRRL\n" +
            "BFBFFFBLLR\n" +
            "FFBBBBBLRL\n" +
            "FBFBBBBRRL\n" +
            "BFFBBFBLRL\n" +
            "FBBFBBFLRL\n" +
            "FFBFFFBRLR\n" +
            "FBFFFBFRRL\n" +
            "FFFFBBFRLL\n" +
            "BFBBFBFLRL\n" +
            "FFFBFFBLLR\n" +
            "FBBFBFBRRL\n" +
            "FFBFFFBLLR\n" +
            "FFBBBBFLLL\n" +
            "BFBBBBFLRR\n" +
            "FBFFFBFLLL\n" +
            "BBFBFFBRRL\n" +
            "FBBBFBBRRR\n" +
            "FFFBBBBLLL\n" +
            "FBBBBBFRRL\n" +
            "BFBBBBFRLR\n" +
            "BFFBBBFRLL\n" +
            "FBFFFFFLRR\n" +
            "BFBBBBFLRL\n" +
            "FBFBFBBRLL\n" +
            "BFFBBBBLRR\n" +
            "BFFFFFFLLL\n" +
            "BBBFFFFLLL\n" +
            "BFBFFFFRLL\n" +
            "FBBFBFFLLL\n" +
            "BFBFFBBRLR\n" +
            "BBFBFFFRRL\n" +
            "FBFBFFBRLL\n" +
            "FBFBBFBLLR\n" +
            "FBFFBFBRLR\n" +
            "FBBFFBBRLR\n" +
            "FBBFBBFRRL\n" +
            "FFBBFFBRLL\n" +
            "FBFFBFFLLL\n" +
            "BBFBBFFRLL\n" +
            "BFFFFBFRRL\n" +
            "FBFBFBFRLL\n" +
            "FFFBBFBRRL\n" +
            "BFFFBBFRRR\n" +
            "BFFBBBFLRR\n" +
            "FBBFFBFRLL\n" +
            "BBFBFBBLRR\n" +
            "BBFBBFBRLR\n" +
            "BFBFBBFRRL\n" +
            "FBBBFFFLRL\n" +
            "FBFBFFBRRR\n" +
            "BFBFBBFRRR\n" +
            "FFBBFBBLRL\n" +
            "BFFBFFBRLR\n" +
            "BFFFFFBRRR\n" +
            "BBFFBBFRRR\n" +
            "BBFFBBFRLL\n" +
            "FFBFFBFRLR\n" +
            "BBFBBBBRLR\n" +
            "BBFFBBBRRL\n" +
            "BFFFBFFLRL\n" +
            "BFBBFBFRLR\n" +
            "FFBBBBBLLL\n" +
            "FBBBBFFRLR\n" +
            "BFBFFFBLRR\n" +
            "BFBFFFFRRL\n" +
            "BFFBBBBLLL\n" +
            "BFBBFFFRLL\n" +
            "FFFBBBFLLL\n" +
            "BFFBBFBLLL\n" +
            "BFFFBBFRRL\n" +
            "FBBBFBBRRL\n" +
            "BFFFBBFLLL\n" +
            "BBBFFFFLLR\n" +
            "BFBBFFFLRL\n" +
            "BFFBBBBLLR\n" +
            "FBBFFFBRRR\n" +
            "BFBFFFFLLR\n" +
            "FBFFBFBLLR\n" +
            "FFBBFFBLLR\n" +
            "FBFBBBFLLL\n" +
            "BBFFFBBRLR\n" +
            "BFBFBFFLLL\n" +
            "FBFFFBBLRL\n" +
            "BFFFBFFRLL\n" +
            "FBBFFFBRRL\n" +
            "BFFFFBBRLL\n" +
            "BFBFBBBLLL\n" +
            "BFBFBFBLLL\n" +
            "FFFBBFBLLL\n" +
            "BBBFFFBLRL\n" +
            "BFBBFBBLLR\n" +
            "BFBFBFFRRR\n" +
            "BBFFFFBLRR\n" +
            "BFFFBBBRRR\n" +
            "FBFFFFFRLL\n" +
            "FFBFFFFRRR\n" +
            "BFBBFFFRRL\n" +
            "BFFFBBBRLL\n" +
            "FFBBFBBRRR\n" +
            "FFBFBFBRRL\n" +
            "FFBFBBFRLR\n" +
            "BFBFFBBLRL\n" +
            "FBBBBBFLLR\n" +
            "BFBFFBBRRL\n" +
            "FFBFFFFLLL\n" +
            "FFFBBFBRLL\n" +
            "FBFBBFFLLR\n" +
            "BFBBFFBRLL\n" +
            "FFBBFBBLLL\n" +
            "FBBBFBFLRR\n" +
            "FFBBFBBLLR\n" +
            "FFBFFFFLLR\n" +
            "BFBBFBBLLL\n" +
            "BBFFBBFLLL\n" +
            "FFBBFFFRRL\n" +
            "FBFBBBBLLL\n" +
            "FBBBBBFLLL\n" +
            "FBBBBFBRLR\n" +
            "BBBFFFFLRR\n" +
            "FBFBFBFLLL\n" +
            "BFBBFFFLRR\n" +
            "FFBBFBBRLL\n" +
            "FBBBBFBRLL\n" +
            "FBFBBFFLRR\n" +
            "FBBBBBFRLR\n" +
            "BFBFBBFLRL\n" +
            "BFBFBFFLRR\n" +
            "FBFBFBFRRR\n" +
            "BFBFFFBRRR\n" +
            "FBBFFFFLLR\n" +
            "BFBFBFBLLR\n" +
            "BFFFFFFLRL\n" +
            "FFFBBBFRRL\n" +
            "BFFFBFFLLL\n" +
            "FFBFBBBRRR\n" +
            "BBFFFFBRLL\n" +
            "FBBBFBFLRL\n" +
            "BFBBBFBLRR\n" +
            "BBFFFBBRRR\n" +
            "FBFFBBFRLL\n" +
            "BFBBFBBLRL\n" +
            "FBBFBBBRLL\n" +
            "BBFBBBFLLR\n" +
            "FFBFFFBRRL\n" +
            "BBBFFFBLLR\n" +
            "FFFFBBBLLL\n" +
            "BFFFBFBLLL\n" +
            "BFFBFBBLRL\n" +
            "FBBFFFBRLR\n" +
            "BFBFFBFRRR\n" +
            "FBBBFBBRLL\n" +
            "FBBBBBFRRR\n" +
            "FBFFFFBRLR\n" +
            "BFFBBBFRLR\n" +
            "BFBBFBFLLR\n" +
            "BFBFBFFRLR\n" +
            "FBFFBFBRRR\n" +
            "FBFFFBBLLL\n" +
            "BFBFBFBRRL\n" +
            "FFFFBFBRLL\n" +
            "FFFBBFFRLR\n" +
            "FFFFBFBLRL\n" +
            "FFFBFFBLLL\n" +
            "FBFFBFFRLR\n" +
            "FBBBFFFLRR\n" +
            "FBFBFFFRLR\n" +
            "FFBFBBFLRR\n" +
            "FFFBFBBLRL\n" +
            "BBFFBFBLRR\n" +
            "BFBFFBFLRR\n" +
            "FBBFFFFRRL\n" +
            "FBFBBFBRLL\n" +
            "FBBBBFFLRR\n" +
            "FBFFFFFLRL\n" +
            "BFBBBFFRRL\n" +
            "FBFFBBBLLR\n" +
            "BBFFFBFRRL\n" +
            "FBBFFBBLRL\n" +
            "BBFFFFFLRR\n" +
            "FBBFFBBRLL\n" +
            "FBBFFBFRRL\n" +
            "FFBFFBFRRR\n" +
            "FBBBBFFRRL\n" +
            "BFBFBBBLLR\n" +
            "FBFFFFFRRR\n" +
            "FFBBFBFRRL\n" +
            "BFBBBFBRLR\n" +
            "FFBBFFBRRL\n" +
            "BBFFBBFRLR\n" +
            "FFBFBBBLRR\n" +
            "FFFBFFFLRL\n" +
            "BBFBBFBLLR\n" +
            "FBBBFBBLLR\n" +
            "BFBBFBFRLL\n" +
            "BBFBFFBRRR\n" +
            "FFBFFBBLRL\n" +
            "BFBFBBBRRR\n" +
            "FBBBFBFRRR\n" +
            "FFBBFFFRLR\n" +
            "BBFBFFBRLR\n" +
            "FFBFBBBLLR\n" +
            "BBFBFFFLRR\n" +
            "BBFFFBBLLR\n" +
            "BFFFFBBLLR\n" +
            "BFFBBFBRRR\n" +
            "BFFBBBFLRL\n" +
            "BFBBFFBLLL\n" +
            "FFFFBFFRRR\n" +
            "FFFBFFBLRR\n" +
            "BFFBFFBLRL\n" +
            "BFBBBBBLLL\n" +
            "FFBFFBFLLL\n" +
            "BFBFBFBRRR\n" +
            "BFFFFFBLRR\n" +
            "BFFFBBFLRR\n" +
            "BBFFFFFLLR\n" +
            "FFFBFBBRRL\n" +
            "FBFFBBFRRL\n" +
            "BBFBBBFLLL\n" +
            "BBFFFFBLRL\n" +
            "BFFFFFFLRR\n" +
            "FBBBBBBLRR\n" +
            "BFFBBBFLLR\n" +
            "FFBFBBFRRR\n" +
            "BFBFBFBLRL\n" +
            "FBFFBFFLRL\n" +
            "FBBBBBBRLL\n" +
            "FFBBBFFLLL\n" +
            "BFBFFFBLLL\n" +
            "FBBFFBBLLL\n" +
            "BBFFBFFLRR\n" +
            "FBBBBBBRLR\n" +
            "FBFFBBFLRL\n" +
            "FBBBBFFLLR\n" +
            "BFBFFFBRLR\n" +
            "FFFBFBBRRR\n" +
            "FFBBFBFLRL\n" +
            "BBFFBFFRRR\n" +
            "FBBFBFBLRR\n" +
            "BFBFBFBLRR\n" +
            "FBFBBFFLLL\n" +
            "FBFBBFFRLR\n" +
            "FBFFBBBRLL\n" +
            "FBBFBFBRLL\n" +
            "BFFFFFFRLL\n" +
            "FBBBBBBLLL\n" +
            "BFFBBFFRRL\n" +
            "FFBFBFBLLR\n" +
            "BFFFFFBRLR\n" +
            "FBFFFBBRRL\n" +
            "BFBFFBFRLR\n" +
            "BFFBFFFRRR\n" +
            "FFBFBBBLLL\n" +
            "BFBFFBFLLL\n" +
            "BFFFFBFLLR\n" +
            "FFBFBFBRLR\n" +
            "BBFBBBBRRR\n" +
            "BBFFFBFRRR\n" +
            "BFFFFFBRRL\n" +
            "FBFFBBFLLL\n" +
            "FBFFFBBLLR\n" +
            "BBFBFBFLRR\n" +
            "BFBFBFFRLL\n" +
            "FBFFFFFLLL\n" +
            "BFBBFBFLLL\n" +
            "FFBFBFFLRR\n" +
            "FBBFFFBLRR\n" +
            "FBBBFFBLRL\n" +
            "BFFFFBBRRR\n" +
            "FBFBBFFRLL\n" +
            "FFBFFBBRLR\n" +
            "FBFFFBBLRR\n" +
            "BFBBFFFLLR\n" +
            "BFBFBFBRLR\n" +
            "BFBFFBBRRR\n" +
            "FFBBBFFRLR\n" +
            "FFBFBBBLRL\n" +
            "FFBFBFBRLL\n" +
            "FFBBFBFRLR\n" +
            "FBFFBFBLRR\n" +
            "BFBBBFBRRL\n" +
            "FBBFFFBLLL\n" +
            "FBFBBBFLRL\n" +
            "FBFBBBFLRR\n" +
            "BBFBBBBLLR\n" +
            "BFFBBBFLLL\n" +
            "FFFFBBBRRR\n" +
            "FFBBFFFRLL\n" +
            "FFBBBFBLRR\n" +
            "FFFBFBBRLL\n" +
            "BBFFFFFRRR\n" +
            "FFBBBFBLLR\n" +
            "FFFBBFFLLL\n" +
            "FFFFBFFLRL\n" +
            "FFFFBBFRLR\n" +
            "BFBFBBFRLL\n" +
            "FFBFBFBLRR\n" +
            "FBBFFBBLLR\n" +
            "BBFBBBBRLL\n" +
            "FFFBFBFRRL\n" +
            "FBBFBFBRLR\n" +
            "FBBFBBBLRR\n" +
            "BFFBBBBLRL\n" +
            "FFFBBFFLRR\n" +
            "BFFFFBFRLR\n" +
            "FFBBBFFRLL\n" +
            "BFBFFFBRLL\n" +
            "BFBBFBFLRR\n" +
            "FFFFBBFRRR\n" +
            "FBFBBBBRLR\n" +
            "FBBBBBBRRL\n" +
            "BBFFBFBLLL\n" +
            "FBBFFBFLLR\n" +
            "BFBBBFFLRL\n" +
            "BBFBFFFLLR\n" +
            "BBFFBBBRRR\n" +
            "BFFBFBFRRR\n" +
            "FFBBBBBLLR\n" +
            "BBFBBBFRRR\n" +
            "FBBFBFFRRL\n" +
            "FBFFFBFRLR\n" +
            "BFFFBBBLLL\n" +
            "FBBFFBBRRL\n" +
            "FBBBBBFLRL\n" +
            "BBFBFFFLRL\n" +
            "FFFBFFFRLL\n" +
            "FFBFFBFRLL\n" +
            "BFFFBFFRRR\n" +
            "BFBBFFFRLR\n" +
            "BBBFFFFRLR\n" +
            "FBBBBFBLRR\n" +
            "BBFBFFBRLL\n" +
            "FBFFBBFLRR\n" +
            "BFFFBFFRLR\n" +
            "BFBFBBFRLR\n" +
            "BFFFBBBLLR\n" +
            "FBFBBFBRLR\n" +
            "FFBBBBFRLR\n" +
            "FBBBBFFRLL\n" +
            "BBFBFFBLRL\n" +
            "BFBFFBBLLL\n" +
            "FFBBFFBLRR\n" +
            "FBBBFFBLLL\n" +
            "BFFBFFBRRL\n" +
            "FBFFBBBRRL\n" +
            "FFBFBBFRRL\n" +
            "FBBFFBFRRR\n" +
            "FBBBBBFRLL\n" +
            "BBFBFBFRRR\n" +
            "FFFBBFFRRL\n" +
            "BFFFBBFRLL\n" +
            "FBFFBFBLRL\n" +
            "BFBBBFBLRL\n" +
            "FFFBBBFLRL\n" +
            "FFBBFBFLLL\n" +
            "BBFBFFBLLR\n" +
            "BFFFFFFRRL\n" +
            "BFFBFBBLRR\n" +
            "BFBFBBFLLR\n" +
            "FFBBBFFLLR\n" +
            "FBBFFBFRLR\n" +
            "FFFBBFBRLR\n" +
            "FBFFBFBLLL\n" +
            "FBBBBFBLLR\n" +
            "FFBBBFBLRL\n" +
            "BFFFFFBLLR\n" +
            "FBFFFFFLLR\n" +
            "FFFBFBBLLL\n" +
            "FFBBFFFLRR\n" +
            "BFFBFBFRLL\n" +
            "FBFBFBFLLR\n" +
            "FBFBFBBLLL\n" +
            "FBBFBFBLLR\n" +
            "BBFFFFFRRL\n" +
            "FBFFBFBRLL\n" +
            "FBBBFFFRLL\n" +
            "FBFBFBFRRL\n" +
            "BFFFFBBLRL\n" +
            "BBFBBFBLLL\n" +
            "BBFBBBBLRL\n" +
            "FBBBFBFRRL\n" +
            "FFFFBBFLLR\n" +
            "BFBFFFFLRL\n" +
            "BBFFFFBRRR\n" +
            "FFFFBBFLRL\n" +
            "BBFFFBFRLL\n" +
            "BFFFFBFLRL\n" +
            "BFFBFFBRRR\n" +
            "FBFBFFFRRR\n" +
            "FBFFBBBLRL\n" +
            "FFFBFBFRLR\n" +
            "FBBFFFFRLL\n" +
            "BBFBBFFRRR\n" +
            "FFBFFBBLLL\n" +
            "FFBFFFFLRL\n" +
            "BBFBBFFLRR\n" +
            "FBFBFFBLLR\n" +
            "FFBBFFFRRR\n" +
            "BBFBFFBLLL\n" +
            "FFFBFFFLLL\n" +
            "BFBFFBFRRL\n" +
            "FBBBFFFLLR\n" +
            "FBFBBBBLRR\n" +
            "FBFFBFFLRR\n" +
            "BBFFFFFRLR\n" +
            "FBBBFFBRRR\n" +
            "FFFBFFBRRR\n" +
            "FBFBFBBLLR\n" +
            "BFBBFFBRRR\n" +
            "BFFFFBBRLR\n" +
            "FBBFFBBLRR\n" +
            "BFFBFFBRLL\n" +
            "FFBBBFFRRR\n" +
            "BFFBFBBLLR\n" +
            "FBFBBFFRRL\n" +
            "FBFBFFBLLL\n" +
            "BFFFFFBLLL\n" +
            "BBFBBBFRLL\n" +
            "BFBBBBBRRR\n" +
            "BFFBBFBLRR\n" +
            "FFFFBBBLLR\n" +
            "BFFBFBBRRR\n" +
            "FBFFFFBLLL\n" +
            "FFBFBFBLRL\n" +
            "BFBFFFFLRR\n" +
            "BFBBFFBLRR\n" +
            "FFFBBBFRRR\n" +
            "FBBBBBBRRR\n" +
            "BFBFFBBLLR\n" +
            "FBBBFBFLLL\n" +
            "FBFFBBFRRR\n" +
            "FBBFBBBRRR\n" +
            "FFBFBBBRLL\n" +
            "BFBBBBBLRL\n" +
            "BFFBBFBRLR\n" +
            "FBFBBFBLRL\n" +
            "FBBFBFFLRR\n" +
            "BFFFFFBLRL\n" +
            "FFBBFFBLRL\n" +
            "FFFFBFFLLR\n" +
            "FBFBFBBRRR\n" +
            "FFBBFBFLLR\n" +
            "BFFBFFBLLL\n" +
            "FFFBFBBRLR\n" +
            "BFBFBBBLRL\n" +
            "BFBFFFFLLL\n" +
            "FFBBFBFRRR\n" +
            "FBFBBBBRRR\n" +
            "FBBBFFBLLR\n" +
            "BBFBFFFLLL\n" +
            "FBFBFBFRLR\n" +
            "FBFBFBBLRL\n" +
            "BFBBFFBLLR\n" +
            "FFBBBBBRRR\n" +
            "FFFFBFFLLL\n" +
            "BBFBFBBRLR\n" +
            "BFFBFBBLLL\n" +
            "BBFFBFBRLL\n" +
            "BFBBFBBRRL\n" +
            "BBBFFFBLLL\n" +
            "BBFFBFFRRL\n" +
            "FFFFBBBRLL\n" +
            "BFFFFFBRLL\n" +
            "BBFBFFFRRR\n" +
            "FFBFFBBRLL\n" +
            "BFFBBBBRLR\n" +
            "FFBBBBFRLL\n" +
            "FBBBBFBLLL\n" +
            "BFFFFBBRRL\n" +
            "BBFBBBFLRL\n" +
            "FBBFBBBRRL\n" +
            "FFFBBFFRLL\n" +
            "FFBBBFFLRR\n" +
            "FFBFFBFLRL\n" +
            "FFFBFFBRLR\n" +
            "FBFFFFBRLL\n" +
            "FBFFBBBLRR\n" +
            "FFFFBFBRLR\n" +
            "FBFBFBBLRR\n" +
            "BFBBFFFRRR\n" +
            "FBFBFBFLRL\n" +
            "FBBBFBBRLR\n" +
            "FFBBBFFRRL\n" +
            "FBFFFBBRLL\n" +
            "BBFFBFBLRL\n" +
            "BBFFFBBLRR\n" +
            "BFFBBBFRRR\n" +
            "BBFBFFFRLL\n" +
            "FFBBBBFLRR\n" +
            "FBFFBBFLLR\n" +
            "BFFFFBFLRR\n" +
            "FBFBBBFLLR\n" +
            "FFFBFBFLLR\n" +
            "FFBBFBBRLR\n" +
            "BBFBBFFRRL\n" +
            "BBFFBBFLLR\n" +
            "FFBFFBFLRR\n" +
            "FBFFBBBRRR\n" +
            "BFBFBBBRLL\n" +
            "FFBBFBFLRR\n" +
            "BFFBBFFRLL\n" +
            "BFFFFFFRLR\n" +
            "BBBFFFBRLR\n" +
            "BFBBBFBLLR\n" +
            "FBFFFFBLLR\n" +
            "FFFFBFBLRR\n" +
            "FBBBFBFRLL\n" +
            "FFBFBFBRRR\n" +
            "BBFFFBFLRL\n" +
            "FFBFFFFLRR\n" +
            "BBFBFBBRLL\n" +
            "BBFBFBFRLL\n" +
            "FBBBBFFLRL\n" +
            "BFFFBFBRLL\n" +
            "FFFFBFBLLR\n" +
            "FBBFFFBLRL\n" +
            "FBBBBFFLLL\n" +
            "BFBBFBBLRR\n" +
            "FBFBFFFRRL\n" +
            "FBBBFFFLLL\n" +
            "FBBBFBFRLR\n" +
            "BBFBBFBLRR\n" +
            "FBBFBBFRRR\n" +
            "FFBBBBBRRL\n" +
            "FFFBBBFRLL\n" +
            "BFFBBFFLLR\n" +
            "BFBBBFFRLL\n" +
            "BBFFFBBRRL\n" +
            "BFBBFBBRLL\n" +
            "BFBFBFFRRL\n" +
            "BFFBFBFLRR\n" +
            "FFBFBFBLLL\n" +
            "BBFFFBBLRL\n" +
            "BFBFBBFLRR\n" +
            "BBFBBFFLLL\n" +
            "BFFFFFFRRR\n" +
            "BFBBBBFRLL\n" +
            "FBBFBBFLRR\n" +
            "BFBFFBFRLL\n" +
            "FFBBFBBLRR\n" +
            "BFFBBFBRLL\n" +
            "BFBFFBFLRL\n" +
            "FFFBFBBLRR\n" +
            "BFBBBFBRRR\n" +
            "FFFFBFBLLL\n" +
            "FFBFBFFRLR\n" +
            "FFFFBBFLLL\n" +
            "FBFBBFFRRR\n" +
            "BBFFFFFRLL\n" +
            "BFBFBFFLLR\n" +
            "FFBBFFFLLR\n" +
            "FFBFFFFRLR\n" +
            "BFBBBFFRLR\n" +
            "BFFBBFFLRR\n" +
            "FBBFFFBRLL\n" +
            "FFBBBBBRLL\n" +
            "FFBFFBBLLR\n" +
            "FFFFBFBRRL\n" +
            "BFFBFBFLLL\n" +
            "FBBFBBBLRL\n" +
            "BBFBBFBRRL\n" +
            "FFBBBBFLRL\n" +
            "BFFFBBFLLR\n" +
            "FFFBFBFRLL\n" +
            "FFFBBBBRRR\n" +
            "FBFBBBFRRL\n" +
            "BFFFBBBRLR\n";

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
//        assertEquals(0, isTrue((byte)'F'));
//        assertEquals(0, isTrue((byte)'R'));
//        assertEquals(1, isTrue((byte)'B'));
//        assertEquals(1, isTrue((byte)'L'));

//        assertEquals(0, toBinary(new byte[] {'F', 'F'}));
//        assertEquals(1, toBinary(new byte[] {'F', 'B'}));
//        assertEquals(2, toBinary(new byte[] {'B', 'F'}));
//        assertEquals(3, toBinary(new byte[] {'B', 'B'}));
//        assertEquals(5, toBinary(new byte[] {'R', 'L', 'R'}));
//        assertEquals(44, toBinary(new byte[]{'F', 'B', 'F', 'B', 'B', 'F', 'F'}));
//        assertEquals(44, toBinary(new byte[]{'B', 'B', 'B', 'B', 'B', 'B', 'B', 'L', 'L', 'L'}));
        assertEquals(44, toBinary(new byte[]{'B', 'B', 'B', 'B', 'B', 'B', 'B', 'R', 'R', 'R'}));
    }
}
