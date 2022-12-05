package done.misc;

import java.util.Arrays;
import java.util.Scanner;

public class Solution2 {

    static long maxK = 0L;

    static int ARRAY_SIZE = 20_000;
    static Interval interval;
    static Interval[] intervals;
    static int step = 1;

    public static void main(String[] args) {
        try (Scanner in = new Scanner(System.in)) {
            int n = in.nextInt();
            int m = in.nextInt();

            init(n);

            for (int i = 0; i < m; i++) {
                in.nextLine();
                nextLine(in.nextInt(), in.nextInt(), in.nextInt());
            }
        }

        System.out.println(maxK);
    }

    static void init(int n) {
        interval = new Interval(1, n, 0);
        if (n <= ARRAY_SIZE) {
            step = 1;
            intervals = new Interval[n];
        }
        else {
            step = (n / ARRAY_SIZE) + 1;
            intervals = new Interval[n / step];
        }

        Arrays.fill(intervals, interval);
    }

    static void nextLine(int a, int b, long k) {
        interval(a).add(a, b, k);
    }

    static Interval interval(int a) {
        int idx = a / step;
        if (step == 1) {
            idx--;
        }
        return intervals[idx];
    }

    static void update(int a, int b, Interval interval) {
        int start = a / step;
        int end = b / step;
        if (step == 1) {
            start--;
            end--;
        }
        if (start < end) {
            Arrays.fill(intervals, start, end, interval);
        }
        else {
            intervals[start] = interval;
        }
    }

    static void update(Interval interval) {
        update(interval.a, interval.b, interval);
    }

    static class Interval {
        int a;
        int b;
        long k;

        Interval left;
        Interval right;

        Interval(int a, int b, long k) {
            this.a = a;
            this.b = b;
            this.k = k;
            updateMax();
        }

        void add(int _a, int _b, long _k) {
            if (a <= _b && _b < b) {
                splitRight(_b);
            }
            if (a < _a && _a <= b) {
                splitLeft(_a);
            }

            if (_a <= a && b <= _b) {
                k += _k;
                updateMax();
            }

            if (_a < a) {
                updateLeft(_a, Math.min(_b, a-1), _k);
            }
            if (b < _b) {
                updateRight(Math.max(b+1, _a), _b, _k);
            }
        }

        private void splitRight(int _b) {
            Interval newRight = new Interval(_b+1, b, k);
            update(newRight);
            newRight.left = this;

            if (right != null) {
                right.left = newRight;
                newRight.right = right;
            }

            right = newRight;
            b = _b;
        }

        private void splitLeft(int _a) {
            Interval newLeft = new Interval(a, _a-1, k);
            update(newLeft);
            newLeft.right = this;

            if (left != null) {
                left.right = newLeft;
                newLeft.left = left;
            }

            left = newLeft;
            a = _a;
        }

        private void updateRight(int _a, int _b, long _k) {
            if (_a <= _b && _k > 1 && right != null) {
                right.add(_a, _b, _k);
            }
        }

        private void updateLeft(int _a, int _b, long _k) {
            if (_a <= _b && _k > 1 && left != null) {
                left.add(_a, _b, _k);
            }
        }

        private void updateMax() {
            if (k > maxK) {
                maxK = k;
            }
        }
    }
}
