package util;

import java.util.Scanner;
import java.util.function.Supplier;

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
            buffer.append(delimiter).append(line);
        }
        String res = buffer.toString();
        return res.isBlank() ? null : res.trim();
    }
}
