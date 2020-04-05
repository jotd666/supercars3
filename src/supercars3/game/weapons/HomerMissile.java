package supercars3.game.weapons;

import supercars3.game.ExplosionView;
import supercars3.game.MobileGuider;
import supercars3.game.RoutableMobile;
import supercars3.game.cars.Car;
import supercars3.base.*;


public class HomerMissile extends Weapon implements RoutableMobile
{
	private Route m_current_route;
	private final double MAX_SPEED = 1.0;
	private final double LINEAR_SPEED = 0.5;
	private final double ACCELERATION = 0.05;
	private final double ANGULAR_SPEED = 1.5;
	/*private final double MAX_SPEED = 0.2;
	private final double LINEAR_SPEED = 0.1;
	private final double ACCELERATION = 0.05;*/
		
	private CircuitData m_data;
	private MobileGuider m_guider = new MobileGuider(this,true);

	public void handle_brake()
	{
		// do nothing
	}
	protected void p_update()
	{
		if (is_alive())
		{
			//m_guider.doit(this);
			handle_acceleration();
		}
	}
	
	public void die(int damage)
	{
		// never called
	}
	
	protected double get_linear_speed() 
	{
		return LINEAR_SPEED;
	}
	protected double get_acceleration(double speed) 
	{
		double rval = 0.0;
		
		if (speed < MAX_SPEED)
		{
			rval = ACCELERATION;
		}
		
		return rval;
	}		
	
	// TEMP debug
	
	/*
	protected void p_render(Graphics2D g, Rectangle view_bounds)
	{
		super.p_render(g, view_bounds);
		if ((m_aim != null) && (m_aim.point != null))
		{
			ControlPoint cp = m_aim.point;
			cp.paint(g,java.awt.Color.RED);
			
			double angle = Math.toRadians(m_aim.angle);
			
			g.drawLine(cp.getX(),cp.getY(),(int)(cp.getX()+50*Math.cos(angle)),
					(int)(cp.getY()+50*Math.sin(angle)));
		}
		
	}*/
	
	public CircuitData get_circuit_data()
	{
		return m_data;
	}
	
	public Route get_route()
	{
		return m_current_route;
	}
	public void set_route(Route r)
	{
		m_current_route = r;
	}
	
	public void zone_entered(Zone zone,Boundary by)
	{
		 m_guider.zone_entered(zone,this);
	}
	
	 public void zone_exited(Zone zone,Boundary by)
	 {
			 
		
	 }

	 
	public void predict()
	{
		  apply_speed();
		  m_guider.doit(ANGULAR_SPEED * m_elapsed_time, m_elapsed_time);	
	}
	
	public HomerMissile(Car launcher,WeaponView wv,ExplosionView ev)
	{
		super(launcher,wv,ev);
		m_data = launcher.get_circuit_data();
		m_current_route = launcher.get_route();
	}
	
}
