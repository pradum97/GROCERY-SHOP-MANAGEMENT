package com.grocery.management.Controller;

import com.grocery.management.Dialog;
import com.grocery.management.Main;
import com.grocery.management.Method.Method;
import com.grocery.management.Model.All_Product;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.util.Callback;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.net.URL;
import java.sql.*;
import java.util.Optional;
import java.util.Properties;
import java.util.ResourceBundle;

public class MyProduct implements Initializable {
    public TableView<All_Product> product_table_view;
    public TableColumn<All_Product, String> col_id;
    public TableColumn<All_Product, String> col_title;
    public TableColumn<All_Product, String> col_price;
    public TableColumn<All_Product, String> col_category;
    public TableColumn<All_Product, String> col_quantity;
    public TableColumn<All_Product, String> col_categoryId;
    public TableColumn<All_Product, String> col_user_id;
    public TableColumn<All_Product, String> col_action;
    public TableColumn<All_Product, String> col_unit;
    public TableColumn<All_Product, String> col_subcategory;
    public HBox refresh_bn;
    public ImageView refresh_img;
    public TextField product_find_tf;
    Method method;
    Dialog dialog;
    Properties properties;
    int count = 5 ;

    ObservableList<All_Product> productList = FXCollections.observableArrayList();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        product_table_view.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        method = new Method();
        dialog = new Dialog();
        properties = method.properties("query.properties");

        getProducts();
        load_Table_Data();

        tableSetting();

        setImage();
        search_Item();
    }

    private void tableSetting() {

       // product_table_view.setFixedCellSize(45);
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
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void getProducts() {

        Connection connection = method.connection();
        PreparedStatement ps = null;
        ResultSet resultSet = null;

        if (null == connection) {
            System.out.println("connection failed");
            return;
        }

        try {

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
                        All_Product(id, p_Image, price, quantity,unit, title, description,
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
                    ps.close();
                    resultSet.close();

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private void load_Table_Data() {

        col_id.setCellValueFactory(new PropertyValueFactory<>("product_id"));
        col_price.setCellValueFactory(new PropertyValueFactory<>("price"));
        col_title.setCellValueFactory(new PropertyValueFactory<>("title"));
        col_subcategory.setCellValueFactory(new PropertyValueFactory<>("sub_category"));
        col_category.setCellValueFactory(new PropertyValueFactory<>("category"));
        col_quantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        col_unit.setCellValueFactory(new PropertyValueFactory<>("unit"));
        col_categoryId.setCellValueFactory(new PropertyValueFactory<>("category_id"));
        col_user_id.setCellValueFactory(new PropertyValueFactory<>("user_id"));

        Callback<TableColumn<All_Product, String>, TableCell<All_Product, String>>
                cellFoctory = (TableColumn<All_Product, String> param) ->
        {
            // make cell containing buttons
            final TableCell<All_Product, String> cell = new TableCell<All_Product, String>() {
                @Override
                public void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    //that cell created only on non-empty rows
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

                            All_Product edit_selection = product_table_view.
                                    getSelectionModel().getSelectedItem();

                            int id = edit_selection.getProduct_id();

                            Main.primaryStage.setUserData(id);
                            dialog.show_fxml_dialog("Dashboard/update_products.fxml", "Update Product");

                            refresh();

                        });

                        iv_delete.setOnMouseClicked((MouseEvent event) -> {

                            All_Product delete_selection = product_table_view.
                                    getSelectionModel().getSelectedItem();
                            Alert alert = new Alert(Alert.AlertType.NONE);
                            alert.setAlertType(Alert.AlertType.CONFIRMATION);
                            alert.setHeaderText("Are you sure you want to delete this item?");
                            alert.initModality(Modality.APPLICATION_MODAL);
                            alert.initOwner(Main.primaryStage);
                            Optional<ButtonType> result = alert.showAndWait();
                            ButtonType button = result.orElse(ButtonType.CANCEL);

                            if (button == ButtonType.OK) {
                                deleteProducts(delete_selection.getProduct_id(),delete_selection.getProduct_image());
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
        col_action.setCellFactory(cellFoctory);
        product_table_view.setItems(productList);


        col_title.setCellFactory(tc -> {
            TableCell<All_Product, String> cell = new TableCell<>();
            Text text = new Text();
            text.setStyle("-fx-font-size: 13");
            cell.setGraphic(text);
            text.setStyle("-fx-text-alignment: CENTER");
            cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
            text.wrappingWidthProperty().bind(col_title.widthProperty());
            text.textProperty().bind(cell.itemProperty());
            return cell ;
        });


    }

    private void deleteProducts(int product_id, String product_image) {
        Connection connection = method.connection();
        PreparedStatement ps = null;

        if (null == connection) {
            System.out.println("connection Failed");
            return;
        }

        try {
            ps = connection.prepareStatement(properties.getProperty("DELETE_PRODUCT"));
            ps.setInt(1, product_id);
            int result = ps.executeUpdate();

            if (result > 0) {

                File file = new File(product_image);
                if (file.exists()) {
                    FileUtils.forceDelete(file);
                }
                dialog.show_alertBox("", "Successfully Deleted");
                refresh();
            }


        } catch (SQLException | IOException e) {
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

    public void refresh_bn(MouseEvent event) {

        refresh();
        product_find_tf.setText("");
    }

    private void refresh(){

        if (null != productList) {
            productList.clear();
        }
        getProducts();
        load_Table_Data();
        search_Item();

    }
}
