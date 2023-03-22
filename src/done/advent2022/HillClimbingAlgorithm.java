package done.advent2022;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Stream;

import static done.advent2022.HillClimbingAlgorithm.Cells.columnCount;
import static done.advent2022.HillClimbingAlgorithm.Cells.rowCount;
import static java.util.function.Function.identity;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toSet;
import static util.LineSupplier.lines;

public class HillClimbingAlgorithm {

    static boolean debugEnabled = false;
    static HeightMap heightMap;

    public static void main(String[] args) {
        try (Scanner in = new Scanner(INPUT)) {
            heightMap = new HeightMap(createCells(in));

            List<Cell> route = heightMap.findRoute();
            debug(() -> route.forEach(System.out::println));

            System.out.println("Result: " + route.size());
        }
    }

    static void debug(Runnable log) {
        if (debugEnabled) {
            log.run();
        }
    }

    private static Cell[][] createCells(Scanner in) {
        return lines(in)
                .map(Cells::createRow)
                .toArray(Cell[][]::new);
    }

    static class HeightMap {
        private final Cell[][] cells;
        private final Set<Cell> visited = new HashSet<>();

        HeightMap(Cell[][] cells) {
            this.cells = cells;
        }

        List<Cell> findRoute() {
            Collection<Visit> current = higingStart();
            Visit end = null;
            while (end == null) {
                current = findNeighbours(current);
                if (current.isEmpty()) {
                    throw new IllegalStateException();
                }
                end = findEnd(current);
            }
            return route(end);
        }

        private static Visit findEnd(Collection<Visit> current) {
            return current.stream()
                          .filter(Visit::isEnd)
                          .findAny()
                          .orElse(null);
        }

        private static List<Cell> route(Visit end) {
            List<Cell> route = new ArrayList<>();
            while (end.prev != null) {
                route.add(end.cell);
                end = end.prev;
            }
            Collections.reverse(route);
            return route;
        }

        private Collection<Visit> findNeighbours(Collection<Visit> cells) {
            cells.stream().map(Visit::cell).forEach(visited::add);
            return cells.stream()
                        .flatMap(this::neighbours)
                        .collect(toSet());
        }

        private Stream<Visit> neighbours(Visit current) {
            return Stream.of(up(current), down(current), left(current), right(current))
                         .flatMap(identity())
                         .filter(not(visited::contains))
                         .filter(current::canGo)
                         .map(current::visit);
        }

        @SuppressWarnings("unused")
        private Collection<Visit> start() {
            return Stream.of(Cells.start).map(Visit::start).toList();
        }

        private Collection<Visit> higingStart() {
            return Stream.of(cells)
                         .flatMap(Stream::of)
                         .filter(Cell::isHikingStart)
                         .map(Visit::start)
                         .toList();
        }

        private Stream<Cell> up(Visit visit) {
            return up(visit.cell());
        }

        private Stream<Cell> up(Cell cell) {
            return cell(cell.x, cell.y - 1);
        }

        private Stream<Cell> down(Visit visit) {
            return down(visit.cell());
        }

        private Stream<Cell> down(Cell cell) {
            return cell(cell.x, cell.y + 1);
        }

        private Stream<Cell> left(Visit visit) {
            return left(visit.cell());
        }

        private Stream<Cell> left(Cell cell) {
            return cell(cell.x - 1, cell.y);
        }

        private Stream<Cell> right(Visit visit) {
            return right(visit.cell());
        }

        private Stream<Cell> right(Cell cell) {
            return cell(cell.x + 1, cell.y);
        }

        private Stream<Cell> cell(int x, int y) {
            if (x >= 0 && x < columnCount() && y >= 0 && y < rowCount()) {
                return Stream.of(cells[y][x]);
            }
            return Stream.empty();
        }
    }

    record Visit(Cell cell, Visit prev) {
        Visit visit(Cell cell) {
            return new Visit(cell, this);
        }

        boolean canGo(Cell target) {
            return cell.elevation().canClimb(target.elevation());
        }

        boolean isEnd() {
            return cell.isEnd();
        }

        static Visit start(Cell start) {
            return new Visit(start, null);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Visit visit = (Visit) o;
            return cell.equals(visit.cell);
        }

        @Override
        public int hashCode() {
            return Objects.hash(cell);
        }
    }

    record Cell(int x, int y, Elevation elevation) {
        boolean isEnd() {
            return elevation.isEnd();
        }

        boolean isHikingStart() {
            return elevation.height() == Elevation.START_HEIGHT;
        }
    }

    record Cells() {
        static int currentX = -1;
        static int currentY = -1;
        static Cell start;
        static Cell end;

        static int rowCount() {
            return currentY + 1;
        }

        static int columnCount() {
            return currentX + 1;
        }

        static Cell[] createRow(String line) {
            resetColumn();
            nextRow();
            return line.chars()
                       .mapToObj(Cells::createCell)
                       .toArray(Cell[]::new);
        }

        private static void nextRow() {
            currentY++;
        }

        private static void resetColumn() {
            currentX = -1;
        }

        static Cell createCell(int ch) {
            nextColumn();
            Elevation elevation = height(ch);
            Cell cell = cell(elevation);
            if (elevation.isStart()) {
                start = cell;
            } else if (elevation.isEnd()) {
                end = cell;
            }
            return cell;
        }

        private static Cell cell(Elevation elevation) {
            return new Cell(currentX, currentY, elevation);
        }

        private static Elevation height(int ch) {
            return new Elevation((char) ch);
        }

        private static void nextColumn() {
            currentX++;
        }
    }

    record Elevation(char name) {
        private static final char START_ELEVATION = 'S';
        private static final char END_ELEVATION = 'E';
        private static final char TOP_ELEVATION = 'z';
        private static final char BOTTOM_ELEVATION = 'a';

        private static final int START_HEIGHT = 0;
        private static final int END_HEIGHT = TOP_ELEVATION - BOTTOM_ELEVATION;

        boolean isStart() {
            return name == START_ELEVATION;
        }

        boolean isEnd() {
            return name == END_ELEVATION;
        }

        int height() {
            if (isStart()) {
                return START_HEIGHT;
            } else if (isEnd()) {
                return END_HEIGHT;
            }
            return name - BOTTOM_ELEVATION;
        }

        boolean canClimb(Elevation target) {
            int h = height();
            int th = target.height();
            boolean canClimb = th <= h + 1;
            debug(() -> System.out.printf("'%s'(%d) -> '%s'(%d) : canClimb = %b%n",
                                          name, h, target.name, th, canClimb)
            );
            return canClimb;
        }
    }

    @SuppressWarnings("unused")
    private static final String INPUT1 = """
            Sabqponm
            abcryxxl
            accszExk
            acctuvwj
            abdefghi
            """;

    @SuppressWarnings("unused")
    private static final String INPUT = """
            abcccccccccccccccccccccccccccccccccccccaaaaaaacccccccaaaaaaaaaaaccccccccccccccccccccaaacaaaaaaaacccccccccccccccccccccccccccccccccccaaaaa
            abccccccccccccccccccaaccaacccccccccccccaaaaaaaccccccccaaaaaaaaaaacccccccaaaaccccccccaaaaaaaaaaaaacccccccccccccccccccccccccccccccccaaaaaa
            abccccccccccccccccccaaaaaaccccccccccaaaccaaaaaacccccccaaaaaaaaaaccccccccaaaaccccccaaaaaaaaaaaaaaacccccccccccccccccccaaacccccccccccaaaaaa
            abcccccccccccccccccccaaaaacccccccccccaaccaacaaaccccccaaaaaaaaaaaccccccccaaaacccccaaaaaaaaacaaaaaaacccccccccccccccccaaaacccccccccccaaacaa
            abccccccccccccccccccaaaaaaccccccccaacaaaaaacccccccccaaaaaaaaaaaaacaaaccccaaccccccaaaaaaaaacaacccccccccccccccccaaaccaaaacccccccccccccccaa
            abcccccccccccccccccaaaaaaaacccccccaaaaaaaaccccccaaaaaaaacaaaacaaaaaaacccccccccaaccccaaaaaacaaacccccccccccccccaaaakkkaaccccccccccccccccaa
            abcccccccccccccccccaaaaaaaaccccccccaaaaaccccaacccaaaaaaaaaaaacaaaaaaccccccccccaacccaaaaaaaaaaaacccccccccccccccakkkkkklcccccccccccccccccc
            abaaacccccccccccaaccccaaccccccccccccaaaaaccaaacccaaaaaaaaaaaaaaaaaaaaccccccaaaaaaaacaacccaaaaaaccccccccccccccckkkkkkkllcccccccaaaccccccc
            abaaaacccccccaacaaccccaacccccccccccaaacaaaaaaaccccaaaaaaaaaaaaaaaaaaaacccccaaaaaaaaaaaccccaaaaacccccccccccccckkkksssllllccccccaaaaaacccc
            abaaaacccccccaaaaacccccccccccaaaccccaacaaaaaaccccaaaaaacaaaaaaaaaaaaaacccccccaaaaccccccccaaaaacccccccccccccckkkksssssllllcccccaaaaaacccc
            abaaacccccccccaaaaaaccccccccaaaaccccccccaaaaaaaacaaaaaaaaaaaaacaaacaaacccccccaaaaacccccccaaaaacccccccccccccjkkkrssssssllllccccccaaaccccc
            abccccccccccaaaaaaaaccccccccaaaacccccccaaaaaaaaacaacaaaaaaaaaacaaaccccccccccaaacaaccccccccccccccccccccccccjjkkrrsuuussslllllcccccaaccccc
            abccaaacccccaaaaacccccccccccaaaaccccccaaaaaaaaaacccccaaaaaaaaaacaaccccccccccaacccacccccccccccccccccccccjjjjjjrrrsuuuussslllllmcccddacccc
            abcccaaaccaccacaaaccccccccccccccccccccaaaaaaaccccccccccaaaaaaaaccccccaacccccccccccaaaaacccccccccccccccjjjjjjrrrruuuuuusssllmmmmmddddcccc
            abccaaaaaaaacccaaaccccccccccccccccaaacccccaaaccccccccccccaaacccccccccaacccccccccccaaaaacccccccccccccjjjjjrrrrrruuuxuuussqqqqmmmmmdddcccc
            abcaaaaaaaacccaaaaaacaaaaaccccccaaaaaaccccaaacccaaccccccccaaccccccaaaaaaaaccaaacccaaaaaaccccccccccccjjjjrrrrrruuuxxxuuuqqqqqqqmmmdddcccc
            abaaaaaaaaaccccaaaaacaaaaaccccccaaaaaaaaccccccaaaaaaccccccccccccccaaaaaaaaccaaacaaaaaaaacccccccccccjjjjrrrtttuuuuxxxyvvvvvqqqqmmmdddcccc
            abaaaaaaaaaccaaaaaaacaaaaaaccccccaaaaaaaacccccaaaaaaccccccccccccccccaaaaccaaaaaaaaaaaaaacccccccccaaiijqqqrttttuuuxxyyvvvvvvvqqmmmdddcccc
            abcaaaaaaaaccaaaaaaaaaaaaaacccccaaaaaaaacccccccaaaacccccaaaaccccccccaaaaacaaaaaaaaccaaccccccccccaaaiiiqqqttttxxxxxxyyyyyyvvvqqmmmdddcccc
            abcccaaaaaaacaaaaaaaaaaaaaacccccaaaaaaaaaaaccccaaaaccccaaaaacccccccaaaaaacaaaaaaacccccccccccccccaaaiiiqqqtttxxxxxxxyyyyyyvvqqqmmmdddcccc
            SbcccaacccaccccaaacacccaaacccccccccaaaaaaaaacccaccaccccaaaaaaccccccaaccaacccaaaaaccccccccccccccccaaiiiiqqtttxxxxEzzzyyyyvvvqqqmmmddccccc
            abccaaaccccccccaaccccccccccccccccccaaaaaaaaccccccccccccaaaaaaccccccccccccccaaacaaaccaacccccccccccccciiiqqqttttxxxyyyyyvvvvqqqmmmdddccccc
            abccccccccccccccccccccccccccccccccaaaaaaaccccccccccccccaaaaaacccccccccccccccaacccccaaaaaaaccccccccccciiiqqqttttxxyyyyyvvvrrrnnneeecccccc
            abcaaaaccccccccccccccccccccccccccaaaaaaaaccccccccccccccccaacccccccccccccccccccccccccaaaaacccccccccccciiiqqqqttxxyyyyyyyvvrrnnnneeecccccc
            abcaaaaacccccccccccccccccccccccccaaaacaaacccaccaaacccccccccccccccccccccccccaaaccccaaaaaaaccccccccccccciiiqqqttwwyywwyyywwrrnnneeeccccccc
            abaaaaaacccaccaccccccccccccccccccaaaaccaacccaaaaaaccccccccccccccccaaaccccaaaaaacccaaaaaaaacccccccccccciiiqqqtswwwwwwwwwwwrrnnneeeccccccc
            abaaaaaacccaaaaccccccccaaaacccccccaaacccccccaaaaaacccccccccccccccaaaaaaccaaaaaacccaaaaaaaacaaccccccaaciiiqppsswwwwsswwwwwrrrnneeeccccccc
            abcaaaaacccaaaaacccccccaaaacccccccccccccccccaaaaaaaccccccccccccccaaaaaaccaaaaaacccccaaaaaaaaaccccccaaaahhpppssswwsssswwwwrrrnneeeacccccc
            abcaaaccccaaaaaacccccccaaaaccccccccccccccccaaaaaaaaccccccccccccccaaaaacccaaaaaccccccaacaaaaaaaaccaaaaaahhpppsssssssssrrrrrrnnneeeacccccc
            abccccccccaaaaaaccccccccaacccccccccccccccccaaaaaaaaccccaacccccccccaaaaaccaaaaacccccccccaaaaaaaaccaaaaachhpppssssssoosrrrrrrnnneeeaaacccc
            abccccccccccaaccccccccccccccccaaaaaccccccaacccaaacccaaaaacccccccccaacaacccccccccccccccccaaaaaaacccaaaaahhhppppssppooooorroonnffeaaaacccc
            abaaccccccccccccccccccccccccccaaaaaccccccaacccaaaccccaaaaacccccccccccccccccccccccccccaacaaaaacccccaacaahhhppppppppoooooooooonfffaaaacccc
            abaccccccccccccccccccccccccccaaaaaacccaaaaaaaacccccccaaaaaccccccccccccccccccccccccaaaaaaaaaaaccccccccccchhhpppppppgggoooooooffffaacccccc
            abaccccccccccccccccccccccccccaaaaaacccaaaaaaaaccccccaaaaaccccccacccaacccccccccccccaaaaaccccaaccccccccccchhhhhhggggggggfffffffffaaacccccc
            abaacccccccccccccccccccccccccaaaaaacccccaaaacccccccccaaaacccaacaacaaacccccccccccccaaaaaaacccccccccccccccchhhhgggggggggffffffffccaacccccc
            abcccccccaacccccccccccccccccccaaaccccccaaaaaccccccccaaaaccaaaacaaaaacccccccccccccaaaaaaaaccccccccccccccccchhhggggaaaagffffffcccccccccccc
            abcccccccaacccccccccccccaacccccccccccccaaaaaaccaaccccaaaaaaaaacaaaaaacccccccaaaacaaaaaaaacccccccccccaacccccccaaaacaaaacccccccccccccccccc
            abccccaaaaaaaacccccccaacaaaccccccccccccaaccaacaaaacccaaaaaaaacaaaaaaaaccccccaaaaccacaaaccaaaccccaaaaaacccccccaacccaaaacccccccccccccaaaaa
            abccccaaaaaaaacccccccaaaaaccccccccccccccccccccaaaaccccaaaaaaacaaaaaaaaccccccaaaaccccaaaccaaaaaccaaaaaaaacccccccccccaaaccccccccccccccaaaa
            abccccccaaaaccccccccccaaaaaaccccccccccccccccccaaaacccaaaaaaaaaaccaaccccccccccaacccccccccaaaaacccaaaaaaaacccccccccccaaaccccccccccccccaaaa
            abcccccaaaaaacccccccaaaaaaaacccccccccccccccccccccccaaaaaaaaaaaaaaaacccccccccccccccccccccaaaaaacccaaaaaaaccccccccccccccccccccccccccaaaaaa
            """;

    @Test
    public void test() {

    }
}