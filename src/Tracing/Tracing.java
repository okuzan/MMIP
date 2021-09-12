package Tracing;

import java.util.Stack;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;

@SuppressWarnings("Duplicates")
public class Tracing {

    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        Mat image = Imgcodecs.imread("img/mouse.png", Imgcodecs.IMREAD_GRAYSCALE);
//        HighGui.imshow("img/traced.tif", image);
//        HighGui.waitKey();

        Mat contoured = contour(image);

        HighGui.imshow("img/traced.tif", contoured);
        HighGui.waitKey();
    }

    //Tracing image and finding contour
    private static Mat contour(Mat image) {
        //Finding points
        Stack<Point> points = tracing(image);


        Mat contoured = new Mat(image.rows(), image.cols(), Imgcodecs.IMREAD_GRAYSCALE);
        for (Point p : points){
            contoured.put(p.y, p.x, 255);
            System.out.println(p.toString());
        }
        return contoured;
    }

    //Tracing image and finding points
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
//            System.out.println("Direction: " + direction);
        } while (ptStack.peek().compareTo(secondPoint) == 1 && ptStack.peek().compareTo(contourStart) == 1);
        return ptStack;
    }

    // Find the first point which brightness equals (255)
    private static Point startPoint(Mat image) {
        for (int i = 0; i < image.rows(); i++) {
            for (int j = 0; j < image.cols(); j++) {
                if (image.get(i, j)[0] == 255) {
                    return new Point(i, j);
                }
            }
        }
        return null;
    }

    //Looking for next point
    private static int nextDir(Mat image, int dir, Point start, Stack<Point> contour) {

        int tempDir;

        if (dir % 2 == 0)
            tempDir = (dir + 7) % 8;
        else
            tempDir = (dir + 6) % 8;

        Point found = null;

        int i, j;
        while (found == null) {
//            System.out.println(tempDir);
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
                found = new Point(start.y + j, start.x + i);
            else
                tempDir = (tempDir + 1) % 8;
        }

        contour.push(found);
        return tempDir;
    }
}
