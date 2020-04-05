package supercars3.game.trains;

import java.awt.Graphics2D;
import java.awt.Rectangle;


import supercars3.base.*;
import supercars3.game.*;

public class Train
{
	private ControlPoint m_start;
	private ControlPoint m_end;
	private Wagon [] m_wagon;
	private int m_x_wagon_offset = 0;
	private int m_y_wagon_offset = 0;
	private int m_x_end_offset = 0;
	private int m_y_end_offset = 0;
	
	private OpponentProperties m_properties;
	private Wagon m_engine_wagon;
	private enum Direction { LEFT_TO_RIGHT, RIGHT_TO_LEFT, UP_TO_DOWN, DOWN_TO_UP }
	private Direction m_direction;
	private int m_reappear_counter = 0;
	private Zone m_start_zone;
	private int m_nb_wagons;
	private WagonView [] m_wagon_view;
	private CircuitChecker m_circuit_checker;
	
	private static final int NB_MAX_WAGONS = 4;
	
	public Wagon [] get_wagons()
	{
		return m_wagon;
	}
	
	
	public int get_nb_wagons()
	{
		return m_nb_wagons;
	}
	Train(WagonView [] wagon_view,
			OpponentProperties properties,Zone start_zone,Zone end_zone,CircuitChecker cc)
	{
		m_properties = properties;
		
		m_wagon_view = wagon_view;
		
		m_wagon = new Wagon[NB_MAX_WAGONS];
				
		double linear_speed = m_properties.get_train_linear_speed();
		
		m_engine_wagon = new Wagon(wagon_view[0],linear_speed);
		
		m_wagon[0] = m_engine_wagon;
		
		m_start_zone = start_zone;
		
		m_circuit_checker = cc;
		
		double speed_x = 0;
		double speed_y = 0;
		double angle = 0;
		
		// compute the angle & speed
		
		ControlPoint end = end_zone.get_centre();
		ControlPoint start = start_zone.get_centre();
		
		int delta_x = end.getX() - start.getX();
		int delta_y = end.getY() - start.getY();
		m_start = start;
		
		if (Math.abs(delta_y) < Math.abs(delta_x))
		{
			// horizontal
			m_end = new ControlPoint(end.getX(),m_start.getY());
			speed_x = linear_speed;
			speed_y = 0;
			boolean left_to_right = m_end.getX() > m_start.getX();
			angle =  left_to_right ? 0 : 180;
			
			m_x_wagon_offset = m_wagon_view[0].get_frame(0).image.getWidth();
			
			if (left_to_right)
			{
				m_start.setX(start_zone.get_extreme_point(Zone.Direction.LEFT).getX()-m_x_wagon_offset/2);
				m_end.setX(end_zone.get_extreme_point(Zone.Direction.RIGHT).getX()+m_x_wagon_offset/2);
				m_direction = Direction.LEFT_TO_RIGHT;
				m_x_wagon_offset *= -1;
			}
			else
			{
				m_start.setX(start_zone.get_extreme_point(Zone.Direction.RIGHT).getX()+m_x_wagon_offset/2);
				m_end.setX(end_zone.get_extreme_point(Zone.Direction.LEFT).getX()-m_x_wagon_offset/2);
				m_direction = Direction.RIGHT_TO_LEFT;
				speed_x *= -1;
			}
		}
		else
		{
			// vertical
			
			m_end = new ControlPoint(m_start.getX(),end.getY());
			speed_x = 0;
			speed_y = linear_speed; 
			
			boolean down_to_top = m_end.getY() < m_start.getY();
			angle =  down_to_top ? 90 : -90;
			
			m_y_wagon_offset = m_wagon_view[0].get_frame(0).image.getWidth();
			
			if (down_to_top)
			{
				m_start.setY(start_zone.get_extreme_point(Zone.Direction.DOWN).getY()+m_y_wagon_offset/2);
				m_end.setY(end_zone.get_extreme_point(Zone.Direction.UP).getY()-m_y_wagon_offset/2);
				m_direction = Direction.DOWN_TO_UP;
				speed_y *= -1;
			}
			else
			{
				m_start.setY(start_zone.get_extreme_point(Zone.Direction.UP).getY()-m_y_wagon_offset/2);
				m_end.setY(end_zone.get_extreme_point(Zone.Direction.DOWN).getY()+m_y_wagon_offset/2);
				m_direction = Direction.UP_TO_DOWN;
				m_y_wagon_offset *= -1;
			}
			
		}
		
		m_engine_wagon.init(speed_x, speed_y, angle, m_start, start_zone);
		
		for (int i = 1; i < NB_MAX_WAGONS; i++)
		{
			int index = 1;
			m_wagon[i] = new Wagon(m_wagon_view[index],linear_speed);
			m_wagon[i].get_current().copy_from(m_engine_wagon.get_current());
			m_wagon[i].get_predicted().copy_from(m_engine_wagon.get_predicted());
		}

	}	
	
	public void update(long elapsed_time)
	{
		// switch states according to direction
		
		if (m_engine_wagon.get_state() == Wagon.HIDDEN)
		{
			m_reappear_counter += elapsed_time;
			
			if (m_reappear_counter > m_properties.train_wait)
			{
				m_reappear_counter = 0;
				m_engine_wagon.set_state(Wagon.ALIVE);
				
				m_nb_wagons = (int)(Math.random()*(m_properties.max_wagons-m_properties.min_wagons+1))+m_properties.min_wagons;
				
				m_x_end_offset = (m_x_wagon_offset*(m_nb_wagons-1));
				m_y_end_offset = (m_y_wagon_offset*(m_nb_wagons-1));
				m_engine_wagon.init(m_start,m_start_zone);
				
				// select trailing wagons at random
				
				for (int i = 1; i < m_nb_wagons; i++)
				{
					int index = 1+(int)(Math.random()*(m_wagon_view.length-1));
					m_wagon[i].set_view(m_wagon_view[index]);
				}
			}
		}
		
		if (m_engine_wagon.get_state() == Wagon.ALIVE)
		{
			m_engine_wagon.set_elapsed_time(elapsed_time);
			m_engine_wagon.apply_speed();
			Mobile.GenericParameters engine_params = m_engine_wagon.get_predicted();
			
			m_engine_wagon.get_current().copy_from(engine_params);
						
			for (int i = 1; i < m_nb_wagons; i++)
			{
				Mobile.GenericParameters p = m_wagon[i].get_predicted();
				p.copy_from(m_wagon[i-1].get_predicted());
				p.location.x += m_x_wagon_offset;
				p.location.y += m_y_wagon_offset;
				m_circuit_checker.locate_predict_corners(m_wagon[i], 4);
				
				m_wagon[i].get_current().copy_from(p);
				m_wagon[i].set_state(Wagon.ALIVE);
			}

			boolean is_hidden = false;
			double x = engine_params.location.x;
			double y = engine_params.location.y;
			
			switch (m_direction)
			{
			case DOWN_TO_UP:
				is_hidden = (y < m_end.getY() - m_y_end_offset);
				break;
			case UP_TO_DOWN:
				is_hidden = (y > m_end.getY() - m_y_end_offset);
				break;
			case LEFT_TO_RIGHT:
				is_hidden = (x > m_end.getX() - m_x_end_offset);
				break;
			case RIGHT_TO_LEFT:
				is_hidden = (x < m_end.getX() - m_x_end_offset);
				break;				
			}
			if (is_hidden)
			{
				for (Wagon w : m_wagon)
				{
					w.set_state(Wagon.HIDDEN);
				}
			}
		}
			
	}


	public void render(Graphics2D g, Rectangle view_bounds)
	{
		for (int i = 0; i < m_nb_wagons; i++)
		{			
			m_wagon[i].render(g, view_bounds);
		}
	}


}
