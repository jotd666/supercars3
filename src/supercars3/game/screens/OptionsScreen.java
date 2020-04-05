package supercars3.game.screens;

import supercars3.game.SCGame;
import supercars3.game.GameState;
import supercars3.sys.Localizer;

import java.util.Vector;
import java.awt.*;
import java.awt.event.*;

import joystick.Joystick;

/**
 * <p>Titre : </p>
 * <p>Description : </p>
 * <p>Copyright : Copyright (c) 2005</p>
 * <p>Société : </p>
 * @author non attribuable
 * @version 1.0
 */

public abstract class OptionsScreen extends GameState 
{
	private final static String[] yes_no = { "Yes", "No" };
	private int m_current;
	protected Vector<Option> m_select = new Vector<Option>();
	protected boolean m_tab_pressed;
	private Rectangle m_tab_box;
	private WoodBackground m_wood;
	private float m_old_y_pos = 0;
    
	public abstract void update_options();
    	
	abstract class Option
   {
     private String m_title;
     protected String m_localized_title;
     private boolean m_localized = false;
     protected double m_y_percent;

     protected String get_title()
     {
    	 return m_localized_title;
     }
     protected Option(double y_percent)
     {
    	 this(null,y_percent, false);
     }
     
     protected Option(String title, double y_percent, boolean localized)
     {
       m_title = title;
       m_localized = localized;
       update_title();
       m_y_percent = y_percent;
     }
     public abstract void render(Graphics2D g, boolean gray_2);
     public abstract void process_input();
     
     private void update_title()
     {
         if (m_localized)
         {
      	   m_localized_title = Localizer.value(m_title);
         }
         else
         {
      	   m_localized_title = m_title;
         }
     }
     void update_localized_items()
     {
    	 update_title();
     }
   }

class NameOption extends Option
   {
     private String m_name;
     
     public String get_name()
     {
    	 return m_name;
     }
     public NameOption(String title, String name, double y_percent)
     {
       super(title,y_percent,false);
       m_name = name;
     }
     public void process_input()
     {
       int e = m_game.bsInput.getKeyPressed();

       if ((e == KeyEvent.VK_DELETE)||(e == KeyEvent.VK_BACK_SPACE))
       {
         if (m_name.length() > 0)
         {
           m_name = m_name.substring(0,m_name.length()-1);
         }
       }
       else
       {
         if (!m_game.keyPressed(KeyEvent.VK_LEFT) &&
             !m_game.keyPressed(KeyEvent.VK_RIGHT))
         {
           boolean upper = (m_game.keyDown(KeyEvent.VK_SHIFT));

           if ( (e >= ' ') && (e <= 'z')) {
             char c = (char) e;
             String s = "" + c;
             if (!upper) {
               s = s.toLowerCase();
             }
             m_name += s;
           }
         }
       }
     }
     public void render(Graphics2D g, boolean gray_2)
     {
       draw_2_strings(g,get_title(),m_name,m_y_percent,gray_2);
     }
   }

class SelectableOption extends Option
  {
    private String[] m_options;
    private Object[] m_passed_options;
    private int m_index = 0;
    private float m_old_x_pos = 0;
    
    public SelectableOption(String[] options, int index, double y_percent) 
    {
    	this(null,options,index,y_percent);
    }
    
    public SelectableOption(String title,Object[] options, int index, double y_percent) 
    {
      super(title,y_percent,true);
      
      m_options = new String[options.length];
      m_passed_options = options;
      
      localize_items();
      
      if (index < m_options.length)
      {
    	  m_index = index;
      }
      else
      {
    	  m_index = 0;
      }
    }
    
    private void localize_items()
    {
        for (int i = 0; i < m_passed_options.length; i++)
        {
      	  m_options[i] = Localizer.value(m_passed_options[i].toString(),true);
        }
    }
    void update_localized_items()
    {
    	super.update_localized_items();
    	localize_items();
    }
    public void process_input()
     {
    	float x_pos = 0;
    	if (m_joystick != null)
    	{
    		x_pos = m_joystick.getXPos();
    	}
    	
    	if ((m_game.keyPressed(KeyEvent.VK_LEFT) || ((x_pos > 0.5) && m_old_x_pos < 0.5)))
    	{
    		prev_index();
    	}
    	else if ((m_game.keyPressed(KeyEvent.VK_RIGHT) || ((x_pos < -0.5) && m_old_x_pos > -0.5)))
    	{
    		next_index();
    	}
       m_old_x_pos = x_pos;
     }
    
    protected void index_changed()
    {
    	
    }
    private void next_index() 
    {
      m_index = (m_index + 1) % m_options.length;
     	index_changed();
     	     }

    private void prev_index() 
    {
      m_index--;
      if (m_index < 0)
      {
        m_index += m_options.length;
      }
     	index_changed();
     	 
    }

    public int get_index() {
      return m_index;
    }
    
    
    public String get_option()
    {
      return m_options[m_index];
    }

    public void render(Graphics2D g, boolean gray_2)
    {
    	if (get_title() != null)
    	{
    		g.setColor(Color.WHITE);
    		draw_2_strings(g,get_title(),m_options[m_index],m_y_percent,gray_2);
    	}
    	else
    	{
    		draw_string(g, m_options[m_index],m_y_percent,gray_2);
      }
    }
    
 
  }

public class BooleanOption extends SelectableOption
{ 
	
	public BooleanOption(String title,boolean value, double y_percent)
	{
		super(title,yes_no,value ? 0 : 1,y_percent);
	}
	
	public boolean get_value()
	{
		return get_index() == 0;
	}
	
}
protected int get_selectable_option_index(int i)
{
	return ((SelectableOption)(m_select.elementAt(i))).get_index();
}

public OptionsScreen() throws Exception 
{
	set_fadeinout_time(500,500);
}

public void locale_changed()
{
	for (Option o : m_select)
	{
		o.update_localized_items();
	}
}

  protected abstract void set_next_screen();
  

  protected void p_init() 
  {  
	  m_wood = new WoodBackground(getWidth(),getHeight());
	  m_current = 0;
	  m_tab_box = SCGame.NORMAL_BITMAP_FONT.text_position(Localizer.value("next screen"), getWidth(), getHeight(), 0, false, false);
		
	  m_tab_box.x -= m_tab_box.width;
	  m_tab_box.y -= m_tab_box.height;

  }

  protected void p_update()
  {

	  float y_pos = 0;
	  int jstate = 0;
	  if (m_joystick != null)
	  {
		  y_pos = m_joystick.getYPos();
		  jstate = m_joystick.getButtons();
	  }
	  
	  if ((m_game.keyPressed(KeyEvent.VK_TAB) || (jstate & Joystick.BUTTON2) != 0)) 
	  {
		  m_tab_pressed = true;
		  update_options();
	  }
	  if (m_tab_pressed || (m_game.keyPressed(KeyEvent.VK_ENTER) || ((jstate & Joystick.BUTTON1) != 0)))
	  {
		  fadeout(); // no need to update options there because init_cars does it
	  }	  	 
	
	  Option s = m_select.elementAt(m_current);
	  
	  
	  
	  if ((m_game.keyPressed(KeyEvent.VK_DOWN) || ((y_pos > 0.5) && (m_old_y_pos < 0.5))))
	  {
		  m_current = (m_current + 1) % m_select.size();
	  }
	  else if ((m_game.keyPressed(KeyEvent.VK_UP) || ((y_pos < -0.5) && (m_old_y_pos > -0.5))))
	  {
		  m_current--;
		  if (m_current < 0) m_current += m_select.size();
	  }
	  else
	  {
		  s.process_input();
	  }
	  
	  if (is_fadeout_done())
	  {
		  if (is_escaped())
		  {
			  m_game.stop_music();
			  set_next(new JffScreen());
		  }
		  else
		  {			  
			  set_next_screen();
		  }
	  }
	  
	  m_old_y_pos = y_pos;
  }

protected void p_render(Graphics2D g) 
{
    m_wood.render(g);

    SCGame.NORMAL_BITMAP_FONT.write_line(g, Localizer.value("next screen"), 0, m_tab_box);
 
    boolean gray_2;
    long e = get_state_elapsed_time();
    
    for (int i = 0; i < m_select.size(); i++)
    {
      gray_2 = true;

      if (i == m_current) {
        // flashing
        if ( (e % 70) > 35)
        {
        	gray_2 = false;
        }
      }

      m_select.elementAt(i).render(g,gray_2);
    }

    if (e % 1000 > 500)
    {
    	draw_localized_string(g,"RETURN TO PLAY",0.90);
    }
  }

 



}