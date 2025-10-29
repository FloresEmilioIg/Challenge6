package Store.Service;

import Store.Model.Product;
import java.util.*;

public class ProductS {
    private static Map<Integer, Product> products = new HashMap<>();

    public static Collection<Product> getAllProducts() {
        return products.values();
    }

    public static Product getProduct(int id) {
        return products.get(id);
    }

    public static void addProduct(Product product) {
        products.put(product.getId(), product);
    }

    public static boolean updateProduct(int id, Product product) {
        if (products.containsKey(id)) {
            products.put(id, product);
            return true;
        }
        return false;
    }

    public static boolean deleteProduct(int id) {
        return products.remove(id) != null;
    }

    public static boolean exists(int id) {
        return products.containsKey(id);
    }
}

