package CornerDetection;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;

import java.util.ArrayList;

@SuppressWarnings("Duplicates")
public class Moravec {

    static int[] x_delta = {-1, 0, 1, -1, 0, 1, -1, 0, 1};

    static int[] y_delta = {-1, -1, -1, 0, 0, 0, 1, 1, 1};

    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        Mat mat = Imgcodecs.imread("img/Geneva.tif", Imgcodecs.IMREAD_GRAYSCALE);
        HighGui.imshow("Original", mat);
        HighGui.imshow("Contour", contour(mat, 600));
        HighGui.waitKey();
    }

    private static Mat contour(Mat img, int threshold) {
        Mat res = img.clone();

    //4. Видаляємо кути, які повторюються застосовуючи процедуру пошуку локальних максимумів функції
    //(non-maximal suppression),
    //Є проблема, відсутність інваріативності і якщо в нас багато діагональних ребер, то вона трошки погано буде відображатися
        ArrayList<Point> xy_shifts = new ArrayList<>();
        xy_shifts.add(new Point(-1, 0));
        xy_shifts.add(new Point(0, -1));
        xy_shifts.add(new Point(1, 0));
        xy_shifts.add(new Point(0, 1));
        xy_shifts.add(new Point(-1, -1));
        xy_shifts.add(new Point(-1, 1));
        xy_shifts.add(new Point(1, -1));
        xy_shifts.add(new Point(1, 1));

        for (int i = 2; i < img.rows() - 2; i++)
            for (int j = 2; j < img.cols() - 2; j++) {
                int minSum = 100000;

                for (Point shift : xy_shifts) {
                    int sx = i + shift.x;
                    int sy = j + shift.y;

                    int sum = 0;
                    for (int k = 0; k < 9; k++) {
                        int redX = i + x_delta[k];
                        int redY = j + y_delta[k];
                        int blueX = sx + x_delta[k];
                        int blueY = sy + y_delta[k];
                        int red_value = (int) img.get(redX, redY)[0];
                        int blue_value = (int) img.get(blueX, blueY)[0];
                        int dif = red_value - blue_value;
                        dif *= dif;
                        sum += dif;
                    }
                    if (minSum > sum)
                        minSum = sum;
                }

                if (minSum > threshold) // draw point
                    for (int k = i - 1; k <= i + 1; k++)
                        for (int l = j - 1; l <= j + 1; l++)
                            res.put(k, l, 255);
            }

        return res;
    }
}
