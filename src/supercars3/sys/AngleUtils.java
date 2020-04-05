package supercars3.sys;

public class AngleUtils
{
	public static final double angle_difference(double a1, double a2)
	{
		  return normalize_m180_180(a1 - a2);
	}
	
	public static final double average_angle(double a1, double a2)
	{
		double angle = (a1 + a2) / 2;
		return normalize_m180_180(angle);
	}
	
	public static final double oppose(double a)
	{
		return normalize_m180_180(a+180);
	}
	public static final double normalize_m180_180(double a)
	{
		double angle = a;
		
		  while (angle > 180.0) {
			  angle -= 360.0;
		  }
		  while (angle < -180.0) 
		  {
			  angle += 360.0;
		  }
		  
		  return angle;
	}
	
	public static final double normalize_0_360(double a)
	{
		double angle = a;
		
		  while (angle < 0) 
		  {
			  angle += 360.0;
		  }
		  while (angle > 360) 
		  {
			  angle -= 360.0;
		  }
		  
		  return angle;
	}
}
