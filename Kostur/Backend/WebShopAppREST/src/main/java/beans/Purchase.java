package beans;

import java.time.LocalDateTime;
import java.util.List;
import beans.enums.PurchaseStatus;

public class Purchase {
    private String id; // Unique identifier (10 characters)
    private List<Chocolate> chocolates; // Purchased chocolates
    private String factoryId; // Factory ID from which it was purchased
    private LocalDateTime purchaseDateTime; // Date and time of purchase
    private double price; // Price
    private String customerName; // Customer (name and surname)
    private PurchaseStatus status; // Status (Processing, Approved, Rejected, Cancelled)

    public Purchase() {
        // Default constructor
    }

    public Purchase(String id, List<Chocolate> chocolates, String factoryId, LocalDateTime purchaseDateTime, double price, String customerName, PurchaseStatus status) {
        this.id = id;
        this.chocolates = chocolates;
        this.factoryId = factoryId;
        this.purchaseDateTime = purchaseDateTime;
        this.price = price;
        this.customerName = customerName;
        this.status = status;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Chocolate> getChocolates() {
        return chocolates;
    }

    public void setChocolates(List<Chocolate> chocolates) {
        this.chocolates = chocolates;
    }

    public String getFactoryId() {
        return factoryId;
    }

    public void setFactoryId(String factoryId) {
        this.factoryId = factoryId;
    }

    public LocalDateTime getPurchaseDateTime() {
        return purchaseDateTime;
    }

    public void setPurchaseDateTime(LocalDateTime purchaseDateTime) {
        this.purchaseDateTime = purchaseDateTime;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        if (price < 0) {
            throw new IllegalArgumentException("Price cannot be negative.");
        }
        this.price = price;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public PurchaseStatus getStatus() {
        return status;
    }

    public void setStatus(PurchaseStatus status) {
        this.status = status;
    }
}
