package Marketplace.Payloads;

import Marketplace.Item;

import java.io.Serializable;

public class AddStockPayload implements Serializable {
    private final Item item;
    private final int quantity;

    public AddStockPayload(Item item, int quantity) {
        this.item = item;
        this.quantity = quantity;
    }

    public Item getItem() {
        return item;
    }

    public int getQuantity() {
        return quantity;
    }
}
