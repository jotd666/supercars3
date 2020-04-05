package supercars3.game;

import java.awt.image.BufferedImage;
import java.awt.Color;

import com.golden.gamedev.engine.*;
import com.golden.gamedev.util.ImageUtil;

import java.io.File;

public class ExplosionView
{
	
	private static final String FRAME_PREFIX = 
			"sprites"+File.separator+"explosion"+File.separator;
	
	private BufferedImage[] m_image_set;
	private int m_nb_frames;
	
	public BufferedImage get_frame(int i)
	{
		return m_image_set[i];
	}
	
	protected ExplosionView(BaseLoader bsl, Color mask_color, String subdir, int nb_frames, double scale)
	{
		this(bsl,mask_color, subdir, nb_frames);
		for (int i = 0; i < m_nb_frames; i++)
		{
			BufferedImage bi = m_image_set[i];
			m_image_set[i] = ImageUtil.resize(bi,
					(int)(bi.getWidth()*scale),
					(int)(bi.getHeight()*scale));
		}
	}
	
	protected ExplosionView(BaseLoader bsl, Color mask_color, String subdir, int nb_frames)
	{
		bsl.setMaskColor(mask_color);
		
		m_nb_frames = nb_frames;
		m_image_set = new BufferedImage[m_nb_frames];
		                                
		for (int i = 0; i < m_nb_frames; i++)
		{
			m_image_set[i] = bsl.getImage(FRAME_PREFIX+subdir+File.separator+(i+1)+".png",true);
			
		}
	}

}
