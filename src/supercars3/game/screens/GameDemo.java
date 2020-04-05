package supercars3.game.screens;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

import supercars3.base.CircuitDirectory;
import supercars3.base.Levels;
import supercars3.game.GameOptions;
import supercars3.game.GameState;
import supercars3.game.MainGame;

public class GameDemo extends GameState
{
	OptionsScreenMain osm;
	MainGame maingame;	
	
	public GameDemo()
	{

	}
	
	@Override
	protected void p_init()
	{
		try
		{
			osm = new OptionsScreenMain();
			Levels levels = osm.get_levels();
			int nb_levels = levels.get_names().length;

			int level_index = (int)Math.floor(nb_levels * Math.random());
			
			CircuitDirectory current_level = levels.get_level(level_index);
			
			osm.set_demo_level(current_level);
			
			int circuit_index = (int)(Math.ceil(current_level.nb_circuits * Math.random()));
			
			osm.init(m_dimension,m_game);
			
			osm.no_sfx();
			
			osm.init_race(0,circuit_index,GameOptions.instance().read_only.total_nb_cars);
			
			maingame = new MainGame(osm,true);	
			
			maingame.init(m_dimension,m_game);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			fadeout();
		}
	}

	@Override
	protected void p_render(Graphics2D g)
	{
		// TODO Auto-generated method stub
		maingame.render(g);
	}

	@Override
	protected void p_update()
	{
		maingame.update(get_elapsed_time());
		
		if (maingame.is_fadeout_done() || (is_fadeout_done()))
		{
			set_next(new CreditsScreen());
		}
		
		if ((m_game.keyPressed(KeyEvent.VK_SPACE)) || (m_game.keyPressed(KeyEvent.VK_RIGHT)))
		{
			fadeout();
		}
	}

}
