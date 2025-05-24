package Marketplace.Payloads;

import java.io.Serializable;
/**
 * @author: Thomas Louis Fernando Berckmoes (netid: tb000026)
 */
public class CartUpdatePayload implements Serializable {
    private String clientName;
    private String item_id;
    private int quantity;

    public CartUpdatePayload(String clientName, String item_id, int quantity) {
        this.clientName = clientName;
        this.item_id = item_id;
        this.quantity = quantity;
    }

    public String getClientName() {
        return clientName;
    }

    public String getItem_id() {
        return item_id;
    }

    public int getQuantity() {
        return quantity;
    }
}
