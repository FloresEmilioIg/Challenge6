package Store.Controller;

import static spark.Spark.*;

import Store.Model.Offer;
import Store.Model.Product;
import Store.Service.OfferS;
import Store.Service.ProductS;
import Store.WebSockets;
import com.google.gson.Gson;

import spark.ModelAndView;
import spark.template.mustache.MustacheTemplateEngine;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ProductController {

    public static void registerRoutes(Gson gson) {

        /* ==========================
         *  MUSTACHE WEB VIEWS
         * ========================== */

        // ✅ IMPORTANT: define these BEFORE /products/:id
        // GET /products/view — show all products
        get("/products/view", (req, res) -> {
            String search = req.queryParams("search");
            String minParam = req.queryParams("minPrice");
            String maxParam = req.queryParams("maxPrice");
            String sortBy = req.queryParams("sortBy");

            double minPrice = (minParam != null && !minParam.isEmpty()) ? Double.parseDouble(minParam) : 0;
            double maxPrice = (maxParam != null && !maxParam.isEmpty()) ? Double.parseDouble(maxParam) : Double.MAX_VALUE;

            // Step 1: Get filtered products
            List<Product> filtered = ProductS.getProductsByPriceRange(minPrice, maxPrice);

            // Step 2: Apply name filtering if 'search' was provided
            if (search != null && !search.isEmpty()) {
                String lowerSearch = search.toLowerCase();
                filtered = filtered.stream()
                        .filter(p -> p.getProductName().toLowerCase().contains(lowerSearch))
                        .collect(Collectors.toList());
            }

            // Step 3: Sort results based on sortBy
            if (sortBy != null) {
                switch (sortBy) {
                    case "priceAsc":
                        filtered.sort(Comparator.comparingDouble(Product::getProductPrice));
                        break;
                    case "priceDesc":
                        filtered.sort(Comparator.comparingDouble(Product::getProductPrice).reversed());
                        break;
                    case "nameAsc":
                        filtered.sort(Comparator.comparing(Product::getProductName, String.CASE_INSENSITIVE_ORDER));
                        break;
                    case "nameDesc":
                        filtered.sort(Comparator.comparing(Product::getProductName, String.CASE_INSENSITIVE_ORDER).reversed());
                        break;
                }
            }

            // Step 4: Prepare model for Mustache
            Map<String, Object> model = new HashMap<>();
            model.put("products", filtered);
            model.put("search", search);
            model.put("minPrice", minParam);
            model.put("maxPrice", maxParam);

            // For keeping the selected sorting option checked
            model.put("isPriceAsc", "priceAsc".equals(sortBy));
            model.put("isPriceDesc", "priceDesc".equals(sortBy));
            model.put("isNameAsc", "nameAsc".equals(sortBy));
            model.put("isNameDesc", "nameDesc".equals(sortBy));

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

        get("/api/products", (req, res) -> {
            res.type("application/json");
            String search = req.queryParams("search");
            String minParam = req.queryParams("minPrice");
            String maxParam = req.queryParams("maxPrice");
            String sortBy = req.queryParams("sortBy");

            double minPrice = (minParam != null && !minParam.isEmpty()) ? Double.parseDouble(minParam) : 0;
            double maxPrice = (maxParam != null && !maxParam.isEmpty()) ? Double.parseDouble(maxParam) : Double.MAX_VALUE;

            List<Product> filtered = ProductS.getProductsByPriceRange(minPrice, maxPrice);

            if (search != null && !search.isEmpty()) {
                filtered = filtered.stream()
                        .filter(p -> p.getProductName().toLowerCase().contains(search.toLowerCase()))
                        .collect(Collectors.toList());
            }

            // Sort
            if (sortBy != null) {
                switch (sortBy) {
                    case "priceAsc": filtered.sort(Comparator.comparingDouble(Product::getProductPrice)); break;
                    case "priceDesc": filtered.sort(Comparator.comparingDouble(Product::getProductPrice).reversed()); break;
                    case "nameAsc": filtered.sort(Comparator.comparing(Product::getProductName, String.CASE_INSENSITIVE_ORDER)); break;
                    case "nameDesc": filtered.sort(Comparator.comparing(Product::getProductName, String.CASE_INSENSITIVE_ORDER).reversed()); break;
                }
            }

            return new Gson().toJson(filtered);
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

            // Broadcast real-time update to all clients
            String message = new Gson().toJson(Map.of(
                    "type", "updatePrice",
                    "itemId", offer.getId(),
                    "price", offer.getAmount()
            ));
            WebSockets.broadcast(message);

            res.status(201);
            return "Offer submitted successfully!";
        });

        get("/api/offers", (req, res) -> {
            res.type("application/json");
            return gson.toJson(OfferS.getAllOffers());
        });
    }
}
