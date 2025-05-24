package Marketplace;

import java.util.*;
/**
 * @author: Thomas Louis Fernando Berckmoes (netid: tb000026)
 */
public class Stock {
    private HashMap<String, ArrayList<Item>> stock = new HashMap<String, ArrayList<Item>>();



    //METHODS
    public synchronized void addItem(Item item) {
        ArrayList<Item> items = stock.computeIfAbsent(item.getId(), k -> new ArrayList<Item>());
        items.add(item);
        stock.put(item.getId(), items);
    }
    public Either<String> addAmountofItems(Item item, int quantity) {
        if(item == null) return new Either<String>(null, "The item that was given is null.", false);
        if(quantity <= 0) return new Either<String>(null, "The quantity that was given is null or is under null.", false);
        for(int i = 0; i < quantity; i++) {
            addItem(item);
        }
        return new Either<>("The items have been added.", null, true);
    }
    public synchronized Either<ArrayList<Item>> takeItem(String id, int quantity) {
        ArrayList<Item> items = stock.get(id);
        if (items == null) {
            return new Either<ArrayList<Item>>(null, "No such item was found in the stock of the marketplace.", false);
        }
        items.sort(Comparator.comparing(Item::getPrice));
        ArrayList<Item> result = new ArrayList<Item>();
        if(!(items.size() >= quantity)) {
            return new Either<ArrayList<Item>>(null, "Not enough items for the given quantity.", false);
        }
        for(int i = 0; i < quantity; i++) {
            result.add(items.removeFirst());
        }
        return new Either<ArrayList<Item>>(result, null, true);
    }
    public void displayItems() {
        if (stock.isEmpty()) System.out.println("The stock of the marketplace is empty.");
        stock.forEach((x, l) -> {
            System.out.println("Item with id: " + x + " is available.");
        });
        System.out.println("For the price and the amount available of a specific item, please use list-item-prices command.");
    }
    public Either<ArrayList<String>> showItem(String id) {
        ArrayList<Item> items = stock.get(id);
        if (items == null) {
            return new Either<ArrayList<String>>(null, "No such item was found in the stock of the marketplace.", false);
        }

        HashMap<String, Integer> intermediary_result = new HashMap<String, Integer>();
        for (Item item : items) {
            String key = item.getVendorName() + "@" + item.getPrice();
            int count = intermediary_result.getOrDefault(key, 0);
            intermediary_result.put(key, count + 1);
        }
        ArrayList<String> result = new ArrayList<String>();
        for (String key : intermediary_result.keySet()) {
            int count = intermediary_result.get(key);
            result.add(key.concat("@" + count));
        }
        return new Either<ArrayList<String>>(result, null, true);
    }
    public Either<ArrayList<Item>> getVendorStock(String vendorName) {
        ArrayList<Item> return_items = new ArrayList<Item>();
        for (ArrayList<Item> items : stock.values())
            for (Item item : items) {
                if(Objects.equals(item.getVendorName(), vendorName)) {
                    return_items.add(item);
                }
            }
        return new Either<ArrayList<Item>>(return_items, null, true);
    }

    @Override
    public String toString() {
        return "Stock{" +
                "stock=" + stock +
                '}';
    }

}
