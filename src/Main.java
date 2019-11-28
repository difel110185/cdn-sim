import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class Main {
    private static final int NUMBER_OF_SIMULATIONS_TO_RUN = 1;
    private static final int MIN_TTL_IN_DAYS = 1;
    private static final int MAX_TTL_IN_DAYS = 30;
    private static final int HOURS_IN_DAY = 24;
    private static final int MIN_ACCEPTABLE_CACHE_HIT_PERCENTAGE = 45;
    private static final float MIN_ACCEPTABLE_COST_IMPROVEMENT_PERCENTAGE = 0.05f;
    private static final int NUMBER_OF_DAYS_IN_WEEK = 7;
    private static ArrayList<Integer> ttlQueue = new ArrayList<>();
    private static int bestTTL = 0;
    private static double bestCost = Double.MAX_VALUE;
    private static ArrayList<Integer> usedTTLs = new ArrayList<>();
    private static HashMap<Integer, Double> ttlCosts = new HashMap<>();
    private static HashMap<Integer, Float> ttlCacheHitRatios = new HashMap<>();

    public static void main (String[] args) throws Exception {
        //bruteForce();
        qLearning();
    }

    private static void bruteForce() throws Exception {
        for (int i = 1; i < MAX_TTL_IN_DAYS; i++)
            for (int j = 0; j < NUMBER_OF_SIMULATIONS_TO_RUN; j++) {
                Simulation simulation = new Simulation(i * HOURS_IN_DAY);
                simulation.run();
                ttlCosts.put(i, simulation.getCostPer100000Requests());
                ttlCacheHitRatios.put(i, simulation.getCacheHitRatio());

            }

        System.out.println("TTL Costs per 100000 request: $" + ttlCosts);
        System.out.println("TTL Cache Hit Ratios: $" + ttlCosts);
    }

    private static void qLearning() throws Exception {
        int initialTTL = getRandomIntegerInRange(MIN_TTL_IN_DAYS, MAX_TTL_IN_DAYS);

        bestTTL = initialTTL;
        ttlQueue.add(initialTTL);

        while (!ttlQueue.isEmpty()) {
            int ttl = ttlQueue.remove(0);

            usedTTLs.add(ttl);

            Simulation simulation = new Simulation(ttl * HOURS_IN_DAY);
            simulation.run();

            double reward;
            double cost = simulation.getCostPer100000Requests();
            ttlCosts.put(ttl, cost);
            ttlCacheHitRatios.put(ttl, simulation.getCacheHitRatio());

            if (bestCost == Double.MAX_VALUE) {
                reward = 1;
                bestCost = cost;
                bestTTL = ttl;
            }
            else if (simulation.getCacheHitRatio() < MIN_ACCEPTABLE_CACHE_HIT_PERCENTAGE)
                reward = -1000;
            else if (cost > bestCost)
                reward = -10;
            else if (cost > (1-MIN_ACCEPTABLE_COST_IMPROVEMENT_PERCENTAGE) * bestCost)
                reward = 0;
            else {
                reward = bestCost - cost;
                bestCost = cost;
                bestTTL = ttl;
            }

            if (reward > 0) {
                explore(ttl, 1, 1);

                explore(ttl, NUMBER_OF_DAYS_IN_WEEK, NUMBER_OF_DAYS_IN_WEEK);

                explore(ttl, (int) Math.floor((ttl + MAX_TTL_IN_DAYS) / 2.0), (int) Math.floor((ttl + MIN_TTL_IN_DAYS) / 2.0));
            }
        }

        System.out.println("Best TTL in days: " + bestTTL);
        System.out.println("Lowest Cost per 100000 request: $" + String.format("%.2f", bestCost));
        System.out.println("TTL Costs per 100000 request: $" + ttlCosts);
        System.out.println("TTL Cache Hit Ratios: $" + ttlCosts);
    }

    private static void explore(int ttl, int stepSizeUp, int StepSizeDown) {
        if (!usedTTLs.contains(ttl+stepSizeUp) && ttl+stepSizeUp <= MAX_TTL_IN_DAYS) {
            ttlQueue.add(ttl+stepSizeUp);
            usedTTLs.add(ttl+stepSizeUp);
        }

        if (!usedTTLs.contains(ttl-StepSizeDown) && ttl-StepSizeDown >= MIN_TTL_IN_DAYS) {
            ttlQueue.add(ttl-StepSizeDown);
            usedTTLs.add(ttl-StepSizeDown);
        }
    }

    private static int getRandomIntegerInRange(int min, int max) {
        return new Random().nextInt(max + 1 - min) + min;
    }
}
