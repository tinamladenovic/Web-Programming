package beans;

import java.time.LocalTime;

import java.util.List;

import beans.enums.ChocolateFactoryObjectStatus;

public class ChocolateFactoryObject {
    private String factoryId;
    private String name;
    private List<Chocolate> chocolates; 
    private LocalTime openingTime;
    private LocalTime closingTime;
    private Location location;
    private String logo;
    private Double rating;
    private ChocolateFactoryObjectStatus status;
    private String managerId; // Novo polje
    
    public ChocolateFactoryObject() {
        // Default constructor
    }

    public ChocolateFactoryObject(String factoryId, String name, List<Chocolate> chocolates, LocalTime openingTime, LocalTime closingTime, Location location, String logo, double rating, ChocolateFactoryObjectStatus status, String managerId) {
        this.factoryId = factoryId;
        this.name = name;
        this.chocolates = chocolates;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
        this.location = location;
        this.logo = logo;
        this.rating = rating;
        this.status = status;
        this.managerId = managerId;
    }

    // Getters and setters
    public String getFactoryId() {
        return factoryId;
    }

    public void setFactoryId(String factoryId) {
        this.factoryId = factoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Chocolate> getChocolates() {
        return chocolates;
    }

    public void setChocolates(List<Chocolate> chocolates) {
        this.chocolates = chocolates;
    }

    public LocalTime getOpeningTime() {
        return openingTime;
    }

    public void setOpeningTime(LocalTime openingTime) {
        if (openingTime == null) {
            throw new IllegalArgumentException("Opening time cannot be null");
        }
        this.openingTime = openingTime;
    }

    public LocalTime getClosingTime() {
        return closingTime;
    }

    public void setClosingTime(LocalTime closingTime) {
        if (closingTime == null) {
            throw new IllegalArgumentException("Closing time cannot be null");
        }
        this.closingTime = closingTime;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        if (location == null) {
            throw new IllegalArgumentException("Location cannot be null");
        }
        this.location = location;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        if (rating < 0 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 0 and 5");
        }
        this.rating = rating;
    }

    public ChocolateFactoryObjectStatus getStatus() {
        return status;
    }

    public void setStatus(ChocolateFactoryObjectStatus status) {
        this.status = status;
    }

    public String getManagerId() {
        return managerId;
    }

    public void setManagerId(String managerId) {
        this.managerId = managerId;
    }

    @Override
    public String toString() {
        return "ChocolateFactoryObject{" +
                "factoryId='" + factoryId + '\'' +
                ", name='" + name + '\'' +
                ", chocolates=" + chocolates +
                ", openingTime=" + openingTime +
                ", closingTime=" + closingTime +
                ", location=" + location +
                ", logo='" + logo + '\'' +
                ", rating=" + rating +
                ", status=" + status +
                ", managerId='" + managerId + '\'' +
                '}';
    }
}
