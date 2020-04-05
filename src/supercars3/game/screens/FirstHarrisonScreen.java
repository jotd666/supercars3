package supercars3.game.screens;

import supercars3.game.GameState;

public class FirstHarrisonScreen extends NewsScreen
{
	public FirstHarrisonScreen()
	{
		super("harrison","harrison_welcome",true);
	}
	
	  protected GameState default_next_screen()
	  {
		  return new SpecsScreen();
	  }
}
