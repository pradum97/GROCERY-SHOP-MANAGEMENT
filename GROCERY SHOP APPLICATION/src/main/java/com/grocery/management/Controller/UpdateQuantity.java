package com.grocery.management.Controller;

import com.grocery.management.Dialog;
import com.grocery.management.Main;
import com.grocery.management.Method.Method;
import com.grocery.management.Model.ProductSale;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class UpdateQuantity implements Initializable {
    public Label title_l;
    public Label price_l;
    public Label total_Quantity_l;
    public Label q_hint;
    public TextField quantity_tf;
    public ComboBox<String> unitType;
    public TextField discount_tf;
    Method method;
    ProductSale model;
    ObservableList<String> unit_List = FXCollections.observableArrayList();

    double total_quantity;
    String unit;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        method = new Method();
        Stage stage = Main.primaryStage;
        model = (ProductSale) stage.getUserData();

        if (null == model) {
            return;
        }
        getCartProduct(model.getId());
        getAllProductQuantity();
    }

    private void unitTypeCheck() {

        String unit = model.getUnit();

        switch (unit) {
            case "GRAM", "KG" -> {
                unit_List.add("GRAM");
                unit_List.add("KG");
                unitType.setItems(unit_List);
                if (null != unitType.getValue() ){
                    q_hint.setText("( In " + unitType.getValue() + " )");
                }
            }
            case "METER", "CM" -> {
                unit_List.add("METER");
                unit_List.add("CM");
                unitType.setItems(unit_List);
                if (null != unitType.getValue() ){
                    q_hint.setText("( In " + unitType.getValue() + " )");
                }

            }
            case "ML", "LITRE" -> {
                unit_List.add("ML");
                unit_List.add("LITRE");
                unitType.setItems(unit_List);
                if (null != unitType.getValue() ){
                    q_hint.setText("( In " + unitType.getValue() + " )");
                }
            }
            case "PACKET" -> {
                unitType.setItems(unit_List);
                q_hint.setText("( In " + unitType.getValue() + " )");
                unitType.getSelectionModel().select(0);
                unit_List.add("PACKET");
            }
        }

        unitType.valueProperty().addListener((observableValue, s, newValue ) -> {

            if (null != unitType.getValue() ){
                q_hint.setText("( In " + unitType.getValue() + " )");
            }
        });

    }

    private void getAllProductQuantity() {

        int product_id = model.getProduct_ID();

        Connection connection = method.connection();
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {

            if (null == connection) {
                System.out.println("connection failed");
                return;
            }
            String query = "select * from all_products where product_id = ?";
            ps = connection.prepareStatement(query);
            ps.setInt(1, product_id);

            rs = ps.executeQuery();

            while (rs.next()) {

                total_quantity = rs.getDouble("quantity");
                unit = rs.getString("unit");
                total_Quantity_l.setText(total_quantity + "-" + unit);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {

            if (null != connection) {

                if (null != ps) {
                    try {
                        ps.close();
                        if (rs != null) {
                            rs.close();
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }

    private void getCartProduct(int id) {

        Connection connection = method.connection();
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {

            if (null == connection) {
                System.out.println("connection failed");
                return;
            }
            String query = "select * from cart where id = ?";
            ps = connection.prepareStatement(query);
            ps.setInt(1, id);

            rs = ps.executeQuery();

            while (rs.next()) {

                String p_name = rs.getString("name");
                double quantity = rs.getDouble("quantity");
                double price = rs.getDouble("price");
                double discount = rs.getDouble("discount");
                String unit = rs.getString("unit");

                title_l.setText(p_name);
                quantity_tf.setText(String.valueOf(quantity));
                price_l.setText(String.valueOf(price));

                discount_tf.setText(String.valueOf(discount));

                unitType.getItems().setAll(unit);
                unitType.getSelectionModel().select(0);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {

            if (null != connection) {

                if (null != ps) {
                    try {
                        ps.close();
                        if (rs != null) {
                            rs.close();
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        unitType.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

                if (null != unit_List){
                    unit_List.clear();
                }
                unitTypeCheck();

            }
        });

    }

    public void updateProduct_bn(ActionEvent event) {

        if (null == model) {
            return;
        }
        String input_Quantity = quantity_tf.getText();

        if (input_Quantity.isEmpty()) {
            method.show_popup("Enter Quantity", quantity_tf);
            return;
        } else if (null == unitType.getValue()) {
            method.show_popup("Select Unit ", unitType);
            return;
        } else if (Double.parseDouble(input_Quantity) <= 0) {
            method.show_popup("Enter valid input_quantity ", quantity_tf);
            return;
        }

        String input_unit = unitType.getValue();
        double input_quantity = Double.parseDouble(input_Quantity);


        switch (input_unit) {


            case "GRAM", "KG" -> {
               if ( gram_and_kg(input_unit, input_quantity, total_quantity)){
                   update();
               }
            }
            case "METER", "CM" -> {

                if (meter_and_cm(input_unit, input_quantity, total_quantity)){
                    update();
                }
            }
            case "ML", "LITRE" -> {

                if (ml_and_litre(input_unit, input_quantity, total_quantity)){
                    update();
                }

            }
            case "PACKET" -> {

                if (packet(input_unit, input_quantity, total_quantity)){
                    update();
                }
            }
        }

    }

    private boolean ml_and_litre(String input_unit, double input_quantity, double total_quantity) {

        double totalQuantityInMl = 0;
        double inputQuantityInMl = 0;

        switch (model.getUnit()) {
            case "ML" -> totalQuantityInMl = total_quantity;
            case "LITRE" -> totalQuantityInMl = total_quantity * 1000;
        }

        switch (input_unit) {
            case "ML" -> inputQuantityInMl = input_quantity;
            case "LITRE" -> inputQuantityInMl = input_quantity * 1000;
        }

        if (totalQuantityInMl <= inputQuantityInMl) {

            method.show_popup("Quantity not available please enter less then "+
                    total_quantity+"-"+unit, quantity_tf);
            return false;
        }

        return true;
    }

    private boolean packet(String input_unit, double input_quantity, double total_quantity) {


        if (total_quantity <= input_quantity) {

            method.show_popup("Quantity not available please enter less then "
                    +  total_quantity+"-"+unit, quantity_tf);
            return false;
        }

        return  true;
    }

    private boolean meter_and_cm(String input_unit, double input_quantity,
                              double total_quantity) {

        double totalQuantityInCm = 0;
        double inputQuantityInCm = 0;

        switch (model.getUnit()) {
            case "METER" -> totalQuantityInCm = total_quantity *100;
            case "CM" -> totalQuantityInCm = total_quantity;
        }

        switch (input_unit) {
            case "METER" -> inputQuantityInCm = input_quantity * 100;
            case "CM" -> inputQuantityInCm = input_quantity;
        }

        if (totalQuantityInCm <= inputQuantityInCm) {

            method.show_popup("Quantity not available please enter less then " +
                    total_quantity+"-"+unit, quantity_tf);
            return false;
        }

        return true;
    }

    public boolean gram_and_kg(String input_unit, double input_quantity,
                            double total_quantity) {

        double totalQuantityInGrams = 0;
        double inputQuantityInGrams = 0;

        switch (unit) {
            case "GRAM" -> totalQuantityInGrams = total_quantity;
            case "KG" -> totalQuantityInGrams = total_quantity * 1000;

        }

        switch (input_unit) {
            case "GRAM" -> inputQuantityInGrams = input_quantity;
            case "KG" -> inputQuantityInGrams = input_quantity * 1000;
        }

        if (totalQuantityInGrams <= inputQuantityInGrams) {

            method.show_popup("Quantity not available please enter less then " +
                    total_quantity+"-"+unit, quantity_tf);
            return false;
        }

        return true;
    }

    private void update() {

        Connection connection = method.connection();
        PreparedStatement ps = null;

        double quantity = Double.parseDouble(quantity_tf.getText());
        String unit = unitType.getValue();
        double discount;

        try {
             discount = Double.parseDouble(discount_tf.getText());
        } catch (NumberFormatException e) {
            discount_tf.setText(String.valueOf(0));
            method.show_popup("Enter valid Discount",discount_tf);
            return;
        }

        try {

                double finalPrice = 0;
                double price = model.getOriginal_price();

                switch (unit) {
                    case "GRAM", "KG" -> {

                        if ("GRAM".equals(unit)) {
                            quantity = quantity / 1000;
                        }

                        finalPrice = finalPrice + (quantity * price);

                    }
                    case "METER", "CM" -> {

                        if ("CM".equals(unit)) {
                            quantity = quantity / 100;
                        }

                        finalPrice = finalPrice + (quantity * price);

                    }
                    case "ML", "LITRE" -> {

                        if ("ML".equals(unit)) {
                            quantity = quantity / 1000;
                        }

                        finalPrice = finalPrice + (quantity * price);
                    }
                    case "PACKET" -> {

                        finalPrice = finalPrice + (quantity * price);
                    }

                }

            String query = "UPDATE cart SET QUANTITY =? , UNIT =? , PRICE =? , DISCOUNT = ? WHERE ID = ?";

            ps = connection.prepareStatement(query);
            ps.setDouble(1,quantity);
            ps.setString(2,unit);
            ps.setDouble(3,finalPrice);
            ps.setDouble(4,discount);
            ps.setInt(5,model.getId());

            int res = ps.executeUpdate();
            if (res>0){
                Stage stage =  Dialog.stage;

                if (stage.isShowing()){
                    stage.close();
                }
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }finally {

            if (null != connection){
                try {
                    connection.close();
                    if (ps != null) {
                        ps.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

    }

}
