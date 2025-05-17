package Marketplace;

import org.jspace.RemoteSpace;

import java.io.IOException;
import java.util.Scanner;

public class Vendor {
    private String id;
    private String name;
    private double balance;
    // TODO miss iets doen met error handling hier
    private final RemoteSpace ts;

    {
        try {
            ts = new RemoteSpace("tcp://localhost:10101/ts?keep");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Vendor(String id, String name) {
        this.id = id;
        this.name = name;
        this.balance = 0.0;
    }
    public void addStock(String id, Vendor vendor, double price) {
        try {
            ts.put("Marketplace", "Add Stock", new Item(id, vendor.id, price), "NO_PAYLOAD");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("ITEM ADDED!");
    }
    public double getBalance() {
        return balance;
    }
    public void commands() {
        System.out.println("Welcome to the vendor interface of the Marketplace system.");
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
                    System.out.println("'add-stock <item-id> <item-price>': To add stock to the marketplace.");
                    System.out.println("'balance': To show your balance.");
                    break;
                case "add-stock":
                    addStock(input_parts[1], this, Double.valueOf(input_parts[2]));
                    break;
                case "balance":
                    System.out.println("Your current balance is: " + balance);
                    break;
            }
        }
    }

    @Override
    public String toString() {
        return "Vendor{" +
                "name='" + name +"'" +
                '}';
    }

    public static void main(String[] args) {
        Vendor vendor = new Vendor("1", "mediamarkt");
        vendor.commands();

    }
}
