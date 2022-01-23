package com.grocery.management.Controller;

import com.grocery.management.Dialog;
import com.grocery.management.Main;
import com.grocery.management.Method.Method;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.InputStream;
import java.net.URL;
import java.sql.*;
import java.util.Date;
import java.util.Properties;
import java.util.ResourceBundle;

public class MyProfile implements Initializable {

    public ImageView user_image;
    public Label role_l;
    public Label fullName_l;
    public Label username_l;
    public Label email_l;
    public Label phone_l;
    public Label gender_l;
    public Label dob_l;
    public Label fullAddress_l;
    public Label city_l;
    public Label zip_l;
    public Label state_l;
    public Label country_l;
    public Label fatherName_l;
    public Label id_l;
    public Label registration_date_l;
    Dialog dialog;
    Method method;

    Properties properties;
    int id ;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        method = new Method();
        properties = method.properties("query.properties");
        dialog = new Dialog();

        id  =(int) Main.primaryStage.getUserData();
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
                String create_time = rs.getString("create_time");

                Blob user_Image = rs.getBlob("user_image");

                fullName_l.setText(first_name+" "+last_name);
                fatherName_l.setText(father_name);
                email_l.setText(email);
                phone_l.setText(String.valueOf(phone));
                dob_l.setText(dob);
                fullAddress_l.setText(full_address);
                country_l.setText(country);
                state_l.setText(state);
                city_l.setText(city);
                zip_l.setText(zip);
                username_l.setText(username);
                role_l.setText(role);
                gender_l.setText(gender);
                registration_date_l.setText(create_time);

                InputStream inputStream = user_Image.getBinaryStream();
                Image image = new Image(inputStream);
                user_image.setImage(image);
                id_l.setText(String.valueOf(uid));


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

    public void forget_password_bn(ActionEvent actionEvent) {

        dialog.show_fxml_dialog("forgot_password.fxml","Forgot Password");
    }

    public void update_profile(ActionEvent event) {

        Main.primaryStage.setUserData(id);
        dialog.show_fxml_dialog("Dashboard/edit_profile.fxml","Update Profile");
    }
}
