package supercars3.game.players;

import com.golden.gamedev.engine.*;
import java.awt.event.*;

import supercars3.base.Equipment;
import supercars3.game.cars.*;
import joystick.Joystick;

//import supercars3.game.GameOptions;

/**
 * <p>Titre : </p>
 * <p>Description : </p>
 * <p>Copyright : Copyright (c) 2005</p>
 * <p>Société : </p>
 * @author non attribuable
 * @version 1.0
 */

public class Human extends Driver {
  private BaseInput m_input;
  private boolean m_cheat;
  private KeySet m_keys;
  private int m_buttons = 0,m_old_buttons = 0;
  private float m_old_y_pos = 0;
  
  private boolean just_pressed(int button_id)
  {
	  return ((m_buttons & button_id) == button_id) && ((m_old_buttons & button_id) == 0);
  }
  private boolean pressed(int button_id)
  {
	  return ((m_buttons & button_id) == button_id);
  }
  public Human(String name,BaseInput input, KeySet keys, boolean cheat)
  {
    super(name,true);

    m_input = input;
    m_cheat = cheat;
    m_keys = keys;
   }

  public void move(Car ca,long elapsed_time) 
  {
	  HumanCar c = (HumanCar)ca;
	  
	c.reset_command();
	
	if (m_keys.joystick != null)
	{
		// joystick test
		Joystick j = m_keys.joystick;
		float x_pos = j.getXPos();
		
		if (x_pos > 0.5)
		{
			c.right();
		}
		else if (x_pos < -0.5)
		{
			c.left();
		}
		
		m_buttons = j.getButtons();
		
		if (m_keys.up_down_fire)
		{
			float y_pos = j.getYPos();
			
			if ((y_pos > 0.5) && (m_old_y_pos < 0.5))
			{
				c.fire(c.get_equipment().mounted_rear);
			}
			else if ((y_pos < -0.5) && (m_old_y_pos > -0.5))
			{
				c.fire(c.get_equipment().mounted_front);
			}
			m_old_y_pos = y_pos;
			
			if (m_buttons != 0)
			{
				if (m_keys.fire_to_accelerate)
				{
					c.accelerate();
				}
				else
				{
					c.brake();
				}			
			}
		}
		else
		{
			if (just_pressed(Joystick.BUTTON1))
			{
				c.fire(c.get_equipment().mounted_front);
			}
			if (just_pressed(Joystick.BUTTON2))
			{
				c.fire(c.get_equipment().mounted_rear);
			}
			if (pressed(Joystick.BUTTON3))
			{
				if (m_keys.fire_to_accelerate)
				{
					c.accelerate();
				}
				else
				{
					c.brake();
				}			
			}
		}
		
		
		m_old_buttons = m_buttons;
	}
    if (m_input.isKeyDown(m_keys.left))
    {
      c.left();
    }
    else if (m_input.isKeyDown(m_keys.right))
    {
      c.right();
    }

    if (m_input.isKeyPressed(m_keys.fire_1))
    {
    	c.fire(c.get_equipment().mounted_front);
    }
    else if (m_input.isKeyPressed(m_keys.fire_2))
    {
    	c.fire(c.get_equipment().mounted_rear);    	
    }
    
    if (m_cheat)
    {
    	// shift: enables weapon keys
    	
    	if (m_input.isKeyDown(KeyEvent.VK_SHIFT))
    	{
    		if (m_input.isKeyPressed(KeyEvent.VK_N))
        	{
        		c.fire(Equipment.Item.NITRO);
        	}
           	else if (m_input.isKeyPressed(KeyEvent.VK_F))
        	{
        		c.fire(Equipment.Item.FRONT_MISSILE);
        	}
           	else if (m_input.isKeyPressed(KeyEvent.VK_R))
        	{
        		c.fire(Equipment.Item.REAR_MISSILE);
        	}
           	else if (m_input.isKeyPressed(KeyEvent.VK_S))
        	{
        		c.fire(Equipment.Item.SUPER_MISSILE);
        	}
           	else if (m_input.isKeyPressed(KeyEvent.VK_H))
        	{
        		c.fire(Equipment.Item.HOMER_MISSILE);
        	}
           	else if (m_input.isKeyPressed(KeyEvent.VK_M))
    		{
    			c.fire(Equipment.Item.MINE);
    		}
    	}

    }
    
    if (m_input.isKeyDown(m_keys.speed_button) == m_keys.fire_to_accelerate)
    {
      c.accelerate();
    }
    else
    {
      c.brake();
    }

  }
}