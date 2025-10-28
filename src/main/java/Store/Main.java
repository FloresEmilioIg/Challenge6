package Store;

import static spark.Spark.*;

public class Main {
    public static void main(String[] args) {
        // Global Spark setup
        port(8080);

        // Initialize controllers
        new UserController();

        System.out.println("🚀 Server running on http://localhost:8080");
    }
}
