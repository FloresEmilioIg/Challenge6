package Store.Service;

import Store.Model.Offer;
import java.util.ArrayList;
import java.util.List;

public class OfferS {

    private static List<Offer> offers = new ArrayList<>();

    public static List<Offer> getAllOffers() {
        return offers;
    }

    public static void addOffer(Offer offer) {
        offers.add(offer);
    }
}
