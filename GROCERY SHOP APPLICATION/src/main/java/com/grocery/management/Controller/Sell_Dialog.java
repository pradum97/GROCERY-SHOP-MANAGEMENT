package com.grocery.management.Controller;

import com.grocery.management.Dialog;
import com.grocery.management.Main;
import com.grocery.management.Method.Method;
import com.grocery.management.Model.ProductSale;
import com.grocery.management.Model.All_Product;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.ResourceBundle;

public class Sell_Dialog implements Initializable {

    public Label title_l;
    public Label price_l;
    public TextField quantity_tf;
    public ComboBox<String> unitType;
    public Label total_Quantity;
    public Label q_hint;
    public TextField discount_tf;
    private Method method;
    Dialog dialog;
    private Properties properties;

    double total_quantity_In_Cart;
    String cartUnit;

    ObservableList<String> unit_List = FXCollections.observableArrayList();

    All_Product models;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        method = new Method();
        dialog = new Dialog();
        properties = method.properties("query.properties");

        models = (All_Product) Main.primaryStage.getUserData();

        if (null != models) {

            title_l.setText(models.getTitle());
            price_l.setText(String.valueOf(models.getPrice()));
            total_Quantity.setText(models.getQuantity() + "-" + models.getUnit());

        }

        textFormatter();
        unitTypeCheck();
        getCartQuantity();

        unitType.valueProperty().addListener((observableValue, old, newValue) -> {
            q_hint.setText("( In " + newValue + " )");
        });

    }

    private void checkQuantityAvailability() {

        if (total_quantity_In_Cart < 0) {
            return;
        }

        String productUnit = models.getUnit();
        String cartunit = cartUnit;
        String myUnit = null;

        double productAvailableQuantity = models.getQuantity();
        double cartAvailableQuantity = total_quantity_In_Cart;

        // My Product List
        switch (cartunit) {

            case "GRAM", "KG" -> {

                if ("GRAM".equals(cartunit)) {
                    cartAvailableQuantity = cartAvailableQuantity / 1000;
                }

            }
            case "METER", "CM" -> {

                if ("CM".equals(cartunit)) {
                    cartAvailableQuantity = cartAvailableQuantity / 100;
                }


            }
            case "ML", "LITRE" -> {
                if ("ML".equals(cartunit)) {
                    cartAvailableQuantity = cartAvailableQuantity / 1000;
                }


            }

        }

        switch (productUnit) {

            case "GRAM", "KG" -> {

                if ("GRAM".equals(productUnit)) {
                    productAvailableQuantity = productAvailableQuantity / 1000;
                }
                myUnit = "KG";
            }
            case "METER", "CM" -> {

                if ("CM".equals(productUnit)) {
                    productAvailableQuantity = productAvailableQuantity / 100;
                }
                myUnit = "METER";
            }
            case "ML", "LITRE" -> {
                if ("ML".equals(productUnit)) {
                    productAvailableQuantity = productAvailableQuantity / 1000;
                }

                myUnit = "LITRE";
            }
            case "PACKET" -> myUnit = "PACKET";
        }


        double totalQuantity = productAvailableQuantity - cartAvailableQuantity;

        total_Quantity.setText(totalQuantity + " - " + myUnit);
    }

    private void textFormatter() {


        quantity_tf.setTextFormatter(new TextFormatter<>(new IntegerStringConverter()));
        quantity_tf.textProperty().addListener((obs, oldv, newv) -> {
            try {
                quantity_tf.getTextFormatter().getValueConverter().fromString(newv);
                // no exception above means valid
                quantity_tf.setBorder(null);
            } catch (NumberFormatException e) {
                method.show_popup("Enter Quantity in Number", quantity_tf);
                quantity_tf.setBorder(new Border(new BorderStroke(Color.RED,
                        BorderStrokeStyle.SOLID, new CornerRadii(3), new BorderWidths(2), new Insets(-2))));
            }
        });

    }

    private void unitTypeCheck() {

        String unit = models.getUnit();

        switch (unit) {
            case "GRAM", "KG" -> {
                unit_List.add("GRAM");
                unit_List.add("KG");
                unitType.setItems(unit_List);
                unitType.getSelectionModel().select(1);
                q_hint.setText("( In " + unitType.getValue() + " )");
            }
            case "METER", "CM" -> {
                unit_List.add("METER");
                unit_List.add("CM");
                unitType.setItems(unit_List);
                unitType.getSelectionModel().select(0);
                q_hint.setText("( In " + unitType.getValue() + " )");
            }
            case "ML", "LITRE" -> {
                unit_List.add("ML");
                unit_List.add("LITRE");
                unitType.setItems(unit_List);
                unitType.getSelectionModel().select(1);
                q_hint.setText("( In " + unitType.getValue() + " )");
            }
            case "PACKET" -> {
                unit_List.add("PACKET");
                unitType.setItems(unit_List);
                unitType.getSelectionModel().select(0);
                q_hint.setText("( In " + unitType.getValue() + " )");
            }
        }


    }

    private void getCartQuantity() {

        int product_id = models.getProduct_id();

        Connection connection = method.connection();
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {

            if (null == connection) {
                System.out.println("connection failed");
                return;
            }
            String query = "select * from cart where product_id = ?";
            ps = connection.prepareStatement(query);
            ps.setInt(1, product_id);

            rs = ps.executeQuery();

            while (rs.next()) {

                total_quantity_In_Cart = rs.getDouble("quantity");
                cartUnit = rs.getString("unit");

                checkQuantityAvailability();

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

    public void addToCart_bn(ActionEvent event) {

        if (null == models) {
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
        double total_Quantity = models.getQuantity();

        switch (input_unit) {
            case "GRAM", "KG" -> {
                gram_and_kg(input_unit, input_quantity, total_Quantity);
            }
            case "METER", "CM" -> {
                meter_and_cm(input_unit, input_quantity, total_Quantity);
            }
            case "ML", "LITRE" -> {
                ml_and_litre(input_unit, input_quantity, total_Quantity);
            }
            case "PACKET" -> packet(input_unit, input_quantity, total_Quantity);
        }

    }

    private void ml_and_litre(String input_unit, double input_quantity, double total_quantity) {

        double totalQuantityInMl = 0;
        double inputQuantityInMl = 0;

        switch (models.getUnit()) {
            case "ML" -> totalQuantityInMl = total_quantity;
            case "LITRE" -> totalQuantityInMl = total_quantity * 1000;
        }

        switch (input_unit) {
            case "ML" -> inputQuantityInMl = input_quantity;
            case "LITRE" -> inputQuantityInMl = input_quantity * 1000;
        }

        if (totalQuantityInMl <= inputQuantityInMl) {

            method.show_popup("Quantity not available please enter less then "
                    + models.getQuantity() + "-" + models.getUnit(), quantity_tf);
            return;
        }

        getCartQuantity();

        if (null != cartUnit) {

            if ("LITRE".equals(cartUnit)) {
                total_quantity_In_Cart = total_quantity_In_Cart * 1000;
            }

            double totalQuantity = inputQuantityInMl + total_quantity_In_Cart;

            if (totalQuantityInMl <= totalQuantity) {
                method.show_popup("Quantity not available please enter less then " +
                        String.format("%.2f", ((totalQuantityInMl - total_quantity_In_Cart) / 1000)) + "- LITRE", quantity_tf);
                total_quantity_In_Cart = 0;
                return;
            }
        }
        if (total_quantity_In_Cart < 0) {

            return;
        }

        addCart(input_quantity, input_unit);

    }

    private void packet(String input_unit, double input_quantity, double total_quantity) {


        if (total_quantity <= input_quantity) {

            method.show_popup("Quantity not available please enter less then "
                    + models.getQuantity() + "-" + models.getUnit(), quantity_tf);
            return;
        }

        getCartQuantity();

        if (null != cartUnit) {

            double totalQuantity = input_quantity + total_quantity_In_Cart;

            if (total_quantity <= totalQuantity) {
                method.show_popup("Quantity not available please enter less then " +
                        (total_quantity - total_quantity_In_Cart) + "- PACKET", quantity_tf);

                total_quantity_In_Cart = 0;
                return;
            }
        }
        if (total_quantity_In_Cart < 0) {

            return;
        }

        addCart(input_quantity, input_unit);

    }

    private void meter_and_cm(String input_unit, double input_quantity,
                              double total_quantity) {

        double totalQuantityInCm = 0;
        double inputQuantityInCm = 0;

        switch (models.getUnit()) {
            case "METER" -> totalQuantityInCm = total_quantity * 100;
            case "CM" -> totalQuantityInCm = total_quantity;
        }

        switch (input_unit) {
            case "METER" -> inputQuantityInCm = input_quantity * 100;
            case "CM" -> inputQuantityInCm = input_quantity;
        }

        if (totalQuantityInCm <= inputQuantityInCm) {

            method.show_popup("Quantity not available please enter less then " + models.getQuantity() + "-" + models.getUnit(), quantity_tf);
            return;
        }

        getCartQuantity();

        if (null != cartUnit) {

            if ("METER".equals(cartUnit)) {
                total_quantity_In_Cart = total_quantity_In_Cart * 100;
            }

            double totalQuantity = inputQuantityInCm + total_quantity_In_Cart;

            if (totalQuantityInCm <= totalQuantity) {
                method.show_popup("Quantity not available please enter less then " +
                        String.format("%.2f", ((totalQuantityInCm - total_quantity_In_Cart) / 100)) + "- METER", quantity_tf);
                total_quantity_In_Cart = 0;
                return;
            }
        }
        if (total_quantity_In_Cart < 0) {

            return;
        }


        addCart(input_quantity, input_unit);

    }

    public void gram_and_kg(String input_unit, double input_quantity,
                            double total_quantity) {

        double totalQuantityInGrams = 0;
        double inputQuantityInGrams = 0;

        switch (models.getUnit()) {
            case "GRAM" -> totalQuantityInGrams = total_quantity;
            case "KG" -> totalQuantityInGrams = total_quantity * 1000; // convert kg to grams
        }

        switch (input_unit) {
            case "GRAM" -> inputQuantityInGrams = input_quantity;
            case "KG" -> inputQuantityInGrams = input_quantity * 1000;
        }

        if (totalQuantityInGrams <= inputQuantityInGrams) {

            method.show_popup("Quantity not available please enter less then " + models.getQuantity()
                    + "-KG", quantity_tf);
            return;
        }

        getCartQuantity();

        if (null != cartUnit) {

            if ("KG".equals(cartUnit)) {
                total_quantity_In_Cart = total_quantity_In_Cart * 1000;
            }

            double totalQuantity = inputQuantityInGrams + total_quantity_In_Cart;

            if (totalQuantityInGrams <= totalQuantity) {
                method.show_popup("Quantity not available please enter less then " +
                        String.format("%.2f", ((totalQuantityInGrams - total_quantity_In_Cart) / 1000)) + "-" + models.getUnit(), quantity_tf);
                total_quantity_In_Cart = 0;
                return;
            }
        }
        if (total_quantity_In_Cart < 0) {

            return;
        }

        addCart(input_quantity, input_unit);

    }

    private void addCart(double input_quantity, String input_unit) {

        double discount = 0;
        try {
            discount = Double.parseDouble(discount_tf.getText());
        } catch (NumberFormatException e) {
            discount_tf.setText(String.valueOf(0));
            method.show_popup("Enter valid Discount",discount_tf);
            return;
        }

        System.out.println(discount);

        ProductSale sale = new ProductSale(0, models.getProduct_id(), models.getPrice(),
                input_quantity, input_unit
                , models.getTitle(), models.getPrice(), models.getCategory(), models.getSub_category(), discount);

        Main.primaryStage.setUserData(sale);
        Stage stage = Dialog.stage;
        if (stage.isShowing()) {
            stage.close();
        }
    }

}
