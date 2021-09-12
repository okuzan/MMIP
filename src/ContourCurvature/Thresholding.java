package ContourCurvature;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;

@SuppressWarnings("Duplicates")
public class Thresholding {
    static Mat result;
    private static int[] histogram = new int[256];
    private static double optimalThreshold = 0;

    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        Mat input = Imgcodecs.imread("img/IM15.tif", Imgcodecs.IMREAD_GRAYSCALE);
        threshold(input, 150);
    }

    // Driver function
    static Mat threshold(Mat input, int initialT) {
        createHistogram(input);
        countThreshold(initialT);
        createImage(input);
        return result;
    }

    // 1. Creating histogram of the image
    private static void createHistogram(Mat img) {
        for (int i = 0; i < img.rows(); i++) {
            for (int j = 0; j < img.cols(); j++) {
                histogram[(int) img.get(i, j)[0]]++;
            }
        }
    }

    // 2. Calculating the optimal threshold
    private static void countThreshold(int initialT) {
        // Current and previous threshold
        double prevT, curT = initialT;

        do {
            prevT = curT;
            System.out.println("________________________");
            System.out.println("CurT: " + curT);

            // Values less and bigger than current threshold
            double lessT;
            double biggerT;

            // Upper and lower parts of threshold
            double upSum = 0;
            double lowSum = 0;

            // Counting value less than threshold
            for (int i = 0; i <= curT; i++) {
                upSum += i * histogram[i];
//            System.out.println(upSum + " - upSum");
                lowSum += histogram[i];
            }

            System.out.println(upSum + " - upSum");
            System.out.println(lowSum + " - lowSum");
            lessT = upSum / lowSum;
            System.out.println(lessT + " - lessT");

            upSum = 0;
            lowSum = 0;
            // Counting value bigger than threshold
            for (int i = (int) curT; i <= 255; i++) {
                upSum += i * histogram[i];
                lowSum += histogram[i];
            }
            biggerT = upSum / lowSum;

            System.out.println(upSum + " - upSum");
            System.out.println(lowSum + " - lowSum");
            System.out.println(biggerT + " - biggerT");

            // Current threshold counting
            curT = (lessT + biggerT) / 2;

        } while (curT != prevT);

        optimalThreshold = curT;
        System.out.println(optimalThreshold);
    }

    // 3. Generating image due to optimal threshold
    private static void createImage(Mat img) {
         result = new Mat(img.rows(), img.cols(), Imgcodecs.IMREAD_GRAYSCALE);
        for (int i = 0; i < img.rows(); i++) {
            for (int j = 0; j < img.cols(); j++) {
                if (img.get(i, j)[0] < optimalThreshold)
                    result.put(i, j, 255);
                else
                    result.put(i, j, 0);
            }
        }
        HighGui.imshow("img/Contoured2.tif", result);
        HighGui.waitKey();
    }
}
