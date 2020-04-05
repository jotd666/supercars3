package supercars3.game;

import java.awt.Color;
import com.golden.gamedev.engine.*;

public class MissileExplosionView extends ExplosionView
{
	public static final int NB_FRAMES = 6;
	
	public MissileExplosionView(BaseLoader bsl)
	{
		super(bsl,Color.BLUE,"missile",NB_FRAMES,0.4);
	}
}
