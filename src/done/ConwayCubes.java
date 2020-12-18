package done;

import done.ConwayCubes.Cell.State;
import org.junit.Test;
import util.LineSupplier;
import util.Utils.IntIndex;

import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static done.ConwayCubes.Cell.State.ACTIVE;
import static done.ConwayCubes.Cell.State.EMPTY;
import static java.util.function.Predicate.not;

@SuppressWarnings("unused")
public class ConwayCubes {

    static final Map<Key, Cell> cells = new HashMap<>();
    static int minX, minY, minZ, maxX, maxY, maxZ, minW, maxW;

    static class Cell {
        final Key key;
        State state;
        int activeNeighbours;

        Cell(Key key, State state) {
            this.key = key;
            this.state = state;
        }

        @Override
        public String toString() {
            return state == ACTIVE ? "#" : ".";
        }

        public String print() {
            return (state == ACTIVE ? "#" : ".") + "[ x=" + key.x() + ", y=" + key.y() + ", z=" + key.z() + " ]";
        }

        void incNeighbours() {
//            System.out.println("--------------------------------------------------");
//            System.out.println("inc neighbours: " + print());
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    for (int dz = -1; dz <= 1; dz++) {
                        for (int dw = -1; dw <= 1; dw++) {
                            if (dx == 0 && dy == 0 && dz == 0 && dw == 0) {
                                continue;
                            }
//                        System.out.print("inc: x=" + (key.x() + dx) + ", y=" + (key.y() + dy) + ", z=" + (key.z() + dz));
                            Cell cell = getCell(key, dx, dy, dz, dw);
                            cell.inc();
//                        System.out.println(", neighbours: " + cell.activeNeighbours);
                        }
                    }
                }
            }
        }

        private void inc() {
            activeNeighbours++;
        }

        private void reset() {
            activeNeighbours = 0;
        }

        public boolean isActive() {
            return state == ACTIVE;
        }

        public void update() {
            switch (state) {
                case ACTIVE -> updateActive();
                case EMPTY -> updateEmpty();
            }
        }

        public void updateEmpty() {
            state = activeNeighbours == 3
                    ? ACTIVE
                    : EMPTY;
        }

        public void updateActive() {
            state = switch (activeNeighbours) {
                case 2, 3 -> ACTIVE;
                default -> EMPTY;
            };
        }

        enum State {EMPTY, ACTIVE}
    }

    record Key(int x, int y, int z, int w) {
        Key(int x, int y) {
            this(x, y, 0, 0);
        }
    }

    public static void main(String[] args) {
        try (Scanner in = new Scanner(INPUT2)) {
            IntIndex row = new IntIndex();
            Stream.generate(new LineSupplier(in))
                    .takeWhile(Objects::nonNull)
                    .filter(not(String::isBlank))
                    .forEach(line -> parseRow(row.getAsInt(), line));

            printCells(0);

//            cells.values().stream()
//                    .filter(Cell::isActive)
//                    .map(Cell::print)
//                    .forEach(System.out::println);
//            System.out.println("===========================================");

            IntStream.generate(new IntIndex())
                    .limit(6)
                    .forEach(gen -> advanceCells(gen + 1));

            long result = cells.values().stream()
                    .filter(Cell::isActive)
                    .count();

            System.out.println("Result: " + result);

//            cells.values().stream()
//                    .filter(Cell::isActive)
//                    .map(Cell::print)
//                    .forEach(System.out::println);
        }
    }

    private static void advanceCells(int gen) {
        new ArrayList<>(cells.values()).stream()
                .filter(Cell::isActive)
                .forEach(Cell::incNeighbours);

        cells.values().forEach(Cell::update);
        cells.values().forEach(Cell::reset);

        printCells(gen);
    }

    private static void printCells(int gen) {
        long count = cells.values().stream()
                .filter(Cell::isActive)
                .count();

        System.out.println("Generation: " + gen + ", count = " + count);

        minX = cells.keySet().stream().mapToInt(Key::x).min().orElse(0);
        maxX = cells.keySet().stream().mapToInt(Key::x).max().orElse(0);
        minY = cells.keySet().stream().mapToInt(Key::y).min().orElse(0);
        maxY = cells.keySet().stream().mapToInt(Key::y).max().orElse(0);
        minZ = cells.keySet().stream().mapToInt(Key::z).min().orElse(0);
        maxZ = cells.keySet().stream().mapToInt(Key::z).max().orElse(0);
        minW = cells.keySet().stream().mapToInt(Key::w).min().orElse(0);
        maxW = cells.keySet().stream().mapToInt(Key::w).max().orElse(0);

        for (int w = minZ; w <= maxZ; w++) {
            for (int z = minZ; z <= maxZ; z++) {
                System.out.println("w="+w+", z=" + z);
                for (int y = minY; y <= maxY; y++) {
                    for (int x = minX; x <= maxX; x++) {
                        Cell cell = cells.get(new Key(x, y, z, w));
                        String ch = cell != null
                                ? cell.toString()
                                : ".";
                        System.out.print(ch);
                    }
                    System.out.println();
                }
                System.out.println();
            }
        }
    }

    private static Cell getCell(Key key, int dx, int dy, int dz, int dw) {
        return getCell(new Key(
                key.x() + dx,
                key.y() + dy,
                key.z() + dz,
                key.w() + dw
        ));
    }

    private static Cell getCell(Key key) {
        return cells.computeIfAbsent(key, ConwayCubes::newCell);
    }

    private static Cell newCell(Key key) {
        return new Cell(key, EMPTY);
    }

    private static void parseRow(int row, String line) {
        IntIndex col = new IntIndex();
        line.chars()
                .mapToObj(ConwayCubes::toState)
                .map(state -> new Cell(newKey(col, row), state))
                .forEach(cell -> cells.put(cell.key, cell));

    }

    private static Key newKey(IntIndex col, int row) {
        return new Key(col.getAsInt(), row);
    }

    private static State toState(int ch) {
        return ch == '#'
                ? ACTIVE
                : EMPTY;
    }

    private static final String INPUT = """
            .#.
            ..#
            ###
            """;

    private static final String INPUT2 = """
            ######.#
            ##.###.#
            #.###.##
            ..#..###
            ##.#.#.#
            ##...##.
            #.#.##.#
            .###.###
            """;

    @Test
    public void test() {

    }
}