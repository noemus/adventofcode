package active;

import java.util.Scanner;

import static util.LineSupplier.lines;

public class MonkeyMap {

    public static void main(String[] args) {
        try (Scanner in = new Scanner(INPUT1)) {
            lines(in)
                    .mapToInt(Integer::parseInt)
                    .forEach(n -> {

                    });

            long result = 1L;

            System.out.println("Result: " + result);
        }
    }

    @SuppressWarnings("unused")
    private static final String INPUT1 = """
            """;

    @SuppressWarnings("unused")
    private static final String INPUT = """
            """;
}