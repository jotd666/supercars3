package supercars3.game;

import java.awt.Graphics2D;
import java.util.LinkedList;

import supercars3.base.*;

import supercars3.game.RoutableMobile;
import supercars3.sys.AngleUtils;

/**
 * The A.I. for computer cars & homer missiles
 * @author jffabre
 *
 */
public class MobileGuider
{
	private final GameOptions.ReadOnly ro_settings = GameOptions.instance().read_only;
	private PointDirection m_current_aim = null;
	private PointDirection m_next_aim = null;
	private final int AIM_CHANGE_SQUARE_DISTANCE = ro_settings.aim_change_square_distance;
	private static final int AIM_CHANGE_THRESHOLD_ANGLE = 90;
	private final int NB_MAX_WALL_BOUNCES = 5;
	
	private ControlPoint m_work_cp_1 = new ControlPoint();
	private ControlPoint m_work_cp_2 = new ControlPoint();
	private Segment m_segment = new Segment();
	private GameOptions game_options = GameOptions.instance();
	private int current_sqdist = Integer.MAX_VALUE;
	private boolean m_fixed_guiding;
	private long m_aim_change_time_lock = 0;
	private int m_nb_wall_bounces;
	private RoutableMobile m_mobile;
	
	private void goto_next_aim()
	{
		Mobile.GenericParameters predicted = m_mobile.get_predicted();

		double current_sqdist;
		
		do
		{
			m_current_aim = m_next_aim;

			current_sqdist = m_current_aim.square_distance(predicted.location);

			m_next_aim = get_next_aim();
		}
		while (current_sqdist < AIM_CHANGE_SQUARE_DISTANCE);
		
		
	}
	
	public MobileGuider(RoutableMobile m, boolean fixed_guiding)
	{
		m_fixed_guiding = fixed_guiding;
		m_mobile = m;
		reset();
	}
	
	 public void reset()
	 {
		 m_current_aim = null;
		 m_next_aim = null;
		 m_aim_change_time_lock = 0;
		 m_nb_wall_bounces = 0;
		 
		 if (m_mobile.get_circuit_data() != null)
		 {
			 // set main route again

			 m_mobile.set_route(m_mobile.get_circuit_data().get_main_route());
		 }
		 
	 }
	 
	 private PointDirection get_next_aim()
	 {
		 // get current mobile route
		 
		 Route r = m_mobile.get_route();
		 
		 // try to find another route (random)
		 
		 Route altr = m_mobile.get_circuit_data().get_alternate_route(r, m_current_aim.get_point());
		 
		 // change route
		 
		 m_mobile.set_route(altr);
		 
		 return altr.get_next_pzd(m_current_aim,false,altr == r);			 					

	 }
	 
	 public void lock_aim_change(long time)
	 {
		 m_aim_change_time_lock = time;
	 }
	 
	 public void on_wall_bounce()
	 {
		 m_nb_wall_bounces++;

		 if (m_nb_wall_bounces > NB_MAX_WALL_BOUNCES)
		 {
			 // sorry, could not find another way to prevent
			 // computer car to be stuck than killing it and allow it
			 // to be placed again on the right track
			 
			 m_mobile.die(ro_settings.carvcar_damage);
		 }
	 }
	 
	public void doit(double delta_angle, long elapsed_time)
	{

		Mobile.GenericParameters current = m_mobile.get_current();
		Mobile.GenericParameters predicted = m_mobile.get_predicted();

		if (m_aim_change_time_lock > 0)
		{
			m_aim_change_time_lock -= elapsed_time;
		}
		
		if (m_current_aim != null)
		{
			current_sqdist = m_current_aim.square_distance(predicted.location);
		}

		if (m_next_aim == null)
		{
			if (m_current_aim != null)
			{
				// initialize next aim
				m_next_aim = get_next_aim();
			}				
		}

		if ((m_next_aim != null) && (m_aim_change_time_lock <= 0))
		{
			boolean skip_to_next = (current_sqdist * current.get_speed() < AIM_CHANGE_SQUARE_DISTANCE);

			if (!skip_to_next)
			{
				
				m_work_cp_1.set_location((int)current.location.x,
						(int)current.location.y);
				m_work_cp_2.set_location((int)predicted.location.x,
						(int)predicted.location.y);

				m_segment.set_points(m_work_cp_1,m_work_cp_2);
				
				double opening_angle = m_segment.get_angle(m_current_aim.get_point());
				
				// also change aim if angle between aim and current and predicted is too big
				// (which would mean that object went past current without having been detected)
				
				skip_to_next = (Math.abs(opening_angle) > AIM_CHANGE_THRESHOLD_ANGLE);
			}
			
			
			if (!skip_to_next)
			{
				int next_sqdist = m_next_aim.square_distance(predicted.location);
				skip_to_next = next_sqdist < current_sqdist;
			}
			
			
			if (skip_to_next)
			{
				// closer to next than to current: scroll aims

				goto_next_aim();
			}

		}
		if (m_current_aim != null)
		{
			/*
			 * compute ideal angle between mobile and point
			 */

			m_work_cp_1.set_location((int)current.location.x,
					(int)current.location.y);

			m_segment.set_points(m_work_cp_1,m_current_aim.get_point());

			m_current_aim.angle = m_segment.get_angle_x(); 

			double angle_diff = AngleUtils.angle_difference(m_current_aim.angle,current.angle);
			double abs_angle_diff = Math.abs(angle_diff);
			
			if (abs_angle_diff > delta_angle)
			{
				double change_direction_coeff = 1.0;
				
				// prevent car against rotating forever around
				// the control point
				
				if (abs_angle_diff > AIM_CHANGE_THRESHOLD_ANGLE)
				{
					m_mobile.handle_brake();
				}
				
				if (!m_fixed_guiding)
				{
					change_direction_coeff = Math.min(1.0,Math.random() * 2);
				}
				
				
				angle_diff = Math.signum(angle_diff)*delta_angle*change_direction_coeff;
				predicted.angle = AngleUtils.normalize_m180_180(angle_diff + current.angle);
				
			}
			else
			{
				predicted.angle = m_current_aim.angle;
			}
						
			if (m_fixed_guiding)
			{
				// recompute speed coordinates according to current speed
				// and current angle

				predicted.set_speed(predicted.get_speed());

				current.copy_from(predicted);
			}
		}
	}
	
	public void zone_entered(Zone zone, RoutableMobile m)
	{
		// reset number of bounces only if checkpoint or finish
		if (zone.get_checkpoint_type() != Zone.CheckpointType.NONE)
		{
			m_nb_wall_bounces = 0;
		}
		LinkedList<PointDirection> zpl = zone.get_route_point_list(m.get_route().get_name());

		 if ((zpl != null) && (m_current_aim == null))
		 {
			 // aim is not defined: pick first point of zone
			 
			 m_current_aim = zpl.getFirst();		
					 
			 //m_current_aim = get_next_aim(m);
		 }
	}
	
	public void render(Graphics2D g, java.awt.Color paint)
	{
		if (game_options.debug_mode && (m_current_aim != null))
		{
			ControlPoint cp = m_current_aim.get_point();
			cp.paint(g,paint);
			
			double angle = Math.toRadians(m_current_aim.angle);
						
			g.drawLine(cp.getX(),cp.getY(),(int)(cp.getX()+50*Math.cos(angle)),
					(int)(cp.getY()+50*Math.sin(angle)));
		}
		
	}
}
