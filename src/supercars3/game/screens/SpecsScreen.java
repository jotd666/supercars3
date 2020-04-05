package supercars3.game.screens;



import java.awt.*;
import java.awt.image.*;

import java.io.File;
import java.util.StringTokenizer;

import supercars3.game.GameState;
import supercars3.game.SCGame;
import supercars3.sys.*;

/**
 * <p>Titre : </p>
 * <p>Description : </p>
 * <p>Copyright : Copyright (c) 2005</p>
 * <p>Société : </p>
 * @author non attribuable
 * @version 1.0
 */

public class SpecsScreen extends DemoGameState {
	  private BufferedImage m_image = null;
	  private BufferedImage m_side_car = null;
  private BufferedImage [] m_rotating_car = null;
  private static final int FRAME_SPEED = 150;
  private int m_frame_counter = 0;
  private int m_current_frame = 0;


  public SpecsScreen()
  {
    set_fadeinout_time(0,500);
    set_maximum_duration(20000);
   }
  
  protected GameState default_next_screen()
  {
	  return new SecondHarrisonScreen();
  }
  protected void p_init()
{
	  m_image = bsLoader.getImage("images"+File.separator+"specification.png");
	  m_side_car = bsLoader.getImage("images"+File.separator+"side_car.png");
	  bsLoader.setMaskColor(Color.BLUE);
	  m_rotating_car = bsLoader.getImages("images"+File.separator+"rotating_car.png", 1, 16);

	  m_image.getGraphics().drawImage(m_side_car,4,28,null);
	  

}
  private void draw_item(Graphics2D g,String title, String desc,int p_label_y)
  {
	  int label_x = 4;
	  int label_y = p_label_y;
	  int y_offset = -6;
	  GameFont wf = SCGame.NORMAL_BITMAP_FONT;
	  GameFont gf = SCGame.NORMAL_BITMAP_GRAY_FONT;

	  /* first, draw the title */

	  Rectangle rt = wf.write_line(g, Localizer.value(title)+" ", label_x,
			  label_y, -2,false,false);
	  
	  if (desc.length() > 0)
	  {
		  boolean first_line = true;

		  /* then, draw the description */

		  StringTokenizer tok = new StringTokenizer(Localizer.value(desc),"\n");
		  while (tok.hasMoreTokens())
		  {
			  Rectangle r = gf.write_line(g,tok.nextToken(),
					  (int)(label_x + (first_line ? rt.getWidth() : 0)),
					  label_y,-2,false,false);
			  first_line = false;
			  label_y += r.height + y_offset;
		  }
	  }
  }
  

  protected void p_render(Graphics2D g)
  {
 
   	g.drawImage(m_image,null,0,0);

   	g.drawImage(m_rotating_car[m_current_frame],null,412,160);
   	
    draw_item(g, "Specification","", 2);
  	draw_item(g,"Engine:","engine_desc",160);
  	
  
  	draw_item(g,"Max. speed:","max_speed_desc",208);
  	draw_item(g,"Max. power:","max_power_desc",232);
  	draw_item(g,"Acceleration:","acceleration_desc",256);	
  	draw_item(g,"Special features:","special_features_desc",280); 

  }

  protected void p_update()
  {
	  super.p_update();
	  
 

    m_frame_counter += get_elapsed_time();
    
    if (m_frame_counter > FRAME_SPEED)
    {
    	m_frame_counter = 0;
    	m_current_frame++;
    	if (m_current_frame == m_rotating_car.length)
    	{	    		
    		m_current_frame = 0;
    	}
    }
  }
}