package ContourCurvature;

public class Point extends org.opencv.core.Point implements Comparable<Point>{
	protected int y;
	protected int x;
	
	public Point(int y, int x) {
		this.y = y;
		this.x = x;
	}


	@Override
	public int compareTo(Point that) {
		if (this.x == that.x && this.y == that.y) {
			return 0;
		}
		return 1;
	}
	
	
}
