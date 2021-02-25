package com.rosscon.llce.gui;

import com.rosscon.llce.components.graphics.NES2C02.NES2C02Constants;
import com.rosscon.llce.computers.nintendo.NES;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;

import java.net.URL;
import java.util.ResourceBundle;

public class ImageController implements Initializable {

    @FXML
    private ImageView imageView;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        int screenScaling = 1;

        WritableImage image = new WritableImage(256 * screenScaling, 240 * screenScaling);
        imageView.setImage(image);

        new AnimationTimer() {

            NES console;
            {
                try {
                    console = new NES();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void handle(long l) {
                int[] screenBuffer = console.getScreenBuffer();
                for (int x = 0; x < NES2C02Constants.WIDTH_VISIBLE_PIXELS; x++){
                    for (int y = 0; y < NES2C02Constants.HEIGHT_VISIBLE_SCANLINES; y++){

                        // TODO find a fast scaling algorithm
                        //for (int dx = x * screenScaling; dx < (x * screenScaling) + screenScaling; dx++){
                        //    for (int dy = y * screenScaling; dy < (y * screenScaling) + screenScaling; dy++){
                        //        image.getPixelWriter().setArgb(dx, dy, screenBuffer[(y * NES2C02Constants.WIDTH_VISIBLE_PIXELS) + x]);
                        //    }
                        //}

                        image.getPixelWriter().setArgb(x, y, screenBuffer[(y * NES2C02Constants.WIDTH_VISIBLE_PIXELS) + x]);
                    }
                }
            }
        }.start();

    }
}
