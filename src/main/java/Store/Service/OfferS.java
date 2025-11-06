package Store.Service;

import Store.DB;
import Store.Model.Offer;
import java.sql.*;
import java.util.*;

public class OfferS {

    public static List<Offer> getAllOffers() {
        List<Offer> offers = new ArrayList<>();
        String sql = "SELECT name, email, id, amount FROM offers";

        try (Connection conn = DB.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Offer offer = new Offer();
                offer.setName(rs.getString("name"));
                offer.setEmail(rs.getString("email"));
                offer.setId(rs.getString("id"));
                offer.setAmount(rs.getDouble("amount"));
                offers.add(offer);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return offers;
    }

    public static void addOffer(Offer offer) {
        String sql = "INSERT INTO offers (name, email, id, amount) VALUES (?, ?, ?, ?)";
        try (Connection conn = DB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, offer.getName());
            ps.setString(2, offer.getEmail());
            ps.setString(3, offer.getId());
            ps.setDouble(4, offer.getAmount());
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}