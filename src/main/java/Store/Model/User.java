package Store.Model;

public class User {
    private int userId;
    private String name;
    private String email;

    public User(){}

    public User(int id, String name, String email) {
        this.userId = id;
        this.name = name;
        this.email = email;
    }

    //Get and set
    public String getEmail() {

        return email;
    }

    public void setEmail(String email) {

        this.email = email;
    }

    public int getId() {

        return userId;
    }

    public void setId(int id) {

        this.userId = id;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {

        this.name = name;
    }
}
