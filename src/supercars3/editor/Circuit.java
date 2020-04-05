package supercars3.editor;

import javax.swing.*;
import java.awt.*;

import supercars3.base.*;

import javax.imageio.*;
import java.awt.image.*;
import java.io.*;
import java.awt.event.*;
import java.util.*;

/**
 * <p>Titre : </p>
 * <p>Description : </p>
 * <p>Copyright : Copyright (c) 2005</p>
 * <p>Société : </p>
 * @author non attribuable
 * @version 1.0
 */

public class Circuit extends JPanel  implements MouseListener {
	enum ClickMode {NO_OP,ADD_CONTROL_POINT,ADD_REGION,
   ADD_ROUTE,SELECT_OBJECT,DEFINE_LAKE_SEED_P0INT,DEFINE_RESUME_POINT,
   DEFINE_CAR_START_POINT,
  ADD_POINT_TO_ROUTE,
   DEFINE_MULTI_CAR_START_POINT,
   MOVE_CONTROL_POINT_VERTICAL,
   MOVE_CONTROL_POINT_HORIZONTAL,
   MOVE_CONTROL_POINT }
  
  public static final int REGION_SELECT_TOGGLE = 1;
  public static final int ROUTE_ADDED = 2;
  public static final int POINT_REMOVED = 3;

  ClickMode m_mode = ClickMode.SELECT_OBJECT;
  int m_zone_counter = 0;
  private ControlPoint m_first_control_point;
  private Set<ControlPoint> m_point_set = new HashSet<ControlPoint>();
  private Vector<NamedControlPoint> m_route_point_vector = new Vector<NamedControlPoint>();

  private Zone m_selected_zone = null;

  public Zone get_selected_zone()
  {
    return m_selected_zone;
  }
  public class Message extends Observable
  {
    void set_data(int code)
    {
      m_code = code;
      setChanged();
    }
    int get_code()
    {
      return m_code;
    }
 

    private int m_code;
  }
  Message m_notifier;

  void toggle_zones_points_displayed()
  {
	  m_data.toggle_zones_points_displayed();
	  repaint();
  }
  void toggle_zones_segments_displayed()
  {
	  m_data.toggle_zones_segments_displayed();
	  repaint();
  }
  void toggle_displayed_route(int r)
	{
	  m_data.toggle_filtered_route(r);
		repaint();
	}
	
  ClickMode get_mode()
  {
    return m_mode;
  }
  void set_mode(ClickMode mode)
  {
    m_zone_counter = 0;
    unselect_all();
    m_mode = mode;
    m_point_set.clear();
    m_route_point_vector.clear();
    m_first_control_point = null;
  }
  BufferedImage m_main_image = null;
  
  int m_width = -1;
  int m_height = -1;
  CircuitData m_data = null;


  void delete_selection()
  {
    m_data.delete_selected_objects();
    repaint();
  }

  Route add_route(Vector<NamedControlPoint> points)
  {
	  return m_data.add_route(points);
  }

  boolean add_zone(Collection<ControlPoint> points)
  {
    select_zone(m_data.add_zone(points));
    return true;
  }

  NamedControlPoint add_control_point(int x,int y)
  {
    // don't add a point too close to another

	  NamedControlPoint rval = null;
	  
    if (point_lookup(x,y,ControlPoint.RADIUS*2) == null)

    {
    	rval = m_data.add_point(new ControlPoint(x,y));
    }

    return rval;
  }

 
  boolean del_control_point(int x,int y)
  {
    ControlPoint to_remove = point_lookup(x,y,ControlPoint.RADIUS);
    if (to_remove != null)
    {    	
      m_data.remove_point(to_remove);
      m_notifier.set_data(POINT_REMOVED);
      m_notifier.notifyObservers();
    }
    return to_remove != null;
  }
  void unselect_all_points()
  {
    m_data.unselect_all_points();
    repaint();
  }
  public void unselect_all()
  {
    m_data.unselect_all_points();
    m_data.unselect_all_zones();
    repaint();
  }

  /**
   * get closest segment to a point
   * @param x
   * @param y
   * @return null if no segment matches
   */
  /*
  Segment segment_lookup(int x,int y)
  {
    int sz = m_data.get_segment_list_size();
    Segment rval = null;

    double dist,min_dist = Double.MAX_VALUE;

    for (int i=0;i<sz;i++)
    {
      Segment s = m_data.get_segment(i);
      dist = s.distance_to(x,y);
      if (dist < min_dist)
      {
        min_dist = dist;
        rval = s;
      }
    }

    return rval;
  }
      */

     NamedControlPoint point_lookup(int x, int y, int radius) 
     {
       NamedControlPoint rval = null;

       int s = m_data.size();
       int min_dist = Integer.MAX_VALUE;
       int square_dist = radius * radius;

       ControlPoint pretendant = new ControlPoint(x, y);

       for (int i = 0; i < s; i++) 
       {
         NamedControlPoint p = m_data.get_point(i);
         if (m_data.check_showed(p))
         {
        	 int sd = p.square_distance(pretendant);

        	 if ( (sd < square_dist) && (sd < min_dist)) {
        		 min_dist = sd;
        		 rval = p;
        	 }
         }
       }

       return rval;
  }

  java.util.Collection<Zone> get_zones_containing(int x,int y)
  {
    return m_data.locate(x,y);
  }
  void load_main_image() throws IOException
  {

    m_main_image = ImageIO.read(new File(m_data.get_main_image_file()));
	
    setPreferredSize(new Dimension(m_main_image.getWidth(), m_main_image.getHeight()));

    revalidate();
    repaint();

   }

  
  class GrayPixel
  {
	  public GrayPixel(int intensity,int x, int y)
	  {
		  this.intensity = intensity;
		  this.x = x;
		  this.y = y;
		  this.shadow = false;
		  this.marked = false;
	  }
	  public int intensity;
	  public boolean shadow;
	  public boolean marked;
	  int x,y;
  }
  
  public void generate_shadows() throws IOException
  {
	  File image_file = m_data.get_shadow_image_file();
	  
	  if (m_main_image != null)
	  {
		  int image_width = m_main_image.getWidth();
		  int image_height = m_main_image.getHeight();
		  
		  GrayPixel [][] gray_pixels = new GrayPixel[2+image_width][2+image_height];
	
		  // first, locate the gray pixels of the image
		  
			HashSet<GrayPixel> to_process = new HashSet<GrayPixel>();

			for (int x = 0; x < image_width; x++) 
			{
				for (int y = 0; y < image_height; y++) 
				{
					int rgb = m_main_image.getRGB(x, y);
					int red = (rgb & 0xFF0000) >> 16;
					int green = (rgb & 0x00FF00) >> 8;
					int blue = rgb & 0x0000FF;
					
					if ((red == green) && (green == blue))
					{
						// this is a gray pixel: create object
						
						GrayPixel gp = new GrayPixel(red,x,y);
						gray_pixels[x+1][y+1] = gp;
						to_process.add(gp);
					}
				}
			}

			// then, use forest fire-like algorithm to detect shadow pixels
			
			while (to_process.size() > 0)
			{
				HashSet<GrayPixel> to_add = new HashSet<GrayPixel>();			
				Iterator<GrayPixel> it = to_process.iterator();
				while (it.hasNext())
				{
					GrayPixel gp = it.next();
	
						for (int xoff = 0; xoff < 3; xoff++)
						{
							for (int yoff = 0; yoff < 3; yoff++)
							{
								if ((xoff == 1) || (yoff == 1))
								{
									if (gp.shadow)
									{
										gp.marked = true;
									}
									GrayPixel neighbor = gray_pixels[gp.x + xoff][gp.y + yoff];
									if (neighbor != null)
									{
										// a gray neighbour exists
										
										if ((gp.intensity < 60) && (neighbor.intensity/2 == gp.intensity))
										{
											// gp is a shadow of neighbor: mark it as a shadow
											// I had to set a limitation for the intensity or else
											// the flood propagates everywhere
											
											gp.shadow = true;
											gp.marked = true;
										}
										else
										{
											if ((gp.shadow)&&(!neighbor.marked)&&
													(neighbor.intensity == gp.intensity))
											{
												neighbor.shadow = true;									
												to_add.add(neighbor);
											}
										}
									}
								}
							
						}
					}				
				}
				to_process = to_add;
				
			}
			
			
			// finally, build a 2-color image with shadows
			
			BufferedImage bis = new BufferedImage(image_width, image_height, BufferedImage.TYPE_BYTE_BINARY);
			
			Graphics g = bis.getGraphics();
			g.setColor(Color.BLACK);
			g.fillRect(0,0,bis.getWidth(),bis.getHeight());
			
			for (int x = 0; x < image_width; x++) 
			{
				for (int y = 0; y < image_height; y++) 
				{

					GrayPixel gpi = gray_pixels[x+1][y+1];
					if ((gpi != null) && (gpi.shadow))
					{
						bis.setRGB(gpi.x,gpi.y,0x00FFFFFF);
					}
				}
			}
			
			// now save the image
			
			ImageIO.write(bis,"png",image_file);
			
	  }
  }
  public Circuit(CircuitData data, Observer obs) {
    super();
    m_data = data;
    addMouseListener(this);
    m_notifier = new Message();
    m_notifier.addObserver(obs);

  }






    public void paintComponent(Graphics graphics)
    {
 //     super.paintComponent(graphics);

      if (m_main_image != null) 
      {
        graphics.drawImage(m_main_image, 0, 0, null);

        m_data.paint_points(graphics);
        m_data.paint_zones(graphics);
        m_data.paint_routes(graphics);
      }
  }

  //Handle mouse events.
   public void mouseReleased(MouseEvent e) {


   }
   public void mousePressed(MouseEvent e)
   {
     ControlPoint cp;
     NamedControlPoint ncp;
     Object argument = null;
     
     switch (e.getButton()) {
       case MouseEvent.BUTTON1: {
         switch (m_mode) 
         {
         case DEFINE_LAKE_SEED_P0INT:
        	 cp = point_lookup(e.getX(), e.getY(),
        			 ControlPoint.RADIUS);
        	 if (cp != null)
        	 {
        		 m_data.set_lake_seed_point(cp,!cp.is_lake_seed_point());
        		 repaint();
        	 }
        	 break;
         case DEFINE_RESUME_POINT:
             cp = point_lookup(e.getX(), e.getY(),
                               ControlPoint.RADIUS);
             if ((cp != null) &&
                 (!m_data.get_routes_containing(cp).isEmpty()))
             {
               m_data.set_resume_point(cp,!cp.is_resume_point());
               repaint();
             }
             break;
         case DEFINE_MULTI_CAR_START_POINT:
             cp = point_lookup(e.getX(), e.getY(),
                     ControlPoint.RADIUS);
             
             if (cp != null)
             {
            	 cp.select();
            	 
            	 if (m_first_control_point == null)
            	 {
            		 m_first_control_point = cp;
            	 }
            	 else
            	 {
            		 String answer = JOptionPane.showInputDialog("Number of car start points ( >= 2)", 
            				 new Integer(10));
            		 
            		 if (answer != null)
            		 {
            			 int nb_car_start_points = 0;
            			 
            			 
            			 try 
            			 {
            				 nb_car_start_points = Integer.parseInt(answer);
            			 } 
            			 catch (NumberFormatException e1) 
            			 {
            				
            			 }
            			 
            			 if (nb_car_start_points >= 2)
            			 {
            				 cp.select();
            				 repaint();
            				 m_data.create_car_start_points(m_first_control_point,cp,nb_car_start_points);
            				 
            				 unselect_all_points();
            				 set_mode(ClickMode.NO_OP);
            			 }
            		 }
            	 }
            	 
                 repaint();
            }
        	 break;
         case DEFINE_CAR_START_POINT:
             cp = point_lookup(e.getX(), e.getY(),
                               ControlPoint.RADIUS);
             if ((cp != null) &&
                 (!m_data.get_routes_containing(cp).isEmpty()))
             {
               m_data.set_car_start_point(cp,!cp.is_car_start_point());
               repaint();
             }
             break;
           case ADD_POINT_TO_ROUTE:
        	   // first, get the closest segment on all routes
         	   double smallest_ratio = 0;
        	   DistanceSegment closest_segment = null;
        	   Route selected_route = null;
        	   
        	   for (Route r : m_data.get_showed_route_list())
        	   {

        		   DistanceSegment s = r.closest_segment(e.getX(),e.getY(),false);

        		   double ratio = 
        			   s.projection(e.getX(),e.getY());

        		   if ((ratio > 0.0) && (ratio < 1.0))
        		   {
        			   if ((closest_segment == null) || (closest_segment.distance > s.distance))
        			   {
        				   closest_segment = s;
        				   smallest_ratio = ratio;
        				   selected_route = r;
        			   }
        		   }

        	   }
        	   
       		 
        	   if ((selected_route != null) && (closest_segment != null))
        	   {
            	   cp = closest_segment.point(smallest_ratio);
            	   ncp = add_control_point(cp.getX(),cp.getY());
            	   if (ncp != null)
            	   {
            		   Collection<Zone> zs = m_data.locate(cp.getX(),cp.getY());
            		   if (zs.size() > 1)
            		   {
            			   resolve_ambiguous_zone(zs,ncp,e.getX(),e.getY());
            		   }
            				  
            		   selected_route.insert_point(ncp,closest_segment.get_start_point(),
            				   closest_segment.get_end_point());            	   
            		   repaint();
            	   }
               }
               break;
             case ADD_CONTROL_POINT:
             cp = add_control_point(e.getX(), e.getY());
 
             if (cp != null)
             {
               repaint();
             }
             break;
             case MOVE_CONTROL_POINT:
             case MOVE_CONTROL_POINT_HORIZONTAL:
             case MOVE_CONTROL_POINT_VERTICAL:
            	 
             if (m_first_control_point == null) 
             {
               m_first_control_point = point_lookup(e.getX(), e.getY(),
                   ControlPoint.RADIUS);
               if (m_first_control_point != null) 
               {
                 m_first_control_point.select();
               }
             }
             else 
             {
            	 int x = e.getX();
            	 int y = e.getY();
            	 
            	 if (m_mode == ClickMode.MOVE_CONTROL_POINT_VERTICAL)
            	 {
            		 // fix x
            		 
            		 x = m_first_control_point.getX();
            	 }
               	 if (m_mode == ClickMode.MOVE_CONTROL_POINT_HORIZONTAL)
            	 {
               		 // fix y
               		 
            		 y = m_first_control_point.getY();
            	 }
            	 
            	 Zone fz = m_first_control_point.get_explicit_zone();
            	 
                 // second part: move the point
            	 
            	 Collection<Zone> zs = m_data.move_point(m_first_control_point,x,y);
            	 
            	 if (zs != null)
            	 {
            		 switch (zs.size())
            		 {
            		 case 0:
            			 // impossible
            			 
            			 break;
            		 case 1:
            			 // one zone: no ambiguity
            			 m_first_control_point.set_forced_zone(null,false);
            			 break;
            		 default:
            			 if (fz == null)
            			 {
            				 // was not ambiguous, now it is
            				 resolve_ambiguous_zone(zs,m_first_control_point,e.getX(),e.getY());
            			 }
   
            			 break;
            		 }
            	 }
            	 m_first_control_point.unselect();
            	 m_first_control_point = null;
            	 
            	 
             }
             repaint();

             break;
             case ADD_ROUTE:
             ncp = point_lookup(e.getX(), e.getY(),
                              ControlPoint.RADIUS);

             if (ncp != null) 
             {
               ncp.toggle_selection();
               if (ncp.is_selected()) 
               {
            	   if (!m_route_point_vector.contains(ncp))
            	   {
            		   m_route_point_vector.add(ncp);
            	   }


            	   Collection<Zone> zs = m_data.locate(e.getX(), e.getY());

            	   switch (zs.size()) 
            	   {
            	   case 0:
            		   ncp.toggle_selection(); // illegal: not within a zone
            		   m_route_point_vector.remove(ncp);
            		   break;
            	   case 1:
            		   break; // ok
            	   default:
            		   resolve_ambiguous_zone(zs,ncp,e.getX(),e.getY());
            	   break;
            	   }

               }
               else 
               {
            	   // end of path if same point gets deselected
            	   ControlPoint fcp = m_route_point_vector.firstElement();
            	   if (fcp.equalsTo(ncp))
            	   {

            		   if (m_route_point_vector.size() >= Route.MIN_NB_POINTS)
            		   {
            			   argument = add_route(m_route_point_vector);
            			   // we just defined a new route
            			   m_notifier.set_data(ROUTE_ADDED); 
            		   }
            	   }

            	   unselect_all();
            	   m_route_point_vector.clear();


               }
               repaint();
             }
             break;

           case ADD_REGION:
             cp = point_lookup(e.getX(), e.getY(),
                              ControlPoint.RADIUS);

             if (cp != null) {
               cp.toggle_selection();
               if (cp.is_selected()) {
                 m_point_set.add(cp);
                 if (m_point_set.size() == Zone.NB_SEGMENTS) 
                 {
                   add_zone(m_point_set);
                   // we just defined a region
                   unselect_all();
                   m_point_set.clear();
                 }
               }
               else {
                 m_point_set.remove(cp);
               }
             }

             repaint();
             break;
           case SELECT_OBJECT:
             select_object(e.getX(), e.getY());
             repaint();

               break;
         }
       }
       break;

       default:
       {
         // right or middle mouse button
         switch (m_mode) {
           case ADD_CONTROL_POINT:
             if (del_control_point(e.getX(), e.getY()))
             {
               repaint();
             }
             break;
         }
       }
       break;
    }

    m_notifier.notifyObservers(argument);

   }
   
   private void resolve_ambiguous_zone(Collection<Zone> zs, ControlPoint cp,
		   int x, int y)
   {
	   JPopupMenu zone_popup = new JPopupMenu();
       zone_popup.add(new JLabel("Zone selection"));
       zone_popup.add(new JSeparator(JSeparator.HORIZONTAL));

       for (Zone z : zs)
       {
         JMenuItem menu_item = new JMenuItem(z.toString());
         menu_item.addActionListener
         (new
      		   zone_selected_actionAdapter(cp,z));
         zone_popup.add(menu_item);
       }
       zone_popup.show(this, x,y);
   }
private void select_object(int x, int y)
   {
     // priority: 1) control point, 2) route, 3) zone
     ControlPoint cp = point_lookup(x, y, ControlPoint.RADIUS);
     if (cp != null) 
     {
       cp.toggle_selection();
     }
     else {
      Route rf = null;

       for (Route r : m_data.get_showed_route_list()) 
       {
         if (r.closest_segment(x,y,true) != null)
         {
           rf = r;
           break;
         }

       }
       if (rf != null)
       {
         rf.toggle_selection();
       }
       else if (m_data.are_zones_displayed())
       {
    	   // check if clicked in a zone, and toggle selection
    	   // of this zone

    	   Collection<Zone> c = m_data.locate(x, y);

    	   if (!c.isEmpty()) {
    		   if (c.size() == 1) {
    			   select_zone( c.iterator().next());
    		   }
    		   else {
    			   JPopupMenu zone_popup = new JPopupMenu();
    			   zone_popup.add(new JLabel("Zone selection"));
    			   zone_popup.add(new JSeparator(JSeparator.HORIZONTAL));

    			   for (Zone z : c)
    			   {

    				   JMenuItem menu_item = new JMenuItem(z.toString());
    				   menu_item.addActionListener(new
    						   zone_edited_actionAdapter(z));
    				   zone_popup.add(menu_item);
    			   }
    			   zone_popup.show(this, x, y);
    		   }
    	   }

    	   m_notifier.set_data(REGION_SELECT_TOGGLE);
       }
     }
   }

   private void select_zone(Zone zone) 
   {
	   if (zone != null)
	   {
		   zone.toggle_selection();

		   m_selected_zone = zone.is_selected() ? zone : null;
	   }
     m_notifier.set_data(REGION_SELECT_TOGGLE);
     m_notifier.notifyObservers();
}

   public void mouseEntered(MouseEvent e){}
   public void mouseExited(MouseEvent e){}
   public void mouseClicked(MouseEvent e){}


   class zone_edited_actionAdapter implements java.awt.event.ActionListener {
     Zone zone;

     zone_edited_actionAdapter(Zone z) {
       this.zone = z;
     }
     public void actionPerformed(ActionEvent e) {
       select_zone(zone);
     }
   }

   class zone_selected_actionAdapter implements java.awt.event.ActionListener {
     Zone zone;
     ControlPoint point;
     zone_selected_actionAdapter(ControlPoint c, Zone z) {
       this.zone = z;
       this.point = c;
     }
     public void actionPerformed(ActionEvent e) 
     {
       point.set_forced_zone(zone,true);
     }
   }

}