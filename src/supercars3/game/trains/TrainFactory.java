package supercars3.game.trains;

import com.golden.gamedev.engine.*;
import java.awt.*;
import java.io.*;

import supercars3.base.*;
import supercars3.game.CircuitChecker;

public class TrainFactory
{
	private String [] view_names = {"engine","carriage","wagon"};
	private WagonView [] view = new WagonView[view_names.length];
	private OpponentProperties opponents;

	TrainFactory(BaseLoader bsl,OpponentProperties opp)
	{
		opponents = opp;
		
		bsl.setMaskColor(Color.BLUE);
		
		for (int i = 0; i < view.length; i++)
		{
			String imf = "sprites" + File.separator + "train_" + view_names[i];
			//String desc = DirectoryBase.get_root() + imf + ".sc3";

			view[i] = new WagonView(bsl.getImage(imf + ".png"),1.0);

		}
		
	}
	
	Train create(Zone start_zone, Zone end_zone,CircuitChecker cc)
	{
		return new Train(view,
				opponents,start_zone,end_zone,cc); 
		}
	
}
