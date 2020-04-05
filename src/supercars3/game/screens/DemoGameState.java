package supercars3.game.screens;



import java.awt.event.*;

import supercars3.game.GameState;


/**
 * <p>Titre : </p>
 * <p>Description : </p>
 * <p>Copyright : Copyright (c) 2005</p>
 * <p>Société : </p>
 * @author non attribuable
 * @version 1.0
 */

public abstract class DemoGameState extends GameState {
  private boolean m_space_pressed = false;
  
  
  protected abstract GameState default_next_screen();
  
  protected void p_update()
  {
	  
    if (m_game.fire_pressed())
    {
      m_space_pressed = true;
      fadeout();
    }
    if (m_game.keyPressed(KeyEvent.VK_RIGHT))
    {
      fadeout();
    }

    if (is_fadeout_done())
    {
      GameState gs = null;
      if (m_space_pressed)
        {
          gs = m_game.get_options_screen();
        }
        else
        {
          gs = default_next_screen();
        }

        set_next(gs);
    }
  }
}