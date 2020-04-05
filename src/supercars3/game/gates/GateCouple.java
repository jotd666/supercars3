package supercars3.game.gates;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Point2D;

import supercars3.base.*;

public class GateCouple
{
	private Gate m_left;
	private Gate m_right;
	private int m_vertical_position;
	private int m_left_horizontal_position;
	private int m_right_horizontal_position;
	
	private static final int OPEN = 1, CLOSED = 2, OPENING = 3, CLOSING = 4;
	
	private static final int OPEN_CLOSE_TIME = 3000;
	
	private int m_state;
	private int m_current_time;
	private int m_offset;
	private int m_max_offset;
	
	public boolean is_closing()
	{
		return m_state == CLOSING;
	}
	public GateCouple(Zone z,Gate left, Gate right)
	{
		m_left = left;
		m_right = right;
		
		ControlPoint centre = z.get_centre();

		// we assume that the creator of the circuit has selected a nice square
		// well-fitting zone for the gates
		
		Segment s0 = z.get_boundary(0);
		Segment s1 = z.get_boundary(1);
			
		int delta_x_0 = Math.abs(s0.get_start_point().getX() - s0.get_end_point().getX());
		int delta_x_1 = Math.abs(s1.get_start_point().getX() - s1.get_end_point().getX());
		
		int opposite_index = 3;
		Segment vertical_segment = s1;
		
		if (delta_x_0 < delta_x_1)
		{
			// segment 0 is a "vertical" segment
			opposite_index = 2;
			vertical_segment = s0;
		}

		Segment si = z.get_boundary(opposite_index);
		
		// gates are located on the biggest rectangle of the zone
		
		m_left_horizontal_position = Math.max(si.get_start_point().getX(),
				si.get_end_point().getX());				
		m_right_horizontal_position = Math.min(vertical_segment.get_start_point().getX(),
				vertical_segment.get_end_point().getX());
		
		if (m_left_horizontal_position > m_right_horizontal_position)
		{
			m_right_horizontal_position = Math.min(si.get_start_point().getX(),
					si.get_end_point().getX());				
			m_left_horizontal_position = Math.max(vertical_segment.get_start_point().getX(),
					vertical_segment.get_end_point().getX());
					
		}

		m_max_offset = (m_right_horizontal_position - m_left_horizontal_position) / 2;

		m_right_horizontal_position -= m_max_offset;
		
		m_vertical_position = centre.getY() - (m_left.get_height() / 2);
		
		// constant values
		m_left.get_current().angle = 0;
		m_right.get_current().angle = 0;
		m_left.get_current().location.setLocation(m_left_horizontal_position,m_vertical_position);
		m_right.get_current().location.setLocation(m_right_horizontal_position ,m_vertical_position);
		
		m_state = CLOSED;
		m_current_time = 0;
		m_offset = 0;
	}
	
	public boolean contains(Point2D.Double p)
	{
		return (m_left.contains(p) || m_right.contains(p));
	}
	
	public void update(long elapsed_time)
	{		
		m_current_time += elapsed_time;
		
		switch (m_state)
		{
		case CLOSED:
			if (m_current_time > OPEN_CLOSE_TIME)
			{
				m_current_time = 0;
				m_state = OPENING;
			}
			break;
		case OPEN:
			if (m_current_time > OPEN_CLOSE_TIME)
			{
				m_current_time = 0;
				m_state = CLOSING;				
			}
			break;
		case CLOSING:		
			m_offset -= (elapsed_time / 10);
			if (m_offset < 0)
			{
				m_offset = 0;
				m_state = CLOSED;
			}			
			break;
		case OPENING:
		
			m_offset += (elapsed_time / 10);
			if (m_offset > m_max_offset)
			{
				m_offset = m_max_offset;
				m_state = OPEN;
			}			
			break;
			
		}
		m_left.set_offset(m_offset);
		m_right.set_offset(-m_offset);
		
	}
	
	public void render(Graphics2D g,Rectangle view_bounds)
	{
		// optimization/avoid trouble: if open, don't draw anything
		
		if (m_state != OPEN)
		{
			m_left.render(g, null);
			m_right.render(g, null);
		}
	}
}