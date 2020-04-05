package supercars3.game;

import java.awt.geom.AffineTransform;
import java.awt.Polygon;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import com.golden.gamedev.util.ImageUtil;

public class MobileImageSet 
{
	protected AffineTransform m_rotation = AffineTransform.getRotateInstance(0);

	protected AffineTransform m_translation = AffineTransform
			.getTranslateInstance(0, 0);

	protected AffineTransform m_scale = AffineTransform.getScaleInstance(0, 0);

	private ImageBounds[] m_frame;

	private int m_nb_frames;

	private double m_step;
	
	public int get_nb_frames()
	{
		return m_nb_frames;
	}
	
	/**
	 * creation with direct frames, no bounds detection
	 * @param images
	 */
	
	public MobileImageSet(BufferedImage [] images)
	{
		m_nb_frames = images.length;

		m_frame = new ImageBounds[m_nb_frames];
		m_step = 360.0 / m_nb_frames;
		
		for (int i = 0; i < m_nb_frames; i++) 
		{
			m_frame[i] = new ImageBounds();
			m_frame[i].image = images[i];
			m_frame[i].frame_index = i;
		}
		
	}

	public MobileImageSet(BufferedImage bi,
			Rectangle2D detection_bounds,
			int nb_frames,
			double h_scale, double v_scale,double brightness) 
	{		
		m_nb_frames = nb_frames;

		m_frame = new ImageBounds[m_nb_frames];
		m_step = 360.0 / m_nb_frames;

		Point2D offset = new Point2D.Double(detection_bounds.getCenterX(),
				detection_bounds.getCenterY());
		double[] src_points = new double[8];
		double[] dest_points = new double[8];

		
		for (int i = 0; i < m_nb_frames; i++) 
		{
			BufferedImage bout = ImageUtil.resize(bi,
					(int) (bi.getWidth() * h_scale),
					(int) (bi.getHeight() * v_scale));

			m_frame[i] = new ImageBounds();

			m_frame[i].image = ImageUtil.rotate(bout, (int) (m_step * i));
			m_frame[i].frame_index = i;

			m_rotation.setToRotation(Math.toRadians(m_step * i));
			int j = 0;
			src_points[j++] = detection_bounds.getX();
			src_points[j++] = detection_bounds.getY();
			src_points[j++] = detection_bounds.getX()
					+ detection_bounds.getWidth();
			src_points[j++] = detection_bounds.getY();
			src_points[j++] = detection_bounds.getX()
					+ detection_bounds.getWidth();
			src_points[j++] = detection_bounds.getY()
					+ detection_bounds.getHeight();
			src_points[j++] = detection_bounds.getX();
			src_points[j++] = detection_bounds.getY()
					+ detection_bounds.getHeight();

			AffineTransform
					.getTranslateInstance(-offset.getX(), -offset.getY())
					.transform(src_points, 0, src_points, 0, 4);

			m_rotation.transform(src_points, 0, dest_points, 0, 4);

			Polygon gp = new Polygon();
			m_frame[i].bounds = gp;

			for (int k = 0; k < 4; k++) 
			{				
				gp.addPoint((int) dest_points[k * 2], (int) dest_points[k * 2 + 1]);
			}
			
			if (brightness < 1.0)
			{
				BufferedImage bif = m_frame[i].image;

				BufferedImage rgb_image = new BufferedImage(bif.getWidth(), bif
						.getHeight(), BufferedImage.TYPE_INT_ARGB);
				rgb_image.getGraphics().drawImage(bif, 0, 0, null);


				for (int x = 0; x < rgb_image.getWidth(); x++) 
				{
					for (int y = 0; y < rgb_image.getHeight(); y++) 
					{
						int rgb = rgb_image.getRGB(x, y);
						int alpha = (rgb & 0xFF000000);
						int red = (rgb & 0xFF0000) >> 16;
					    int green = (rgb & 0x00FF00) >> 8;
			            int blue = rgb & 0x0000FF;

			            // apply brightness

			            red *= brightness;
			            blue *= brightness;
			            green *= brightness;
			            rgb = (red << 16) + (green << 8) + blue;



			            rgb_image.setRGB(x, y, alpha | rgb);
					}

				}

				m_frame[i].image = rgb_image;
			}
		}
	}

	public ImageBounds [] get_frames()
	{
		return m_frame;
	}
	
	public double get_rounded_angle(double angle)
	{
		return Math.round(angle / m_step) * m_step;
	}
	
	public ImageBounds get_frame(double angle) 
	{
		int i = (int) Math.round(angle / m_step);
		
		return m_frame[(i + m_frame.length) % m_frame.length];
	}
}

