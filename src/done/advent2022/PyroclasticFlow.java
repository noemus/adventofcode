package done.advent2022;

import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.function.UnaryOperator;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.lang.System.lineSeparator;
import static java.util.stream.Collectors.joining;
import static util.LineSupplier.lines;

public class PyroclasticFlow {
//    private static final long MAX_SHAPES_COUNT = 2022;
//    private static final long MAX_SHAPES_COUNT = 3*2022;
//    private static final long MAX_SHAPES_COUNT = 1_000_000_000_000L;
//    private static final long MAX_SHAPES_COUNT = 16 + 34;
    private static final long MAX_SHAPES_COUNT = 1751 + 1169;

    // ex 1: period 35, starting from 16, height increases 53 for each period
    // count 16 => height 26
    // count 51 => height 79
    // count 86 => height 132
    // PERIODS = (COUNT - 16) / 35
    // =========================================================================
    // ex 2: period 1740, starting from 1751, height increases 2716 for each period
    // count 1751 => height 26
    // count 3491 => height 5437
    // PERIODS = (COUNT - 1751) / 1740
    // shape: 1, jet: 9, height: 2721, count: 1751
    // shape: 1, jet: 9, height: 5437, count: 3491
    public static void main(String[] args) {
        try (Scanner in = new Scanner(INPUT)) {
            Chamber chamber = lines(in)
                    .map(Chamber::new)
                    .findFirst()
                    .orElseThrow();

            while (!chamber.hasFinished()) {
                chamber.nextStep();
            }

//            System.out.println(chamber.print());

            System.out.println("Result   : " + chamber.height());
//            int offset = 16;
//            int period = 35;
//            int increase = 53;
            int offset = 1751;
            int period = 1740;
            int increase = 2716;
            long start = 1_000_000_000_000L - offset;
            long periods = start / period;
            long correction = chamber.height();
            System.out.println("Periods  : " + periods);
            System.out.println("Remainder: " + (start - (periods * period)));
            long result = periods * increase + correction;
            System.out.println("Result2  : " + result);
        }
    }

    static class Chamber {
        private int nextShapeIndex;
        private final List<Shape> shapesOrder = List.of(
                // -
                Shape.of(
                        Point.of(0, 0),
                        Point.of(1, 0),
                        Point.of(2, 0),
                        Point.of(3, 0)
                ),
                // +
                Shape.of(
                        Point.of(1, 0),
                        Point.of(1, -1),
                        Point.of(1, -2),
                        Point.of(0, -1),
                        Point.of(2, -1)
                ),
                // reversed L
                Shape.of(
                        Point.of(0, 0),
                        Point.of(1, 0),
                        Point.of(2, 0),
                        Point.of(2, -1),
                        Point.of(2, -2)
                ),
                // |
                Shape.of(
                        Point.of(0, 0),
                        Point.of(0, -1),
                        Point.of(0, -2),
                        Point.of(0, -3)
                ),
                // square
                Shape.of(
                        Point.of(0, 0),
                        Point.of(1, 0),
                        Point.of(0, -1),
                        Point.of(1, -1)
                )
        );

        private int nextJetIndex;
        private final List<Direction> jets;

        private State state = State.NEW_SHAPE;

        private long shapeCount = 0;
        private long removedRows = 0;
        private final Set<Point> shapes = new HashSet<>();

        private Shape currentShape;

        private static final int FLOOR = 100;
        private static final int LEFT_WALL = -1;
        private static final int RIGHT_WALL = 7;

        private int topRow = FLOOR;

        Chamber(String line) {
            this.jets = line.chars().mapToObj(Direction::from).toList();
        }

        long height() {
            return Math.abs(topRow - FLOOR) + removedRows;
        }

        void nextStep() {
            if (state == State.NEW_SHAPE) {
                placeNewShape();
            } else {
                move();
            }
        }

        boolean hasFinished() {
            return state == State.NEW_SHAPE && shapeCount == MAX_SHAPES_COUNT;
        }

        private void move() {
            switch (nextMove()) {
                case TOP -> moveTop();
                case LEFT -> moveLeft();
                case RIGHT -> moveRight();
                case DOWN -> moveDown();
            }
        }

        private void moveTop() {
            currentShape = currentShape.moveTo(new Point(2, topRow - 4));
            state = State.JETS;
        }

        private void moveLeft() {
            Shape movedShape = currentShape.moveLeft();
            if (movedShape.positions().noneMatch(shapes::contains) && movedShape.left() > LEFT_WALL) {
                currentShape = movedShape;
            }
            state = State.FALL;
        }

        private void moveRight() {
            Shape movedShape = currentShape.moveRight();
            if (movedShape.positions().noneMatch(shapes::contains) && movedShape.right() < RIGHT_WALL) {
                currentShape = movedShape;
            }
            state = State.FALL;
        }

        private void moveDown() {
            Shape movedShape = currentShape.moveDown();
            if (movedShape.positions().anyMatch(shapes::contains) || movedShape.bottom() >= FLOOR) {
                state = State.NEW_SHAPE;
            } else {
                currentShape = movedShape;
                state = State.JETS;
            }
        }

        private void nextShape() {
            nextShapeIndex = nextShapeIndex % shapesOrder.size();
            currentShape = shapesOrder.get(nextShapeIndex++);
        }

        private void placeNewShape() {
            saveShape();
            nextShape();
            move();
            state = State.JETS;
        }

        private void saveShape() {
            if (currentShape != null) {
                currentShape.positions().forEach(shapes::add);
                shapeCount++;
                topRow = Math.min(currentShape.top(), topRow);
            }
//            if (shapeCount > 0 && (shapeCount % 1000) == 0) {
//                pruneRows();
//            }
            if (nextShapeIndex == 1) {
                System.out.println("shape: " + nextShapeIndex + ", jet: " + nextJetIndex + ", height: " + height() + ", count: " + shapeCount);
            }
        }

        private Direction nextMove() {
            return switch (state) {
                case JETS -> nextJet();
                case FALL -> Direction.DOWN;
                case NEW_SHAPE -> Direction.TOP;
            };
        }

        private Direction nextJet() {
            nextJetIndex = nextJetIndex % jets.size();
//            if (nextJetIndex == 0) {
//                System.out.println("shape: " + (nextShapeIndex + 1) + ", height: " + height() + ", count: " + shapeCount);
//            }
            return jets.get(nextJetIndex++);
        }

        private void pruneRows() {
            IntStream.rangeClosed(Math.min(topRow - 4, currentShape.top()), FLOOR)
                     .mapToLong(y -> IntStream.rangeClosed(LEFT_WALL, RIGHT_WALL)
                                                        .mapToObj(x -> new Point(x, y))
                                                        .flatMap(p -> Stream.of(p, p.moveUp()))
                                                        .filter(shapes::contains)
                                                        .mapToInt(Point::x)
                                                        .distinct()
                                                        .count())
                     .filter(count -> count == 7)
                     .findFirst().ifPresent(newFloor -> {
                         int delta = (int) (FLOOR - newFloor);
                         removedRows += delta;
                         topRow += delta;
                         List<Point> movedPoints = shapes.stream()
                                                         .filter(p -> p.y < newFloor)
                                                         .map(p -> new Point(p.x, p.y + delta))
                                                         .toList();
                         shapes.clear();
                         shapes.addAll(movedPoints);
                     });
        }

        String print() {
            return IntStream.rangeClosed(Math.min(topRow - 4, currentShape.top()), FLOOR)
                     .mapToObj(y -> IntStream.rangeClosed(LEFT_WALL, RIGHT_WALL)
                                             .mapToObj(x -> new Point(x, y))
                                             .map(this::print)
                                             .collect(joining())
                     ).collect(joining(lineSeparator())) + lineSeparator();
        }

        private String print(Point p) {
            if (shapes.contains(p)) {
                return "#";
            }
            if (currentShape.positions().anyMatch(p::equals)) {
                return "@";
            }
            if (p.x == LEFT_WALL || p.x == RIGHT_WALL) {
                if (p.y == FLOOR) {
                    return "+";
                }
                return "|";
            }
            if (p.y == FLOOR) {
                return "-";
            }
            return ".";
        }
    }

    enum State {
        JETS,
        FALL,
        NEW_SHAPE,
    }

    enum Direction {
        TOP,
        LEFT,
        RIGHT,
        DOWN,
        ;

        static Direction from(int ch) {
            if (ch == '>') {
                return RIGHT;
            }
            if (ch == '<') {
                return LEFT;
            }
            throw new IllegalArgumentException("Illegal direction: " + ch);
        }
    }

    record Shape(Point[] points) {
        int top() {
            return Stream.of(points).mapToInt(Point::y).min().orElseThrow();
        }

        int bottom() {
            return Stream.of(points).mapToInt(Point::y).max().orElseThrow();
        }

        int left() {
            return Stream.of(points).mapToInt(Point::x).min().orElseThrow();
        }

        int right() {
            return Stream.of(points).mapToInt(Point::x).max().orElseThrow();
        }

        Shape moveTo(Point vector) {
            return transform(p -> p.moveTo(vector));
        }

        Shape moveDown() {
            return transform(Point::moveDown);
        }

        Shape moveLeft() {
            return transform(Point::moveLeft);
        }

        Shape moveRight() {
            return transform(Point::moveRight);
        }

        private Shape transform(UnaryOperator<Point> transformation) {
            return new Shape(Stream.of(points).map(transformation).toArray(Point[]::new));
        }

        Stream<Point> positions() {
            return Stream.of(points);
        }

        static Shape of(Point... points) {
            return new Shape(points);
        }
    }

    record Point(int x, int y) {
        Point moveTo(Point vector) {
            return new Point(x + vector.x, y + vector.y);
        }

        Point moveUp() {
            return new Point(x, y - 1);
        }

        Point moveDown() {
            return new Point(x, y + 1);
        }

        Point moveLeft() {
            return new Point(x - 1, y);
        }

        Point moveRight() {
            return new Point(x + 1, y);
        }

        static Point of(int x, int y) {
            return new Point(x, y);
        }
    }

    @SuppressWarnings("unused")
    private static final String SHAPES = """
            ####
                        
            .#.
            ###
            .#.
                        
            ..#
            ..#
            ###
                        
            #
            #
            #
            #
                        
            ##
            ##
            """;

    @SuppressWarnings("unused")
    private static final String INPUT1 = """
            >>><<><>><<<>><>>><<<>>><<<><<<>><>><<>>
            """;

    @SuppressWarnings("unused")
    private static final String INPUT = """
            >>><<<>>>><<<<>><<<<>><>>><>>>><><<<<>>>><<>>><<<>><>>><<>><>>><<>>><<>><>>><>>><<<<><<<>><<<>>><<>>>><>>>><<<<>>>><<<>>>><<<>>>><<<>>>><<<<><<<<><<<>>>><<<>>><<<><<<>>>><<>><<<>><<<><<<><<>><<>>>><<>>><<><<>><<><<>>><<>>>><<<><>>><<<<>>><<<<><><<<><<>>><<>><>>>><<<<><<>>><<<>>><<<<>>>><<<<>><<<>>><<<<>>><<<>>>><<<>>>><<<>>>><<<<>>><<<>>>><<<><><<<><<<><>>>><>><>>><<><<<>>><><<<>><<<<>><<>><>><>>>><<<<>>><<>>>><<><<>><<<<>><><<>>>><<<>>>><<<>><<<<>>><<<<>>>><>>>><<>><<<><<<>><<<<>>><<<<>><<>><><<<<>>><<><<<>>>><<>>>><<<<>>>><<<>>>><<<>>><<>>>><>>>><<>>><<<>>>><>>>><<<<>>>><>>>><<<><<<>>>><>><>>><>>>><<<><>><<<<><<><>>>><<><<<<><<<>>><><>>>><<><>>>><<<>>><<<<>>><>>>><<<>>><<<<>>>><<>><<<>>><<>>>><<><<<<><><<<<>>><<<<>>><<<<>>><<>>><<<>><<>>><<<<>>>><<<<>>>><<<>>><>>>><<<<>>><>><<>>><<><<<<>><<>>><<<><><<<<>>>><<<>><><<>>><<>><<><>>><<<>>>><<<<>>><<<><<>><<>>><>><<>>>><<>>>><<>>>><<>>>><<>><>>>><<><>>>><<<>>>><<<>>><<><<<<><>><<<>><<<<>><<<<>><<>>>><<<>><<>><>>>><<<>>>><<<>>>><<<<>>><<<<>><<><<<>>>><<<>>>><<>>><<><><<<<>><<<<><<<><<>><<><<<<>><<<<>><<<<><<<<>>>><<<>><<<><<<<>>><>><>>><>>><<><<<<>>><<<<>>>><<<><<<>><<<>><<<>>><<>>><<<>>><>>><<><>>>><<<<>>><<<<>>>><<<>>>><>>><>><<>><<<>><><<<>>>><<<>>><><<><<><<<<><<<<>>><<<<><>>>><<<>>>><<>>><<<<>>>><<<<>>><<>>><>>>><>><<<>>><<<<>>>><>><<>>><<<><<<>>><<<<><<<<><>>>><<><>><<<<>><<<<>>>><>>>><<>>><<<<><<<><<>>>><<><<<>>><<<><<<<>><<>>><<<<>><>>>><><<<<>>><<<>>>><><<>>>><<<<>>>><<<<>><<<>>><<<<>>><<<>>><>>>><<>><>><<<<><<<>><<>>>><>><>>>><<<>>><<><<<>><<>><<<>><<>>><<<<>><><<<>>><<<<>>><<<>>><<<<>>><<<<>>><<>>><>>><<>>><<<>><>><<<>>><<<<>><>>>><<<>>><>><>><<<>>><>>><<>>><<>><<<<>>>><<>>>><<<<>><>>><<<<>>><<<<>>>><<<>>>><>><<<>>>><<<><<>>><<><>><<<>>><<<>>><<><>>><<>>>><<<><<<<><><>>><<<<>>>><<<<><<<>><<<>><<<>>><<<>>>><>>><<<>>><<<<>><<>><<<>>>><<<><<>>><<<>><<<<>>>><<<<>><<<><<<>>><<<<><>><<<><<<<>>>><<><>><<<>>><<>>>><>>>><<<><<<<>>>><<>>>><<>><<<>>>><><<<>><<>>><<><<<<>>><<>><<<>><<<<>>>><<>>><<>>><<<<>>>><<>>><<<>><<>>>><<>><<<>>><<<>>><<>>>><<<<><>><>>><<<<>>><<<><<>><<<><<<>>><>><<<><<><<<>>><<><<>>><>><<<>>>><<><>><<<>>><<<>>>><<>><><>>><<<<>>><<>><<<><<<<>>>><>>><<<<>>><><><<<<><><<>><<>><>>>><<<<>>><<<>>>><><<<<>><<<<>><<><<<<>>><>>><<<><<<>>><<<>>><<<<>>><<>><<<<><<<<>>>><<>><<<<>>><<<>><<>>><<<><<<>>>><<>><<<<>>><<<<>>>><<<>>>><<>>><>>><<<<><<<<><><<>>>><<<>><>>>><<<>>><>><>><><<>>><<<>>>><<<>><<><<><<>><<<<><<<><>>>><>>><>><>>><<<<>>><<<><>><<<<>>><<<<><><>>>><<>>>><<>>>><<>>>><<<<>>><>><<<<>>>><<<>><<><<<<>>>><>><<>><>><>>>><<>>><>>>><<<<>><<<>>><<<>>>><<<<>><<><<<<>><>>><<>>>><<<<>><<<<>><>>>><><<<<>>>><<><<<><<>><<<<>>>><<><<<<><<<<>>>><>><<>><<><<<<>><<<><<<<>>><><<>>><>>>><<<<>><>>><<<><<>>><>>>><>><<<>>>><>>><>><<<<>>><<<><<<<>>>><<<>>><<<>><<<>>>><<<>>><<<<>>><<<<><>>><<<<>>>><>><>>><>>><<>><<<>>>><<<>><<<>>><<<>>><<>>>><<<<><<<<><<<<>>>><<<>>><<<>>>><<<><<<<>>><<<<><<<<>><<<<>><>><<<<><<<<>>><<>><><<<<><><<<<>><<>>>><<>><>><<<<><<<<>>>><<>>>><<<>>><<<<>>>><<<<>>>><<<<>><<><>>>><>>>><<><<<<><<>><<>>>><<>><<<>>><>><><<<<><<><<>><<<>>><<>>><<>>>><<<><>>>><<<<>>>><<<>><<<><<<<><<><<<><<<>>>><<<<>>>><<<<><<<<><>>><><<<<>>>><<<<><<>><<<>>>><<<<>>><>><<<<>>><<<<>>>><<<<>>><>>><<>>><<<><>>><><<<<>>><>>><>><><<<>><>>>><><<<>><<><<<>>><<><<<<>>>><<<<>>>><<<<>><<<><<<><><>>><<<><>><<>>><>>>><<<<>>><<>>><<<<>>>><<<<>>><<<>><<<<><>><<<<>>><>><<<>>><<<<>><<<<>><<>>><<<<>>>><<<>>><<>>>><<>>>><<><<<<><<>>><<<<>><<>><<<><<<<>><<<>>><<<>>>><<<<>><>>>><>><<>>>><><<>><<<<><><<<>>><<<>><<<>>>><<<<>>>><<<<>>>><<<>>>><>>>><<<>>>><>><<<>><>>><<<><>>><<><>>><<<<>><<<<>><<><<<>>>><<<<>>><<<<>>>><<<<><<<<>>><<<>>><<<>><<<>>><<<<>><<<><<<>><<<><<<<>>>><<<>>><<<><<<<>>><<<>>>><><<<<>>><><<<>>>><<<<>><>>>><<><>>>><><<<><<<<>>>><<<>><<>>><<<<><<>>><><<><<>>><<<>>><<<<><<>><<><<<>>><<>>>><<<>>><<><>>><<<>><<<>>><>>>><<<<>>>><<>>>><<>><<>><<<<><<<>>>><<<<>>>><><<<><<>><<<<>>><>><>><<>>>><<<><<<>>>><>>><<><<><<<<>>>><<<>>>><<>><><<<<>>><<>>><<<<>><>><<<<><<>>><<<>>><<<<>>><>><<<>><<>>>><<<>>><><<<>><<<<>><>>><<>><<<<>>><<><<>>><>>><><<<<>>>><<<<>><<>><<<>>><<<<>>><<>>>><<><<<<>>>><>>><<>>>><>>><<<<>>>><<<<>>><<>>><<<>>><<<><<<<><>>>><<>><><<<<>>>><>>>><<<<>><>>>><>><>>>><<<<>>>><<>>>><<><<<<>>>><<<><>>><<<<><<>>><<<><<<<><>><<<>>><<>><<<<><<<>>>><<>><<>>><<<><<<<>><><<<><>>><<<<>>>><<>>><>>>><>>><<<<>>><<<><><<>>>><<<>><<<<>>>><<><<<>>><<<<>><<<<>>><>>>><<<<>><<>>><<<<>>><>>><<<<><<<>>>><<>>><<<<><<><<<>>>><<<<>><><<<<>>>><<<>>>><<<<><<>><<>>><<<>>><>><><<>>><<>><<<<><>><<<><>>><<<<>>><<>>><>><<<>>>><<<<>><><<<>><<<>><<>><<<>><<<<>>>><<<<><<<<>>>><<<<>>><<<><<<>>><>>>><<<>>><<<<>>>><>>>><>>><<<<>>>><<>>><<>>>><<>><>>><<>>>><>>>><<<<>>>><>>><<<><<<<><>>><<<<><>>>><<<>>>><>>><<>><<><<<>><<<<><<<><<<>><<<><<>>>><<<><<>>>><<><>><<>>><<>>><>><<<>><>>>><<>>>><<>>><>>><<<<>>>><<><<>>>><<<>>>><><<>>>><<<<>><>><<><>><<><>>><>>>><<<>>>><><<<>>><<<>>>><<<>>><<<>>><>><>>><>>>><<<><<<>><>>><<><<>><<<<><<>><<>><<<>>><<>>><<<<>>><<><>>><<>>>><<<><<<>>><><<<<><<><<<>><<<<>>><><<<<><<<<><>>>><<>>>><<<<>>><<<<>><<<<>><<<><<>><<>><>><<<<>>><<<<><>><<<<><<<<><<><<>><<<><<>>><<<<>>><<<>>>><<<>><>><<<>>><<<>><>>><<>>>><<>>><<<<>><<>>>><<<<><>>><<<><<<>>><<<>>><<><>>><>>>><<<>><<<<>><><<<>>>><<<<>>><<>><<<<>>><<<>><><<>>>><<>>>><<<>>>><<<><<<>>>><<<<>><<<<>>>><<>>><<<<><>>>><>>>><>><>>>><<<>>>><>>><<<<><<<><<<<>>><><<<<>><>><<>>>><<<>><<<>>>><>><<<><>>><<<><>><<<<>><<>>>><<>><>>><<<<>>><<<>><>>><<<>>><<>>><>>><<<<>>>><<>>>><>>>><<<<>><<<>><><>><<><<<>>>><<<>>><<<>>>><<<<>><<<<><<<>>>><<>><>><<<<>>><<>>>><>>>><<<<>>><><<<>>>><<<>>><<<>>>><<<><<>><<>>>><<<<>><<<>>>><<<>><<<<>>>><<>><<>>><<>>><<>>>><<>><>>><><<>>>><>><>><>><>><<<>><<<<>><>>><<<><><<<<>>>><<<>>>><<<<>><<<<><>>><<><<<<>><<<<>><<<>>>><<<<>>><>>>><<<<>>><>><>>>><<<>>><<<<>><>>>><<><<<<>>>><<<>>>><<<><<<>>>><<<>>><<<>><>>><<<>>><<<<>>>><<<>><>>>><<>>><<>>><><<<<>>><<>><<>>><<>>><<<<>>><<<>><>>><<>>>><<<>><<<>>>><<<>><<<<>><<>>>><<<<><>>><<<><<<>>>><<>><<<<><<<>>><>>><<<<>>><<<<>><<<<>><<<<>><<<>><<<>>><<<<>>>><<<>>>><><>><>><<<>>><<<<><<<<>>><<<<><<<<><><>>><<<>>>><<<<>>>><>><<<<><<<>><>>>><<<>><>>>><<<>><>>><<>>><<<<>><><<>>><<<<><<<<><<<><<<<>>><<><<><>>>><<><<<<>><>>><<<<>>><>>><<<<><<<<>>><>>>><<>>>><<<>>>><<<>>>><>>><>><<<<><<<<>><<<<>>>><<<<>><>>><<>>><<<>>><>>><<<><><><>>><<<<><<>>>><>><<>><<<<>><<<<>>><><<<<>><<>>><<<>>>><>><><><<<><>><<<<>>><>>>><>>><<>><<<<><<>>><<<>>><<><<<>>><>>>><<<<>>>><<<><>>><>>><<<>><>>><<><<>><<><<<>><><>>><<<<>>>><<<>><<<><<<>>><<>><>>>><>>>><<<>>><<<>>><<>>>><<<<><<>>>><<>>>><<<<>>>><<<<>>>><<<>>><<>>><<>><<>>><<<<>><<<><>>>><<<>>><<<>>><<>>>><>><<>>>><<<<>>><<>>><<<<>><<>>>><<<<>>><>>>><<>>><<<>>><<<>><<<<><>>>><<><<>>><>>><<<>><<><<<<>>>><<<<>><>>>><<>>><<<>><<>>>><>><<>><<<<>><<>>><>>><<<>><<>><><<<<>>><>><<><>>>><<>>>><<<<>><><>>>><>>>><>>>><<>><<<<><>>>><<<>><<<>>><<>><<<><<<<>>><>>>><<>>><<<<>><>><<<<>><>>>><<<>><<<<>>>><<<<>>><>>>><<><<<><<>>><<><<>><<<><<><>>>><<>>><>><<<<>>>><<<<><>><<<>><>><<<><><<<<>>><<>>><<<>><<<><<<>>>><<<<>>><<<><>>>><<<><<><>><<<<>>>><<<<><<>>><<<<>>><<>>><>>>><<<>>>><<><>>><<<<>><>>><<<><<<>>>><<<<>><<<<>>><>>>><<>>><>>>><>>>><<>>>><<<>><<<><<<><<>>>><<<<><<>>><<>><>><<>><<<>>><<<<>>>><<>>><<<>><<>>><><>>>><<<>>><<<<>>>><<<<>>><<<<>><<><<<>><>><<<<><<<<>>><<<<><<<<><<<>>><<<<>>><<<<>>>><<<>>>><<<<>>><<<<><<<>>>><<><<<>>>><<<<>>>><>>>><<<<>>><<><<>><<>><<>>>><<<<><>>><<><<>><<><<<>><<>>>><<<<><>><<<><<<><<<>>>><<<<><<><<<<><<>>><>>>><<>>><<<>>>><<><<<<><<<<>><<<<><<><<>><<>>>><>><<<>>><<><<<>>>><<<>>>><<>><<><>><>><<<<>><<<>><>>>><<<<><<<>>>><<><<<<>><<<<><<<<>><<<<>>>><<<>>><<<<>>><<><<<>>>><>>>><<<>>>><<<<>><<><<<<>>><<<>>><<>><<<><><<>>>><<><<<>>>><<<>>>><<<<>>>><>>>><<<>>>><<<>>><>><<<><<<>>><<><<>>>><<<><<<>>><<>><<<>><><<<<>><><<<><<>>><<<>><<<<>>>><<>><<<<>>><<<<><>><>>>><<>><<<<>>>><<>><<<<>>>><>>>><<<>><<<>>>><><>>>><<>>><<>>><<<>><>><<><>><<<<>><<<<><<><<<<>><<<>>><<<><<<>>>><<<>>>><<<>>>><<<<>>>><<>>><<<<>><<<<>><<>>><>>><<><>><<<><><>>>><<><>>><<>>><<<>><>>><<><<<<>>><<>><<<<>>><<><>><<><<<<>>><<<>>>><>><<<<><<>>><<>><<<>>>><<<>><<<<>><<<<><<<>><>>><<<<>><<<<>>>><<<<><<>>><>>><<<<>>>><<<<>>><><><<<><<<<>>><><>><>>>><>>><>>>><<<>>>><<>><<<<><<<>>>><>>><<<<>>>><<>>>><<<<><<><<>>>><<>>><>><><>>><<<<>><>><<>>><<<>><>>><<>><<<><<>>>><<<<>>><<>>>><<<<>>><<><<<>>>><<<>>><<<>>><><<<>><<<<>>><>><<<<>><<>>><<<<><<<<>><<<<>>><>>><<>>><<>><<<>><>>><<<<>>><<<<><<<>>><<<>>>><<><><<<><<>><<>>>><<<>>>><<<<><<<<>>><>>>><<><<<<>><<<<>>>><<<<>>>><>>><<<<>><<<>>>><<<<>>><<<<>>>><<<<><>>>><<><>><<><<><<>>><<<<><<>>><<>>>><<<>><<<<><<<>>>><<<><>><<<<>>><<<>>><<<><<>><<<<>><<<><<>>>><><>>><<>>>><<>><>>>><<<><<>>>><<>>><<<<>><<<>>>><<>>>><<<<>>>><<<<>>><>><<<>>><<<<><<<>>>><<<>>>><>><<<<>>><<>><<<>>>><<>>><>>><<<<>><>>><<<>>><<<<><><<<>><<<<>>>><<<<>>>><<<<>>><>><><<>>>><<<>>>><<<>><<<>>><<>><<<<>><><<><<>>>><<>>><<<><<>>>><<<<>>>><<<>><>>><><<<><<<>>><<><<<<>>>><<<<>>><<>><<>>><<<<><<>>>><<>>>><<<>>>><<><>><<<>><<<<>>><<<<>>><<>><<<<>>>><<<<>>>><<<<><<<<>>>><<>>>><<<>>><<>><>>><<>>><<>>><<<>>>><<<>>><><>>>><<>><>>>><<<>><>>><<<><<>>><<><<<>><>>><<>><>>><<>>>><<<<>><<>>>><>><<<><<<<>>><<<><>>><<<<>>>><<<>><<<<><>>><<<<>><>>><<<>>>><<>>>><<<<>>><<><<<<>>><<<>>><><<<>>>><<><<<>>>><<>><>>>><<>>>><><<<><<>>>><<>><<>>><>><><<<>>>><<>>><<<<>><>>>><<<>><<><>>><<><<<>>>><<><<>>><>>>><<>>>><<<><<>>><>>><>><>><<<>>>><<<><<<>><<<<>><<<>><<>>>><>>>><<<<>>>><<>>><<><<<>>><<<<>><<<<>>>><<>><<<<><<><>>>><<<>>>><<>>><><<<<>><>>><<<<><<<<>>>><<<<>><>><>>>><<<<>>><<>>><>>>><<>>><<<<>>><><<<>>>><<>>><<<<>><<<><>>><><<><<><<><<<><<<>>>><<<>>>><<<<><>>>><>>><>><<>>><<<><>><<<<>>>><><<<>>><<<>><<><<<>>><<><<<>><<<><<<<>>><<>>>><<>>><<<><<<>><<><><<>>>><<>><<<>>>><<>>><<<<>>><<<<>>><<<>>>><<<<>><<<>>><<>>><<>>><>>><<>>><<>>><<<<>><<<<><<>><<<<>>>><>>><<>>>><<<<>><>><>>>><><>>>><>>>><<<>><<<<>>>><<<>>><<>>><<<<><<>><<<<>>><<<>>>><>>>><<<>><<<><<<>><<<<>><<><<>>><><<<<>><>><<>>><><<<<>>><<>>><<<><<>><<>><>><<>><<>>>><<<><<<<>>>><<<>>><<>><<<<>>><<<<>><><<<<>>>><<<<><>><>>>><<<><<>>>><<>>><<>><<<<>>>><><>><<<><>>>><<<>>><<<>>><>><<>>>><<<><<<><<<<><<>><<<>>><<<<>><<<<><<<<><<<>>>><<<><><<<<>><<><<>>>
            """;
}