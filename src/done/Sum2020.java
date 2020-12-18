package done;

import java.util.Scanner;
import java.util.function.IntSupplier;
import java.util.stream.IntStream;

public class Sum2020 {

    public static void main(String[] args) {
        try (Scanner in = new Scanner(INPUT)) {
            IntSupplier supplier = () -> {
                if (in.hasNext()) {
                    int n = in.nextInt();
                    if (in.hasNext()) in.nextLine();
                    return n;
                }
                return -1;
            };

            int[] numbers = IntStream.generate(supplier)
                    .takeWhile(i -> i >= 0)
                    .toArray();

            int min = IntStream.of(numbers).min().orElse(0);

            int result = 0;
            for (int i = 0; i < numbers.length; i++) {
                int number = numbers[i];
                IntSupplier counter = new Counter();
                result = IntStream.of(numbers)
                        .skip(i + 1L)
                        .filter(n -> (n + number) <= 2020 - min)
                        .mapToObj(n -> new Partial(counter.getAsInt(), n, number))
                        .mapToInt(pair -> IntStream.of(numbers)
                                .skip(pair.skip)
                                .filter(n -> (n + pair.sum) == 2020)
                                .map(n -> n * pair.mult)
                                .findFirst()
                                .orElse(0))
                        .filter(n -> n > 0)
                        .findFirst()
                        .orElse(0);
                if (result > 0) {
                    break;
                }
            }
            System.out.println("Result: " + result);
        }
    }

    private static final String INPUT =
            "1945\n" +
            "2004\n" +
            "1520\n" +
            "1753\n" +
            "1463\n" +
            "1976\n" +
            "1994\n" +
            "1830\n" +
            "1942\n" +
            "1784\n" +
            "1858\n" +
            "1841\n" +
            "1721\n" +
            "1480\n" +
            "1821\n" +
            "1584\n" +
            "978\n" +
            "1530\n" +
            "1278\n" +
            "1827\n" +
            "889\n" +
            "1922\n" +
            "1996\n" +
            "1992\n" +
            "1819\n" +
            "1847\n" +
            "2010\n" +
            "2002\n" +
            "210\n" +
            "1924\n" +
            "1482\n" +
            "1451\n" +
            "1867\n" +
            "1364\n" +
            "1578\n" +
            "1623\n" +
            "1117\n" +
            "1594\n" +
            "1476\n" +
            "1879\n" +
            "1797\n" +
            "1952\n" +
            "2005\n" +
            "1734\n" +
            "1898\n" +
            "1880\n" +
            "1330\n" +
            "1854\n" +
            "1813\n" +
            "1926\n" +
            "1686\n" +
            "1286\n" +
            "1808\n" +
            "1876\n" +
            "1366\n" +
            "1995\n" +
            "1632\n" +
            "1699\n" +
            "2001\n" +
            "1365\n" +
            "1343\n" +
            "1979\n" +
            "1868\n" +
            "1815\n" +
            "820\n" +
            "1966\n" +
            "1888\n" +
            "1916\n" +
            "1852\n" +
            "1932\n" +
            "1368\n" +
            "1606\n" +
            "1825\n" +
            "1731\n" +
            "1980\n" +
            "1990\n" +
            "1818\n" +
            "1702\n" +
            "1419\n" +
            "1897\n" +
            "1970\n" +
            "1276\n" +
            "1914\n" +
            "1889\n" +
            "1953\n" +
            "1588\n" +
            "1958\n" +
            "1310\n" +
            "1391\n" +
            "1326\n" +
            "1131\n" +
            "1959\n" +
            "1844\n" +
            "1307\n" +
            "1998\n" +
            "1961\n" +
            "1708\n" +
            "1977\n" +
            "1886\n" +
            "1946\n" +
            "1516\n" +
            "1999\n" +
            "1859\n" +
            "1931\n" +
            "1853\n" +
            "1265\n" +
            "1869\n" +
            "1642\n" +
            "1740\n" +
            "1467\n" +
            "1944\n" +
            "1956\n" +
            "1263\n" +
            "1940\n" +
            "1912\n" +
            "1832\n" +
            "1872\n" +
            "1678\n" +
            "1319\n" +
            "1839\n" +
            "1689\n" +
            "1765\n" +
            "1894\n" +
            "1242\n" +
            "1983\n" +
            "1410\n" +
            "1985\n" +
            "1387\n" +
            "1022\n" +
            "1358\n" +
            "860\n" +
            "112\n" +
            "1964\n" +
            "1836\n" +
            "1838\n" +
            "1285\n" +
            "1943\n" +
            "1718\n" +
            "1351\n" +
            "760\n" +
            "1925\n" +
            "1842\n" +
            "1921\n" +
            "1967\n" +
            "1822\n" +
            "1978\n" +
            "1837\n" +
            "1378\n" +
            "1618\n" +
            "1266\n" +
            "2003\n" +
            "1972\n" +
            "666\n" +
            "1321\n" +
            "1938\n" +
            "1616\n" +
            "1892\n" +
            "831\n" +
            "1865\n" +
            "1314\n" +
            "1571\n" +
            "1806\n" +
            "1225\n" +
            "1882\n" +
            "1454\n" +
            "1257\n" +
            "1381\n" +
            "1284\n" +
            "1907\n" +
            "1950\n" +
            "1887\n" +
            "1492\n" +
            "1934\n" +
            "1709\n" +
            "1315\n" +
            "1574\n" +
            "1794\n" +
            "1576\n" +
            "1883\n" +
            "1864\n" +
            "1981\n" +
            "1317\n" +
            "1397\n" +
            "1325\n" +
            "1620\n" +
            "1895\n" +
            "1485\n" +
            "1828\n" +
            "1803\n" +
            "1715\n" +
            "1374\n" +
            "1251\n" +
            "1460\n" +
            "1863\n" +
            "1581\n" +
            "1499\n" +
            "1933\n" +
            "1982\n" +
            "1809\n" +
            "1812";

    static class Partial {
        final int skip;
        final int sum;
        final int mult;

        Partial(int skip, int n1, int n2) {
            this.skip = skip;
            this.sum = n1 + n2;
            this.mult = n1 * n2;
        }
    }

    static class Counter implements IntSupplier {
        private int i;

        @Override
        public int getAsInt() {
            return ++i;
        }
    }
}
