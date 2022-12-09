package done.advent2022;

import org.junit.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.joining;
import static util.LineSupplier.lines;
import static util.Utils.Repeat.repeat;

public class RopeBridge {

    static final Bridge bridge = new Bridge();

    public static void main(String[] args) {
        try (Scanner in = new Scanner(INPUT)) {
            System.out.println();

            lines(in)
                    .map(Command::create)
                    .forEach(bridge::execute);

            System.out.println(bridge);
            System.out.println();

            System.out.println("Result: " + bridge.visited());
        }
    }

    static class Bridge {
        private final Head head;
        private final List<Tail> tails;

        private int minX;
        private int minY;
        private int maxX;
        private int maxY;

        Bridge() {
            this.head = new Head();
            // tails 1 to 9
            this.tails = List.of(
                    new Tail(), new Tail(), new Tail(),
                    new Tail(), new Tail(), new Tail(),
                    new Tail(), new Tail(), new Tail()
            );
        }

        void execute(Command cmd) {
            repeat(cmd.steps).action(() -> {
                head.move(cmd.dir);
                follow();
                updateBounds();
            });
        }

        private void follow() {
            Head h = head;
            for (Tail t : tails) {
                t.follow(h);
                h = t;
            }
        }

        private void updateBounds() {
            minX = Math.min(minX, head.pos.x);
            minY = Math.min(minY, head.pos.y);
            maxX = Math.max(maxX, head.pos.x);
            maxY = Math.max(maxY, head.pos.y);
        }

        int visited() {
            return visitedPositions().size();
        }

        @Override
        public String toString() {
            return IntStream.rangeClosed(minY, maxY)
                     .mapToObj(this::printRow)
                     .collect(joining("\n"));
        }

        private String printRow(int y) {
            return IntStream.rangeClosed(minX, maxX)
                            .mapToObj(x -> new Pos(x, y))
                            .map(this::printPos)
                            .collect(joining());
        }

        private String printPos(Pos pos) {
            if (pos.x == 0 && pos.y == 0) {
                return "s";
            }
            if (visitedPositions().contains(pos)) {
                return "#";
            }
            return ".";
        }

        private Set<Pos> visitedPositions() {
            return tails.get(8).visited;
        }
    }

    static class Head {
        protected Pos pos = new Pos(0, 0);

        void move(Direction direction) {
            pos = switch (direction) {
                case Left -> pos.left();
                case Right -> pos.right();
                case Up -> pos.up();
                case Down -> pos.down();
            };
        }

        void left() {
            move(Direction.Left);
        }

        void right() {
            move(Direction.Right);
        }

        void up() {
            move(Direction.Up);
        }

        void down() {
            move(Direction.Down);
        }
    }

    static class Tail extends Head {
        private final Set<Pos> visited = new HashSet<>();

        Tail() {
            visited.add(pos);
        }

        void follow(Head head) {
            if (pos.distance(head.pos) > 1) {
                if (pos.x + 1 < head.pos.x) {
                    right();
                    alignVertical(head);
                }
                if (pos.x - 1 > head.pos.x) {
                    left();
                    alignVertical(head);
                }
                if (pos.y + 1 < head.pos.y) {
                    down();
                    alignHorizontal(head);
                }
                if (pos.y - 1 > head.pos.y) {
                    up();
                    alignHorizontal(head);
                }
                visited.add(pos);
            }
        }

        private void alignVertical(Head head) {
            if (topright(head) || topleft(head)) {
                up();
            } else if (bottomright(head) || bottomleft(head)) {
                down();
            }
        }

        private void alignHorizontal(Head head) {
            if (topright(head) || bottomright(head)) {
                right();
            } else if (topleft(head) || bottomleft(head)) {
                left();
            }
        }

        /** .T
         *  H.  */
        private boolean bottomleft(Head head) {
            return left(head) && bottom(head);
        }

        /** H.
         *  .T  */
        private boolean topleft(Head head) {
            return left(head) && top(head);
        }

        /** T.
         *  .H  */
        private boolean bottomright(Head head) {
            return right(head) && bottom(head);
        }

        /** .H
         *  T.  */
        private boolean topright(Head head) {
            return right(head) && top(head);
        }

        private boolean top(Head head) {
            return pos.y > head.pos.y;
        }

        private boolean bottom(Head head) {
            return pos.y < head.pos.y;
        }

        private boolean left(Head head) {
            return pos.x > head.pos.x;
        }

        private boolean right(Head head) {
            return pos.x < head.pos.x;
        }
    }

    record Pos(int x, int y) {
        Pos left() {
            return new Pos(x - 1 , y);
        }

        Pos right() {
            return new Pos(x + 1 , y);
        }

        Pos up() {
            return new Pos(x, y - 1);
        }

        Pos down() {
            return new Pos(x, y + 1);
        }

        int distance(Pos pos) {
            return Math.max(
                    Math.abs(x - pos.x),
                    Math.abs(y - pos.y)
            );
        }
    }

    record Command (Direction dir, int steps) {
        static Command create(String line) {
            String[] tokens = line.split(" ");
            return new Command(
                    Direction.from(tokens[0]),
                    Integer.parseInt(tokens[1])
            );
        }
    }

    enum Direction {
        Left,
        Right,
        Up,
        Down,
        ;

        private static final Map<String, Direction> codeToDir = Map.of(
                "L", Left,
                "R", Right,
                "U", Up,
                "D", Down
        );

        static Direction from(String code) {
            return codeToDir.get(code.trim());
        }
    }

    @SuppressWarnings("unused")
    private static final String INPUT1 = """
            R 4
            U 4
            L 3
            D 1
            R 4
            D 1
            L 5
            R 2
            """;

    @SuppressWarnings("unused")
    private static final String INPUT2 = """
            R 5
            U 8
            L 8
            D 3
            R 17
            D 10
            L 25
            U 20
            """;

    @SuppressWarnings("unused")
    private static final String INPUT = """
            U 2
            D 2
            L 2
            R 2
            D 1
            L 2
            R 1
            L 2
            U 2
            L 1
            R 1
            L 1
            R 1
            U 2
            D 2
            L 1
            U 1
            L 2
            D 1
            U 2
            D 1
            R 1
            D 1
            U 2
            D 2
            U 1
            R 2
            D 2
            R 1
            U 1
            D 1
            R 1
            L 2
            U 2
            D 2
            L 1
            R 2
            L 2
            R 1
            D 1
            R 1
            D 2
            U 2
            R 2
            D 2
            U 2
            D 1
            U 1
            R 2
            U 2
            R 2
            U 2
            D 1
            U 1
            R 2
            D 1
            U 2
            L 1
            U 2
            D 1
            R 2
            U 1
            R 2
            D 1
            L 2
            R 1
            L 1
            R 2
            U 1
            L 1
            D 2
            L 2
            D 1
            R 2
            D 1
            R 2
            U 1
            D 1
            L 1
            R 1
            L 2
            R 1
            U 1
            D 2
            L 1
            D 2
            R 1
            L 1
            R 1
            U 2
            L 2
            D 1
            U 1
            D 2
            U 2
            R 2
            U 2
            R 1
            U 2
            D 1
            R 2
            D 2
            U 1
            D 1
            R 2
            U 1
            R 2
            L 1
            R 1
            D 2
            U 1
            R 3
            D 1
            R 1
            L 3
            D 2
            R 1
            D 1
            R 3
            L 2
            D 2
            L 1
            U 3
            D 3
            L 1
            D 1
            L 3
            D 1
            U 3
            L 1
            R 2
            U 2
            D 1
            L 2
            D 3
            R 1
            U 2
            L 2
            U 1
            L 1
            U 2
            D 2
            U 1
            L 1
            D 3
            R 1
            D 3
            R 1
            D 1
            U 1
            L 2
            U 3
            D 2
            U 1
            R 3
            D 1
            U 3
            L 3
            U 1
            R 2
            L 2
            R 1
            L 1
            U 1
            L 1
            D 2
            L 2
            U 2
            L 1
            D 2
            R 1
            U 2
            L 2
            R 2
            L 1
            U 2
            L 2
            R 3
            L 3
            D 1
            R 3
            L 3
            D 2
            U 3
            L 3
            D 2
            R 2
            L 2
            D 2
            U 2
            L 3
            R 1
            U 2
            L 1
            D 3
            U 2
            D 3
            L 2
            D 3
            U 1
            D 3
            U 3
            L 3
            D 2
            L 3
            R 2
            D 3
            R 1
            D 2
            U 2
            D 1
            U 3
            D 2
            L 2
            D 1
            R 1
            L 2
            R 2
            L 3
            U 3
            L 1
            D 2
            R 1
            L 2
            D 2
            R 1
            D 1
            L 4
            D 2
            R 3
            U 4
            R 4
            U 1
            D 4
            U 4
            D 4
            U 2
            R 3
            U 3
            R 4
            U 3
            L 2
            R 1
            U 4
            D 4
            R 2
            D 4
            R 2
            U 2
            D 1
            R 1
            L 3
            R 2
            D 3
            R 3
            U 1
            D 2
            R 4
            U 1
            D 4
            R 1
            U 1
            D 2
            U 4
            D 3
            L 1
            D 4
            L 2
            D 2
            R 3
            L 1
            U 4
            L 4
            U 3
            D 3
            L 2
            D 3
            U 1
            R 4
            D 3
            L 3
            D 4
            L 1
            D 2
            U 4
            L 2
            D 1
            R 3
            D 4
            U 3
            D 4
            U 2
            L 2
            R 3
            L 4
            R 2
            L 4
            U 3
            L 4
            D 3
            U 4
            L 2
            U 3
            R 2
            D 2
            U 1
            D 2
            L 1
            R 4
            D 3
            L 1
            D 3
            U 2
            L 4
            D 4
            L 4
            R 3
            L 3
            U 2
            L 1
            R 2
            L 4
            D 2
            L 3
            U 3
            D 1
            L 2
            R 3
            U 4
            R 1
            U 3
            D 4
            L 2
            U 2
            L 2
            R 5
            D 5
            U 3
            R 4
            L 4
            U 5
            D 3
            U 2
            L 5
            U 1
            R 3
            L 3
            U 4
            L 3
            D 5
            U 2
            R 1
            U 4
            R 5
            D 2
            L 1
            U 3
            R 5
            D 2
            U 3
            D 5
            U 2
            D 2
            U 2
            L 4
            U 5
            R 4
            U 4
            L 3
            U 5
            R 4
            L 4
            D 5
            R 4
            D 4
            L 2
            R 3
            L 5
            D 3
            U 2
            D 4
            R 1
            U 5
            R 4
            D 1
            U 1
            L 1
            U 3
            D 2
            U 4
            D 3
            U 5
            R 2
            U 5
            L 1
            R 5
            U 5
            R 4
            D 3
            R 1
            D 3
            U 3
            R 1
            L 1
            D 5
            U 4
            R 3
            D 3
            L 2
            U 1
            L 2
            U 5
            D 5
            U 2
            D 4
            L 2
            D 2
            L 1
            D 4
            U 2
            D 4
            R 1
            U 1
            D 4
            L 3
            D 3
            U 2
            D 2
            L 1
            D 5
            R 1
            L 2
            D 5
            U 5
            L 3
            D 1
            R 2
            L 2
            U 1
            L 2
            D 5
            L 1
            R 2
            D 4
            U 6
            L 4
            R 5
            D 4
            U 2
            R 1
            D 4
            L 2
            R 5
            L 6
            R 2
            U 6
            R 5
            D 1
            U 1
            L 6
            R 2
            L 6
            D 3
            L 2
            R 6
            U 6
            L 1
            D 3
            R 1
            D 2
            R 4
            L 4
            U 6
            L 3
            D 1
            U 1
            L 4
            R 3
            D 3
            R 6
            D 3
            L 4
            R 5
            L 5
            D 3
            U 3
            D 6
            R 4
            D 5
            L 2
            D 1
            R 4
            L 6
            R 5
            U 4
            D 5
            L 3
            D 1
            L 6
            D 5
            L 3
            U 3
            R 4
            L 4
            D 6
            R 3
            L 1
            R 1
            U 4
            D 6
            L 3
            R 2
            D 1
            L 2
            U 3
            D 3
            L 5
            U 6
            L 5
            D 5
            R 5
            U 6
            D 2
            U 2
            R 5
            U 6
            D 2
            R 1
            L 3
            U 1
            L 6
            U 1
            R 3
            L 3
            D 4
            U 2
            R 3
            L 1
            D 3
            R 5
            L 2
            D 3
            L 2
            U 6
            R 5
            U 5
            L 1
            U 5
            L 6
            D 4
            U 2
            L 5
            U 3
            D 2
            U 4
            D 4
            U 2
            R 5
            L 4
            D 3
            R 6
            D 1
            R 4
            U 5
            L 7
            D 2
            R 6
            U 4
            L 5
            D 5
            L 2
            D 1
            R 4
            D 6
            L 5
            D 4
            R 7
            U 4
            D 7
            U 4
            R 3
            L 7
            R 7
            D 3
            R 5
            U 2
            R 3
            D 5
            R 5
            U 4
            D 2
            U 5
            R 1
            U 5
            R 5
            U 7
            R 4
            D 6
            L 1
            U 3
            D 4
            U 3
            D 7
            U 2
            L 4
            D 6
            R 7
            U 1
            L 6
            D 4
            R 6
            D 4
            U 5
            L 4
            D 6
            L 5
            D 1
            L 5
            U 3
            L 3
            U 2
            R 6
            U 3
            D 4
            U 4
            L 1
            D 3
            L 3
            R 6
            U 2
            L 3
            D 7
            L 2
            U 7
            L 4
            U 4
            D 7
            L 4
            D 3
            L 3
            D 3
            U 1
            D 5
            L 1
            R 4
            D 7
            L 7
            D 6
            R 3
            U 1
            R 6
            L 4
            D 6
            R 5
            U 7
            L 6
            R 6
            L 3
            D 4
            U 1
            R 2
            L 1
            R 1
            U 4
            D 1
            L 4
            R 2
            U 4
            R 3
            D 1
            U 3
            R 4
            L 2
            U 4
            L 3
            D 7
            R 6
            U 4
            L 4
            D 7
            R 6
            D 7
            L 5
            R 5
            D 7
            R 4
            D 1
            L 6
            U 8
            L 4
            U 2
            L 6
            R 1
            L 5
            U 5
            D 3
            L 8
            R 3
            L 3
            R 5
            U 4
            R 6
            D 4
            U 1
            L 2
            R 8
            D 7
            U 3
            D 4
            L 8
            U 6
            L 5
            R 1
            U 1
            R 4
            U 5
            D 2
            R 2
            U 2
            L 1
            U 8
            D 4
            R 7
            U 3
            D 6
            R 6
            L 3
            R 8
            D 7
            U 2
            R 5
            L 3
            U 1
            D 6
            R 1
            D 3
            L 2
            U 2
            D 8
            U 8
            L 8
            U 5
            D 6
            U 6
            L 6
            U 6
            D 5
            R 8
            L 7
            D 6
            R 1
            D 4
            L 1
            U 7
            R 3
            L 2
            D 6
            R 6
            D 7
            L 6
            U 5
            R 4
            D 7
            U 7
            D 5
            L 4
            R 6
            D 3
            L 6
            U 2
            D 7
            R 7
            U 5
            D 3
            U 2
            L 1
            D 2
            U 4
            L 1
            R 3
            L 6
            U 9
            D 8
            L 3
            R 3
            D 4
            R 3
            U 6
            L 7
            D 8
            R 9
            D 1
            L 3
            R 6
            D 4
            L 3
            U 6
            L 7
            R 7
            U 9
            L 9
            R 7
            U 8
            R 7
            U 7
            D 1
            L 9
            R 6
            U 4
            D 6
            L 8
            R 7
            D 6
            U 5
            R 6
            D 8
            L 2
            D 5
            R 9
            U 9
            L 7
            U 2
            R 7
            L 6
            U 6
            D 5
            L 8
            D 7
            L 8
            D 9
            R 7
            L 5
            D 9
            L 7
            R 2
            L 6
            R 9
            D 8
            R 8
            U 7
            R 4
            L 6
            R 1
            U 9
            D 2
            R 7
            D 5
            R 4
            D 1
            U 7
            D 5
            R 4
            U 2
            D 5
            L 9
            R 6
            U 7
            D 5
            U 4
            D 6
            L 3
            R 6
            U 3
            R 9
            L 9
            U 4
            D 1
            U 3
            R 7
            D 8
            R 9
            U 1
            R 1
            U 4
            D 7
            R 1
            L 1
            R 8
            D 1
            U 1
            L 3
            U 2
            D 6
            R 6
            L 1
            R 9
            L 2
            D 3
            U 8
            D 9
            U 1
            L 10
            U 10
            D 8
            U 9
            L 3
            R 2
            D 3
            U 4
            L 2
            R 10
            L 9
            U 5
            L 5
            D 4
            L 7
            U 1
            L 1
            R 1
            L 7
            D 7
            U 7
            L 3
            U 10
            R 7
            U 9
            L 6
            R 2
            D 9
            L 10
            D 9
            L 9
            R 5
            L 5
            R 8
            D 9
            R 1
            D 6
            L 1
            U 9
            R 3
            L 10
            D 10
            L 5
            D 10
            R 3
            U 7
            D 6
            U 7
            D 3
            R 5
            D 5
            L 6
            D 4
            U 7
            D 7
            R 9
            U 10
            R 4
            U 1
            R 1
            U 8
            R 3
            U 10
            R 6
            L 1
            U 3
            R 9
            U 2
            D 8
            R 5
            D 3
            R 3
            U 9
            R 6
            L 8
            R 2
            L 5
            R 2
            L 10
            D 2
            L 3
            U 1
            D 10
            L 5
            R 10
            D 7
            L 2
            R 7
            U 6
            D 4
            L 8
            D 8
            U 3
            R 2
            U 10
            D 8
            U 7
            D 9
            U 4
            R 9
            L 2
            R 8
            U 9
            R 10
            U 10
            D 8
            R 8
            U 2
            L 7
            U 3
            L 1
            D 7
            R 1
            U 2
            R 8
            U 4
            D 4
            L 2
            U 9
            L 7
            D 7
            L 2
            R 2
            D 9
            L 11
            U 4
            L 3
            D 7
            U 7
            R 6
            D 7
            U 11
            R 4
            L 3
            R 2
            U 9
            L 1
            U 11
            D 7
            R 10
            D 10
            U 7
            L 1
            U 8
            L 2
            D 11
            L 6
            D 1
            L 8
            D 9
            U 9
            R 5
            L 4
            D 7
            U 8
            L 5
            D 3
            R 6
            L 5
            R 7
            L 7
            D 7
            L 10
            U 4
            L 1
            U 9
            D 2
            R 2
            U 4
            D 3
            R 9
            D 11
            U 10
            L 3
            R 9
            U 6
            L 1
            R 9
            L 8
            U 2
            L 3
            U 9
            D 2
            L 4
            D 5
            U 4
            L 5
            D 11
            L 6
            U 10
            D 9
            U 3
            L 9
            U 11
            D 3
            L 7
            D 4
            L 8
            U 5
            D 10
            L 7
            U 10
            L 10
            U 4
            D 10
            L 7
            R 2
            D 3
            L 6
            R 5
            U 11
            D 4
            U 6
            D 9
            R 2
            L 9
            U 9
            R 11
            U 9
            L 10
            R 8
            L 5
            R 2
            L 12
            R 8
            U 10
            R 7
            D 7
            U 3
            L 10
            U 10
            L 11
            U 1
            R 8
            D 4
            R 3
            D 7
            U 9
            L 2
            R 9
            L 8
            R 6
            L 7
            D 11
            U 12
            D 6
            U 7
            L 11
            D 1
            U 9
            R 7
            L 3
            D 3
            U 5
            R 5
            L 3
            R 11
            L 2
            U 9
            D 9
            R 8
            U 7
            D 5
            L 9
            D 2
            L 1
            R 5
            U 9
            R 10
            U 12
            L 9
            D 12
            R 5
            U 2
            L 7
            R 6
            L 1
            U 12
            L 10
            R 11
            D 1
            L 8
            U 6
            D 10
            R 10
            L 1
            U 11
            D 2
            R 6
            U 1
            R 4
            D 1
            R 6
            U 12
            D 1
            L 1
            U 4
            D 4
            U 12
            D 12
            L 5
            R 10
            D 4
            R 1
            D 8
            U 6
            R 7
            D 12
            U 9
            D 6
            U 6
            D 1
            R 2
            L 7
            R 5
            L 4
            U 1
            L 10
            R 8
            L 12
            D 11
            U 2
            L 10
            D 12
            R 9
            D 1
            U 7
            L 5
            D 4
            U 12
            L 12
            D 11
            U 11
            L 6
            D 2
            R 11
            L 1
            U 10
            L 12
            D 11
            R 8
            D 2
            L 10
            R 4
            L 10
            D 13
            L 10
            D 5
            U 9
            L 3
            D 2
            U 3
            D 11
            L 7
            D 11
            U 4
            D 11
            U 8
            L 5
            U 6
            L 3
            U 3
            R 8
            D 10
            R 1
            D 13
            L 7
            D 7
            R 1
            D 2
            L 3
            D 5
            R 5
            L 5
            U 7
            L 4
            D 1
            L 11
            R 10
            U 8
            D 9
            U 8
            R 11
            L 1
            R 6
            D 6
            U 4
            L 2
            D 11
            U 10
            D 4
            L 11
            R 5
            U 2
            D 10
            U 12
            L 12
            D 11
            L 12
            D 10
            L 4
            U 7
            D 1
            R 4
            L 10
            D 9
            R 5
            D 3
            L 3
            D 5
            R 9
            D 10
            L 8
            R 6
            D 4
            U 5
            R 11
            U 13
            R 10
            U 11
            R 9
            U 4
            D 8
            U 4
            D 6
            R 12
            U 12
            R 9
            U 12
            L 13
            U 6
            D 10
            R 2
            L 5
            U 3
            L 8
            R 11
            D 13
            R 6
            U 13
            L 3
            U 3
            D 4
            L 11
            U 14
            D 13
            U 1
            L 8
            U 13
            R 14
            U 2
            L 13
            R 1
            U 1
            L 13
            R 4
            D 10
            R 10
            D 14
            R 7
            D 10
            L 2
            R 7
            U 10
            R 13
            D 7
            U 11
            D 13
            L 7
            U 8
            L 4
            D 7
            L 2
            R 2
            L 7
            D 13
            U 2
            R 10
            U 2
            L 6
            R 11
            D 11
            L 13
            D 13
            L 14
            R 4
            L 7
            D 7
            R 11
            U 10
            D 3
            R 4
            L 2
            R 9
            U 7
            L 1
            R 5
            L 3
            U 8
            R 6
            U 10
            L 3
            R 12
            U 13
            D 3
            R 7
            D 1
            U 7
            R 13
            D 1
            R 10
            L 8
            R 8
            L 4
            R 9
            L 5
            D 13
            L 3
            U 8
            L 13
            U 4
            R 3
            U 2
            R 7
            D 9
            R 5
            U 4
            R 5
            D 7
            L 14
            R 13
            D 3
            L 12
            D 7
            U 5
            L 8
            D 10
            U 14
            D 8
            R 12
            U 3
            L 9
            R 7
            U 9
            R 5
            L 6
            U 13
            R 6
            L 8
            R 12
            D 13
            U 6
            L 12
            U 15
            D 10
            U 15
            D 7
            R 14
            D 5
            R 9
            U 11
            L 8
            D 9
            R 3
            U 3
            R 8
            D 6
            L 3
            R 9
            U 12
            L 12
            D 3
            U 13
            L 4
            R 3
            D 6
            R 12
            D 15
            L 3
            D 9
            R 9
            D 11
            L 6
            D 6
            L 3
            U 2
            D 6
            L 4
            D 3
            U 5
            D 1
            U 11
            R 11
            U 5
            L 12
            U 14
            R 1
            D 10
            L 8
            R 12
            L 5
            U 2
            D 2
            U 8
            D 5
            U 14
            D 2
            L 6
            R 5
            D 10
            L 10
            U 2
            R 2
            U 2
            L 15
            R 14
            D 12
            L 4
            U 2
            D 10
            R 14
            U 13
            R 9
            L 4
            U 15
            D 8
            R 4
            D 9
            U 1
            R 4
            D 3
            L 3
            D 7
            L 12
            R 8
            D 7
            L 15
            R 12
            U 10
            L 11
            R 1
            U 13
            R 14
            L 14
            D 14
            U 1
            R 1
            U 13
            L 15
            U 11
            D 15
            U 1
            R 15
            L 4
            U 12
            R 8
            U 10
            R 7
            U 3
            L 14
            U 14
            R 8
            D 1
            U 9
            L 3
            U 8
            D 5
            L 12
            U 15
            D 13
            L 11
            R 4
            U 12
            R 7
            L 15
            U 8
            D 7
            L 8
            R 7
            D 1
            L 12
            U 15
            L 15
            U 9
            R 10
            D 8
            U 10
            R 15
            D 3
            R 14
            D 13
            L 3
            R 2
            U 14
            L 3
            R 7
            L 15
            D 4
            R 14
            U 11
            L 2
            U 16
            L 6
            D 4
            L 3
            U 8
            R 16
            U 8
            D 13
            L 11
            U 11
            D 11
            R 14
            D 15
            L 4
            R 13
            U 4
            D 15
            U 10
            D 4
            R 14
            D 5
            U 7
            R 1
            L 6
            U 5
            L 10
            U 15
            L 14
            D 2
            U 5
            R 8
            L 14
            U 3
            R 9
            L 14
            R 8
            D 10
            L 8
            R 9
            L 14
            D 8
            R 16
            U 2
            D 12
            L 10
            R 12
            D 3
            R 11
            U 11
            D 7
            L 10
            D 15
            U 13
            R 5
            D 8
            U 12
            L 3
            R 6
            D 10
            R 5
            U 8
            R 16
            U 16
            D 3
            R 11
            D 7
            U 4
            R 14
            L 12
            U 5
            L 1
            U 14
            L 15
            R 13
            U 16
            D 6
            U 7
            D 11
            L 11
            U 13
            R 10
            D 16
            U 14
            L 1
            D 17
            U 14
            D 2
            L 17
            U 1
            L 6
            D 17
            R 13
            L 10
            U 16
            L 3
            U 4
            D 4
            L 2
            D 10
            L 2
            R 11
            L 14
            D 6
            U 13
            R 8
            D 8
            L 4
            U 8
            D 17
            R 11
            L 7
            U 15
            L 7
            R 11
            D 13
            U 3
            R 12
            D 9
            L 11
            D 14
            U 10
            L 11
            D 13
            L 17
            R 7
            D 4
            R 16
            D 1
            U 8
            L 6
            R 9
            L 7
            D 2
            L 3
            D 13
            L 6
            U 8
            L 13
            U 6
            L 12
            U 5
            D 1
            R 10
            L 17
            D 13
            U 1
            L 9
            R 8
            D 10
            U 10
            R 6
            U 17
            R 16
            D 3
            L 4
            D 13
            L 1
            U 6
            R 15
            U 6
            D 6
            U 1
            D 12
            U 12
            R 6
            D 2
            R 13
            D 16
            L 14
            D 16
            U 12
            R 13
            U 14
            R 4
            U 3
            D 3
            R 11
            U 2
            L 17
            R 13
            D 2
            R 5
            L 5
            D 17
            R 9
            D 3
            U 16
            R 8
            L 18
            R 12
            L 8
            R 10
            L 9
            R 4
            U 1
            D 4
            U 7
            L 2
            D 9
            R 12
            D 2
            R 1
            U 11
            D 4
            L 16
            U 18
            D 3
            R 14
            L 4
            U 2
            L 16
            R 4
            L 5
            D 3
            U 17
            R 3
            U 5
            R 8
            D 4
            L 3
            D 18
            L 12
            D 17
            U 15
            L 1
            D 17
            R 11
            U 5
            R 3
            L 16
            R 6
            L 14
            R 4
            U 15
            D 7
            R 15
            L 17
            U 14
            L 9
            R 9
            L 9
            D 11
            L 10
            U 7
            L 1
            U 11
            R 4
            U 7
            L 10
            D 16
            R 3
            L 17
            R 16
            L 9
            D 4
            R 17
            L 7
            U 5
            D 16
            R 5
            D 13
            U 5
            R 8
            D 10
            R 1
            L 2
            R 10
            U 12
            D 2
            U 12
            L 10
            R 6
            L 18
            U 7
            D 9
            R 16
            L 18
            D 17
            R 14
            U 9
            R 5
            D 4
            R 18
            D 6
            R 7
            U 12
            L 7
            D 11
            U 12
            R 12
            U 4
            L 8
            U 6
            D 11
            L 8
            R 18
            U 12
            R 2
            U 13
            D 3
            R 10
            D 4
            R 10
            L 18
            D 1
            L 18
            U 2
            D 19
            U 14
            L 14
            U 3
            D 14
            R 4
            L 10
            D 13
            R 6
            D 14
            R 13
            D 13
            R 11
            U 16
            L 18
            U 3
            R 8
            U 17
            R 7
            L 12
            D 3
            L 6
            R 6
            D 18
            L 2
            D 16
            R 1
            U 16
            R 14
            L 9
            D 6
            L 5
            D 6
            U 9
            L 11
            D 18
            R 5
            L 15
            D 18
            R 18
            L 3
            R 18
            L 18
            R 2
            L 15
            R 12
            D 9
            L 3
            U 17
            R 1
            U 18
            D 16
            U 17
            L 5
            U 9
            R 4
            L 14
            D 15
            R 11
            U 18
            L 19
            U 12
            R 13
            U 16
            D 16
            L 8
            D 16
            R 4
            U 5
            D 18
            R 13
            D 13
            U 9
            R 14
            L 14
            U 17
            D 9
            L 8
            R 18
            L 12
            R 16
            L 11
            U 14
            D 2
            L 19
            U 19
            D 11
            R 11
            U 16
            D 15
            U 16
            L 18
            D 3
            L 18
            U 7
            L 14
            D 6
            U 9
            D 2
            L 19
            D 17
            """;

    @Test
    public void test() {

    }
}