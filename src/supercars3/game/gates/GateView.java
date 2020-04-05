package supercars3.game.gates;


import supercars3.game.ObjectView;
import java.awt.image.*;

public class GateView extends ObjectView
{
	
	public GateView(BufferedImage bi, double scale)
	{		
		super(bi, 1, scale, scale,0, 0,1.0); 

//		m_centre_offset.setLocation(bi.getWidth() * scale / 2,
	//			bi.getHeight() * scale / 2);

	}
}
