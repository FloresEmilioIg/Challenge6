package Store.Controller;

import static spark.Spark.*;

import Store.Model.Offer;
import Store.Model.Product;
import Store.Service.OfferS;
import Store.Service.ProductS;
import com.google.gson.Gson;
import spark.ModelAndView;
import spark.template.mustache.MustacheTemplateEngine;

import java.util.HashMap;
import java.util.Map;

public class ProductController {

    public static void registerRoutes(Gson gson) {

        /* ==========================
         *  MUSTACHE WEB VIEWS
         * ========================== */

        // ✅ IMPORTANT: define these BEFORE /products/:id
        // GET /products/view — show all products
        get("/products/view", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            model.put("products", ProductS.getAllProducts());
            return new ModelAndView(model, "products.mustache");
        }, new MustacheTemplateEngine());

        // GET /products/view/:id — show single product
        get("/products/view/:id", (req, res) -> {
            int id = Integer.parseInt(req.params(":id"));
            Product product = ProductS.getProduct(id);

            Map<String, Object> model = new HashMap<>();

            if (product == null) {
                res.status(404);
                model.put("message", "Product not found");
                return new ModelAndView(model, "error.mustache"); // ✅
            }

            model.put("productName", product.getProductName());
            model.put("productPrice", product.getProductPrice());
            model.put("description", product.getDescription() != null ? product.getDescription() : "No description available");
            return new ModelAndView(model, "product_detail.mustache");
        }, new MustacheTemplateEngine());

        // GET /products/view/:id/offer — show the offer form for a specific product
        get("/products/view/:id/offer", (req, res) -> {
            int id = Integer.parseInt(req.params(":id"));
            Product product = ProductS.getProduct(id);

            Map<String, Object> model = new HashMap<>();

            if (product == null) {
                res.status(404);
                model.put("message", "Product not found");
                return new ModelAndView(model, "error.mustache");  // ✅ Render error view
            }

            model.put("product", product);
            return new ModelAndView(model, "form.mustache");  // ✅ Normal case
        }, new MustacheTemplateEngine());

        /* ==========================
         *  JSON API ENDPOINTS
         * ========================== */

        // GET /products — list all products
        get("/products", (req, res) -> {
            res.type("application/json");
            return gson.toJson(ProductS.getAllProducts());
        });

        // GET /products/:id — get product by id
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

        // POST /products/:id — add product
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

        // PUT /products/:id — update product
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

        // DELETE /products/:id — delete product
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

        // OPTIONS /products/:id — existence check
        options("/products/:id", (req, res) -> {
            int id = Integer.parseInt(req.params(":id"));
            res.type("application/json");
            return gson.toJson(Map.of("exists", ProductS.exists(id)));
        });

        /* ==========================
         *  OFFERS
         * ========================== */

        post("/api/offer", (req, res) -> {
            Offer offer = new Offer();
            offer.setName(req.queryParams("name"));
            offer.setEmail(req.queryParams("email"));
            offer.setAmount(Double.parseDouble(req.queryParams("amount")));
            offer.setId(req.queryParams("id"));
            OfferS.addOffer(offer);
            res.status(201);
            return "Offer submitted successfully!";
        });

        get("/api/offers", (req, res) -> {
            res.type("application/json");
            return gson.toJson(OfferS.getAllOffers());
        });
    }
}
