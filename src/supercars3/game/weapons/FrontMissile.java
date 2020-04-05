package supercars3.game.weapons;

import supercars3.game.*;
import supercars3.game.cars.Car;
	
public class FrontMissile extends LinearMissile
{
	public FrontMissile(Car launcher, WeaponView view, ExplosionView ev)
	{
		super(launcher,view,ev,false);
	}
}
