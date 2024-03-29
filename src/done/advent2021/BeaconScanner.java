package done.advent2021;

import util.BatchSupplier;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.lang.Math.abs;
import static java.lang.System.lineSeparator;
import static java.util.Comparator.comparing;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.joining;

public class BeaconScanner {

    static final int OVERLAP = 12;

    static scanner[] scanners;
    static final Set<Integer> visited = new HashSet<>();
    static final List<scanner> matched = new ArrayList<>();

    static final Set<point> beacons = new HashSet<>();

    public static void main(String[] args) {
        System.out.println();

        scanners = scanners(INPUT);

        matched.add(scanners[0]);
        visited.add(scanners[0].id);

        int skip = 0;
        while (matched.size() < scanners.length) {
            System.out.println("---------------------------------------------------------------------");
            System.out.print("Matched: ");
            System.out.println(matched.stream()
                                      .map(scanner::id)
                                      .map(String::valueOf)
                                      .collect(joining(", ")));
            System.out.println("---------------------------------------------------------------------");

            Set<scanner> newMatched = new HashSet<>();
            matched.stream().skip(skip).forEach(s -> {
                if (matched.size() + newMatched.size() == scanners.length) {
                    return;
                }
                matchPairs(s, newMatched);
            });

            skip = matched.size();
            addMatched(newMatched);
        }

        matched.forEach(s -> beacons.addAll(s.beacons));

        long result = beacons.size();
        System.out.println("Result: " + result);

        long result2 = scannerPairs(matched).mapToLong(scanner_pair::distance).max().orElse(0L);
        System.out.println("Result2: " + result2);
    }

    private static void matchPairs(scanner s, Collection<scanner> newMatched) {
        scannerPairs(s)
                .map(scanner_pair::match)
                .filter(Objects::nonNull)
                .map(scanner_match::s2)
                .forEach(newMatched::add);
    }

    static Stream<scanner_pair> scannerPairs(scanner s1) {
        return Stream.of(scanners)
                     .filter(s2 -> !visited.contains(s2.id))
                     .map(s2 -> new scanner_pair(s1, s2));
    }

    private static void addMatched(Collection<scanner> newMatched) {
        newMatched.stream().map(scanner::id).forEach(visited::add);
        matched.addAll(newMatched);
    }

    static scanner[] scanners(String input) {
        try (Scanner in = new Scanner(input)) {
            return Stream.generate(new BatchSupplier(in))
                             .takeWhile(Objects::nonNull)
                             .map(scanner::from)
                             .map(scanner::sort)
                             .peek(System.out::println)
                             .toArray(scanner[]::new);
        }
    }

    static Stream<scanner_pair> scannerPairs(List<scanner> scanners) {
        return IntStream.range(0, scanners.size())
                        .mapToObj(i -> scanners.stream()
                                               .skip(i + 1)
                                               .map(s2 -> new scanner_pair(scanners.get(i), s2)))
                        .flatMap(Function.identity());
    }

    private static int toInt(String part) {
        return Integer.parseInt(part.trim());
    }

    record point(int x, int y, int z) {
        point move(point o) {
            return new point(x + o.x, y + o.y, z + o.z);
        }

        point diff(point p2) {
            return new point(p2.x - x, p2.y - y, p2.z - z);
        }

        point negate() {
            return new point(-x, -y, -z);
        }

        point rotate(rotation rot) {
            return rot.rotate(this);
        }

        long distance(point o) {
            return abs(x - o.x) + abs(y - o.y) + abs(z - o.z);
        }

        @Override
        public String toString() {
            return x + "," + y + "," + z;
        }

        static point initial() {
            return new point(0, 0, 0);
        }

        static point from(String line) {
            String[] parts = line.trim().split("[,]");
            return new point(toInt(parts[0]), toInt(parts[1]), toInt(parts[2]));
        }
    }

    record rotation(z_rotation rot_z, x_rotation rot_x, y_rotation rot_y) {
        rotation rotate(rotation rot) {
            return new rotation(rot_z.rotate(rot.rot_z), rot_x.rotate(rot.rot_x), rot_y.rotate(rot.rot_y));
        }

        point rotate(point p) {
            return rot_y.rotate(rot_x.rotate(rot_z.rotate(p)));
        }

        @Override
        public String toString() {
            return "["+ rot_z + "],[" + rot_x + "],[" + rot_y + "]";
        }

        static rotation initial() {
            return new rotation(z_rotation.initial(), x_rotation.initial(), y_rotation.initial());
        }

        static Stream<rotation> all() {
            return z_rotation.stream().flatMap(z_rot ->
                    x_rotation.stream().flatMap(x_rot ->
                            y_rotation.stream().map(y_rot ->
                                    new rotation(z_rot, x_rot, y_rot))));
        }
    }

    record z_rotation(int x, int y) {
        static final z_rotation POS_X = new z_rotation(1, 0);
        static final z_rotation NEG_X = new z_rotation(-1, 0);
        static final z_rotation POS_Y = new z_rotation(0, 1);
        static final z_rotation NEG_Y = new z_rotation(0, -1);

        z_rotation {
            if (abs(x) > 1 || abs(y) > 1
                    || abs(x) == 1 && y != 0
                    || abs(y) == 1 && x != 0) {
                throw new IllegalArgumentException("Invalid rotation: x = " + x + ",y = " + y);
            }
        }

        z_rotation rotate(z_rotation z) {
            point p = new point(z.x, z.y, 0);
            return new z_rotation(newX(p), newY(p));
        }

        point rotate(point p) {
            return new point(newX(p), newY(p), p.z);
        }

        int newX(point p) {
            return x * p.x - y * p.y;
        }

        int newY(point p) {
            return x * p.y + y * p.x;
        }

        @Override
        public String toString() {
            return x + "," + y;
        }

        static z_rotation initial() {
            return new z_rotation(1, 0);
        }

        static Stream<z_rotation> stream() {
            return Stream.of(POS_X, NEG_X, POS_Y, NEG_Y);
        }
    }

    record x_rotation(int y, int z) {
        static final x_rotation POS_Y = new x_rotation(1, 0);
        static final x_rotation NEG_Y = new x_rotation(-1, 0);
        static final x_rotation POS_Z = new x_rotation(0, 1);
        static final x_rotation NEG_Z = new x_rotation(0, -1);

        x_rotation {
            if (abs(y) > 1 || abs(z) > 1
                    || abs(y) == 1 && z != 0
                    || abs(z) == 1 && y != 0) {
                throw new IllegalArgumentException("Invalid rotation: y = " + y + ",z = " + z);
            }
        }

        x_rotation rotate(x_rotation x) {
            point p = new point(0, x.y, x.z);
            return new x_rotation(newY(p), newZ(p));
        }

        point rotate(point p) {
            return new point(p.x, newY(p), newZ(p));
        }

        int newY(point p) {
            return y * p.y - z * p.z;
        }

        int newZ(point p) {
            return y * p.z + z * p.y;
        }

        @Override
        public String toString() {
            return y + "," + z;
        }

        static x_rotation initial() {
            return new x_rotation(1, 0);
        }

        static Stream<x_rotation> stream() {
            return Stream.of(POS_Y, NEG_Y, POS_Z, NEG_Z);
        }
    }

    record y_rotation(int x, int z) {
        static final y_rotation POS_X = new y_rotation(1, 0);
        static final y_rotation NEG_X = new y_rotation(-1, 0);
        static final y_rotation POS_Z = new y_rotation(0, 1);
        static final y_rotation NEG_Z = new y_rotation(0, -1);

        y_rotation {
            if (abs(x) > 1 || abs(z) > 1
                    || abs(x) == 1 && z != 0
                    || abs(z) == 1 && x != 0) {
                throw new IllegalArgumentException("Invalid rotation: x = " + x + ",z = " + z);
            }
        }

        y_rotation rotate(y_rotation y) {
            point p = new point(y.x, 0, y.z);
            return new y_rotation(newX(p), newZ(p));
        }

        point rotate(point p) {
            return new point(newX(p), p.y, newZ(p));
        }

        int newX(point p) {
            return x * p.x - z * p.z;
        }

        int newZ(point p) {
            return x * p.z + z * p.x;
        }

        @Override
        public String toString() {
            return x + "," + z;
        }

        static y_rotation initial() {
            return new y_rotation(1, 0);
        }

        static Stream<y_rotation> stream() {
            return Stream.of(POS_X, NEG_X, POS_Z, NEG_Z);
        }
    }

    record direction(point delta, rotation rot) {
        direction move(point other) {
            return new direction(delta.move(other), rot);
        }

        direction rotate(rotation rot) {
            return new direction(delta.rotate(rot), rot.rotate(rot));
        }

        @Override
        public String toString() {
            return "(" + delta + ") x " + rot;
        }

        static direction initial() {
            return new direction(point.initial(), rotation.initial());
        }
    }

    record scanner(int id, List<point> beacons, direction dir) {
        scanner sort() {
            return new scanner(id,
                    beacons.stream()
                           .sorted(comparing(point::x)
                                   .thenComparing(point::y)
                                   .thenComparing(point::z))
                           .toList(),
                    dir);
        }

        @Override
        public String toString() {
            return "--- scanner " + id + " ---" + lineSeparator() +
                    "--- " + dir + " --- " + lineSeparator() +
                    printBeacons() + lineSeparator();
        }

        private String printBeacons() {
            return beacons.stream().map(point::toString).collect(joining(lineSeparator()));
        }

        static scanner from(String batch) {
            int number = batch.indexOf("scanner") + 7;
            int dashes = batch.lastIndexOf("---");
            int id = toInt(batch.substring(number, dashes));
            String[] parts = batch.substring(dashes + 3).trim().split("[ ]");
            List<point> beacons = Stream.of(parts)
                                         .map(String::trim)
                                         .filter(not(String::isBlank))
                                         .map(point::from)
                                         .toList();
            return new scanner(id, beacons, direction.initial());
        }

        public scanner move(point delta) {
            return new scanner(id,
                    beacons.stream()
                           .map(b -> b.move(delta))
                           .sorted(comparing(point::x)
                                   .thenComparing(point::y)
                                   .thenComparing(point::z))
                           .toList(),
                    dir.move(delta));
        }

        public scanner rotate(rotation rot) {
            return new scanner(id,
                    beacons.stream()
                           .map(b -> b.rotate(rot))
                           .sorted(comparing(point::x)
                                   .thenComparing(point::y)
                                   .thenComparing(point::z))
                           .toList(),
                    dir.rotate(rot));
        }

        public scanner_match match(scanner s2) {
            return rotation.all()
                           .map(s2::rotate)
                           .map(this::matchPoints)
                           .map(m -> scanner_match.create(this, m))
                           .filter(m -> m.count >= OVERLAP)
                           .findFirst()
                           .orElse(null);
        }

        private match matchPoints(scanner s) {
            return match.create(s, matchPoints(beacons, s.beacons));
        }

        private static rot_match matchPoints(List<point> points1, List<point> points2) {
            int maxMatch = 0;
            int len1 = points1.size();
            int len2 = points2.size();
            point bestDiff = null;
            Set<point> diffs = new HashSet<>();

            for (int i = 0; i < len1; i++) {
                point p1 = points1.get(i);
                for (int j = 0; j < len2; j++) {
                    point p2 = points2.get(j);
                    point diff = p1.diff(p2);
                    if (diffs.add(diff)) {
                        int match = innerMatch(diff, points1, points2);
                        if (match > maxMatch) {
                            maxMatch = match;
                            bestDiff = diff;
                        }
                    }
                }
            }

            return new rot_match(maxMatch, bestDiff.negate());
        }

        private static int innerMatch(point diff, List<point> points1, List<point> points2) {
            return (int) points1.stream()
                                .map(p1 -> p1.move(diff))
                                .filter(m1 -> points2.stream().anyMatch(m1::equals))
                                .count();
        }
    }

    record scanner_pair(scanner s1, scanner s2) {
        scanner_match match() {
            return s1.match(s2);
        }

        long distance() {
            return s1.dir.delta.distance(s2.dir.delta);
        }
    }

    record rot_match(int count, point delta) {}

    record match(int count, point delta, scanner scanner) {
        static match create(scanner s, rot_match rot_match) {
            return new match(rot_match.count, rot_match.delta, s);
        }
    }

    record scanner_match(int count, scanner s1, scanner s2) {
        static scanner_match create(scanner s, match match) {
            return new scanner_match(match.count, s, match.scanner.move(match.delta));
        }
    }

    @SuppressWarnings("unused")
    private static final String INPUT1 = """
            --- scanner 0 ---
            0,2,0
            4,1,0
            3,3,0
            
            --- scanner 1 ---
            -1,-1,0
            -5,0,0
            -2,1,0""";

    @SuppressWarnings("unused")
    private static final String INPUT2 = """
            --- scanner 0 ---
            -1,-1,1
            -2,-2,2
            -3,-3,3
            -2,-3,1
            5,6,-4
            8,0,7
            
            --- scanner 0 ---
            1,-1,1
            2,-2,2
            3,-3,3
            2,-1,3
            -5,4,-6
            -8,-7,0
            
            --- scanner 0 ---
            -1,-1,-1
            -2,-2,-2
            -3,-3,-3
            -1,-3,-2
            4,6,5
            -7,0,8
            
            --- scanner 0 ---
            1,1,-1
            2,2,-2
            3,3,-3
            1,3,-2
            -4,-6,5
            7,0,8
            
            --- scanner 0 ---
            1,1,1
            2,2,2
            3,3,3
            3,1,2
            -6,-4,-5
            0,7,-8""";

    @SuppressWarnings("unused")
    private static final String INPUT3 = """
            --- scanner 0 ---
            404,-588,-901
            528,-643,409
            -838,591,734
            390,-675,-793
            -537,-823,-458
            -485,-357,347
            -345,-311,381
            -661,-816,-575
            -876,649,763
            -618,-824,-621
            553,345,-567
            474,580,667
            -447,-329,318
            -584,868,-557
            544,-627,-890
            564,392,-477
            455,729,728
            -892,524,684
            -689,845,-530
            423,-701,434
            7,-33,-71
            630,319,-379
            443,580,662
            -789,900,-551
            459,-707,401
            
            --- scanner 1 ---
            686,422,578
            605,423,415
            515,917,-361
            -336,658,858
            95,138,22
            -476,619,847
            -340,-569,-846
            567,-361,727
            -460,603,-452
            669,-402,600
            729,430,532
            -500,-761,534
            -322,571,750
            -466,-666,-811
            -429,-592,574
            -355,545,-477
            703,-491,-529
            -328,-685,520
            413,935,-424
            -391,539,-444
            586,-435,557
            -364,-763,-893
            807,-499,-711
            755,-354,-619
            553,889,-390
            
            --- scanner 2 ---
            649,640,665
            682,-795,504
            -784,533,-524
            -644,584,-595
            -588,-843,648
            -30,6,44
            -674,560,763
            500,723,-460
            609,671,-379
            -555,-800,653
            -675,-892,-343
            697,-426,-610
            578,704,681
            493,664,-388
            -671,-858,530
            -667,343,800
            571,-461,-707
            -138,-166,112
            -889,563,-600
            646,-828,498
            640,759,510
            -630,509,768
            -681,-892,-333
            673,-379,-804
            -742,-814,-386
            577,-820,562
            
            --- scanner 3 ---
            -589,542,597
            605,-692,669
            -500,565,-823
            -660,373,557
            -458,-679,-417
            -488,449,543
            -626,468,-788
            338,-750,-386
            528,-832,-391
            562,-778,733
            -938,-730,414
            543,643,-506
            -524,371,-870
            407,773,750
            -104,29,83
            378,-903,-323
            -778,-728,485
            426,699,580
            -438,-605,-362
            -469,-447,-387
            509,732,623
            647,635,-688
            -868,-804,481
            614,-800,639
            595,780,-596
            
            --- scanner 4 ---
            727,592,562
            -293,-554,779
            441,611,-461
            -714,465,-776
            -743,427,-804
            -660,-479,-426
            832,-632,460
            927,-485,-438
            408,393,-506
            466,436,-512
            110,16,151
            -258,-428,682
            -393,719,612
            -211,-452,876
            808,-476,-593
            -575,615,604
            -485,667,467
            -680,325,-822
            -627,-443,-432
            872,-547,-609
            833,512,582
            807,604,487
            839,-516,451
            891,-625,532
            -652,-548,-490
            30,-46,-14""";

    @SuppressWarnings("unused")
    private static final String INPUT = """
            --- scanner 0 ---
            -779,774,841
            -623,687,-622
            -569,692,-472
            -815,791,890
            545,-807,-699
            655,564,594
            -781,645,919
            561,908,-538
            -453,-675,694
            -885,-565,-404
            596,-595,-679
            464,-620,445
            481,-658,-759
            -574,-702,800
            -145,151,93
            -814,-575,-354
            -611,653,-643
            532,950,-506
            -475,-638,816
            403,-510,477
            736,559,518
            -17,7,140
            -861,-741,-321
            513,-554,586
            518,512,519
            549,924,-562
            
            --- scanner 1 ---
            689,772,-758
            537,371,685
            -672,-658,689
            123,51,-21
            -401,435,-501
            12,-98,95
            735,588,-720
            844,-573,-785
            -423,-889,-362
            -816,-649,595
            655,605,-727
            785,-703,-813
            536,-624,720
            -360,-902,-383
            -432,400,599
            -689,-669,583
            625,506,710
            455,445,698
            650,-556,751
            747,-531,-750
            -346,-875,-402
            -417,420,420
            -501,413,-386
            490,-584,661
            -499,374,505
            -449,476,-453
            
            --- scanner 2 ---
            663,667,-407
            -595,-818,-576
            -309,605,562
            -598,-610,-587
            -517,-586,691
            46,-151,22
            -881,281,-669
            732,-947,-380
            587,624,746
            -55,-3,100
            -458,-497,709
            593,687,-248
            572,498,738
            694,-939,-344
            401,-634,682
            -847,239,-653
            397,-527,600
            -610,-743,-467
            -474,659,598
            -414,-596,646
            657,542,-317
            -397,630,676
            378,-659,657
            -848,236,-666
            514,509,638
            753,-870,-512
            
            --- scanner 3 ---
            438,673,-607
            -400,-430,341
            -795,-514,-738
            -705,-416,-761
            760,-806,792
            -469,803,349
            670,-798,675
            363,648,-428
            -349,-447,506
            -425,412,-482
            520,455,660
            452,-616,-878
            553,372,771
            24,68,-59
            654,413,728
            -464,-413,481
            -404,598,-552
            -459,781,522
            538,-597,-851
            -346,488,-476
            420,-481,-842
            -470,896,540
            651,-822,721
            421,522,-506
            -690,-511,-769
            
            --- scanner 4 ---
            844,592,402
            -636,679,516
            -720,-400,911
            -509,-714,-788
            -826,755,-326
            39,-121,70
            661,741,-539
            727,636,527
            869,-741,-241
            742,-762,619
            -703,-565,962
            725,-852,686
            -639,710,621
            829,579,592
            654,730,-279
            -737,-540,847
            -734,702,606
            -487,-705,-778
            799,-689,-246
            -754,837,-380
            854,-715,-315
            754,-648,701
            -615,725,-335
            -628,-713,-672
            736,727,-443
            
            --- scanner 5 ---
            -405,590,-366
            -409,651,-375
            439,489,371
            -542,-507,536
            -367,404,-381
            299,-367,848
            -670,-452,-700
            -49,132,11
            340,-226,895
            478,-800,-337
            613,500,-723
            279,-348,939
            -558,-502,654
            390,411,415
            366,500,429
            400,-731,-350
            -612,-578,-728
            629,662,-720
            -522,-490,-688
            -453,406,393
            -679,460,405
            -652,411,356
            -532,-505,547
            707,591,-772
            635,-703,-379
            
            --- scanner 6 ---
            539,-841,504
            743,731,-445
            -822,-423,673
            -770,-492,627
            -762,805,536
            622,783,749
            -757,320,-629
            710,684,-611
            393,-892,456
            -769,705,691
            365,-675,-379
            -831,401,-736
            561,779,740
            -815,623,585
            385,-726,-591
            305,-611,-528
            -26,-6,-67
            -690,441,-682
            -830,-855,-756
            -707,-427,667
            808,821,734
            660,755,-621
            444,-961,435
            -831,-858,-732
            -873,-889,-537
            -116,-144,21
            
            --- scanner 7 ---
            620,-673,-810
            601,-454,-802
            -496,543,-614
            -596,554,-672
            963,-487,692
            -701,-396,401
            808,816,748
            638,-546,-875
            887,-534,765
            -604,428,-687
            916,-611,754
            -456,393,374
            -554,-588,-462
            549,385,-763
            -420,447,209
            -784,-504,377
            811,771,781
            -510,-687,-472
            -725,-524,511
            459,510,-757
            795,813,747
            71,-145,-43
            390,365,-693
            -534,422,297
            -565,-706,-544
            122,13,-148
            
            --- scanner 8 ---
            300,838,-511
            723,-429,-827
            -489,-531,-733
            -862,690,478
            272,779,-641
            365,729,650
            -749,-606,618
            -412,-399,-712
            -420,-363,-674
            -763,627,434
            584,-407,497
            -725,663,-381
            702,-366,-840
            358,649,667
            -832,581,-377
            683,-352,-813
            -773,672,537
            699,-507,475
            -616,-486,630
            -700,638,-358
            327,683,834
            632,-455,538
            335,741,-469
            -541,-595,615
            -76,130,123
            
            --- scanner 9 ---
            643,-906,-639
            554,-930,-576
            -438,-780,848
            885,648,-659
            -617,748,624
            708,-887,-479
            562,675,352
            513,-416,680
            -551,607,575
            867,701,-509
            567,599,433
            -593,592,-482
            533,-475,546
            -618,-692,-723
            -629,538,-371
            928,733,-581
            116,-32,-131
            405,-499,656
            515,674,570
            -629,-736,-551
            71,-144,33
            -384,-744,683
            -655,512,-481
            -658,-823,-634
            -413,-666,760
            -507,716,685
            
            --- scanner 10 ---
            392,400,680
            715,-577,488
            872,575,-780
            -527,-916,-745
            -756,759,-452
            -341,-954,-730
            360,461,570
            553,-799,-423
            -489,-888,625
            731,-478,552
            -844,478,532
            609,-456,515
            777,572,-702
            -439,-817,615
            641,-709,-383
            501,510,636
            -705,828,-431
            -764,593,595
            -610,708,-500
            -406,-864,449
            -77,-136,-15
            515,-651,-411
            -588,-912,-736
            1,17,72
            844,455,-646
            -784,542,383
            
            --- scanner 11 ---
            -705,-729,760
            -796,-680,666
            34,174,59
            681,672,720
            -634,-677,682
            685,-381,-430
            736,900,-429
            -892,-338,-314
            -28,77,-93
            -796,472,419
            731,-487,-512
            -885,399,-599
            746,727,-451
            -885,458,443
            678,-536,-330
            652,-373,448
            666,-453,483
            -706,-323,-352
            -807,414,492
            734,-275,479
            602,750,578
            -890,436,-400
            591,761,630
            -645,-332,-356
            -836,448,-626
            735,709,-336
            
            --- scanner 12 ---
            560,-631,663
            3,-21,-120
            425,-576,-600
            455,612,-831
            -472,492,-534
            615,888,330
            -382,383,326
            414,511,-837
            76,90,15
            -423,476,-725
            -568,-599,563
            751,919,441
            480,-626,-579
            -464,570,-714
            425,438,-790
            -744,-590,-577
            461,-722,-487
            -459,-674,578
            -688,-533,-461
            -366,425,292
            -369,380,325
            -448,-466,575
            597,-635,584
            647,-481,631
            753,946,365
            -709,-755,-482
            
            --- scanner 13 ---
            -793,-712,526
            43,-66,-77
            -592,814,658
            608,-557,-325
            -817,-758,545
            -447,441,-677
            -476,805,776
            377,-808,643
            685,332,-669
            -306,539,-675
            -533,-777,-578
            527,469,775
            576,-489,-523
            666,440,-717
            418,-765,542
            -678,-863,-623
            623,-570,-556
            -625,-783,-492
            460,-697,676
            653,391,833
            -611,841,744
            774,405,-693
            -801,-829,436
            678,570,758
            -11,91,43
            -418,455,-584
            
            --- scanner 14 ---
            805,-443,-751
            494,718,482
            2,-16,82
            104,91,-72
            -597,622,-615
            -753,481,430
            -531,502,409
            -672,731,-630
            -588,446,525
            571,-495,655
            -464,742,-627
            385,808,-378
            751,-407,-916
            382,821,-377
            -351,-554,-767
            -338,-407,491
            560,-451,483
            -450,-491,-811
            478,767,673
            531,-364,566
            -440,-536,-708
            -297,-435,349
            776,-521,-828
            572,726,662
            427,824,-344
            -266,-423,363
            
            --- scanner 15 ---
            -459,-573,-393
            -541,-681,657
            -691,578,-927
            846,-686,740
            854,-755,-493
            -670,527,-858
            -18,154,-79
            540,944,-704
            -392,942,855
            -622,673,-807
            -435,-635,739
            -453,-688,-389
            673,-696,718
            689,-806,-510
            549,934,-726
            345,935,570
            541,828,-585
            122,-41,-59
            -499,-636,-458
            382,940,769
            -377,756,817
            714,-691,-476
            -590,-628,839
            655,-685,833
            -386,695,813
            394,886,543
            
            --- scanner 16 ---
            729,789,-842
            -819,-334,502
            -741,-438,568
            399,-785,-551
            682,756,-676
            753,706,-666
            -9,-118,-9
            318,-711,-658
            -857,-784,-531
            439,-644,-605
            -891,-827,-750
            479,467,552
            659,-682,340
            -106,61,-102
            -681,548,823
            -664,604,849
            -860,-785,-555
            -613,351,-759
            -677,411,-908
            550,-780,387
            -728,431,-696
            -844,-469,484
            437,638,591
            -693,572,807
            491,488,586
            669,-789,409
            
            --- scanner 17 ---
            -418,-800,568
            690,-779,507
            441,-370,-505
            -573,-900,575
            -327,-603,-487
            552,-453,-472
            863,-801,517
            -436,328,515
            420,808,-712
            731,415,512
            -524,-718,522
            491,824,-603
            -18,24,64
            109,-51,-38
            -303,-536,-502
            -677,620,-677
            -482,397,567
            537,800,-622
            -442,332,630
            706,449,397
            484,-411,-606
            672,-894,518
            -576,655,-745
            871,432,448
            -517,524,-688
            -258,-444,-547
            
            --- scanner 18 ---
            -674,750,495
            -412,-547,817
            610,-432,859
            -410,-437,-273
            648,612,-461
            -691,786,446
            738,446,520
            -464,-684,742
            637,554,-561
            -494,402,-503
            33,115,-7
            775,424,642
            -612,484,-492
            541,-269,-637
            -545,752,417
            720,-493,891
            -430,-571,-332
            487,-434,-601
            -31,4,175
            802,545,536
            654,-542,750
            -302,-461,-356
            516,-357,-523
            522,627,-453
            -459,-765,818
            -593,412,-547
            
            --- scanner 19 ---
            -62,159,-61
            396,-693,-479
            -641,729,543
            -709,-486,661
            -552,-810,-705
            -796,708,-772
            -656,620,468
            522,-264,566
            -653,609,676
            -662,-436,507
            360,-565,-518
            -774,808,-657
            7,-11,2
            419,-516,-543
            409,-300,420
            352,889,-418
            383,849,609
            341,867,-488
            406,888,-551
            -733,760,-705
            501,-319,516
            -741,-399,511
            404,870,387
            499,905,552
            -725,-728,-734
            -597,-770,-717
            
            --- scanner 20 ---
            -649,836,908
            463,700,656
            552,809,589
            524,739,-436
            758,-723,501
            528,-639,-338
            484,-540,-348
            -914,776,-711
            -716,790,773
            -942,-616,878
            4,117,4
            -627,-773,-678
            -917,764,-831
            376,757,614
            -810,864,802
            699,-703,635
            -646,-554,-686
            539,-521,-420
            579,871,-390
            675,-831,525
            -184,-41,64
            -950,617,-725
            -664,-681,-751
            -989,-740,816
            -913,-693,889
            610,802,-431
            
            --- scanner 21 ---
            -328,515,-481
            -417,574,-499
            -621,584,416
            -465,-344,-508
            595,590,900
            793,-530,-444
            -358,-340,-590
            -386,-522,745
            154,-26,152
            -358,-580,619
            705,529,868
            50,99,-7
            799,454,-514
            540,-327,651
            577,-349,529
            -755,704,388
            -427,471,-544
            661,639,759
            774,366,-628
            -490,-321,-533
            513,-448,545
            -677,701,483
            -398,-670,707
            667,-515,-452
            678,-457,-552
            828,531,-592
            
            --- scanner 22 ---
            -354,917,781
            -281,841,734
            639,-856,-861
            562,853,614
            600,-872,-861
            650,-340,634
            -669,-830,764
            615,792,515
            -514,-535,-659
            -645,-747,872
            566,-839,-856
            -581,-624,-768
            399,493,-762
            -262,577,-795
            535,464,-683
            391,829,533
            -535,-771,744
            -368,738,-823
            -397,644,-749
            74,32,12
            587,-316,723
            490,-359,569
            -292,870,657
            549,579,-709
            -556,-448,-716
            
            --- scanner 23 ---
            -854,-686,494
            471,874,648
            488,-734,-887
            764,427,-727
            -759,681,-745
            -725,-714,449
            -605,728,-695
            795,538,-591
            458,-463,533
            405,770,623
            387,-740,-928
            -843,-741,525
            477,-316,412
            -885,549,507
            -750,-482,-862
            822,462,-589
            -158,-21,43
            461,812,673
            -755,760,-782
            -166,125,-115
            448,-380,515
            -700,-503,-917
            -884,648,444
            -824,602,404
            -846,-390,-881
            314,-693,-917
            
            --- scanner 24 ---
            -886,423,811
            785,453,423
            -396,553,-508
            -81,38,127
            -415,618,-348
            -522,-528,979
            706,-897,440
            -479,-748,-503
            652,-801,-610
            -610,-708,-592
            603,-785,-419
            611,-707,-599
            693,-894,441
            861,406,496
            736,672,-473
            -636,-503,891
            -372,464,-445
            814,530,434
            -29,-119,-2
            -558,-474,814
            -803,315,787
            773,654,-249
            -566,-719,-420
            709,-852,407
            797,573,-420
            -827,443,677
            
            --- scanner 25 ---
            -729,-745,477
            -809,409,-655
            628,-585,534
            405,464,-835
            309,581,335
            -697,-756,-375
            -459,599,568
            -390,539,409
            -626,-825,-487
            -823,-783,491
            340,-640,-575
            -14,-66,28
            -435,584,501
            334,-554,-667
            420,614,418
            352,480,376
            -157,-24,-97
            560,-387,566
            254,-685,-720
            359,483,-859
            -649,398,-559
            -616,-862,-402
            547,-618,581
            -708,341,-690
            288,374,-843
            -751,-815,603
            
            --- scanner 26 ---
            409,289,-495
            -847,-575,496
            413,467,-563
            508,-736,-627
            -930,-464,529
            530,380,608
            -764,280,526
            628,-869,503
            -42,-169,-23
            650,-658,-548
            503,274,552
            624,-954,561
            -499,-886,-493
            -406,-695,-494
            -856,235,571
            560,-747,-638
            -400,550,-551
            -889,-507,630
            -500,395,-544
            -521,-713,-483
            -582,501,-498
            -697,304,511
            440,253,530
            -170,-6,-82
            378,387,-577
            649,-801,614
            
            --- scanner 27 ---
            367,-847,-431
            805,450,827
            -651,-490,-341
            574,-485,737
            86,-58,110
            706,386,811
            -461,301,-317
            -745,-441,-432
            419,347,-310
            827,413,705
            -667,-493,-503
            329,-832,-245
            466,513,-343
            -529,329,473
            -599,353,-300
            -438,-863,890
            -541,433,385
            -516,-876,959
            371,-838,-492
            -673,-834,862
            632,-513,611
            -573,307,-497
            484,-525,649
            395,523,-393
            -513,468,378
            
            --- scanner 28 ---
            -568,386,784
            20,-72,121
            -121,24,13
            544,777,598
            -450,-510,942
            778,-914,-767
            830,-551,427
            -470,476,807
            -501,832,-460
            -378,-456,860
            358,756,528
            -717,-908,-600
            552,-860,-755
            790,-530,473
            -589,-465,830
            579,-909,-804
            -484,781,-295
            750,-556,440
            434,835,522
            -576,-802,-608
            727,722,-476
            -565,491,815
            -467,717,-435
            749,712,-303
            842,711,-494
            -695,-918,-609
            
            --- scanner 29 ---
            417,-415,713
            -449,647,870
            27,91,28
            402,-451,-539
            476,382,916
            -393,704,754
            548,-455,624
            -428,869,849
            857,539,-539
            -777,-601,-329
            -126,-2,126
            -665,-378,797
            827,434,-697
            -541,876,-635
            319,-462,-354
            504,-444,796
            -462,887,-670
            -714,-362,606
            -673,-456,617
            460,454,788
            488,413,930
            -888,-669,-268
            842,448,-606
            -852,-520,-276
            400,-532,-343
            -583,781,-617
            
            --- scanner 30 ---
            397,-399,-587
            -692,640,606
            -549,-426,-591
            517,-468,-682
            503,792,687
            -665,724,474
            683,415,-508
            469,573,730
            -665,-505,-567
            -470,730,-541
            632,627,-528
            747,564,-448
            466,-420,-536
            72,63,-118
            935,-637,679
            -375,-466,480
            -385,-494,361
            931,-631,564
            866,-591,627
            500,768,688
            -305,727,-516
            -647,841,608
            -776,-459,-594
            -413,713,-575
            -379,-503,254
            
            --- scanner 31 ---
            -626,-772,665
            -298,775,-361
            795,558,497
            -650,-716,745
            470,819,-606
            -465,-720,-367
            783,706,535
            476,861,-359
            426,-668,854
            16,67,24
            -559,670,547
            59,-95,-131
            754,-852,-774
            775,-921,-723
            560,-548,826
            -456,-698,-357
            823,616,623
            -452,696,550
            706,-801,-628
            -457,-579,-473
            -310,549,-415
            -441,686,552
            504,-665,759
            461,823,-545
            -755,-747,659
            -261,576,-366
            
            --- scanner 32 ---
            908,721,721
            86,-57,-97
            -713,-299,-592
            -758,-783,430
            -779,-597,500
            818,456,-829
            -623,521,428
            -837,-707,424
            907,-562,-902
            549,-311,329
            528,-351,375
            860,594,720
            -655,405,401
            -714,-392,-401
            829,529,-887
            912,-363,-865
            554,-391,342
            695,675,687
            -724,-414,-574
            -659,757,-629
            -69,-11,3
            -569,667,-540
            -586,448,388
            -580,615,-551
            907,480,-843
            858,-589,-853
            
            --- scanner 33 ---
            -779,-614,345
            695,317,397
            -753,576,801
            505,341,-394
            411,432,-345
            -667,-708,-706
            615,-385,529
            643,-378,564
            557,300,449
            -879,-666,355
            483,-862,-738
            -112,-148,91
            489,503,-397
            718,-890,-767
            -711,-622,-690
            -721,-601,-807
            -870,-758,402
            33,-26,-17
            -604,557,-459
            640,-959,-780
            -592,444,-618
            627,-473,425
            -758,670,920
            -605,562,-647
            583,497,403
            -870,560,878
            """;
}