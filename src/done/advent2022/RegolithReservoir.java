package done.advent2022;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;
import static util.LineSupplier.lines;

public class RegolithReservoir {

    static final Reservoir reservoir = new Reservoir();

    public static void main(String[] args) {
        try (Scanner in = new Scanner(INPUT)) {
            lines(in)
                    .map(Coordinate::parseCoordinates)
                    .forEach(reservoir::addCoordinates);

            System.out.println(reservoir);

            reservoir.addInfiniteWall();
            reservoir.simulateSand();

            long result = reservoir.sandInRest();

            System.out.println("Result: " + result);
        }
    }

    static class Reservoir {
        private static final int SIZE = 202;
        private static final int OFFSET_X = 300;


        private final Place[][] places = new Place[SIZE][];

        private int sandInRest = 0;
        private int rockBottom = 0;
        private int minX = 500;
        private int maxX = 500;
        private boolean hasInfiniteWall = false;

        Reservoir() {
            for (int y = 0; y < SIZE; y++) {
                places[y] = new Place[2*SIZE];
            }
        }

        void addCoordinates(Coordinate[] coordinates) {
            toLines(coordinates)
                    .stream()
                    .map(CoordLine::normalize)
                    .flatMap(CoordLine::coords)
                    .forEach(this::addRock);
        }

        void simulateSand() {
            int lastInRest;
            do {
                lastInRest = sandInRest;
                addSandGrain();
            } while (sandInRest != lastInRest);
        }

        int sandInRest() {
            return sandInRest;
        }

        private void addSandGrain() {
            Place grain;
            Place fallen = Place.grainOfSand;
            do {
                grain = fallen;
                if (isBelowRocks(grain)) {
                    return;
                }
                fallen = fall(grain);
            } while (fallen != grain);
            addInRest(grain);
        }

        private boolean isBelowRocks(Place fallen) {
            return !hasInfiniteWall && fallen.coord.y > rockBottom;
        }

        private Place fall(Place grain) {
            Place below = below(grain.coord);
            if (below.isFree()) {
                return Place.sand(below.coord);
            }
            Place left = left(grain.coord);
            if (left.isFree()) {
                return Place.sand(left.coord);
            }
            Place right = right(grain.coord);
            if (right.isFree()) {
                return Place.sand(right.coord);
            }
            return grain;
        }

        private void addRock(Coordinate coord) {
            places[coord.y][coord.x - OFFSET_X] = Place.rock(coord);
            rockBottom = Math.max(rockBottom, coord.y);
            minX = Math.min(minX, coord.x);
            maxX = Math.max(maxX, coord.x);
        }

        private void addInRest(Place grain) {
            if (place(grain.coord).isFree()) {
                places[grain.coord.y][grain.coord.x - OFFSET_X] = grain;
                sandInRest++;
                minX = Math.min(minX, grain.coord.x);
                maxX = Math.max(maxX, grain.coord.x);
            } else {
                System.out.println("Not free: " + grain.coord);
            }
        }

        private Place below(Coordinate coord) {
            return place(coord.below());
        }

        private Place left(Coordinate coord) {
            return place(coord.left());
        }

        private Place right(Coordinate coord) {
            return place(coord.right());
        }

        private Place place(Coordinate coord) {
            Place place = place(coord.x, coord.y);
            if (place != null) {
                return place;
            }
            if (hasInfiniteWall && coord.y == rockBottom + 2) {
                return Place.rock(coord);
            }
            return Place.air(coord);
        }

        private Place place(int x, int y) {
            if (x < OFFSET_X || x >= OFFSET_X + (SIZE * 2) || y < 0 || y >= SIZE) {
                return null;
            }
            return places[y][x - OFFSET_X];
        }

        private List<CoordLine> toLines(Coordinate[] coordinates) {
            Coordinate last = null;
            List<CoordLine> lines = new ArrayList<>();
            for (Coordinate coord : coordinates) {
                if (last != null) {
                    lines.add(new CoordLine(last, coord));
                }
                last = coord;
            }
            return lines;
        }

        @Override
        public String toString() {
            return IntStream.rangeClosed(0, rockBottom + 2)
                            .mapToObj(this::printLine)
                            .collect(joining("\n"));
        }

        private String printLine(int y) {
            return IntStream.rangeClosed(minX, maxX)
                            .mapToObj(x -> new Coordinate(x, y))
                            .map(this::place)
                            .map(Place::print)
                            .collect(joining());
        }

        void addInfiniteWall() {
            hasInfiniteWall = true;
        }
    }

    record Place(Coordinate coord, State state) {
        static final Place grainOfSand = new Place(new Coordinate(500, 0), State.SAND);

        boolean isFree() {
            return state == State.AIR;
        }

        String print() {
            return switch (state) {
                case AIR -> ".";
                case ROCK -> "#";
                case SAND -> "o";
            };
        }

        static Place rock(Coordinate coord) {
            return new Place(coord, State.ROCK);
        }

        static Place sand(Coordinate coord) {
            return new Place(coord, State.SAND);
        }

        static Place air(Coordinate coord) {
            return new Place(coord, State.AIR);
        }

        enum State {
            ROCK,
            AIR,
            SAND,
        }
    }

    record CoordLine(Coordinate start, Coordinate end) {
        Stream<Coordinate> coords() {
            if (isVertical()) {
                return verticalLine();
            }
            return horizontalLine();
        }

        CoordLine normalize() {
            if (start.x <= end.x && start.y <= end.y) {
                return this;
            }
            return new CoordLine(end, start);
        }

        private Stream<Coordinate> verticalLine() {
            return IntStream.rangeClosed(start.y, end.y).mapToObj(y -> new Coordinate(start.x, y));
        }

        private Stream<Coordinate> horizontalLine() {
            return IntStream.rangeClosed(start.x, end.x).mapToObj(x -> new Coordinate(x, start.y));
        }

        private boolean isVertical() {
            return start.x == end.x;
        }
    }

    record Coordinate(int x, int y) {
        Coordinate below() {
            return new Coordinate(x, y + 1);
        }

        Coordinate left() {
            return new Coordinate(x - 1, y + 1);
        }

        Coordinate right() {
            return new Coordinate(x + 1, y + 1);
        }

        static Coordinate create(String input) {
            String[] tokens = input.split(",");
            return new Coordinate(
                Integer.parseInt(tokens[0].trim()),
                Integer.parseInt(tokens[1].trim())
            );
        }

        static Coordinate[] parseCoordinates(String line) {
            return Stream.of(line.split("->"))
                         .map(Coordinate::create)
                         .toArray(Coordinate[]::new);
        }
    }

    @SuppressWarnings("unused")
    private static final String INPUT1 = """
            498,4 -> 498,6 -> 496,6
            503,4 -> 502,4 -> 502,9 -> 494,9
            """;

    @SuppressWarnings("unused")
    private static final String INPUT = """
            499,65 -> 499,68 -> 491,68 -> 491,74 -> 508,74 -> 508,68 -> 504,68 -> 504,65
            479,42 -> 479,33 -> 479,42 -> 481,42 -> 481,32 -> 481,42 -> 483,42 -> 483,34 -> 483,42 -> 485,42 -> 485,36 -> 485,42 -> 487,42 -> 487,33 -> 487,42
            523,102 -> 523,92 -> 523,102 -> 525,102 -> 525,99 -> 525,102 -> 527,102 -> 527,95 -> 527,102 -> 529,102 -> 529,101 -> 529,102 -> 531,102 -> 531,101 -> 531,102 -> 533,102 -> 533,99 -> 533,102 -> 535,102 -> 535,94 -> 535,102 -> 537,102 -> 537,92 -> 537,102
            485,24 -> 485,25 -> 496,25 -> 496,24
            485,24 -> 485,25 -> 496,25 -> 496,24
            509,174 -> 509,170 -> 509,174 -> 511,174 -> 511,169 -> 511,174 -> 513,174 -> 513,169 -> 513,174 -> 515,174 -> 515,167 -> 515,174 -> 517,174 -> 517,166 -> 517,174 -> 519,174 -> 519,173 -> 519,174 -> 521,174 -> 521,164 -> 521,174 -> 523,174 -> 523,172 -> 523,174
            519,149 -> 524,149
            536,105 -> 536,109 -> 533,109 -> 533,115 -> 545,115 -> 545,109 -> 540,109 -> 540,105
            494,16 -> 499,16
            536,105 -> 536,109 -> 533,109 -> 533,115 -> 545,115 -> 545,109 -> 540,109 -> 540,105
            499,65 -> 499,68 -> 491,68 -> 491,74 -> 508,74 -> 508,68 -> 504,68 -> 504,65
            523,102 -> 523,92 -> 523,102 -> 525,102 -> 525,99 -> 525,102 -> 527,102 -> 527,95 -> 527,102 -> 529,102 -> 529,101 -> 529,102 -> 531,102 -> 531,101 -> 531,102 -> 533,102 -> 533,99 -> 533,102 -> 535,102 -> 535,94 -> 535,102 -> 537,102 -> 537,92 -> 537,102
            504,83 -> 508,83
            523,102 -> 523,92 -> 523,102 -> 525,102 -> 525,99 -> 525,102 -> 527,102 -> 527,95 -> 527,102 -> 529,102 -> 529,101 -> 529,102 -> 531,102 -> 531,101 -> 531,102 -> 533,102 -> 533,99 -> 533,102 -> 535,102 -> 535,94 -> 535,102 -> 537,102 -> 537,92 -> 537,102
            516,83 -> 520,83
            514,88 -> 514,89 -> 530,89
            520,155 -> 525,155
            479,42 -> 479,33 -> 479,42 -> 481,42 -> 481,32 -> 481,42 -> 483,42 -> 483,34 -> 483,42 -> 485,42 -> 485,36 -> 485,42 -> 487,42 -> 487,33 -> 487,42
            479,42 -> 479,33 -> 479,42 -> 481,42 -> 481,32 -> 481,42 -> 483,42 -> 483,34 -> 483,42 -> 485,42 -> 485,36 -> 485,42 -> 487,42 -> 487,33 -> 487,42
            522,140 -> 527,140
            536,105 -> 536,109 -> 533,109 -> 533,115 -> 545,115 -> 545,109 -> 540,109 -> 540,105
            509,174 -> 509,170 -> 509,174 -> 511,174 -> 511,169 -> 511,174 -> 513,174 -> 513,169 -> 513,174 -> 515,174 -> 515,167 -> 515,174 -> 517,174 -> 517,166 -> 517,174 -> 519,174 -> 519,173 -> 519,174 -> 521,174 -> 521,164 -> 521,174 -> 523,174 -> 523,172 -> 523,174
            502,22 -> 507,22
            494,56 -> 494,59 -> 489,59 -> 489,62 -> 501,62 -> 501,59 -> 500,59 -> 500,56
            525,137 -> 530,137
            523,102 -> 523,92 -> 523,102 -> 525,102 -> 525,99 -> 525,102 -> 527,102 -> 527,95 -> 527,102 -> 529,102 -> 529,101 -> 529,102 -> 531,102 -> 531,101 -> 531,102 -> 533,102 -> 533,99 -> 533,102 -> 535,102 -> 535,94 -> 535,102 -> 537,102 -> 537,92 -> 537,102
            523,102 -> 523,92 -> 523,102 -> 525,102 -> 525,99 -> 525,102 -> 527,102 -> 527,95 -> 527,102 -> 529,102 -> 529,101 -> 529,102 -> 531,102 -> 531,101 -> 531,102 -> 533,102 -> 533,99 -> 533,102 -> 535,102 -> 535,94 -> 535,102 -> 537,102 -> 537,92 -> 537,102
            523,152 -> 528,152
            529,128 -> 529,123 -> 529,128 -> 531,128 -> 531,123 -> 531,128 -> 533,128 -> 533,123 -> 533,128
            507,77 -> 511,77
            494,56 -> 494,59 -> 489,59 -> 489,62 -> 501,62 -> 501,59 -> 500,59 -> 500,56
            536,140 -> 541,140
            509,174 -> 509,170 -> 509,174 -> 511,174 -> 511,169 -> 511,174 -> 513,174 -> 513,169 -> 513,174 -> 515,174 -> 515,167 -> 515,174 -> 517,174 -> 517,166 -> 517,174 -> 519,174 -> 519,173 -> 519,174 -> 521,174 -> 521,164 -> 521,174 -> 523,174 -> 523,172 -> 523,174
            509,174 -> 509,170 -> 509,174 -> 511,174 -> 511,169 -> 511,174 -> 513,174 -> 513,169 -> 513,174 -> 515,174 -> 515,167 -> 515,174 -> 517,174 -> 517,166 -> 517,174 -> 519,174 -> 519,173 -> 519,174 -> 521,174 -> 521,164 -> 521,174 -> 523,174 -> 523,172 -> 523,174
            543,140 -> 548,140
            479,42 -> 479,33 -> 479,42 -> 481,42 -> 481,32 -> 481,42 -> 483,42 -> 483,34 -> 483,42 -> 485,42 -> 485,36 -> 485,42 -> 487,42 -> 487,33 -> 487,42
            479,42 -> 479,33 -> 479,42 -> 481,42 -> 481,32 -> 481,42 -> 483,42 -> 483,34 -> 483,42 -> 485,42 -> 485,36 -> 485,42 -> 487,42 -> 487,33 -> 487,42
            523,102 -> 523,92 -> 523,102 -> 525,102 -> 525,99 -> 525,102 -> 527,102 -> 527,95 -> 527,102 -> 529,102 -> 529,101 -> 529,102 -> 531,102 -> 531,101 -> 531,102 -> 533,102 -> 533,99 -> 533,102 -> 535,102 -> 535,94 -> 535,102 -> 537,102 -> 537,92 -> 537,102
            492,28 -> 492,29 -> 509,29
            485,45 -> 485,47 -> 480,47 -> 480,53 -> 496,53 -> 496,47 -> 489,47 -> 489,45
            518,161 -> 527,161 -> 527,160
            523,102 -> 523,92 -> 523,102 -> 525,102 -> 525,99 -> 525,102 -> 527,102 -> 527,95 -> 527,102 -> 529,102 -> 529,101 -> 529,102 -> 531,102 -> 531,101 -> 531,102 -> 533,102 -> 533,99 -> 533,102 -> 535,102 -> 535,94 -> 535,102 -> 537,102 -> 537,92 -> 537,102
            523,102 -> 523,92 -> 523,102 -> 525,102 -> 525,99 -> 525,102 -> 527,102 -> 527,95 -> 527,102 -> 529,102 -> 529,101 -> 529,102 -> 531,102 -> 531,101 -> 531,102 -> 533,102 -> 533,99 -> 533,102 -> 535,102 -> 535,94 -> 535,102 -> 537,102 -> 537,92 -> 537,102
            509,174 -> 509,170 -> 509,174 -> 511,174 -> 511,169 -> 511,174 -> 513,174 -> 513,169 -> 513,174 -> 515,174 -> 515,167 -> 515,174 -> 517,174 -> 517,166 -> 517,174 -> 519,174 -> 519,173 -> 519,174 -> 521,174 -> 521,164 -> 521,174 -> 523,174 -> 523,172 -> 523,174
            499,65 -> 499,68 -> 491,68 -> 491,74 -> 508,74 -> 508,68 -> 504,68 -> 504,65
            529,128 -> 529,123 -> 529,128 -> 531,128 -> 531,123 -> 531,128 -> 533,128 -> 533,123 -> 533,128
            523,102 -> 523,92 -> 523,102 -> 525,102 -> 525,99 -> 525,102 -> 527,102 -> 527,95 -> 527,102 -> 529,102 -> 529,101 -> 529,102 -> 531,102 -> 531,101 -> 531,102 -> 533,102 -> 533,99 -> 533,102 -> 535,102 -> 535,94 -> 535,102 -> 537,102 -> 537,92 -> 537,102
            523,102 -> 523,92 -> 523,102 -> 525,102 -> 525,99 -> 525,102 -> 527,102 -> 527,95 -> 527,102 -> 529,102 -> 529,101 -> 529,102 -> 531,102 -> 531,101 -> 531,102 -> 533,102 -> 533,99 -> 533,102 -> 535,102 -> 535,94 -> 535,102 -> 537,102 -> 537,92 -> 537,102
            536,105 -> 536,109 -> 533,109 -> 533,115 -> 545,115 -> 545,109 -> 540,109 -> 540,105
            518,161 -> 527,161 -> 527,160
            531,131 -> 536,131
            509,174 -> 509,170 -> 509,174 -> 511,174 -> 511,169 -> 511,174 -> 513,174 -> 513,169 -> 513,174 -> 515,174 -> 515,167 -> 515,174 -> 517,174 -> 517,166 -> 517,174 -> 519,174 -> 519,173 -> 519,174 -> 521,174 -> 521,164 -> 521,174 -> 523,174 -> 523,172 -> 523,174
            501,85 -> 505,85
            497,13 -> 502,13
            529,128 -> 529,123 -> 529,128 -> 531,128 -> 531,123 -> 531,128 -> 533,128 -> 533,123 -> 533,128
            507,85 -> 511,85
            479,42 -> 479,33 -> 479,42 -> 481,42 -> 481,32 -> 481,42 -> 483,42 -> 483,34 -> 483,42 -> 485,42 -> 485,36 -> 485,42 -> 487,42 -> 487,33 -> 487,42
            513,155 -> 518,155
            485,45 -> 485,47 -> 480,47 -> 480,53 -> 496,53 -> 496,47 -> 489,47 -> 489,45
            509,174 -> 509,170 -> 509,174 -> 511,174 -> 511,169 -> 511,174 -> 513,174 -> 513,169 -> 513,174 -> 515,174 -> 515,167 -> 515,174 -> 517,174 -> 517,166 -> 517,174 -> 519,174 -> 519,173 -> 519,174 -> 521,174 -> 521,164 -> 521,174 -> 523,174 -> 523,172 -> 523,174
            509,174 -> 509,170 -> 509,174 -> 511,174 -> 511,169 -> 511,174 -> 513,174 -> 513,169 -> 513,174 -> 515,174 -> 515,167 -> 515,174 -> 517,174 -> 517,166 -> 517,174 -> 519,174 -> 519,173 -> 519,174 -> 521,174 -> 521,164 -> 521,174 -> 523,174 -> 523,172 -> 523,174
            523,102 -> 523,92 -> 523,102 -> 525,102 -> 525,99 -> 525,102 -> 527,102 -> 527,95 -> 527,102 -> 529,102 -> 529,101 -> 529,102 -> 531,102 -> 531,101 -> 531,102 -> 533,102 -> 533,99 -> 533,102 -> 535,102 -> 535,94 -> 535,102 -> 537,102 -> 537,92 -> 537,102
            532,137 -> 537,137
            485,45 -> 485,47 -> 480,47 -> 480,53 -> 496,53 -> 496,47 -> 489,47 -> 489,45
            509,174 -> 509,170 -> 509,174 -> 511,174 -> 511,169 -> 511,174 -> 513,174 -> 513,169 -> 513,174 -> 515,174 -> 515,167 -> 515,174 -> 517,174 -> 517,166 -> 517,174 -> 519,174 -> 519,173 -> 519,174 -> 521,174 -> 521,164 -> 521,174 -> 523,174 -> 523,172 -> 523,174
            485,24 -> 485,25 -> 496,25 -> 496,24
            523,102 -> 523,92 -> 523,102 -> 525,102 -> 525,99 -> 525,102 -> 527,102 -> 527,95 -> 527,102 -> 529,102 -> 529,101 -> 529,102 -> 531,102 -> 531,101 -> 531,102 -> 533,102 -> 533,99 -> 533,102 -> 535,102 -> 535,94 -> 535,102 -> 537,102 -> 537,92 -> 537,102
            509,174 -> 509,170 -> 509,174 -> 511,174 -> 511,169 -> 511,174 -> 513,174 -> 513,169 -> 513,174 -> 515,174 -> 515,167 -> 515,174 -> 517,174 -> 517,166 -> 517,174 -> 519,174 -> 519,173 -> 519,174 -> 521,174 -> 521,164 -> 521,174 -> 523,174 -> 523,172 -> 523,174
            523,102 -> 523,92 -> 523,102 -> 525,102 -> 525,99 -> 525,102 -> 527,102 -> 527,95 -> 527,102 -> 529,102 -> 529,101 -> 529,102 -> 531,102 -> 531,101 -> 531,102 -> 533,102 -> 533,99 -> 533,102 -> 535,102 -> 535,94 -> 535,102 -> 537,102 -> 537,92 -> 537,102
            479,42 -> 479,33 -> 479,42 -> 481,42 -> 481,32 -> 481,42 -> 483,42 -> 483,34 -> 483,42 -> 485,42 -> 485,36 -> 485,42 -> 487,42 -> 487,33 -> 487,42
            495,22 -> 500,22
            529,128 -> 529,123 -> 529,128 -> 531,128 -> 531,123 -> 531,128 -> 533,128 -> 533,123 -> 533,128
            516,152 -> 521,152
            529,128 -> 529,123 -> 529,128 -> 531,128 -> 531,123 -> 531,128 -> 533,128 -> 533,123 -> 533,128
            499,65 -> 499,68 -> 491,68 -> 491,74 -> 508,74 -> 508,68 -> 504,68 -> 504,65
            529,128 -> 529,123 -> 529,128 -> 531,128 -> 531,123 -> 531,128 -> 533,128 -> 533,123 -> 533,128
            529,128 -> 529,123 -> 529,128 -> 531,128 -> 531,123 -> 531,128 -> 533,128 -> 533,123 -> 533,128
            513,81 -> 517,81
            479,42 -> 479,33 -> 479,42 -> 481,42 -> 481,32 -> 481,42 -> 483,42 -> 483,34 -> 483,42 -> 485,42 -> 485,36 -> 485,42 -> 487,42 -> 487,33 -> 487,42
            536,105 -> 536,109 -> 533,109 -> 533,115 -> 545,115 -> 545,109 -> 540,109 -> 540,105
            485,45 -> 485,47 -> 480,47 -> 480,53 -> 496,53 -> 496,47 -> 489,47 -> 489,45
            529,128 -> 529,123 -> 529,128 -> 531,128 -> 531,123 -> 531,128 -> 533,128 -> 533,123 -> 533,128
            530,152 -> 535,152
            479,42 -> 479,33 -> 479,42 -> 481,42 -> 481,32 -> 481,42 -> 483,42 -> 483,34 -> 483,42 -> 485,42 -> 485,36 -> 485,42 -> 487,42 -> 487,33 -> 487,42
            505,19 -> 510,19
            494,56 -> 494,59 -> 489,59 -> 489,62 -> 501,62 -> 501,59 -> 500,59 -> 500,56
            491,19 -> 496,19
            509,174 -> 509,170 -> 509,174 -> 511,174 -> 511,169 -> 511,174 -> 513,174 -> 513,169 -> 513,174 -> 515,174 -> 515,167 -> 515,174 -> 517,174 -> 517,166 -> 517,174 -> 519,174 -> 519,173 -> 519,174 -> 521,174 -> 521,164 -> 521,174 -> 523,174 -> 523,172 -> 523,174
            499,65 -> 499,68 -> 491,68 -> 491,74 -> 508,74 -> 508,68 -> 504,68 -> 504,65
            523,102 -> 523,92 -> 523,102 -> 525,102 -> 525,99 -> 525,102 -> 527,102 -> 527,95 -> 527,102 -> 529,102 -> 529,101 -> 529,102 -> 531,102 -> 531,101 -> 531,102 -> 533,102 -> 533,99 -> 533,102 -> 535,102 -> 535,94 -> 535,102 -> 537,102 -> 537,92 -> 537,102
            522,146 -> 527,146
            523,102 -> 523,92 -> 523,102 -> 525,102 -> 525,99 -> 525,102 -> 527,102 -> 527,95 -> 527,102 -> 529,102 -> 529,101 -> 529,102 -> 531,102 -> 531,101 -> 531,102 -> 533,102 -> 533,99 -> 533,102 -> 535,102 -> 535,94 -> 535,102 -> 537,102 -> 537,92 -> 537,102
            499,65 -> 499,68 -> 491,68 -> 491,74 -> 508,74 -> 508,68 -> 504,68 -> 504,65
            533,149 -> 538,149
            541,155 -> 546,155
            523,102 -> 523,92 -> 523,102 -> 525,102 -> 525,99 -> 525,102 -> 527,102 -> 527,95 -> 527,102 -> 529,102 -> 529,101 -> 529,102 -> 531,102 -> 531,101 -> 531,102 -> 533,102 -> 533,99 -> 533,102 -> 535,102 -> 535,94 -> 535,102 -> 537,102 -> 537,92 -> 537,102
            488,22 -> 493,22
            539,137 -> 544,137
            494,56 -> 494,59 -> 489,59 -> 489,62 -> 501,62 -> 501,59 -> 500,59 -> 500,56
            510,83 -> 514,83
            528,134 -> 533,134
            509,174 -> 509,170 -> 509,174 -> 511,174 -> 511,169 -> 511,174 -> 513,174 -> 513,169 -> 513,174 -> 515,174 -> 515,167 -> 515,174 -> 517,174 -> 517,166 -> 517,174 -> 519,174 -> 519,173 -> 519,174 -> 521,174 -> 521,164 -> 521,174 -> 523,174 -> 523,172 -> 523,174
            537,152 -> 542,152
            509,174 -> 509,170 -> 509,174 -> 511,174 -> 511,169 -> 511,174 -> 513,174 -> 513,169 -> 513,174 -> 515,174 -> 515,167 -> 515,174 -> 517,174 -> 517,166 -> 517,174 -> 519,174 -> 519,173 -> 519,174 -> 521,174 -> 521,164 -> 521,174 -> 523,174 -> 523,172 -> 523,174
            495,85 -> 499,85
            523,102 -> 523,92 -> 523,102 -> 525,102 -> 525,99 -> 525,102 -> 527,102 -> 527,95 -> 527,102 -> 529,102 -> 529,101 -> 529,102 -> 531,102 -> 531,101 -> 531,102 -> 533,102 -> 533,99 -> 533,102 -> 535,102 -> 535,94 -> 535,102 -> 537,102 -> 537,92 -> 537,102
            501,16 -> 506,16
            494,56 -> 494,59 -> 489,59 -> 489,62 -> 501,62 -> 501,59 -> 500,59 -> 500,56
            514,88 -> 514,89 -> 530,89
            485,45 -> 485,47 -> 480,47 -> 480,53 -> 496,53 -> 496,47 -> 489,47 -> 489,45
            504,79 -> 508,79
            509,174 -> 509,170 -> 509,174 -> 511,174 -> 511,169 -> 511,174 -> 513,174 -> 513,169 -> 513,174 -> 515,174 -> 515,167 -> 515,174 -> 517,174 -> 517,166 -> 517,174 -> 519,174 -> 519,173 -> 519,174 -> 521,174 -> 521,164 -> 521,174 -> 523,174 -> 523,172 -> 523,174
            525,143 -> 530,143
            523,102 -> 523,92 -> 523,102 -> 525,102 -> 525,99 -> 525,102 -> 527,102 -> 527,95 -> 527,102 -> 529,102 -> 529,101 -> 529,102 -> 531,102 -> 531,101 -> 531,102 -> 533,102 -> 533,99 -> 533,102 -> 535,102 -> 535,94 -> 535,102 -> 537,102 -> 537,92 -> 537,102
            509,174 -> 509,170 -> 509,174 -> 511,174 -> 511,169 -> 511,174 -> 513,174 -> 513,169 -> 513,174 -> 515,174 -> 515,167 -> 515,174 -> 517,174 -> 517,166 -> 517,174 -> 519,174 -> 519,173 -> 519,174 -> 521,174 -> 521,164 -> 521,174 -> 523,174 -> 523,172 -> 523,174
            529,146 -> 534,146
            479,42 -> 479,33 -> 479,42 -> 481,42 -> 481,32 -> 481,42 -> 483,42 -> 483,34 -> 483,42 -> 485,42 -> 485,36 -> 485,42 -> 487,42 -> 487,33 -> 487,42
            494,56 -> 494,59 -> 489,59 -> 489,62 -> 501,62 -> 501,59 -> 500,59 -> 500,56
            526,149 -> 531,149
            509,174 -> 509,170 -> 509,174 -> 511,174 -> 511,169 -> 511,174 -> 513,174 -> 513,169 -> 513,174 -> 515,174 -> 515,167 -> 515,174 -> 517,174 -> 517,166 -> 517,174 -> 519,174 -> 519,173 -> 519,174 -> 521,174 -> 521,164 -> 521,174 -> 523,174 -> 523,172 -> 523,174
            527,155 -> 532,155
            507,81 -> 511,81
            509,174 -> 509,170 -> 509,174 -> 511,174 -> 511,169 -> 511,174 -> 513,174 -> 513,169 -> 513,174 -> 515,174 -> 515,167 -> 515,174 -> 517,174 -> 517,166 -> 517,174 -> 519,174 -> 519,173 -> 519,174 -> 521,174 -> 521,164 -> 521,174 -> 523,174 -> 523,172 -> 523,174
            492,28 -> 492,29 -> 509,29
            535,134 -> 540,134
            499,65 -> 499,68 -> 491,68 -> 491,74 -> 508,74 -> 508,68 -> 504,68 -> 504,65
            479,42 -> 479,33 -> 479,42 -> 481,42 -> 481,32 -> 481,42 -> 483,42 -> 483,34 -> 483,42 -> 485,42 -> 485,36 -> 485,42 -> 487,42 -> 487,33 -> 487,42
            523,102 -> 523,92 -> 523,102 -> 525,102 -> 525,99 -> 525,102 -> 527,102 -> 527,95 -> 527,102 -> 529,102 -> 529,101 -> 529,102 -> 531,102 -> 531,101 -> 531,102 -> 533,102 -> 533,99 -> 533,102 -> 535,102 -> 535,94 -> 535,102 -> 537,102 -> 537,92 -> 537,102
            536,105 -> 536,109 -> 533,109 -> 533,115 -> 545,115 -> 545,109 -> 540,109 -> 540,105
            523,102 -> 523,92 -> 523,102 -> 525,102 -> 525,99 -> 525,102 -> 527,102 -> 527,95 -> 527,102 -> 529,102 -> 529,101 -> 529,102 -> 531,102 -> 531,101 -> 531,102 -> 533,102 -> 533,99 -> 533,102 -> 535,102 -> 535,94 -> 535,102 -> 537,102 -> 537,92 -> 537,102
            536,105 -> 536,109 -> 533,109 -> 533,115 -> 545,115 -> 545,109 -> 540,109 -> 540,105
            485,45 -> 485,47 -> 480,47 -> 480,53 -> 496,53 -> 496,47 -> 489,47 -> 489,45
            523,102 -> 523,92 -> 523,102 -> 525,102 -> 525,99 -> 525,102 -> 527,102 -> 527,95 -> 527,102 -> 529,102 -> 529,101 -> 529,102 -> 531,102 -> 531,101 -> 531,102 -> 533,102 -> 533,99 -> 533,102 -> 535,102 -> 535,94 -> 535,102 -> 537,102 -> 537,92 -> 537,102
            509,174 -> 509,170 -> 509,174 -> 511,174 -> 511,169 -> 511,174 -> 513,174 -> 513,169 -> 513,174 -> 515,174 -> 515,167 -> 515,174 -> 517,174 -> 517,166 -> 517,174 -> 519,174 -> 519,173 -> 519,174 -> 521,174 -> 521,164 -> 521,174 -> 523,174 -> 523,172 -> 523,174
            523,102 -> 523,92 -> 523,102 -> 525,102 -> 525,99 -> 525,102 -> 527,102 -> 527,95 -> 527,102 -> 529,102 -> 529,101 -> 529,102 -> 531,102 -> 531,101 -> 531,102 -> 533,102 -> 533,99 -> 533,102 -> 535,102 -> 535,94 -> 535,102 -> 537,102 -> 537,92 -> 537,102
            509,174 -> 509,170 -> 509,174 -> 511,174 -> 511,169 -> 511,174 -> 513,174 -> 513,169 -> 513,174 -> 515,174 -> 515,167 -> 515,174 -> 517,174 -> 517,166 -> 517,174 -> 519,174 -> 519,173 -> 519,174 -> 521,174 -> 521,164 -> 521,174 -> 523,174 -> 523,172 -> 523,174
            509,174 -> 509,170 -> 509,174 -> 511,174 -> 511,169 -> 511,174 -> 513,174 -> 513,169 -> 513,174 -> 515,174 -> 515,167 -> 515,174 -> 517,174 -> 517,166 -> 517,174 -> 519,174 -> 519,173 -> 519,174 -> 521,174 -> 521,164 -> 521,174 -> 523,174 -> 523,172 -> 523,174
            509,174 -> 509,170 -> 509,174 -> 511,174 -> 511,169 -> 511,174 -> 513,174 -> 513,169 -> 513,174 -> 515,174 -> 515,167 -> 515,174 -> 517,174 -> 517,166 -> 517,174 -> 519,174 -> 519,173 -> 519,174 -> 521,174 -> 521,164 -> 521,174 -> 523,174 -> 523,172 -> 523,174
            485,45 -> 485,47 -> 480,47 -> 480,53 -> 496,53 -> 496,47 -> 489,47 -> 489,45
            510,79 -> 514,79
            529,140 -> 534,140
            509,174 -> 509,170 -> 509,174 -> 511,174 -> 511,169 -> 511,174 -> 513,174 -> 513,169 -> 513,174 -> 515,174 -> 515,167 -> 515,174 -> 517,174 -> 517,166 -> 517,174 -> 519,174 -> 519,173 -> 519,174 -> 521,174 -> 521,164 -> 521,174 -> 523,174 -> 523,172 -> 523,174
            498,83 -> 502,83
            494,56 -> 494,59 -> 489,59 -> 489,62 -> 501,62 -> 501,59 -> 500,59 -> 500,56
            479,42 -> 479,33 -> 479,42 -> 481,42 -> 481,32 -> 481,42 -> 483,42 -> 483,34 -> 483,42 -> 485,42 -> 485,36 -> 485,42 -> 487,42 -> 487,33 -> 487,42
            519,85 -> 523,85
            498,19 -> 503,19
            501,81 -> 505,81
            513,85 -> 517,85
            509,22 -> 514,22
            523,102 -> 523,92 -> 523,102 -> 525,102 -> 525,99 -> 525,102 -> 527,102 -> 527,95 -> 527,102 -> 529,102 -> 529,101 -> 529,102 -> 531,102 -> 531,101 -> 531,102 -> 533,102 -> 533,99 -> 533,102 -> 535,102 -> 535,94 -> 535,102 -> 537,102 -> 537,92 -> 537,102
            479,42 -> 479,33 -> 479,42 -> 481,42 -> 481,32 -> 481,42 -> 483,42 -> 483,34 -> 483,42 -> 485,42 -> 485,36 -> 485,42 -> 487,42 -> 487,33 -> 487,42
            509,174 -> 509,170 -> 509,174 -> 511,174 -> 511,169 -> 511,174 -> 513,174 -> 513,169 -> 513,174 -> 515,174 -> 515,167 -> 515,174 -> 517,174 -> 517,166 -> 517,174 -> 519,174 -> 519,173 -> 519,174 -> 521,174 -> 521,164 -> 521,174 -> 523,174 -> 523,172 -> 523,174
            479,42 -> 479,33 -> 479,42 -> 481,42 -> 481,32 -> 481,42 -> 483,42 -> 483,34 -> 483,42 -> 485,42 -> 485,36 -> 485,42 -> 487,42 -> 487,33 -> 487,42
            534,155 -> 539,155
            """;
}