package supercars3.editor;

import java.awt.*;
import java.util.*;

import supercars3.base.*;

import java.awt.geom.*;
import java.awt.font.GlyphVector;
import java.awt.event.*;

/**
 * <p>Titre : </p>
 * <p>Description : </p>
 * <p>Copyright : Copyright (c) 2005</p>
 * <p>Société : </p>
 * @author non attribuable
 * @version 1.0
 */

public class SegmentSelector extends Canvas
{
  private Zone m_zone = null;
  private AffineTransform m_transform = null,m_inverse_transform = null;
  private Polygon m_polygon;
  private double m_margin_ratio = 0.8;
  private Point2D.Double m_clicked = new Point2D.Double();
  private Point2D.Double m_clicked_transformed = new Point2D.Double();

  public class ObservableSegment extends Observable
  {
    private Boundary m_boundary = null;
    public Boundary get_boundary() { return m_boundary; }

    public void set_selected_boundary(Boundary b)
    {
      if (m_boundary != b)
      {
        m_boundary = b;
        setChanged();
        notifyObservers();
      }
    }
  }
  private ObservableSegment m_observable = new ObservableSegment();

  public SegmentSelector()
  {
    addMouseListener(new mouseAdapter(this));
  }

  public void set_observer(Observer obs)
  {
      m_observable.addObserver(obs);
  }

  public void set_zone(Zone z) 
  {
    m_zone = z;
    // compute centered path for this zone
    Polygon orig = m_zone.get_polygon();
    
    Polygon p = new Polygon();
    Rectangle r = orig.getBounds();
    double ty = this.getHeight() / r.getHeight() * m_margin_ratio;
    double tx = this.getWidth() / r.getWidth() * m_margin_ratio;
    
    for (int i = 0; i < orig.npoints; i++)
    {
    	p.addPoint((int)(orig.xpoints[i]*tx), (int)(orig.ypoints[i]*ty));
    }
    AffineTransform at = AffineTransform.getScaleInstance(tx, ty);

    r = p.getBounds();
    double dx = (this.getWidth() - r.getWidth()) / 2 - r.getX();
    double dy = (this.getHeight() - r.getHeight()) / 2 - r.getY();
    m_transform = AffineTransform.getTranslateInstance(dx, dy);

    
    p.translate((int)dx,(int)dy);
 
    
    m_transform.concatenate(at);
    try
    {
      m_inverse_transform = m_transform.createInverse();
    }
    catch (Exception e)
    {
      // cannot happen with our transforms
    }
    // create transformed shape once for all

    m_polygon = p;

  }



  static public void centered_draw_string(Graphics2D g2d, String s,
                                          int label_x,int label_y) {
    GlyphVector gv = g2d.getFont().createGlyphVector
        (g2d.getFontRenderContext(), s);

    Rectangle2D r2d = gv.getLogicalBounds();

    g2d.drawGlyphVector(gv, label_x - (int) (r2d.getWidth() / 2),
                        label_y - (int) (r2d.getHeight() / 2));
}

  public void paint(Graphics g) 
  {
    Graphics2D g2d = (Graphics2D)g;
    g2d.setColor(Color.WHITE);
    g2d.fillRect(0,0,getWidth(),getHeight());
    if (m_zone != null)
    {
      int new_x, new_y,x=0,y=0;
      
      // we don't use the draw facility because we need a custom draw

      for (int i = 0; i < m_polygon.npoints; i++)
      {
          new_x = m_polygon.xpoints[i];
          new_y = m_polygon.ypoints[i];
         
          if (i > 0)
          {
            int label_x = (new_x + x)/2;
            int label_y = (new_y + y)/2;
            Boundary b = m_zone.get_boundary(i-1);

            String s = "";


            switch (b.get_slope_type())
            {
            case Boundary.SLOPE_NORMAL:
            	break;
            case Boundary.SLOPE_TOP:
            	s = "top";
            	break;
            case Boundary.SLOPE_BOTTOM:
            	s = "bottom";
            	break;
            }

            s += " "+i;

            if (b.is_fence())
            {
            	s += " (fence)";
            }
         
            g2d.setColor(b.is_selected() ? Color.RED : Color.BLACK);
            g2d.drawLine(x, y, new_x, new_y);
 
            centered_draw_string(g2d,s,label_x,label_y);

          }
          x = new_x;
          y = new_y;
         
      }
   
    }
  }

  void mousePressed(MouseEvent e) 
  {
 
    m_clicked.setLocation(e.getX(),e.getY());
    // warp in the circuit coordinates to check with segment objects
    m_inverse_transform.transform(m_clicked,m_clicked_transformed);

    m_zone.select_closest_boundary((int)m_clicked_transformed.getX(),
                                  (int)m_clicked_transformed.getY());

      m_observable.set_selected_boundary(m_zone.get_selected_boundary());
      m_observable.notifyObservers();
      repaint();
  }

  class mouseAdapter extends java.awt.event.MouseAdapter {
    SegmentSelector adaptee;

    mouseAdapter(SegmentSelector adaptee) {
      this.adaptee = adaptee;
    }
    public void mousePressed(MouseEvent e) {
      adaptee.mousePressed(e);
    }
  }

}