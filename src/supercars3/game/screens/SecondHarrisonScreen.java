package supercars3.game.screens;

import supercars3.game.GameState;

public class SecondHarrisonScreen extends NewsScreen
{
	public SecondHarrisonScreen()
	{
		super("harrison","harrison_mean",true);
	}
	
	  protected GameState default_next_screen()
	  {
		  return new FirstNancyScreen();
	  }
}
