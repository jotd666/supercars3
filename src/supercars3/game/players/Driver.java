package supercars3.game.players;

import supercars3.game.cars.Car;

/**
 * <p>Titre : </p>
 * <p>Description : </p>
 * <p>Copyright : Copyright (c) 2005</p>
 * <p>Société : </p>
 * @author non attribuable
 * @version 1.0
 */

public abstract class Driver {
  private String m_name;
  private boolean m_is_human;
  
  public Driver(String name, boolean is_human)
  {
    m_name = name;
    m_is_human = is_human;
  }

  public String get_name()
  {
    return m_name;
  }

  public boolean is_human()
  {
	  return m_is_human;
  }

  public abstract void move(Car c, long elapsed_time);

}