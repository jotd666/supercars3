package supercars3.game.players;


import supercars3.game.cars.Car;
import supercars3.game.GameOptions;

/**
 * <p>Titre : </p>
 * <p>Description : </p>
 * <p>Copyright : Copyright (c) 2005</p>
 * <p>Société : </p>
 * @author non attribuable
 * @version 1.0
 */

public class Computer extends Driver
{	
	private int m_aggressivity;
	private GameOptions game_options = GameOptions.instance();
	
  public Computer(String name, int aggressivity)
  {
    super(name,false);
    m_aggressivity = aggressivity;
  }
  
  public void move(Car c, long elapsed_time) 
  {	  
   if ((!game_options.freeze_cpu_cars) && elapsed_time > 0)
	  {

		  int rand_value = (int)(Math.random() * ((0x10000*1000)/elapsed_time));

		  if (rand_value < m_aggressivity)
		  {	   
			  c.fire(c.get_equipment().mounted_front);
		  }
		  else if (rand_value < m_aggressivity*2)
		  {
			  c.fire(c.get_equipment().mounted_rear);    	
		  }
	  }
  }
	
	
}