package supercars3.game.screens;



import java.awt.*;
import java.awt.image.*;

import java.io.File;

import supercars3.game.SCGame;
import supercars3.sys.GameFont;
import supercars3.sys.Localizer;

/**
 * <p>Titre : </p>
 * <p>Description : </p>
 * <p>Copyright : Copyright (c) 2005</p>
 * <p>Société : </p>
 * @author non attribuable
 * @version 1.0
 */

public abstract class NewsScreen extends DemoGameState {
  private BufferedImage m_image = null, m_sclogo = null;
  private String m_text, m_pic;
  	private boolean m_show_sc3_logo;
  	private static final Color GRAY59 = new Color(0x3A3A3A);
  	
  NewsScreen(String pic,String text,boolean show_sc3_logo)
  {
	  m_pic = pic;
	  m_text = Localizer.value(text);
	  m_show_sc3_logo = show_sc3_logo;
    set_fadeinout_time(0,500);
    set_maximum_duration(8000);
   }
  

  
  protected void p_init()
{
	  m_image = bsLoader.getImage("images"+File.separator+m_pic+".png");
	  bsLoader.setMaskColor(Color.BLACK);
	  if (m_show_sc3_logo)
	  {
		  m_sclogo = bsLoader.getImage("images"+File.separator+"supercars3.png",true);
	  }

}

  protected void p_render(Graphics2D g)
  {
 
    	g.drawImage(m_image,null,0,0);
      	if (m_sclogo != null) g.drawImage(m_sclogo,415,40,125,125,null);
      	g.setColor(GRAY59);
      	g.fillRect(0, 356, 640, 400);
       	g.setColor(Color.WHITE);
      	g.drawRect(0, 356, 640, 400);
      	
      	GameFont gf = SCGame.NORMAL_BITMAP_GRAY_FONT;
     	  
      	gf.write(g, m_text, getWidth()/2, 360,-2,true,false,-4);
      
  }

}