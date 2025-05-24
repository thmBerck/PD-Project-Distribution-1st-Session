package Marketplace.Payloads;

import java.io.Serializable;
/**
 * @author: Thomas Louis Fernando Berckmoes (netid: tb000026)
 */
public class ListItemPricesPayload implements Serializable {
    private String itemId;
    private String clientName;

    public ListItemPricesPayload(String itemId, String clientName) {
        this.itemId = itemId;
        this.clientName = clientName;
    }

    public String getItemId() {
        return itemId;
    }

    public String getClientName() {
        return clientName;
    }
}
