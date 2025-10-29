package Store.Controller;

import static spark.Spark.*;

import Store.Model.User;
import Store.Service.UserS;
import com.google.gson.Gson;

import java.util.Map;

public class UserController {

    public static void registerRoutes(Gson gson) {


        //GET /users - Retrieve all users
        get("/users", (req, res) -> {
            res.type("application/json");
            return gson.toJson(UserS.getAllUsers());
        });

        //GET users/:id - Retrieve user by ID
        get("/users/:id", (req, res) -> {int id = Integer.parseInt(req.params(":id"));
            User user = UserS.getUser(id);
            if(user == null){
                res.status(404);
                return "User not found";
            }
            res.type("application/json");
            return gson.toJson(user);

        });

        // POST /users/:id — Add new user
        post("/users/:id", (req, res) -> {
            int id = Integer.parseInt(req.params(":id"));
            if (UserS.exists(id)) {
                res.status(400);
                return "User already exists";
            }
            User user = gson.fromJson(req.body(), User.class);
            user.setId(id);
            UserS.addUser(user);
            res.status(201);
            return "User added";
        });

        // PUT /users/:id — Update user
        put("/users/:id", (req, res) -> {
            int id = Integer.parseInt(req.params(":id"));
            User user = gson.fromJson(req.body(), User.class);
            user.setId(id);
            if (UserS.updateUser(id, user)) {
                res.status(200);
                return "User updated";
            } else {
                res.status(404);
                return "User not found";
            }
        });

        // OPTIONS /users/:id — Check existence
        options("/users/:id", (req, res) -> {
            int id = Integer.parseInt(req.params(":id"));
            res.type("application/json");
            return gson.toJson(Map.of("exists", UserS.exists(id)));
        });

        // DELETE /users/:id — Delete user
        delete("/users/:id", (req, res) -> {
            int id = Integer.parseInt(req.params(":id"));
            if (UserS.deleteUser(id)) {
                res.status(200);
                return "User deleted";
            } else {
                res.status(404);
                return "User not found";
            }
        });
    }


}
