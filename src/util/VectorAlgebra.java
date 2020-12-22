package util;

import org.junit.Test;
import util.Utils.LongIndex;

import java.util.function.Supplier;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static util.Utils.zip;

public final class VectorAlgebra {
    record V1(long x) implements V {
        @Override
        public int dim() {
            return 1;
        }

        @Override
        public LongStream coords() {
            return LongStream.of(x);
        }

        @Override
        public long x(int pos) {
            return switch (pos) {
                case 1 -> x;
                default -> throw invalidCoord(pos, 1);
            };
        }

        @Override
        public V join(V other) {
            return V.of(LongStream.concat(coords(), other.coords()).toArray());
        }

        @Override
        public int compareTo(V o) {
            int minDim = Math.min(dim(), o.dim());
            for (int pos = 1; pos <= minDim; pos++) {
                if (x(pos) < o.x(pos)) return -1;
                if (x(pos) > o.x(pos)) return 1;
            }
            return 0;
        }
    }

    record V2(long x, long y) implements V {
        @Override
        public int dim() {
            return 2;
        }

        @Override
        public LongStream coords() {
            return LongStream.of(x, y);
        }

        @Override
        public long x(int pos) {
            return switch (pos) {
                case 1 -> x;
                case 2 -> y;
                default -> throw invalidCoord(pos, 2);
            };
        }

        @Override
        public V join(V other) {
            return V.of(LongStream.concat(coords(), other.coords()).toArray());
        }

        @Override
        public int compareTo(V o) {
            int minDim = Math.min(dim(), o.dim());
            for (int pos = 1; pos <= minDim; pos++) {
                if (x(pos) < o.x(pos)) return -1;
                if (x(pos) > o.x(pos)) return 1;
            }
            return 0;
        }
    }

    record V3(long x, long y, long z) implements V {
        @Override
        public int dim() {
            return 3;
        }

        @Override
        public LongStream coords() {
            return LongStream.of(x, y, z);
        }

        @Override
        public long x(int pos) {
            return switch (pos) {
                case 1 -> x;
                case 2 -> y;
                case 3 -> z;
                default -> throw invalidCoord(pos, 3);
            };
        }

        @Override
        public V join(V other) {
            return V.of(LongStream.concat(coords(), other.coords()).toArray());
        }

        @Override
        public int compareTo(V o) {
            int minDim = Math.min(3, o.dim());
            for (int pos = 1; pos <= minDim; pos++) {
                if (x(pos) < o.x(pos)) return -1;
                if (x(pos) > o.x(pos)) return 1;
            }
            return 0;
        }
    }

    record VN(int dim, long[] x) implements V {
        @Override
        public LongStream coords() {
            return LongStream.of(x);
        }

        @Override
        public long x(int pos) {
            return coord(pos, x);
        }

        @Override
        public V join(V other) {
            return V.of(LongStream.concat(coords(), other.coords()).toArray());
        }

        @Override
        public int compareTo(V o) {
            int minDim = Math.min(dim, o.dim());
            for (int pos = 1; pos <= minDim; pos++) {
                if (x(pos) < o.x(pos)) return -1;
                if (x(pos) > o.x(pos)) return 1;
            }
            return 0;
        }
    }

    record R1(long low, long high) implements R {
        R1 {
            if (low > high) {
                throw new IllegalArgumentException("Range low must be lesser than high: low = " + low + ", high = " + high);
            }
        }

        @Override
        public int dim() {
            return 1;
        }

        @Override
        public Stream<V> members() {
            return LongStream.generate(LongIndex.from(low))
                             .takeWhile(LongIndex.until(high))
                             .mapToObj(V1::new);
        }

        public R1 product(R1 r2) {
            return null;
        }
    }

    record RN(V low, V high) implements R {
        RN {
            requireNonNull(low);
            requireNonNull(high);
            if (low.dim() != high.dim()) {
                throw new IllegalArgumentException("Range vectors must have same dimension: low.dim() = " + low.dim() + ", high.dim() = " + high.dim());
            }
            if (low.compareTo(high) > 1) {
                throw new IllegalArgumentException("Range low must be lesser than high: low = " + low + ", high = " + high);
            }
        }

        @Override
        public int dim() {
            return low.dim();
        }

        @Override
        public Stream<V> members() {
            return zip(low.coords(), high.coords(), R::of)
                            .map(R::membersSupplier)
                            .reduce((s1,s2) -> () -> s1.get().flatMap(v1 -> s2.get().map(v1::join)))
                            .map(Supplier::get)
                            .orElse(Stream.of());
        }
    }

    public interface V extends Comparable<V> {
        int dim();

        LongStream coords();

        long x(int pos);

        V join(V other);

        static V of(long... x) {
            return switch (requireNonNull(x).length) {
                case 1 -> new V1(coord(1, x));
                case 2 -> new V2(coord(1, x), coord(2, x));
                case 3 -> new V3(coord(1, x), coord(2, x), coord(3, x));
                default -> new VN(x.length, x);
            };
        }
    }

    public interface R {
        int dim();

        Stream<V> members();

        static R of(V low, V high) {
            return new RN(low, high);
        }

        static R of(long low, long high) {
            return new R1(low, high);
        }

        static Supplier<Stream<V>> membersSupplier(R range) {
            return range::members;
        }
    }

    private static long coord(int pos, long[] x) {
        checkBounds(pos, x);
        return x[pos - 1];
    }

    private static void checkBounds(int pos, long[] x) {
        requireNonNull(x);
        if (pos <= 0 || pos > x.length) {
            throw invalidCoord(pos, x.length);
        }
    }

    private static RuntimeException invalidCoord(int pos, int dim) {
        if (pos <= 0) {
            return new IllegalArgumentException("Vector coord must be greater than zero");
        }
        return new IllegalArgumentException("Invalid vector coord: " + pos + ", vector dimension is: " + dim);
    }

    @Test
    public void test() {
        assertEquals(1, V.of(1, 2, 3).x(1));
        assertEquals(2, V.of(1, 2, 3).x(2));
        assertEquals(3, V.of(1, 2, 3).x(3));

        assertArrayEquals(new V[]{V.of(-1), V.of(0), V.of(1)},
                          R.of(V.of(-1), V.of(1)).members().toArray(V[]::new));

        assertArrayEquals(new V[]{
                                  V.of(-1, -1), V.of(-1, 0), V.of(-1, 1),
                                  V.of(0, -1), V.of(0, 0), V.of(0, 1),
                                  V.of(1, -1), V.of(1, 0), V.of(1, 1)
                          },
                          R.of(V.of(-1, -1), V.of(1, 1)).members().toArray(V[]::new));

        assertArrayEquals(new V[]{
                                  V.of(-1, -1, -1), V.of(-1, -1, 0), V.of(-1, -1, 1),
                                  V.of(-1, 0, -1), V.of(-1, 0, 0), V.of(-1, 0, 1),
                                  V.of(-1, 1, -1), V.of(-1, 1, 0), V.of(-1, 1, 1),

                                  V.of(0, -1, -1), V.of(0, -1, 0), V.of(0, -1, 1),
                                  V.of(0, 0, -1), V.of(0, 0, 0), V.of(0, 0, 1),
                                  V.of(0, 1, -1), V.of(0, 1, 0), V.of(0, 1, 1),

                                  V.of(1, -1, -1), V.of(1, -1, 0), V.of(1, -1, 1),
                                  V.of(1, 0, -1), V.of(1, 0, 0), V.of(1, 0, 1),
                                  V.of(1, 1, -1), V.of(1, 1, 0), V.of(1, 1, 1),
                          },
                          R.of(V.of(-1, -1, -1), V.of(1, 1, 1)).members().toArray(V[]::new));
    }
}
