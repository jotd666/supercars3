package supercars3.game.screens;



import java.awt.*;
import java.awt.image.*;

import java.io.File;
import supercars3.game.GameState;

/**
 * <p>Titre : </p>
 * <p>Description : </p>
 * <p>Copyright : Copyright (c) 2005</p>
 * <p>Société : </p>
 * @author non attribuable
 * @version 1.0
 */

public class PeopleCarScreen extends DemoGameState {
	  private BufferedImage m_image = null;
	  private BufferedImage m_3 = null;
 
  PeopleCarScreen()
  {
    set_fadeinout_time(0,500);
    set_maximum_duration(12000);
   }
  
  protected GameState default_next_screen()
  {
	  return new FirstHarrisonScreen();
  }
  
  protected void p_init()
{
	  m_image = bsLoader.getImage("images"+File.separator+"people_car.png");
	  bsLoader.setMaskColor(Color.BLACK);
	  m_3 = bsLoader.getImage("images"+File.separator+"III.png");
	  
	  int w = m_3.getWidth() / 2;
	  int h = m_3.getHeight() / 2;
	  
	  int x = (getWidth()-w)/2;
	  int y = (getHeight() * 110)/400;
	  
	  m_image.getGraphics().drawImage(m_3, x, y, w, h, null);
	  
	  m_3 = null;

}

  protected void p_render(Graphics2D g)
  {
 
   
    	g.drawImage(m_image,null,0,0);
     
  }

}