package supercars3.game.shop;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.util.Vector;

import supercars3.game.*;
import supercars3.game.screens.TitleScreen2;
import supercars3.sys.Localizer;

import com.golden.gamedev.util.ImageUtil;

import supercars3.base.Equipment;

public class RepairScreen extends GameState
{
	static public final Font LETTER_FONT = Font.decode("Courier-BOLD-10");
	
	private class DamageJauge extends ClickableBox
	{
		public DamageJauge(Rectangle outline)
		{
			super(outline);
		}
		public void render(Graphics2D g)
		{		
			int border_offset = 8;
			
			int damage_value = m_equipment.get_health();
			
			// border
			
			g.setColor(Color.RED);
			g.drawRect(m_outline.x, m_outline.y, m_outline.width, m_outline.height);
			
			// bar
			
			int width = ((m_outline.width - border_offset) * damage_value)/Equipment.MAX_HEALTH;
			int x = m_outline.x + border_offset/2;
			int y = m_outline.y + border_offset/2;
			int height = m_outline.height-border_offset;
			
			int red_width = Math.min(width,m_outline.width / 6);
			
			g.fillRect(x, y, red_width, height);
			
			g.setColor(Color.BLUE);
			g.fillRect(x + red_width, y, width - red_width, height);
		}
	}
	private Vector<ClickableBox> m_trade_button_items = new Vector<ClickableBox>();
	private Vector<ClickableBox> m_repair_button_items = new Vector<ClickableBox>();
	private Vector<AccessoryBox> m_accessory_items = new Vector<AccessoryBox>();
	private Vector<RepairTextBox> m_repair_items = new Vector<RepairTextBox>();
	private boolean m_sell_mode = false;
	
	private DamageJauge m_damage_jauge;
	private TextBox m_damage_text_box;
	
	private BufferedImage m_image;
	private Rectangle m_bounds = new Rectangle(166,38,480-166,244-38);
	private AffineTransform m_translation = AffineTransform.getTranslateInstance(m_bounds.getX(),m_bounds.getY());
	private boolean m_repair_mode = true;
	private int m_mount_mode = AccessoryBox.MOUNT_NONE;
	
	private Equipment m_equipment;
	private GameState m_next_screen;
	private String m_player_name;
	
	private static final int TRADE_REPAIR = 0;
	private static final int FRONT = 1;
	private static final int REAR = 2;
	private static final int SELL = 3;
	private static final int BUY = 4;
	private static final int TRADE_QUIT = 5;

	private static final int REPAIR_QUIT = 1;

	public RepairScreen(GameState next_screen, Equipment e, String player_name)
	{
		m_equipment = e;
		m_next_screen = next_screen;
		m_player_name = player_name.toUpperCase();
	}

	private int get_clicked_box(Vector<? extends ClickableBox> items, int x, int y)
	{
		int idx = -1;
		for (int i = 0; i < items.size() && idx == -1; i++)
		{
			if (get_box(items,i).is_clicked(x,y))
			{
				idx = i;
			}
		}
		return idx;
	}
	
	protected void p_update()
	{
		if (m_game.bsInput.getMousePressed() == MouseEvent.BUTTON1)
		{
			int mx = m_game.bsInput.getMouseX() - m_bounds.x;
			int my = m_game.bsInput.getMouseY() - m_bounds.y;
			
			if (m_repair_mode)
			{
				int idx = get_clicked_box(m_repair_button_items,mx,my);
				
				switch (idx)
				{
				case TRADE_REPAIR:
					m_repair_mode = false;
					break;
				case REPAIR_QUIT:
					fadeout();	
					break;
				default:
					//idx = get_clicked_box(m_)
					break;
				}
				
				handle_repairs(mx,my);
			}
			else
			{
				int idx = get_clicked_box(m_trade_button_items,mx,my);
				switch (idx)
				
				{
				case TRADE_REPAIR:
					m_repair_mode = true;
					break;
				case FRONT:
					m_mount_mode = AccessoryBox.MOUNT_FRONT;
					set_text_color(FRONT,Color.WHITE);
					set_text_color(REAR,Color.GRAY);
					break;
				case REAR:
					m_mount_mode = AccessoryBox.MOUNT_REAR;
					set_text_color(REAR,Color.WHITE);
					set_text_color(FRONT,Color.GRAY);
					break;
				case BUY:					
					set_text_color(BUY,Color.WHITE);
					set_text_color(SELL,Color.GRAY);
					m_sell_mode = false;
					set_sell_buy();
					break;
				case SELL:
					set_text_color(BUY,Color.GRAY);
					set_text_color(SELL,Color.WHITE);
					m_sell_mode = true;
					set_sell_buy();
					break;
				case TRADE_QUIT:
					fadeout();
					break;
				default:
					handle_accessories(mx,my);
					break;
				}
			}
		}
		
		if (is_fadeout_done())
		{
			m_game.hideCursor();
			
		   	if (is_escaped())
	    	{		   		
	    		set_next(new TitleScreen2());
	    	}
	    	else
	    	{
	    		// configure equipement for rear/front weapon mounting
	    		
	    		for (int i = 0; i < m_accessory_items.size(); i++)
	    		{
	    			AccessoryBox ab = m_accessory_items.elementAt(i);
	    			if (ab.get_mounted() == AccessoryBox.MOUNT_FRONT)
	    			{
	    				m_equipment.mounted_front = Equipment.Item.values()[i];
	    			}
	    			else if (ab.get_mounted() == AccessoryBox.MOUNT_REAR)
	    			{
	    				m_equipment.mounted_rear = Equipment.Item.values()[i];
	    			}
	    			
	    		}

	    		set_next(m_next_screen);
	    	}
		}

	}
	
	private void set_text_color(int idx, Color c)
	{
		((TextBox)get_box(m_trade_button_items,idx)).set_text_color(c);
	}
	
	private void handle_repairs(int mx, int my)
	{
		int idx = get_clicked_box(m_repair_items,mx,my);
		if (idx != -1)
		{
			RepairTextBox rtb = m_repair_items.elementAt(idx);
			
			if (rtb.is_enabled() && (m_equipment.get_money() >= rtb.get_price()))
			{
				m_equipment.add_health(rtb.get_repair_power());
				m_equipment.add_money(-rtb.get_price());
				
				rtb.disable();
			}
		}
		
	}
	private void handle_accessories(int mx, int my)
	{
		int idx = get_clicked_box(m_accessory_items,mx,my);
		if (idx != -1)
		{
			AccessoryBox ab = m_accessory_items.elementAt(idx);
			if (ab.is_weapon())
			{
				// weapon: front/rear mount priority
				reset_mount_mode(ab);
				ab.handle_click(m_mount_mode); 
				m_mount_mode = AccessoryBox.MOUNT_NONE;
				set_text_color(REAR,Color.GRAY);
				set_text_color(FRONT,Color.GRAY);
			}
			else
			{
				// not a weapon
				ab.handle_click(m_mount_mode);
			}
		}
	}
	
	private void set_sell_buy()
	{
		
		for (int i = 0; i < m_accessory_items.size(); i++)
		{
			AccessoryBox ab = m_accessory_items.elementAt(i);
			ab.set_sell_mode(m_sell_mode);
		}
	}
	private void reset_mount_mode(AccessoryBox clicked)
	{
		if (m_mount_mode != AccessoryBox.MOUNT_NONE)
		{
			AccessoryBox ab_front = null;
			AccessoryBox ab_rear = null;
			
			// find front/rear accessories
			
			for (int i = 0; i < m_accessory_items.size() && 
			(ab_front == null || ab_rear == null); i++)
			{
				AccessoryBox ab = m_accessory_items.elementAt(i);
				switch(ab.get_mounted())
				{
				case AccessoryBox.MOUNT_REAR:
					ab_rear = ab;
					break;
				case AccessoryBox.MOUNT_FRONT:
					ab_front = ab;
					break;
				default:
					break;
				}
			}	
			
			clicked.set_mounted(m_mount_mode);
			
			switch (m_mount_mode)
			{
			case AccessoryBox.MOUNT_REAR:
				if (ab_rear != null)
				{
					if (ab_front == clicked)
					{
						// was in front: swap
						ab_rear.set_mounted(AccessoryBox.MOUNT_FRONT);
					}
					else
					{
						ab_rear.set_mounted(AccessoryBox.MOUNT_NONE);
					}
				}
				break;
			case AccessoryBox.MOUNT_FRONT:
				if (ab_front != null)
				{
					if (ab_rear == clicked)
					{
						// was at rear: swap
						ab_front.set_mounted(AccessoryBox.MOUNT_REAR);
					}
					else
					{
						ab_front.set_mounted(AccessoryBox.MOUNT_NONE);
					}
				}
				break;
				
			}
		}
		
	}
	
	private int add_repair_item(String part, int y, int price, int repair_power)
	{
		int h = 16;
		int w = m_bounds.width - 20;
		int x = 20;
		
		RepairTextBox tb = new RepairTextBox(Localizer.value(part),new Rectangle(x,y,w,h),price,repair_power);
		
		m_repair_items.add(tb);
		
		return y + h;
	}
	private int add_text_bottom_item(Vector<ClickableBox> list,String text, int px, boolean right_adjust, Color c)
	{
		int h = 16;
		int y = m_bounds.height - h;
		int w = text.length() * 10; // fixed font
		int x = right_adjust ? m_bounds.width - w - px : px;
		
		TextBox tb = new TextBox(new Rectangle(x,y,w,h),Localizer.value(text),c);
		list.add(tb);
		
		return x + w;
	}
	
	private int add_accessory_item(String image_name, Equipment.Item acc_id, int x, int y, boolean rotate, boolean weapon)
	{
		Equipment.Accessory acc = m_equipment.get_accessory(acc_id);

		BufferedImage image = bsLoader.getImage("sprites"+File.separator+"shop"+File.separator+image_name+".png");
		if (rotate)
		{
			image = ImageUtil.rotate(image,-90);
		}
		
		AccessoryBox cb = new AccessoryBox(x,y,acc,image,weapon);
		m_accessory_items.add(cb);
		
		return x + cb.get_outline().width + 5;
	}
	protected void p_init()
	{
		m_image = bsLoader.getImage("images"+File.separator+"repair.png");
		m_game.showCursor();
				
		int next_x;
		
		next_x = add_text_bottom_item(m_trade_button_items,"REPAIR",0,false,Color.WHITE);
		next_x = add_text_bottom_item(m_trade_button_items,"FRONT",next_x+10,false,Color.GRAY);
		next_x = add_text_bottom_item(m_trade_button_items,"REAR",next_x,false,Color.GRAY);
		next_x = add_text_bottom_item(m_trade_button_items,"SELL",next_x,false,Color.GRAY);
		next_x = add_text_bottom_item(m_trade_button_items,"BUY",next_x,false,Color.WHITE);
		
		add_text_bottom_item(m_trade_button_items,"QUIT",0,true,Color.WHITE);
		
		bsLoader.setMaskColor(Color.BLUE);
		next_x = 40;
		int y = 30;
		
		next_x = add_accessory_item("missile",Equipment.Item.FRONT_MISSILE,next_x,y,true,true);
		next_x = add_accessory_item("rear_missile",Equipment.Item.REAR_MISSILE,next_x,y,true,true);
		next_x = add_accessory_item("homer_missile",Equipment.Item.HOMER_MISSILE,next_x,y,true,true);
		next_x = add_accessory_item("super_missile",Equipment.Item.SUPER_MISSILE,next_x,y,true,true);
		y += 84;
		next_x = 10;
		next_x = add_accessory_item("mine",Equipment.Item.MINE,next_x,y,false,true);
		next_x = add_accessory_item("armour",Equipment.Item.ARMOUR,next_x,y,false,false);
		next_x = add_accessory_item("ram",Equipment.Item.RAM,next_x,y,false,false);
		next_x = add_accessory_item("engine",Equipment.Item.ENGINE,next_x,y,false,false);
		next_x = add_accessory_item("nitro",Equipment.Item.NITRO,next_x,y,false,true);
		
		m_accessory_items.elementAt(m_equipment.mounted_front.ordinal()).set_mounted(AccessoryBox.MOUNT_FRONT);
		m_accessory_items.elementAt(m_equipment.mounted_rear.ordinal()).set_mounted(AccessoryBox.MOUNT_REAR);
		
		//m_trade_items.addAll(m_accessory_items);
		
		next_x = add_text_bottom_item(m_repair_button_items,"TRADE",0,false,Color.WHITE);
		add_text_bottom_item(m_repair_button_items,"QUIT",0,true,Color.WHITE);

		int next_y = 30;
		
		int [] rep_array = m_equipment.cast_repairs();
		
		for (int i = 0; i < Equipment.REPAIR_PARTS.length; i++)
		{
			next_y = add_repair_item(Equipment.REPAIR_PARTS[i],next_y,rep_array[2*i+1],rep_array[2*i]);
		}
		Rectangle damage_box = new Rectangle(20,next_y+3,50,16);
		
		m_damage_text_box = new TextBox(damage_box,Localizer.value("DAMAGE"),Color.WHITE);
		
		int w = m_bounds.width - damage_box.width - damage_box.x;
		int x = damage_box.x + damage_box.width;
		
		y = damage_box.y ;
		
		int h = damage_box.height;
		
		m_damage_jauge = new DamageJauge(new Rectangle(x,y,w,h));
		

		
	}

	private ClickableBox get_box(Vector<? extends ClickableBox> items, int idx)
	{
		return items.elementAt(idx);
	}
	

		
	private void render_vector(Graphics2D g, Vector<? extends ClickableBox> items)
	{
		for (int i = 0; i < items.size(); i++)
		{
			get_box(items,i).render(g);
		}
	}
	protected void p_render(Graphics2D g)
	{
		g.setFont(LETTER_FONT);
		
		
		g.drawImage(m_image,0,0,null);
		g.setTransform(m_translation);

		g.setColor(Color.WHITE);
		String s = "- " + Localizer.value(m_repair_mode ? "R E P A I R" : "T R A D E") + " -";
		GfxUtils.centered_draw_string(g,s,m_bounds.getWidth()/2,5);
		
		g.drawString(m_player_name+": "+Localizer.value("currency")+m_equipment.get_money(),10,10);
		
		if (m_repair_mode)
		{
			render_vector(g,m_repair_items);
			render_vector(g,m_repair_button_items);
			m_damage_jauge.render(g);
			m_damage_text_box.render(g);
		}
		else
		{
			render_vector(g,m_accessory_items);
			render_vector(g,m_trade_button_items);
		}
		
	}

}
