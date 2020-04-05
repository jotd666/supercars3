package supercars3.game.trains;

import supercars3.base.*;
import supercars3.game.*;
import supercars3.game.cars.CarSet;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.Collection;
import java.util.Vector;
import java.util.LinkedList;
import com.golden.gamedev.engine.*;

public class TrainSet
{	
	Vector<Train> m_train_set = new Vector<Train>();
	
	public void init(CircuitData data,BaseLoader bsLoader,CircuitChecker cc)
	{		
		TrainFactory tf = new TrainFactory(bsLoader,data.get_opponent_properties());
		 
		Collection<Zone> start_zones = new LinkedList<Zone>();
		Collection<Zone> end_zones = new LinkedList<Zone>();
		
		// scan all zones and find train start/end
		for (Zone z : data.get_zone_list())
		{
			if (z.get_visible_type() == Zone.ZoneType.TRAIN_START)
			{
				start_zones.add(z);
			}
			else if (z.get_visible_type() == Zone.ZoneType.TRAIN_END)
			{
				end_zones.add(z);
			}
			
		}
		int start_size;
		int old_start_size;
		do
		{
			Zone start_zone=null, end_zone=null;

			for (Zone z1 : start_zones)
			{
				ControlPoint p1 = z1.get_centre();
				
				// lookup for relevant end zone
				for (Zone z2 : end_zones)
				{
					ControlPoint p2 = z2.get_centre();
					Segment s = new Segment(p1,p2);
					
					if (s.is_rather_horizontal(5.0) || s.is_rather_vertical(5.0))
					{
						// associate
						start_zone=z1;
						end_zone=z2;
						break;
					}
				}
				if (start_zone != null)
				{
					break;
				}
			}
			old_start_size = start_zones.size();
			if (start_zone != null)
			{
	
				m_train_set.add(tf.create(start_zone,end_zone,cc));

				start_zones.remove(start_zone);
				end_zones.remove(end_zone);
			}
			start_size = start_zones.size();
		}
		while(old_start_size < start_size);
			
	}
	
		
	public void update(long elapsed_time,CarSet cs,CircuitChecker cc)
	{
		
		
		if (m_train_set.size() > 0)
		{
			for (Train tr : m_train_set)
			{
				tr.update(elapsed_time);
				cc.accept_move(tr);
			}				
		}
		 		 
	}
	 public void render(Graphics2D g,Rectangle view_bounds)
	 {
		 for (Train tr : m_train_set)
		 {
			 tr.render(g,view_bounds);
		 }
	 }

}
