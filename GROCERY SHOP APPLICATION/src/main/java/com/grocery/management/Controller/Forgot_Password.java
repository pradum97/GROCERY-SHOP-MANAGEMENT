package com.grocery.management.Controller;

import com.grocery.management.Dialog;
import com.grocery.management.Method.Method;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.ResourceBundle;

public class Forgot_Password implements Initializable {

    public TextField email_f;
    public VBox verification_container;
    public VBox password_container;
    public Button submit_bn;
    public TextField confirm_password;
    public TextField new_password;
    public Label error_label;
    private Connection connection;
    private Method method;
    private PreparedStatement ps;
    private Properties properties;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        method = new Method();
        password_container.setVisible(false);
        password_container.managedProperty().bind(password_container.visibleProperty());
        properties = new Method().properties("query.properties");

    }

    public void forgetPassword_bn(ActionEvent actionEvent) {

        String email = email_f.getText();
        String newPassword = new_password.getText();
        String con_Password = confirm_password.getText();

        if (newPassword.isEmpty()) {
            method.show_popup("Enter New Password", new_password);
            return;
        } else if (con_Password.isEmpty()) {
            method.show_popup("Enter Confirm Password", confirm_password);
            return;
        } else if (!newPassword.equals(con_Password)) {
            method.show_popup("Confirm Password do not match", confirm_password);
            return;
        }

        if (null == properties) {
            System.out.println("invalid properties");
            return;
        }


        try {

            ps = connection.prepareStatement(properties.getProperty("updatePassword"));
            ps.setString(1, con_Password);
            ps.setString(2, email);
            int update_result = ps.executeUpdate();

            if (update_result > 0) {
                email_f.setText("");
                new_password.setText("");
                confirm_password.setText("");
                error_label.setVisible(true);
                error_label.setTextFill(Color.GREEN);
                method.message_label(error_label, "Successfully Updated ", Color.GREEN);


                Stage stage = Dialog.stage;

                email_f.setText("");
                new_password.setText("");
                confirm_password.setText("");


                if (null == stage) {
                    return;
                }

                if (stage.isShowing()) {

                    stage.close();
                    new Dialog().show_alertBox(Method.APPLICATION_NAME, "Password Successfully Updated");
                }

            }


        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    public void submit(ActionEvent actionEvent) {

        String email = email_f.getText();

        if (email.isEmpty()) {
            method.show_popup("Please enter valid username or email", email_f);
            return;
        }
        try {
            String query = "SELECT email_id FROM users WHERE email_id = ?";
            connection = method.connection();
            ps = connection.prepareStatement(query);
            ps.setString(1, email);
            ResultSet resultSet = ps.executeQuery();

            if (resultSet.next()) {
                error_label.setVisible(false);
                verification_container.setVisible(false);
                verification_container.managedProperty().bind(verification_container.visibleProperty());
                password_container.setVisible(true);

            } else {
                method.message_label(error_label, "Invalid Email !", Color.RED);
            }

        } catch (Exception e) {
            System.out.println("Error : " + e.getMessage());
        }

    }
}
