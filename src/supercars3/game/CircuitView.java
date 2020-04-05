package supercars3.game;

import java.awt.*;
import java.awt.image.*;
import java.awt.event.KeyEvent;
import java.awt.geom.*;

import com.golden.gamedev.engine.*;
import java.util.*;

import supercars3.base.Zone;
import supercars3.game.cars.Car;
import supercars3.game.cars.CarSet;
import supercars3.game.gates.GateSet;
import supercars3.game.trains.TrainSet;

/**
 * <p>Titre : </p>
 * <p>Description : </p>
 * <p>Copyright : Copyright (c) 2005</p>
 * <p>Société : </p>
 * @author non attribuable
 * @version 1.0
 */

public class CircuitView 
{
	private Rectangle m_bounds;
	private Rectangle m_shifted_bounds = new Rectangle();
	private CarSet m_car_set;
    private GateSet m_gate_set;
    private TrainSet m_train_set;
    private LakeImageSet m_lake_image_set;
	private Car m_car_viewed;
	private BaseInput m_input;
	private DashBoard m_dashboard;
	private BufferedImage m_circuit_image;
	private BufferedImage m_shadow_image;
	private Collection<Zone> m_top_priority_zones;
	private double m_half_width;
	private double m_half_height;
	private AffineTransform m_translation = AffineTransform.getTranslateInstance(0,0);
    private final double dashboard_limit = 0.85;
    /*private double m_old_x = Double.MAX_VALUE;
    private double m_old_y = Double.MAX_VALUE;*/
    
    public CircuitView(CarSet cars, 
    		GateSet gates, 
    		TrainSet trains, LakeImageSet lakes,
    		Car car_viewed,
    		BaseInput bsInput,
    		BufferedImage circuit_image,
    		BufferedImage shadow_image,
    		Collection<Zone> top_pri_zones,
    		int x, int y,
    		int width,int height,
    		boolean dual_player)
  {

    m_top_priority_zones = top_pri_zones;
    
    m_gate_set = gates;
    m_train_set = trains;
    m_car_set = cars;
    m_car_viewed = car_viewed;
    m_lake_image_set = lakes;
    m_input = bsInput;
    
    m_dashboard =  new DashBoard(m_car_viewed, new Rectangle(x,
                                               (int) (height * dashboard_limit),
                                               width,
                                               (int)(height * (1 - dashboard_limit))),dual_player);

      m_bounds = new Rectangle(x, y, width,(int) (height * dashboard_limit));
      m_circuit_image = circuit_image;
      m_shadow_image = shadow_image;
      
      m_half_width = (m_bounds.getWidth()/2.0);
      m_half_height = (m_bounds.getHeight()/2.0);
           
  }

    public void update_cheat_keys(boolean cpu_only_mode)
    {
    	Car c = m_car_viewed;

    	if (m_input.isKeyPressed(KeyEvent.VK_K))
    	{
    		if (c.get_state()!=Mobile.ALIVE)
    		{
    			// kill once more: another opponent finishes the race
    			int i;
    			for (i=0;i<m_car_set.size();i++)
    			{
    				Car other = m_car_set.get_item(i);
    				if (other.is_alive())
    				{
    					// make other car win
    					other.set_state(Car.WINNER);
    					break;
    				}
    			}
    		}
    		else
    		{
    			c.die(false,0);
    		}
    	}
    	else if (m_input.isKeyPressed(KeyEvent.VK_G))
    	{
    		c.die(true,0);
    	}
    	else if (m_input.isKeyPressed(KeyEvent.VK_D))
    	{
    		c.do_damage(10);
    	}

    	else if (m_input.isKeyPressed(KeyEvent.VK_ENTER))
    	{
    		c.lap_completed();
    	}
    	else if (cpu_only_mode && m_input.isKeyPressed(KeyEvent.VK_C))
    	{
    		// change focus car
    		int pos = m_car_viewed.get_position()-1;
    		pos++;
    		if (pos == m_car_set.size())
    		{
    			pos = 0;
    		}
    		m_car_viewed = m_car_set.get_item(pos);
    		m_dashboard.set_car(m_car_viewed);
    	}
    }
    
  public void render(Graphics2D g)
  {
	  m_translation.setToTranslation(0,0);
	  
	  g.setTransform(m_translation);
	  
	  // save clip for next view
	  
	 Shape s = g.getClip();
	  
    // set circuit view limits

    g.setClip(m_bounds);

    // try to center the circuit view on the car whenever possible

    Point2D location = m_car_viewed.get_current().location;

    double car_x = location.getX();
    double car_y = location.getY();
/*
    if (m_old_x == Double.MAX_VALUE)
    {
       	m_old_x = car_x;
       	m_old_y = car_y;        
    }
    
    else
    {
    	// avoid too much screen shake
    	
    	if (Math.abs(m_old_x - car_x) > 10.0)
    	{
    		car_x = (car_x + m_old_x)/2;
    	}
    	if (Math.abs(m_old_y - car_y) > 10.0)
    	{
    		car_y = (car_y + m_old_y)/2;
    	}
    }
    
    m_old_x = car_x;
    m_old_y = car_y;
    */
    
    double trans_x = Math.min(Math.max(car_x - m_half_width,0.0),
    		m_circuit_image.getWidth()-m_bounds.getWidth());
    double trans_y = Math.min(Math.max(car_y - m_half_height,0.0),
    		m_circuit_image.getHeight()-m_bounds.getHeight());

    m_shifted_bounds.setBounds((int)m_bounds.getX(),
    		(int)m_bounds.getY(),
    		(int)(m_bounds.getWidth()+trans_x),
    		(int)(m_bounds.getHeight()+trans_y));

    m_translation.setToTranslation(m_bounds.getX()-trans_x,m_bounds.getY()-trans_y);

    g.setTransform(m_translation);

    g.drawImage(m_circuit_image, 0, 0, null);

    // draw the lakes
    
    m_lake_image_set.render(g);

    // draw the cars that are not jumping
    
    m_car_set.render(g,m_shifted_bounds,false);

    // draw the trains
    
    m_train_set.render(g, m_shifted_bounds);

    // draw the shadows
    
    if (m_shadow_image != null)
    {
    	g.drawImage(m_shadow_image, 0, 0, null);
    }
    
    // draw the gates
    
    m_gate_set.render(g, m_shifted_bounds);
       
    // draw the cars that are jumping
    
    m_car_set.render(g,m_shifted_bounds,true);
        
    // draw top priority
    
    if (!m_top_priority_zones.isEmpty())
    {
    	for (Zone z : m_top_priority_zones)
    	{
    		g.setClip(z.get_polygon());
    	    g.drawImage(m_circuit_image, 0, 0, null);  	
    	}
    }
    
    // restore old clip
    
    g.setClip(s);
   

    // draw the dashboard

    m_dashboard.render(g);
    
   }

  public Rectangle get_image_bounds()
  {
	  return new Rectangle(0,0,m_circuit_image.getWidth(),
			  (int)(m_circuit_image.getHeight() / dashboard_limit));
  }
  public Rectangle get_bounds()
  {
	  return m_bounds;
  }

  public void set_bounds(Rectangle m_bounds)
  {
	  this.m_bounds = m_bounds;
  }

}