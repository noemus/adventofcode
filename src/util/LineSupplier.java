package util;

import java.util.Objects;
import java.util.Scanner;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class LineSupplier implements Supplier<String> {
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

    public static Stream<String> lines(Scanner in) {
        return Stream.generate(new LineSupplier(in)).takeWhile(Objects::nonNull);
    }
}
