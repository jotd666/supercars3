package supercars3.game.screens;

import java.awt.*;
import java.awt.image.*;
import java.io.*;

import javax.imageio.ImageIO;

import supercars3.base.DirectoryBase;


/**
 * <p>Titre : </p>
 * <p>Description : </p>
 * <p>Copyright : Copyright (c) 2005</p>
 * <p>Société : </p>
 * @author non attribuable
 * @version 1.0
 */

public class WoodBackground
{
  private BufferedImage m_wood;
  private BufferedImage m_background;
  
  public WoodBackground(int w, int h)
  {
	  m_background = new BufferedImage(w,h,BufferedImage.TYPE_INT_RGB);
	  try
	  {
		  File f = new File(DirectoryBase.get_images_path() + "wood.jpg");
		  m_wood = ImageIO.read(f);

		  int clip_height = (h / 3);
		  Graphics g = m_background.getGraphics();

		  for (int i = 0; i < 3; i++) {
			  int y = clip_height * i;
			  g.setClip(0, y, w, clip_height - 4);

			  int offset = 0;
			  while (offset < clip_height) {
				  int x = 0;
				  while (x < w) {
					  g.drawImage(m_wood, x, y + offset, null);
					  x += m_wood.getWidth();
				  }
				  offset += m_wood.getHeight();
			  }
		  }
	  }
	  catch (IOException e)
	  {
		  
	  }
    }
  
  protected void render(Graphics2D g)
  {
	  g.drawImage(m_background,0,0,null);   
  }

}
