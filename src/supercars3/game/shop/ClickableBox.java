package supercars3.game.shop;

import java.awt.*;


public abstract class ClickableBox
{
	protected Rectangle m_outline;

	public Rectangle get_outline()
	{
		return m_outline;
	}
	public boolean is_clicked(int x, int y)
	{
		return m_outline.contains(x,y);
	}
	
	public ClickableBox(Rectangle outline)
	{
		m_outline = outline;
	}

	public abstract void render(Graphics2D g);
	
}
