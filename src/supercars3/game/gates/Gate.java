package supercars3.game.gates;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Point2D;


import supercars3.base.Boundary;
import supercars3.base.Zone;
import supercars3.game.Mobile;
import supercars3.game.MobileImageSet;

public class Gate extends Mobile
{
	private GateView m_view = null;
	private int m_offset;
	private Rectangle m_dest_bounds = new Rectangle();

	private int sx1;
	private int sy1;
	private int sx2;
	private int sy2;
	
	Gate(GateView wv)
	{
		super(wv.get_nb_frames());

		m_view = wv;
		m_current = new GenericParameters(4);
		m_predicted = new GenericParameters(4);



	}	
	
	
	void set_offset(int offset)
	{
		m_offset = offset;

		int dx1 = (int)m_current.location.x;
		int dy1 = (int)m_current.location.y;
		int dx2 = dx1 + get_width();
		int dy2 = dy1 + get_height();
		
		sx1 = m_offset;
		sy1 = 0;
		sx2 = get_width();
		sy2 = get_height();
		
		if (m_offset > 0)
		{
			dx2 -= m_offset;
		}
		else
		{
			dx1 -= m_offset;
			sx1 = 0;
			sx2 += m_offset;
		}
		
		m_dest_bounds.x = dx1;
		m_dest_bounds.y = dy1;
		m_dest_bounds.width = dx2 - dx1;
		m_dest_bounds.height = dy2 - dy1;
		
	}
	
	boolean contains(Point2D.Double p)
	{		
		return m_dest_bounds.contains(p);
	}
	
	public int get_width()
	{
		return m_view.get_frames()[0].image.getWidth();
	}
	public int get_height()
	{
		return m_view.get_frames()[0].image.getHeight();
	}
	
	@Override
	protected double get_acceleration(double x)
	{
		return 0;
	}

	@Override
	protected MobileImageSet get_image_set()
	{
		return null;
	}

	@Override
	protected double get_linear_speed()
	{
		return 0;
	}

	@Override 
	protected void p_render(Graphics2D g, Rectangle view_bounds)
	{

		
		g.drawImage(m_view.get_frame(m_current.angle).image,
				m_dest_bounds.x,
				m_dest_bounds.y,
				m_dest_bounds.width + m_dest_bounds.x,
				m_dest_bounds.height + m_dest_bounds.y,
				sx1,
				sy1,
				sx2,
				sy2,
				null);
	}

	@Override
	public void predict()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void zone_entered(Zone zone, Boundary by)
	{		

	}

	@Override
	public void zone_exited(Zone zone, Boundary by)
	{
	
	}

}
