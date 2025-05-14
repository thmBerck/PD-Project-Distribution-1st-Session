package Marketplace;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.RemoteSpace;

import java.io.IOException;

public class Marketplace {
    Stock stock = new Stock();
    private final RemoteSpace ts = new RemoteSpace("tcp://localhost:10101/ts?keep");

    public Marketplace() throws IOException {
    }
    public void jobListener() {
        new Thread(() -> {
            while(true) {
                Object[] result = null;
                try {
                    result = ts.get(new ActualField("Marketplace"),new FormalField(String.class), new FormalField(Object.class));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println((String) result[1]);
                switch((String) result[1]) {
                    case "Buy Item":
                        try {
                            stock.takeItem((String) result[2]);
                        } catch (NoSuchItemError e) {
                            System.out.println("Marketplace: " + e.toString());
                            //TODO maybe put this error on the ts to say it has not been added.
                        }
                        break;
                    case "Add Stock":
                        stock.addItem((Item) result[2]);
                        System.out.println((Item) result[2]);
                        System.out.println(stock.toString());
                        break;
                    case "List Items":
                        try {
                            ts.put("Client", "List Items", stock);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
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
