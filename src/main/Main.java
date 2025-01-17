package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        //ScalableContentPane scale = new ScalableContentPane();
        Parent root = FXMLLoader.load(getClass().getResource("/resources/fxml/Login.fxml"));
        //scale.setContent(root);
        root.requestFocus();
        primaryStage.setTitle("Control de Horarios Palobiofarma S.L & Medibiofarma");
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/resources/images/palobiofarma.png")));
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setScene(new Scene(root));
        primaryStage.show();


    }


    public static void main(String[] args) {
        launch(args);
    }
}
