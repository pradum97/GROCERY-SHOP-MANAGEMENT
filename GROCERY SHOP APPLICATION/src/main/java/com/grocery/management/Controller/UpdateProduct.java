package com.grocery.management.Controller;

import com.grocery.management.Dialog;
import com.grocery.management.Main;
import com.grocery.management.Method.Method;
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
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.net.URL;
import java.sql.*;
import java.util.Properties;
import java.util.ResourceBundle;

public class UpdateProduct implements Initializable {

    public TextArea product_title,product_description;
    public TextField product_price,product_quantity;
    public ComboBox<String> product_categories;
    public ComboBox<String> subCategory,unitType;
    public ImageView product_image;
    public Button uploadImage_bn,submit_bn;

    private Method method;
    private Dialog dialog;
    private Properties properties;
    private String image_path,product_Image;
    private int id;
    ObservableList<String> categories_list = FXCollections.observableArrayList();
    ObservableList<String> subCategory_list = FXCollections.observableArrayList();
    private ObservableList<String> unit_list;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        method = new Method();
        dialog = new Dialog();
        properties = method.properties("query.properties");

        product_description.setWrapText(true);
        product_title.setWrapText(true);

        unit_list = method.getUnit();

        id = (int) Main.primaryStage.getUserData();

        getProducts(id);


    }

    private void getProducts(int id) {

        Connection connection = method.connection();
        PreparedStatement ps = null;
        ResultSet resultSet = null;

        if (null == connection) {
            System.out.println("connection failed");
            return;
        }
        try {

            ps = connection.prepareStatement(properties.getProperty("get_Spefic_Product"));
            ps.setInt(1, id);
            resultSet = ps.executeQuery();

            while (resultSet.next()) {

                int uid = resultSet.getInt("product_id");
                product_Image = resultSet.getString("product_image");
                double price = resultSet.getDouble("product_price");
                String title = resultSet.getString("product_title");
                String description = resultSet.getString("product_description");
                String category = resultSet.getString("product_categories");
                String sub_Category = resultSet.getString("sub_category");
                double quantity = resultSet.getDouble("quantity");
                String unit = resultSet.getString("unit");
                String category_id = resultSet.getString("categories_id");
                String user_id = resultSet.getString("user_id");

                product_title.setText(title);
                product_price.setText(String.valueOf(price));
                product_description.setText(description);

                subCategory.getItems().add(sub_Category);
                subCategory.getSelectionModel().selectFirst();

                product_categories.getItems().add(category);
                product_categories.getSelectionModel().selectFirst();

                product_quantity.setText(String.valueOf(quantity));

                unitType.getItems().add(unit);
                unitType.getSelectionModel().selectFirst();

                InputStream is = new FileInputStream(new File(product_Image).getAbsolutePath());
                Image image = new Image(is);
                product_image.setImage(image);
                is.close();

            }


            unitType.setItems(unit_list);

        } catch (Exception e) {

            System.out.println("getProduct : " + e.getMessage());
            e.getStackTrace();

        } finally {
            getCategory();
            getSubCategory();

            if (null != connection) {

                try {

                    connection.close();
                    ps.close();
                    resultSet.close();

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private void getCategory() {

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

                    getSubCategory();
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

    private void getSubCategory() {

        if (!categories_list.isEmpty()) {
            subCategory_list.clear();
        }

        String value;

        if (null == product_categories.getValue()) {
            return;
        }

        value = product_categories.getValue();
        String[] words = value.split("-");
        String result = (words[1]);


        Connection connection = method.connection();
        PreparedStatement ps_c_data = null;
        ResultSet rs_c_data = null;

        try {
            ps_c_data = connection.prepareStatement(properties.getProperty("getSubCategory"));
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
                subCategory.getSelectionModel().selectFirst();

            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != connection) {
                    connection.close();
                    ps_c_data.close();
                    rs_c_data.close();

                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

    public void submit_bn(ActionEvent event) {

        String title = product_title.getText();
        String description = product_description.getText();
        double quantity = Double.parseDouble(product_quantity.getText());

        String unit = unitType.getValue();

        double price = 0;
        try {
            price = Double.parseDouble(product_price.getText());

        } catch (NumberFormatException e) {
            //TODO
            method.show_popup("Please Enter Product Price In Number", product_price);
            return;
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
            method.show_popup("Please Enter Full Description ", product_description);
            return;
        }

        String subcategory_value = subCategory.getValue();

        String category_value = product_categories.getValue().toString();
        if (null == category_value) {
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

        try {

            ps = connection.prepareStatement(properties.getProperty("UPDATE_PRODUCTS"));

            ps.setString(1, title);
            ps.setString(2, description);
            ps.setDouble(3, price);
            ps.setString(5, category_value);
            ps.setDouble(6, quantity);
            ps.setString(7, unit);
            ps.setInt(8, category_id);
            ps.setString(9, subcategory_value);
            ps.setInt(10, id);

            if (null == image_path) {
                ps.setString(4, product_Image);
            } else {

                String fileName = "p_image" + System.currentTimeMillis();
                String imgPath = method.copyImage(image_path, fileName);

                File file = new File(product_Image);
                if (file.exists()) {
                    FileUtils.forceDelete(file);
                }


                ps.setString(4, imgPath);
            }

            int i = ps.executeUpdate();

            if (i > 0) {

                product_title.setText("");
                product_price.setText("");
                product_quantity.setText("");
                product_description.setText("");

                image_path = null;

                Dialog.stage.close();

                dialog.show_alertBox("", "Successfully Updated");

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


        if (null != file) {
            product_image.setImage(null);
            image_path = file.getAbsolutePath();

            InputStream is = null;

            try {
                is = new FileInputStream(file.getAbsolutePath());
                Image image = new Image(is);
                product_image.setImage(image);
                uploadImage_bn.setText("Change Image");
                is.close();
            } catch (Exception e) {
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        } else {
            File previewImgFile = null;
            InputStream is = null;
            Image image = null;

            try {


                if (null == image_path && null == product_Image) {

                    previewImgFile = new File("src/main/resources/com/grocery/management/drawable/img_preview.png");
                    is = new FileInputStream(previewImgFile.getAbsolutePath());
                    image = new Image(is);
                    product_image.setImage(image);
                    image_path = null;

                } else if (null != product_Image) {

                    is = new FileInputStream(product_Image);
                    image = new Image(is);
                    product_image.setImage(image);

                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


        }
    }
}
