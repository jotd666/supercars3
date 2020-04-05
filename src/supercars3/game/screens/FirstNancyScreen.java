package supercars3.game.screens;

import supercars3.game.GameState;

public class FirstNancyScreen extends NewsScreen
{
	public FirstNancyScreen()
	{
		super("nancy","nancy_scores",false);
	}
	
	  protected GameState default_next_screen()
	  {
		  OptionsScreenMain opt = m_game.get_options_screen();
		 		  
		  return new HiScoreScreen(opt.get_course_records(),opt.get_levels(),0);
	  }
}
