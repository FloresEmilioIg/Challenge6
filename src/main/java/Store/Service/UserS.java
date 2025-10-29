package Store.Service;

import Store.Model.User;

import java.util.*;

public class UserS {
    private static Map<Integer, User> users = new HashMap<>();

    public static Collection<User> getAllUsers(){
        return users.values();
    }

    public static User getUser(int id){
        return users.get(id);
    }

    public static void addUser(User user){
        users.put(user.getId(),user);
    }

    public static boolean updateUser(int id,User user){
        if(users.containsKey(id)){
            users.put(id,user);
            return true;
        }
        return false;
    }

    public static boolean deleteUser(int id){
        return users.remove(id) != null;
    }

    public static boolean exists(int id){
        return users.containsKey(id);
    }
}
