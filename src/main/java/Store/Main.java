package Store;

import static spark.Spark.*;

import Store.Controller.OrderController;
import Store.Controller.UserController;
import Store.Controller.ProductController;
import Store.Errors.GlobalExceptionHandler;
import Store.Model.Product;
import Store.Service.ProductS;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import spark.template.mustache.MustacheTemplateEngine;
import spark.ModelAndView;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.lang.reflect.Type;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // Global Spark setup
        port(8080);

        // ✅ Serve static files (from /resources/public)
        staticFiles.location("/static");

        webSocket("/ws", WebSockets.class);

        Gson gson = new Gson();

        try (Reader reader = new InputStreamReader(
                Main.class.getResourceAsStream("/data.json"))) {

            Type productListType = new TypeToken<List<Product>>() {}.getType();
            List<Product> products = gson.fromJson(reader, productListType);
            ProductS.setInitialProducts(products);

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("⚠️ Could not load initial products JSON.");
        }

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
