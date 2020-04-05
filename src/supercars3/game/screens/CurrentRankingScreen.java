package supercars3.game.screens;

import java.awt.Graphics2D;

import supercars3.base.*;
import supercars3.game.GameState;
import supercars3.game.MainGame;
import supercars3.game.SCGame;
import supercars3.game.cars.Car;
import supercars3.game.cars.CarSet;
import supercars3.game.shop.RepairScreen;
import supercars3.sys.GameFont;
import supercars3.sys.Localizer;

public class CurrentRankingScreen extends GameState
{
	private String m_title;
	private String [] m_positions;
	private ScoreEntry [] m_score_entries;
	private WoodBackground m_wood;
	private boolean m_game_over;
	private OptionsScreenMain m_options;
	private boolean m_flash = false;
	
	public CurrentRankingScreen(OptionsScreenMain options,boolean game_over)
	{
		set_maximum_duration(7000);
		set_fadeinout_time(0, 500);
		m_options = options;
		m_game_over = game_over;
	}
	

	protected void p_update()
	{
		if (m_game.fire_pressed())
		{
			fadeout();			
		}
		 if (is_fadeout_done())
	      {
			 boolean season_complete = m_options.next_circuit();
			 boolean game_over = m_game_over || season_complete;
			 
			 if (!game_over)
			 {
				 Car [] cars = m_options.get_player_cars();

				 GameState next_circuit = new MainGame(m_options,false);
				 GameState next_screen = next_circuit;
				 
				 if (cars != null)
				 {
					 Equipment player_1_equipment = cars[0].get_equipment();

					 player_1_equipment.cast_prices();

					 if (cars.length > 1)
					 {
						 // 2 players

						 Equipment player_2_equipment = cars[1].get_equipment();
						 player_2_equipment.copy_prices(player_1_equipment);

						 next_screen = new RepairScreen(next_circuit,player_2_equipment,
								 cars[1].get_driver().get_name());

						 String cs = m_options.get_comm_screen(2);
						 if (cs != null)
						 {
							 next_screen = new CommunicationScreen(2,cars[1],cs,next_screen);
						 }
					 }

					 {
						 // 1-2 players
						 next_screen = new RepairScreen(next_screen,player_1_equipment,
								 cars[0].get_driver().get_name());

						 String cs = m_options.get_comm_screen(1);
						 if (cs != null)
						 {
							 next_screen = new CommunicationScreen(1,cars[0],cs,next_screen);
						 }
					 }
				 }
				 set_next(next_screen);
			 }
			 else
			 {
				 // log best scores if any
				 
				 Car [] player_cars = m_options.get_player_cars();
				 for (Car c : player_cars)
				 {
					 m_options.get_record_score().set(c.get_driver().get_name(), c.get_points());
				 }
				 if (season_complete)
				 {
					 set_next(new EndSeasonScreen(m_options.get_car_set().get_score_entries()[0],player_cars,m_options.get_circuit_set()));
				 }
				 else
				 {
					 set_next(new GameOverScreen(player_cars,m_options.get_circuit_set().nb_circuits));
				 }
			 }
	      }

	}
	
	protected void p_init()
	{
		m_wood = new WoodBackground(getWidth(),getHeight());
		
		m_title = Localizer.value("CURRENT TABLE");
		CarSet cs = m_options.get_car_set();
		m_score_entries = cs.get_score_entries();
		m_positions = new String[m_score_entries.length];
		
		for (int i = 0; i < m_positions.length; i++)
		{
			m_positions[i] = new String(""+(i+1)+CourseRecords.get_suffix(i+1, true));			
		}
	}
	
	protected void p_render(Graphics2D g)
	{
		m_wood.render(g);

		m_flash = !m_flash;
		
		GameFont gf = SCGame.NORMAL_BITMAP_GRAY_FONT;
		GameFont wf = SCGame.NORMAL_BITMAP_FONT;

		draw_string(g,m_title,PIXEL_TO_RATIO*22);

		int i = 98;
		int idx = 0;
		
		wf.write_line(g,Localizer.value("Pos"),62,i,0,false,false);
		wf.write_line(g,Localizer.value("Name"),148,i,0,false,false);
		wf.write_line(g,Localizer.value("Pts"),580,i,0,false,false);
		
		i = 140;
		
		for (ScoreEntry se : m_score_entries)
		{
			GameFont f = gf;
			
			if (m_flash)
			{
				if (se.human)
				{
					f = wf;					
				}
			}
			
			f.write_line(g,m_positions[idx++],62,i,0,false,false);
			f.write_line(g,se.driver,148,i,0,false,false);
			f.write_line(g,se.score+"",580,i,0,false,false);

			i += 30;
		}


	}
}
