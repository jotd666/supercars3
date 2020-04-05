package supercars3.game.trains;


import java.awt.image.*;

import supercars3.game.ObjectView;

public class WagonView extends ObjectView
{

	public WagonView(BufferedImage bi, double scale)
	{		
		// 4 frames
		super(bi,4,scale,scale,0,50,1.0);
	}
}
