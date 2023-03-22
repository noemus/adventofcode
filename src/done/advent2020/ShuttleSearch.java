package done.advent2020;

import org.junit.jupiter.api.Test;

import java.util.Scanner;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class ShuttleSearch {

    static int startTime;
    static int[] numbers;

    public static void main(String[] args) {
        try (Scanner in = new Scanner(INPUT2)) {
            startTime = in.nextInt();
            in.nextLine();
            Route[] routes = Stream
                    .of(in.nextLine().split(","))
                    .map(Route::toRoute)
                    .toArray(Route[]::new);

            int validLines = (int) Stream.of(routes)
                    .filter(route -> route.busLine != 0)
                    .count();

            int delta = 0;
            for (Route route : routes) {
                if (route.busLine != 0) {
                    route.remainder = ((route.busLine - delta) + 5*route.busLine) % route.busLine;
                    route.coef = validLines / gcd(route.busLine, validLines);
                    System.out.println(route);
                }
                delta++;
            }

            int firstModulus = Stream.of(routes)
                    .filter(route -> route.busLine != 0)
                    .mapToInt(route -> route.busLine)
                    .findFirst()
                    .orElse(0);

            long product = Stream.of(routes)
                    .filter(route -> route.busLine != 0)
                    .mapToLong(route -> route.busLine)
                    .reduce((x, y) -> x * y)
                    .orElse(0L);

            System.out.println("Product: " + product);

            Route[] rest = Stream.of(routes)
                    .filter(route -> route.busLine != 0)
                    .toArray(Route[]::new);

//            int minTime = IntStream.of(busLines)
//                    .mapToObj(Route::toRoute)
//                    .min(Comparator.comparing(Route::time))
//                    .map(Route::result)
//                    .orElse(0);

            long result;
            for (result = inc; result < product; result += inc) {
                if (matches(rest, result)) {
                    break;
                }
            }

            printPartialResult(routes, result);

            int actual   = 3162341;
            int expected = 1068781;
            System.out.println("Result: " + result);
        }
    }

    private static void printPartialResult(Route[] routes, long result) {
        long finalResult = result;
        Stream.of(routes)
                .filter(route -> route.busLine != 0)
                .forEach(r -> System.out.println("Remainder: " + (finalResult % r.busLine)));
    }

    static long inc = 1;
    private static boolean matches(Route[] rest, long k) {
        for (Route r : rest) {
            if ((k % r.busLine) == r.remainder) {
                if ((inc % r.busLine) != 0) {
                    System.out.println("Found: candidate for busId = " + r.busLine + ", k = " + k);
                    printPartialResult(rest, k);
                    inc = inc * r.busLine;
                }
            } else {
                return false;
            }
        }
        return true;
    }

    static class Route {
        final int busLine;
        int remainder;
        int coef;

        private Route(String busId) {
            if ("x".equals(busId)) {
                busLine = 0;
            } else {
                busLine = Integer.parseInt(busId);
            }
        }

        static Route toRoute(String busLine) {
            //return new Route(busLine, busLine - (startTime % busLine));
            return new Route(busLine);
        }

//        int result() {
//            return busLine * time;
//        }
//
//        int time() {
//            return time;
//        }

        @Override
        public String toString() {
            return "busID: " + busLine + ", remainder = " + remainder;
        }
    }

    private static int gcd(int p, int q) {
        if (q == 0) {
            return p;
        }
        return gcd(q, p % q);
    }

    private static final String INPUT = "" +
            "939\n" +
            "7,13,x,x,59,x,31,19\n";

    private static final String INPUT2 = "" +
            "1002460\n" +
            "29,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,41,x,x,x,x,x,x,x,x,x,601,x,x,x,x,x,x,x,23,x,x,x,x,13,x,x,x,17,x,19,x,x,x,x,x,x,x,x,x,x,x,463,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,37\n";

    @Test
    public void test() {
        int startTime = 939;
        final int[] busLines = {7, 13, 59, 31, 19};
        for (int busLine : busLines) {
            System.out.println("busLine " + busLine + ": modulo == " + (busLine - (startTime % busLine)));
        }
    }
}