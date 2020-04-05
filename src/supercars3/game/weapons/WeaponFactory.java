package supercars3.game.weapons;

import com.golden.gamedev.engine.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

import supercars3.base.*;
import supercars3.game.MissileExplosionView;
import supercars3.game.cars.Car;
import supercars3.sys.ParameterParser;

public class WeaponFactory
{
	private WeaponView [] view = new WeaponView[Equipment.NB_WEAPONS];
	private MissileExplosionView explosion;
	public WeaponFactory(BaseLoader bsl) throws IOException
	{
		bsl.setMaskColor(Color.BLUE);
		
		explosion = new MissileExplosionView(bsl);
		 
		build_view(Equipment.Item.FRONT_MISSILE,"missile",bsl);
		build_view(Equipment.Item.REAR_MISSILE,"missile",bsl);
		build_view(Equipment.Item.HOMER_MISSILE,"homer_missile",bsl);
		build_view(Equipment.Item.SUPER_MISSILE,"super_missile",bsl);
		build_view(Equipment.Item.MINE,"mine",bsl);		
	}
	
	public Weapon create(Equipment.Item id, Car launcher)
	{
		Weapon w = null;
		if (id != Equipment.Item.NO_WEAPON)
		{
			WeaponView wv = view[id.ordinal()];

			switch (id)
			{
			case MINE:			
				w = new Mine(launcher,wv,explosion);
				break;
			case SUPER_MISSILE:			
				w = new SuperMissile(launcher,wv,explosion);
				break;
			case FRONT_MISSILE:			
				w = new FrontMissile(launcher,wv,explosion);
				break;
			case REAR_MISSILE:			
				w = new RearMissile(launcher,wv,explosion);
				break;
			case HOMER_MISSILE:			
				w = new HomerMissile(launcher,wv,explosion);
				break;
			}
		}
		
		return w;
	}
	private void build_view(Equipment.Item id, String file,BaseLoader bsl) throws IOException
	{
		String imf = "sprites" + File.separator + file;
		String desc = DirectoryBase.get_root() + imf + ".sc3";
		ParameterParser fr = ParameterParser.open(desc);
		fr.startBlockVerify("WEAPON_DESCRIPTOR");
		int nb_animation_frames = fr.readInteger("nb_animation_frames");
		int nb_frames = fr.readInteger("nb_rotation_frames");
		int h_offset = fr.readInteger("h_offset");
		int v_offset = fr.readInteger("v_offset");
		fr.endBlockVerify();
		fr.close();
			
		BufferedImage [] bi = new BufferedImage[nb_animation_frames];
		
		if (nb_animation_frames == 1)
		{
			bi[0] = bsl.getImage(imf + ".png");
		}
		else
		{
			for (int i = 0; i < nb_animation_frames; i++)
			{
				bi[i] = bsl.getImage(imf + "_"+(i+1)+".png");
			}
		}
		view[id.ordinal()] = new WeaponView(bi,nb_frames,
				h_offset,v_offset);
		
	}
}
