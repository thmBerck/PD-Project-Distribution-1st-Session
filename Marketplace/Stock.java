package Marketplace;

import java.math.BigDecimal;
import java.util.*;

public class Stock {
    private HashMap<String, ArrayList<Item>> stock = new HashMap<String, ArrayList<Item>>();



    //METHODS
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
        Item result = items.getFirst();
        items.removeFirst();
        return result;
    }

    @Override
    public String toString() {
        return "Stock{" +
                "stock=" + stock +
                '}';
    }

    //MAIN
    public static void main(String[] args) throws NoSuchItemError {
        Stock stock = new Stock();
        Vendor mediamarkt = new Vendor("MediaMarkt");
        stock.addItem(new Item("1", mediamarkt, new BigDecimal("5.45")));
        stock.addItem(new Item("1", mediamarkt, new BigDecimal("23.45")));
        stock.addItem(new Item("1", mediamarkt, new BigDecimal("20.45")));
        stock.addItem(new Item("1", mediamarkt, new BigDecimal("3.45")));
        System.out.println(stock.toString());
        stock.takeItem("1");
        System.out.println(stock);

    }


}
