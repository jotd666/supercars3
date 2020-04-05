package supercars3.game.weapons;

import supercars3.game.ExplosionView;
import supercars3.game.GameOptions;
import supercars3.game.cars.Car;
import supercars3.sys.AngleUtils;
import supercars3.base.OpponentProperties;

//import supercars3.base.AngleUtils;

public class LinearMissile extends Weapon
{
	private final GameOptions.ReadOnly ro_settings = GameOptions.instance().read_only;
	private final double m_linear_speed = ro_settings.linear_speed;
	private double m_acceleration_power;
	private double m_max_speed;

	protected double get_linear_speed() 
	{
		  return m_linear_speed;
	  }
	  protected double get_acceleration(double speed) 
	  {
		  double rval = 0.0;
		  
		  if (speed < m_max_speed) 
		  {
			  rval = m_acceleration_power * 10 * m_elapsed_time;
		  } 
		  return rval;
	  }	
	public LinearMissile(Car launcher,WeaponView wv,ExplosionView ev,boolean back)
	{
		super(launcher,wv,ev);
		GenericParameters current_launcher = launcher.get_current();
		
		m_current.angle = m_current.discreet_angle();
		
		

		double engine_value = ro_settings.front_missile_engine_value;
		double initial_speed = 0;
		
		if (back)
		{
			m_acceleration_power = ro_settings.rear_missile_acceleration_power;
			m_current.angle = AngleUtils.oppose(m_current.angle);
			engine_value = ro_settings.rear_missile_engine_value;
			initial_speed = 0; //-current_launcher.get_speed(); // SEB/JFF: from Pandora port
		}
		else
		{
			m_acceleration_power = ro_settings.front_missile_acceleration_power;
			initial_speed = current_launcher.get_speed();
		}
		m_current.set_speed(initial_speed,m_current.angle);
	    m_max_speed = OpponentProperties.convert_speed(engine_value);

		m_predicted.copy_from(m_current);

	}
	
	public void predict()
	{
		  apply_speed();
		  m_predicted.angle = m_current.angle;
	}
	
	protected void p_update()
	{
		if (is_alive())
		{
			handle_acceleration();
		}
	}
}
