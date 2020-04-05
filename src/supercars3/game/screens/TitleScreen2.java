package supercars3.game.screens;


import java.awt.*;
import java.awt.image.*;

import supercars3.game.GameState;


/**
 * <p>Titre : </p>
 * <p>Description : </p>
 * <p>Copyright : Copyright (c) 2005</p>
 * <p>Société : </p>
 * @author non attribuable
 * @version 1.0
 */

public class TitleScreen2 extends DemoGameState
{
   private BufferedImage m_image;

  public TitleScreen2()
  {
    set_fadeinout_time(500,2000);
    set_maximum_duration(6000);
  }
  protected GameState default_next_screen()
  {
	  return new PeopleCarScreen();
  }

  protected void p_render(Graphics2D g)
  {
  
      g.drawImage(m_image,
                  (int)(m_dimension.getWidth()-m_image.getWidth())/2,
                  (int)(m_dimension.getHeight()-m_image.getHeight())/2,
                  null);

   
  }

  protected void p_init()
  {
	     bsLoader.setMaskColor(Color.BLACK);
	      m_image = bsLoader.getImage("images/supercars3.png",true);

	      if (!m_game.is_music_playing())
	      {
	        m_game.load_music("supercars21");
	      }

  }

 
}
