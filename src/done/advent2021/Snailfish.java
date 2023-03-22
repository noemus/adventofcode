package done.advent2021;

import org.junit.jupiter.api.Test;
import util.LineSupplier;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Stream;

import static java.lang.Character.isDigit;
import static java.util.stream.Collectors.toCollection;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class Snailfish {

    static SnailfishNumber[] numbers;

    public static void main(String[] args) {
        try (Scanner in = new Scanner(INPUT)) {
            numbers = Stream.generate(new LineSupplier(in))
                    .takeWhile(Objects::nonNull)
                    .map(SnailfishNumber::parse)
                    .toArray(SnailfishNumber[]::new);

            long result = Stream.of(numbers)
                                .reduce(SnailfishNumber::add)
                                .stream()
                                .mapToLong(SnailfishNumber::magnitude).sum();

            System.out.println("Result 1: " + result);

            long result2 = Stream.of(numbers)
                                 .map(snail1 -> Stream.of(numbers)
                                                      .map(snail2 -> SnailPair.create(snail1, snail2))
                                                      .toList())
                                 .flatMap(List::stream)
                                 .filter(SnailPair::isDistinct)
                                 .map(SnailPair::add)
                                 .mapToLong(SnailfishNumber::magnitude)
                                 .max()
                                 .orElse(0L);

            System.out.println("Result 2: " + result2);
        }
    }

    record SnailPair(SnailfishNumber first, SnailfishNumber second) {
        static SnailPair create(SnailfishNumber first, SnailfishNumber second) {
            return new SnailPair(first.copy(), second.copy());
        }
        boolean isDistinct() {
            return !first.toString().equals(second.toString());
        }

        SnailfishNumber add() {
            return first.add(second);
        }
    }

    static class SnailfishNumberParser {
        private final Deque<Character> chars;

        SnailfishNumberParser(String line) {
            chars = line.chars()
                        .mapToObj(c -> (char) c)
                        .collect(toCollection(ArrayDeque::new));
        }

        public SnailfishNumber number() {
            if (chars.peek() == '[') {
                return pair();
            }
            return regular();
        }

        private SnailfishNumber pair() {
            pop('[');
            SnailfishNumber left = number();
            pop(',');
            SnailfishNumber right = number();
            pop(']');
            return new PairSnailfish(left, right);
        }

        private SnailfishNumber regular() {
            int num = 0;
            while (isDigit(chars.peek())) {
                num *= 10;
                num += digit();
            }
            return new RegularSnailfish(num);
        }

        private int digit() {
            char c = chars.pop();
            return c - '0';
        }

        private void pop(char c) {
            if (c != chars.peek()) {
                throw new IllegalStateException("Expected '" + c + "', but got: " + chars.peek());
            }
            chars.pop();
        }
    }

    interface SnailfishNumber {
        SnailfishNumber add(SnailfishNumber other);
        long magnitude();

        SnailfishNumber copy();
        SnailfishNumber split();
        SnailfishNumber explode();
        SnailfishNumber addLevel();
        SnailfishNumber leftRegular();
        SnailfishNumber rightRegular();

        int number();
        void add(int number);

        boolean isRegular();
        boolean willExplode();
        boolean willSplit();

        Optional<SnailfishNumber> prev();
        void prev(SnailfishNumber prev);
        Optional<SnailfishNumber> next();
        void next(SnailfishNumber next);

        static SnailfishNumber parse(String line) {
            return new SnailfishNumberParser(line).number();
        }
    }

    static final class RegularSnailfish extends AbstractSnailfish {
        private Optional<SnailfishNumber> prev = Optional.empty();
        private Optional<SnailfishNumber> next = Optional.empty();
        private int n;

        RegularSnailfish(int n) {
            this.n = n;
        }

        @Override
        public long magnitude() {
            return number();
        }

        @Override
        public SnailfishNumber split() {
            if (n < 10) {
                return this;
            }

            SnailfishNumber left = regular(n / 2);
            SnailfishNumber right = regular((n / 2) + (n % 2));

            left.next(right);
            right.prev(left);

            prev().ifPresent(left::prev);
            next().ifPresent(right::next);

            prev().ifPresent(p -> p.next(left));
            next().ifPresent(p -> p.prev(right));

            return pair(left, right);
        }

        @Override
        public SnailfishNumber explode() {
            return this;
        }

        @Override
        public int number() {
            return n;
        }

        @Override
        public void add(int number) {
            this.n += number;
        }

        @Override
        public SnailfishNumber leftRegular() {
            return this;
        }

        @Override
        public SnailfishNumber rightRegular() {
            return this;
        }

        @Override
        public boolean isRegular() {
            return true;
        }

        @Override
        public boolean willExplode() {
            return false;
        }

        @Override
        public boolean willSplit() {
            return n >= 10;
        }

        @Override
        public Optional<SnailfishNumber> prev() {
            return prev;
        }

        @Override
        public void prev(SnailfishNumber prev) {
            this.prev = Optional.ofNullable(prev);
        }

        @Override
        public Optional<SnailfishNumber> next() {
            return next;
        }

        @Override
        public void next(SnailfishNumber next) {
            this.next = Optional.ofNullable(next);
        }

        @Override
        public String toString() {
            return Integer.toString(n);
        }
    }

    static final class PairSnailfish extends AbstractSnailfish {
        private SnailfishNumber left;
        private SnailfishNumber right;

        PairSnailfish(SnailfishNumber left, SnailfishNumber right) {
            this.left = left.addLevel();
            this.right = right.addLevel();
            this.left.next(right.leftRegular());
            this.right.prev(left.rightRegular());
        }

        @Override
        public long magnitude() {
            return 3 * left.magnitude() + 2 * right.magnitude();
        }

        @Override
        public SnailfishNumber leftRegular() {
            return left.leftRegular();
        }

        @Override
        public SnailfishNumber rightRegular() {
            return right.rightRegular();
        }

        @Override
        public SnailfishNumber addLevel() {
            left.addLevel();
            right.addLevel();
            return super.addLevel();
        }

        public SnailfishNumber reduce() {
            while (willExplode() || willSplit()) {
                if (willExplode()) {
                    explode();
                } else {
                    split();
                }
            }
            return this;
        }

        @Override
        public SnailfishNumber split() {
            if (left.willSplit()) {
                left = left.split();
            } else if (right.willSplit()) {
                right = right.split();
            }
            return this;
        }

        @Override
        public SnailfishNumber explode() {
            if (left.isRegular() && right.isRegular()) {
                prev().ifPresent(p -> p.add(left.number()));
                next().ifPresent(p -> p.add(right.number()));
                return regular(0);
            } else if (left.willExplode()) {
                left = left.explode();
            } else if (right.willExplode()) {
                right = right.explode();
            }
            return this;
        }

        @Override
        public boolean willExplode() {
            if (left.isRegular() && right.isRegular()) {
                return super.willExplode();
            }
            return left.willExplode() || right.willExplode();
        }

        @Override
        public boolean willSplit() {
            return left.willSplit() || right.willSplit();
        }

        @Override
        public Optional<SnailfishNumber> prev() {
            return left.prev();
        }

        @Override
        public void prev(SnailfishNumber prev) {
            left.prev(prev);
        }

        @Override
        public Optional<SnailfishNumber> next() {
            return right.next();
        }

        @Override
        public void next(SnailfishNumber next) {
            right.next(next);
        }

        @Override
        public String toString() {
            return "[" + left + "," + right + "]";
        }
    }

    static abstract class AbstractSnailfish implements SnailfishNumber {
        private int level = 0;

        @Override
        public SnailfishNumber add(SnailfishNumber other) {
            PairSnailfish snailfish = new PairSnailfish(this, other);
            return snailfish.reduce();
        }

        @Override
        public SnailfishNumber copy() {
            return SnailfishNumber.parse(toString());
        }

        @Override
        public int number() {
            return 0;
        }

        @Override
        public void add(int number) {
            // no need to do anything
        }

        @Override
        public boolean willExplode() {
            return level >= 4;
        }

        @Override
        public boolean isRegular() {
            return false;
        }

        @Override
        public SnailfishNumber addLevel() {
            level++;
            return this;
        }

        protected SnailfishNumber level(int l) {
            level = l;
            return this;
        }

        protected SnailfishNumber pair(SnailfishNumber left, SnailfishNumber right) {
            return new PairSnailfish(left, right).level(level);
        }

        protected SnailfishNumber regular(int n) {
            SnailfishNumber number = new RegularSnailfish(n).level(this.level);
            prev().ifPresent(number::prev);
            next().ifPresent(number::next);
            prev().ifPresent(p -> p.next(number));
            next().ifPresent(p -> p.prev(number));
            return number;
        }
    }

    @SuppressWarnings("unused")
    private static final String INPUT1 = """
            [[[0,[4,5]],[0,0]],[[[4,5],[2,6]],[9,5]]]
            [7,[[[3,7],[4,3]],[[6,3],[8,8]]]]
            [[2,[[0,8],[3,4]]],[[[6,7],1],[7,[1,6]]]]
            [[[[2,4],7],[6,[0,5]]],[[[6,8],[2,8]],[[2,1],[4,5]]]]
            [7,[5,[[3,8],[1,4]]]]
            [[2,[2,2]],[8,[8,1]]]
            [2,9]
            [1,[[[9,3],9],[[9,0],[0,7]]]]
            [[[5,[7,4]],7],1]
            [[[[4,2],2],6],[8,7]]""";

    @SuppressWarnings("unused")
    private static final String INPUT2 = """
            [[[0,[5,8]],[[1,7],[9,6]]],[[4,[1,2]],[[1,4],2]]]
            [[[5,[2,8]],4],[5,[[9,9],0]]]
            [6,[[[6,2],[5,6]],[[7,6],[4,7]]]]
            [[[6,[0,7]],[0,9]],[4,[9,[9,0]]]]
            [[[7,[6,4]],[3,[1,3]]],[[[5,5],1],9]]
            [[6,[[7,3],[3,2]]],[[[3,8],[5,7]],4]]
            [[[[5,4],[7,7]],8],[[8,3],8]]
            [[9,3],[[9,9],[6,[4,9]]]]
            [[2,[[7,7],7]],[[5,8],[[9,3],[0,2]]]]
            [[[[5,2],5],[8,[3,7]]],[[5,[7,5]],[4,4]]]""";

    @SuppressWarnings("unused")
    private static final String INPUT = """
            [[[5,[4,8]],0],[[[2,2],5],[7,9]]]
            [[[[4,2],0],[4,[9,9]]],[[[7,0],[9,8]],5]]
            [[[[3,2],2],4],0]
            [[8,7],[[9,4],[8,[5,5]]]]
            [[[1,7],[[8,2],[3,5]]],[6,8]]
            [[[[7,1],[4,2]],[[6,0],[3,8]]],[[[5,2],8],[7,[4,7]]]]
            [[6,[5,8]],[[4,[3,0]],5]]
            [[[[1,2],[1,5]],[[7,1],6]],[2,[4,7]]]
            [[9,[[5,3],3]],9]
            [6,[[[6,1],2],[6,[5,6]]]]
            [[[9,8],[[5,6],7]],[[4,[9,9]],[8,1]]]
            [[4,7],[[[3,1],[1,5]],[5,8]]]
            [[[9,7],[5,[6,0]]],[[9,[3,5]],7]]
            [[[[1,2],[1,4]],4],0]
            [[[[0,0],[1,2]],[[0,2],[1,6]]],[0,[[6,2],[5,1]]]]
            [[[1,2],[[0,2],4]],[[5,[7,3]],2]]
            [[5,[1,[6,3]]],[5,[2,[5,3]]]]
            [[[9,[7,7]],7],[[8,1],[[9,1],7]]]
            [[[[6,5],6],[6,5]],[7,[9,[3,9]]]]
            [[6,[9,3]],6]
            [[5,[[2,3],[9,1]]],[0,[[5,8],4]]]
            [[[[4,9],[2,3]],7],6]
            [[[2,6],6],[[[9,0],9],[4,[6,1]]]]
            [[[9,[9,1]],[4,4]],[0,[6,8]]]
            [8,[2,[[0,4],[5,4]]]]
            [[3,[9,4]],[[0,[6,9]],2]]
            [[[1,1],[[0,1],[1,9]]],[[5,4],[6,9]]]
            [4,[2,[[6,9],0]]]
            [[[6,[3,7]],[3,7]],[1,[2,[4,7]]]]
            [[[[6,4],[0,0]],[[8,2],5]],[[8,[2,4]],[4,[9,1]]]]
            [[[[8,1],[8,0]],[5,[7,6]]],[2,[[0,2],[9,2]]]]
            [[6,7],[[9,[1,1]],[[9,2],9]]]
            [[[[7,2],[8,8]],0],4]
            [[[2,1],[[3,1],9]],9]
            [[[[5,5],9],[[7,8],[6,0]]],[[[4,0],[0,6]],[6,2]]]
            [[6,[3,[9,4]]],[[[5,5],5],2]]
            [[[4,3],[9,[8,4]]],4]
            [[[0,[5,9]],[[9,6],8]],[7,[3,[8,9]]]]
            [[6,[[8,2],[0,2]]],[[8,8],[[7,9],2]]]
            [[[0,[8,0]],7],[[[7,2],[6,6]],[[5,5],5]]]
            [5,[[1,[3,6]],[[0,7],6]]]
            [0,[[[5,7],[6,2]],8]]
            [[[4,[5,4]],[[2,9],[5,3]]],[7,[2,4]]]
            [[6,[[8,4],6]],9]
            [[[7,[7,7]],[2,9]],[8,[5,[6,4]]]]
            [[[[7,9],[9,9]],[[6,1],[5,5]]],[[[4,3],[7,3]],[6,[0,3]]]]
            [[2,[2,0]],6]
            [[[[2,3],2],1],[0,2]]
            [[[[8,6],[5,6]],3],1]
            [[[[4,9],[2,4]],2],[2,[[6,3],[3,4]]]]
            [0,[[[1,0],[4,0]],8]]
            [[4,[6,[2,1]]],[[[5,8],4],[[8,0],4]]]
            [[[0,0],[[3,4],1]],[9,[1,[7,0]]]]
            [[0,0],[[[9,3],8],[[1,7],[4,6]]]]
            [[[4,3],3],[[[3,3],9],9]]
            [[[[2,0],[0,1]],[[1,2],[1,0]]],[[[6,6],1],[7,1]]]
            [[1,[[2,7],9]],[[[9,1],6],[[7,0],0]]]
            [[7,[[5,4],0]],8]
            [[6,9],[[[8,1],6],[5,[1,2]]]]
            [[7,6],[[[1,9],2],[0,3]]]
            [[[9,7],[9,[5,2]]],[[[0,0],2],[0,8]]]
            [[9,[6,2]],[5,8]]
            [[[6,[0,3]],[[5,1],[4,4]]],[6,[5,[1,9]]]]
            [[8,8],[[[3,1],7],[[8,3],3]]]
            [[[[1,1],[9,5]],9],[[[2,8],[6,4]],[[1,2],[4,5]]]]
            [[[1,7],8],[[5,[0,6]],[9,[3,3]]]]
            [[7,3],[[[8,2],3],4]]
            [[9,3],[[1,[7,0]],5]]
            [[[9,[2,2]],[7,5]],[[7,[1,7]],[[0,5],7]]]
            [[1,[[0,3],3]],1]
            [[9,[[3,0],[9,0]]],1]
            [[2,[[3,9],7]],[[[8,1],[7,2]],[9,[6,3]]]]
            [4,[[0,[0,4]],[0,1]]]
            [[[[2,8],6],[[6,6],[5,8]]],[[1,[7,5]],[[2,2],[6,0]]]]
            [[[6,7],8],[[[1,5],[9,3]],[0,2]]]
            [[[[6,6],[6,2]],[0,6]],[[[1,5],2],[[0,3],[3,9]]]]
            [[5,[8,2]],[3,8]]
            [[8,7],[[0,5],[3,[6,8]]]]
            [[6,[[2,3],5]],[[9,[0,8]],[[2,4],[1,8]]]]
            [[[[5,7],[4,3]],[[5,4],5]],[0,[[6,5],2]]]
            [2,[[5,[0,7]],[3,[4,0]]]]
            [1,9]
            [[[[1,4],1],[0,[1,2]]],2]
            [[4,3],[5,[6,4]]]
            [[[4,4],[[8,0],[6,5]]],[[4,[9,1]],[[1,1],[2,2]]]]
            [[4,3],[[[1,1],1],[[4,6],[5,7]]]]
            [[[[6,1],[5,3]],2],[[[0,6],[7,3]],8]]
            [[[2,8],5],[1,[3,[8,7]]]]
            [[7,[5,[9,0]]],[[[9,1],2],[2,[9,6]]]]
            [[[[7,3],1],[[4,6],[5,1]]],[[[4,7],4],[[5,2],[3,7]]]]
            [[[[2,3],8],[7,8]],[[[5,5],[2,5]],[[6,8],1]]]
            [[[2,1],[[8,9],[4,3]]],[[8,[9,0]],7]]
            [[[[8,2],5],0],[[8,[9,6]],[[6,1],1]]]
            [[[3,[4,9]],[[5,4],[2,2]]],4]
            [[[[9,8],4],[[7,4],9]],[[0,7],6]]
            [[7,[[6,1],8]],[[2,0],[2,5]]]
            [[[[3,2],6],5],6]
            [6,[3,5]]
            [[[[7,1],7],4],[[[4,6],5],[1,[7,9]]]]
            [[[[7,0],7],[8,9]],[5,[[2,5],6]]]
            """;

    @Test
    public void testWillExplode() {
        assertFalse(willExplode("[[[[4,0],[5,4]],[[7,7],[6,0]]],[[8,[7,7]],[[0,15],[11,0]]]]"));
        assertTrue(willSplit("[[[[4,0],[5,4]],[[7,7],[6,0]]],[[8,[7,7]],[[0,15],[11,0]]]]"));
    }

    @Test
    public void testSimpleAdd() {
        assertEquals("[[[[0,7],4],[[7,8],[6,0]]],[8,1]]",
                     add("[[[[4,3],4],4],[7,[[8,4],9]]]", "[1,1]"));
    }

    @Test
    public void testAdd1() {
        assertEquals("[[[[4,0],[5,4]],[[7,7],[6,0]]],[[8,[7,7]],[[7,9],[5,0]]]]",
                     add("[[[0,[4,5]],[0,0]],[[[4,5],[2,6]],[9,5]]]", "[7,[[[3,7],[4,3]],[[6,3],[8,8]]]]"));
    }

    @Test
    public void testAdd2() {
        assertEquals("[[[[6,7],[6,7]],[[7,7],[0,7]]],[[[8,7],[7,7]],[[8,8],[8,0]]]]",
                     add("[[[[4,0],[5,4]],[[7,7],[6,0]]],[[8,[7,7]],[[7,9],[5,0]]]]", "[[2,[[0,8],[3,4]]],[[[6,7],1],[7,[1,6]]]]"));
    }

    @Test
    public void testAdd3() {
        assertEquals("[[[[7,0],[7,7]],[[7,7],[7,8]]],[[[7,7],[8,8]],[[7,7],[8,7]]]]",
                     add("[[[[6,7],[6,7]],[[7,7],[0,7]]],[[[8,7],[7,7]],[[8,8],[8,0]]]]", "[[[[2,4],7],[6,[0,5]]],[[[6,8],[2,8]],[[2,1],[4,5]]]]"));
    }

    @Test
    public void testAdd4() {
        assertEquals("[[[[7,7],[7,8]],[[9,5],[8,7]]],[[[6,8],[0,8]],[[9,9],[9,0]]]]",
                     add("[[[[7,0],[7,7]],[[7,7],[7,8]]],[[[7,7],[8,8]],[[7,7],[8,7]]]]", "[7,[5,[[3,8],[1,4]]]]"));
    }

    @Test
    public void testAdd5() {
        assertEquals("[[[[6,6],[6,6]],[[6,0],[6,7]]],[[[7,7],[8,9]],[8,[8,1]]]]",
                     add("[[[[7,7],[7,8]],[[9,5],[8,7]]],[[[6,8],[0,8]],[[9,9],[9,0]]]]", "[[2,[2,2]],[8,[8,1]]]"));
    }

    @Test
    public void testAdd6() {
        assertEquals("[[[[6,6],[7,7]],[[0,7],[7,7]]],[[[5,5],[5,6]],9]]",
                     add("[[[[6,6],[6,6]],[[6,0],[6,7]]],[[[7,7],[8,9]],[8,[8,1]]]]", "[2,9]"));
    }

    @Test
    public void testAdd7() {
        assertEquals("[[[[7,8],[6,7]],[[6,8],[0,8]]],[[[7,7],[5,0]],[[5,5],[5,6]]]]",
                     add("[[[[6,6],[7,7]],[[0,7],[7,7]]],[[[5,5],[5,6]],9]]", "[1,[[[9,3],9],[[9,0],[0,7]]]]"));
    }

    @Test
    public void testAdd8() {
        assertEquals("[[[[7,7],[7,7]],[[8,7],[8,7]]],[[[7,0],[7,7]],9]]",
                     add("[[[[7,8],[6,7]],[[6,8],[0,8]]],[[[7,7],[5,0]],[[5,5],[5,6]]]]", "[[[5,[7,4]],7],1]"));
    }

    @Test
    public void testAdd9() {
        assertEquals("[[[[8,7],[7,7]],[[8,6],[7,7]]],[[[0,7],[6,6]],[8,7]]]",
                     add("[[[[7,7],[7,7]],[[8,7],[8,7]]],[[[7,0],[7,7]],9]]", "[[[[4,2],2],6],[8,7]]"));
    }

    @Test
    public void testExplode() {
        assertEquals("[[[[0,9],2],3],4]", explode("[[[[[9,8],1],2],3],4]"));
        assertEquals("[7,[6,[5,[7,0]]]]", explode("[7,[6,[5,[4,[3,2]]]]]"));
        assertEquals("[[6,[5,[7,0]]],3]", explode("[[6,[5,[4,[3,2]]]],1]"));
        assertEquals("[[3,[2,[8,0]]],[9,[5,[4,[3,2]]]]]", explode("[[3,[2,[1,[7,3]]]],[6,[5,[4,[3,2]]]]]"));
        assertEquals("[[3,[2,[8,0]]],[9,[5,[7,0]]]]", explode("[[3,[2,[8,0]]],[9,[5,[4,[3,2]]]]]"));
    }

    private static boolean willExplode(String input) {
        return snailfish(input).willExplode();
    }

    private static boolean willSplit(String input) {
        return snailfish(input).willSplit();
    }

    private static String explode(String input) {
        return snailfish(input).explode().toString();
    }

    private static String add(String input1, String input2) {
        return snailfish(input1).add(snailfish(input2)).toString();
    }

    private static SnailfishNumber snailfish(String input) {
        return SnailfishNumber.parse(input);
    }
}