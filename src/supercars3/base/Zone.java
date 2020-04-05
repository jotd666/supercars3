package supercars3.base;


import java.util.*;
import java.awt.geom.*;
import java.awt.*;
import java.io.IOException;

import supercars3.sys.AngleUtils;
import supercars3.sys.ParameterParser;


//import supercars3.game.cars.Car;

/**
 * <p>Titre : </p>
 * <p>Description : </p>
 * <p>Copyright : Copyright (c) 2005</p>
 * <p>Société : </p>
 * @author non attribuable
 * @version 1.0
 */

public class Zone extends Selectable implements Nameable, Observer {
  public static final int NB_SEGMENTS = 4;
  public static final String[] FINISH_TYPE_STR = {
      "none", "normal", "checkpoint"};
  public static final String[] ZONE_TYPE_STR = {
      "normal", "hidden", "jump", "top", "gates", "train-start", "train-end"};
  // finish line type
  
  public enum CheckpointType { NONE,FINISH,CHECKPOINT }

  public enum ZoneType { NORMAL,HIDDEN,JUMP,TOP_PRIORITY,GATES,TRAIN_START,TRAIN_END }
  
  public enum Direction { LEFT, RIGHT, UP, DOWN }
  
  private CheckpointType m_checkpoint_type = CheckpointType.NONE;
  private ZoneType m_visible_type = ZoneType.NORMAL;
  
  private HashSet<Zone> m_neighbour_zones;
  
  private LinkedList<ControlPoint> m_border_point_list = new LinkedList<ControlPoint>();
  
  public class RoutePointList extends LinkedList<PointDirection>
  {
	  
  }
  
  private HashMap<Integer,RoutePointList> m_route_point_lists = new HashMap<Integer,RoutePointList>();
  private Polygon m_polygon = new Polygon();
  private Rectangle m_bounding_box = null;
  
  private Boundary[] m_boundary = new Boundary[NB_SEGMENTS];
  private int m_selected_boundary;
  private int m_name;
  
  private double m_rising_slope_angle = 0;
  private boolean m_has_slopes = false;
  private ControlPoint m_centre = null;
  
  private class SegmentAngle extends Segment implements Comparable<SegmentAngle>
  {
    private double m_angle;

    
    public int compareTo(SegmentAngle seg)
    {
      return (AngleUtils.normalize_0_360(seg.m_angle) < 
    		  AngleUtils.normalize_0_360(m_angle)) ? 1 : -1;
    }

    public SegmentAngle(ControlPoint p1, ControlPoint p2, ControlPoint barycentre)
    {
      super(p1,p2);
      m_angle = get_angle(barycentre);
    }
  }

  public CheckpointType get_checkpoint_type() { return m_checkpoint_type; }
  public void set_checkpoint_type(CheckpointType f) { m_checkpoint_type = f; }
  public void set_checkpoint_type(int f) { m_checkpoint_type = CheckpointType.values()[f]; }
  public boolean is_hidden() { return m_visible_type == ZoneType.HIDDEN; }
  public boolean is_jump() { return m_visible_type == ZoneType.JUMP; }

  public ZoneType get_visible_type() { return  m_visible_type; }
  
  public void set_visible_type(int type)
  {
	  m_visible_type = ZoneType.values()[type];
	 
  }
  public void set_visible_type(ZoneType type)
  {
	  m_visible_type = type;

  }
  
  public ControlPoint get_extreme_point(Direction d)
  {
	  ControlPoint rval = null;
	  int min = Integer.MAX_VALUE;
	  int max = 0;

	  for (ControlPoint p : m_border_point_list)
	  {
		  switch(d)
		  {
		  case LEFT:
			  if (min > p.getX())
			  {
				  rval = p;
				  min = p.getX();
			  }
			  break;
		  case RIGHT:
			  if (max < p.getX())
			  {
				  rval = p;
				  max = p.getX();
			  }
			  break;
		  case DOWN:
			  if (max < p.getY())
			  {
				  rval = p;
				  max = p.getY();		
			  }
			  break;
		  case UP:
			  if (min > p.getY())
			  {
				  rval = p;
				  min = p.getY();
			  }
			  break;
			  
		  }
	  }
	  return rval;
  }
 /* public double [] get_angles_x()
  {
	  double [] rval = new double[NB_SEGMENTS];
	  int i = 0;
	  
	  for (Boundary b : m_boundary)
	  {
		  rval[i++] = b.get_angle_x();
	  }
	  
	  return rval;
  }*/

  public Boundary get_intersected_boundary(Point2D p1, Point2D p2)
  {
    Boundary b = null;

    for (int i = 0; i < NB_SEGMENTS && b == null;i++)
      {
        Boundary candidate = get_boundary(i);

        if (candidate.intersects_line(p1.getX(),p1.getY(),p2.getX(),p2.getY()))
        {
          b = candidate;
        }
      }
    return b;
  }
  public void update(Observable obs, Object x)
    {
      build_path(m_border_point_list);
    }

  public Boundary get_selected_boundary()
  {
    return m_selected_boundary != -1 ? m_boundary[m_selected_boundary] : null;
  }

  public int get_selected_boundary_index()
  {
    return m_selected_boundary;
  }
  public Boundary get_boundary(int i)
  {
    return m_boundary[i];
  }
  public int get_nb_fences()
  {
    int rval = 0;
    for (int i = 0; i < NB_SEGMENTS; i++)
    {
      if (get_boundary(i).is_fence())
      {
        rval++;
      }
    }

    return rval;
  }

  public String toString()
  {
    String rval = "Zone "+get_name();
    if (m_visible_type != ZoneType.NORMAL)
    {
      rval += " ("+ZONE_TYPE_STR[m_visible_type.ordinal()]+")";
    }
 
    return rval;
  }
  public boolean has_slopes()
  {
    return m_has_slopes;
  }

  public double get_rising_slope_angle()
  {
    return m_rising_slope_angle;
  }
  /**
   *
   * @param other
   * @return list of non-fence shared boundaries
   */
  public Vector<Boundary> get_shared_boundaries(Zone other)
  {
    Vector<Boundary> rval = new Vector<Boundary>();
    for (int i = 0; i < NB_SEGMENTS; i++)
    {
      Boundary b = get_boundary(i);
      if (!b.is_fence())
      {
        boolean found = false;
        for (int j = 0; j < NB_SEGMENTS && !found; j++) 
        {
          Boundary otherb = other.get_boundary(j);

          found = (!otherb.is_fence() && otherb.equalsTo(b));

          if (found) 
          {
            rval.add(b);
          }
        }
      }
    }
    return rval;
  }

  /*private class PointComparer implements Comparator<PointZoneDirection>
  {
	  public int compare(PointZoneDirection a, PointZoneDirection b)
	  {
		  return (a.get_route_index() - b.get_route_index());
	  }
  }
  
  final PointComparer POINT_COMPARER = new PointComparer();
  */
  
  /*
   * @return points of the zone boundary
   */
  public LinkedList<ControlPoint> get_border_point_list() 
  {
	  //Collections.sort(m_border_point_list,POINT_COMPARER);
	  
	  return m_border_point_list;
  }
  
  public RoutePointList get_route_point_list(int route_index)
  {
	  return m_route_point_lists.get(new Integer(route_index));
  }

  
  public Polygon get_polygon() { return m_polygon; }
  public Rectangle get_bounding_box() { return m_bounding_box; }
  
  public int get_name() { return m_name; }
  public void set_name(int name) { m_name = name; }

public boolean is_bound(ControlPoint c)
  {
    return m_border_point_list.contains(c);
  }

  public Zone(Collection<ControlPoint> c, int name)
  {
    set_name(name);

    build_path(c);
    
 
  }

  public void set_selected_boundary_type(int type,boolean oppose_slopes)
  {

    if (m_selected_boundary != -1)
      {
        int other_type = -1;
        //int old_type = m_boundary[m_selected_boundary].get_type();

        m_boundary[m_selected_boundary].set_slope_type(type);

        if (oppose_slopes)
        {
          if (type == Boundary.SLOPE_BOTTOM) {
            other_type = Boundary.SLOPE_TOP;
          }
          else if (type == Boundary.SLOPE_TOP) {
            other_type = Boundary.SLOPE_BOTTOM;
          }
        }
        if (other_type == -1)
        {
          if (type == Boundary.SLOPE_NORMAL)
            {
              int other_index = (m_selected_boundary+m_boundary.length/2) % m_boundary.length;
              m_boundary[other_index].set_slope_type(type);
            }
        }
        else
        {
          for (int i=0;i<m_boundary.length;i++)
          {
            if (i != m_selected_boundary)
            {
              if (Math.abs(i-m_selected_boundary) == m_boundary.length/2)
              {
                m_boundary[i].set_slope_type(other_type);
              }
              else
              {
                m_boundary[i].set_slope_type(Boundary.SLOPE_NORMAL);
              }
            }
          }
        }
      }
  }

  public void serialize(ParameterParser fw) throws java.io.IOException
   {
     fw.startBlockWrite("zone");
     fw.write("name",get_name());
     fw.write("type",ZONE_TYPE_STR[get_visible_type().ordinal()]);
     
     fw.write("finish_line",FINISH_TYPE_STR[get_checkpoint_type().ordinal()]);
     
     int j = 0;
     
     for (ControlPoint c : m_border_point_list)
     {
        NamedControlPoint nc = (NamedControlPoint) c;
        fw.write("p"+(j+1),nc.get_name());
        j++;
      }
      for (int i = 0; i < NB_SEGMENTS; i++)
      {
        get_boundary(i).serialize(fw);
      }

      fw.endBlockWrite();
   }
  /*private void set_gates_dimensions()
  {
	  // find the upper left point
	  NamedControlPoint upper_left_point = find_corner_point(true,true);
	  NamedControlPoint upper_right_point = find_corner_point(true,false);
	  NamedControlPoint lower_left_point = find_corner_point(false,true);
	  NamedControlPoint lower_right_point = find_corner_point(false,false);

	  upper_right_point.m_y = upper_left_point.m_y;
	  upper_right_point.m_x = upper_left_point.m_x + GATES_WIDTH * 2;
	  
	  lower_left_point.m_x = upper_left_point.m_x;
	  lower_left_point.m_y = upper_left_point.m_y + GATES_HEIGHT;
	  
	  lower_right_point.m_x = upper_right_point.m_x;
	  lower_right_point.m_y = lower_left_point.m_y;
 }*/
  
  /*private NamedControlPoint find_corner_point(boolean upper, boolean left)
  {
	  Iterator<ControlPoint> it = m_border_point_list.iterator();
	  
	  NamedControlPoint rval = (NamedControlPoint)it.next();
	  
	  for (int j = 1; j < m_border_point_list.size(); j++) 
	  {
		  NamedControlPoint cp2 = (NamedControlPoint)it.next();
		  
		  if (((cp2.m_x < rval.m_x) == upper) && 
				  ((cp2.m_y < rval.m_y) == left))
		  {
			  rval = cp2;
		  }	
	  }
	  return rval;
  }
  */
  
  /**
   * read from sc3 file
   * @param fw parameter file handler
   * @param point_list existing named points
   * @return list of points to build the zone
   */
  public Zone(ParameterParser fr, Vector<NamedControlPoint> point_list) throws IOException
  {
    Vector<ControlPoint> the_list = new Vector<ControlPoint>();

    fr.startBlockVerify("zone");
    m_name = fr.readInteger("name");
    set_visible_type(fr.readEnumerate("type",ZONE_TYPE_STR));
    
    set_checkpoint_type(fr.readEnumerate("finish_line",FINISH_TYPE_STR));

    for (int i = 0; i < NB_SEGMENTS; i++)
    {
        add_point(point_lookup_by_name(point_list, fr.readInteger("p"+(i+1))),
                  the_list);
    }

    reorder_points(the_list);
    for (int i = 0; i < NB_SEGMENTS; i++)
    {
      get_boundary(i).fill(fr);
    }
    fr.endBlockVerify();
 
 
    Boundary up = get_top_boundary();
    Boundary down = get_bottom_boundary();

    m_has_slopes = false;
    
    if ((up != null) && (down != null))
    {

    	m_has_slopes = true;

    	// compute slope angle: angle colinear to the slope
    	// to be able to get the proper pseudo-3D car view
    	ControlPoint down_centre = down.centre();
    	ControlPoint up_centre = up.centre();

    	m_rising_slope_angle = Math.toDegrees(Math.atan2
    			(up_centre.getY()-down_centre.getY(),
    					up_centre.getX()-down_centre.getX()));
    }
    
    build_path(m_border_point_list);
 
    
    m_centre = new ControlPoint();
    
    for (ControlPoint cp : m_border_point_list)
    {
    	m_centre.m_x += cp.m_x;
    	m_centre.m_y += cp.m_y;
    }
    m_centre.m_x /= m_border_point_list.size();
    m_centre.m_y /= m_border_point_list.size();
    
  }

  public ControlPoint get_centre()
  {
	  return m_centre;
  }
  
  public Boundary get_top_boundary()
  {
    Boundary rval = null;

    for (int i = 0; i < NB_SEGMENTS && rval == null; i++)
    {
      Boundary b = get_boundary(i);

      if (b.get_slope_type() == Boundary.SLOPE_TOP)
      {
        rval = b;
      }
    }

    return rval;
  }
  public Boundary get_bottom_boundary()
  {
    Boundary rval = null;

    for (int i = 0; i < NB_SEGMENTS && rval == null; i++)
    {
      Boundary b = get_boundary(i);

      if (b.get_slope_type() == Boundary.SLOPE_BOTTOM)
      {
        rval = b;
      }
    }

    return rval;
  }

  public void select_closest_boundary(int x,int y)
  {
    double min_distance = Double.MAX_VALUE;
    int min_segment = -1;

    unselect_boundaries();

    for (int i=0;i<NB_SEGMENTS;i++)
    {
      Boundary b = get_boundary(i);
      double dist = b.distance_to(x, y, false);

      if (dist < min_distance)
      {
        min_distance = dist;
        min_segment = i;
      }
    }
    if (min_segment != -1)
    {
      select_boundary(min_segment);
    }

  }

  public boolean compute_neighbour_zones(Collection<Zone> zones) 
  {
     m_neighbour_zones = new HashSet<Zone>();
     
     for (Zone nz : zones)
     {
    	 if (nz != this) 
       {
         // check if zone has shared non-fences with other
         if (!nz.get_shared_boundaries(this).isEmpty()) 
         {
           m_neighbour_zones.add(nz);
         }
       }
     }
     return !m_neighbour_zones.isEmpty();
   }
  
  public HashSet<Zone> get_neighbours()
  {
    return m_neighbour_zones;
  }
  /*
  public boolean intersects(Shape gp)
  {
	  //c.get_car_current().get_bounds()
    boolean found = false;
    Polygon this_path = get_polygon();

 
       // iterate on car bounds
       PathIterator pi = gp.getPath---Iterator(null);
       while ((!pi.isDone() && !found))
       {
         pi.currentSegment(m_coords);
         found = ((this_path.contains(m_coords[0],m_coords[1]) ||
                   (this_path.contains(m_coords[2],m_coords[3]))));
         pi.next();
       }
     

    return found;
  }*/
  
   public boolean intersects(double [] segment)
   {
     return (m_polygon.contains(segment[0],segment[1]) ||
             m_polygon.contains(segment[2],segment[3]));
   }
   public boolean contains(double [] segment)
   {
     return (m_polygon.contains(segment[0],segment[1]) &&
             m_polygon.contains(segment[2],segment[3]));
   }

   public boolean contains(int x, int y) {
     return m_polygon.contains(x, y);
   }
   public boolean contains(double x, double y) 
   {
     return m_polygon.contains(x, y);
   }
   public boolean contains(Point2D.Double p) {
	     return m_polygon.contains(p.x, p.y);
	   }

   public boolean contains(ControlPoint cp) {
     return m_polygon.contains(cp.getX(),cp.getY());
   }

	
	
  public void paint(Graphics g)
  {
    Graphics2D g2 = (Graphics2D)g;
    g.setColor(java.awt.Color.GREEN);
    g2.draw(m_polygon);
    ControlPoint centre = ControlPoint.barycentre(m_border_point_list);
    String s = ""+get_name();
    if (is_selected())
    {
      s += " *";
    }

    g2.drawString(s,centre.getX(),centre.getY());
    
   /* Stroke olds = g2.getStroke();
    
    g2.setStroke(new BasicStroke(1.0f,                      // Width
            BasicStroke.CAP_SQUARE,    // End cap
            BasicStroke.JOIN_MITER,    // Join style
            1.0f,                     // Miter limit
            new float[] {16.0f,20.0f}, // Dash pattern
            0.0f));                     // Dash phase
    

    g2.setStroke(olds);*/
    
    
  }
  public void unselect_boundaries()
  {
    m_selected_boundary = -1;
    for (int i = 0; i < m_boundary.length;i++)
    {
      m_boundary[i].unselect();
    }
  }
  public void select_boundary(int idx)
  {
    m_selected_boundary = idx;
    m_boundary[idx].select();
  }
  
  public void reset_route_points()
  {
	  m_route_point_lists.clear();
  }
  

  /*
   * add points of the route to the zone
   */
  public void add_route_points(Route r)
  {
	  Integer route_name = new Integer(r.get_name());
	  
	  RoutePointList rpl = m_route_point_lists.get(route_name);
	  
	  for (PointDirection cp : r.get_point_list())
	  {
		  Zone z = cp.get_point().get_explicit_zone();
		  
		  if (z == this)
		  {			  
			  if (rpl == null)
			  {
				  rpl = new RoutePointList();
				  m_route_point_lists.put(route_name,rpl);
			  }
			  
			  rpl.add(cp);
		  }
	  }
	  /*
	  System.out.println("zone #"+get_name()+" has "+m_route_point_lists.size()+" different routes");
	  if (rpl != null)
	  {
		  System.out.println("route #"+r.get_name()+" points contained in zone: "+rpl.size());
		  for (ControlPoint cp : rpl)
		  {
			  NamedControlPoint ncp = (NamedControlPoint)cp;
			  if (get_name() == 35)
			  {
			  System.out.println("point #"+ncp.get_name());
			  }
		  }
	  }*/
  }
private void add_point(ControlPoint p,Vector<ControlPoint> the_list)
  {
    if (!the_list.contains(p))
    {
      p.addObserver(this);
      the_list.add(p);
    }
  }
private void build_path(Collection<ControlPoint> c) 
{
    Vector<ControlPoint> the_list = new Vector<ControlPoint>();

    Iterator<ControlPoint> it = c.iterator();

    while (it.hasNext()) {
      add_point( it.next(), the_list);
    }

    reorder_points(the_list);
    
  }

private void reorder_points(Vector<ControlPoint> the_list)
  {
    ControlPoint centre = ControlPoint.barycentre(the_list);

    m_border_point_list.clear();
    m_polygon.reset();

    TreeSet<SegmentAngle> ts = new TreeSet<SegmentAngle>();

    ControlPoint first_point = the_list.elementAt(0);

    // sort angles in increasing order

    for (int i = 1; i < NB_SEGMENTS; i++)
    {
      SegmentAngle s = new SegmentAngle
          (first_point,the_list.elementAt(i),
           centre);

      ts.add(s);
    }
    // draws the path so the figure is convex

    m_polygon.addPoint(first_point.getX(),first_point.getY());

    m_border_point_list.add(first_point);

    Iterator<Zone.SegmentAngle> its = ts.iterator();
    
    for (int i = 0; i < NB_SEGMENTS-1;i++)
    {
      Segment s1 = its.next();

      ControlPoint cp = s1.get_end_point();
      m_border_point_list.add(cp);

      m_polygon.addPoint(cp.getX(),cp.getY());

    }

    Iterator<ControlPoint> it = m_border_point_list.iterator();
    ControlPoint c1 = it.next();
    ControlPoint c2 = null;
   
    for (int i = 0; i < NB_SEGMENTS;i++)
    {
    	if (!it.hasNext())
    	{
    		it = m_border_point_list.iterator();
    	}
    	
    	c2 = it.next();
    	
    	
    	Boundary b = m_boundary[i];
    	// avoid to lose boundary properties if not needed
    	if ((b == null) || ((b.get_start_point() != c1) && (b.get_end_point() != c2)))
    	{
    		m_boundary[i] = new Boundary(c1, c2);
    	}
    	
    	c1 = c2;
    }

    m_polygon.addPoint(first_point.getX(),first_point.getY());


    m_bounding_box = m_polygon.getBounds();

  }
private static NamedControlPoint point_lookup_by_name(Collection<NamedControlPoint> named_points_vector,int name)
  {
    NamedControlPoint rval = null;

    Iterator<NamedControlPoint> cp = named_points_vector.iterator();
    
    while (cp.hasNext() && rval == null)
    {
    	
      NamedControlPoint nc = cp.next();
      if (nc.get_name() == name)
      {
        rval = nc;
      }
    }
    return rval;
  }
}