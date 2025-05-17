package Marketplace;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.RemoteSpace;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Marketplace {
    Stock stock = new Stock();
    private final RemoteSpace ts = new RemoteSpace("tcp://localhost:10101/ts?keep");
    HashMap<String, Client> clients = new HashMap<String, Client>();

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
            return new Either<Double>(null, "The client's balance went negative.", false);
        }
        client.setBalance(client.getBalance() + delta);
        return new Either<Double>(client.getBalance(), null, true);
    }
    public void jobListener() {
        new Thread(() -> {
            while(true) {
                Object[] result = null;
                try {
                    result = ts.get(new ActualField("Marketplace"),new FormalField(String.class), new FormalField(Object.class), new FormalField(Object.class));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println((String) result[1]);
                switch((String) result[1]) {
                    case "Add Stock": {
                        Item item = (Item) result[2];
                        stock.addItem(item);
                        System.out.println(item);
                        System.out.println(stock.toString());
                        break;
                    }
                    case "List Items": {
                        try {
                            ts.put("Client", "List Items", stock);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        break;
                    }
                    case "List Item Prices": {
                        ArrayList<Double> items;
                        try {
                            items = stock.showItem((String) result[2]);
                        } catch (NoSuchItemError e) {
                            throw new RuntimeException(e);
                        }
                        try {
                            ts.put("Client", "List Item Prices", items);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        break;
                    }

                    case "Add To Cart": {
                        Either<Item> either;
                        Client client = clients.get((String) result[3]);
                        either = stock.takeItem((String) result[2], client.getBalance());
                        if(either.isSuccess()) {
                            Item retrieved_item = either.getValue();
                            HashMap<String, ArrayList<Item>> clientCart = client.getCart();
                            ArrayList<Item> items_in_cart = clientCart.computeIfAbsent(retrieved_item.getId(), k -> new ArrayList<Item>());
                            items_in_cart.add(retrieved_item);
                        }
                        try {
                            ts.put("Client", "Add To Cart", either);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        break;
                    }

                    case "Register As Client": {
                        String clientName = (String) result[2];
                        clients.put(clientName, new Client(clientName, true));
                        System.out.println(clients.toString());
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
                        //TODO: Change payloads to instances of a class.
                        Either<Double> either;
                        String clientName = (String) result[2];
                        String delta = (String) result[3];
                        either = topUpBalance(clientName, Double.parseDouble(delta));
                        try {
                            ts.put("Admin", "Top Up Balance", either);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        break;
                    }
                    case "Get Vendor Stock": {
                        ArrayList<Item> items;
                        String vendorName = (String) result[2];
                        items = stock.getVendorStock(vendorName);
                        try {
                            ts.put("Admin", "Get Vendor Stock", items);
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
                    case "View Cart": {
                        String clientName = (String) result[2];
                        HashMap<String, ArrayList<Item>> clientCart = clients.get(clientName).getCart();
                        System.out.println(clientCart);
                        try {
                            ts.put("Client", "View Cart", clientCart);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        break;
                    }
                    case "Client Show Balance": {
                        Either<Double> either;
                        String name = (String) result[2];
                        either = checkClientBalance(name);
                        try {
                            ts.put("Client", "Show Balance", either);
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
