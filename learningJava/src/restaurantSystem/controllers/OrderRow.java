package restaurantSystem.controllers;


public class OrderRow {

    private final String item;
    private final String category;
    private final String price;

    public OrderRow(String item, String category, String price) {
        this.item     = item;
        this.category = category;
        this.price    = price;
    }

    public String getItem()     { return item; }
    public String getCategory() { return category; }
    public String getPrice()    { return price; }
}
