package done;

import java.util.*;

public class Solution {

    static Component[] components;
    static Set<Component> componentSet = new HashSet<>();
    static SortedSet<Component> sorted;

    public static void main(String[] args) {
        try (Scanner in = new Scanner(System.in)) {
            int N = in.nextInt();

            init(N);

            for (int i = 0; i < N; i++) {
                in.nextLine();
                int G = in.nextInt();
                int B = in.nextInt();

                edge(G, B);
            }

            finish();

            System.out.println(minimum() + " " + maximum());
        }
    }

    static void finish() {
        components = null;
        sorted = new TreeSet<>(componentSet);
    }

    static int minimum() {
        return sorted.first().set.size();
    }

    static int maximum() {
        return sorted.last().set.size();
    }

    static void edge(int g, int b) {
        if (components[g - 1] != null) {
            components[g - 1].merge(b);
        } else if (components[b - 1] != null) {
            components[b - 1].add(g);
        } else {
            new Component(g, b);
        }
    }

    static void init(int n) {
        components = new Component[2 * n];
        minimum = 2*n;
        maximum = 0;
    }

    static int minimum = 0;
    static int maximum = 0;
    static int nextId = 0;

    static class Component implements Comparable<Component> {
        Set<Integer> set = new HashSet<>();
        int id = ++nextId;

        Component(int a, int b) {
            set.add(a);
            set.add(b);
            components[a - 1] = this;
            components[b - 1] = this;

            componentSet.add(this);
        }

        void merge(int b) {
            Component componentB = components[b - 1];
            if (componentB != null) {
                if (componentB != this) {
                    set.addAll(componentB.set);
                    for (int n : componentB.set) {
                        components[n - 1] = this;
                    }
                    componentSet.remove(componentB);
                }
            } else {
                add(b);
            }
        }

        private void add(int b) {
            set.add(b);
            components[b - 1] = this;
        }

        @Override
        public int compareTo(Component o) {
            int cmp = Integer.compare(set.size(), o.set.size());
            return cmp == 0 ? Integer.compare(id, o.id) : cmp;
        }
    }

}
