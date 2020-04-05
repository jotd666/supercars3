package supercars3.game.weapons;


import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.*;

import supercars3.game.MultipleView;
import supercars3.game.MobileImageSet;

public class WeaponView implements MultipleView
{
	private int m_nb_frames;
	private Point2D m_centre_offset = new Point2D.Double();	
	
	protected MobileImageSet[] m_image_set;
	
	public int get_nb_frames()
	{
		return m_nb_frames;
	}
	
	public int get_nb_image_sets()
	{
		return m_image_set.length;
	}
	
	public MobileImageSet get_image_set(int i)
	{
		return m_image_set[i];
	}
	
	public Point2D get_centre_offset() 
	{
		return m_centre_offset;
	}
	
	public WeaponView(BufferedImage [] bi, int nb_rotation_frames, int h_offset,int v_offset)
	{					
		m_image_set = new MobileImageSet[bi.length];
		m_nb_frames = nb_rotation_frames;
		
		int w = bi[0].getWidth();
		int h = bi[0].getHeight();
		
		int l = h_offset;
		int t = v_offset;
		int r = w - l;
		int b = h - t;
		
		Rectangle2D detection_bounds = new Rectangle2D.Double(l,
				t , (r - l) ,
				(b - t) );

		m_centre_offset.setLocation(w / 2, h / 2);
		
		for (int i = 0; i < m_image_set.length; i++)
		{
			m_image_set[i] = new MobileImageSet(bi[i],
					detection_bounds,
					nb_rotation_frames,
					1.0, 1.0, 1.0); 
		}
	}
	
	
	 
}
