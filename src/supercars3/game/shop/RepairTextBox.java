package supercars3.game.shop;

import java.awt.*;

import supercars3.sys.Localizer;

public class RepairTextBox extends TextBox
{
	private int m_repair_power;
	private int m_price;
	private boolean m_enabled = true;
	private String m_item;
	
	boolean is_enabled()
	{
		return m_enabled;
	}
	
	void disable()
	{
		m_enabled = false;
		m_price = 0;
		m_repair_power = 0;
	}
	
	int get_repair_power()
	{
		return m_repair_power;
	}
	
	int get_price()
	{
		return m_price;
	}
	
	public RepairTextBox(String item, Rectangle r, 
			int price, int repair_power)
	{
		super(r,"",Color.WHITE);
		m_item = item + " "+ Localizer.value("currency");
		m_price = price;
		m_repair_power = repair_power;
		
	}

	public void render(Graphics2D g2d)
	{
		super.set_text_string(m_item + m_price);
		super.render(g2d);
	}

}
