package supercars3.game.screens;


import java.awt.*;
import java.awt.image.*;

import supercars3.game.*;
import supercars3.base.DirectoryBase;
import supercars3.sys.*;

/**
 * <p>Titre : </p>
 * <p>Description : </p>
 * <p>Copyright : Copyright (c) 2005</p>
 * <p>Société : </p>
 * @author non attribuable
 * @version 1.0
 */

class MagneticFieldsScreen extends DemoGameState
{
  private final Font m_intro_font_2 = Font.decode("Courier-BOLD-8");
  private BufferedImage m_image;
  private Mp3Play m_sample;
  private boolean m_sample_played = false;
  private boolean m_picture_displayed = false;
  private static final int MAXIMUM_DURATION = 17000;
  private static final int LOGO_ZOOM_START = 2000;
  private static final int LOGO_ZOOM_END = 3500;
  private static final int LOGO_ZOOM_MAX = 5;
  
	private int image_zoomed_width = 0;
	private	int image_zoomed_height = 0;

	private	int image_x = 0;
	private	int image_y = 0;
	
  private double m_a, m_b;
  
  
  MagneticFieldsScreen(Dimension d,SCGame g)
  {
	  set_fadeinout_time(2000,2000);
	  set_maximum_duration(MAXIMUM_DURATION);
  }

  protected GameState default_next_screen()
  {
	  return new TitleScreen2();
  }
  

  protected void p_update()
  {
	  super.p_update();
		long state_elapsed_time = get_state_elapsed_time();
   		
  		if (m_sample != null)
  		{
  			if ((!m_sample_played)&&(state_elapsed_time > (LOGO_ZOOM_END+LOGO_ZOOM_START)/2))
  			{
  				m_sample_played = true;
  				m_sample.play();
  			}
    	}	
  		
  		if (is_fadeout_done())
  		{
  			if (m_sample != null)
  			{
  				m_sample.stop();
  			}
  		}
	  
  }
  protected void p_render(Graphics2D g)
  {
	 
  	  int image_height = m_image.getHeight();
	  int image_width = m_image.getWidth();  
  
		long state_elapsed_time = get_state_elapsed_time();
		  

   		if ((!m_picture_displayed)&&
   				(state_elapsed_time > LOGO_ZOOM_START)&&
   				(state_elapsed_time < LOGO_ZOOM_END))
   		{
   			m_picture_displayed = false;
   			g.setColor(Color.BLACK);
  	  		g.fillRect(image_x,image_y,image_zoomed_width,image_zoomed_height);
 
   			double zoom_factor = state_elapsed_time * m_a + m_b;

   			image_zoomed_width = (int)(image_width * zoom_factor);
   			image_zoomed_height = (int)(image_height * zoom_factor);

   			image_x = (int) (m_dimension.getWidth() - image_zoomed_width) / 2;
   			image_y = (int) ( (m_dimension.getHeight() - image_zoomed_height) * .4);


    		}
    		g.drawImage(m_image,image_x,image_y,image_zoomed_width,image_zoomed_height,null);
			 
   		
   		g.setColor(Color.BLUE);
   		GfxUtils.centered_draw_string(g, Localizer.value("RIPOFF_1"), m_dimension.getWidth() / 2,
   				m_dimension.getHeight() * 0.20);

   		GfxUtils.centered_draw_string(g, Localizer.value("RIPOFF_2"),
   				m_dimension.getWidth() / 2,
   				m_dimension.getHeight() * 0.65);

   		g.setFont(m_intro_font_2);
   		GfxUtils.centered_draw_string(g, "COPYRIGHT 2007-2015 JFF SOFTWARE DESIGN LTD",
   				m_dimension.getWidth() / 2,
   				m_dimension.getHeight() * 0.90);


   
  }

  protected void p_init()
  {
	  bsLoader.setMaskColor(Color.BLACK);
	  m_image = bsLoader.getImage("images/magnetic_fields.png",true);
	  
	  m_sample = null;
	  
	  if (GameOptions.instance().get_sfx_mode() != GameOptions.SoundSelection.no_sound)
	  {
		  m_sample = new Mp3Play(DirectoryBase.get_sound_path()+"siegfrieds");
		  //m_sample_duration = (int)(m_sample.duration() * 1000);
		  //m_sample_duration = 10000;
	  }
	  
	  m_a = (double)(1 - LOGO_ZOOM_MAX) / (LOGO_ZOOM_END - LOGO_ZOOM_START);
	  m_b = 1 - (m_a * LOGO_ZOOM_END);
  }

}
