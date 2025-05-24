package Marketplace;

import Marketplace.Payloads.TopUpBalancePayload;
import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.RemoteSpace;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
/**
 * @author: Thomas Louis Fernando Berckmoes (netid: tb000026)
 */
public class Admin {
    private final RemoteSpace ts;

    {
        try {
            ts = new RemoteSpace("tcp://localhost:10101/ts?keep");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public Admin(){

    }
    private void showClientBalance(String clientName) {
        try {
            ts.put("Marketplace", "Show Balance", clientName, "NO_PAYLOAD");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    private void topUpBalance(String clientName, String delta) {
        try {
            ts.put("Marketplace", "Top Up Balance", new TopUpBalancePayload(clientName, Double.parseDouble(delta)), "NO_PAYLOAD");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    private void getVendorStock(String vendorName) {
        try {
            ts.put("Marketplace", "Get Vendor Stock", vendorName, "NO_PAYLOAD");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    private void getMarketStock() {
        try {
            ts.put("Marketplace", "Get Market Stock", "NO_PAYLOAD", "NO_PAYLOAD");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    private void getMarketLog() {
        try {
            ts.put("Marketplace", "Get Market Log", "NO_PAYLOAD", "NO_PAYLOAD");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void jobListener() {
        new Thread(() -> {
            while(true) {
                Object[] result = null;
                try {
                    result = ts.get(new ActualField("Admin"),new FormalField(String.class), new FormalField(Object.class));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                switch((String) result[1]) {
                    case "Show Balance": {
                        Either<Double> either = (Either<Double>) result[2];
                        if(either.isSuccess() == false) {
                            System.out.println(either.getError());
                        } else {
                            System.out.println("The balance of the client is: " + either.getValue().toString());
                        }
                        break;
                    }
                    case "Top Up Balance": {
                        Either<Double> either = (Either<Double>) result[2];
                        if(!either.isSuccess()) {
                            System.out.println(either.getError());
                        } else {
                            System.out.println("After modification, the balance of the client is: " + either.getValue().toString());
                        }
                        break;
                    }
                    case "Get Vendor Stock": {
                        Either<ArrayList<Item>> either = (Either<ArrayList<Item>>) result[2];
                        if(!either.isSuccess()) {
                            System.out.println(either.getError());
                        } else {
                            System.out.println(either.getValue().toString());
                        }
                        break;
                    }
                    case "Get Market Stock": {
                        Stock stock = (Stock) result[2];
                        System.out.println(stock.toString());
                        break;
                    }
                    case "Get Market Log": {
                        HashMap<String, HashMap<String, ArrayList<Item>>> market_log = (HashMap<String, HashMap<String, ArrayList<Item>>>) result[2];
                        System.out.println(market_log.toString());
                        break;
                    }
                }
            }
        }).start();
    }

    public void commands() {
        System.out.println("Welcome to the admin interface of the Marketplace system.");
        System.out.println("Type 'help' to get all the commands.");
        Scanner scanner = new Scanner(System.in);
        while(true) {
            String input = scanner.nextLine();
            String[] input_parts = input.split(" ");
            String command = input_parts[0];
            switch(command) {
                case "help": {
                    System.out.println("ALL COMMANDS CURRENTLY AVAILABLE FOR THE ADMIN INTERFACE:");
                    System.out.println("'help': To get all the commands that are possible.");
                    System.out.println("'balance-of-client <name-of-client>': To see the balance of a client.");
                    System.out.println("'top-up-client <name-of-client> <delta>': Change the balance of a client. Delta can be positive or negative.");
                    System.out.println("'vendor-stock <vendor-name>': Get the current stock per vendor.");
                    System.out.println("'market-stock': Get the current stock of the Marketplace.");
                    System.out.println("'market-log': Get the market log of the Marketplace.");
                    break;
                }
                case "balance-of-client": {
                    if(input_parts.length < 2) {
                        System.out.println("This command needs 1 argument. Usage: 'balance-of-client <name-of-client>'");
                        break;
                    }
                    String clientName = input_parts[1];
                    showClientBalance(clientName);
                    break;
                }
                case "top-up-client": {
                    if(input_parts.length < 3) {
                        System.out.println("This command needs 2 arguments. Usage: 'top-up-client <name-of-client> <delta>'");
                        break;
                    }
                    String clientName = input_parts[1];
                    String delta = input_parts[2];
                    topUpBalance(clientName, delta);
                    break;
                }
                case "vendor-stock": {
                    if(input_parts.length < 2) {
                        System.out.println("This command needs 1 argument. Usage: 'vendor-stock <vendor-name>'");
                        break;
                    }
                    String vendorName = input_parts[1];
                    getVendorStock(vendorName);
                    break;
                }
                case "market-stock": {
                    getMarketStock();
                    break;
                }
                case "market-log": {
                    getMarketLog();
                    break;
                }
                default:
                    System.out.println("Command not recognized, please try again.");
                    break;
            }
        }
    }

    public static void main(String[] args) {
        Admin admin = new Admin();
        admin.jobListener();
        admin.commands();
    }
}
