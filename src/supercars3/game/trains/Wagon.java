package supercars3.game.trains;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.Polygon;

import supercars3.base.Boundary;
import supercars3.base.ControlPoint;
import supercars3.base.Zone;
import supercars3.game.*;
import supercars3.game.cars.*;


public class Wagon extends Mobile
{
	protected final GameOptions.ReadOnly ro_settings = GameOptions.instance().read_only;
	private WagonView m_view;
	private WagonShadowView m_shadow_view;
	private double m_linear_speed;
	public final static int HIDDEN = 1; // same as exploding, for train

	public Wagon(WagonView wv, double linear_speed)
	{
		super(4); // 4 frames
		m_view = wv;
		m_shadow_view = new WagonShadowView(m_view);
		m_linear_speed = linear_speed;
		
		m_current = new GenericParameters(4);
		m_predicted = new GenericParameters(4);
		
		set_state(HIDDEN);
	}
	public void set_state(int state)
	{
		super.set_state(state);
	}
	void set_view(WagonView wv)
	{
		m_view = wv;
	}
	
	@Override
	protected double get_acceleration(double x)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected MobileImageSet get_image_set()
	{
		// TODO Auto-generated method stub
		return m_view;
	}

	@Override
	protected double get_linear_speed()
	{
		return m_linear_speed;
	}

	@Override
	protected void p_render(Graphics2D g, Rectangle view_bounds)
	{
		  BufferedImage body_image = null;
		  BufferedImage shadow_image = null;
		  
		  switch (get_state())
		  {
		  case HIDDEN:
		  
		  break;
		  case ALIVE:
			  // manage hidden zones
			  
			  get_clip(view_bounds);
			  
			  
			  MobileImageSet image_set = get_image_set();
			  ImageBounds ib = image_set.get_frame(m_current.angle);
			  
			  body_image = ib.image;
			  
			  shadow_image = m_shadow_view.get_image_set().get_frame(m_current.angle).image;
		  		  
			  break;
			  default:
		  }
		  
		  if (body_image != null)
		  {
			  draw_clipped_view(g,shadow_image,m_shadow_view);
			  draw_clipped_view(g,body_image,m_view);

		  }
		 
	}

	private void draw_clipped_view(Graphics2D g, BufferedImage image, View view)
	{
		  Point2D centre_offset = view.get_centre_offset();
		  int x = (int)
		  (Math.round(m_current.location.getX() - centre_offset.getX()));
		  int y = (int) (Math.round(m_current.location.getY() -
				  centre_offset.getY()));

		  draw_clipped_image(g,image,x,y);

	}
	
	
	public void set_elapsed_time(long et)
	{
		m_elapsed_time = et;
	}
	
	@Override
	public void predict()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void zone_entered(Zone zone, Boundary by)
	{
		// TODO Auto-generated method stub

	}
	public void check_collisions(CarSet car_set)
	{
		Polygon p = get_current().get_bounds();
		
		for (int i = 0; i < car_set.size(); i++)
		{
			Car c = car_set.get_item(i);
			if ((c.is_alive()) && !c.is_jumping_high_enough())
			{
				if (get_current().shared_zone(c.get_current()))
				{
					Corner [] corners = c.get_current().get_corners();
					
					for (Corner corner : corners)
					{
						if (p.contains(corner.location))
						{
							c.die(false,ro_settings.train_damage);
							break;					
						}
					}
				}
			}
		}
	}
	@Override
	public void zone_exited(Zone zone, Boundary by)
	{

	}

	void init(double speed_x, double speed_y, double angle, ControlPoint location,Zone start_zone)
	{
		get_predicted().angle = angle;
		get_predicted().speed.x = speed_x; 
		get_predicted().speed.y = speed_y; 
		
		init(location,start_zone);
	}
	
	void init(ControlPoint location,Zone start_zone)
	{
		get_predicted().location.setLocation(location.getX(),location.getY());
		
		Corner [] cl = get_predicted().retrieve_corners();
		for (Corner c : cl)
		{
			c.zone = start_zone;
		}
		
		get_current().copy_from(get_predicted());		
	}
}
