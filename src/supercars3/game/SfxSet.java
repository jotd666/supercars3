package supercars3.game;

import supercars3.base.DirectoryBase;
import supercars3.sys.WavSample;
import supercars3.sys.WavSound;

import java.io.File;

public class SfxSet
{
	private final static String sound_dir = DirectoryBase.get_sound_path() + "game" + File.separator;
	
	public enum Sound { explosion, braking, fire, jump, land, bounce_wall, mine_drop, car_collide,
		horn_low, horn_high_long }
	
	public SfxSet(boolean active, double volume)
	{
		if (active)
		{
			int nb_sounds = Sound.values().length;
			
			sound_array = new WavSound[nb_sounds];
			
			for (Sound s : Sound.values())
			{
				sound_array[s.ordinal()] = new WavSample(sound_dir + s.toString(), volume);
			}
		}
	}
	
	public void dispose()
	{
		if (sound_array != null)
		{
			for (WavSound ws : sound_array)
			{
				ws.end();
			}
			sound_array = null;
		}		
	}
	
	public void play(Sound s)
	{
		if (sound_array != null)
		{
			sound_array[s.ordinal()].play();
		}
	}
	
	private WavSound [] sound_array = null;
	

}
