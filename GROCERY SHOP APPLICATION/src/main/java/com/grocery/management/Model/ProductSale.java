package com.grocery.management.Model;

public class ProductSale {

    private int id, product_ID;
    private double productPrice,quantity;
    private String unit, productName;
    private double original_price;
    private String category;
    private String sub_category;
    private double discount;

    public ProductSale(int id, int product_ID, double productPrice, double quantity,
                       String unit, String productName, double original_price,
                       String category, String sub_category, double discount) {
        this.id = id;
        this.product_ID = product_ID;
        this.productPrice = productPrice;
        this.quantity = quantity;
        this.unit = unit;
        this.productName = productName;
        this.original_price = original_price;
        this.category = category;
        this.sub_category = sub_category;
        this.discount = discount;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
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

    public double getOriginal_price() {
        return original_price;
    }

    public void setOriginal_price(double original_price) {
        this.original_price = original_price;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProduct_ID() {
        return product_ID;
    }

    public void setProduct_ID(int product_ID) {
        this.product_ID = product_ID;
    }

    public double getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(double productPrice) {
        this.productPrice = productPrice;
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

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

}
