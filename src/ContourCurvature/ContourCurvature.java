package ContourCurvature;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;

import static java.lang.Math.atan2;
import static java.lang.StrictMath.*;
import static org.opencv.highgui.HighGui.imshow;
import static org.opencv.imgproc.Imgproc.line;

@SuppressWarnings("Duplicates")
public class ContourCurvature {

    private static double[][] data;
    private static double[][] output;

    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        Mat image = Imgcodecs.imread("img/IM15.tif", Imgcodecs.IMREAD_GRAYSCALE);
//        image = Thresholding.threshold(image,100);
        Mat contoured = contour(image);
        imshow("Result", contoured);
        HighGui.waitKey();
//      Imgcodecs.imwrite("img/result.tif", contoured);

    }

    private static Mat contour(Mat in) {

        int as = in.rows();
        int bs = in.cols();

        data = new double[as][bs];
        output = new double[as][bs];
        for (int i = 0; i < as; i++)
            for (int l = 0; l < bs; l++) {
                data[i][l] = (int) in.get(i, l)[0];
//                output[i][l] = data[i][l];
                output[i][l] = 0;
            }
        Point[] cont = new Point[as*bs];
        Mat res = new Mat(in.rows(), in.cols(), Imgcodecs.IMREAD_GRAYSCALE);

        int k = 7;
        int tti, ttj, ti, tj;
        int n = 0;

        for (int i = 2; i < in.rows() - 2; ++i) {
            for (int j = 2; j < in.cols() - 2; ++j) {
                if (data[i][j] > 1 && output[i][j] < 254 && data[i][j - 1] == 0) {
                    tti = i;
                    ttj = j;
                    do {
                        Point px = new Point(ttj, tti);
                        cont[n++] = px;
                        output[tti][ttj] = 255;
                        if (k % 2 == 0) k = (k + 7) % 8;
                        else k = (k + 6) % 8;
                        while (true) {
                            ti = tti;
                            tj = ttj;
                            switch (k) {
                                case 1:
                                    tj++;
                                    ti--;
                                    break;
                                case 2:
                                    ti--;
                                    break;
                                case 3:
                                    ti--;
                                    tj--;
                                    break;
                                case 4:
                                    tj--;
                                    break;
                                case 5:
                                    ti++;
                                    tj--;
                                    break;
                                case 6:
                                    ti++;
                                    break;
                                case 7:
                                    ti++;
                                    tj++;
                                    break;
                                default:
                                    tj++;
                            }

//                            System.out.println(ti+" "+tj);
                            try {
                                if (data[i][j] == 0) k = (k + 1) % 8;
                                else {
                                    tti = ti;
                                    ttj = tj;
                                    break;
                                }
                            } catch (Exception ignored) {
                                tti = ti;
                                ttj = tj;
                                break;
                            }
                        }
                    } while (i != tti || j != ttj);
                    //break;
                    k = 7;
                }
            }
        }
//        imshow("res", res);
        for (int i = 0; i < as; i++)
            for (int l = 0; l < bs; l++) {
                res.put(i, l, output[i][l]);
            }
        double[] c = new double[90000000];
        k = 0;
        int sm = 5;
        int b, f;
        for (int i = 0; i < n; ++i) {
            if (i - sm < 0) b = n + (i - sm);
            else b = i - sm;
            if (i + sm > n) f = (i + sm) - n;
            else f = i + sm;
            if(cont[f] == null) System.out.println("!");
            Point fk = new Point(cont[i].x - cont[f].x, cont[i].y - cont[f].y);
            //cout << fk.x << ' ' << fk.y << endl;
            Point bk = new Point(cont[i].x - cont[b].x, cont[i].y - cont[b].y);
            //cout << bk.y << " " << bk.x << endl;
            double df = sqrt(pow(fk.x - cont[i].x, 2) + pow(fk.y - cont[i].y, 2));
            double db = sqrt(pow(bk.x - cont[i].x, 2) + pow(bk.y - cont[i].y, 2));
            //cout << bk.y << " " << cont[i].y << endl;
            double af, ab;
            if (cont[f].y - cont[i].y == 0) {
                af = 0;
            } else {
                af = atan2((double) abs(cont[f].x - cont[i].x), abs(cont[f].y - cont[i].y));
            }
            if (cont[b].y - cont[i].y == 0) {
                ab = 0;
            } else {
                ab = atan2((double) abs(cont[b].x - cont[i].x), abs(cont[b].y - cont[i].y));
            }
            double ai = ab / 2 + af / 2;
            double dif = af - ai;
            //cout <<  af << " " << ab << ' ' << ai << endl;
            double cur = dif * (db + df) / (2 * db * df);
            c[k++] = cur;
            //cout << cur << endl;
        }
        int height = 500;
        int width = 1000;
        Mat gist = new Mat(height, width, Imgcodecs.IMREAD_GRAYSCALE);

        line(gist, new Point(0, height / 2), new Point(width - 10, height / 2), Scalar.all(255));
        for (int i = 0, x = 1; i < n; i++, x++) {
            line(gist, new Point(x, height / 2), new Point(x, (int) (height / 2 + c[i] * k * 100)), Scalar.all(255));
        }
        imshow("gist", gist);
        return res;
    }
}