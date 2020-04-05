package supercars3.base;

import java.awt.Color;
import java.awt.geom.Point2D;

/**
 * <p>Titre : </p>
 * <p>Description : </p>
 * <p>Copyright : Copyright (c) 2005</p>
 * <p>Société : </p>
 * @author non attribuable
 * @version 1.0
 */

public class ControlPoint extends Selectable {
  static public final int RADIUS = 5;

  private Zone m_forced_zone = null;
  private boolean m_ambiguous = false;
  private boolean m_resume_point = false;
  private boolean m_car_start_point = false;
  private boolean m_lake_seed_point = false;

  public ControlPoint() {
  }
  
  public ControlPoint(int px, int py)
  {
    m_x = px;
    m_y = py;
  }
  void set_car_start_point(boolean rp)
  {
    if (rp != m_car_start_point)
    {
      m_car_start_point = rp;
    }
  }
  
  boolean is_ambiguous()
  {
	  return m_ambiguous;
  }
  
  void set_resume_point(boolean rp)
  {
    if (rp != m_resume_point)
    {
      m_resume_point = rp;
      /*set_changed();
      notifyObservers(this);*/
    }
  }
  public boolean is_lake_seed_point()
  {
	  return m_lake_seed_point;
  }
  
  public void set_lake_seed_point(boolean s)
  {
	  m_lake_seed_point = s;
  }
  
  public boolean is_resume_point()
  {
    return m_resume_point;
  }
  public boolean is_car_start_point()
  {
    return m_car_start_point;
  }
  public void set_forced_zone(Zone z, boolean ambiguous)
  {
    m_forced_zone = z;
    m_ambiguous = ambiguous;
  }

  public Zone get_explicit_zone()
  {
    return m_forced_zone;
  }


  public static ControlPoint barycentre(java.util.Collection<ControlPoint> points)
  {
    int nb_points = points.size();
    int x = 0, y = 0;

    for (ControlPoint p : points)
    {
    	x += p.getX();
    	y += p.getY();
    }

    return new ControlPoint(x/nb_points,y/nb_points);

  }
  public String toString()
  {
    return "(" + getX() + ',' + getY() + ')';
  }

  public boolean equalsTo(ControlPoint other)
  {
    return (getX() == other.getX()) && (getY() == other.getY());
  }

  public void paint(java.awt.Graphics g, Color c)
	{
		g.setColor(c);

		draw_circle(g, RADIUS - 2, true);
		draw_circle(g, RADIUS, false);
	
  }
  public void paint(java.awt.Graphics g)
  {
    Color c = Color.YELLOW;
    if (is_selected())
     {
       c = Color.CYAN;
     }
     else 
     {
    	 if (is_resume_point())
    	 {
    		 if (is_car_start_point())
    		 {
    			c = Color.ORANGE; 
    		 }
    		 else
    		 {
    			 c = Color.MAGENTA;
    		 }
    	 }
    	 // priority
    	 else if (is_car_start_point())
    	 {
    		 c = Color.GREEN;
    	 }
    	 else if (is_lake_seed_point())
    	 {
    		 c = Color.BLUE;
    	 }
     }
    paint(g,c);

  }


  public void set_location(int px, int py)
  {
    boolean changed = (px != m_x) || (py != m_y);

    m_x = px;
    m_y = py;

    if (changed)
    {
      set_changed();
      notifyObservers(this);
    }
  }
  public int getX() { return m_x; }
  public int getY() { return m_y; }

  public int square_distance(Point2D other)
  {
	  return square_distance((int)other.getX(), (int)other.getY());
	  	  
  }
  public int square_distance(ControlPoint other)
  {
   return square_distance(other.getX(), other.getY());
  }
  
  public int square_distance(int px, int py)
  {
   int dx = (m_x-px);
   int dy = (m_y-py);

   return dx*dx + dy*dy;
  }

  public void setX(int px)
  {
	  m_x = px;
  }
  
  public void setY(int py)
  {
	  m_y = py;
  }
  private void draw_circle(java.awt.Graphics graphics,int radius,boolean filled)
     {
       int px = getX() - radius;
       int py = getY() - radius;

       if (filled) {
         graphics.fillArc(px, py, radius * 2, radius * 2, 0, 360);
       }
       else {
         graphics.drawArc(px, py, radius * 2, radius * 2, 0, 360);
       }
     }

  protected int m_x = 0;
  protected int m_y = 0;

}