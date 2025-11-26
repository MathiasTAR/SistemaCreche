package com.salo.sistemacreche;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("main-view.fxml"));

        Scene scene = new Scene(fxmlLoader.load(), 1000, 600);

        stage.getIcons().add(new Image("/com/salo/sistemacreche/icons/logoPreenchida.png"));
        stage.setTitle("Sistema Creche");
        stage.setScene(scene);
        stage.show();
    }


    public static void main(String[] args) {
        launch();
    }
}
