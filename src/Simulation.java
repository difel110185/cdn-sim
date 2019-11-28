import models.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

public class Simulation {
    private static final int NUMBER_OF_CLIENTS = 3000;
    private static final int NUMBER_OF_CONTENTS = 100;
    private static final int VERY_POPULAR_CONTENT_WEIGHT = 5;
    private static final int POPULAR_CONTENT_WEIGHT = 10;
    private static final int REGULAR_POPULAR_CONTENT_WEIGHT = 75;
    private static final int OBSOLETE_POPULAR_CONTENT_WEIGHT = 10;
    private static final int MIN_NUMBER_OF_CONTENT_PARTS_TO_REQUEST = 10;
    private static final int MAX_NUMBER_OF_CONTENT_PARTS_TO_REQUEST = 100;
    private static final int VERY_POPULAR_CONTENT_CHANCE_OF_REQUEST_PERCENTAGE = 80;
    private static final int POPULAR_CONTENT_CHANCE_OF_REQUEST_PERCENTAGE = 50;
    private static final int REGULAR_CONTENT_CHANCE_OF_REQUEST_PERCENTAGE = 20;
    private static final int OBSOLETE_CONTENT_CHANCE_OF_REQUEST_PERCENTAGE = 5;

    private static final PopularityLevel veryPopularPopularityLevel = new PopularityLevel("Very Popular");
    private static final PopularityLevel popularPopularityLevel = new PopularityLevel("Popular");
    private static final PopularityLevel regularPopularityLevel = new PopularityLevel("Regular");
    private static final PopularityLevel obsoletePopularityLevel = new PopularityLevel("Obsolete");

    private static final int[] hourlyChanceToRequestContent = {4,4,3,3,3,4,4,4,4,4,4,4,5,5,4,4,4,4,4,5,6,5,4,4};
    private static final int[] weeklyChanceToRequestContent = {12,11,12,13,17,19,16};

    private static final int NUMBER_OF_WEEKS_TO_SIMULATE = 50;
    private static final int NUMBER_OF_DAYS_IN_WEEK = 7;
    private static final int HOURS_IN_DAY = 24;
    private static final int DAYS_TO_RUN = NUMBER_OF_DAYS_IN_WEEK * NUMBER_OF_WEEKS_TO_SIMULATE;
    private int cacheTtlNumberOfCycles;

    private ArrayList<Region> regions = new ArrayList<>();
    private ArrayList<Client> clients = new ArrayList<>();
    private ArrayList<CacheNode> cacheNodes = new ArrayList<>();
    private ArrayList<Content> contents = new ArrayList<>();
    private ArrayList<ContentPart> contentParts = new ArrayList<>();
    private HashMap<String, ArrayList<ClientContentPartRequest>> clientContentPartRequests = new HashMap<>();

    private static int totalNumberOfRequests = 0;
    private static int totalNumberOfRequestsThatHitCache = 0;

    public Simulation(int cacheTtlNumberOfCycles) {
        this.cacheTtlNumberOfCycles = cacheTtlNumberOfCycles;
    }

    public void run() throws Exception {
        setupSimulation();

        System.out.println("Cache TTL in days: " + cacheTtlNumberOfCycles / HOURS_IN_DAY);
        //printRequestCountPerHour();
        //printWeekRequestDistribution();

        runSimulationSteps();

        printKpis();
    }

    private void setupSimulation() throws Exception {
        createRegions();
        createClients();
        createCacheNodes();
        createContents();
        createClientContentPartRequests();
    }

    private void printKpis() {
        System.out.println("Total cost: $" + String.format("%.2f", getCost()));
        System.out.println("Number of requests: " + totalNumberOfRequests);
        System.out.println("Cost per 100000 requests: $" + String.format("%.2f", getCostPer100000Requests()));
        System.out.println("Cache hit ratio: " + String.format("%.2f%%", getCacheHitRatio()));
    }

    public double getCostPer100000Requests() {
        return 100000 * getCost() / totalNumberOfRequests;
    }

    public float getCacheHitRatio() {
        return ((float) totalNumberOfRequestsThatHitCache / (float) totalNumberOfRequests) * 100;
    }

    private double getCost() {
        double cost = 0;

        for (CacheNode cacheNode : cacheNodes)
            cost = cacheNode.calculateCost();

        return cost;
    }

    private void runSimulationSteps() {
        for (int day = 0 ; day < DAYS_TO_RUN ; day++)
            for (int hour = 0 ; hour < HOURS_IN_DAY ; hour++)
                runSimulationStep(day, hour);
    }

    private void runSimulationStep(int day, int hour) {
        decreaseCachedContentPartsTtl();

        executeRequests(day, hour);
    }

    private void executeRequests(int day, int hour) {
        ArrayList<ClientContentPartRequest> requests = this.clientContentPartRequests.get(day + "-" + hour);

        if (requests != null)
            for (ClientContentPartRequest clientContentPartRequest : requests)
                executeRequest(clientContentPartRequest);
    }

    private static void executeRequest(ClientContentPartRequest clientContentPartRequest) {
        totalNumberOfRequests++;

        CacheNode cacheNode = clientContentPartRequest.getClient().getRegion().getCacheNode();
        ContentPart contentPart = clientContentPartRequest.getContentPart();
        cacheNode.increaseRequestCount();
        if (cacheNode.getContentParts().contains(contentPart)) {
            totalNumberOfRequestsThatHitCache++;
            cacheNode.renewTtl(contentPart);
        }
        else
            cacheNode.cacheContentPart(contentPart);
    }

    private void decreaseCachedContentPartsTtl() {
        for (CacheNode node : this.cacheNodes)
            node.decreaseCachedContentPartsTtl();
    }

    private void printWeekRequestDistribution() {
        long[][] requestCount = new long[NUMBER_OF_DAYS_IN_WEEK][HOURS_IN_DAY];

        for (int day = 0; day < DAYS_TO_RUN ; day++)
            for (int hour = 0 ; hour < HOURS_IN_DAY ; hour++)
                requestCount[day % NUMBER_OF_DAYS_IN_WEEK][hour] += (clientContentPartRequests.get(day + "-" + hour) != null ? clientContentPartRequests.get(day + "-" + hour).size() : 0);

        for (int i = 0; i < NUMBER_OF_DAYS_IN_WEEK; i++)
            for (int j = 0; j < HOURS_IN_DAY; j++)
                System.out.println(requestCount[i][j] / NUMBER_OF_WEEKS_TO_SIMULATE);
    }

    private void printRequestCountPerHour() {
        for (int day = 0 ; day < DAYS_TO_RUN ; day++)
            for (int hour = 0 ; hour < HOURS_IN_DAY ; hour++)
                System.out.println((this.clientContentPartRequests.get(day + "-" + hour) != null ? this.clientContentPartRequests.get(day + "-" + hour).size() : 0 ));
    }

    private void createClientContentPartRequests() {
        for (Client client : clients)
            for (Content content : contents)
                if (contentShouldBeRequested(content))
                    createClientContentPartRequest(client, content);
    }

    private void createClientContentPartRequest(Client client, Content content) {
        int lastSequenceNumberToRequest = getRandomIntegerInRange(0, content.getParts().size() - 1);

        int weekDay = getRequestWeekDay();
        int randomWeekNumber = getRandomIntegerInRange(0, NUMBER_OF_WEEKS_TO_SIMULATE - 1);
        int day = randomWeekNumber * NUMBER_OF_DAYS_IN_WEEK + weekDay;
        int hour = getRequestHour();

        for (int i = 0; i < lastSequenceNumberToRequest; i++) {
            String key = day + "-" + hour;
            ClientContentPartRequest clientContentPartRequest = new ClientContentPartRequest(content.getParts().get(i), client, day, hour);
            if (!clientContentPartRequests.containsKey(key))
                clientContentPartRequests.put(key, new ArrayList<>());
            clientContentPartRequests.get(key).add(clientContentPartRequest);
        }
    }

    private int getRequestWeekDay() {
        int chance = new Random().nextInt(100);

        if (chance < Arrays.stream(Arrays.copyOfRange(weeklyChanceToRequestContent, 0, 1)).sum())
            return 0;
        else if (chance < Arrays.stream(Arrays.copyOfRange(weeklyChanceToRequestContent, 0, 2)).sum())
            return 1;
        else if (chance < Arrays.stream(Arrays.copyOfRange(weeklyChanceToRequestContent, 0, 3)).sum())
            return 2;
        else if (chance < Arrays.stream(Arrays.copyOfRange(weeklyChanceToRequestContent, 0, 4)).sum())
            return 3;
        else if (chance < Arrays.stream(Arrays.copyOfRange(weeklyChanceToRequestContent, 0, 5)).sum())
            return 4;
        else if (chance < Arrays.stream(Arrays.copyOfRange(weeklyChanceToRequestContent, 0, 6)).sum())
            return 5;
        else
            return 6;
    }

    private int getRequestHour() {
        int chance = new Random().nextInt(100);

        if (chance < Arrays.stream(Arrays.copyOfRange(hourlyChanceToRequestContent, 0, 1)).sum())
            return 0;
        else if (chance < Arrays.stream(Arrays.copyOfRange(hourlyChanceToRequestContent, 0, 2)).sum())
            return 1;
        else if (chance < Arrays.stream(Arrays.copyOfRange(hourlyChanceToRequestContent, 0, 3)).sum())
            return 2;
        else if (chance < Arrays.stream(Arrays.copyOfRange(hourlyChanceToRequestContent, 0, 4)).sum())
            return 3;
        else if (chance < Arrays.stream(Arrays.copyOfRange(hourlyChanceToRequestContent, 0, 5)).sum())
            return 4;
        else if (chance < Arrays.stream(Arrays.copyOfRange(hourlyChanceToRequestContent, 0, 6)).sum())
            return 5;
        else if (chance < Arrays.stream(Arrays.copyOfRange(hourlyChanceToRequestContent, 0, 7)).sum())
            return 6;
        else if (chance < Arrays.stream(Arrays.copyOfRange(hourlyChanceToRequestContent, 0, 8)).sum())
            return 7;
        else if (chance < Arrays.stream(Arrays.copyOfRange(hourlyChanceToRequestContent, 0, 9)).sum())
            return 8;
        else if (chance < Arrays.stream(Arrays.copyOfRange(hourlyChanceToRequestContent, 0, 10)).sum())
            return 9;
        else if (chance < Arrays.stream(Arrays.copyOfRange(hourlyChanceToRequestContent, 0, 11)).sum())
            return 10;
        else if (chance < Arrays.stream(Arrays.copyOfRange(hourlyChanceToRequestContent, 0, 12)).sum())
            return 11;
        else if (chance < Arrays.stream(Arrays.copyOfRange(hourlyChanceToRequestContent, 0, 13)).sum())
            return 12;
        else if (chance < Arrays.stream(Arrays.copyOfRange(hourlyChanceToRequestContent, 0, 14)).sum())
            return 13;
        else if (chance < Arrays.stream(Arrays.copyOfRange(hourlyChanceToRequestContent, 0, 15)).sum())
            return 14;
        else if (chance < Arrays.stream(Arrays.copyOfRange(hourlyChanceToRequestContent, 0, 16)).sum())
            return 15;
        else if (chance < Arrays.stream(Arrays.copyOfRange(hourlyChanceToRequestContent, 0, 17)).sum())
            return 16;
        else if (chance < Arrays.stream(Arrays.copyOfRange(hourlyChanceToRequestContent, 0, 18)).sum())
            return 17;
        else if (chance < Arrays.stream(Arrays.copyOfRange(hourlyChanceToRequestContent, 0, 19)).sum())
            return 18;
        else if (chance < Arrays.stream(Arrays.copyOfRange(hourlyChanceToRequestContent, 0, 20)).sum())
            return 19;
        else if (chance < Arrays.stream(Arrays.copyOfRange(hourlyChanceToRequestContent, 0, 21)).sum())
            return 20;
        else if (chance < Arrays.stream(Arrays.copyOfRange(hourlyChanceToRequestContent, 0, 22)).sum())
            return 21;
        else if (chance < Arrays.stream(Arrays.copyOfRange(hourlyChanceToRequestContent, 0, 23)).sum())
            return 22;
        else
            return 23;
    }

    private boolean contentShouldBeRequested(Content content) {
        int randomChance = new Random().nextInt(100);

        if (content.getPopularityLevel().equals(veryPopularPopularityLevel) && randomChance <= VERY_POPULAR_CONTENT_CHANCE_OF_REQUEST_PERCENTAGE)
            return true;

        if (content.getPopularityLevel().equals(popularPopularityLevel) && randomChance <= POPULAR_CONTENT_CHANCE_OF_REQUEST_PERCENTAGE)
            return true;

        if (content.getPopularityLevel().equals(regularPopularityLevel) && randomChance <= REGULAR_CONTENT_CHANCE_OF_REQUEST_PERCENTAGE)
            return true;

        if (content.getPopularityLevel().equals(obsoletePopularityLevel) && randomChance <= OBSOLETE_CONTENT_CHANCE_OF_REQUEST_PERCENTAGE)
            return true;

        return false;
    }

    private void createContents() throws Exception {
        for (int i = 0 ; i < NUMBER_OF_CONTENTS ; i++)
            createContent(i);
    }

    private void createContent(int i) throws Exception {
        Content content = new Content("Content " + i, getRandomPopularityLevel());

        createContentParts(content);

        contents.add(content);
    }

    private void createContentParts(Content content) {
        int numberOfParts = getRandomIntegerInRange(MIN_NUMBER_OF_CONTENT_PARTS_TO_REQUEST, MAX_NUMBER_OF_CONTENT_PARTS_TO_REQUEST);
        for (int j = 0 ; j < numberOfParts ; j++)
            createContentPart(content, j);
    }

    private void createContentPart(Content content, int j) {
        ContentPart contentPart = new ContentPart(content, j);
        contentParts.add(contentPart);
        content.getParts().add(contentPart);
    }

    private PopularityLevel getRandomPopularityLevel() throws Exception {
        int randomPopularityLevel = new Random().nextInt(100);

        if (randomPopularityLevel < OBSOLETE_POPULAR_CONTENT_WEIGHT)
            return obsoletePopularityLevel;
        else if (randomPopularityLevel < OBSOLETE_POPULAR_CONTENT_WEIGHT + REGULAR_POPULAR_CONTENT_WEIGHT)
            return regularPopularityLevel;
        else if (randomPopularityLevel < OBSOLETE_POPULAR_CONTENT_WEIGHT + REGULAR_POPULAR_CONTENT_WEIGHT + POPULAR_CONTENT_WEIGHT)
            return popularPopularityLevel;
        else if (randomPopularityLevel < OBSOLETE_POPULAR_CONTENT_WEIGHT + REGULAR_POPULAR_CONTENT_WEIGHT + POPULAR_CONTENT_WEIGHT + VERY_POPULAR_CONTENT_WEIGHT)
            return veryPopularPopularityLevel;
        else
            throw new Exception("Invalid popularity level chance");
    }

    private void createCacheNodes() {
        for (Region region : regions)
            createCacheNode(region);
    }

    private void createCacheNode(Region region) {
        CacheNode cacheNode = new CacheNode(region, cacheTtlNumberOfCycles);
        cacheNodes.add(cacheNode);
        region.setCacheNode(cacheNode);
    }

    private void createClients() {
        for (int i = 0 ; i < NUMBER_OF_CLIENTS ; i++)
            createClient();
    }

    private void createClient() {
        Region region = regions.get(new Random().nextInt(regions.size()));
        Client client = new Client(region, region.getName() + " " + region.getClients().size());
        region.getClients().add(client);
        clients.add(client);
    }

    private void createRegions() {
        this.regions.add(new Region("South America", -3));
        this.regions.add(new Region("North America", -2));
        this.regions.add(new Region("Europe", 1));
        this.regions.add(new Region("Asia", 8));
        this.regions.add(new Region("Oceania", 10));
        this.regions.add(new Region("Africa", 1));
    }

    private int getRandomIntegerInRange(int min, int max) {
        return new Random().nextInt(max + 1 - min) + min;
    }
}
