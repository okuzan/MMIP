package Tracing;

public class Point implements Comparable<Point>{
	int y;
	int x;

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

	@Override
	public String toString() {
		return "[" +
				"x = " + x +
				"; y = " + y +
				']';
	}
}
