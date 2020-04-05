package supercars3.game.gates;

import supercars3.base.*;
import supercars3.game.Corner;
import supercars3.game.GameOptions;
import supercars3.game.cars.Car;
import supercars3.game.cars.CarSet;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.Vector;

import com.golden.gamedev.engine.*;

public class GateSet
{	
	Vector<GateCouple> m_gate_couple = new Vector<GateCouple>();
	protected final GameOptions.ReadOnly ro_settings = GameOptions.instance().read_only;
	
	public void init(CircuitData data,BaseLoader bsLoader)
	{		
		GateFactory gf = new GateFactory(bsLoader);
		 
		// scan all zones and find gates
		for (Zone z : data.get_zone_list())
		{
			if (z.get_visible_type() == Zone.ZoneType.GATES)
			{
				m_gate_couple.add(gf.create(z));
			}
		}
	}
	
	public boolean empty()
	{
		return m_gate_couple.isEmpty();
	}
	
	public GateCouple contains(Point2D.Double p)
	{
		java.util.Iterator<GateCouple> it = m_gate_couple.iterator();
		GateCouple rval = null;
		
		while(it.hasNext() && rval == null)
		{
			GateCouple gc = it.next();
			
			if (gc.contains(p))
			{
				rval = gc;
			}
		}
		
		return rval;
				
	}
	
	public void update(long elapsed_time,CarSet cs)
	{
		if (m_gate_couple.size() > 0)
		{
			for (GateCouple gc : m_gate_couple)
			{
				gc.update(elapsed_time);
			}		

			if (m_gate_couple.firstElement().is_closing())
			{
				destroy_squeezed_cars(cs);
			}
		}
		 		 
	}
	 public void render(Graphics2D g,Rectangle view_bounds)
	 {
		 for (GateCouple gc : m_gate_couple)
		 {
			 gc.render(g,view_bounds);
		 }
	 }
	 private void destroy_squeezed_cars(CarSet cs)
	  {	  	  
		  int nbc = cs.size();
		  
		   for (int j = 0; j < nbc; j++) 
		    {
			   Car c = cs.get_item(j);
			   
			   if ((c != null) && c.is_alive())
			   {
				   Corner [] corners = c.get_current().retrieve_corners();
				   boolean found = false;
				   for (int i = 0; i < corners.length && !found; i++)
				   {
					   found = (contains(corners[i].location) != null);
				   }
				   if (found)
				   {
					   c.die(false,ro_settings.gate_damage);
				   }
			   }
		    
		    }
	  }	 
}
