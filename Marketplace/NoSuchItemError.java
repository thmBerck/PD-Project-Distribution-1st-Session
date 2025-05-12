package Marketplace;

public class NoSuchItemError extends Exception {
    public NoSuchItemError(String message) {
        super(message);
    }
}
