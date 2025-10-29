package Store.Controller;

import static spark.Spark.*;

import Store.Model.Product;
import Store.Service.ProductS;
import com.google.gson.Gson;

import java.util.Map;

public class ProductController {

    public static void registerRoutes(Gson gson) {


        // GET /products
        get("/products", (req, res) -> {
            res.type("application/json");
            return gson.toJson(ProductS.getAllProducts());
        });

        // GET /products/:id
        get("/products/:id", (req, res) -> {
            int id = Integer.parseInt(req.params(":id"));
            Product product = ProductS.getProduct(id);
            if (product == null) {
                res.status(404);
                return "Product not found";
            }
            res.type("application/json");
            return gson.toJson(product);
        });

        // POST /products/:id
        post("/products/:id", (req, res) -> {
            int id = Integer.parseInt(req.params(":id"));
            if (ProductS.exists(id)) {
                res.status(400);
                return "Product already exists";
            }
            Product product = gson.fromJson(req.body(), Product.class);
            product.setId(id);
            ProductS.addProduct(product);
            res.status(201);
            return "Product added";
        });

        // PUT /products/:id
        put("/products/:id", (req, res) -> {
            int id = Integer.parseInt(req.params(":id"));
            Product product = gson.fromJson(req.body(), Product.class);
            product.setId(id);
            if (ProductS.updateProduct(id, product)) {
                res.status(200);
                return "Product updated";
            } else {
                res.status(404);
                return "Product not found";
            }
        });

        // OPTIONS /products/:id
        options("/products/:id", (req, res) -> {
            int id = Integer.parseInt(req.params(":id"));
            res.type("application/json");
            return gson.toJson(Map.of("exists", ProductS.exists(id)));
        });

        // DELETE /products/:id
        delete("/products/:id", (req, res) -> {
            int id = Integer.parseInt(req.params(":id"));
            if (ProductS.deleteProduct(id)) {
                res.status(200);
                return "Product deleted";
            } else {
                res.status(404);
                return "Product not found";
            }
        });
    }

}
