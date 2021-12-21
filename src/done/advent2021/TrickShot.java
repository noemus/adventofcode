package done.advent2021;

import java.util.stream.IntStream;

import static java.util.function.Function.identity;

public class TrickShot {
    static int xMin = 244, xMax = 303, yMin = -91, yMax = -54;
    static int minVelX = 22, maxVelX = 303, minVelY = -91, maxVelY = 90;

    static int maxSteps = 2 * (maxVelY + 1);

    public static void main(String[] args) {
//        List<Velocity> velocities = IntStream.rangeClosed(minVelX, maxVelX)
//                                             .mapToObj(velX -> IntStream.rangeClosed(minVelY, maxVelY).mapToObj(velY -> new Velocity(velX, velY)))
//                                             .flatMap(identity())
//                                             .parallel()
//                                             .filter(Velocity::hitTarget)
//                                             .toList();
//
//        long result = velocities.size();

        long result = IntStream.rangeClosed(minVelX, maxVelX)
                               .mapToObj(velX -> IntStream.rangeClosed(minVelY, maxVelY).mapToObj(velY -> new Velocity(velX, velY)))
                               .flatMap(identity())
                               .parallel()
                               .filter(Velocity::hitTarget)
                               .count();

        System.out.println("Result: " + result);
    }

    record Velocity(int x, int y) {
        boolean hitTarget() {
            Velocity velocity = this;
            Point position = new Point(0, 0);
            int distance = position.distance();
            int steps = maxSteps;
            boolean approach = false;
            while (distance > 0 && steps-- > 0) {
                position = position.advance(velocity);
                velocity = velocity.advance();
                int newDistance = position.distance();
                if (approach && newDistance > (distance + Math.abs(yMax - yMin))) {
                    break;
                }
                if (newDistance < distance) {
                    approach = true;
                }
                distance = newDistance;
            }
            return distance == 0;
        }

        Velocity advance() {
            return new Velocity(advanceX(), advanceY());
        }

        private int advanceX() {
            return Math.max(x - 1, 0);
        }

        private int advanceY() {
            return y - 1;
        }
    }

    record Point(int x, int y) {
        Point advance(Velocity v) {
            return new Point(x + v.x, y + v.y);
        }

        int distance() {
            return distanceX(x) + distanceY(y);
        }

        private int distanceX(int x) {
            if (x < xMin) {
                return xMin - x;
            }
            if (x > xMax) {
                return x - xMax;
            }
            return 0;
        }

        private int distanceY(int y) {
            if (y < yMin) {
                return yMin - y;
            }
            if (y > yMax) {
                return y - yMax;
            }
            return 0;
        }
    }
}




