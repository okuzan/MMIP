package ContourCurvature;

import java.util.ArrayList;
import java.util.Stack;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;

@SuppressWarnings("Duplicates")
public class Main {

    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        Mat image = Imgcodecs.imread("img/IM10.tif", Imgcodecs.IMREAD_GRAYSCALE);

        Mat contoured = contour(image, true);
        HighGui.imshow("Result", contoured);
        HighGui.waitKey();
//        Imgcodecs.imwrite("img/result.tif", contoured);

    }

    // Tracing image and finding contour
    private static Mat contour(Mat image, boolean camber) {
        // Finding points
        Stack<Point> points = tracing(image);
        if (camber) {
            ArrayList<Double> camb = contourCamber(points, 7);
            generateChart(camb);
        }

        Mat contoured = new Mat(image.rows(), image.cols(), Imgcodecs.IMREAD_GRAYSCALE);
        for (Point p : points)
            contoured.put(p.y, p.x, 255);
        return contoured;
    }

    // Tracing image and finding points
    private static Stack<Point> tracing(Mat image) {
        // Looking for start point
        Point contourStart = startPoint(image);

        Stack<Point> ptStack = new Stack<>();
        ptStack.push(contourStart);

        int direction = 7;
        direction = nextDir(image, direction, contourStart, ptStack);
        Point secondPoint = ptStack.peek();

        direction = nextDir(image, direction, secondPoint, ptStack);
        do {
            direction = nextDir(image, direction, ptStack.peek(), ptStack);
        } while (ptStack.peek().compareTo(secondPoint) == 1 && ptStack.peek().compareTo(contourStart) == 1);
        return ptStack;
    }

    // Find the first point which brightness equals (255)
    private static Point startPoint(Mat image) {
        for (int i = 0; i < image.rows(); i++) {
            for (int j = 0; j < image.cols(); j++) {
                if (image.get(i, j)[0] == 255) {
                    Point contourStart = new Point(i, j);
                    return contourStart;
                }
            }
        }
        return null;
    }

    // Looking for next point
    private static int nextDir(Mat image, int dir, Point start, Stack<Point> contour) {
        int tempDir;
        if (dir % 2 == 0)
            tempDir = (dir + 7) % 8;
        else
            tempDir = (dir + 6) % 8;

        Point exact = null;
        int i, j;
        while (exact == null) {
//                System.out.println("dd");
            switch (tempDir) {
                case 0:
                    i = 1;
                    j = 0;
                    break;
                case 1:
                    i = 1;
                    j = -1;
                    break;
                case 2:
                    i = 0;
                    j = -1;
                    break;
                case 3:
                    i = -1;
                    j = -1;
                    break;
                case 4:
                    i = -1;
                    j = 0;
                    break;
                case 5:
                    i = -1;
                    j = 1;
                    break;
                case 6:
                    i = 0;
                    j = 1;
                    break;
                case 7:
                    i = 1;
                    j = 1;
                    break;
                default:
                    System.out.println("Unknown direction!");
                    throw new IllegalArgumentException();
            }

            if (image.get(start.y + j, start.x + i)[0] == 255)
                exact = new Point(start.y + j, start.x + i);
            else
                tempDir = (tempDir + 1) % 8;
        }

        contour.push(exact);
        return tempDir;
    }

    //Counting camber of contour points
    private static ArrayList<Double> contourCamber(Stack<Point> contour, int c) {
        int contourSize = contour.size();
        ArrayList<Double> camber = new ArrayList<>();
        for (int i = 0; i < contourSize; i++) {
            Point right, left;
            Point center = contour.get(i);

            if (i + c < contourSize)
                right = contour.get(i + c);
            else
                right = contour.get(i + c - contourSize);

            if (i - c >= 0)
                left = contour.get(i - c);
            else
                left = contour.get(contourSize + i - c);

            camber.add(calCamber(left, right, center));
        }

        return camber;
    }

    // Calculating camber
    private static double calCamber(Point left, Point right, Point center) {

        // Distance calculating
        double distanceRight = Math
                .sqrt((right.x - center.x) * (right.x - center.x) + (right.y - center.y) * (right.y - center.y));
        double distanceLeft = Math
                .sqrt((left.x - center.x) * (left.x - center.x) + (left.y - center.y) * (left.y - center.y));


        //Angle calculating
        double angleRight = 0;
        if (right.y - center.y != 0)
            angleRight = Math.atan((double) Math.abs(right.x - center.x) / Math.abs(right.y - center.y));
        else
            angleRight = Math.atan((double) Math.abs(right.y - center.y) / Math.abs(right.x - center.x));

        double angleLeft = 0;
        if (right.y - center.y != 0)
            angleLeft = Math.atan((double) Math.abs(left.x - center.x) / Math.abs(left.y - center.y));
        else
            angleLeft = Math.atan((double) Math.abs(left.y - center.y) / Math.abs(left.x - center.x));

        double averageAngle = (angleRight + angleLeft) / 2;
        return (angleRight - averageAngle) * (angleRight + distanceLeft) / (2 * distanceRight * distanceLeft);
    }

    // Creating chart
    private static void generateChart(ArrayList<Double> curvature) {
        final int x = 400;
        final int y = 400;
        Mat chart = new Mat(y, x, Imgcodecs.IMREAD_GRAYSCALE);
        for (int i = 0; i < x; i++) {
            chart.put(y / 2, i, 255);
        }

        int mid = curvature.size() / x;
        for (int i = 0, j = 0; i < curvature.size(); i += mid, j++) {
            chart.put((int) (y / 2 + y / 2 * curvature.get(i)), j, 255);
        }

        showCamberEnergy(curvature);
        HighGui.imshow("img/chart.tif", chart);
//        HighGui.waitKey();
    }

    // Print camber energy
    private static void showCamberEnergy(ArrayList<Double> camber) {
        double sum = 0;
        for (double camb : camber)
            sum += camb * camb;
        System.out.println("Camber energy: " + sum / camber.size());
    }
}
