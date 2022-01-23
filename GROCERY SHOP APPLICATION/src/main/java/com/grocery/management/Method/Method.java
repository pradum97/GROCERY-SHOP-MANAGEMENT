package com.grocery.management.Method;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.io.*;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class Method {

    public static final String APPLICATION_ICON = "drawable/App_Icon.png";
    public static final String APPLICATION_NAME = "Grocery Shop";
   public ContextMenu form_Validator ;

    public Connection connection() {

        Properties properties = properties("query.properties");

        String DB_URL = properties.getProperty("DB_URL");
        String DB_USERNAME = properties.getProperty("DB_USERNAME");
        String DB_PASSWORD = properties.getProperty("DB_PASSWORD");

        try {

            Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            return connection;

        } catch (SQLException e) {
            e.getStackTrace();
            return null;
        }

    }
    public Label message_label(Label label, String message, Color color){

        label.setText(message);
        label.setTextFill(color);
        label.setVisible(true);
        Timer timer = new Timer();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (label.isVisible()) {

                    label.setVisible(false);
                }
            }
        }, 5000);


        return label;
    }

    public Image getImage(String path){

        try {

            File file = new File(path);
           InputStream is = new FileInputStream(file.getAbsolutePath());


           return new Image(is);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ContextMenu show_popup(String message, Object textField){

        form_Validator = new ContextMenu();
        form_Validator.setAutoHide(true);
        form_Validator.getItems().add(new MenuItem(message));
        form_Validator.show((Node) textField, Side.RIGHT, 10, 0);

        return form_Validator;

    }

    public  ObservableList<Object> getLimit(){

        return FXCollections.observableArrayList(20,50,100,150,200,300,500,700,1000,"Lifetime");
    }

    public Properties properties(String filename){

        try {
            File file = new File("src/main/java/com/grocery/management/Properties/" + filename);
            FileInputStream fileInputStream = new FileInputStream(file.getAbsolutePath());
            Properties prop = new Properties();
            prop.load(fileInputStream);
            return prop;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public String get_mac_address() {

        InetAddress ip;
        StringBuilder sb;
        try {

            ip = InetAddress.getLocalHost();
            NetworkInterface network = NetworkInterface.getByInetAddress(ip);

            byte[] mac = network.getHardwareAddress();

            sb = new StringBuilder();
            for (int i = 0; i < mac.length; i++) {
                sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;

        }
    }
    public  ObservableList<String> getGender(){

        return FXCollections.observableArrayList("Male", "Female", "Other");
    }

    public  ObservableList<String> getRole() {

        return FXCollections.observableArrayList("ADMIN", "MANAGER",
                "ASSISTANT MANAGER", "SELLER", "STOCKIST");
    }
    public  ObservableList<String> getUnit() {

        return FXCollections.observableArrayList("GRAM", "KG", "LITRE","ML","METER","CM","PACKET");
    }
    public String copyImage(String filePath, String fileName) {

        Random random = new Random();
        int num = random.nextInt(1, 1000);
        fileName = fileName.concat(String.valueOf(num));

        String newPath = "src/main/resources/Product_Image/";
        File file_dir = new File(newPath);

        if (!file_dir.exists()) {
            if (file_dir.mkdirs()) {
                System.out.println("File Successfully Created");
            }
        }
        String extension = filePath.substring(filePath.lastIndexOf('.') + 1);
        File sourceFile = new File(filePath);
        File destinationFile = new File(newPath + fileName + "." + extension);
        try {

            Path path = Files.copy(sourceFile.toPath(), destinationFile.toPath());
            return path.toFile().getAbsolutePath();

        } catch (IOException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
}
