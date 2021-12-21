package done.advent2021;

import org.junit.Test;

import java.util.EnumMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.Stream;

import static done.advent2021.DiracDice.QuantumDice.quantumRoll;
import static java.util.function.Predicate.not;

public class DiracDice {

    private static final int WINNING_SCORE = 1000;
    private static final int QUANTUM_WINNING_SCORE = 21;

    private static final Map<Game, LongAdder> games = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        try (Scanner in = new Scanner(INPUT)) {
            Player player1 = Player.from(in.nextLine());
            Player player2 = Player.from(in.nextLine());

            long result = playSimple(player1, player2);
            System.out.println("Result: " + result);

            Game game = new Game(player1, player2);
            gameAdder(game).increment();

            while (anyGameNotFinished()) {
                gamesTurn();
                System.out.println("Games: " + games.values().stream().mapToLong(LongAdder::longValue).sum());
            }

            Map<Winner, LongAdder> results = createResults();
            long p1Score = results.get(Winner.P1).longValue();
            long p2Score = results.get(Winner.P2).longValue();

            long noneScore = results.get(Winner.NONE).longValue();
            System.out.println("none: " + noneScore);

            long result2 = Math.max(p1Score, p2Score);
            System.out.println("Result2: " + result2);
        }
    }

    private static Map<Winner, LongAdder> createResults() {
        Map<Winner, LongAdder> results = new EnumMap<>(Winner.class);
        results.put(Winner.P1, new LongAdder());
        results.put(Winner.P2, new LongAdder());
        results.put(Winner.NONE, new LongAdder());
        games.forEach((state, count) -> results.get(state.winner()).add(count.longValue()));
        return results;
    }

    private static boolean anyGameNotFinished() {
        return games.keySet().stream().anyMatch(not(Game::isFinished));
    }

    private static void gamesTurn() {
        Map<Game, LongAdder> nextState = new ConcurrentHashMap<>();
        games.keySet().stream()
             .filter(not(Game::isFinished))
             .forEach(g -> gameTurn(nextState, g));
        games.keySet().removeIf(not(Game::isFinished));
        nextState.forEach((state, count) -> gameAdder(state).add(count.longValue()));
    }

    private static void gameTurn(Map<Game, LongAdder> nextState, Game game) {
        long count = gameAdder(game).longValue();
        game.turn().forEach(g -> gameAdder(nextState, g).add(count));
        gameAdder(game).reset();
    }

    private static LongAdder gameAdder(Game g) {
        return gameAdder(games, g);
    }

    private static LongAdder gameAdder(Map<Game, LongAdder> games, Game g) {
        return games.computeIfAbsent(g, k -> new LongAdder());
    }

    private static long playSimple(Player player1, Player player2) {
        Dice dice = new Dice();

        while (true) {
            player1 = player1.move(dice.trippleRoll());
            if (player1.score() >= WINNING_SCORE) {
                break;
            }
            player2 = player2.move(dice.trippleRoll());
            if (player2.score() >= WINNING_SCORE) {
                break;
            }
        }

        long loosingScore = player1.score() >= WINNING_SCORE ? player2.score() : player1.score();
        return dice.rolled() * loosingScore;
    }

    enum Winner {
        P1, P2, NONE
    }

    record Game(Player p1, Player p2) {
        Stream<Game> turn() {
            if (isFinished()) {
                return Stream.of(this);
            }
            return quantumRoll()
                    .map(this::play1)
                    .flatMap(g1 -> g1.isFinished()
                                   ? Stream.of(g1)
                                   : quantumRoll().map(g1::play2));
        }

        Game play1(QuantumTripple tripple) {
            if (isFinished()) {
                return this;
            }
            return new Game(p1.move(tripple.roll()), p2);
        }

        Game play2(QuantumTripple tripple) {
            if (isFinished()) {
                return this;
            }
            return new Game(p1, p2.move(tripple.roll()));
        }

        boolean isFinished() {
            return player1Wins() || player2Wins();
        }

        boolean player1Wins() {
            return p1.score() >= QUANTUM_WINNING_SCORE;
        }

        boolean player2Wins() {
            return p2.score() >= QUANTUM_WINNING_SCORE;
        }

        Winner winner() {
            return player1Wins() ? Winner.P1 : player2Wins() ? Winner.P2 : Winner.NONE;
        }
    }

    record Player(int id, int position, int score) {
        Player {
            if (position < 0 || position >= 10) {
                throw new IllegalArgumentException("Invalid player position: " + position);
            }
        }

        Player move(long inc) {
            int newPos = (int) ((position + inc) % 10);
            return new Player(id, newPos, score + newPos + 1);
        }

        @Override
        public String toString() {
            return "Player " + id + " position: " + (position + 1);
        }

        static Player from(String line) {
            String[] parts = line.split("[ ]");
            return new Player(Integer.parseInt(parts[1]), Integer.parseInt(parts[4]) - 1 , 0);
        }
    }

    static class Dice {
        private long value = 1L;

        long roll() {
            return value++;
        }

        long trippleRoll() {
            return roll() + roll() + roll();
        }

        long rolled() {
            return value - 1;
        }
    }

    record QuantumTripple(QuantumDice d1, QuantumDice d2, QuantumDice d3) {
        int roll() {
            return d1.roll() + d2.roll() + d3.roll();
        }
    }

    enum QuantumDice {
        ONE,
        TWO,
        THREE,
        ;
        int roll() {
            return ordinal() + 1;
        }

        static Stream<QuantumDice> all() {
            return Stream.of(ONE, TWO, THREE);
        }

        static Stream<QuantumTripple> quantumRoll() {
            return all().flatMap(d1 ->
                    all().flatMap(d2 ->
                            all().map(d3 -> new QuantumTripple(d1, d2, d3))));
        }
    }

    @SuppressWarnings("unused")
    private static final String INPUT1 = """
            Player 1 starting position: 4
            Player 2 starting position: 8""";

    @SuppressWarnings("unused")
    private static final String INPUT = """
            Player 1 starting position: 6
            Player 2 starting position: 7""";

    @Test
    public void test() {

    }
}