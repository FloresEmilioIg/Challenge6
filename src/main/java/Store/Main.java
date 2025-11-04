package Store;

import static spark.Spark.*;

import Store.Controller.OrderController;
import Store.Controller.UserController;
import Store.Controller.ProductController;
import Store.Errors.GlobalExceptionHandler;
import com.google.gson.Gson;
import spark.template.mustache.MustacheTemplateEngine;
import spark.ModelAndView;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        // Global Spark setup
        port(8080);

        // ✅ Serve static files (from /resources/public)
        staticFiles.location("/static");

        Gson gson = new Gson();


        // Register all controller routes
        GlobalExceptionHandler.register();
        UserController.registerRoutes(gson);
        ProductController.registerRoutes(gson);
        OrderController.registerRoutes(gson);

        // ✅ Example: render a Mustache template
        get("/", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            model.put("title", "Welcome to the Store!");
            return new ModelAndView(model, "products.mustache"); // located in /resources/templates/
        }, new MustacheTemplateEngine());

        System.out.println("🚀 Server running on http://localhost:8080");
    }
}
