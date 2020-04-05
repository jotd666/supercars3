package supercars3.game.cars;

import java.awt.geom.Point2D;

import supercars3.game.MobileImageSet;
import supercars3.game.MultipleView;

public class CarView implements MultipleView
{
	protected static final String[] IMAGE_NAME_ARRAY = { "straight", "up", "down",
		"side_1", "side_2" };
	
	protected MobileImageSet[] m_image_set = new MobileImageSet[IMAGE_NAME_ARRAY.length];
	
	public static final int NB_TOTAL_FRAMES = 36;
	
	public static final int STRAIGHT = 0;
	public static final int UP = 1;
	public static final int DOWN = 2;
	public static final int SIDE_1 = 3;
	public static final int SIDE_2 = 4;

	protected Point2D m_centre_offset;
		
	public MobileImageSet get_image_set(int idx)
	{
		return m_image_set[idx];
	}
	
	public int get_nb_image_sets()
	{
		return m_image_set.length;
	}	
	public Point2D get_centre_offset() 
	{
		return m_centre_offset;
	}
	public int get_nb_frames()
	{
		return NB_TOTAL_FRAMES;
	}
	
	public CarView()
	{
	}

}
