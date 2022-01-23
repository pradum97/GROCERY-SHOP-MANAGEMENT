package com.grocery.management;

import com.grocery.management.Method.Method;
import com.grocery.management.Model.ProfileDetails;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Dashboard implements Initializable {


    @FXML
    public StackPane contentArea;
    public HBox hb_home, hb_myProfile, hb_myProduct, hb_sell, hb_viewUsers;

    public ImageView user_image;
    public Label fullname;
    public Label role;
    Connection connection;
    Main main;
    Method method;
    Stage stage;
    List<ProfileDetails> profile_result = new ArrayList<>();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        connection = new Method().connection();

        main = new Main();
        method = new Method();
        stage = Main.primaryStage;
        profile_result.add((ProfileDetails) stage.getUserData());

        stage.setMaximized(true);

        replace_scene("home1.fxml");
        hb_home.setStyle("-fx-background-color: black; -fx-cursor: hand;-fx-padding: 5 10 5 10;" +
                "-fx-border-radius: 5;" +
                "-fx-background-radius: 5");

            setProfileDetalis();

    }

    private void setProfileDetalis() {
        ProfileDetails p_result = profile_result.get(0);

        if (null == p_result){
            System.out.println("Profile Result null");
            return;
        }
        fullname.setText(p_result.getFull_name());
        role.setText(p_result.getRole());
        try {
            InputStream is = p_result.getUser_image().getBinaryStream();
            Image image = new Image(is);
            user_image.setFitWidth(60);
            user_image.setFitHeight(60);

            user_image.setStyle("-fx-border-radius: 500");
            user_image.setPreserveRatio(true);
            user_image.setImage(image);


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void home_bn(ActionEvent actionEvent) {

        replace_scene("home1.fxml");
        hb_home.setStyle("-fx-background-color: black; -fx-cursor: hand;-fx-padding: 5 10 5 10;" +
                "-fx-border-radius: 5;" +
                "-fx-background-radius: 5");

        hb_myProfile.setStyle(null);
        hb_myProduct.setStyle(null);
        hb_sell.setStyle(null);
        hb_viewUsers.setStyle(null);
        


    }

    public void myProfile_bn(ActionEvent actionEvent) {

        Main.primaryStage.setUserData(profile_result.get(0).getId());

        replace_scene("Dashboard/my_profile.fxml");
        hb_myProfile.setStyle("-fx-background-color: black; -fx-cursor: hand;-fx-padding: 5 10 5 10;" +
                "-fx-border-radius: 5;" +
                "-fx-background-radius: 5");

        hb_home.setStyle(null);
        hb_myProduct.setStyle(null);
        hb_sell.setStyle(null);
        hb_viewUsers.setStyle(null);

    }

    public void myProduct_bn(ActionEvent actionEvent) {

        hb_myProduct.setStyle("-fx-background-color: black; -fx-cursor: hand;-fx-padding: 5 7 5 10;" +
                "-fx-border-radius: 5;" +
                "-fx-background-radius: 5");

        hb_home.setStyle(null);
        hb_myProfile.setStyle(null);
        hb_sell.setStyle(null);
        hb_viewUsers.setStyle(null);
        

        replace_scene("Dashboard/my_product.fxml");


    }

    public void sellProduct_bn(ActionEvent actionEvent) {
        replace_scene("Dashboard/sell_product.fxml");
        hb_sell.setStyle("-fx-background-color: black; -fx-cursor: hand;-fx-padding: 5 7 5 10;" +
                "-fx-border-radius: 5;" +
                "-fx-background-radius: 5");

        hb_home.setStyle(null);
        hb_myProduct.setStyle(null);
        hb_myProfile.setStyle(null);
        hb_viewUsers.setStyle(null);
    }

    public void viewUsers(ActionEvent actionEvent) {

        replace_scene("Dashboard/view_user.fxml");
        hb_viewUsers.setStyle("-fx-background-color: black; -fx-cursor: hand;-fx-padding: 5 7 5 10;" +
                "-fx-border-radius: 5;" +
                "-fx-background-radius: 5");
        hb_home.setStyle(null);
        hb_myProduct.setStyle(null);
        hb_myProfile.setStyle(null);
        hb_sell.setStyle(null);
    }


    public void logout(MouseEvent actionEvent) {

        main.changeScene("login.fxml", "Login Here");
    }

    public void replace_scene(String fxml_file_name) {

        try {
            Parent parent = FXMLLoader.load(getClass().getResource(fxml_file_name));
            contentArea.getChildren().removeAll();
            contentArea.getChildren().setAll(parent);
        } catch (IOException e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, e);
        }

    }

    public void addProduct(ActionEvent event) {

        Dialog dialog = new Dialog();

        Main.primaryStage.setUserData(profile_result.get(0));
        dialog.show_fxml_dialog("Dashboard/add_products.fxml", "Add New Products");
    }

}

