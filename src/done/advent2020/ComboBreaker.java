package done.advent2020;

import org.junit.Test;

@SuppressWarnings("unused")
public class ComboBreaker {

    //static long doorsPubKey = 17_807_724L;
    static long doorsPubKey = 15_065_270L;
    static long doorsLoopSize;
    static long doorsEncryptionKey;

    //static long cardsPubKey = 5_764_801L;
    static long cardsPubKey = 17_607_508L;

    static long cardsLoopSize;
    static long cardsEncryptionKey;

    static long seed = 2020_12_27;
    static long subjectKey = 7;

    public static void main(String[] args) {
        cardsLoopSize = determineLoopSize(cardsPubKey);
        System.out.println("Card's loop size: " + cardsLoopSize);
        doorsLoopSize = determineLoopSize(doorsPubKey);
        System.out.println("Door's loop size: " + doorsLoopSize);

        cardsEncryptionKey = executeLoop(cardsPubKey, doorsLoopSize);
        System.out.println("Card's encryption key: " + cardsEncryptionKey);
        doorsEncryptionKey = executeLoop(doorsPubKey, cardsLoopSize);
        System.out.println("Door's encryption key: " + doorsEncryptionKey);
    }

    static long nextValue(long value, long subj) {
        return (value * subj) % seed;
    }

    static long determineLoopSize(long pubKey) {
        int loop = 0;
        long value = 1L;
        while (value != pubKey) {
            loop++;
            value = nextValue(value, 7);
        }
        return loop;
    }

    static long executeLoop(long subjectKey, long loopSize) {
        int loop = 0;
        long value = 1L;
        while (loop++ < loopSize) {
            value = nextValue(value, subjectKey);
        }
        return value;
    }

    @Test
    public void test() {

    }
}