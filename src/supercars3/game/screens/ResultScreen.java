package supercars3.game.screens;

import java.awt.Graphics2D;

import supercars3.base.*;
import supercars3.game.*;
import supercars3.game.cars.*;
import supercars3.game.players.*;
import supercars3.sys.Localizer;

public class ResultScreen extends GameState
{
	private class CarResult
	{
		String position;
		String name;
		String prize;
		String points;
	}
	
	private OptionsScreenMain m_options;
	private String m_best_time;
	private int [] m_player_index = new int[2];
	private CarResult [] m_line;
	private boolean m_flash = false;
	private boolean m_game_over;
	private WoodBackground m_wood;
	
	protected void p_init()
	{
		m_wood = new WoodBackground(getWidth(),getHeight());
		
		m_game.load_music("supercars22");
	}

	
	public ResultScreen(OptionsScreenMain options, 
			Driver winner,
			long race_time,
			boolean game_over)
	{
		m_options = options;
		m_game_over = game_over;
		
		boolean record_broken = m_options.get_record_time().set(winner,race_time);
		RaceTime rt = new RaceTime();
		rt.set_milliseconds(race_time);
		
		m_best_time = Localizer.value("Winning time")+" - "+rt.time();
		if (record_broken)
		{
			m_best_time += " R";
		}
		
		CarSet cs = m_options.get_car_set();
		
		m_line = new CarResult[cs.size()];
		
		for (int i = 0; i < m_player_index.length; i++)
		{
			m_player_index[i] = -1; // invalidate
		}
		
		int idx = 0;
		
		boolean human_player_in_best_half = false;
		
		for (int i = 0; i < cs.size(); i++)
		{
			Car c = cs.get_item(i);
			if (c.get_driver().is_human())
			{
				m_player_index[idx++] = i;
				// next race is OK if at least one player is ranked well
				if (c.get_position() <= (cs.size()/2))
				{
					human_player_in_best_half = true;
				}
			}
			m_line[i] = new CarResult();
			
			
			CarResult l = m_line[i];
			
			int position = c.get_position();
			
			l.position = ""+position+CourseRecords.get_suffix(position,true);
			
			l.name = c.get_driver().get_name();
			
			l.prize = "" + CourseRecords.PRIZES[i];
			
			l.points = "" + CourseRecords.POINTS[i];
			
			c.get_equipment().add_money(CourseRecords.PRIZES[i]);
			c.add_points(CourseRecords.POINTS[i]);
						
		}
		
		if ((!human_player_in_best_half) && (!GameOptions.instance().get_cheat_mode()))
		{
			m_game_over = true;
		}
		
	}


	protected void p_render(Graphics2D g)
	{
		m_wood.render(g);

		
		draw_localized_string(g,"RACE RESULT",PIXEL_TO_RATIO*16);
		
		draw_string(g,m_best_time,PIXEL_TO_RATIO*60);
		
		draw_localized_string(g,"Pos",66-4,100,false);
		draw_localized_string(g,"Name",154-4,100,false);
		draw_localized_string(g,"Prize",472-4,100,false);
		draw_localized_string(g,"Pts",560-4,100,false);

		for (int i = 0; i < m_line.length; i++)
		{			
			boolean gray_2 = true;
			
			if (m_flash)
			{
				for (int j : m_player_index)
				{
					if (i == j)
					{
						gray_2 = false;
						break;
					}
				}
			}
			
			
			int y = 142 + i * 24;
			
			CarResult l = m_line[i];
			
			draw_string(g,l.position,66,y,gray_2);
			draw_string(g,l.name,154,y,gray_2);
			draw_string(g,l.prize,472,y,gray_2);
			draw_string(g,l.points,560,y,gray_2);
		}
		m_flash = !m_flash;
		
	}
	
	 protected void p_update()
	  {
		 if (m_game.fire_pressed())
		  {
			  fadeout();
		  }	   

		 if (is_fadeout_done())
		 {
			 set_next(new CurrentRankingScreen(m_options,m_game_over));
		 }

	  }
}
