package asem.uniproject3;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class HelloApplication extends Application {

    @Override
    public void start(Stage stage) {
        UI ui = new UI();
        Scene scene = ui.createScene(stage);
        stage.setTitle("Notation Converter");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
