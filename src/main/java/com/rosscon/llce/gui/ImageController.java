package com.rosscon.llce.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

import java.net.URL;
import java.util.ResourceBundle;

public class ImageController implements Initializable {

    @FXML
    private ImageView imageView;

    public static PixelWriter pixelWriter;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        WritableImage image = new WritableImage(256, 240);
        imageView.setImage(image);
        this.pixelWriter = image.getPixelWriter();
    }
}
