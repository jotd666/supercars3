package supercars3.base;

import java.io.*;
import java.awt.*;
import java.awt.image.BufferedImage;

import java.util.*;

import supercars3.sys.AnimatedImage;
import supercars3.sys.ParameterParser;

/**
 * <p>Titre : </p>
 * <p>Description : </p>
 * <p>Copyright : Copyright (c) 2005</p>
 * <p>Société : </p>
 * @author non attribuable
 * @version 1.0
 */

public class CircuitData implements PointContainer 
{
  	// speed table uses original maximum speeds from Supercars 2 depending on the engine

	public CircuitData(CircuitDirectory dir) 
  {
	  m_opponent_properties = new OpponentProperties(dir);
  }
	private static int LAKE_REFRESH_RATE = 100;


  private Vector<NamedControlPoint> m_point_list = new Vector<NamedControlPoint>();
  private Vector<Zone> m_zone_list = new Vector<Zone>();
  private Vector<Route> m_route_list = new Vector<Route>();
  private HashSet<Zone> m_top_priority_zones = new HashSet<Zone>();
  private String m_project_file = null;
  private boolean m_modified = false;
  private int m_spare_zone_name = 0;
  private int m_spare_route_name = 0;
  private int m_spare_point_name = 0;
  private int m_missed_checkpoint_tolerance = 0;
  
  private int m_nb_laps = 1;
  private Route [] m_shared_route_vector;
  
  private LinkedList<Integer> m_route_display_filter = new LinkedList<Integer>();
  private boolean m_zones_segments_displayed = true;
  private boolean m_zones_points_displayed = true;
   
  public boolean split_zone(Zone z, boolean vertical)
  {
	  boolean rval = true;
	  double threshold = 20.0;
	  
	  Boundary [] split_segments = new Boundary[2];
	  Boundary [] opposed_segments = new Boundary[2];
	  Boundary b0 = z.get_boundary(0);
	  Boundary b1 = z.get_boundary(1);
	  Boundary b2 = z.get_boundary(2);
	  Boundary b3 = z.get_boundary(3);
	  
	  // find the more vertical (resp. horizontal) segments if possible

	  if (!vertical)
	  {		  
		  boolean vert_02 = b0.is_rather_vertical(threshold) &&
		  b2.is_rather_vertical(threshold);
		  boolean vert_13 = b1.is_rather_vertical(threshold) &&
		  b3.is_rather_vertical(threshold);

		  if (vert_02)
		  {
			  split_segments[0] =  b0;
			  split_segments[1] =  b2;	
			  opposed_segments[0] =  b1;
			  opposed_segments[1] =  b3;	
		  }
		  else if (vert_13)
		  {
			  split_segments[0] =  b1;
			  split_segments[1] =  b3;				  
			  opposed_segments[0] =  b0;
			  opposed_segments[1] =  b2;	
		  }
	  }
	  else
	  {
		  boolean hori_02 = b0.is_rather_horizontal(threshold) &&
		  z.get_boundary(2).is_rather_horizontal(threshold);
		  boolean hori_13 = z.get_boundary(1).is_rather_horizontal(threshold) &&
		  z.get_boundary(3).is_rather_horizontal(threshold);

		  if (hori_02)
		  {
			  split_segments[0] =  z.get_boundary(0);
			  split_segments[1] =  z.get_boundary(2);
			  opposed_segments[0] =  z.get_boundary(1);
			  opposed_segments[1] =  z.get_boundary(3);	
		  }
		  else if (hori_13)
		  {
			  split_segments[0] =  z.get_boundary(1);
			  split_segments[1] =  z.get_boundary(3);				  
			  opposed_segments[0] =  z.get_boundary(0);
			  opposed_segments[1] =  z.get_boundary(2);	
		  }

	  }
	  
	  rval = (split_segments[0] != null);
		  
	  if (rval)
	  {
		  
		  // first, remove zone
		  remove_zone(z);
		  // create 2 new points
		  NamedControlPoint p1 = add_point(split_segments[0].centre());
		  NamedControlPoint p2 = add_point(split_segments[1].centre());
		  

		  for (int i = 0; i < 2; i++)
		  {
			  Collection<ControlPoint> cpl = new LinkedList<ControlPoint>();
			  cpl.add(opposed_segments[i].m_start);
			  cpl.add(opposed_segments[i].m_end);
			  cpl.add(p1);
			  cpl.add(p2);
			  add_zone(new Zone(cpl,0));
		  }			  						  

	  }
	  
	  return rval;
  }
  
 
  
  public void toggle_zones_points_displayed()
  {
	  m_zones_points_displayed = !m_zones_points_displayed;
  }
  
  public void toggle_zones_segments_displayed()
  {
	  m_zones_segments_displayed = !m_zones_segments_displayed;
  }
  
  public boolean are_zones_displayed()
  {
	  return m_zones_segments_displayed;
  }
  public void toggle_filtered_route(int r)
  {
	Integer rn = new Integer(r);
	
	if (m_route_display_filter.contains(rn))
	{
		m_route_display_filter.remove(rn);
	}
	else
	{
		m_route_display_filter.add(rn);
	}
  }
  
  public void reset_display_filters()
  {
	  m_route_display_filter.clear();
  }
  
  
  private OpponentProperties m_opponent_properties = null;
  
  public OpponentProperties get_opponent_properties()
  {
	  return m_opponent_properties;
  }
  
  public String get_project_file() 
  {
    return m_project_file;
  }

  public void new_project(String image_file)
  {
    int idx = image_file.lastIndexOf('.');
    reset();
    m_project_file = image_file.substring(0, idx) +
        DirectoryBase.CIRCUIT_EXTENSION;
  }

  /**
   *
   * @param x
   * @param y
   * @return zones in the list containing coords x,y
   */
  public Collection<Zone> locate(int x, int y) 
  {
    HashSet<Zone> rval = new HashSet<Zone>();
    int s = get_zone_list_size();
    for (int i = 0; i < s; i++) {
      Zone z = get_zone(i);
      if (z.contains(x, y)) 
      {
        rval.add(z);
      }
    }
    return rval;

  }
  
  public void clear_car_start_points(boolean remove_points)
  {
	  for (Route r : m_route_list)
	  {
		  r.clear_car_start_points(remove_points,this);
	  }
  }
  public void create_car_start_points(ControlPoint p1, ControlPoint p2, int nb_points)
  {	  
	 
	  // first, locate all routes containing both points
	  
	  for (Route r : m_route_list)
	  {
		  if (r.create_car_start_points(p1,p2,nb_points,this))
		  {
			  set_modified();
		  }
	  }	 
  }
  
  /**
   * find zones hosting control points of routes
   *
   */
  public void resolve_zone_ambiguities()
  {
    for (Route r : m_route_list)
    {
      Collection<PointDirection> point_list = r.get_point_list();
      Iterator<PointDirection> point_it = point_list.iterator();

      while (point_it.hasNext())
      {
        ControlPoint cp = point_it.next().get_point();
        if (cp.get_explicit_zone() == null)
        {
          Collection<Zone> c = locate(cp.getX(),cp.getY());
          if (c.size() == 1)
          {
            cp.set_forced_zone( c.iterator().next(),false);
          }
          // is checked somewhere else
          /*else
          {
            throw (new Exception("non-solved ambiguity for point "+
                                 cp.toString()));
          }*/
        }
      }
    }
  }
  public File get_shadow_image_file()
  {
	   int idx = get_project_file().lastIndexOf('.');
	   String rval = get_project_file().substring(0, idx) + "_shadow.png";
	   return new File(rval);
  }
  public String get_main_image_file() 
  {
    int idx = get_project_file().lastIndexOf('.');
    String rval = get_project_file().substring(0, idx) + ".png";

    return rval;
  }

  public void set_project_file(String imf) {
    m_project_file = imf;
  }

  public int size() 
  {
    return m_point_list.size();
  }

  public Collection<Zone> get_zone_list()
  {
    return m_zone_list;
  }

  public int get_zone_list_size() 
  {
    return m_zone_list.size();
  }


  public NamedControlPoint get_point(int i) 
  {
    return m_point_list.get(i);
  }

  public Collection<Route> get_route_list()
  {
	  return m_route_list;
  }
  
  public Collection<NamedControlPoint> get_lake_seed_points()
  {
	  Vector<NamedControlPoint> rval = new Vector<NamedControlPoint>();
	  for (NamedControlPoint cp : m_point_list)
	  {
		  if (cp.is_lake_seed_point())
		  {
			  rval.add(cp);
		  }
	  }
	  
	  return rval;
  }
  
  public Collection<Route> get_showed_route_list()
  {
	  LinkedList<Route> rval = new LinkedList<Route>();
	  
	  for (Route r : m_route_list)
	  {
		  if (check_showed(r))
		  {
			  rval.add(r);
		  }
	  }
	  
	  return rval;
  }
  

  private void fence_auto_detection()
  {
	  for (Zone z1 : m_zone_list)
	  { 
		  for (int i = 0; i < Zone.NB_SEGMENTS;i++)
		  {
			  Boundary b = z1.get_boundary(i);
			  boolean has_neighbour = false;
			  
			  for (Zone z2 : m_zone_list)
			  {
				  if (z1 != z2)
				  {
					  Vector<Boundary> blist = z1.get_shared_boundaries(z2);
					  if (blist.contains(b))
					  {
						  has_neighbour = true;					  
						  break;
					  }
				  }
			  }
			  
			  if (!has_neighbour)
			  {
				  b.set_fence(true);
			  }
		  }
	  }
  }
  
  public Zone get_zone(int i) 
  {
    return m_zone_list.get(i);
  }

  /**
   * save circuit data to sc3 file
   *
   * @throws IOException
   */
  public void save() throws IOException 
  {
    ParameterParser fw = ParameterParser.create(m_project_file);

    fw.startBlockWrite("SUPERCARS3_CIRCUIT");

    fw.write("nb_laps",m_nb_laps);
    fw.write("missed_checkpoint_tolerance",m_missed_checkpoint_tolerance);

    fw.startBlockWrite("control-points");

    fw.write("nb_points", size());

    for (NamedControlPoint cp : m_point_list) 
    {
      cp.serialize(fw);
    }
    fw.endBlockWrite();

    fw.startBlockWrite("control-zones");
    fw.write("nb_zones", get_zone_list_size());
    for (int i = 0; i < get_zone_list_size(); i++) {
    	get_zone(i).serialize(fw);
    }
    fw.endBlockWrite();

    fw.startBlockWrite("control-routes");
    fw.write("nb_routes", get_route_list().size());
    for (Route r : get_route_list())
    {
    	r.serialize(fw);
    }
    fw.endBlockWrite();

    fw.startBlockWrite("lakes");
    Collection<NamedControlPoint> lake_points = get_lake_seed_points();
    
    fw.write("nb_lakes", lake_points.size());
    for (NamedControlPoint cp : lake_points)
    {
    	fw.write("seed_point", cp.get_name());
    }
    fw.endBlockWrite();

    fw.startBlockWrite("opponent-properties");    
    fw.write("car_front_weapon_index",m_opponent_properties.car_front_weapon);
    fw.write("car_rear_weapon_index",m_opponent_properties.car_rear_weapon);
    fw.write("train_engine",m_opponent_properties.train_engine);     
    fw.write("train_min_wagons",m_opponent_properties.min_wagons);     
    fw.write("train_max_wagons",m_opponent_properties.max_wagons);     
    fw.write("train_wait",m_opponent_properties.train_wait);     
    fw.write("train_offset",m_opponent_properties.train_offset);     
    fw.endBlockWrite();
    
    // final
    fw.endBlockWrite();
    fw.close();

    m_modified = false;
  }

 

  public void set_nb_laps(int n)
  {
    m_nb_laps = n;
  }


  public int get_nb_laps()
  {
    return m_nb_laps;
  }


  public HashSet<Zone> get_top_priority_zones()
  {
	  return m_top_priority_zones;
  }
  public void set_missed_checkpoint_tolerance(int mct)
  {
	  m_missed_checkpoint_tolerance = mct;
  }
  
  
  public int get_missed_checkpoint_tolerance()
  {
	  return m_missed_checkpoint_tolerance;
  }
  
  public int get_nb_checkpoints()
  {
	  int rval = 0;
	  
	  for (int i = 0; i < m_zone_list.size(); i++)
	  {
		  Zone z = get_zone(i);
		  if (z.get_checkpoint_type() == Zone.CheckpointType.CHECKPOINT)
		  {
			  rval++;
		  }
	  }
	  return rval;
  }
  /**
   * load from game
   * @param file
   * @param scale
   * @throws Exception
   */
  public void load(String file, boolean scale2x) throws Exception
  {
    unchecked_load(file,scale2x);
    
    // compute fence
    
	fence_auto_detection();

    Collection<String> err = report_errors();
    
    if (!err.isEmpty())
    {
    	for (String s : err)
    	{
    		System.out.println(s);
    	}
      throw new Exception("Circuit "+m_project_file+" has errors, use editor to fix them");
    }
  }
  public void unchecked_load(String file, boolean scale2x) throws IOException {
    IOException exc = null;
    double scale = scale2x ? 2 : 1;
    try
    {
    	reset();
    	ParameterParser fr = ParameterParser.open(file);
    	fr.startBlockVerify("SUPERCARS3_CIRCUIT");

    	m_nb_laps = fr.readInteger("nb_laps");
    	m_missed_checkpoint_tolerance = fr.readInteger("missed_checkpoint_tolerance");

    	fr.startBlockVerify("control-points");
    	int nb_points = fr.readInteger("nb_points");
    	for (int i = 0; i < nb_points; i++) {
    		add_point(new NamedControlPoint(fr,scale));
    	}

    	fr.endBlockVerify();

    	fr.startBlockVerify("control-zones");
    int nb_zones = fr.readInteger("nb_zones");
    for (int i = 0; i < nb_zones; i++) 
    {
      Zone z = new Zone(fr, m_point_list);
      add_zone(z);
     
      if (z.get_visible_type() == Zone.ZoneType.TOP_PRIORITY)
      {
    	  // top priority: fence it to avoid problems
    	  // (no car can be in that zone)
    	  
    	  for (int j = 0; j < Zone.NB_SEGMENTS; j++)
    	  {
    		  z.get_boundary(j).set_fence(true);
    	  }
    	  m_top_priority_zones.add(z);
      }
    }

    fr.endBlockVerify();

    fr.startBlockVerify("control-routes");
    int nb_routes = fr.readInteger("nb_routes");
    for (int i = 0; i < nb_routes; i++) 
    {
      add_route(new Route(fr, m_point_list, m_zone_list));
    }

    fr.endBlockVerify();
    
    fr.startBlockVerify("lakes");
    int nb_lakes = fr.readInteger("nb_lakes");
    for (int i = 0; i < nb_lakes; i++) 
    {
      int cpn = fr.readInteger("seed_point");
      NamedControlPoint p = lookup_by_name(cpn);
      if (p != null)
      {
    	  p.set_lake_seed_point(true);
      }
    }

    fr.endBlockVerify();
    
    
    fr.startBlockVerify("opponent-properties");
    m_opponent_properties.car_front_weapon = fr.readInteger("car_front_weapon_index");
    m_opponent_properties.car_rear_weapon = fr.readInteger("car_rear_weapon_index");
    m_opponent_properties.train_engine = fr.readFloat("train_engine");
    m_opponent_properties.min_wagons = fr.readInteger("train_min_wagons");   
    m_opponent_properties.max_wagons = fr.readInteger("train_max_wagons");     
    m_opponent_properties.train_wait = fr.readInteger("train_wait");     
    m_opponent_properties.train_offset = fr.readInteger("train_offset");     

    fr.endBlockVerify();

    fr.endBlockVerify();

    m_modified = false;
	  }
	  catch (IOException e)
	  {
		  exc = e;
	  }
	  
    m_project_file = file;
    update_spare_names();
    
    if (exc != null) throw exc;
  }

  public void unselect_all_points() 
  {
    for (int i = 0; i < m_point_list.size(); i++) {
      get_point(i).unselect();
    }
  }

  public void unselect_all_zones() 
  {
    for (int i = 0; i < m_zone_list.size(); i++) {
      get_zone(i).unselect();
    }
  }

  public NamedControlPoint add_point(ControlPoint c) 
  {
    return add_point(new NamedControlPoint(c, ++m_spare_point_name));
  }

  public NamedControlPoint add_point(NamedControlPoint c) 
  {
    m_point_list.add(c);
    m_modified = true;
    return c;
  }

  public void remove_point(ControlPoint c) 
  {
	  boolean keep_point = false;
	  
	  // lookup for point in hidden routes
	  for (Route r : m_route_list)
	  {
		  if (r.contains(c))
		  {
			  if (!check_showed(r))
			  {
				  // keep the point if a hidden route
				  // references it
				  
				  keep_point = true;
			  }
			  else
			  {
				  // remove the point from the showed route
				  r.remove_point(c);
				  m_modified = true;
			  }
		  }
		  
	  }
	  if (!keep_point)
	  {
		  if (m_point_list.remove(c))
		  {
			  m_modified = true;
		  }
	  }
	  
	  if (m_modified)
	  {
		  update_referencing_zones(c);
	  }
	  
	  update_spare_names();
  }
  
  public void set_lake_seed_point(ControlPoint p,boolean state)
  {
    if (state != p.is_lake_seed_point())
    {
      m_modified = true;
    }
    p.set_lake_seed_point(state);
  }

  public void set_resume_point(ControlPoint p,boolean state)
  {
    if (state != p.is_resume_point())
    {
      m_modified = true;
    }
    p.set_resume_point(state);
  }
  public void set_car_start_point(ControlPoint p,boolean state)
  {
    if (state != p.is_car_start_point())
    {
      m_modified = true;
    }
    p.set_car_start_point(state);
  }

  public void delete_selected_objects()
  {
    //delete_selected_objects(m_point_list);
    delete_selected_objects(m_zone_list);
    delete_selected_objects(m_route_list);
  }

  public boolean is_modified() {
    return m_modified;
  }

  	public void set_modified()
  	{
  		m_modified = true;
  	}
  public Zone add_zone(Collection<ControlPoint> points) {
    Zone z = new Zone(points, ++m_spare_zone_name);

    m_modified = m_zone_list.add(z);

    return z;
  }
 
 
  
  public Route add_route(Vector<NamedControlPoint> points) 
  {
    Route r = new Route(points, ++m_spare_route_name);

    m_modified = true;
    m_route_list.add(r);

    update_split_points();
    
    return r;
  }
  public void add_route(Route r) 
  {
     // ensure unicity for the next generated names

     m_spare_route_name = Math.max(r.get_name(), m_spare_route_name + 1);

     r.set_name(m_spare_route_name);

     m_modified = true;
     m_route_list.add(r);

     update_split_points();
   }


  public Zone add_zone(Zone z) 
  {

    // ensure unicity for the next generated names

    m_spare_zone_name = Math.max(z.get_name(), m_spare_zone_name + 1);

    z.set_name(m_spare_zone_name);
	  
    m_modified = m_zone_list.add(z);
    
    return z;
  }

  public Route get_main_route()
  {
	  return m_route_list.isEmpty() ? null : m_route_list.elementAt(0);
  }
  public HashSet<Route> get_routes_containing(ControlPoint p)
  {
    HashSet<Route> hs = new HashSet<Route>();
    Iterator<Route> it = m_route_list.iterator();
    while (it.hasNext())
    {
      Route r = it.next();
      if (r.contains(p))
      {
        hs.add(r);
      }
    }
    return hs;
  }


  /**
   * 
   * @param cp point to move
   * @param x new x
   * @param y new y
   * @return list of control zones containing point at new location
   */
  public Collection<Zone> move_point(ControlPoint cp, int x, int y)
  {
	  HashSet<Route> routes = get_routes_containing(cp);
	  Collection<Zone> zs = null;
	  
	  boolean move_point = (routes.isEmpty());
	  
	  if (!move_point)
	  {
		  // point belongs to one or many routes
		  // check if new position is within a zone
		  
          zs = locate(x,y);
          
          move_point = !zs.isEmpty();

	  }
	  if (move_point)
	  {
		  cp.set_location(x,y);
		  set_modified();
	  }
	  
 	 return zs;
  }


  /**
   * locate a convex shape within the zone list
   * @param s convex shape to test
   * @param at affine transform to set the shape to the zone scale
   * @return list of zones intersecting the shape
   */
  /*
  public Vector<Zone> locate(Shape s,AffineTransform at)
  {
    Vector<Zone> v = new Vector<Zone>();

    for (int i = 0; i < get_zone_list_size(); i++) 
    {
      Zone z = get_zone(i);

      PathIterator pi = s.getPath---Iterator(at);

      boolean found = false;

      while (!pi.isDone() && !found) {
        pi.currentSegment(m_work_array);
        if (z.intersects(m_work_array))
        {
          found = true;
          v.add(z);
        }
      }
    }
    return v;
  }
  */
  public Vector<String> report_errors()
  {
    Vector<String> v = new Vector<String>();
     boolean fatal = false;
     
     resolve_zone_ambiguities();
     link_zones_and_points();
     
    // 0) check for more than 2 zones
    // 1) check for at least 1 route

    if (m_zone_list.size() < 6)
    {
      v.add("Not enough zones, 6 minimum required");
      fatal = true;
    }
    if (get_nb_checkpoints() < 4)
    {
    	v.add("Not enough checkpoint zones, 4 minimum required");
    }
    if (m_route_list.isEmpty())
    {
      v.add("At least 1 route required");
      fatal = true;
    }

    Iterator<Zone> it_zone = m_zone_list.iterator();
    while (it_zone.hasNext()) 
    {
      Zone z = it_zone.next();

      if ( (z.get_visible_type() != Zone.ZoneType.TOP_PRIORITY))
      {
    	  if (!z.compute_neighbour_zones(m_zone_list))    	 
    	  {
    		  v.add("Zone "+z.get_name()+" has no neighbours");
    	  }
      }
      

      if (z.get_checkpoint_type() == Zone.CheckpointType.CHECKPOINT)
      {
    	  // only check for main route (other resume points are useless)
    	  Route r = get_main_route();
    	  {
    		  Zone.RoutePointList rpl = z.get_route_point_list(r.get_name());
    		  if (rpl != null)
    		  {
    			  Iterator<PointDirection> it = rpl.iterator();
    			  boolean found = false;
    			  while(it.hasNext() && !found)
    			  {
    				  PointDirection cp = it.next();
    				  found = (cp.is_resume_point());    				  
    			  }
    			  if (!found)
    			  {
    				  v.add("Checkpoint zone "+z.get_name()+" has no resume point for route "+r.get_name());
    			  }
    		  }

    	  }
      }
    }
    if (!fatal)
    {
      // check routes for 10 points at least
      Iterator<Route> it_route = m_route_list.iterator();
      while (it_route.hasNext())
      {
        Route r = it_route.next();
        if (r.get_point_list().size() < Route.MIN_NB_POINTS)
        {
          v.add("Routes must have at least "+Route.MIN_NB_POINTS+
                " points to position cars at start");
        }
      }

      int nb_resume_points = 0;

      Iterator<NamedControlPoint> it_cp = m_point_list.iterator();
      while (it_cp.hasNext())
      {
        ControlPoint cp = it_cp.next();
        if (cp.is_resume_point())
        {
          nb_resume_points++;
        }
      }

      if (nb_resume_points < get_nb_checkpoints())
      {
        v.add("No or not enough resume points ("+nb_resume_points+". At least "+get_nb_checkpoints()+" required (nb checkpoints)");
      }
      boolean finish_line_found = false;

      it_zone = m_zone_list.iterator();
      while (it_zone.hasNext()) 
      {
        Zone z = it_zone.next();
        // 2) check for at least a zone with a finish line
        if (z.get_checkpoint_type() == Zone.CheckpointType.FINISH)
        {
          finish_line_found = true;
        }
       
      }
      if (!finish_line_found)
      {
        v.add("No finish line defined");
      }
    }

    for (Route r : m_route_list)
    {

    	Vector<PointDirection> point_list = r.get_point_list();

    	for (int i=0;i<point_list.size();i++)
    	{
    		ControlPoint cp = point_list.elementAt(i).get_point();

    		if (cp.get_explicit_zone() == null)
    		{
    			Collection<Zone> c = locate(cp.getX(),cp.getY());

    			switch (c.size())
    			{
    			case 0:
    				v.add("Route "+r.get_name()+": point "+(i+1)+" belongs to no zone");        	 
    				break;
    			case 1:
    				break;
    			default:
    			{
    				String zones = "";        	
    				it_zone = c.iterator();
    				while (it_zone.hasNext())
    				{
    					zones += " "+it_zone.next().get_name();
    				}
    				v.add("Route "+r.get_name()+": point "+(i+1)+" could belong to "+c.size()+" zones:"+ zones);

    				break;
    			}
    			}
    		}
    	}
    }


    return v;
  }
  
  public void link_zones_and_points()
  {
	  for (Zone z : get_zone_list())
	  {
		  z.reset_route_points();
	  
	  }
// for each route
      
      for (Route r : get_route_list())
      {
    	  for (Zone z : get_zone_list())
    	  {
    		  z.add_route_points(r);
    	  }
      }
  }
  
  public boolean check_showed(Route r)
  {
	  return m_route_display_filter.contains(new Integer(r.get_name()));
  }
  
  public boolean check_showed(ControlPoint p)
  {
	  boolean showed = true;
	  
	  if (!m_zones_points_displayed)
	  {
		  for (Zone z : m_zone_list)
		  {
			  if (z.get_border_point_list().contains(p))
			  {
				  showed = false;
				  break;
			  }
		  }
	  }
	  
	  if ((showed) && (m_route_list.size() != m_route_display_filter.size()))
	  {
		  for (Route r : m_route_list) 
		  {
			  if (!check_showed(r))
			  {
				  if (r.contains(p))
				  {
					  // points belongs to a hidden route
					  // do not display it

					  showed = false;

					  // unless it is contained in another showed route

					  for (Route r2 : m_route_list)
					  {
						  if (r2.contains(p) && (check_showed(r2)))
						  {
							  showed = true;
							  break;
						  }
					  }
					  break;
				  }
			  }
		  }
	  }
      return showed;
  }
  
  public void paint_points(Graphics graphics) {
   
    for (ControlPoint p : m_point_list) 
    {
    	if (check_showed(p))
        {
        	p.paint(graphics);
        }    
    }

  }

  public void paint_zones(Graphics graphics) 
  {
	  if (m_zones_segments_displayed)
	  {
		  int s = get_zone_list_size();
		  for (int i = 0; i < s; i++) 
		  {
			  Zone p = get_zone(i);
			  p.paint(graphics);

		  }
	  }
  }
  public void paint_routes(Graphics graphics) 
  {
    for (Route r : m_route_list) 
    {
    	if (check_showed(r))
    	{
    		r.paint(graphics);
    	}

    }
  }

  public void remove_zone(Zone z) 
  {
    if (m_zone_list.remove(z)) 
    {
      m_modified = true;
      update_spare_names();
    }
  }

  public void reset() 
  {
    m_spare_zone_name = 0;
    m_spare_route_name = 0;
    m_spare_point_name = 0;
    m_point_list.clear();
    m_zone_list.clear();
    m_route_list.clear();
    m_route_display_filter.clear();
    m_zones_points_displayed = true;
    m_zones_segments_displayed = true;
       
    m_modified = true;
    m_nb_laps = 1;
    m_missed_checkpoint_tolerance = 0;
    m_project_file = null;
  }

  public Route get_alternate_route(Route r,ControlPoint p)
  {
	  Route rval = r;
	  int nb_matching_routes = 0;
	  
	  if (m_route_list.size() > 1)
	  {
		  m_shared_route_vector[nb_matching_routes++] = r;
		  
		  // lookup for the point in other routes
		  for (Route r1 : m_route_list)
		  {
			  if ((r1 != r) && (r1.contains(p)))				  
			  {
				  // we found an alternate point
				  m_shared_route_vector[nb_matching_routes++] = r1;
			  }
		  }
		  
		  // draw a route at random
		  
		  int new_route_index = (int)(Math.random() * nb_matching_routes);
		  
		  rval = m_shared_route_vector[new_route_index];
	  }
	  
	  return rval;
  }
  private void update_split_points()
  {
	  m_shared_route_vector = new Route[m_route_list.size()];
  }
  
private void update_spare_names() 
{
    m_spare_route_name = 0;
    m_spare_zone_name = 0;
    m_spare_point_name = 0;

    int s = get_zone_list_size();
    for (int i = 0; i < s; i++) {
      Zone z = get_zone(i);
      if (z.get_name() > m_spare_zone_name) 
      {
        m_spare_zone_name = z.get_name();
      }
    }
    
    for (NamedControlPoint c : m_point_list)
    {
      if (c.get_name() > m_spare_point_name) 
      {
        m_spare_point_name = c.get_name();
      }
    }

 
    for (Route c : m_route_list)
    {
      if (c.get_name() > m_spare_route_name) 
      {
        m_spare_route_name = c.get_name();
      }
    }

  }

private void delete_selected_objects(Vector<? extends Selectable> list)
  {
    HashSet<Selectable> hs = new HashSet<Selectable>();

   Iterator<? extends Selectable> it = list.iterator();
    while (it.hasNext())
    {
      Selectable s = it.next();
      if (s.is_selected())
      {
        hs.add(s);
        m_modified = true;
      }
    }
    it = hs.iterator();
    while (it.hasNext())
    {
      list.remove(it.next());
    }
  }

private enum MatchingColor { TO_DO, MATCH, DONT_MATCH }

private boolean try_to_add(BufferedImage img, int x, int y, MatchingColor [] mark_array, 
		Stack<ControlPoint> l, int required_rgb, int [] min_coords, int [] max_coords)
{
	boolean ok = false;
	int i = img.getWidth() * y + x;
	
	if ((x>=0)&&(x<img.getWidth()&&(y>=0)&&(y<img.getHeight())) && (mark_array[i] == MatchingColor.TO_DO))
	{
		if (img.getRGB(x, y) == required_rgb)
		{
			ok = true;

			l.push(new ControlPoint(x,y));

			// compute min/max for bounding box

			if (x < min_coords[0])
			{
				min_coords[0] = x;				
			} else if (x > max_coords[0])
			{
				max_coords[0] = x;
			}

			if (y < min_coords[1])
			{
				min_coords[1] = y;
			} else if (y > max_coords[1])
			{
				max_coords[1] = y;
			}
			mark_array[i] = MatchingColor.MATCH;
		}
		else
		{
			mark_array[i] = MatchingColor.DONT_MATCH;
		}
	}

	return ok;
}


public AnimatedImage generate_lake_animation(BufferedImage source, ControlPoint seed)
{
	Color [] palette = new Color[6];
	Color medium = new Color(source.getRGB(seed.m_x,seed.m_y));
	int red = medium.getRed();
	int blue = medium.getBlue();
	int green = medium.getGreen();
	int delta = 4;
	
	// generate palette
		
	int offset = 0;

	for (int i = 0; i < palette.length; i++)
	{
		offset = i * delta;
		
		palette[i] = new Color(Math.min(red+offset,255),
				Math.min(255,green+offset),Math.min(255,blue+offset));
	}
	
	// create point list (forest fire search)
	
	int [] min_coords = new int[2];
	int [] max_coords = new int[2];
	
	min_coords[0] = seed.m_x;
	min_coords[1] = seed.m_y;
	max_coords[0] = seed.m_x;
	max_coords[1] = seed.m_y;
	
	Stack<ControlPoint> to_do = new Stack<ControlPoint>();
	
	MatchingColor [] mark_array = new MatchingColor[source.getWidth() * source.getHeight()];
	
	for (int i = 0; i < mark_array.length; i++)
	{
		mark_array[i] = MatchingColor.TO_DO;
	}
	
	int medium_rgb = medium.getRGB();
	
	try_to_add(source,seed.m_x,seed.m_y,mark_array,to_do,medium_rgb,min_coords,max_coords);
	

	while (!to_do.isEmpty())
	{
		ControlPoint p = to_do.pop();

		int x = p.m_x;
		int y = p.m_y;
		
		try_to_add(source,x+1,y,mark_array,to_do,medium_rgb,min_coords,max_coords);
		try_to_add(source,x-1,y,mark_array,to_do,medium_rgb,min_coords,max_coords);
		try_to_add(source,x,y+1,mark_array,to_do,medium_rgb,min_coords,max_coords);
		try_to_add(source,x,y-1,mark_array,to_do,medium_rgb,min_coords,max_coords);
		

	}
	
	
	Rectangle r = new Rectangle(min_coords[0],min_coords[1],
			max_coords[0]-min_coords[0],max_coords[1]-min_coords[1]);
	
	Color transparent = new Color(0,0,0,0);
	
	AnimatedImage rval = new AnimatedImage(palette.length,LAKE_REFRESH_RATE,true,r);

	// initialize palette for first frame
	
	int match = 0;

	for (int i = 0; i < mark_array.length; i++)
	{
		if (mark_array[i] == MatchingColor.MATCH)
		{
			match++;
		}
	}
	
	int [] color_level = new int[match];
	
	for (int i = 0; i < match; i++)
	{
		color_level[i] = (int)(Math.random() * palette.length);
	}
	
	// create images
	
	for (BufferedImage img : rval.get_frames())
	{
		Graphics g = img.getGraphics();
		
		// draw transparent
		
		g.setColor(transparent);
		g.fillRect(0, 0, r.width, r.height);
		
		// now fill lake with various colors
					
		int w = source.getWidth();
		
		int color_index = 0;
		
		for (int i = 0; i < mark_array.length; i++)
		{
			if (mark_array[i] == MatchingColor.MATCH)
			{
				int cli = color_level[color_index]++;
								
				Color c = palette[cli];
							
				if (color_level[color_index] == palette.length)
				{
					color_level[color_index] = 0;
				}

				color_index++;
				
				// draw the color

				g.setColor(c);
				
				// change the color for the next frame
				
				int y = i / w;
				int x = i - (y * w);
				
				g.fillRect(x-r.x,y-r.y,2,1);
			}
		}
	}
	
	return rval;
}

  

/*private void update_referencing_objects(ControlPoint c)
{
	update_referencing_zones(c);
	update_referencing_routes(c);
//	update spare zone & points names
	update_spare_names();
}

private void update_referencing_routes(ControlPoint c)
{
    // scan all routes referencing this point
    int s = m_route_list.size();
    int i = s;
    // remove all routes which contain the removed point
    // remove all points of removed routes
    while (i > 0) 
    {
      i--;
      Route r = m_route_list.elementAt(i);
      
      if (r.remove_point(c))
      {
    	  m_route_list.remove(r);
      }
    }
  }

*/

private void update_referencing_zones(ControlPoint c)
  {
    // scan all zones referencing this point
    int s = get_zone_list_size();
    int i = s;
    // remove all zones which contain the removed point
    while (i > 0) 
    {
      i--;
      Zone z = get_zone(i);
      if (z.is_bound(c)) 
      {
        m_zone_list.remove(z);
      }
    }
  }


public NamedControlPoint lookup_by_name(int point_name)
{
  NamedControlPoint rval = null;
  for (int i=0; i < m_point_list.size() && rval == null; i++)
  {
   NamedControlPoint nc = m_point_list.elementAt(i);
    if (nc.get_name() == point_name)
    {
      rval = nc;
    }
  }
  return rval;
}


}