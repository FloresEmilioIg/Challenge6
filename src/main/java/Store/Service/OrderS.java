package Store.Service;

import Store.Model.Order;
import java.util.*;

public class OrderS {

    private static final Map<Integer, Order> orders = new HashMap<>();

    // Get all orders
    public static Collection<Order> getAllOrders() {
        return orders.values();
    }

    // Get a specific order by ID
    public static Order getOrder(int id) {
        return orders.get(id);
    }

    // Add a new order
    public static void addOrder(Order order) {
        orders.put(order.getOrderId(), order);
    }

    // Update an existing order
    public static boolean updateOrder(int id, Order order) {
        if (orders.containsKey(id)) {
            orders.put(id, order);
            return true;
        }
        return false;
    }

    // Delete an order
    public static boolean deleteOrder(int id) {
        return orders.remove(id) != null;
    }

    // Check if an order exists
    public static boolean exists(int id) {
        return orders.containsKey(id);
    }

}
