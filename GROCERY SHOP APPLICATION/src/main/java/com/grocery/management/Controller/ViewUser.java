package com.grocery.management.Controller;

import com.grocery.management.Dialog;
import com.grocery.management.Main;
import com.grocery.management.Method.Method;
import com.grocery.management.Model.ProfileDetails;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.Properties;
import java.util.ResourceBundle;

public class ViewUser implements Initializable {
    public TableView<ProfileDetails> user_table_view;
    public TableColumn<ProfileDetails, String> col_id;
    public TableColumn<ProfileDetails, String> col_email;
    public TableColumn<ProfileDetails, String> col_role;
    public TableColumn<ProfileDetails, String> col_dob;
    public TableColumn<ProfileDetails, String> col_username;
    public TableColumn<ProfileDetails, String> col_phone;
    public TableColumn<ProfileDetails, String> col_createtime;
    public TableColumn<ProfileDetails, String> col_gender;
    public TableColumn<ProfileDetails, String> col_address;
    public TableColumn<ProfileDetails, String> editCol;
    public TableColumn<ProfileDetails, String> col_fullName;

    Method method;
    Dialog dialog;
    Properties properties;

    ObservableList<ProfileDetails> userList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        user_table_view.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        method = new Method();
        dialog = new Dialog();
        properties = method.properties("query.properties");



        getAllUsers();
        load_Table_Data();
        tableSetting();


    }

    private void refresh(){

        if (null != userList){
            userList.clear();
        }

        getAllUsers();
        load_Table_Data();
        tableSetting();
    }

    private void getAllUsers() {

        Connection connection = new Method().connection();
        Statement statement = null;
        ResultSet rs = null;

        if (null == properties){
            return;
        }

        try {
            statement = connection.createStatement();
            rs = statement.executeQuery(properties.getProperty("get_All_User_Details"));
            while (rs.next()) {

                int id = rs.getInt("id");
                long phone = rs.getLong("phone_number");
                Blob user_image = rs.getBlob("USER_IMAGE");
                String full_name = rs.getString("first_name") + " " + rs.getString("last_name");
                String gender = rs.getString("gender");
                String email = rs.getString("email_id");
                String role = rs.getString("role");
                String dob = rs.getString("date_of_birth");
                String username = rs.getString("username");
                String address = rs.getString("full_address");
                String create_time = rs.getString("create_time");

                userList.add(new ProfileDetails(id, phone,user_image,
                        full_name, email, role, dob, username,
                        gender, address, create_time));

            }

            user_table_view.setItems(userList);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                    if (statement != null) {
                        statement.close();
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

    private void tableSetting() {

        user_table_view.setFixedCellSize(45);
        user_table_view.prefHeightProperty().bind(Bindings.size(
                user_table_view.getItems()).multiply(user_table_view.getFixedCellSize()).add(30));
    }

    private void load_Table_Data() {

        col_id.setCellValueFactory(new PropertyValueFactory<>("id"));
        col_phone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        col_fullName.setCellValueFactory(new PropertyValueFactory<>("full_name"));
        col_gender.setCellValueFactory(new PropertyValueFactory<>("gender"));
        col_email.setCellValueFactory(new PropertyValueFactory<>("email_id"));
        col_role.setCellValueFactory(new PropertyValueFactory<>("role"));
        col_dob.setCellValueFactory(new PropertyValueFactory<>("date_of_birth"));
        col_username.setCellValueFactory(new PropertyValueFactory<>("username"));
        col_address.setCellValueFactory(new PropertyValueFactory<>("address"));
        col_createtime.setCellValueFactory(new PropertyValueFactory<>("create_time"));

        Callback<TableColumn<ProfileDetails, String>, TableCell<ProfileDetails, String>>
                cellFoctory = (TableColumn<ProfileDetails, String> param) -> {

            final TableCell<ProfileDetails, String> cell = new TableCell<ProfileDetails, String>() {
                @Override
                public void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                        setText(null);

                    } else {

                        FileInputStream input_edit, input_view, input_delete ;
                        File edit_file, view_file, delete_file;
                        ImageView iv_edit,iv_view,iv_delete;
                        Image image_edit = null,image_view = null,image_delete = null;

                        try {
                            edit_file = new File("src/main/resources/com/grocery/management/drawable/Icon/edit_ic.png");
                            view_file = new File("src/main/resources/com/grocery/management/drawable/Icon/view_ic.png");
                            delete_file = new File("src/main/resources/com/grocery/management/drawable/Icon/delete_ic.png");

                            input_edit = new FileInputStream(edit_file.getPath());
                            input_view = new FileInputStream(view_file.getPath());
                            input_delete = new FileInputStream(delete_file.getPath());

                            image_edit = new Image(input_edit);
                            image_view = new Image(input_view);
                            image_delete = new Image(input_delete);


                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }

                        iv_edit = new ImageView(image_edit);
                        iv_edit.setFitHeight(25);
                        iv_edit.setFitHeight(25);
                        iv_edit.setPreserveRatio(true);

                        iv_view = new ImageView(image_view);
                        iv_view.setFitHeight(25);
                        iv_view.setFitWidth(25);
                        iv_view.setPreserveRatio(true);

                        iv_delete = new ImageView(image_delete);
                        iv_delete.setFitHeight(20);
                        iv_delete.setFitWidth(20);
                        iv_delete.setPreserveRatio(true);


                        iv_edit.setStyle(
                                " -fx-cursor: hand ;"
                                        + "-glyph-size:28px;"
                                        + "-fx-fill:#c506fa;"
                        );
                        iv_view.setStyle(
                                " -fx-cursor: hand ;"
                                        + "-glyph-size:28px;"
                                        + "-fx-fill:#44ee0c;"
                        );

                        iv_delete.setStyle(
                                " -fx-cursor: hand ;"
                                        + "-glyph-size:28px;"
                                        + "-fx-fill:#ff0000;"
                        );


                        iv_edit.setOnMouseClicked((MouseEvent event) -> {

                            ProfileDetails edit_selection = user_table_view.
                                    getSelectionModel().getSelectedItem();

                            if (null == edit_selection){
                                method.show_popup("Please Select",user_table_view);
                                return;
                            }

                            Main.primaryStage.setUserData(edit_selection.getId());

                            dialog.show_fxml_dialog("Dashboard/edit_profile.fxml","Update Profile");
                            refresh();


                        });
                        iv_view.setOnMouseClicked((MouseEvent event) -> {

                            ProfileDetails view_selection = user_table_view.
                                    getSelectionModel().getSelectedItem();

                            if (null == view_selection){
                                method.show_popup("Please Select",user_table_view);
                                return;
                            }

                            Main.primaryStage.setUserData(view_selection.getId());
                            dialog.show_fxml_dialog("Dashboard/my_profile.fxml","User Profile");
                        });
                        iv_delete.setOnMouseClicked((MouseEvent event) -> {


                            ProfileDetails delete_selection = user_table_view.
                                    getSelectionModel().getSelectedItem();

                            if (null == delete_selection){
                                method.show_popup("Please Select ",user_table_view);
                                return;
                            }

                            System.out.println("choose : "+delete_selection.getFull_name());

                        });

                        iv_delete.setVisible(false);

                        HBox managebtn = new HBox(iv_edit,iv_delete ,iv_view);
                        managebtn.setStyle("-fx-alignment:center");
                        HBox.setMargin(iv_edit, new Insets(2, 2, 0, 3));
                        HBox.setMargin(iv_view, new Insets(2, 3, 0, 20));
                        HBox.setMargin(iv_delete, new Insets(2, 3, 0, 10));

                        setGraphic(managebtn);

                        setText(null);

                    }
                }

            };

            return cell;
        };
        editCol.setCellFactory(cellFoctory);
        user_table_view.setItems(userList);
    }

    public void Add_Users(ActionEvent actionEvent) throws IOException {

        dialog.show_fxml_dialog("signup.fxml","Add New Member");


    }
}
