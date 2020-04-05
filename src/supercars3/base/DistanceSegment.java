package supercars3.base;


public class DistanceSegment extends Segment
{
	public double distance;
	public void set_points(PointDirection p1, PointDirection p2, double distance)
	
	{
		this.distance = distance;
		super.set_points(p1,p2);
	}
	public void set_points(ControlPoint p1, ControlPoint p2, double distance)
	
	{
		this.distance = distance;
		super.set_points(p1,p2);
	}
}
