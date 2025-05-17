package Marketplace;

import java.io.IOException;
import java.util.*;

public class Stock {
    private HashMap<String, ArrayList<Item>> stock = new HashMap<String, ArrayList<Item>>();



    //METHODS
    public synchronized void addItem(Item item) {
        ArrayList<Item> items = stock.computeIfAbsent(item.getId(), k -> new ArrayList<Item>());
        items.add(item);
        stock.put(item.getId(), items);
    }
    public synchronized Either<Item> takeItem(String id, double balance) {
        ArrayList<Item> items = stock.get(id);
        if (items == null) {
            return new Either<Item>(null, "No such item found in the stock of the marketplace.", false);
        }
        items.sort(Comparator.comparing(Item::getPrice));
        Item result = items.getFirst();
        if(result.getPrice() > balance) {
            return new Either<Item>(null, "Your balance is insufficient.", false) ;
        }
        items.removeFirst();

        return new Either<Item>(result, null, true);
    }
    public void displayItems() {
        stock.forEach((x, l) -> System.out.println("Item with id: " + x));
    }
    public ArrayList<Double> showItem(String id) throws NoSuchItemError {
        ArrayList<Item> items = stock.get(id);
        if (items == null) {
            throw new NoSuchItemError("No such item found in the stock of the marketplace.");
        }

        ArrayList<Double> result = new ArrayList<Double>();
        for (Item item : items) {
            result.add(item.getPrice());
        }
        return result;
    }
    public ArrayList<Item> getVendorStock(String vendorName) {
        ArrayList<Item> return_items = new ArrayList<Item>();
        for (ArrayList<Item> items : stock.values())
            for (Item item : items) {
                if(Objects.equals(item.getVendorName(), vendorName)) {
                    return_items.add(item);
                }
            }
        return return_items;
    }

    @Override
    public String toString() {
        return "Stock{" +
                "stock=" + stock +
                '}';
    }

}
