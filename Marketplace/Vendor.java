package Marketplace;

import java.math.BigDecimal;
import java.util.Scanner;

public class Vendor {
    private String name;
    private BigDecimal balance;

    public Vendor(String name) {
        this.name = name;
        this.balance = new BigDecimal("0");
    }
    public void addStock(String id, Vendor vendor, String price) {
        //TODO add implementation with tuple spaces
        System.out.println("ITEM ADDED!" + id + vendor + price);
    }
    public BigDecimal getBalance() {
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
                    addStock(input_parts[1], this, input_parts[2]);
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
        Vendor vendor = new Vendor("MediaMarkt");
        vendor.commands();

    }
}
