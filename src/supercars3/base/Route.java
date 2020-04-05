package supercars3.base;

import java.util.*;
import java.awt.*;

import java.io.IOException;

import supercars3.sys.AngleUtils;
import supercars3.sys.ParameterParser;

/**
 * <p>
 * Titre :
 * </p>
 * <p>
 * Description :
 * </p>
 * <p>
 * Copyright : Copyright (c) 2005
 * </p>
 * <p>
 * Société :
 * </p>
 * 
 * @author non attribuable
 * @version 1.0
 */

public class Route extends Selectable implements Observer
{
	private Polygon m_path = new Polygon();

	private int m_name = 0;

	private Vector<PointDirection> m_point_list;
	private Vector<PointDirection> m_resume_point_direction_list;
	
	private double m_length;

	public static final int MIN_NB_POINTS = 10;

	
	public class PointDirectionIterator
	{
		private Vector<PointDirection> container;
		private int it;
		private boolean m_lap_completed;
		
		private void reset()
		{
			it = 0;
			m_lap_completed = false;
		}
		PointDirectionIterator(Vector<PointDirection> container)
		{
			this.container = container;
			reset();
		}
		
		public void lap_completed()
		{
			m_lap_completed = true;
		}
		
		public boolean is_lap_completed()
		{
			return m_lap_completed;
		}
		
		public PointDirection next()
		{
			m_lap_completed = false;
			
			if (it == container.size())
			{
				reset();
			}
		
			return container.elementAt(it++);
		}
		public PointDirection previous()
		{
			m_lap_completed = false;
			
			if (it == 0)
			{
				it = container.size();
			}
		
			return container.elementAt(--it);
		}
		
		public boolean is_first_resume_point(PointDirection ref)
		{
			return (ref != null) && (m_resume_point_direction_list.firstElement().get_route_index() == ref.get_route_index());
		}
		
		public PointDirection next(PointDirection ref)
		{
			int ref_index = ref.get_route_index();

			boolean found = false;

			int nb_points = container.size();

			for (int i = 0; i < nb_points && !found; i++)
			{
				PointDirection p = container.elementAt(i);
				int p_index = p.get_route_index();
				
				if (i == 0)
				{
					if (p_index > ref_index)
					{
						it = i;
						found = true;
					}
				}
				else
				{
					// normal case

					if ((p_index > ref_index) && (container.elementAt(i-1).get_route_index() <= ref_index))
					{
						it = i;
						found = true;
					}
					else if ((i == nb_points-1) && (p_index <= ref_index))
					{
						// special case last point
						it = 0;
						found = true;					
					}
				}
			}

			return next();
		}
	}
	
	public Route(ParameterParser fr, 
			Vector<NamedControlPoint> point_list, Vector<Zone> zone_list)
			throws IOException
	{
		fr.startBlockVerify("route");
		/* int pn = */fr.readInteger("name");
		int nb_points = fr.readInteger("nb_points");
		m_point_list = new Vector<PointDirection>(nb_points);
	
		for (int i = 0; i < nb_points; i++) 
		{
			fr.startBlockVerify("p" + (i + 1));
			NamedControlPoint cp = (NamedControlPoint) lookup_by_name(point_list, fr
					.readInteger("name"));
			
			if (fr.readBoolean("ambiguous")) 
			{
				Zone zone = (Zone) lookup_by_name(zone_list, fr
						.readInteger("zone"));
				cp.set_forced_zone(zone,true);
			}
			m_point_list.add(new PointDirection(cp));
	
			fr.endBlockVerify();
	
		}
	
		fr.endBlockVerify();
		
		build_path();
	}

	public Route(Vector<NamedControlPoint> points, int name)
	{
		int sz = points.size();
	
		m_point_list = new Vector<PointDirection>(sz);
	
		for (int i = 0; i < sz; i++) 
		{
			NamedControlPoint cp = points.elementAt(i);
	
			// avoids adding twice the same point
	
			if (!m_point_list.contains(cp))
			{
				m_point_list.add(new PointDirection(cp));
			}
			
		}
	
		set_name(name);
		build_path();
	}

	public int get_name()
	{
		return m_name;
	}

	public PointDirectionIterator resume_point_iterator()
	{
		return new PointDirectionIterator(m_resume_point_direction_list);
	}
	

	
	public PointDirection get_point(int idx)
	{
		return m_point_list.elementAt(idx);
	}
	public Vector<PointDirection> get_point_list()
	{
		return m_point_list;
	}

	
	
	public boolean remove_point(ControlPoint p)
	{
		PointDirection pzd = get_pzd(p);
		if (pzd != null)
		{
			m_point_list.remove(pzd);
			build_path();
		}
		return m_point_list.isEmpty();
	}

	public boolean insert_point(NamedControlPoint p,
			ControlPoint before, ControlPoint after)
	{
		int insertion_pos = -1;
		boolean rebuild = false;
				
		int last_pos = m_point_list.size()-1;
		
		for (int i = 0; (i < last_pos) && !rebuild;i++)
		{
			ControlPoint ei = m_point_list.elementAt(i).get_point();
			ControlPoint eip1 = m_point_list.elementAt(i+1).get_point();
			
			if (ei.equalsTo(before) && eip1.equalsTo(after))
			{
				insertion_pos = i+1;
				rebuild = true;
			}
			/*
			else if ((eip1 == before) && (ei == after))
			{
				insertion_pos = i+1;
				rebuild = true;				
			}*/
		}
		if (!rebuild)
		{
			// try end and start
			if (m_point_list.elementAt(last_pos).get_point().equalsTo(before) && 
					m_point_list.elementAt(0).get_point().equalsTo(after))
			{
				m_point_list.add(new PointDirection(p));
				rebuild = true;
			}
			else if (m_point_list.elementAt(0).get_point().equalsTo(before) && 
					m_point_list.elementAt(last_pos).get_point().equalsTo(after))
			{
				m_point_list.add(new PointDirection(p));
				rebuild = true;
			}
		}
		else
		{
			m_point_list.add(insertion_pos,new PointDirection(p));
		}
		
		if (rebuild)
		{
			build_path();
		}
		
		return rebuild;
	}

	
	public double get_length()
	{
		return m_length;
	}

	/**
	 * 
	 * @param c
	 * @return next point of route not in the same zone
	 * @throws Exception
	 */
	/*public PointZoneDirection get_next_pzd(ControlPoint c) 
	{
		PointZoneDirection rval = null;
		Zone start_zone = c.get_forced_zone();
		
		
		
		PointZoneDirection pzd = get_pzd(c);
		
		int point_index = pzd.get_route_index();
		do
		{
			point_index++;
			if (point_index >= m_point_list.size())
			{
				point_index = 0;
			}
			rval = m_point_list.elementAt(point_index);
		}
		while(rval.get_forced_zone() == start_zone);

		return rval;
	}*/
	
	
	public PointDirection get_next_pzd(PointDirection pzd, boolean needs_zone_change, boolean same_route)
	{
		PointDirection rval = pzd;
		
		if (!same_route)
		{
			ControlPoint cp = rval.get_point();
			
			// pzd is OK but the index is useless since route has changed
			// re-compute it (with risks of errors since routes could cross, but ATM it's this way)
			
			for (PointDirection p : m_point_list)
			{
				if (p.get_point().equalsTo(cp))
				{
					rval = p;
					break;
				}
			}
		}
		
		rval = get_next_pzd(rval,needs_zone_change);
		
	
		return rval;
	}
	

	private PointDirection get_next_pzd(PointDirection pzd, boolean needs_zone_change) /*throws Exception*/
	{
		PointDirection rval = null;
		int point_index = pzd.get_route_index()-1;
		
		if (needs_zone_change)
		{
			// loops till zone change
			
			Zone start_zone = pzd.get_point().get_explicit_zone();

			do
			{
				point_index++;
				if (point_index >= m_point_list.size())
				{
					point_index = 0;
				}
				rval = m_point_list.elementAt(point_index);
			}
			while(rval.get_point().get_explicit_zone() == start_zone);
		}
		else
		{
			// simple modulo
			
			point_index++;
			if (point_index >= m_point_list.size())
			{
				point_index = 0;
			}
			rval = m_point_list.elementAt(point_index);
		}
						

		return rval;
	}
	public PointDirection get_prev_pzd(PointDirection pzd) /*throws Exception*/
	{
		PointDirection rval = null;
		int point_index = pzd.get_route_index()-1;

		// simple modulo

		point_index--;
		if (point_index < 0)
		{
			point_index = m_point_list.size() - 1;
		}
		rval = m_point_list.elementAt(point_index);


		return rval;
	}
	
	public boolean contains(ControlPoint p)
	{
		return get_pzd(p) != null;
	}
	
	public DistanceSegment closest_segment(double x, double y, boolean use_bounding_box)
	{
		DistanceSegment rval = null;
		DistanceSegment work = new DistanceSegment();
		Vector<PointDirection> work_list = m_point_list;
		
		
		Iterator<PointDirection> it = work_list.iterator();
		if (it.hasNext()) {
			PointDirection p1 = it.next();
			double distance = Double.MAX_VALUE;

			while (it.hasNext()) {
				PointDirection p2 = it.next();
				work.set_points(p1, p2);
				double d = work.distance_to((int) x, (int) y, use_bounding_box);
				if (d < distance) {
					distance = d;
					if (rval == null) rval = new DistanceSegment();
	
					rval.set_points(p1, p2, distance);						
				}
				
				// next segment
				p1 = p2;
			}
		
			{
				// start and end of route
				
				p1 = work_list.firstElement();
				PointDirection p2 = work_list.lastElement();
				work.set_points(p1, p2);
				double d = work.distance_to((int) x, (int) y, use_bounding_box);
				if (d < distance) {
					if (rval == null) rval = new DistanceSegment();
					
					rval.set_points(p1, p2, distance);						
					
				}
				
			}
		}
		return rval;
	}
	
	/* called within the game, not within the editor
	 */
	
	public PointDirection get_car_start_point(int position) throws Exception
	{
		PointDirection point = null;
		
		int p = 1;
		
		for (int i = m_point_list.size()-1; i >= 0 && point == null; i--)
		{
			PointDirection c = m_point_list.elementAt(i);
			if (c.is_car_start_point())
			{
				if (p == position)
				{
					point = new PointDirection(c);
					map_point(point,true);
				}
				p++;
			}
			
		}
		
		if (point == null)
	    {
	    	throw new Exception("Car position "+position+" not found");
	    }
		
		return point;
	}
	

	public void serialize(ParameterParser fw) throws IOException
	{
		fw.startBlockWrite("route");
		fw.write("name", get_name());
		fw.write("nb_points", m_point_list.size());
		for (int j = 0; j < m_point_list.size(); j++) 
		{
			NamedControlPoint c = m_point_list.elementAt(j).get_point();
			fw.startBlockWrite("p" + (j + 1));
			fw.write("name", c.get_name());

			Zone z = c.get_explicit_zone();
			boolean ambiguous = (c.is_ambiguous()) && (z != null);
			fw.write("ambiguous", ambiguous);

			if ((ambiguous) && (z != null)) // z!=null test prevents a java warning
			{
				fw.write("zone", z.get_name());
			}
			fw.endBlockWrite();
		}
		fw.endBlockWrite();
	}

	/**
	 * call this method withing game to get the
	 * resume points belonging to the main route
	 * 
	 * @return resume points of the route (ordered)
	 */
	public Collection<PointDirection> get_resume_points()
	{
		Vector<PointDirection> rval = new Vector<PointDirection>();
		for (PointDirection cp : m_point_list)
		{
			if (cp.is_resume_point())
			{
				rval.add(cp);
			}
		}
		
		return rval;
	}
	public void paint(java.awt.Graphics g)
	{
		Graphics2D g2 = (Graphics2D) g;
		g.setColor(is_selected() ? java.awt.Color.RED : java.awt.Color.WHITE);
		g2.draw(m_path);
		for (PointDirection pzd : m_point_list)
		{
			pzd.paint(g);
		}
	}
	
	void clear_car_start_points(boolean remove_points, PointContainer pc)
	{
		if (!remove_points)
		{
			for (PointDirection cp : m_point_list)
			{
				cp.get_point().set_car_start_point(false);
			}
		}
		else
		{			
			Vector<PointDirection> new_list = new Vector<PointDirection>();
			Vector<ControlPoint> deleted_list = new Vector<ControlPoint>();
			for (PointDirection cp : m_point_list)
			{
				if (cp.is_car_start_point())
				{
					deleted_list.add(cp.get_point());
				}
				else
				{
					new_list.add(cp);
				}
			}
			m_point_list = new_list;
			for (ControlPoint cp : deleted_list)
			{
				pc.remove_point(cp);
			}
			
			build_path();
		}
	}
	boolean create_car_start_points(ControlPoint cp1, ControlPoint cp2, int nb_points, PointContainer pc)
	{
		boolean rval = (cp1 != cp2);
		PointDirection p1 = null, p2 = null;
		if (rval)
		{
			p1 = get_pzd(cp1);
			p2 = get_pzd(cp2);
		}
		if ((p1 != null) && (p2 != null))
		{
			clear_car_start_points(false,pc);
			
			// first, compute distance between both points
			
			PointDirection start = p1.get_route_index() < p2.get_route_index() ? p1 : p2;
			PointDirection end = p1.get_route_index() < p2.get_route_index() ? p2 : p1;
			
			int start_index = start.get_route_index() - 1;
			int end_index = end.get_route_index() - 1;
			Segment s = new Segment();
			
			Vector<Double> segment_lengths = new Vector<Double>();
			
			double total_length = 0.0;
			double length;
			
			// build a partial point list copy of the route to be able to insert
			// points on the original point list without trouble (or else there
			// are insertion effects and this is all wrong)
			// at the same time, compute length of the route portion
			
			Vector<ControlPoint> partial_point_list = new Vector<ControlPoint>();
			
			for (int i = start_index; i < end_index; i++)
			{
				partial_point_list.add(m_point_list.elementAt(i).get_point());
				
				s.set_points(m_point_list.elementAt(i), m_point_list.elementAt(i+1));
				length = s.length();
				
				total_length += length;
				segment_lengths.add(new Double(length));
			}
			partial_point_list.add(m_point_list.elementAt(end_index).get_point());
			
			// divide the total length by the number of segments (which is
			// the number of points minus 1)
			
			double distance_between_points = total_length / (nb_points - 1);
			double distance_to_match = 0.0;
			
			// now that we know the distance, insert points evenly
			// (this is kind of tricky even if it appears easy at first!)
			
			int segment_index = 0;
			int point_index = 0;
			ControlPoint previous_point = null;
			
			while (point_index < nb_points - 1)
			{
				double current_distance = segment_lengths.elementAt(segment_index).doubleValue();
				if (distance_to_match < current_distance)
				{
	
					// insert point between 2 segments
					ControlPoint point1 = partial_point_list.elementAt(segment_index);
					ControlPoint point2 = partial_point_list.elementAt(segment_index+1);
					s.set_points(point1, point2);
	
					double ratio = distance_to_match / current_distance;
					
					boolean new_point = true;
					
					// avoid creating a new point if there's one
					// close enough to be a good match
					
					if (ratio < 0.05)
					{
						previous_point = point1;
						
						new_point = false;
						previous_point.set_car_start_point(true);
					}
					else if (ratio > 0.95)
					{
						previous_point = point2;
						
						new_point = false;
						previous_point.set_car_start_point(true);
					}
					
					if (new_point)
					{
						ControlPoint p = s.point(ratio);
						p.set_car_start_point(true);
						NamedControlPoint ncp = pc.add_point(p);
	
						// if previous point was cancelled, use point 1
						if (previous_point == null)
						{
							previous_point = point1;
						}
						
						this.insert_point(ncp, previous_point, point2);
						previous_point = ncp;
					}					
					
										
					distance_to_match += distance_between_points;
					
					// next point
					point_index++;
				}
				else
				{
					distance_to_match -= current_distance;
					// next segment
					segment_index++;
					// cancels previous point because we skip to next segment
					previous_point = null;
				}	
			}
	
			end.get_point().set_car_start_point(true);
			
		}
		
		return rval;
	}

	void set_name(int name)
	{
		m_name = name;
	}

	private PointDirection get_pzd(ControlPoint p)
	{
		// TODO optimize it
		PointDirection rval = null;
		for (PointDirection pzd : m_point_list)
		{
			if (pzd.get_point().equalsTo(p))
			{
				rval = pzd;
				break;
			}
		}
		return rval;
	}

	private void map_point(PointDirection point,boolean round_angles)
	{	    	
	    Segment s = new Segment(point,get_next_pzd(point,false));
	    
	    // not the exact zone angle but a discreet range (looks better)
	    
	    if (round_angles)
	    {
	    	double angle_x = s.get_angle_x();
	    	double rounded  = Math.round(angle_x / 45.0) * 45.0;
	    	
	    	// round by 45 only if originally very close to 45
	    	
	    	if (Math.abs(AngleUtils.angle_difference(rounded,angle_x))>15.0)
	    	{
	    		rounded = Math.round(angle_x / 90.0) * 90.0;
	    	}
	    	point.angle = rounded;
	    }
	    else
	    {
	    	point.angle = s.get_angle_x();
	    }
	    
	    point.route = this;
	}

	public void update(Observable obs, Object x)
	{
		build_path();
	}
	
	private void build_point_zone_directions()
	{
		// list of resume points (+direction)
		m_resume_point_direction_list = new Vector<PointDirection>();
		
		for (PointDirection pzd : m_point_list)
		{
			map_point(pzd,false);			
			
			if (pzd.is_resume_point())
			{				
				m_resume_point_direction_list.add(pzd);
			}
		}
				
	}

	private void build_path()
	{
		int ri = 1;
		Iterator<PointDirection> it = m_point_list.iterator();
	
		m_length = 0.0;
	
		m_path.reset();
	
		PointDirection fpzd = it.next();
		fpzd.set_route_index(ri++);
		ControlPoint fcp = fpzd.get_point();
		// first point
		m_path.addPoint(fcp.getX(), fcp.getY());
		fcp.addObserver(this);
		
		while (it.hasNext()) 
		{
			PointDirection c = it.next();
			ControlPoint cp = c.get_point();
			m_length += Math.sqrt(cp.square_distance(fcp));
			c.set_route_index(ri++);
			cp.addObserver(this);
			m_path.addPoint(cp.getX(), cp.getY());
		}
	
		//m_path.addPoint(fcp.getX(), fcp.getY());
	
		build_point_zone_directions();
	}

	private static Nameable lookup_by_name(Vector<? extends Nameable> named_points_vector, int name)
	{
		Nameable rval = null;
	
		for (int i = 0; i < named_points_vector.size() && rval == null; i++) {
			Nameable nc = named_points_vector.elementAt(i);
			if (nc.get_name() == name) 
			{
				rval = nc;
			}
		}
		return rval;
	}

}