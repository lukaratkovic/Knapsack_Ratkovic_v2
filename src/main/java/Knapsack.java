import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class Knapsack {
    private int size;
    private double fitness;
    private HashMap<Item, Integer> items;

    public Knapsack(int size) {
        this.size = size;
        items = new HashMap<>();
    }

    public void initializeWithRandomValues(List<Item> itemList) {
        Random random = new Random();
        itemList.forEach(item -> items.put(item, random.nextInt(4)));
    }

    public void setItems(HashMap<Item, Integer> items) {
        this.items = items;
    }

    public void addItem(Item item, Integer quantity) {
        items.put(item, quantity);
    }

    public HashMap<Item, Integer> getItems() {
        return items;
    }

    public Double getFitness() {
        this.fitness = totalWeight() < size ? totalValue() : (double) size * 100 / totalWeight();
        return fitness;
    }

    private int totalWeight() {
        return items.entrySet().stream().mapToInt(item -> item.getKey().getWeight() * item.getValue()).sum();
    }

    private int totalValue() {
        return items.entrySet().stream().mapToInt(item -> item.getKey().getValue() * item.getValue()).sum();
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (var item : items.entrySet()) {
            result.append("\n").append(item.getKey() + "\tAmount: " + item.getValue());
        }
        result.append("\nTotal weight: ").append(totalWeight());
        result.append("\nTotal value: ").append(totalValue());
        result.append("\nFitness: ").append(fitness);
        return result.toString();
    }
}