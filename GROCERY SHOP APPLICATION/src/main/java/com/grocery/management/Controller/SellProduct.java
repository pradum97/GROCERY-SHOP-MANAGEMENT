package com.grocery.management.Controller;

import com.grocery.management.Dialog;
import com.grocery.management.Login;
import com.grocery.management.Main;
import com.grocery.management.Method.GenerateReport;
import com.grocery.management.Method.Method;
import com.grocery.management.Model.*;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.converter.IntegerStringConverter;

import java.io.*;
import java.net.URL;
import java.sql.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class SellProduct implements Initializable {

    public TableView<All_Product> product_table_view;
    public TableColumn<All_Product, String> col_id;
    public TableColumn<All_Product, String> col_title;
    public TableColumn<All_Product, String> col_price;
    public TableColumn<All_Product, String> col_quantity;
    public TableColumn<All_Product, String> col_action;
    public TableColumn<All_Product, String> col_subcategory;
    public HBox refresh_bn;
    public ImageView refresh_img;
    public TextField product_find_tf;
    public TableColumn<ProductSale, String> p_id;
    public TableColumn<ProductSale, String> p_name;
    public TableColumn<ProductSale, String> p_price;
    public TableColumn<ProductSale, String> p_quantity;
    public TableColumn<ProductSale, String> p_unit;
    public TableColumn<ProductSale, String> p_action;
    public TableView<ProductSale> cart_table_view;
    public TableColumn<String, String> c_id;
    public TableColumn<String, String> col_unit;
    public TableColumn<String, String> p_rate;
    public Label payableAmount;
    public Label discount_l;
    public Label totalAmount;
    public TableColumn<String, String> p_discount;
    private double discountPrice;



    Collection<InvoiceBean> invoiceBeans = new ArrayList<>();
    int count = 0;

    Method method;
    Dialog dialog;
    Properties properties;
    ObservableList<All_Product> productList = FXCollections.observableArrayList();
    ObservableList<ProductSale> cartList = FXCollections.observableArrayList();

    ObservableList<Update_Quantity> updateQuantity = FXCollections.observableArrayList();

    ObservableList<SaleReport> salesReport = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        method = new Method();
        dialog = new Dialog();
        properties = method.properties("query.properties");

        getProducts();
        load_ProductList_Data();

        tableSetting();
        setImage();
        search_Item();

    }

    private double findPercentage(double discount, double price) {

        return (discount * price) / 100;

    }

    private void tableSetting() {

        product_table_view.setFixedCellSize(60);
        product_table_view.prefHeightProperty().bind(Bindings.size(
                product_table_view.getItems()).multiply(product_table_view.getFixedCellSize()).add(30));
    }

    private void search_Item() {

        FilteredList<All_Product> filteredData = new FilteredList<>(productList, p -> true);

        product_find_tf.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(person -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();

                if (person.getTitle().toLowerCase().contains(lowerCaseFilter)) {
                    return true;

                } else if (person.getSub_category().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                return false; // Does not match.
            });
        });

        SortedList<All_Product> sortedData = new SortedList<>(filteredData);

        sortedData.comparatorProperty().bind(product_table_view.comparatorProperty());

        product_table_view.setItems(sortedData);
        tableSetting();

    }

    private void setImage() {

        File file = new File("src/main/resources/com/grocery/management/drawable/Icon/refresh_ic.png");
        try {
            InputStream is = new FileInputStream(file.getAbsolutePath());
            Image image = new Image(is);
            refresh_img.setImage(image);
            refresh_img.setFitHeight(15);
            refresh_img.setFitWidth(15);
            refresh_img.setPreserveRatio(true);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getProducts() {

        Connection connection = method.connection();
        PreparedStatement ps = null;
        ResultSet resultSet = null;

        try {

            if (null == connection) {
                System.out.println("connection failed");
                return;
            }

            ps = connection.prepareStatement(properties.getProperty("getAll_Product"));

            resultSet = ps.executeQuery();

            while (resultSet.next()) {

                int id = resultSet.getInt("product_id");
                String p_Image = resultSet.getString("product_image");
                double price = resultSet.getDouble("product_price");
                String title = resultSet.getString("product_title");
                String description = resultSet.getString("product_description");
                String category = resultSet.getString("product_categories");
                String subCategory = resultSet.getString("sub_category");
                String unit = resultSet.getString("unit");
                String category_id = resultSet.getString("categories_id");
                String user_id = resultSet.getString("user_id");

                double quantity = 0;
                try {
                    quantity = Double.parseDouble(String.format("%.2f", resultSet.getDouble("quantity")));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

                productList.add(new
                        All_Product(id, p_Image, price, quantity, unit, title, description,
                        category, subCategory, category_id, user_id));

                product_table_view.setItems(productList);


            }

        } catch (SQLException e) {

            System.out.println(e.getMessage());
            e.getStackTrace();

        } finally {

            if (null != connection) {

                try {

                    connection.close();
                    if (ps != null) {
                        ps.close();
                    }
                    if (resultSet != null) {
                        resultSet.close();
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private void load_ProductList_Data() {

        col_id.setCellValueFactory(new PropertyValueFactory<>("product_id"));
        col_price.setCellValueFactory(new PropertyValueFactory<>("price"));
        col_title.setCellValueFactory(new PropertyValueFactory<>("title"));
        col_subcategory.setCellValueFactory(new PropertyValueFactory<>("sub_category"));
        col_quantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        col_unit.setCellValueFactory(new PropertyValueFactory<>("unit"));

        Callback<TableColumn<All_Product, String>, TableCell<All_Product, String>>
                cellFoctory = (TableColumn<All_Product, String> param) -> {


            final TableCell<All_Product, String> cell = new TableCell<>() {
                @Override
                public void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {

                        setGraphic(null);
                        setText(null);

                    } else {

                        All_Product model = product_table_view.getSelectionModel().getSelectedItem();

                        Label sell_bn = new Label();
                        sell_bn.setAlignment(Pos.CENTER);
                        sell_bn.setText("ADD ➡️");
                        sell_bn.setOnMouseClicked(new EventHandler<MouseEvent>() {
                            @Override
                            public void handle(MouseEvent event) {
                                All_Product models = product_table_view.getSelectionModel().getSelectedItem();
                                Stage stage = Main.primaryStage;

                                stage.setUserData(models);

                                dialog.show_fxml_dialog("Dashboard/sell_dialog.fxml", "ADD TO CART");

                                ProductSale productSale = null;
                                try {
                                    productSale = (ProductSale) stage.getUserData();
                                } catch (ClassCastException e) {
                                    //TODO
                                }
                                check_if_exists(productSale);

                            }
                        });
                        sell_bn.setStyle(
                                "-fx-border-radius: 3;-fx-background-radius: 3;" +
                                        "-fx-padding: 5px;-fx-background-color: #670404;" +
                                        " -fx-text-fill: white;-fx-cursor: hand ;"
                                        + "-glyph-size:28px;"
                                        + "-fx-fill:#c506fa;"
                        );

                        HBox managebtn = new HBox(sell_bn);
                        managebtn.setStyle("-fx-alignment:center");
                        managebtn.setAlignment(Pos.CENTER);
                        HBox.setMargin(sell_bn, new Insets(2, 3, 0, 10));
                        setGraphic(managebtn);
                        setText(null);
                        setImage();
                    }
                }

            };
            return cell;


        };
        col_action.setCellFactory(cellFoctory);
        product_table_view.setItems(productList);
        loadCartData();

        col_title.setCellFactory(tc -> {
            TableCell<All_Product, String> cell = new TableCell<>();
            Text text = new Text();
            cell.setGraphic(text);
            text.setStyle("-fx-text-alignment: CENTER");

            cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
            text.wrappingWidthProperty().bind(col_title.widthProperty());
            text.textProperty().bind(cell.itemProperty());
            return cell;
        });

    }

    public void refresh_bn(MouseEvent event) {

        refresh();
        product_find_tf.setText("");
    }

    public void refresh() {

        if (null != productList) {
            productList.clear();
        }
        getProducts();
        load_ProductList_Data();
        search_Item();
    }

    // cart
    private void check_if_exists(ProductSale sale) {

        if (null == sale) {
            return;
        }
        Connection connection = method.connection();
        PreparedStatement ps = null, ps_update = null;

        try {

            if (null == connection) {
                System.out.println("connection failed");
                return;
            }

            String query = "SELECT * FROM cart WHERE product_id =?";
            ps = connection.prepareStatement(query);
            ps.setInt(1, sale.getProduct_ID());

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String old_unit = rs.getString("unit");
                double old_discount = rs.getDouble("discount");
                String new_unit = sale.getUnit();

                String myUnit = null;

                double old_quantity = rs.getDouble("quantity");
                double new_quantity = sale.getQuantity();
                double totalQuantity = 0;

                switch (old_unit) {
                    case "KG" -> old_quantity = old_quantity * 1000;
                    case "LITRE" -> old_quantity = old_quantity * 1000;
                    case "METER" -> old_quantity = old_quantity * 100;
                }
                switch (new_unit) {
                    case "GRAM" -> {
                        totalQuantity = (old_quantity + new_quantity) / 1000;
                        myUnit = "KG";
                    }
                    case "KG" -> {
                        new_quantity = new_quantity * 1000;
                        totalQuantity = (old_quantity + new_quantity) / 1000;
                        myUnit = "KG";
                    }
                    case "ML" -> {
                        totalQuantity = (old_quantity + new_quantity) / 1000;
                        myUnit = "LITRE";
                    }
                    case "LITRE" -> {
                        new_quantity = new_quantity * 1000;
                        totalQuantity = (old_quantity + new_quantity) / 1000;
                        myUnit = "LITRE";
                    }
                    case "METER" -> {
                        new_quantity = new_quantity * 100;
                        totalQuantity = (old_quantity + new_quantity) / 100;
                        myUnit = "METER";
                    }
                    case "CM" -> {
                        totalQuantity = (old_quantity + new_quantity) / 100;
                        myUnit = "METER";
                    }
                    case "PACKET" -> {
                        totalQuantity = (old_quantity + new_quantity);
                        myUnit = "PACKET";
                    }
                }

                double totalPrice = totalQuantity * sale.getProductPrice();
                double totalDiscount = sale.getDiscount() + old_discount;


                String update_quantity = "UPDATE cart SET QUANTITY =?,UNIT =?,PRICE =? , DISCOUNT = ? where PRODUCT_ID =?";
                ps_update = connection.prepareStatement(update_quantity);
                ps_update.setDouble(1, totalQuantity);
                ps_update.setString(2, myUnit);
                ps_update.setDouble(3, totalPrice);
                ps_update.setDouble(4, totalDiscount);
                ps_update.setInt(5, sale.getProduct_ID());

                int resultSet = ps_update.executeUpdate();
                if (resultSet > 0) {
                    refresh_cart();
                    ps_update.close();
                }


            } else {
                add_To_cart(sale);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (null != connection) {
                try {
                    if (ps != null) {
                        ps.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void add_To_cart(ProductSale sale) {

        if (null == sale) {
            return;
        }

        Connection connection = method.connection();
        PreparedStatement ps = null;

        try {

            if (null == connection) {
                System.out.println("connection failed");
                return;
            }

            double finalPrice = 0;

            String unit = sale.getUnit();
            double quantity = sale.getQuantity();
            double price = sale.getProductPrice();

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

            String query = properties.getProperty("INSERT_INTO_CART");
            ps = connection.prepareStatement(query);
            ps.setInt(1, sale.getProduct_ID());
            ps.setDouble(2, finalPrice);
            ps.setDouble(3, sale.getQuantity());
            ps.setString(4, sale.getUnit());
            ps.setString(5, sale.getProductName());
            ps.setDouble(6, sale.getOriginal_price());
            ps.setString(7, sale.getCategory());
            ps.setString(8, sale.getSub_category());
            ps.setDouble(9, sale.getDiscount());

            int result = ps.executeUpdate();

            if (result > 0) {
                refresh_cart();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (null != connection) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void loadCartData() {
        AtomicInteger cartItemCount = new AtomicInteger();

        if (null != cartList) {
            cartList.clear();
        }

        Connection connection = method.connection();

        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            if (null == connection) {
                System.out.println("connection failed");
                return;
            }

            String query = properties.getProperty("FETCH_CART_DATA");
            ps = connection.prepareStatement(query);
            rs = ps.executeQuery();

            while (rs.next()) {

                int id = rs.getInt("id");
                int product_id = rs.getInt("product_id");
                double originalPrice = rs.getDouble("RATE");
                double price = rs.getDouble("price");
                String unit = rs.getString("unit");
                String product_name = rs.getString("name");
                String category = rs.getString("category");
                String sub_category = rs.getString("sub_category");

                double quantity = 0;
                try {
                    quantity = Double.parseDouble(String.format("%.2f", rs.getDouble("quantity")));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

                double discount = 0;
                try {
                    discount = Double.parseDouble(String.format("%.2f", rs.getDouble("discount")));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

                ProductSale productSale = new ProductSale(id, product_id, price, quantity, unit,
                        product_name, originalPrice, category, sub_category, discount);

                cartList.add(productSale);

                c_id.setCellFactory(tc -> {
                    TableCell<String, String> cell = new TableCell<>();
                    Text text = new Text();
                    cell.setGraphic(text);
                    text.setStyle("-fx-text-alignment: CENTER");

                    cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
                    text.wrappingWidthProperty().bind(col_title.widthProperty());
                    text.setText(String.valueOf(cartItemCount.getAndIncrement()));
                    return cell;
                });
            }

            cart_table_view.setItems(cartList);


        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (null != connection) {
                try {
                    connection.close();
                    if (ps != null) {
                        ps.close();
                    }
                    if (rs != null) {
                        rs.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        p_id.setCellValueFactory(new PropertyValueFactory<>("product_ID"));
        p_name.setCellValueFactory(new PropertyValueFactory<>("productName"));
        p_price.setCellValueFactory(new PropertyValueFactory<>("productPrice"));
        p_quantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        p_unit.setCellValueFactory(new PropertyValueFactory<>("unit"));
        p_rate.setCellValueFactory(new PropertyValueFactory<>("original_price"));
        p_discount.setCellValueFactory(new PropertyValueFactory<>("discount"));

        Callback<TableColumn<ProductSale, String>, TableCell<ProductSale, String>>
                cellFoctory = (TableColumn<ProductSale, String> param) -> {
            final TableCell<ProductSale, String> cell = new TableCell<ProductSale, String>() {
                @Override
                public void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                        setText(null);

                    } else {

                        FileInputStream input_edit, input_delete;
                        File edit_file, delete_file;
                        ImageView iv_edit, iv_delete;
                        Image image_edit = null, image_delete = null;
                        try {
                            edit_file = new File("src/main/resources/com/grocery/management/drawable/Icon/edit_ic.png");
                            delete_file = new File("src/main/resources/com/grocery/management/drawable/Icon/delete_ic.png");
                            input_edit = new FileInputStream(edit_file.getPath());
                            input_delete = new FileInputStream(delete_file.getPath());

                            image_edit = new Image(input_edit);
                            image_delete = new Image(input_delete);


                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }

                        iv_edit = new ImageView(image_edit);
                        iv_edit.setFitHeight(25);
                        iv_edit.setFitHeight(25);
                        iv_edit.setPreserveRatio(true);

                        iv_delete = new ImageView(image_delete);
                        iv_delete.setFitHeight(20);
                        iv_delete.setFitWidth(20);
                        iv_delete.setPreserveRatio(true);


                        iv_edit.setStyle(
                                " -fx-cursor: hand ;"
                                        + "-glyph-size:28px;"
                                        + "-fx-fill:#c506fa;"
                        );

                        iv_delete.setStyle(
                                " -fx-cursor: hand ;"
                                        + "-glyph-size:28px;"
                                        + "-fx-fill:#ff0000;"
                        );

                        iv_edit.setOnMouseClicked((MouseEvent event) -> {
                            ProductSale edit_selection = cart_table_view.getSelectionModel().getSelectedItem();

                            Stage stage = Main.primaryStage;
                            stage.setUserData(edit_selection);
                            dialog.show_fxml_dialog("Dashboard/quantity_edit.fxml", "UPDATE QUANTITY");
                            refresh_cart();
                        });

                        iv_delete.setOnMouseClicked((MouseEvent event) -> {

                            ProductSale model = cart_table_view.getSelectionModel().getSelectedItem();

                            Alert alert = new Alert(Alert.AlertType.NONE);
                            alert.setAlertType(Alert.AlertType.CONFIRMATION);
                            alert.setHeaderText("Are you sure you want to delete this item?");
                            alert.initModality(Modality.APPLICATION_MODAL);
                            alert.initOwner(Main.primaryStage);
                            Optional<ButtonType> result = alert.showAndWait();
                            ButtonType button = result.orElse(ButtonType.CANCEL);


                            if (button == ButtonType.OK) {
                                delete_cart_products(model.getId());
                                refresh_cart();
                            } else {
                                alert.close();
                            }
                        });

                        HBox managebtn = new HBox(iv_edit, iv_delete);
                        managebtn.setStyle("-fx-alignment:center");
                        HBox.setMargin(iv_edit, new Insets(2, 2, 0, 10));
                        HBox.setMargin(iv_delete, new Insets(2, 3, 0, 30));

                        setGraphic(managebtn);

                        setText(null);

                    }
                }

            };

            return cell;
        };
        p_action.setCellFactory(cellFoctory);
        cart_table_view.setItems(cartList);

        countPrice();

    }

    public void clear(ActionEvent event) {

        Connection connection = method.connection();
        PreparedStatement ps = null;

        try {
            if (null == connection) {
                System.out.println("connection failed");
                return;
            }
            String query = properties.getProperty("CLEAR_CART");

            ps = connection.prepareStatement(query);

            int result = ps.executeUpdate();

            if (result > 0) {
                if (null != cartList) {
                    cartList.clear();
                    loadCartData();
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (null != connection) {
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

    private void countPrice() {
        discountPrice = 0;

        double payablePrice = 0;

        for (ProductSale list : cartList) {

            String unit = list.getUnit();

            double price = list.getProductPrice();

            switch (unit) {
                case "GRAM", "KG", "METER", "CM", "ML",
                        "LITRE", "PACKET" -> payablePrice = payablePrice + price;
            }

            discountPrice = discountPrice + findPercentage(list.getDiscount(), list.getProductPrice());
        }


        totalAmount.setText(String.valueOf(payablePrice));

        discount_l.setText(String.valueOf(discountPrice));
        payableAmount.setText(payablePrice + " Rs");

        amountRefresh(payablePrice);
    }

    private void amountRefresh(double payablePrice) {

        totalAmount.setText(String.valueOf(payablePrice));
        payablePrice = payablePrice - discountPrice;
        //discount_l.setText(" ( " + discount_tf.getText() + "% ) -" + String.format("%.2f", discountPrice));
        payableAmount.setText(String.format("%.2f", payablePrice) + " Rs");

    }

    public void sellNow(Customer customer) {

        if (cartList.isEmpty()) {
            return;
        }

        Connection connection = method.connection();
        PreparedStatement ps = null;
        ResultSet rs = null;

        if (null == connection) {
            System.out.println("connection failed");
            return;
        }
        double payablePrice = 0;
        for (ProductSale list : cartList) {

            String unit = list.getUnit();
            double price = list.getProductPrice();

            switch (unit) {
                case "GRAM", "KG", "METER", "CM", "ML", "LITRE", "PACKET" -> payablePrice = payablePrice + price;
            }

            try {

                String query = "SELECT * FROM all_products WHERE product_id =?";
                ps = connection.prepareStatement(query);
                ps.setInt(1, list.getProduct_ID());

                rs = ps.executeQuery();

                if (rs.next()) {

                    String myUnit = null;
                    double old_quantity = rs.getDouble("quantity");
                    double new_quantity = list.getQuantity();

                    String oldUnit = rs.getString("unit");
                    String newUnit = list.getUnit();

                    switch (oldUnit) {
                        case "GRAM" -> {
                            old_quantity = old_quantity / 1000;
                        }

                        case "ML" -> {
                            old_quantity = old_quantity / 1000;
                        }
                        case "CM" -> {
                            old_quantity = old_quantity / 100;
                        }
                    }
                    switch (newUnit) {

                        case "GRAM" -> {
                            new_quantity = new_quantity / 1000;
                            myUnit = "KG";
                        }
                        case "ML" -> {

                            new_quantity = new_quantity / 1000;
                            myUnit = "LITER";
                        }

                        case "CM" -> {
                            new_quantity = new_quantity / 100;
                            myUnit = "METER";
                        }

                        case "KG" -> myUnit = "KG";
                        case "LITRE" -> myUnit = "LITRE";
                        case "METER" -> myUnit = "METER";
                        case "PACKET" -> myUnit = "PACKET";
                    }

                    double totalQuantity = old_quantity - new_quantity;
                    updateQuantity.add(new Update_Quantity(myUnit, totalQuantity, list.getProduct_ID()));


                    salesReport.add(new SaleReport(customer, list.getProductPrice(), list.getQuantity(),
                            list.getProductName(), list.getUnit(), list.getOriginal_price(),
                            list.getDiscount(), list.getProduct_ID(), list.getCategory(), list.getSub_category()));
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        updateQuantity(connection, ps);
    }

    private void refresh_cart() {

        if (null != cartList) {
            cartList.clear();
        }
        loadCartData();

    }

    private void delete_cart_products(int product_id) {

        Connection connection = method.connection();
        PreparedStatement ps = null;

        if (null == connection) {
            System.out.println("connection Failed");
            return;
        }
        try {
            ps = connection.prepareStatement(properties.getProperty("DELETE_CART_PRODUCT"));
            ps.setInt(1, product_id);

            int result = ps.executeUpdate();

            if (result > 0) {
                refresh_cart();
            }

        } catch (SQLException e) {
            dialog.show_alertBox("", e.getMessage());
            e.printStackTrace();
        } finally {

            if (null != connection) {

                try {
                    connection.close();
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void sellNow_bn(ActionEvent event) {

        dialog.show_fxml_dialog("Dashboard/customerDetails.fxml", "Customer Details");
        Customer customer = null;
        try {
            customer = (Customer) Main.primaryStage.getUserData();
        } catch (ClassCastException e) {
            //TODO
        }

        if (null == customer) {
            return;
        }

        sellNow(customer);
    }

    private void updateQuantity(Connection connection, PreparedStatement ps) {

        if (null == connection) {
            System.out.println("connection failed");
            return;
        }
        for (Update_Quantity up : updateQuantity) {

            try {
                String update_quantity = "UPDATE all_products set QUANTITY = ? , UNIT = ? WHERE PRODUCT_ID = ?";
                ps = connection.prepareStatement(update_quantity);

                ps.setDouble(1, up.getQuantity());
                ps.setString(2, up.getUnit());
                ps.setInt(3, up.getProduct_id());
                long result = ps.executeUpdate();


            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        createSaleReport(connection, ps);

    }

    private void createSaleReport(Connection connection, PreparedStatement ps) {

        if (null == salesReport) {
            return;
        }

        int seller_id = Login.login_id;

        for (SaleReport sale : salesReport) {

            try {

                String query = "INSERT INTO sales_report (CUSTOMER_NAME, CUSTOMERPHONE, CUSTOMER_ADDRESS, PRODUCT_NAME,\n" +
                        "                          RATE, PRODUCT_PRICE, PRODUCTSQUANTITY, PRODUCT_UNIT,\n" +
                        "                          DISCOUNT, SELLER_ID,PRODUCT_ID,CATEGORY,SUB_CATEGORY) values (?,?,?,?,?,?,?,?,?,?,?,?,?)";

                ps = connection.prepareStatement(query);
                ps.setString(1, sale.getCustomer().getCustomer_name());
                ps.setLong(2, sale.getCustomer().getCustomer_phone());
                ps.setString(3, sale.getCustomer().getCustomer_address());
                ps.setString(4, sale.getProduct_name());
                ps.setDouble(5, sale.getOriginal_rate());
                ps.setDouble(6, sale.getProduct_price());
                ps.setDouble(7, sale.getProductsQuantity());
                ps.setString(8, sale.getProduct_unit());
                ps.setDouble(9, sale.getDiscount());
                ps.setInt(10, seller_id);
                ps.setInt(11, sale.getProduct_id());
                ps.setString(12, sale.getCategory());
                ps.setString(13, sale.getSub_category());

                int res = ps.executeUpdate();
                if (res > 0) {
                    String quantity = sale.getProductsQuantity() + "-" + sale.getProduct_unit();

                    refresh();
                    refresh_cart();
                    clear(null);
                    count++;

                    invoiceBeans.add(new InvoiceBean(count, sale.getProduct_name(), quantity, sale.getOriginal_rate(), sale.getProduct_price(), sale.getDiscount(), sale.getCustomer().getCustomer_name(),
                            String.valueOf(sale.getCustomer().getCustomer_phone()), sale.getCustomer().getCustomer_address()));

                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }


        new GenerateReport().generate(invoiceBeans);

        count = 0;
        if (null != salesReport) {
            salesReport.clear();

        }
        if (null != invoiceBeans) {
            invoiceBeans.clear();
        }
    }
}
