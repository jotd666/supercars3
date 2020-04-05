package supercars3.game;

import supercars3.base.*;
import supercars3.sys.AnimatedImage;

import java.util.*;
import java.awt.Graphics;
import java.awt.image.*;

public class LakeImageSet
{
	private Vector<AnimatedImage> m_lake = new Vector<AnimatedImage>();
	public void init(CircuitData data, BufferedImage circuit_image)
	{
		for (ControlPoint cp : data.get_lake_seed_points())
		{
			m_lake.add(data.generate_lake_animation(circuit_image,cp));
		}
		
	}
	
	public void update(long elapsed_time)
	{
		for (AnimatedImage a : m_lake)
		{
			a.update(elapsed_time);
		}	
	}
	public void render(Graphics g)
	{		
		for (AnimatedImage a : m_lake)
		{
			a.render(g);
		}
	}
	
}
