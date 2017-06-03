package org.donorcalendar.web.dto;

import java.util.ArrayList;
import java.util.List;


public class ShoppingFacilities {
    private List<String> shoppingFacilities;

    public ShoppingFacilities() {
        shoppingFacilities = new ArrayList<>();
    }

    public List<String> getShoppingFacilities() {
        return shoppingFacilities;
    }

    public void setShoppingFacilities(List<String> shoppingFacilities) {
        this.shoppingFacilities = shoppingFacilities;
    }

    public void addShoppingFacility(String shoppingFacility){
        shoppingFacilities.add(shoppingFacility);
    }
}
