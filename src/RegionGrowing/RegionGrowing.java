package RegionGrowing;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;

@SuppressWarnings("Duplicates")
public class RegionGrowing {

    private static Mat input, result;
    private static boolean[][] processed;
    private static int threshold = 2;
    private static int a, b;
    private static double[][] data;
    private static double[][] output;

    static class Point {
        int x, y, c;

        Point(int x, int y) {
            this.x = x;
            this.y = y;
            this.c = (int) data[x][y];
        }

        @Override
        public String toString() {
            return "Point{" +
                    "x=" + x +
                    ", y=" + y +
                    ", c=" + c +
                    '}';
        }
    }


    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        input = Imgcodecs.imread("img/lena.tiff", Imgcodecs.IMREAD_GRAYSCALE);
        HighGui.imshow("Inp", input);

        a = input.rows();
        b = input.cols();

        result = new Mat(a, b, Imgcodecs.IMREAD_GRAYSCALE);
        for (int i = 0; i < a; i++) {
            for (int j = 0; j < b; j++) {
//                output[i][j] = 0;
                result.put(i, j, 0);
            }


        }
//        for (int i = 0; i < a; i++) for (int l = 0; l < b; l++) result.put(i, l, output[i][l]);
//        result = Clone;


        data = new double[a][b];
        output = new double[a][b];
        for (int i = 0; i < a; i++) for (int l = 0; l < b; l++) data[i][l] = (int) input.get(i, l)[0];


        processed = new boolean[a + 1][b + 1];

        for (int i = 0; i < a; i++)
            for (int l = 0; l < b; l++) {
                Queue q = region(i, l);
                putData(q);
            }


        HighGui.imshow("Result", result);
        HighGui.waitKey();
    }

    static void putData(Queue<Point> points) {
        int size = points.size();
        while (!points.isEmpty()) {
            Point p = points.dequeue();
            result.put(p.x, p.y, p.c);
        }
        if (size > 40) {
            HighGui.imshow("Result", result);
            HighGui.waitKey();
        }
    }

    private static Queue<Point> region(int i, int j) {

        Queue<Point> stable = new Queue<Point>();
        Queue<Point> q = new Queue<Point>();
        q.enqueue(new Point(i, j));
        stable.enqueue(new Point(i, j));
        processed[i][j] = true;

        while (!q.isEmpty()) {
            Point p = q.dequeue();
            i = p.x;
            j = p.y;
            for (int k = -1; k <= 1; k++) {
                for (int l = -1; l <= 1; l++) {
                    try {
                        if (input.get(i + k, j + l) != null) {
                            int dif = (int) Math.abs(data[p.x][p.y] - data[i + k][j + l]);
                            if (dif <= threshold) {
                                if (!processed[i + k][j + l]) {
                                    output[i + k][j + l] = input.get(i, j)[0];
                                    processed[i + k][j + l] = true;
                                    Point pp = new Point(i + k, j + l);
                                    q.enqueue(pp);
                                    stable.enqueue(pp);
                                }
                            } else {
                                output[i + k][j + l] = 0;
                                processed[i + k][j + l] = true;
                            }
                        }
                    } catch (Exception e) {
//                        System.out.println(".");
                    }
                }
            }
        }
        return stable;
    }
}