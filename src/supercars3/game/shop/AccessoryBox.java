package supercars3.game.shop;

import java.awt.*;
import java.awt.image.BufferedImage;

import supercars3.game.GfxUtils;
import supercars3.sys.Localizer;
import supercars3.base.Equipment;

public class AccessoryBox extends ClickableBox
{
	private Equipment.Accessory m_accessory;
	private BufferedImage m_image;
	private int m_image_x;
	private int m_image_y;
	private boolean m_sell_mode = false;
	private boolean m_weapon = false;
	
	public final static int MOUNT_NONE = 0;
	public final static int MOUNT_FRONT = 1;
	public final static int MOUNT_REAR = 2;
	
	private int m_mounted = MOUNT_NONE;

	public void set_sell_mode(boolean s)
	{
		m_sell_mode = s;
	}
	
	public void set_mounted(int m)
	{
		if (m_weapon)
		{
			m_mounted = m;
		}
	}
	
	public boolean is_weapon()
	{
		return m_weapon;
	}
	public int get_mounted()
	{
		return m_mounted;
	}
	
	public void handle_click(int m)
	{
		if (m == MOUNT_NONE)
		{
			if (m_sell_mode)
			{
				m_accessory.sell();
			}
			else
			{
				m_accessory.buy();
			}
		}
		else
		{
			set_mounted(m);
		}
	}
	
	public AccessoryBox(int x, int y, 
			Equipment.Accessory acc,BufferedImage image, boolean weapon)
	{
		super(new Rectangle(x,y,image.getWidth()+20,image.getHeight()+30));
		m_accessory = acc;
		m_image_x = (m_outline.width - image.getWidth())/2 + m_outline.x;
		m_image_y = (m_outline.height - image.getHeight())/2 + m_outline.y;
		m_image = image;
		m_weapon = weapon;
	}

	public void render(Graphics2D g2d)
	{
		g2d.setColor(Color.RED);
		
		g2d.drawLine(m_outline.x,m_outline.y+m_outline.height,
				m_outline.x+m_outline.width,m_outline.y+m_outline.height);

		switch (m_mounted)
		{
		case MOUNT_NONE:
		
		g2d.drawLine(m_outline.x,m_outline.y,
				m_outline.x,m_outline.height+m_outline.y);
			break;
		case MOUNT_FRONT:
			GfxUtils.vertical_draw_string(g2d,Localizer.value("FRONT"),m_outline.x,m_outline.y,-4);
			break;
		case MOUNT_REAR:
			GfxUtils.vertical_draw_string(g2d,Localizer.value("REAR"),m_outline.x,m_outline.y,-4);
			break;
		}
		g2d.drawLine(m_outline.x+m_outline.width,m_outline.y,
				m_outline.x+m_outline.width,m_outline.height+m_outline.y);

		int price = m_sell_mode ? m_accessory.get_sell_price() : m_accessory.get_buy_price();
		
		int x_centre = m_outline.x+m_outline.width/2;
		
		GfxUtils.centered_draw_string(g2d,m_accessory.get_name(),x_centre,
				m_outline.y-2);

		g2d.setColor(Color.WHITE);

		GfxUtils.centered_draw_string(g2d,Localizer.value("currency")+price,x_centre,
				m_outline.y+10);
		GfxUtils.centered_draw_string(g2d,""+(int)(m_accessory.get_count()),
				x_centre,
				m_outline.y+m_outline.height);
		

		g2d.drawImage(m_image,m_image_x, m_image_y,null);

	
	}

}
