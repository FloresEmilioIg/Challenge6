package Store.Errors;

import com.google.gson.Gson;

import java.util.Map;

import static spark.Spark.exception;

public class GlobalExceptionHandler {
    public static void register() {
        Gson gson = new Gson();

        exception(NotFoundException.class, (ex, req, res) -> {
            res.type("application/json");
            res.status(404);
            res.body(gson.toJson(Map.of("error", ex.getMessage())));
        });

        exception(BadRequestException.class, (ex, req, res) -> {
            res.type("application/json");
            res.status(400);
            res.body(gson.toJson(Map.of("error", ex.getMessage())));
        });

        exception(Exception.class, (ex, req, res) -> {
            res.type("application/json");
            res.status(500);
            res.body(gson.toJson(Map.of("error", "Internal server error")));
            ex.printStackTrace();
        });
    }
}
