package Store;

public class UpdateMessage {
    private String type;
    private int itemId;
    private String name;
    private double price;

    public UpdateMessage(String type, int itemId, String name, double price) {
        this.type = type;
        this.itemId = itemId;
        this.name = name;
        this.price = price;
    }

    // Getters (optional, only needed if you want to deserialize)
    public String getType() { return type; }
    public int getItemId() { return itemId; }
    public String getName() { return name; }
    public double getPrice() { return price; }
}
