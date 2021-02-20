package com.rosscon.llce.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;

public class ImageController implements Initializable {

    @FXML
    private ImageView imageView;

    public static PixelWriter pixelWriter;

    Random rand;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        WritableImage image = new WritableImage(256, 240);
        imageView.setImage(image);
        this.pixelWriter = image.getPixelWriter();

        rand = new Random();

        for (int x = 0; x < 256; x ++){
            for (int y= 0; y < 240; y++){
                int c = rand.nextInt(Integer.MAX_VALUE) | 0xFF000000;
                this.pixelWriter.setArgb(x, y, c);
            }
        }
    }
}
