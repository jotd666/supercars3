package supercars3.game.cars;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;

import com.golden.gamedev.engine.*;
import supercars3.game.MobileImageSet;
import supercars3.sys.ParameterParser;

public class CarBodyView extends CarView {


	private String m_name;
	private String m_directory;
	private double m_v_scale;
	private double m_h_scale;
	private double m_brightness;
	private double m_green_threshold;
	private int l;
	private int r;
	private int t;
	private int b;
	private int hm;
	private int vm;
	private Rectangle2D m_detection_bounds;
	private int m_width, m_height;
	private Color m_paint = Color.GRAY;
	
	public Color get_paint()
	{
		return m_paint;
	}
	public int get_width()
	{
		return m_width;
	}
	
	public int get_height()
	{
		return m_height;
	}

	public String get_name()
	{
		return m_name;
	}
	public CarBodyView(BaseLoader bsl,String directory,Color paint)
	throws java.io.IOException 
	{
		this(directory);
		select(bsl,paint);
	}
	public CarBodyView(String directory)
			throws java.io.IOException 
			{

		m_directory = "cars" + File.separatorChar + directory
		+ File.separatorChar;

		ParameterParser p = ParameterParser.open(m_directory
				+ File.separatorChar + "info.sc3");

		p.startBlockVerify("CAR_DESCRIPTION");
		m_name = p.readString("name");
		p.startBlockVerify("geometry");
		l = p.readInteger("left");
		r = p.readInteger("right");
		t = p.readInteger("top");
		b = p.readInteger("bottom");
		hm = p.readInteger("h_margin");
		vm = p.readInteger("v_margin");
		m_h_scale = p.readFloat("h_scale");
		m_v_scale = p.readFloat("v_scale");
		m_brightness = p.readFloat("brightness");
		m_green_threshold = p.readFloat("green_threshold");
		
		p.endBlockVerify();
		p.endBlockVerify();

		m_detection_bounds = new Rectangle2D.Double((l - hm) * m_h_scale,
				(t - vm) * m_v_scale, (r - l + hm * 2) * m_h_scale,
				(b - t + vm * 2) * m_v_scale);
		m_width = (int)(m_detection_bounds.getWidth());
		m_height = (int)(m_detection_bounds.getHeight());

	}
	
	private void select(BaseLoader bsl,Color paint_color)
	{
		int red_paint = paint_color.getRed();
		int blue_paint = paint_color.getBlue();
		int green_paint = paint_color.getGreen();
		
		m_paint = paint_color;
		
		bsl.setMaskColor(Color.BLUE);
		

		m_centre_offset = new Point2D.Double(m_detection_bounds.getCenterX(), 
				m_detection_bounds.getCenterY());

		// load the images

		for (int i = 0; i < IMAGE_NAME_ARRAY.length; i++) 
		{
			String car_file = m_directory + IMAGE_NAME_ARRAY[i] + ".png";
			BufferedImage bi = bsl.getImage(car_file, true);

			BufferedImage rgb_image = new BufferedImage(bi.getWidth(), bi
					.getHeight(), BufferedImage.TYPE_INT_ARGB);
			rgb_image.getGraphics().drawImage(bi, 0, 0, null);


			for (int x = 0; x < rgb_image.getWidth(); x++) 
			{
				for (int y = 0; y < rgb_image.getHeight(); y++) 
				{
					int rgb = rgb_image.getRGB(x, y);
					int alpha = (rgb & 0xFF000000);
					int red = (rgb & 0xFF0000) >> 16;
				    int green = (rgb & 0x00FF00) >> 8;
		            //int blue = rgb & 0x0000FF;
		            
		            if (green > m_green_threshold * red)
		            {
		            	rgb = (((green * red_paint) / 255) << 16) +
		            	(((green * green_paint) / 255) << 8) + (green * blue_paint) / 255;
		            			            	
		            }
           
		            rgb_image.setRGB(x, y, alpha | rgb);
				}

			}
			
			bi = rgb_image;
				//bi.getGraphics().drawImage(rgb_image, 0, 0, null);				
			

			m_image_set[i] = new MobileImageSet(bi, m_detection_bounds, NB_TOTAL_FRAMES,
					m_h_scale, m_v_scale,m_brightness);

		}

	}
}
