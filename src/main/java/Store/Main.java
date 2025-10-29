package Store;

import static spark.Spark.*;

import Store.Controller.OrderController;
import Store.Controller.UserController;
import Store.Controller.ProductController;
import com.google.gson.Gson;

public class Main {
    public static void main(String[] args) {
        // Global Spark setup
        port(8080);
        Gson gson = new Gson();


        // Register all controller routes
        UserController.registerRoutes(gson);
        ProductController.registerRoutes(gson);
        OrderController.registerRoutes(gson);

        System.out.println("🚀 Server running on http://localhost:8080");
    }
}
