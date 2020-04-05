package supercars3.game;

import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.*;
import java.io.File;

import javax.imageio.ImageIO;

import joystick.Joystick;

import supercars3.base.DirectoryBase;
import supercars3.sys.GameFont;
import supercars3.sys.Localizer;

import com.golden.gamedev.engine.*;

/**
 * <p>Titre : </p>
 * <p>Description : </p>
 * <p>Copyright : Copyright (c) 2005</p>
 * <p>Société : </p>
 * @author non attribuable
 * @version 1.0
 */

public abstract class GameState
{
	protected final static double PIXEL_TO_RATIO = 1.2/480;

	private boolean m_fake_fadein;
	private long m_fadein_time;
	private long m_fadeout_time;
	private long m_maximum_duration;
	private long m_fadeout_start_time;
	private long m_state_elapsed;
	private long m_elapsed_time;
	private GameState m_next_state;
	private boolean m_fadeout, m_fadeout_done;
	private boolean m_escaped;
	protected SCGame m_game;
	protected BaseLoader bsLoader;
	protected Color m_background_color;
	private String m_error_message = null;
	private boolean m_error_displayed = false;
	private BufferedImage m_black;
	
	protected Joystick m_joystick = Joystick.create(0);

	protected Dimension m_dimension;

	protected abstract void p_update();
	protected abstract void p_init();
	protected abstract void p_render(Graphics2D g);

	protected static final String pad_string(String s, 
			int pad_length, boolean left)
	{
		String ps = "";

		for (int i = 0; i < pad_length - s.length(); i++)
		{
			ps += " ";
		}
		if (left)
		{
			ps += s;
		}
		else
		{
			ps = s + ps;
		}

		return ps;
	}

	protected void draw_localized_string(Graphics2D g,String s,double height_ratio)
	  {
		  draw_string(g,Localizer.value(s),height_ratio);
	  }	
	
	protected void draw_localized_string(Graphics2D g,String s,double height_ratio,boolean gray_2)
	  {
		  draw_string(g,Localizer.value(s),height_ratio,gray_2);
	  }
  /**
   * draw an horizontally centered string 
   * @param g
   * @param s
   * @param height_ratio
   */
  protected void draw_string(Graphics2D g,String s,double height_ratio)
  {
	  draw_string(g,s,height_ratio,false);
  }
  protected void draw_string(Graphics2D g,String s,double height_ratio, boolean gray)
  {
	GameFont gf = gray ? SCGame.NORMAL_BITMAP_GRAY_FONT : SCGame.NORMAL_BITMAP_FONT;
	
	gf.write_line(g, s, (int)(getWidth() / 2.0),
			(int)(getHeight() * height_ratio),0,true,false);
  }
  protected void draw_localized_string(Graphics2D g,String s,int x, int y, boolean gray)
  {
	  draw_string(g,Localizer.value(s),x,y,gray);
  }
  
  protected void draw_string(Graphics2D g,String s,int x, int y, boolean gray)
  {
	GameFont gf = gray ? SCGame.NORMAL_BITMAP_GRAY_FONT : SCGame.NORMAL_BITMAP_FONT;
	
	gf.write_line(g, s, x, y, 0, false, false);
  }
  /**
   * draw an horizontally centered string, multiline,
   * with color selection
   * @param g
   * @param s
   * @param height_ratio
   */
  protected void draw_multiline_string(Graphics2D g,String s,double height_ratio, boolean gray )
  {
	  draw_multiline_string(g, s, height_ratio, gray,0);
  }
  
  protected void draw_multiline_string(Graphics2D g,String s,double height_ratio, boolean gray, int y_offset)
  {
		GameFont gf = gray ? SCGame.NORMAL_BITMAP_GRAY_FONT : SCGame.NORMAL_BITMAP_FONT;
		
		gf.write(g, s, (int)(getWidth() / 2.0),
				(int)(getHeight() * height_ratio),0,true,false,y_offset);
  }

 /**
  * draw a pair of centered strings
  * with color selection
  * @param g
 * @param height_ratio
 * @param s
  */

  protected void draw_2_strings(Graphics2D g,String s1,String s2,double height_ratio,boolean gray_2)
  {
    Rectangle r = SCGame.NORMAL_BITMAP_FONT.write_line(g, s1, (int)(getWidth() / 2.0),
        (int)(getHeight() * height_ratio),0,true,false);
    GameFont gf2 = gray_2 ? SCGame.NORMAL_BITMAP_GRAY_FONT : SCGame.NORMAL_BITMAP_FONT;
 	  
    gf2.write(g, s2, (int)(getWidth() / 2.0),
            (int)(r.getHeight() - 2 + getHeight() * height_ratio),0,true,false,0);
  }

  protected long get_elapsed_time()
  {
    return m_elapsed_time;
  }
  protected long get_state_elapsed_time()
  {
    return m_state_elapsed;
  }
  
  protected void no_elapsed_time()
  {
	m_state_elapsed -= m_elapsed_time;  
  }
  
  protected void set_next(GameState ns)
  {
    m_next_state = ns;
  }

  public boolean is_escaped()
  {
    return m_escaped;
  }
  protected GameState() 
  {
	  m_fadein_time = 0;
	  m_fadeout_time = 0;
	  m_maximum_duration = Long.MAX_VALUE;

  }



  protected void set_fadeinout_time(long fadein_time, long fadeout_time) {
    m_fadein_time = fadein_time;
    m_fadeout_time = fadeout_time;
  }

  protected void set_maximum_duration(long maximum_duration) {
    m_maximum_duration = maximum_duration;

 }


  protected int getWidth() {
    return (int)m_dimension.getWidth();
  }

  protected int getHeight() {
    return (int)m_dimension.getHeight();
  }

 protected void fadeout() 
 {
    if (!m_fadeout)
    {
      m_fadeout_done = false;
      m_fadeout = true;
      m_fadeout_start_time = m_state_elapsed;
    }
  }

  protected boolean is_fadein()
  {
    boolean rval = (m_fadein_time >= m_state_elapsed);
    if ((!rval) && m_fake_fadein)
    {
    	rval = true;
    	m_fake_fadein = false;
    }
    
    return rval;
  }


  public boolean is_fadeout_done() {
    return m_fadeout_done;
  }



  public void init(Dimension d, SCGame g)
  {
     m_dimension = d;
     m_game = g;
     bsLoader = m_game.bsLoader;
     m_background_color = Color.BLACK;
     m_black = new BufferedImage(getWidth(),getHeight(),BufferedImage.TYPE_INT_RGB);
     
     m_elapsed_time = 0;
     m_state_elapsed = 0;
     m_fadeout = false;
     m_fadeout_done = false;
      m_escaped = false;
     m_next_state = null;
     p_init();
  }
  
  protected void create_snapshot()
  {
 	 BufferedImage snapshot_image;
 	 String suffix = "view";
 	 
 	 Dimension d = m_game.bsGraphics.getSize();
 	 snapshot_image = new BufferedImage(d.width,d.height,BufferedImage.TYPE_INT_RGB);
 	 
 	 // hack to make believe a fadein
 	
 	 m_fake_fadein = true;
 	 render((Graphics2D)snapshot_image.getGraphics());
 	
 	 
 	 save_snapshot(snapshot_image,suffix);
  }
  
  protected void save_snapshot(BufferedImage snapshot_image, String suffix)
  {
 	 // now save the image
 		
 	 try
 	 {
 		 File snapshot_dir = new File(DirectoryBase.get_root() + "snapshots");
 		 String [] filelist = snapshot_dir.list();
 		 int index = 0;
 		 if (filelist.length > 0)
 		 {
 			 String last_item = filelist[filelist.length - 1];
 			 int dotidx = last_item.lastIndexOf('.');
 			 int uscidx = last_item.lastIndexOf('_');
 			 try
 			 {
 				 String s = last_item.substring(uscidx+1,dotidx);
 				 index = Integer.parseInt(s) + 1;
 			 }
 			 catch (Exception e)
 			 {
 				 
 			 }
 		 }
 		 String idx = "0"+index;
 		 
 		 while (idx.length() < 4)
 		 {
 			 idx = "0" + idx;
 		 }
 		 
 		 ImageIO.write(snapshot_image,"png",new File(snapshot_dir.getPath()+
 				 File.separator+suffix+"_"+idx+".png"));
 	 }
 	 catch (Exception e)
 	 {
 		 
 	 }
  }
  public GameState update(long elapsed)
  {
	  m_elapsed_time = elapsed;
	  m_state_elapsed += elapsed;
	  if (m_state_elapsed > m_maximum_duration - m_fadeout_time)
	  {
		  fadeout();
	  }

	  
	  if (m_error_message == null)
	  {
		  p_update();
	  }
	  
	  if (m_game.keyPressed(KeyEvent.VK_ESCAPE))
	  {
		  m_escaped = true;
		  fadeout();
	  } 
	  else if (m_game.bsInput.isKeyPressed(KeyEvent.VK_F1))
	  {
		  create_snapshot();
	  }
	  else 
		  if (m_game.keyPressed(KeyEvent.VK_F10))
		  {
			  m_game.finish();
		  }
	  
	  // setting next state to null avoids keeping a reference of the next state
	  // triggered out of heap after a while
	  GameState s = m_next_state;
	  m_next_state = null;
	  
	  return s;
  }
  protected void show_error(String msg)
  {
    m_error_message = msg;
  }


protected void set_composite(Graphics2D g, float percent, int type)
  {
    g.setComposite(GfxUtils.make_composite(percent,type));
  }

  public void render(Graphics2D g) 
  {
    if (m_error_message == null) 
    {
    	float percent = 0f;
    	
    	if (m_fadeout) 
    	{
    		long deltat = m_state_elapsed - m_fadeout_start_time;
    		if (m_fadeout_time==0)
    		{
    			percent = 0f;
    		}
    		else
    		{
    			percent = 1 - ( deltat / (float)m_fadeout_time);
    		}
    		
    		if (percent <= 0) {
    			m_fadeout_done = true;
    			percent = 0f;
    		}
 
    		set_composite(g, 1, AlphaComposite.CLEAR);
       		g.drawImage(m_black,0,0,null);
  			set_composite(g, percent, AlphaComposite.SRC_OVER);
  			 
    	}
    	else
    	{
    		if (m_state_elapsed < m_fadein_time) 

    		{
    			// during fade-in
    			percent = (float) m_state_elapsed / m_fadein_time;
       			set_composite(g, percent, AlphaComposite.SRC_OVER);
    		}
    		
   		    		
    	}
    	
    	p_render(g);
			   
    }

    if (m_error_message != null) 
    {
      if (!m_error_displayed) 
      {
        m_error_displayed = true;
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(Color.RED);
        g.setFont(Font.decode("Arial-BOLD-14"));
        
        if (m_error_message.length() > 50)
        {
        	m_error_message = m_error_message.substring(0,50) + "\n" + m_error_message.substring(50);
        }
        m_error_message += "\n\nPress F10 to quit";
        g.setColor(Color.RED);
        GfxUtils.centered_draw_multiline_string(g, 
            m_error_message, getWidth() / 2,
            getHeight() / 2,0);
      
      }
    }
  }

}