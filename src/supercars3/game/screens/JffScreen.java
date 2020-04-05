package supercars3.game.screens;

import java.awt.*;
import java.awt.image.BufferedImage;

import supercars3.game.GameState;
import supercars3.game.GfxUtils;
import supercars3.sys.Localizer;

/**
 * <p>Titre : </p>
 * <p>Description : </p>
 * <p>Copyright : Copyright (c) 2005</p>
 * <p>Société : </p>
 * @author non attribuable
 * @version 1.0
 */

public class JffScreen extends GameState
{
  private final Font m_intro_font = Font.decode("Arial-BOLDITALIC-30");
  private BufferedImage m_image;
  
  public JffScreen()
  {
   set_fadeinout_time(2000,2000);
   set_maximum_duration(5000);

  }

  protected void p_render(Graphics2D g)
  {

	  g.setFont(m_intro_font);

	  g.setColor(Color.WHITE);

	  GfxUtils.centered_draw_multiline_string(g, Localizer.value("JFF Software And"),
			  (int)(m_dimension.getWidth() / 2),
			  (int)(m_dimension.getHeight() * 0.33),10);

	  double resize = 0.5;
	  int w = (int)(m_image.getWidth() * resize);
	  int h = (int)(m_image.getHeight() * resize);
	  g.drawImage(m_image, (int)(m_dimension.getWidth() - w) / 2,
			  (int)((m_dimension.getHeight() - h)* 0.60),w,h,null);

	  GfxUtils.centered_draw_string(g, Localizer.value("Present"),
			  (int)(m_dimension.getWidth() / 2),
				  (int)(m_dimension.getHeight() * 0.85));

    
  }

  protected void p_init()
  {
	  bsLoader.setMaskColor(Color.BLACK);
	  m_image = bsLoader.getImage("images/gremlin.png",true);
  }

  protected void p_update()
  {

    if (is_fadeout_done())
      {
        set_next(new MagneticFieldsScreen(m_dimension,m_game));
      }

  }
}
