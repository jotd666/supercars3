package supercars3.base;

import java.awt.geom.Point2D;


public class PointDirection
{
	private NamedControlPoint point;
	public double angle;
	public Route route;	
	
	  private int m_route_index = -1;

	  public NamedControlPoint get_point()
	  {
		  return point;
	  }
	  
	  public int get_route_index()
	  {
	    return m_route_index;
	  }
	  public void set_route_index(int ri)
	  {
	    m_route_index = ri;
	  }
	  public void reset_route_index()
	  {
	    set_route_index(-1);
	  }
	  public void paint(java.awt.Graphics g)
		{
		  NamedControlPoint p = get_point();
		  
		  p.paint(g);

		  g.drawString(m_route_index + "", p.getX(), p.getY() + ControlPoint.RADIUS * 2);
	  }  
	  
	public PointDirection(NamedControlPoint cp)
	{
		point = cp;
	}
	
	  public int square_distance(Point2D other)
	  {
		  return get_point().square_distance((int)other.getX(), (int)other.getY());
		  	  
	  }
	
	public int square_distance(ControlPoint other)
	{
		return get_point().square_distance(other);
	}
	public int square_distance(PointDirection other)
	{
		return get_point().square_distance(other.get_point());
	}
	
	public boolean equalsTo(PointDirection other)
	{
		return other.get_point().equalsTo(get_point());
	}
	public boolean is_car_start_point()
	{
		return get_point().is_car_start_point();
	}
	
	public boolean is_resume_point()
	{
		return get_point().is_resume_point();
	}
	public PointDirection(PointDirection pzd)
	{
		this.m_route_index = pzd.m_route_index;
		this.angle = pzd.angle;
		this.route = pzd.route;
		this.point = pzd.point;
	}
}
