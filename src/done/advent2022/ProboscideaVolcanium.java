package done.advent2022;

import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Stream;

import static done.advent2022.ProboscideaVolcanium.Valves.valve;
import static done.advent2022.ProboscideaVolcanium.Valves.valvesCount;
import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;
import static java.util.Objects.requireNonNull;
import static java.util.function.Function.identity;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toSet;
import static util.LineSupplier.lines;
import static util.Utils.split;
import static util.Utils.substring;

public class ProboscideaVolcanium {

    private static final long maxMinute = 26L;
    private static final Valves valves = Valves.instance;

    public static void main(String[] args) {
        try (Scanner in = new Scanner(INPUT)) {
            lines(in)
                    .map(Valve::create)
                    .forEach(valves::add);

            valves.normalize();
            System.out.println(valves.print());

            System.out.println("Result: " + valves.optimalRate());
        }
    }

    static class Valves {
        static final Valves instance = new Valves();

        private final Map<String, Valve> valves = new HashMap<>();
        private final Map<String, CombinedPath> start = new HashMap<>();

        static Stream<Valve> valves() {
            return instance.valves.values().stream();
        }

        static Valve valve(String name) {
            Valve valve = instance.valves.get(name);
            if (valve == null) {
                System.out.println("WARNING: trying to search for valve " + name);
            }
            return valve;
        }

        void add(Valve valve) {
            valves.put(valve.name(), valve);
        }

        void normalize() {
            start.put("AA", new CombinedPath("AA"));
            zeroValves().forEach(this::removeZeroValve);
        }

        long optimalRate() {
            SortedSet<CombinedPath> paths = new TreeSet<>(pathOrder());
            start.values().stream()
                 .flatMap(s1 -> start.values().stream()
                                     .map(s2 -> new CombinedPath(s1.you, s2.elephant, s1.pressure, new ValvesSet())))
                 .forEach(paths::add);

            long bestPressure = 0L;

            while (!paths.isEmpty()) {
                CombinedPath bestPath = paths.last();
                paths.remove(bestPath);
                if (bestPath.isFinished()) {
                    if (bestPath.pressure > bestPressure) {
                        bestPressure = Math.max(bestPath.pressure, bestPressure);
                        System.out.println("Best pressure: " + bestPressure + " - still running (" + paths.size() + ")");
                        System.out.println("You     : " + bestPath.you.history.print());
                        System.out.println("Elephant: " + bestPath.elephant.history.print());
                    }
                // prunning need improvement
                } else if (!bestPath.isNotWorthIt(bestPressure)) {
                    bestPath.moveOrOpen().forEach(paths::add);
                }
            }
            return bestPressure;
        }

        private static boolean hasHistory(CombinedPath bestPath, int index, String prefix) {
            final List<String> youHistory = bestPath.you.history.valves;
            final List<String> elephantHistory = bestPath.elephant.history.valves;
            return youHistory.size() > index && youHistory.get(index).startsWith(prefix)
                    || elephantHistory.size() > index && elephantHistory.get(index).startsWith(prefix);
        }

        private static Comparator<CombinedPath> pathOrder() {
            Comparator<CombinedPath> orderByMinutes = Comparator.comparing(CombinedPath::minute).reversed();
            Comparator<CombinedPath> orderByValves = Comparator.comparing(CombinedPath::openValvesCount).reversed();
            return Comparator.comparing(CombinedPath::potentialPressure)
                             .thenComparing(orderByMinutes)
                             .thenComparing(orderByValves)
                             .thenComparing(CombinedPath::currentValveNames);
        }

        private void removeZeroValve(String valveName) {
            List<Valve> merged = valves.values().stream()
                                       .filter(v -> v.leadsTo(valveName))
                                       .map(v -> merge(v, valveName))
                                       .toList();
            merged.forEach(v -> valves.replace(v.name, v));
            merged.forEach(valve -> start.computeIfPresent(valve.name, (k,p) -> new CombinedPath(valve, p.you.minute)));
            if (start.containsKey(valveName)) {
                CombinedPath removed = start.remove(valveName);
                for (Valve valve : merged) {
                    int minutes = removed.you.minute;
                    minutes += valve(valveName)
                            .tunnels()
                            .stream()
                            .filter(t -> t.to.equals(valve.name))
                            .mapToInt(Tunnel::length)
                            .findFirst()
                            .orElse(0);
                    CombinedPath prevStart = start.remove(valve.name);
                    if (prevStart != null) {
                        minutes = Math.min(minutes, prevStart.you.minute);
                    }
                    start.put(valve.name, new CombinedPath(valve, minutes));
                }
            }
            valves.remove(valveName);
        }

        private Valve merge(Valve valve, String zeroValve) {
            return new Valve(valve.name, valve.flowRate, mergeTunnels(valve.tunnels, zeroValve));
        }

        private Collection<Tunnel> mergeTunnels(Collection<Tunnel> tunnels, String zeroValve) {
            Map<String, Tunnel> merged = new HashMap<>();
            tunnels.stream()
                   .flatMap(tunnel -> mergeTunnel(tunnel, zeroValve))
                   .forEach(tunnel -> merged.compute(
                           tunnel.to,
                           (k, t) -> t == null || tunnel.length < t.length ? tunnel : t)
                   );
            return merged.values();
        }

        private Stream<Tunnel> mergeTunnel(Tunnel tunnel, String zeroValve) {
            return zeroValve.equals(tunnel.to)
                   ? valves.get(tunnel.to).tunnels
                           .stream()
                           .filter(not(tunnel::leadsBack))
                           .map(tunnel::merge)
                   : Stream.of(tunnel);
        }

        Set<String> zeroValves() {
            return valves.values()
                         .stream()
                         .filter(Valve::hasZeroFlowRate)
                         .map(Valve::name)
                         .collect(toSet());
        }

        String print() {
            Comparator<Valve> sortByFlowRate = Comparator.comparing(Valve::flowRate);
            return valves.values().stream()
                         .sorted(sortByFlowRate.reversed())
                         .map(Valve::toString)
                         .collect(joining("\n")) +
                    "\nNormalized: " + valves.size() +
                    "\nStart from: " + String.join(", ", start.keySet());
        }

        static int valvesCount() {
            return instance.valves.size();
        }
    }

    record History(List<String> valves) {
        History(String start, int minute) {
            this(singletonList(start + "(" + minute + ")"));
        }

        History add(String valve, int minute) {
            return new History(Stream.concat(valves.stream(), Stream.of(valve + "(" + minute + ")")).toList());
        }

        String print() {
            return String.join(", ", valves);
        }
    }

    record ValvesSet(Set<String> valves) {
        ValvesSet() {
            this(emptySet());
        }

        ValvesSet(String start) {
            this(singleton(start));
        }

        ValvesSet add(String valve) {
            return new ValvesSet(Stream.concat(valves.stream(), Stream.of(valve)).collect(toSet()));
        }

        boolean contains(String valve) {
            return valves.contains(valve);
        }

        String print() {
            return String.join(", ", valves);
        }

        int size() {
            return valves.size();
        }
    }

    record SinglePath(Valve currentValve, int minute, ValvesSet visitedValves, History history) {
        SinglePath(String start) {
            this(requireNonNull(valve(start)), 0, new ValvesSet(start), new History(start, 0));
        }

        SinglePath(Valve valve, int minute) {
            this(requireNonNull(valve), minute, new ValvesSet(valve.name), new History(valve.name, minute));
        }

        boolean sameValve(SinglePath other) {
            return currentValve.name.equals(other.currentValve.name);
        }

        long potentialPressure() {
            if (isFinished()) {
                return 0;
            }
            return currentValve.flowRate * (maxMinute - minute - 1);
        }

        boolean isFinished() {
            return minute >= maxMinute;
        }

        private Stream<SinglePath> openValve() {
            int newMinutes = minute + 1;
            return Stream.of(new SinglePath(
                    currentValve,
                    newMinutes,
                    new ValvesSet(currentValve.name),
                    history.add(currentValve.name + "+", newMinutes))
            );
        }

        private Stream<SinglePath> move() {
            return currentValve.tunnels.stream().filter(this::canVisit).map(this::moveTo);
        }

        private boolean cannotMove() {
            return currentValve.tunnels.stream().noneMatch(this::canVisit);
        }

        private boolean canVisit(Tunnel tunnel) {
            return !visitedValves.contains(tunnel.to);
        }

        private SinglePath moveTo(Tunnel tunnel) {
            return new SinglePath(
                    requireNonNull(valve(tunnel.to)),
                    minute + tunnel.length,
                    visitedValves.add(tunnel.to),
                    history.add(tunnel.to, minute + tunnel.length));
        }
    }

    record CombinedPath(SinglePath you, SinglePath elephant, long pressure, ValvesSet openValves) {
        CombinedPath(String start) {
            this(new SinglePath(start), new SinglePath(start), 0L, new ValvesSet());
        }

        CombinedPath(Valve valve, int minute) {
            this(new SinglePath(valve, minute), new SinglePath(valve, minute), 0L, new ValvesSet());
        }

        long potentialPressure() {
            if (isFinished()) {
                return pressure;
            }
            long potentialPressure = pressure;
            if (!isOpened(you.currentValve.name)) {
                potentialPressure += you.currentValve.flowRate * (maxMinute - you.minute - 1);
            }
            if (!isOpened(elephant.currentValve.name)) {
                potentialPressure += elephant.currentValve.flowRate * (maxMinute - elephant.minute - 1);
            }
            return potentialPressure;
        }

        String currentValveNames() {
            return you.currentValve.name + elephant.currentValve.name;
        }

        int openValvesCount() {
            return openValves.size();
        }

        boolean isFinished() {
            return you.isFinished() && elephant.isFinished()
                    || openValves.size() == valvesCount();
        }

        boolean isNotWorthIt(long bestPressure) {
            int closedValvesRate = Valves.valves().filter(v -> !openValves.contains(v.name)).mapToInt(Valve::flowRate).sum();
            long minutesToEnd = maxMinute - minute() - 1;
            long maxPossible = pressure + minutesToEnd * closedValvesRate;
            return maxPossible <= bestPressure;
        }

        Stream<CombinedPath> moveOrOpen() {
            if (isFinished()) {
                return Stream.empty();
            }
            return Stream.concat(openValve(), move());
        }

        private Stream<CombinedPath> openValve() {
            if (isOpened(you.currentValve.name) && isOpened(elephant.currentValve.name)) {
                return Stream.empty();
            }
            if (isOpened(you.currentValve.name)) {
                return youMoveHeOpens();
            }
            if (isOpened(elephant.currentValve.name)) {
                return heMovesYouOpen();
            }
            if (you.sameValve(elephant)) {
                return Stream.concat(youMoveHeOpens(), heMovesYouOpen());
            }
            return Stream.of(
                    bothOpen(),
                    youMoveHeOpens(),
                    heMovesYouOpen()
            ).flatMap(identity());
        }

        private Stream<CombinedPath> bothOpen() {
            return elephant.openValve().flatMap(ep -> you.openValve().map(yp -> {
                long yPressure = (maxMinute - yp.minute) * yp.currentValve.flowRate;
                long ePressure = (maxMinute - ep.minute) * ep.currentValve.flowRate;
                long newPressure = pressure + yPressure + ePressure;
                return new CombinedPath(
                        yp, ep,
                        newPressure,
                        openValves.add(yp.currentValve.name).add(ep.currentValve.name));
            }));
        }

        private Stream<CombinedPath> heMovesYouOpen() {
            return elephant.move().flatMap(ep -> you.openValve().map(yp -> {
                long newPressure = pressure + (maxMinute - yp.minute) * yp.currentValve.flowRate;
                return new CombinedPath(yp, ep, newPressure, openValves.add(yp.currentValve.name));
            }));
        }

        private Stream<CombinedPath> youMoveHeOpens() {
            return you.move().flatMap(yp -> elephant.openValve().map(ep -> {
                long newPressure = pressure + (maxMinute - ep.minute) * ep.currentValve.flowRate;
                return new CombinedPath(yp, ep, newPressure, openValves.add(ep.currentValve.name));
            }));
        }

        private boolean isOpened(String valveName) {
            return openValves.contains(valveName);
        }

        private Stream<CombinedPath> move() {
            return you.move().flatMap(yp -> elephant.move().map(ep -> moveTo(yp, ep)));
        }

        private CombinedPath moveTo(SinglePath you, SinglePath elephant) {
            return new CombinedPath(
                    you,
                    elephant,
                    pressure,
                    openValves);
        }

        int minute() {
            return Math.min(you.minute, elephant.minute);
        }
    }

    record ReleasePath(Valve currentValve, long pressure, int minute, ValvesSet openValves, ValvesSet visitedValves, History history) {
        ReleasePath(String start) {
            this(requireNonNull(valve(start)), 0L, 0, new ValvesSet(), new ValvesSet(start), new History(start, 0));
        }

        ReleasePath(Valve valve, int minute) {
            this(requireNonNull(valve), 0L, minute, new ValvesSet(), new ValvesSet(valve.name), new History(valve.name, minute));
        }

        long potentialPressure() {
            if (isFinished()) {
                return pressure;
            }
            return isOpened()
                   ? pressure
                   : pressure + currentValve.flowRate * (maxMinute - minute - 1);
        }

        String currentValveName() {
            return currentValve.name;
        }

        int openValvesCount() {
            return openValves.size();
        }

        boolean isFinished() {
            return minute >= maxMinute || openValves.size() == valvesCount();
        }

        boolean isNotWorthIt(long bestPressure) {
            int closedValvesRate = Valves.valves().filter(v -> !openValves.contains(v.name)).mapToInt(Valve::flowRate).sum();
            long maxPossible = pressure + (maxMinute - minute - 1) * closedValvesRate;
            return maxPossible <= bestPressure;
        }

        Stream<ReleasePath> moveOrOpen() {
            return isFinished()
                   ? Stream.empty()
                   : Stream.concat(openValve(), move());
        }

        private Stream<ReleasePath> openValve() {
            if (isOpened()) {
                return Stream.empty();
            }
            int newMinutes = minute + 1;
            long newPressure = pressure + (maxMinute - newMinutes) * currentValve.flowRate;
            return Stream.of(new ReleasePath(
                    requireNonNull(currentValve),
                    newPressure,
                    newMinutes,
                    openValves.add(currentValve.name),
                    new ValvesSet(currentValve.name),
                    history.add(currentValve.name + "+", newMinutes))
            );
        }

        private boolean isOpened() {
            return openValves.contains(currentValve.name);
        }

        private Stream<ReleasePath> move() {
            return currentValve.tunnels.stream().filter(this::canVisit).map(this::moveTo);
        }

        private boolean canVisit(Tunnel tunnel) {
            return !visitedValves.contains(tunnel.to);
        }

        private ReleasePath moveTo(Tunnel tunnel) {
            return new ReleasePath(
                    requireNonNull(valve(tunnel.to)),
                    pressure,
                    minute + tunnel.length,
                    openValves,
                    visitedValves.add(tunnel.to),
                    history.add(tunnel.to, minute + tunnel.length));
        }
    }

    record Valve(String name, int flowRate, Collection<Tunnel> tunnels) {
        boolean hasZeroFlowRate() {
            return flowRate == 0;
        }

        boolean leadsTo(String valve) {
            return tunnels.stream().map(Tunnel::to).anyMatch(valve::equals);
        }

        @Override
        public String toString() {
            return name + " has rate " + flowRate +
                    " leads to " + tunnels.stream().map(t -> t.to + "(" + t.length + ")").collect(joining(", "));
        }

        static Valve create(String line) {
            String name = valveName(line);
            return new Valve(
                    name,
                    flowRate(line),
                    tunnels(name, line)
            );
        }

        private static List<Tunnel> tunnels(String name, String line) {
            String tunnelsStr = substring(line, " to ");
            tunnelsStr = substring(tunnelsStr, " ");
            return split(tunnelsStr)
                    .map(String::trim)
                    .map(target -> Tunnel.create(name, target))
                    .toList();
        }

        private static int flowRate(String line) {
            return Integer.parseInt(substring(line, "flow rate=", ";").trim());
        }

        private static String valveName(String line) {
            return substring(line, "Valve", "has").trim();
        }
    }

    record Tunnel(String from, String to, int length) {
        Tunnel merge(Tunnel other) {
            return new Tunnel(from, other.to, length + other.length);
        }

        boolean leadsBack(Tunnel other) {
            return from.equals(other.to);
        }

        static Tunnel create(String from, String to) {
            return new Tunnel(from, to, 1);
        }
    }

    @SuppressWarnings("unused")
    private static final String INPUT1 = """
            Valve AA has flow rate=0; tunnels lead to valves DD, II, BB
            Valve BB has flow rate=13; tunnels lead to valves CC, AA
            Valve CC has flow rate=2; tunnels lead to valves DD, BB
            Valve DD has flow rate=20; tunnels lead to valves CC, AA, EE
            Valve EE has flow rate=3; tunnels lead to valves FF, DD
            Valve FF has flow rate=0; tunnels lead to valves EE, GG
            Valve GG has flow rate=0; tunnels lead to valves FF, HH
            Valve HH has flow rate=22; tunnel leads to valve GG
            Valve II has flow rate=0; tunnels lead to valves AA, JJ
            Valve JJ has flow rate=21; tunnel leads to valve II
            """;

    // HX+(2) -> 28 * 14
    // HH+(5) -> 25 * 12
    // SV+(9) -> 21 * 24
    // QR+(12) -> 18 * 20
    // OW+(15) -> 15 * 25
    // IN+(18) -> 12 * 16
    // LX+(22) -> 8 * 22
    // AW+(25) -> 5 * 21
    // PH+(28) -> 2 * 11
    @SuppressWarnings("unused")
    private static final String INPUT = """
            Valve ED has flow rate=0; tunnels lead to valves PS, AW
            Valve SI has flow rate=0; tunnels lead to valves AA, HX
            Valve LX has flow rate=22; tunnels lead to valves DY, YH
            Valve CR has flow rate=0; tunnels lead to valves BE, HX
            Valve BI has flow rate=0; tunnels lead to valves GC, AY
            Valve PB has flow rate=4; tunnels lead to valves IX, YG, RI, KR, BV
            Valve YY has flow rate=0; tunnels lead to valves PH, GJ
            Valve PH has flow rate=11; tunnels lead to valves YY, VE, ZG, MM
            Valve DY has flow rate=0; tunnels lead to valves LX, AW
            Valve SD has flow rate=0; tunnels lead to valves AY, EC
            Valve SV has flow rate=24; tunnels lead to valves CC, GF
            Valve RL has flow rate=0; tunnels lead to valves OW, IN
            Valve GF has flow rate=0; tunnels lead to valves RQ, SV
            Valve BE has flow rate=5; tunnels lead to valves CR, JC, MF, IT
            Valve PR has flow rate=0; tunnels lead to valves BV, GJ
            Valve AW has flow rate=21; tunnels lead to valves VE, DY, TR, ED
            Valve FY has flow rate=17; tunnels lead to valves GG, KJ
            Valve GC has flow rate=0; tunnels lead to valves BI, GJ
            Valve RI has flow rate=0; tunnels lead to valves PB, AY
            Valve RQ has flow rate=0; tunnels lead to valves HH, GF
            Valve IT has flow rate=0; tunnels lead to valves MZ, BE
            Valve XG has flow rate=0; tunnels lead to valves BL, AA
            Valve MK has flow rate=0; tunnels lead to valves HX, DV
            Valve IX has flow rate=0; tunnels lead to valves PB, JC
            Valve BV has flow rate=0; tunnels lead to valves PR, PB
            Valve TR has flow rate=0; tunnels lead to valves CD, AW
            Valve PS has flow rate=0; tunnels lead to valves ED, AY
            Valve HH has flow rate=12; tunnels lead to valves RQ, NL, ZQ
            Valve AA has flow rate=0; tunnels lead to valves KR, SI, XG, EC, ZG
            Valve FT has flow rate=0; tunnels lead to valves IN, YH
            Valve YG has flow rate=0; tunnels lead to valves PB, HX
            Valve HX has flow rate=14; tunnels lead to valves MK, ZQ, YG, SI, CR
            Valve DV has flow rate=0; tunnels lead to valves MK, QR
            Valve GJ has flow rate=3; tunnels lead to valves PR, CD, YY, GC, BL
            Valve BL has flow rate=0; tunnels lead to valves GJ, XG
            Valve CD has flow rate=0; tunnels lead to valves TR, GJ
            Valve GG has flow rate=0; tunnels lead to valves FY, NL
            Valve JC has flow rate=0; tunnels lead to valves IX, BE
            Valve JN has flow rate=0; tunnels lead to valves OW, QR
            Valve RM has flow rate=18; tunnel leads to valve KJ
            Valve NL has flow rate=0; tunnels lead to valves GG, HH
            Valve QR has flow rate=20; tunnels lead to valves CC, DV, PN, JN
            Valve ZG has flow rate=0; tunnels lead to valves AA, PH
            Valve AY has flow rate=6; tunnels lead to valves RI, PS, SD, BI, MM
            Valve VE has flow rate=0; tunnels lead to valves PH, AW
            Valve OW has flow rate=25; tunnels lead to valves MZ, RL, JN
            Valve MM has flow rate=0; tunnels lead to valves AY, PH
            Valve KJ has flow rate=0; tunnels lead to valves RM, FY
            Valve MF has flow rate=0; tunnels lead to valves BE, PN
            Valve YH has flow rate=0; tunnels lead to valves LX, FT
            Valve ZQ has flow rate=0; tunnels lead to valves HX, HH
            Valve KR has flow rate=0; tunnels lead to valves AA, PB
            Valve PN has flow rate=0; tunnels lead to valves MF, QR
            Valve CC has flow rate=0; tunnels lead to valves SV, QR
            Valve MZ has flow rate=0; tunnels lead to valves OW, IT
            Valve EC has flow rate=0; tunnels lead to valves SD, AA
            Valve IN has flow rate=16; tunnels lead to valves RL, FT
            """;

    @Test
    public void test() {

    }
}