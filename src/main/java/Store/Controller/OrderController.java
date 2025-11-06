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

        OrderS orderS = new OrderS();
        UserS userS = new UserS();
        ProductS productS = new ProductS();

        // GET /orders — all orders
        get("/orders", (req, res) -> {
            res.type("application/json");
            return gson.toJson(orderS.getAllOrders());
        });

        // GET /orders/:id
        get("/orders/:id", (req, res) -> {
            int id = Integer.parseInt(req.params(":id"));
            Order order = orderS.getOrder(id);
            if (order == null) {
                res.status(404);
                return "Order not found";
            }
            res.type("application/json");
            return gson.toJson(order);
        });

        // POST /orders
        post("/orders", (req, res) -> {
            Map<String, Object> data = gson.fromJson(req.body(), Map.class);
            int userId = ((Double) data.get("userId")).intValue();
            int productId = ((Double) data.get("productId")).intValue();
            int quantity = ((Double) data.get("quantity")).intValue();

            User user = userS.getUser(userId);
            Product product = productS.getProduct(productId);

            if (user == null || product == null) {
                res.status(400);
                return "Invalid user or product ID";
            }

            Order order = new Order(0, user, product, quantity);
            orderS.addOrder(order);
            res.status(201);
            return "Order created";
        });

        // PUT /orders/:id
        put("/orders/:id", (req, res) -> {
            int id = Integer.parseInt(req.params(":id"));
            Map<String, Object> data = gson.fromJson(req.body(), Map.class);

            int userId = ((Double) data.get("userId")).intValue();
            int productId = ((Double) data.get("productId")).intValue();
            int quantity = ((Double) data.get("quantity")).intValue();

            User user = userS.getUser(userId);
            Product product = productS.getProduct(productId);

            if (user == null || product == null) {
                res.status(400);
                return "Invalid user or product ID";
            }

            Order updatedOrder = new Order(id, user, product, quantity);
            boolean success = OrderS.updateOrder(id, updatedOrder);

            if (success) {
                res.status(200);
                return "Order updated";
            } else {
                res.status(404);
                return "Order not found";
            }
        });

        // DELETE /orders/:id
        delete("/orders/:id", (req, res) -> {
            int id = Integer.parseInt(req.params(":id"));
            boolean deleted = orderS.deleteOrder(id);
            if (deleted) {
                res.status(200);
                return "Order deleted";
            } else {
                res.status(404);
                return "Order not found";
            }
        });
    }
}