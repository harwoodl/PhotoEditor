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
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import java.io.*;
import java.nio.file.Path;
import java.util.InputMismatchException;
import java.util.Scanner;

public class ImageIO {



    public Image read(Path path) throws IOException {
        Image image;
        if(getExtension(path).equalsIgnoreCase("msoe")) {
            image = readMSOE(path);
        } else if (getExtension(path).equalsIgnoreCase("bmsoe")) {
            image = readBMSOE(path);
        }else{
            image = ImageUtil.readImage(path);
        }
        return image;
    }

    private Image readBMSOE(Path path) throws IOException{
        WritableImage bmsoeImage;
        File file = new File(String.valueOf(path.toAbsolutePath()));
        FileInputStream inputStream = new FileInputStream(file);
        DataInputStream dataInputStream = new DataInputStream(new BufferedInputStream(inputStream));
        int width = 0;
        int height = 0;
        PixelWriter writer;
        String header = "";
        try {
            for (int i = 0; i < 5; i++) {
                header += (char) inputStream.read();
            }
        }catch(IOException exception){
            throw new IOException("File is empty");
        }
        if (!header.equalsIgnoreCase("bmsoe")){
            throw new IOException("Improper Header information in BMSOE file");
        } else {
            try {
                width = dataInputStream.readInt();
                height = dataInputStream.readInt();
                bmsoeImage = new WritableImage(width, height);
                writer = bmsoeImage.getPixelWriter();
                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        writer.setColor(x, y, intToColor(dataInputStream.readInt()));
                    }
                }
            } catch (IOException exception) {
                exception.printStackTrace();
                throw new IOException("Improper format in Color Section of BMSOE file");
            }
        }
        dataInputStream.close();
        return bmsoeImage;
    }

    public void write(Image image, Path path) throws IOException {
        if(getExtension(path).equalsIgnoreCase("msoe")){
            writeMSOE(image, path);
        } else if (getExtension(path).equalsIgnoreCase("bmsoe")) {
            writeBMSOE(image, path);
        } else {
            ImageUtil.writeImage(path, image);
        }
    }

    private void writeBMSOE(Image image, Path path) throws IOException {
        int height = (int) image.getHeight();
        int width = (int) image.getWidth();
        File file = new File(String.valueOf(path.toAbsolutePath()));
        PixelReader reader = image.getPixelReader();
        FileOutputStream outputStream = new FileOutputStream(file);
        DataOutputStream dataOutputStream = new DataOutputStream(new BufferedOutputStream(outputStream));
        String header = "BMSOE";
        for (int i = 0; i < header.length(); i++) {
            outputStream.write(header.charAt(i));
        }
        dataOutputStream.writeInt(width);
        dataOutputStream.writeInt(height);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                dataOutputStream.writeInt(colorToInt(reader.getColor(x, y)));
            }
        }
        dataOutputStream.close();
    }

    private Image readMSOE(Path path) throws IOException {
        WritableImage msoeImage;
        File file = new File(String.valueOf(path.toAbsolutePath()));
        Scanner reader = new Scanner(file);
        if(!reader.hasNextLine()){
            throw new IOException("File is empty");
        }
        int width = 0;
        int height = 0;
        PixelWriter writer;
        String header = reader.nextLine();
        if (!header.equalsIgnoreCase("MSOE")){
            throw new IOException("Improper Header information in MSOE file");
        } else {
            try {
                width = reader.nextInt();
                height = reader.nextInt();
                msoeImage = new WritableImage(width, height);
                writer = msoeImage.getPixelWriter();
                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        String color = reader.next();
                        writer.setColor(x, y, stringToColor(color));
                    }
                    reader.nextLine();
                }
            } catch (InputMismatchException exception) {
                throw new IOException("Improper format in Color Section of MSOE file");
            }

        }
        reader.close();
        return msoeImage;
    }

    private void writeMSOE(Image image, Path path) throws IOException {
        int height = (int) image.getHeight();
        int width = (int) image.getWidth();
        PrintWriter writer = new PrintWriter(String.valueOf(path.toAbsolutePath()));
        PixelReader reader = image.getPixelReader();
        writer.write("MSOE\n");
        writer.write(width + " ");
        writer.write(height + "\n");
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = reader.getColor(x, y);
                writer.write("#" + colorToString(color)+ " ");
            }
            writer.write("\n");
        }
        writer.close();
    }

    private String getExtension(Path path) {
        String filename = path.toString();
        int dotIndex = filename.lastIndexOf(46);
        if (dotIndex != -1 && dotIndex != filename.length() - 1) {
            return filename.substring(dotIndex + 1).toLowerCase();
        } else {
            throw new IllegalArgumentException("No file extension for " + path);
        }


    }

    private Color stringToColor(String color) throws InputMismatchException {
        color = color.trim();
        if(!color.equalsIgnoreCase("") && (color.charAt(0) == '#') && color.length() == 7) {
            color = color.substring(1, 7);
            return Color.web(color);
        } else {
            throw new InputMismatchException();
        }
    }

    private String colorToString(Color color) {
        String strColor = color.toString();
        String newColor = "";
        for(int i =0; i < strColor.length(); i++){
            char character = strColor.charAt(i);
            if(Character.isLetter(character)){
                character = Character.toUpperCase(character);
            }
            newColor += character;
        }
        return newColor.substring(2,8);
    }

    private static Color intToColor(int color) {
        double red = ((color >> 16) & 0x000000FF)/255.0;
        double green = ((color >> 8) & 0x000000FF)/255.0;
        double blue = (color & 0x000000FF)/255.0;
        double alpha = ((color >> 24) & 0x000000FF)/255.0;
        return new Color(red, green, blue, alpha);
    }

    private static int colorToInt(Color color) {
        int red = ((int)(color.getRed()*255)) & 0x000000FF;
        int green = ((int)(color.getGreen()*255)) & 0x000000FF;
        int blue = ((int)(color.getBlue()*255)) & 0x000000FF;
        int alpha = ((int)(color.getOpacity()*255)) & 0x000000FF;
        return (alpha << 24) + (red << 16) + (green << 8) + blue;
    }



}
