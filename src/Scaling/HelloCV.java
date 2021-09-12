package Scaling;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;

@SuppressWarnings("Duplicates")
class HelloCV {
    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        Mat image = Imgcodecs.imread("img/lena.tiff", 0);
        HighGui.imshow( "SRC image", image );
        decrease(image, 2);
        increase(image, 2);
    }

    private static void decrease(Mat image, int n) {
        Mat newImage = new Mat(image.rows() / n, image.cols() / n, CvType.CV_8UC1, Scalar.all(0));

        int bufferSize = image.cols() * image.rows();
        byte[] oldData = new byte[bufferSize];
        image.get(0, 0, oldData);

        int row, col;
        int oldLoc;

        byte [] newData = new byte [bufferSize / n / n];

        for (int i = 0; i < newData.length; i++) {
            row = i / newImage.cols();
            col = i % newImage.rows();
            oldLoc = n * n * row * newImage.cols() + col * n;
            double oldVal = oldData[oldLoc];

            for (int k = 0; k < n - 1; k++) {
                for (int j = 0; j < n - 1; j++) oldVal += oldData[oldLoc + image.width() * k + j];
                double newVal = oldVal / n;
                newData[i] = (byte) newVal;
            }
        }
        newImage.put(0, 0, newData);
        HighGui.imshow( "Decreased " + n + " times", newImage );
    }

    private static void increase(Mat image, int n) {
        Mat newImage = new Mat(image.rows() * n, image.cols() * n, CvType.CV_8UC1, Scalar.all(0));

        int bufferSize = image.cols() * image.rows();
        byte[] oldData = new byte[bufferSize];

        image.get(0, 0, oldData);

        int row, col;

        byte [] newData = new byte[bufferSize * n * n];

        for (int i = 0; i < oldData.length; i++) {
            row = i / image.cols();
            col = i % image.rows();

            int newLoc = n * n * row * image.cols() + col * n;

            for (int k = 0; k < n ; k++) {
                for (int j = 0; j < n; j++) {
                    newData[newLoc + newImage.width()*k + j] = oldData[i];
                }
            }
        }

        newImage.put(0, 0, newData);
        HighGui.imshow( "Increased " + n + " times", newImage );
        HighGui.waitKey(0);
        System.exit(0);
    }
}