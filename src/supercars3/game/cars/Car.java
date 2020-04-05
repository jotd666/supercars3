package supercars3.game.cars;

import java.awt.*;
import java.awt.image.*;
import java.awt.geom.*;
import java.util.*;


import supercars3.base.*;
import supercars3.game.*;
import supercars3.game.GameOptions.SoundSelection;
import supercars3.game.players.Driver;
import supercars3.game.weapons.*;
import supercars3.sys.*;

/**
 * <p>Titre : </p>
 * <p>Description : </p>
 * <p>Copyright : Copyright (c) 2005</p>
 * <p>Société : </p>
 * @author non attribuable
 * @version 1.0
 */

public abstract class Car extends Mobile
{
	protected final static GameOptions.ReadOnly ro_settings = GameOptions.instance().read_only;
	private final double LINEAR_SPEED = ro_settings.linear_speed;
	private final double BRAKE_POWER = ro_settings.car_brake_power;
	private final double SPEED_LOSS_COEFFICIENT = ro_settings.speed_loss_coefficient_ground;
	private final double SPEED_LOSS_COEFFICIENT_AIR = ro_settings.speed_loss_coefficient_air;
	private final double RESAMPLING_RATIO = ro_settings.resampling_ratio;	
	private final double ACCELERATION_POWER = ro_settings.car_acceleration_power;
	private final double m_power_slide_threshold = ro_settings.power_slide_threshold;
	//skidspds	dc.w	130*16,105*16,080*16
	private long m_speed_mix_time;
	private double m_damp_factor;
	private static final int NB_SIMULTANEOUS_WEAPONS = 3;
	private static final double MIN_SPEED = 0.2;
	
	private static final long RESUME_DURATION = 2500;
	
	private static final long NITRO_DURATION = 1200;
	
	// jump duration is strongly correlated to engine 2 max speed & linear speed
	// because if not finely tuned enough soime circuits with multiple jumps are
	// not playable
		
	private static final double JUMP_ALTITUDE = 6.0;
	
	private static final long RESURRECT_DURATION = 3000;
	private static final long RESURRECT_RESUME = 2500;
	private static final double NITRO_ALTITUDE = 8.0;
	private static final double ALTITUDE_THRESHOLD = 2.0;
	private static final long BUMP_DURATION = 500;
	
	private static final double SPEED_SLOPE_INCREASE = 1.1;
	private static final double SPEED_SLOPE_DECREASE = 0.5;

	private AffineTransform m_work_rotation = AffineTransform.getRotateInstance(0);
	
	private CircuitChecker m_circuit_checker;
	
	private Route m_current_route;
	protected CarBodyView m_body_view;
	private SfxSet m_sfx_set;
	private CarShadowView m_shadow_view;
	private ExplosionView m_explosion_view;
	private WeaponFactory m_weapon_factory;
	private Driver m_driver;

	private Weapon [] m_fired_weapons = new Weapon[NB_SIMULTANEOUS_WEAPONS*Equipment.Item.values().length];
	private ControlZone [] m_control_zone;
	private int[] m_zone_indirection;
	private boolean m_force_lap_validation;
	private boolean m_within_jump_zone;
	private boolean m_entered_jump_zone;
	protected long m_last_screech_sound = 0;
	private int m_unsolved_counter = 0;
	private Point2D.Double m_bounce_offset = new Point2D.Double();
	
	private WavLoop m_engine_sound = null;

	
	private CircuitData m_data = null;
	public CircuitData get_circuit_data()
	{
		return m_data;
	}

	void set_sfx_set(SfxSet sfx_set)
	{
		m_sfx_set = sfx_set;
	}
	

	protected class Jump
	{
	  public boolean active;
	  public boolean kill_when_landed;
	  public double altitude;
	  public double max_altitude;
	  public double min_altitude;
	  public double altitude_offset;
	  private int vertical_speed;
	  public long counter;
	  public long duration;
	  	  
	  public void init(long duration, long counter, double max_altitude, double min_altitude)
	  {
		  this.duration = duration;
		  this.counter = counter;
		  this.max_altitude = max_altitude;
		  this.min_altitude = min_altitude;
		  this.altitude_offset = 0;
		  active = true;
		  kill_when_landed = false;
		  vertical_speed = 4;
	  }
	  
	  public void end()
	  {
		  active = false;
		  
		  if (kill_when_landed)
		  {
			  kill_when_landed = false;
			  die(false,ro_settings.carexpl_damage);
		  }
		  else
		  {
			  altitude = 0.0;
		  }
	  }
	  
	  public void update(long elapsed_time)
	  {

		  if (active)
	    	{
	    	// compute altitude
	    	
	    		counter += elapsed_time;
	    		
	    		if ((counter > duration/2) && (altitude < min_altitude + altitude_offset))
	    		{
	    			// end of jump
	    			if ((m_within_jump_zone) && (!kill_when_landed))
	    			{
	    				// end of jump while in jump zone
	    				// jump of death
	    				m_current.set_speed(0);
	    				m_predicted.set_speed(0);
	    		    	altitude_offset = -JUMP_ALTITUDE; // crash below
	    		    	vertical_speed = 1;
	    		    	kill_when_landed = true;
	    			}
	    			else
	    			{
	    				if (m_driver.is_human())
	    				{
							m_sfx_set.play(SfxSet.Sound.land);

	    				}
	    				
	    				// so car will skid a little 
	    				
	    				m_bump_timer = BUMP_DURATION;
	    				
	    				end();
	    			}
	    		}
	    		else
	    		{
	    			// parabolic law for altitude
	    			
	    			altitude = min_altitude + max_altitude / 
	    			(duration*duration/vertical_speed) * (counter) * (duration - counter);
	    		}
	    	}		  
	  }
  }
  
  protected Jump m_jump = new Jump();
  
  private Equipment m_equipment;
  protected int m_view_index;
  private long m_resume_timer;
  private long m_bump_timer;
  private int m_explosion_frame;
  private double m_max_speed;
  
  private int m_points = 0;
  
  private int m_total_nb_checkpoints;
  private int m_missed_checkpoints_tolerance;
  private PointDirection m_resume_point;
  private Point2D.Double m_kill_point = new Point2D.Double();
  private Route.PointDirectionIterator m_resume_point_iterator;

  private HashSet<Zone> m_entered_zone_list = new HashSet<Zone>();
  private HashSet<Zone> m_entered_checkpoint_zone_list = new HashSet<Zone>();
  
  private int m_position;
  private int m_laps_to_go;
  private int m_initial_handicap;
  
  public static final int REPOSITION = 3;
  public static final int REPOSITION_COMPLETE = 4;
  public static final int RESURRECT = 5;
  public static final int WINNER = 6;
    
  public int get_width()
  {
	  return m_body_view.get_width();
  }
  public int get_height()
  {
	  return m_body_view.get_height();
  }
  public int get_points()
  {
	  return m_points;
  }
  
  public int get_nb_checkpoints()
  {
	  return m_entered_checkpoint_zone_list.size();
  }
  
  void reset_location_offset()
  {
	  m_bounce_offset.setLocation(0,0);
  }
  
  Point2D get_location_offset()
  {
	  return m_bounce_offset;
  }
  
  void add_location_offset(double x_offset, double y_offset)
  {
	  m_bounce_offset.x += x_offset;
	  m_bounce_offset.y += y_offset;
  }

  public void add_points(int p)
  {
	  if (m_points + p >= 0)
	  {
		  m_points += p;
	  }
  }
  public Equipment get_equipment()
  {
	  return m_equipment;
  }
  
  public double get_altitude()
  {
	  return m_jump.altitude;
  }
 
  public abstract void on_gate_bounce();
  public abstract void on_wall_bounce();
  public abstract void on_car_bounce(Car other);
 
  
  public void set_circuit_checker(CircuitChecker cc)
  {
	  m_circuit_checker = cc;
  }
  
  public CircuitChecker get_circuit_checker()
  {
	  return m_circuit_checker;
  }
  
  public Route get_route()
  {
	  return m_current_route;
  }
  
  public void set_route(Route r)
  {
	  m_current_route = r;
  }
public void set_initial_location(PointDirection car_start_point,Route.PointDirectionIterator rpi) throws Exception
  {

	ControlPoint cp = car_start_point.get_point();

	m_current.location.setLocation(cp.getX(), cp.getY());
	m_current.angle = car_start_point.angle;
	m_unsolved_counter = 0;

	if (rpi != null)
	{
		m_resume_point_iterator = rpi;
	}
	
	// tricky test to count the lap if finish line is between killed car and next resume point
	// when lap is completed by crossing the line, a flag is set in the iterator
	// when next resume point is selected, this flag is cleared
	
	if (!m_resume_point_iterator.is_lap_completed() && 
			(m_resume_point_iterator.is_first_resume_point(m_resume_point)))
	{
		lap_completed();
	}
	
	// compute next resume point

	m_resume_point = m_resume_point_iterator.next(car_start_point);
	
	Mobile.GenericParameters current = get_current();
	Corner[] corners = current.retrieve_corners();

	Zone start_zone = cp.get_explicit_zone();

	ControlZone cz = m_control_zone[m_zone_indirection[start_zone.get_name()]];
	cz.set_appeared(true);
	
	for (int i = 0; i < corners.length; i++) 
	{
		m_circuit_checker.update_mobile_corner(corners[i], start_zone, 2);
		if (corners[i].zone == null) 
		{
			throw new Exception("initial car location (corner " + (i + 1) + ")");
		}
	}

	// at start, make predict state valid

	get_predicted().copy_from(get_current()); 	  
  }

  public PointDirection get_resume_point()
  {
	  return m_resume_point;
  }
  
  public int get_position()
  {
	  return m_position;
  }
  
  public int get_laps_to_go()
  {
	  return m_laps_to_go;
  }
  
  @Override
  public void set_state(int state)
	{
	  // controls to lock WINNER state unless ALIVE state is set
	  if ((get_state() != WINNER) || state == ALIVE)
	  {
		  super.set_state(state);
	  }
	}
	
  public void lap_completed()
  {
	  // no need for handicap now
	  
	  m_initial_handicap = 0;
	  
	  if (m_laps_to_go > 0)
	  {
		  m_laps_to_go--;
	  }
	  else
	  {
		  set_state(WINNER);
	  }
	  reset_zones();
	  m_entered_checkpoint_zone_list.clear();
	  m_entered_zone_list.clear();
	  
	  m_force_lap_validation = false; // was set to true at first lap
	  
	  m_resume_point_iterator.lap_completed();
  }
  public int get_health()
  {
    return m_equipment.get_health();
  }
  public Point2D get_centre_offset()
  {
	  return  m_body_view.get_centre_offset();
  }
  
 
  public void die(boolean for_good,int damage)
  {
	  if (for_good)
	  {
		  m_equipment.set_health(0);
	  }
	  killed_by(null,damage);
  }
  
  public Equipment.Accessory get_accessory(Equipment.Item item)
  {
	  return get_equipment().get_accessory(item);
  }
  private static int [] RAM_POWER = {0,0x400,0x800,0xC00};
  private static int RAM_DAMAGE = 0x180;
  
  public boolean rammed_by(Car ramming_car)
  {
	  Equipment.Accessory ram = ramming_car.get_accessory(Equipment.Item.RAM);
	  double ram_level = ram.get_count();
	  boolean killed = false;
	  
	  int raml = RAM_POWER[(int)ram_level];
	  if (raml>0)
	  {
		 // from original game
		 // 1 chance out of 21 (ram:3), 32 (ram:2), 64 (ram:3) of being rammed

		  killed = (Math.random()*0x10000 < raml);
		  
		  if (killed)
		  {
		  // the more ram, the more luck
			  killed_by(ramming_car,RAM_DAMAGE);
		  }
	  
	  }

	  return killed;
  }
  
  public void killed_by(Car killer_car,int damage)
  {
	  // play sound only if human or killed by human weapon/ramming
	  
	  if (m_driver.is_human() || ((killer_car != null) && killer_car.get_driver().is_human()))
	  {
		  m_sfx_set.play(SfxSet.Sound.explosion);
		  stop_engine_sound();
	  }
	  set_state(EXPLODING);
	  m_resume_timer = 0;
	  m_explosion_frame = 0;
	  m_current.speed.setLocation(0,0); // no speed
	  m_predicted.copy_from(m_current);
	  // remember the point where the car was destroyed
	  m_kill_point.setLocation(m_predicted.location);
	  
	  do_damage(damage);
  }
  
  public void do_damage(int damage_points)
  {
//  carnrj 10000
//	  carvcardam	equ	$08
//	  carvwaldam	equ	$10
//	  carexpldam	equ	$100
//	  carmissdam	equ	$180
//	  carramdam	equ	$180
//	  gatedamage	equ	$180
//	  traindamage	equ	$200

	  m_equipment.add_health(-damage_points);
  }
  
  public boolean same_plane(Car other)
  {
	  return (Math.abs(m_jump.altitude - other.m_jump.altitude) < ALTITUDE_THRESHOLD);
  }
  
  public void init_engine_sound()
  {
	  m_last_screech_sound = 0;
	  
	  if (m_driver.is_human())
	  {
		  if (GameOptions.instance().get_sfx_mode() == SoundSelection.sound_effects)
		  {
			  if (m_engine_sound == null)
			  {
				  m_engine_sound = new WavLoop(DirectoryBase.get_sound_path()+"game"+
						  java.io.File.separator+"engine");
				  m_engine_sound.play();
			  }
			  else
			  {
				  m_engine_sound.resume();
			  }
		  }
		  
	  }	  
  }
  public void stop_engine_sound()
  {
	  if (m_engine_sound != null)
	  {
		  m_engine_sound.pause();
	  }
  }  
  public void end_engine_sound()
  {
	  if (m_engine_sound != null)
	  {
		  m_engine_sound.end();
	  }
  }  
  public boolean is_above(Car other)
  {
	  return (m_jump.altitude > other.m_jump.altitude);
  }
  
  
  public Driver get_driver()
  {
	  return m_driver;
  }
  
  public void weapon_collision(Car launcher)
  {
	  boolean kill_me = true; 
  
	  if (m_driver.is_human())
	  {
		  Equipment.Accessory armour = m_equipment.get_accessory(Equipment.Item.ARMOUR);
		  
		  double armour_level = armour.get_count();
		  if (armour_level > 0)
		  {
			  
			  // the more armour, the more luck
			  kill_me = (Math.random() > armour_level/(armour.get_max_items()+1));
		  }
	  }
	  
	  if (kill_me)
	  {

		  killed_by(launcher,ro_settings.carmissile_damage);
	  }
  }
 
  
  public void delay_resurrect()
  {
	  if (get_state() == RESURRECT)
	  {
		  set_state(REPOSITION_COMPLETE);
	  }
  }
  public void drive()
  {
	  switch (get_state())
	  { 
	  case EXPLODING:
	  {
		  m_explosion_frame = (int)(m_resume_timer / 150);
		  if (m_explosion_frame >= CarExplosionView.NB_FRAMES)
		  {
			  set_state(REPOSITION);
			  m_resume_timer = 0;
			  m_jump.altitude = NITRO_ALTITUDE * 2; // top priority display
		  }			  
	  }
	  break;
	  case ALIVE:
		  m_driver.move(this,m_elapsed_time);
		  break;
	  case REPOSITION:
		  m_resume_timer += m_elapsed_time;
		  
		  // compute current "ghost" car location
		 
		  if (m_resume_timer < RESUME_DURATION)
		  {
			  double currsq = m_resume_timer;
			  double maxsq = RESUME_DURATION;
			  double maxmcurr = maxsq - currsq;
			  
			  double carx = ((m_kill_point.x * maxmcurr) + 
					  (m_resume_point.get_point().getX() * currsq)) / maxsq;
			  double cary = ((m_kill_point.y * maxmcurr) + 
					  (m_resume_point.get_point().getY() * currsq)) / maxsq;
			  
			  m_predicted.location.x = carx;
			  m_predicted.location.y = cary;
		  }
		  else
		  {
			  reposition();
		  }
		  break;
	  case REPOSITION_COMPLETE:
		  set_state(RESURRECT); // can be canceled by delay_resurrect()
		  break;
			  
	  case RESURRECT:
		  resurrect();
		  break;
	  default:
		  break;
	  }
  }
  
  private boolean is_bumped()
  {
	  return (m_bump_timer > 0);
  }
  
  public void fire(Equipment.Item weapon_id)
  {
	  Equipment.Accessory acc = m_equipment.get_accessory(weapon_id);	  
	  
	  if (weapon_id == Equipment.Item.NITRO)
	  {
		  // fire the nitro
		  
		  if (!m_jump.active)
		  {			  
			  if ((acc != null) && (acc.get_count() > 0))
			  {
				  acc.one_less();
				  
				  // increase speed (both cases, while
				  // accelerating or not)

				  m_predicted.add_speed(m_max_speed / 4);
				  m_current.add_speed(m_max_speed / 2);

				  m_jump.init(NITRO_DURATION,0,NITRO_ALTITUDE,0.0);

					m_sfx_set.play(SfxSet.Sound.jump);

			  }
		  }
	  }
	  else
	  {
		  // cannot drop a mine while in the air
		  
		  if (!m_jump.active || (weapon_id != Equipment.Item.MINE))
		  {
			  // find a free slot in the allocated range of the fired weapons array
			  int slot = -1;
			  int slot_start = weapon_id.ordinal();
			  int slot_end = slot_start + NB_SIMULTANEOUS_WEAPONS;
			  for (int i = slot_start; i < slot_end && slot == -1; i++)
			  {
				  if (m_fired_weapons[i] == null)
				  {
					  slot = i;
				  }
			  }

			  if ((slot != -1) && acc != null)
			  {

				  if (acc.get_count() > 0)
				  {			  
					  
					  if (m_driver.is_human() && (m_sfx_set != null))
					  {
						  // play sound
					  
						  switch (weapon_id)
						  {
						  case MINE:
								m_sfx_set.play(SfxSet.Sound.mine_drop);
							  break;
						  case NO_WEAPON:
							  break;
						  default:
								m_sfx_set.play(SfxSet.Sound.fire);
						  break;
						  }
								
					  }


					  m_fired_weapons[slot] = m_weapon_factory.create(weapon_id,this);
					  acc.one_less();
				  }
			  }
		  }
	  }
  }
 

 public void solved_iteration()
 {
	 m_unsolved_counter = 0;
 }
 public void unsolved_iteration()
 {
	 m_unsolved_counter++;
	 // unable to solve the new car position: put back current position
	 // at least it is valid
	 m_predicted.copy_from(m_current);

	 // if too many times solved in a row: explode
	 if (m_unsolved_counter>50)
	 {
		 die(false,0);
	 }
 }
 
  public void accept_move(CircuitChecker cc)
  {
	  cc.accept_move(this);
	  
	  // move the weapons
	  for (int i = 0; i < m_fired_weapons.length; i++)
	  {
		  Weapon w = m_fired_weapons[i];
		  if (w != null)
		  {
			  cc.accept_move(w);
		  }
	  }	  
  }
  
  public void set_health(int health)
  {
    m_equipment.set_health(health);
  }

  public long get_last_bounce_time()
  {
	  return m_absolute_last_bounce_time;
  }
  public void set_last_bounce_time(long last_bounce_time)
  {
	  m_absolute_last_bounce_time = last_bounce_time;
  }
  public long get_last_update_time()
  {
	  return m_absolute_elapsed_time;
  }
  public void update(long elapsed_time, long absolute_elapsed_time) 
  {
	  if (m_engine_sound != null)
	  {
		  float resampling_ratio = (float)(m_current.get_speed()*RESAMPLING_RATIO + 1.0);
	  
		  m_engine_sound.set_resampling_ratio(resampling_ratio);
	  }
	  
    m_predicted.copy_from(m_current);
    
    m_elapsed_time = elapsed_time;
    m_absolute_elapsed_time = absolute_elapsed_time;
    
    if (m_bump_timer > 0)
    {
    	m_bump_timer -= elapsed_time;
    }
    
    if (get_state() != ALIVE)
    {
    	m_resume_timer += elapsed_time;
    }
    else
    {
    	// update jump if alive
    	m_jump.update(elapsed_time);
 
    }
    // detect dead weapons
    
    for (int i = 0; i < m_fired_weapons.length; i++)
    {
    	Weapon w = m_fired_weapons[i];
    	
    	if (w != null)
    	{
    		if (w.is_dead())
    		{
    			m_fired_weapons[i] = null; // de-reference
    		}
    		else
    		{
    			w.update(elapsed_time);
    		}
    	}
    }
  }
  
  protected abstract void p_predict();

  public void predict()
  {
	  m_accelerating = false;
	  
	  if (get_state() == ALIVE)
	  {
		  p_predict();
	  }

	  
	  // weapons are active whatever the car state

	  for (int i = 0; i < m_fired_weapons.length;i++)
	  {
		  Weapon w = m_fired_weapons[i];
		  if (w != null)
		  {
			  w.predict();
		  }
	  }
  }

  
public void render(Graphics2D g, Rectangle view_bounds) {
	// render weapons
	
	for (int i = 0; i < m_fired_weapons.length; i++)
	{
		Weapon w = m_fired_weapons[i];
		if (w != null)
		{
			w.render(g,view_bounds);
		}
	}
	
	super.render(g,view_bounds);
	
	if (game_options.debug_mode) 
	{
		g.setColor(Color.WHITE);
		g.drawString("p="+m_position+" z="+get_nb_zones()+" c="+get_nb_checkpoints(),
				(int)m_current.location.x,(int)m_current.location.y+10);
		g.drawString("l="+m_laps_to_go+" e="+(int)(m_equipment.get_engine()*10)/10.0,
				(int)m_current.location.x,(int)m_current.location.y+20);
				
		m_resume_point.paint(g);
		
	}

}
private boolean one_jump_zone() 
{
	boolean jump = false;

	Corner[] corners = m_predicted.get_corners();

	for (int i = 0; i < corners.length && !jump; i++) 
	{
		Zone z = corners[i].zone;
		if ((z != null) && z.is_jump()) 
		{
			jump = true;
		}
	}
	return jump;
  }
private boolean all_jump_zones() 
{
	boolean jump = true;

	Corner[] corners = m_predicted.get_corners();

	for (int i = 0; i < corners.length && jump; i++) 
	{
		Zone z = corners[i].zone;
		if ((z != null) && !z.is_jump()) 
		{
			jump = false;
		}
	}
	return jump;
  }
public void zone_entered(Zone zone,Boundary entered_by)
{
	boolean just_entered_in_jump = false;
	
	ControlZone cz = m_control_zone[m_zone_indirection[zone.get_name()]];
	
	// if first enter in zone for this lap, increase number of checkpoints
	
	if (cz.entered == null)
	{		
		boolean was_checkpoint = (zone.get_checkpoint_type() == Zone.CheckpointType.CHECKPOINT);
		
		if (was_checkpoint)
		{
			m_entered_checkpoint_zone_list.add(zone);
		}

		m_entered_zone_list.add(zone);

		if (m_resume_point.get_point().get_explicit_zone() == zone)
		{
			// just entered in the resume point zone
			//System.out.println("before: "+m_resume_point.get_route_index());
			m_resume_point = m_resume_point_iterator.next();
			//System.out.println("after: "+m_resume_point.get_route_index());
		}
		else if (was_checkpoint)
		{
			// just entered in a checkpoint zone: resync
			
			LinkedList<PointDirection> l = zone.get_route_point_list(1);
			
			if ((l != null) && (!l.isEmpty()))
			{
				PointDirection p = l.getFirst();

				m_resume_point = m_resume_point_iterator.next(p);
			}
		}
	}

	cz.entered(zone,entered_by);

	// update zone is jump flag
	if (zone.is_jump())
	{
		if (one_jump_zone() && (!m_entered_jump_zone))
		{
			m_entered_jump_zone = true;
		}
		if (all_jump_zones() && (!m_within_jump_zone))
		{
			m_within_jump_zone = true;
			just_entered_in_jump = true;
		}
	}
	else
	{
		m_within_jump_zone = false;
		m_entered_jump_zone = false;
	}
	
	if (just_entered_in_jump)
	{
		// entered in the jump zone: make a jump according to
		// the current car speed
				
		if (!m_jump.active)
		{
			double speed = m_current.get_speed();
			long duration = OpponentProperties.convert_jump(speed);
			
			double max_altitude = JUMP_ALTITUDE * speed;
			m_jump.init(duration,0,max_altitude + JUMP_ALTITUDE,JUMP_ALTITUDE);
		}
	}
}


  public void zone_exited(Zone zone,Boundary exited_by)
  {
	  ControlZone cz = m_control_zone[m_zone_indirection[zone.get_name()]];
	  	  	  
	  boolean appeared = cz.is_appeared();
	  
	  boolean exit_by_entry = cz.exited(zone,exited_by);
	  
	  if ((!exit_by_entry) || appeared)
	  {
		  // zone has been exited by another boundary
		  // than the entering boundary
		  
		  if (zone.get_checkpoint_type() == Zone.CheckpointType.FINISH)
		  {
			  
			  /*
			  // exited the finish line: check if all checkpoints
			  // have been validated, recount them to be sure
			  
			  int nb_checkpoints = 0;
			  
				  
			  if (!m_force_lap_validation)
			  {
				  
				  for (int i = 0; i < m_control_zone.length && (nb_checkpoints < m_total_nb_checkpoints); i++)
				  {
					  Zone z = m_control_zone[i].zone;
					  if ((z != null) && 
							  (z.get_checkpoint_type() == Zone.CheckpointType.CHECKPOINT))
					  {
						  nb_checkpoints++;
					  }
				  }
			  }
			  
			  m_nb_checkpoints = nb_checkpoints;
			  */
			  
			  if ((m_force_lap_validation) || (get_nb_checkpoints() >= m_total_nb_checkpoints-m_missed_checkpoints_tolerance))
			  {
				  // validate the lap
				  
				  lap_completed();
				  
				  // clear the "appeared" flag
				  
			  }
		  }
	  }
	  cz.set_appeared(false);

  }
  
  // init variables for race start
  
  public void race_start(int laps_to_go, CircuitData data, 
		int nb_checkpoints, int missed_checkpoints_tolerance, Route current_route)
  {
	  Collection<Zone> zones = data.get_zone_list();
	  
	  m_absolute_last_bounce_time = 0;
	  
	  m_data = data;
	  
	  // inertia to stabilize the initial ranking before all zone counters are
	  // reset when the cars cross the finish line for the first time
	  
	  m_initial_handicap = m_position * 2;

	  // the last will be the first ...

	  if (GameOptions.instance().get_car_moves() == GameOptions.CarMoves.normal)
	  {
		  m_position = Route.MIN_NB_POINTS - m_position + 1;
	  }
	  	  
	  m_laps_to_go = laps_to_go;

    double engine_value = m_equipment.get_engine();
 
    m_max_speed = OpponentProperties.convert_speed(engine_value);
    
    m_current_route = current_route;
    
	m_current = new Parameters();		
	m_predicted = new Parameters();
    set_state(ALIVE);
    m_control_zone = new ControlZone[zones.size()];
        
    int i = 0;
    int max_name = 0;
    for (Zone z : zones)
    {
    	if (z.get_name() > max_name)
    	{
    		max_name = z.get_name();
    	}
    	
       	m_control_zone[i] = new ControlZone();
    	i++;
    	
    }
    m_zone_indirection = new int[max_name+1];
    for (i = 0; i < m_zone_indirection.length; i++)
    {
    	m_zone_indirection[i] = -1;
    }
    
    i = 0;
    
    for (Zone z : zones)
    {
    	m_zone_indirection[z.get_name()] = i;
    	i++;
    }
   
    
    m_total_nb_checkpoints = nb_checkpoints;
    
    m_entered_checkpoint_zone_list.clear();
    m_entered_zone_list.clear();
    
    m_force_lap_validation = true;
    m_missed_checkpoints_tolerance = missed_checkpoints_tolerance;
    
    m_view_index = CarBodyView.STRAIGHT;
    m_resume_timer = 0;
    m_bump_timer = 0;
    m_jump.end();
    
    m_within_jump_zone = false;
    m_entered_jump_zone = false;
    
    // remove old weapons from previous race
    
    for (i = 0; i < m_fired_weapons.length; i++)
    {
    	m_fired_weapons[i] = null;
    }
    reset_zones();
    init_engine_sound(); // SEB
  }

  public Car(CarBodyView body_view,
		  CarShadowView shadow_view,
		  ExplosionView explosion,
		  WeaponFactory weapons,		  
		  Driver driver,
		  int position)  
  {
	  super(CarBodyView.NB_TOTAL_FRAMES);
	  m_body_view = body_view;
	  m_shadow_view = shadow_view;
	  m_explosion_view = explosion;
	  m_weapon_factory = weapons;
	  m_driver = driver;
	  m_position = position;
	  
	  set_debug_paint(m_body_view.get_paint());
	  
	  int power_slide = GameOptions.instance().get_power_slide().ordinal();
	  int total_power_slide = GameOptions.PowerSlide.values().length;
	  m_speed_mix_time =  power_slide * (ro_settings.speed_mix_time_max-ro_settings.speed_mix_time_min) / (total_power_slide - 1) 
	  + ro_settings.speed_mix_time_min;
	  
	  m_damp_factor = power_slide * (ro_settings.damp_factor_max-ro_settings.damp_factor_min) / (total_power_slide - 1) 
	  + ro_settings.damp_factor_min;
	  boolean turbocharged = (m_driver.is_human() && GameOptions.instance().get_player_boost());

	  m_equipment = new Equipment(turbocharged);
	  

  }

protected double get_linear_speed()
  {
    return LINEAR_SPEED;
  }

  protected double get_acceleration(double speed) 
  {
	  double rval = 0.0;
	  
	  // cannot accelerate while in air
	  	  
	  if ((speed < m_max_speed) && (!m_jump.active))
	  {
		  rval = ACCELERATION_POWER * 
		  (2 + m_equipment.get_engine()) * m_elapsed_time;
	  }
	  
	  return rval;
  }
  
  protected void p_render(Graphics2D g, Rectangle view_bounds)
  {	  
	  BufferedImage car_sprite = null;
	  BufferedImage explosion_sprite = null;
	  int frame_index = 0;
	  boolean draw_the_car = false;
	  
	  switch (get_state()) 
	  {
	  case EXPLODING:
		  // manage hidden zones
		  get_clip(view_bounds);

		  explosion_sprite = m_explosion_view.get_frame(m_explosion_frame);
		  
		  if (m_explosion_frame < 4)
		  {
			  // still draw the car underneath
			  draw_the_car = true;
		  }
		  break;
	  case ALIVE:
	  case WINNER:
		  get_clip(view_bounds);
		  // manage hidden zones
		  
		  // car view update
		  		  
		  draw_the_car = true;
		 
		  
		  break;
	  }
	  
	  if (draw_the_car)
	  {
		  update_view();
		  MobileImageSet image_set = get_image_set();
		  ImageBounds ib = image_set.get_frame(m_current.angle);
		  
		  car_sprite = ib.image;
		  frame_index = ib.frame_index;
	  }
	  
	  if ((car_sprite != null) || (explosion_sprite != null))
	  {
		  Point2D centre_offset = m_body_view.get_centre_offset();
		  
		  
		  if (draw_the_car)
		  {

			  BufferedImage bis = m_shadow_view.get_image_set(m_view_index).get_frames()[frame_index].image;
			  double corrected_altitude = get_altitude();
			  
			  if ((m_entered_jump_zone) && (get_state() == ALIVE))
			  {
				  // the car is higher because the ground is one floor below
				  // -> shadow of the car must appear more shifted than it is
				  
				  corrected_altitude += JUMP_ALTITUDE;
			  }
			  
			  Point2D shadow_centre_offset = m_shadow_view.get_centre_offset();

			  int x = (int)
			  (Math.round(m_current.location.getX() - shadow_centre_offset.getX() + corrected_altitude));
			  int y = (int) (Math.round(m_current.location.getY() -
					  shadow_centre_offset.getY() + corrected_altitude));

			  draw_clipped_image(g,bis,x,y);
		  }
		  
		  int x = (int)
		  (Math.round(m_current.location.getX() - centre_offset.getX()));
		  int y = (int) (Math.round(m_current.location.getY() -
				  centre_offset.getY()));
		  
		  if (car_sprite != null)
		  {
			  draw_clipped_image(g,car_sprite,x,y);  
		  }
		  if (explosion_sprite != null)
		  {
			  draw_clipped_image(g,explosion_sprite,x,y);  
		  }
	  }
	  
  }
  protected void reset_zones()
  {
	  for (ControlZone cz : m_control_zone)
	  {
		  cz.reset();
	  }
  }
  protected MobileImageSet get_image_set()
  {
	  return m_body_view.get_image_set(m_view_index);
  }
  
  int get_nb_zones()
  {
	  /*int rval = 0;
	  for (ControlZone cz : m_control_zone)
	  {
		  if (cz.zone != null)
		  {
			  rval++;
		  }
	  }
	  
	  return rval;*/
	  return m_entered_zone_list.size() + m_initial_handicap;
  }
  
  void set_position(int position)
  {
	  m_position = position;
  }



/**
 * 
 * @return true if still alive
 */
private boolean reposition()
{
	boolean rval = (get_health() <= 0.0);
	
	if (rval)
	{
		// race over for this player
		set_state(DEAD);
	}
	else
	{
		// ATM resume points only on current route
		try
		{
			set_initial_location(m_resume_point,null);
		}
		catch (Exception e)
		{
			
		}
	  set_route(m_data.get_main_route());
	  
	  set_state(REPOSITION_COMPLETE);
	}
	
	return !rval;
}

private void resurrect()
{	  
	set_state(ALIVE); // TODO: check for winner

	m_jump.init(RESURRECT_DURATION,RESURRECT_RESUME,NITRO_ALTITUDE,0);
	m_within_jump_zone = false;
	m_entered_jump_zone = false;
	
	if (m_engine_sound != null)
	{
		m_engine_sound.resume();
	}

  }

protected void physical_car_behaviour(boolean allow_skid)
{
	double speed_x = m_predicted.speed.getX();
	double speed_y = m_predicted.speed.getY();
	double speed = m_predicted.get_speed();

	if (speed > 0.0) 
	{
		apply_speed();
		
		m_predicted.add_offset(m_bounce_offset.x, m_bounce_offset.y);
		
		double coeff = SPEED_LOSS_COEFFICIENT;
		long coeff_power = m_elapsed_time;

		if (!m_jump.active)
		{

			double old_speed = m_current.get_speed();

			// predict speed
			// compute angular difference between car movement and car position

			double speed_angle = Math.toDegrees(Math.atan2(speed_y, speed_x));
			double angle_diff = AngleUtils.angle_difference(m_predicted.angle,speed_angle);


			double abs_angle = Math.abs(angle_diff);
			
			if (abs_angle > m_power_slide_threshold)
			{
				//double skid_coeff = abs_angle * speed;
				
				if (!m_accelerating || is_bumped()) // accelerator not activated or just bumped 
				{

					// speed does not change, but tyres screech...
					// if angle delta > 20 & 2s between 2 screech sounds

					if (m_driver.is_human() &&
							((old_speed > 0.38) && (abs_angle > 20) && 
							((m_last_screech_sound + 2000) < m_absolute_elapsed_time)))
					{
						m_sfx_set.play(SfxSet.Sound.braking);
						m_last_screech_sound = m_absolute_elapsed_time;
					}
					
					m_predicted.skid = true;

					// reduce damp coefficient on road
					
					coeff = coeff - m_damp_factor * abs_angle;

				}
				else
				{
					long total_coeff = m_speed_mix_time;
					long mixing_coefficient_1 = Math.max(total_coeff - m_elapsed_time,0);
					long mixing_coefficient_2 = total_coeff - mixing_coefficient_1;
					
					// skid coeff OK: convert speed to match car position

					m_rotation.setToRotation(Math.toRadians(angle_diff));
					m_rotation.transform(m_predicted.speed, m_predicted.speed);
					speed_x = (mixing_coefficient_2 * m_predicted.speed.x + mixing_coefficient_1 * speed_x) / total_coeff;
					speed_y = (mixing_coefficient_2 * m_predicted.speed.y + mixing_coefficient_1 * speed_y) / total_coeff;										 
				}
			}

			if (m_view_index == CarBodyView.UP)
			{
				coeff_power *= SPEED_SLOPE_INCREASE;
			}
			else if (m_view_index == CarBodyView.DOWN)
			{
				coeff_power *= SPEED_SLOPE_DECREASE;
			}
		}
		else
		{
			//jump: less speed loss
			coeff = SPEED_LOSS_COEFFICIENT_AIR;
		}

		coeff = Math.pow(coeff, coeff_power);

		m_predicted.speed.setLocation(speed_x * coeff,
				speed_y * coeff);

		
	}
}

public boolean is_jumping_high_enough()
{
	return m_jump.active && m_jump.altitude > m_jump.max_altitude/2;
}
public boolean is_jumping()
{
	return m_jump.active;
}


private void update_view()
  {
     // get without recomputing
    Corner[] corners = m_current.get_corners();
    int nb_slope_corners = 0;
    Zone slope = null;

    // there's a hidden zone
    for (int i = 0; i < corners.length && nb_slope_corners < 2; i++) 
    {
      Corner c = corners[i];
      Zone z = c.zone;
      if (z.has_slopes())
      {
        slope = z;
        nb_slope_corners++;
      }
    }

    m_view_index = CarBodyView.STRAIGHT;

    if (slope != null)
    {
      double rsa = slope.get_rising_slope_angle();
      double angle_diff = AngleUtils.angle_difference(m_current.angle,rsa);
 
      double angle_diff_abs = Math.abs(angle_diff);

      if (angle_diff_abs < 45)
      {
        m_view_index = CarBodyView.UP;
      }
      else if (angle_diff_abs > 135)
      {
        m_view_index = CarBodyView.DOWN;
      }
      else if (angle_diff < 0)
      {
        m_view_index = CarBodyView.SIDE_1;
      }
      else
      {
        m_view_index = CarBodyView.SIDE_2;
      }


    }

  }

protected double get_brake_power(double speed) 
{
    return BRAKE_POWER * m_elapsed_time * speed;
}
public void was_collided(double hurt_speed)
{
  	m_bump_timer = BUMP_DURATION; 
  	
}
public void hurt(int damage)
{
  	((Parameters)get_current()).hurt(damage);
  	((Parameters)get_predicted()).hurt(damage);
}
public void handle_brake() 
{

	if (!m_jump.active)
	{
		m_accelerating = false;
		
		// accelerate using the position of the car

		double speed = m_current.get_speed();

		double bp = -get_brake_power(speed);

		double p_x = m_current.speed.getX() + bp * current_cos();
		double p_y = m_current.speed.getY() + bp * current_sin();

		// apply coeff with a power ratio: the more elapsed, the more coeff
		// is applied

		/*for (long elapsed = 0; elapsed < m_elapsed_time; elapsed+=5)
		{
			coeff *= coeff;
	}*/		

		/*double coeff = ro_settings.speed_loss_coefficient_air;
		p_x *= coeff;
		p_y *= coeff;
		m_predicted.speed.setLocation(p_x * coeff, p_y * coeff);
*/
		m_predicted.speed.setLocation(p_x, p_y);
		
		double predicted_speed = m_predicted.get_speed();

		// limit speed infinite decrease

		if (predicted_speed < 1e-6) 
		{
			m_predicted.speed.setLocation(0, 0);
		}

		// forbid to go backwards

		else if (speed < predicted_speed) 
		{
			m_predicted.speed.setLocation(m_current.speed.getX(),
					m_current.speed.getY());
		}
	}
}
  public class Parameters extends Mobile.GenericParameters
  {
	  
	  public Parameters()
	  {
		  super(6); // six points instead of four for a better accuracy
	  }
	  
    public Corner[] retrieve_corners()
    {
    	super.retrieve_corners();
     
     // add extra points on the side of the car
     
        m_corners[4].location.setLocation
        ((m_corners[0].location.x+m_corners[1].location.x)/2,
       		 (2*m_corners[0].location.y+m_corners[1].location.y)/3);
        m_corners[5].location.setLocation
        ((m_corners[3].location.x+m_corners[2].location.x)/2,
       		 (2*m_corners[3].location.y+m_corners[2].location.y)/3);
        
     

     return m_corners;
   }

    private final double BOUNCE_ANGLE_SAFETY = ro_settings.bounce_angle_safety;
  
    public void hurt(int damage)
    {

        m_equipment.add_health(-damage);
   	
        if (get_health() <= 0)
        {
        	killed_by(null,0);
        }
    }
    
     
    /**
     * bounce against a wall with damage
     * @param damp_factor [0 .. 1]
     */

    public void bounce(double df, double boundary_angle) 
    {
    	// damage is done once in a while
    	//hurt(ro_settings.carvwall_damage);

    	double damp_factor = df;
    	
    	if (m_jump.active)
    	{
    		damp_factor /= 2;
    	}
    	
        // speed angle
    	double speed_angle = Math.toDegrees(Math.atan2(speed.getY(),speed.getX()));
  
    	double rotangle = AngleUtils.angle_difference(boundary_angle, speed_angle);
           	
       	rotangle = AngleUtils.normalize_m180_180(rotangle * 2);
       	
    	if (Math.abs(rotangle) < BOUNCE_ANGLE_SAFETY)
    	{
    		rotangle = BOUNCE_ANGLE_SAFETY * (rotangle > 0 ? 1 : -1);
    		
    	} else if (Math.abs(rotangle) > 180-BOUNCE_ANGLE_SAFETY)
    	{
    		rotangle = 180-BOUNCE_ANGLE_SAFETY * (rotangle > 0 ? 1 : -1);
    	}
		//System.out.println("bounce 2: "+boundary_angle+" "+rotangle);

		m_work_rotation.setToRotation(Math.toRadians(rotangle));
    	m_work_rotation.transform(speed,speed);
    	speed.x *= damp_factor;
    	speed.y *= damp_factor;
    	double new_speed = get_speed();

    	if (new_speed < 1e-5)
    	{
    		// car rotation without speed
    		new_speed = 1e-5;
    		set_speed(new_speed,rotangle+speed_angle);

    	}
    	if (new_speed < MIN_SPEED)
    	{
    		// not fast enough: risk to be stuck
    		// and to explode without reason
    		speed.x *= MIN_SPEED/new_speed;
    		speed.y *= MIN_SPEED/new_speed;
    	}
    	// avoid screech & bounce
    	
    	m_last_screech_sound = m_absolute_elapsed_time;
    	
     }
    


    public void bounce(double damp_factor) 
    {
    	double df_bounded = damp_factor;
    	
    	// damage is done once in a while
    	//hurt(ro_settings.carvwall_damage);
    	
    	double current_speed = get_speed();
    	
    	// TEMP avoid that speed gets too high
    	
    	if (current_speed * df_bounded > m_max_speed)
    	{
    		df_bounded = m_max_speed / current_speed;
    	}
    	
    	speed.setLocation( -speed.getX() * df_bounded,
    			-speed.getY() * df_bounded);
    	
    	// avoid screech & bounce
    	
    	m_last_screech_sound = m_absolute_elapsed_time;
    	
        
    }

  }
}


