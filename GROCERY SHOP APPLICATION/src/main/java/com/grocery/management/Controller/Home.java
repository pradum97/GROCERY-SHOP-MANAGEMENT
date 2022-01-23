package com.grocery.management.Controller;

import com.grocery.management.Dialog;
import com.grocery.management.Method.Method;
import com.grocery.management.Model.ProductSale;
import com.grocery.management.Model.ReportView;
import com.grocery.management.Model.SaleReport;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.converter.IntegerStringConverter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.sql.*;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;

public class Home implements Initializable {

    public Label totalUser;
    public Label totalProduct;
    public Label totalCategory;
    public Label sub_Category;
    public ImageView sub_c_icon;
    Method method;
    Dialog dialog;
    Properties properties;

    private static final String LIFE_TIME = "lifetime";
    private static final String FILTER_WITH_DATE = "date";
    public TableColumn<String, String> col_sno;
    public TableColumn<ReportView, String> c_name;
    public TableColumn<ReportView, String> c_phone;
    public TableColumn<ReportView, String> c_address;
    public TableColumn<ReportView, String> col_p_id;
    public TableColumn<ReportView, String> col_p_title;
    public TableColumn<ReportView, String> col_p_rate;
    public TableColumn<ReportView, String> col_p_price;
    public TableColumn<ReportView, String> col_p_category;
    public TableColumn<ReportView, String> col_p_sub_category;
    public TableColumn<ReportView, String> col_p_seller_name;
    public TableColumn<ReportView, String> col_p_date;
    public Label total_price;
    public Label total_items;
    public DatePicker to_Date;
    public DatePicker from_date;
    public TableView<ReportView> table_view;
    public ComboBox<Object> limit_com;
    AtomicInteger count = new AtomicInteger();

    ObservableList<ReportView> sellReportList = FXCollections.observableArrayList();

    ObservableList<Object> limit_list;

  //  int count = 0;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        method = new Method();
        dialog = new Dialog();
        properties = method.properties("query.properties");

        limit_list = method.getLimit();
        limit_com.setItems(limit_list);
        limit_com.getSelectionModel().select(0);

        showReport("", "", LIFE_TIME, limit_com.getValue());
        loadTableData();

        countProduct();
        countUser();
        countCategory();
        countSubCategory();

        sub_c_icon.setImage(method.getImage("src/main/resources/com/grocery/management/drawable/Icon/sub_category.png"));


        limit_com.valueProperty().addListener(new ChangeListener<
                >() {
            @Override
            public void changed(ObservableValue<?> observableValue, Object o, Object newValue) {

                if (null == from_date.getValue()) {
                    showReport("", "", LIFE_TIME, newValue);
                    loadTableData();
                } else {

                    String startDate = getDate(from_date.getEditor().getText(), "start");
                    String endDate = getDate(to_Date.getEditor().getText(), "end");

                    showReport(startDate, endDate, FILTER_WITH_DATE, limit_com.getValue());
                    loadTableData();
                }
            }
        });
    }

    private void countPrice() {

        double payablePrice = 0;

        for (ReportView list : sellReportList) {

            String unit = list.getProduct_unit();

            double price = list.getProduct_price();

            switch (unit) {
                case "GRAM", "KG", "METER", "CM", "ML",
                        "LITRE", "PACKET" -> payablePrice = payablePrice + price;
            }
        }

        amountRefresh(payablePrice);
    }

    private void amountRefresh(double payablePrice) {

        total_price.setText(payablePrice +" INR");

    }

    private void loadTableData() {

        col_p_category.setCellValueFactory(new PropertyValueFactory<>("category"));
        col_p_sub_category.setCellValueFactory(new PropertyValueFactory<>("sub_category"));
       // col_sno.setCellValueFactory(new PropertyValueFactory<>("sale_id"));
        c_name.setCellValueFactory(new PropertyValueFactory<>("customer_name"));
        c_phone.setCellValueFactory(new PropertyValueFactory<>("customer_phone"));
        c_address.setCellValueFactory(new PropertyValueFactory<>("customer_address"));
        col_p_id.setCellValueFactory(new PropertyValueFactory<>("product_id"));
        col_p_title.setCellValueFactory(new PropertyValueFactory<>("product_name"));
        col_p_rate.setCellValueFactory(new PropertyValueFactory<>("original_rate"));
        col_p_price.setCellValueFactory(new PropertyValueFactory<>("product_price"));
        col_p_seller_name.setCellValueFactory(new PropertyValueFactory<>("seller_name"));
        col_p_date.setCellValueFactory(new PropertyValueFactory<>("date"));

        countPrice();
        countSaleProduct();

    }

    private void showReport(String date_from, String date_to, String filterType, Object limit_com) {

        if (null != sellReportList) {
            sellReportList.clear();
        } else if (null == limit_com) {
            return;
        }

        Connection connection = method.connection();
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            if (null == connection) {
                System.out.println("Report : Connection Failed");
                return;
            }


            switch (filterType) {

                case FILTER_WITH_DATE -> {

                    if (limit_com instanceof String) {
                        ps = connection.prepareStatement("SELECT * FROM sales_report");
                    } else {

                        ps = connection.prepareStatement(properties.getProperty("REPORT_FILTER_BETWEEN_TWO_DATE"));
                        ps.setString(1, date_from);
                        ps.setString(2, date_to);
                        ps.setInt(3, (int) limit_com);

                    }
                }
                case LIFE_TIME -> {

                    if (limit_com instanceof String) {
                        ps = connection.prepareStatement("SELECT * FROM sales_report");
                    } else {

                        ps = connection.prepareStatement(properties.getProperty("LIFE_TIME_REPORT"));
                        ps.setInt(1, (int) limit_com);
                    }


                }
            }
            if (ps != null) {
                rs = ps.executeQuery();
            }


            while (rs != null && rs.next()) {

                int sale_id = rs.getInt("sale_id");
                int product_id = rs.getInt("product_id");
                int seller_id = rs.getInt("seller_id");
                long customer_phone = rs.getLong("customerPhone");

                String customer_name = rs.getString("customer_name");
                String customer_address = rs.getString("customer_Address");
                String product_name = rs.getString("product_name");
                String product_unit = rs.getString("product_unit");
                String date = rs.getString("date");

                String category = rs.getString("category");
                String sub_category = rs.getString("sub_category");

                double rate = rs.getDouble("rate");
                double product_price = rs.getDouble("product_price");
                double product_quantity = rs.getDouble("productsQuantity");
                double discount = rs.getDouble("discount");


                Connection con = method.connection();
                PreparedStatement preparedStatement;
                ResultSet resultSet;

                String getSeller = "SELECT * FROM users WHERE ID =? ";
                preparedStatement = con.prepareStatement(getSeller);
                preparedStatement.setInt(1, seller_id);
                resultSet = preparedStatement.executeQuery();

                String sellerName = null;

                if (resultSet.next()) {
                    sellerName = resultSet.getString("FIRST_NAME") + " " + resultSet.getString("LAST_NAME");
                }

                sellReportList.add(new ReportView(sale_id, sellerName, customer_name, customer_address, customer_phone, product_price,
                        product_quantity, product_name, product_unit, rate, discount, product_id, date, category, sub_category));

                col_sno.setCellFactory(tc -> {
                    TableCell<String, String> cell = new TableCell<>();
                    Text text = new Text();
                    cell.setGraphic(text);
                    text.setStyle("-fx-text-alignment: CENTER");

                    cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
                    //text.wrappingWidthProperty().bind(col_sno.widthProperty());
                    text.setText(String.valueOf(count.getAndIncrement()));
                    return cell;
                });
            }


            table_view.setItems(sellReportList);


        } catch (SQLException e) {
            e.printStackTrace();
        } finally {

            if (null != connection) {
                try {
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
    }

    public void search_bn(ActionEvent event) {

        if (null == from_date.getValue()) {
            method.show_popup("Please Choose Start Date", from_date);
            return;
        } else if (null == to_Date.getValue()) {
            method.show_popup("Please Choose End Date", to_Date);
            return;
        }

        if (null != sellReportList) {

            sellReportList.clear();
        }

        String startDate = getDate(from_date.getEditor().getText(), "start");
        String endDate = getDate(to_Date.getEditor().getText(), "end");

        showReport(startDate, endDate, FILTER_WITH_DATE, limit_com.getValue());
        loadTableData();


    }

    public void reset_bn(ActionEvent event) {

        showReport("", "", LIFE_TIME, limit_com.getValue());
        loadTableData();
        if (null != from_date.getValue()) {
            from_date.setValue(null);
        }
        if (null != to_Date.getValue()) {
            to_Date.setValue(null);
        }

    }

    private String getDate(String enterDate, String type) {

        // mm-dd-yy
        String[] date = enterDate.split("/");

        String month = date[0];
        String year = date[2];
        int day = 0;

        switch (type) {

            case "start" -> day = Integer.parseInt(date[1]);
            case "end" -> day = (Integer.parseInt(date[1])) + 1;
        }

        String finalDate = year + "-" + month + "-" + day;

        return finalDate;
    }

    private void countCategory() {

        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {

            String query = properties.getProperty("countCategory");
            connection = method.connection();

            if (null == connection) {
                System.out.println("connection failed");
                return;
            }

            ps = connection.prepareStatement(query);
            rs = ps.executeQuery();

            while (rs.next()) {

                int count = rs.getInt(1);
                totalCategory.setText(String.valueOf(count));

            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (null != connection) {

                try {
                    connection.close();
                    ps.close();
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }


    }

    private void countSubCategory() {

        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {

            String query = properties.getProperty("countSubCategory");
            connection = method.connection();

            if (null == connection) {
                System.out.println("connection failed");
                return;
            }

            ps = connection.prepareStatement(query);
            rs = ps.executeQuery();

            while (rs.next()) {

                int count = rs.getInt(1);
                sub_Category.setText(String.valueOf(count));

            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (null != connection) {

                try {
                    connection.close();
                    ps.close();
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }


    }

    private void countProduct() {

        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {

            String query = properties.getProperty("countProduct");
            connection = method.connection();

            if (null == connection) {
                System.out.println("connection failed");
                return;
            }

            ps = connection.prepareStatement(query);
            rs = ps.executeQuery();

            while (rs.next()) {

                int count = rs.getInt(1);
                totalProduct.setText(String.valueOf(count));

            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (null != connection) {

                try {
                    connection.close();
                    ps.close();
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }


    }

    private void countSaleProduct() {

        total_items.setText(String.valueOf(sellReportList.size()));

    }

    private void countUser() {

        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {

            String query = properties.getProperty("countUser");
            connection = method.connection();

            if (null == connection) {
                System.out.println("connection failed");
                return;
            }

            ps = connection.prepareStatement(query);
            rs = ps.executeQuery();

            while (rs.next()) {

                int count = rs.getInt(1);
                totalUser.setText(String.valueOf(count));

            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (null != connection) {

                try {
                    connection.close();
                    ps.close();
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }


    }


}
