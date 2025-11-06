package Store.Model;

public class Order {
    private int orderId;
    private User user;
    private Product product;
    private int quantity;
    private double totalPrice;

    public Order(int orderId, User user, Product product, int quantity, double totalPrice) {
        this.orderId = orderId;
        this.user = user;
        this.product = product;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
    }

    public Order(int orderId, User user, Product product, int quantity) {
        this.orderId = orderId;
        this.user = user;
        this.product = product;
        this.quantity = quantity;
        this.totalPrice = product.getProductPrice() * quantity;
    }

    // Getters and setters
    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
        this.totalPrice = product != null ? product.getProductPrice() * quantity : 0;
    }

    public double getTotalPrice() { return totalPrice; }
}