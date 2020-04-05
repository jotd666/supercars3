package supercars3.game.cars;

public class RenderComparer extends CarComparer
{

	  protected boolean is_before(Car c1, Car other)
	  {
		  boolean rval = false;
		  
		  if (c1.is_alive())
		  {
			  // highest cars must be located at the end
			  // of the list to be drawn in the end
			  
			  rval = other.is_above(c1);  
		  }
		  
		  return rval;
	  }
}

