package util;

import java.util.Scanner;
import java.util.function.Supplier;

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
}
