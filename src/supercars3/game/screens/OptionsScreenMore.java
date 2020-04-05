package supercars3.game.screens;

import java.awt.Graphics2D;
import java.util.Vector;

import supercars3.base.DirectoryBase;
import supercars3.game.GameOptions;
import supercars3.game.MainGame;
import supercars3.game.cars.*;
import supercars3.sys.Localizer;

public class OptionsScreenMore extends OptionsScreen
{

	private OptionsScreenMain m_options_screen_main = null;
	private OptionsScreenCheat m_options_screen_cheat;

	protected void p_init()
	{
		super.p_init();
	}

	protected void set_next_screen()
	{
		if (m_tab_pressed)
		{
			m_tab_pressed = false;
		    if (GameOptions.instance().get_cheat_mode())
		    {
				m_options_screen_cheat.set_previous_screen(m_options_screen_main);
				
				m_options_screen_cheat.locale_changed();

		    	set_next(m_options_screen_cheat);
		    }
		    else
		    {
		    	set_next(m_options_screen_main);
		    }
		}
		else
		{
			m_options_screen_main.init_cars();
			set_next(new MainGame(m_options_screen_main,false));
		}
	}	
	


	class LanguageOption extends SelectableOption
	{
		public LanguageOption(String title,String[] options,  int idx, double y_percent) {
			super(title,options,idx,y_percent);
		}
		protected void index_changed()
		{
			GameOptions.instance().set_language(get_index());
			locale_changed();
		}
	}
	
	
	public OptionsScreenMore() throws Exception
	{				
		
		m_options_screen_cheat = new OptionsScreenCheat();

		String [] languages = GameOptions.instance().LANGUAGES;
		
		String [] languages_displayed = new String[languages.length];
		
		GameOptions options = GameOptions.instance();

		for (int i = 0; i < languages.length; i++)
		{
			languages_displayed[i] = Localizer.value(languages[i]);
		}
		
		Vector<String> car_dir = DirectoryBase.get_cars();
		
		int nb_available_cars = car_dir.size();
		String [] car_names = new String[nb_available_cars];
		for (int i = 0; i < nb_available_cars; i++)
		{
			CarBodyView cbv = new CarBodyView(car_dir.elementAt(i));
			
			car_names[i] = cbv.get_name();
		}
		
		m_select.clear();
		
	    m_select.add(new SelectableOption("CAR TYPE",car_names,options.get_car_type(),0.07));
	    	    
	    m_select.add(new SelectableOption("POWER SLIDE",
	    		GameOptions.PowerSlide.values(),
	    		options.get_power_slide().ordinal(),0.19));

	    m_select.add(new SelectableOption("CONTROL",GameOptions.CONTROL_METHOD,options.get_control_method(),0.30));
	    
	    m_select.add(new LanguageOption("LANGUAGE",languages_displayed,options.get_language(),0.42));
	    
	    m_select.add(new SelectableOption("MUSIC",GameOptions.MusicSelection.values(),
	    		options.get_music_mode().ordinal(),0.54));

	    m_select.add(new SelectableOption("SOUND",
	    		GameOptions.SoundSelection.values(),
	    				options.get_sfx_mode().ordinal(),0.65));
	    
	    m_select.add(new SelectableOption("MAX FRAME RATE",GameOptions.FRAME_RATE_SELECTION,options.get_max_fps().ordinal(),0.78));
	}
	
	public void update_options()
	{
		update_options(false);
	}
	
	public void update_options(boolean affect_screen)
	{
		GameOptions go = GameOptions.instance();
		
		int cnt = 0;
		go.set_car_type(get_selectable_option_index(cnt++)); // car type
		go.set_power_slide(get_selectable_option_index(cnt++));
		go.set_control_method(get_selectable_option_index(cnt++));
		go.set_language(get_selectable_option_index(cnt++)); // language
		boolean audio_change = go.set_music_mode(get_selectable_option_index(cnt++)); // music
		go.set_sfx_mode(get_selectable_option_index(cnt++)); // sound effects
		boolean fps_change = go.set_max_fps(get_selectable_option_index(cnt++)); // max fps
		
		m_options_screen_cheat.update_options();
		
		if (affect_screen)
		{
			if (audio_change)
			{
				m_options_screen_main.update_music();
			}
			if (fps_change)
			{
				m_game.setFPS(go.get_max_fps_value());
			}
		}
		
	}
	protected void p_render(Graphics2D g)
	{
	    
	    super.p_render(g);

	    draw_localized_string(g,"MORE OPTIONS",9*PIXEL_TO_RATIO);
	   
	    update_options(true);
	    
	  }
	void set_previous_screen(OptionsScreenMain osm)
	{		
		m_options_screen_main = osm;
		m_options_screen_main.m_resume_music = false;
	}
	int get_car()
	  {
	    return ((SelectableOption)(m_select.elementAt(GameOptions.CAR))).get_index();
	  }

}
