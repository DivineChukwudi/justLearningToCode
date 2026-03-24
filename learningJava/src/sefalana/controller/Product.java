package sefalana.controller;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Product {

    private final StringProperty productName;
    private final StringProperty category;
    private final DoubleProperty price;

    public Product(String productName, String category, double price) {
        this.productName = new SimpleStringProperty(productName);
        this.category    = new SimpleStringProperty(category);
        this.price       = new SimpleDoubleProperty(price);
    }

    // productName
    public String getProductName()              { return productName.get(); }
    public void setProductName(String v)        { productName.set(v); }
    public StringProperty productNameProperty() { return productName; }

    // category
    public String getCategory()                 { return category.get(); }
    public void setCategory(String v)           { category.set(v); }
    public StringProperty categoryProperty()    { return category; }

    // price
    public double getPrice()                    { return price.get(); }
    public void setPrice(double v)              { price.set(v); }
    public DoubleProperty priceProperty()       { return price; }
}