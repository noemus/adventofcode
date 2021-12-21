package active;

import org.junit.Test;
import util.LineSupplier;

import java.util.Objects;
import java.util.Scanner;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class Template {

    static int[] numbers;

    public static void main(String[] args) {
        try (Scanner in = new Scanner(INPUT1)) {
            numbers = Stream.generate(new LineSupplier(in))
                    .takeWhile(Objects::nonNull)
                    .mapToInt(Integer::parseInt)
                    .toArray();

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

    @Test
    public void test() {

    }
}