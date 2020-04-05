package supercars3.game.screens;

import java.awt.Graphics2D;

import supercars3.game.cars.Car;
import supercars3.sys.Localizer;
import supercars3.game.GameState;

public class GameOverScreen extends GameState
{
	private Car [] m_players;
	private static final String [] onetwo = {"one","two"};
	private WoodBackground m_wood;
	private int m_total_nb_circuits;
	
	public GameOverScreen(Car [] players, int total_nb_circuits)
	{
		m_players = players;
		m_total_nb_circuits = total_nb_circuits;
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
			 m_game.stop_music();
			 set_next(new TitleScreen2());
	      }

	}
	
	private void manage_player(Graphics2D g,Car player,int player_index,double hr1, double hr2, double hr3)
	{
		String s = Localizer.value("Player") + " " + Localizer.value(onetwo[player_index]) + " - "
		+ player.get_driver().get_name();

		draw_string(g, s, hr1,true);

		s = Localizer.value("Score") + " - " + player.get_points(); 
		draw_string(g, s, hr2);

		// ensure that number of points won't be superior to 40
		
		int reframed_points = (player.get_points() * 7) / m_total_nb_circuits;
		
		int rating_index = ((reframed_points/2)*2);
				
		s = Localizer.value("Rating") + " - " + Localizer.value("rating_"+rating_index);		
		draw_string(g, s, hr3,true);
		
	}
	
	protected void p_render(Graphics2D g)
	{
	   m_wood.render(g);

		draw_localized_string(g,"GAME OVER",PIXEL_TO_RATIO*41);
		
		
		manage_player(g,m_players[0],0,128*PIXEL_TO_RATIO,162*PIXEL_TO_RATIO,190*PIXEL_TO_RATIO);
		
		if (m_players.length > 1)
		{
			manage_player(g,m_players[1],1,268*PIXEL_TO_RATIO,302*PIXEL_TO_RATIO,334*PIXEL_TO_RATIO);
		}
	}
}
