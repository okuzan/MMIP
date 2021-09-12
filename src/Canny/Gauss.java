package Canny;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;

public class Gauss {

    public static final double SIGMA = 1;
    public static final int MASK_SIZE = 3;


    public static double sumOfMask = 0;
    public static double[][] MASK;


    private static double[][] createMask() {
        MASK = new double[MASK_SIZE][MASK_SIZE];
        double sigmaSquared = 2 * SIGMA * SIGMA;
        int mid = MASK_SIZE / 2;

        for (int i = 0, x = i - mid; i < MASK_SIZE; i++, x++) {
            for (int j = 0, y = j - mid; j < MASK_SIZE; j++, y++) {
                MASK[i][j] = (1 / (Math.PI * sigmaSquared)) * (Math.pow(Math.E, -((x * x + y * y)) / sigmaSquared));
                sumOfMask += MASK[i][j];
            }
        }

        for (int i = 0; i < MASK_SIZE; i++) {
            for (int j = 0; j < MASK_SIZE; j++) {
                MASK[i][j] /= sumOfMask;
            }
        }
        return MASK;

    }

    public static Mat blur(Mat mat) {
        double[][] mask = createMask();
        Mat res = new Mat(mat.rows(), mat.cols(), Imgcodecs.IMREAD_GRAYSCALE);
        System.out.println(mat.rows() + " - rows, " + mat.cols() + " - cols");
        int mid = mask.length / 2;
        System.out.println(mid);
        for (int row = mid; row < mat.rows() - mid; row++) {
            for (int col = mid; col < mat.cols() - mid; col++) {
                double sum = 0;
                for (int i = 1; i <= mask.length / 2; i++) {
                    for (int j = 1; j <= mask.length / 2; j++) {
                        sum += (mat.get(row, col)[0] * mask[mid][mid]);
                        sum += (mat.get(row - i, col - j)[0] * mask[mid - i][mid - j]);
                        sum += (mat.get(row + i, col + j)[0] * mask[mid + i][mid + j]);
                        sum += (mat.get(row, col - j)[0] * mask[mid][mid - j]);
                        sum += (mat.get(row, col + j)[0] * mask[mid][mid + j]);
                        sum += (mat.get(row - i, col)[0] * mask[mid - i][mid]);
                        sum += (mat.get(row + i, col)[0] * mask[mid + i][mid]);

                    }
                }
                int res1 = (int) (sum / sumOfMask);
                if (res1 > 256) res1 = 256;
                if (res1 < 0) res1 = 0;
                res.put(row, col, res1);
            }
        }
        return res;
    }

    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        Mat input = Imgcodecs.imread("./img/lena.tiff", Imgcodecs.IMREAD_GRAYSCALE);
        HighGui.imshow("Original", input);
        HighGui.imshow("Blurred", blur(input));
        HighGui.waitKey();
    }
}