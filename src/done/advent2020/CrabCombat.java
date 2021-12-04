package done.advent2020;

import org.junit.Test;
import util.BatchSupplier;
import util.Utils.IntIndex;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.function.Predicate.not;

@SuppressWarnings("unused")
public class CrabCombat {

    static final IntIndex gameCounter = new IntIndex(1);
    static Player[] players;

    public static void main(String[] args) throws IOException {
        PrintStream OUT = System.out;

        try {
            //System.setOut(new PrintStream(Files.newOutputStream(Path.of("C:/temp/crab.txt"))));
            playCrabGame();
        } finally {
            System.setOut(OUT);
        }
    }

    private static void playCrabGame() {
        try (Scanner in = new Scanner(INPUT2)) {
            players = Stream.generate(new BatchSupplier(in).withDelimiter(';'))
                            .takeWhile(Objects::nonNull)
                            .filter(not(String::isBlank))
                            .map(Player::of)
                            .toArray(Player[]::new);


            final Game game = new Game(players[0], players[1]);

            game.playGame();

            List<Integer> winnerCards = new ArrayList<>(game.winner().cards);
            Collections.reverse(winnerCards);
            IntIndex multiplier = new IntIndex(1);

            long result = winnerCards.stream()
                                     .mapToLong(Integer::longValue)
                                     .map(card -> card * multiplier.getAsInt())
                                     .sum();

            System.out.println("Result: " + result);
        }
    }

    static class Game {
        final int gameNumber = gameCounter.getAsInt();
        final IntIndex roundCounter = new IntIndex(1);
        final Player player1;
        final Player player2;
        Player winner;
        boolean finished;

        Game(Player player1, Player player2) {
            this.player1 = player1;
            this.player2 = player2;
            this.winner = player1;
        }

        void playCards() {
            int round = roundCounter.getAsInt();
            System.out.println("-- Round " + round + " (Game " + gameNumber + ") --");

            player1.printDeck();
            player2.printDeck();

            if (player1.deckWasPlayed() || player2.deckWasPlayed()) {
                System.out.println("Preventing infinite loop winner is player 1\n");
                winner = player1;
                finished = true;
                return;
            }

            int c1 = player1.playCard();
            int c2 = player2.playCard();

            if (player1.shouldPlaySubGame(c1) && player2.shouldPlaySubGame(c2)) {
                System.out.println("Playing a sub-game to determine the winner...\n");
                Game subgame = new Game(player1.copyWithCardCount(c1), player2.copyWithCardCount(c2));
                subgame.playGame();
                winner = subgame.winnerIsPlayer1() ? player1 : player2;
            } else {
                winner = c1 > c2 ? player1 : player2;
            }

            addCards(c1, c2);

            System.out.println(winner.name + " wins the round of game " + gameNumber + "!\n");
        }

        private void addCards(int c1, int c2) {
            if (player1 == winner) {
                player1.addCards(c1, c2);
            } else {
                player2.addCards(c2, c1);
            }
        }

        boolean canPlay() {
            return !finished && player1.canPlay() && player2.canPlay();
        }

        Player winner() {
            return winner;
        }

        boolean winnerIsPlayer1() {
            return winner == player1;
        }

        void playGame() {
            System.out.println("=== Game " + gameNumber + " ===");
            while (canPlay()) {
                playCards();
            }
        }
    }

    static class Player {
        final String name;
        final Deque<Integer> cards = new ArrayDeque<>();
        final Set<String> playedDecks = new HashSet<>();

        Player(String name, int[] cards) {
            this.name = name;
            Arrays.stream(cards).forEach(this.cards::add);
        }

        Player(String name, Collection<Integer> cards, int count) {
            this.name = name;
            cards.stream().limit(count).forEach(this.cards::add);
        }

        boolean deckWasPlayed() {
            return playedDecks.contains(String.valueOf(cards));
        }

        boolean canPlay() {
            return !cards.isEmpty();
        }

        int playCard() {
            playedDecks.add(String.valueOf(cards));

            Integer card = cards.pop();
            System.out.println(name + " plays: " + card);
            return card;
        }

        boolean shouldPlaySubGame(int card) {
            return card <= cards.size();
        }

        Player copyWithCardCount(int count) {
            return new Player(name, cards, count);
        }

        void printDeck() {
            System.out.println(name + "'s deck: " + cards);
        }

        void addCards(int c1, int c2) {
            cards.add(c1);
            cards.add(c2);
        }

        static Player of(String batch) {
            String[] lines = batch.split(";");
            String name = lines[0].substring(0, lines[0].length() - 1);
            int[] cards = Stream.of(lines)
                                .skip(1)
                                .map(String::trim)
                                .mapToInt(Integer::parseInt)
                                .toArray();
            return new Player(name, cards);
        }
    }

    private static final String INPUT = """
                                        Player 1:
                                        9
                                        2
                                        6
                                        3
                                        1
                                                                            
                                        Player 2:
                                        5
                                        8
                                        4
                                        7
                                        10            
                                        """;

    private static final String INPUT1 = """
                                        Player 1:
                                        43
                                        19
                                        
                                        Player 2:
                                        2
                                        29
                                        14
                                        """;

    private static final String INPUT2 = """
                                         Player 1:
                                         30
                                         42
                                         25
                                         7
                                         29
                                         1
                                         16
                                         50
                                         11
                                         40
                                         4
                                         41
                                         3
                                         12
                                         8
                                         20
                                         32
                                         38
                                         31
                                         2
                                         44
                                         28
                                         33
                                         18
                                         10
                                                  
                                         Player 2:
                                         36
                                         13
                                         46
                                         15
                                         27
                                         45
                                         5
                                         19
                                         39
                                         24
                                         14
                                         9
                                         17
                                         22
                                         37
                                         47
                                         43
                                         21
                                         6
                                         35
                                         23
                                         48
                                         34
                                         26
                                         49
                                         """;

    @Test
    public void test() {

    }
}