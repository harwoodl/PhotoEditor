/*
 * Course: CS1021
 * Winter 2020-21
 * Lab 8 - Photo Editor
 * Name: Luke Harwood
 * Created: 02/05/2021
 * Modified: 02/16/2021
 */
package harwoodl;

import edu.msoe.cs1021.ImageUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.image.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Controller {
        private static WritableImage newImage;
        private static Image currentImage;
        private Image image = null;
        private ImageIO imageIO;
        private static final double WINDOW_HEIGHT = 707;
        private static final double WINDOW_WIDTH = 1110;
        private static int changeCount = 0;
        private static ArrayList<Image> undoList = new ArrayList<>();
        private static final int BUTTON_HEIGHT = 48;
        private static final int BUTTON_WIDTH = 126;
        private static final double SCALE_FACTOR = 1.2;

        @FXML
        Button openButton;
        @FXML
        Button saveButton;
        @FXML
        Button grayscaleButton;
        @FXML
        Button negativeButton;
        @FXML
        Button reloadButton;
        @FXML
        ImageView imageView;
        @FXML
        Button saturateButton;
        @FXML
        Button desaturateButton;
        @FXML
        Button addButton;
        @FXML
        Button minusButton;
        @FXML
        Label brightnessLabel;

        @FXML
        ColorPicker colorPicker;
        @FXML
        Button filterButton;
        @FXML
        Button undoButton;
        @FXML
        Button sharpenButton;
        @FXML
        Button blurButton;
        @FXML
        Button redButton;
        @FXML
        Button redGrayButton;
        @FXML
        Label saveLabel;
        @FXML
        Label statusLabel;

        //Created my own colorTo methods because I preferred their functionality
        public void negative(ActionEvent event) {
            displayImage(transformImage(currentImage, (y, color) -> colorToNegative(color)));

        }
        public void grayscale(ActionEvent event) {
            displayImage(transformImage(currentImage, (y, color) -> colorToGrayscale(color)));
        }
        public void saturate(ActionEvent event) {
            displayImage(transformImage(currentImage, (y, color) -> colorToSaturate(color)));

        }

        public void desaturate(ActionEvent event) {
            displayImage(transformImage(currentImage, (y, color) -> colorToDesaturate(color)));
        }

        public void brighten(ActionEvent event) {
            displayImage(transformImage(currentImage, (y, color) -> increaseBrightness(color)));
        }

        public void unbrighten(ActionEvent event) {
            Transformable transform = (y, color) -> decreaseBrightness(color);
            displayImage(transformImage(currentImage, transform));
        }

        public void redGray(ActionEvent event) {
            displayImage(transformImage(currentImage, (y, color) -> colorToRedGray(y, color)));
        }

        public void red(ActionEvent event) {
            displayImage(transformImage(currentImage, (y, color) -> colorToRed(color)));
        }


        public void reload(ActionEvent event) {
            imageView.setImage(image);
            undoList.add(currentImage);
            changeCount++;
            currentImage = image;
        }

        public void save(ActionEvent event) {
            try {
                FileChooser fileChooser = new FileChooser();
                imageIO = new ImageIO();
                fileChooser.setTitle("Select Folder to Save File");
                fileChooser.getExtensionFilters().addAll(
                        new FileChooser.ExtensionFilter("PNG Files", "*.png"),
                        new FileChooser.ExtensionFilter("JPG Files", "*.jpg"),
                        new FileChooser.ExtensionFilter("MSOE Files", "*.msoe"),
                        new FileChooser.ExtensionFilter("BMSOE Files", "*.bmsoe"));
                fileChooser.setInitialDirectory(new File("C:\\Users\\harwoodl\\Pictures\\Camera Roll"));
                File saveFile = fileChooser.showSaveDialog(null);
                if(saveFile != null) {
                    imageIO.write(currentImage, saveFile.toPath());
                    if(saveFile.exists()){
                        saveLabel.setText("***File Saved***");
                        saveLabel.setVisible(true);
                    }
                }
            } catch(IOException exception) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Cannot save file.");
                alert.showAndWait();
            }
        }

        public void open(ActionEvent event) {
            undoList.clear();
            changeCount = 0;
            try {
                FileChooser fileChooser = new FileChooser();
                imageIO = new ImageIO();
                fileChooser.setTitle("Open Image File");
                fileChooser.getExtensionFilters().addAll(
                        new FileChooser.ExtensionFilter("Picture Files", "*.png", "*.jpg", "*.msoe", "*.bmsoe"));
                fileChooser.setInitialDirectory(new File("C:\\Users\\harwoodl\\Pictures\\Camera Roll"));
                File file = fileChooser.showOpenDialog(null);
                if (file != null) {
                    image = imageIO.read(file.toPath());
                    imageView.setImage(image);
                    if(image != null) {
                        statusLabel.setVisible(false);
                        saveLabel.setVisible(false);
                        undoButton.setDisable(false);
                        colorPicker.setDisable(false);
                        filterButton.setDisable(false);
                        saveButton.setDisable(false);
                        reloadButton.setDisable(false);
                        negativeButton.setDisable(false);
                        grayscaleButton.setDisable(false);
                        saturateButton.setDisable(false);
                        desaturateButton.setDisable(false);
                        sharpenButton.setDisable(false);
                        blurButton.setDisable(false);
                        redButton.setDisable(false);
                        redGrayButton.setDisable(false);
                        addButton.setDisable(false);
                        minusButton.setDisable(false);
                        brightnessLabel.setOpacity(1);
                        currentImage = image;
                    }
                }
            } catch (IOException exception) {
                Alert alert = new Alert(Alert.AlertType.ERROR, exception.getMessage());
                alert.showAndWait();
            }
        }


        private Color colorToNegative(Color currentColor){
            double newRed = 1 - currentColor.getRed();
            double newGreen = 1 - currentColor.getGreen();
            double newBlue = 1 - currentColor.getBlue();
            return new Color(newRed, newGreen, newBlue, currentColor.getOpacity());
        }

        private Color colorToGrayscale(Color currentColor){
            double grayscale = (currentColor.getRed() * (0.2126)) + (currentColor.getGreen() * (0.7152))
                     + (currentColor.getBlue() * (0.0722));
            return new Color(grayscale, grayscale, grayscale, currentColor.getOpacity());
        }

        private Color increaseBrightness(Color color){
            double newRed = truncate(color.getRed() + .01);
            double newGreen = truncate(color.getGreen() + .01);
            double newBlue = truncate(color.getBlue() + .01);
            return new Color(newRed, newGreen, newBlue, color.getOpacity());
        }
        private Color decreaseBrightness(Color color){
            double newRed = truncate(color.getRed() - .01);
            double newGreen = truncate(color.getGreen() - .01);
            double newBlue = truncate(color.getBlue() - .01);
            return new Color(newRed, newGreen, newBlue, color.getOpacity());
        }

        private Color filterToColor(Color currentColor, Color newColor){
            double newRed = currentColor.getRed() * newColor.getRed();
            double newGreen = currentColor.getGreen() * newColor.getGreen();
            double newBlue = currentColor.getBlue() * newColor.getBlue();
            return new Color(newRed, newGreen, newBlue, currentColor.getOpacity());
        }


        private Color colorToSaturate(Color color){
            return color.saturate();
        }
        private Color colorToDesaturate(Color color){
            return color.desaturate();
        }

        private Color colorToRed(Color color) {
            return new Color(color.getRed(), 0, 0, color.getOpacity());
        }

        private Color colorToRedGray(Double y, Color color) {
            boolean oddNumber = (y%2) == 1;
            Color newColor;
            if(oddNumber){
                newColor = color.grayscale();
            } else {
                newColor = colorToRed(color);
            }
            return newColor;
        }

        private double truncate(Double color) {
            if(color > 1) {
                color = 1.0;
            } else if (color < 0){
                color = 0.0;
            }
            return color;
        }

        public void convertToColor(ActionEvent event) {
            Color newColor = colorPicker.getValue();
            PixelReader reader = currentImage.getPixelReader();
            newImage = new WritableImage((int)currentImage.getWidth(), (int)currentImage.getHeight());
            PixelWriter pixelWriter = newImage.getPixelWriter();
            for(int y = 0; y < currentImage.getHeight(); y++){
                for(int x = 0; x < currentImage.getWidth(); x++) {
                    pixelWriter.setColor(x, y, filterToColor(reader.getColor(x, y), newColor));
                }
            }
            displayImage(newImage);
        }

        public void undo(ActionEvent event) {
            if(changeCount > 0) {
                currentImage = undoList.get(changeCount - 1);
                undoList.remove(currentImage);
                changeCount--;
                imageView.setImage(currentImage);
            }

        }


        private static Image transformImage(Image image, Transformable transform) {
            PixelReader reader = image.getPixelReader();
            newImage = new WritableImage((int)currentImage.getWidth(), (int)currentImage.getHeight());
            PixelWriter pixelWriter = newImage.getPixelWriter();
            for(int y = 0; y < currentImage.getHeight(); y++){
                for(int x = 0; x < currentImage.getWidth(); x++) {
                    pixelWriter.setColor(x, y, transform.apply(y, reader.getColor(x, y)));
                }
            }
            return newImage;
        }

        public Image getCurrentImage(){
            return currentImage;
        }
        public void displayImage(Image newImage){
            imageView.setImage(newImage);
            undoList.add(currentImage);
            changeCount++;
            currentImage = newImage;
            saveLabel.setVisible(true);
            statusLabel.setVisible(true);
            saveLabel.setText("Unsaved Changes");
        }


    public void enlarge(MouseEvent event) {
        Button button =  (Button)event.getSource();
        double height = button.getPrefHeight();
        double width = button.getPrefWidth();
        button.setPrefSize(width * SCALE_FACTOR, height * SCALE_FACTOR);
    }

    public void shrink(MouseEvent event) {
        Button button = (Button)event.getSource();
        double height = button.getPrefHeight();
        double width = button.getPrefWidth();
        button.setPrefSize(width/SCALE_FACTOR, height/SCALE_FACTOR);
    }

    public void blur(ActionEvent event) {
        Image newImage = null;
        double[] blurValues = { 0.0,  1.0/9,  0.0,
                1.0/9, 5.0/9, 1.0/9,
                0.0,  1.0/9,  0.0};
        for(int i = 0; i < 10; ++i) {
            newImage = ImageUtil.convolve(currentImage, blurValues);
        }
        displayImage(newImage);
    }

    public void sharpen(ActionEvent event) {
        final double[] sharpenValues = { 0.0, -1.0,  0.0,
                -1.0,  5.0, -1.0,
                0.0, -1.0,  0.0};
        Image newImage = ImageUtil.convolve(currentImage, sharpenValues);
        displayImage(newImage);
    }
}
