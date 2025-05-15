package Marketplace;

import java.math.BigDecimal;

public class Item {
    private String id;
    private String vendor_id;
    private BigDecimal price;

    public Item(String id, String vendor_id, BigDecimal price) {
        this.id = id;
        this.vendor_id = vendor_id;
        this.price = price;
    }
    public Item() {}

    public void setId(String id) {
        this.id = id;
    }

    public String getVendor_id() {
        return vendor_id;
    }

    public void setVendor_id(String vendor_id) {
        this.vendor_id = vendor_id;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public BigDecimal getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return "Item{" +
                "vendor=" + vendor_id +
                ", price=" + price +
                '}';
    }
}
