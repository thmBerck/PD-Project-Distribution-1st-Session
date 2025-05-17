package Marketplace.Payloads;

import java.io.Serializable;

public class AddToCartPayload implements Serializable {
    private String clientName;
    private String item_id;
    private int quantity;

    public AddToCartPayload(String clientName, String item_id, int quantity) {
        this.clientName = clientName;
        this.item_id = item_id;
        this.quantity = quantity;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getItem_id() {
        return item_id;
    }

    public void setItem_id(String item_id) {
        this.item_id = item_id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
