package beans;

import beans.enums.CustomerTypeName;

public class CustomerType {
    public static final CustomerType GOLD = new CustomerType(CustomerTypeName.GOLD, 0.2, 1000);
    public static final CustomerType SILVER = new CustomerType(CustomerTypeName.SILVER, 0.1, 500);
    public static final CustomerType BRONZE = new CustomerType(CustomerTypeName.BRONZE, 0.05, 100);
    public static final CustomerType NO_TYPE = new CustomerType(CustomerTypeName.NO_TYPE, 0.0, 0);

    private CustomerTypeName name;
    private Double discount;
    private Integer requiredPoints;
    
    public CustomerType() {
        super();
    }
    
    public CustomerType(CustomerTypeName name, Double discount, Integer requiredPoints) {
        super();
        this.name = name;
        this.discount = discount;
        this.requiredPoints = requiredPoints;
    }

    public CustomerTypeName getName() {
        return name;
    }
    
    public void setName(CustomerTypeName name) {
        this.name = name;
    }
    
    public Double getDiscount() {
        return discount;
    }
    
    public void setDiscount(Double discount) {
        if (discount < 0 || discount > 1) {
            throw new IllegalArgumentException("Discount must be between 0 and 1.");
        }
        this.discount = discount;
    }
    
    public Integer getRequiredPoints() {
        return requiredPoints;
    }
    
    public void setRequiredPoints(Integer requiredPoints) {
        if (requiredPoints < 0) {
            throw new IllegalArgumentException("Required points must be a positive integer.");
        }
        this.requiredPoints = requiredPoints;
    }

    public static CustomerType valueOf(String name) {
        if (name != null) {
            switch (name.toUpperCase()) {
                case "GOLD":
                    return CustomerType.GOLD;
                case "SILVER":
                    return CustomerType.SILVER;
                case "BRONZE":
                    return CustomerType.BRONZE;
                case "NO_TYPE":
                    return CustomerType.NO_TYPE;
                default:
                    throw new IllegalArgumentException("Unknown customer type: " + name);
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "CustomerType{" +
                "name=" + name +
                ", discount=" + discount +
                ", requiredPoints=" + requiredPoints +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CustomerType that = (CustomerType) o;

        if (name != that.name) return false;
        if (!discount.equals(that.discount)) return false;
        return requiredPoints.equals(that.requiredPoints);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + discount.hashCode();
        result = 31 * result + requiredPoints.hashCode();
        return result;
    }
}
