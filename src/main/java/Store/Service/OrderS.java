package Store.Service;

import Store.DB;
import Store.Model.*;
import java.sql.*;
import java.util.*;

public class OrderS {

    public static List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();
        String sql = """
            SELECT o.order_id, o.quantity, o.total_price,
                   u.user_id, u.name AS user_name, u.email AS user_email,
                   p.id AS product_id, p.product_name, p.product_price, p.description
            FROM orders o
            JOIN users u ON o.user_id = u.user_id
            JOIN products p ON o.product_id = p.id
        """;

        try (Connection conn = DB.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                User user = new User(rs.getInt("user_id"), rs.getString("user_name"), rs.getString("user_email"));
                Product product = new Product(rs.getInt("product_id"), rs.getString("product_name"), rs.getDouble("product_price"), rs.getString("description"));
                Order order = new Order(rs.getInt("order_id"), user, product, rs.getInt("quantity"), rs.getDouble("total_price"));
                orders.add(order);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

    public static void addOrder(Order order) {
        String sql = "INSERT INTO orders (order_id, user_id, product_id, quantity, total_price) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, order.getOrderId());
            ps.setInt(2, order.getUser().getId());
            ps.setInt(3, order.getProduct().getId());
            ps.setInt(4, order.getQuantity());
            ps.setDouble(5, order.getTotalPrice());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Order getOrder(int id) {
        String sql = """
            SELECT o.order_id, o.quantity, o.total_price,
                   u.user_id, u.name AS user_name, u.email AS user_email,
                   p.id AS product_id, p.product_name, p.product_price, p.description
            FROM orders o
            JOIN users u ON o.user_id = u.user_id
            JOIN products p ON o.product_id = p.id
            WHERE o.order_id = ?
        """;
        try (Connection conn = DB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                User user = new User(rs.getInt("user_id"), rs.getString("user_name"), rs.getString("user_email"));
                Product product = new Product(rs.getInt("product_id"), rs.getString("product_name"), rs.getDouble("product_price"), rs.getString("description"));
                return new Order(rs.getInt("order_id"), user, product, rs.getInt("quantity"), rs.getDouble("total_price"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean updateOrder(int id, Order order) {
        String sql = """
        UPDATE orders
        SET user_id = ?, product_id = ?, quantity = ?, total_price = ?
        WHERE order_id = ?
    """;

        try (Connection conn = DB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, order.getUser().getId());
            ps.setInt(2, order.getProduct().getId());
            ps.setInt(3, order.getQuantity());
            ps.setDouble(4, order.getTotalPrice());
            ps.setInt(5, id);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0; // true if updated successfully

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public static boolean deleteOrder(int id) {
        String sql = "DELETE FROM orders WHERE order_id=?";
        try (Connection conn = DB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
