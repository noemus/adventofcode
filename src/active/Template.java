package active;

import org.junit.Test;
import util.LineSupplier;

import java.util.*;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class Template {

    static int[] numbers;

    public static void main(String[] args) {
        try (Scanner in = new Scanner(INPUT)) {
            numbers = Stream.generate(new LineSupplier(in))
                    .takeWhile(Objects::nonNull)
                    .mapToInt(Integer::parseInt)
                    .toArray();

            long result = 1L;

            System.out.println("Result: " + result);
        }
    }

    private static final String INPUT = """
            
            """;

    private static final String INPUT2 = """
            """;

    @Test
    public void test() {

    }
}