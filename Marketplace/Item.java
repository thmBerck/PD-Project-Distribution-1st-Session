package Marketplace;

public class Item {
    private String id;
    private String vendor_id;
    private double price;

    public Item(String id, String vendor_id, double price) {
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

    public void setPrice(double price) {
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public double getPrice() {
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
