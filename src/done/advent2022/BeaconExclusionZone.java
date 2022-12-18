package done.advent2022;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.lang.System.lineSeparator;
import static java.util.stream.Collectors.joining;
import static util.LineSupplier.lines;
import static util.Utils.split;
import static util.Utils.substring;

@SuppressWarnings("unused")
public class BeaconExclusionZone {

    private static final int TARGET_ROW1 = 10;
    private static final int TARGET_ROW = 2_000_000;

    private static final Beacons beacons = new Beacons();

    public static void main(String[] args) {
        try (Scanner in = new Scanner(INPUT)) {
            lines(in)
                    .map(Sensor::create)
                    .forEach(beacons::addSensor);

            if (beacons.bounds.maxX - beacons.bounds.minX <= 1000) {
                System.out.println(beacons);
            }

            System.out.println(beacons.printSensors());
            System.out.println(beacons.printRanges(TARGET_ROW));

            System.out.println("Result: " + beacons.countNonBeaconPositions(TARGET_ROW));

            long result = IntStream.rangeClosed(0, 4_000_000)
                                   .mapToObj(beacons::ranges)
                                   .filter(Ranges::hasMoreRanges)
                                   .map(r -> new Point(r.first().to + 1 , r.y))
                                   .mapToLong(p -> p.x * 4_000_000L + p.y)
                                   .findFirst()
                                   .orElse(0);
            System.out.println("Rows with 2 and more: " + result);
        }
    }

    static class Beacons {
        private final List<Sensor> sensors = new ArrayList<>();
        private final Set<Point> sensorPositions = new HashSet<>();
        private final Set<Point> beaconPositions = new HashSet<>();

        private final Bounds bounds = new Bounds();

        void addSensor(Sensor sensor) {
            sensors.add(sensor);
            sensorPositions.add(sensor.position());
            beaconPositions.add(sensor.nearestBeacon());
            bounds.update(sensor.position());
            bounds.update(sensor.nearestBeacon());
        }

        long countNonBeaconPositions(int y) {
            long beaconsInRow = beaconPositions.stream().filter(p -> p.y == y).count();
            System.out.println("Becons in row: " + beaconsInRow);
            return ranges(y).rangesSize() - beaconsInRow;
        }

        Ranges ranges(int y) {
            return new Ranges(y,
                    sensors.stream()
                           .map(s -> s.range(y))
                           .flatMap(Optional::stream)
                           .toList()
            ).normalize();
        }

        @Override
        public String toString() {
            return bounds.rows()
                         .mapToObj(this::printLine)
                         .collect(joining("\n"));
        }

        private String printLine(int y) {
            return row(y)
                    .map(this::printPoint)
                    .collect(joining());
        }

        private String printPoint(Point p) {
            if (sensorPositions.contains(p)) {
                return "S";
            }
            if (beaconPositions.contains(p)) {
                return "B";
            }
            return ".";
        }

        private Stream<Point> row(int y) {
            return bounds.cols().mapToObj(x -> new Point(x, y));
        }

        public String printSensors() {
            return sensors.stream().map(Sensor::print).collect(joining(lineSeparator()));
        }

        public String printRanges(int y) {
            Ranges ranges = ranges(y);
            String rangesList = ranges.stream()
                                      .map(Range::toString)
                                      .collect(joining(lineSeparator()));
            String rangeSizes = ranges.stream()
                                         .mapToInt(r -> Math.abs(r.to - r.from))
                                         .mapToObj(Integer::toString)
                                         .collect(joining(", "));
            return rangesList +
                    lineSeparator() + rangeSizes +
                    lineSeparator() + "Total: " + ranges.rangesSize();
        }
    }

    static class Bounds {
        private int minX = Integer.MAX_VALUE;
        private int maxX = Integer.MIN_VALUE;

        private int minY = Integer.MAX_VALUE;
        private int maxY = Integer.MIN_VALUE;

        void update(Point point) {
            updateX(point.x);
            updateY(point.y);
        }

        IntStream cols() {
            return IntStream.rangeClosed(minX, maxX);
        }

        IntStream rows() {
            return IntStream.rangeClosed(minY, maxY);
        }

        private void updateX(int x) {
            minX = Math.min(x, minX);
            maxX = Math.max(x, maxX);
        }

        private void updateY(int y) {
            minY = Math.min(y, minY);
            maxY = Math.max(y, maxY);
        }
    }

    record Ranges(int y, List<Range> ranges) {
        Range first() {
            return ranges.stream().findFirst().orElse(null);
        }

        boolean isInRange(Point p) {
            return ranges.stream().anyMatch(p::isInRange);
        }

        int rangesSize() {
            return ranges.stream()
                         .mapToInt(Range::size)
                         .sum();
        }

        Ranges normalize() {
            List<Range> prevRanges = ranges;
            List<Range> mergedRanges = normalize(ranges);
            while (mergedRanges.size() != prevRanges.size()) {
                prevRanges = mergedRanges;
                mergedRanges = normalize(mergedRanges);
            }
            return new Ranges(y, mergedRanges);
        }

        boolean hasMoreRanges() {
            return ranges.size() > 1;
        }

        private static List<Range> normalize(Collection<Range> ranges) {
            List<Range> mergedRanges = new ArrayList<>();
            Deque<Range> rangesStack = new LinkedList<>(ranges);
            while (rangesStack.peek() != null) {
                Range range = rangesStack.pop();
                Set<Range> removed = new HashSet<>();
                for (Range other : rangesStack) {
                    if (range.canMerge(other)) {
                        range = range.merge(other);
                        removed.add(other);
                    }
                }
                rangesStack.removeIf(removed::contains);
                mergedRanges.add(range);
            }
            return mergedRanges;
        }

        Stream<Range> stream() {
            return ranges.stream();
        }
    }

    record Range(int from, int to) {
        int size() {
            return to - from + 1;
        }

        boolean canMerge(Range other) {
            //      <======>
            // <=================>
            // OR
            // <=================>
            //      <======>
            // OR
            //      <======>
            // <=======>
            // OR
            // <=========>
            //        <=========>
            return from >= other.from && to <= other.to
                    || other.from >= from && other.to <= to
                    || from >= other.from && from <= other.to
                    || to >= other.from && to <= other.to;
        }

        Range merge(Range other) {
            if (from >= other.from && to <= other.to) {
                return other;
            }
            if (other.from >= from && other.to <= to) {
                return this;
            }
            return new Range(Math.min(from, other.from), Math.max(to, other.to));
        }
    }

    record Sensor(Point position, Point nearestBeacon) {
        Optional<Range> range(int y) {
            int sensorY = position.y;
            int radius = radius();
            int deltaY = Math.abs(y - sensorY);
            if (deltaY > radius) {
                return Optional.empty();
            }
            int deltaX = radius - deltaY;
            /*
                   3     9 => deltaX = 6, deltaY = 3 => radius = 9
            7  ....######S
            8  ....#......
            9  ....#......
            10 ....B......
             */
            return Optional.of(new Range(position.x - deltaX, position.x + deltaX));
        }

        boolean isInRange(Point p) {
            boolean inRange = position.distance(p) <= radius();
            if (inRange) {
                System.out.println(" - is in range of sensor " + position.print());
            }
            return inRange;
        }

        int radius() {
            return position.distance(nearestBeacon);
        }

        String print() {
            return "Sensor at " + position.print() + ", radius=" + radius();
        }

        static Sensor create(String line) {
            String sensorPos = substring(line, "Sensor at ", ":");
            String beaconPos = substring(line, "beacon is at ");
            return new Sensor(Point.create(sensorPos), Point.create(beaconPos));
        }
    }

    record Point(int x, int y) {
        int distance(Point p) {
            return Math.abs(x - p.x) + Math.abs(y - p.y);
        }

        boolean isInRange(Range r) {
            return x >= r.from && x <= r.to;
        }

        String print() {
            return "x=" + x + ", y=" + y;
        }

        static Point create(String line) {
            int[] coords = split(line)
                    .map(s -> substring(s, "="))
                    .map(String::trim)
                    .mapToInt(Integer::parseInt)
                    .toArray();
            return new Point(coords[0], coords[1]);
        }
    }

    private static final String INPUT1 = """
            Sensor at x=2, y=18: closest beacon is at x=-2, y=15
            Sensor at x=9, y=16: closest beacon is at x=10, y=16
            Sensor at x=13, y=2: closest beacon is at x=15, y=3
            Sensor at x=12, y=14: closest beacon is at x=10, y=16
            Sensor at x=10, y=20: closest beacon is at x=10, y=16
            Sensor at x=14, y=17: closest beacon is at x=10, y=16
            Sensor at x=8, y=7: closest beacon is at x=2, y=10
            Sensor at x=2, y=0: closest beacon is at x=2, y=10
            Sensor at x=0, y=11: closest beacon is at x=2, y=10
            Sensor at x=20, y=14: closest beacon is at x=25, y=17
            Sensor at x=17, y=20: closest beacon is at x=21, y=22
            Sensor at x=16, y=7: closest beacon is at x=15, y=3
            Sensor at x=14, y=3: closest beacon is at x=15, y=3
            Sensor at x=20, y=1: closest beacon is at x=15, y=3
            """;

    private static final String INPUT = """
            Sensor at x=2692921, y=2988627: closest beacon is at x=2453611, y=3029623
            Sensor at x=1557973, y=1620482: closest beacon is at x=1908435, y=2403457
            Sensor at x=278431, y=3878878: closest beacon is at x=-1050422, y=3218536
            Sensor at x=1432037, y=3317707: closest beacon is at x=2453611, y=3029623
            Sensor at x=3191434, y=3564121: closest beacon is at x=3420256, y=2939344
            Sensor at x=3080887, y=2781756: closest beacon is at x=3420256, y=2939344
            Sensor at x=3543287, y=3060807: closest beacon is at x=3420256, y=2939344
            Sensor at x=2476158, y=3949016: closest beacon is at x=2453611, y=3029623
            Sensor at x=3999769, y=3985671: closest beacon is at x=3420256, y=2939344
            Sensor at x=2435331, y=2200565: closest beacon is at x=1908435, y=2403457
            Sensor at x=3970047, y=2036397: closest beacon is at x=3691788, y=1874066
            Sensor at x=2232167, y=2750817: closest beacon is at x=2453611, y=3029623
            Sensor at x=157988, y=333826: closest beacon is at x=-1236383, y=477990
            Sensor at x=1035254, y=2261267: closest beacon is at x=1908435, y=2403457
            Sensor at x=1154009, y=888885: closest beacon is at x=1070922, y=-543463
            Sensor at x=2704724, y=257848: closest beacon is at x=3428489, y=-741777
            Sensor at x=3672526, y=2651153: closest beacon is at x=3420256, y=2939344
            Sensor at x=2030614, y=2603134: closest beacon is at x=1908435, y=2403457
            Sensor at x=2550448, y=2781018: closest beacon is at x=2453611, y=3029623
            Sensor at x=3162759, y=2196461: closest beacon is at x=3691788, y=1874066
            Sensor at x=463834, y=1709480: closest beacon is at x=-208427, y=2000000
            Sensor at x=217427, y=2725325: closest beacon is at x=-208427, y=2000000
            Sensor at x=3903198, y=945190: closest beacon is at x=3691788, y=1874066
            """;
}