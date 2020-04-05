package supercars3.game;

import java.awt.*;
import java.util.*;

import supercars3.base.*;
import supercars3.game.cars.*;
import supercars3.game.gates.*;
import supercars3.game.trains.*;
import supercars3.game.weapons.Weapon;

/**
 * <p>Titre : </p>
 * <p>Description : </p>
 * <p>Copyright : Copyright (c) 2005</p>
 * <p>Société : </p>
 * @author non attribuable
 * @version 1.0
 */

public class CircuitChecker 
{
	private static final double BOUNCE_FACTOR = GameOptions.instance().read_only.bounce_factor;
	protected final GameOptions.ReadOnly ro_settings = GameOptions.instance().read_only;
	
	private static final int MIN_TIME_BETWEEN_WALL_BOUNCES = 500;
	private CircuitData m_data;
    private CarSet m_cars;
    private GateSet m_gate_set;
    
  private class CurrentPredictCorners
  {
	  Corner [] current; 
	  Corner [] predict; 
  }
  
  private CurrentPredictCorners m_work_corners_set = new CurrentPredictCorners();
  
/**
   * @return true if at least one of the corner zone has changed
   */
  private enum ChangeStatus {NO_ZONE_CHANGE,ZONE_CHANGE,OUT_OF_BOUNDS }

  public boolean locate_predict_corners(Mobile c,int recursion_level)
  {
    boolean rval = false;

    // current containing zones have been validated
    // now find the containing zones of the predicted position of the car
    // with the constraint that they must be neighbours of the current containing
    // zones
    
    m_work_corners_set.current = c.get_current().retrieve_corners();
    m_work_corners_set.predict = c.get_predicted().retrieve_corners();

     for (int i = 0; i < m_work_corners_set.current.length; i++)
     {
       Corner predict = m_work_corners_set.predict[i];
       Corner current = m_work_corners_set.current[i];

       update_mobile_corner(predict,current.zone,recursion_level);

       // changed is set if predict zone
       // is different from current zone

       ChangeStatus change_status = ChangeStatus.NO_ZONE_CHANGE;
       
       if (predict.zone != current.zone)
       {
         if (predict.zone == null)
         {
           change_status = ChangeStatus.ZONE_CHANGE;
         }
         else
         {
           change_status = ChangeStatus.OUT_OF_BOUNDS;
         }
       }

       if (change_status != ChangeStatus.NO_ZONE_CHANGE)
       {
         // at least one corner zone has changed

         rval = true;
       }
     }

     return rval;
  }
  
  
 
  
  /**
   * called at car external positionning
   * there's only 1 zone set for the car (the zone of the
   * resume or start control point of the route)
   *
   * this method scans the neighbours of the zone and
   * checks which ones contain the car too
   *
   * results are updated into the car containing zone list
   *
   * @param car
   */

  private void set_initial_locations() throws Exception
  {
    for (int ci = 0; ci < m_cars.size(); ci++) 
    {
      Car car = m_cars.get_item(ci);
      if (car != null) 
      {
 
        PointDirection pd = 
        	m_data.get_main_route().get_car_start_point(car.get_position());

        // route control points are designed to have only 1 zone (checked at circuit load)
 
        car.set_circuit_checker(this);
        car.set_initial_location(pd,m_data.get_main_route().resume_point_iterator());
 
      }
    }
    
  }

  
  public long extrapolate_winning_time(long elapsed_time)
  {
	  m_cars.compute_ranks();
	  
	  Car winner = m_cars.get_item(0);
	  
	  int nb_laps = m_data.get_nb_laps();
	  int nb_checkpoints = m_data.get_nb_checkpoints();
	  
	  // add 10% to be sure not to break a record
	  
	  int nb_total_units = (int)(nb_laps * nb_checkpoints * 1.1);
	  
	  int nb_units = ((nb_laps - 1 - winner.get_laps_to_go()) * nb_checkpoints) + winner.get_nb_checkpoints() + 1;

	  // finishing time is the ratio
	  
	  long rval = (elapsed_time * nb_total_units) / nb_units;

	  return rval;
  }
  
  /**
   * corner lookup with controlled recursion
   */

  public void update_mobile_corner(Corner corner,
                                Zone start_zone,
                                int neighbour_depth)
  {
	  corner.zone = null;

	  if (start_zone.contains(corner.location))
	  {
		  // no need to look further
		  corner.zone = start_zone;
	  }
	  else if (neighbour_depth > 0)
	  {
		  // lookup in the neighbours
		  HashSet<Zone> nz = start_zone.get_neighbours();

		  Iterator<Zone> it = nz.iterator();

		  while (it.hasNext() && corner.zone == null)
		  {
			  Zone n = it.next();

			  update_mobile_corner(corner,n,neighbour_depth-1);
		  }
	  }

  }


  public CircuitChecker(CircuitData data,CarSet cars,GateSet gates,TrainSet trains) throws Exception
  {
    m_data = data;
    m_cars = cars;
    m_gate_set = gates;
    //m_train_set = trains;
    
    // copy zone data into zones needed for check purposes

    Collection<Zone> zl = m_data.get_zone_list();

    for (int i = 0; i < m_data.get_zone_list_size();i++)
    {
    	Zone z = m_data.get_zone(i);
    	if ( (z.get_visible_type() != Zone.ZoneType.TOP_PRIORITY))
    	{
    		if (!z.compute_neighbour_zones(zl))
    		{
    			throw new Exception("Zone "+z.get_name()+" has no neighbours");
    		}
    	}
    }
    
    set_initial_locations();

  }

// for debug
  public void render(Graphics2D g)
  {
    /*Iterator it = m_checked_zone_list.iterator();
    while (it.hasNext())
    {
      CheckedZone cz = (CheckedZone)it.next();
      cz.paint(g);
    }*/

  }
  
  private enum BounceType { NONE, WALL, GATE }
  
  /**
 * validate move for a car
 * @param c car
 * @return true if car "wins" (still we have to tell which one wins
 * in case of a conflicts)
 */
  
public boolean accept_move(Car c) 
{
	boolean solved = true;
	
	if (c.is_alive())
	{
		boolean gates_present = !m_gate_set.empty();
		boolean play_bounce_sound = false;
		BounceType bt = BounceType.NONE;

		// find where are the bounds of the predicted position of the car
		// and check for changes


		boolean zone_change = locate_predict_corners(c,2);

		if (zone_change || gates_present)
		{
			// zone change or gates are present: some extra checks need to be performed
			// and for cars, there are more corners, so we need to
			// retrieve the corners specifically

			Corner [] predict_corners = c.get_predicted().retrieve_corners();
			Corner [] current_corners = c.get_current().retrieve_corners();


			for (int i = 0; i < predict_corners.length && bt == BounceType.NONE; i++)
			{
				Corner corner = predict_corners[i];
				Corner current_corner = current_corners[i];
				if (current_corner.zone == null)
				{
					// try to solve the case when car is stuck
					// even if I tried hard for that not to happen
					// it still happens in some rare cases
					bt = BounceType.WALL;
					c.die(false, 0);
					break;
				}
				double boundary_angle = 0.0;
				boolean boundary_angle_defined = false;

				Boundary b = current_corner.zone.get_intersected_boundary
						(current_corner.location,corner.location);


				if (corner.zone == null)
				{
					bt = BounceType.WALL;
					c.on_wall_bounce();
				}
				else
				{
					if ((zone_change) && (corner.zone != current_corner.zone))
					{
						// note down the zone change

						c.zone_entered(corner.zone,b);

						c.zone_exited(current_corner.zone,b);
					}

					if (gates_present)
					{
						// check the gates

						GateCouple gc = m_gate_set.contains(corner.location);

						if (gc != null)
						{
							bt = BounceType.GATE;
							boundary_angle_defined = true; // only horizontal bounce supported right now

							c.on_gate_bounce();

						}
					}
				}

				if (bt != BounceType.NONE)
				{
					// a corner has no zone: out of bounds

					// out of bounds

					play_bounce_sound = true;

					c.get_predicted().angle = c.get_current().angle;

					if (c.get_predicted().get_speed() < 1e-4)
					{
						// car was rotating on itself
						c.get_predicted().speed.setLocation(0,0);
					}
					else
					{												
						Car.Parameters predicted = (Car.Parameters)c.get_predicted();
						Car.Parameters current = (Car.Parameters)c.get_current();

						if (b != null) 
						{
							boundary_angle = b.get_angle_x();
							boundary_angle_defined = true;
						}

						// car is drawn back from where it came from, symmetricaly, with some
						// extra help
						double delta_x = 3 * (predicted.location.x - current.location.x);
						double delta_y = 3 * (predicted.location.y - current.location.y);


						// shift other current car position
						predicted.location.x -= delta_x;
						predicted.location.y -= delta_y;

						// update corner positions:not necessary
						//predicted.retrieve_corners();

						
						if (boundary_angle_defined)
						{
							predicted.bounce(BOUNCE_FACTOR, boundary_angle);
						}
						else
						{
							// no boundary intersects: strange, but still do something
							predicted.bounce(BOUNCE_FACTOR);
						}

						// now that we bounced, check points again with a
						// recursion of 2
						locate_predict_corners(c,2);

						for (int k = 0; k < predict_corners.length; k++)
						{
							if (predict_corners[k].zone == null)
							{
								solved=false;
								break;
							}

						}


						if (solved)
						{
							c.solved_iteration();
						}
						else
						{
							c.unsolved_iteration();
						}
						


					}

				}
			}
		}
		

		if (bt == BounceType.NONE)
		{
			// no wall bounce: check collision between c and other cars
			// (c just did the move)
			m_cars.check_collisions(c);
		}


		if (play_bounce_sound)
		{

			if (c.get_last_bounce_time()+MIN_TIME_BETWEEN_WALL_BOUNCES < c.get_last_update_time())
			{
				if (c.get_driver().is_human())
				{
					// don't play too much bouncing sound (it's horrible!)
					m_cars.get_sfx_set().play(SfxSet.Sound.bounce_wall);
				}
				// also reduce damage frequency, it's unfair
				c.hurt(ro_settings.carvwall_damage);
				c.set_last_bounce_time(c.get_last_update_time());	
			}

		}

		
	}
	// move is accepted: update current data with predicted
	if (solved)
	{
		c.get_current().copy_from(c.get_predicted());
	}
	return solved;
}

/**
 * validate move for a weapon
 * @param c weapon
 * @return true if move is valid
 */
public boolean accept_move(Weapon c) 
{
	
  // find where are the bounds of the predicted position of the car
  // and check for changes
	
	if (locate_predict_corners(c,2))
	{
		// zone change: some extra checks need to be performed
		// we can use the work variable updated by locate_predict_corners
		
		Corner [] predict_corners = m_work_corners_set.predict;
		Corner [] current_corners = m_work_corners_set.current;
				
		for (int i = 0; i < predict_corners.length; i++)
		{
			Corner corner = predict_corners[i];
			Corner current_corner = current_corners[i];

			if (corner.zone != null)
			{
				if (corner.zone != current_corner.zone)
				{
					Boundary b = current_corner.zone.get_intersected_boundary
					(current_corner.location,corner.location);

					// note down the zone change
					
					c.zone_entered(corner.zone,b);
					
					c.zone_exited(current_corner.zone,b);
				}
			}			
			else
			{
				// out of bounds: send out of bounds signal to the weapon
				c.out_of_bounds();
			}
		}
		
	}

	if (c.get_state() == Weapon.ALIVE)
	{
		c.check_collisions(m_cars);
	}
	
  // check if the car is fully contained in the current containing zones
  // (most frequent case)

  c.get_current().copy_from(c.get_predicted());

  return true; // false: explosion*/
}


/**
 * validate move for a weapon
 * @param c car
 * @return true if move is valid
 */
public boolean accept_move(Train t) 
{
	Wagon [] ws = t.get_wagons();
	int nb_wagons = t.get_nb_wagons();
	
	for (int i = 0; i < nb_wagons; i++)
	{
		Wagon w = ws[i];
		
		// first check collisions with cars
		w.check_collisions(m_cars);
		
		// find where are the bounds of the predicted position of the car
		// and check for changes (big recursion level, because initial zone
		// is the train engine zone)
		// it still is not enough to locate predict corners precisely, hence the
		// non-collision bug
		
		if (locate_predict_corners(w,4))
		{
			// zone change: some extra checks need to be performed
			// we can use the work variable updated by locate_predict_corners

			Corner [] predict_corners = m_work_corners_set.predict;
			Corner [] current_corners = m_work_corners_set.current;

			for (int j = 0; j < predict_corners.length; j++)
			{
				Corner corner = predict_corners[j];
				Corner current_corner = current_corners[j];

				if (corner.zone != null)
				{
					if (corner.zone != current_corner.zone)
					{
						Boundary b = current_corner.zone.get_intersected_boundary
						(current_corner.location,corner.location);

						// note down the zone change

						w.zone_entered(corner.zone,b);

						w.zone_exited(current_corner.zone,b);
					}
				}			
			}

		}


		w.get_current().copy_from(w.get_predicted());
	}
	
  return true; // false: explosion*/
}



}