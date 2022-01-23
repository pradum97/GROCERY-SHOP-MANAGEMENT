package com.grocery.management.Controller;

import com.grocery.management.Dialog;
import com.grocery.management.Main;
import com.grocery.management.Method.Method;
import com.grocery.management.Model.Customer;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class CustomerDetails implements Initializable {
    public TextField c_name;
    public TextField c_phone;
    public TextField c_address;
    Dialog dialog;
    Method method;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        method = new Method();
        dialog = new Dialog();
    }

    public void submit_bn(ActionEvent event) {

        String name = c_name.getText();
        String phone = c_phone.getText();
        String address = c_address.getText();

        if (name.isEmpty()){
            method.show_popup("Enter customer name",c_name);
            return;
        }else if (phone.isEmpty()){
            method.show_popup("Enter customer Phone",c_phone);
            return;
        } if (address.isEmpty()){
            method.show_popup("Enter Address",c_address);
            return;
        }

        Customer customer = new Customer(name,address,Long.parseLong(phone));
        Main.primaryStage.setUserData(customer);

        Stage stage =  Dialog.stage;

        if (stage.isShowing()){
            stage.close();
        }

        if (stage.isShowing()){

            stage.close();
        }
    }

    public void cancel_Bn(ActionEvent event) {

     Stage stage =  Dialog.stage;
     if (stage.isShowing()){

         stage.close();
     }

    }
}
