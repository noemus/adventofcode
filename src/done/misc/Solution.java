package done.misc;

import org.junit.Test;

import java.util.HashSet;
import java.util.List;
import java.util.stream.IntStream;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.summingInt;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

class Result {
    public static void checkMagazine(List<String> magazine, List<String> note) {
        System.out.println(containsAllWords(magazine, note) ? Answer.Yes : Answer.No);
    }

    static boolean containsAllWords(List<String> magazine, List<String> note) {
        var missing = new HashSet<>();
        var words = magazine.stream().collect(groupingBy(identity(), summingInt(s -> 1)));
        note.forEach(word -> {
             int count = words.getOrDefault(word, 0);
             if (count > 0) {
                 words.put(word, count - 1);
             } else {
                 missing.add(word);
             }
        });
        return missing.isEmpty();
    }
}

enum Answer {
    Yes, No
}

public class Solution {

    static IntStream rotLeft(int[] a, int d) {
        return IntStream.range(d, d + a.length)
                        .map(i -> i % a.length)
                        .map(i -> a[i]);
    }

    static String printStream(IntStream stream) {
        return stream.mapToObj(Integer::toString)
                     .collect(joining(" ", "", "\n"));
    }

    @Test
    public void test() {
        assertTrue(Result.containsAllWords(List.of("give","me","one","grand","today","night"), List.of("give","one","grand","today")));
        assertFalse(Result.containsAllWords(List.of("two","times","three","is","not","four"), List.of("two","times","two","is","four")));
        assertFalse(Result.containsAllWords(List.of("give","me","one","grand","today","night"), List.of("give","one","grand","grand","today")));
    }
}
