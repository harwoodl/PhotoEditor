/*
 * Course: CS1021
 * Winter 2020-21
 * Lab 8 - Photo Editor
 * Name: Luke Harwood
 * Created: 02/05/2021
 * Modified: 02/16/2021
 */

package harwoodl;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    Controller mainController;
    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader mainLoader = new FXMLLoader(getClass().getResource("photoEditor.fxml"));
        Parent root = mainLoader.load();
        mainController = mainLoader.getController();
        primaryStage.setTitle("Photo Editor");
        primaryStage.setScene(new Scene(root));

        primaryStage.setResizable(false);
        primaryStage.show();

    }


    public static void main(String[] args) {
        launch(args);
    }
}
