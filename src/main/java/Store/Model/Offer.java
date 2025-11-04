package Store.Model;

public class Offer {
    private String name;
    private String email;
    private String id; // product ID being bid on
    private double amount;

    public Offer() {}

    public Offer(String name, String email, String id, double amount) {
        this.name = name;
        this.email = email;
        this.id = id;
        this.amount = amount;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
}
