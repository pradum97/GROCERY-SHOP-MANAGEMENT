package com.grocery.management.Model;

public class SaleReport {

    private Customer customer;
    private double product_price, productsQuantity;
    private String product_name, product_unit;
    private double original_rate, discount;
    int product_id;
    private String category;
    private String sub_category;


    public SaleReport(Customer customer, double product_price, double productsQuantity,
                      String product_name, String product_unit,
                      double original_rate, double discount, int product_id,
                      String category, String sub_category) {

        this.customer = customer;
        this.product_price = product_price;
        this.productsQuantity = productsQuantity;
        this.product_name = product_name;
        this.product_unit = product_unit;
        this.original_rate = original_rate;
        this.discount = discount;
        this.product_id = product_id;
        this.category = category;
        this.sub_category = sub_category;
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

    public int getProduct_id() {
        return product_id;
    }

    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }

    public double getOriginal_rate() {
        return original_rate;
    }

    public void setOriginal_rate(double original_rate) {
        this.original_rate = original_rate;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public double getProduct_price() {
        return product_price;
    }

    public void setProduct_price(double product_price) {
        this.product_price = product_price;
    }

    public double getProductsQuantity() {
        return productsQuantity;
    }

    public void setProductsQuantity(double productsQuantity) {
        this.productsQuantity = productsQuantity;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getProduct_unit() {
        return product_unit;
    }

    public void setProduct_unit(String product_unit) {
        this.product_unit = product_unit;
    }
}

