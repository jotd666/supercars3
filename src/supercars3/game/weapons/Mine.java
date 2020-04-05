package supercars3.game.weapons;

import supercars3.game.*;
import supercars3.game.cars.Car;


public class Mine extends Weapon
{
	private int m_counter = 5000; // 5 secs
	public void predict()
	{
		m_predicted.copy_from(m_current);
	}
	
	protected void p_update()
	{
		
		if (is_alive())
		{
			m_counter -= m_elapsed_time;
			if (m_counter < 0)
			{
				// explodes by itself without harming anyone
				detonate();
			}
		}
	}

	public Mine(Car launcher,WeaponView wv,ExplosionView ev)
	{
		super(launcher,wv,ev);
		
	}
	
}
