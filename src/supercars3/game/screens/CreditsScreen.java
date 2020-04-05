package supercars3.game.screens;



import java.awt.*;
import java.awt.image.*;

import supercars3.game.GameState;
import supercars3.sys.Localizer;

import java.io.File;

/**
 * <p>Titre : </p>
 * <p>Description : </p>
 * <p>Copyright : Copyright (c) 2005</p>
 * <p>Société : </p>
 * @author non attribuable
 * @version 1.0
 */

public class CreditsScreen extends DemoGameState {
  private BufferedImage m_image = null;
  private BufferedImage m_sclogo = null;
  
  public CreditsScreen()
  {
    set_fadeinout_time(0,500);
    set_maximum_duration(8000);
   }
   
  protected GameState default_next_screen()
  {
	  return new CreditsScreen2();
  }
  protected void p_init()
{
	  m_image = bsLoader.getImage("images"+File.separator+"gradient.png");
	  bsLoader.setMaskColor(Color.BLACK);
	  m_sclogo = bsLoader.getImage("images"+File.separator+"supercars3.png",true);

}

  protected void p_render(Graphics2D g)
  {
 
  
    	g.drawImage(m_image,null,0,0);
    	int nd=120;
    	g.drawImage(m_sclogo,(getWidth()-nd)/2,5,nd,nd,null);
  
      draw_multiline_string(g, Localizer.value("supercars_series_by"),0.35,false);
      
      draw_2_strings(g,Localizer.value("Java version & add-ons"),Localizer.value("by JOTD")+" (email: jotd"+
    		  "@"+"orange.fr)",0.55,true);
      draw_2_strings(g,Localizer.value("Original music"),Localizer.value("by Barry Leitch"),0.70,true);
      draw_2_strings(g,Localizer.value("Remixed mp3 music"),Localizer.value("mp3_music_authors"),0.82,true);
          
      draw_string(g, "§ 2006-2015 JFF Software Ltd",0.94);
 
    
  }

}