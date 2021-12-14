package done.advent2021;

import util.LineSupplier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Stream;

public class PassagePathing {

    public static void main(String[] args) {
        try (Scanner in = new Scanner(FINAL_INPUT)) {
            Stream.generate(new LineSupplier(in))
                    .takeWhile(Objects::nonNull)
                    .map(NodePair::from)
                    .forEach(NodePair::init);

            long result = 0L;

            int count = 100;
            List<Path> paths = Path.start();
            while (paths.stream().anyMatch(Path::canAdvance)) {
                result += paths.stream().filter(Path::terminal).count();

                paths = paths.stream()
                             .filter(Path::canAdvance)
                             .flatMap(Path::advance)
                             .toList();

                System.out.println("Paths: " + paths);
                if (count-- == 0) {
                    break;
                }
            }

            result += paths.stream().filter(Path::terminal).count();

            System.out.println("Result: " + result);
        }
    }

    record NodePair(Node start, Node end) {
        void init() {
            start.add(end);
            end.add(start);
        }

        static NodePair from(String line) {
            String[] parts = line.trim().split("[-]");
            return new NodePair(Node.of(parts[0]), Node.of(parts[1]));
        }
    }

    record Path(Set<Node> visited, Node last, Node twice) {
        Path {
            System.out.println("\t" + last.name());
        }
        /**
         * Returns all possible paths based on this path
         */
        Stream<Path> advance() {
            System.out.println("Advance from " + last.name() + " -> ");
            return last.adjacent()
                       .filter(this::notVisited)
                       .map(this::create);
        }

        boolean terminal() {
            return last == Node.END;
        }

        boolean notVisited(Node node) {
            return (twice == null && node != Node.START)
                    || !visited.contains(node) && node != twice;
        }

        boolean canAdvance() {
            return !terminal() && last.adjacent().anyMatch(this::notVisited);
        }

        static List<Path> start() {
            return List.of(new Path(Set.of(Node.START), Node.START, null));
        }

        Path create(Node node) {
            if (this.visited.contains(node) && node != Node.START && node != Node.END) {
                if (twice != null) {
                    throw new IllegalStateException("Already contains twice node: " + twice);
                }
                return new Path(visited, node, node);
            }
            Set<Node> visited = new HashSet<>(this.visited);
            node.visited().forEach(visited::add);
            return new Path(visited, node, twice);
        }

        @Override
        public String toString() {
            return last.name();
        }
    }

    interface Node {
        Node END = Stream::empty;
        Node START = new Start();

        Map<String,Node> nodes = new HashMap<>();

        /**
         * Returns adjacent nodes.
         */
        Stream<Node> adjacent();

        /**
         * Add adjacent node.
         */
        default void add(Node node) {}

        /**
         * Returns true if node was visited.
         */
        default Stream<Node> visited() { return Stream.of(this); }

        default String name() { return "end"; }

        static Node of(String name) {
            return switch (name) {
                case "start" -> START;
                case "end" -> END;
                default -> nodes.computeIfAbsent(name, n -> isUpperCase(name) ? new BigNode(name) : new SmallNode(name));
            };
        }

        private static boolean isUpperCase(String name) {
            return Character.isUpperCase(name.charAt(0));
        }

        class Start extends AbstractNode {
            public Start() {
                super("start");
            }
        }

        class BigNode extends AbstractNode {
            public BigNode(String name) {
                super(name);
            }

            @Override
            public Stream<Node> visited() { return Stream.empty(); }
        }

        class SmallNode extends AbstractNode {
            public SmallNode(String name) {
                super(name);
            }
        }

        class AbstractNode implements Node {
            protected final String name;
            protected final List<Node> nodes = new ArrayList<>();

            public AbstractNode(String name) {
                this.name = name;
            }

            @Override
            public Stream<Node> adjacent() {
                return nodes.stream();
            }

            @Override
            public void add(Node node) {
                nodes.add(node);
            }

            @Override
            public String name() { return name; }
        }
    }

    private static final String INPUT1 = """
            start-A
            start-b
            A-c
            A-b
            b-d
            A-end
            b-end""";

    private static final String INPUT2 = """
            dc-end
            HN-start
            start-kj
            dc-start
            dc-HN
            LN-dc
            HN-end
            kj-sa
            kj-HN
            kj-dc""";

    private static final String INPUT3 = """
            fs-end
            he-DX
            fs-he
            start-DX
            pj-DX
            end-zg
            zg-sl
            zg-pj
            pj-he
            RW-he
            fs-DX
            pj-RW
            zg-RW
            start-pj
            he-WI
            zg-he
            pj-fs
            start-RW""";

    private static final String FINAL_INPUT = """
            xx-end
            EG-xx
            iy-FP
            iy-qc
            AB-end
            yi-KG
            KG-xx
            start-LS
            qe-FP
            qc-AB
            yi-start
            AB-iy
            FP-start
            iy-LS
            yi-LS
            xx-AB
            end-KG
            iy-KG
            qc-KG
            FP-xx
            LS-qc
            FP-yi
            """;
}