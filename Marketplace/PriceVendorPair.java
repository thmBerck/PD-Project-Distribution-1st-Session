package Marketplace;

import java.io.Serializable;

public class PriceVendorPair implements Serializable {
    private String vendor;
    private Double price;

    public PriceVendorPair(String vendor, Double price) {
        this.vendor = vendor;
        this.price = price;
    }

    public String getVendor() {
        return vendor;
    }

    public Double getPrice() {
        return price;
    }
}
