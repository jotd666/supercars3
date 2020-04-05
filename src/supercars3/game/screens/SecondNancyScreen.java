package supercars3.game.screens;

import supercars3.game.GameState;

public class SecondNancyScreen extends NewsScreen
{
	public SecondNancyScreen()
	{
		super("nancy","nancy_pretty",false);
	}
	
	  protected GameState default_next_screen()
	  {
		  return new ThirdHarrisonScreen();
	  }
}
