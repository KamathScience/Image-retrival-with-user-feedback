
/**
 * The CalculateUtil class contains all the methods that calculate certain values. 
 * It is responsible for instantiating the priority queue sortedDistance.
 * 
 * @author Divya Kamath
 */

import java.util.Comparator;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

/**
 * The DistanceValue Class object is designed to hold two values
 * (a) index of an image
 * (b) Manhattan distance of the image with the selected image
 * 
 */
class DistanceValue {

    private final int imageIndex;
    private final double distance;

    DistanceValue(double distance, int index) {
        this.distance = distance;
        this.imageIndex = index;
    }

    public double getDistance() {
        return distance;
    }

    public int getImageIndex() {
        return imageIndex;
    }
}

/**
 * constructor
 * 
 * pre: none
 * 
 * post: instantiates sortedDistance
 * 
 * 
 */

public class CalculateUtil {

    PriorityQueue<DistanceValue> sortedDistance;

    CalculateUtil() {
        sortedDistance = new PriorityQueue<DistanceValue>(100,
                new Comparator<DistanceValue>() {
                    @Override
                    public int compare(DistanceValue o1, DistanceValue o2) {
                        return Double.compare(o1.getDistance(), o2.getDistance());
                    }
                });
    }

    /**
     * calculateManhattanDistance method calculates the manhattan distance bteween
     * the
     * selected image and every other image based on the matrix passed
     * 
     * pre: colrCodeMatrix and/or intensityMatrix is populated with values
     * 
     * post: Following changes are made after calculating manhattan distance
     * (a) Arranges the image index in ascending values of the manhattan distance in
     * buttonOrder
     * (b) calls ImageOrder method to diplay the image on rightPanel in the defined
     * order
     * 
     */

    public void calculateManhattanDistance(int[][] matrix, int[] imageSize, int picNo, int[] buttonOrder) {
        // sortedDistance stores the DistanceValue object of each image

        double sizeOfSelectedImage = imageSize[picNo];
        double sizeOfImage;

        for (int i = 1; i < matrix.length; i++) {
            double distance = 0.0;
            sizeOfImage = imageSize[i];
            for (int j = 1; j < matrix[i].length; j++) {
                double value = (matrix[picNo][j] / sizeOfSelectedImage) - (matrix[i][j] / sizeOfImage);
                distance += Math.abs(value);
            }
            sortedDistance.add(new DistanceValue(distance, i));
        }
        for (int i = 1; i < buttonOrder.length; i++) {
            buttonOrder[i] = sortedDistance.poll().getImageIndex();
        }
    }

    /**
     * calculateGaussianNormalization method normalises the feature of all the
     * images based on the intensity and color-code matrix passed
     * 
     * pre: colrCodeMatrix and intensityMatrix is populated with values
     * 
     * post: normalizedFeatureMartix is populated with normalised values that is
     * calculculated by
     * (1) dividing each feature by the image size
     * (2) calculating the average and standard deviation of each feature
     * (3) by using gaussian normalisation formula on each feature
     * 
     */
    public void calculateGaussianNormalization(double[][] normalizedFeatureMartix, int[][] intensityMatrix,
            int[][] colorCodeMatrix, int[] imageSize) {

        int colorIndex = 1;
        int intensityIndex = 1;
        double[][] featureMatrix = new double[normalizedFeatureMartix.length][normalizedFeatureMartix[0].length];
        double[][] avgSdMatrix = new double[featureMatrix[0].length][2];

        for (int i = 1; i < featureMatrix.length; i++) {
            for (int j = 1; j < featureMatrix[i].length; j++) {
                if (j < intensityMatrix[i].length) {
                    featureMatrix[i][j] = (intensityMatrix[i][intensityIndex++] / ((double) imageSize[i]));
                } else {
                    featureMatrix[i][j] = (colorCodeMatrix[i][colorIndex++] / ((double) imageSize[i]));
                }
            }
            colorIndex = 1;
            intensityIndex = 1;
        }

        calculateStandardDeviation(featureMatrix, avgSdMatrix);

        for (int i = 1; i < normalizedFeatureMartix.length; i++) {
            for (int j = 1; j < normalizedFeatureMartix[i].length; j++) {
                double value = (featureMatrix[i][j] - avgSdMatrix[j][0]) / avgSdMatrix[j][1];
                normalizedFeatureMartix[i][j] = (Double.isNaN(value)) ? 0 : value;
            }
        }
    }

    /**
     * calculateStandardDeviation method calculates the standard deviation for the
     * given featureMatrix
     * 
     * pre: featureMatrix is populated with values
     * 
     * post: avgSdMatrix is populated with average and standard deviation of each
     * feature in featureMatrix
     */
    private void calculateStandardDeviation(double[][] featureMatrix, double[][] avgSdMatrix) {
        // Calculate the average of each feature
        for (int i = 1; i < featureMatrix[0].length; i++) {
            for (int j = 1; j < featureMatrix.length; j++) {
                avgSdMatrix[i][0] += featureMatrix[j][i];
            }
            avgSdMatrix[i][0] /= (double) (featureMatrix.length - 1);

        }

        // Corner case if SD = 0
        double minStandardDeviation = Double.MAX_VALUE;
        Set<Integer> zeroSD = new HashSet<>();

        for (int i = 1; i < featureMatrix[0].length; i++) {
            // Calculate Standard Deviation
            for (int j = 1; j < featureMatrix.length; j++) {
                avgSdMatrix[i][1] += Math.pow(featureMatrix[j][i] - avgSdMatrix[i][0], 2);
            }
            avgSdMatrix[i][1] /= (double) (featureMatrix.length - 2);
            avgSdMatrix[i][1] = Math.sqrt(avgSdMatrix[i][1]);

            // Catch corner case
            if (avgSdMatrix[i][1] != 0 && avgSdMatrix[i][1] < minStandardDeviation) {
                minStandardDeviation = avgSdMatrix[i][1];
            }
            if (avgSdMatrix[i][1] == 0) {
                zeroSD.add(i);
            }

        }

        // Fix corner case
        for (Integer i : zeroSD) {
            if (avgSdMatrix[i][0] != 0) {
                avgSdMatrix[i][1] = 0.5 * minStandardDeviation;
            }
        }

    }

    /**
     * calculateWeight method is responsible to calculate weight for each iteration
     * 
     * pre: normalisedFeatureMatrix, relevanceSet and picNo is populated with values
     * 
     * post: calculates weight based on images in relevance set and populates weight
     * array
     * it follows the following steps:
     * (1) selects all the feature of images that are present in relevance set - if
     * set is empty it sets the uniform weight of 1/ number of features
     * (2) calculates new average and standard deviation from the selected rows
     * (3) normalises the weight.
     */
    public void calculateWeight(double[] weight, Set<Integer> relevanceSet, double[][] normalizedFeatureMartix,
            int picNo) {
        // First weight
        if (relevanceSet.isEmpty()) {
            for (int i = 1; i < weight.length; i++) {
                weight[i] = 1 / 89.0; // or (double) weight.length;
            }
        } else {
            double[][] selectedFeatureMatrix = new double[relevanceSet.size() + 1][weight.length];
            double[][] averageSdSelected = new double[weight.length][2];
            int matrixIndex = 1;

            // Add query image
            if (!relevanceSet.contains(picNo)) {
                relevanceSet.add(picNo);
            }

            // get selected image features
            for (Integer i : relevanceSet) {
                for (int j = 1; j < normalizedFeatureMartix[i].length; j++) {
                    selectedFeatureMatrix[matrixIndex][j] = normalizedFeatureMartix[i][j];
                }
                matrixIndex++;
            }
            // calculate avg and SD
            calculateStandardDeviation(selectedFeatureMatrix, averageSdSelected);

            // Add all SD
            double sum = 0.0;
            for (int i = 1; i < weight.length; i++) {
                weight[i] = (averageSdSelected[i][1] == 0) ? 0 : (1 / averageSdSelected[i][1]);
                sum += weight[i];
            }
            // Divide SD by SUM
            for (int i = 1; i < weight.length; i++) {
                weight[i] /= sum;

            }

        }

    }

    /**
     * calculateDistanceMetrix method is responsible for calculating the distance of
     * every image with query image
     * 
     * pre: normalisedFeatureMatrix, weight and picNo is populated with values
     * 
     * post: populates the buttonorder array accoringly
     * it follows the following steps:
     * (1) calculates the distance using distance metrix formula
     * (2) sorts the distance in descending order
     * (3) populates the buttonOrder.
     */
    public void calculateDistanceMetrix(double[][] normalizedFeatureMartix, double[] weight, int picNo,
            int[] buttonOrder) {

        for (int i = 1; i < normalizedFeatureMartix.length; i++) {
            double distance = 0.0;
            for (int j = 1; j < normalizedFeatureMartix[i].length; j++) {
                double value = weight[j] * Math.abs(normalizedFeatureMartix[picNo][j] - normalizedFeatureMartix[i][j]);
                distance += value;

            }

            sortedDistance.add(new DistanceValue(distance, i));

        }

        for (int i = 1; i < buttonOrder.length; i++) {
            buttonOrder[i] = sortedDistance.poll().getImageIndex();

        }

    }

}

/*
 * double[][] demoMatrix = { { 0.25, 0.375, 0.375, 0.25, 0.25, 0.25, 0.25 },
 * { 0.1, 0.5, 0.4, 0, 0, 0.5, 0.5 },
 * { 0.4, 0.4, 0.2, 0.4, 0.4, 0.2, 0 },
 * { 0.4, 0.4, 0.2, 0.2, 0.2, 0.2, 0.4 }
 * };
 * 
 * for (int i = 0; i < demoMatrix[0].length; i++) {
 * for (int j = 0; j < demoMatrix.length; j++) {
 * avgSdMatrix[i][0] += demoMatrix[j][i];
 * }
 * avgSdMatrix[i][0] /= (double) (demoMatrix.length);
 * 
 * }
 * 
 * for (int i = 0; i < demoMatrix[0].length; i++) {
 * for (int j = 0; j < demoMatrix.length; j++) {
 * avgSdMatrix[i][1] += Math.pow(demoMatrix[j][i] - avgSdMatrix[i][0], 2);
 * }
 * avgSdMatrix[i][1] /= (double) (demoMatrix.length - 1);
 * avgSdMatrix[i][1] = Math.sqrt(avgSdMatrix[i][1]);
 * }
 * double[][] matrix = new double[4][7];
 * for (int i = 0; i < demoMatrix.length; i++) {
 * for (int j = 0; j < demoMatrix[i].length; j++) {
 * matrix[i][j] = (demoMatrix[i][j] - avgSdMatrix[j][0]) /
 * avgSdMatrix[j][1];
 * System.out.print(matrix[i][j] + "   ");
 * }
 * System.out.println("");
 * 
 * }
 */