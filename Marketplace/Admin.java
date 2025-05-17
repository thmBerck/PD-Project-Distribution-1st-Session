package Marketplace;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.RemoteSpace;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

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
            ts.put("Marketplace", "Top Up Balance", clientName, delta);
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
            System.out.println(command);
            switch(command) {
                case "help": {
                    System.out.println("ALL COMMANDS CURRENTLY AVAILABLE FOR THE ADMIN INTERFACE:");
                    System.out.println("'help': To get all the commands that are possible.");
                    System.out.println("'balance-of-client <name-of-client>': To see the balance of a client.");
                    System.out.println("'top-up-client <name-of-client> <delta>': Change the balance of a client. Delta can be positive or negative.");
                    System.out.println("'vendor-stock <vendor-name>: Get the current stock per vendor.");
                    System.out.println("'market-stock: Get the current stock of the Marketplace.");
                    break;
                }
                case "balance-of-client": {
                    String clientName = input_parts[1];
                    showClientBalance(clientName);
                    break;
                }
                case "top-up-client": {
                    String clientName = input_parts[1];
                    String delta = input_parts[2];
                    topUpBalance(clientName, delta);
                    break;
                }
                case "vendor-stock": {
                    String vendorName = input_parts[1];
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
