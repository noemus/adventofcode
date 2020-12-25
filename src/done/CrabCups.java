package done;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;

import static java.util.Objects.requireNonNull;
import static org.junit.Assert.assertEquals;

@SuppressWarnings("unused")
public class CrabCups {

    static final int[] INITIAL_CUPS_1 = {3, 8, 9, 1, 2, 5, 4, 6, 7};
    static final int[] INITIAL_CUPS_2 = {1, 9, 3, 4, 6, 7, 2, 5, 8};

    static class Game {
        final Set<Integer> pickUp = new LinkedHashSet<>();
        final List<CyclicList.Entry> pickUpEntries = new ArrayList<>();
        final CyclicList cups = new CyclicList();
        final int max;
        CyclicList.Entry pos;

        Game(int[] cups, int max) {
            for (int cup : cups) {
                this.cups.add(cup);
            }
            this.max = max;

            IntStream.range(cups.length + 1, max + 1).forEach(this.cups::add);
            this.pos = this.cups.first;
        }

        int selected() {
            return cups.first.value;
        }

        void advance(int moveNum) {
            System.out.println("-- move " + (moveNum + 1) + " --");
            //printCups();

            pickUp3();
            //System.out.println("pick up: " + pickUp);

            int dest = destination();
            //System.out.println("destination: " + dest);

            move3(dest);

            incPos();
        }

        void incPos() {
            cups.moveForward();
            pos = cups.first;
        }

        void move3(int dest) {
            cups.moveTo(dest);

            final CyclicList.Entry pickup1 = pickUpEntries.get(0);
            final CyclicList.Entry pickup2 = pickUpEntries.get(1);
            final CyclicList.Entry pickup3 = pickUpEntries.get(2);

            cups.insert(pickup1);
            pickup1.addAfter(pickup2);
            pickup2.addAfter(pickup3);

            cups.moveTo(pos);
        }

        void pickUp3() {
            pickUpEntries.clear();
            pickUpEntries.add(cups.removeNext());
            pickUpEntries.add(cups.removeNext());
            pickUpEntries.add(cups.removeNext());

            pickUp.clear();
            pickUp.add(pickUpEntries.get(0).value);
            pickUp.add(pickUpEntries.get(1).value);
            pickUp.add(pickUpEntries.get(2).value);
        }

        int destination() {
            final int selected = selected();
            int dest = selected - 1;
            while (isPickedUp(dest)) {
                if (dest < 1) {
                    dest = max;
                } else {
                    dest--;
                }
            }
            return dest;
        }

        boolean isPickedUp(int num) {
            return num == 0 || pickUp.contains(num);
        }

        private void printCups() {
            System.out.print("cups:");
            int sel = selected();
            final CyclicList.Entry first = cups.first;
            do {
                int cup = cups.first.value;
                System.out.print(' ');
                if (cup == sel) {
                    System.out.print('(');
                    System.out.print(cup);
                    System.out.print(')');
                } else {
                    System.out.print(cup);
                }
                cups.moveForward();
            } while(cups.first != first);

            System.out.println();
        }

        void printAfterOne() {
            System.out.print("final: ");
            cups.moveTo(1);
            cups.moveForward();
            final long first = cups.first.value;
            System.out.print(first);
            cups.moveForward();
            System.out.print(" * ");
            final long second = cups.first.value;
            System.out.print(second);
            System.out.print(" = ");
            System.out.print(first * second);
        }
    }

    static class CyclicList {
        private final Map<Integer, Entry> entryMap = new HashMap<>();

        Entry first = null;
        Entry last = null;

        int size = 0;

        Entry position(int value) {
            return entryMap.get(value);
        }

        void add(int value) {
            final Entry newEntry = new Entry(value);
            if (last == null) {
                first = last = newEntry;
            } else {
                last.addAfter(newEntry);
                last = newEntry;
            }
            entryMap.put(value, newEntry);
        }

        void insert(Entry entry) {
            first.addAfter(entry);
        }

        Entry removeNext() {
            Entry removed = first.next;
            first.next = removed.next;
            first.next.prev = first;
            return removed;
        }

        void moveTo(int value) {
            Entry entry = entryMap.get(value);
            moveTo(entry);
        }

        void moveTo(Entry entry) {
            first = requireNonNull(entry);
            last = first.prev;
        }

        void moveForward() {
            moveTo(first.next);
        }

        private static class Entry {
            int value;
            Entry prev;
            Entry next;

            private Entry(int value) {
                this.value = value;
                this.prev = this;
                this.next = this;
            }

            private void addAfter(Entry newEntry) {
                newEntry.next = next;
                newEntry.prev = this;
                next = newEntry;
            }
        }
    }

    public static void main(String[] args) {
        //723850 * 655865 = 474747880250
        Game game = new Game(INITIAL_CUPS_1, 1_000_000);
        IntStream.range(0, 10_000_000).forEach(game::advance);

        game.printAfterOne();
    }

    @Test
    public void test() {
        final Game game1 = new Game(INITIAL_CUPS_1, 10);
        game1.pickUp3();
        assertEquals(2, game1.destination());

        final Game game2 = new Game(INITIAL_CUPS_2, 10);
        game2.pickUp3();
        assertEquals(8, game2.destination());
    }
}