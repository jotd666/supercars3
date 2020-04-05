package supercars3.game;

import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.awt.*;

import supercars3.base.Boundary;
import supercars3.base.Zone;

/**
 * <p>
 * Titre :
 * </p>
 * <p>
 * Description :
 * </p>
 * <p>
 * Copyright : Copyright (c) 2005
 * </p>
 * <p>
 * Société :
 * </p>
 * 
 * @author non attribuable
 * @version 1.0
 */

public abstract class Mobile implements View 
{
	public final static int ALIVE = 0;
	public final static int EXPLODING = 1;
	public final static int DEAD = 2;
	
	private static final int NO_HIDDEN_ZONES = 0;
	private static final int CORNERS_ABOVE_HIDDEN = 1;
	private static final int CORNERS_BELOW_HIDDEN = 2;
	private static final int CORNERS_LEFT_TO_HIDDEN = 3;
	private static final int CORNERS_RIGHT_TO_HIDDEN = 4;
	static protected final Point2D zero = new Point2D.Double(0, 0);

	private int m_state = ALIVE;
	protected long m_elapsed_time;
	protected long m_absolute_elapsed_time;
	protected long m_absolute_last_bounce_time = 0;
	private Clip m_clip = new Clip();
	protected boolean m_accelerating = false;
	
	protected AffineTransform m_rotation = AffineTransform.getRotateInstance(0);

	protected AffineTransform m_scale = AffineTransform.getScaleInstance(0, 0);

	protected GenericParameters m_current;
	protected GenericParameters m_predicted;

	protected Point2D m_centre_offset;

	protected int m_nb_frames;
	private double m_step;
	private Color m_debug_paint = Color.RED;
	
	protected GameOptions game_options = GameOptions.instance();

	public Mobile(int nb_frames) 
	{
		m_nb_frames = nb_frames;
		m_step = 360.0 / m_nb_frames;
	}
	
	protected Color get_debug_paint()
	{
		return m_debug_paint;
	}
	
	protected void set_state(int state)
	{
		m_state = state;
	}
	protected void set_debug_paint(Color debug_paint)
	{
		m_debug_paint = debug_paint;
	}
	protected void handle_acceleration() 
	{
		// note down that we're accelerating
		
		m_accelerating = true;
		// accelerate using the position of the object
	
		double speed = m_current.get_speed();
		double accel = get_acceleration(speed);
	
		m_predicted.speed.setLocation(m_predicted.speed.getX() + accel
				* current_cos(), m_predicted.speed.getY() + accel
				* current_sin());
	
	}
	
	public abstract void zone_entered(Zone zone,Boundary by);
	
	
	 public abstract void zone_exited(Zone zone,Boundary by);
	  
	public void apply_speed() 
	{
		double speed_x = m_predicted.speed.getX();
		double speed_y = m_predicted.speed.getY();

		double linear_speed_time = get_linear_speed() * m_elapsed_time;
	
		m_predicted.location.setLocation(m_current.location.getX() + speed_x
				* linear_speed_time, m_current.location.getY() + speed_y
				* linear_speed_time);
	
	}

	public Point2D get_centre_offset() 
	{
		return m_centre_offset;
	}
	public int get_nb_frames()
	{
		return m_nb_frames;
	}
	public GenericParameters get_predicted()
	  {
		  return m_predicted;
	  }
	  public boolean is_dead()
	{
		return m_state == DEAD;
	}

	public boolean is_alive()
	{
		return m_state == ALIVE;
	}

	public int get_state()
	{
		return m_state;
	}

	public abstract void predict();

	public void render(Graphics2D g, Rectangle view_bounds) 
	{
		Zone cz = m_current.corners_shared_zone();
	
		boolean draw_something = (cz == null) || (!cz.is_hidden());
	
		if (draw_something) 
		{
			p_render(g,view_bounds);
		}
		if (game_options.debug_mode) 
		{
			// draw mobile rectangular outline

			Polygon bounds = m_current.get_bounds();
			if (bounds != null)
			{
				g.setFont(GfxUtils.SMALL_FONT);
				g.setColor(m_debug_paint);
				g.draw(bounds);
				g.setColor(Color.WHITE);
				g.drawString("spd: "+(int)(m_current.get_speed()*10),
						(int)m_current.location.x,(int)m_current.location.y);

				Corner [] cl = m_current.retrieve_corners();
				int diametre = 8;

				for (int i = 0; i < cl.length; i++)
				{
					Corner c = cl[i];
					if (c.zone.is_hidden())
					{
						g.setColor(Color.YELLOW);
					}
					else
					{
						g.setColor(Color.RED);
					}
					int x = (int)(c.location.x-diametre/2);
					int y = (int)(c.location.y-diametre/2);

					g.fillArc(x, y, diametre, diametre ,
							0, 350);
					g.drawString(c.zone.get_name()+"", x+diametre, y);

				}
				{
					int x = (int)((cl[0].location.x+cl[2].location.x)/2-diametre/2);
					int y = (int)((cl[0].location.y+cl[2].location.y)/2-diametre/2);


					g.setColor(Color.WHITE);
					g.fillArc(x,y,diametre,diametre,0,350);
				}
			}
		}
	}

	public GenericParameters get_current()
	  {
		  return m_current;
	  }
	  
	  
	  private int corner_status_on_hidden_zone(Zone hidden) 
	  {
	    int rval = NO_HIDDEN_ZONES;

	    // work on predicted because zone/corner link is valid
	    // only on predicted zones
	    
	    Corner[] corners = m_predicted.m_corners;
	    Rectangle r = hidden.get_bounding_box();
	    
	    boolean found = false;
	    int r_y_min = (int)r.getY();
	    int r_y_max = r_y_min + (int)r.getHeight();

	    // there's a hidden zone

	    for (int i = 0; i < corners.length && !found; i++) 
	    {
	    	Corner c = corners[i];
	    	Zone z = c.zone;

	    	if ((z == null) || (!z.is_hidden()))
	    	{
	    		// corner is not hidden: try to locate it

	    		if (r_y_min >= c.location.getY()) 
	    		{
	    			rval = CORNERS_ABOVE_HIDDEN;
	    		}
	    		else if (r_y_max <= c.location.getY())
	    		{
	    			rval = CORNERS_BELOW_HIDDEN;
	    		}
	    		else if (r.getX() >= c.location.getX())
	    		{
	    			rval = CORNERS_LEFT_TO_HIDDEN;
	    		}
	    		else
	    		{
	    			rval = CORNERS_RIGHT_TO_HIDDEN;
	    		}
    			found = true; // we assume there's only 1 visible zone besides hidden

	    	}
	    }
	    
	    return rval;
	  }

		private Zone get_hidden_zone() 
		{
		    Zone hidden = null;
		
		    Corner[] corners = m_predicted.m_corners;
		
		    for (int i = 0; i < corners.length && hidden == null; i++) 
		    {
		    	Zone z = corners[i].zone;
		      if ((z != null) && z.is_hidden()) 
		      {
		        hidden = z;
		      }
		    }
		    return hidden;
		  }
		


	protected abstract double get_acceleration(double x);

	protected final double current_cos() 
	{
		double angle = m_current.discreet_angle();
		return Math.cos(Math.toRadians(angle));
	}

	protected final double current_sin() 
	{
		double angle = m_current.discreet_angle();
		return Math.sin(Math.toRadians(angle));
	}

	public class Clip
	{
		public int x_min;
		public int y_min;
		public int x_max;
		public int y_max;
		
		public void set(int x1,int y1, int x2, int y2)
		{
			x_min = x1;
			x_max = x2;
			y_min = y1;
			y_max = y2;
		}
		
		public void draw_image(Graphics2D g,BufferedImage img,int x, int y)
		{
			int dx1 = Math.max(x, x_min);
			int dy1 = Math.max(y, y_min);
			int dx2 = Math.min(dx1 + img.getWidth(),x_max);
			int dy2 = Math.min(dy1 + img.getHeight(),y_max);
			
			int sx1 = dx1 - x;
			int sy1 = dy1 - y;
			int sx2 = dx2 - x;
			int sy2 = dy2 - y;
			
			g.drawImage(img,dx1,dy1,dx2,dy2,sx1,sy1,sx2,sy2,null);

		}
	}
	
	protected Clip get_clip(Rectangle view_bounds)
	{
		Clip c = m_clip;
	    Zone hidden_zone = get_hidden_zone();
	    Rectangle2D r = null;
	    c.set(0, 0, view_bounds.width, view_bounds.height);
	    
	    if (hidden_zone != null)
	    {
	    	// at least one of the corners is within a hidden zone
	    	
	    	r = hidden_zone.get_polygon().getBounds2D();
	    	
	    	switch (corner_status_on_hidden_zone(hidden_zone)) 
	    	{
	    	case CORNERS_ABOVE_HIDDEN:
	    		c.y_max = (int) r.getY();
	    		break;
	    	case CORNERS_BELOW_HIDDEN:
	    		c.y_min = (int) (r.getY() + r.getHeight());	    	
	    		break;
	    	case CORNERS_LEFT_TO_HIDDEN:
	    		c.x_max = (int) r.getX();	    		    	
	    		break;
	    	case CORNERS_RIGHT_TO_HIDDEN:
	    		c.x_min = (int) (r.getX() + r.getWidth());
	    		break;
	    		default:
	    			break;
	    	}
	    }		
	    
	    return c;
	}
	
	protected void draw_clipped_image(Graphics2D g, BufferedImage img, int x, int y)
	{
		m_clip.draw_image(g, img, x, y);
	}
	
	
	protected abstract void p_render(Graphics2D g, Rectangle view_bounds);

	protected abstract MobileImageSet get_image_set();

	protected abstract double get_linear_speed();

	public class GenericParameters 
	{
		protected Corner[] m_corners;
		public boolean skid = false;
		
		public Corner[] get_corners()
		{
			return m_corners;
		}
		
		public Corner[] retrieve_corners()
		{
			Polygon gp = get_image_set().get_frame(angle).bounds;
			if (gp != null)
			{
				for (int i = 0; i < 4; i++) // not corners.length
				{
					Point2D.Double c = m_corners[i].location;
					c.x = gp.xpoints[i] + location.x;
					c.y = gp.ypoints[i] + location.y;
				}
			}
			return m_corners;
		}
		/**
		 * create a speed vector according to current angle
		 * and a scalar speed value
		 * @param speed_value scalar speed value
		 */
		public void set_speed(double speed_value, double speed_angle)
		{
			double radians_angle = Math.toRadians(speed_angle);
			
	   		 speed.x = speed_value * Math.cos(radians_angle);
    		 speed.y = speed_value * Math.sin(radians_angle);
		}
		public double discreet_angle()
		{
			return Math.round(angle / m_step) * m_step;
		}
		
		public void add_offset(double xo, double yo)
		{
			location.x += xo;
			location.y += yo;

		}
		
		public void copy_from(GenericParameters source) 
		{
			location.setLocation(source.location);
			speed.setLocation(source.speed);
			angle = source.angle;
			
			// update corner zones (source number of corners can be
			// bigger than destination corners)

			for (int i = 0; i < m_corners.length; i++) 
			{
				Corner c1 = m_corners[i];
				Corner c2 = source.m_corners[i];

				if (c2.zone != null) {
					c1.zone = c2.zone;
				}
			}

		}

		public Polygon get_bounds() 
		{
			return get_bounds(0,0);
		}
		
		public Polygon get_bounds(double x_offset, double y_offset) 
		{
			Polygon rval = null;
			MobileImageSet mis = get_image_set();
			
			if (mis != null)
			{
				rval = new Polygon();
				Polygon orig = mis.get_frame(angle).bounds;

				for (int i = 0; i < orig.npoints; i++)
				{		
					rval.addPoint((int)(orig.xpoints[i]+location.x+x_offset), 
							(int)(orig.ypoints[i]+location.y+y_offset));
				}
			}
			return rval;
		}

		public GenericParameters(int nb_corners) 
		{
			m_corners = new Corner[nb_corners];
			
			for (int i = 0; i < m_corners.length; i++) 
			{
				m_corners[i] = new Corner();
			}
		}
		
		public boolean shared_zone(GenericParameters other)
		{
			boolean found = false;
			Corner [] other_corners = other.m_corners;
			
			// check that the cars are in the same vincinity
			// (optimizes and avoid collision of cars/weapons when
			// one is under a bridge and the other is above the bridge
			
			for (int i = 0; i < m_corners.length && !found;i++)
			{
				Zone this_corner_zone = m_corners[i].zone;
				
				for (int j = 0; j < other_corners.length && !found; j++)
				{
					found = (this_corner_zone == other_corners[j].zone);
				}
			}
			
			return found;
		}
		
		private boolean intersects(Polygon other_bounds)
		{
			for (int i = 0; i < m_corners.length; i++)  // all "corners" (including mi-segment points) 
			{
				Corner c = m_corners[i];
				if (other_bounds.contains(c.location.x,c.location.y))
				{
					return true; // stops at first intersection
				}
			}


			return false;
		}


		
		public boolean get_intersection(GenericParameters other)
		{
			boolean rval = false;
			
			if (shared_zone(other))
			{
				rval = intersects(other.get_bounds());
			}
			return rval;
		}
			
		public boolean get_intersection(GenericParameters other, Point2D offset)
		{
			boolean rval = false;
			
			if (shared_zone(other))
			{
				rval = intersects(other.get_bounds(offset.getX(),offset.getY()));
			}
			
			return rval;
		}
		
		public Zone corners_shared_zone() 
		{
			Zone rval = m_corners[0].zone;

			for (int i = 1; i < m_corners.length && rval != null; i++) 
			{
				if (m_corners[i].zone != rval) 
				{
					rval = null;
				}
			}
			return rval;
		}

		public double get_speed() {
			return speed.distance(zero);
		}

		public void set_speed(double new_speed)
		{
			speed.x = new_speed * Math.cos(Math.toRadians(angle));
			speed.y = new_speed * Math.sin(Math.toRadians(angle));
			
		}
		public void add_speed(double other_speed)
		{
			speed.x += other_speed * Math.cos(Math.toRadians(angle));
			speed.y += other_speed * Math.sin(Math.toRadians(angle));
			
		}
		public void add_speed(Point2D.Double other_speed, double coeff)
		{
			speed.x += other_speed.x * coeff;
			speed.y += other_speed.y * coeff;
		}
		public Point2D.Double location = new Point2D.Double(0.0, 0.0);

		public Point2D.Double speed = new Point2D.Double(0.0, 0.0);

		public double angle = 0.0;

	}

}