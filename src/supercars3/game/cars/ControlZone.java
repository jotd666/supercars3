package supercars3.game.cars;

import supercars3.base.Boundary;
import supercars3.base.Zone;

class ControlZone
{
	  // flag appeared must be set when car is teleported
	  // after a crash or after a race start
	  
	  void reset()
	  {
		  zone = null;
		  entered = null;
		  exited = null;
		  set_appeared(false);
	  }
	  
	  void entered(Zone z, Boundary by)
	  {
		  zone = z;
		  entered = by;
		  set_appeared(false);
	  }
	  // returns true if exit by entry
	  
	  boolean is_appeared()
	  {
		  return appeared;
	  }
	  boolean exited(Zone z, Boundary by)
	  {
		  zone = z;
		  exited = by;
		  set_appeared(false);
		  return exit_by_entry();
	  }
	  private boolean exit_by_entry()
	  {
		  return (entered == null) || ((exited != null) && (entered.equalsTo(exited)));
	  }
	  
	  void set_appeared(boolean appeared)
	  {
		  this.appeared = appeared;
	  }
	  Zone zone;
	  Boundary entered;
	  Boundary exited;
	  private boolean appeared;
}