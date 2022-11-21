
/**
 * 
 * The ReadImage reads each image and calculate the following two values using its RGB value
 * (a) 25 Intensity histogram values
 * (b) 64 Color Code histogram values 
 * 
 * @author Divya Kamath
 */

import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Paths;

import javax.imageio.ImageIO;

public class ReadImage {

    private int imageCount = 1;

    private int intensityBins[] = new int[26];
    private int intensityMatrix[][] = new int[101][26];

    private int colorCodeBins[] = new int[65];
    private int colorCodeMatrix[][] = new int[101][65];

    /**
     * constructor
     * 
     * pre: none
     * 
     * post: calls the ExtractImageValues method
     */
    ReadImage() {
        // tk = this.getDefaultToolkit();
        extractImageValues();
    }

    /**
     * ExtractImageValues method is responsible for the following
     * (a) To read each image and call the getPixelValue method to extract the RGB
     * value of the image
     * (b) To call the writeIntensity and writeColorCode method to write the
     * histogram values in text file
     * 
     * pre: none
     * 
     * post: ColorCode.txt and Intensity.txt files are generated
     */

    private void extractImageValues() {
        while (imageCount < 101) {
            try {
                java.net.URL ImgUrl = this.getClass().getResource("images/" + imageCount + ".jpg");
                BufferedImage image = ImageIO.read(ImgUrl);
                getPixelValues(image, image.getHeight(), image.getWidth());
                imageCount++;
            } catch (IOException e) {
                System.out.println("Error occurred when reading the image file.");
            }
        }

        writeIntensity();
        writeColorCode();
    }

    /**
     * getPixelValues method is responsible to read the pixel values of each image
     * and call getIntensity and getColorCode method to calculate the respective
     * histogram values.
     * 
     * pre: image and its height and width
     * 
     * post: intenistyMatrix and colorCodeMatrix are populated with histogram values
     * of the image at the index that is the same as image number.
     * 
     */

    private void getPixelValues(BufferedImage image, int height, int width) {
        int[] pixel;
        for (int i = 0; i < 26; i++) {
            intensityBins[i] = 0;
        }

        for (int i = 0; i < 65; i++) {
            colorCodeBins[i] = 0;
        }

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                pixel = image.getRaster().getPixel(x, y, new int[3]);
                getIntensity(pixel);
                getColorCode(pixel);
            }
        }

        for (int i = 1; i < 26; i++) {
            intensityMatrix[imageCount][i] = intensityBins[i];
        }

        for (int i = 1; i < 65; i++) {
            colorCodeMatrix[imageCount][i] = colorCodeBins[i];
        }

    }

    /**
     * getIntensity method calculates the Intensity of a pixel using the formula
     * I = 0.299R + 0.587G + 0.114B and increases the count of the respective bin.
     * 
     * pre: calculated R G B value of the pixel
     * 
     * post: count of respecitve bin in the intensityBin array is increased by 1
     */

    private void getIntensity(int[] pixel) {

        Double intensityDouble = (0.299 * pixel[0]) + (0.587 * pixel[1]) + (0.114 * pixel[2]);
        int intensity = (int) Math.floor(intensityDouble);
        intensity = (int) Math.floor(intensity / 10);
        if (intensity >= 24) {
            intensityBins[25] += 1;
        } else {
            intensityBins[intensity + 1] += 1;
        }

    }

    /**
     * getColorCode method calculates the 6bit color code of a pixel using the most
     * significant 2 bits of each of the three color components, and increases the
     * count of the respective bin.
     * 
     * pre: calculated R G B value of the pixel
     * 
     * post: count of respecitve bin in the colorCodeBin array is increased by 1
     */

    private void getColorCode(int[] pixel) {
        String sixBitColor = "";
        for (int i = 0; i < 3; i++) {
            String color = Integer.toBinaryString(pixel[i]);
            color = String.format("%08d%n", Integer.valueOf(color));
            sixBitColor += color.charAt(0);
            sixBitColor += color.charAt(1);
        }
        int colorCode = Integer.parseInt(sixBitColor, 2);
        colorCodeBins[colorCode + 1] += 1;
    }

    /**
     * writeColorCode method is responsible to write the colorCodeMatrix values in a
     * ColorCode text file
     * 
     * pre: colorCodeMatrix is populated with count of 64 histogram bin values of
     * each image
     * in database
     * 
     * post: ColorCode.txt file is saved in the cwd
     */

    private void writeColorCode() {
        writeFile(colorCodeMatrix, "ColorCode.txt");

    }

    /**
     * writeIntensity method is responsible to write the intensityMatrix values in a
     * Intensity text file
     * 
     * pre: IntensityMatrix is populated with count of 25 histogram bin values of
     * each image
     * in database
     * 
     * post: Intensity.txt file is saved in the cwd
     */

    private void writeIntensity() {
        writeFile(intensityMatrix, "Intensity.txt");
    }

    /**
     * writeFile method is responsible to write the values of a matrix in a
     * text file
     * 
     * pre: matrix that contains the values and text fileName to write the values
     * 
     * post: file with filename is saved in the cwd
     * 
     */

    private void writeFile(int[][] matrix, String fileName) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                builder.append(matrix[i][j] + "");// append to the output string
                if (j < matrix.length - 1)// if this is not the last row element
                    builder.append(",");
            }
            builder.append("\n");// append new line at the end of the row
        }
        BufferedWriter writer = null;
        try {
            File file = new File(
                    Paths.get("").toAbsolutePath().toString().replace("\\", "/") + "/" + fileName);
            if (file.exists()) {
                file.delete();
            }

            writer = new BufferedWriter(
                    new FileWriter(
                            Paths.get("").toAbsolutePath().toString().replace("\\", "/") + "/" + fileName));
        } catch (IOException e) {
            System.out.println("Unble to write into " + fileName + "file");
            e.printStackTrace();
        }
        try {
            writer.write(builder.toString());
        } catch (IOException e) {
            System.out.println("Error occurred while saving the string representation of the " + fileName + " matrix");
            e.printStackTrace();
        } // save the string representation of the board
        try {
            writer.close();
        } catch (IOException e) {
            System.out.println("Error occured while closing the writer");
            e.printStackTrace();
        }
    }

}
