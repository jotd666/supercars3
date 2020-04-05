package supercars3.game.cars;

import java.util.Comparator;

public abstract class CarComparer implements Comparator<Car>
{
	  protected abstract boolean is_before(Car c1, Car other);
	  public int compare(Car c1, Car other)
	  {
		  int rval = 0;
		  		  
		  if (other != c1)
		  {
			  rval = is_before(c1,other) ? -1 : 1;
		  }
		  
		  return rval;
	  }
}