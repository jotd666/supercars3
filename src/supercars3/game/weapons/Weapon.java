package supercars3.game.weapons;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import supercars3.base.Boundary;
import supercars3.base.Zone;
import supercars3.game.*;
import supercars3.game.cars.Car;
import supercars3.game.cars.CarSet;

/**
 * <p>Titre : </p>
 * <p>Description : </p>
 * <p>Copyright : Copyright (c) 2005</p>
 * <p>Société : </p>
 * @author non attribuable
 * @version 1.0
 */

public abstract class Weapon extends Mobile 
{
	private WeaponView m_view = null;
	protected Car m_launcher;
	protected CircuitChecker m_circuit_checker;
	private int m_explosion_timer = 0;
	private ExplosionView m_explosion_view;
	private int m_animation_counter = 0;
	private int m_frame_counter = 0;
	private int m_frame_increment = 1;
	private int m_nb_animation_frames;
	private int m_explosion_frame = 0;

	protected double get_linear_speed() {
		return 0;
	}
	protected double get_acceleration(double speed) {
		return 0;
	}		
	  
	public void update(long elapsed_time)
	{
		m_predicted.copy_from(m_current);
		m_elapsed_time = elapsed_time;

		if (m_nb_animation_frames > 1)
		{
			m_animation_counter += elapsed_time;

			while (m_animation_counter > 50)
			{
				m_animation_counter -= 50;
				m_frame_counter += m_frame_increment;

				if (m_frame_counter == m_nb_animation_frames - 1)
				{
					m_frame_increment = -1;
				}
				else if (m_frame_counter == 0)
				{
					m_frame_increment = 1;
				}
			}
		}
		
		if (get_state() == EXPLODING)
		{
			m_explosion_timer += elapsed_time;
			m_explosion_frame = m_explosion_timer / 100;
			if (m_explosion_frame >= MissileExplosionView.NB_FRAMES)
			{
				set_state(DEAD);
				m_explosion_frame = 0;
			}
		}
		if (get_state() != DEAD)
		{
			p_update();
		}
	}

	/**
	 * default behaviour for weapon out of bounds
	 */
	public void out_of_bounds()
	{
		if (is_alive())
		{
			detonate();
		}
	}

	public void zone_entered(Zone zone,Boundary by)
	{

	}
	
	 public void zone_exited(Zone zone,Boundary by)
	 {
		
	 }
	  
	
	public void detonate()
	{
		set_state(EXPLODING);
		// damp speed
		m_current.speed.x /= 2;
		m_current.speed.y /= 2;
		m_predicted.copy_from(m_current);
		
	}
	public void car_collision()
	{
		detonate();
	}
	
	protected MobileImageSet get_image_set()
	{
		return m_view.get_image_set(m_frame_counter);
	}
	
	protected abstract void p_update();
	
	public boolean check_collisions(CarSet car_set)
	{
		boolean found = false;
		GenericParameters predicted = get_predicted();
		
		for (int i = 0; i < car_set.size() && !found; i++)
		{
			Car c = car_set.get_item(i);
			
			// kill car if not launcher car and car is alive
			
			if (c.is_alive() && (c != m_launcher))
			{
				// check collisions and kill car &
				// notify weapon about the killing
				
				//int cind = c.get_current().get_intersection(predicted);
				// since weapon is generally smaller than the car, the intersection
				// is computed this way, not the other
				
				found = predicted.get_intersection(c.get_current());
				
				if (found)
				{
					c.weapon_collision(m_launcher);
					car_collision();
				}
			}
		}
		
		return found;
	}
	

	
  protected void p_render(Graphics2D g, Rectangle view_bounds)
  {
	  
	  BufferedImage bi = null;
	  
	  switch (get_state())
	  {
	  case EXPLODING:
		  get_clip(view_bounds);

		  bi = m_explosion_view.get_frame(m_explosion_frame);
		  	  
	  break;
	  case ALIVE:
		  // manage hidden zones
		  
		  get_clip(view_bounds);
		  
		  
		  MobileImageSet image_set = get_image_set();
		  ImageBounds ib = image_set.get_frame(m_current.angle);
		  
		  bi = ib.image;
		  
	  
		  break;
		  default:
	  }
	  
	  if (bi != null)
	  {
		  Point2D centre_offset = m_view.get_centre_offset();
		  draw_clipped_image(g, bi, (int)
				  (Math.round(m_current.location.getX() - centre_offset.getX())),
				  (int) (Math.round(m_current.location.getY() -
						  centre_offset.getY())));
	  }
	 
	  
	  
  }
  
  public Weapon(Car launcher,WeaponView wv, 
		  ExplosionView ev)
  {
	  super(wv.get_nb_frames());

	  m_launcher = launcher;
	  m_circuit_checker = launcher.get_circuit_checker();
	  m_view = wv;
	  m_explosion_view = ev;
	  m_current = new GenericParameters(4);
	  m_predicted = new GenericParameters(4);
	  
	  // same characteristics as the launcher
	  
	  m_current.copy_from(launcher.get_current());
	  m_predicted.copy_from(m_current);
	  
	  m_nb_animation_frames = wv.get_nb_image_sets();
	  			  

  }
}