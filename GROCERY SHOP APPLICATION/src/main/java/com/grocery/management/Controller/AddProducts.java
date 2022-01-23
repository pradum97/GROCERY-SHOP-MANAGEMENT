package com.grocery.management.Controller;

import com.grocery.management.Dialog;
import com.grocery.management.Main;
import com.grocery.management.Method.Method;
import com.grocery.management.Model.ProfileDetails;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import java.io.*;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class AddProducts implements Initializable {
    public TextField product_price, product_quantity;
    public ImageView product_image;
    public Button submit_bn, uploadImage_bn;
    public ComboBox<String> product_categories, subCategory, unit;
    public TextArea productDescription,product_title;
    private Method method;
    private Dialog dialog;
    private String image_path;

    private ObservableList<String> categories_list = FXCollections.observableArrayList();
    private ObservableList<String> subCategory_list = FXCollections.observableArrayList();
    private ObservableList<String> unit_list ;
    private ProfileDetails profileDetails;

    Properties properties;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        method = new Method();
        dialog = new Dialog();
        properties = method.properties("query.properties");
        unit_list = method.getUnit();
        profileDetails = (ProfileDetails) Main.primaryStage.getUserData();
        unit.setItems(unit_list);
        productDescription.setWrapText(true);
        product_title.setWrapText(true);

        unit.getSelectionModel().select(1);
        subCategory.getSelectionModel().selectFirst();
        load_Data_InCombobox();
    }

    private void load_Data_InCombobox() {
        Connection connection_addProducts = method.connection();

        PreparedStatement ps_categories = null;
        ResultSet rs_country = null;
        try {
            ps_categories = connection_addProducts.prepareStatement(properties.getProperty("getCategory_query"));
            rs_country = ps_categories.executeQuery();

            while (rs_country.next()) {

                int id = rs_country.getInt("categories_id");
                String title = rs_country.getString("title");

                categories_list.add(title + "-" + id);
            }
            product_categories.setItems(categories_list);

            product_categories.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {

                    if (!categories_list.isEmpty()) {
                        subCategory_list.clear();
                        subCategory.setItems(null);
                    }

                    String value = null;

                    if (null == product_categories.getValue()) {
                        return;
                    }
                    value = product_categories.getValue();
                    String[] words = value.split("-");
                    String result = (words[1]);


                    PreparedStatement ps_c_data = null;
                    ResultSet rs_c_data = null;

                    try {
                        ps_c_data = connection_addProducts.prepareStatement(properties.getProperty("getSubCategory"));
                        ps_c_data.setString(1, result);
                        rs_c_data = ps_c_data.executeQuery();

                        while (rs_c_data.next()) {

                            int id = rs_c_data.getInt("id");
                            String title = rs_c_data.getString("title");
                            subCategory_list.add(title);
                        }
                        subCategory.setItems(subCategory_list);

                        if (null == subCategory.getValue()) {

                            subCategory_list.add(words[0]);
                            subCategory.setItems(subCategory_list);

                        }
                        subCategory.getSelectionModel().selectFirst();

                        System.out.println(subCategory.getValue());

                    } catch (SQLException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            if (null != connection_addProducts) {

                                ps_c_data.close();
                                rs_c_data.close();
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != connection_addProducts) {

                    ps_categories.close();
                    rs_country.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void submit_bn(ActionEvent event) {


        if (null == profileDetails) {
            System.out.println("Profile Details null! Please re-Login");
            return;
        }

        String title = product_title.getText();
        String description = productDescription.getText();
        double quantity = Double.parseDouble(product_quantity.getText());
        String unit_Value = unit.getValue();

        int userID = profileDetails.getId();

        double price = 0;

        try {
            price = Double.parseDouble(product_price.getText());

        } catch (NumberFormatException e) {
            //TODO
        }
        if (title.isEmpty()) {
            method.show_popup("Please Enter Product Title", product_title);
            return;
        } else if (price < 0) {
            method.show_popup("Please Enter Product Price In Number", product_price);
            return;
        } else if (null == product_categories.getValue()) {
            method.show_popup("Please Choose Product Category", product_categories);
            return;

        } else if (null == subCategory.getValue()) {
            method.show_popup("Please Choose Product Category", subCategory);
            return;
        } else if (quantity <= 0) {
            method.show_popup("Please Enter Quantity In Number", product_quantity);
            return;
        } else if (description.isEmpty()) {
            method.show_popup("Please Enter Full Description ", productDescription);
            return;
        } else if (null == image_path) {
            method.show_popup("Please Upload Product Image", product_image);
            return;
        }

        String subcategory_value = subCategory.getValue();

        String category_value = product_categories.getValue().toString();
        if (null == category_value) {
            System.out.println("select categories");
            return;
        }
        String[] words = category_value.split("-");
        int category_id = Integer.parseInt((words[1]));


        Connection connection = method.connection();
        PreparedStatement ps = null;

        if (null == connection) {
            System.out.println("Connection Failed");
            return;
        } else if (null == properties) {
            System.out.println("Invalid Properties File");
            return;
        }

        String fileName = "p_image"+System.currentTimeMillis();
        String imgPath = method.copyImage(image_path,fileName);
        try {

            ps = connection.prepareStatement(properties.getProperty("ADD_PRODUCTS"));
            ps.setString(1, title);
            ps.setString(2, description);
            ps.setDouble(3, price);
            ps.setString(4,imgPath);
            ps.setString(5,category_value);
            ps.setDouble(6, quantity);
            ps.setString(7, unit_Value);
            ps.setInt(8, category_id);
            ps.setInt(9, userID);
            ps.setString(10, subcategory_value);

            int i = ps.executeUpdate();

            if (i > 0) {

                product_title.setText("");
                product_price.setText("");

                product_quantity.setText("");
                productDescription.setText("");

                image_path = null;

                File file = new File("src/main/resources/com/grocery/management/drawable/img_preview.png");
                InputStream inputStream = new FileInputStream(file.getPath());
                Image image = new Image(inputStream);
                product_image.setImage(image);

                dialog.show_alertBox("", "Successfully Added");
            }


        } catch (Exception e) {
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

    public void uploadImage_bn(ActionEvent event) {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Image");
        fileChooser.getExtensionFilters().addAll(new FileChooser
                .ExtensionFilter("JPG , PNG , JPEG", "*.JPG", "*.PNG", "*.JPEG"));

        File file = fileChooser.showOpenDialog(Main.primaryStage);
        product_image.setImage(null);

        if (null != file) {
            image_path = file.getAbsolutePath();

            try {
                InputStream is = new FileInputStream(file.getAbsolutePath());
                Image image = new Image(is);
                product_image.setImage(image);
                uploadImage_bn.setText("Change Image");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                product_image.setImage(null);
            }

        }
    }
}
