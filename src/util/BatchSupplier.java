package util;

import java.util.Objects;
import java.util.Scanner;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class BatchSupplier implements Supplier<String> {
    final Scanner in;
    char delimiter = ' ';

    public BatchSupplier(Scanner in) {
        this.in = in;
    }

    public BatchSupplier withDelimiter(char delim) {
        delimiter = delim;
        return this;
    }

    @Override
    public String get() {
        StringBuilder buffer = new StringBuilder();
        while (in.hasNext()) {
            String line = in.nextLine();
            if (line.isBlank()) {
                break;
            }
            if (!buffer.isEmpty()) {
                buffer.append(delimiter);
            }
            buffer.append(line);
        }
        String res = buffer.toString();
        return res.isBlank() ? null : res.trim();
    }

    public Stream<String> lines() {
        return Stream.generate(this).takeWhile(Objects::nonNull);
    }

    public static Stream<String> batchLines(Scanner in) {
        return Stream.generate(new BatchSupplier(in)).takeWhile(Objects::nonNull);
    }

    public static BatchSupplier batch(Scanner in) {
        return new BatchSupplier(in);
    }
}
