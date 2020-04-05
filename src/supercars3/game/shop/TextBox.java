package supercars3.game.shop;

import java.awt.*;
import supercars3.game.GfxUtils;


public class TextBox extends ClickableBox
{	
	private String m_text;
	private Color m_color;
	
	public TextBox(Rectangle outline, String text, Color c)
	{
		super(outline);
		m_text = text;
		m_color = c;
	}

	public void set_text_string(String s)
	{
		m_text = s;
	}
	
	public void set_text_color(Color c)
	{
		m_color = c;
	}
	
	public void render(Graphics2D g2d)
	{
		g2d.setColor(Color.RED);
		
		g2d.drawRect(m_outline.x,m_outline.y,
				m_outline.width,m_outline.height);
		
		g2d.setColor(m_color);
		
		GfxUtils.centered_draw_string(g2d,m_text,m_outline.x+m_outline.width/2,
				m_outline.y+m_outline.height);
	}

}
