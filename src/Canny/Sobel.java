package Canny;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;


class Sobel {

    private static int[][] Gx = {{-1, -2, -1}, {0, 0, 0}, {1, 2, 1}};
    private static int[][] Gy = {{-1, 0, 1}, {-2, 0, 2}, {-1, 0, 1}};

    private static double[][] edgeDirection;
    private static int[][] intensityMask;

    static double[][] getEdgeDirection() {
        return edgeDirection;
    }

    static int[][] getIntensityMask() {
        return intensityMask;
    }

    private static final int maskSize = 3;
    private static double[][] matrix = new double[maskSize][maskSize];

    static Mat operate(Mat mat) {
        mat = Gauss.blur(mat);
        Mat res = mat.clone();
        edgeDirection = new double[mat.rows()][mat.cols()];
        intensityMask = new int[mat.rows()][mat.cols()];

        for (int i = maskSize / 2; i < mat.rows() - maskSize / 2; i++)
            for (int j = maskSize / 2; j < mat.cols() - maskSize / 2; j++) {

                for (int k = 0; k < matrix.length; k++)
                    for (int l = 0; l < matrix.length; l++)
                        matrix[k][l] = mat.get(i + k - maskSize / 2, j + l - maskSize / 2)[0];


                int grad = gradientOf(matrix);
                res.put(i, j, grad);

                intensityMask[i][j] = grad;
                edgeDirection[i][j] = edgeDirection(matrix);
            }

        return res;
    }

    private static int gradientOf(double[][] core) {
        int gxCum = 0;
        int gyCum = 0;

        for (int i = 0; i < Gx.length; i++) {
            for (int j = 0; j < Gy.length; j++) {
                gxCum += core[i][j] * Gx[i][j];
                gyCum += core[i][j] * Gy[i][j];
            }
        }

        return (int) Math.sqrt(Math.pow(gxCum, 2) + Math.pow(gyCum, 2));
    }

    private static double edgeDirection(double[][] core) {

        int gx = 0;
        int gy = 0;
        for (int i = 0; i < Gx.length; i++) {
            for (int j = 0; j < Gx.length; j++) {
                gx += core[i][j] * Gx[i][j];
                gy += core[i][j] * Gy[i][j];
            }
        }
        return Math.atan2(gy, gx) * (180 / Math.PI);
    }

    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        Mat input = Imgcodecs.imread("./img/lena.tiff", Imgcodecs.IMREAD_GRAYSCALE);
        HighGui.imshow("Original", input);
        HighGui.imshow("Operated", operate(input));
        HighGui.waitKey();
    }
}