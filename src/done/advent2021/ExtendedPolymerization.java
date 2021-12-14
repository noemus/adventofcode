package done.advent2021;

import util.LineSupplier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Scanner;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.summingLong;
import static java.util.stream.Collectors.toMap;

public class ExtendedPolymerization {

    static String template;
    static Map<String, List<String>> rules;

    static Map<String, Long> counts = new HashMap<>();

    public static void main(String[] args) {
        try (Scanner in = new Scanner(INPUT2)) {
            template = in.nextLine();
            rules = Stream.generate(new LineSupplier(in))
                          .takeWhile(Objects::nonNull)
                          .map(line -> line.split("->"))
                          .filter(parts -> parts.length == 2)
                          .collect(toMap(parts -> parts[0].trim(), Rules::toExpansion));

            IntStream.range(0, 40).forEach(Rules::applyRules);

            Map<Character, LongAdder> frequency = Rules.aggregate();
            long max = frequency.values().stream().mapToLong(LongAdder::longValue).max().orElse(0);
            long min = frequency.values().stream().mapToLong(LongAdder::longValue).min().orElse(0);

            long result = max - min;

            System.out.println("Result: " + result);
            /*
            NNCB -> NCNBCHB
            NN -> NC, CN
            NC -> NB, BC
            CB -> CH, HB
            NBCCNBBBCBHCB:
             */
        }
    }

    record Rules() {
        static void applyRules(int step) {
            counts = pairs().flatMap(Rules::expand)
                            .collect(groupingBy(Count::pair, summingLong(Count::count)));
        }

        static Map<Character, LongAdder> aggregate() {
            Map<Character, LongAdder> charCounts = new HashMap<>();
            add(charCounts, template.charAt(0), 1L);
            counts.forEach((pair, counts) -> add(charCounts, pair.charAt(1), counts));
            return charCounts;
        }

        static void add(Map<Character, LongAdder> charCounts, char ch, long count) {
            charCounts.computeIfAbsent(ch, c -> new LongAdder()).add(count);
        }

        static Stream<Count> expand(String pair) {
            long count = counts.getOrDefault(pair, 1L);
            return rules.getOrDefault(pair, List.of(pair)).stream().map(p -> new Count(p, count));
        }

        static Stream<String> pairs() {
            return counts.isEmpty()
                   ? initialRules()
                   : counts.keySet().stream();
        }

        static Stream<String> initialRules() {
            Pairs pairs = new Pairs();
            return template.chars()
                           .mapToObj(pairs::next)
                           .flatMap(identity());
        }

        static List<String> toExpansion(String[] parts) {
            String pair = parts[0].trim();
            String insert = parts[1].trim();
            return List.of(pair.charAt(0) + insert, insert + pair.substring(1));
        }

        record Count(String pair, long count) {}
    }

    static class Pairs {
        private Optional<String> last = Optional.empty();

        Stream<String> next(int c) {
            String second = Character.toString((char) c);
            return last.map(first -> applyNext(first, second))
                       .orElseGet(() -> applyFirst(second));
        }

        private Stream<String> applyFirst(String second) {
            this.last = Optional.of(second);
            return Stream.empty();
        }

        private Stream<String> applyNext(String first, String second) {
            this.last = Optional.of(second);
            return Stream.of(first + second);
        }
    }

    private static final String INPUT = """
                                        NNCB
                                                    
                                        CH -> B
                                        HH -> N
                                        CB -> H
                                        NH -> C
                                        HB -> C
                                        HC -> B
                                        HN -> C
                                        NN -> C
                                        BH -> H
                                        NC -> B
                                        NB -> B
                                        BN -> B
                                        BB -> N
                                        BC -> B
                                        CC -> N
                                        CN -> C""";

    private static final String INPUT2 = """
                                         BVBNBVPOKVFHBVCSHCFO
                                                     
                                         SO -> V
                                         PB -> P
                                         HV -> N
                                         VF -> O
                                         KS -> F
                                         BB -> C
                                         SH -> H
                                         SB -> C
                                         FS -> F
                                         PV -> F
                                         BC -> K
                                         SF -> S
                                         NO -> O
                                         SK -> C
                                         PO -> N
                                         VK -> F
                                         FC -> C
                                         VV -> S
                                         SV -> S
                                         HH -> K
                                         FH -> K
                                         HN -> O
                                         NP -> F
                                         PK -> N
                                         VO -> K
                                         NC -> C
                                         KP -> B
                                         CS -> C
                                         KO -> F
                                         BK -> N
                                         OO -> N
                                         CF -> H
                                         KN -> C
                                         BV -> S
                                         OK -> O
                                         CN -> F
                                         OP -> O
                                         VP -> N
                                         OC -> P
                                         NH -> C
                                         VN -> S
                                         VC -> B
                                         NF -> H
                                         FO -> H
                                         CC -> B
                                         KB -> N
                                         CP -> N
                                         HK -> N
                                         FB -> H
                                         BH -> V
                                         BN -> N
                                         KC -> F
                                         CV -> K
                                         SP -> V
                                         VS -> P
                                         KF -> S
                                         CH -> V
                                         NS -> N
                                         HS -> O
                                         CK -> K
                                         NB -> O
                                         OF -> K
                                         VB -> N
                                         PS -> B
                                         KH -> P
                                         BS -> C
                                         VH -> C
                                         KK -> F
                                         FN -> F
                                         BP -> B
                                         HF -> O
                                         HB -> V
                                         OV -> H
                                         NV -> N
                                         HO -> S
                                         OS -> H
                                         SS -> K
                                         BO -> V
                                         OB -> K
                                         HP -> P
                                         CO -> B
                                         PP -> K
                                         HC -> N
                                         BF -> S
                                         NK -> S
                                         ON -> P
                                         PH -> C
                                         FV -> H
                                         CB -> H
                                         PC -> K
                                         FF -> P
                                         PN -> P
                                         NN -> O
                                         PF -> F
                                         SC -> C
                                         FK -> K
                                         SN -> K
                                         KV -> P
                                         FP -> B
                                         OH -> F
                                         """;

}