package com.grocery.management.Model;

public class Update_Quantity {

   private String unit;
   private Double quantity;
   private int product_id;

    public Update_Quantity(String unit, Double quantity, int product_id) {
        this.unit = unit;
        this.quantity = quantity;
        this.product_id = product_id;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public int getProduct_id() {
        return product_id;
    }

    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }
}
