package util;

import java.util.Iterator;
import java.util.PrimitiveIterator;
import java.util.function.BiFunction;
import java.util.function.IntSupplier;
import java.util.function.LongPredicate;
import java.util.function.LongSupplier;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Utils {
    public static boolean validIntRange(String text, int lowerBound, int upperBound) {
        try {
            int value = Integer.parseInt(text);
            return value >= lowerBound && value <= upperBound;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static <A, B, C> Stream<C> zip(Stream<A> streamA, Stream<B> streamB, BiFunction<A, B, C> zipper) {
        final Iterator<A> iteratorA = streamA.iterator();
        final Iterator<B> iteratorB = streamB.iterator();
        final Iterator<C> iteratorC = new Iterator<C>() {
            @Override
            public boolean hasNext() {
                return iteratorA.hasNext() && iteratorB.hasNext();
            }

            @Override
            public C next() {
                return zipper.apply(iteratorA.next(), iteratorB.next());
            }
        };
        final boolean parallel = streamA.isParallel() || streamB.isParallel();
        return iteratorToFiniteStream(iteratorC, parallel);
    }

    public static <T> Stream<T> iteratorToFiniteStream(Iterator<T> iterator, boolean parallel) {
        final Iterable<T> iterable = () -> iterator;
        return StreamSupport.stream(iterable.spliterator(), parallel);
    }

    public static <T> Stream<T> zip(LongStream streamA, LongStream streamB, LongBiFunction<T> zipper) {
        final PrimitiveIterator.OfLong iteratorA = streamA.iterator();
        final PrimitiveIterator.OfLong iteratorB = streamB.iterator();
        final Iterator<T> iteratorC = new Iterator<>() {
            @Override
            public boolean hasNext() {
                return iteratorA.hasNext() && iteratorB.hasNext();
            }

            @Override
            public T next() {
                return zipper.apply(iteratorA.next(), iteratorB.next());
            }
        };
        final boolean parallel = streamA.isParallel() || streamB.isParallel();
        return iteratorToFiniteStream(iteratorC, parallel);
    }

    public static Stream<String> split(String str) {
        return split(str, ",");
    }

    public static Stream<String> split(String str, String delimiter) {
        return Stream.of(str.split(delimiter)).map(String::trim);
    }

    public static String substring(String str, String from) {
        return str.substring(str.indexOf(from) + from.length());
    }

    public static String substring(String str, String from, String to) {
        String initial = str.substring(str.indexOf(from) + from.length());
        return initial.substring(0, initial.indexOf(to));
    }

    public static class IntIndex implements IntSupplier {
        private int index = 0;

        public IntIndex() {
            this(0);
        }

        public IntIndex(int start) {
            index = start;
        }

        @Override
        public int getAsInt() {
            return index++;
        }
    }

    public static class LongIndex implements LongSupplier {
        private long index = 0;

        public LongIndex() {
            this(0);
        }

        public LongIndex(long start) {
            index = start;
        }

        public static LongIndex from(long start) {
            return new LongIndex(start);
        }

        public static LongPredicate until(long end) {
            return num -> num <= end;
        }

        @Override
        public long getAsLong() {
            return index++;
        }
    }

    public record LongPair(long left, long right) {}

    public record Repeat(int times) {
        public void action(Runnable action) {
            IntStream.range(0, times).forEach(n -> action.run());
        }

        public <T> Stream<T> stream(Supplier<T> action) {
            return IntStream.range(0, times).mapToObj(n -> action.get());
        }

        public static Repeat repeat(int times) {
            return new Repeat(times);
        }
    }
}
