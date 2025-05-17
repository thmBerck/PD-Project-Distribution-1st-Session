package Marketplace;

import Marketplace.Payloads.AddToCartPayload;
import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.RemoteSpace;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Client {
    private final String name;
    private double balance = 0;
    private HashMap<String, ArrayList<Item>> cart = new HashMap<String, ArrayList<Item>>();
    private final RemoteSpace ts;

    {
        try {
            ts = new RemoteSpace("tcp://localhost:10101/ts?keep");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Client(String name, boolean skipRegistration) {
        this.name = name;
        if(!skipRegistration) {
            registerAsClient();
        }
    }

    private void registerAsClient() {
        try {
            ts.put("Marketplace", "Register As Client", name, "NO_PAYLOAD");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void listItems() {
        try {
            ts.put("Marketplace", "List Items", "NO_PAYLOAD", "NO_PAYLOAD");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    public void displayItems(Stock stock) {
        stock.displayItems();
    }
    public void displayPrices(ArrayList<Double> list) {
        for(Double price : list) {
            System.out.println(price);
        }
    }
    public void listItemPrices(String itemId) {
        try {
            ts.put("Marketplace", "List Item Prices", itemId, "NO_PAYLOAD");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    private void addToCart(String itemId, String quantity) {
        try {
            ts.put("Marketplace", "Add To Cart", new AddToCartPayload(name, itemId, Integer.parseInt(quantity)), "NO_PAYLOAD");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    private void viewCart() {
        try {
            ts.put("Marketplace", "View Cart", name, "NO_PAYLOAD");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    private void showBalance() {
        try {
            ts.put("Marketplace", "Client Show Balance", name, "NO_PAYLOAD");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public HashMap<String, ArrayList<Item>> getCart() {
        return cart;
    }


    public double getBalance() {
        return balance;
    }
    public void setBalance(double balance) {
        this.balance = balance;
    }

    public void jobListener() {
        new Thread(() -> {
            while(true) {
                Object[] result = null;
                try {
                    result = ts.get(new ActualField("Client"),new FormalField(String.class), new FormalField(Object.class));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                switch((String) result[1]) {
                    case "List Items": {
                        displayItems((Stock) result[2]);
                        break;
                    }
                    case "List Item Prices": {
                        displayPrices((ArrayList<Double>) result[2]);
                        break;
                    }
                    case "Add To Cart": {
                        Either<Item> either = (Either<Item>) result[2];
                        if(!either.isSuccess()) {
                            System.out.println(either.getError());
                        } else {
                            System.out.println("Successfully added the item.");
                        }
                        break;
                    }
                    case "View Cart": {
                        HashMap<String, ArrayList<Item>> cart = (HashMap<String, ArrayList<Item>>) result[2];
                        System.out.println(cart.toString());
                        break;
                    }
                    case "Show Balance": {
                        Either<Double> either = (Either<Double>) result[2];
                        if(!either.isSuccess()) {
                            System.out.println(either.getError());
                        } else {
                            System.out.println(either.getValue());
                        }
                        break;
                    }
                    default:
                        System.out.println("Wrong command and/or wrong api call: " + result[1]);
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
            System.out.println(command);
            switch(command) {
                case "help":
                    System.out.println("ALL COMMANDS CURRENTLY AVAILABLE:");
                    System.out.println("'help': To get all the commands that are possible.");
                    System.out.println("'balance': To show your balance.");
                    System.out.println("'list-items-store': View all items available in the marketplace.");
                    System.out.println("'list-item-prices <item-id>': For a given <item-id>, view the prices for which it is available.");
                    System.out.println("'add-to-cart <item-id>': Add an item to your cart. The cheapest item will always be chosen. By putting an item in your cart, you have a mutual exclusive lock.");
                    System.out.println("'view-cart': View your cart.");

                    break;
                case "list-items-store":
                    listItems();
                    break;
                case "list-item-prices":
                    listItemPrices(input_parts[1]);
                    break;
                case "balance":
                    showBalance();
                    break;
                case "add-to-cart":
                    addToCart(input_parts[1], input_parts[2]);
                    break;
                case "view-cart":
                    viewCart();
                    break;
                default:
                    System.out.println("Command not recognized, please try again.");
                    break;
            }
        }
    }

    public static void main(String[] args) {
        Client client = new Client("Thomas", false);
        client.jobListener();
        client.commands();
    }


}
