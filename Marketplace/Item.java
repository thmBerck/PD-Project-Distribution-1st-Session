package Marketplace;
/**
 * @author: Thomas Louis Fernando Berckmoes (netid: tb000026)
 */
public class Item {
    private String id;
    private String vendorName;
    private double price;

    public Item(String id, String vendorName, double price) {
        this.id = id;
        this.vendorName = vendorName;
        this.price = price;
    }
    public Item() {}

    public void setId(String id) {
        this.id = id;
    }

    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
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
                "id='" + id + '\'' +
                ", vendorName='" + vendorName + '\'' +
                ", price=" + price +
                '}';
    }
}
