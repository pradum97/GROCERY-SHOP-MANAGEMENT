package com.grocery.management;

import com.grocery.management.Method.Method;
import com.grocery.management.Model.ProfileDetails;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Login implements Initializable {

    @FXML
    public Label message_label;
    public TextField email_f;
    public TextField product_title;
    public TextField product_price;
    private Method method;
    public PasswordField pass;
    URL url;
    Main main;
    Properties properties;
    public  static int login_id;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        method = new Method();
        this.url = url;
        main = new Main();
        properties = method.properties("query.properties");

    }

    public void forget_password_bn(ActionEvent actionEvent) throws IOException {
        new Dialog().show_fxml_dialog("forgot_password.fxml", "Forgot Password");


    }

    public void login_bn(ActionEvent actionEvent) {

        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        String email = email_f.getText();
        String password = pass.getText();

        String regexPattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

        if (email.isEmpty()) {
            method.show_popup("Please enter valid username", email_f);
            return;
        } else if (password.isEmpty()) {
            method.show_popup("Please enter password", pass);
            return;
        }

        Pattern pattern_email = Pattern.compile(regexPattern);
        Matcher matcher_email = pattern_email.matcher(email);

        if (!matcher_email.find()) {

            method.show_popup("Email Not Valid!", email_f);
            method.message_label(message_label, "Email Not Valid! Please enter valid Email", Color.RED);

            return;
        }

        try {
            if (null == properties){
                System.out.println("Properties File Not Found");
                return;
            }

            connection = method.connection();
            ps = connection.prepareStatement(properties.getProperty("login"));
            ps.setString(1, email);
            ps.setString(2, password);

            rs = ps.executeQuery();

            if (rs.next()) {

                int id = rs.getInt("id");
                long phone = rs.getLong("phone_number");
                Blob user_image = rs.getBlob("USER_IMAGE");
                String full_name = rs.getString("first_name") + " " + rs.getString("last_name");
                String gender = rs.getString("gender");
                String email1 = rs.getString("email_id");
                String role = rs.getString("role");
                String dob = rs.getString("date_of_birth");
                String username = rs.getString("username");
                String address = rs.getString("full_address");
                String create_time = rs.getString("create_time");

                login_id = id;

                ProfileDetails profile_details = new ProfileDetails(id, phone, user_image,
                        full_name, email1, role, dob, username,
                        gender, address, create_time);

                Main.primaryStage.setUserData(profile_details);

                method.message_label(message_label, "Login Successful ", Color.GREEN);
                main.changeScene("dashboard.fxml", "Dashboard");


            } else {

                method.message_label(message_label, "Invalid email or password !", Color.RED);

            }

        } catch (Exception e) {
            System.out.println("Error : " + e.getMessage());
        } finally {

            try {
                if (null != connection) {

                    connection.close();
                    ps.close();
                    rs.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void create_new_account(ActionEvent actionEvent) {

        main.changeScene("signup.fxml", "Create New Account");

    }
}
