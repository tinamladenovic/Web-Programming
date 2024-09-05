package beans;

public class Comment {
    private String id; // Jedinstveni identifikator komentara
    private User customer; // Kupac koji je ostavio komentar
    private ChocolateFactoryObject factory; // Fabrika na koju se komentar odnosi
    private String text; // Tekst komentara
    private int rating; // Ocena (na skali od 1 do 5)

    public Comment() {
        // Default constructor
    }

    public Comment(String id, User customer, ChocolateFactoryObject factory, String text, int rating) {
        this.id = id;
        this.customer = customer;
        this.factory = factory;
        this.text = text;
        this.setRating(rating);
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getCustomer() {
        return customer;
    }

    public void setCustomer(User customer) {
        this.customer = customer;
    }

    public ChocolateFactoryObject getFactory() {
        return factory;
    }

    public void setFactory(ChocolateFactoryObject factory) {
        this.factory = factory;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5.");
        }
        this.rating = rating;
    }
}
