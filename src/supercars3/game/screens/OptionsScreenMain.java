package supercars3.game.screens;

import supercars3.base.*;
import supercars3.game.*;
import supercars3.game.cars.*;
import supercars3.game.players.*;
import supercars3.game.weapons.WeaponFactory;

import supercars3.sys.*;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import joystick.Joystick;

/**
 * <p>Titre : </p>
 * <p>Description : </p>
 * <p>Copyright : Copyright (c) 2005</p>
 * <p>Société : </p>
 * @author non attribuable
 * @version 1.0
 */

public class OptionsScreenMain extends OptionsScreen 
{
	private class CommScreenEntry
	{
		public CommScreenEntry(int circuit_index, int player_index, String n)
		{
			this.circuit_index = circuit_index;
			this.player_index = player_index;
			name = n;
		}
		public int circuit_index;
		public int player_index;
		public String name;
	}
	
	private static final int CIRCUIT = 0;
	private static final int PLAYER = 1;
	private static final int PLAYER_1_NAME = 2;
	private static final int FIRE_MODE_1 = 3;
	private static final int PLAYER_2_NAME = 4;
	private static final int FIRE_MODE_2 = 5;
	private static final int COMM_SCREENS = 6;
	
	private static final Color LIGHT_BLUE = new Color(0x1010D0);
	private static final Color PALE_GREEN = new Color(80,136,72);
	private static final Color PALE_PINK = new Color(172,132,120);
	private static final Color BLUE_GREEN = new Color(116,144,120);
	private static final Color PALE_XXX = new Color(132,120,172);
	
	private static final Color [] CPU_CAR_COLORS = { LIGHT_BLUE, 
		Color.MAGENTA, 
		BLUE_GREEN, 
		PALE_GREEN,
		Color.CYAN, 
		PALE_PINK, 
		Color.WHITE, 
		Color.ORANGE, 
		Color.PINK,
		PALE_XXX};
	
	private static final int TOTAL_MAX_NB_CARS = GameOptions.instance().read_only.total_nb_cars;
	private int m_total_nb_cars;
	private int m_circuit;
	private Levels m_levels;
	private CourseRecords m_course_records = null;
	private WeaponFactory m_weapon_factory;
	private Car [] m_player_cars;
	private KeySet [] m_key_set;
	private boolean m_first_render;
	private RandomInGameMusic m_random_music = null;
	private SfxSet m_sfx_set = null;
	
	private CarSet m_car;
				
	private CircuitDirectory m_demo_circuit_set =  null;
	
	private OptionsScreenMore m_options_screen_more;
	
	protected boolean m_resume_music = true;
	
	private Driver [] m_driver;
	
	// 2 players
	RandomList<CommScreenEntry> m_comm_screen_sequence;

private static final int [] player_pos = { 150, 230 };

public void set_demo_level(CircuitDirectory demo_circuit_set)
{
	m_demo_circuit_set = demo_circuit_set;
}

public OptionsScreenMain() throws Exception 
{
	GameOptions go = GameOptions.instance();
	
	m_options_screen_more = new OptionsScreenMore();
	
	m_levels = new Levels();
	
    set_fadeinout_time(200, 500);
    
	if (m_course_records == null)
	{
	  m_course_records = new CourseRecords(m_levels);
	 }
	  
	  String dl = "DIFFICULTY LEVEL";
	  
    m_select.add(new SelectableOption(dl,m_levels.get_names(),go.get_difficulty_level(),40*PIXEL_TO_RATIO));
    m_select.add(new SelectableOption("NUMBER OF PLAYERS",GameOptions.PLAYER_CHOICE,go.get_nb_players(),
    		96*PIXEL_TO_RATIO));
    
    
    for (int i = 0; i < GameOptions.PLAYER_CHOICE.length; i++)
    {
      m_select.add(new NameOption(Localizer.value("PLAYER")+" "+Localizer.value(GameOptions.PLAYER_CHOICE[i]).toUpperCase(),
    		  go.get_player_name(i),player_pos[i] * PIXEL_TO_RATIO));
      m_select.add(new SelectableOption(GameOptions.FIRE_CHOICE,go.get_fire_mode(i),
    		  (player_pos[i]+50) * PIXEL_TO_RATIO));
    }
    
    m_select.add(new SelectableOption("COMMUNICATION SCREENS",GameOptions.COMM_SCREENS,go.get_comm_screens(),
            310 * PIXEL_TO_RATIO));
  }

public void save_records()
  {
	  if (m_course_records != null)
	  {
		  try
		  {
			  m_course_records.save();
		  }
		  catch (Exception e)
		  {
		  }
		  
	  }
  }
protected void set_next_screen()
{
	
	if (m_tab_pressed)
	{
		m_tab_pressed = false;
		m_options_screen_more.set_previous_screen(this);
		//m_options_screen_more.locale_changed();

		set_next(m_options_screen_more);		
	}
	else
	{
		init_cars();
		set_next(new MainGame(this,false));
	}
}

public RandomInGameMusic get_random_music()
{
	return m_random_music;
}

public SfxSet get_sfx_set()
{
	return m_sfx_set;
}

public RecordTime get_record_time()
{
	return m_course_records.lookup_time(get_circuit(),get_circuit_set().name);
	
}
public RecordScore get_record_score()
{
	  return get_record_score(get_circuit_set().name);
	
}
public RecordScore get_record_score(String circuit_name)
{
	  return m_course_records.lookup_score(circuit_name);
	
}

public CourseRecords get_course_records()
{
	return m_course_records;
}
public Levels get_levels()
{
	return m_levels;
}
  
  public CircuitDirectory get_circuit_set()
  {
	  CircuitDirectory rval = m_demo_circuit_set;
	  if (rval == null)
	  {
		  int idx = ((SelectableOption)(m_select.elementAt(CIRCUIT))).get_index();
		  rval = m_levels.get_level(idx);
	  }
	  return rval;
  }

public Car [] get_player_cars()
  {
	  return m_player_cars;
  }

public String get_comm_screen(int player_index)
{
	String rval = null;
	
	if (m_comm_screen_sequence != null)
	{
		Collection<CommScreenEntry> vcs = m_comm_screen_sequence.get_contents();
		Iterator<CommScreenEntry> it = vcs.iterator();
		while (it.hasNext() && rval == null)
		{
			CommScreenEntry cse = it.next();
			if ((cse.circuit_index == m_circuit) && (cse.player_index == player_index))
			{
				rval = cse.name;
			}
		}
	}
	
	return rval;
}

public boolean next_circuit()
{
	boolean rval = (m_circuit >= get_circuit_set().nb_circuits);
	
	if (!rval)
	{
		m_circuit++;
		compute_car_speeds(false);
	}
	
	return rval;
}

private void compute_car_speeds(boolean init)
{
  	CircuitDirectory current_level = get_circuit_set();
  	double circuit_max_cpu_engine = current_level.get_cpu_engine(m_circuit) * 0.8;
  	
  	m_car.compute_car_speeds(circuit_max_cpu_engine,current_level.initial_human_engine,init);
}
   public int get_circuit()
   {
     return m_circuit;
   }

  public CarSet get_car_set()
  {
	  return m_car;
  }

  protected void p_init() {

	  super.p_init();

	  m_first_render = true;
	  // load weapon images

	  try
	  {
		  m_weapon_factory = new WeaponFactory(bsLoader);
	  }
	  catch (Exception e)
	  {
		  show_error(e.getMessage());
	  }
  }

  protected void p_render(Graphics2D g)
  {
	  if (m_first_render && m_resume_music)
	  {
		  update_music();
		  locale_changed();
		  m_first_render = false;
	  }

	  super.p_render(g);

	  draw_localized_string(g,"GAME OPTIONS",9*PIXEL_TO_RATIO);



  }



  private int get_nb_players()
  {
	  return get_selectable_option_index(PLAYER)+1;
  }

  void update_music()
  {
	  m_game.load_music("supercars22");
  }

  void init_comm_screens()
  {
	  GameOptions go = GameOptions.instance();
	  
	  m_comm_screen_sequence = null;
	  
	  if (go.get_comm_screens() == GameOptions.COMM_SCREEN_YES)
	  {
		  // initialize comm screens

		  File f = new File(DirectoryBase.get_comm_screen_root());
		  File [] cs_dirs = f.listFiles();
		  if (cs_dirs != null)
		  {
			  LinkedList<String> dir_list = new LinkedList<String>();
			  
			  for (File s : cs_dirs)
			  {
				  if (s.isDirectory())
				  {
					  dir_list.add(s.getName());
				  }
			  }
			  
			  // number of communication screens: 3 for 7 circuits, 4 for 10 circuits
			  // minus 1 because no comm screen at the end of last circuit!
			  
			  int nb_circuits_comm_screens = get_circuit_set().nb_circuits - 1;
			  
			  Vector<Integer> circuit_indexes = new Vector<Integer>(nb_circuits_comm_screens);
			  
			  int nb_comm_screens = go.get_comm_screens_always() ? Math.min(dir_list.size(),nb_circuits_comm_screens) : 
				  (nb_circuits_comm_screens / 2);
			  
			  for (int i = 0; i < nb_circuits_comm_screens; i++)
			  {
				 circuit_indexes.add(new Integer(i+2));
			  }
			  
			  
			  Vector<CommScreenEntry> vcs = new Vector<CommScreenEntry>();

			  for (int pi = 0; pi < get_nb_players(); pi++)
			  {
				  Vector<Integer> randomized_circuit_indexes = new RandomList<Integer>(circuit_indexes,nb_comm_screens).get_contents();
				  
				  Vector<String> randomized_dirs = new RandomList<String>(dir_list).get_contents();

				  for (int j = 0; j < nb_comm_screens; j++)
				  {
					  int index = randomized_circuit_indexes.elementAt(j).intValue();
					  
					  vcs.add(new CommScreenEntry(index,pi+1,randomized_dirs.elementAt(j)));					
				  }
			  }
			  m_comm_screen_sequence = new RandomList<CommScreenEntry>(vcs);
		  }
	  }
  }
  
  public void update_options()
  {
	  GameOptions go = GameOptions.instance();
	  
	  go.set_player_name(0,  ( (NameOption)(m_select.elementAt(PLAYER_1_NAME))).get_name());
	  go.set_player_name(1,  ( (NameOption)(m_select.elementAt(PLAYER_2_NAME))).get_name());

	  go.set_difficulty_level( get_selectable_option_index(CIRCUIT));
	  go.set_nb_players(get_selectable_option_index(PLAYER));

	  go.set_fire_mode(0,get_selectable_option_index(FIRE_MODE_1));
	  go.set_fire_mode(1,get_selectable_option_index(FIRE_MODE_2));

	  go.set_comm_screens(get_selectable_option_index(COMM_SCREENS));
	  
  }
  // game main initialization routine
  
  void init_cars()
  {
	  m_resume_music = true;

	  m_options_screen_more.update_options();

	  update_options();
	  
	  GameOptions.CarMoves car_moves = GameOptions.CarMoves.normal;
	  int nb_human_players = get_nb_players();
	  int nb_cpu_players = TOTAL_MAX_NB_CARS - nb_human_players;

	  init_comm_screens();

	  int start_circuit;
	  
	  GameOptions go = GameOptions.instance();
	  
	  if (go.get_cheat_mode())
	  {
		  int nb_circuits = get_circuit_set().nb_circuits;
		  start_circuit = go.get_start_circuit();

		  // avoid that cheatmode crashes the game

		  if (start_circuit > nb_circuits)
		  {
			  start_circuit = nb_circuits;
		  }

		  start_circuit++;

		  car_moves = go.get_car_moves();
	  }
	  else
	  {
		  start_circuit = 1;
	  }

	  switch(car_moves)
	  {
	  case normal:
		  break;
	  case one_cpu_only:
		  nb_human_players = 0;
		  nb_cpu_players = 1;
		  break;
	  case players:
		  nb_cpu_players = 0;
		  break;
	  case players_1_cpu:
		  nb_cpu_players = 1;
		  break;
	  case cpu_only:
		  nb_human_players = 0;
		  nb_cpu_players = TOTAL_MAX_NB_CARS;
		  break;
	  }

	  go.save();

	  // get rid of the old remaining sound playing threads if any
	  
	  if (m_sfx_set != null)
	  {
		  m_sfx_set.dispose();
	  }
	  
	  m_sfx_set = null;
	  
	  switch (go.get_sfx_mode())
	  {
	  case no_engine:
	  case sound_effects:
		  m_sfx_set = new SfxSet(true, 0.5);
		  break;
	  default:
		  m_sfx_set = new SfxSet(false, 0.0);
	  break;
	  }
	  
	  if (m_random_music != null)
	  {
		  m_random_music.stop();
	  }
	  
	  switch (go.get_music_mode())
	  {
	  case full_music:
	  case music_in_game:
		  if (m_random_music == null)
		  {
			  m_random_music = new RandomInGameMusic();
		  }
		  break;
	  default:
		  m_random_music = null;
	  break;
	  }
	  try
	  {
		  init_race(nb_human_players,start_circuit,nb_cpu_players);
	  }

	catch (IOException e)
	{
		show_error(e.toString());
	}
  }
  	public void no_sfx()
  	{
  		if (m_sfx_set != null)
  		{
  			m_sfx_set.dispose();
  		}
  		
  		m_sfx_set = new SfxSet(false, 0.0);
  	}
  	
	public void init_race(int nb_human_players,int start_circuit,int nb_cpu_players) throws IOException
	{
		m_total_nb_cars = nb_cpu_players + nb_human_players;
		GameOptions go = GameOptions.instance();
		
		CircuitDirectory current_level = get_circuit_set();
		
		m_circuit = start_circuit;
		m_driver = new Driver[m_total_nb_cars];
		m_car = new CarSet(m_total_nb_cars,get_sfx_set());

		CarExplosionView ev = new CarExplosionView(bsLoader);
		CarShadowView csv = null;

		Vector<String> car_dir = DirectoryBase.get_cars();
		String car_name = car_dir.elementAt(m_options_screen_more.get_car());

		m_player_cars = null;
		
		if (nb_human_players > 0)
		{
			double initial_human_engine = current_level.initial_human_engine;
			m_player_cars = new Car[nb_human_players];

			m_key_set = new KeySet[2];
			m_key_set[0] = new KeySet();
			m_key_set[0].left = KeyEvent.VK_LEFT;
			m_key_set[0].right = KeyEvent.VK_RIGHT;
			m_key_set[0].fire_1 = KeyEvent.VK_UP;
			m_key_set[0].fire_2 = KeyEvent.VK_DOWN;
			m_key_set[0].fire_to_accelerate = go.get_fire_mode(0) == 0;
			m_key_set[0].speed_button = KeyEvent.VK_SPACE;
			m_key_set[0].joystick = Joystick.create(0);
			m_key_set[0].up_down_fire = go.get_control_method() == 0;
			
			m_key_set[1] = new KeySet();
			m_key_set[1].left = KeyEvent.VK_NUMPAD4;
			m_key_set[1].right = KeyEvent.VK_NUMPAD6;
			m_key_set[1].fire_1 = KeyEvent.VK_NUMPAD8;
			m_key_set[1].fire_2 = KeyEvent.VK_NUMPAD2;
			m_key_set[1].fire_to_accelerate = go.get_fire_mode(1) == 0;
			m_key_set[1].speed_button = KeyEvent.VK_NUMPAD0;
			m_key_set[1].joystick = null; //Joystick.create(1);
			m_key_set[1].up_down_fire = m_key_set[0].up_down_fire;

			for (int i = 0; i < get_nb_players(); i++)
			{
				CarBodyView cv = new CarBodyView(bsLoader, car_dir.elementAt(go.get_car_type()), 
						i == 0 ? Color.RED : Color.GREEN);

				if (csv == null)
				{
					csv = new CarShadowView(cv);
				}

				m_driver[i] = new Human(GameOptions.instance().get_player_name(i),m_game.bsInput,
						m_key_set[i],
						GameOptions.instance().get_cheat_mode());
				Car car = new HumanCar(cv, csv, ev, m_weapon_factory, m_driver[i],i+1); // will be reverted
				
				// don't set engine if cheat has been set
				if (!GameOptions.instance().get_cheat_mode())
				{
					car.get_equipment().set_engine(initial_human_engine);
				}
				m_car.set_item(i, car);
				m_player_cars[i] = car;
			}
		}
		else
		{
			// computer only mode
			
			CarBodyView cv = new CarBodyView(bsLoader, car_name, Color.RED);

			csv = new CarShadowView(cv);			

		}
		boolean increase_color = GameOptions.instance().colored_ennemies;
		
		int cpu_color_index = 0;
		CarBodyView cpu_cv = null;
		Color cpu_color_1 = Color.GRAY;
		
		if (increase_color)
		{
			cpu_color_1 = CPU_CAR_COLORS[cpu_color_index];
		}		
			
		cpu_cv = new CarBodyView(bsLoader,car_name, cpu_color_1);
		
		for (int i = nb_human_players;i < m_total_nb_cars;i++)
		{
			m_driver[i] = new Computer(CourseRecords.get_opponent_name(i),current_level.aggressivity[m_circuit]);			
			ComputerCar car = new ComputerCar(cpu_cv, csv, ev, m_weapon_factory, m_driver[i],i+1);
			if (increase_color)
			{
				if (cpu_color_index < CPU_CAR_COLORS.length-1)
				{
					cpu_color_index++;
					cpu_cv = new CarBodyView(bsLoader,car_name, CPU_CAR_COLORS[cpu_color_index]);					
				}
			}
			m_car.set_item(i,car);
		}
		
		compute_car_speeds(true);
	}


}