package done;

import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class RambuctiousRecitation {

    static long turnNumber = 1;
    static long lastNumber = 0;
    static final Map<Long, List<Long>> spokenNumbers = new HashMap<>();

    public static void main(String[] args) {
        try (Scanner in = new Scanner(INPUT)) {
            Stream.of(in.nextLine().split(","))
                    .mapToLong(Long::parseLong)
                    .forEach(RambuctiousRecitation::sayNumber);

            while (turnNumber <= 30000000) {
                sayNumber(nextNumber());
            }

            System.out.println("Result: " + lastNumber);
        }
    }

    private static void sayNumber(long number) {
        System.out.println("Turn " + turnNumber + ": Say: " + number);
        List<Long> turns = spokenNumbers.getOrDefault(number, List.of());
        if (turns.size() == 2) {
            spokenNumbers.put(number, List.of(turns.get(1), turnNumber));
        } else if (turns.size() == 1) {
            spokenNumbers.put(number, List.of(turns.get(0), turnNumber));
        } else {
            spokenNumbers.put(number, List.of(turnNumber));
        }

        turnNumber++;
        lastNumber = number;
    }

    static long nextNumber() {
        List<Long> turns = spokenNumbers.getOrDefault(lastNumber, List.of());
        if (turns.size() == 1) {
            return 0;
        } else if (turns.size() == 2) {
            return turns.get(1) - turns.get(0);
        }
        throw new IllegalStateException("Invalid number of previous turns!");
    }

    private static final String INPUT1 = "1,3,2" +
            "\n";

    private static final String INPUT2 = "2,1,3" +
            "\n";

    private static final String INPUT3 = "1,2,3" +
            "\n";

    private static final String INPUT = "0,8,15,2,12,1,4" +
            "";

    @Test
    public void test() {

    }
}