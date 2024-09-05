package beans;

import beans.enums.ChocolateType;
import beans.enums.ChocolateKind;
import beans.enums.ChocolateStatus;

public class Chocolate {
    private String id;
    private String name;
    private double price;
    private ChocolateType type;
    private String factoryId;
    private ChocolateKind kind;
    private double weight;
    private String description;
    private String picture;
    private ChocolateStatus status;
    private int quantity;

    public Chocolate() {
        // Default constructor
    }

    public Chocolate(String id, String name, double price, ChocolateType type, String factoryId, ChocolateKind kind,
                     double weight, String description, String picture, ChocolateStatus status, int quantity) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.type = type;
        this.factoryId = factoryId;
        this.kind = kind;
        this.weight = weight;
        this.description = description;
        this.picture = picture;
        this.status = status;
        this.quantity = quantity;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public ChocolateType getType() {
        return type;
    }

    public void setType(ChocolateType type) {
        this.type = type;
    }

    public String getFactoryId() {
        return factoryId;
    }

    public void setFactoryId(String factoryId) {
        this.factoryId = factoryId;
    }

    public ChocolateKind getKind() {
        return kind;
    }

    public void setKind(ChocolateKind kind) {
        this.kind = kind;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        if (weight < 0) {
            throw new IllegalArgumentException("Weight cannot be negative.");
        }
        this.weight = weight;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public ChocolateStatus getStatus() {
        return status;
    }

    public void setStatus(ChocolateStatus status) {
        this.status = status;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative.");
        }
        this.quantity = quantity;
    }
}
