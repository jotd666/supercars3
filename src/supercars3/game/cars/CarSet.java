package supercars3.game.cars;

import java.awt.*;

import java.util.*;

import supercars3.base.*;
import supercars3.game.players.*;

import supercars3.game.*;
import supercars3.game.Mobile.GenericParameters;
import supercars3.sys.RandomList;

/**
 * <p>Titre : Supercars 3</p>
 * <p>Description : </p>
 * <p>Copyright : Copyright (c) 2005</p>
 * <p>Société : </p>
 * @author JOTD
 * @version 1.0
 */

public class CarSet
{
	private Car [] m_items;
	private final GameOptions.ReadOnly ro_settings = GameOptions.instance().read_only;
	private static final RenderComparer RENDER_COMPARER = new RenderComparer();
	private static final RankComparer RANK_COMPARER = new RankComparer();
	private int m_nb_players = 0;
	private SfxSet m_sfx_set;
	
  public CarSet(int count, SfxSet sfx_set)
  {
    m_items = new Car[count];
    m_sfx_set = sfx_set;
  }
  public SfxSet get_sfx_set()
  {
	  return m_sfx_set;
  }
  
  public ScoreEntry [] get_score_entries()
  {
	  ScoreEntry [] rval = new ScoreEntry[size()];
	  
	  for (int i = 0; i < m_items.length; i++)
	  {
		  Car c = m_items[i];
		  Driver driver = c.get_driver();
		  rval[i] = new ScoreEntry(driver.get_name(),c.get_points(),driver.is_human());				  
	  }
	  
	  Arrays.sort(rval);
	  
	  return rval;
  }
  public int size()
  {
    return m_items.length;
  }
  
 
  public Car get_item(int i)
  {
    return m_items[i];
  }
  public void set_item(int i, Car c)
  {
    m_items[i] = c;
    
    if (c.get_driver().is_human())
    {
    	m_nb_players++;
    }
  }
  
  private class EngPos
  {
	  double engine;
	  int position;
	  EngPos(double e, int p)
	  {
		  engine = e;
		  position = p;
	  }
	  
  }
  private void shuffle_cpu_cars()
  {
	  Vector<EngPos> cpu_cars = new Vector<EngPos>();

	  for (Car c : m_items)
	  {
		 
		  if (!c.get_driver().is_human())
		  {
			  cpu_cars.add(new EngPos(c.get_equipment().get_engine(),c.get_position()));
		  }
	  }

	  RandomList<EngPos> rlc = new RandomList<EngPos>(cpu_cars);

	  Iterator<EngPos> it = rlc.get_contents().iterator();

	  for (Car c : m_items)
	  {
		  if (!c.get_driver().is_human())
		  {
			  EngPos ep = it.next();
			  c.set_position(ep.position);
			  c.get_equipment().set_engine(ep.engine);
		  }
	  }
  }
  
  public void race_start_pass_1(CircuitData data) 
  {
	  int nb_laps = data.get_nb_laps();
	  int nb_checkpoints = data.get_nb_checkpoints();
	  int missed_checkpoint_tolerance = data.get_missed_checkpoint_tolerance();
	  
	  shuffle_cpu_cars();
	  
	  
	  for (int i = 0; i < m_items.length; i++) 
	  {
		  Car c = m_items[i];
		  if (c != null) 
		  {
			  c.set_sfx_set(m_sfx_set);
			  c.race_start(nb_laps,data,nb_checkpoints,
					  missed_checkpoint_tolerance,data.get_main_route());
		  }
	  }
	    
  }
  
  public void race_start_pass_2(CircuitChecker cc, OpponentProperties opp)
  { 
  
	   // for car ranked n, mark zones of cars behind it as exited
	    // so initial rankings are correct
	  for (int i = 0; i < m_items.length; i++) 
	  {
		  Car c = m_items[i];
		  if (c != null) 
		  {
			  c.set_circuit_checker(cc);
				
			  for (int j = i+1; j < m_items.length; j++) 
			  {
				  Car other = m_items[j];
				  PointDirection pzd = other.get_resume_point();
				  c.zone_exited(pzd.get_point().get_explicit_zone(),null);
			  }
			  
			  if (!c.get_driver().is_human())
			  {
				  // set equipment to computer cars so they are able
				  // to defend themselves (poor devils)
				  
				  Equipment e = c.get_equipment();
				  
				  e.mounted_front = Equipment.Item.values()[opp.car_front_weapon];
				  e.mounted_rear = Equipment.Item.values()[opp.car_rear_weapon];
				  				  
				  e.get_accessory(e.mounted_front).set_count(opp.nb_front_weapons);
				  e.get_accessory(e.mounted_rear).set_count(opp.nb_rear_weapons);
				  
				  // reset energy for computer cars
				  
				  e.reset_health();
			  }
			  
		  }
	  }
  }
  
  public void init_sounds()
  {
	  for (int i = 0; i < m_items.length; i++) 
	  {
		  Car c = m_items[i];
		  if (c != null) 
		  {
			  c.init_engine_sound();
		  }
	  }
  }
  public void stop_sounds()
  {
	  for (int i = 0; i < m_items.length; i++) 
	  {
		  Car c = m_items[i];
		  if (c != null) 
		  {
			  c.stop_engine_sound();
			  
		  }
	  }

  }
  public void end_sounds()
  {
	  for (int i = 0; i < m_items.length; i++) 
	  {
		  Car c = m_items[i];
		  if (c != null) 
		  {
			  c.end_engine_sound();
		  }
	  }
  }
  
  /**
   * 
   * @param c
   * @return true if another car shares a zone with the current car
   */
  public boolean shared_zone(Car c)
  {
	  boolean rval = false;
	  GenericParameters current = c.get_current();
	  
	  for (int i = 0; i < m_items.length && !rval; i++) 
	  {
		  Car other = m_items[i];
		  if ((other != c) && other.is_alive())
		  {
			  rval = current.shared_zone(other.get_current());
		  }
	  }
	  
	  return rval;
  }
  public void compute_car_speeds(double circuit_max_cpu_engine, double initial_human_engine, boolean init)
  {
	  int total_nb_cars = m_items.length;

	  double cpu_engine_delta = 1.0 / total_nb_cars;
	  double smaller_cpu_engine = circuit_max_cpu_engine - (cpu_engine_delta * total_nb_cars);

	  for (int i = 0;i < total_nb_cars;i++)
	  {
		  Car c = get_item(i);
		  if (!c.get_driver().is_human())
		  {    		
			  double initial_cpu_engine = smaller_cpu_engine;
			  if (i >= (total_nb_cars/2))
			  {
				  // only half first cars have differentiated speeds
				  initial_cpu_engine += cpu_engine_delta * (i - (total_nb_cars/2));
			  }
			  c.get_equipment().set_engine(Math.max(initial_cpu_engine,-1.0));
		  }
		  else
		  {
			  // human engine (do not touch if cheatmode turbocharge is on)

			  if (init && !c.get_equipment().is_turbocharged())
			  {
				  c.get_equipment().set_engine(initial_human_engine);
			  }
		  }
	  }
  }
  
  /* car to car collisions */
  
  public boolean check_collisions(Car c)
  {
	  GenericParameters predicted = c.get_predicted();
	  boolean collision_occured = false;
	  
	  for (int i = 0; i < m_items.length && !collision_occured; i++) 
	  {
		  Car other = m_items[i];
		  if ((other != null) && 
				  (c != other) &&
				  (other.same_plane(c)) && 
				  c.is_alive() && 
				  other.is_alive())
		  {
			  boolean intersected = false;
			  boolean bounce_car = false;

			  intersected = other.get_current().get_intersection(predicted);

			  if (intersected)
			  {
				  if (other.is_above(c))
				  {
					  c.killed_by(other,ro_settings.carexpl_damage);
					  break; // was intersected=false
				  }
				  else if (c.is_above(other))
				  {
					  other.killed_by(c,ro_settings.carexpl_damage);
					  break;
				  }
				  else
				  {
					  bounce_car = true;


					  if ((other.rammed_by(c)) || c.rammed_by(other))
					  {
						  // car was just rammed: end loop
						  bounce_car = false;
						  intersected = false;
					  }
				  }
			  }

			  if (bounce_car)
			  {
				  double predicted_speed = predicted.get_speed();
				  
				  GenericParameters current = c.get_current();
				  
				  c.on_car_bounce(other);
				  other.on_car_bounce(c);
				  
				  if (predicted_speed < 0.01)
				  {
					  predicted.angle = current.angle;
				  }

				  collision_occured = true;

				  // car looses most of its speed, in the same direction
				  predicted.set_speed(predicted_speed * ro_settings.car_collision_self_damp);
				  
				  // car is drawn back from where it came from, symmetricaly
				  double delta_x = 2 * (predicted.location.x - current.location.x);
				  double delta_y = 2 * (predicted.location.y - current.location.y);
				  
				 
				  // shift other current car position
				  predicted.location.x -= delta_x;
				  predicted.location.y -= delta_y;
				  
				  // update corner positions
				  predicted.retrieve_corners();
				  
				  GenericParameters other_current = other.get_current();
				  				  
				  other_current.add_speed(current.speed,ro_settings.car_collision_speed_transfer);
				  
				  // try to avoid the jam but progressively, by iterations
				  
				  for (int j = 0; j < ro_settings.nb_jam_avoid_attemps; j++)
				  {
					  // shift other current car position
					  other.add_location_offset(delta_x,delta_y);
					  // until cars not colliding anymore
					  if (!predicted.get_intersection(other_current,other.get_location_offset()))
					  {
						  other.add_location_offset(delta_x,delta_y);
						  break;
					  }
				  }
				  
				  if (current.get_speed()<ro_settings.damage_speed_threshold && 
						  other_current.get_speed()<ro_settings.damage_speed_threshold)
				  {
					  // do nothing to avoid a lot of damage when cars are interlocked
					  // for computer cars, TODO a counter to kill them after a given lockup time
				  }
				  else
				  {
					  c.hurt(ro_settings.carvcar_damage);

					  // set a counter: car will be harder to control during a while

					  other.was_collided(current.get_speed());

					  if (c.get_driver().is_human() || other.get_driver().is_human())
					  {
						  m_sfx_set.play(SfxSet.Sound.car_collide);
					  }
				  }
			  }
		  }	  
	  }
	  return collision_occured;
  }
  
  public void compute_ranks()
  {
	  // bubble sort
	  
	  Arrays.sort(m_items,RANK_COMPARER);
	  
	  // compute positions
	  for (int i = 0; i < m_items.length; i++) 
	  {	  		  
		  m_items[i].set_position(i+1);
	  }
  }
  
  public static final int NO_EVENT = 0;
  public static final int WINNER = 1;
  public static final int GAME_OVER = 2;
  
  public int update(long elapsed_time, long absolute_elapsed_time, CircuitChecker cc) 
  {
	  int rval = NO_EVENT;
	  int nb_dead = 0;
	  
	  // first, reset all current position offsets
	  
	  for (Car c : m_items)
	  {
		  c.reset_location_offset();
	  }
	  
	  // compute collisions
	  
	  for (int i = 0; i < m_items.length && rval != WINNER; i++) 
	  {
		  Car c = m_items[i];
		  if (c != null) 
		  {
			  c.update(elapsed_time,absolute_elapsed_time);
			  c.drive();
			  c.predict();
			  c.accept_move(cc);

			  switch (c.get_state())
			  {
			  case Car.WINNER:
				  rval = WINNER;
				  break;
			  case Car.RESURRECT:
				  // car is ready to reappear: check that no other car
				  // is in the vincinity
				  if (shared_zone(c))
				  {
					  c.delay_resurrect();
				  }
				  break;
			  case Car.DEAD:
				  if (c.get_driver().is_human())
				  {
					  nb_dead++;
					  if (nb_dead == m_nb_players)
					  {
						  rval = GAME_OVER;
					  }
				  }
				  break;
			  }
		  }
	  }
	  
	  return rval;
  }
  
  public void render(Graphics2D g, Rectangle view_bounds, boolean jumping) 
  {
	  Arrays.sort(m_items,RENDER_COMPARER);

    for (Car c : m_items) 
    {
      if ((c != null) && (c.is_jumping() == jumping))
      {
        c.render(g,view_bounds);
      }
    }
    
  }
  
 
}