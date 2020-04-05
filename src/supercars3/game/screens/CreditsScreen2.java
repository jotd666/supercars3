package supercars3.game.screens;



import java.awt.*;
import java.awt.image.*;

import java.io.File;

import supercars3.game.GameState;
import supercars3.sys.Localizer;

/**
 * <p>Titre : </p>
 * <p>Description : </p>
 * <p>Copyright : Copyright (c) 2005</p>
 * <p>Société : </p>
 * @author non attribuable
 * @version 1.0
 */

public class CreditsScreen2 extends DemoGameState {
  private BufferedImage m_image = null;
  
  CreditsScreen2()
  {
    set_fadeinout_time(0,500);
    set_maximum_duration(8000);
   }
  
  protected GameState default_next_screen()
  {
	  return new TitleScreen2();
  }
  protected void p_init()
{
	  m_image = bsLoader.getImage("images"+File.separator+"gradient.png");
	  bsLoader.setMaskColor(Color.BLACK);

}

  protected void p_render(Graphics2D g)
  {
 
   
    	g.drawImage(m_image,null,0,0);
   
      draw_string(g, Localizer.value("Thanks go to"),	  0.05);
      
      draw_2_strings(g,"Hall Of Light (http://hol.abime.net)",
    		  Localizer.value("for Supercars I & II maps"),0.12,true);
      draw_2_strings(g,"Christophe Giuge",Localizer.value("for pseudo-XML format"),0.26,true);
      draw_2_strings(g,"Martin Cameron",Localizer.value("for Micromod module player"),0.40,true);
      draw_2_strings(g,"EmuChicken",Localizer.value("for 3D original car model"),0.55,true);
      draw_2_strings(g,"Guillaume Hattab",Localizer.value("for playtesting"),0.70,true);
      draw_2_strings(g,"Seb",Localizer.value("for Pandora port & fixes"),0.83,true);
      draw_string(g, "§ 2006-2015 JFF Software Ltd",0.94);

    
  }

 
}