package done.advent2021;

import org.junit.jupiter.api.Test;
import util.LineSupplier;

import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.function.ToIntFunction;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

public class ReactorReboot {

    static Cuboid[] cuboids;
    static List<Cuboid> active = emptyList();

    static final Cuboid BORDERS = new Cuboid(new Point(-50,-50,-50), new Point(50,50,50), true);

    public static void main(String[] args) {
        try (Scanner in = new Scanner(INPUT)) {
            System.out.println();

            cuboids = Stream.generate(new LineSupplier(in))
                    .takeWhile(Objects::nonNull)
                    .map(Cuboid::from)
                    .map(Cuboid::normalize)
//                    .flatMap(BORDERS::intersect)
                    .peek(System.out::println)
                    .toArray(Cuboid[]::new);

            Stream.of(cuboids).forEach(Cuboids::switchOnOff);

            System.out.println();
            active.forEach(System.out::println);

            long result = active.stream().mapToLong(Cuboid::size).sum();
            System.out.println("Result: " + result);
        }
    }

    record Cuboids() {
        static void switchOnOff(Cuboid cuboid) {
            if (cuboid.on()) {
                switchOn(cuboid);
            } else {
                switchOff(cuboid);
            }
        }

        static void switchOff(Cuboid cuboid) {
            active = subtract(cuboid).toList();
        }

        static void switchOn(Cuboid cuboid) {
            if (active.isEmpty()) {
                active = singletonList(cuboid);
            } else {
                active = Stream.concat(Stream.of(cuboid), subtract(cuboid)).toList();
            }
        }

        private static Stream<Cuboid> subtract(Cuboid cuboid) {
            return active.stream().flatMap(c -> c.subtract(cuboid));
        }
    }

    record Edge(int min, int max, boolean intersect) {}

    record Cuboid(Point min, Point max, boolean on) {
        Cuboid normalize() {
            if (min.x > max.x) {
                return new Cuboid(
                        new Point(max.x, min.y, min.z),
                        new Point(min.x, max.y, max.z),
                        on).normalize();
            }
            if (min.y > max.y) {
                return new Cuboid(
                        new Point(min.x, max.y, min.z),
                        new Point(max.x, min.y, max.z),
                        on).normalize();
            }
            if (min.z > max.z) {
                return new Cuboid(
                        new Point(min.x, min.y, max.z),
                        new Point(max.x, max.y, min.z),
                        on).normalize();
            }
            return this;
        }

        Stream<Cuboid> intersect(Cuboid c) {
            if (!c.on) {
                return Stream.of(c);
            }
            if (xOutside(c) || yOutside(c) || zOutside(c)) {
                return Stream.empty();
            }
            return xEdges(c).flatMap(x ->
                    yEdges(c).flatMap(y ->
                            zEdges(c).flatMap(z -> createIntersect(x, y, z))));
        }

        Stream<Cuboid> subtract(Cuboid c) {
            if (xOutside(c) || yOutside(c) || zOutside(c)) {
                return Stream.of(this);
            }
            return xEdges(c).flatMap(x ->
                    yEdges(c).flatMap(y ->
                            zEdges(c).flatMap(z -> createSubtract(x, y, z))));
        }

        long size() {
            return (max.x - min.x + 1L) * (max.y - min.y + 1L) * (max.z - min.z + 1L);
        }

        boolean xOutside(Cuboid c) {
            return max.x < c.min.x || c.max.x < min.x;
        }

        boolean yOutside(Cuboid c) {
            return max.y < c.min.y || c.max.y < min.y;
        }

        boolean zOutside(Cuboid c) {
            return max.z < c.min.z || c.max.z < min.z;
        }

        Stream<Edge> xEdges(Cuboid c) {
            return edges(c, Point::x);
        }

        Stream<Edge> yEdges(Cuboid c) {
            return edges(c, Point::y);
        }

        Stream<Edge> zEdges(Cuboid c) {
            return edges(c, Point::z);
        }

        private Stream<Edge> edges(Cuboid c, ToIntFunction<Point> f) {
            int min = f.applyAsInt(this.min);
            int max = f.applyAsInt(this.max);
            int cmin = f.applyAsInt(c.min);
            int cmax = f.applyAsInt(c.max);

            Edge left = null;
            Edge middle = null;
            Edge right = null;

            if (min <= cmin && cmax <= max) {
                if (min < cmin) {
                    left = new Edge(min, cmin - 1, false);
                }
                if (cmax < max) {
                    right = new Edge(cmax + 1, max, false);
                }
                middle = new Edge(cmin, cmax, true);
            } else if (min <= cmin && cmin <= max) {
                if (min < cmin) {
                    left = new Edge(min, cmin - 1, false);
                }
                middle = new Edge(cmin, max, true);
            } else if (cmin <= min && max <= cmax) {
                middle = new Edge(min, max, true);
            } else if (cmin <= min && min <= cmax) {
                middle = new Edge(min, cmax, true);
                if (cmax < max) {
                    right = new Edge(cmax + 1, max, false);
                }
            }

            return Stream.of(left, middle, right).filter(Objects::nonNull);
        }

        static Cuboid from(String line) {
            int spaceIdx = line.indexOf(' ');
            String[] parts = line.substring(spaceIdx + 1).split("[,]");
            String[] xParts = interval(parts[0]);
            String[] yParts = interval(parts[1]);
            String[] zParts = interval(parts[2]);
            Point min = Point.create(xParts[0], yParts[0], zParts[0]);
            Point max = Point.create(xParts[1], yParts[1], zParts[1]);
            return new Cuboid(min, max, line.startsWith("on"));
        }

        static Stream<Cuboid> createIntersect(Edge xEdge, Edge yEdge, Edge zEdge) {
            if (xEdge.intersect && yEdge.intersect && zEdge.intersect) {
                return Stream.of(new Cuboid(min(xEdge, yEdge, zEdge), max(xEdge, yEdge, zEdge), true));
            }
            return Stream.empty();
        }

        static Stream<Cuboid> createSubtract(Edge xEdge, Edge yEdge, Edge zEdge) {
            if (xEdge.intersect && yEdge.intersect && zEdge.intersect) {
                return Stream.empty();
            }
            return Stream.of(new Cuboid(min(xEdge, yEdge, zEdge), max(xEdge, yEdge, zEdge), true));
        }

        static Point min(Edge xEdge, Edge yEdge, Edge zEdge) {
            return new Point(xEdge.min, yEdge.min, zEdge.min);
        }

        static Point max(Edge xEdge, Edge yEdge, Edge zEdge) {
            return new Point(xEdge.max, yEdge.max, zEdge.max);
        }

        static String[] interval(String part) {
            return part.substring(2).split("[.][.]");
        }
    }

    record Point(int x, int y, int z) {
        static Point create(String x, String y, String z) {
            return new Point(Integer.parseInt(x), Integer.parseInt(y), Integer.parseInt(z));
        }
    }

    @SuppressWarnings("unused")
    private static final String INPUT1 = """
            on x=10..12,y=10..12,z=10..12
            on x=11..13,y=11..13,z=11..13
            off x=9..11,y=9..11,z=9..11
            on x=10..10,y=10..10,z=10..10""";

    @SuppressWarnings("unused")
    private static final String INPUT2 = """
            on x=-20..26,y=-36..17,z=-47..7
            on x=-20..33,y=-21..23,z=-26..28
            on x=-22..28,y=-29..23,z=-38..16
            on x=-46..7,y=-6..46,z=-50..-1
            on x=-49..1,y=-3..46,z=-24..28
            on x=2..47,y=-22..22,z=-23..27
            on x=-27..23,y=-28..26,z=-21..29
            on x=-39..5,y=-6..47,z=-3..44
            on x=-30..21,y=-8..43,z=-13..34
            on x=-22..26,y=-27..20,z=-29..19
            off x=-48..-32,y=26..41,z=-47..-37
            on x=-12..35,y=6..50,z=-50..-2
            off x=-48..-32,y=-32..-16,z=-15..-5
            on x=-18..26,y=-33..15,z=-7..46
            off x=-40..-22,y=-38..-28,z=23..41
            on x=-16..35,y=-41..10,z=-47..6
            off x=-32..-23,y=11..30,z=-14..3
            on x=-49..-5,y=-3..45,z=-29..18
            off x=18..30,y=-20..-8,z=-3..13
            on x=-41..9,y=-7..43,z=-33..15
            on x=-54112..-39298,y=-85059..-49293,z=-27449..7877
            on x=967..23432,y=45373..81175,z=27513..53682""";

    @SuppressWarnings("unused")
    private static final String INPUT3 = """
            on x=-5..47,y=-31..22,z=-19..33
            on x=-44..5,y=-27..21,z=-14..35
            on x=-49..-1,y=-11..42,z=-10..38
            on x=-20..34,y=-40..6,z=-44..1
            off x=26..39,y=40..50,z=-2..11
            on x=-41..5,y=-41..6,z=-36..8
            off x=-43..-33,y=-45..-28,z=7..25
            on x=-33..15,y=-32..19,z=-34..11
            off x=35..47,y=-46..-34,z=-11..5
            on x=-14..36,y=-6..44,z=-16..29
            on x=-57795..-6158,y=29564..72030,z=20435..90618
            on x=36731..105352,y=-21140..28532,z=16094..90401
            on x=30999..107136,y=-53464..15513,z=8553..71215
            on x=13528..83982,y=-99403..-27377,z=-24141..23996
            on x=-72682..-12347,y=18159..111354,z=7391..80950
            on x=-1060..80757,y=-65301..-20884,z=-103788..-16709
            on x=-83015..-9461,y=-72160..-8347,z=-81239..-26856
            on x=-52752..22273,y=-49450..9096,z=54442..119054
            on x=-29982..40483,y=-108474..-28371,z=-24328..38471
            on x=-4958..62750,y=40422..118853,z=-7672..65583
            on x=55694..108686,y=-43367..46958,z=-26781..48729
            on x=-98497..-18186,y=-63569..3412,z=1232..88485
            on x=-726..56291,y=-62629..13224,z=18033..85226
            on x=-110886..-34664,y=-81338..-8658,z=8914..63723
            on x=-55829..24974,y=-16897..54165,z=-121762..-28058
            on x=-65152..-11147,y=22489..91432,z=-58782..1780
            on x=-120100..-32970,y=-46592..27473,z=-11695..61039
            on x=-18631..37533,y=-124565..-50804,z=-35667..28308
            on x=-57817..18248,y=49321..117703,z=5745..55881
            on x=14781..98692,y=-1341..70827,z=15753..70151
            on x=-34419..55919,y=-19626..40991,z=39015..114138
            on x=-60785..11593,y=-56135..2999,z=-95368..-26915
            on x=-32178..58085,y=17647..101866,z=-91405..-8878
            on x=-53655..12091,y=50097..105568,z=-75335..-4862
            on x=-111166..-40997,y=-71714..2688,z=5609..50954
            on x=-16602..70118,y=-98693..-44401,z=5197..76897
            on x=16383..101554,y=4615..83635,z=-44907..18747
            off x=-95822..-15171,y=-19987..48940,z=10804..104439
            on x=-89813..-14614,y=16069..88491,z=-3297..45228
            on x=41075..99376,y=-20427..49978,z=-52012..13762
            on x=-21330..50085,y=-17944..62733,z=-112280..-30197
            on x=-16478..35915,y=36008..118594,z=-7885..47086
            off x=-98156..-27851,y=-49952..43171,z=-99005..-8456
            off x=2032..69770,y=-71013..4824,z=7471..94418
            on x=43670..120875,y=-42068..12382,z=-24787..38892
            off x=37514..111226,y=-45862..25743,z=-16714..54663
            off x=25699..97951,y=-30668..59918,z=-15349..69697
            off x=-44271..17935,y=-9516..60759,z=49131..112598
            on x=-61695..-5813,y=40978..94975,z=8655..80240
            off x=-101086..-9439,y=-7088..67543,z=33935..83858
            off x=18020..114017,y=-48931..32606,z=21474..89843
            off x=-77139..10506,y=-89994..-18797,z=-80..59318
            off x=8476..79288,y=-75520..11602,z=-96624..-24783
            on x=-47488..-1262,y=24338..100707,z=16292..72967
            off x=-84341..13987,y=2429..92914,z=-90671..-1318
            off x=-37810..49457,y=-71013..-7894,z=-105357..-13188
            off x=-27365..46395,y=31009..98017,z=15428..76570
            off x=-70369..-16548,y=22648..78696,z=-1892..86821
            on x=-53470..21291,y=-120233..-33476,z=-44150..38147
            off x=-93533..-4276,y=-16170..68771,z=-104985..-24507""";

    @SuppressWarnings("unused")
    private static final String INPUT = """
            on x=-28..25,y=-34..15,z=-36..13
            on x=-9..36,y=-2..43,z=-47..5
            on x=-27..26,y=-7..41,z=-8..46
            on x=-28..26,y=-11..39,z=-32..17
            on x=-13..41,y=-30..24,z=-47..1
            on x=-4..45,y=-48..-3,z=-39..13
            on x=-49..-3,y=-29..20,z=-39..7
            on x=-29..20,y=2..46,z=-31..15
            on x=-10..34,y=-12..41,z=-15..32
            on x=-12..41,y=-6..42,z=-22..28
            off x=-8..9,y=-31..-21,z=-30..-11
            on x=1..47,y=-30..16,z=-9..38
            off x=-34..-16,y=-48..-31,z=9..25
            on x=-29..19,y=-38..15,z=-34..20
            off x=-3..12,y=25..34,z=-43..-32
            on x=-36..17,y=-23..31,z=-29..16
            off x=14..27,y=-30..-21,z=3..21
            on x=-31..14,y=-36..8,z=-36..12
            off x=24..34,y=-27..-10,z=-34..-17
            on x=-33..14,y=-45..6,z=-42..2
            on x=-84084..-49906,y=-34022..-13687,z=-26508..-7161
            on x=-5505..17661,y=15376..39557,z=64634..85669
            on x=-77470..-55853,y=-25906..-5142,z=-42096..-20083
            on x=-44899..-13829,y=5050..26536,z=67119..86305
            on x=-63460..-40980,y=33003..59169,z=25378..44440
            on x=-17311..-1574,y=-86884..-62730,z=-54963..-35830
            on x=26480..34369,y=39725..55309,z=-77095..-43655
            on x=22634..49093,y=-13808..7138,z=-79760..-60071
            on x=2496..13198,y=-73951..-53407,z=-43731..-30331
            on x=-27451..-20414,y=1868..32906,z=-75318..-62730
            on x=31848..53483,y=-73320..-63684,z=-24860..-14722
            on x=6461..28672,y=1908..29124,z=-84376..-68877
            on x=16931..39395,y=37598..63107,z=47860..60054
            on x=14945..40546,y=-7317..549,z=-93498..-67762
            on x=-85540..-58126,y=7461..23740,z=-29051..-17311
            on x=38640..49114,y=55796..68805,z=-35888..-8915
            on x=-25483..8215,y=32431..39809,z=70198..82364
            on x=-3703..17675,y=61466..90313,z=16909..48922
            on x=44604..75529,y=-22304..-6299,z=45999..60729
            on x=-70454..-38359,y=25776..59602,z=-45661..-30347
            on x=-20074..9993,y=56063..71942,z=-49242..-17209
            on x=35569..59289,y=-52746..-21591,z=33105..49948
            on x=-69553..-46700,y=-21120..10014,z=-78684..-51923
            on x=-64276..-54684,y=41723..63992,z=-20013..-7784
            on x=-1824..9573,y=66316..76607,z=23895..46349
            on x=-54006..-31537,y=9127..34043,z=57224..87032
            on x=61803..80774,y=-50282..-13511,z=-35593..-9531
            on x=-65653..-36057,y=-71428..-40824,z=-50428..-20504
            on x=-35635..-12972,y=-6991..2475,z=-88189..-59265
            on x=67324..77277,y=12215..17359,z=9446..16623
            on x=46320..63691,y=23806..30377,z=-62984..-38536
            on x=-6894..13135,y=-44916..-38815,z=49846..79849
            on x=-67348..-54769,y=-57422..-29061,z=25855..36474
            on x=-28019..-23220,y=-60400..-37046,z=49526..71048
            on x=-42960..-30831,y=-78337..-64643,z=-22038..12986
            on x=-43601..-27531,y=-35599..-20965,z=52822..83732
            on x=-85892..-68621,y=-10994..9023,z=-20838..-9147
            on x=59413..97068,y=-22230..-1408,z=-3604..19485
            on x=62228..85826,y=-31716..-2979,z=12114..36300
            on x=46514..66369,y=-35480..1374,z=36011..51592
            on x=-48685..-36357,y=55919..77922,z=-20577..-12176
            on x=63584..82792,y=11027..31562,z=-43607..-35346
            on x=-13917..12041,y=-73682..-62007,z=35642..62665
            on x=45316..56476,y=13007..26619,z=45798..63220
            on x=-58080..-47733,y=8079..17846,z=53185..62392
            on x=2479..30325,y=-52517..-31422,z=-70357..-52270
            on x=-39911..-29141,y=52574..71764,z=-33493..-5376
            on x=75460..84989,y=-7735..-6452,z=-5685..823
            on x=45533..71393,y=-47931..-13874,z=30994..40795
            on x=-14338..7548,y=-96290..-65900,z=-14911..-8244
            on x=39472..46973,y=-45007..-16123,z=-68972..-43340
            on x=15323..43991,y=-79867..-60758,z=-46..13763
            on x=-34857..-7822,y=70341..75898,z=22751..44742
            on x=60051..71299,y=6656..26788,z=-46635..-29393
            on x=46242..67391,y=-60111..-42428,z=-63406..-43389
            on x=-35622..-13029,y=61821..88175,z=-19300..-3468
            on x=45267..78654,y=940..23235,z=34829..52055
            on x=-64544..-32718,y=-52424..-32259,z=-51391..-42170
            on x=-45310..-38884,y=60663..76511,z=-20350..6437
            on x=-19546..3011,y=-85555..-56269,z=-48635..-31142
            on x=76489..97638,y=-35407..-9542,z=-22900..679
            on x=-34856..-12798,y=57873..72105,z=28432..41250
            on x=-12695..5075,y=-51326..-34170,z=60410..74327
            on x=48950..55957,y=21833..41299,z=33762..48570
            on x=-72062..-57619,y=-30380..-10079,z=-68295..-30427
            on x=9722..29351,y=-48714..-33393,z=-78170..-44048
            on x=8231..26025,y=-77925..-54037,z=-47724..-25194
            on x=-95495..-73464,y=-7621..14339,z=-29593..-13088
            on x=55959..79890,y=-5967..31737,z=-67844..-36315
            on x=53044..74610,y=-30723..-8664,z=-43868..-14796
            on x=-4307..16454,y=35270..42969,z=-81647..-68472
            on x=45874..60812,y=-69404..-54747,z=-8298..-3138
            on x=-41808..-29758,y=-75803..-46116,z=23276..33715
            on x=-65837..-40686,y=-69179..-53790,z=-47995..-22105
            on x=-9912..23894,y=-1700..23494,z=-97303..-62588
            on x=19429..37260,y=-67376..-50156,z=-53512..-16652
            on x=-57067..-52828,y=-12667..-9591,z=-67443..-58160
            on x=-3523..27218,y=74994..90403,z=-3493..14395
            on x=14169..48854,y=-1568..28900,z=-71852..-52553
            on x=32821..57220,y=47717..78952,z=-34811..-13134
            on x=-79129..-71287,y=-18716..3046,z=21374..41841
            on x=-11439..14964,y=-16250..3021,z=65364..90013
            on x=-38129..-32162,y=58740..80207,z=-9827..9520
            on x=40614..58324,y=-11736..-1046,z=47782..68564
            on x=68136..85433,y=19038..53903,z=-31789..4892
            on x=6721..20772,y=74233..93387,z=-17766..5311
            on x=-38118..-12460,y=-57425..-34797,z=55638..66558
            on x=2737..32272,y=-76998..-55030,z=43688..49155
            on x=12198..38223,y=-73791..-64926,z=31631..39745
            on x=-20261..-2856,y=-74905..-54211,z=7861..34791
            on x=19178..35422,y=-11638..11563,z=58261..77953
            on x=-31001..1243,y=11174..27421,z=-83906..-70511
            on x=26738..51818,y=41573..66947,z=5480..38191
            on x=-4120..27825,y=-85989..-63889,z=-33571..-17260
            on x=65955..73858,y=-30937..-20357,z=19699..51919
            on x=59986..81449,y=-17094..-6765,z=-21325..4146
            on x=-15287..13119,y=-67414..-61189,z=-46979..-43603
            on x=-82390..-66004,y=-10271..3338,z=-38978..-23333
            on x=48845..69167,y=43156..62169,z=6515..37326
            on x=-29512..-18622,y=66618..85799,z=16557..37597
            on x=-13680..11486,y=-73990..-52854,z=34292..59824
            on x=16528..41696,y=-88669..-71438,z=6756..28376
            on x=47386..68246,y=26567..41173,z=32881..45573
            on x=41552..60418,y=-13177..5608,z=40188..72493
            on x=-17271..1089,y=66757..90442,z=-8173..4840
            on x=-83957..-59723,y=-5356..22092,z=-46860..-26958
            on x=-66297..-47544,y=-37434..-22806,z=-60731..-47364
            on x=-14961..-8051,y=-82471..-63555,z=-17655..1207
            on x=4207..34538,y=61925..92475,z=-30435..-15910
            on x=-17617..-10570,y=-95348..-63368,z=-16210..14719
            on x=38496..55233,y=-64106..-40918,z=16863..31642
            on x=50719..79640,y=-61152..-34221,z=-10774..9297
            on x=-42969..-7619,y=-26388..-18715,z=70740..87672
            on x=-47929..-35133,y=49347..82369,z=-14714..10853
            on x=-82604..-63475,y=-34831..-12713,z=-11929..4941
            on x=29358..63182,y=8015..29986,z=54315..82349
            on x=-20131..-1839,y=60333..80914,z=7967..29376
            on x=9353..36272,y=59895..89044,z=-25822..3230
            on x=43168..59888,y=-66721..-49667,z=32648..46482
            on x=10941..29136,y=-72555..-53370,z=-54573..-35565
            on x=11496..31590,y=-44959..-13029,z=53082..87310
            on x=-43159..-20524,y=-3743..13835,z=-87866..-52777
            on x=26627..54751,y=-43550..-24270,z=49732..64107
            on x=28255..59585,y=43842..68816,z=-44115..-40179
            on x=-66412..-55612,y=12166..35074,z=39006..51003
            on x=6312..30405,y=57804..74608,z=39542..48278
            on x=-14943..3493,y=10401..33502,z=-94020..-67669
            on x=35476..48357,y=-61493..-38961,z=-71382..-32979
            on x=20435..28523,y=7481..28051,z=-78353..-58295
            on x=-83068..-71048,y=-23721..6279,z=-22062..-4203
            on x=-51208..-17644,y=46982..85355,z=-40990..-15261
            on x=547..20809,y=60044..87576,z=22999..55796
            on x=-17902..-14415,y=-31625..-509,z=-87372..-60462
            on x=566..30656,y=65157..93943,z=-10094..25356
            on x=-57372..-44522,y=-43215..-4269,z=47571..76802
            on x=51928..61917,y=27192..61997,z=16462..35892
            on x=-91859..-77183,y=-25309..-6900,z=-6636..4658
            on x=35451..49841,y=42840..59899,z=27902..37733
            on x=46697..63066,y=-61951..-35956,z=-43402..-31431
            on x=56820..85532,y=22368..37611,z=8181..29028
            on x=3479..28543,y=-92950..-65707,z=-20147..3586
            on x=-30715..-8102,y=-74389..-38791,z=41838..65462
            on x=53923..84954,y=21345..48479,z=-31117..-22707
            on x=30692..68472,y=51381..74010,z=-43163..-26289
            on x=3963..13629,y=-82301..-68766,z=-32189..-28695
            on x=-26230..-18986,y=48059..75879,z=33800..57890
            on x=-11868..364,y=-17805..-6376,z=-87840..-69361
            on x=-62123..-34039,y=53323..65684,z=-18759..11519
            on x=-49686..-22329,y=44482..62763,z=-50165..-42433
            on x=8295..16668,y=-21473..2954,z=72427..92773
            on x=61061..82812,y=-34561..-15760,z=12195..29626
            on x=18871..46026,y=45645..74462,z=34738..47404
            on x=45250..66555,y=-51615..-23083,z=11910..48756
            on x=-74771..-44970,y=-21127..-11037,z=-64988..-33929
            on x=-28575..-24424,y=71792..78110,z=-18221..-8555
            on x=-25338..-6923,y=-8631..16382,z=-96932..-68836
            on x=-15816..8291,y=23502..46422,z=61737..75046
            on x=24012..60280,y=-83721..-54294,z=7578..28977
            on x=11501..29155,y=3402..11782,z=75736..82902
            on x=-57546..-26246,y=36304..67304,z=-45286..-27281
            on x=-6325..5065,y=22635..45274,z=-81700..-68260
            on x=-66720..-54631,y=-61236..-46412,z=-5425..22854
            on x=-72710..-48798,y=50508..64307,z=1333..13462
            on x=13865..37998,y=51466..65722,z=36006..59077
            on x=-28281..-7465,y=-68705..-40266,z=50853..66666
            on x=-38630..-17127,y=-7930..4812,z=-74660..-58504
            on x=20647..33497,y=-62711..-45250,z=-65188..-41498
            on x=-81459..-63994,y=11465..40585,z=18212..44406
            on x=-52840..-33789,y=-40941..-18153,z=40378..62402
            on x=-53664..-41652,y=19752..32228,z=50381..70997
            on x=-1080..23636,y=-33517..-25727,z=-86099..-57148
            on x=-23319..-3146,y=24049..42456,z=-71419..-57820
            on x=9982..44125,y=-22469..-5211,z=-89475..-57073
            on x=-59300..-41829,y=25692..39878,z=-65256..-42734
            on x=-47735..-38054,y=57249..66648,z=-43826..-25726
            on x=-39553..-32542,y=5165..28643,z=55369..70627
            on x=-12872..3087,y=-36519..-10753,z=60621..76591
            on x=59192..83731,y=18036..35836,z=20877..51212
            on x=26020..48490,y=12554..14377,z=-87022..-63383
            on x=-11854..-4515,y=-12639..-5946,z=-85165..-60021
            on x=-52581..-30843,y=59055..82392,z=-11397..22455
            on x=-36423..-16444,y=29020..41390,z=-71107..-58690
            on x=55463..71190,y=-39991..-27121,z=29306..33897
            on x=-41896..-15502,y=-51414..-28802,z=-67096..-46901
            on x=21576..51776,y=-40007..-10272,z=-84715..-46191
            on x=8292..28045,y=6514..32122,z=66114..83717
            on x=-66406..-58191,y=-43227..-14898,z=-59859..-26427
            on x=65446..83471,y=-7820..13657,z=-50600..-32289
            on x=35134..36286,y=-59572..-40161,z=-60756..-35239
            on x=9254..29476,y=67387..82064,z=-6059..20274
            on x=-53418..-42461,y=-25344..-8754,z=-68658..-59076
            on x=-20702..-12302,y=61483..82828,z=-4045..12127
            on x=-15861..5533,y=65152..81160,z=13062..33238
            on x=-67682..-56916,y=-59758..-23693,z=-36108..-26134
            on x=-19546..10161,y=-85094..-56733,z=-41987..-28045
            on x=48091..80455,y=-55246..-43231,z=-2817..22958
            on x=-77749..-58276,y=50507..63707,z=-10508..8335
            on x=-64911..-37001,y=-62267..-44370,z=-47966..-28755
            on x=62326..86674,y=-51021..-17862,z=-32602..-1819
            on x=-70525..-48780,y=-47797..-19221,z=25331..55925
            on x=-50427..-25959,y=-48504..-28133,z=49547..73361
            on x=-73330..-63515,y=-35500..-23379,z=-5114..19001
            off x=51657..61655,y=-4634..27427,z=-69455..-41980
            off x=-84315..-77210,y=19373..36993,z=-21053..15930
            on x=-22479..10246,y=-36815..-4914,z=-83609..-58867
            off x=-55431..-27453,y=-5767..21070,z=60471..70160
            off x=-5688..3205,y=873..13973,z=77366..82873
            off x=-53425..-21862,y=41423..59744,z=-55084..-43328
            on x=-22427..5852,y=-74250..-39007,z=37690..67662
            on x=-63041..-49752,y=-75175..-41598,z=-28260..4287
            off x=15413..50919,y=-63278..-30199,z=54233..71948
            off x=37405..60088,y=-30130..-3727,z=50275..74270
            on x=44953..76055,y=45013..54715,z=3402..24097
            on x=4477..16728,y=585..26623,z=-96312..-73210
            off x=-60707..-37806,y=33556..55805,z=-55830..-34506
            on x=-43041..-34231,y=-75795..-62433,z=-39802..-6990
            on x=7577..34465,y=63182..75089,z=-51613..-28445
            on x=-40656..-32795,y=39054..59670,z=39143..61093
            on x=61949..77774,y=-8028..6257,z=-25989..-7928
            off x=62480..84973,y=-25581..6076,z=-51280..-37961
            on x=-72929..-40057,y=-35351..-8508,z=-65932..-29811
            off x=-65518..-39994,y=-279..16674,z=-73725..-40504
            off x=-37297..-22465,y=-75675..-45041,z=39399..60052
            off x=-52661..-19343,y=49842..69777,z=-49307..-28892
            on x=16218..47208,y=-36176..-16797,z=62683..80721
            off x=-46068..-21667,y=60526..77312,z=4157..8754
            off x=34070..60691,y=-41133..-30172,z=-53958..-38014
            off x=64392..81661,y=-45176..-23170,z=-14523..15457
            off x=-15744..-7180,y=58905..69594,z=43007..61745
            off x=-52963..-30458,y=-10496..7448,z=-83916..-66777
            off x=69857..81390,y=-25485..-7871,z=5030..29910
            on x=-60959..-46201,y=50105..71526,z=-45080..-16114
            on x=18978..27486,y=55295..76687,z=-58694..-25206
            off x=-6820..20202,y=61572..82949,z=17286..19675
            on x=1728..22714,y=-70670..-51775,z=39263..62822
            off x=-45895..-27411,y=-82575..-60562,z=-35050..-16558
            off x=34247..55770,y=-32892..-572,z=49237..79617
            off x=6279..24334,y=-76907..-57315,z=-58904..-36830
            off x=-29510..-16989,y=69568..77772,z=14319..29972
            on x=60510..76515,y=-9611..13786,z=-33980..-20539
            on x=5371..24846,y=-53948..-32082,z=-72848..-59739
            on x=-16322..-10658,y=-42921..-29325,z=-82769..-55636
            on x=-33676..-21096,y=37528..59995,z=-58072..-30498
            off x=-47215..-31769,y=55087..91254,z=-14424..-5524
            off x=51272..71648,y=-9020..25748,z=48474..57714
            on x=-14910..-9150,y=-31157..-7755,z=-86976..-75518
            off x=-55245..-27776,y=-49766..-31808,z=42101..67152
            on x=-69367..-63985,y=-2671..13295,z=-53128..-27235
            on x=58929..86759,y=24302..53114,z=-4060..16704
            off x=-47623..-41234,y=52703..55259,z=-54922..-38332
            off x=234..33436,y=-38688..-18973,z=-78624..-64983
            on x=-77931..-67269,y=-25142..-18496,z=2965..13641
            off x=-35975..-19833,y=3648..26277,z=-89061..-67038
            off x=47790..55618,y=38688..74556,z=10070..21478
            on x=40666..45048,y=-82962..-64543,z=-4571..9994
            on x=11044..15406,y=66273..95175,z=-5970..10319
            off x=27658..45091,y=-13546..1126,z=47771..81258
            off x=-51366..-27007,y=-44699..-24577,z=46267..69042
            on x=-7039..14125,y=-67429..-36525,z=55266..77835
            off x=-14528..14651,y=60280..82464,z=-5661..19789
            off x=6049..22392,y=57922..78173,z=-68277..-33899
            on x=5350..16965,y=-67738..-44461,z=50282..75195
            on x=-29214..-16694,y=-8319..-575,z=66463..75789
            off x=-15036..-8425,y=70237..95064,z=-4849..24023
            off x=-53599..-37851,y=41971..53928,z=41571..46610
            on x=-13677..2807,y=78384..91484,z=-25356..-8582
            off x=24528..48582,y=62896..85790,z=12460..18522
            on x=-48176..-33364,y=14289..15341,z=54092..87843
            on x=-46960..-30174,y=-12120..6661,z=61935..70809
            on x=3481..27256,y=12288..22963,z=-97219..-62916
            off x=-28747..5507,y=17194..46753,z=-91768..-56220
            off x=42783..58738,y=51060..58581,z=820..24693
            on x=-49353..-37799,y=6360..36216,z=48187..77532
            on x=52517..57152,y=-57632..-40065,z=-52048..-22663
            off x=45577..82271,y=-53151..-34086,z=7606..27599
            on x=-49439..-39837,y=30695..51461,z=-60976..-40779
            off x=-66635..-46630,y=49057..63361,z=3526..12518
            on x=54010..66216,y=36321..55042,z=-6269..24374
            off x=4695..7233,y=455..14476,z=62747..89572
            off x=-85493..-66739,y=2471..22091,z=-36223..-29382
            off x=7884..18171,y=-68024..-45024,z=55300..75806
            off x=43549..76092,y=49654..69738,z=1376..15517
            off x=12346..17191,y=-4032..16643,z=72903..90015
            on x=60703..73244,y=-20825..-3421,z=-57128..-36177
            off x=-9866..20026,y=16321..41773,z=60404..85148
            on x=29431..48382,y=48462..74606,z=41279..44993
            on x=62612..79405,y=-5487..10920,z=-40427..-31710
            off x=11859..30527,y=-34097..-19197,z=-73407..-51576
            off x=63862..89056,y=21916..38067,z=-5939..30841
            on x=40214..68256,y=25471..56425,z=-62984..-30420
            on x=-25366..-18100,y=66147..95119,z=3805..18767
            off x=68425..79895,y=-47516..-25384,z=15293..41106
            on x=30487..56428,y=-1794..5876,z=51906..74514
            off x=46653..51836,y=-76040..-44634,z=18181..37159
            off x=-41608..-12051,y=72020..86112,z=-8983..14501
            on x=-88247..-62536,y=-8129..11276,z=-24055..-4427
            off x=3323..10767,y=59354..66158,z=-59632..-39108
            on x=-76419..-67309,y=3713..37240,z=-37530..-11311
            off x=-2144..12919,y=-54075..-34178,z=57045..63905
            off x=62020..79582,y=-31594..-23121,z=-26597..-24909
            off x=-17401..-9352,y=73039..84740,z=-44057..-13270
            off x=-24093..-6768,y=57829..80320,z=-61101..-38285
            on x=-11495..16269,y=-13453..-6215,z=63349..88399
            on x=36287..58675,y=59742..70305,z=-17524..18377
            off x=56801..73533,y=19040..42381,z=-1979..-474
            on x=-58575..-33767,y=-25457..8768,z=-73498..-64326
            off x=-12572..14666,y=-1123..34793,z=-83475..-75872
            on x=-3033..9242,y=17802..28168,z=74794..85676
            on x=2218..27568,y=67949..82818,z=-25004..-15400
            off x=-6346..-4816,y=56884..81616,z=-27429..-3943
            on x=854..25707,y=22958..43224,z=-82067..-52397
            on x=32311..41886,y=-68875..-42153,z=-52635..-38254
            off x=26308..38287,y=-83310..-60192,z=28347..48640
            on x=-32295..-7848,y=-87573..-75370,z=-12081..5422
            on x=-42105..-23185,y=-29547..-19413,z=-72177..-51007
            on x=-14692..-9809,y=73589..83475,z=-36237..-2728
            off x=-73543..-55115,y=23711..51688,z=-3991..1464
            off x=-14534..22568,y=-54527..-44508,z=57714..69383
            on x=-63081..-39714,y=-45491..-26566,z=-49886..-38562
            off x=-93619..-68748,y=-16696..-4661,z=-30558..-4126
            on x=-66806..-44552,y=37360..55716,z=35175..52862
            on x=-6316..26070,y=-54927..-28748,z=-69954..-47453
            off x=-78871..-50085,y=17954..32161,z=-36569..-23985
            on x=6369..25338,y=67499..88203,z=-26047..-4496
            on x=24755..54569,y=-67118..-37387,z=32897..50992
            off x=46528..63982,y=-54598..-26438,z=37906..65831
            on x=4634..24098,y=-45650..-35611,z=-67709..-58572
            off x=44078..48115,y=-75447..-53188,z=-5442..33129
            off x=-62868..-56634,y=46581..58996,z=-29362..-5667
            on x=-6024..8438,y=65634..74871,z=-38003..-30673
            off x=-59956..-46026,y=41808..66700,z=10165..36114
            off x=-32873..-20372,y=-88729..-60899,z=-19758..7186
            on x=-67393..-60159,y=-56680..-32136,z=-8305..17511
            off x=-20681..3599,y=-92468..-57916,z=12916..28612
            on x=-7301..9140,y=37170..58369,z=-72713..-47101
            on x=66247..85599,y=3262..12242,z=-7260..4927
            off x=-40965..-24082,y=-82146..-62002,z=20400..46309
            off x=-70415..-51837,y=-56997..-42331,z=-875..19799
            on x=57902..75722,y=35482..60227,z=-26389..-9051
            off x=-22870..7672,y=-4882..25620,z=-87984..-60436
            on x=3155..35881,y=-80545..-67121,z=-6933..22549
            off x=-65134..-44685,y=-66218..-45701,z=21040..41162
            on x=36126..69247,y=23453..60238,z=38126..59678
            on x=55937..67003,y=6113..21587,z=45836..60761
            off x=18451..41277,y=-70926..-44655,z=-60498..-33470
            on x=-59038..-40083,y=-53819..-22588,z=-55419..-43900
            on x=-22584..6019,y=59421..80916,z=29210..42933
            on x=-33931..-15311,y=-76316..-51022,z=21445..40518
            on x=42213..72279,y=-36048..-15004,z=26488..50017
            on x=-28842..-8697,y=-68609..-44348,z=40673..64865
            off x=-7167..8781,y=-3621..17677,z=-93905..-64435
            off x=-82972..-60284,y=6678..34439,z=-3504..-1846
            off x=-67834..-53928,y=-58517..-42067,z=-42608..-8421
            off x=-4855..22956,y=9296..23886,z=-94874..-60207
            off x=-79569..-70626,y=2629..27696,z=-22941..3340
            on x=-10965..20710,y=51680..63775,z=45787..69225
            off x=46280..63357,y=42031..64449,z=3459..25386
            on x=-254..28129,y=-81371..-67737,z=21692..52613
            on x=-2383..5437,y=66740..96837,z=10033..17517
            off x=17147..30452,y=-91140..-71945,z=1097..25760
            on x=-47454..-28074,y=-77661..-55440,z=-24637..-384
            off x=-57862..-35813,y=19453..29451,z=39191..70727
            off x=-57539..-22945,y=22003..33680,z=-67047..-49723
            off x=-3162..20231,y=-54634..-42988,z=53582..62417
            on x=-81550..-64507,y=-27996..-14746,z=-10531..-7267
            off x=20586..40845,y=-81557..-67548,z=6923..34475
            off x=22518..23900,y=-66351..-34420,z=51370..64347
            on x=-49052..-35860,y=-19130..11213,z=61999..78547
            off x=-34188..-18864,y=-81419..-70168,z=-37648..-12239
            off x=77362..83490,y=2745..18791,z=8832..18419
            on x=-32402..-14262,y=68320..76432,z=-35620..-15790
            off x=67237..90757,y=4695..12771,z=-19624..10840
            off x=27845..36686,y=-50404..-33761,z=-80878..-55141
            off x=36370..69461,y=33759..65210,z=-54985..-28791
            on x=-59135..-46273,y=7474..26326,z=-65975..-58673
            on x=26877..52927,y=-79947..-54801,z=-2779..22702
            off x=-71675..-51921,y=-59152..-34586,z=-8374..19519
            on x=6403..17478,y=-75737..-58369,z=-71127..-34808
            on x=13332..40836,y=-40399..-31096,z=-85343..-51700
            off x=-67594..-49756,y=11777..23306,z=-64433..-55048
            off x=21971..44163,y=52916..80818,z=-13270..-4413
            on x=66677..74076,y=22958..42365,z=5671..22730
            on x=-17496..17047,y=2018..35721,z=62177..87439
            off x=-46335..-30771,y=12773..50699,z=45373..69810
            on x=-82592..-68036,y=-13665..-9385,z=185..18296
            off x=31849..55495,y=-68614..-40470,z=37173..62475
            on x=-57887..-33776,y=-71721..-40636,z=-45..25174
            off x=35969..44108,y=-5435..14452,z=-88200..-55792
            on x=60659..86542,y=7942..20443,z=4839..32918
            off x=50661..57689,y=-33927..-25875,z=46713..54231
            on x=-30700..-7902,y=71035..78599,z=-30964..-7588
            off x=769..24000,y=-68842..-41325,z=-63861..-53498
            on x=-83517..-62573,y=-23803..5911,z=20605..50776
            off x=-77447..-65015,y=-7232..20379,z=36322..47544
            off x=54759..77852,y=-9847..18404,z=-54569..-25017
            on x=8814..38891,y=44583..61983,z=27053..54606
            off x=-32755..-26721,y=-27446..-17253,z=-80334..-52377
            on x=23465..37441,y=-37240..-20297,z=-68128..-49025
            on x=-5136..18927,y=17373..30697,z=66878..78408
            on x=-29189..-6136,y=-76807..-52880,z=26943..52440
            """;

    @Test
    public void test() {

    }
}