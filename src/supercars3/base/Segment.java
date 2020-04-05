package supercars3.base;

import java.awt.geom.*;

import supercars3.sys.AngleUtils;

/**
 * <p>Titre : </p>
 * <p>Description : </p>
 * <p>Copyright : Copyright (c) 2005</p>
 * <p>Société : </p>
 * @author non attribuable
 * @version 1.0
 */

public class Segment extends Selectable {
  private Line2D.Double m_line_2d = new Line2D.Double();
  private double m_inclination; // [-180,180]

  public Segment()
  {

  }

  public Segment(ControlPoint start, ControlPoint end)
  {
    set_points(start,end);
  }
  public Segment(PointDirection start, PointDirection end)
  {
    set_points(start,end);
  }

  public double get_angle_x()
  {
    return m_inclination;
  }

  public void set_points(PointDirection start, PointDirection end)
  {
	  set_points(start.get_point(),end.get_point());
  }
  public void set_points(ControlPoint start, ControlPoint end)
  {
    m_start = start;
    m_end = end;

    double BC = m_end.getY() - m_start.getY();
    double AC = m_end.getX() - m_start.getX();

    m_inclination = Math.toDegrees(Math.atan2(BC,AC));

    m_line_2d.setLine(m_start.getX(), m_start.getY(),
                  m_end.getX(),m_end.getY());

  }
  public double projection(int x, int y)
  {
	  return projection(new ControlPoint(x,y));
  }
  /**
   * compute projection relative distance
   * @param B
   * @return ratio 0..1: within segment
   */
  public double projection(ControlPoint B)
  {
	  Segment SA = new Segment(m_start,B);
	  double rval = dot_prod(SA) / (double)dot_prod(this);
	  
	  return rval;
  }
  public boolean intersects_line(double x1, double y1, double x2, double y2)
  {
    return m_line_2d.intersectsLine(x1,y1,x2,y2);
  }

  public ControlPoint get_start_point() {return m_start;}
  public ControlPoint get_end_point() {return m_end;}

  public boolean equalsTo(Segment other)
  {
    return ((other != null) && 
    		(((m_start.equalsTo(other.m_start)) && 
    				(m_end.equalsTo(other.m_end)))
            ||
            (m_start.equalsTo(other.m_end)) && 
            (m_end.equalsTo(other.m_start))));
  }

  public boolean contains(ControlPoint cp)
  {
	  return cp.equalsTo(m_end) || cp.equals(m_start);
  }
  /**
  *
  * @param A
  * @return oriented angle at A of the triangle A,B,C, B being
  * the start of this segment, and C the end
  */
 public double get_angle(ControlPoint A)
  {
     ControlPoint B = this.get_start_point();
    ControlPoint C = this.get_end_point();

    return get_angle(A,B,C);
  }
 /**
 *
 * @param A
 * @return oriented angle at A of the triangle A,B,C
 */

    static public double get_angle(ControlPoint A, ControlPoint B, ControlPoint C)
    {
 
    Segment AC = new Segment(A,C);
    Segment AB = new Segment(A,B);

    // AB ^ AC

    double vect_prod = AB.vect_prod(AC);

    double AB_times_AC = AB.length() * AC.length();

    int AB_AC_prod = AB.dot_prod(AC);

    double rval = java.lang.Math.asin(vect_prod / AB_times_AC);

    if (AB_AC_prod < 0)
    {
      // symmetrical angle

      rval = java.lang.Math.PI - rval;
    }
    rval = AngleUtils.normalize_m180_180(java.lang.Math.toDegrees(rval));


    return rval;
  }

    /**
     * 
     * @param ratio 0: start -> 1: end
     * @return point of the segment accoring to ratio
     */
  public ControlPoint point(double ratio)
  {
	  double one_minus_ratio = 1.0 - ratio;
	  
	  return new ControlPoint(
			  (int)(m_start.getX()*one_minus_ratio+m_end.getX()*ratio),
			  (int)(m_start.getY()*one_minus_ratio+m_end.getY()*ratio)
	  );
  }
  public ControlPoint centre()
  {
	   return new ControlPoint(
	    		(m_end.getX()+m_start.getX())/2,
	    		(m_end.getY()+m_start.getY())/2);
 }
  public double length()
  {
    return java.lang.Math.sqrt(dot_prod(this));
  }
  public double distance_to(int x, int y, boolean use_bounding_box)
  {
    double dist = Double.MAX_VALUE;


// optimization: only compute if in the bounding box

    if ((!use_bounding_box)||(m_line_2d.getBounds().contains(x,y)))
    {
      dist = m_line_2d.ptSegDist(x,y);
    }

    return dist;
  }

  public int vect_prod(Segment other)
  {
    ControlPoint A = this.get_start_point();
    ControlPoint B = this.get_end_point();
    ControlPoint C = other.get_start_point();
    ControlPoint D = other.get_end_point();

    return ((B.getX()-A.getX())*(D.getY()-C.getY())) -
        ((B.getY()-A.getY())*(D.getX()-C.getX()));
  }
  public int dot_prod(Segment other)
  {
    return (((m_end.getX() - m_start.getX()) * (other.get_end_point().getX()
                                               - other.get_start_point().getX()))) +
       (((m_end.getY() - m_start.getY()) * (other.get_end_point().getY()
                                            - other.get_start_point().getY())));
 }
  
  public void paint(java.awt.Graphics g)
  {
	  g.drawLine(m_start.getX(),m_start.getY(),
			  m_end.getX(),m_end.getY());
  }
  
  public boolean is_rather_vertical(double threshold)
  {
	  return (Math.abs(AngleUtils.angle_difference(Math.abs(get_angle_x()), 90)) < threshold);
  }
  
  public boolean is_rather_horizontal(double threshold)
  {
	  double absangle = Math.abs(get_angle_x());
	  return ((absangle < threshold) || (absangle > 180-threshold));
  }
  
  protected ControlPoint m_start;
  protected ControlPoint m_end;
}