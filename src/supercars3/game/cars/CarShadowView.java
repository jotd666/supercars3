package supercars3.game.cars;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import supercars3.game.*;

public class CarShadowView extends CarView
{
	private CarBodyView m_parent;
		
	public CarShadowView(CarBodyView parent)
	{
		m_parent = parent;
		m_centre_offset = new Point2D.Double(
				m_parent.get_centre_offset().getX()-3,
				m_parent.get_centre_offset().getY()-3);
		
		for (int i = 0; i < m_image_set.length; i++)
		{
			MobileImageSet mis = m_parent.get_image_set(i);
			
			BufferedImage [] image = new BufferedImage[NB_TOTAL_FRAMES];
			
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
			
			m_image_set[i] = new MobileImageSet(image);
		}
	}

}
