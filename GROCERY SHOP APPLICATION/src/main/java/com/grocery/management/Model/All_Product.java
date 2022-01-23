package com.grocery.management.Model;

import java.sql.Blob;

public class All_Product {

    private int product_id;
    private String product_image;
    private double price,quantity;
    private String unit, title, description, category,sub_category,
             category_id, user_id;

    public All_Product() {}

    public All_Product(int product_id, String product_image, double price, double quantity, String unit, String title, String description,
                       String category, String sub_category, String category_id, String user_id) {
        this.product_id = product_id;
        this.product_image = product_image;
        this.price = price;
        this.quantity = quantity;
        this.unit = unit;
        this.title = title;
        this.description = description;
        this.category = category;
        this.sub_category = sub_category;
        this.category_id = category_id;
        this.user_id = user_id;
    }


    public int getProduct_id() {
        return product_id;
    }

    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }

    public String getProduct_image() {
        return product_image;
    }

    public void setProduct_image(String product_image) {
        this.product_image = product_image;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSub_category() {
        return sub_category;
    }

    public void setSub_category(String sub_category) {
        this.sub_category = sub_category;
    }
    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}
