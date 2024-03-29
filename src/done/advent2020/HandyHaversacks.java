package done.advent2020;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SuppressWarnings("unused")
public class HandyHaversacks {

    static final Map<String, Bag> bags = new HashMap<>();
    static final Set<String> visited = new HashSet<>();
    static int sum = 0;

    static class Bag {
        final String type;
        final Set<String> parents = new HashSet<>();
        final List<BagEntry> contain = new ArrayList<>();
        int sum = 0;

        Bag(String type) {
            this.type = type;
        }

        void addParent(String parent) {
            parents.add(parent);
        }

        void addBags(List<BagEntry> content) {
            this.contain.addAll(content);
        }
    }

    static class BagEntry {
        final int count;
        final String type;

        BagEntry(int count, String type) {
            this.count = count;
            this.type = type;
        }
    }

    public static void main(String[] args) {
        try (Scanner in = new Scanner(INPUT2)) {
            Stream.generate(new LineSupplier(in))
                    .takeWhile(Objects::nonNull)
                    .map(HandyHaversacks::parseBag)
                    .forEach(bag -> bags.put(bag.type, bag));

            Bag shinyGoldBag = bags.get("shiny gold");
            int sum = visit(1, shinyGoldBag);
            int result = sum - 1;
            System.out.println("Result: " + result);
        }
    }

    private static int visit(int count, Bag bag) {
        if (!visited.contains(bag.type)) {
            bag.sum = bag.contain.stream()
                    .mapToInt(entry -> visit(entry.count, toBag(entry.type)))
                    .sum();

            visited.add(bag.type);
        }
        return count + count * bag.sum;
    }

    private static Bag toBag(String type) {
        return bags.computeIfAbsent(type, Bag::new);
    }

    private static Bag parseBag(String line) {
        String[] bagDefinition = line.split("bags contain");
        Bag bag = toBag(bagDefinition[0].trim());
        bag.addBags(parseBagContent(bagDefinition[1].trim()));
        return bag;
    }

    private static List<BagEntry> parseBagContent(String bagContent) {
        if ("no other bags.".equals(bagContent)) {
            return emptyList();
        }
        List<BagEntry> bags = new ArrayList<>();
        String[] splitContent = bagContent.substring(0, bagContent.length() - 1).split("bag[s]?[,]?");
        for (String entry : splitContent) {
            entry = entry.trim();
            int space = entry.indexOf(' ');
            bags.add(new BagEntry(Integer.parseInt(entry.substring(0, space).trim()), entry.substring(space + 1).trim()));
        }
        return bags;
    }

    private static final String INPUT = "" +
            "shiny gold bags contain 2 dark red bags.\n" +
            "dark red bags contain 2 dark orange bags.\n" +
            "dark orange bags contain 2 dark yellow bags.\n" +
            "dark yellow bags contain 2 dark green bags.\n" +
            "dark green bags contain 2 dark blue bags.\n" +
            "dark blue bags contain 2 dark violet bags.\n" +
            "dark violet bags contain no other bags.\n";

    private static final String INPUT2 = "" +
            "pale turquoise bags contain 3 muted cyan bags, 5 striped teal bags.\n" +
            "light tan bags contain 5 posh tomato bags.\n" +
            "shiny coral bags contain 2 muted bronze bags.\n" +
            "wavy orange bags contain 4 faded tomato bags.\n" +
            "light plum bags contain 3 drab orange bags, 4 faded coral bags.\n" +
            "pale purple bags contain 5 bright crimson bags.\n" +
            "bright blue bags contain 1 pale beige bag, 1 light teal bag.\n" +
            "pale bronze bags contain 1 dotted salmon bag, 1 striped blue bag, 2 clear tan bags.\n" +
            "muted maroon bags contain 5 pale crimson bags.\n" +
            "clear lavender bags contain 4 vibrant black bags, 2 posh red bags.\n" +
            "pale cyan bags contain 4 light olive bags, 2 dull lime bags, 4 faded black bags, 4 plaid red bags.\n" +
            "faded blue bags contain 1 posh tan bag, 1 dotted violet bag, 3 posh gold bags.\n" +
            "wavy teal bags contain 3 pale brown bags.\n" +
            "striped red bags contain 2 light bronze bags, 3 dark cyan bags.\n" +
            "drab brown bags contain 3 striped magenta bags, 3 clear silver bags.\n" +
            "posh salmon bags contain 4 bright purple bags, 5 mirrored green bags, 3 pale gold bags, 5 dull crimson bags.\n" +
            "light black bags contain 2 wavy coral bags.\n" +
            "striped tan bags contain 3 clear blue bags, 3 mirrored teal bags, 5 striped red bags.\n" +
            "posh plum bags contain 3 drab orange bags.\n" +
            "striped blue bags contain 4 bright violet bags, 5 dotted gray bags, 3 dotted violet bags, 1 dotted blue bag.\n" +
            "shiny white bags contain 4 dotted orange bags, 1 faded silver bag, 1 drab coral bag.\n" +
            "plaid maroon bags contain 3 light gold bags.\n" +
            "shiny fuchsia bags contain 2 dotted olive bags, 3 vibrant white bags, 3 dark salmon bags, 4 pale white bags.\n" +
            "muted bronze bags contain 4 vibrant bronze bags, 2 posh yellow bags, 1 shiny turquoise bag.\n" +
            "wavy cyan bags contain 2 striped crimson bags, 4 plaid tan bags.\n" +
            "vibrant indigo bags contain 4 pale gold bags, 3 posh gold bags, 1 drab red bag, 4 dull crimson bags.\n" +
            "drab tan bags contain 3 dark indigo bags, 3 striped black bags.\n" +
            "dull tan bags contain 3 drab blue bags, 3 pale green bags, 3 dotted red bags, 3 striped maroon bags.\n" +
            "dark red bags contain 4 bright chartreuse bags.\n" +
            "drab beige bags contain 5 bright teal bags, 1 faded cyan bag, 2 muted yellow bags, 1 dim lime bag.\n" +
            "dim black bags contain 5 wavy fuchsia bags, 3 muted tomato bags, 4 faded blue bags.\n" +
            "pale red bags contain 2 drab gray bags, 5 dull coral bags, 4 striped purple bags.\n" +
            "light cyan bags contain 4 mirrored gold bags, 3 vibrant bronze bags.\n" +
            "posh orange bags contain 2 dark silver bags, 3 striped chartreuse bags.\n" +
            "shiny cyan bags contain 5 dark turquoise bags.\n" +
            "vibrant cyan bags contain 2 light turquoise bags, 2 clear cyan bags, 4 dark cyan bags, 4 dotted orange bags.\n" +
            "vibrant gray bags contain 5 dark purple bags, 5 dark lime bags.\n" +
            "posh green bags contain 4 bright salmon bags, 2 muted tan bags.\n" +
            "posh tan bags contain 2 dotted indigo bags, 1 dull purple bag.\n" +
            "clear blue bags contain 5 dull purple bags.\n" +
            "wavy gray bags contain 4 pale cyan bags, 2 pale tomato bags.\n" +
            "posh red bags contain 4 dim green bags, 2 pale teal bags.\n" +
            "light tomato bags contain 5 dotted chartreuse bags.\n" +
            "faded yellow bags contain 3 plaid orange bags, 4 mirrored maroon bags.\n" +
            "dark black bags contain 1 faded gold bag, 3 striped purple bags, 2 dim teal bags.\n" +
            "light red bags contain 5 dark magenta bags, 3 striped purple bags.\n" +
            "dotted violet bags contain no other bags.\n" +
            "faded gold bags contain 1 dotted aqua bag, 2 light turquoise bags, 5 wavy violet bags.\n" +
            "dotted beige bags contain 5 vibrant turquoise bags, 5 clear maroon bags, 3 dim tomato bags, 4 pale maroon bags.\n" +
            "pale lavender bags contain 5 muted red bags, 3 dark teal bags, 3 faded black bags, 1 dim fuchsia bag.\n" +
            "clear tomato bags contain 3 dull white bags, 3 mirrored gold bags, 1 dark black bag.\n" +
            "vibrant silver bags contain 4 plaid orange bags, 2 shiny chartreuse bags, 3 dark salmon bags, 4 light silver bags.\n" +
            "plaid cyan bags contain 1 dark black bag.\n" +
            "drab green bags contain 1 plaid white bag.\n" +
            "posh turquoise bags contain 3 posh plum bags, 3 light gold bags, 1 bright crimson bag.\n" +
            "pale lime bags contain 3 pale olive bags, 3 vibrant chartreuse bags, 1 dotted tan bag, 5 striped cyan bags.\n" +
            "wavy fuchsia bags contain 3 shiny chartreuse bags, 3 vibrant tomato bags, 3 posh salmon bags, 1 light cyan bag.\n" +
            "shiny beige bags contain 1 muted orange bag, 3 clear olive bags.\n" +
            "posh tomato bags contain 5 muted tomato bags, 5 drab coral bags, 4 pale gold bags.\n" +
            "dark fuchsia bags contain 3 wavy blue bags, 5 faded indigo bags.\n" +
            "dotted green bags contain 5 dull plum bags, 5 muted lavender bags, 3 faded magenta bags, 4 clear white bags.\n" +
            "light salmon bags contain 2 muted purple bags, 5 shiny turquoise bags, 4 muted red bags, 5 posh red bags.\n" +
            "plaid purple bags contain 1 muted indigo bag, 4 pale silver bags, 4 dull crimson bags, 1 posh cyan bag.\n" +
            "light chartreuse bags contain 2 posh brown bags.\n" +
            "dotted black bags contain 3 dull gray bags, 5 muted gray bags, 5 pale maroon bags, 1 vibrant green bag.\n" +
            "mirrored teal bags contain 4 light gold bags, 5 striped maroon bags, 2 pale maroon bags.\n" +
            "dotted coral bags contain 3 dull teal bags.\n" +
            "shiny lime bags contain 3 mirrored gray bags.\n" +
            "shiny chartreuse bags contain no other bags.\n" +
            "clear red bags contain 4 dim tan bags, 4 dotted brown bags, 2 striped orange bags.\n" +
            "clear magenta bags contain 3 pale blue bags, 2 pale crimson bags.\n" +
            "faded black bags contain 5 dark turquoise bags.\n" +
            "wavy violet bags contain 5 light purple bags.\n" +
            "vibrant crimson bags contain 1 shiny gold bag, 1 dotted tomato bag, 1 plaid black bag, 1 drab olive bag.\n" +
            "plaid violet bags contain 1 shiny tan bag, 3 muted gray bags, 4 drab chartreuse bags.\n" +
            "vibrant lime bags contain 3 dark magenta bags, 2 dotted white bags, 4 muted tan bags.\n" +
            "faded violet bags contain 2 drab red bags.\n" +
            "plaid black bags contain 2 clear aqua bags, 2 wavy silver bags, 4 dim violet bags, 2 plaid red bags.\n" +
            "dull orange bags contain 1 clear tan bag, 1 plaid crimson bag, 1 pale chartreuse bag.\n" +
            "vibrant magenta bags contain 4 striped blue bags.\n" +
            "plaid fuchsia bags contain 1 vibrant lime bag, 4 faded indigo bags, 2 wavy fuchsia bags, 1 dim purple bag.\n" +
            "mirrored olive bags contain 1 dark teal bag, 1 pale brown bag, 1 light violet bag, 1 shiny yellow bag.\n" +
            "wavy plum bags contain 5 vibrant cyan bags, 1 pale gold bag, 2 wavy gray bags, 5 pale gray bags.\n" +
            "striped magenta bags contain 5 faded silver bags, 1 mirrored teal bag.\n" +
            "light indigo bags contain 1 light olive bag.\n" +
            "mirrored crimson bags contain 2 vibrant crimson bags.\n" +
            "muted crimson bags contain 2 plaid olive bags.\n" +
            "muted yellow bags contain 1 muted brown bag, 4 striped lavender bags, 1 bright violet bag.\n" +
            "posh lavender bags contain 2 wavy coral bags, 3 light tan bags.\n" +
            "striped indigo bags contain 4 dark brown bags.\n" +
            "dotted red bags contain 4 pale olive bags, 3 dark teal bags, 1 posh fuchsia bag.\n" +
            "plaid indigo bags contain 5 faded gold bags, 4 clear coral bags, 3 dull purple bags, 5 pale brown bags.\n" +
            "plaid magenta bags contain 5 faded black bags, 3 drab aqua bags, 5 vibrant green bags.\n" +
            "light coral bags contain 1 bright gray bag, 4 wavy orange bags, 2 drab coral bags, 1 dark coral bag.\n" +
            "clear brown bags contain 4 plaid aqua bags, 4 plaid coral bags, 5 drab red bags.\n" +
            "drab black bags contain 2 dotted white bags, 1 muted purple bag, 3 posh gold bags.\n" +
            "dull magenta bags contain 1 dull tomato bag, 4 posh gray bags, 4 wavy white bags, 1 pale cyan bag.\n" +
            "muted violet bags contain 3 posh chartreuse bags, 1 dotted magenta bag.\n" +
            "vibrant turquoise bags contain 4 plaid brown bags, 5 drab indigo bags, 4 mirrored green bags.\n" +
            "striped aqua bags contain 2 posh violet bags, 5 shiny blue bags, 3 pale tomato bags.\n" +
            "light teal bags contain 4 faded cyan bags, 2 clear turquoise bags.\n" +
            "clear turquoise bags contain 3 shiny aqua bags, 5 posh tomato bags.\n" +
            "dark maroon bags contain 2 shiny gold bags, 5 faded green bags.\n" +
            "vibrant beige bags contain 5 plaid turquoise bags, 2 shiny gold bags, 2 clear tan bags, 1 wavy black bag.\n" +
            "muted chartreuse bags contain 2 muted white bags, 2 striped tan bags, 1 muted brown bag, 5 posh lime bags.\n" +
            "pale maroon bags contain 5 pale teal bags, 4 dim violet bags, 5 posh teal bags.\n" +
            "vibrant white bags contain 5 light bronze bags, 1 wavy silver bag.\n" +
            "plaid olive bags contain 5 mirrored teal bags, 5 faded gray bags, 4 light olive bags.\n" +
            "dull silver bags contain 5 clear cyan bags, 1 dim tan bag, 5 dim black bags.\n" +
            "bright crimson bags contain 5 dotted indigo bags.\n" +
            "muted teal bags contain 3 striped aqua bags, 4 dotted gray bags, 2 bright salmon bags.\n" +
            "wavy salmon bags contain 5 posh tomato bags.\n" +
            "bright purple bags contain 4 light maroon bags, 2 dotted violet bags.\n" +
            "plaid green bags contain 3 faded black bags, 2 plaid red bags, 4 clear turquoise bags.\n" +
            "wavy gold bags contain 1 bright white bag.\n" +
            "drab violet bags contain 4 vibrant tomato bags.\n" +
            "posh maroon bags contain 3 clear tan bags, 3 light gold bags, 1 dim lime bag.\n" +
            "muted beige bags contain 4 clear white bags, 5 light maroon bags, 2 clear orange bags.\n" +
            "dull turquoise bags contain 4 posh blue bags, 1 mirrored green bag, 5 dotted orange bags, 5 wavy fuchsia bags.\n" +
            "shiny red bags contain 4 dark magenta bags.\n" +
            "light turquoise bags contain 3 dark cyan bags.\n" +
            "posh olive bags contain 3 clear green bags, 5 bright bronze bags, 5 light olive bags.\n" +
            "dim chartreuse bags contain 5 vibrant plum bags.\n" +
            "dark salmon bags contain 1 dotted orange bag, 3 light brown bags, 3 dotted chartreuse bags.\n" +
            "dull teal bags contain 4 wavy gold bags, 5 faded red bags, 4 light turquoise bags.\n" +
            "dark green bags contain 3 pale silver bags, 5 clear tan bags.\n" +
            "bright cyan bags contain 2 striped maroon bags, 1 clear silver bag, 1 dark maroon bag.\n" +
            "wavy crimson bags contain 3 wavy tan bags.\n" +
            "mirrored white bags contain 4 mirrored teal bags, 2 muted silver bags.\n" +
            "dark magenta bags contain 1 drab blue bag, 4 light white bags, 3 dark black bags.\n" +
            "light gold bags contain 4 posh red bags, 1 striped maroon bag, 5 bright purple bags, 4 dotted violet bags.\n" +
            "pale tomato bags contain 5 drab coral bags, 3 posh teal bags, 4 dotted blue bags.\n" +
            "dull purple bags contain 4 posh gold bags.\n" +
            "mirrored gold bags contain 3 mirrored teal bags, 1 striped maroon bag, 2 dotted indigo bags.\n" +
            "dotted turquoise bags contain 2 dim violet bags.\n" +
            "muted magenta bags contain 1 muted red bag.\n" +
            "bright maroon bags contain 3 wavy fuchsia bags, 2 dark magenta bags, 2 dim maroon bags, 1 dotted teal bag.\n" +
            "bright aqua bags contain 5 dim green bags, 2 striped tan bags, 1 faded olive bag.\n" +
            "striped silver bags contain 5 striped aqua bags, 2 striped purple bags, 3 dim blue bags, 3 faded olive bags.\n" +
            "light brown bags contain 4 posh red bags, 3 clear gold bags.\n" +
            "dark brown bags contain 4 clear olive bags.\n" +
            "dim crimson bags contain 4 dotted teal bags, 3 dark salmon bags.\n" +
            "wavy black bags contain 3 drab coral bags, 1 striped purple bag, 2 light brown bags, 4 plaid red bags.\n" +
            "striped coral bags contain 2 plaid blue bags, 5 drab tan bags, 5 light violet bags.\n" +
            "dull blue bags contain 3 pale gold bags, 1 posh crimson bag.\n" +
            "pale blue bags contain 4 dotted blue bags, 3 muted beige bags, 1 faded red bag.\n" +
            "mirrored yellow bags contain 4 muted cyan bags, 2 mirrored tan bags.\n" +
            "dim maroon bags contain 1 muted aqua bag.\n" +
            "drab plum bags contain 2 shiny magenta bags.\n" +
            "clear maroon bags contain 2 drab orange bags, 3 shiny red bags, 1 clear brown bag.\n" +
            "muted red bags contain 5 pale teal bags, 3 dim fuchsia bags, 1 light maroon bag.\n" +
            "dark plum bags contain 5 light salmon bags, 4 dim olive bags.\n" +
            "faded indigo bags contain 1 mirrored red bag, 3 faded lime bags.\n" +
            "bright coral bags contain 3 posh gold bags, 3 vibrant crimson bags.\n" +
            "dark indigo bags contain 5 faded silver bags, 2 dull tomato bags.\n" +
            "drab white bags contain 5 light silver bags.\n" +
            "vibrant violet bags contain 2 pale gray bags, 4 bright white bags, 3 light aqua bags.\n" +
            "mirrored maroon bags contain 1 pale cyan bag, 4 clear bronze bags.\n" +
            "wavy chartreuse bags contain 1 bright brown bag, 4 dim lime bags.\n" +
            "dull brown bags contain 3 bright olive bags.\n" +
            "vibrant yellow bags contain 3 shiny white bags, 2 clear blue bags.\n" +
            "posh bronze bags contain 1 light bronze bag.\n" +
            "dim aqua bags contain 5 wavy yellow bags, 3 muted purple bags, 3 pale crimson bags.\n" +
            "clear gray bags contain 4 faded coral bags, 1 striped violet bag, 5 pale crimson bags, 4 muted lavender bags.\n" +
            "muted plum bags contain 4 mirrored cyan bags.\n" +
            "dim lime bags contain 2 clear plum bags, 2 dim green bags, 5 posh tan bags.\n" +
            "dim red bags contain 2 plaid brown bags.\n" +
            "drab bronze bags contain 4 faded plum bags, 4 clear plum bags, 1 posh cyan bag, 1 dark cyan bag.\n" +
            "shiny purple bags contain 5 posh beige bags, 4 pale fuchsia bags, 2 wavy brown bags, 2 shiny maroon bags.\n" +
            "dull beige bags contain 1 mirrored indigo bag, 2 drab cyan bags, 1 dim fuchsia bag.\n" +
            "wavy blue bags contain 4 dotted maroon bags, 3 light maroon bags.\n" +
            "faded white bags contain 2 vibrant purple bags, 1 muted purple bag.\n" +
            "pale teal bags contain no other bags.\n" +
            "bright tan bags contain 3 clear indigo bags, 2 pale orange bags.\n" +
            "dull crimson bags contain 3 pale maroon bags, 3 vibrant bronze bags.\n" +
            "posh black bags contain 1 muted tomato bag.\n" +
            "vibrant maroon bags contain 5 mirrored crimson bags, 4 wavy beige bags.\n" +
            "dark lime bags contain 4 dim yellow bags, 1 pale beige bag, 1 vibrant beige bag.\n" +
            "bright white bags contain 2 dark turquoise bags.\n" +
            "faded plum bags contain 1 vibrant indigo bag, 5 dotted maroon bags, 1 vibrant bronze bag.\n" +
            "pale violet bags contain 2 pale lavender bags, 4 light brown bags, 5 vibrant tomato bags.\n" +
            "wavy purple bags contain 2 shiny tomato bags, 2 clear maroon bags, 3 posh bronze bags, 4 dull aqua bags.\n" +
            "plaid yellow bags contain 4 pale tomato bags, 2 dotted magenta bags, 5 wavy violet bags.\n" +
            "bright silver bags contain 5 light gold bags, 2 posh tan bags, 4 faded gray bags.\n" +
            "dark orange bags contain 4 dull yellow bags, 5 dull salmon bags.\n" +
            "wavy coral bags contain 5 dim lavender bags, 2 mirrored teal bags, 1 shiny chartreuse bag, 2 light gold bags.\n" +
            "pale aqua bags contain 1 faded plum bag, 5 vibrant plum bags.\n" +
            "dotted brown bags contain 2 light purple bags, 4 dim beige bags, 5 pale white bags.\n" +
            "plaid blue bags contain 1 shiny red bag, 5 light silver bags, 5 clear orange bags.\n" +
            "dim violet bags contain 3 posh teal bags.\n" +
            "dull yellow bags contain 5 muted aqua bags.\n" +
            "shiny yellow bags contain 5 muted aqua bags, 2 drab white bags, 5 muted purple bags.\n" +
            "dotted maroon bags contain 4 dim green bags, 2 faded silver bags.\n" +
            "bright brown bags contain 4 striped bronze bags.\n" +
            "posh crimson bags contain 2 posh red bags, 1 dotted indigo bag, 4 muted red bags.\n" +
            "dim orange bags contain 2 light tan bags, 4 dotted salmon bags.\n" +
            "mirrored beige bags contain 4 dim bronze bags, 5 vibrant salmon bags, 4 dim maroon bags.\n" +
            "shiny olive bags contain 5 dotted orange bags.\n" +
            "wavy tan bags contain 1 wavy brown bag, 1 faded silver bag.\n" +
            "mirrored magenta bags contain 5 drab teal bags, 3 striped bronze bags, 3 striped magenta bags, 5 dark tan bags.\n" +
            "muted brown bags contain 1 light indigo bag, 4 dotted blue bags.\n" +
            "vibrant aqua bags contain 1 shiny red bag, 5 wavy gold bags.\n" +
            "dark violet bags contain 2 dim orange bags, 5 dark purple bags, 2 pale yellow bags.\n" +
            "plaid teal bags contain 3 vibrant bronze bags.\n" +
            "shiny gold bags contain 5 drab red bags, 2 mirrored green bags, 2 muted tomato bags, 1 striped magenta bag.\n" +
            "wavy bronze bags contain 2 vibrant green bags, 2 plaid orange bags, 2 vibrant orange bags.\n" +
            "dark tomato bags contain 4 posh indigo bags.\n" +
            "drab red bags contain no other bags.\n" +
            "clear silver bags contain 1 plaid olive bag.\n" +
            "striped cyan bags contain 3 wavy silver bags, 2 faded indigo bags.\n" +
            "dark purple bags contain 1 wavy violet bag, 5 clear olive bags, 3 drab indigo bags, 5 striped purple bags.\n" +
            "dotted plum bags contain 4 vibrant purple bags, 3 muted lavender bags, 1 wavy coral bag.\n" +
            "posh yellow bags contain 5 light salmon bags, 2 light bronze bags.\n" +
            "dim tomato bags contain 3 pale white bags.\n" +
            "drab lime bags contain 3 drab chartreuse bags, 4 clear silver bags, 4 drab aqua bags.\n" +
            "plaid coral bags contain 2 bright olive bags.\n" +
            "mirrored salmon bags contain 3 plaid green bags.\n" +
            "faded chartreuse bags contain 2 light cyan bags, 5 pale tomato bags.\n" +
            "pale crimson bags contain 5 bright white bags, 3 shiny turquoise bags.\n" +
            "pale brown bags contain 4 bright silver bags.\n" +
            "shiny bronze bags contain 5 dull chartreuse bags, 4 dotted gray bags, 3 shiny blue bags, 1 dull blue bag.\n" +
            "faded red bags contain 2 muted gray bags.\n" +
            "dark lavender bags contain 5 dim coral bags, 4 muted gray bags, 1 shiny yellow bag.\n" +
            "faded purple bags contain 2 dotted white bags.\n" +
            "mirrored silver bags contain 3 pale maroon bags, 2 pale cyan bags, 4 dark chartreuse bags, 3 bright plum bags.\n" +
            "dull violet bags contain 1 dim coral bag, 3 wavy lavender bags.\n" +
            "faded beige bags contain 4 plaid bronze bags, 1 light salmon bag, 2 light brown bags.\n" +
            "vibrant orange bags contain 1 faded cyan bag, 2 vibrant olive bags, 2 bright plum bags.\n" +
            "vibrant purple bags contain 5 dotted orange bags, 1 striped aqua bag, 4 clear white bags, 3 dim olive bags.\n" +
            "faded crimson bags contain 1 clear lavender bag, 3 dim lavender bags, 3 dim cyan bags, 2 wavy tan bags.\n" +
            "dark yellow bags contain 1 plaid silver bag, 3 wavy maroon bags.\n" +
            "vibrant brown bags contain 5 dotted chartreuse bags, 4 clear silver bags, 4 dull lavender bags.\n" +
            "wavy lavender bags contain 4 dull gray bags.\n" +
            "bright green bags contain 5 striped gold bags.\n" +
            "faded magenta bags contain 5 dull beige bags.\n" +
            "posh white bags contain 2 dark coral bags.\n" +
            "muted green bags contain 1 wavy lavender bag, 1 striped aqua bag.\n" +
            "plaid turquoise bags contain 1 striped brown bag, 4 mirrored maroon bags.\n" +
            "plaid lavender bags contain 4 striped tan bags, 2 posh brown bags, 5 shiny brown bags.\n" +
            "pale fuchsia bags contain 5 light brown bags, 3 vibrant lime bags.\n" +
            "light gray bags contain 3 drab violet bags.\n" +
            "dim green bags contain no other bags.\n" +
            "light olive bags contain 4 vibrant bronze bags.\n" +
            "dotted aqua bags contain 5 muted red bags.\n" +
            "vibrant fuchsia bags contain 5 dull crimson bags, 5 dotted violet bags.\n" +
            "clear crimson bags contain 5 dotted tomato bags, 3 posh crimson bags, 5 vibrant magenta bags.\n" +
            "pale orange bags contain 5 mirrored indigo bags, 5 muted purple bags, 4 plaid orange bags.\n" +
            "mirrored lavender bags contain 4 dotted orange bags, 3 posh violet bags.\n" +
            "dotted tan bags contain 5 mirrored gray bags.\n" +
            "dim bronze bags contain 3 mirrored olive bags, 3 plaid magenta bags, 5 dim black bags, 2 drab blue bags.\n" +
            "clear olive bags contain 3 bright purple bags, 4 dim lime bags, 5 dim fuchsia bags.\n" +
            "dotted blue bags contain 1 dim green bag, 3 drab red bags, 2 posh gold bags.\n" +
            "shiny gray bags contain 3 pale lavender bags, 1 clear gold bag, 2 drab violet bags, 2 clear bronze bags.\n" +
            "bright plum bags contain 3 muted yellow bags, 4 posh chartreuse bags, 3 posh brown bags, 3 dim orange bags.\n" +
            "shiny violet bags contain 5 dim coral bags.\n" +
            "posh lime bags contain 3 plaid silver bags.\n" +
            "light beige bags contain 5 pale yellow bags, 3 light bronze bags, 5 pale turquoise bags.\n" +
            "drab gray bags contain 4 bright purple bags, 5 faded gold bags, 2 dim green bags.\n" +
            "muted aqua bags contain 4 clear white bags.\n" +
            "mirrored blue bags contain 2 vibrant green bags, 2 drab gray bags.\n" +
            "posh magenta bags contain 2 striped magenta bags, 5 dim cyan bags, 5 plaid orange bags, 1 wavy black bag.\n" +
            "dim yellow bags contain 2 muted purple bags, 1 striped black bag, 3 wavy coral bags.\n" +
            "dull indigo bags contain 3 posh fuchsia bags, 1 dotted beige bag.\n" +
            "posh gray bags contain 5 drab black bags.\n" +
            "dark blue bags contain 4 dim gold bags, 3 drab olive bags, 1 light cyan bag, 2 light tomato bags.\n" +
            "mirrored red bags contain 5 shiny white bags, 1 mirrored green bag, 4 wavy black bags, 1 dark brown bag.\n" +
            "mirrored violet bags contain 3 dim tan bags, 4 dark fuchsia bags, 4 pale turquoise bags.\n" +
            "shiny black bags contain 5 clear orange bags, 2 vibrant silver bags, 2 plaid maroon bags, 3 light olive bags.\n" +
            "vibrant chartreuse bags contain 4 dull salmon bags, 3 bright beige bags, 1 faded blue bag, 2 plaid brown bags.\n" +
            "dull cyan bags contain 3 faded green bags.\n" +
            "bright gold bags contain 3 posh plum bags.\n" +
            "vibrant olive bags contain 5 mirrored coral bags, 3 dotted lime bags, 5 drab blue bags, 2 dotted green bags.\n" +
            "faded maroon bags contain 4 mirrored green bags, 2 light lime bags, 3 light bronze bags.\n" +
            "vibrant red bags contain 2 posh lime bags, 1 dull maroon bag.\n" +
            "dull bronze bags contain 5 vibrant chartreuse bags.\n" +
            "vibrant bronze bags contain 2 vibrant tomato bags, 3 mirrored teal bags.\n" +
            "pale black bags contain 1 muted silver bag, 5 mirrored teal bags, 2 shiny blue bags.\n" +
            "dull maroon bags contain 1 clear cyan bag.\n" +
            "dotted crimson bags contain 5 posh black bags, 1 dotted teal bag, 4 vibrant salmon bags, 4 shiny silver bags.\n" +
            "striped tomato bags contain 5 drab tomato bags, 2 faded coral bags, 2 dim salmon bags.\n" +
            "bright bronze bags contain 5 plaid red bags, 4 striped yellow bags.\n" +
            "dark aqua bags contain 4 bright purple bags, 1 striped gold bag.\n" +
            "striped teal bags contain 4 striped black bags, 3 clear indigo bags.\n" +
            "dark olive bags contain 2 pale cyan bags, 5 mirrored tan bags.\n" +
            "dark teal bags contain 1 posh gold bag, 1 plaid orange bag, 1 vibrant bronze bag, 1 mirrored teal bag.\n" +
            "faded green bags contain 5 vibrant fuchsia bags, 3 dim olive bags.\n" +
            "posh brown bags contain 4 drab lime bags, 2 mirrored fuchsia bags, 3 shiny lime bags, 2 dim violet bags.\n" +
            "striped lavender bags contain 2 plaid red bags, 5 dark brown bags, 3 clear turquoise bags.\n" +
            "shiny indigo bags contain 2 striped lavender bags, 1 light gray bag.\n" +
            "plaid orange bags contain 1 pale teal bag, 5 dim violet bags, 5 vibrant bronze bags, 3 light maroon bags.\n" +
            "dull salmon bags contain 1 striped bronze bag, 4 shiny aqua bags, 4 dark brown bags.\n" +
            "plaid white bags contain 1 dim green bag.\n" +
            "drab lavender bags contain 2 dotted maroon bags, 3 pale aqua bags, 1 light olive bag.\n" +
            "striped olive bags contain 5 striped magenta bags.\n" +
            "mirrored gray bags contain 2 dim lavender bags, 2 shiny chartreuse bags.\n" +
            "dull coral bags contain 1 mirrored gold bag, 5 clear gold bags, 5 clear olive bags, 2 posh tomato bags.\n" +
            "pale beige bags contain 2 dark indigo bags, 4 dim beige bags.\n" +
            "posh teal bags contain 1 dim green bag, 3 dim fuchsia bags, 1 pale teal bag, 2 dotted indigo bags.\n" +
            "mirrored turquoise bags contain 4 dim olive bags, 2 plaid tomato bags.\n" +
            "dim turquoise bags contain 2 bright yellow bags, 1 striped lavender bag.\n" +
            "light violet bags contain 4 faded gold bags, 3 clear plum bags, 1 dark teal bag.\n" +
            "dark cyan bags contain 3 drab red bags, 4 pale maroon bags.\n" +
            "shiny crimson bags contain 5 dotted turquoise bags, 1 vibrant fuchsia bag, 5 dotted lime bags, 2 wavy green bags.\n" +
            "muted lime bags contain 2 light brown bags, 5 plaid tomato bags, 4 plaid aqua bags.\n" +
            "vibrant tan bags contain 4 wavy fuchsia bags.\n" +
            "wavy maroon bags contain 4 dull white bags, 5 dark crimson bags, 5 mirrored salmon bags, 4 vibrant purple bags.\n" +
            "muted indigo bags contain 5 shiny red bags.\n" +
            "clear violet bags contain 2 plaid green bags.\n" +
            "drab teal bags contain 2 pale cyan bags, 1 shiny turquoise bag.\n" +
            "wavy green bags contain 5 plaid indigo bags, 3 muted silver bags, 5 light brown bags.\n" +
            "striped crimson bags contain 3 shiny aqua bags.\n" +
            "drab purple bags contain 3 drab orange bags, 3 dark aqua bags, 1 bright lavender bag.\n" +
            "plaid aqua bags contain 1 plaid tomato bag.\n" +
            "striped green bags contain 2 vibrant tomato bags, 2 faded plum bags.\n" +
            "drab tomato bags contain 4 posh black bags, 3 dull brown bags, 1 drab cyan bag.\n" +
            "dotted bronze bags contain 1 clear indigo bag.\n" +
            "pale white bags contain 5 mirrored green bags, 2 dark turquoise bags, 3 dull olive bags, 4 drab indigo bags.\n" +
            "drab yellow bags contain 2 dotted orange bags, 4 light turquoise bags, 4 light salmon bags, 2 dotted tomato bags.\n" +
            "pale green bags contain 3 muted gold bags, 3 shiny turquoise bags.\n" +
            "dark tan bags contain 4 muted cyan bags, 5 dotted tomato bags, 2 dark indigo bags.\n" +
            "plaid tan bags contain 4 vibrant olive bags, 1 plaid aqua bag, 3 dotted coral bags, 4 bright violet bags.\n" +
            "shiny aqua bags contain 2 dull brown bags, 1 vibrant cyan bag, 2 dim lime bags, 5 light bronze bags.\n" +
            "bright turquoise bags contain 2 plaid yellow bags, 3 posh lavender bags, 1 pale yellow bag.\n" +
            "vibrant gold bags contain 1 clear tomato bag, 4 clear turquoise bags, 4 dark bronze bags.\n" +
            "posh cyan bags contain 3 dotted green bags, 5 plaid tomato bags, 3 wavy crimson bags, 2 striped olive bags.\n" +
            "clear salmon bags contain 3 plaid fuchsia bags, 5 muted bronze bags, 5 dull green bags, 2 pale brown bags.\n" +
            "drab maroon bags contain 5 dull crimson bags, 2 shiny white bags, 5 light purple bags.\n" +
            "clear tan bags contain 5 dull lime bags, 5 muted red bags, 2 clear cyan bags.\n" +
            "pale gray bags contain 3 shiny white bags.\n" +
            "vibrant teal bags contain 1 clear green bag, 1 dull beige bag.\n" +
            "vibrant plum bags contain 4 muted red bags, 2 faded blue bags, 5 vibrant tomato bags.\n" +
            "dark coral bags contain 2 plaid tomato bags, 1 bright yellow bag, 2 mirrored gray bags.\n" +
            "wavy indigo bags contain 1 mirrored tan bag, 1 wavy lavender bag.\n" +
            "dotted fuchsia bags contain 2 vibrant silver bags, 3 mirrored tan bags.\n" +
            "muted gold bags contain 3 muted red bags, 1 clear olive bag.\n" +
            "striped fuchsia bags contain 2 clear aqua bags, 4 mirrored coral bags, 3 muted gray bags, 2 dark beige bags.\n" +
            "striped maroon bags contain no other bags.\n" +
            "bright lavender bags contain 2 faded indigo bags, 1 dotted violet bag, 5 posh tomato bags, 3 clear indigo bags.\n" +
            "posh gold bags contain 1 dim violet bag, 2 shiny chartreuse bags.\n" +
            "drab olive bags contain 4 faded silver bags.\n" +
            "pale chartreuse bags contain 4 striped teal bags.\n" +
            "mirrored lime bags contain 5 dotted plum bags, 1 light yellow bag, 3 pale fuchsia bags.\n" +
            "clear bronze bags contain 4 plaid brown bags.\n" +
            "wavy tomato bags contain 4 faded lavender bags, 3 dull aqua bags, 1 drab green bag, 3 vibrant gray bags.\n" +
            "dim brown bags contain 4 dull teal bags, 2 vibrant black bags, 1 mirrored gold bag.\n" +
            "bright fuchsia bags contain 4 faded lavender bags, 1 dull crimson bag, 1 mirrored brown bag, 5 dark indigo bags.\n" +
            "muted gray bags contain 1 dull olive bag.\n" +
            "dull white bags contain 1 light maroon bag, 4 dark lavender bags, 2 posh red bags.\n" +
            "dull black bags contain 5 dark green bags, 4 bright lime bags, 4 mirrored gray bags.\n" +
            "dull tomato bags contain 4 dotted tomato bags.\n" +
            "pale coral bags contain 1 mirrored white bag, 5 clear aqua bags, 4 dim blue bags.\n" +
            "posh indigo bags contain 2 bright bronze bags.\n" +
            "plaid gold bags contain 4 dark maroon bags, 4 shiny lavender bags, 1 plaid tomato bag, 3 bright yellow bags.\n" +
            "plaid gray bags contain 4 muted bronze bags, 2 posh chartreuse bags, 5 pale tomato bags, 3 drab coral bags.\n" +
            "dim beige bags contain 4 dim blue bags, 4 dark lavender bags.\n" +
            "bright olive bags contain no other bags.\n" +
            "clear aqua bags contain 4 wavy fuchsia bags, 5 dim green bags.\n" +
            "posh violet bags contain 2 vibrant indigo bags, 3 posh tomato bags, 4 clear gold bags, 5 dim green bags.\n" +
            "faded coral bags contain 4 light purple bags, 4 mirrored salmon bags, 5 pale maroon bags.\n" +
            "dotted chartreuse bags contain 2 clear aqua bags, 4 plaid coral bags.\n" +
            "striped orange bags contain 5 bright tan bags, 5 pale white bags, 5 mirrored lavender bags.\n" +
            "bright tomato bags contain 5 muted white bags.\n" +
            "shiny silver bags contain 1 dotted orange bag, 2 light olive bags, 1 striped gold bag.\n" +
            "striped yellow bags contain 1 shiny turquoise bag.\n" +
            "faded aqua bags contain 5 shiny cyan bags, 3 dotted indigo bags, 4 faded fuchsia bags.\n" +
            "striped beige bags contain 1 bright white bag, 5 dim lavender bags, 5 striped black bags, 1 wavy black bag.\n" +
            "shiny orange bags contain 4 dark aqua bags.\n" +
            "striped white bags contain 3 vibrant fuchsia bags, 1 dotted teal bag, 5 dotted green bags, 2 shiny white bags.\n" +
            "bright black bags contain 5 pale blue bags, 2 drab teal bags, 1 dull gray bag.\n" +
            "shiny lavender bags contain 1 pale gold bag, 2 bright crimson bags, 2 pale maroon bags.\n" +
            "shiny maroon bags contain 2 wavy white bags, 2 muted aqua bags, 3 plaid gold bags.\n" +
            "drab cyan bags contain 4 posh crimson bags, 5 drab red bags, 5 bright purple bags.\n" +
            "dark bronze bags contain 5 posh teal bags.\n" +
            "shiny turquoise bags contain 2 shiny gold bags, 5 mirrored teal bags, 5 mirrored gray bags, 1 drab cyan bag.\n" +
            "dark turquoise bags contain 1 dim violet bag, 5 mirrored teal bags.\n" +
            "light lime bags contain 4 drab chartreuse bags.\n" +
            "light yellow bags contain 5 wavy olive bags, 2 wavy gray bags, 4 bright red bags, 5 shiny violet bags.\n" +
            "posh aqua bags contain 3 vibrant salmon bags.\n" +
            "drab silver bags contain 3 pale tan bags.\n" +
            "pale tan bags contain 3 wavy white bags.\n" +
            "light white bags contain 4 wavy black bags, 2 dark teal bags, 2 faded blue bags.\n" +
            "shiny teal bags contain 5 wavy gold bags.\n" +
            "shiny tomato bags contain 3 faded violet bags.\n" +
            "wavy brown bags contain 1 dim black bag, 1 bright yellow bag.\n" +
            "dim tan bags contain 2 clear teal bags, 5 drab teal bags, 4 posh lime bags.\n" +
            "faded cyan bags contain 1 pale plum bag, 4 posh gold bags, 4 posh yellow bags.\n" +
            "dotted magenta bags contain 4 drab tomato bags, 5 drab yellow bags, 2 clear maroon bags.\n" +
            "clear green bags contain 5 striped maroon bags, 4 shiny aqua bags.\n" +
            "clear fuchsia bags contain 5 dotted chartreuse bags, 5 pale plum bags, 2 muted red bags.\n" +
            "bright lime bags contain 3 dark salmon bags, 3 bright cyan bags, 4 striped black bags, 4 posh violet bags.\n" +
            "bright violet bags contain 1 light olive bag, 2 dark coral bags, 1 dull beige bag, 5 plaid maroon bags.\n" +
            "vibrant lavender bags contain 5 dim green bags, 1 plaid violet bag, 4 dotted coral bags.\n" +
            "wavy silver bags contain 2 mirrored green bags, 4 clear olive bags, 5 dark beige bags, 5 plaid orange bags.\n" +
            "clear orange bags contain 4 dotted orange bags, 3 bright silver bags, 5 dotted tomato bags, 4 striped purple bags.\n" +
            "light crimson bags contain 5 striped teal bags, 1 striped coral bag, 1 pale tomato bag, 2 dark crimson bags.\n" +
            "bright gray bags contain 3 posh tan bags.\n" +
            "mirrored indigo bags contain 1 mirrored green bag.\n" +
            "dull chartreuse bags contain 4 pale gold bags, 2 drab lavender bags, 3 shiny cyan bags.\n" +
            "pale salmon bags contain 5 drab purple bags, 2 dark olive bags, 4 mirrored silver bags.\n" +
            "bright indigo bags contain 5 striped beige bags, 5 shiny lime bags.\n" +
            "dim lavender bags contain 5 striped maroon bags.\n" +
            "bright magenta bags contain 2 plaid coral bags, 5 shiny aqua bags, 1 light purple bag.\n" +
            "muted purple bags contain 4 shiny turquoise bags, 1 shiny chartreuse bag, 3 muted tomato bags, 1 dotted aqua bag.\n" +
            "muted cyan bags contain 4 wavy black bags, 2 faded plum bags, 1 dull coral bag, 3 light tomato bags.\n" +
            "shiny plum bags contain 5 pale gray bags, 3 vibrant aqua bags.\n" +
            "dull lime bags contain 4 drab cyan bags, 1 posh gold bag, 4 bright purple bags, 3 posh tan bags.\n" +
            "faded teal bags contain 3 bright purple bags, 4 dotted magenta bags, 4 plaid olive bags.\n" +
            "clear chartreuse bags contain 2 dark brown bags, 1 pale lavender bag, 2 dark coral bags.\n" +
            "dotted tomato bags contain 1 dim green bag, 2 posh tomato bags.\n" +
            "pale silver bags contain 2 pale teal bags, 4 light purple bags, 4 bright yellow bags, 4 clear plum bags.\n" +
            "posh purple bags contain 2 faded tan bags, 3 clear aqua bags, 4 striped lavender bags, 3 dark teal bags.\n" +
            "striped bronze bags contain 4 drab red bags, 5 mirrored gray bags.\n" +
            "striped black bags contain 3 wavy coral bags, 3 faded blue bags, 5 bright olive bags, 2 dark bronze bags.\n" +
            "dark chartreuse bags contain 5 posh tomato bags.\n" +
            "muted white bags contain 2 mirrored gray bags, 5 dark cyan bags, 3 dotted indigo bags.\n" +
            "clear indigo bags contain 3 dark coral bags, 1 pale green bag, 2 plaid orange bags, 4 dim lime bags.\n" +
            "faded tan bags contain 3 dull purple bags, 2 dim orange bags.\n" +
            "clear yellow bags contain 1 vibrant fuchsia bag, 5 faded silver bags, 5 faded black bags.\n" +
            "dark gray bags contain 5 striped cyan bags.\n" +
            "clear plum bags contain 2 drab indigo bags, 5 pale maroon bags.\n" +
            "posh fuchsia bags contain 2 muted lavender bags, 5 posh red bags.\n" +
            "vibrant black bags contain 1 posh gold bag, 1 shiny white bag.\n" +
            "dim salmon bags contain 5 muted tan bags, 2 muted green bags, 2 pale bronze bags.\n" +
            "faded brown bags contain 3 dim tan bags.\n" +
            "mirrored brown bags contain 1 drab bronze bag, 3 wavy coral bags, 4 posh fuchsia bags.\n" +
            "dim gold bags contain 2 mirrored lavender bags, 5 pale gray bags.\n" +
            "faded fuchsia bags contain 3 wavy lavender bags, 5 shiny blue bags, 4 muted tomato bags.\n" +
            "mirrored chartreuse bags contain 2 faded aqua bags, 4 dark coral bags, 4 wavy beige bags, 5 dark orange bags.\n" +
            "muted fuchsia bags contain 3 light olive bags.\n" +
            "dotted silver bags contain 5 dotted turquoise bags, 3 dark cyan bags, 2 plaid red bags.\n" +
            "plaid lime bags contain 5 dull blue bags.\n" +
            "dim gray bags contain 4 striped magenta bags, 3 dotted indigo bags, 2 dim violet bags, 3 light olive bags.\n" +
            "wavy lime bags contain 2 bright salmon bags, 3 shiny cyan bags, 4 light gray bags, 4 shiny plum bags.\n" +
            "striped lime bags contain 5 posh teal bags.\n" +
            "dull red bags contain 3 mirrored tan bags, 3 dim tomato bags, 5 striped crimson bags.\n" +
            "faded lavender bags contain 1 bright indigo bag, 1 dim purple bag, 5 mirrored gray bags, 4 clear cyan bags.\n" +
            "wavy aqua bags contain 1 wavy gray bag, 3 dark crimson bags.\n" +
            "faded turquoise bags contain 1 drab plum bag, 5 dull gray bags, 4 plaid black bags, 1 wavy crimson bag.\n" +
            "dotted salmon bags contain 4 posh teal bags.\n" +
            "clear coral bags contain 5 drab tomato bags.\n" +
            "vibrant blue bags contain 4 dim lavender bags, 4 dark cyan bags.\n" +
            "muted coral bags contain 5 vibrant olive bags, 1 clear plum bag, 1 clear blue bag.\n" +
            "vibrant coral bags contain 5 vibrant silver bags, 2 plaid brown bags, 4 wavy brown bags.\n" +
            "mirrored aqua bags contain 4 bright lavender bags, 4 striped lavender bags, 1 posh fuchsia bag.\n" +
            "clear gold bags contain 1 posh tan bag, 1 dark beige bag, 5 striped gold bags.\n" +
            "dull aqua bags contain 4 dull plum bags, 2 light indigo bags.\n" +
            "clear cyan bags contain 5 light maroon bags, 5 posh tan bags, 3 dim lavender bags.\n" +
            "dotted olive bags contain 1 mirrored maroon bag, 2 dotted red bags, 4 drab lime bags.\n" +
            "wavy yellow bags contain 4 light silver bags, 4 dotted orange bags.\n" +
            "faded salmon bags contain 1 shiny lavender bag, 4 muted tomato bags, 3 plaid coral bags, 3 pale green bags.\n" +
            "dim blue bags contain 2 dim black bags.\n" +
            "faded tomato bags contain 3 shiny magenta bags.\n" +
            "light magenta bags contain 5 dim olive bags, 3 muted lavender bags.\n" +
            "muted turquoise bags contain 4 posh gold bags, 2 wavy beige bags, 3 posh magenta bags.\n" +
            "light purple bags contain 3 light maroon bags.\n" +
            "pale indigo bags contain 2 light green bags, 5 plaid bronze bags.\n" +
            "dim fuchsia bags contain no other bags.\n" +
            "plaid chartreuse bags contain 3 shiny silver bags, 1 posh teal bag.\n" +
            "plaid salmon bags contain 5 drab lime bags, 4 light aqua bags, 2 striped tan bags.\n" +
            "drab coral bags contain no other bags.\n" +
            "dotted gold bags contain 2 clear teal bags, 2 posh salmon bags, 1 plaid green bag, 5 muted tomato bags.\n" +
            "dull olive bags contain 2 dim lavender bags.\n" +
            "dotted purple bags contain 5 vibrant white bags, 5 wavy black bags.\n" +
            "dark crimson bags contain 4 posh chartreuse bags, 3 muted green bags, 3 dull plum bags, 5 muted beige bags.\n" +
            "mirrored tomato bags contain 2 clear crimson bags, 4 mirrored indigo bags, 2 muted black bags, 2 dark gray bags.\n" +
            "muted blue bags contain 5 pale brown bags.\n" +
            "wavy beige bags contain 5 plaid red bags.\n" +
            "muted black bags contain 3 muted magenta bags, 2 clear tomato bags, 1 pale red bag.\n" +
            "mirrored bronze bags contain 5 bright olive bags, 5 vibrant cyan bags, 2 drab cyan bags.\n" +
            "vibrant green bags contain 2 dull brown bags, 4 wavy white bags, 3 pale teal bags, 4 dark bronze bags.\n" +
            "dotted orange bags contain 5 light gold bags, 5 vibrant tomato bags, 3 light silver bags, 4 drab cyan bags.\n" +
            "clear purple bags contain 4 dull fuchsia bags.\n" +
            "mirrored green bags contain 2 muted red bags, 2 dim lavender bags.\n" +
            "wavy red bags contain 1 posh yellow bag, 2 shiny coral bags.\n" +
            "drab chartreuse bags contain 5 dull purple bags, 2 bright purple bags, 3 faded silver bags, 4 muted lavender bags.\n" +
            "dim indigo bags contain 1 light red bag, 4 wavy olive bags.\n" +
            "drab crimson bags contain 5 muted indigo bags, 5 vibrant crimson bags.\n" +
            "wavy olive bags contain 3 light black bags, 2 wavy plum bags.\n" +
            "dark silver bags contain 4 dull fuchsia bags, 3 dotted chartreuse bags.\n" +
            "pale olive bags contain 1 dark purple bag, 1 drab yellow bag, 1 vibrant coral bag.\n" +
            "posh silver bags contain 4 faded magenta bags, 5 muted coral bags, 4 posh cyan bags, 2 faded gray bags.\n" +
            "clear white bags contain 4 shiny blue bags.\n" +
            "light silver bags contain 3 mirrored green bags, 2 muted red bags, 1 muted tomato bag, 3 clear olive bags.\n" +
            "faded gray bags contain 5 dim green bags, 5 pale teal bags, 4 posh crimson bags, 3 dotted indigo bags.\n" +
            "clear teal bags contain 2 posh chartreuse bags, 2 posh blue bags.\n" +
            "dull green bags contain 5 muted tan bags, 3 faded gray bags, 2 dark tan bags.\n" +
            "dotted yellow bags contain 1 dim aqua bag, 5 dotted blue bags, 1 plaid teal bag, 2 dim salmon bags.\n" +
            "dotted indigo bags contain 1 muted red bag.\n" +
            "pale gold bags contain 1 mirrored green bag, 2 faded gray bags, 4 drab olive bags.\n" +
            "pale magenta bags contain 1 pale gray bag.\n" +
            "drab orange bags contain 4 dull beige bags, 1 dim gray bag.\n" +
            "light maroon bags contain no other bags.\n" +
            "dim silver bags contain 2 dull olive bags, 2 muted lavender bags, 5 dark fuchsia bags, 5 dotted tan bags.\n" +
            "shiny magenta bags contain 5 light purple bags.\n" +
            "shiny green bags contain 2 dim green bags, 1 pale plum bag, 2 striped teal bags.\n" +
            "drab magenta bags contain 1 plaid yellow bag, 3 bright crimson bags, 4 shiny salmon bags.\n" +
            "shiny salmon bags contain 4 dark bronze bags, 1 pale aqua bag, 5 posh red bags, 2 light gold bags.\n" +
            "mirrored cyan bags contain 2 bright olive bags, 2 bright aqua bags, 4 shiny turquoise bags.\n" +
            "drab aqua bags contain 1 drab olive bag, 5 shiny white bags, 2 dim gray bags.\n" +
            "wavy white bags contain 1 plaid red bag.\n" +
            "clear black bags contain 1 dim fuchsia bag, 5 pale white bags, 3 drab fuchsia bags.\n" +
            "dotted cyan bags contain 3 wavy aqua bags, 4 shiny brown bags, 4 faded tan bags.\n" +
            "dim magenta bags contain 4 striped orange bags, 2 mirrored turquoise bags, 3 vibrant turquoise bags, 3 pale chartreuse bags.\n" +
            "faded silver bags contain 5 dim fuchsia bags, 2 bright purple bags.\n" +
            "faded olive bags contain 3 dull lavender bags, 2 striped salmon bags, 1 bright yellow bag.\n" +
            "faded lime bags contain 4 posh tan bags, 4 dotted lavender bags, 3 striped magenta bags.\n" +
            "dark white bags contain 2 bright beige bags, 3 shiny chartreuse bags.\n" +
            "striped brown bags contain 5 muted lavender bags.\n" +
            "dotted lime bags contain 1 mirrored green bag, 4 dotted chartreuse bags, 2 shiny cyan bags, 1 bright purple bag.\n" +
            "bright beige bags contain 4 dull gray bags, 3 wavy violet bags, 5 light silver bags, 5 drab white bags.\n" +
            "dark beige bags contain 3 light gold bags, 1 muted tomato bag, 4 pale teal bags, 4 posh crimson bags.\n" +
            "dim olive bags contain 4 dark cyan bags.\n" +
            "plaid plum bags contain 1 clear tan bag, 4 posh brown bags.\n" +
            "wavy magenta bags contain 4 dotted plum bags, 2 dull tan bags.\n" +
            "drab gold bags contain 4 dark cyan bags, 2 clear yellow bags.\n" +
            "muted orange bags contain 2 faded tan bags, 5 vibrant salmon bags.\n" +
            "pale yellow bags contain 3 light turquoise bags, 3 plaid maroon bags, 2 dull salmon bags.\n" +
            "plaid tomato bags contain 2 dark beige bags.\n" +
            "dim purple bags contain 5 posh salmon bags, 2 dim lime bags, 2 dotted white bags.\n" +
            "bright red bags contain 4 dark teal bags, 3 shiny cyan bags.\n" +
            "bright salmon bags contain 4 pale aqua bags, 3 clear orange bags, 3 plaid black bags, 5 faded aqua bags.\n" +
            "plaid crimson bags contain 4 plaid tan bags, 4 dim aqua bags.\n" +
            "striped violet bags contain 2 dotted olive bags, 2 dotted red bags, 4 shiny gold bags.\n" +
            "vibrant tomato bags contain 3 dim lime bags.\n" +
            "drab turquoise bags contain 5 shiny purple bags, 1 light green bag, 1 pale chartreuse bag.\n" +
            "wavy turquoise bags contain 1 dark teal bag, 5 shiny fuchsia bags, 4 muted brown bags, 4 bright green bags.\n" +
            "striped plum bags contain 3 bright chartreuse bags, 1 dotted violet bag, 1 posh maroon bag.\n" +
            "mirrored plum bags contain 4 plaid indigo bags, 5 dotted white bags.\n" +
            "muted salmon bags contain 5 faded magenta bags, 3 plaid blue bags.\n" +
            "shiny brown bags contain 3 drab salmon bags.\n" +
            "drab blue bags contain 3 drab olive bags, 5 muted red bags, 2 bright purple bags.\n" +
            "striped gray bags contain 3 clear olive bags, 2 muted coral bags.\n" +
            "bright yellow bags contain 2 dull olive bags, 5 dark turquoise bags, 5 posh teal bags.\n" +
            "dim plum bags contain 2 dim cyan bags, 5 vibrant crimson bags.\n" +
            "muted lavender bags contain 2 clear bronze bags.\n" +
            "muted silver bags contain 3 vibrant salmon bags, 5 muted cyan bags, 1 dotted black bag.\n" +
            "mirrored orange bags contain 2 dull olive bags, 4 striped beige bags, 3 shiny aqua bags, 2 striped salmon bags.\n" +
            "dim teal bags contain 2 drab blue bags.\n" +
            "plaid brown bags contain 1 pale maroon bag, 4 light salmon bags, 1 vibrant indigo bag, 5 clear cyan bags.\n" +
            "bright chartreuse bags contain 4 plaid coral bags, 2 dull crimson bags, 3 plaid aqua bags, 2 faded blue bags.\n" +
            "dotted gray bags contain 1 dotted indigo bag, 2 posh crimson bags.\n" +
            "drab indigo bags contain 1 dotted violet bag, 1 dim fuchsia bag, 4 muted red bags, 4 striped maroon bags.\n" +
            "plaid bronze bags contain 3 drab plum bags, 1 posh violet bag, 2 dark tan bags, 3 plaid white bags.\n" +
            "striped salmon bags contain 3 clear brown bags.\n" +
            "dull lavender bags contain 4 pale green bags.\n" +
            "dull gray bags contain 4 drab indigo bags, 4 light salmon bags, 2 plaid coral bags, 3 striped magenta bags.\n" +
            "mirrored purple bags contain 2 vibrant brown bags, 1 plaid teal bag, 4 drab red bags, 4 plaid turquoise bags.\n" +
            "dull gold bags contain 3 drab teal bags.\n" +
            "light fuchsia bags contain 2 clear tan bags, 1 posh tan bag.\n" +
            "dim white bags contain 1 bright beige bag.\n" +
            "light green bags contain 1 dotted red bag, 4 muted gray bags, 5 dotted orange bags, 3 dim chartreuse bags.\n" +
            "bright teal bags contain 5 bright bronze bags, 2 pale green bags.\n" +
            "striped purple bags contain 1 dark beige bag.\n" +
            "muted tan bags contain 4 pale silver bags, 2 bright lavender bags, 4 drab cyan bags.\n" +
            "light blue bags contain 5 drab white bags, 1 pale olive bag.\n" +
            "dim cyan bags contain 1 wavy fuchsia bag, 5 posh teal bags.\n" +
            "striped chartreuse bags contain 4 bright beige bags, 1 muted lavender bag.\n" +
            "light lavender bags contain 2 posh lavender bags, 1 dim lavender bag.\n" +
            "striped gold bags contain 1 posh salmon bag, 3 mirrored gray bags, 1 faded silver bag.\n" +
            "light orange bags contain 4 dark black bags.\n" +
            "plaid silver bags contain 5 posh salmon bags, 3 vibrant tomato bags.\n" +
            "dotted white bags contain 3 dim fuchsia bags, 4 shiny gold bags, 2 bright olive bags, 4 muted purple bags.\n" +
            "faded bronze bags contain 3 pale green bags, 3 light yellow bags, 1 clear teal bag.\n" +
            "striped turquoise bags contain 4 mirrored aqua bags, 2 wavy orange bags, 1 pale lavender bag, 4 drab aqua bags.\n" +
            "clear lime bags contain 1 plaid green bag, 3 pale gold bags, 2 bright gray bags.\n" +
            "drab salmon bags contain 3 dull tomato bags.\n" +
            "plaid red bags contain 5 pale brown bags.\n" +
            "posh coral bags contain 2 dim violet bags, 4 dotted teal bags, 2 plaid red bags, 4 muted green bags.\n" +
            "light bronze bags contain 4 light purple bags.\n" +
            "faded orange bags contain 2 light teal bags.\n" +
            "dotted lavender bags contain 2 light olive bags, 3 muted tomato bags.\n" +
            "bright orange bags contain 3 light gray bags, 4 striped purple bags, 5 dull tomato bags.\n" +
            "mirrored coral bags contain 3 faded gray bags, 5 pale green bags, 4 pale aqua bags, 4 muted bronze bags.\n" +
            "dull plum bags contain 4 posh crimson bags, 4 clear cyan bags, 4 shiny white bags, 2 dotted maroon bags.\n" +
            "shiny blue bags contain 5 dim lime bags, 2 dim gray bags, 5 dark cyan bags, 3 posh teal bags.\n" +
            "mirrored fuchsia bags contain 4 muted tomato bags, 5 dotted chartreuse bags, 1 light red bag, 2 bright yellow bags.\n" +
            "dotted teal bags contain 1 dotted gray bag, 1 muted brown bag.\n" +
            "mirrored tan bags contain 2 clear aqua bags, 4 dim violet bags, 1 wavy gray bag.\n" +
            "posh chartreuse bags contain 2 faded blue bags, 4 dark coral bags, 2 light maroon bags, 5 dark purple bags.\n" +
            "shiny tan bags contain 1 wavy salmon bag, 2 shiny red bags, 5 clear coral bags, 3 wavy gold bags.\n" +
            "vibrant salmon bags contain 3 clear brown bags, 3 pale gold bags, 5 clear blue bags.\n" +
            "plaid beige bags contain 5 vibrant lavender bags, 2 dim brown bags, 4 dull yellow bags.\n" +
            "muted olive bags contain 3 vibrant blue bags, 5 shiny crimson bags, 5 pale beige bags, 2 dotted chartreuse bags.\n" +
            "clear beige bags contain 4 drab coral bags, 4 dark maroon bags, 1 light indigo bag.\n" +
            "dull fuchsia bags contain 2 pale magenta bags, 1 dotted indigo bag.\n" +
            "dark gold bags contain 3 posh crimson bags, 3 mirrored lavender bags.\n" +
            "pale plum bags contain 1 light bronze bag, 5 dotted violet bags, 2 dark salmon bags.\n" +
            "drab fuchsia bags contain 4 dull brown bags, 5 muted bronze bags.\n" +
            "mirrored black bags contain 1 muted silver bag, 3 plaid gray bags, 4 bright purple bags.\n" +
            "posh blue bags contain 3 dull beige bags, 5 dull olive bags.\n" +
            "posh beige bags contain 3 vibrant turquoise bags, 3 dotted lime bags.\n" +
            "light aqua bags contain 2 mirrored teal bags, 1 vibrant lime bag, 1 dim olive bag.\n" +
            "muted tomato bags contain 5 dim lavender bags.\n" +
            "dim coral bags contain 4 shiny magenta bags, 4 drab violet bags, 5 clear brown bags.\n";

    private static Set<Integer> toSet(String group) {
        return group.chars().boxed().collect(Collectors.toSet());
    }

    private static <T> Set<T> disjoint(Set<T> g1, Set<T> g2) {
        g1.retainAll(g2);
        return g1;
    }

    static boolean validIntRange(String text, int lowerBound, int upperBound) {
        try {
            int value = Integer.parseInt(text);
            return value >= lowerBound && value <= upperBound;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static class LineSupplier implements Supplier<String> {
        final Scanner in;

        public LineSupplier(Scanner in) {
            this.in = in;
        }

        @Override
        public String get() {
            return in.hasNext()
                    ? in.nextLine()
                    : null;
        }
    }

    private static class BatchSupplier implements Supplier<String> {
        final Scanner in;

        public BatchSupplier(Scanner in) {
            this.in = in;
        }

        @Override
        public String get() {
            StringBuilder buffer = new StringBuilder();
            while (in.hasNext()) {
                String line = in.nextLine();
                if (line.isBlank()) {
                    break;
                }
                buffer.append(' ').append(line);
            }
            String res = buffer.toString();
            return res.isBlank() ? null : res.trim();
        }
    }

    @Test
    public void test() {
        assertEquals(Set.of(), parseBagContent("no other bags."));
        assertEquals(Set.of("bright white", "muted yellow"), parseBagContent("1 bright white bag, 2 muted yellow bags."));
    }
}
