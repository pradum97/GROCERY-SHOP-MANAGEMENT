package com.grocery.management.Controller;

import com.grocery.management.Dialog;
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

import java.io.*;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUp implements Initializable {
    @FXML
    public ComboBox<String> gender_comboBox, county_comboBox, state_comboBox,
            city_comboBox, role_combobox;
    public Button submit_bn;
    public TextField first_name_f;
    public TextField username_f;
    public TextField last_name_f;
    public TextField father_name_f;
    public TextField phone_f;
    public TextField email_f;
    public DatePicker dob_f;
    public TextArea full_address_f;
    public TextField zip_f;
    public PasswordField password_f;
    public TextField con_password_f;
    public ImageView profile_photo;
    public Button profile_img_choose;
    public Label message_label;
    Method method;
    private String profile_image_path;
    private Properties properties;
    private ObservableList<String> gender_list, role_combobox_list;
    private ObservableList<String> country_list = FXCollections.observableArrayList();
    private ObservableList<String> state_list = FXCollections.observableArrayList();
    private ObservableList<String> cities_list = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        method = new Method();
        properties = method.properties("query.properties");

        gender_list = method.getGender();
        role_combobox_list = method.getRole();

        gender_comboBox.setItems(gender_list);
        role_combobox.setItems(role_combobox_list);
        dob_f.setEditable(false);

        setData_In_ComboBox();



    }

    public void submit_bn(ActionEvent actionEvent) {
        check_empty_field();
    }

    private void setData_In_ComboBox() {

        Connection connection_address = method.connection();

        PreparedStatement ps_country = null;
        ResultSet rs_country = null;
        try {

            ps_country = connection_address.prepareStatement(properties.getProperty("getCountry"));
            rs_country = ps_country.executeQuery();

            while (rs_country.next()) {

                String country_Name = rs_country.getString("country_Name") +
                        "(" + rs_country.getString("country_code") + ")";

                country_list.add(country_Name);
            }

            county_comboBox.setItems(country_list);

            county_comboBox.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {

                    if (!state_list.isEmpty()) {
                        state_list.clear();
                        state_comboBox.setItems(null);
                    }

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
                            public void handle(ActionEvent actionEvent) {
                                if (!cities_list.isEmpty()) {
                                    cities_list.clear();
                                }

                                String value = state_comboBox.getValue();
                                if (null == value) {
                                    return;
                                }
                                String[] words = value.split("\\(");
                                String result = (words[1]).replaceAll("\\)", "");
                                PreparedStatement ps_city = null;
                                ResultSet rs_city = null;

                                try {

                                    ps_city = connection_address.prepareStatement(properties.getProperty("getCity"));
                                    ps_city.setString(1, result);
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
                        });


                    } catch (SQLException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            if (null != connection_address) {

                                ps_state.close();
                                rs_state.close();
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
                if (null != connection_address) {

                    ps_country.close();
                    rs_country.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void check_empty_field() {

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
        String password = password_f.getText();
        String confirm_password = con_password_f.getText();
        String regexPattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

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
        } else if (password.isEmpty()) {
            method.show_popup("Enter Password", password_f);
            return;
        } else if (confirm_password.isEmpty()) {
            method.show_popup("Enter Confirm Password", con_password_f);
            return;
        } else if (null == profile_image_path) {
            method.show_popup("please upload Photo", profile_img_choose);
            return;
        } else if (!password.equals(confirm_password)) {
            method.show_popup("confirm password doesn't match", con_password_f);
            return;
        } else if (mac_address.isEmpty()) {
            System.out.println("Invalid Mac Address");
            return;
        }

        Pattern pattern_email = Pattern.compile(regexPattern);
        Matcher matcher_email = pattern_email.matcher(email);

        if (!matcher_email.find()) {

            method.show_popup("Email Not Valid!", email_f);
            method.message_label(message_label, "Email Not Valid! Please enter valid Email", Color.RED);

            return;
        }

        if (null == properties) {
            System.out.println("Properties");
            return;
        }


        try {
            InputStream profile_is = new FileInputStream(profile_image_path);

            connection = method.connection();
            ps_insert_data = connection.prepareStatement(properties.getProperty("user_Registration"));

            ps_insert_data.setString(1, first_name);
            ps_insert_data.setString(2, last_name);
            ps_insert_data.setString(3, father_name);
            ps_insert_data.setString(4, dob); // date of birth
            ps_insert_data.setString(5, gender_comboBox.getValue());
            ps_insert_data.setString(6, role_combobox.getValue());
            ps_insert_data.setString(7, email);
            ps_insert_data.setString(8, username);
            ps_insert_data.setString(9, password);
            ps_insert_data.setString(10, phone);
            ps_insert_data.setString(11, full_address);
            ps_insert_data.setString(12, county_comboBox.getValue());
            ps_insert_data.setString(13, state_comboBox.getValue());
            ps_insert_data.setString(14, city_comboBox.getValue());
            ps_insert_data.setString(15, zip);
            ps_insert_data.setBlob(16, profile_is); // user image
            ps_insert_data.setString(17, mac_address); // mac address

            int result = ps_insert_data.executeUpdate();


            if (result > 0) {

                method.message_label(message_label, "Registration Successful", Color.GREEN);
                new Main().changeScene("login.fxml", "Login Here");
                new Dialog().show_alertBox("", "Registration Successful");

            } else {
                method.message_label(message_label, "Registration Failed", Color.RED);

            }

        } catch (SQLException | FileNotFoundException e) {
            e.printStackTrace();
            method.message_label(message_label, "Registration Failed : " + e.getMessage(), Color.RED);
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

            profile_image_path = file.getAbsolutePath();

            InputStream is = new FileInputStream(file.getAbsolutePath());

            Image image = new Image(is);
            profile_photo.setImage(image);
            profile_img_choose.setText("Change Image");

        }
    }

    public void already_account_bn(ActionEvent actionEvent) throws IOException {

        new Main().changeScene("login.fxml", "Login Here");

    }
}
