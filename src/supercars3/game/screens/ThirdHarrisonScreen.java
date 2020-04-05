package supercars3.game.screens;

import supercars3.game.GameState;

public class ThirdHarrisonScreen extends NewsScreen
{
	public ThirdHarrisonScreen()
	{
		super("harrison","harrison_goodbye",true);
	}
	
	  protected GameState default_next_screen()
	  {
		  return new GameDemo();
	  }
}
