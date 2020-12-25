package done;

import org.junit.Test;
import util.LineSupplier;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.function.Predicate.not;

@SuppressWarnings("unused")
public class LobbyLayout {

    static final Map<Coord, Tile> tiles = new HashMap<>();

    static class Tile {
        private final Coord coord;
        private Color color;
        private long blackNeighbours;

        Tile(Coord coord) {
            this.coord = coord;
            this.color = Color.WHITE;
        }

        void addBlackNeighbour() {
            blackNeighbours++;
        }

        void reset() {
            blackNeighbours = 0;
        }

        void updateColor() {
            switch(color) {
                case WHITE -> updateWhite();
                case BLACK -> updateBlack();
            };
        }

        void findNeigbours() {
            blackNeighbours = neighhbours().filter(Tile::isBlack).count();
        }

        private void updateWhite() {
            if (blackNeighbours == 2) {
                color = Color.BLACK;
            }
        }

        private void updateBlack() {
            if (blackNeighbours < 1 || blackNeighbours > 2) {
                color = Color.WHITE;
            }
        }

        void flip() {
            color = (color == Color.WHITE)
                    ? Color.BLACK
                    : Color.WHITE;
        }

        boolean isBlack() {
            return color == Color.BLACK;
        }

        Stream<Tile> neighhbours() {
            return coord.adjacent().map(Tile::fromCoord);
        }

        static Tile fromCoord(Coord coord) {
            return tiles.computeIfAbsent(coord, c -> new Tile(coord));
        }
    }

    record Coord(int x, int y) {
        Stream<Coord> adjacent() {
            return Stream.of(
                    new Coord(x + 1, y), // e
                    new Coord(x - 1, y), // w
                    new Coord(x, y + 1), // ne
                    new Coord(x - 1, y + 1), // nw
                    new Coord(x, y - 1), // sw
                    new Coord(x + 1, y - 1)  // se
            );
        }

        static Coord fromDirections(String directions) {
            int x = 0;
            int y = 0;
            for (int i = 0; i < directions.length(); i++) {
                char c = directions.charAt(i);
                switch (c) {
                    case 'e' -> x++;
                    case 'w' -> x--;
                    case 'n' -> {
                        switch (directions.charAt(++i)) {
                            case 'e' -> y++;
                            case 'w' -> { y++; x--; }
                            default -> throw new IllegalStateException();
                        }
                    }
                    case 's' -> {
                        switch (directions.charAt(++i)) {
                            case 'e' -> { y--; x++; }
                            case 'w' -> y--;
                            default -> throw new IllegalStateException();
                        }
                    }
                    default -> throw new IllegalStateException();
                }
            }
            return new Coord(x, y);
        }
    }

    enum Color {
        BLACK,
        WHITE
    }

    static void flipTile(String directions) {
        Coord coord = Coord.fromDirections(directions);
        Tile.fromCoord(coord).flip();
    }

    public static void main(String[] args) {
        try (Scanner in = new Scanner(INPUT2)) {
            Stream.generate(new LineSupplier(in))
                  .takeWhile(Objects::nonNull)
                  .filter(not(String::isBlank))
                  .forEach(LobbyLayout::flipTile);

            IntStream.range(0, 100).forEach(round -> {
                // this adds all tiles adjacent to black
                new HashSet<>(tiles.values())
                        .stream()
                        .filter(Tile::isBlack)
                        .flatMap(Tile::neighhbours)
                        .forEach(Tile::reset);

                new HashSet<>(tiles.values()).forEach(Tile::findNeigbours);
                tiles.values().forEach(Tile::updateColor);
                tiles.values().forEach(Tile::reset);
            });

            long result = tiles.values().stream().filter(Tile::isBlack).count();

            System.out.println("Result: " + result);
        }
    }

    private static final String INPUT = """
                                        sesenwnenenewseeswwswswwnenewsewsw
                                        neeenesenwnwwswnenewnwwsewnenwseswesw
                                        seswneswswsenwwnwse
                                        nwnwneseeswswnenewneswwnewseswneseene
                                        swweswneswnenwsewnwneneseenw
                                        eesenwseswswnenwswnwnwsewwnwsene
                                        sewnenenenesenwsewnenwwwse
                                        wenwwweseeeweswwwnwwe
                                        wsweesenenewnwwnwsenewsenwwsesesenwne
                                        neeswseenwwswnwswswnw
                                        nenwswwsewswnenenewsenwsenwnesesenew
                                        enewnwewneswsewnwswenweswnenwsenwsw
                                        sweneswneswneneenwnewenewwneswswnese
                                        swwesenesewenwneswnwwneseswwne
                                        enesenwswwswneneswsenwnewswseenwsese
                                        wnwnesenesenenwwnenwsewesewsesesew
                                        nenewswnwewswnenesenwnesewesw
                                        eneswnwswnwsenenwnwnwwseeswneewsenese
                                        neswnwewnwnwseenwseesewsenwsweewe
                                        wseweeenwnesenwwwswnew            
                                        """;

    private static final String INPUT2 = """
                                         sweswseeeseseneeeeeeenwnweswe
                                         neswswsenwseeswsesesenwseseeseseseseswnw
                                         swswswnwwneeseneswseseneeswswseswswsenw
                                         eeswwnwnwwnwnwnwwnwswnwnwnwnweswnw
                                         sewwwnwwewswewswnewnwwnenesw
                                         swswswwnesesweeswewwnewnewswswsw
                                         nwswswnweneswswwwswswswswseswswwswsesw
                                         nweneweneswswsenwseswnwwsesewnwwe
                                         senwewseneswwswwsesesesenwseseesenee
                                         sesenwseseseneseeseswswswweesewwse
                                         swswsewswwneswseswneseswswseswswneswswsw
                                         swswnwwwswswnwswsweswswswswswnweswsese
                                         swnenwneeeneneswswnenewneswnwnenwneene
                                         swseswswswseneeseswswswwswwsesw
                                         swnwneenewweeenwenenenesenesenene
                                         seseeweeeweeseeeneeeweee
                                         enweseseseswseswsesesesesenewsenwsesese
                                         neseswsweswswseswswswwswswnwsw
                                         neneeswneswnenwneneneeewneswneenee
                                         nenwwnenwseneenenwnenwenwswneneswnenwenw
                                         seseseseseswseneswsesesese
                                         seeseewnwewwnwwswnwswnweeweww
                                         sweseseseswswseswsewsesesenwsene
                                         sesenwwesweseseeseseeneseneeesewse
                                         nwneneseswnenenenwne
                                         nweeeswenweeeswwenweswwneeese
                                         nwseswnesenwswswsewseswsenweneseeswswe
                                         swseswswneewwnwweswwsenwwwwsww
                                         seeseeweeeesweseneeeeenwsesw
                                         swswswsweswswnwswsw
                                         wwswswnwwswswwnwswneseseneseswnweesw
                                         nenenwnwnweswnenwwneswneseswnwswnwnee
                                         swswswswswswsweswswswswsww
                                         sesesesenwsewenwsesewsesesenesese
                                         seeneeeweswnenenwnwseeneseenwesw
                                         wnenwnwwsweneswsewsesenenenewswnwnw
                                         enwseswwnwseswswnewnwseseeseneswesew
                                         neneneneenenewneenenenenewne
                                         sesewewnweeeeeswe
                                         wwewswwwwwewwww
                                         wnesesesenewsenewsee
                                         seseseenwseweseswsesesesesesenewnwse
                                         swnesesewseswseswswswseswnwswsweswwsw
                                         wwneswswseseswwneswnewswswswewswsww
                                         swsewewnwnwswwesw
                                         neeeenweneseneneeene
                                         seseswnwwsewswwesesenwseswneeneenwnw
                                         eesweeeseneenwneneenewewnwsenw
                                         seseenwnwswswswswswneswseseswwsesenwsw
                                         eeneseeeseeseseseneseesewwswsese
                                         eeseseeswewnwenw
                                         wwwwswseswwwwswswnw
                                         swswneseswswwswswneswsewseswwswseswswswne
                                         eeneeneneeneeeswe
                                         wsesesewseseeesenweneseeswsesenese
                                         nenwenenenenewnenweenesesesenewnenee
                                         eseneeewenweeseweeswneeseneenenw
                                         wswwwwswwwwne
                                         nenwseseswswwsesesesesenwseeseswswsese
                                         nenwsenwnenwnwwnwnwnwnwsenenwnenesenwnw
                                         wseweneeswnwnwsweewswsesesesesese
                                         seseseseswsweswseswseswswswnwnwsese
                                         nwwswenwenenwswnene
                                         neweeweeeeeneeseeeeeswee
                                         nenewneweneneneneneneeeneswnenesenesw
                                         nesewnwwneswnwswneseneseenesweeswe
                                         nwseseneswsewseneswswewnenwseew
                                         swswnwwwwswswswwneseswwswewneww
                                         neeneeneseneesweeneweneenee
                                         seseswswswswswsesesesesenwsee
                                         wnewwwswwnwseneswsenewwewseew
                                         nenesewwnenewnenwsenwnenenwewsenwnenenw
                                         sesewnwnwwneswnwnewnwnwsenewww
                                         neeswsewnwnewwwsenwesew
                                         eeeeseeseeneweeseeewwnesenw
                                         swwswswwwswwnesene
                                         nenenenewwswnweeweswneneneneswnese
                                         neneewenenenenenenenenene
                                         swnwewnwewnwwnenwwnwwswnww
                                         swenwwswneseswswswswswswwneewswswse
                                         seeseeeesewseeee
                                         wswwswseswwswwswwswnenwnesenwenwsew
                                         wwwwwwnwwwesenewwwwewwnwsw
                                         eeeeenenweese
                                         nweeweeeneseneesweneswseswnwneee
                                         wsenenenwseenwnenewseswnwnesene
                                         wwsewwwenwweswnwwwnwwwswwnwne
                                         nwsewnenenenenenenwsenwneswneenenenenenw
                                         ewneswneneswenwwewneneewneseene
                                         swswwswswswnweswswswenwswseswswswwsw
                                         senweseseeswnenwsweweseesewse
                                         wswwnenwewwwwwnesewnwnwsewww
                                         nenwnenewseneneneneneneneseneeneswwnenene
                                         neswseswswswwnwswswwwneneswseswswsesw
                                         eneenenwneneneneneeeswnenene
                                         neseseswsesesesesesesewe
                                         eewnwenwseenenesenenwse
                                         swswswewwswswswswwnwsww
                                         wnenwsenwewnwnwnenwnwweeseswnwnwnesw
                                         wwwenwnwswseswswswseseenewnewswswww
                                         nwseneneeenewnenenenenenenewnenenese
                                         eeswseswswseswnwseseseswsww
                                         eneneenwneneeswnewnwsenwnwneswnenwnwne
                                         swweswswswswwswsweswswswnwnesewswswne
                                         nenwneenwwneneswnwne
                                         nenewwsewwwsesewewswnwnwwsewww
                                         wnwsesesesewwwswweswnwnwwnwnwsww
                                         eseeswseeeeneseeswseswnwseseenwnwse
                                         senwsewseseswseswneneseseseseseswswsesw
                                         nenwneswnenwnenenwnenenwsenenwsewnenewne
                                         sweseeneewewnesesesweweseeenee
                                         wwnesenwnwwwnwswnwnwsenenwsewwenw
                                         swwsewswwnewwwswswswswnwneswnewswse
                                         wnwenwswnenenwnwnwseneswnwenwswnwsenesw
                                         swseswseseseesewsesesesesese
                                         wnwseseeseseswesewsenesesesesesesesesw
                                         swswnwwnwswsenwneseseseeswsenwswwswne
                                         swenwwseeneneweweseswneeeseenenw
                                         swseswswneswswneewseswwswnwwseeseswsw
                                         nwnwsenwsenwsenewnwnwwwnwnewwnwswnw
                                         wswswwwnwwwwwwwwsewnwnewwe
                                         nesenenenenwnwnwnwnwnenesenwnewnwnewsw
                                         wneewwenwnwenesewsweswsweswsenw
                                         neswswwswseeswwnwswwnwseswnenweneesese
                                         eweneeneweseeeeeese
                                         seseseenweseseseseese
                                         esewneeswseswsenwneenwenwswwnenenw
                                         eneseswnewneneenwnweesweswneneesw
                                         nwnwnwnwwewswswewwnwwewnwnwwsw
                                         wwwwsewwwwwnewwwnw
                                         nwneneneneseneenewsewwsweweeseene
                                         eeewsewwwneswwnwneseweswswwwww
                                         nenenenenesewseneeneeneswnwnenwnenene
                                         senesenwnwsenwsenwnwenwwnwnwnewwnwnw
                                         swwseesesesesesenwseseeeseseseenwse
                                         wwneeswsesenenesenewwneseenenenew
                                         nwnwnenwnenewswnwswswneneeenwnwnwenwne
                                         neneeneeewewneneeeeewneneneswne
                                         nenwnewwnwneseenwswnwnwnwswnenenwnwne
                                         enwswsweenenenwwwseneeeneweesw
                                         wwnwwweswwwwnwewwwwwsenwnew
                                         nwneneswsenwswnwnenwnwnenwsenwsenwnwenenw
                                         weswnesenweseesenwse
                                         eweeeenenwweeswsweneeenwe
                                         wewwnewwwwwwwwwswwwwese
                                         nwwneenwnwnwnwesewnwnwwenwnesw
                                         swwnewswwwwseenewwwwswwwww
                                         eswnenesesenwesesewswwseseneeesee
                                         weneneneseswnwswneneneswnenwneeneenenee
                                         wnwnwnenwneenwnenenenene
                                         swswswswnwswswswewseswswseswsweeewnw
                                         nwnwesesenwseswseeseseneswseee
                                         swswswsesesewseesesesese
                                         seswneswneesenwnwneswesesww
                                         nwwswswwwswwwsewwnwewsw
                                         swnwswswwswewnwswwnweswneswswwwswse
                                         newesesenesesesesesenwswsesewsesesesenw
                                         wswnwswswenwswswwwswswswse
                                         swswswneswswsesewsese
                                         swsewswwnenewwwswneswnwswenwnwne
                                         eseswnwnwenwswesesweewwnwenwsesese
                                         wwnwnwweswsewwwnwnwnwwne
                                         swswsenwswswenwswwnewwsew
                                         seeenwwnwwnwwnwweswwnwnwnenwnwnw
                                         eeneswseneneneeweeew
                                         enenwsenenwnenwnenwswnwnwwnwnweswnwnwnw
                                         enwneeeneeeswnwneeneneneseene
                                         wnwewwswseswnwwnwswneseeweewsw
                                         eneneeswswneeewnwnweeeseeneee
                                         newnwnwenwnwnwnwnwnenwnesenenwne
                                         wswwewswswswswwswsw
                                         swnwnwnewwswwwsenewwnwewnwwsewnw
                                         nweeeeenwnweeeewseenwswseesew
                                         swesenwswswwnweeneeseeeeeeesee
                                         nenenenwnwwnwnenesenwnenwnenenwwe
                                         eeeneeneweseeeeeswnewnweeswe
                                         nesewwwweswswwwswnenwwwwwesww
                                         nesweesweenweneeeswnweneeeeesw
                                         seswseeeweseesenweneweneeswnesw
                                         sesweswswswwseneeswnwwwsenwswnwwswsw
                                         neneneenwnewnwseesewseneneeeeseew
                                         seeswsesenwnwseseeseseseesenweenewse
                                         ewswwseseswswswsene
                                         nwnwnwnwwwsenwnwnwsewwnwnwnwsenenwnw
                                         nwenweseenwwnwnewsweswnwwseswwnwne
                                         wswswseswneswnwwneswnewwwsewswsw
                                         neseswsewswswswsenenwsesesweswsweseswsw
                                         nenwsewnwwnenwnenesesenwneswwnesenwwne
                                         swwweswswwwnewwwwnwswwwwesw
                                         nesenewsenwnweneewswnwwswwsenwseseswne
                                         swwnewwwwnwwneswwsewwswwsesew
                                         neneneneeseneewswneneseewneswnewsw
                                         nwnenenwnenwswnwnwnwswswnwnenwnwwsesee
                                         swwseswswswswwswnesw
                                         enenwneeeweseseeseeswsewsewneew
                                         wwwnwnwwwwnwwseenenenwwswwwsew
                                         swseswsenwswswswswnwswswswnwneeswsesese
                                         nwnenwsenwswenwnenwnwswnwnwnwnwseswnewnw
                                         swwwwnewswnesewwnesewswwwseew
                                         ewnwnenwnwnwnwnwnwnw
                                         seneneswwnewnwnwsewnwnwnwseenwwwsee
                                         seeseseenwseseenwseesenwesesenwswsese
                                         enewnwewseswnesewwwwnwswnwnwwne
                                         swswwswneswsweswnw
                                         nenenewswneswenenwswneeeseewneneee
                                         nwwewnwwnenenwnwnwsewnenwswsenwnwsw
                                         nwwnwnenenwwsewenwswswnweswswsewnwe
                                         nwnwswnwnwnwnwnwnwenwwseswwwwnenwwnwe
                                         nwnesenweesewenwweseeseeeswwsw
                                         wnewwwnwnwwwwsewwewsewwnewse
                                         newseneenweseswesweeesweeesew
                                         enwswneeesweswsenwseneseeseswnewse
                                         eeseswwseneseseeeswnweeneseesee
                                         sewswswwsesweswnwesenwnwswswsweswsw
                                         nesenenenesenwnenwneeweneneeswneee
                                         nwnweewnwnwnwnwnwnwnwnwnesewnwnwswnw
                                         nwenwswswnwnenwnwnwwsenwnwnwnwenwnwnwse
                                         seswswswswwsweswnewsweseseswnw
                                         swseswwsesesenesesweswseseswnwwswsesw
                                         wnweeeseseeeeenwneeweeseeee
                                         eenwswnwwnwweenwwneswnweswwnwsw
                                         wnewswewwswsenwsenwwwsewswnwwenew
                                         swswneeneneneenenwnenenenesenenenwnene
                                         newnwneneesenewswsewnwneseneeswsew
                                         wnwewseseswwenwnenwwnwwswswwwnwnwe
                                         enwnenenewneneneneneneneneneswswwnese
                                         nenwswseseseswswesenwseswsene
                                         nwnenwnwnwwneseneenenenesweeseneeswnese
                                         nwnwswnenenwnwnwnenwnwnwnwnwnw
                                         swswnwswswswswseswswneesenwswsweswwswesw
                                         nwswnesewseseneneseswsesesewswneswsesw
                                         nwnenwesenenesenwnwnenewnwwnwnwnwnwwnw
                                         seeneneneneneenwnee
                                         newsweeseesesesewnesesenwsesenesesese
                                         eenewnwswnwesewnwwswnwswnwwswnwnwe
                                         wewsewwwwsewnwnwnewwsewwswnw
                                         swnewnwwnwswwwswwwwswewwseswwne
                                         nwswnenwsewnwseswwwneweweeswwww
                                         wnwnwenwnwsenwnwsewnewnwwnwnwnwenwnenw
                                         swnesenwenwneswswneswsesewseseeseesene
                                         nwnwnwnwewsenwswnwenwnwwenwnenw
                                         ewneewseenwswneswenwsenwewwe
                                         senwesenenwsewseseseseseseseswesesenw
                                         nesenewwnweneneneneneneesenewnwneswne
                                         neneeeswwneeeswswenenwwnewwee
                                         weseeseseeneeeneesw
                                         nwnwnwnenwswsenwneswenwnwsewnwneneswnenenw
                                         wneweenwenesweneeweenenenewse
                                         sweenwswnweneneseneeseseweswseseese
                                         swnwnesewwwseewnewwwwwwwww
                                         eenenenweswwnwwnw
                                         swswnwsweswneneeswswswswswswswsenwnewse
                                         wnenwewwewseewswswnwnesenwnwnwene
                                         seswseseswnwseseswseswswsw
                                         nwnwnwnwswwenwwnwwwswewwewnwnw
                                         nesewsewneswwwsenewsenewnwwsw
                                         nweneeeewneseseweneswseseswnesewse
                                         wswswwwwsenenenewwswsewwwnw
                                         eenwwwswnwwwwnwenwwewnwswswwnw
                                         swsenwnwsesenwseswseseseneswseneswwese
                                         sewseseseswseeneseseswsesewsesenewse
                                         swswswswswswswswswswwswseneneeswwswsw
                                         nenenenwneneeseeseneenewnenewneewsw
                                         nwsenwnwnenenewnwswnwnwswnwwnwnwswnwwe
                                         wwwnenwsenwnwnenwsewwnwsenenw
                                         neeswwweseseenwsenwseseesesenwwne
                                         esesewseseneneeseseenwseswswseeeee
                                         neneneneswseneneweneswnenenw
                                         nweswswwseswwseeneseseseswewswswswnw
                                         swswwwnwswnewwnewwwwwwswwswe
                                         enwwnwwnwwwnwwnwenw
                                         sewwnwnwwewswwwswswnwwseswswswsw
                                         swswseswnwnwswseswnwsenwswwswwswswesww
                                         weewwwsewwnewsewnewnwwwww
                                         nweswsenwswneseewnenwswwewnwnwnwnwenw
                                         eeewnweeneseswsweswseenenweeswe
                                         swswswwwswnenwsweswseswswnewneswswsw
                                         swneswswseswsenwseswnewsesenesenwswsesw
                                         nesewnesweswnenew
                                         nenwsesenenenwweenesweseswneneneew
                                         eeswnweneswseesenwwese
                                         esewwseswnwneeseswnwswnwesenwnwnwnww
                                         nwnwwnwwwwwnwewnwnw
                                         nwnwwesweenenenwnweswnwwnwenesweswne
                                         nwwswesewnwwenwswewneseseeesesenw
                                         swwseenwnwsewsewseneneww
                                         neswneswenwwneneneenwnwnenesewne
                                         neeneenweweeneneseseswnwsweeee
                                         wswnwswwswswnenwswswwwswesewswnese
                                         nwwnwwsewnweswsesesweenenwswnwnee
                                         sweeneneneneneneswnweneneswnesenwwnee
                                         eeswnwsweenenesenenwwnwneswseneese
                                         swwnwnwswseeseswswseseseswneneswsewsesene
                                         swnwwseneswnenenenewsenewneneneenenesw
                                         sewwseseseseseneewseeseenwseenenesee
                                         eeenwwweneneeeeswnesweeeenwne
                                         seseseeeneseseseswe
                                         nwnenwsewnwsenwnwnwnwwnw
                                         nwswnenwswneseneswnwnwesenwnenewnenene
                                         swseswnenwseeseswsewswswsewseneseswnwse
                                         wswwswnwnewseweneswenwswswswswnese
                                         wsenwnwnwnwnweewnwnwnwnwseswe
                                         eenewneseeeneeeseenwnenewneneeswe
                                         neswnwnwswnwswenenwnewnenenenenenwenene
                                         sewsewsesenwesesweseswswsewseneew
                                         swwswseswneeweswswseswnwwewnwswwne
                                         eenesweeeeenenwnwseswsesewnwsesw
                                         wwwnwsewnwwnwsenwnwwewwwnewenwe
                                         nwwnwneneswnwnwnwnwnwwswnenwwnwsenesesenw
                                         swseewseseseseseneseswsesesenese
                                         newnwsenwnwswswenwnenwnwsewswnwnwnwwnw
                                         eeneeweeneeeseeewenweswesw
                                         wesewwswnweseneseneweswwwwneww
                                         wnenesenwnwneseneenwese
                                         nenenenwnewswnenwnwswneseswswneswnwseesw
                                         swswseswswnwseswswswswnesese
                                         seswswnwseseseswswseseswseswsw
                                         neswsesewnwswwnweneseseswenwnwswe
                                         nwsesweswnesewnwswsenenenwswneseswswswsw
                                         swnwneneneswswnenenwwsenwnwnwse
                                         swswseseseneswneswsesesenwseneneswwsese
                                         nenwenenwnwsenwneewnwswenenwwnenew
                                         eewnwseeneeeeweesewseneswee
                                         seswseswsenwseseesenwsewseeseseseee
                                         senwwnwnwnwnenwnwwwnww
                                         wneeswnwswseswswnwwewsw
                                         nwsweneswneeeneenesenwneneeswnw
                                         esweenwnenenwweneneneseneeneneeesw
                                         seeewseeseseeseenwwseesesweese
                                         swswsewswseeswnwnweswwswnenwswswswwsw
                                         swnwneneneeneenenenewenenene
                                         nwsenwnwwsenwsenenenwenwwneneswseneew
                                         enwneswswnesenewswwe
                                         sesesenwnwswsesenewsesenwenwseseseseswe
                                         nwseneswneswnwneneeneeswnenenwnenwese
                                         seenesesenwnwwswnewswwwnwwwneswne
                                         swseswnwnwswswswswswswseswswswswneswenwsw
                                         senwnweenwesweseeeeeeeeewese
                                         swwseneswwswwnewwneeseswenesewsene
                                         eseneweenenenenenwneenenwneneswneneswne
                                         nwnesenwseeeneeesewnwweswweesw
                                         neseseseseseewwsesee
                                         swnwseswswswneeswswswswswswwswnwneswsw
                                         wnwwnwsenenesesewnenwswneesw
                                         """;

    @Test
    public void test() {

    }
}