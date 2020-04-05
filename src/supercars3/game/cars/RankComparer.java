package supercars3.game.cars;

public class RankComparer extends CarComparer
{
	  protected boolean is_before(Car assumed_before, Car assumed_after)
	  {
		  boolean rval = false;
		  
		  int assumed_before_state = assumed_before.get_state();
		  int assumed_after_state = assumed_after.get_state();
		  
		  boolean current_wins = assumed_before_state == Car.WINNER;
		  boolean other_wins = assumed_after_state == Car.WINNER;
		  
		  if (current_wins && !other_wins)
		  {
			  rval = true; // current wins
		  }
		  else if (other_wins && !current_wins)
		  {
			  rval = false; // other wins
		  }
		  else
		  {
			  boolean current_dead = assumed_before_state == Car.DEAD;
			  boolean other_dead = assumed_after_state == Car.DEAD;
			  
			  if (current_dead && !other_dead)
			  {
				  rval = false; // other wins
			  }
			  else if (other_dead && !current_dead)
			  {
				  rval = true; // before wins
			  }
			  else
			  {
				  
				  // none is a winner or both are winners,
				  // or none is dead or both are dead
				  
				  rval = (assumed_after.get_laps_to_go() > assumed_before.get_laps_to_go());
				  
				  if (!rval)
				  {
					  // current is not before other in terms of laps
					  
					  if (assumed_after.get_laps_to_go() == assumed_before.get_laps_to_go())
					  {
						  rval = assumed_after.get_nb_checkpoints() < assumed_before.get_nb_checkpoints();
						  
						  if (!rval)
						  {
							  if (assumed_after.get_nb_checkpoints() == assumed_before.get_nb_checkpoints())
							  {
								  int other_zones = assumed_after.get_nb_zones();
								  int before_zones = assumed_before.get_nb_zones();
								  
								  // same laps, same checkpoints, compare nb zones
								  
								  rval = (other_zones < before_zones);
								  
								  if (!rval)
								  {
									  if (other_zones == before_zones)
									  {
										  // same nb zones: keep old positions (for stability)
										  
										  rval = assumed_after.get_position() > assumed_before.get_position();
									  }
								  }
							  }
						  }
					  }
				  }
			  }
		  }
		  
		  return rval;
	  }  
}