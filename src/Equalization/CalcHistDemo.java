package Equalization;

import org.opencv.core.*;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

import static org.opencv.core.CvType.CV_8UC1;

@SuppressWarnings("Duplicates")
public class CalcHistDemo {
    static int[] histogram;

    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        Mat src = Imgcodecs.imread("./img/Geneva.tif");
        createHistogram(src);
        myEqualize(src);
    }

    public static void createHistogram(Mat src) {

        if (src.empty()) {
            System.out.println("Empty");
            System.exit(0);
        }
        List<Mat> bgrPlanes = new ArrayList<>();
        Core.split(src, bgrPlanes);
        int histSize = 256;
        [][]
        float[] range = {0, 256}; //the upper boundary is exclusive
        MatOfFloat histRange = new MatOfFloat(range);
        boolean accumulate = false;
        Mat bHist = new Mat(), gHist = new Mat(), rHist = new Mat();
        Imgproc.calcHist(bgrPlanes, new MatOfInt(0), new Mat(), bHist, new MatOfInt(histSize), histRange, accumulate);
        Imgproc.calcHist(bgrPlanes, new MatOfInt(1), new Mat(), gHist, new MatOfInt(histSize), histRange, accumulate);
        Imgproc.calcHist(bgrPlanes, new MatOfInt(2), new Mat(), rHist, new MatOfInt(histSize), histRange, accumulate);
        int histW = 512, histH = 400;
        int binW = (int) Math.round((double) histW / histSize);
        Mat histImage = new Mat( histH, histW, CvType.CV_8UC3, new Scalar( 0,0,0) );
        Core.normalize(bHist, bHist, 0, histImage.rows(), Core.NORM_MINMAX);
        Core.normalize(gHist, gHist, 0, histImage.rows(), Core.NORM_MINMAX);
        Core.normalize(rHist, rHist, 0, histImage.rows(), Core.NORM_MINMAX);
        float[] bHistData = new float[(int) (bHist.total() * bHist.channels())];
        bHist.get(0, 0, bHistData);
        float[] gHistData = new float[(int) (gHist.total() * gHist.channels())];
        gHist.get(0, 0, gHistData);
        float[] rHistData = new float[(int) (rHist.total() * rHist.channels())];
        rHist.get(0, 0, rHistData);
        for( int i = 1; i < histSize; i++ ) {
            Imgproc.line(histImage, new Point(binW * (i - 1), histH - Math.round(bHistData[i - 1])),
                    new Point(binW * (i), histH - Math.round(bHistData[i])), new Scalar(255, 0, 0), 2);
            Imgproc.line(histImage, new Point(binW * (i - 1), histH - Math.round(gHistData[i - 1])),
                    new Point(binW * (i), histH - Math.round(gHistData[i])), new Scalar(0, 255, 0), 2);
            Imgproc.line(histImage, new Point(binW * (i - 1), histH - Math.round(rHistData[i - 1])),
                    new Point(binW * (i), histH - Math.round(rHistData[i])), new Scalar(0, 0, 255), 2);
        }
        HighGui.imshow( "Source image", src );
        HighGui.imshow( "calcHist ", histImage );
        HighGui.waitKey(0);
//        System.exit(0);

    }

    private static void myEqualize(Mat src) {

        Mat newImage = new Mat(src.rows(), src.cols(), CV_8UC1, Scalar.all(0));

        int size = 256;

        histogram = new int[size];
        int[] hist_cum = new int[size];

        for (int i = 0; i < size; i++) {
            histogram[i] = 0;
            hist_cum[i] = 0;
        }

        double[][] oldData = new double[src.rows()][src.cols()];

        for (int row = 0; row < src.rows(); row++) {
            for (int col = 0; col < src.cols(); col++) {
                double[] val = src.get(row, col);
                oldData[row][col] = val[0];
            }
        }

        int bufferSize = src.cols() * src.rows();
        double alfa = 255 / (double) (bufferSize);

        double[][] newData = new double[src.rows()][src.cols()];

        for (int i = 0; i < src.rows(); i++) {
            for (int j = 0; j < src.cols(); j++) {
                int value = (int) oldData[i][j];
//                System.out.println(value);
                histogram[value]++;
            }
        }

        hist_cum[0] = histogram[0];

        for (int i = 1; i < size; i++) {
            hist_cum[i] = hist_cum[i - 1] + histogram[i];
        }

        for (int i = 0; i < src.rows(); i++) {
            for (int j = 0; j < src.cols(); j++) {
                int value = (int) oldData[i][j];
                newData[i][j] = (hist_cum[value] * alfa);
                newImage.put(i, j, newData[i][j]);
            }
        }

        HighGui.imshow("Equalized", newImage);
        HighGui.waitKey(0);
    }

}