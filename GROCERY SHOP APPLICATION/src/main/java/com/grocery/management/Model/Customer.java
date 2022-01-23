package com.grocery.management.Model;

public class Customer {

    private String customer_name, customer_address;
    private long customer_phone;


    public Customer(String customer_name, String customer_address, long customer_phone) {
        this.customer_name = customer_name;
        this.customer_address = customer_address;
        this.customer_phone = customer_phone;
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
}


