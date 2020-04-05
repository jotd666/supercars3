package supercars3.game.trains;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import supercars3.game.*;

public class WagonShadowView implements View
{
	private WagonView m_parent;
		
	protected MobileImageSet m_image_set;
	

	protected Point2D m_centre_offset;
		
	public MobileImageSet get_image_set()
	{
		return m_image_set;
	}
	
	public Point2D get_centre_offset() 
	{
		return m_centre_offset;
	}
	public int get_nb_frames()
	{
		return 4;
	}
	

	public WagonShadowView(WagonView parent)
	{
		m_parent = parent;
		m_centre_offset = new Point2D.Double(
				m_parent.get_centre_offset().getX()-8,
				m_parent.get_centre_offset().getY()-8);
		

			MobileImageSet mis = parent;
			
			BufferedImage [] image = new BufferedImage[get_nb_frames()];
			
			for (int j = 0; j < image.length; j++)
			{
				BufferedImage bi = mis.get_frames()[j].image;
				
				BufferedImage rgb_image = new BufferedImage(bi.getWidth(), bi
						.getHeight(), BufferedImage.TYPE_INT_ARGB);
				
				rgb_image.getGraphics().drawImage(bi, 0, 0, null);

				for (int x = 0; x < rgb_image.getWidth(); x++) 
				{
					for (int y = 0; y < rgb_image.getHeight(); y++) 
					{
						int rgb = rgb_image.getRGB(x, y);
						// half-bright : divide by 2
						int alpha = (rgb & 0xFF000000);

						if (alpha != 0)
						{
							alpha = 0x77000000;
						}
						
						rgb_image.setRGB(x, y, alpha);
					}
				}
				image[j] = rgb_image;
			}
			
			m_image_set = new MobileImageSet(image);
		}	

}
