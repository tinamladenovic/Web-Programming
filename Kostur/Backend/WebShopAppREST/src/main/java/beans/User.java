package beans;

import java.time.LocalDate;
import javax.json.bind.annotation.JsonbDateFormat;

import beans.enums.Gender;
import beans.enums.UserRole;

public class User {
    private String id;
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private Gender gender;
    
    @JsonbDateFormat("yyyy-MM-dd")
    private LocalDate birthDate;
    
    private UserRole role;
    private CustomerType customerType;
    private Integer earnedPoints;
    private ShoppingCart shoppingCart;
    private boolean blocked;
    private String factoryId;

    public User() {
        super();
    }

    public User(String id, String username, String password, String firstName, String lastName, Gender gender,
                LocalDate birthDate, UserRole role, CustomerType customerType, Integer earnedPoints,
                ShoppingCart shoppingCart, boolean blocked, String factoryId) {
        super();
        this.id = id;
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.birthDate = birthDate;
        this.role = role;
        this.customerType = customerType;
        this.earnedPoints = earnedPoints;
        this.shoppingCart = shoppingCart;
        this.blocked = blocked;
        this.factoryId = factoryId;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public CustomerType getCustomerType() {
        return customerType;
    }

    public void setCustomerType(CustomerType customerType) {
        this.customerType = customerType;
    }

    public Integer getEarnedPoints() {
        return earnedPoints;
    }

    public void setEarnedPoints(Integer earnedPoints) {
        this.earnedPoints = earnedPoints;
    }

    public ShoppingCart getShoppingCart() {
        return shoppingCart;
    }

    public void setShoppingCart(ShoppingCart shoppingCart) {
        this.shoppingCart = shoppingCart;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    public boolean isValid() {
        return this.username != null && !this.username.isEmpty()
                && this.password != null && !this.password.isEmpty()
                && this.firstName != null && !this.firstName.isEmpty()
                && this.lastName != null && !this.lastName.isEmpty()
                && this.gender != null
                && this.birthDate != null && this.birthDate.isBefore(LocalDate.now().minusYears(18).plusDays(1));
    }

    public String getFactoryId() {
        return factoryId;
    }

    public void setFactoryId(String factoryId) {
        this.factoryId = factoryId;
    }
}
