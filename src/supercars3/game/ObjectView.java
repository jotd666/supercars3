package supercars3.game;

import java.awt.geom.*;
import java.awt.image.*;

import supercars3.game.MobileImageSet;
import supercars3.game.View;

public class ObjectView extends MobileImageSet implements View
{
	private Point2D m_centre_offset = new Point2D.Double();	

	public Point2D get_centre_offset() 
	{
		return m_centre_offset;
	}
	
	public ObjectView(BufferedImage bi, int nb_rotation_frames, double x_scale,double y_scale, 
			int h_offset,int v_offset, double brightness)
	{		
		super(bi,new Rectangle2D.Double(h_offset,v_offset,
				(bi.getWidth()-h_offset*2) * x_scale,
				((bi.getHeight()-v_offset*2)) * y_scale), nb_rotation_frames, x_scale, y_scale,brightness);
		
		m_centre_offset.setLocation(bi.getWidth() * x_scale / 2,
				bi.getHeight() * y_scale / 2);
		
	}
	
}
