package supercars3.game.cars;

import supercars3.game.*;
import supercars3.game.players.Driver;
import supercars3.game.weapons.WeaponFactory;
import supercars3.sys.AngleUtils;

public class HumanCar extends Car
{
	private static final int LEFT = 1;
	private static final int RIGHT = 2;
	private static final int ACCELERATE = 4;
	private static final int BRAKE = 8;
	private int m_control_mask;

	private double m_angular_speed;

	public HumanCar(CarBodyView body_view, CarShadowView shadow_view,
			ExplosionView explosion, WeaponFactory weapons, Driver driver,
			int position)
	{
		super(body_view, shadow_view, explosion, weapons, driver,position);
	
		m_angular_speed = GameOptions.instance().get_power_slide().ordinal() * 
		  (ro_settings.human_angular_speed_max-ro_settings.human_angular_speed_min) / (GameOptions.PowerSlide.values().length - 1) 
		  + ro_settings.human_angular_speed_min;

	}
		
	public void reset_command()
	{
		m_control_mask = 0;
	}

	public void left() 
	{
		if (!is_jumping())  // SEB: fixed turn while in mid-air
		{
			m_control_mask |= LEFT;
		}
	}
	public void right() 
	{
		if (!is_jumping())  // SEB: fixed turn while in mid-air
		{
			m_control_mask |= RIGHT;
		}
	  }
	public void brake()
	{
		m_control_mask |= BRAKE;
	}

	public void accelerate()
	{
		m_control_mask |= ACCELERATE;
	}
	private double angle_variation() 
	{
	    return m_angular_speed * m_elapsed_time * (4 - m_predicted.get_speed());
	  }
	

	
	protected void p_predict() 
	{

		// predict position
		
		m_predicted.skid = false;

		if ((m_control_mask & LEFT) != 0)
		{
			m_predicted.angle = AngleUtils.normalize_m180_180(m_predicted.angle - angle_variation());

		}
		else if ((m_control_mask & RIGHT) != 0)
		{
			m_predicted.angle = AngleUtils.normalize_m180_180(m_predicted.angle + angle_variation());
		}

		if ((m_control_mask & ACCELERATE) != 0)
		{
			handle_acceleration();
		}
		else if ((m_control_mask & BRAKE) != 0)
		{
			handle_brake();
		}

		physical_car_behaviour(true);
				
	}

	@Override
	public void on_gate_bounce()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void on_wall_bounce()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void on_car_bounce(Car other)
	{
		// TODO Auto-generated method stub
		
	}


}
