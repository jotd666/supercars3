package supercars3.game.weapons;

import supercars3.game.ExplosionView;
import supercars3.game.cars.Car;
import supercars3.sys.AngleUtils;

import java.awt.geom.*;

public class SuperMissile extends Weapon
{
	private double m_radius;
	private int m_elapsed;
	private static final double ANGLE_SPEED = 0.3;
	

	protected void p_update()
	{
		m_elapsed += m_elapsed_time;
		
		if (m_elapsed > 3000)
		{
			detonate();
		}
	}
	
	public void out_of_bounds()
	{
		// does nothing to the super missile
	}
	public void car_collision()
	{
		// does nothing to the super missile
	}
	
	public void predict()
	{
		Point2D.Double centre = m_launcher.get_current().location;
		// centre of the circle
		m_predicted.angle = AngleUtils.normalize_m180_180
		(m_predicted.angle - m_elapsed_time * ANGLE_SPEED);
			
		double radangle = Math.toRadians(m_predicted.angle+90.0);
		
		m_predicted.location.setLocation(centre.x + 
				m_radius * Math.cos(radangle),
				centre.y + m_radius * Math.sin(radangle));
		
	}
	public SuperMissile(Car launcher,WeaponView wv,ExplosionView ev)
	{
		super(launcher,wv,ev);
		
		double angle = launcher.get_current().angle;
		m_radius = launcher.get_centre_offset().getX() * 1.6;
		
		m_current.angle = angle;
		
		m_elapsed = 0;

	}
	
}
