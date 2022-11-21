import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;

public class ReadTextFile {
    ReadTextFile() {
    }

    /**
     * readFile method opens the given text file from the pwd containing the matrix
     * with the histogram bin values of each image.
     * 
     * pre: (a) ColorCodes.txt and/or Intensity.txt file is loaded
     * (b) colorCodeMatrix and/or intensityMatrix is instantiated
     * 
     * post: The contents of the file are read and stored in the two
     * dimensional array passed.
     * 
     */

    public int[][] readFile(int[][] matrix, String fileName) {
        try {

            BufferedReader reader = new BufferedReader(
                    new FileReader(Paths.get("").toAbsolutePath().toString().replace("\\", "/") + "/" + fileName));
            String line = "";
            int row = 0;
            try {
                while ((line = reader.readLine()) != null) {
                    String[] cols = line.split(",");
                    int col = 0;
                    for (String c : cols) {
                        matrix[row][col] = Integer.parseInt(c);
                        col++;
                    }
                    row++;
                }

            } catch (NumberFormatException | IOException e) {
                System.out.println("Error while reading the line");
                e.printStackTrace();
            }
            try {
                reader.close();
            } catch (IOException e) {
                System.out.println("Error during closing the reader");
                e.printStackTrace();
            }

        } catch (FileNotFoundException e) {
            System.out.println("The file " + fileName + " does not exist");
        }

        return matrix;
    }
}
