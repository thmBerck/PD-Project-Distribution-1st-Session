package Marketplace.Payloads;

import java.io.Serializable;
/**
 * @author: Thomas Louis Fernando Berckmoes (netid: tb000026)
 */
public class TopUpBalancePayload implements Serializable {
    private String clientName;
    private Double delta;

    public TopUpBalancePayload(String clientName, Double delta) {
        this.clientName = clientName;
        this.delta = delta;
    }

    public String getClientName() {
        return clientName;
    }

    public Double getDelta() {
        return delta;
    }
}
