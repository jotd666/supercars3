package supercars3.game.cars;



import java.awt.Graphics2D;
import java.awt.Rectangle;

import supercars3.base.*;
import supercars3.game.*;
import supercars3.game.players.Driver;
import supercars3.game.weapons.WeaponFactory;


public class ComputerCar extends Car implements RoutableMobile
{
	private final static double ANGULAR_SPEED = ro_settings.cpu_angular_speed;
	private MobileGuider m_guider;
	private final static int LOCK_AIM_TIME = ro_settings.lock_aim_time;
	private Car m_last_bounced_car = null;
	private int m_nb_same_car_bounce = 0;
	private final static int MAX_SAME_CAR_BOUNCE_FRONT = ro_settings.max_same_car_bounce_front;
	private final static int MAX_SAME_CAR_BOUNCE_REAR = ro_settings.max_same_car_bounce_rear;
	
	public void die(int damage)
	{
		die(false,damage);
	}
	
	public void on_car_bounce(Car other)
	{
		m_guider.lock_aim_change(200); // no aim change during a while
		if (other == m_last_bounced_car)
		{
			int prev_same_car_bounce = m_nb_same_car_bounce;
			m_nb_same_car_bounce+=(int)(Math.random()*2+1);
			if (prev_same_car_bounce < MAX_SAME_CAR_BOUNCE_REAR && 
					m_nb_same_car_bounce>=MAX_SAME_CAR_BOUNCE_REAR)
			{
				// fire rear missile to get rid of the obstacle
				fire(get_equipment().mounted_rear);
				m_nb_same_car_bounce = 0;
				m_last_bounced_car = null;
			}
			else if (prev_same_car_bounce < MAX_SAME_CAR_BOUNCE_FRONT && 
					m_nb_same_car_bounce>=MAX_SAME_CAR_BOUNCE_FRONT)
			{
				// fire front missile to get rid of the obstacle
				fire(get_equipment().mounted_front);
			}
		}
		else
		{
			m_nb_same_car_bounce=0;
			m_last_bounced_car = other;
		}
		
	}

	public void on_gate_bounce()
	{
		m_guider.lock_aim_change(LOCK_AIM_TIME); // no aim change during a while to focus on the gate
	}
	public void on_wall_bounce()
	{
		m_guider.on_wall_bounce();
	}

	 public void killed_by(Car killer_car,int damage)
	  {
		 super.killed_by(killer_car,damage);
		 m_guider.reset();
	  }

	 public void race_start(int laps_to_go, CircuitData data, 
				int nb_checkpoints, int missed_checkpoints_tolerance, Route current_route)
	 {
		 super.race_start(laps_to_go, data, nb_checkpoints, missed_checkpoints_tolerance, current_route);
		
		 m_guider.reset();
	 }
	
	protected void p_render(Graphics2D g, Rectangle view_bounds)
	{
		  super.p_render(g, view_bounds);
		  m_guider.render(g,get_debug_paint());
		  //g.drawString(get_equipment().accessory[Equipment.ENGINE].get_count()+"", (int)m_current.location.x,(int)m_current.location.y);
	}
	
	public void zone_entered(Zone zone,Boundary by)
	{
		 m_guider.zone_entered(zone,this);
		 
		 super.zone_entered(zone, by);
	}
	
	private double angle_variation() 
	{
	    return ANGULAR_SPEED * m_elapsed_time;// * (2 - m_predicted.get_speed());
	  }

	 
	protected void p_predict()
	{
		if (!game_options.freeze_cpu_cars)
		{
			// computer car always accelerates
			handle_acceleration();
			apply_speed();
			m_guider.doit(angle_variation(),m_elapsed_time);
		}
		
		physical_car_behaviour(true);
	}
	
	public ComputerCar(CarBodyView body_view,
			CarShadowView shadow_view,
			ExplosionView explosion,
			WeaponFactory weapons,
			Driver driver,
			int position)  
	{	
		super(body_view,shadow_view,explosion,weapons,driver,position);
		 m_guider = new MobileGuider(this,false);
	}
}
