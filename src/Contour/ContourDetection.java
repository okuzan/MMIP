package Contour;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;

@SuppressWarnings("Duplicates")
public class ContourDetection {
    private static double[][] data;
    private static double[][] output;

    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        Mat mat = Imgcodecs.imread("img/contour.tif", Imgcodecs.IMREAD_GRAYSCALE);
        HighGui.imshow("Contour", contour(mat));
        HighGui.imshow("Original", mat);
        HighGui.waitKey();
    }

    private static Mat contour(Mat mat) {


        int[][] insideData = insideData(mat);
        Mat res = mat.clone();
        for (int i = 0; i < mat.cols(); i++)
            for (int j = 0; j < mat.rows(); j++)
                res.put(i, j, mat.get(i, j)[0] - insideData[i][j]);


        return res;
    }

    private static int[][] insideData(Mat mat) {
        int[][] insideData = new int[mat.cols()][mat.rows()];

        for (int i = 1; i < mat.cols() - 1; i++) {
            for (int j = 1; j < mat.rows() - 1; j++) {
//                check if it is edge
                boolean itBorders = true;
                for (int k = -1; k <= 1; k++) {
                    for (int l = -1; l <= 1; l++) {
//                        get and check
                        double dataPoint = mat.get(i + k, j + l)[0];

                        if (dataPoint != 255)
                            itBorders = false;
                    }
                }
                if (itBorders)
                    insideData[i][j] = 255;
                else
                    insideData[i][j] = 0;

            }
        }
        return insideData;
    }
}
