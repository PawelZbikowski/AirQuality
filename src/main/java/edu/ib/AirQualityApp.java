package edu.ib;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class AirQualityApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/AirQualityApp.fxml"));
        //Tworzenie odslony - sceny
        Scene scene = new Scene(root, 1200, 900);
        stage.setScene(scene);
        stage.setTitle("AirQualityApp");
        stage.show();
    }
}
