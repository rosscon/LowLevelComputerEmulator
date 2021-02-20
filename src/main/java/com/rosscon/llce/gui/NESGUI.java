package com.rosscon.llce.gui;

import com.rosscon.llce.computers.nintendo.NES;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class NESGUI extends Application {


    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(NESGUI.class.getResource("/NES.fxml"));
        primaryStage.setTitle("NES");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.setMaxHeight(790);
        primaryStage.setMinHeight(790);
        primaryStage.setMinWidth(1024);
        primaryStage.setMaxWidth(1500);
        primaryStage.setWidth(1500);
        primaryStage.show();

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                System.exit(0);
            }
        });

        NES console = new NES(ImageController.pixelWriter);
    }

    public static void main(String[] args){
        launch(args);
    }
}
