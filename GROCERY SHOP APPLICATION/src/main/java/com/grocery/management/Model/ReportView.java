package com.grocery.management.Model;

public class ReportView {

    private int sale_id;
    private String seller_name;
    private String customer_name, customer_address;
    private long customer_phone;
    private double product_price, productsQuantity;
    private String product_name, product_unit;
    private double original_rate, discount;
    private int product_id;
    private String date;
    private String category;
    private String sub_category;


    public ReportView(int sale_id, String seller_name, String customer_name, String customer_address,
                      long customer_phone, double product_price, double productsQuantity, String product_name,
                      String product_unit, double original_rate,
                      double discount, int product_id, String date, String category, String sub_category) {
        this.sale_id = sale_id;
        this.seller_name = seller_name;
        this.customer_name = customer_name;
        this.customer_address = customer_address;
        this.customer_phone = customer_phone;
        this.product_price = product_price;
        this.productsQuantity = productsQuantity;
        this.product_name = product_name;
        this.product_unit = product_unit;
        this.original_rate = original_rate;
        this.discount = discount;
        this.product_id = product_id;
        this.date = date;
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

    public String getSeller_name() {
        return seller_name;
    }

    public void setSeller_name(String seller_name) {
        this.seller_name = seller_name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getSale_id() {
        return sale_id;
    }

    public void setSale_id(int sale_id) {
        this.sale_id = sale_id;
    }

    public String getCustomer_name() {
        return customer_name;
    }

    public void setCustomer_name(String customer_name) {
        this.customer_name = customer_name;
    }

    public String getCustomer_address() {
        return customer_address;
    }

    public void setCustomer_address(String customer_address) {
        this.customer_address = customer_address;
    }

    public long getCustomer_phone() {
        return customer_phone;
    }

    public void setCustomer_phone(long customer_phone) {
        this.customer_phone = customer_phone;
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

    public int getProduct_id() {
        return product_id;
    }

    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }
}
