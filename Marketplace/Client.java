package Marketplace;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.RemoteSpace;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Client {
    private BigDecimal balance = new BigDecimal("0");
    private HashMap<String, ArrayList<Item>> cart = new HashMap<String, ArrayList<Item>>();
    private final RemoteSpace ts;

    {
        try {
            ts = new RemoteSpace("tcp://localhost:10101/ts?keep");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void listItems() {
        System.out.println("We in baby");
        try {
            ts.put("Marketplace", "List Items", "NO_PAYLOAD");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    public void displayItems(Stock stock) {
        stock.displayItems();
    }
    public void listItemPrices(String id) {

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
                    case "List Items":
                        displayItems((Stock) result[2]);
                        break;
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
                    System.out.println("'list-item-prices <item>': For a given <item>, view the prices for which it is available.");

                    break;
                case "list-items-store":
                    listItems();
                    break;
                case "list-item-prices":
                    listItemPrices(input_parts[1]);
                    break;
                case "balance":
                    System.out.println("Your current balance is: " + balance);
                    break;
                default:
                    System.out.println("Command not recognized, please try again.");
                    break;
            }
        }
    }

    public static void main(String[] args) {
        Client client = new Client();
        client.jobListener();
        client.commands();
    }


}
