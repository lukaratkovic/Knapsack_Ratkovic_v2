import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class App {
    private static final int NUMBER_OF_ITEMS = 10;
    private static final int ITEM_MAX_WEIGHT = 10;
    private static final int ITEM_MAX_VALUE = 100;
    private static final int KNAPSACK_SIZE = 50;

    public static final int POPULATION_SIZE = 20;
    public static final int ELITISM_COUNT = 5;  //Minimum 2
    public static final int TOTAL_GENERATIONS = 100;
    public static final double MUTATION_LIKELIHOOD = 0.05;

    public static Random seededRandom = new Random(2022);
    public static Random seedlessRandom = new Random();
    public static List<Item> items;

    public static void main(String[] args) {
        long startTime = System.nanoTime();

        // List of items
        items = Stream.generate(() -> new Item(1 + seededRandom.nextInt(ITEM_MAX_WEIGHT), 1 + seededRandom.nextInt(ITEM_MAX_VALUE)))
                .limit(NUMBER_OF_ITEMS)
                .collect(Collectors.toList());
        System.out.println(items);

        // Initial population
        List<Knapsack> population = Stream.generate(() -> new Knapsack(KNAPSACK_SIZE))
                .limit(POPULATION_SIZE)
                .collect(Collectors.toList());
        population.forEach(knapsack -> knapsack.initializeWithRandomValues(items));

        // Run genetic algorithm
        for (int generation = 0; generation < TOTAL_GENERATIONS; generation++) {
            population = createNewGeneration(population);
            population.stream()
                    .sorted((k1, k2) -> k2.getFitness().compareTo(k1.getFitness()))
                    .forEach(k -> System.out.print(k.getFitness() + " "));
            System.out.println();
        }

        // Determine and output best knapsack of final generation
        Knapsack best = population.stream().max(Comparator.comparing(Knapsack::getFitness)).get();
        System.out.println(best);

        long elapsedTime = System.nanoTime() - startTime;
        System.out.println("Total execution time: " + elapsedTime / 1000000 + "ms");
    }

    /**
     * Creates new generation from previous, elitism enabled
     *
     * @param population
     * @return new population
     */
    public static List<Knapsack> createNewGeneration(List<Knapsack> population) {
        // Add ELITISM_COUNT best genomes to generation
        population = population.stream()
                .sorted((k1, k2) -> k2.getFitness().compareTo(k1.getFitness()))
                .limit(ELITISM_COUNT)
                .collect(Collectors.toList());

        List<Knapsack> newPopulation = new ArrayList<>();
        population.forEach(k -> newPopulation.add(k));

        // Fill rest of generation with recombined elements, using random two elite genomes as parents
        while (newPopulation.size() < POPULATION_SIZE) {
            // Select random two elements from elite genomes as parents
            Collections.shuffle(population);
            newPopulation.add(singlePointRecombination(population.get(0), population.get(1)));
        }
        // Mutate
        newPopulation.forEach(knapsack -> knapsack = mutate(knapsack));
        return newPopulation;
    }

    /**
     * Creates new genome by recombining two parent genomes, crossover point chosen randomly
     *
     * @param parent1
     * @param parent2
     * @return recombined child of two parents
     */
    public static Knapsack singlePointRecombination(Knapsack parent1, Knapsack parent2) {
        int crossoverPoint = 1 + seedlessRandom.nextInt(NUMBER_OF_ITEMS - 2);
        Knapsack knapsack = new Knapsack(KNAPSACK_SIZE);
        //Add items up to crossover point from parent1, and rest from parent2
        for (int i = 0; i < crossoverPoint; i++) {
            Item item = items.get(i);
            knapsack.addItem(item, parent1.getItems().get(item));
        }
        for (int i = crossoverPoint; i < NUMBER_OF_ITEMS; i++) {
            Item item = items.get(i);
            knapsack.addItem(item, parent2.getItems().get(item));
        }
        return knapsack;
    }

    /**
     * Mutates a genome
     * Each item's quantity has a chance to randomly increase or decrease by 1
     * Likelihood determined by MUTATION_LIKELIHOOD
     *
     * @param knapsack
     * @return mutated genome
     */
    public static Knapsack mutate(Knapsack knapsack) {
        for (var entry : knapsack.getItems().entrySet()) {
            Item item = entry.getKey();
            Integer quantity = entry.getValue();
            if (seedlessRandom.nextInt(100) < MUTATION_LIKELIHOOD * 100) {
                quantity += seedlessRandom.nextBoolean() ? 1 : -1;
                if (quantity > 3) quantity = 3;
                else if (quantity < 0) quantity = 0;
                knapsack.addItem(item, quantity);
            }
        }
        return knapsack;
    }
}