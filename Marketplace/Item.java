package Marketplace;

import java.math.BigDecimal;

public class Item {
    private String id;
    private Vendor vendor;
    private BigDecimal price;

    public Item(String id, Vendor vendor, BigDecimal price) {
        this.id = id;
        this.vendor = vendor;
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
                "vendor=" + vendor +
                ", price=" + price +
                '}';
    }
}
