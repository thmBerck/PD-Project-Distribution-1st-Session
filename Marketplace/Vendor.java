package Marketplace;

import Marketplace.Payloads.AddStockPayload;
import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.RemoteSpace;

import java.io.IOException;
import java.util.Scanner;

public class Vendor {
    private String name;
    private double balance;
    // TODO miss iets doen met error handling hier
    // TODO Ik moet ervoor zorgen dat wanneer de marketplace iets terugstuurt naar de vendor deze specifiek naar de naam van de vendor wordt gestuurd, ook met client doen.
    private final RemoteSpace ts;

    {
        try {
            ts = new RemoteSpace("tcp://localhost:10101/ts?keep");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Vendor(String name, boolean skipRegistration) {
        this.name = name;
        this.balance = 0.0;

        if (!skipRegistration) {
            registerAsVendor();
        }
    }
    private void registerAsVendor() {
        try {
            ts.put("Marketplace", "Register As Vendor", name, "NO_PAYLOAD");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    public void addStock(String id, int quantity, Vendor vendor, double price) {
        try {
            ts.put("Marketplace", "Add Stock", new AddStockPayload(new Item(id, vendor.name, price), quantity), "NO_PAYLOAD");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    private void callBalance() {
        try {
            ts.put("Marketplace", "Get Vendor Balance", this.name, "NO_PAYLOAD");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
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
                case "help": {
                    System.out.println("ALL COMMANDS CURRENTLY AVAILABLE:");
                    System.out.println("'help': To get all the commands that are possible.");
                    System.out.println("'add-stock <item-id> <number> <item-price>': To add a number of items to the stock of the marketplace.");
                    System.out.println("'balance': To show your balance.");
                    break;
                }
                case "add-stock":{
                    addStock(input_parts[1], Integer.parseInt(input_parts[2]), this, Double.parseDouble(input_parts[3]));
                    break;
                }
                case "balance": {
                    callBalance();
                    break;
                }
                default: {
                    System.out.println("Command not recognized, please try again.");
                    break;
                }
            }
        }
    }
    public void jobListener() {
        new Thread(() -> {
            while(true) {
                Object[] result;
                try {
                    result = ts.get(new ActualField("Vendor"),new FormalField(String.class), new FormalField(Object.class));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                switch((String) result[1]) {
                    case "Add Stock": {
                        Either<String> either = (Either<String>) result[2];
                        if(!either.isSuccess()) {
                            System.out.println(either.getError());
                        } else {
                            System.out.println("The stock has been added succesfully.");
                        }
                        break;
                    }
                    case "Get Vendor Balance": {
                        Either<Double> either = (Either<Double>) result[2];
                        if(!either.isSuccess()) {
                            System.out.println(either.getError());
                        } else {
                            System.out.println("Your balance is " + either.getValue());
                        }
                        break;
                    }
                    default:
                        System.out.println("Wrong command and/or wrong api call: " + result[1]);
                }
            }
        }).start();
    }

    @Override
    public String toString() {
        return "Vendor{" +
                "name='" + name +"'" +
                '}';
    }

    public static void main(String[] args) {
        System.out.println("Choose your username: ");
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        Vendor vendor = new Vendor(input, false);
        //TODO add vendor interface that lets you choose name and register it to the marketplace.
        vendor.jobListener();
        vendor.commands();

    }
}
