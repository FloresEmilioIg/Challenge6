package Store.Controller;

import static spark.Spark.*;
import Store.Model.Order;
import Store.Model.Product;
import Store.Model.User;
import Store.Service.OrderS;
import Store.Service.UserS;
import Store.Service.ProductS;
import com.google.gson.Gson;
import java.util.Map;

public class OrderController {

    public static void registerRoutes(Gson gson) {

        // GET /orders — all orders
        get("/orders", (req, res) -> {
            res.type("application/json");
            return gson.toJson(OrderS.getAllOrders());
        });

        // GET /orders/:id — single order
        get("/orders/:id", (req, res) -> {
            int id = Integer.parseInt(req.params(":id"));
            Order order = OrderS.getOrder(id);
            if (order == null) {
                res.status(404);
                return "Order not found";
            }
            res.type("application/json");
            return gson.toJson(order);
        });

        // POST /orders/:id — create new order
        post("/orders/:id", (req, res) -> {
            int id = Integer.parseInt(req.params(":id"));
            if (OrderS.exists(id)) {
                res.status(400);
                return "Order already exists";
            }

            // Parse request body
            Map<String, Object> data = gson.fromJson(req.body(), Map.class);
            int userId = ((Double) data.get("userId")).intValue();
            int productId = ((Double) data.get("productId")).intValue();
            int quantity = ((Double) data.get("quantity")).intValue();

            // Fetch existing user and product
            User user = UserS.getUser(userId);
            Product product = ProductS.getProduct(productId);

            if (user == null) {
                res.status(400);
                return "Invalid user ID";
            }
            if (product == null) {
                res.status(400);
                return "Invalid product ID";
            }

            // Create the order
            Order order = new Order(id, user, product, quantity);
            OrderS.addOrder(order);

            res.status(201);
            return "Order created";
        });

        // PUT /orders/:id — update existing order
        put("/orders/:id", (req, res) -> {
            int id = Integer.parseInt(req.params(":id"));

            Map<String, Object> data = gson.fromJson(req.body(), Map.class);
            int userId = ((Double) data.get("userId")).intValue();
            int productId = ((Double) data.get("productId")).intValue();
            int quantity = ((Double) data.get("quantity")).intValue();

            User user = UserS.getUser(userId);
            Product product = ProductS.getProduct(productId);

            if (user == null) {
                res.status(400);
                return "Invalid user ID";
            }
            if (product == null) {
                res.status(400);
                return "Invalid product ID";
            }

            Order updatedOrder = new Order(id, user, product, quantity);

            if (OrderS.updateOrder(id, updatedOrder)) {
                res.status(200);
                return "Order updated";
            } else {
                res.status(404);
                return "Order not found";
            }
        });

        // OPTIONS /orders/:id
        options("/orders/:id", (req, res) -> {
            int id = Integer.parseInt(req.params(":id"));
            res.type("application/json");
            return gson.toJson(Map.of("exists", OrderS.exists(id)));
        });

        // DELETE /orders/:id
        delete("/orders/:id", (req, res) -> {
            int id = Integer.parseInt(req.params(":id"));
            if (OrderS.deleteOrder(id)) {
                res.status(200);
                return "Order deleted";
            } else {
                res.status(404);
                return "Order not found";
            }
        });
    }

}
