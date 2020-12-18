package util;

import java.util.function.IntSupplier;

public class Utils {
    public static boolean validIntRange(String text, int lowerBound, int upperBound) {
        try {
            int value = Integer.parseInt(text);
            return value >= lowerBound && value <= upperBound;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static class IntIndex implements IntSupplier {
        private int index = 0;

        @Override
        public int getAsInt() {
            return index++;
        }
    }
}
