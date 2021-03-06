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

    private NES console;

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(NESGUI.class.getResource("/NES.fxml"));
        Parent root = loader.load();
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

        console = new NES();

        NESGraphicsController nesGraphicsController = (NESGraphicsController)loader.getController();
        nesGraphicsController.setGpu(console.getGpu());

        primaryStage.getScene().setOnKeyPressed(console.getKeyPressHandler());
        primaryStage.getScene().setOnKeyReleased(console.getKeyReleaseHandler());

        //NES console = new NES();

        /*new Thread(() -> {
            try {
                while(true) {
                    Thread.sleep(10000);
                    System.out.println("\n");
                    System.out.println("Used Memory: " + ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1000000.0) + "MB");
                    System.out.println("Free Memory: " + (Runtime.getRuntime().freeMemory() / 1000000.0) + "MB");
                    System.out.println("Total Memory: " + (Runtime.getRuntime().totalMemory() / 1000000.0) + "MB");

                    System.gc();
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();*/
    }

    public static void main(String[] args){
        launch(args);
    }
}
