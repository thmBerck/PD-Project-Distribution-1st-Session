package Marketplace;

import java.math.BigDecimal;
import java.util.*;

public class Stock {
    private HashMap<String, ArrayList<Item>> stock = new HashMap<String, ArrayList<Item>>();


    //https://www.w3schools.com/java/ref_hashmap_computeifabsent.asp
    public void addItem(Item item) {
        ArrayList<Item> items = stock.computeIfAbsent(item.getId(), k -> new ArrayList<Item>());
        items.add(item);
        stock.put(item.getId(), items);
    }
    public Item takeItem(String id) throws NoSuchItemError {
        ArrayList<Item> items = stock.get(id);
        if (items == null) {
            throw new NoSuchItemError("No such item found in the stock of the marketplace.");
        }
        items.sort(Comparator.comparing(Item::getPrice));
        Item result = items.get(0);
        items.remove(0);
        return result;
    }

    @Override
    public String toString() {
        return "Stock{" +
                "stock=" + stock +
                '}';
    }

    public static void main(String[] args) throws NoSuchItemError {
        Stock stock = new Stock();
        stock.addItem(new Item("1", new Vendor(), new BigDecimal("5.45")));
        stock.addItem(new Item("1", new Vendor(), new BigDecimal("23.45")));
        stock.addItem(new Item("1", new Vendor(), new BigDecimal("20.45")));
        stock.addItem(new Item("1", new Vendor(), new BigDecimal("3.45")));
        System.out.println(stock.toString());
        Item item = stock.takeItem("5");
        System.out.println(item.getPrice());

    }


}
