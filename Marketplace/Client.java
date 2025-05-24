package Marketplace;

import Marketplace.Payloads.CartUpdatePayload;
import Marketplace.Payloads.ListItemPricesPayload;
import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.RemoteSpace;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
/**
 * @author: Thomas Louis Fernando Berckmoes (netid: tb000026)
 */
public class Client {
    private final String name;
    private double balance = 0;
    private HashMap<String, ArrayList<Item>> cart = new HashMap<String, ArrayList<Item>>();
    private final RemoteSpace ts;


    public Client(String name, boolean skipRegistration) {
        this.name = name;
        try {
            ts = new RemoteSpace("tcp://localhost:10101/ts?keep");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if(!skipRegistration) {
            registerAsClient();
        }
    }

    private void registerAsClient() {
        try {
            ts.put("Marketplace", "Register As Client", name);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void listItems() {
        try {
            ts.put("Marketplace", "List Items", name);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    public void listItemPrices(String itemId) {
        try {
            ts.put("Marketplace", "List Item Prices", new ListItemPricesPayload(itemId, name));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    private void addToCart(String itemId, String quantity) {
        try {
            ts.put("Marketplace", "Add To Cart", new CartUpdatePayload(name, itemId, Integer.parseInt(quantity)));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    private void removeFromCart(String itemId, String quantity) {
        try {
            ts.put("Marketplace", "Remove From Cart", new CartUpdatePayload(name, itemId, Integer.parseInt(quantity)));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    private void viewCart() {
        try {
            ts.put("Marketplace", "View Cart", name);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    private void showBalance() {
        try {
            ts.put("Marketplace", "Client Show Balance", name);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    private void purchase() {
        try {
            ts.put("Marketplace", "Purchase", name);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    // https://www.geeksforgeeks.org/hashmap-getordefaultkey-defaultvalue-method-in-java-with-examples/
    public void displayPrices(ArrayList<String> list) {
        int amount = list.size();
        System.out.println(amount + " unit(s) found:");
        for(String match : list) {
            String[] parts = match.split("@");
            String vendor = parts[0];
            String price = parts[1];
            String count = parts[2];
            System.out.println("There is/are " + count + " unit(s) of this item available at " + price + " from the vendor " + vendor + ".");
        }
    }

    public HashMap<String, ArrayList<Item>> getCart() {
        return cart;
    }

    public void setCart(HashMap<String, ArrayList<Item>> cart) {
        this.cart = cart;
    }

    public double getBalance() {
        return balance;
    }
    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getName() {
        return name;
    }

    public void jobListener() {
        new Thread(() -> {
            while(true) {
                Object[] result;
                try {
                    result = ts.get(new ActualField("Client"), new ActualField(name), new FormalField(String.class), new FormalField(Object.class));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                switch((String) result[2]) {
                    case "List Items": {
                        Stock stock = (Stock) result[3];
                        stock.displayItems();
                        break;
                    }
                    case "List Item Prices": {
                        Either<ArrayList<String>> either = (Either<ArrayList<String>>) result[3];
                        displayPrices(either.getValue());
                        break;
                    }
                    case "Add To Cart": {
                        Either<Item> either = (Either<Item>) result[3];
                        if(!either.isSuccess()) {
                            System.out.println(either.getError());
                        } else {
                            System.out.println("Successfully added the item(s).");
                        }
                        break;
                    }
                    case "Remove From Cart": {
                        Either<String> either = (Either<String>) result[3];
                        if(!either.isSuccess()) {
                            System.out.println(either.getError());
                        } else {
                            System.out.println("Successfully removed the item(s).");
                        }
                        break;
                    }
                    case "View Cart": {
                        HashMap<String, ArrayList<Item>> cart = (HashMap<String, ArrayList<Item>>) result[3];
                        if(cart.isEmpty()) {
                            System.out.println("Your cart is empty.");
                            break;
                        }
                        System.out.println("Your cart consists of the following items: " + cart.toString());
                        break;
                    }
                    case "Show Balance": {
                        Either<Double> either = (Either<Double>) result[3];
                        if(!either.isSuccess()) {
                            System.out.println(either.getError());
                        } else {
                            System.out.println("Your balance is currently: "+ either.getValue());
                        }
                        break;
                    }
                    case "Purchase": {
                        Either<Double> either = (Either<Double>) result[3];
                        if(!either.isSuccess()) {
                            System.out.println(either.getError());
                        } else {
                            System.out.println("Your purchase has been made with a total value of: " + either.getValue());
                        }
                        break;
                    }
                    default:
                        System.out.println("Wrong command and/or wrong api call: " + result[2]);
                }
            }
        }).start();
    }

    public void commands() {
        System.out.println("Welcome to the client interface of the Marketplace system.");
        System.out.println("Type 'help' to get all the commands.");
        Scanner scanner = new Scanner(System.in);
        while(true) {
            String input = scanner.nextLine();
            String[] input_parts = input.split(" ");
            String command = input_parts[0];
            switch(command) {
                case "help":
                    System.out.println("ALL COMMANDS CURRENTLY AVAILABLE:");
                    System.out.println("'help': To get all the commands that are possible.");
                    System.out.println("'balance': To show your balance.");
                    System.out.println("'list-items-store': View all items available in the marketplace.");
                    System.out.println("'list-item-prices <item-id>': For a given <item-id>, view the prices for which it is available.");
                    System.out.println("'add-to-cart <item-id> <quantity>': Add a given quantity of an item to your cart. The cheapest item will always be chosen. By putting an item in your cart, you have a mutual exclusive lock.");
                    System.out.println("'remove-from-cart <item-id> <quantity>': Remove a given quantity of an item from your cart.");
                    System.out.println("'purchase': Purchase the items in your cart. Your balance will be accredited. If you do not have enough balance, the operation will fail.");
                    System.out.println("'view-cart': View your cart.");

                    break;
                case "list-items-store":
                    listItems();
                    break;
                case "list-item-prices":
                    if(input_parts.length < 2) {
                        System.out.println("This command needs 1 argument. Usage: 'list-item-prices <item-id>'");
                        break;
                    }
                    listItemPrices(input_parts[1]);
                    break;
                case "balance":
                    showBalance();
                    break;
                case "add-to-cart":
                    if(input_parts.length < 3) {
                        System.out.println("This command needs 2 arguments. Usage: 'add-to-cart <item-id> <quantity>'");
                        break;
                    }
                    addToCart(input_parts[1], input_parts[2]);
                    break;
                case "remove-from-cart":
                    if(input_parts.length < 3) {
                        System.out.println("This command needs 2 arguments. Usage: 'remove-from-cart <item-id> <quantity>'");
                        break;
                    }
                    removeFromCart(input_parts[1], input_parts[2]);
                    break;
                case "view-cart":
                    viewCart();
                    break;
                case "purchase":
                    purchase();
                    break;
                default:
                    System.out.println("Command not recognized, please try again.");
                    break;
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("Choose your username: ");
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        Client client = new Client(input, false);
        System.out.println("Hello " + client.getName() + "!");
        client.jobListener();
        client.commands();
    }


}
