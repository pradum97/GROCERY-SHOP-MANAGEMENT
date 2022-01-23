package com.grocery.management.Model;

public class InvoiceBean {

    private int serial_Number ;
    private String  product_Name , quantity ;
    private double rate , amount,discount;
    private String customer_name , customer_phone, customer_address;



    public InvoiceBean(int serial_Number, String product_Name, String quantity, double rate, double amount,
                       double discount, String customer_name, String customer_phone, String customer_address) {
        super();
        this.serial_Number = serial_Number;
        this.product_Name = product_Name;
        this.quantity = quantity;
        this.rate = rate;
        this.amount = amount;
        this.discount = discount;
        this.customer_name = customer_name;
        this.customer_phone = customer_phone;
        this.customer_address = customer_address;
    }

    public String getCustomer_name() {
        return customer_name;
    }

    public void setCustomer_name(String customer_name) {
        this.customer_name = customer_name;
    }

    public String getCustomer_phone() {
        return customer_phone;
    }

    public void setCustomer_phone(String customer_phone) {
        this.customer_phone = customer_phone;
    }

    public String getCustomer_address() {
        return customer_address;
    }

    public void setCustomer_address(String customer_address) {
        this.customer_address = customer_address;
    }

    public int getSerial_Number() {
        return serial_Number;
    }

    public void setSerial_Number(int serial_Number) {
        this.serial_Number = serial_Number;
    }

    public String getProduct_Name() {
        return product_Name;
    }

    public void setProduct_Name(String product_Name) {
        this.product_Name = product_Name;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }
}