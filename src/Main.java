public class Main {
    private static final int NUMBER_OF_SIMULATIONS_TO_RUN = 1;
    private static final int NUMBER_OF_WEEKS_TO_SIMULATE = 50;
    private static final int NUMBER_OF_DAYS_IN_WEEK = 7;
    private static final int HOURS_IN_DAY = 24;
    private static final int DAYS_TO_RUN = NUMBER_OF_DAYS_IN_WEEK * NUMBER_OF_WEEKS_TO_SIMULATE;

    public static void main (String[] args) throws Exception {
        for (int i = 1; i < 30; i++)
            for (int j = 0; j < NUMBER_OF_SIMULATIONS_TO_RUN; j++)
                new Simulation(i*HOURS_IN_DAY).run();
    }
}
