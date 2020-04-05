package supercars3.game;

import java.awt.*;
import java.awt.geom.*;
import java.awt.font.GlyphVector;
import java.util.StringTokenizer;

/**
 * <p>Titre : </p>
 * <p>Description : </p>
 * <p>Copyright : Copyright (c) 2005</p>
 * <p>Société : </p>
 * @author non attribuable
 * @version 1.0
 */

public abstract class GfxUtils 
{
	static public final Font TINY_FONT = Font.decode("Courier-BOLD-8");
	static public final Font SMALL_FONT = Font.decode("Courier-BOLD-14");
	
	public static Rectangle2D vertical_draw_string(Graphics2D g2d,String s,int label_x,int label_y,int offset)
	{
		int current_y = label_y;
		int w = 0;
		int h = 0;
		
		for (int i = 0; i < s.length(); i++)
		{
		   GlyphVector gv = g2d.getFont().createGlyphVector
	        (g2d.getFontRenderContext(), s.charAt(i)+"");
		    Rectangle2D r2d = gv.getLogicalBounds();
		    if (w < r2d.getWidth())
		    {
		    	w = (int)r2d.getWidth();
		    }

		    g2d.drawGlyphVector(gv, label_x, h + current_y);

		    h += (int)r2d.getHeight() + offset;
		    

		}
		
		return new Rectangle2D.Double(label_x,label_y,w,h);
	}
  public static Rectangle2D centered_draw_string(Graphics2D g2d, String s,
                                                 double label_x, double label_y) {
    return centered_draw_string(g2d, s, (int) label_x, (int) label_y);
  }

  public static Rectangle2D centered_draw_string(Graphics2D g2d, String s,
		  int label_x, int label_y) 
  {
	  GlyphVector gv = g2d.getFont().createGlyphVector
	  (g2d.getFontRenderContext(), s);

	  Rectangle2D r2d = gv.getLogicalBounds();
	  r2d.setFrame(label_x - (r2d.getWidth() / 2), label_y - (r2d.getHeight() / 2),
			  r2d.getWidth(), r2d.getHeight());

	  g2d.drawGlyphVector(gv, (int) r2d.getX(), (int) r2d.getY());

	  return r2d;
  }
  
  public static Rectangle2D draw_string(Graphics2D g2d, String s,
		  int label_x, int label_y) 
  {
	  GlyphVector gv = g2d.getFont().createGlyphVector
	  (g2d.getFontRenderContext(), s);

	  Rectangle2D r2d = gv.getLogicalBounds();
	  r2d.setFrame(label_x, label_y , r2d.getWidth(), r2d.getHeight());

	  g2d.drawGlyphVector(gv, (int) r2d.getX(), (int) r2d.getY());

	  return r2d;
  }


    public static void centered_draw_multiline_string
    (Graphics2D g2d, String s,
    		int label_x, int label_y, int y_offset)
    {
    	int y = label_y;
    	StringTokenizer tok = new StringTokenizer(s,"\n");
    	while (tok.hasMoreTokens())
    	{
    		Rectangle2D r = centered_draw_string(g2d,tok.nextToken(),label_x,y);
    		y += r.getHeight() + y_offset;
    	}
    }
 


  public static AlphaComposite make_composite(float alpha,int type)
  {
	  // enforce bounds
	  float a = ((alpha < 0.0f) ? 0.0f : ((alpha > 1.0f) ? 1.0f : alpha));
	  
    return (AlphaComposite.getInstance(type, a));
  }

}