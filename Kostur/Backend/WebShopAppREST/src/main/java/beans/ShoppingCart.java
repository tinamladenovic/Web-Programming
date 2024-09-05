package beans;

import java.util.ArrayList;
import java.util.List;

public class ShoppingCart {
    private Integer id;
    private User user;
    private List<ShoppingCartItem> items = new ArrayList<>();
    private Double price;

    public ShoppingCart() {
        // Default constructor
    }

    public ShoppingCart(Integer id, User user, List<ShoppingCartItem> items, Double price) {
        this.id = id;
        this.user = user;
        this.items = items;
        this.price = price;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<ShoppingCartItem> getItems() {
        return items;
    }

    public void setItems(List<ShoppingCartItem> items) {
        this.items = items;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        if (price < 0) {
            throw new IllegalArgumentException("Price cannot be negative.");
        }
        this.price = price;
    }
}
