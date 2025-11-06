package Store.Service;

import Store.DB;
import Store.Model.Product;
import java.sql.*;
import java.util.*;

public class ProductS {

    public static void setInitialProducts(List<Product> initialProducts) {
        String countSql = "SELECT COUNT(*) FROM products";
        String insertSql = "INSERT INTO products (id, product_name, product_price, description) VALUES (?, ?, ?, ?)";

        try (Connection conn = DB.getConnection();
             Statement countStmt = conn.createStatement();
             ResultSet rs = countStmt.executeQuery(countSql)) {

            if (rs.next() && rs.getInt(1) == 0) {
                System.out.println("🪄 Database is empty — loading initial products...");

                try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
                    for (Product product : initialProducts) {
                        ps.setInt(1, product.getId());
                        ps.setString(2, product.getProductName());
                        ps.setDouble(3, product.getProductPrice());
                        ps.setString(4, product.getDescription());
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }

                System.out.println("✅ Loaded " + initialProducts.size() + " products into the database.");
            } else {
                System.out.println("ℹ️ Products table already contains data. Skipping preload.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("⚠️ Failed to insert initial products into database.");
        }
    }

    public static List<Product> getAllProducts() {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT id, product_name, product_price, description FROM products";

        try (Connection conn = DB.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Product p = new Product();
                p.setId(rs.getInt("id"));
                p.setProductName(rs.getString("product_name"));
                p.setProductPrice(rs.getDouble("product_price"));
                p.setDescription(rs.getString("description"));
                list.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static Product getProduct(int id) {
        String sql = "SELECT * FROM products WHERE id = ?";
        try (Connection conn = DB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Product p = new Product();
                p.setId(rs.getInt("id"));
                p.setProductName(rs.getString("product_name"));
                p.setProductPrice(rs.getDouble("product_price"));
                p.setDescription(rs.getString("description"));
                return p;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Product> getProductsByPriceRange(double minPrice, double maxPrice) {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM products WHERE product_price BETWEEN ? AND ?";

        try (Connection conn = DB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDouble(1, minPrice);
            ps.setDouble(2, maxPrice);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Product product = new Product(
                        rs.getInt("id"),
                        rs.getString("product_name"),
                        rs.getDouble("product_price"),
                        rs.getString("description")
                );
                products.add(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return products;
    }

    public static void addProduct(Product product) {
        String sql = "INSERT INTO products (id, product_name, product_price, description) VALUES (?, ?, ?, ?)";
        try (Connection conn = DB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, product.getId());
            ps.setString(2, product.getProductName());
            ps.setDouble(3, product.getProductPrice());
            ps.setString(4, product.getDescription());
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean updateProduct(int id, Product product) {
        String sql = "UPDATE products SET product_name=?, product_price=?, description=? WHERE id=?";
        try (Connection conn = DB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, product.getProductName());
            ps.setDouble(2, product.getProductPrice());
            ps.setString(3, product.getDescription());
            ps.setInt(4, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean deleteProduct(int id) {
        String sql = "DELETE FROM products WHERE id=?";
        try (Connection conn = DB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean exists(int id) {
        return getProduct(id) != null;
    }
}

