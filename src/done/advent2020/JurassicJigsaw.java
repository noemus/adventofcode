package done.advent2020;

import org.junit.Test;
import util.BatchSupplier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.Collections.emptySet;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static util.Utils.zip;

@SuppressWarnings("unused")
public class JurassicJigsaw {

    static Tile[] tiles;
    static List<Tile> corners = new ArrayList<>();

    public static void main(String[] args) {
        try (Scanner in = new Scanner(INPUT2)) {
            tiles = Stream.generate(new BatchSupplier(in).withDelimiter(';'))
                            .takeWhile(Objects::nonNull)
                            .map(batch -> batch.split(";"))
                            .map(Tile::of)
                            .toArray(Tile[]::new);

            for (Tile tile : tiles) {
                findAttachedTiles(tile);
                if (tile.isCorner()) {
                    corners.add(tile);
                }
            }

            Image image = new Image(createImageTiles());
            final String[] maze = image.toMaze();

            Stream.of(maze).forEach(System.out::println);

            long monsters = mazeRotations().stream()
                                           .map(rot -> rot.apply(maze))
                                           .mapToLong(JurassicJigsaw::findMonsters)
                                           .filter(count -> count > 0L)
                                           .findFirst()
                                           .orElse(0L);

            Stream.of(mazeCopy).forEach(System.out::println);

            long hashes = Stream.of(mazeCopy)
                                .map(String::chars)
                                .mapToLong(JurassicJigsaw::countHashes)
                                .sum();

            long result = corners.stream()
                                 .mapToLong(Tile::id)
                                 .reduce((x,y) -> x * y)
                                 .orElse(0);

            System.out.println("Corners: " + result);
            System.out.println("Monsters: " + monsters);
            System.out.println("Result: " + hashes);
        }
    }

    private static long countHashes(IntStream chars) {
        return chars.filter(ch -> ch == '#').count();
    }

    static int mazeNumber = 1;

    private static final Pattern MONSTER_LINE_1 = Pattern.compile("..................(#).");
    private static final Pattern MONSTER_LINE_2 = Pattern.compile("(#)....(##)....(##)....(###)");
    private static final Pattern MONSTER_LINE_3 = Pattern.compile(".(#)..(#)..(#)..(#)..(#)..(#)...");

    record MazeRes(long count, String[] maze) {
        static MazeRes of(long count, String[] maze) {
            return new MazeRes(count, maze);
        }
    }

    static String[] mazeCopy;

    private static long findMonsters(String[] maze) {
        mazeCopy = maze;

        System.out.println("Searching for monsters in maze " + mazeNumber);
//        System.out.println(maze[0]);
//        System.out.println(maze[1]);
//        System.out.println(maze[2]);
//        System.out.println(maze[3]);
//        System.out.println(maze[4]);
//        System.out.println("-----------------------------------------------------------------------");

        long count = 0L;
        for (int row = 1; row < maze.length - 1; row++) {
            int start = 0;
            Matcher matcher1 = MONSTER_LINE_1.matcher(maze[row - 1]);
            Matcher matcher2 = MONSTER_LINE_2.matcher(maze[row]);
            Matcher matcher3 = MONSTER_LINE_3.matcher(maze[row + 1]);

            count += searchForMonster(row, matcher1, matcher2, matcher3);
        }
        System.out.println("Found " + count + " monsters in maze " + mazeNumber);
        System.out.println("-----------------------------------------------------------------------");
        mazeNumber++;
        return count;
    }

    private static long searchForMonster(int row, Matcher matcher1, Matcher matcher2, Matcher matcher3) {
        int start = 0;
        long count = 0L;

        while (matcher2.find(start)) {
            start = matcher2.start();

            if (matcher1.find(start) && matcher3.find(start)) {
                if (matcher1.start() == start && matcher3.start() == start) {
                    count++;
                    System.out.println("Found monster at row " + row);
                    updateMonsterLine1(row, matcher1);
                    updateMonsterLine2(row, matcher2);
                    updateMonsterLine3(row, matcher3);
                    start++;
                } else {
                    start = Math.min(matcher1.start(), matcher3.start());
                }
            } else {
                break;
            }
        }

        return count;
    }

    private static void updateMonsterLine1(int row, Matcher matcher1) {
        String monsterLine = mazeCopy[row - 1];
        monsterLine = monsterLine.substring(0, matcher1.start(1)) + "O" + monsterLine.substring(matcher1.end(1));
        mazeCopy[row - 1] = monsterLine;
    }

    private static void updateMonsterLine2(int row, Matcher matcher2) {
        String monsterLine = mazeCopy[row];
        monsterLine = monsterLine.substring(0, matcher2.start(1)) + "O" + monsterLine.substring(matcher2.end(1));
        monsterLine = monsterLine.substring(0, matcher2.start(2)) + "OO" + monsterLine.substring(matcher2.end(2));
        monsterLine = monsterLine.substring(0, matcher2.start(3)) + "OO" + monsterLine.substring(matcher2.end(3));
        monsterLine = monsterLine.substring(0, matcher2.start(4)) + "OOO" + monsterLine.substring(matcher2.end(4));
        mazeCopy[row] = monsterLine;
    }

    private static void updateMonsterLine3(int row, Matcher matcher3) {
        String monsterLine = mazeCopy[row + 1];
        monsterLine = monsterLine.substring(0, matcher3.start(1)) + "O" + monsterLine.substring(matcher3.end(1));
        monsterLine = monsterLine.substring(0, matcher3.start(2)) + "O" + monsterLine.substring(matcher3.end(2));
        monsterLine = monsterLine.substring(0, matcher3.start(3)) + "O" + monsterLine.substring(matcher3.end(3));
        monsterLine = monsterLine.substring(0, matcher3.start(4)) + "O" + monsterLine.substring(matcher3.end(4));
        monsterLine = monsterLine.substring(0, matcher3.start(5)) + "O" + monsterLine.substring(matcher3.end(5));
        monsterLine = monsterLine.substring(0, matcher3.start(6)) + "O" + monsterLine.substring(matcher3.end(6));
        mazeCopy[row + 1] = monsterLine;
    }

    private static final Function<String[], String[]> ROTATE_LINES_BACK = JurassicJigsaw::rotateLinesBack;
    private static final Function<String[], String[]> FLIP_LINES_HORIZONTALLY = JurassicJigsaw::flipLinesHorizontally;
    private static final Function<String[], String[]> FLIP_LINES_VERTICALLY = JurassicJigsaw::flipLinesVertically;

    private static List<Function<String[], String[]>> mazeRotations() {
        return List.of(
                Function.identity(),
                FLIP_LINES_HORIZONTALLY,
                FLIP_LINES_VERTICALLY,
                FLIP_LINES_HORIZONTALLY.andThen(FLIP_LINES_VERTICALLY),
                ROTATE_LINES_BACK,
                ROTATE_LINES_BACK.andThen(FLIP_LINES_HORIZONTALLY),
                ROTATE_LINES_BACK.andThen(FLIP_LINES_VERTICALLY),
                ROTATE_LINES_BACK.andThen(FLIP_LINES_HORIZONTALLY).andThen(FLIP_LINES_VERTICALLY)
        );
    }

    private static List<List<Tile>> createImageTiles() {
        List<List<Tile>> imageTiles = new ArrayList<>();

        Tile tile;
        Tile left = null;
        Tile right = corners.get(0);
        Tile top = null;

        int col = 0;
        List<Tile> prevRow = null;

        do {
            List<Tile> row = new ArrayList<>();

            do {
                tile = right;
                tile = tile.rotate(left, top);
//                System.out.println(tile);

                right = tile.rightTile();
                top = right != null && prevRow != null
                      ? prevRow.get(++col)
                      : null;
                left = tile;

                row.add(tile);
            } while (right != null);

            imageTiles.add(row);
            prevRow = row;

            col = 0;
            top = row.get(col);
            right = top.bottomTile();
            left = null;
        } while (right != null);

        return imageTiles;
    }

    private static void findAttachedTiles(Tile candidate) {
        if (candidate.isFullyAttached()) {
            return;
        }

        for (Tile tile : tiles) {
            if (tile == candidate) continue;
            if (tile.isAttachedTo(candidate)) continue;
            if (tile.isDistant(candidate)) continue;

            Optional<Long> candidateSide = candidate.sides.stream().filter(tile::hasSide).findFirst();
            if (candidateSide.isPresent()) {
                candidate.attach(candidateSide.get(), tile);
            } else {
                candidate.distant(tile);
            }
        }
    }

    static class Image {
        final List<List<Tile>> rows;

        Image(List<List<Tile>> rows) {
            this.rows = rows;
        }

        /**
         * Removes sides and merges tile lines
         */
        String[] toMaze() {
            return rows.stream()
                       .map(this::reduceTiles)
                       .collect(toList()).stream().flatMap(Stream::of).toArray(String[]::new);
        }

        private String[] reduceTiles(List<Tile> row) {
            return row.stream()
                      .map(Tile::removeBorder)
                      .map(t -> t.lines)
                      .reduce(Image::merge)
                      .orElseThrow();
        }

        static String[] merge(String[] lines1, String[] lines2) {
            return zip(Stream.of(lines1), Stream.of(lines2), (l1,l2) -> l1 + l2)
                    .toArray(String[]::new);
        }
    }

    static class Tile {
        static final Map<Long, Tile> tilesMap = new HashMap<>();

        final Long id;
        final Set<Long> sides;
        final List<Long> sidesList;
        final Set<Tile> distant;
        final String[] lines;
        final Map<Long, Long> attached;
        Tile right;
        Tile bottom;

        Tile(Tile tile, String[] lines) {
            this.id = tile.id;
            this.attached = tile.attached;
            this.right = tile.right;
            this.bottom = tile.bottom;

            this.sides = tile.sides;
            this.sidesList = tile.sidesList;
            this.distant = emptySet();

            this.lines = lines;

            tilesMap.put(id, this);
        }

        Tile(long id, String[] lines, String... sides) {
            this.id = id;
            this.lines = lines;
            this.attached = new HashMap<>();
            this.sides = new HashSet<>();
            this.sidesList = new ArrayList<>();
            this.distant = new HashSet<>();

            sides = Stream.of(sides)
                          .map(side -> side.replace('#', '1').replace('.', '0'))
                          .toArray(String[]::new);

            Long top = Long.valueOf(sides[0], 2);
            Long right = Long.valueOf(sides[1], 2);
            Long bottom = Long.valueOf(sides[2], 2);
            Long left = Long.valueOf(sides[3], 2);

            addSides(top, right, bottom, left);

            Long topReversed = Long.valueOf(flip(sides[0]), 2);
            Long rightReversed = Long.valueOf(flip(sides[1]), 2);
            Long bottomReversed = Long.valueOf(flip(sides[2]), 2);
            Long leftReversed = Long.valueOf(flip(sides[3]), 2);

            addSides(topReversed, rightReversed, bottomReversed, leftReversed);

            tilesMap.put(id, this);
        }

        private void addSides(Long top, Long right, Long bottom, Long left) {
            addSide(top);
            addSide(right);
            addSide(bottom);
            addSide(left);
        }

        private void addSide(Long side) {
            this.sides.add(side);
            this.sidesList.add(side);
        }

        public long id() {
            return id;
        }

        boolean isAttachedTo(Tile tile) {
            return attached.containsKey(tile.id());
        }

        boolean isDistant(Tile tile) {
            return distant.contains(tile);
        }

        void attach(Long side, Tile tile) {
            attached.put(tile.id(), side);
            tile.attached.put(this.id, side);
        }

        void distant(Tile tile) {
            distant.add(tile);
            tile.distant.add(this);
        }

        boolean hasSide(Long side) {
            return sides.contains(side);
        }

        boolean isCorner() {
            return attached.size() == 2;
        }

        boolean isFullyAttached() {
            return attached.size() >= 4;
        }

        /**
         * Returns right tile to this tile, returns null it is on last column.
         */
        Tile rightTile() {
            return right;
        }

        /**
         * Returns bottom tile to this tile, returns null if it is on last row.
         */
        Tile bottomTile() {
            return bottom;
        }

        /**
         * Changes tile orientation so that left tile is on left and top is on top. If top is null then tile on top row.
         */
        Tile rotate(Tile left, Tile top) {
            if (left == null && top == null) {
                List<Long> topLeftSides = new ArrayList<>(attached.keySet());
                Function<Tile, Tile> rot;
                Long firstId = topLeftSides.get(0);
                Long secondId = topLeftSides.get(1);
                Long firstTile = attached.get(firstId);
                Long secondTile = attached.get(secondId);
                Long rightTile = sidesList.get(RIGHT);
                Long leftTile = sidesList.get(LEFT);
                Long bottomTile = sidesList.get(BOTTOM);

                if (rightTile.equals(firstTile)) {
                    right = Tile.byId(firstId);
                    bottom = Tile.byId(secondId);
                    rot = Function.identity();
                    if (!bottomTile.equals(secondTile)) {
                        rot = rot.andThen(horizontalFlip());
                    }
                } else if (leftTile.equals(firstTile)) {
                    right = Tile.byId(firstId);
                    bottom = Tile.byId(secondId);
                    rot = verticalFlip();
                    if (!bottomTile.equals(secondTile)) {
                        rot = rot.andThen(horizontalFlip());
                    }
                } else if (rightTile.equals(secondTile)) {
                    right = Tile.byId(secondId);
                    bottom = Tile.byId(firstId);
                    rot = Function.identity();
                    if (!bottomTile.equals(firstTile)) {
                        rot = rot.andThen(horizontalFlip());
                    }
                } else if (leftTile.equals(secondTile)) {
                    right = Tile.byId(secondId);
                    bottom = Tile.byId(firstId);
                    rot = verticalFlip();
                    if (!bottomTile.equals(firstTile)) {
                        rot = rot.andThen(horizontalFlip());
                    }
                } else if (bottomTile.equals(secondTile)) {
                    right = Tile.byId(firstId);
                    bottom = Tile.byId(secondId);
                    rot = Function.identity();
                    if (sidesList.get(LEFT_REVERSED).equals(firstTile)) {
                        rot = rot.andThen(verticalFlip());
                    }
                } else {
                    throw new IllegalStateException();
                }

                final Tile rotated = rot.apply(this);

//                System.out.println("left: n/a");
//                System.out.println("top: n/a");
//                System.out.println("right: " + rotated.right.id());
//                System.out.println("bottom: " + rotated.bottom.id());

                return rotated;
            }
            if (left == null) {
                if (attached.size() == 4) {
                    throw new IllegalStateException();
                }
                Long topSide = attached.get(top.id());
                int topSidePos = sidesList.indexOf(topSide);

                return rotateTile(-1, topSidePos);
            }
            if (top == null) {
                if (attached.size() == 4) {
                    throw new IllegalStateException();
                }
                Long leftSide = attached.get(left.id());
                int leftSidePos = sidesList.indexOf(leftSide);

                return rotateTile(leftSidePos, -1);
            }

            Long leftSide = attached.get(left.id());
            int leftSidePos = sidesList.indexOf(leftSide);
            Long topSide = attached.get(top.id());
            int topSidePos = sidesList.indexOf(topSide);

            return rotateTile(leftSidePos, topSidePos);
        }

        private static final int TOP                = 0;
        private static final int RIGHT              = 1;
        private static final int BOTTOM             = 2;
        private static final int LEFT               = 3;
        private static final int TOP_REVERSED       = 4;
        private static final int RIGHT_REVERSED     = 5;
        private static final int BOTTOM_REVERSED    = 6;
        private static final int LEFT_REVERSED      = 7;

        private Tile rotateTile(int leftSidePos, int topSidePos) {
            Tile left = tileBySidePos(leftSidePos);
            Tile top = tileBySidePos(topSidePos);

            Function<Tile, Tile> rot;
            if (leftSidePos != -1) {
                rot = rotationByLeftSide(leftSidePos);
            } else if (topSidePos != -1) {
                rot = rotationByTopSide(topSidePos);
            } else {
                throw new IllegalStateException();
            }

            Tile rotated = rot.apply(this);

            rotated.right = rotated.findRightTile();
            if (rotated.right == left) {
                rotated = flipVertically(rotated);
                rotated.right = rotated.findRightTile();
            }

            rotated.bottom = rotated.findBottomTile();
            if (rotated.bottom == top) {
                rotated = flipHorizontally(rotated);
                rotated.bottom = rotated.findBottomTile();
            }

//            System.out.println("left: " + (left != null ? left.id() : "n/a"));
//            System.out.println("top: " + (top != null ? top.id() : "n/a"));
//
//            if (rotated.right != null) {
//                System.out.println("right: " + rotated.right.id());
//            } else {
//                System.out.println("right: n/a");
//            }
//            if (rotated.bottom != null) {
//                System.out.println("bottom: " + rotated.bottom.id());
//            } else {
//                System.out.println("bottom: n/a");
//            }

            return rotated;
        }

        private Tile findRightTile() {
            Tile right = tileBySidePos(RIGHT);
            if (right == null) {
                right = tileBySidePos(RIGHT_REVERSED);
            }
            return right;
        }

        private Tile findBottomTile() {
            Tile botttom = tileBySidePos(BOTTOM);
            if (botttom == null) {
                botttom = tileBySidePos(BOTTOM_REVERSED);
            }
            return botttom;
        }

        private Function<Tile, Tile> rotationByLeftSide(int leftSidePos) {
            return switch (leftSidePos) {
                case LEFT -> Function.identity();
                case TOP -> rotateBack().andThen(horizontalFlip());
                case RIGHT -> verticalFlip();
                case BOTTOM -> horizontalFlip().andThen(rotateBack()).andThen(horizontalFlip());
                case LEFT_REVERSED -> horizontalFlip();
                case TOP_REVERSED -> rotateBack();
                case RIGHT_REVERSED -> verticalFlip().andThen(horizontalFlip());
                case BOTTOM_REVERSED -> horizontalFlip().andThen(rotateBack());
                default -> throw new IllegalStateException();
            };
        }

        private Function<Tile, Tile> rotationByTopSide(int topSidePos) {
            return switch (topSidePos) {
                case TOP -> Function.identity();
                case RIGHT -> rotateBack();
                case BOTTOM -> horizontalFlip();
                case LEFT -> verticalFlip().andThen(rotateBack());
                case TOP_REVERSED -> verticalFlip();
                case RIGHT_REVERSED -> horizontalFlip().andThen(rotateBack());
                case BOTTOM_REVERSED -> verticalFlip().andThen(horizontalFlip());
                case LEFT_REVERSED -> horizontalFlip().andThen(verticalFlip()).andThen(rotateBack());
                default -> throw new IllegalStateException();
            };
        }

        private Tile oppositeTile(int sidePos) {
            if (sidePos == -1) {
                return null;
            }
            int oppositePos = switch (sidePos) {
                case LEFT -> RIGHT;
                case TOP -> BOTTOM;
                case RIGHT -> LEFT;
                case BOTTOM -> TOP;
                case LEFT_REVERSED -> RIGHT_REVERSED;
                case TOP_REVERSED -> BOTTOM_REVERSED;
                case RIGHT_REVERSED -> LEFT_REVERSED;
                case BOTTOM_REVERSED -> TOP_REVERSED;
                default -> throw new IllegalStateException();
            };
            Long oppositeSide = sidesList.get(oppositePos);
            return findAttached(oppositeSide);
        }

        private Tile findAttached(Long side) {
            return attached.entrySet().stream()
                           .filter(entry -> side.equals(entry.getValue()))
                           .map(Map.Entry::getKey)
                           .findFirst()
                           .map(Tile::byId)
                           .orElse(null);
        }

        private Tile tileBySidePos(int tilePos) {
            if (tilePos < 0 || tilePos > 7) {
                return null;
            }
            Long tileSide = sidesList.get(tilePos);
            return attached.entrySet().stream()
                           .filter(entry -> entry.getValue().equals(tileSide))
                           .map(Map.Entry::getKey)
                           .map(Tile::byId)
                           .findFirst()
                           .orElse(null);
        }

        Tile removeBorder() {
            final String[] borderLessLines =
                    Stream.of(this.lines)
                          .skip(1).limit(this.lines.length - 2)
                          .map(line -> line.substring(1, line.length() - 1))
                          .toArray(String[]::new);

            return new Tile(this, borderLessLines);
        }

        @Override
        public String toString() {
            return "Tile "+id+":\n" + Stream.of(lines).collect(joining(System.lineSeparator()));
        }

        private static Function<Tile,Tile> rotateBack() {
            return Tile::rotateBack;
        }

        private static Function<Tile,Tile> verticalFlip() {
            return Tile::flipVertically;
        }

        private static Function<Tile,Tile> horizontalFlip() {
            return Tile::flipHorizontally;
        }

        private static Tile rotateBack(Tile tile) {
            final Tile rotated = new Tile(tile, rotateLinesBack(tile.lines));

            Long top = rotated.sidesList.get(TOP);
            Long bottom = rotated.sidesList.get(BOTTOM);
            Long topReversed = rotated.sidesList.get(TOP_REVERSED);
            Long bottomReversed = rotated.sidesList.get(BOTTOM_REVERSED);

            Long left = rotated.sidesList.get(LEFT);
            Long right = rotated.sidesList.get(RIGHT);
            Long leftReversed = rotated.sidesList.get(LEFT_REVERSED);
            Long rightReversed = rotated.sidesList.get(RIGHT_REVERSED);

            rotated.sidesList.set(TOP, right);
            rotated.sidesList.set(TOP_REVERSED, rightReversed);
            rotated.sidesList.set(BOTTOM, left);
            rotated.sidesList.set(BOTTOM_REVERSED, leftReversed);

            rotated.sidesList.set(RIGHT, bottomReversed);
            rotated.sidesList.set(RIGHT_REVERSED, bottom);
            rotated.sidesList.set(LEFT, topReversed);
            rotated.sidesList.set(LEFT_REVERSED, top);

            return rotated;
        }

        private static Tile flipVertically(Tile tile) {
            Tile flipped = new Tile(tile, flipLinesVertically(tile.lines));

            Long top = flipped.sidesList.get(TOP);
            Long topReversed = flipped.sidesList.get(TOP_REVERSED);
            flipped.sidesList.set(TOP, topReversed);
            flipped.sidesList.set(TOP_REVERSED, top);

            Long bottom = flipped.sidesList.get(BOTTOM);
            Long bottomReversed = flipped.sidesList.get(BOTTOM_REVERSED);
            flipped.sidesList.set(BOTTOM, bottomReversed);
            flipped.sidesList.set(BOTTOM_REVERSED, bottom);

            Long left = flipped.sidesList.get(LEFT);
            Long right = flipped.sidesList.get(RIGHT);
            flipped.sidesList.set(LEFT, right);
            flipped.sidesList.set(RIGHT, left);

            Long leftReversed = flipped.sidesList.get(LEFT_REVERSED);
            Long rightReversed = flipped.sidesList.get(RIGHT_REVERSED);
            flipped.sidesList.set(LEFT_REVERSED, rightReversed);
            flipped.sidesList.set(RIGHT_REVERSED, leftReversed);

            return flipped;
        }

        private static Tile flipHorizontally(Tile tile) {
            Tile flipped = new Tile(tile, flipLinesHorizontally(tile.lines));

            Long top = flipped.sidesList.get(TOP);
            Long bottom = flipped.sidesList.get(BOTTOM);
            flipped.sidesList.set(TOP, bottom);
            flipped.sidesList.set(BOTTOM, top);

            Long topReversed = flipped.sidesList.get(TOP_REVERSED);
            Long bottomReversed = flipped.sidesList.get(BOTTOM_REVERSED);
            flipped.sidesList.set(TOP_REVERSED, bottomReversed);
            flipped.sidesList.set(BOTTOM_REVERSED, topReversed);

            Long left = flipped.sidesList.get(LEFT);
            Long leftReversed = flipped.sidesList.get(LEFT_REVERSED);
            flipped.sidesList.set(LEFT, leftReversed);
            flipped.sidesList.set(LEFT_REVERSED, left);

            Long right = flipped.sidesList.get(RIGHT);
            Long rightReversed = flipped.sidesList.get(RIGHT_REVERSED);
            flipped.sidesList.set(RIGHT, rightReversed);
            flipped.sidesList.set(RIGHT_REVERSED, right);

            return flipped;
        }

        static Tile of(String[] lines) {
            final int space = lines[0].indexOf(' ');
            final int colon = lines[0].indexOf(':');
            long id = Long.parseLong(lines[0].substring(space + 1, colon));
            lines = Stream.of(lines).skip(1).toArray(String[]::new);
            String top = lines[0];
            String bottom = lines[lines.length - 1];
            String left = Stream.of(lines)
                                .map(line -> Character.toString(line.charAt(0)))
                                .collect(joining());
            String right = Stream.of(lines)
                                 .map(line -> Character.toString(line.charAt(top.length() - 1)))
                                 .collect(joining());
            return new Tile(id, lines, top, right, bottom, left);
        }

        static Tile byId(Long id) {
            return Tile.tilesMap.get(id);
        }
    }

    static String flip(String binary) {
        List<Character> chars = binary.chars().mapToObj(c -> (char) c).collect(toList());
        Collections.reverse(chars);
        return chars.stream().map(Object::toString).collect(joining());
    }

    static String[] rotateLinesBack(String[] lines) {
        final int length = lines.length;
        char[][] rotatedChars = new char[length][];
        for (int row = 0; row < length; row++) {
            rotatedChars[row] = new char[length];
        }
        for (int row = 0; row < length; row++) {
            for (int col = 0; col < length; col++) {
                rotatedChars[length - col - 1][row] = lines[row].charAt(col);
            }
        }
        return Stream.of(rotatedChars).map(String::new).toArray(String[]::new);
    }

    static String[] flipLinesVertically(String[] lines) {
        return Stream.of(lines).map(JurassicJigsaw::flip).toArray(String[]::new);
    }

    static String[] flipLinesHorizontally(String[] lines) {
        List<String> list = Arrays.asList(lines);
        Collections.reverse(list);
        return list.toArray(new String[0]);
    }

    private static final String INPUT = """
            Tile 2311:
            ..##.#..#.
            ##..#.....
            #...##..#.
            ####.#...#
            ##.##.###.
            ##...#.###
            .#.#.#..##
            ..#....#..
            ###...#.#.
            ..###..###
                        
            Tile 1951:
            #.##...##.
            #.####...#
            .....#..##
            #...######
            .##.#....#
            .###.#####
            ###.##.##.
            .###....#.
            ..#.#..#.#
            #...##.#..
                        
            Tile 1171:
            ####...##.
            #..##.#..#
            ##.#..#.#.
            .###.####.
            ..###.####
            .##....##.
            .#...####.
            #.##.####.
            ####..#...
            .....##...
                        
            Tile 1427:
            ###.##.#..
            .#..#.##..
            .#.##.#..#
            #.#.#.##.#
            ....#...##
            ...##..##.
            ...#.#####
            .#.####.#.
            ..#..###.#
            ..##.#..#.
                        
            Tile 1489:
            ##.#.#....
            ..##...#..
            .##..##...
            ..#...#...
            #####...#.
            #..#.#.#.#
            ...#.#.#..
            ##.#...##.
            ..##.##.##
            ###.##.#..
                        
            Tile 2473:
            #....####.
            #..#.##...
            #.##..#...
            ######.#.#
            .#...#.#.#
            .#########
            .###.#..#.
            ########.#
            ##...##.#.
            ..###.#.#.
                        
            Tile 2971:
            ..#.#....#
            #...###...
            #.#.###...
            ##.##..#..
            .#####..##
            .#..####.#
            #..#.#..#.
            ..####.###
            ..#.#.###.
            ...#.#.#.#
                        
            Tile 2729:
            ...#.#.#.#
            ####.#....
            ..#.#.....
            ....#..#.#
            .##..##.#.
            .#.####...
            ####.#.#..
            ##.####...
            ##..#.##..
            #.##...##.
                        
            Tile 3079:
            #.#.#####.
            .#..######
            ..#.......
            ######....
            ####.#..#.
            .#...#.##.
            #.#####.##
            ..#.###...
            ..#.......
            ..#.###...
            """;

    private static final String INPUT2 = """
            Tile 2647:
            #....#####
            .##......#
            ##......##
            .....#..#.
            .........#
            .....#..##
            #.#....#..
            #......#.#
            #....##..#
            ...##.....
                     
            Tile 1283:
            ######..#.
            #.#..#.#..
            ..#..#...#
            .#.##..#..
            #......#..
            #.#....##.
            .#.....#.#
            #.#..#.#.#
            .#......##
            ...##.....
                     
            Tile 3547:
            #.#.#.###.
            #.........
            #....##...
            #.....#..#
            #.....#.#.
            ##..##...#
            #...##....
            ......#..#
            #...##....
            .....###.#
                     
            Tile 1451:
            ##..#.#...
            #.#.......
            ##.#.....#
            ....#.....
            ...#...##.
            ......#.#.
            #...##.##.
            ........#.
            .#.##.#...
            ..##..#...
                     
            Tile 3137:
            ....#.##.#
            #....#...#
            ..#.#.....
            ...####..#
            .#.###...#
            .......#..
            ##.##.#..#
            .#.##....#
            #...#....#
            ..##.##..#
                     
            Tile 2897:
            ###..#.##.
            ..#......#
            .....#....
            ###.#....#
            #.#..#...#
            .#...##..#
            ##..##.##.
            #.....#..#
            .#......##
            #.#.#.##.#
                     
            Tile 1093:
            ..#.#.#.#.
            #.#.......
            ..##....#.
            .#.....#.#
            #........#
            .#....#..#
            ##....#..#
            #.##..#..#
            ..###...##
            .######.##
                     
            Tile 1217:
            #..#....##
            #.....#...
            ##...##..#
            #.....#...
            ..#.#..#..
            #..#....##
            .##.#.....
            ......#...
            .#........
            .#..###.#.
                     
            Tile 2801:
            ###..##.#.
            .........#
            ##.#...###
            #......#..
            #........#
            ......#...
            ##.####...
            .....##...
            ..#..#.##.
            ...###.##.
                     
            Tile 1361:
            ...#.##..#
            ....#.....
            ###.......
            #......#..
            .......##.
            #...#..#..
            #.....##.#
            ##........
            #.#.......
            ###.#..###
                     
            Tile 2063:
            ...#....##
            ##...#..##
            #........#
            ........##
            #.......##
            #.........
            ##.....##.
            .....##..#
            .#.##.#...
            .#..#####.
                     
            Tile 3797:
            ##..#...#.
            .###.#.##.
            .....#.##.
            ..#.......
            ...#.#....
            ........##
            #.#.#.##.#
            #.....#.##
            #.......#.
            .....#.##.
                     
            Tile 1289:
            ####.##.#.
            .....#....
            #..#.#....
            ####...#..
            #.#..#..#.
            .#.##..#..
            #........#
            ....#..#..
            ........#.
            ###.#.####
                     
            Tile 1427:
            ##.##..##.
            ###..#.##.
            #..##...#.
            #..#.#...#
            #........#
            #...##....
            #........#
            .....#..#.
            .####....#
            ##.#.##.#.
                     
            Tile 1951:
            ....##.#.#
            .........#
            #........#
            .#..#...#.
            .....#####
            #......#.#
            ...##....#
            ......#...
            ..#...#..#
            ....####.#
                     
            Tile 1483:
            ....####..
            .......#.#
            ###..#..##
            ...#.#...#
            #..##...##
            ##.#......
            #...#..#..
            ..#...#.##
            .........#
            .#...#....
                     
            Tile 1789:
            ##..#####.
            ....#....#
            ........#.
            ..#.#..#.#
            ..##.#..##
            .........#
            .........#
            #..#.#..##
            ....##....
            #.#.......
                     
            Tile 2129:
            #.###.#..#
            ....##...#
            .#..#..##.
            ...###.##.
            ..#..#...#
            ....##...#
            #.........
            #...#..###
            #...#.....
            ...#....##
                     
            Tile 2137:
            ..#.####.#
            ##...#.#..
            .......###
            .#.....#.#
            .#....##.#
            #.......#.
            #....#...#
            #.....####
            ......##.#
            ..#####.##
                     
            Tile 3761:
            .####.#...
            ####..#..#
            #...##..##
            .#.....#.#
            ....#....#
            #.......#.
            ...#..#..#
            #.##...##.
            ...###...#
            ...##.#..#
                     
            Tile 1327:
            ..####.#.#
            #..#......
            ......#.##
            #..##.....
            ..##.##..#
            #.#.#.....
            ####.....#
            ..#.......
            #.#...##..
            #.##....#.
                     
            Tile 2741:
            .#..#...#.
            #....#..#.
            ......##.#
            ....#.#..#
            ........##
            ...#..#...
            ......##..
            #...#..#.#
            ......##..
            ..#..#..#.
                     
            Tile 1699:
            .###..####
            ##.....#.#
            .....##.##
            #.#...##..
            .#........
            .#....#..#
            #..#....#.
            .#...#...#
            #.......#.
            ##.#..#..#
                     
            Tile 1151:
            ..#.##....
            ##....#...
            ###.#..#.#
            #.......##
            ....#.#..#
            #...###...
            .#..#.#..#
            #.#..##..#
            .#.#.#.#..
            .###..####
                     
            Tile 2273:
            #.#.#.#.##
            ..........
            #......#..
            #.....#...
            #.#...#...
            ##....##..
            ##..##.#..
            #.#####.##
            ##.##...##
            #...##..##
                     
            Tile 1999:
            ##.##...##
            #......#..
            ##..#.....
            #........#
            #.#...####
            ..#....#.#
            #..#...#..
            .........#
            #...##....
            ##.##.##..
                     
            Tile 1721:
            ....##...#
            ###.#....#
            .##..#....
            .#.#.#....
            ...##....#
            ##..#....#
            #....#.###
            #.....##..
            ....#...##
            ..#.#.#..#
                     
            Tile 2521:
            ..#######.
            #.#..##.#.
            .#....##.#
            ..#...####
            .......##.
            ##...###..
            ...##....#
            .##.#.....
            ###..##..#
            ####.##.#.
                     
            Tile 2111:
            ..#.#..#..
            ...#.....#
            ..####...#
            .#.#..##.#
            .##..#.##.
            ........##
            ........##
            #..#.#....
            ...#.###..
            .#.#...#..
                     
            Tile 2767:
            .#######..
            ##.......#
            #...#.##..
            ....#...##
            #........#
            ..#.###...
            ....#..#.#
            ##....#.##
            ..##....##
            .#####.#..
                     
            Tile 2141:
            ####.#....
            #..#.#...#
            ...#..#..#
            .......#..
            .....###.#
            #....#....
            .......#.#
            .#...#..##
            ...#......
            .###.####.
                     
            Tile 2557:
            .#.##..#..
            ..##.....#
            #.#.#....#
            ..##...#..
            ...#..##.#
            ..........
            ##......##
            #..#......
            #.#..#...#
            ##.#####..
                     
            Tile 2269:
            .#.#...##.
            #.......##
            #.....##..
            ##.#......
            #.##..###.
            .#.....##.
            ....#....#
            ....#...##
            #..##.....
            #.#.#.#.##
                     
            Tile 3511:
            .#.#.##...
            .#.....##.
            .#....#..#
            #.#......#
            #.#.#.....
            #........#
            ..#.......
            .##.#.....
            ##.#.....#
            ..####..##
                     
            Tile 2789:
            #......#..
            #...#.....
            #.........
            .......#.#
            ...#....##
            #.##..###.
            #...##...#
            .........#
            .........#
            .###..##..
                     
            Tile 2971:
            #.##.#....
            ...#.....#
            .#....#...
            #.#..##...
            #.....#...
            ####.....#
            #..###..##
            #....#....
            #..#.##...
            #.#..###..
                     
            Tile 3719:
            #.###.....
            ...#.....#
            ...##...##
            .#..#.#..#
            #..#.#..#.
            #.#..#..##
            #...###..#
            .#.#..#.##
            ........#.
            #....###..
                     
            Tile 1901:
            .#...##.##
            #.........
            .#.#.....#
            #.##.....#
            #........#
            #....#...#
            .....##.##
            ##.###..##
            ....#....#
            ....##..##
                     
            Tile 3191:
            #.#..###.#
            #...#..##.
            #.....#...
            .#.#.#....
            .#..##....
            #.....#.#.
            .##.......
            ....#....#
            #..##.#...
            ####....##
                     
            Tile 3709:
            ..#......#
            #..#...#.#
            #.##....#.
            .#..#.##..
            ..#......#
            #....##...
            ##........
            ....#....#
            .........#
            .#.#..###.
                     
            Tile 1613:
            ...##..##.
            #......#..
            ..##.#..##
            ......##..
            .#..#..##.
            .......##.
            .......#.#
            ...#.#....
            #......#.#
            ###..#....
                     
            Tile 2441:
            ..#.######
            #.#.......
            #..#.#....
            ....#...##
            #...#...##
            #.##...#.#
            ........##
            #.#...#...
            #..####.##
            #.##.####.
                     
            Tile 1409:
            ..####.#.#
            ..##....#.
            ..#.#...#.
            ..##.##...
            .#.##....#
            #.....##.#
            ####.....#
            ###....#..
            ####..#.#.
            #..##.##.#
                     
            Tile 1523:
            .#.##..##.
            #..#.#....
            ##.#.#...#
            ....#.##.#
            #........#
            #.#.......
            #...##...#
            ...#..##.#
            #.##...#..
            .####..#..
                     
            Tile 1367:
            #..#...#.#
            #.#.......
            ..#..#....
            .###..###.
            ###..#.##.
            ##...#..#.
            #..#...#.#
            ......##..
            ##.....#.#
            .#####..##
                     
            Tile 1783:
            ...#.####.
            .####..#..
            #....#.###
            #.#..#.#.#
            #.#.#.#..#
            #.......##
            #.##.#.#..
            .#.#....#.
            #..#.#...#
            .###..##.#
                     
            Tile 1871:
            .##..#.##.
            #........#
            #...#....#
            ##.#..##..
            ##.....##.
            #.....#.##
            ........##
            ....#....#
            #.........
            ....#.#..#
                     
            Tile 3217:
            #.#...#.##
            .........#
            .........#
            #...#.....
            #....#.#.#
            .........#
            ...#.##.##
            #...#.....
            .#..#....#
            #..###.#.#
                     
            Tile 3163:
            ...##.#.##
            #.#......#
            ....#...##
            #.......##
            ###..#.#..
            .#....####
            ##....#.##
            #.......#.
            .....#..#.
            .##.#.#.##
                     
            Tile 3271:
            ##.#.#.##.
            ##....##.#
            #.#.##..##
            #.#...##.#
            .##......#
            #.....#.#.
            #........#
            ##..##....
            #.#..##..#
            #..#.####.
                     
            Tile 2707:
            ..###.#...
            #...#.....
            #.#..#....
            #..##...##
            .###......
            .#..##...#
            #...#.....
            ....#.....
            #..#.#....
            .##....#.#
                     
            Tile 3083:
            ##..#.#.##
            #..#....##
            .........#
            ..#.#...##
            ..#.......
            .#.#.....#
            ..#..#.#..
            #...#.#..#
            #..#.#....
            #.###..##.
                     
            Tile 1051:
            ####...##.
            ...#.#...#
            ..........
            ..#.......
            #......#..
            .#.##.##..
            #....#.#.#
            #..#.#...#
            #.#..##..#
            ......###.
                     
            Tile 3767:
            .#..##.###
            ...#.#....
            ..#.....#.
            #.#.......
            .#.....#.#
            ##..#....#
            #...#..#.#
            ........##
            #........#
            ..#....##.
                     
            Tile 2267:
            .#..#..#..
            .#.#.#....
            .#......#.
            #...#....#
            .###..#...
            .##.#...##
            ..#.##.##.
            ...#.#.##.
            ##.#.##..#
            .#.##.....
                     
            Tile 1973:
            #.#####..#
            .#.......#
            #..#.#..#.
            #.#.#.#.#.
            .##.......
            #.#.....#.
            .#.......#
            #...##.#.#
            ##.......#
            .##...####
                     
            Tile 3671:
            #..##.#.##
            ....##...#
            .###.##...
            .........#
            #..#.....#
            ..##...#..
            ......#...
            ..#..#..##
            ..#.......
            ##..###..#
                     
            Tile 3221:
            #.#..###.#
            #..#....##
            #..#......
            #...#...##
            ..#..#..#.
            #..##...#.
            ...#....#.
            .....#..#.
            ##..#..#..
            .....#...#
                     
            Tile 1549:
            .###.##..#
            #.#.##...#
            #....#....
            ..........
            #.#......#
            ##.#.#..##
            ...#.#..##
            ........#.
            #.#....###
            #....#...#
                     
            Tile 3461:
            .######..#
            #.......##
            .......#..
            .#...#....
            ..##....#.
            #.....##..
            ##.#.#..#.
            .........#
            ##.##.#...
            ....#...##
                     
            Tile 2459:
            ..##.##.#.
            ...#..#...
            .........#
            #.#..#..##
            #.###.#...
            ##.#......
            .......#..
            .........#
            ........##
            #.##...#..
                     
            Tile 3203:
            .#...####.
            ..##..#.#.
            #..#..##..
            #.#....##.
            ...#.#....
            .......###
            #.....##..
            ....#....#
            #......#..
            ###.......
                     
            Tile 2203:
            #.#..##.##
            .......#..
            ......#.##
            #.......##
            #..##.##.#
            ..#.....##
            #.##.....#
            #.#....#..
            .##.....##
            ......#...
                     
            Tile 3637:
            #...###.#.
            #.........
            ..#.......
            ...#.....#
            #..##....#
            #........#
            .......#..
            #....#.#..
            #.#..##..#
            ..#.#..##.
                     
            Tile 2467:
            ..##.##...
            ##....####
            ...#.#.#.#
            #.##...#.#
            ...##.##..
            #.....#...
            ##........
            ..#...#.#.
            #...####.#
            #......###
                     
            Tile 2411:
            ...##....#
            ...##..###
            ...##.####
            #.#..##.#.
            ..##.#.###
            .#..#.###.
            ....####.#
            .....##.#.
            #.........
            .#..#..###
                     
            Tile 2221:
            ####.....#
            #.#.....##
            .#....#...
            .#.#......
            .##..#..#.
            ....#.....
            .........#
            ##.......#
            #....#....
            .##.######
                     
            Tile 1487:
            ..#..##...
            .........#
            #..#...###
            ....#...#.
            .#...##.#.
            .....#.#.#
            .....##...
            #.##......
            #.#.......
            #.#####.#.
                     
            Tile 1481:
            #.###.##..
            ....##...#
            ....#.....
            ...#......
            ##.###.#.#
            #.##..####
            ..#......#
            .#....##.#
            ..##.##.#.
            .#####.#.#
                     
            Tile 1669:
            #...##.##.
            ...#..#...
            .##..#.#.#
            #..#..#..#
            #......#.#
            .#......##
            ........#.
            ......#..#
            .##..#.#.#
            ##.##....#
                     
            Tile 3167:
            .#.####...
            .........#
            #......##.
            .....#....
            ..#.#...##
            #.#.####.#
            ...#....#.
            .........#
            #...#.#..#
            #.#.#.#.#.
                     
            Tile 3347:
            ###...##..
            #.#......#
            ...#.....#
            ..........
            #.#.....#.
            ..####..##
            ..#.#.#..#
            ##...#..#.
            ..##.....#
            #..#....#.
                     
            Tile 2213:
            #..#####.#
            ..........
            #..#.##.#.
            ...###.#.#
            ......##..
            ......#..#
            .##.....##
            ..#....###
            ...####..#
            .####.#.##
                     
            Tile 3329:
            ..##...#..
            #.#....#.#
            #...#..#..
            ......#.##
            #...####.#
            ..........
            ##....##.#
            #......##.
            ....##...#
            ..####.##.
                     
            Tile 3851:
            #.#....##.
            .........#
            #.....#...
            ##.##.....
            ...#.###..
            #....##...
            .....#.##.
            .#........
            #......#.#
            ...#..#..#
                     
            Tile 2659:
            #.#...#.#.
            .....#.##.
            #..##.####
            #.#.##....
            #....#..#.
            ...#...#..
            ...#....#.
            #....#.#..
            .##.#....#
            .....#..#.
                     
            Tile 1933:
            .####.##..
            #..####...
            .#..####..
            .#.#.##...
            ......#.#.
            ##........
            .#.#.....#
            #..#......
            ....#.....
            ...#...##.
                     
            Tile 3299:
            ###.##..#.
            .......#..
            ...#...##.
            ###...#.##
            ......##..
            ....#.#..#
            .###......
            .#.#####..
            #..#.#..#.
            .....#.#.#
                     
            Tile 3691:
            ...###...#
            #.........
            #.#.....##
            #.#....#..
            #..#...#..
            ..........
            ##...##..#
            .#...#...#
            #.....#.##
            .###..#...
                     
            Tile 3733:
            #..#.#####
            .....#....
            ....###..#
            #..#.#....
            #.#..#.###
            ..###...##
            ......#.##
            ...###....
            ...#....#.
            ..##......
                     
            Tile 2131:
            ##.#..#.#.
            .#...#..##
            #.......#.
            ....##...#
            .###..#...
            ...#####..
            .....#...#
            ##..#..##.
            ..##....#.
            .#...####.
                     
            Tile 1723:
            .....#####
            .#.#..#...
            ##......#.
            #.......##
            .###...#..
            #..#......
            #.........
            ......#..#
            .........#
            .###.##.##
                     
            Tile 3463:
            ##.#....##
            #....##..#
            ..#.#.....
            #.#...#..#
            #....#....
            ..#....#.#
            #...#..###
            ##....#.##
            ..#.#.....
            .#..#.##..
                     
            Tile 2549:
            #.####.#..
            ...##....#
            ##..#.##.#
            ..###.#..#
            #.#......#
            #........#
            ....#.....
            #......#.#
            #....####.
            ...##.#.##
                     
            Tile 1031:
            #..#.#.#.#
            ......##..
            #........#
            .###......
            ..#..#..#.
            ##....##..
            ......#...
            ...#...###
            .###...#..
            .##.#.###.
                     
            Tile 1979:
            #.######..
            .#.#.....#
            #........#
            #..##.....
            ##........
            ##.....#..
            ......#...
            .........#
            .#........
            ..#.#####.
                     
            Tile 2939:
            #.#...#.##
            .#..#....#
            .#.....#.#
            ##......##
            ...#..##..
            #....#.##.
            #...##.#.#
            ..#...#...
            ##.....#..
            .....##.#.
                     
            Tile 2381:
            ..##.###.#
            ..##...#..
            .#...#....
            #......#.#
            ##.......#
            #..####...
            ...#.#.#.#
            #.##.....#
            ..#......#
            #..#.##...
                     
            Tile 3943:
            #.#.###..#
            .......###
            #.#...###.
            #..##.#..#
            #......#..
            #.##...#.#
            #.........
            ##....##.#
            ....#.#...
            .###.#....
                     
            Tile 1553:
            #####.####
            #...#.....
            #.#.....#.
            ##......#.
            #....#.#..
            .#.....#.#
            ##....#.#.
            #........#
            .........#
            .#.....##.
                     
            Tile 2351:
            .###.###..
            #.....#...
            ##.##....#
            ..#..##.#.
            #.#.......
            #....#....
            ......##.#
            ##...##..#
            .#.....#..
            .#.###..#.
                     
            Tile 2311:
            #.#.#..##.
            #..###.#..
            ...##..#.#
            ###.......
            ##........
            #.#.......
            ..##.....#
            .#.####...
            ..#.#.#...
            ###..##.#.
                     
            Tile 1567:
            ..###.#.##
            .#.....###
            #...#..##.
            #.......#.
            .......#..
            #....#....
            ...#.##.#.
            ....#...##
            ....#....#
            #.#...##..
                     
            Tile 2579:
            #.##..##..
            #......#..
            #..#..#..#
            ##.......#
            ....##.#.#
            #.####..#.
            #..#..#.##
            #...#..#.#
            ...##...#.
            #..#.###..
                     
            Tile 3593:
            .#.##.#.##
            #...#....#
            ..........
            ##....#..#
            ##......##
            #.........
            ......#..#
            ...#.....#
            ....#....#
            ##..###..#
                     
            Tile 2281:
            ##....###.
            ...#......
            #......#.#
            ##.#..#..#
            ###.#..##.
            .#...#...#
            ..........
            .#.###.#..
            #..#......
            #..#.##.#.
                     
            Tile 1193:
            .......###
            ##..#..#..
            .###...###
            ....#.###.
            ..#...#..#
            #.#....#..
            ...####..#
            #....#..##
            .#.......#
            .#.#...##.
                     
            Tile 3833:
            ...#####..
            #..####...
            #.#....###
            ...##.#.##
            ..#...#..#
            .##.#####.
            #..#..#..#
            #...##....
            .....#.#..
            .##.##.#.#
                     
            Tile 2003:
            .#.###.#..
            .........#
            ..#..#....
            #.........
            #..##....#
            .......#.#
            ......#...
            #....##..#
            .#......##
            ..#..##.#.
                     
            Tile 2731:
            #.#..#..##
            ....#..#.#
            ..#...#...
            ..#..#....
            #.#..#...#
            #....##...
            #.........
            #..##..#.#
            #.........
            .###.#....
                     
            Tile 3881:
            ..##......
            #...#..#.#
            ##...#....
            ....#.....
            ##.......#
            .....#####
            ...#....##
            .........#
            ..........
            #..##.####
                     
            Tile 3673:
            ##..###.#.
            ...##....#
            ###.....##
            #..#...#.#
            #.##......
            ..#.#.....
            ..#.#....#
            .###.....#
            .###.##...
            ###.#..#.#
                     
            Tile 1021:
            #..###.#..
            ###..##.#.
            #..##....#
            .....###..
            ....##...#
            ....#.....
            #.##..#..#
            ..........
            .......#.#
            ..#.##..#.
                     
            Tile 2423:
            #.....####
            .##.#....#
            .#........
            ##.....#..
            #.....###.
            #...#...#.
            #...#..#.#
            .#..#..##.
            ##.......#
            .#####.###
                     
            Tile 3923:
            ..#....###
            #.....#..#
            #...#.#.#.
            .#.......#
            #..#.#....
            .......#.#
            ##....##.#
            .#..#...#.
            #...##..#.
            ..#.#.#..#
                     
            Tile 2753:
            ..####..#.
            #.......#.
            #.##.#..##
            #.#.#.....
            #..#......
            ....#.#...
            .#.#..#..#
            #.....#..#
            ##.#..#...
            #####....#
                     
            Tile 3929:
            ....#####.
            ##..#.##..
            ##.#.#.##.
            ##...#.#..
            #........#
            .##.#..#..
            #..#.##...
            ##..#...#.
            .....#...#
            ###..####.
                     
            Tile 3041:
            .##.#..#.#
            #..#...#..
            ###..#..#.
            .#.#....##
            ...##.....
            #....#..##
            #........#
            ##.#...#..
            ##....#..#
            ...#..#..#
                     
            Tile 3433:
            ..#.#.#...
            #.#.......
            .....#....
            ..#......#
            #..#.....#
            ........##
            ##..##.##.
            ##........
            #.#.##..##
            ###.###..#
                     
            Tile 2719:
            ..##..#..#
            #.##..##..
            #......#..
            #...##..##
            ..#..#.#.#
            #......###
            ..###..#..
            ....#.#..#
            ....##...#
            ##..#..###
                     
            Tile 1201:
            .#...##.##
            #........#
            ##...##...
            ..........
            .....#.#..
            #.##.....#
            ...#.##..#
            .........#
            .#.#.....#
            .##...#...
                     
            Tile 1129:
            ...####..#
            ......##..
            #.....##..
            #.......#.
            #......#..
            ...##....#
            ........##
            ##.#.#.#..
            ...#..##.#
            ...##....#
                     
            Tile 3019:
            ..#...###.
            .....#.##.
            #.##.....#
            .#.##..#..
            .#..###..#
            ..#.####.#
            #..#.#...#
            .......#.#
            #..##.#..#
            #.##....##
                     
            Tile 1747:
            ##.###.#..
            #.......#.
            #...#..#.#
            ##...##.#.
            ..###.#..#
            #..#..##..
            #...#.....
            ..#.......
            ...#..#.#.
            .##..##.##
                     
            Tile 1741:
            .##.#..#.#
            #...##..##
            #....#.#.#
            ##...##..#
            ##.......#
            #...#..##.
            ...#.##.##
            ...#..#.#.
            .......#.#
            .#####.###
                     
            Tile 1867:
            #..##.....
            .......###
            #..##....#
            ##...#....
            ...###....
            ##..#.....
            .##.......
            #.....###.
            #...#..#.#
            ...###....
                     
            Tile 2803:
            .#.##....#
            #.####..#.
            #.........
            #.#......#
            .......#.#
            ........#.
            ..#..#.#.#
            ....###...
            #...##....
            ...###....
                     
            Tile 3643:
            #..#..#.##
            ####.#..#.
            #.#...#.##
            .#..#.....
            ##....#..#
            .##.......
            .......#.#
            ...##.#...
            .....#.##.
            #...####.#
                     
            Tile 2437:
            ..###..###
            ....#.....
            ..........
            #.#..#.###
            ##...####.
            ....##....
            ...##.....
            ##..#.##..
            #......#..
            #.#.....#.
                     
            Tile 1069:
            ..####....
            ##..##...#
            .#..#..##.
            .#....##.#
            ###.#.#.##
            ...##..#.#
            ##....#...
            #.#....#.#
            .#.....#.#
            #.#.#.....
                     
            Tile 1381:
            .###.#.##.
            ....#..#..
            #.......##
            #...#.....
            .#...#..##
            ...#....##
            #..#.###..
            ..######.#
            #....#...#
            #######.#.
                     
            Tile 2617:
            ..##..#.#.
            #.....##.#
            ..#.#..#..
            .##.#..#..
            ###...#.#.
            .###.##...
            #.#.......
            #..##.#..#
            ##.....#..
            .##..#..##
                     
            Tile 2393:
            .##..#.#.#
            ..#.#..###
            ..##..#.##
            ....#.....
            #...#.....
            ##.#.....#
            .#.#..#.#.
            ##.....#..
            .......#.#
            ####..#...
                     
            Tile 3529:
            #.#...##.#
            ......#..#
            .........#
            #.....#...
            .......#..
            .....#.#.#
            .....#....
            #....#.#.#
            ....#.##.#
            .####.#..#
                     
            Tile 2953:
            ...##...#.
            ##.#.#..##
            #...#.....
            ##.#...###
            ...#......
            #.#.#..#.#
            .#...#...#
            ##....#.##
            .......#..
            .#.#..#...
                     
            Tile 3617:
            #..##...##
            ......#...
            #....#....
            ..........
            .######.##
            ##..#.#.##
            #.#...#...
            ........#.
            .######.##
            ##...###.#
                     
            Tile 3863:
            .##.#...##
            #...#.....
            ..#.#....#
            #....#..##
            .....###..
            #.#......#
            #.......#.
            ...#.....#
            #.........
            ..###....#
                     
            Tile 3727:
            #.###.##.#
            ..........
            ...##.....
            ..#..#..##
            #......###
            #....##...
            ###.##....
            .....#....
            ##.####.#.
            #..#.#.###
                     
            Tile 3803:
            ###..#.##.
            .##......#
            .........#
            ###.....##
            ....###..#
            .......#.#
            ........##
            #..#......
            ##......##
            #.###..#..
                     
            Tile 1579:
            #...##.###
            .....#.###
            .##...#...
            #.#..#..#.
            ..##.....#
            .........#
            ..........
            #.....#.##
            .....#....
            .###..#...
                     
            Tile 1049:
            #..#.##.##
            ##......##
            ..#.##...#
            #.......#.
            ###.....#.
            .....#.#.#
            ...#......
            ..##......
            #.#....#..
            ##..#.#...
                     
            Tile 2687:
            ##..#.##..
            .#........
            ##..#...#.
            .#.#.....#
            .#..#.#..#
            #.###..#..
            ..#......#
            #.......##
            #..#.....#
            #.##.#..##
                     
            Tile 1637:
            #..##...##
            ##..#....#
            ...#....#.
            #....#....
            .....#...#
            #...#...##
            .#....#...
            #.........
            ..#....#..
            .#.####...
                     
            Tile 3527:
            .#....#.#.
            #.......#.
            ..#....#.#
            ####.#.#.#
            ...#..#...
            ###..#.###
            ##..#....#
            #.##....##
            ..#......#
            .....#.#..
                     
            Tile 2963:
            #.#.#.#.#.
            #.....#...
            ##.#.....#
            ..##......
            ..#.......
            .#...#.##.
            ###......#
            ##....#..#
            .#...#..##
            ..##..##.#
                     
            Tile 2287:
            ##.######.
            .#.##.##..
            #..#....##
            ##.#.#...#
            .......##.
            #...##...#
            ...##..#..
            ##....#.#.
            ....#.##..
            ..#.#..###
                     
            Tile 3677:
            ###.....##
            #..#.#..#.
            #.#.......
            .....#..##
            ..........
            ......#.##
            .....#..#.
            #..#...#..
            .##......#
            #...##.##.
                     
            Tile 3559:
            ..#..#.##.
            ###......#
            ..#.##....
            #.#..#....
            ##..##..##
            ..#...#.#.
            #.....#.##
            ....#....#
            ...#.#...#
            ...#.###..
                     
            Tile 2837:
            ..#...#...
            .....##...
            #.#..#...#
            ....#....#
            ...####.##
            #.........
            ...#...##.
            .#..###.#.
            ....#.....
            .###.##.#.
                     
            Tile 3539:
            ..##....#.
            ........#.
            ......#..#
            ...#..#...
            ###....###
            #...#.....
            .#........
            #.....#...
            ..##.#..#.
            ..###..#.#
                     
            Tile 1667:
            .#..####..
            .....#....
            ......#...
            #.#...##.#
            #...#.#..#
            ##.#.#...#
            ##..#..#..
            #...##...#
            .#..###...
            ..#..####.
                     
            Tile 2791:
            #.##.###.#
            ...#..#...
            ##.....###
            ...#.#..##
            .........#
            .###...#..
            ...#.....#
            ##.....##.
            ###.......
            #..#.#....
                     
            Tile 2609:
            ..##.#....
            ##.#.#...#
            #.#..#....
            #.........
            ...#..#..#
            #...#.#...
            ##.##....#
            .###......
            ##.....##.
            #.#...#.#.
                     
            Tile 3061:
            ####..#.##
            #.....##..
            ..........
            ......#...
            ..#.#..###
            .#.#..#..#
            .#...#...#
            #........#
            .....#.#..
            #..#....##                                                     
            """;

    @Test
    public void testFlip() {
        assertEquals("10010", flip("01001"));
    }

    @Test
    public void testRotateBack() {
        String[] inputLines = {
                "001",
                "011",
                "101",
                };
        String[] expectedLines = {
                "111",
                "010",
                "001",
                };
        assertArrayEquals(expectedLines, rotateLinesBack(inputLines));
    }

    @Test
    public void testVerticalFlip() {
        String[] inputLines = {
                "001",
                "011",
                "101",
                };
        String[] expectedLines = {
                "100",
                "110",
                "101",
                };
        assertArrayEquals(expectedLines, flipLinesVertically(inputLines));
    }

    @Test
    public void testHorizontalFlip() {
        String[] inputLines = {
                "001",
                "011",
                "101",
                };
        String[] expectedLines = {
                "101",
                "011",
                "001",
                };
        assertArrayEquals(expectedLines, flipLinesHorizontally(inputLines));
    }

    @Test
    public void testMonstr() {
        assertTrue(MONSTER_LINE_2.matcher("########################").find(0));
        assertFalse(MONSTER_LINE_2.matcher("........................").find(0));

        assertTrue(MONSTER_LINE_2.matcher("#.##..#.##....##....####").find(0));
        assertTrue(MONSTER_LINE_2.matcher("#.##..#.##....##....####").find(1));
        assertTrue(MONSTER_LINE_2.matcher("#.##..#.##....##....####").find(2));
        assertTrue(MONSTER_LINE_2.matcher("#.##..#.##....##....####").find(3));
        assertFalse(MONSTER_LINE_2.matcher("#.##..#.##....##....####").find(4));

        assertFalse(MONSTER_LINE_2.matcher("#.#.....##..#.##....####").find(0));
        assertFalse(MONSTER_LINE_2.matcher("#.##..#.##....##.....###").find(0));

        final Matcher matcher = MONSTER_LINE_2.matcher("#.##..#.##....##....###");
        matcher.find(0);
        assertEquals(3, matcher.start());
    }
}