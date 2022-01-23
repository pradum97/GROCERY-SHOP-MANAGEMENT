package com.grocery.management;

import com.grocery.management.Method.Method;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.time.Month;

public class Main extends Application {
    Method method;
    public static Stage primaryStage;

    @Override
    public void start(Stage stage) throws IOException {
        method = new Method();
        primaryStage = stage;
        Parent root = FXMLLoader.load(Main.class.getResource("login.fxml"));
        stage.getIcons().add(new Image(getClass().getResourceAsStream(Method.APPLICATION_ICON)));
        stage.setTitle(Method.APPLICATION_NAME);
        stage.setResizable(true);
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("css/main.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    public void changeScene(String fxml, String title) {

        try {

            if (null != primaryStage && primaryStage.isShowing()) {

                Parent pane = FXMLLoader.load(getClass().getResource(fxml));
                primaryStage.getScene().setRoot(pane);
                primaryStage.setTitle(Method.APPLICATION_NAME + " ( " + title + " ) ");

            }

        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
