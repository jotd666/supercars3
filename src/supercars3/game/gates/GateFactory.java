package supercars3.game.gates;

import com.golden.gamedev.engine.*;
import java.awt.*;
import java.io.*;

import supercars3.base.*;


public class GateFactory
{
	private GateView left_view;
	private GateView right_view;

	GateFactory(BaseLoader bsl)
	{
		bsl.setMaskColor(Color.BLUE);

		String imf = "sprites" + File.separator + "gate_left";
		//String desc = DirectoryBase.get_root() + imf + ".sc3";
		
		left_view = new GateView(bsl.getImage(imf + ".png"),1.0);
		
		imf = "sprites" + File.separator + "gate_right";
		//String desc = DirectoryBase.get_root() + imf + ".sc3";
		
		right_view = new GateView(bsl.getImage(imf + ".png"),1.0);
		
	}
	
	GateCouple create(Zone z)
	{
		Gate left = new Gate(left_view);
		Gate right = new Gate(right_view);
		
		return new GateCouple(z,left,right);
	}
	
}
