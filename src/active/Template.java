package active;

import org.junit.Test;

import java.util.Scanner;

import static util.LineSupplier.lines;

public class Template {

    static int[] numbers;

    public static void main(String[] args) {
        try (Scanner in = new Scanner(INPUT1)) {
            numbers = lines(in)
                    .mapToInt(Integer::parseInt)
                    .toArray();

            long result = 1L;

            System.out.println("Result: " + result);
        }
    }

    private static final String INPUT1 = """
            """;

    private static final String INPUT = """
            """;

    @Test
    public void test() {

    }
}