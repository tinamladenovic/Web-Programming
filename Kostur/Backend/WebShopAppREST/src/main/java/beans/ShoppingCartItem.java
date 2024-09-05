package beans;

public class ShoppingCartItem {
    private Integer id;
    private ShoppingCart shoppingCart;
    private Chocolate chocolate;
    private boolean isDeleted;

    public ShoppingCartItem() {
        // Default constructor
    }

    public ShoppingCartItem(Integer id, ShoppingCart shoppingCart, Chocolate chocolate, boolean isDeleted) {
        this.id = id;
        this.shoppingCart = shoppingCart;
        this.chocolate = chocolate;
        this.isDeleted = isDeleted;
    }

    // Getters and setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        if (id == null || id < 0) {
            throw new IllegalArgumentException("ID cannot be null or negative.");
        }
        this.id = id;
    }

    public ShoppingCart getShoppingCart() {
        return shoppingCart;
    }

    public void setShoppingCart(ShoppingCart shoppingCart) {
        if (shoppingCart == null) {
            throw new IllegalArgumentException("Shopping cart cannot be null.");
        }
        this.shoppingCart = shoppingCart;
    }

    public Chocolate getChocolate() {
        return chocolate;
    }

    public void setChocolate(Chocolate chocolate) {
        if (chocolate == null) {
            throw new IllegalArgumentException("Chocolate cannot be null.");
        }
        this.chocolate = chocolate;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }
}
