package supercars3.game.screens;

import java.awt.Graphics2D;

import supercars3.game.cars.Car;
import supercars3.game.players.*;
import supercars3.sys.Localizer;
import supercars3.game.GameState;
import supercars3.game.SCGame;
import supercars3.base.*;

public class EndSeasonScreen extends GameState
{
	private static final String [] onetwo = {"one","two","three","four"};
	private WoodBackground m_wood;
	private String m_title;
	private Driver m_best_player = null;
	private Car [] m_players;
	private int m_player_index = 0;
	private String m_completed_level = null;
	private int m_nb_circuits;
	
	public EndSeasonScreen(ScoreEntry best_score, Car [] players, CircuitDirectory level)
	{
		m_players = players;
				
		m_nb_circuits = level.nb_circuits;
		
		for (Car c : players)
		{
			if (c.get_driver().get_name().equals(best_score.driver))
			{
				m_best_player = c.get_driver();
				break;
			}
			
			m_player_index++;
				
		
		}
		m_title = Localizer.value(m_best_player != null ? "CONGRATULATIONS" : "HARD LUCK");
		if (m_best_player != null)
		{
			m_completed_level = Localizer.value("completed_yes").replaceAll("%level%", Localizer.value(level.name).toLowerCase());
		}
	}
	protected void p_init()
	{
		m_wood = new WoodBackground(getWidth(),getHeight());
	}

	@Override
	protected void p_update()
	{
		 if (m_game.fire_pressed())
		  {
			  fadeout();			  
		  }	   
		 if (is_fadeout_done())
	      {
			 set_next(new GameOverScreen(m_players,m_nb_circuits));
	      }

	}
	
	
	protected void p_render(Graphics2D g)
	{
	   m_wood.render(g);

		draw_string(g,m_title,PIXEL_TO_RATIO*41);
		
		if (m_best_player != null)
		{
			String s = Localizer.value("Player") + " " + Localizer.value(onetwo[m_player_index]) + " - "
			+ m_best_player.get_name();

			draw_string(g, s, PIXEL_TO_RATIO*92,true);
			draw_string(g, m_completed_level, PIXEL_TO_RATIO*128);
			draw_localized_string(g, "now_try_another", PIXEL_TO_RATIO*200);

		}
		else
		{
			SCGame.NORMAL_BITMAP_GRAY_FONT.write(g, Localizer.value("completed_almost"),(int)(getWidth() / 2.0),
				(int)(getHeight() * PIXEL_TO_RATIO*128), 0, true, false, 10);

			draw_localized_string(g, "dont_give_up", PIXEL_TO_RATIO*222);
			
		}
	
	}
}
