package done.advent2021;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

import static java.lang.Math.abs;
import static java.lang.System.lineSeparator;
import static java.util.Comparator.comparing;
import static java.util.function.Predicate.not;

public class Amphipod {

    private static final Pods pods = Pods.create().init(PODS_3());

    public static void main(String[] args) {
        long result = pods.findMinSolution().orElseThrow().energy();
        System.out.println("Result: " + result);
    }

    static Node home(int row, int col, Pod pod) {
        return new Node(new Position(row, col), pod, 0);
    }

    static Node transit(int col) {
        return new Node(new Position(0, col), Pod.EMPTY, 0);
    }

    record Pods(List<Node> nodes, Map<Position, Node> positions, int energy, int additionalEnergy, int level) {
        static final AtomicInteger minEnergy = new AtomicInteger(Integer.MAX_VALUE);
        static final AtomicInteger minAdditional = new AtomicInteger(Integer.MAX_VALUE);
        static final AtomicReference<Pods> best = new AtomicReference<>();

        Pods init(List<Node> nodes) {
            this.nodes.addAll(nodes);
            this.nodes.forEach(node -> positions.put(node.pos, node));
            return this;
        }

        Optional<Pods> findMinSolution() {
            System.out.println(this);

            if (homes().allMatch(Node::atHome)) {
                System.out.println("-> All home");
                return Optional.of(this);
            }

            List<Pods> moves = candidates()
                    .flatMap(this::createMoves)
                    .map(this::move)
                    .filter(p -> p.energy < minEnergy.get())
                    .filter(p -> p.additionalEnergy < minAdditional.get())
                    .sorted(comparing(Pods::energy))
                    .toList();


            System.out.println("============ level "+level+" ============");
            System.out.println("Trying " + moves.size() + " possible moves:");

            for (Pods pods : moves) {
                pods.findMinSolution()
                    .filter(p -> p.energy < minEnergy.get())
                    .filter(p -> p.additionalEnergy < minAdditional.get())
                    .ifPresent(p -> {
                        best.set(p);
                        minEnergy.updateAndGet(min -> Math.min(p.energy, min));
                        minAdditional.updateAndGet(min -> Math.min(p.additionalEnergy, min));
                });
            }

            System.out.println("Min energy: " + minEnergy + ", Min additional: " + minAdditional);
            System.out.println("============ level "+level+" ============");

            return Optional.ofNullable(best.get());
        }

        Stream<Node> transit() {
            return nodes.stream().filter(Node::isTransit);
        }

        Stream<Node> homes() {
            return nodes.stream().filter(not(Node::isTransit)).filter(Node::isHome);
        }

        Stream<Node> candidates() {
            return nodes.stream().filter(n -> n.isCandidate(this));
        }

        Pods move(Node node, Node target) {
            int delta = node.energy(target, node.pod);
            int additional = node.additional(target, node.pod);
            Pods pods = Pods.create(energy + delta, additionalEnergy + additional, level + 1);
            List<Node> newNodes = new ArrayList<>();
            newNodes.add(node.move(target));
            newNodes.add(node.clear());
            nodes.stream()
                 .filter(n -> n != node)
                 .filter(n -> n != target)
                 .forEach(newNodes::add);
            return pods.init(newNodes);
        }

        private Stream<Move> createMoves(Node node) {
            return node.targets(this).map(target -> new Move(node, target));
        }

        private Pods move(Move m) {
            return move(m.node, m.target);
        }

        private Node node(int row, int col) {
            Position pos = new Position(row, col);
            if (pos.isTransit() && (col < 0 || col > 10)) {
                throw new IllegalArgumentException("Illegal position: " + row + ", " + col);
            }
            if (!pos.isTransit()
                    && row != 1 && row != 2 && row != 3 && row != 4
                    && col != 2 && col != 4 && col != 6 && col != 8) {
                throw new IllegalArgumentException("Illegal position: " + row + ", " + col);
            }
            return positions.get(pos);
        }

        @Override
        public String toString() {
            return " --- energy " + energy + " / additional " + additionalEnergy + " ---" + lineSeparator() +
                    "#############" + lineSeparator() +
                    printLine(0, "#...........#") + lineSeparator() +
                    printLine(1, "###.#.#.#.###") + lineSeparator() +
                    printLine(2, "  #.#.#.#.#") + lineSeparator() +
                    printLine(3, "  #.#.#.#.#") + lineSeparator() +
                    printLine(4, "  #.#.#.#.#") + lineSeparator() +
                    "  #########";
        }

        private String printLine(int row, String line) {
            StringBuilder builder = new StringBuilder(line);
            nodes.stream().filter(not(Node::isEmpty))
                     .filter(n -> n.pos.row == row)
                     .forEach(n -> builder.replace(n.pos.col + 1, n.pos.col + 2, n.pod.name()));
            return builder.toString();
        }

        static Pods create() {
            return create(0, 0, 0);
        }

        static Pods create(int energy, int additional, int level) {
            return new Pods(new ArrayList<>(), new HashMap<>(), energy, additional, level);
        }
    }

    record Move(Node node, Node target) {}

    record Position(int row, int col) {
        int distance(Position target) {
            return row + target.row + abs(col - target.col);
        }

        int additional(Position target, Pod pod) {
            if (target.col < pod.col && pod.col < col
                    || target.col > pod.col && pod.col > col) {
                return abs(pod.col - target.col) * 2;
            }
            if (target.col < col && col < pod.col
                    || target.col > col && col > pod.col) {
                return abs(col - target.col) * 2;
            }
            return 0;
        }

        boolean isTransit() {
            return row == 0
                    && col != Pod.A.col
                    && col != Pod.B.col
                    && col != Pod.C.col
                    && col != Pod.D.col;
        }

        boolean isHome() {
            return row > 0;
        }
    }

    record Node(Position pos, Pod pod, int moved) {
        private static final Comparator<Node> COMPARE_BY_ROW_DESC =
                Comparator.<Node>comparingInt(n -> n.pos.row).reversed();

        int energy(Node target, Pod pod) {
            return pod.multiplier * distance(target);
        }

        int additional(Node target, Pod pod) {
            if (target.isTransit()) {
                return pos.additional(target.pos, pod) * pod.multiplier;
            }
            return 0;
        }

        int distance(Node target) {
            return pos.distance(target.pos);
        }

        boolean isEmpty() {
            return pod == Pod.EMPTY;
        }

        boolean isTransit() {
            return pos.isTransit();
        }

        boolean isHome() {
            return pos.isHome();
        }

        boolean atHome() {
            return pod.atHome(pos);
        }

        boolean isFrozen(Pods pods) {
            return atHome() && switch(pos.row) {
                case 1 -> pods.node(2, pos.col).atHome()
                        && pods.node(3, pos.col).atHome()
                        && pods.node(4, pos.col).atHome();
                case 2 -> pods.node(3, pos.col).atHome()
                        && pods.node(4, pos.col).atHome();
                case 3 -> pods.node(4, pos.col).atHome();
                case 4 -> true;
                default -> false;
            };
        }

        boolean isCandidate(Pods pods) {
            return !isEmpty() && !isFrozen(pods);
        }

        Node move(Node target) {
            return new Node(target.pos, pod, moved + 1);
        }

        Node clear() {
            return new Node(pos, Pod.EMPTY, 0);
        }

        Stream<Node> targets(Pods pods) {
            if (moved >= 1) {
                return homes(pods);
            }
            return Stream.concat(
                    homes(pods),
                    pods.transit()
                        .filter(Node::isEmpty)
                        .filter(n -> canMove(pods, n))
            );
        }

        private Stream<Node> homes(Pods pods) {
            return pods.homes()
                       .filter(Node::isEmpty)
                       .filter(n -> pod.atHome(n.pos))
                       .filter(n -> canMove(pods, n))
                       .sorted(Node.COMPARE_BY_ROW_DESC)
                       .limit(1);
        }

        private boolean canMove(Pods pods, Node target) {
            if (target.pos.col < pod.minCol || target.pos.col > pod.maxCol) {
                return false;
            }
            int minCol = Math.min(pos.col, target.pos.col);
            int maxCol = Math.max(pos.col, target.pos.col);
            boolean transitIsEmpty = pods.transit()
                                         .filter(n -> n.pos.col > minCol)
                                         .filter(n -> n.pos.col < maxCol)
                                         .allMatch(Node::isEmpty);
            boolean startColNotBlocked = switch(pos.row) {
                case 0,1 -> true;
                case 2 -> pods.node(1, pos.col).isEmpty();
                case 3 -> pods.node(1, pos.col).isEmpty()
                        && pods.node(2, pos.col).isEmpty();
                case 4 -> pods.node(1, pos.col).isEmpty()
                        && pods.node(2, pos.col).isEmpty()
                        && pods.node(3, pos.col).isEmpty();
                default -> false;
            };
            boolean targetColNotBlocked = switch(target.pos.row) {
                case 0 -> target.isEmpty();
                case 1 -> target.isEmpty()
                        && pods.node(2, target.pos.col).atHome()
                        && pods.node(3, target.pos.col).atHome()
                        && pods.node(4, target.pos.col).atHome();
                case 2 -> target.isEmpty()
                        && pods.node(1, target.pos.col).isEmpty()
                        && pods.node(3, target.pos.col).atHome()
                        && pods.node(4, target.pos.col).atHome();
                case 3 -> target.isEmpty()
                        && pods.node(1, target.pos.col).isEmpty()
                        && pods.node(2, target.pos.col).isEmpty()
                        && pods.node(4, target.pos.col).atHome();
                case 4 -> target.isEmpty()
                        && pods.node(1, target.pos.col).isEmpty()
                        && pods.node(2, target.pos.col).isEmpty()
                        && pods.node(3, target.pos.col).isEmpty();
                default -> false;
            };
            return transitIsEmpty && startColNotBlocked && targetColNotBlocked;
        }
    }

    enum Pod {
        EMPTY,
        A(1, 2),
        B(10, 4),
        C(100, 6, 1, 9),
        D(1000, 8, 2, 8),
        ;

        private final int multiplier;
        private final int col;
        private final int minCol;
        private final int maxCol;

        Pod() {
            this(0, -1);
        }

        Pod(int multiplier, int col) {
            this(multiplier, col, 0, 10);
        }

        Pod(int multiplier, int col, int minCol, int maxCol) {
            this.multiplier = multiplier;
            this.col = col;
            this.minCol = minCol;
            this.maxCol = maxCol;
        }

        public boolean atHome(Position pos) {
            return this != EMPTY && pos.row != 0 && pos.col == col;
        }
    }

    @SuppressWarnings("unused")
    static List<Node> PODS_1() {
        return List.of(
                transit(0),
                transit(1),
                transit(2),
                transit(3),
                transit(4),
                transit(5),
                transit(6),
                transit(7),
                transit(8),
                transit(9),
                transit(10),

                home(1, 2, Pod.B),
                home(2, 2, Pod.A),

                home(1, 4, Pod.C),
                home(2, 4, Pod.D),

                home(1, 6, Pod.B),
                home(2, 6, Pod.C),

                home(1, 8, Pod.D),
                home(2, 8, Pod.A)
        );
    }

    @SuppressWarnings("unused")
    static List<Node> PODS_2() {
        return List.of(
                transit(0),
                transit(1),
                transit(2),
                transit(3),
                transit(4),
                transit(5),
                transit(6),
                transit(7),
                transit(8),
                transit(9),
                transit(10),

                home(1, 2, Pod.C),
                home(2, 2, Pod.D),

                home(1, 4, Pod.A),
                home(2, 4, Pod.D),

                home(1, 6, Pod.B),
                home(2, 6, Pod.B),

                home(1, 8, Pod.C),
                home(2, 8, Pod.A)
        );
    }

    @SuppressWarnings("unused")
    static List<Node> PODS_3() {
        return List.of(
                transit(0),
                transit(1),
                transit(2),
                transit(3),
                transit(4),
                transit(5),
                transit(6),
                transit(7),
                transit(8),
                transit(9),
                transit(10),

                home(1, 2, Pod.C),
                home(2, 2, Pod.D),
                home(3, 2, Pod.D),
                home(4, 2, Pod.D),

                home(1, 4, Pod.A),
                home(2, 4, Pod.C),
                home(3, 4, Pod.B),
                home(4, 4, Pod.D),

                home(1, 6, Pod.B),
                home(2, 6, Pod.B),
                home(3, 6, Pod.A),
                home(4, 6, Pod.B),

                home(1, 8, Pod.C),
                home(2, 8, Pod.A),
                home(3, 8, Pod.C),
                home(4, 8, Pod.A)
        );
    }
}