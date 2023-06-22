package com.grocery.management.Controller;

import com.grocery.management.Dialog;
import com.grocery.management.Login;
import com.grocery.management.Main;
import com.grocery.management.Method.Method;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.Properties;
import java.util.ResourceBundle;

public class EditProfile implements Initializable {
    @FXML
    public ComboBox<String> gender_comboBox, county_comboBox, state_comboBox,
            city_comboBox, role_combobox;
    public Button submit_bn, profile_img_choose;
    public TextField first_name_f, username_f, last_name_f, father_name_f, phone_f, email_f, zip_f;
    public DatePicker dob_f;
    public TextArea full_address_f;
    public ImageView profile_photo;
    public Label message_label;
    private Method method;
    private Blob user_Image;
    private String user_image_path;
    private int id;
    private Properties properties;

    private ObservableList<String> gender_list, role_combobox_list;

    private ObservableList<String> state_list = FXCollections.observableArrayList();
    private ObservableList<String> cities_list = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        method = new Method();
        properties = method.properties("query.properties");

        gender_list = method.getGender();
        role_combobox_list = method.getRole();

        dob_f.setEditable(false);

        id = (int) Main.primaryStage.getUserData();

        get_Profile_Details(id);
    }

    private void get_Profile_Details(int id) {

        Connection connection = method.connection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        String country = null;
        String state = null;

        if (null == connection) {
            System.out.println("Connection Failed");
            return;
        }

        try {
            ps = connection.prepareStatement(properties.getProperty("get_Spefic_Profile_Details"));
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {

                int uid = rs.getInt("id");
                String first_name = rs.getString("first_name");
                String last_name = rs.getString("last_name");
                String father_name = rs.getString("father_name");
                String email = rs.getString("email_id");
                long phone = rs.getLong("phone_number");
                String dob = rs.getString("date_of_birth");
                String full_address = rs.getString("full_address");
                country = rs.getString("country");
                state = rs.getString("state");
                String city = rs.getString("city");
                String zip = rs.getString("zip_code");
                String username = rs.getString("username");
                String role = rs.getString("role");
                String gender = rs.getString("gender");

                user_Image = rs.getBlob("user_image");
                first_name_f.setText(first_name);
                last_name_f.setText(last_name);
                father_name_f.setText(father_name);
                email_f.setText(email);
                phone_f.setText(String.valueOf(phone));
                full_address_f.setText(full_address);
                zip_f.setText(zip);
                username_f.setText(username);

                state_comboBox.getItems().add(state);
                state_comboBox.getSelectionModel().selectFirst();

                city_comboBox.getItems().add(city);
                city_comboBox.getSelectionModel().selectFirst();

                gender_comboBox.getItems().add(gender);
                gender_comboBox.getSelectionModel().selectFirst();

                role_combobox.getItems().add(role);
                role_combobox.getSelectionModel().selectFirst();

                InputStream inputStream = user_Image.getBinaryStream();

                Image image = new Image(inputStream);

                profile_photo.setImage(image);

                String[] date_of_birth = dob.split("/");

                int month = Integer.parseInt(date_of_birth[0]);
                int day = Integer.parseInt(date_of_birth[1]);
                int year = Integer.parseInt(date_of_birth[2]);

                dob_f.setValue(LocalDate.of(year, month, day));

            }
            get_State(country);
            get_city(state);

            role_combobox.setItems(role_combobox_list);
            gender_comboBox.setItems(gender_list);




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


    private void get_State(String country) {

        Connection connection_address = method.connection();

        if (!state_list.isEmpty()) {
            state_list.clear();
            state_comboBox.setItems(null);
        }
        county_comboBox.getItems().add(country);
        county_comboBox.getSelectionModel().selectFirst();
        String value = county_comboBox.getValue();

        if (null == value) {
            return;
        }
        String[] words = value.split("\\(");

        String result = (words[1]).replaceAll("\\)", "");

        PreparedStatement ps_state = null;
        ResultSet rs_state = null;

        try {

            ps_state = connection_address.prepareStatement(properties.getProperty("getState"));
            ps_state.setString(1, result);
            rs_state = ps_state.executeQuery();


            while (rs_state.next()) {

                String state_name = rs_state.getString("state_name") +
                        "(" + rs_state.getString("state_code") + ")";

                state_list.add(state_name);

            }
            state_comboBox.setItems(state_list);

            state_comboBox.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {

                    if (!cities_list.isEmpty()) {
                        cities_list.clear();
                    }

                    get_city(state_comboBox.getValue());
                }
            });


        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();

        } finally {
            try {
                if (null != connection_address) {
                    connection_address.close();
                    ps_state.close();
                    rs_state.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void get_city(String state) {

        Connection connection_address = method.connection();


        if (null == connection_address) {
            System.out.println("connection failed");
            return;
        }

        if (null == state) {
            return;
        }
        String[] str = state.split("\\(");
        String s_result = (str[1]).replaceAll("\\)", "");
        PreparedStatement ps_city = null;
        ResultSet rs_city = null;

        try {

            ps_city = connection_address.prepareStatement(properties.getProperty("getCity"));
            ps_city.setString(1, s_result);
            rs_city = ps_city.executeQuery();

            while (rs_city.next()) {

                String city_name = rs_city.getString("city_name");
                cities_list.add(city_name);

            }
            city_comboBox.setItems(cities_list);


        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != connection_address) {

                    connection_address.close();
                    rs_city.close();
                    ps_city.close();
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

    public void submit_bn(ActionEvent actionEvent) {

        Connection connection = null;
        PreparedStatement ps_insert_data = null;

        String mac_address = method.get_mac_address();
        String first_name = first_name_f.getText();
        String last_name = last_name_f.getText();
        String father_name = father_name_f.getText();
        String username = username_f.getText();
        String phone = phone_f.getText();
        String email = email_f.getText();
        String dob = dob_f.getEditor().getText();
        String full_address = full_address_f.getText();
        String zip = zip_f.getText();

        if (first_name.isEmpty()) {
            method.show_popup("Enter First Name", last_name_f);
            return;

        } else if (father_name.isEmpty()) {
            method.show_popup("Enter Father Name", father_name_f);
            return;

        } else if (username.isEmpty()) {
            method.show_popup("Enter Username", username_f);
            return;

        } else if (phone.isEmpty()) {
            method.show_popup("Enter Phone Number", phone_f);
            return;

        } else if (email.isEmpty()) {
            method.show_popup("Enter Valid Email", email_f);
            return;

        } else if (dob.isEmpty()) {
            method.show_popup("Choose Date_Of_Birth", dob_f.getEditor());
            return;

        } else if (null == gender_comboBox.getValue()) {
            method.show_popup("Choose Your Gender", gender_comboBox);
            return;

        } else if (null == role_combobox.getValue()) {
            method.show_popup("Choose role_combobox", role_combobox);
            return;
        } else if (full_address.isEmpty()) {
            method.show_popup("Enter Full Address", full_address_f);
            return;
        } else if (null == county_comboBox.getValue()) {
            method.show_popup("Choose Country", county_comboBox);
            return;
        } else if (null == state_comboBox.getValue()) {
            method.show_popup("Choose State", state_comboBox);
            return;
        } else if (null == city_comboBox.getValue()) {
            method.show_popup("Choose City", city_comboBox);
            return;
        } else if (zip.isEmpty()) {
            method.show_popup("Enter ZIP or PIN CODE", zip_f);
            return;
        } else if (null == properties) {
            System.out.println("Properties");
            return;
        }


        try {

            connection = method.connection();
            ps_insert_data = connection.prepareStatement(properties.getProperty("UPDATE_USER"));

            ps_insert_data.setString(1, first_name);
            ps_insert_data.setString(2, last_name);
            ps_insert_data.setString(3, father_name);
            ps_insert_data.setString(4, dob); // date of birth
            ps_insert_data.setString(5, gender_comboBox.getValue());
            ps_insert_data.setString(6, role_combobox.getValue());
            ps_insert_data.setString(7, email);
            ps_insert_data.setString(8, username);
            ps_insert_data.setString(9, phone);
            ps_insert_data.setString(10, full_address);
            ps_insert_data.setString(11, state_comboBox.getValue());
            ps_insert_data.setString(12, city_comboBox.getValue());
            ps_insert_data.setString(13, zip);


            if (null == user_image_path) {

                ps_insert_data.setBlob(14, user_Image); // user image

            } else {

                InputStream profile_is = new FileInputStream(user_image_path);
                ps_insert_data.setBlob(14, profile_is); // user image
            }

            ps_insert_data.setInt(15, id);

            int result = ps_insert_data.executeUpdate();

            if (result > 0) {

                method.message_label(message_label, "Successfully Updated", Color.GREEN);
                new Dialog().show_alertBox("", "Successfully Updated");

                Stage stage = Dialog.stage;

                int login_id = Login.login_id;

                if (login_id > 0){

                    if (login_id == id){
                        new Main().changeScene("login.fxml", "Re-Login");
                    }

                }

                if (null != stage) {

                    if (stage.isShowing()) {

                        stage.close();
                    }
                }

            } else {
                method.message_label(message_label, "Update Failed", Color.RED);
            }

        } catch (SQLException | FileNotFoundException e) {
            e.printStackTrace();
            method.message_label(message_label, "Update Failed : " + e.getMessage(), Color.RED);
        } finally {

            if (null != connection) {
                try {
                    connection.close();
                    ps_insert_data.close();

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void chooseProfilePic(ActionEvent actionEvent) throws FileNotFoundException {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Image");
        fileChooser.getExtensionFilters().addAll(new FileChooser
                .ExtensionFilter("JPG , PNG , JPEG", "*.JPG", "*.PNG", "*.JPEG"));

        File file = fileChooser.showOpenDialog(Main.primaryStage);

        if (null != file) {

            user_image_path = file.getAbsolutePath();

            InputStream is = new FileInputStream(file.getAbsolutePath());

            Image image = new Image(is);
            profile_photo.setImage(image);
            profile_img_choose.setText("Change Image");

        }
    }
}

