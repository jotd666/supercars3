package supercars3.game.screens;

import java.awt.Graphics2D;

import supercars3.game.GameOptions;
import supercars3.game.MainGame;


public class OptionsScreenCheat extends OptionsScreen
{

	private OptionsScreenMain m_options_screen_main = null;
	private String [] m_one_to_ten = new String[10];
	
	protected void p_init()
	{
		super.p_init();
	}
	
	
	protected void set_next_screen()
	{
		if (m_tab_pressed)
		{
			m_tab_pressed = false;

			m_options_screen_main.locale_changed();
			set_next(m_options_screen_main);		
		}
		else
		{
			m_options_screen_main.init_cars();
			set_next(new MainGame(m_options_screen_main,false));
		}
	}	
	

	

	
	public OptionsScreenCheat() throws Exception
	{						
		GameOptions go = GameOptions.instance();

		for (int i = 0; i < m_one_to_ten.length; i++)
		{
			m_one_to_ten[i] = ""+(i+1);
		}
		
		m_select.clear();
		
	
		m_select.add(new SelectableOption("CAR MOVES",GameOptions.CarMoves.values(),go.get_car_moves().ordinal(),0.15));
		m_select.add(new SelectableOption("START CIRCUIT",m_one_to_ten,go.get_start_circuit(),0.30));
		m_select.add(new BooleanOption("PLAYER BOOST",go.get_player_boost(),0.45));
		m_select.add(new BooleanOption("ALWAYS COMMUNICATION SCREENS",go.get_comm_screens_always(),0.60));
		m_select.add(new BooleanOption("COLORED ENNEMIES",go.colored_ennemies,0.75));
		    
	}
	
	public void update_options()
	{
		GameOptions go = GameOptions.instance();

		go.set_car_moves(get_selectable_option_index(0)); // car moves

		go.set_start_circuit(get_selectable_option_index(1)); // start circuit

		go.set_player_boost(((BooleanOption)(m_select.elementAt(2))).get_value()); // boost yes/no

		go.set_comm_screens_always(((BooleanOption)(m_select.elementAt(3))).get_value()); 
		
		go.colored_ennemies = ((BooleanOption)m_select.elementAt(4)).get_value(); // colored ennemies
	}
	protected void p_render(Graphics2D g)
	{
	    
	    super.p_render(g);

	    draw_localized_string(g,"CHEAT MENU",9*PIXEL_TO_RATIO);	    

	    
	  }
	void set_previous_screen(OptionsScreenMain osm)
	{		
		m_options_screen_main = osm;
		m_options_screen_main.m_resume_music = false;
	}
	String get_car()
	  {
	    return ((SelectableOption)(m_select.elementAt(GameOptions.CAR))).get_option();
	  }

}
