package com.grocery.management;

import com.grocery.management.Method.Method;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class Dialog {

    public static Stage stage;

    public Parent show_fxml_dialog(String fxml_file, String title)  {

        try {
            Parent  parent = FXMLLoader.load(getClass().getResource(fxml_file));
            stage = new Stage();
            stage.getIcons().add(new Image(getClass().getResourceAsStream("drawable/App_Icon.png")));
            stage.initStyle(StageStyle.UTILITY);
            stage.setTitle(Method.APPLICATION_NAME + " ( " + title + " ) ");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(parent));
            stage.setResizable(false);
            stage.showAndWait();
            return parent;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }

    public Alert show_alertBox(String title, String message) {


        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(message);
        alert.showAndWait();

        return alert;
    }
}
