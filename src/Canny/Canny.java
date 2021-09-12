package Canny;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;

public class Canny {

    private static final int thresholdLow = 45;
    private static final int thresholdHigh = 70;

    // Direction of pixels in degrees
    // Counted as: atan2(gy,gx) * (180 / Math.PI)
    private static double[][] edgeDirection;

    // Direction mask.
    // Counted as: sqrt(gx^2 + gy^2)
    private static int[][] gradientMagnitude;

    //casted edgeDirection
    private static int[][] gradientDirection;

    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        Mat input = Imgcodecs.imread("./img/lena.tiff", Imgcodecs.IMREAD_GRAYSCALE);
        HighGui.imshow("Original", input);
        HighGui.waitKey();
        detectEdges(input);
    }

    private static void detectEdges(Mat input) {
        //sobel + gauss
        Mat processed = Sobel.operate(input);
        HighGui.imshow("Canny.Sobel", processed);
//        HighGui.waitKey();
        edgeDirection = Sobel.getEdgeDirection();
        gradientMagnitude = Sobel.getIntensityMask();
        //filling gradient direction matrix
        findDirection(processed);
        nonMaxSuppress();
        //Hysteresis input is not original image, but matrixes
        HighGui.imshow("000", useHysteresis());
        HighGui.waitKey();
    }


    // 3. Get the gradient edgeDirection
    private static void findDirection(Mat mat) {
        gradientDirection = new int[mat.rows()][mat.cols()];
        for (int i = 0; i < mat.rows(); i++) {
            for (int j = 0; j < mat.cols(); j++) {
                //adjusting = only positive and multiple of pi/4
                double angle = edgeDirection[i][j];
                if (angle < 0) angle += 360;
                //
                gradientDirection[i][j] = (int) (45 * (Math.round(angle / 45)));
//                System.out.println("Angle: "+ angle);
//                System.out.println("Rounded: "+ gradientDirection[i][j]);
            }
        }
    }

    // 4. Suppress image in order to delete unnecessary pixels and directions
    private static void nonMaxSuppress() {
        Mat suppressed = new Mat(gradientMagnitude.length, gradientMagnitude.length, Imgcodecs.IMREAD_GRAYSCALE);

        for (int i = 1; i < gradientMagnitude.length - 1; i++) {
            for (int j = 1; j < gradientMagnitude.length - 1; j++) {
                int direction = gradientDirection[i][j]%90;
                System.out.println(direction);
                int x1 = 0, y1 = 0, x2 = 0, y2 = 0;
                switch (direction) {
                    case 0:
                        x1 = i;
                        y1 = j - 1;
                        x2 = i;
                        y2 = j + 1;
                        break;
                    case 45:
                        x1 = i - 1;
                        y1 = j + 1;
                        x2 = i + 1;
                        y2 = j - 1;
                        break;
                    case 90:
                        x1 = i - 1;
                        y1 = j;
                        x2 = i + 1;
                        y2 = j;
                        break;
                    case 135:
                        x1 = i - 1;
                        y1 = j - 1;
                        x2 = i + 1;
                        y2 = j + 1;
                        break;
                }
                //if lesser than either neighbor - 0.
                if (gradientMagnitude[i][j] < gradientMagnitude[x1][y1] || gradientMagnitude[i][j] < gradientMagnitude[x2][y2]) {
                    gradientMagnitude[i][j] = 0;
                }
                else
                suppressed.put(i, j, gradientMagnitude[i][j]);
            }
        }
        HighGui.imshow("Suppressed", suppressed);
        HighGui.waitKey();
    }

    // 5. Leaving just directions/pixels that match our density
    private static Mat useHysteresis() {

        Mat res = new Mat(gradientDirection.length, gradientDirection.length, Imgcodecs.IMREAD_GRAYSCALE);
        double magnitude;

        for (int i = 1; i < gradientDirection.length; i++) {
            for (int j = 1; j < gradientDirection.length; j++) {

                magnitude = gradientMagnitude[i][j];
//                magnitude = 255;

                if (magnitude >= thresholdHigh) {
                    res.put(i, j, 255);
                } else if (magnitude <= thresholdLow) {
                    res.put(i, j, 0);
                } else {
                    //for candidates - if all neighbours surpass - ok
                    boolean ok = false;
                    for (int k = -1; k < 1; k++)
                        for (int l = -1; l < 1; l++) {
                            if (gradientMagnitude[i + k][j + l] >= thresholdHigh)
                                ok = true;
                        }
                    if (ok) {
                        res.put(0, 0, 255);
                    } else {
                        res.put(0, 0, 0);
                    }
                }
            }
        }
        
        return res;
    }
}
