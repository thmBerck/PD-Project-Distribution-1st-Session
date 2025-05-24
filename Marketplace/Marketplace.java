package Marketplace;

import Marketplace.Payloads.AddStockPayload;
import Marketplace.Payloads.CartUpdatePayload;
import Marketplace.Payloads.ListItemPricesPayload;
import Marketplace.Payloads.TopUpBalancePayload;
import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.RemoteSpace;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
/**
 * @author: Thomas Louis Fernando Berckmoes (netid: tb000026)
 */
public class Marketplace {
    Stock stock = new Stock();
    private final RemoteSpace ts = new RemoteSpace("tcp://localhost:10101/ts?keep");
    HashMap<String, Client> clients = new HashMap<String, Client>();
    HashMap<String, Vendor> vendors = new HashMap<String, Vendor>();
    HashMap<String, HashMap<String, ArrayList<Item>>> market_log = new HashMap<String, HashMap<String, ArrayList<Item>>>(); // Marketlog per Username (String), Marketlog of the user per item, List of all the purchased items.

    public Marketplace() throws IOException {
    }
    private Either<Double> checkClientBalance(String clientName) {
        Client client = clients.get(clientName);
        if(client == null) {
            return new Either<Double>(null, "The client was not found.", false);
        }
        return new Either<Double>(client.getBalance(), null, true);
    }
    private Either<Double> topUpBalance(String clientName, double delta) {
        Client client = clients.get(clientName);
        if(client == null) {
            return new Either<Double>(null, "The client was not found.", false);
        }
        if(client.getBalance() + delta < 0) {
            return new Either<Double>(null, "An error has occurred. The client's balance would of went negative, but this was prevented.", false);
        }
        client.setBalance(client.getBalance() + delta);
        return new Either<Double>(client.getBalance(), null, true);
    }
    private Either<String> removeItemsFromCart(String itemId, int quantity, Client client) {
        HashMap<String, ArrayList<Item>> cart = client.getCart();
        ArrayList<Item> items = cart.get(itemId);
        if (items == null) {
            return new Either<String>(null, "The item id has not been found in the client's cart.", false);
        }
        if (items.size() < quantity) {
            return new Either<String>(null, "There were not enough items of this item id to be removed.", false);
        }
        items.sort(Comparator.comparing(Item::getPrice).reversed());
        ArrayList<Item> to_be_returned = new ArrayList<Item>();
        for(int i = 0; i < quantity; i++) {
            to_be_returned.add(items.removeFirst());
        }
        for(Item item : to_be_returned) {
            stock.addItem(item);
        }
        return new Either<String>("The items were correctly removed from your cart.", null, true);
    }
    private synchronized Either<Double> purchase(String name) {
        Client client = clients.get(name);
        market_log.putIfAbsent(name, new HashMap<>());
        HashMap<String, ArrayList<Item>> client_market_log = market_log.get(name);
        if(client == null) {
            return new Either<Double>(null, "The client does not exist", false);
        }
        HashMap<String, ArrayList<Item>> cart = client.getCart();
        if(cart == null) {
            return new Either<Double>(null, "The client does not yet have a cart on this marketplace.", false);
        }
        double total_price = 0.0;
        for(ArrayList<Item> items : cart.values()) {
            for(Item item : items) {
                total_price += item.getPrice();
            }
        }
        if(total_price > client.getBalance()) {
            return new Either<Double>(null, "The total price of the items in the cart exceeds the balance of the client.", false);
        }
        client.setBalance(client.getBalance() - total_price);

        // Go through all the item id's that are in the cart and add them to the client's market log respectively. Use putIfAbsent to avoid null pointer exceptions.
        for(String itemId : cart.keySet()) {
            ArrayList<Item> purchasedItems = cart.get(itemId);
            for(Item item : purchasedItems) {
                String vendorName = item.getVendorName();
                Vendor vendor = vendors.get(vendorName);
                vendor.setBalance(vendor.getBalance() + item.getPrice());
            }
            client_market_log.putIfAbsent(itemId, new ArrayList<>());
            client_market_log.get(itemId).addAll(purchasedItems);
        }
        client.setCart(new HashMap<String, ArrayList<Item>>());
        return new Either<Double>(total_price, null, true);
    }
    private Either<Double> getVendorBalance(String vendorName) {
        Vendor vendor = vendors.get(vendorName);
        if(vendor == null) {
            return new Either<Double>(null, "The vendor was not found.", false);
        }
        return new Either<Double>(vendor.getBalance(), null, true);
    }

    public void jobListener() {
        new Thread(() -> {
            while(true) {
                Object[] result;
                try {
                    result = ts.get(new ActualField("Marketplace"),new FormalField(String.class), new FormalField(Object.class));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                switch((String) result[1]) {
                    case "Add Stock": {
                        System.out.println("Entering add stock");
                        Either<String> either;
                        AddStockPayload payload = (AddStockPayload) result[2];
                        either = stock.addAmountofItems(payload.getItem(), payload.getQuantity());
                        try {
                            ts.put("Vendor", payload.getItem().getVendorName(), "Add Stock", either);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        break;
                    }
                    case "List Items": {
                        String clientName = (String) result[2];
                        try {
                            ts.put("Client", clientName, "List Items", stock);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        break;
                    }
                    case "List Item Prices": {
                        ListItemPricesPayload payload = (ListItemPricesPayload) result[2];
                        Either<ArrayList<String>> either;
                        either = stock.showItem(payload.getItemId());
                        try {
                            ts.put("Client", payload.getClientName(), "List Item Prices", either);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        break;
                    }

                    case "Add To Cart": {
                        Either<ArrayList<Item>> either;
                        CartUpdatePayload payload = (CartUpdatePayload) result[2];
                        Client client = clients.get(payload.getClientName());
                        either = stock.takeItem(payload.getItem_id(), payload.getQuantity());
                        if(either.isSuccess()) {
                            ArrayList<Item> retrieved_items = either.getValue();
                            HashMap<String, ArrayList<Item>> clientCart = client.getCart();
                            ArrayList<Item> items_in_cart = clientCart.computeIfAbsent(payload.getItem_id(), k -> new ArrayList<Item>());
                            items_in_cart.addAll(retrieved_items);
                        }
                        try {
                            ts.put("Client", payload.getClientName(), "Add To Cart", either);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        break;
                    }
                    case "Remove From Cart": {
                        Either<String> either;
                        CartUpdatePayload payload = (CartUpdatePayload) result[2];
                        Client client = clients.get(payload.getClientName());
                        either = removeItemsFromCart(payload.getItem_id(), payload.getQuantity(), client);
                        try {
                            ts.put("Client", payload.getClientName(), "Remove From Cart", either);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        break;
                    }

                    case "Register As Client": {
                        String clientName = (String) result[2];
                        clients.put(clientName, new Client(clientName, true));
                        break;
                    }
                    case "Register As Vendor": {
                        String vendorName = (String) result[2];
                        vendors.put(vendorName, new Vendor(vendorName, true));
                        break;
                    }

                    case "Show Balance": {
                        Either<Double> either;
                        String name = (String) result[2];
                        either = checkClientBalance(name);
                        try {
                            ts.put("Admin", "Show Balance", either);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        break;
                    }
                    case "Top Up Balance": {
                        Either<Double> either;
                        TopUpBalancePayload payload = (TopUpBalancePayload) result[2];
                        either = topUpBalance(payload.getClientName(), payload.getDelta());
                        try {
                            ts.put("Admin", "Top Up Balance", either);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        break;
                    }
                    case "Get Vendor Stock": {
                        Either<ArrayList<Item>> either;
                        String vendorName = (String) result[2];
                        either = stock.getVendorStock(vendorName);
                        if(vendors.get(vendorName) == null) either = new Either<ArrayList<Item>>(null, "The vendor was not found.", false);
                        try {
                            ts.put("Admin", "Get Vendor Stock", either);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        break;
                    }
                    case "Get Market Stock": {
                        try {
                            ts.put("Admin", "Get Market Stock", stock);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        break;
                    }
                    case "Get Market Log": {
                        try {
                            ts.put("Admin", "Get Market Log", market_log);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        break;
                    }
                    case "View Cart": {
                        String clientName = (String) result[2];
                        HashMap<String, ArrayList<Item>> clientCart = clients.get(clientName).getCart();
                        try {
                            ts.put("Client", clientName, "View Cart", clientCart);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        break;
                    }
                    case "Client Show Balance": {
                        Either<Double> either;
                        String clientName = (String) result[2];
                        either = checkClientBalance(clientName);
                        try {
                            ts.put("Client", clientName, "Show Balance",  either);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        break;
                    }
                    case "Purchase": {
                        Either<Double> either;
                        String clientName = (String) result[2];
                        either = purchase(clientName);
                        try {
                            ts.put("Client", clientName, "Purchase", either);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        break;
                    }
                    case "Get Vendor Balance": {
                        System.out.println("Entering get vendor balance");
                        Either<Double> either;
                        String name = (String) result[2];
                        either = getVendorBalance(name);
                        try {
                            System.out.println("Sending vendor balance back");
                            ts.put("Vendor", name, "Get Vendor Balance", either);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        break;
                    }
                }
            }
        }).start();
    }

    public static void main(String[] args) {
        try {
            Marketplace marketplace = new Marketplace();
            marketplace.jobListener();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
