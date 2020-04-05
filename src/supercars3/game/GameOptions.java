package supercars3.game;

import java.io.File;

import supercars3.base.DirectoryBase;
import supercars3.sys.Localizer;
import supercars3.sys.ParameterParser;

public class GameOptions
{
	public static final int NO_TEST = 0; 
	public static final int SHOP_TEST = 1;
	public static final int SPECS_TEST = 2;
	public static final int COMM_SCREEN_TEST = 3;
	public static final int DEMO_TEST = 4;

	
	public final static String[] CONTROL_METHOD = { "control method 1", "control method 2" };
	
	public enum SoundSelection { sound_effects, no_engine, no_sound }
	public enum MusicSelection { full_music, music_in_menus, music_in_game, no_music }
	

	
	public final static String[] FRAME_RATE_SELECTION = { "10", "20", "30", "50", "Unlimited" };
	private final static int[] FRAME_RATE_VALUES = { 10, 20, 30, 50, 1000 };
	
	public final static String[] COMM_SCREENS = { "Yes", "No comms screens" };
	
	public enum PowerSlide { Minimal, Normal, Abusive }
	
	public final static int COMM_SCREEN_YES = 0;
	public final static int COMM_SCREEN_NO = 0;
	
	public enum CarMoves { normal,players,players_1_cpu,one_cpu_only,cpu_only }
	
	public enum MaxFps { TEN, TWENTY, THIRTY, FIFTY, UNLIMITED }
		
	public final static int CONTROL_METHOD_KEYBOARD = 0;

	public String[] LANGUAGES;
	static public final String[] PLAYER_CHOICE = { "One","Two" };
	static public final String[] FIRE_CHOICE = { "Fire to accelerate","Fire to brake" };
	
	public static final int CAR = 0;
	public static final int CONTROLS = 1;

	private boolean cheat_mode = false;	
	public boolean with_intro = true;
	public boolean full_screen = false;
	public boolean debug_mode = false;
	public boolean freeze_cpu_cars = false;
	
	public boolean colored_ennemies = true;
	
	public String comm_screen_test;
	
	/* state */
	class Player
	{
		String name;
		int fire_mode = 0;
		
		void serialize(ParameterParser fw) throws java.io.IOException
		{
			fw.startBlockWrite("player");
			fw.write("name", name);
			fw.write("fire_mode", fire_mode);
			fw.endBlockWrite();
		}
		void load(ParameterParser fr) throws java.io.IOException
		{
			fr.startBlockVerify("player");
			name = fr.readString("name");
			fire_mode = fr.readInteger("fire_mode");
			fr.endBlockVerify();
		}
	}
	private Player[] player = new Player[PLAYER_CHOICE.length];
	public int test_mode = NO_TEST;
	
	private MusicSelection music_mode = MusicSelection.full_music;
	private SoundSelection sfx_mode = SoundSelection.sound_effects;
	
	private int car_type = 0;
	private int current_language_index = 0;
	private int difficulty_level = 0;
	private int nb_players = 0;
	private int start_circuit = 0;
	private CarMoves car_moves = CarMoves.normal;
	private int comm_screens = COMM_SCREEN_YES;
	private PowerSlide power_slide = PowerSlide.Minimal;
	private MaxFps max_fps = MaxFps.UNLIMITED;
	private int m_control_method = 0;
	
	private boolean comm_screens_always = false;
	
	private boolean player_boost = false;
	
	public ReadOnly read_only = new ReadOnly();
	
	private static final GameOptions m_instance = new GameOptions();

	public class ReadOnly
	{
		public int total_nb_cars = 10;
		public double damage_speed_threshold = 0.05;
		public double resampling_ratio = 1.5;
		public double linear_speed = 0.57;
		public double car_acceleration_power = 0.0006;
		public double car_brake_power = 0.0005;
		public double front_missile_acceleration_power = 0.0005;
		public double rear_missile_acceleration_power = 0.0001;
		public double speed_loss_coefficient_ground = 0.9995;
		public double speed_loss_coefficient_air = 0.9999;
		public double front_missile_engine_value = 4.2;
		public double rear_missile_engine_value = 3.0;

		public int carvcar_damage = 0x08;
		public int carvwall_damage	= 0x10;
		public int carexpl_damage = 0x100;
		public int carmissile_damage = 0x180;
		public int carram_damage =	0x180;
		public int gate_damage	= 0x180;
		public int train_damage = 0x200;
		public int car_power = 10000;
		public double bounce_angle_safety = 30;
		public double bounce_factor = 0.75;
		
		// car behaviour
		
		public double power_slide_threshold = 5;
		public double human_angular_speed_min = 0.04;
		public double human_angular_speed_max = 0.05; // reduced from 0.06 in 0.6
		public long speed_mix_time_min = 50;
		public long speed_mix_time_max = 250;
		public double damp_factor_max = 0.00001;
		public double damp_factor_min = 0.00008;
	
		public double car_collision_self_damp = 0.3;
		public double car_collision_speed_transfer = 0.2;
		public int nb_jam_avoid_attemps = 5;
		public int lock_aim_time = 500;
		public double cpu_angular_speed = 0.5;
		public int max_same_car_bounce_front = 40;
		public int max_same_car_bounce_rear = max_same_car_bounce_front+20;
		public int aim_change_square_distance = 500;
	}
	
	public boolean set_max_fps(int m)
	{
		boolean rval = max_fps != MaxFps.values()[m];
		
		max_fps = MaxFps.values()[m];
		
		return rval;
	}
	
	public int get_max_fps_value()
	{
		return FRAME_RATE_VALUES[max_fps.ordinal()];
	}
	public MaxFps get_max_fps()
	{
		return max_fps;
	}

	public void set_comm_screens_always(boolean csa)
	{
		comm_screens_always = csa;
	}
	
	public boolean get_comm_screens_always()
	{
		return comm_screens_always;
	}
	
	public static final GameOptions instance()
	{
		return m_instance;
	}

	public int get_comm_screens()
	{
		return comm_screens;
	}
	
	public void set_comm_screens(int cs)
	{
		comm_screens = cs;
	}
	public int get_car_type()
	{
		return car_type;
	}
	public void set_car_type(int car_type)
	{
		this.car_type = car_type;
	}
	
	public boolean get_cheat_mode()
	{
		return cheat_mode;
	}
	
	public void set_player_name(int i,String name)
	{
		player[i].name = name;
		update_cheat_mode();

	}
	
	public int get_fire_mode(int player_index)
	{
		return player[player_index].fire_mode;
	}
	
	public void set_fire_mode(int player_index, int fire_mode)
	{
		player[player_index].fire_mode = fire_mode;
	}
	
	private void update_cheat_mode()
	{
		cheat_mode = player[0].name.equals("Wonderland") && player[1].name.equals("The Seer");
	}
	public String get_player_name(int i)
	{
		return player[i].name;
	}

	protected GameOptions()
	{
		File localedir = new File(DirectoryBase.get_root() + "locale");
		LANGUAGES = localedir.list();
		for (int i = 0; i < LANGUAGES.length; i++)
		{
			LANGUAGES[i] = LANGUAGES[i].split("\\.")[0];
		}
		for (int i = 0; i < player.length; i++)
		{
			player[i] = new Player();
		}
		load_settings();
	}

	public int get_language()
	{
		return current_language_index;
	}
	public void set_language(int language_index)
	{
		current_language_index = language_index; 
		Localizer.set_language(LANGUAGES[language_index]);	
	}
	
	public int find_index(String s, String [] sl)
	{
		int idx = 0;
		boolean found = false;
		
		for (int i = 0; i < sl.length && (!found); i++)
		{
			if (sl[i].equals(s))
			{
				found = true;
				idx = i;
			}
		}
		return idx;
	}
	public void save()
	{
		try
		{
			ParameterParser fr = ParameterParser.create(DirectoryBase.get_user_path()+".sc3_settings");
			fr.startBlockWrite("SC3_SETTINGS");
			
			player[0].serialize(fr);
			player[1].serialize(fr);
			
			fr.write("language",current_language_index);
			
			fr.write("music", music_mode);
			
			fr.write("sfx",sfx_mode);
			
			fr.write("car_type", car_type);
	
			fr.write("difficulty_level",difficulty_level);
			
			fr.write("nb_players",nb_players + 1);
			
			fr.write("start_circuit",start_circuit + 1);
			
			fr.write("car_moves",car_moves.ordinal());
			
			fr.write("player_boost",player_boost);
			
			fr.write("comm_screens", comm_screens);
			
			fr.write("comm_screen_always",comm_screens_always);

			fr.write("colored_ennemies",colored_ennemies);
			
			fr.write("power_slide", get_power_slide().ordinal());
			
			fr.write("max_fps", max_fps.ordinal());
			
			fr.endBlockWrite();
			fr.close();
		}
		catch (Exception e)
		{
			
		}		
		

	}


	private void load_settings()
	{
		try
		{
			ParameterParser fr = ParameterParser.open(DirectoryBase.get_user_path()+".sc3_settings");
			fr.startBlockVerify("SC3_SETTINGS");
			
			player[0].load(fr);
			player[1].load(fr);
			
			update_cheat_mode();
	
			set_language(fr.readInteger("language"));

			music_mode = MusicSelection.valueOf(fr.readString("music"));
			
			sfx_mode = SoundSelection.valueOf(fr.readString("sfx"));
			
			car_type = fr.readInteger("car_type");
			
			difficulty_level = fr.readInteger("difficulty_level");
			
			nb_players = fr.readInteger("nb_players") - 1;
			
			start_circuit = fr.readInteger("start_circuit") - 1;
					
			car_moves = CarMoves.values()[fr.readInteger("car_moves")];
			
			player_boost = fr.readBoolean("player_boost");
			
			comm_screens = fr.readInteger("comm_screens");

			comm_screens_always = fr.readBoolean("comm_screen_always");
	
			colored_ennemies = fr.readBoolean("colored_ennemies");

			set_power_slide(fr.readInteger("power_slide"));
			
			set_max_fps(fr.readInteger("max_fps"));
			
			fr.endBlockVerify();
			fr.close();
		}
		catch (Exception e)
		{
			
		}
		
		// default values for player names
		
	    for (int i = 0; i < player.length; i++)
	    {
	      if (player[i].name == null)
	      {
	        player[i].name = Localizer.value("player")+" "+(i+1);
	      }
	    }
	}

	public SoundSelection get_sfx_mode()
	{
		return sfx_mode;
	}
	
	public MusicSelection get_music_mode()
	{
		return music_mode;
	}

	public boolean set_sfx_mode(SoundSelection sfx_mode)
	{
		boolean rval = (this.sfx_mode != sfx_mode);
		
		this.sfx_mode = sfx_mode;
		
		return rval;
	}
	
	public boolean set_music_mode(int music_mode)
	{
		return set_music_mode(MusicSelection.values()[music_mode]);
	}
	public boolean set_sfx_mode(int sfx_mode)
	{
		return set_sfx_mode(SoundSelection.values()[sfx_mode]);
	}
	
	public boolean set_music_mode(MusicSelection music_mode)
	{
		boolean rval = (this.music_mode != music_mode);
		
		this.music_mode = music_mode;
		
		return rval;
	}

	public int get_difficulty_level()
	{
		return difficulty_level;
	}

	public void set_difficulty_level(int difficulty_level)
	{
		this.difficulty_level = difficulty_level;
	}

	public int get_nb_players()
	{
		return nb_players;
	}

	public void set_nb_players(int nb_players)
	{
		this.nb_players = nb_players;
	}

	public int get_start_circuit()
	{
		return start_circuit;
	}

	public void set_start_circuit(int start_circuit)
	{
		this.start_circuit = start_circuit;
	}

	public void set_control_method(int cm)
	{
		m_control_method = cm;
	}
	public int get_control_method()
	{
		return m_control_method;
	}
	public GameOptions.PowerSlide get_power_slide()
	{
		return power_slide;
	}
	
	public void set_power_slide(int ps)
	{
		this.power_slide = GameOptions.PowerSlide.values()[ps];
	}
	public GameOptions.CarMoves get_car_moves()
	{
		return car_moves;
	}

	public void set_car_moves(int car_moves)
	{
		this.car_moves = GameOptions.CarMoves.values()[car_moves];
	}

    public boolean get_player_boost()
    {
    	update_cheat_mode();
    	
    	return cheat_mode && player_boost;
    }
    
    public void set_player_boost(boolean b)
    {
    	player_boost = b;
    }
}
