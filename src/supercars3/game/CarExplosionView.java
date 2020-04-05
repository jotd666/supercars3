package supercars3.game;

import java.awt.Color;
import com.golden.gamedev.engine.*;

public class CarExplosionView extends ExplosionView
{
	public static final int NB_FRAMES = 11;
	//private static final Color transparent = new Color(0x444444);
	
	public CarExplosionView(BaseLoader bsl)
	{
		super(bsl,Color.BLUE,"car",NB_FRAMES);
	}
}
